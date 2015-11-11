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
package si.pint.eoskrba.ediabates.inputFlat.delegate;

import java.io.Serializable;
import java.util.HashMap;
import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.delegate.AInitProcessDelegate;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;

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
		
		log.info("");
		
		// get user (patient) from session
		String subjectString = (String) execution.getVariable(Constants.VAR_USERNAME_APP);
		if (subjectString == null) {
			throw new RuntimeException(
					"User on proces 'Diabetes-Opomnik-Vnos-Generic' not defined (diab)!");
		}
	
		//load patient from LDAP
		User patient = UserRegistryFactory.getUserFromLDAP(subjectString, Role.PATIENT);
		patient.setPatientsCM(patient.getCaremanager().getUsername());
		execution.setVariable(Constants.VAR_PATIENT, patient);
		
		
		//parse input data & save to eXist (same as periodic value entry)
		if (!(si.pint.eoskrba.ediabetes.inputDiab.delegate.PatientEntersDiabValuesCompleteListener.saveInputAndSetCalcData(execution)))
			return;		
		
		HashMap<String, Object> variables = new HashMap<String, Object>();	

		variables.put(Constants.VAR_maxNumberOfSentReminders, 3);
		variables.put(Constants.VAR_reminderPacientIntervalDuration, "PT24H");
		
		execution.setVariables(variables);

	}


}
