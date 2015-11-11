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
package si.pint.eoskrba.eastma.regularCheckup;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.eoskrba.conf.SystemConstant;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.Email;
import si.pint.eoskrba.model.User;

public class PatientNotifierPrepareEmailDelegate implements JavaDelegate {

	public void execute(DelegateExecution execution) throws Exception {
		
		// Pridobi pacienta
		User patient = UserRegistryFactory.getUserFromLDAP( (String) execution.getVariable(Constants.VAR_USERNAME) );
		
		// Pripravi e-mail
		Email email = new Email(
			patient.getEmail(),
			"Pozdravljeni, " + patient.getFirstNameLastName() +"!<br /><br />Prosimo, da se naročite na redni pregled pri Vašem zdravniku. Kmalu bo namreč minilo 6 mesecev od Vašega zadnjega obiska.<br /><br />Hvala za sodelovaje in lep dan Vam želi ekipa eOskrbe!",
			"eOskrba: Narocilo na redni pregled"
		);
		email.setFrom(SystemConstant.EMAIL_ASTMA.toString());
		
		// Shrani e-mail
		execution.setVariable(Constants.VAR_EMAIL, email);
		
	}
	
}
