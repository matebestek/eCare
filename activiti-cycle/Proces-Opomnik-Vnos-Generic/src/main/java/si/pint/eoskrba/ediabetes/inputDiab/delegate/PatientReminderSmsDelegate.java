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
/**
 * 
 */

package si.pint.eoskrba.ediabetes.inputDiab.delegate;

import java.io.Serializable;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.apache.log4j.MDC;

import si.pint.eoskrba.conf.SystemConstant;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.delegate.ANotifySmsDelegate;
import si.pint.eoskrba.messages.MessageRepo;
import si.pint.eoskrba.model.User;
import si.pint.sms.webservice.SmsServiceUtils;
import si.pint.sms.webservice.SmswsPortType;

/**
 * implements Serializable sem uporabil zato, da lahko v bpmn20.xml-ju uporabim
 * activiti:delegateExpression, kjer dejansko napišeš referenco na instanco
 * razreda JavaDelegate. Ker pa unit testi ne gredo skozi drugače, je treba
 * implementirat Serializable in dat instanco objekta kot spremenljivko v
 * konteks procesa. Ta način je postmortem najden in predlagan tudi na uradnem
 * forumu: http://forums.activiti.org/en/viewtopic.php?f=6&t=1858&p=7853&hilit=
 * activiti%3AdelegateExpression#p7853
 * */
@Log4j
public class PatientReminderSmsDelegate extends ANotifySmsDelegate implements
		Serializable {
	private static final long serialVersionUID = 4795134868883648125L;

	@Override
	public void execute(DelegateExecution execution) throws Exception {		
		User patient = (User)execution.getVariable(Constants.VAR_PATIENT);
		
		SmswsPortType port= SmsServiceUtils.smsServiceInit();		
		port.send_sms(patient.getMobilePhone(), MessageRepo.DIABETES_PATIENT_SMS_REMINDER, SystemConstant.SMS_SECRET.toString());		

		MDC.put("user", patient.getUsername());
		MDC.put("userRole",patient.getRole().toString());
		MDC.put("task", "PatientReminderSmsDelegate");
		MDC.put("taskContent", "Prosimo, da v aplikaciji eOskrba izpolnete vase naloge.");
		MDC.put("taskType", "D");
		log.info("Pacient je obveščen preko SMS");
	}

}
