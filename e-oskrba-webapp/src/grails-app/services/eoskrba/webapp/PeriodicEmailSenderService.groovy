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

import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;

class PeriodicEmailSenderService {

    static transactional = true
	
	def mailService

	// Odpošlje vse čakajoče zahteve
    def sendPendingMail() {

		// Pridobi vse zahteve za pošiljanje e-mailov, ki še niso bile odposlane in ki niso bile več
		// kot 3-krat neuspešno obdelane
		def c = EmailSendRequest.createCriteria()
		def pendingEmail = c {
			and {
				eq("sent",false)
				lt("attempt",3)
			}
		}
		
		// Odpošlji vsako zahtevo
		pendingEmail.each { email ->
			
			// Poskusi poslati
			try {
				
				// Pridobi prejemnikov LDAP vnos
				MainLdapSchema toLdap = MainLdapSchema.find(
					directory: "mainLdap",
					base: "ou=people",
					filter: "(uidatt=" + email.toId + ")"
				)
				
				// Odpošlji sporočilo
				mailService.sendMail {
					to toLdap.eMailAtt
					from employeeTypeToEmail(toLdap.employeeType)
					subject email.title
					html (email.content+si.pint.eoskrba.model.Email.EMAIL_SIG);
				}
				
				// Označi sporočilo kot odposlano
				email.sent = true;
				System.out.println("[eoskrba-webapp] [periodicEmailSenderService] Sent email to: " + email.toId)
				
			} catch (Exception e) {
			
				// Če je pošiljanje spodletelo, povečaj število poskusov za 1
				email.attempt = email.attempt + 1
				System.out.println("[eoskrba-webapp] [periodicEmailSenderService] Could not send email to: " + email.toId)
				e.printStackTrace()
				
			}
			
			// Shrani posodobljeno email zahtevo
			email.save()
			
		}
		
    }
	
	// Pretvori employeeType (A,D,S,P,H) v ustrezen e-mail naslov
	static String employeeTypeToEmail( String et ) {
		
		if(et == "A")      return "eastma@pint.upr.si"
		else if(et == "D") return "ediabetes@pint.upr.si"
		else if(et == "H") return "ehujsanje@pint.upr.si"
		else if(et == "P") return "esport@pint.upr.si"
		else if(et == "S") return "eshizofrenija@pint.upr.si"
		else               return "eoskrba@pint.upr.si"
		
	}
	
}
