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
package si.pint.eoskrba.esport.inputSport.listeners;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.log4j.MDC;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import si.pint.eoskrba.conf.SystemConstant;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.model.ValueObject;
import si.pint.eoskrba.model.Email;
import si.pint.eoskrba.model.User;

@Log4j
public class NotifyCmSportExecutionListener implements ExecutionListener, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2470915111615784863L;
	
	public void notify(DelegateExecution e) throws Exception {
		
		// POZOR: Pri eŠportu je caremanager zapisan kot doctor, zato se uporablja getDoctor()
		// in getDoctorEmail().
		
		//nastavim vsebino Email-a
		User patient = (User)e.getVariable(Constants.VAR_PATIENT);
		String subject = "Udeleženec " + patient.getFirstNameLastName() + " že tri dni ni vnesel vrednosti za športno vadbo";
		String content = "Udeleženec " + patient.getFirstNameLastName() + " kljub opozorilom že dva dni ni vnesel vrednosti športne vadbe v aplikacijo eOskrba. " +
						 "Prosimo, da se prijavite v aplikacijo eOskrba, kjer vas caka nova naloga. Uporabite udeleženčeve kontaktne podatke in preverite, ce se zeli sodelovati v studiji.";
		Email email = null;
		if(e.getVariable(Constants.VAR_NOTIFY_CM_EMAIL) != null){
			email = (Email)e.getVariable(Constants.VAR_NOTIFY_CM_EMAIL);
			email.setSubject(subject);
			email.setContent(content);
			email.setTo(patient.getDoctorEmail());
		} else {
			email = new Email(patient.getDoctorEmail(),content,subject);
		}
		
		email.setFrom(SystemConstant.EMAIL_SPORT.toString());
		e.setVariable(Constants.VAR_NOTIFY_CM_EMAIL, email);
		//sedaj nastavim še za Task
		VelocityEngine ve = new VelocityEngine();
		//ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
		Properties p = new Properties();
		p.setProperty("resource.loader","class,jar");
		p.setProperty("class.resource.loader.description","Velocity Classpath Resource Loader");
		p.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);		

		VelocityContext context = new VelocityContext();
		context.put(Constants.VAR_PATIENT, patient);
		Template template = null;
		try {
			template = ve.getTemplate("notify-cm-patient-not-responding-sport.vm","UTF-8");
			StringWriter sw = new StringWriter();
			template.merge(context, sw);
			e.setVariable(Constants.VAR_notifyCaremanagerForm, new ValueObject(sw.toString()));
		} catch (Exception e1) {
			log.error("Velocity napaka", e1);
		}
		
		e.setVariable(Constants.VAR_patientStopCoopTaskName, "Udeleženec " + patient.getFirstNameLastName() + " že dva dni ni vnesel vrednosti za športno vadbo");
		
		MDC.put("user", patient.getDoctor().getUsername());
		MDC.put("userRole",patient.getDoctor().getRole().toString());
		MDC.put("task", "SequenceFLowNotifyCmExecutionListener");
		MDC.put("taskContent", "Udeleženec " + patient.getFirstNameLastName() + " že dva dni ni vnesel vrednosti za športno vadbo");
		MDC.put("taskType", "D");
		log.info("Obvestilo caremanager-ju");
	}

}
