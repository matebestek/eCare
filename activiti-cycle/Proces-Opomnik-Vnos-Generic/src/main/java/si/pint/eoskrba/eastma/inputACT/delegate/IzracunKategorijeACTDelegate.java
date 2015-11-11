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
import si.pint.eoskrba.model.Email;
import si.pint.eoskrba.model.User;

@Log4j
public class IzracunKategorijeACTDelegate extends
		AIzracunKategorijeGenericDelegate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8145496895150675542L;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		log.info("Računam kategorijo za vrednost ACT:");
		User patient = (User) execution.getVariable(Constants.VAR_PATIENT);
		execution.setVariable(Constants.VAR_DOCTOR, patient.getDoctor());
		Object obj = null;
		Integer vsotaACT = null;
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

			vsotaACT = new Integer((String) ((Map) obj).get(Constants.VAR_ACT_RESULT));
			context.put("vsotaACT", vsotaACT);
			
			if (vsotaACT >= 15) {
				categoryOK = true;
				execution = prepareVarsActOK(execution, ve, context, patient);
			} else if (vsotaACT < 10) {
				categoryCritical = true;
				execution = prepareVarsActCritical(execution, ve, context, patient);
			} else {
				categoryNotOk = true;
				execution = prepareVarsActNotOK(execution, ve, context, patient);
			}
				
			execution.setVariable(Constants.VAR_CATEGORY_OK, categoryOK);
			execution.setVariable(Constants.VAR_CATEGORY_NOT_OK, categoryNotOk);
			execution.setVariable(Constants.VAR_CATEGORY_CRITICAL, categoryCritical);
			execution.setVariable(Constants.VAR_CATEGORY_INVALID, false);
			// TODO: izračun vostaACT mora prit iz arhetipa in ne takole!!!!!
			// nastavim še obvestilo za pacienta ozr. za vse skupaj

		} else {
			// očitno je neka napaka in rezultat ni prišel skozi. Obvesti
			// administratorja!!
			log.error("Rezultata izpolnjevanja vprašalnika ni na voljo za pacienta "
					+ patient.getBid());
			throw new MissingProcessVariableException(
					"Manjka vsotaACT pri pacientu: " + patient.getBid());

		}

	}



	private DelegateExecution prepareVarsActOK(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
		
		Template template = null;
		
		try {
			template = ve.getTemplate("notify-patient-ACT-OK.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		
		//task name
		execution.setVariable(Constants.VAR_patientUserTaskName, "Vrednost vašega ACT točkovnika je normalna");
		return execution;
		
	}
	
	private DelegateExecution prepareVarsActNotOK(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
		//cm
		Email cmEmail = new Email(patient.getCmEmail(),  
								  "Bolnik " + patient.getFirstNameLastName() + 
								  " ima mejno vrednost ACT točkovnika. Prijavite se prosim v aplikacijo eOskrba, kjer vas čaka nova naloga. Uporabite bolnikove kontaktne podatke in preverite kaj je vzrok padca vrednosti ACT točkovnika.",
								  "Bolnik " + patient.getFirstNameLastName() + " ima mejno vrednost ACT točkovnika");
		execution.setVariable(Constants.VAR_caremanagerEmail, cmEmail);

		Template template = null;
		try {
			template = ve.getTemplate("notify-cm-ACT.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyCaremanagerForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_cmUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima mejno vrednost ACT točkovnika");

		//patient
		Email patientEmail = new Email(patient.getEmail(),						
									   "Vrednost vašega ACT točkovnika je mejna, zaradi česar vas bo v kratkem poklicala medicinska sestra.",
										"Mejna vrednost ACT točkovnika");
		execution.setVariable(Constants.VAR_patientEmail, patientEmail);
		try {
			template = ve.getTemplate("notify-patient-ACT-NOK.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_patientUserTaskName, "Mejna vrednost ACT točkovnika");
		return execution;
		
	}
	
	private DelegateExecution prepareVarsActCritical(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
		//cm
		Email cmEmail = new Email(patient.getCmEmail(),  
								  "Bolnik " + patient.getFirstNameLastName() + " ima kritično vrednost ACT točkovnika. Prijavite se prosim v aplikacijo eOskrba, " +
								  "kjer vas čaka nova naloga. Uporabite bolnikove kontaktne podatke in ga usmerite k izbranem zdravniku ali v dežurno ambulanto!",
								  "Bolnik " + patient.getFirstNameLastName() + " ima kritično vrednost ACT točkovnika!");
		execution.setVariable(Constants.VAR_caremanagerEmail, cmEmail);

		Template template = null;
		try {
			template = ve.getTemplate("notify-cm-ACT-critical.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyCaremanagerForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_cmUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima kritično vrednost ACT točkovnika!");

		//patient
		Email patientEmail = new Email(patient.getEmail(),						
									   "Kaze se poslabšanje astme. Mora vas pregledati zdravnik. Prosimo, da se obrnete na izbranega zdravnika ali dežurno ambulanto.",
										"Poslabšanje vrednosti ACT točkovnika");
		execution.setVariable(Constants.VAR_patientEmail, patientEmail);
		try {
			template = ve.getTemplate("notify-patient-ACT-critical.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_patientUserTaskName, "Poslabšanje vrednosti ACT točkovnika");

		//doctor
		Email doctorEmail = new Email(patient.getDoctorEmail(),  
				  				  "Bolnik " + patient.getFirstNameLastName() + " ima kritično vrednost ACT točkovnika. " +
				  				  "Prijavite se prosim v aplikacijo eOskrba, kjer vas čaka nova naloga. V njej so bolnikovi kontaktni podatki. To obvestilo je prejela tudi medicinska sestra " +
				  				  patient.getCaremanager().getFirstNameLastName() + ".",
				  				  "Bolnik " + patient.getFirstNameLastName() + " ima kritično vrednost ACT točkovnika!");
		execution.setVariable(Constants.VAR_doctorEmail, doctorEmail);
	
		try {
			template = ve.getTemplate("notify-doctor-ACT.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyDoctorForm, new ValueObject(sw.toString()));	
		execution.setVariable(Constants.VAR_doctorUserTaskName, "Bolnik " + patient.getFirstNameLastName() + " ima kritično vrednost ACT točkovnika!");
		return execution;
	}	

}
