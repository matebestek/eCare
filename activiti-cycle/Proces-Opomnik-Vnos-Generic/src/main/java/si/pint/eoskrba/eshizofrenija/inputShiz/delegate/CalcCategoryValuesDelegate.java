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
package si.pint.eoskrba.eshizofrenija.inputShiz.delegate;

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
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.delegate.AIzracunKategorijeGenericDelegate;
import si.pint.eoskrba.eastma.inputGeneric.exceptions.MissingProcessVariableException;
import si.pint.eoskrba.eastma.inputGeneric.model.ValueObject;
import si.pint.eoskrba.messages.MessageRepo;
import si.pint.eoskrba.model.Email;
import si.pint.eoskrba.model.User;
//TODO:implementiraj
@Log4j
public class CalcCategoryValuesDelegate extends
		AIzracunKategorijeGenericDelegate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7455250504968650220L;
	
	//template name
	public static final String FIRST_ENCOUNTER_TEMPLATE = "openEHR-EHR-SECTION.vkljucitev_eo.v1";

	//path to weight & height in eXist XML
	public static final String BODY_WEIGHT_EXIST_PATH = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	public static final String BODY_HEIGHT_EXIST_PATH = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.height.v1']/data[@archetype_node_id='at0001']/events[@archetype_node_id='at0002']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		log.info("Racunam kategorijo za urejenost shizofrenije:");
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
			
			String bodyWeight = (String) map.get(Constants.VAR_INPUT_BODY_WEIGHT) != null ? (String) map.get(Constants.VAR_INPUT_BODY_WEIGHT) : "";
			context.put("inputBodyWeight", bodyWeight);
			
			
			Float bodyWeightObj = null; 
					
			if (StringUtils.isNotBlank(bodyWeight)){
				bodyWeightObj = new Float(bodyWeight);
			}
			//pridobim višino
			BigDecimal height= getHeight(((String) execution.getVariable(Constants.VAR_USERNAME_APP)),patient.getUsername());
			context.put("height", height);
			//izračunam novi BMI
			float heightInMeters = height.floatValue()/100;
			float squaredHeightInMeters = heightInMeters*heightInMeters;
			Float newBMI = bodyWeightObj/squaredHeightInMeters;
			context.put("newBMI", newBMI);
			//get current body weight
			BigDecimal bodyWeightFirst = getFirstBodyWeightFromDB(((String) execution.getVariable(Constants.VAR_USERNAME_APP)),patient.getUsername());
			Float oldBMI = bodyWeightFirst.floatValue()/squaredHeightInMeters;
			context.put("oldBMI", oldBMI);			
			//razlika med obema BMI
			float diffBMI = newBMI - oldBMI;
			context.put("diffBMI", diffBMI);
			//calculate category
			if (diffBMI < 0.5) {
				categoryOK = true;
				execution = prepareVarsBMIOK(execution, ve, context, patient);
			} else if (diffBMI >= 1) {
				categoryCritical = true;
				execution = prepareVarsBMICritical(execution, ve, context, patient);
			} else {//med 0,5 in 1 je rumena zona
				categoryNotOk = true;
				execution = prepareVarsBMINotOK(execution, ve, context, patient);
			}
				
			execution.setVariable(Constants.VAR_CATEGORY_OK, categoryOK);
			execution.setVariable(Constants.VAR_CATEGORY_NOT_OK, categoryNotOk);
			execution.setVariable(Constants.VAR_CATEGORY_CRITICAL, categoryCritical);
			execution.setVariable(Constants.VAR_CATEGORY_INVALID, false);

		} else {
			// očitno je neka napaka in rezultat ni prišel skozi. Obvesti administratorja!!
			log.error("Rezultata izpolnjevanja vprašalnika ni na voljo za pacienta "
					+ patient.getBid());
			throw new MissingProcessVariableException("Manjka vnos vrednosti za diabetes pri pacientu: " + patient.getBid());

		}

	}



	private DelegateExecution prepareVarsBMIOK(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
		
		Template template = null;
		
		try {
			template = ve.getTemplate("notify-patient-shiz-OK.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		
		//task name
		execution.setVariable(Constants.VAR_patientUserTaskName, MessageRepo.SHIZOFRENIJA_PATIENT_TASK_NAME_NORMAL);
		return execution;
		
	}
	
	private DelegateExecution prepareVarsBMINotOK(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
		//cm
		Email cmEmail = new Email(patient.getCmEmail(),  
								  "Bolnik " + patient.getFirstNameLastName() + 
								  " ima mejno eno ali vec vrednosti na obrazcu za vnos meritev. Prijavite se prosim v aplikacijo eOskrba, kjer vas caka nova naloga. Uporabite bolnikove kontaktne podatke in preverite kaj je vzrok sprememb vnesenih vrednosti.",
								  "Bolnik " + patient.getFirstNameLastName() + " ima mejno vrednost indeksa telesne mase");
		execution.setVariable(Constants.VAR_caremanagerEmail, cmEmail);

		Template template = null;
		try {
			template = ve.getTemplate("notify-cm-shiz.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyCaremanagerForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_cmUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima mejno vrednost indeksa telesne mase");

		//patient
		Email patientEmail = new Email(patient.getEmail(),						
										MessageRepo.SHIZOFRENIJA_PATIENT_MAIL_VALUES_NOK_CONTENT,
										MessageRepo.SHIZOFRENIJA_PATIENT_MAIL_VALUES_NOK_SUBJECT);
		execution.setVariable(Constants.VAR_patientEmail, patientEmail);
		try {
			template = ve.getTemplate("notify-patient-shiz-NOK.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_patientUserTaskName, MessageRepo.SHIZOFRENIJA_PATIENT_TASK_VALUES_NOK);
		return execution;
		
	}
	
	private DelegateExecution prepareVarsBMICritical(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
		//cm
		Email cmEmail = new Email(patient.getCmEmail(),  
								  "Bolnik " + patient.getFirstNameLastName() + " ima kriticno vrednost indeksa telesne mase. Prijavite se prosim v aplikacijo eOskrba, " +
								  "kjer vas caka nova naloga. Uporabite bolnikove kontaktne podatke in ga usmerite k izbranem zdravniku ali v dezurno ambulanto!",
								  "Bolnik " + patient.getFirstNameLastName() + " ima kritično vnešeno vrednost indeksa telesne mase.");
		execution.setVariable(Constants.VAR_caremanagerEmail, cmEmail);

		Template template = null;
		try {
			template = ve.getTemplate("notify-cm-shiz-critical.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyCaremanagerForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_cmUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima kritično vnešeno vrednost indeksa telesne mase");

		//patient
		Email patientEmail = new Email(patient.getEmail(),						
									   MessageRepo.SHIZOFRENIJA_PATIENT_MAIL_VALUES_CRITICAL_CONTENT,
									   MessageRepo.SHIZOFRENIJA_PATIENT_MAIL_VALUES_CRITICAL_SUBJECT);
		execution.setVariable(Constants.VAR_patientEmail, patientEmail);
		try {
			template = ve.getTemplate("notify-patient-shiz-critical.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_patientUserTaskName, MessageRepo.SHIZOFRENIJA_PATIENT_TASK_VALUES_CRITICAL);

		//doctor
		Email doctorEmail = new Email(patient.getDoctorEmail(),  
				  				  "Bolnik " + patient.getFirstNameLastName() + " ima kriticno vrednost indeksa telesne mase. " +
				  				  "Prijavite se prosim v aplikacijo eOskrba, kjer vas caka nova naloga. V njej so bolnikovi kontaktni podatki. To obvestilo je prejela tudi medicinska sestra " +
				  				  patient.getCaremanager().getFirstNameLastName() + ".",
				  				  "Bolnik " + patient.getFirstNameLastName() + " ima kritično vrednost indeksa telesne mase");
		execution.setVariable(Constants.VAR_doctorEmail, doctorEmail);
	
		try {
			template = ve.getTemplate("notify-doctor-shiz.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyDoctorForm, new ValueObject(sw.toString()));	
		execution.setVariable(Constants.VAR_doctorUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima kritično vrednost indeksa telesne mase");
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
	
	private BigDecimal getHeight(String userId, String patientId) {
		String bwString;
		try {
			bwString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(userId, patientId, FIRST_ENCOUNTER_TEMPLATE, BODY_HEIGHT_EXIST_PATH);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigDecimal height = null;
		try {
			height = new BigDecimal(bwString);
		}
		catch (NumberFormatException nfe) {
		}
		
		return height;
	}

}
