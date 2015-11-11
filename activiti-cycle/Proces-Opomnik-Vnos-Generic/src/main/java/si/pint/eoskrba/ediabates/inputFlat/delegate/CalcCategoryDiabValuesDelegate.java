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
package si.pint.eoskrba.ediabates.inputFlat.delegate;

import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.conf.SystemConstant;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.delegate.AIzracunKategorijeGenericDelegate;
import si.pint.eoskrba.eastma.inputGeneric.exceptions.MissingProcessVariableException;
import si.pint.eoskrba.eastma.inputGeneric.model.ValueObject;
import si.pint.eoskrba.messages.MessageRepo;
import si.pint.eoskrba.model.Email;
import si.pint.eoskrba.model.User;

@Log4j
public class CalcCategoryDiabValuesDelegate extends
		AIzracunKategorijeGenericDelegate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7455250504968650220L;
	
	//template name
	public static final String FIRST_ENCOUNTER_TEMPLATE = "openEHR-EHR-SECTION.vkljucitev_eo.v1";

	//path to weight & height in eXist XML
	public static final String BODY_WEIGHT_EXIST_PATH = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";


	@Override
	public void execute(DelegateExecution execution) throws Exception {
		log.info("Racunam kategorijo za urejenost diabetesa:");
		User patient = (User) execution.getVariable(Constants.VAR_PATIENT);
		execution.setVariable(Constants.VAR_DOCTOR, patient.getDoctor());
		Object obj = null;
		VelocityEngine ve = new VelocityEngine();
		Properties p = new Properties();
		p.setProperty("resource.loader","class,jar");
		p.setProperty("class.resource.loader.description","Velocity Classpath Resource Loader");
		p.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);		
		VelocityContext context = new VelocityContext();	
		context.put("patient",patient);
		
		if ((obj = execution.getVariable(Constants.VAR_GENERIC_RESULT)) != null
				|| (obj = execution.getVariableLocal(Constants.VAR_GENERIC_RESULT)) != null) {
			boolean categoryOK = false;
			boolean categoryCritical = false;
			boolean categoryNotOk = false;
			
			//get entered values
			Map map = (Map) obj;
			
			String sugar = (String) map.get(Constants.VAR_INPUT_SUGAR) != null ? (String) map.get(Constants.VAR_INPUT_SUGAR) : "";
			String diastolicBp = (String) map.get(Constants.VAR_INPUT_DIASTOLIC_BP) != null ? (String) map.get(Constants.VAR_INPUT_DIASTOLIC_BP) : "";
			String sistolicBp = (String) map.get(Constants.VAR_INPUT_SISTOLIC_BP) != null ? (String) map.get(Constants.VAR_INPUT_SISTOLIC_BP) : "";
			String bodyWeight = (String) map.get(Constants.VAR_INPUT_BODY_WEIGHT) != null ? (String) map.get(Constants.VAR_INPUT_BODY_WEIGHT) : "";
			
			context.put("inputSugar", sugar);
			context.put("inputDiastolicBP", diastolicBp);
			context.put("inputSistolicBP", sistolicBp);
			context.put("inputBodyWeight", bodyWeight);
			
			Float sugarObj = null;
			Integer diastolicBpObj = null;
			Integer sistolicBpObj = null;
			Float bodyWeightObj = null; 
					
			if (StringUtils.isNotBlank(sugar))
				sugarObj = new Float(sugar);
			
			if (StringUtils.isNotBlank(diastolicBp))
				diastolicBpObj = new Integer(diastolicBp);
			
			if (StringUtils.isNotBlank(sistolicBp))
				sistolicBpObj = new Integer(sistolicBp);
			
			if (StringUtils.isNotBlank(bodyWeight))
				bodyWeightObj = new Float(bodyWeight);			
			
			//get current body weight
			BigDecimal bodyWeightFirst = getFirstBodyWeightFromDB(((String) execution.getVariable(Constants.VAR_USERNAME_APP)),patient.getUsername());
			BigDecimal bwLowerLimit = (new BigDecimal("0.95")).multiply(bodyWeightFirst);
			BigDecimal bwUpperLimit = (new BigDecimal("1.03")).multiply(bodyWeightFirst);			
			
			//calculate category
			if ((sugarObj == null || sugarObj.floatValue() < 6.1) && (diastolicBpObj == null || diastolicBpObj.intValue() < 84) && (sistolicBpObj == null || sistolicBpObj.intValue() <= 129) && (bodyWeightObj == null || (bodyWeightObj.floatValue() >= bwLowerLimit.floatValue() && bodyWeightObj.floatValue() <= bodyWeightFirst.floatValue()))) {
				categoryOK = true;
				execution = prepareVarsValuesOK(execution, ve, context, patient);
			} else if ((sugarObj != null && sugarObj.floatValue() > 7.1) || (diastolicBpObj != null && diastolicBpObj.intValue() >= 100) || (sistolicBpObj != null && sistolicBpObj.intValue() >= 160) || (bodyWeightObj != null && bodyWeightObj.floatValue() > bwUpperLimit.floatValue())) {
				categoryCritical = true;
				execution = prepareVarsValuesCritical(execution, ve, context, patient);
			} else {
				categoryNotOk = true;
				execution = prepareVarsValuesNotOK(execution, ve, context, patient);
			}
				
			execution.setVariable(Constants.VAR_CATEGORY_OK, categoryOK);
			execution.setVariable(Constants.VAR_CATEGORY_NOT_OK, categoryNotOk);
			execution.setVariable(Constants.VAR_CATEGORY_CRITICAL, categoryCritical);

		} else {
			// očitno je neka napaka in rezultat ni prišel skozi. Obvesti administratorja!!
			log.error("Rezultata izpolnjevanja vprašalnika ni na voljo za pacienta "
					+ patient.getBid());
			throw new MissingProcessVariableException("Manjka vnos vrednosti za diabetes pri pacientu: " + patient.getBid());

		}

	}



	private DelegateExecution prepareVarsValuesOK(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
		
		Template template = null;
		
		try {
			template = ve.getTemplate("notify-patient-diab-OK.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		
		//task name
		execution.setVariable(Constants.VAR_patientUserTaskName, MessageRepo.DIABETES_PATIENT_TASK_NAME_NORMAL);
		return execution;
		
	}
	
	private DelegateExecution prepareVarsValuesNotOK(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
		//cm
		Email cmEmail = new Email(patient.getCmEmail(),  
								  "Bolnik " + patient.getFirstNameLastName() + 
								  " ima mejno eno ali vec vrednosti na obrazcu za vnos meritev. Prijavite se prosim v aplikacijo eOskrba, kjer vas čaka nova naloga. Uporabite bolnikove kontaktne podatke in preverite kaj je vzrok padca vnesenih vrednosti.",
								  "Bolnik " + patient.getFirstNameLastName() + " ima mejno eno ali več vrednosti (krvni tlak, sladkor, teža)");
		cmEmail.setFrom(SystemConstant.EMAIL_DIABETES.toString());
		execution.setVariable(Constants.VAR_caremanagerEmail, cmEmail);

		Template template = null;
		try {
			template = ve.getTemplate("notify-cm-diab.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyCaremanagerForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_cmUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima mejno eno ali več od vnešenih vrednosti (krvni sladkor, krvni tlak ali telesna teža)");

		//patient
		Email patientEmail = new Email(patient.getEmail(),						
										MessageRepo.DIABETES_PATIENT_MAIL_VALUES_NOK_CONTENT,
										MessageRepo.DIABETES_PATIENT_MAIL_VALUES_NOK_SUBJECT);
		patientEmail.setFrom(SystemConstant.EMAIL_DIABETES.toString());
		execution.setVariable(Constants.VAR_patientEmail, patientEmail);
		try {
			template = ve.getTemplate("notify-patient-diab-NOK.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_patientUserTaskName, MessageRepo.DIABETES_PATIENT_TASK_VALUES_NOK);
		return execution;
		
	}
	
	private DelegateExecution prepareVarsValuesCritical(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
		//cm
		Email cmEmail = new Email(patient.getCmEmail(),  
								  "Bolnik " + patient.getFirstNameLastName() + " ima kriticno eno ali vec od vnesenih vrednosti (krvni tlak, krvni sladkor ali telesna teža). Prijavite se prosim v aplikacijo eOskrba, " +
								  "kjer vas čaka nova naloga. Uporabite bolnikove kontaktne podatke in ga usmerite k izbranem zdravniku ali v dežurno ambulanto!",
								  "Bolnik " + patient.getFirstNameLastName() + " ima kritično eno ali več vnešenih vrednosti (krvni tlak, krvni sladkor ali telesna teža)");
		cmEmail.setFrom(SystemConstant.EMAIL_DIABETES.toString());
		execution.setVariable(Constants.VAR_caremanagerEmail, cmEmail);

		Template template = null;
		try {
			template = ve.getTemplate("notify-cm-diab-critical.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyCaremanagerForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_cmUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima kritično eno ali več vnešenih vrednosti (krvni tlak, krvni sladkor ali telesna teža)");

		//patient
		Email patientEmail = new Email(patient.getEmail(),						
									   MessageRepo.DIABETES_PATIENT_MAIL_VALUES_CRITICAL_CONTENT,
									   MessageRepo.DIABETES_PATIENT_MAIL_VALUES_CRITICAL_SUBJECT);
		patientEmail.setFrom(SystemConstant.EMAIL_DIABETES.toString());
		execution.setVariable(Constants.VAR_patientEmail, patientEmail);
		try {
			template = ve.getTemplate("notify-patient-diab-critical.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_patientUserTaskName, MessageRepo.DIABETES_PATIENT_TASK_VALUES_CRITICAL);

		//doctor
		Email doctorEmail = new Email(patient.getDoctorEmail(),  
				  				  "Bolnik " + patient.getFirstNameLastName() + " ima kriticno eno ali vec od vnesenih vrednosti (krvni tlak, krvni sladkor ali telesna teža). " +
				  				  "Prijavite se prosim v aplikacijo eOskrba, kjer vas čaka nova naloga. V njej so bolnikovi kontaktni podatki. To obvestilo je prejela tudi medicinska sestra " +
				  				  patient.getCaremanager().getFirstNameLastName() + ".",
				  				  "Bolnik " + patient.getFirstNameLastName() + " ima kritično eno ali več vnešenih vrednosti (krvni tlak, krvni sladkor ali telesna teža)");
		doctorEmail.setFrom(SystemConstant.EMAIL_DIABETES.toString());
		execution.setVariable(Constants.VAR_doctorEmail, doctorEmail);
	
		try {
			template = ve.getTemplate("notify-doctor-diab.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyDoctorForm, new ValueObject(sw.toString()));	
		execution.setVariable(Constants.VAR_doctorUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima kritično eno ali več vnešenih vrednosti (krvni tlak, krvni sladkor ali telesna teža)");
		return execution;
	}	
	
	private BigDecimal getFirstBodyWeightFromDB(String userId, String patientId) {
		String bwString;
		try {
			bwString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(userId,patientId, FIRST_ENCOUNTER_TEMPLATE, BODY_WEIGHT_EXIST_PATH);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigDecimal bodyWeight = null;
		try {
			bodyWeight = new BigDecimal(bwString);
		}
		catch (NumberFormatException nfe) {
		}
		
		return bodyWeight;
	}

}
