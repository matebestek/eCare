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

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import si.pint.eoskrba.eastma.inputGeneric.delegate.ANotifySmsDelegate;


/**
 *
 * 
 **/
@Log4j
public class PatientNotifySmsDelegate extends ANotifySmsDelegate implements Serializable{

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		//trenutno nimamo kategorije 'kriticna vrednost'
		
//		SmswsPortType port = SmsServiceUtils.smsServiceInit();
//		
//		User patient = (User) execution.getVariable(Constants.VAR_PATIENT);
//		boolean valuesCritical = (Boolean) execution.getVariable(Constants.VAR_CATEGORY_CRITICAL);
//		
//		if (valuesCritical) {
//			//TODO: odkomentiraj!!
////			port.send_sms(patient.getMobilePhone(), MessageRepo.DIABETES_PATIENT_SMS_CRITICAL, SystemConstant.SMS_SECRET.toString());		
//			log.info("Messege sent to: " + patient.getMobilePhone());
//		}
//		MDC.put("user", patient.getUsername());
//		MDC.put("userRole",patient.getRole().toString());
//		MDC.put("task", "PatientNotifySmsDelegate");
//		MDC.put("taskContent", MessageRepo.DIABETES_PATIENT_SMS_CRITICAL);
//		MDC.put("taskType", "D");
//		log.info("Opozorilo poslano na " + patient.getMobilePhone());
		
	}
	

}
