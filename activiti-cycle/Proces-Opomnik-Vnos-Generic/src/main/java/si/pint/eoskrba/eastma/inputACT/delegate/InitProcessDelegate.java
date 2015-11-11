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
package si.pint.eoskrba.eastma.inputACT.delegate;

import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.delegate.AInitProcessDelegate;
import si.pint.eoskrba.eastma.inputGeneric.model.ValueObject;
import si.pint.eoskrba.model.Email;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;

@Log4j
public class InitProcessDelegate extends AInitProcessDelegate implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7450885070699274734L;

	@Override
	@SuppressWarnings("unused")
	public void execute(DelegateExecution execution) throws Exception {
		
		log.info("Inicializacija procesnih spremenljivk za Opomnik-Vnos-Generic");
		
		// get user (patient) from session
		String subjectString = (String) execution
				.getVariable(Constants.VAR_PATIENT_OBJ);
		//subjectString = "{\"sex\":\"MALE\",\"height\":\"183\",\"weight\":\"85\",\"eMail\":\"EMAIL@DOMAIN.COM\",\"role\":\"1\",\"birthDate\":\"376929213750\",\"bid\":\"ivan\",\"username\":\"ivan\",\"mobilePhoneNum\":\"041485751\"}";
		//log.fatal("TOLE JE SAMO ZA POTREBE TESTIRANJA!!!!!!!!!!!!!!!!!!! User mora prit od drugega procesa torej od zunaj!");
		if (subjectString == null) {
			throw new RuntimeException(
					"User on proces 'Opomnik-Vnos-Generic' not defined!");
			// TODO: go to end event
		}

		
		
		JSONObject subjectJSON = new JSONObject(subjectString);

		User patient = new User();
		patient.setBid((String) subjectJSON.get(Constants.VAR_BID));
		patient.setEmail((String) subjectJSON.get(Constants.VAR_eMAIL));
		patient.setMobilePhone((String) subjectJSON
				.get(Constants.VAR_MOBILE_TEL_NUM));
		patient.setSex(subjectJSON.get(Constants.VAR_SEX).equals(
				User.Sex.MALE.toString()) ? User.Sex.MALE : User.Sex.FEMALE);

		patient.setDateOfBirth(UserRegistryFactory.parseTimestamp((String) (subjectJSON.get(Constants.VAR_BIRTH_DATE))).getTime());

		patient.setWeight(new BigDecimal((String) subjectJSON
				.get(Constants.VAR_WEIGHT)));
		patient.setHeight(new BigDecimal((String) subjectJSON
				.get(Constants.VAR_HEIGHT)));
		patient.setRole(Role.getRoleFromId((String) subjectJSON
				.get(Constants.VAR_ROLE)));
		patient.setUsername((String) subjectJSON.get(Constants.VAR_USERNAME));
		patient.setFirstNameLastName((String) subjectJSON.get(Constants.VAR_FIRST_NAME_LAST_NAME));
		patient.setPatientsCM(patient.getCaremanager().getUsername());
		
		execution.setVariable(Constants.VAR_PATIENT, patient);
		
		//cleanup
		execution.removeVariable(Constants.VAR_PATIENT_OBJ);
		
		HashMap<String, Object> variables = new HashMap<String, Object>();

		// Velocity uporabljan za template-e. Pretvorim jih v String in jih
		// podtaknem v .form datoteke
		VelocityEngine ve = new VelocityEngine();
		//ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
		Properties p = new Properties();
		p.setProperty("resource.loader","class,jar");
		p.setProperty("class.resource.loader.description","Velocity Classpath Resource Loader");
		p.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);		

		VelocityContext context = new VelocityContext();
		context.put("patient",patient);
		
		Template template = null;
		try {
			template = ve.getTemplate("pacientReminderEmail-Content.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		
		Template template1 = null;
		try {
			template1 = ve.getTemplate("pacientReminderEmail-Subject.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw1 = new StringWriter();
		template1.merge(context, sw1);
		
		Email patientEmail = new Email(patient.getEmail(), sw.toString(), sw1.toString());
		variables.put(Constants.VAR_patientReminderEmail, patientEmail);
		
		//zdej pripravim template še za UserTask
		Template templateACT = null;
		try {
			templateACT = ve.getTemplate("vnos-vrednosti-ACT.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter swACT = new StringWriter();
		templateACT.merge(context, swACT);
		
		variables.put(Constants.VAR_vnosVrednostiForm, new ValueObject(swACT.toString()));
		variables.put(Constants.VAR_enterActTaskName, "Izpolnite vprašalnik ACT");		

		variables.put(Constants.VAR_maxNumberOfSentReminders, 3);
		variables.put(Constants.VAR_reminderPacientIntervalDuration, "PT24H");// TODO:
																				// spremeni
																				// na
																				// PT24H
		execution.setVariables(variables);

	}

}
