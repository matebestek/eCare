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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.MDC;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.openehr.validation.ValidationError;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.ArchetypeService;
import si.pint.archetypes.service.VnosPacientaDiabetesArcheSrv;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.model.ValueObject;
import si.pint.eoskrba.eastma.model.RandomGroupGenerator;
import si.pint.eoskrba.model.Medicine;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;
import si.pint.eoskrba.utils.PatientRegistryUtils;


@Log4j
public class SelectClinicalTrialGroupDelegate implements JavaDelegate {

	// template path for 'included in study'
	public static final String TEMPLATE_PATH_INCLUDED_IN_STUDY = "/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0012]/value";
	// 'other conditions' template path
	public static final String TEMPLATE_PATH_OTHER_CONDITIONS = "/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0011]/value";
	// wonca archetype
	public static final String WONCA_ARCHETYPE_ID = "openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1";
	//wonca questionary
	public static final String TEMPLATE_PATH_WONCA_SUM = "/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0047]/value";
	//archetype id for 'basic patient data'
	public static final String BASIC_DATA_ARCHETYPE_ID = "openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1";
	
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
		boolean satisfyingConditions = false;

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
		boolean otherConditions = false;
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
				if (keyR.indexOf(TEMPLATE_PATH_OTHER_CONDITIONS) >= 0) {
					otherConditions = Boolean.parseBoolean((String) var);
				}
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
					
						list = putMedicineValueToList(list, keyR, var);
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

		User u = createUserFromContext(execution, archeService);
		// archetype data ok
		boolean patientDataParseOk = false;
		// choose random group (experimental/control)
		boolean isExperimentalGroup = false;
		//if CM logged, assign task	to CM. Otherwise to doctor
		String loggedMedical = null;
		boolean cmLoggedIn = false;
		if (((String) execution.getVariable(Constants.VAR_USERNAME_APP)).equals(u.getPatientsDoctor()))
			loggedMedical = u.getPatientsDoctor();
		else if (((String) execution.getVariable(Constants.VAR_USERNAME_APP)).equals(u.getPatientsCM())) {
			loggedMedical = u.getPatientsCM();
			cmLoggedIn = true;
		}
		
		// data validation
		//data not OK
		if (userInputsMap.isEmpty()	|| !archeService.setAndValidataData(userInputsMap)) {
			
			satisfyingConditions = false;
			if (archeService.getErrorList() != null) {
				for (ValidationError ve : archeService.getErrorList()) {
					log.error("path:" + ve.getArchetypePath() + " napaka:"	+ ve.getErrorType().toString());
				}
			}
		//data OK
		} else {
			// check other conditions
			if (otherConditions)
				satisfyingConditions = true;

			// choose random group (experimental/control)
			isExperimentalGroup = RandomGroupGenerator.getRandomGroup(archeService, u.getPatientsDoctor(), u.getHealthcareInstitution(),loggedMedical,u.getUsername());
			//
			// add user to LDAP
			if (satisfyingConditions && isExperimentalGroup)
				UserRegistryFactory.addPatient(u);

			patientDataParseOk = true;
		}
		
		
			
		execution.setVariable(Constants.VAR_DOCTOR, UserRegistryFactory.getUserFromLDAP(loggedMedical));
		
		//logging
		MDC.put("user", loggedMedical);
		MDC.put("userRole", cmLoggedIn?Role.CAREMANAGER.getName():Role.DOCTOR.getName());
		MDC.put("task", "Dodajanje novega pacienta v študijo");
		MDC.put("taskContent", "");
		MDC.put("taskType", EMPLOYEE_TYPE_ENUM.DIABETES.toDomainName());
		
		if (patientDataParseOk) {// save data in XML form to DB - for all patients
			if (!satisfyingConditions)
				userInputsMap.put(new ArcheDataPath(TEMPLATE_PATH_INCLUDED_IN_STUDY), "false");
			else
				userInputsMap.put(new ArcheDataPath(TEMPLATE_PATH_INCLUDED_IN_STUDY), "true");
			
			archeService.setAndValidataData(userInputsMap); // repopulate RMObject
			String visitFileName = archeService.saveInputReturnFilename(SystemType.EOSKRBAPROCESSENGINE,loggedMedical,u.getUsername());			
			DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).addNewVisit(loggedMedical, cmLoggedIn?Role.CAREMANAGER.getName():Role.DOCTOR.getName(),u.getUsername(),visitFileName,"FIRST_VISIT",EMPLOYEE_TYPE_ENUM.DIABETES.toDomainName());
			
			log.info("V studijo za diabetes je dodan nov pacient: " + u.getUsername());
		}

		// nastavim spremenljivk skupinaEksperiment, skupinaKontrolna ali
		// notSatisfyingConditions
		if (!satisfyingConditions) {
			execution.setVariable(Constants.VAR_notSatisfyingConditions, true);
			execution.setVariable(Constants.VAR_skupinaEksperiment, false);
			execution.setVariable(Constants.VAR_skupinaKontrolna, false);

			setTemplateContent(execution, u, "notify-doctor-notIncludedInStudy.vm",	Constants.VAR_notifyDoctorForm);
			execution.setVariable(Constants.VAR_doctorUserTaskName, "Bolnik " + u.getFirstNameLastName() + " ne ustreza kriterijem za študijo!");
			log.info("Bolnik " + u.getFirstNameLastName() + " ne ustreza kriterijem za študijo diabetesa!");
			return;
		}
		
		//if conditions for study OK -> CM & doctor are both notified
		if (cmLoggedIn)
			execution.setVariable(Constants.VAR_DOCTOR, UserRegistryFactory.getUserFromLDAP(u.getPatientsDoctor()));

		// experimental/control group
		execution.setVariable(Constants.VAR_skupinaEksperiment,	isExperimentalGroup);
		execution.setVariable(Constants.VAR_skupinaKontrolna, !isExperimentalGroup);
		execution.setVariable(Constants.VAR_notSatisfyingConditions, false);

		// rabim še za opomnike zdravniku in oskrbovalcu
		setTemplateContent(execution, u, "notify-includedInStudy.vm", Constants.VAR_notifyDoctorForm_1);
		setTemplateContent(execution, u, "notify-includedInStudy.vm", Constants.VAR_notifyCaremanagerForm);
		execution.setVariable(Constants.VAR_doctorUserTaskName,	"Bolnik " + u.getFirstNameLastName() + " ustreza kriterijem za študijo");
		execution.setVariable(Constants.VAR_cmUserTaskName,	"Bolnik " + u.getFirstNameLastName() + " ustreza kriterijem za študijo");

		execution.setVariable(Constants.VAR_PATIENT, u);
		
		log.info("Pacient " + u.getUsername() + " je dodan v " + (isExperimentalGroup ? "eksperimentalno" : "kontrolno") + " skupino!");

	}

	/**
	 * Set value for medicine in list depending on 'key' suffix number. (?0, ?1, ?2, ...)
	 * 
	 * @param list
	 * @param key
	 * @param var
	 */
	public static List<Medicine> putMedicineValueToList(List<Medicine> list, String key, Object var) {
		
		if (list == null)
			list = new ArrayList<Medicine>();

		//find index
		if (key.length() < 3)
			return list;
		String indexTxt = key.substring(key.lastIndexOf('/') + 1);
		int index = Integer.parseInt(indexTxt); 
		
		while (index > list.size() - 1) {
			Medicine m = new Medicine();
			list.add(m);
		}

		
		Medicine m = list.get(index);
		if (key.indexOf(TEMPLATE_MEDICINE_NAME) >= 0) {
			m.setMedicineName((String) var);
		}
		else if (key.indexOf(TEMPLATE_ATC_CODE) >=0) {
			m.setAtcCode((String) var);
		}
		else if (key.indexOf(TEMPLATE_CON_FREQ) >=0) {
			m.setConsumptionFreq((String) var);
		}
		else if (key.indexOf(TEMPLATE_GENERIC_NAME) >=0) {
			m.setGenericName((String) var);
		}
		else if (key.indexOf(TEMPLATE_PACK_NUM) >=0) {
			m.setPackingNum(new Integer((String) var));
		}
		else if (key.indexOf(TEMPLATE_QUANTITY) >=0) {
			m.setQunatityToTake(new Integer((String) var));
		}
		
		return list;
	}

	private void setTemplateContent(DelegateExecution execution, User u, String templateFile, String executionVarName) {
		VelocityEngine ve = new VelocityEngine();
		Template template = null;
		StringWriter sw = null;
		Properties p = new Properties();
		p.setProperty("resource.loader", "class,jar");
		p.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
		p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);
		VelocityContext context = new VelocityContext();

		context.put("patient", u);

		boolean isExperimentalGroup = (Boolean) execution.getVariable(Constants.VAR_skupinaEksperiment);
		context.put("groupName", isExperimentalGroup ? "eksperimentalno" : "kontrolno");

		// doctor
		try {
			template = ve.getTemplate(templateFile, "UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(executionVarName, new ValueObject(sw.toString()));
	}

	public static User createUserFromContext(DelegateExecution execution,	ArchetypeService service) {
		User u = new User();

		u.setFirstNameLastName((String) execution.getVariable(Constants.VAR_FIRST_NAME_PATH_DI)
								+ " "
								+ execution.getVariable(Constants.VAR_LAST_NAME_PATH_DI)); // mendatory
		u.setBid((String) execution.getVariable(Constants.VAR_BID_PATH_DI)); // mendatory
		u.setEmail((String) execution.getVariable(Constants.VAR_eMAIL_PATH_DI)); // mendatory

		String sexCode = (String) execution.getVariable(Constants.VAR_SEX_PATH_DI);
		u.setSex(User.Sex.valueOf((String) service.getOntologyValue(BASIC_DATA_ARCHETYPE_ID, sexCode))); //checked through archetype
		u.setMobilePhone((String) execution.getVariable(Constants.VAR_MOBILE_TEL_NUM_PATH_DI));
		u.setHealthcareInstitution((String) execution.getVariable(Constants.VAR_HC_INSTITUTION_PATH_DI));

		try {
			u.setDateOfBirth(UserRegistryFactory.parseTimestamp((String) execution.getVariable(Constants.VAR_BIRTH_DATE_PATH_DI)).getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		u.setRole(Role.PATIENT);
		u.setUsername(u.getEmail());
		u.setPassword(UserRegistryFactory.randomstring(6, 8));
		u.setEmployeeType(UserRegistryFactory.EMPLOYEE_TYPE_ENUM.DIABETES);

		// set patient's doctor from session (eOskrba-webApp)
		u = PatientRegistryUtils.setPatientsDoctor(u, (String) execution.getVariable(Constants.VAR_USERNAME_APP));
		

		execution.setVariable(Constants.VAR_PATIENT_OBJ, u);

		return u;
	}
	
	public static void main(String[] argv) {
		putMedicineValueToList(null, "", null);
	}
}
