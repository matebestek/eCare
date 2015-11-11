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
package si.pint.eoskrba.eastma.inputGeneric;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;

import si.pint.eoskrba.eastma.inputACT.delegate.InitProcessDelegate;
import si.pint.eoskrba.eastma.inputACT.delegate.IzracunKategorijeACTDelegate;
import si.pint.eoskrba.eastma.inputACT.delegate.PatientNotifySmsDelegate;
import si.pint.eoskrba.eastma.inputACT.delegate.PatientReminderSmsDelegate;
import si.pint.eoskrba.eastma.inputACT.listeners.CaremanagerObvestiloCompleteListener;
import si.pint.eoskrba.eastma.inputACT.listeners.CaremanagerObvestiloCreateListener;
import si.pint.eoskrba.eastma.inputACT.listeners.DoctorObvestiloCompleteListener;
import si.pint.eoskrba.eastma.inputACT.listeners.DoctorObvestiloCreateListener;
import si.pint.eoskrba.eastma.inputACT.listeners.PatientEntersACTCompleteListener;
import si.pint.eoskrba.eastma.inputACT.listeners.PatientEntersACTCreateListener;
import si.pint.eoskrba.eastma.inputACT.listeners.PatientObvestiloCompleteListener;
import si.pint.eoskrba.eastma.inputACT.listeners.PatientObvestiloCreateListener;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;

@Log4j
public class VnosACTTestCase extends ActivitiTestCase {

	public void assertInActivity(String processInstanceId, String activityId) {
		List<String> activeActivityIds = runtimeService
				.getActiveActivityIds(processInstanceId);
		assertTrue("Current activities (" + activeActivityIds
				+ ") does not contain " + activityId,
				activeActivityIds.contains(activityId));
	}

	private HashMap<String, Object> getTestSubject() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		variables
				.put(Constants.VAR_PATIENT_OBJ,
						"{\"sex\":\"MALE\",\"height\":\"183\",\"weight\":\"85\",\"eMail\":\"EMAIL@DOMAIN.COM\",\"role\":\"1\",\"birthDate\":\"01.01.1960\",\"birpisId\":\"ivan\",\"username\":\"ivan.pacient\",\"mobilePhoneNum\":\"PHONE-NUMBER\"}");
		User patient = new User();
		patient.setSex(User.Sex.MALE);
		patient.setHeight(new BigDecimal(183));
		patient.setWeight(new BigDecimal(85));
		patient.setEmail("EMAIL@DOMAIN.COM");
		patient.setRole(Role.PATIENT);
		patient.setDateOfBirth(new Date());
		patient.setBid("BID");
		patient.setUsername("USERNAME");
		patient..setMobilePhone("PHONE-NUMBER");

		User doctor = new User();
		doctor.setSex(User.Sex.MALE);
		doctor.setHeight(new BigDecimal(183));
		doctor.setWeight(new BigDecimal(85));
		doctor.setEmail("EMAIL@DOMAIN.COM");
		doctor.setRole(Role.DOCTOR);
		doctor.setDateOfBirth(new Date());
		doctor.setBid("BID");
		doctor.setUsername("USERNAME");
		doctor..setMobilePhone("PHONE-NUMBER");

		User caremanager = new User();
		caremanager.setSex(User.Sex.MALE);
		caremanager.setHeight(new BigDecimal(183));
		caremanager.setWeight(new BigDecimal(85));
		caremanager.setEmail("EMAIL@DOMAIN.COM");
		caremanager.setRole(Role.CAREMANAGER);
		caremanager.setDateOfBirth(new Date());
		caremanager.setBid("BID");
		caremanager.setUsername("USERNAME");
		caremanager..setMobilePhone("PHONE-NUMBER");

		variables.put(Constants.VAR_PATIENT, patient);
//		variables.put(Constants.VAR_DOCTOR, doctor);
//		variables.put(Constants.VAR_CAREMANAGER, caremanager);

		// Email e = new
		// Email("EMAIL@DOMAIN.COM","Tole je vsebina","Že nekaj časa niste izpolnili ACT vprašalnik.");
		// variables.put(Constants.VAR_email, e);
		// b.sms
		variables.put("notifySmsDelegateBean", new PatientNotifySmsDelegate());

		// Vnos vrednosti

		variables.put("initProcessDelegate", new InitProcessDelegate());
		variables.put("listenerPacientVnosComplete",
				new PatientEntersACTCompleteListener());
		variables.put("listenerPacientVnosCreate",
				new PatientEntersACTCreateListener());
		variables.put("izracunKategorijeDelegate",
				new IzracunKategorijeACTDelegate());
		variables.put("listenerCaremanagerObvestiloComplete",
				new CaremanagerObvestiloCompleteListener());
		variables.put("listenerCaremanagerObvestiloCreate",
				new CaremanagerObvestiloCreateListener());
		variables.put("caremanager", caremanager);
		variables.put("doctor", doctor);
		variables.put("patient", patient);
		variables.put("listenerDoctorObvestiloCreate",
				new DoctorObvestiloCreateListener());
		variables.put("listenerDoctorObvestiloComplete",
				new DoctorObvestiloCompleteListener());
		variables.put("patientNotifySmsDelegate",
				new PatientNotifySmsDelegate());
		variables.put("patientReminderSmsDelegate",
				new PatientReminderSmsDelegate());
		variables.put("listenerPatientObvestiloComplete",
				new PatientObvestiloCompleteListener());
		variables.put("listenerPatientObvestiloCreate",
				new PatientObvestiloCreateListener());

//		Email cmEmail = new Email("EMAIL@DOMAIN.COM",
//				"Tole je vsebina za CM", "Pacient ni najbolje");
//		variables.put("caremanagerEmail", cmEmail);

//		Email patientEmail = new Email("EMAIL@DOMAIN.COM",
//				"Tole je vsebina za Pacienta",
//				"Že nekaj časa niste izpolnili ACT vprašalnik.");
//		variables.put("patientEmail", patientEmail);

		variables.put("maxNumberOfSentReminders", 3);
		variables.put("reminderPacientIntervalDuration", "PT24H");// interval je
																	// 3 sekunde
		
		
		
		return variables;
	}

	/**
	 * START-REMINDER-Pacient-->Vnos vrednosti pacienta-->Izračun
	 * kategorije-->NOTIFY-Pacient-KONEC "diagrams/NOTIFIER-ALL.bpmn20.xml",
	 * "diagrams/NOTIFIER-CM.bpmn20.xml",
	 * "diagrams/NOTIFIER-Zdravnik.bpmn20.xml",
	 * */
	@Deployment(resources = { "diagrams/NOTIFIER-Pacient.bpmn20.xml",
			"diagrams/REMINDER-Pacient.bpmn20.xml",
			"diagrams/VnosMeritev.bpmn20.xml",
			"diagrams/Proces-Opomnik-Vnos-Generic.bpmn20.xml" })
	public void testHappyPath() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		ProcessInstance pi = null;
		try{
		pi = runtimeService.startProcessInstanceByKey(
				"Proces-Opomnik-Vnos-Generic", getTestSubject());
		} catch(Exception e){
			log.error("napača:" + e.getMessage());
		}
		String id = pi.getId();
		log.info("Started process instance id " + id);
		assertInActivity(id, "CallActivity-VnosVrednostiPacienta");// imam
																			// CallActivity
																			// za
																			// Reminder
																			// Pacient
		// 1. zgodi se pošiljanje email-a in sms-a pacientu

		// 2. Call Activity - Vnos Meritev
		Task task = taskService.createTaskQuery().singleResult();
		assertTrue(task.getProcessDefinitionId().contains(
				"Proces-Opomnik-Vnos-Meritev"));// gre za process
																// VnosMeritev
		assertNotNull("Task shouldn't be null!", task);
		assertEquals("Wrong task name!", "ACT vprašalnik za spremljanje urejenosti astme",
				task.getName());// Gre za User Task znotraj procesa Vnos Meritev
		//1.1 claim & complete
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0034]?value", "at0042");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0035]?value", "at0047");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0036]?value", "at0052");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0037]?value", "at0057");
		variables.put("?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0038]?value", "at0062");	
		
		//variables.put(Constants.VAR_ACT_RESULT, 15);
		taskService.claim(task.getId(), task.getAssignee());
		taskService.complete(task.getId(),variables);// zaključim usertask
		
		try {
			String query = "//items/archetype_details/archetype_id/value/text()";
//			String archId = DBManager.readByName("ivan.pacient","1311421463327.xml", "openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1", query);
//			assertEquals("openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1", archId);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// 3. Spet nazaj v krovni proces in imam Izračun kategorije
		// izvede se serviceTask in tudi ustrezen delegat, ki nastavi categoryOk
		assertNotNull(runtimeService.getVariable(id, "categoryOk"));
		assertTrue((Boolean) runtimeService.getVariable(id, "categoryOk"));
		// 4. Ker je categoryOk==true, gre proces v NOTIFIER-Pacient
		// serviceTask naredi svoje
		// userTask - user dobi obvestilo na web
		List<Task> tasks = taskService.createTaskQuery().list();
		task = tasks.get(tasks.size()==1?0:1);
		boolean imam = false;
		for (Task t : tasks) {
			log.info("Task:" + task.getName() + " id="
					+ task.getProcessDefinitionId());

			if (t.getProcessDefinitionId().contains(
					"Proces-Opomnik-Vnos-Notifier-Cm")) {
				imam = true;
				assertTrue(t.getProcessDefinitionId().contains(
						"Proces-Opomnik-Vnos-Notifier-Cm"));// gre za
																		// process
																		// NOTIFIER-Patient
				assertNotNull("Task shouldn't be null!", t);
				assertEquals("Wrong task name!", "Obvestilo",
						t.getName());// Gre za User Task znotraj procesa
										// NOTIFIER-Patient
				taskService.complete(t.getId());// zaključim
												// usertask
			}
		}
		assertTrue(imam);
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService
				.createHistoricProcessInstanceQuery().processInstanceId(id)
				.singleResult();
		assertNotNull(historicProcessInstance);

		log.info("Finished, took "
				+ historicProcessInstance.getDurationInMillis() + " millis");

	}
	//
	// //1. REMINDER pacienta
	// ProcessInstance pi = runtimeService.startProcessInstanceByKey(
	// "Proces-Opomnik-Vnos-Reminder-Pacient", getTestSubject());
	// String id = pi.getId();
	// log.info("Started process instance id " + id);
	// HistoricProcessInstance historicProcessInstance = historicDataService
	// .createHistoricProcessInstanceQuery().processInstanceId(id)
	// .singleResult();
	// assertNotNull(historicProcessInstance);
	//
	// log.info("Finished, took "
	// + historicProcessInstance.getDurationInMillis() + " millis");
	//
	// //2. Izvede se userTask za vnos vrednosti
	// pi = runtimeService.startProcessInstanceByKey(
	// "Proces-Opomnik-Vnos-Meritev", getTestSubject());
	// id = pi.getId();
	// log.info("Started process instance id " + id);
	//
	// List<Task> tasks = taskService.createTaskQuery().list();
	// assertNotNull(tasks);
	// for (Task t : tasks) {
	// log.info("Task name: " + t.getName() + " created:"
	// + t.getCreateTime() + " key:" + t.getTaskDefinitionKey()
	// + " assignee:" + t.getAssignee());
	// // pogledam formo
	// String formKey = formService.getTaskFormData(t.getId()).getFormKey();
	// log.info("FORM KEY:" + formKey);
	// //assertEquals("vnosACT.form", formKey);
	// List<FormProperty> props =
	// formService.getTaskFormData(t.getId()).getFormProperties();
	// for(FormProperty fp:props){
	// log.info("Form property: id=" + fp.getId() + " name=" + fp.getName() +
	// " value=" + fp.getValue() + " type name=" + fp.getType().getName());
	// }
	//
	//
	// taskService.complete(t.getId(), null);
	// }
	// tasks = taskService.createTaskQuery().list();
	// assertEquals(0, (tasks == null ? 0 : tasks.size()));
	//
	// // runtimeService.signal(id); (za to rabimo waitState?)
	// assertProcessEnded(id);
	//
	// historicProcessInstance = historicDataService
	// .createHistoricProcessInstanceQuery().processInstanceId(id)
	// .singleResult();
	// assertNotNull(historicProcessInstance);
	//
	// log.info("Finished, took "
	// + historicProcessInstance.getDurationInMillis() + " millis");
	//
	// //3. Trenutno preskočim izračun katergorije prevsem zato, da
	// parametriziram še ostale podprocese
	//
	//
}
