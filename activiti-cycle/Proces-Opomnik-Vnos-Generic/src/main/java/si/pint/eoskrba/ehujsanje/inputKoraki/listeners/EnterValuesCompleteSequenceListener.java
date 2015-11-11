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
package si.pint.eoskrba.ehujsanje.inputKoraki.listeners;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import si.pint.eoskrba.conf.SystemConstant;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.exceptions.MissingProcessVariableException;
import si.pint.eoskrba.eastma.inputGeneric.model.ValueObject;
import si.pint.eoskrba.model.Email;
import si.pint.eoskrba.model.User;

@Log4j
public class EnterValuesCompleteSequenceListener implements ExecutionListener {


	public void notify(DelegateExecution execution) throws Exception {
		
		log.info("Racunam kategorijo za urejenost hujsanja:");
		
		Boolean notOk = (Boolean) execution.getVariable(Constants.VAR_CATEGORY_NOT_OK);
		if (!notOk)
			return;
		
		
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
			
			//get entered values
			Map map = (Map) obj;
			
			String bodyWeight = (String) map.get(Constants.VAR_INPUT_BODY_WEIGHT) != null ? (String) map.get(Constants.VAR_INPUT_BODY_WEIGHT) : "";
			context.put("inputBodyWeight", bodyWeight);
			
			context.put("proteins", map.get(Constants.VAR_INPUT_PROTEINS));
			context.put("proteinsLow", map.get(Constants.VAR_INPUT_PROTEINS_LOW));
			context.put("proteinsHigh", map.get(Constants.VAR_INPUT_PROTEINS_HIGH));
			context.put("fat", map.get(Constants.VAR_INPUT_FAT));
			context.put("fatHigh", map.get(Constants.VAR_INPUT_FAT_HIGH));
			context.put("carbonHydrates", map.get(Constants.VAR_INPUT_CARBON_HYDRATES));
			context.put("carbonHydratesLow", map.get(Constants.VAR_INPUT_CARBON_HYDRATES_LOW));
			context.put("sugar", map.get(Constants.VAR_INPUT_SIMPLE_SUGAR));
			context.put("sugarHigh", map.get(Constants.VAR_INPUT_SIMPLE_SUGAR_HIGH));

			context.put("energyIntake", map.get(Constants.VAR_INPUT_ENERGY_INTAKE));
			
			//ok/nok
			boolean energyIntakeOk = (Boolean) map.get(Constants.VAR_INPUT_ENERGY_INTAKE_OK);
			boolean proteinsOk = (Boolean) map.get(Constants.VAR_INPUT_PROTEINS_OK);
			boolean fatOk = (Boolean) map.get(Constants.VAR_INPUT_FAT_OK);
			boolean carbonHydratesOk = (Boolean) map.get(Constants.VAR_INPUT_CARBON_HYDRATES_OK);
			boolean sugarOk = (Boolean) map.get(Constants.VAR_INPUT_SIMPLE_SUGAR_OK);
			boolean bodyMassOk = (Boolean) map.get(Constants.VAR_INPUT_BODY_MASS_OK);
			
			context.put("energyIntakeOk", energyIntakeOk);
			context.put("proteinsOk", proteinsOk);
			context.put("fatOk", fatOk);
			context.put("carbonHydratesOk", carbonHydratesOk);
			context.put("sugarOk", sugarOk);
			context.put("bodyMassOk", bodyMassOk);
			
			boolean opkpOK = energyIntakeOk && proteinsOk && fatOk && carbonHydratesOk && sugarOk;		
			
			String emailSubject = "";
			String emailBody = "";
			String taskName = "";
			
			if ((Boolean) map.get(Constants.VAR_INPUT_BODY_MASS_OK) && !opkpOK) { //teza ok, OPKP ni ok
				emailSubject = "Neustrezen vnos živil pri bolniku " + patient.getFirstNameLastName();
				emailBody = "Ugotovljen je bil neustrezen tedenski vnos živil pri bolniku " + patient.getFirstNameLastName() +  
						  " v koraku hujšanja " + patient.getProcessStep() + ". Prijavite se prosim v aplikacijo eOskrba, kjer vas čaka nova naloga. Uporabite bolnikove kontaktne podatke in preverite kaj je vzrok spremembe vrednosti.";
				taskName = "Neustrezen vnos živil pri bolniku " + patient.getFirstNameLastName();
			}
			else if (!opkpOK) { //teza ni ok, OPKP ni OK
				emailSubject = "Bolnik " + patient.getFirstNameLastName() + " ima spremenjeno telesno težo in neustrezen vnos živil";
				emailBody = "Bolnik " + patient.getFirstNameLastName() + 
						  " ima spremenjeno telesno težo glede na prejšnjo meritev in neustrezen tedenski vnos živil v koraku hujšanja " + patient.getProcessStep() + ". Prijavite se prosim v aplikacijo eOskrba, kjer vas čaka nova naloga. Uporabite bolnikove kontaktne podatke in preverite kaj je vzrok spremembe vrednosti.";
				taskName = "Bolnik " + patient.getFirstNameLastName() + " ima spremenjeno telesno težo in neustrezen vnos živil";
			}
			else { //teza ni OK, opkp OK 
				emailSubject = "Bolnik " + patient.getFirstNameLastName() + " ima spremenjeno telesno težo";
				emailBody = "Bolnik " + patient.getFirstNameLastName() + 
									  " ima spremenjeno telesno težo glede na prejšnjo meritev. Prijavite se prosim v aplikacijo eOskrba, kjer vas čaka nova naloga. Uporabite bolnikove kontaktne podatke in preverite kaj je vzrok spremembe vrednosti.";
				taskName = "Bolnik " + patient.getFirstNameLastName() + " ima spremenjeno telesno težo";
			}
			
			//prepare userTask & eMail for CM
			Email cmEmail = new Email(patient.getDoctorEmail(),  //specific for eHujsanje: assigned coordinator is written in LDAP atribute 'patientsDoctorAtt'
									  emailBody,
									  emailSubject);
			cmEmail.setFrom(SystemConstant.EMAIL_HUJSANJE.toString());
			execution.setVariable(Constants.VAR_caremanagerEmail, cmEmail);

			Template template = null;
			try {
				template = ve.getTemplate("notify-cm-hujsanje-NOK-opkp-results.vm","UTF-8");
			} catch (Exception e) {
				log.error("Velocity napaka", e);
			}
			StringWriter sw = new StringWriter();
			template.merge(context, sw);
			execution.setVariable(Constants.VAR_notifyCaremanagerForm, new ValueObject(sw.toString()));
			execution.setVariable(Constants.VAR_cmUserTaskName, taskName);	
			
			execution.setVariable(Constants.VAR_listenerCaremanagerObvestiloComplete, new CaremanagerNotifierCompleteListener());
			execution.setVariable(Constants.VAR_listenerCaremanagerObvestiloCreate, new CaremanagerNotifierCreateListener());
			

		} else {
			// očitno je neka napaka in rezultat ni prišel skozi. Obvesti administratorja!!
			log.error("Rezultata izpolnjevanja vprašalnika ni na voljo za pacienta "
					+ patient.getBid());
			throw new MissingProcessVariableException("Manjka vnos vrednosti za hujsanje pri pacientu: " + patient.getBid());

		}		

	}

}
