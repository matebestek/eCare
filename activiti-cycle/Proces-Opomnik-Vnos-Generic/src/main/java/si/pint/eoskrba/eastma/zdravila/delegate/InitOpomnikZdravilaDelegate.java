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
package si.pint.eoskrba.eastma.zdravila.delegate;

import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.log4j.MDC;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.delegate.AInitProcessDelegate;
import si.pint.eoskrba.eastma.inputGeneric.model.ValueObject;
import si.pint.eoskrba.model.Email;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;

@Log4j
public class InitOpomnikZdravilaDelegate extends AInitProcessDelegate implements Serializable {

	private static final long serialVersionUID = -6152426726590962650L;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		//get user (patient) from session
		String ConstantsString = (String) execution.getVariable(Constants.VAR_PATIENT_OBJ);
		
		if (ConstantsString == null) {
			throw new RuntimeException("Constants on proces 'Opomnik-Vnos-Zdravila' not defined!");
		}
				
		JSONObject ConstantsJSON = new JSONObject(ConstantsString);
		
		User u = new User();
		u.setBid((String) ConstantsJSON.get(Constants.VAR_BID)); 
		u.setEmail(((String) ConstantsJSON.get(Constants.VAR_eMAIL)));
		u.setMobilePhone((String) ConstantsJSON.get(Constants.VAR_MOBILE_TEL_NUM));
		u.setSex(ConstantsJSON.get(Constants.VAR_SEX).equals(User.Sex.MALE.toString()) ? User.Sex.MALE : User.Sex.FEMALE);
		
		u.setDateOfBirth(UserRegistryFactory.parseTimestamp((String) (ConstantsJSON.get(Constants.VAR_BIRTH_DATE))).getTime());		
		u.setWeight(new BigDecimal((String) ConstantsJSON.get(Constants.VAR_WEIGHT)));
		u.setHeight(new BigDecimal((String) ConstantsJSON.get(Constants.VAR_HEIGHT)));
		u.setRole(Role.getRoleFromId((String) ConstantsJSON.get(Constants.VAR_ROLE)));
		u.setUsername((String) ConstantsJSON.get(Constants.VAR_USERNAME));
		u.setFirstNameLastName((String) ConstantsJSON.get(Constants.VAR_FIRST_NAME_LAST_NAME));
		execution.setVariable(Constants.VAR_PATIENT_OBJ, u);
		execution.setVariable(Constants.VAR_PATIENT, u);
		
		//set variables ---
		execution.setVariable(Constants.VAR_patientNotifySmsDelegate, new PatientNotifyMedicineSmsDelegate());
		execution.setVariable(Constants.VAR_listenerPatientObvestiloComplete, new PatientObvestiloCompleteListener());
		execution.setVariable(Constants.VAR_listenerPatientObvestiloCreate, new PatientObvestiloCreateListener());
		
		// Velocity uporabljan za template-e. Pretvorim jih v String in jih
		// podtaknem v .form datoteke
		VelocityEngine ve = new VelocityEngine();
		//ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
		Properties p = new Properties();
		p.setProperty("resource.loader","class,jar");
		p.setProperty("class.resource.loader.description","Velocity Classpath Resource Loader");
		p.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);		

		VelocityContext context = new VelocityContext();
		context.put("patient",u);
		
		Email patientEmail = new Email(u.getEmail(), 
									   "Prosimo, da preverite vaso zalogo zdravil preprecevalcev!", 
									   "Prosimo, da preverite vaso zalogo zdravil");
		execution.setVariable(Constants.VAR_patientEmail, patientEmail);
		
		//zdej pripravim template še za UserTask
		Template templateACT = null;
		try {
			templateACT = ve.getTemplate("notify-patient-medicine.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter swACT = new StringWriter();
		templateACT.merge(context, swACT);
		
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(swACT.toString()));
		execution.setVariable(Constants.VAR_patientUserTaskName, "Prosimo, da preverite vašo zalogo zdravil!");		
		
		MDC.put("user", u.getUsername());
		MDC.put("userRole",u.getRole().toString());
		MDC.put("task", "InitOpomnikZdravilaDelegate");
		MDC.put("taskContent", "Prosimo, da preverite vaso zalogo zdravil preprecevalcev!");
		MDC.put("taskType", "A");
		log.info("Pacient je opomnjen preko aplikacije, da si preveri zalogo zdravil");
		
	}

}
