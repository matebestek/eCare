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
package si.pint.eastma.inputPEF.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.EnterPEFArcheSrv;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eastma.inputPEF.delegate.CalcCategoryDelegate;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.User;

/**
 * 
 * User completes task. This object executes validation.
 * 
 * @author Blaz
 *
 */
public class PatientEntersPEFValueListener implements TaskListener {
	

	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution execution = delegateTask.getExecution();
				
		EnterPEFArcheSrv archeService = new EnterPEFArcheSrv();
		if (!archeService.initInput())
			return;
		
		//user input data
		Map<ArcheDataPath, Object>  userInputsMap = new HashMap<ArcheDataPath, Object>();
		
		//look for all possible attributes on session
		Map allAtributes = archeService.getInputsMap();
		Iterator it = allAtributes.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (execution.getVariable(key.replaceAll("/", "?")) != null) {
				//System.out.println("[si.pint.eastma.inputPEF.delegate.PatientEntersPEFValueListener] " + key + " = " + execution.getVariable(key.replaceAll("/", "?")));
				userInputsMap.put(new ArcheDataPath(key), execution.getVariable(key.replaceAll("/", "?")));
			}
		}
		
		execution.setVariable(CalcCategoryDelegate.VAR_PEF_NOT_VALID, Boolean.FALSE);
		
		//data validation
		if (userInputsMap.isEmpty() || !archeService.setAndValidataData(userInputsMap)) {
			execution.setVariable(CalcCategoryDelegate.VAR_PEF_NOT_VALID, Boolean.TRUE);
			return;
		}				
		
		//save data in XML form to DB
		//user
		User user = (User) execution.getVariable(Constants.VAR_PATIENT_OBJ);
		String username = (String) execution.getVariable(Constants.VAR_USERNAME_APP);
		if (user != null && user.getUsername() != null && !user.getUsername().equals(""))
			archeService.saveInput(SystemType.EOSKRBAPROCESSENGINE,username,user.getUsername());
		
	}

}
