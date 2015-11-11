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
package si.pint.eoskrba.ediabetes.vnosPacienta.delegate;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.MDC;
import org.openehr.validation.ValidationError;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.VnosPacientaDiabetesArcheSrv;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.Medicine;
import si.pint.eoskrba.model.User;

@Log4j
/**
 * Popravljanje podatkov, ki se hranijo v eXist-u. Podatkov, ki so tudi v LDAP:
 * - uid (username oz. email)
 * - Ime Priimek
 * - datum rojstva
 * - email
 * - zdr. ustanova
 * - telefonska stevilka
 * - izpolnjuje pogoje
 * 
 * @author Blaz
 *
 */
public class UpdatePatientData implements JavaDelegate {

	// wonca archetype
	public static final String WONCA_ARCHETYPE_ID = "openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1";
	//wonca questionary
	public static final String TEMPLATE_PATH_WONCA_SUM = "/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0047]/value";
	//archetype id for 'basic patient data'
	public static final String BASIC_DATA_ARCHETYPE_ID = "openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1";
	// template path for 'included in study'
	public static final String TEMPLATE_PATH_INCLUDED_IN_STUDY = "/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0012]/value";
	// 'other conditions' template path
	public static final String TEMPLATE_PATH_OTHER_CONDITIONS = "/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0011]/value";

	
	//paths for dynamic meds table
	//ime zdravila
	private static final String TEMPLATE_MEDICINE_NAME = "/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0002]/value";
	
	//gen. ime
	private static final String TEMPLATE_GENERIC_NAME = "/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0003]/value";
	
	//ATC koda
	private static final String TEMPLATE_ATC_CODE = "/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0028]/value";
	
	//stevilo pakiranj
	private static final String TEMPLATE_PACK_NUM= "/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0006]/items[at0018]/value";
	
	//zdravila - kolicina (st/mg/ml/vdih)
	private static final String TEMPLATE_QUANTITY= "/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0005]/items[at0008]/value";
	
	//pogostost jemanja
	private static final String TEMPLATE_CON_FREQ = "/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0005]/items[at0009]/value";

	

	public void execute(DelegateExecution execution) throws Exception {

		VnosPacientaDiabetesArcheSrv archeService = new VnosPacientaDiabetesArcheSrv();
		if (!archeService.initInput())
			return;

		// user input data
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();

		// look for all possible attributes on session
		Map allArcheAtts = archeService.getInputsMap();
//		Iterator it = allAtributes.keySet().iterator();
		
		//look for all possible attributes on session (2)
		Map allAtributes = execution.getVariables();
		Iterator it = allAtributes.keySet().iterator();
		
		//parameters to check
		int vsotaWonca = 0;
		List<Medicine> list = null; //list of medicine

		while (it.hasNext()) {
			String key = (String) it.next();
//			String keyR = key.replaceAll("/", "?");
//			keyR = keyR.replaceAll("_", "!");
			String keyR = key.replaceAll("\\?", "/");
			keyR = keyR.replaceAll("!", "_");
			
			Object var = execution.getVariable(key);
			Set s = execution.getVariableNames();

			if (var != null && !var.equals("")) {

				if (keyR.indexOf(WONCA_ARCHETYPE_ID) >= 0) {
					vsotaWonca += (Integer) archeService.getValue(keyR, (String) var);
				}
				
				//meds
				if (keyR.indexOf(TEMPLATE_MEDICINE_NAME) >= 0 ||
						keyR.indexOf(TEMPLATE_ATC_CODE) >= 0 ||
						keyR.indexOf(TEMPLATE_CON_FREQ) >= 0 ||
						keyR.indexOf(TEMPLATE_GENERIC_NAME) >= 0 ||
						keyR.indexOf(TEMPLATE_PACK_NUM) >= 0 ||
						keyR.indexOf(TEMPLATE_QUANTITY) >= 0) {
					
						list = SelectClinicalTrialGroupDelegate.putMedicineValueToList(list, keyR, var);
				}
				
				//store in map
				if (allArcheAtts.containsKey(keyR))
					userInputsMap.put(new ArcheDataPath(keyR), var);
			}
		}
		
		//also put meds in user inputs Map
		if (list != null) {
			Iterator<Medicine> itMeds = list.iterator();
			while (itMeds.hasNext()) {
				Medicine medicine = itMeds.next();
				if (StringUtils.isNotBlank(medicine.getMedicineName()))
					userInputsMap.put(new ArcheDataPath(TEMPLATE_MEDICINE_NAME), medicine.getMedicineName());
				if (StringUtils.isNotBlank(medicine.getGenericName()))
					userInputsMap.put(new ArcheDataPath(TEMPLATE_GENERIC_NAME), medicine.getGenericName());
				if (StringUtils.isNotBlank(medicine.getAtcCode()))
					userInputsMap.put(new ArcheDataPath(TEMPLATE_ATC_CODE), medicine.getAtcCode());
				if (StringUtils.isNotBlank(medicine.getConsumptionFreq()))
					userInputsMap.put(new ArcheDataPath(TEMPLATE_CON_FREQ), medicine.getConsumptionFreq());
				if (medicine.getPackingNum() != null)
					userInputsMap.put(new ArcheDataPath(TEMPLATE_PACK_NUM), medicine.getPackingNum());
				if (medicine.getQunatityToTake() != null)
					userInputsMap.put(new ArcheDataPath(TEMPLATE_QUANTITY), medicine.getQunatityToTake());
			}
		}

		// v userInputs dam tudi vsoto WONCA tock 
		userInputsMap.put(new ArcheDataPath(TEMPLATE_PATH_WONCA_SUM), vsotaWonca);
		
		//already included in study
		userInputsMap.put(new ArcheDataPath(TEMPLATE_PATH_INCLUDED_IN_STUDY), "true");

		User u = SelectClinicalTrialGroupDelegate.createUserFromContext(execution, archeService);
		
		
		
		
		
		// archetype data ok
		boolean patientDataParseOk = false;

		// data validation
		//data not OK
		if (userInputsMap.isEmpty()	|| !archeService.setAndValidataData(userInputsMap)) {
			
			if (archeService.getErrorList() != null) {
				for (ValidationError ve : archeService.getErrorList()) {
					log.error("path:" + ve.getArchetypePath() + " napaka:"	+ ve.getErrorType().toString());
				}
			}
		//data OK
		} else {
			patientDataParseOk = true;
		}
		String appUserId = ((String) execution.getVariable(Constants.VAR_USERNAME_APP)); 
		String appUserRole = UserRegistryFactory.getUserFromLDAP(appUserId).getRole().getName();
		
		//logging
		MDC.put("user", appUserId);
		MDC.put("userRole", appUserRole);		
		MDC.put("taskContent", "");
		MDC.put("taskType", EMPLOYEE_TYPE_ENUM.DIABETES.toDomainName());
		
		
		//preverim, če gre za Nov obisk. V tem primeru malce prilagodim logiranje, prav tako pa moram
		//v bazo shranit metapodatke o novem obisku zato da jih bomo lahko prikazali v obliki seznama
		if(allAtributes.containsKey("newVisit") && (Boolean)allAtributes.get("newVisit")){//NEW VISIT TO THE DOCTOR
			MDC.put("task", "Dodajanje novega obiska");
			log.info("Oseba " + ((String) execution.getVariable(Constants.VAR_USERNAME_APP)) + " dodaja nov obisk za pacienta: " + u.getUsername());
			String visitFileName = null;
			if (patientDataParseOk) {// save data in XML form to DB - for all patients
				visitFileName = archeService.saveInputReturnFilename(SystemType.EOSKRBAPROCESSENGINE,((String) execution.getVariable(Constants.VAR_USERNAME_APP)),u.getUsername());			
				
				log.info("Dodajanje novega obiska za pacienta: " + u.getUsername() + " filename="+visitFileName);
			}
			// nastavim spremenljivk skupinaEksperiment, skupinaKontrolna ali
			// notSatisfyingConditions
			if (!patientDataParseOk) {
				log.info("Obisk za pacienta " + u.getUsername() + " ni uspešno dodan");
				return;
			}
			DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).addNewVisit(appUserId, appUserRole,u.getUsername(),visitFileName,"",EMPLOYEE_TYPE_ENUM.DIABETES.toDomainName());
			log.info("Nov obisk s strani osebe " + ((String) execution.getVariable(Constants.VAR_USERNAME_APP)) + " je dodan za pacienta: " + u.getUsername());

		} else {//UPDATE OF THE LATEST DATA (also the latest visit)
			MDC.put("task", "Posodabljanje osebnih podatkov pacienta (podatkov prvega pregleda)");
			log.info("Oseba " + ((String) execution.getVariable(Constants.VAR_USERNAME_APP)) + " popravlja podatke o pacientu: " + u.getUsername());
			
			if (patientDataParseOk) {// save data in XML form to DB - for all patients
				archeService.saveInput(SystemType.EOSKRBAPROCESSENGINE,((String) execution.getVariable(Constants.VAR_USERNAME_APP)),u.getUsername());			
				
				log.info("Posodabljanje osebnih podatkov pacienta: " + u.getUsername());
			}
			// nastavim spremenljivk skupinaEksperiment, skupinaKontrolna ali
			// notSatisfyingConditions
			if (!patientDataParseOk) {
				log.info("Pacientu " + u.getUsername() + " se niso pravilno posodobili podatki");
				return;
			}
			
			log.info("Oseba " + ((String) execution.getVariable(Constants.VAR_USERNAME_APP)) + " je popravila podatke o pacientu: " + u.getUsername());

			
		}
		
		

		
	}

}
