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

import java.math.BigDecimal;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.util.json.JSONObject;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;

public class InitProcessVarsDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		//get user (patient) from session
		String ConstantsString = (String) execution.getVariable(Constants.VAR_PATIENT_OBJ);
		
		if (ConstantsString == null) {
			throw new RuntimeException("Constants on proces 'Opomnik-Vnos-PEF' not defined!");
			//TODO: go to end event
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

	}

}
