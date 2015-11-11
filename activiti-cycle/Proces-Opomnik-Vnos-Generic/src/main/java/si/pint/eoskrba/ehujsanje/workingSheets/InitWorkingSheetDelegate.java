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
package si.pint.eoskrba.ehujsanje.workingSheets;

import java.io.StringWriter;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.model.ValueObject;
import si.pint.eoskrba.messages.MessageRepo;
import si.pint.eoskrba.model.User;

@Log4j
public class InitWorkingSheetDelegate implements JavaDelegate {


	
	public void execute(DelegateExecution execution) throws Exception {
		log.info("Inicializacija delegata za prikaz zgodovine delovnih listov.");
		
		String loggedUser = (String) execution.getVariable(Constants.VAR_USERNAME_APP);
		
		//here used to indicate which step was selected from GUI
		String selectedStep = (String) execution.getVariable(Constants.VAR_PROCESS_STEP);
		
		
		//UserTask template
		VelocityContext context = initVelocityContext(null);
		VelocityEngine ve = initVelocityEngine();
		
		Template templateACT = null;
		try {
			//izjema za delovne liste 'psihologija' in korak 1, kjer je Pogodba s seboj
			if (selectedStep.trim().equals("1") || 
				selectedStep.trim().equals("7") || 
				selectedStep.trim().equals("8") || 
				selectedStep.trim().equals("9")) {
				templateACT = ve.getTemplate("hujsanje/hujsanje-delovni-listi-korak-zgodovina-" + selectedStep.trim() +".vm","UTF-8");
			}
			else {
				templateACT = ve.getTemplate("hujsanje/hujsanje-delovni-listi-korak-" + selectedStep.trim() +".vm","UTF-8");
			}
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter swACT = new StringWriter();
		templateACT.merge(context, swACT);
		
		//fill .form file
		execution.setVariable(Constants.VAR_HUJSANJE_HIS_WORKING_SHEETS, new ValueObject(swACT.toString()));
		
		//set user task name
		execution.setVariable(Constants.VAR_userTaskName, MessageRepo.HUJSANJE_HIS_WORKING_SHEETS_TASK_NAME + " " + selectedStep.trim());
		
		
	}
	
	/**
	 * Init Velocity engine.
	 * 
	 * @param patient
	 * @return
	 */
	private VelocityContext initVelocityContext(User patient) {
		
		VelocityContext context = new VelocityContext();
//		context.put("patient",patient);		
		
		return context;
	}
	
	private VelocityEngine initVelocityEngine() {
		
		VelocityEngine ve = new VelocityEngine();
		Properties p = new Properties();
		p.setProperty("resource.loader","class,jar");
		p.setProperty("class.resource.loader.description","Velocity Classpath Resource Loader");
		p.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);	
		
		return ve;
	}




}
