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
package si.pint.eoskrba.ediabetes.regularCheckup;

import java.rmi.RemoteException;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

import si.pint.eoskrba.conf.SystemConstant;
import si.pint.sms.webservice.SmsServiceUtils;
import si.pint.sms.webservice.SmswsPortType;

/**
 * Delegate class used for sending all (standard) SMS messages.
 * 
 * Important: Corresponding serviceTask in bmpn20.xml file has to have 3 activiti:fields declared:
 *  - mobilePhoneNum
 *  - messageSmsText
 *  - username
 *  
 *  e.g.
 *  
 * 
 * @author Blaz
 *
 */
@Log4j
public class PatientNotifierGeneralSmsDelegate implements JavaDelegate {
	
	private Expression mobilePhoneNum;
	private Expression messageSmsText;
	private Expression username;

	public void execute(DelegateExecution execution) throws RemoteException {
		String telNum = (String) mobilePhoneNum.getValue(execution);
		String smsMessText = (String) messageSmsText.getValue(execution);
		String usernameVal = (String) username.getValue(execution);
		
		SmswsPortType port = SmsServiceUtils.smsServiceInit();
		
		port.send_sms(telNum, smsMessText, SystemConstant.SMS_SECRET.toString());		
		log.info("Messege sent to: " + telNum + " , user: " + usernameVal + " ,message: " + smsMessText);
	}

		
}
