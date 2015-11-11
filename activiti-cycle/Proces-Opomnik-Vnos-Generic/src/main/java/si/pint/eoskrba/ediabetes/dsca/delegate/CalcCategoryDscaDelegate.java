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
package si.pint.eoskrba.ediabetes.dsca.delegate;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.exceptions.MissingProcessVariableException;
import si.pint.eoskrba.eastma.inputGeneric.model.ValueObject;
import si.pint.eoskrba.messages.MessageRepo;
import si.pint.eoskrba.model.User;

@Log4j
public class CalcCategoryDscaDelegate implements JavaDelegate, Serializable {


	public void execute(DelegateExecution execution) throws Exception {
		
		User patient = (User) execution.getVariable(Constants.VAR_PATIENT);
		if (patient == null) {
			log.error("Napaka pri pridobivanju pacient za proces opomnik DSCA.");
			throw new MissingProcessVariableException("Manjka pacient pri vnosu vrednosti DSCA");
		}
		
		log.info("Racunam kategorijo (ok) za DSCA:");
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
				
		boolean categoryOK = false;
		boolean categoryCritical = false;
		boolean categoryNotOk = false;
			
		//get entered values
//		String sugar = (String) map.get(Constants.VAR_INPUT_SUGAR) != null ? (String) map.get(Constants.VAR_INPUT_SUGAR) : "";
			
		//DSCA always OK - for now
		categoryOK = true;
		execution = prepareVarsDscaOK(execution, ve, context, patient);
				
		execution.setVariable(Constants.VAR_CATEGORY_OK, categoryOK);
		execution.setVariable(Constants.VAR_CATEGORY_NOT_OK, categoryNotOk);
		execution.setVariable(Constants.VAR_CATEGORY_CRITICAL, categoryCritical);
		execution.setVariable(Constants.VAR_CATEGORY_INVALID, false);

	}
	
	
	private DelegateExecution prepareVarsDscaOK(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient) {
		
		Template template = null;
		
		try {
			template = ve.getTemplate("notify-patient-dsca-OK.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		
		//task name
		execution.setVariable(Constants.VAR_patientUserTaskName, MessageRepo.DIABETES_PATIENT_TASK_NAME_NORMAL_DSCA);
		return execution;
		
	}

}
