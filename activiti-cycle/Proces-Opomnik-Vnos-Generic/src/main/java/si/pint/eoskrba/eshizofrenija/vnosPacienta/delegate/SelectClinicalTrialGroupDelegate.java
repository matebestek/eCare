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
package si.pint.eoskrba.eshizofrenija.vnosPacienta.delegate;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.log4j.MDC;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.openehr.validation.ValidationError;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.ArchetypeService;
import si.pint.archetypes.service.VnosPacientaShizofrenijaArcheSrv;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.model.ValueObject;
import si.pint.eoskrba.eastma.model.RandomGroupGenerator;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;
import si.pint.eoskrba.utils.PatientRegistryUtils;


@Log4j
public class SelectClinicalTrialGroupDelegate implements JavaDelegate {
	// template path for 'included in study'
	public static final String TEMPLATE_PATH_INCLUDED_IN_STUDY = "/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0012]/value";
	// 'other conditions' template path
	public static final String TEMPLATE_PATH_OTHER_CONDITIONS = "/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0011]/value";
	//archetype id for 'basic patient data'
	public static final String BASIC_DATA_ARCHETYPE_ID = "openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1";
	

	public void execute(DelegateExecution execution) throws Exception {
		boolean satisfyingConditions = false;

		VnosPacientaShizofrenijaArcheSrv archeService = new VnosPacientaShizofrenijaArcheSrv();
		if (!archeService.initInput())
			return;

		// user input data
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();

		// look for all possible attributes on session
		Map allAtributes = archeService.getInputsMap();
		Iterator it = allAtributes.keySet().iterator();
		boolean otherConditions = false;

		while (it.hasNext()) {
			String key = (String) it.next();
			String keyR = key.replaceAll("/", "?");
			keyR = keyR.replaceAll("_", "!");

			Object var = execution.getVariable(keyR);
			Set s = execution.getVariableNames();

			if (var != null && !var.equals("")) {
				userInputsMap.put(new ArcheDataPath(key), var);
				if (key.indexOf(TEMPLATE_PATH_OTHER_CONDITIONS) >= 0) {
					if(var instanceof String){
						otherConditions = Boolean.parseBoolean((String) var);
					} else if(var instanceof Boolean){
						otherConditions = (Boolean) var;
					}
					
					
				}
				
			}
		}

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
			isExperimentalGroup = RandomGroupGenerator.getRandomGroup(archeService, loggedMedical,u.getUsername());
			//
			// add user to LDAP
			if (satisfyingConditions && isExperimentalGroup)
				UserRegistryFactory.addPatient(u);

			patientDataParseOk = true;
		}
		
		
			
		execution.setVariable(Constants.VAR_DOCTOR, UserRegistryFactory.getUserFromLDAP(loggedMedical));
		
		//logging
		MDC.put("user", u.getPatientsDoctor());
		MDC.put("userRole", UserRegistryFactory.getUserFromLDAP(u.getPatientsDoctor()).getRole().getName());
		MDC.put("task", "Dodajanje novega pacienta v študijo");
		MDC.put("taskContent", "");
		MDC.put("taskType", EMPLOYEE_TYPE_ENUM.SHIZOFRENIJA.toDomainName());
		
		if (patientDataParseOk) {// save data in XML form to DB - for all patients
			if (!satisfyingConditions)
				userInputsMap.put(new ArcheDataPath(TEMPLATE_PATH_INCLUDED_IN_STUDY), "false");
			else
				userInputsMap.put(new ArcheDataPath(TEMPLATE_PATH_INCLUDED_IN_STUDY), "true");
			
			archeService.setAndValidataData(userInputsMap); // repopulate RMObject
			archeService.saveInput(SystemType.EOSKRBAPROCESSENGINE,((String) execution.getVariable(Constants.VAR_USERNAME_APP)),u.getUsername());			
			
			log.info("V studijo za shizofrenijo je dodan nov pacient: " + u.getUsername());
		}

		// nastavim spremenljivk skupinaEksperiment, skupinaKontrolna ali
		// notSatisfyingConditions
		if (!satisfyingConditions) {
			execution.setVariable(Constants.VAR_notSatisfyingConditions, true);
			execution.setVariable(Constants.VAR_skupinaEksperiment, false);
			execution.setVariable(Constants.VAR_skupinaKontrolna, false);

			setTemplateContent(execution, u, "notify-doctor-notIncludedInStudy.vm",	Constants.VAR_notifyDoctorForm);
			execution.setVariable(Constants.VAR_doctorUserTaskName, "Bolnik " + u.getFirstNameLastName() + " ne ustreza kriterijem za študijo!");
			log.info("Bolnik " + u.getFirstNameLastName() + " ne ustreza kriterijem za študijo shizofrenije!");
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

	private User createUserFromContext(DelegateExecution execution,	ArchetypeService service) {
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
		u.setEmployeeType(UserRegistryFactory.EMPLOYEE_TYPE_ENUM.SHIZOFRENIJA);

		// set patient's doctor from session (eOskrba-webApp)
		u = PatientRegistryUtils.setPatientsDoctor(u, (String) execution.getVariable(Constants.VAR_USERNAME_APP));

		execution.setVariable(Constants.VAR_PATIENT_OBJ, u);

		return u;
	}
}
