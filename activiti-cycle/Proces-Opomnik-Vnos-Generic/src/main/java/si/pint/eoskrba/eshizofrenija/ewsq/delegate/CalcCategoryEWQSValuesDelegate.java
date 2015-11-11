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
package si.pint.eoskrba.eshizofrenija.ewsq.delegate;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.delegate.AIzracunKategorijeGenericDelegate;
import si.pint.eoskrba.eastma.inputGeneric.exceptions.MissingProcessVariableException;
import si.pint.eoskrba.eastma.inputGeneric.model.ValueObject;
import si.pint.eoskrba.messages.MessageRepo;
import si.pint.eoskrba.model.Email;
import si.pint.eoskrba.model.User;
@Log4j
public class CalcCategoryEWQSValuesDelegate extends
		AIzracunKategorijeGenericDelegate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7455250504968650220L;
	
	//template name
	public static final String FIRST_ENCOUNTER_TEMPLATE = "openEHR-EHR-SECTION.vkljucitev_eo.v1";



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
			
			String vsotaEQWS = (String) map.get(Constants.VAR_INPUT_EQSW);
			int vsota = Integer.parseInt(vsotaEQWS);
			int[] counterAnsw= (int[])map.get(Constants.VAR_INPUT_EQSW_VAL_LIST);
			//tabela je velika 5...za vrednosti od 0 do 4
			
			context.put("inputVsotaEQSW", vsotaEQWS);
			context.put("inputCount0", counterAnsw[0]);
			context.put("inputCount1", counterAnsw[1]);
			context.put("inputCount2", counterAnsw[2]);
			context.put("inputCount3", counterAnsw[3]);
			context.put("inputCount4", counterAnsw[4]);
			
			
			//calculate category
			if(counterAnsw[3] >= 3 || counterAnsw[4] >= 2 || vsota >= 21){//CRITICAL-RDEČE
				categoryCritical = true;
				execution = prepareVarsEQSWCritical(execution, ve, context, patient);
			} else if(counterAnsw[3] == 1 || counterAnsw[3] == 2 || counterAnsw[4] == 1 || (vsota >= 10 && vsota <= 20 ) ){//ZMERNO poslabšanje --RUMENO
				categoryNotOk = true;
				execution = prepareVarsEQSWNotOK(execution, ve, context, patient);
			} else{//OK
				categoryOK = true;
				execution = prepareVarsEQSWOK(execution, ve, context, patient);
			}
			
			execution.setVariable(Constants.VAR_CATEGORY_OK, categoryOK);
			execution.setVariable(Constants.VAR_CATEGORY_NOT_OK, categoryNotOk);
			execution.setVariable(Constants.VAR_CATEGORY_CRITICAL, categoryCritical);
			execution.setVariable(Constants.VAR_CATEGORY_INVALID, false);

		} else {
			// očitno je neka napaka in rezultat ni prišel skozi. Obvesti administratorja!!
			log.error("Rezultata izpolnjevanja vprašalnika ni na voljo za pacienta "
					+ patient.getBid());
			throw new MissingProcessVariableException("Manjka vnos vrednosti za EQWS pri pacientu: " + patient.getBid());

		}

	}



	private DelegateExecution prepareVarsEQSWOK(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
		
		Template template = null;
		
		try {
			template = ve.getTemplate("notify-patient-shiz-eqws-OK.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		
		//task name
		execution.setVariable(Constants.VAR_patientUserTaskName, MessageRepo.SHIZOFRENIJA_EQWS_PATIENT_TASK_NAME_NORMAL);
		return execution;
		
	}
	
	private DelegateExecution prepareVarsEQSWNotOK(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
		//cm
		Email cmEmail = new Email(patient.getCmEmail(),  
								  "Bolnik " + patient.getFirstNameLastName() + 
								  " ima mejno eno ali vec vrednosti na obrazcu za vnos meritev. Prijavite se prosim v aplikacijo eOskrba, kjer vas caka nova naloga. Uporabite bolnikove kontaktne podatke in preverite kaj je vzrok padca vnesenih vrednosti.",
								  "Bolnik " + patient.getFirstNameLastName() + " ima mejno eno ali vec vrednosti vprašalnika");
		execution.setVariable(Constants.VAR_caremanagerEmail, cmEmail);

		Template template = null;
		try {
			template = ve.getTemplate("notify-cm-shiz-eqws.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyCaremanagerForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_cmUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima mejno eno ali več od vnešenih vrednosti vprašalnika");

		//patient
		Email patientEmail = new Email(patient.getEmail(),						
										MessageRepo.SHIZOFRENIJA_EQWS_PATIENT_MAIL_VALUES_NOK_CONTENT,
										MessageRepo.SHIZOFRENIJA_EQWS_PATIENT_MAIL_VALUES_NOK_SUBJECT);
		execution.setVariable(Constants.VAR_patientEmail, patientEmail);
		try {
			template = ve.getTemplate("notify-patient-shiz-eqws-NOK.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_patientUserTaskName, MessageRepo.SHIZOFRENIJA_EQWS_PATIENT_TASK_VALUES_NOK);
		return execution;
		
	}
	
	private DelegateExecution prepareVarsEQSWCritical(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
		//cm
		Email cmEmail = new Email(patient.getCmEmail(),  
								  "Bolnik " + patient.getFirstNameLastName() + " ima kriticno eno ali vec od vnesenih vrednosti vprašalnika. Prijavite se prosim v aplikacijo eOskrba, " +
								  "kjer vas caka nova naloga. Uporabite bolnikove kontaktne podatke in ga usmerite k izbranem zdravniku ali v dezurno ambulanto!",
								  "Bolnik " + patient.getFirstNameLastName() + " ima kritično eno ali več vnešenih vrednosti vprašalnika");
		execution.setVariable(Constants.VAR_caremanagerEmail, cmEmail);

		Template template = null;
		try {
			template = ve.getTemplate("notify-cm-shiz-eqws-critical.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyCaremanagerForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_cmUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima kritično eno ali več vnešenih vrednosti vprašalnika");

		//patient
		Email patientEmail = new Email(patient.getEmail(),						
									   MessageRepo.SHIZOFRENIJA_EQWS_PATIENT_MAIL_VALUES_CRITICAL_CONTENT,
									   MessageRepo.SHIZOFRENIJA_EQWS_PATIENT_MAIL_VALUES_CRITICAL_SUBJECT);
		execution.setVariable(Constants.VAR_patientEmail, patientEmail);
		try {
			template = ve.getTemplate("notify-patient-shiz-eqws-critical.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_patientUserTaskName, MessageRepo.SHIZOFRENIJA_EQWS_PATIENT_TASK_VALUES_CRITICAL);

		//doctor
		Email doctorEmail = new Email(patient.getDoctorEmail(),  
				  				  "Bolnik " + patient.getFirstNameLastName() + " ima kriticno eno ali vec od vnesenih vrednosti vprašalnika. " +
				  				  "Prijavite se prosim v aplikacijo eOskrba, kjer vas caka nova naloga. V njej so bolnikovi kontaktni podatki. To obvestilo je prejela tudi medicinska sestra " +
				  				  patient.getCaremanager().getFirstNameLastName() + ".",
				  				  "Bolnik " + patient.getFirstNameLastName() + " ima kritično eno ali več vnešenih vrednosti vprašalnika");
		execution.setVariable(Constants.VAR_doctorEmail, doctorEmail);
	
		try {
			template = ve.getTemplate("notify-doctor-shiz-eqws.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyDoctorForm, new ValueObject(sw.toString()));	
		execution.setVariable(Constants.VAR_doctorUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima kritično eno ali več vnešenih vrednosti vprašalnika");
		return execution;
	}	
	
	
}
