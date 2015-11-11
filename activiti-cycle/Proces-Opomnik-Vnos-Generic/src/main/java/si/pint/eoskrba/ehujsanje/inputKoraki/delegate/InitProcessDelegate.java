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
package si.pint.eoskrba.ehujsanje.inputKoraki.delegate;

import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.archetypes.exceptions.LdapException;
import si.pint.eoskrba.conf.SystemConstant;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.delegate.AInitProcessDelegate;
import si.pint.eoskrba.model.Email;
import si.pint.eoskrba.model.User;
import si.pint.eoskrba.utils.ArchetypeServiceUtils;

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
		
		String s = (String)execution.getVariable(Constants.VAR_USERNAME);
		User u = null;
		try {
			u = UserRegistryFactory.getUserFromLDAP(s);			
			s = (String)execution.getVariable(Constants.VAR_WEIGHT);
			u.setWeight(new BigDecimal(s));
			s = (String)execution.getVariable(Constants.VAR_HEIGHT);
			u.setHeight(new BigDecimal(s));
		} catch (LdapException e) {
			e.printStackTrace();
			log.error("Napaka pri pridobivanju uporabnika iz ldap: " + execution.getVariable(Constants.VAR_USERNAME));
			throw new RuntimeException("User on proces 'Hujsanje-Opomnik-Vnos-Koraki' not defined!");
		}
		
		//----->>>> test
//		if ((String) execution.getVariable(Constants.VAR_PROCESS_STEP) != null)
//			u.setProcessStep((String) execution.getVariable(Constants.VAR_PROCESS_STEP));		
		//<<<<<---- test
		
		
		String caremanager = UserRegistryFactory.findPatientsDoctor(u.getUsername());
		u.setPatientsCM(caremanager); //specific for eHusjanje: assigned coordinator is written in LDAP atribute 'patientsDoctorAtt'
		
		User cmUser = UserRegistryFactory.getUserFromLDAP(caremanager);
				
		execution.removeVariable(Constants.VAR_USERNAME);
		execution.removeVariable(Constants.VAR_WEIGHT);
		execution.removeVariable(Constants.VAR_HEIGHT);
		execution.setVariable(Constants.VAR_PATIENT, u);
		
		HashMap<String, Object> variables = new HashMap<String, Object>();

		//prepare eMail template
		VelocityEngine ve = new VelocityEngine();
		Properties p = new Properties();
		p.setProperty("resource.loader","class,jar");
		p.setProperty("class.resource.loader.description","Velocity Classpath Resource Loader");
		p.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);		

		VelocityContext context = new VelocityContext();
		context.put("patient",u);
		context.put("cmNameSurname", cmUser.getFirstNameLastName());
		
		Template template = null;
		try {
			template = ve.getTemplate("hujsanjePacientReminderEmail-Content.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		
		Template template1 = null;
		try {
			template1 = ve.getTemplate("hujsanjePacientReminderEmail-Subject.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw1 = new StringWriter();
		template1.merge(context, sw1);
		
		Email patientEmail = new Email(u.getEmail(), sw.toString(), sw1.toString());
		patientEmail.setFrom(SystemConstant.EMAIL_HUJSANJE.toString());
		variables.put(Constants.VAR_patientReminderEmail, patientEmail);
			
		//set reminder intervals 
		variables.put(Constants.VAR_maxNumberOfSentReminders, 3);
		variables.put(Constants.VAR_reminderPacientIntervalDuration, "PT72H");
		
		//init subprocess 'VnosMeritev7' control variables
		addSubprocessVisitArray(variables, u.getProcessStep());
		
		// v eXist ustvari nov zapis o zadnji dokončani nalogi
		// (ta vrednost je zdaj 0, ker se je nov korak šele kreiral)
		ArchetypeServiceUtils.updateCompletedSubTasksNo(0, execution, u.getUsername(), u.getUsername());
		
//		if(true){//samo za testiranje. Produkcijsko tega ne sme bit tle, ne bo pa škode če je
//			String url = SequenceFlowListenerGateway.prepareOPKPUrl(u);
//			context.put("url", url);
//		}	
		
		//default: set to null
		variables.put(Constants.VAR_ANSWERS_CORRECT, null);
		
		//default: set to null
		variables.put(Constants.VAR_CATEGORY_OK, null);
			
		execution.setVariables(variables);

	}

	/**
	 * Set process variable which contains information about subtasks to visit depending on process step.
	 * 
	 * @param variables
	 * @param processStep
	 */
	private void addSubprocessVisitArray(HashMap<String, Object> variables, String processStep) {
		Boolean[] subTaskVisitArray = new Boolean[] {true, true, true, true, true, true, true};
//		Arrays.fill(subTaskVisitArray, Boolean.FALSE);		
				
		if (processStep == null)
			return;
		
		if (processStep.equals("1")) {
			
		}
		else if (processStep.equals("2")) {
			
		}
		else if (processStep.equals("3")) {
			
		}		
		else if (processStep.equals("4")) {
			
		}
		else if (processStep.equals("5")) {
			
		}
		else if (processStep.equals("6")) {
			
		}
		else if (processStep.equals("7")) {
			subTaskVisitArray = new Boolean[] {true, true, true, true, true, false, true};
		}		
		else if (processStep.equals("8")) {
			subTaskVisitArray = new Boolean[] {true, true, true, true, true, false, true};
		}
		else if (processStep.equals("9")) {
			subTaskVisitArray = new Boolean[] {true, true, true, true, true, false, true};
		}
		else if (processStep.equals("10")) {
			
		}
		else if (processStep.equals("11")) {
			
		}	
		else if (processStep.equals("12")) {
			
		}
		else if (processStep.equals("13")) {
			
		}
		else if (processStep.equals("14")) {
			
		}
		else if (processStep.equals("15")) {
			
		}	
		else if (processStep.equals("16")) {
			subTaskVisitArray = new Boolean[] {true, true, true, true, true, false, true};
		}		
		
		variables.put(Constants.VAR_SUBTASK_VISIT_ARRAY, subTaskVisitArray);
	}
	
	

}
