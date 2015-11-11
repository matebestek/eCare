/***
 * Copyright (c) 2013.
 * University of Primorska, Andrej Marušič Institute. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * Project eOskrba (http://eOskrba.si) was financed by Slovenian Research
 * Agency and Ministry of Health of Republic of Slovenia.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package si.pint.eoskrba.ediabetes.enterPatient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.archetypes.exceptions.LdapException;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.Role;

@Log4j
public class ProcesEnterPatientDiTestCase extends ActivitiTestCase {

	public void assertInActivity(String processInstanceId, String activityId) {
		List<String> activeActivityIds = runtimeService
				.getActiveActivityIds(processInstanceId);
		assertTrue("Current activities (" + activeActivityIds
				+ ") does not contain " + activityId,
				activeActivityIds.contains(activityId));
	}
	
	@Deployment(resources = {
			"diagrams/Diabetes-Proces-Vkljucitev-Pacienta-V-Studijo.bpmn20.xml",
			"diagrams/NOTIFIER-Zdravnik.bpmn20.xml",
			"diagrams/NOTIFIER-CM.bpmn20.xml",
			"diagrams/NOTIFIER-CM-Zdravnik.bpmn20.xml" })
	public void testPatientIncludedInStudy() {
		Map<String, Object> variables = new LinkedHashMap<String, Object>();
		
		//get user information
		variables = getTestSubject(variables);
		variables = getProcessVars(variables);		
		variables = getValuesAppropriateForStudy(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Diabetes-Proces-Vkljucitev-Pacienta-V-Studijo", variables);
		String id = processInstance.getId();
		log.info("Started process instance id " + id);

		//tmp
		List<Task> taskList = taskService.createTaskQuery().orderByTaskName().asc().list();
		
		// 1. inspect first task name - doctor
		Task task = taskList.get(0);
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Novak ustreza kriterijem za študijo", task.getName().trim());

		// 1.1 complete
		taskService.complete(task.getId(), variables);

		// 2. inspect first task name - CM
		task = taskList.get(1);
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Novak ustreza kriterijem za študijo", task.getName().trim());

		//save user for later
		String userName = (String) variables.get("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0007]?value");
		
		// 2.1 complete
		taskService.complete(task.getId(), variables);
		
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService
				.createHistoricProcessInstanceQuery().processInstanceId(id)
				.singleResult();
		assertNotNull(historicProcessInstance);
		
		//delete user from LDAP
		try {
			if (userName != null && UserRegistryFactory.getUserFromLDAP(userName, Role.PATIENT) != null) {
				UserRegistryFactory.removeUser(userName);
			}
		} catch (LdapException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	@Deployment(resources = {
			"diagrams/Diabetes-Proces-Vkljucitev-Pacienta-V-Studijo.bpmn20.xml",
			"diagrams/NOTIFIER-Zdravnik.bpmn20.xml"})
	public void testPatientNotIncludedInStudy() {
		Map<String, Object> variables = new LinkedHashMap<String, Object>();
		
		//get user information
		variables = getTestSubject(variables);
		variables = getProcessVars(variables);		
		variables = getValuesNotAppropriateForStudy(variables);
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Diabetes-Proces-Vkljucitev-Pacienta-V-Studijo", variables);
		String id = processInstance.getId();
		log.info("Started process instance id " + id);

		//tmp
		List<Task> taskList = taskService.createTaskQuery().orderByTaskName().asc().list();
		
		// 1. inspect first task name - doctor
		Task task = taskList.get(0);
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "Bolnik Janko Novak ne ustreza kriterijem za študijo!", task.getName().trim());

		// 1.1 complete
		taskService.complete(task.getId(), variables);
		
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService
				.createHistoricProcessInstanceQuery().processInstanceId(id)
				.singleResult();
		assertNotNull(historicProcessInstance);
		
	}	
	
	


	private Map<String, Object> getTestSubject(Map<String, Object> variables) {

		//'external' variable from eOskrba-webApp
		variables.put(Constants.VAR_USERNAME_APP, "zdravnik.di");

		return variables;
	}
	
	public Map<String, Object> getProcessVars(Map<String, Object> variables) {
		
		// <----- 1. osnovni podatki ------->
		
		//ime
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0002]?value", "Janko");
		
		//priimek
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0003]?value", "Novak");
		
		//bis
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0004]?value", "bid.janko.novak");
		
		//spol
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0005]?value", "at0013"); //moski
		
		//datum rojstva
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0006]?value", "01.07.1980");
		
		//email
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0007]?value", "EMAIL@DOMAIN.COM");
		
		//GSM
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0008]?value", "PHONE-NUMBER");
		
		//zdravstvena ustanova
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0009]?value", "ZD Novo Mesto");
		
		//poklic
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0010]?value", "tesar");
				
//		//vkljucen v studijo
//		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0012]?value", "FALSE");
		
		// visina
		variables.put("?items[openEHR-EHR-OBSERVATION.height.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "185");
			
		
		// <----- 2. druzinska anamneza ------->
		//povzetek
		variables.put("?items[openEHR-EHR-EVALUATION.druzinska!anamneza!eo!di.v1]?data[at0001]?items[at0002]?value", "to je povzetek");
		
		//diabetes v druzini
		variables.put("?items[openEHR-EHR-EVALUATION.druzinska!anamneza!eo!di.v1]?data[at0001]?items[at0003]?value", "ni prisoten");
		
		// <----- 3. anamneza ------->
		//teza
		variables.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value", "88.5");
		
		//indeks telesne mase
		variables.put("?items[openEHR-EHR-OBSERVATION.body!mass!index.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "9");
		
		//kako pogosto si oseba meri telesno tezo
		variables.put("?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!di.v1]?data[at0001]?items[at0006]?items[at0011]?value", "at0025");
		
		//kako pogosto si oseba meri krvni sladkor
		variables.put("?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!di.v1]?data[at0001]?items[at0006]?items[at0007]?value", "at0018");

		//kako pogosto si oseba meri krvni tlak
		variables.put("?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!di.v1]?data[at0001]?items[at0006]?items[at0015]?value", "at0033");		

		//alkohol ---------->>>>
		
		//pogostost pitja
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0006");
		
		//kolicina pijace
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0008]?value", "at0012");
		
		//Pogostost pitja vecjih kolicin
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0014]?value", "at0016");
		
		//stevilo dosezenih tock
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0020]?value", "9");
		
		//ocena ogrozenosti
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0022]?value", "at0024");
		
		//komentar
		variables.put("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0021]?value", "komentar za alko");
		
		//kajenje ---------->>>>
		
		//kadilski status
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0015");
		
		//dnevno stevilo cigaret
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0008]?items[at0007]?value", "6");
		
		//datum zacetka uzivanja
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0008]?items[at0009]?value", "1990-01-01");
		
		//starost ob zacetku uzivanja
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0008]?items[at0011]?value", "14");
		
		//poskus prenehanja
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0016]?items[at0017]?value", "at0019");
		
		//datum prenehanja
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0016]?items[at0010]?value", "2000-01-01");
		
		//starost ob prenehanju
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0016]?items[at0012]?value", "22");
		
		//stopnja ogrozenosti
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0022]?value", "at0023");
		
		//komentar
		variables.put("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0013]?value", "kadilski komentar");
		
		//telesna dejavnost ---------->>>>
		
		//visoko intenzivna vadba
		variables.put("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0005");
		
		//zmerno intenzivna vadba
		variables.put("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0012]?value", "at0015");
		
		//stevilo dosezenih tock
		variables.put("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0022]?value", "8");
		
		//ogrozenost
		variables.put("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0024]?value", "at0026");
		
		//komentar
		variables.put("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0023]?value", "komentar za telesno dejavnost");
		
		//anamneza ostalo ---------->>>>
		
		//ocesno ozadje
		variables.put("?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!di.v1]?data[at0001]?items[at0002]?value", "ok");
		
		//pregled nog
		variables.put("?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!di.v1]?data[at0001]?items[at0003]?value", "noge ok");
		
		//zgodovina bolezni
		variables.put("?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!di.v1]?data[at0001]?items[at0005]?value", "diabetes 4 life");
			
		//uporaba inzulina
		variables.put("?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!di.v1]?data[at0001]?items[at0035]?value", "TRUE");
		
		//uporaba merilca krvnega sladkorja
		variables.put("?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!di.v1]?data[at0001]?items[at0036]?value", "FALSE");
		
		
		// <----- 3. diagnoza ------->
		
		//diagnoza
		variables.put("?items[openEHR-EHR-EVALUATION.diagnoza!eo.v1]?data[at0001]?items[at0002]?value", "diagnoza umor");
		
		//diagnoza (MKB10)
		variables.put("?items[openEHR-EHR-EVALUATION.diagnoza!eo.v1]?data[at0001]?items[at0003]?value", "diagnoza MKB10");
//		
		// <----- 4. terapija ------->
		
		//opis terapije
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[at0004]?value", "to je opis terapije");
		
		// <----- 5. krvni tlak ------->
		
		//sistolicni
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "120");
		
		//diastolicni
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0005]?value", "80");
		
		//opombe
		variables.put("?items[openEHR-EHR-OBSERVATION.krvni!tlak!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0006]?value", "opomba za krvni tlak");

		// <----- 6. WONCA ------->
		
		//Katera fizična aktivnost, ki se jo lahko opravljali vsaj dve minuti je bila v zadnjih dveh tednih najtežja?
		variables.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0006]?value", "at0018");
		
		//Kako močno so vas v zadnjih dveh tednih pestile čustvene težave, kot so občutek tesnobe, depresije, razdražljivosti, potrtost ali žalost?
		variables.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0005]?value", "at0024");
		
		//Koliko težav ste imeli v zadnjih dveh tednih pri svojih običajnih dejavnosti doma in drugje zaradi svojega telesnega in duševnega zdravja?
		variables.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0008]?value", "at0030");
		
		//Koliko je v zadnjih dveh tednih vaše telesno in duševno zdravje omejevalo vaše družabne aktivnosti z družino, prijatelji, sosedi ali drugimi skupinami?
		variables.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0007]?value", "at0034");

		//Kako bi ocenili svoje trenutno telesno in duševno zdravstveno stanje glede na tisto pred dvemi tedni? 
		variables.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0010]?value", "at0040");

		//Kako bi na splošno ocenili svoje telesno in duševno zdravstveno stanje zadnja dva tedna?
		variables.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0011]?value", "at0043");

		//Koliko telesnih bolečin ste imeli v zadnjih štirih tednih oziroma kako izrazite so bile?
		variables.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0009]?value", "at0014");

//		//stevilo tock
//		variables.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0047]?value", new Integer("17"));
//
//		//komentar
//		variables.put("?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0048]?value", "komentar za wonco");

		
		// <----- 7. laboratorij ------->
		
		//Hb1ac
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0015]?value", "15");
		
		//glukoza v krvi na tesce
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "16");
		
		//albumin
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0007]?value", "17");
		
		//aceton
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0008]?value", "18");
		
		//holesterol
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0009]?value", "19");
		
		//trigliceridi
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0010]?value", "20");
		
		//hdl
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0011]?value", "21");
		
		//ldl
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0012]?value", "22");
		
		//kreatinin
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0013]?value", "23");
		
		//alt
		variables.put("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0014]?value", "24");
				
		// <----- 8. zdravila ------->

		//ime zdravila
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0002]?value?0", "zdravilo XY");
		
		//gen. ime
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0003]?value?0", "gen. ime XY");
		
		//ATC koda
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0028]?value?0", "ATC koda 1234XXX");
		
		//stevilo pakiranj
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0006]?items[at0018]?value?0", "40");
		
		//zdravila - kolicina (st?mg?ml?vdih)
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0005]?items[at0008]?value?0", "12");
		
		//pogostost jemanja
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0005]?items[at0009]?value?0", "at0012");

		//ste danes vzeli vsa zdravila
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0007]?items[at0019]?value?0", "FALSE");
				
		//kako redno ste v zadnjih 14 dneh jemali zdravila
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0007]?items[at0020]?value?0", "at0023");
		
		// <----- 8.1 zdravila (2) ------->
		//ime zdravila
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0002]?value?1", "zdravilo XY2");
		
		//gen. ime
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0003]?value?1", "gen. ime XY2");
		
		//ATC koda
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0028]?value?1", "ATC koda 1234XXX2");
		
		//stevilo pakiranj
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0006]?items[at0018]?value?1", "42");
		
		//zdravila - kolicina (st?mg?ml?vdih)
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0005]?items[at0008]?value?1", "22");
		
		//pogostost jemanja
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0005]?items[at0009]?value?1", "at0013");

		//ste danes vzeli vsa zdravila
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0007]?items[at0019]?value?1", "TRUE");
				
		//kako redno ste v zadnjih 14 dneh jemali zdravila
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0007]?items[at0020]?value?1", "at0024");	

		// <----- 8.2 zdravila (3) ------->
		
		//gen. ime
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0003]?value?2", "gen. ime XY3");
		
		//ATC koda
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0028]?value?2", "ATC koda 1234XXX!3");
				
		// <----- 8.2 zdravila (4) ------->
		//gen. ime
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0003]?value?3", "gen. ime XY4");
		
		//pogostost jemanja
		variables.put("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0005]?items[at0009]?value?3", "at0014");		
		
		
		return variables;
	}
	
	public Map<String, Object> getValuesAppropriateForStudy(Map<String, Object> variables) {
		
		//izpolnjuje ostale vkljucitvene pogoje
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0011]?value", "TRUE");
				
		return variables;
	}
	
	public Map<String, Object> getValuesNotAppropriateForStudy(Map<String, Object> variables) {
		
		//izpolnjuje ostale vkljucitvene pogoje
		variables.put("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0011]?value", "FALSE");
		
		return variables;
	}	
}
