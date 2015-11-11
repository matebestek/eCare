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
package si.pint.eoskrba.ehujsanje.inputFlat.listeners;

import java.io.Serializable;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.ehujsanje.inputFlat.delegate.CalcCategoryWeightLossValuesDelegate;
import si.pint.eoskrba.ehujsanje.inputFlat.delegate.PatientNotifySmsDelegate;


@Log4j
public class ProcessExecutionListenerInputFlat implements ExecutionListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3283560953302006018L;
	
	public void notify(DelegateExecution e) throws Exception {
		log.info("Process: EventName=" + e.getEventName() + " Id=" + e.getId()
				+ " ProcessInstanceId=" + e.getProcessInstanceId());
			Map<String, Object> v = e.getVariables();			
						
			v.put(Constants.VAR_izracunKategorijeDelegate,v.containsKey(Constants.VAR_izracunKategorijeDelegate)?v.get(Constants.VAR_izracunKategorijeDelegate):new CalcCategoryWeightLossValuesDelegate());

			v.put(Constants.VAR_listenerPatientObvestiloComplete,v.containsKey(Constants.VAR_listenerPatientObvestiloComplete)?v.get(Constants.VAR_listenerPatientObvestiloComplete):new PatientObvestiloCompleteListener());
			v.put(Constants.VAR_listenerPatientObvestiloCreate,v.containsKey(Constants.VAR_listenerPatientObvestiloCreate)?v.get(Constants.VAR_listenerPatientObvestiloCreate):new PatientObvestiloCreateListener());

			v.put(Constants.VAR_listenerCaremanagerObvestiloComplete,v.containsKey(Constants.VAR_listenerCaremanagerObvestiloComplete)?v.get(Constants.VAR_listenerCaremanagerObvestiloComplete):new CaremanagerObvestiloCompleteListener());
			v.put(Constants.VAR_listenerCaremanagerObvestiloCreate,v.containsKey(Constants.VAR_listenerCaremanagerObvestiloCreate)?v.get(Constants.VAR_listenerCaremanagerObvestiloCreate):new CaremanagerObvestiloCreateListener());
						
			v.put(Constants.VAR_patientNotifySmsDelegate,v.containsKey(Constants.VAR_patientNotifySmsDelegate)?v.get(Constants.VAR_patientNotifySmsDelegate):new PatientNotifySmsDelegate());
			
			e.setVariables(v);
			
	}

}
