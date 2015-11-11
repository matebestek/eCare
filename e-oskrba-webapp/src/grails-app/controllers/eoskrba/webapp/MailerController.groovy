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
package eoskrba.webapp

import org.apache.log4j.Logger;

import eoskrba.webapp.common.LogEnabledController

class MailerController extends LogEnabledController {
//	Logger log4j = Logger.getLogger(MailerController.class);
    def sendPerosnalMessage = { 
		doLogInfo(log, "",session.user,session.role,
			"","",session.employeeType);
		def subjectString = "[eOskrba] Novo sporočilo "
		if(session.role == 'patient') subjectString += "pacienta "
		else if(session.role == 'caremanager') subjectString += "oskrbovalca "
		else if(session.role == 'doctor') subjectString += "zdravnika "
		
		subjectString += session.firstName + " " + session.lastName
		
		def bodyString = "Spoštovani!<br /><br />"
		bodyString += "Oseba " + session.firstName + " " + session.lastName + " Vam je " + (new Date()).toTimestamp() + " poslala naslednje osebno sporočilo:<br /><br />"
		bodyString += params.content
		
		def newEmailSendRequest = new EmailSendRequest(
			timestamp: new Date(),
			fromId: session.user,
			toId:   params.to,
			title: subjectString,
			content: bodyString,
			sent: false,
			attempt: 0
		)
		
		newEmailSendRequest.save()
		
		flash.success = ["Hvala!","Vaše sporočilo je bilo poslano!"]
		
		redirect(controller: 'redirects', action:'toMyTasks')
		
	}
	
}
