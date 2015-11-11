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
package si.pint.eoskrba.ehujsanje.inputKoraki.listeners;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.log4j.MDC;

import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.WeightLossStepNoArcheSrv;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.User;

/**
 * Stores last completed step in eXist.
 * 
 * @author Blaz
 *
 */
@Log4j
public class VnosMeritevCaCompleteSequenceListener implements ExecutionListener {

	public static final String STEP_NO_TEMPLATE_PATH = "/data[at0001]/items[at0002]/value";
	public static final String DATE_TEMPLATE_PATH = "/data[at0001]/items[at0003]/value";
	

	public void notify(DelegateExecution execution) throws Exception {
		
		User patient = (User) execution.getVariable(Constants.VAR_PATIENT);
		
		WeightLossStepNoArcheSrv service = new WeightLossStepNoArcheSrv();
		service.initInput();
		

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdfNew = new SimpleDateFormat("yyyy-MM-dd");
		
		Map<ArcheDataPath, String> userInputsMap = new HashMap<ArcheDataPath, String>();
		userInputsMap.put(new ArcheDataPath(STEP_NO_TEMPLATE_PATH) , patient.getProcessStep());
		userInputsMap.put(new ArcheDataPath(DATE_TEMPLATE_PATH), sdfNew.format(c.getTime()));
		
		service.setAndValidataData(userInputsMap);
		service.saveInput(SystemType.EOSKRBAPROCESSENGINE, patient.getUsername(), patient.getUsername());
		
		MDC.put("user", patient.getUsername());
		MDC.put("userRole", "patient");
		MDC.put("task", "VnosMeritevCaCompleteSequenceListener");
		MDC.put("taskContent", "Shranjevanje uspesno zakljucenega koraka");
		MDC.put("taskType", "H");
		log.info("Shranjevanje uspesno zakljucenega koraka");

	}

}
