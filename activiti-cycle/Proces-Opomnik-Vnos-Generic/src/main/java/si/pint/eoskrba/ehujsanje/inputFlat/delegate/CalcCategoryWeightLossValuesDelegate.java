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
package si.pint.eoskrba.ehujsanje.inputFlat.delegate;

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

import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
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

/**
 * Calculate 'weight loss' category (also used in process 'Hujsanje-Proces-Opomnik-Korak')
 * 
 * @author Blaz
 *
 */
@Log4j
public class CalcCategoryWeightLossValuesDelegate extends
		AIzracunKategorijeGenericDelegate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7455250504968650220L;
	
	//template name
	public static final String FIRST_ENCOUNTER_TEMPLATE = "openEHR-EHR-SECTION.vkljucitev_eo.v1";
	//path to weight & height in eXist XML
	public static final String BODY_WEIGHT_EXIST_PATH = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	//height path in eXist
	public static final String BODY_HEIGHT_EXIST_PATH = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.height.v1']/data[@archetype_node_id='at0001']/events[@archetype_node_id='at0002']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	
	//template name
	public static final String REMINDER_ENTER_VAL_TEMPLATE = "openEHR-EHR-SECTION.opomnik_eo_hu.v1";
	//template name
	public static final String REMINDER_ENTER_VAL_FREE_TEMPLATE = "openEHR-EHR-SECTION.opomnik_eo_hu_pv.v1";	
	//path to weight & height in eXist XML
	public static final String REMINDER_ENTER_VAL_BODY_WEIGHT_EXIST_PATH = "//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_hu.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	//path to weight & height in eXist XML - free entry
	public static final String REMINDER_ENTER_VAL_BODY_WEIGHT_FREE_EXIST_PATH = "//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_hu_pv.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";

	private boolean freeEntryMode = true;
	
	
	public CalcCategoryWeightLossValuesDelegate() {
		super();
	}
	
	public CalcCategoryWeightLossValuesDelegate(boolean freeEntryMode) {
		super();
		this.freeEntryMode = freeEntryMode;
	}



	@Override
	public void execute(DelegateExecution execution) throws Exception {
		log.info("Racunam kategorijo za urejenost diabetesa:");
		User patient = (User) execution.getVariable(Constants.VAR_PATIENT);
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
								
			if (StringUtils.isNotBlank(bodyWeight))
				bodyWeightObj = new Float(bodyWeight);			
			
			//get current body weight
			BigDecimal bodyWeightFirst = null;
			if (this.freeEntryMode)
				bodyWeightFirst = getLastBodyWeightFromDB(REMINDER_ENTER_VAL_BODY_WEIGHT_FREE_EXIST_PATH, REMINDER_ENTER_VAL_FREE_TEMPLATE, (String) execution.getVariable(Constants.VAR_USERNAME_APP), patient.getUsername());
			else
				bodyWeightFirst = getLastBodyWeightFromDB(REMINDER_ENTER_VAL_BODY_WEIGHT_EXIST_PATH, REMINDER_ENTER_VAL_TEMPLATE, (String) execution.getVariable(Constants.VAR_USERNAME_APP), patient.getUsername());
				
			
			if (bodyWeightFirst == null  || bodyWeightFirst.equals(BigDecimal.ZERO)) 
				bodyWeightFirst = getFirstBodyWeightFromDB((String) execution.getVariable(Constants.VAR_USERNAME_APP), patient.getUsername(), BODY_WEIGHT_EXIST_PATH);			
				
			BigDecimal bwLowerLimit = bodyWeightFirst.subtract(new BigDecimal("1"));
			BigDecimal bwUpperLimit = bodyWeightFirst.add(new BigDecimal("0.5"));
			
			//calculate category
			//if (bodyWeightObj == null || (bodyWeightObj.floatValue() >= bwLowerLimit.floatValue() && bodyWeightObj.floatValue() <= bodyWeightFirst.floatValue())) {
			if (bodyWeightObj == null || (bodyWeightObj.floatValue() >= bwLowerLimit.floatValue() && bodyWeightObj.floatValue() <= bwUpperLimit.floatValue())) {
				boolean idealWeight = false;
				if (bodyWeightObj == null || (bodyWeightObj.floatValue() <= bodyWeightFirst.subtract(new BigDecimal("0.5")).floatValue())) {
					idealWeight = true;
				}
				categoryOK = true;
				execution = prepareVarsValuesOK(execution, ve, context, patient, idealWeight);
				map.put(Constants.VAR_INPUT_BODY_MASS_OK, true); //for caremanager notification
//			} else if (bodyWeightObj != null && bodyWeightObj.floatValue() > bwUpperLimit.floatValue()) { //zaenkrat nimamo kriticne vrednosti
//				categoryCritical = true;
//				categoryNotOk = true;
//				execution = prepareVarsValuesCritical(execution, ve, context, patient);
			} else {
				categoryNotOk = true;
				execution = prepareVarsValuesNotOK(execution, ve, context, patient);
				map.put(Constants.VAR_INPUT_BODY_MASS_OK, false); //for caremanager notification
			}
				
			execution.setVariable(Constants.VAR_CATEGORY_OK, categoryOK);
			execution.setVariable(Constants.VAR_CATEGORY_NOT_OK, categoryNotOk);
			execution.setVariable(Constants.VAR_CATEGORY_CRITICAL, false); //zaenkrat nimamo kriticne vrednosti
			execution.setVariable(Constants.VAR_GENERIC_RESULT, map);

		} else {
			// očitno je neka napaka in rezultat ni prišel skozi. Obvesti administratorja!!
			log.error("Rezultata izpolnjevanja vprašalnika ni na voljo za pacienta "
					+ patient.getBid());
			throw new MissingProcessVariableException("Manjka vnos vrednosti za diabetes pri pacientu: " + patient.getBid());

		}

	}



	private DelegateExecution prepareVarsValuesOK(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient, boolean idealWeight) {
		
		Template template = null;
		
		try {
			if (idealWeight)
				template = ve.getTemplate("notify-patient-weight-OK.vm","UTF-8");
			else
				template = ve.getTemplate("notify-patient-weight-no-changes.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		
		//task name
		execution.setVariable(Constants.VAR_patientUserTaskName, MessageRepo.HUJSANJE_PATIENT_TASK_WEIGHT_NORMAL);
		return execution;
		
	}
	
	public DelegateExecution prepareVarsValuesNotOK(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
		//cm
		Email cmEmail = new Email(patient.getDoctorEmail(),  //specific for eHusjanje: assigned coordinator is written in LDAP atribute 'patientsDoctorAtt'
								  "Bolnik " + patient.getFirstNameLastName() + 
								  " ima spremenjeno telesno težo glede na prejšnjo meritev. Prijavite se prosim v aplikacijo eOskrba, kjer vas čaka nova naloga. Uporabite bolnikove kontaktne podatke in preverite kaj je vzrok spremembe vrednosti.",
								  "Bolnik " + patient.getFirstNameLastName() + " ima spremenjeno telesno težo");
		cmEmail.setFrom(SystemConstant.EMAIL_HUJSANJE.toString());
		execution.setVariable(Constants.VAR_caremanagerEmail, cmEmail);
		
		//periodic task
		if (!this.freeEntryMode) {
			cmEmail.setContent("Bolnik " + patient.getFirstNameLastName() + 
					  	       " ima spremenjeno telesno težo (ali neustrezen vnos živil) glede na prejšnjo meritev. Prijavite se prosim v aplikacijo eOskrba, kjer vas čaka nova naloga. Uporabite bolnikove kontaktne podatke in preverite kaj je vzrok spremembe vrednosti.");
			cmEmail.setSubject("Bolnik " + patient.getFirstNameLastName() + " ima spremenjeno telesno težo/neustrezen vnos živil");
		}

		Template template = null;
		try {
			if (this.freeEntryMode)
				template = ve.getTemplate("notify-cm-hujsanje-NOK.vm","UTF-8");
			else
				template = ve.getTemplate("notify-cm-hujsanje-NOK-opkp-results.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyCaremanagerForm, new ValueObject(sw.toString()));
		if (this.freeEntryMode)
			execution.setVariable(Constants.VAR_cmUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima spremenjeno telesno težo");
		else
			execution.setVariable(Constants.VAR_cmUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima spremenjeno telesno težo/neustrezen vnos živil");

		//patient
		Email patientEmail = new Email(patient.getEmail(),						
										MessageRepo.HUJSANJE_PATIENT_MAIL_VALUES_NOK_CONTENT,
										MessageRepo.HUJSANJE_PATIENT_MAIL_VALUES_NOK_SUBJECT);
		if (!this.freeEntryMode) {
			patientEmail.setContent(MessageRepo.HUJSANJE_PATIENT_MAIL_VALUES_NOK_CONTENT_OPKP_RESULTS);
			patientEmail.setSubject(MessageRepo.HUJSANJE_PATIENT_MAIL_VALUES_NOK_SUBJECT_OPKP_RESULTS);
		}
			
		patientEmail.setFrom(SystemConstant.EMAIL_HUJSANJE.toString());
		execution.setVariable(Constants.VAR_patientEmail, patientEmail);
		try {
			if (this.freeEntryMode)
				template = ve.getTemplate("notify-patient-weight-NOK.vm","UTF-8");
			else 
				template = ve.getTemplate("notify-patient-weight-NOK-opkp-results.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		if (this.freeEntryMode)
			execution.setVariable(Constants.VAR_patientUserTaskName, MessageRepo.HUJSANJE_PATIENT_TASK_WEIGHT_NOT_OK);
		else
			execution.setVariable(Constants.VAR_patientUserTaskName, MessageRepo.HUJSANJE_PATIENT_TASK_WEIGHT_NOT_OK_OPKP_RESULTS);
		
		return execution;
		
	}
	
//	private DelegateExecution prepareVarsValuesCritical(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
//		//cm
//		Email cmEmail = new Email(patient.getCmEmail(),  
//								  "Bolnik " + patient.getFirstNameLastName() + " ima kriticno eno ali vec od vnesenih vrednosti (krvni tlak, krvni sladkor ali telesna teza). Prijavite se prosim v aplikacijo eOskrba, " +
//								  "kjer vas caka nova naloga. Uporabite bolnikove kontaktne podatke in ga usmerite k izbranem zdravniku ali v dezurno ambulanto!",
//								  "Bolnik " + patient.getFirstNameLastName() + " ima kritično eno ali več vnešenih vrednosti (krvni tlak, krvni sladkor ali telesna teža)");
//		cmEmail.setFrom(SystemConstant.EMAIL_DIABETES.toString());
//		execution.setVariable(Constants.VAR_caremanagerEmail, cmEmail);
//
//		Template template = null;
//		try {
//			template = ve.getTemplate("notify-cm-diab-critical.vm","UTF-8");
//		} catch (Exception e) {
//			log.error("Velocity napaka", e);
//		}
//		StringWriter sw = new StringWriter();
//		template.merge(context, sw);
//		execution.setVariable(Constants.VAR_notifyCaremanagerForm, new ValueObject(sw.toString()));
//		execution.setVariable(Constants.VAR_cmUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima kritično eno ali več vnešenih vrednosti (krvni tlak, krvni sladkor ali telesna teža)");
//
//		//patient
//		Email patientEmail = new Email(patient.getEmail(),						
//									   MessageRepo.DIABETES_PATIENT_MAIL_VALUES_CRITICAL_CONTENT,
//									   MessageRepo.DIABETES_PATIENT_MAIL_VALUES_CRITICAL_SUBJECT);
//		patientEmail.setFrom(SystemConstant.EMAIL_DIABETES.toString());
//		execution.setVariable(Constants.VAR_patientEmail, patientEmail);
//		try {
//			template = ve.getTemplate("notify-patient-diab-critical.vm","UTF-8");
//		} catch (Exception e) {
//			log.error("Velocity napaka", e);
//		}
//		sw = new StringWriter();
//		template.merge(context, sw);
//		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
//		execution.setVariable(Constants.VAR_patientUserTaskName, MessageRepo.DIABETES_PATIENT_TASK_VALUES_CRITICAL);
//
//		//doctor
//		Email doctorEmail = new Email(patient.getDoctorEmail(),  
//				  				  "Bolnik " + patient.getFirstNameLastName() + " ima kriticno eno ali vec od vnesenih vrednosti (krvni tlak, krvni sladkor ali telesna teza). " +
//				  				  "Prijavite se prosim v aplikacijo eOskrba, kjer vas caka nova naloga. V njej so bolnikovi kontaktni podatki. To obvestilo je prejela tudi medicinska sestra " +
//				  				  patient.getCaremanager().getFirstNameLastName() + ".",
//				  				  "Bolnik " + patient.getFirstNameLastName() + " ima kritično eno ali več vnešenih vrednosti (krvni tlak, krvni sladkor ali telesna teža)");
//		doctorEmail.setFrom(SystemConstant.EMAIL_DIABETES.toString());
//		execution.setVariable(Constants.VAR_doctorEmail, doctorEmail);
//	
//		try {
//			template = ve.getTemplate("notify-doctor-diab.vm","UTF-8");
//		} catch (Exception e) {
//			log.error("Velocity napaka", e);
//		}
//		sw = new StringWriter();
//		template.merge(context, sw);
//		execution.setVariable(Constants.VAR_notifyDoctorForm, new ValueObject(sw.toString()));	
//		execution.setVariable(Constants.VAR_doctorUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima kritično eno ali več vnešenih vrednosti (krvni tlak, krvni sladkor ali telesna teža)");
//		return execution;
//	}	
	
	public static BigDecimal getFirstBodyWeightFromDB(String userId, String patientId, String path) {
		String bwString;
		try {
			bwString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(userId, patientId, FIRST_ENCOUNTER_TEMPLATE, path);
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
	
	
	/**
	 * Get last entered body weight from DB
	 * 
	 * @param userId
	 * @param patientId
	 * @return
	 */
	private static BigDecimal getLastBodyWeightFromDB(String path, String template, String userId, String patientId) {
		String[] bwString = null;
		try {
			bwString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLastTwo(userId, patientId, EMPLOYEE_TYPE_ENUM.HUJSANJE, template, path);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigDecimal bodyWeight = null;
		try {
			if (bwString != null && bwString.length > 1 && bwString[1] != null)
				bodyWeight = new BigDecimal(bwString[1]);
		}
		catch (NumberFormatException nfe) {
		}
		
		return bodyWeight;
	}


}
