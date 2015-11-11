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
package si.pint.eastma.inputPEF.delegate;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

import si.pint.eoskrba.conf.SystemConstant;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.Email;
import si.pint.eoskrba.model.User;

public class PrepareEmail implements JavaDelegate {
	
	public Expression emailTemplate;

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		// Ugotovi, kakšen email je treba poslati
		int template = Integer.parseInt((String)emailTemplate.getValue(execution));
		
		// Pacient
		User u = (User) execution.getVariable(Constants.VAR_PATIENT_OBJ);
		
		// Pripravi e-mail
		Email email = null;
		
		if(template == 1) {

			email = new Email(
				u.getEmail(),
				"Pozdravljeni "+u.getFirstNameLastName()+"!<br /><br />Prosimo, da v aplikacijo eOskrba vnesete vrednost PEF.<br /><br />Lep dan še naprej vam želi vaše zdravniško osebje "+u.getCaremanager().getFirstNameLastName()+" in "+u.getDoctor().getFirstNameLastName()+"!",
				"eOskrba: Vnesite vrednost PEF"
			);
			
		} else if(template == 2) {

			email = new Email(
				u.getCmEmail(),
				"Bolnik "+u.getFirstNameLastName()+" ima mejno vrednost PEF.<br />Prosimo, prijavite se v aplikacijo eOskrba, kjer Vas čaka nova naloga. Uporabite bolnikove kontaktne podatke in preverite, kaj je vzrok padca vrednosti PEF.",
				"eOskrba: Bolnik "+u.getFirstNameLastName()+" ima mejno vrednost PEF"
			);
			
		} else if(template == 3) {

			email = new Email(
				u.getEmail(),
				"Spoštovani "+u.getFirstNameLastName()+"!<br /><br />Vaša vrednost PEF je mejna, zaradi česar Vas bo v kratkem poklicala medicinska sestra.",
				"eOskrba: Mejna vrednost PEF"
			);
			
		} else if(template == 4) {

			email = new Email(
				u.getEmail(),
				"Spoštovani "+u.getFirstNameLastName()+"!<br /><br />Kaže se poslabšanje astme. Mora vas pregledati zdravnik. Prosimo, da se obrnete na izbranega zdravnika ali dežurno ambulanto!",
				"eOskrba: Poslabsanje vrednosti PEF"
			);
			
		} else if(template == 5) {

			email = new Email(
				u.getCmEmail(),
				"Bolnik "+u.getFirstNameLastName()+" ima kritično vrednost PEF.<br />Prosimo, prijavite se v aplikacijo eOskrba, kjer Vas čaka nova naloga. Uporabite bolnikove kontaktne podatke in ga usmerite k izbranem zdravniku ali v dežurno ambulanto!",
				"eOskrba: Bolnik "+u.getFirstNameLastName()+" ima kriticno vrednost PEF!"
			);
			
		} else if(template == 6) {

			email = new Email(
				u.getDoctorEmail(),
				"Bolnik "+u.getFirstNameLastName()+" ima kritično vrednost PEF.<br />Prosimo, prijavite se v aplikacijo eOskrba, kjer Vas čaka nova naloga. V njej so bolnikovi kontaktni podatki. To obvestilo je prejela tudi medicinska sestra "+u.getCaremanager().getFirstNameLastName()+".",
				"eOskrba: Bolnik "+u.getFirstNameLastName()+" ima kriticno vrednost PEF!"
			);
			
		} else if(template == 7) {

			email = new Email(
				u.getCmEmail(),
				"Bolnik "+u.getFirstNameLastName()+" kljub opozorilom že tri dni ni vnesel vrednosti PEF v aplikacijo eOskrba. Prosimo, da se prijavite v aplikacijo eOskrba, kjer Vas čaka nova naloga. Uporabite bolnikove kontaktne podatke in preverite, ali še želi sodelovati v študiji.",
				"eOskrba: Bolnik "+u.getFirstNameLastName()+" ze tri dni ni vnesel vrednosti PEF"
			);
			
		} else {
			
			System.out.println("ERROR: Napaka v si.pint.eastma.inputPEF.delegate.PrepareMail:");
			System.out.println("          Field-injection vrednost emailTemplate ni veljavna!");
			
		}
		
		// Določi pošiljatelja
		email.setFrom(SystemConstant.EMAIL_ASTMA.toString());
		
		// Shrani e-mail
		execution.setVariable(Constants.VAR_EMAIL, email);

	}

}
