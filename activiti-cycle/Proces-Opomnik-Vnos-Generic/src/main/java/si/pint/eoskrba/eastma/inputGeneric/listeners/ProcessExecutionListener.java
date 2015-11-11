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
package si.pint.eoskrba.eastma.inputGeneric.listeners;

import java.io.Serializable;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import si.pint.eoskrba.eastma.inputACT.delegate.InitProcessDelegate;
import si.pint.eoskrba.eastma.inputACT.delegate.IzracunKategorijeACTDelegate;
import si.pint.eoskrba.eastma.inputACT.delegate.PatientNotifySmsDelegate;
import si.pint.eoskrba.eastma.inputACT.delegate.PatientReminderSmsDelegate;
import si.pint.eoskrba.eastma.inputACT.listeners.CaremanagerObvestiloCompleteListener;
import si.pint.eoskrba.eastma.inputACT.listeners.CaremanagerObvestiloCreateListener;
import si.pint.eoskrba.eastma.inputACT.listeners.DoctorObvestiloCompleteListener;
import si.pint.eoskrba.eastma.inputACT.listeners.DoctorObvestiloCreateListener;
import si.pint.eoskrba.eastma.inputACT.listeners.PatientEntersACTCompleteListener;
import si.pint.eoskrba.eastma.inputACT.listeners.PatientEntersACTCreateListener;
import si.pint.eoskrba.eastma.inputACT.listeners.PatientObvestiloCompleteListener;
import si.pint.eoskrba.eastma.inputACT.listeners.PatientObvestiloCreateListener;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;

@Log4j
public class ProcessExecutionListener implements ExecutionListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2533157442133354907L;
	
	public ProcessExecutionListener() {
	}
	
	public ProcessExecutionListener(DelegateExecution e) throws Exception {
		this.notify(e);
	}


	public void notify(DelegateExecution e) throws Exception {
		log.info("Process: EventName=" + e.getEventName() + " Id=" + e.getId()
				+ " ProcessInstanceId=" + e.getProcessInstanceId());
			Map<String, Object> v = e.getVariables();			
			
			v.put(Constants.VAR_initProcessDelegate,v.containsKey(Constants.VAR_initProcessDelegate)?v.get(Constants.VAR_initProcessDelegate):new InitProcessDelegate());
			v.put(Constants.VAR_listenerPacientVnosComplete, v.containsKey(Constants.VAR_listenerPacientVnosComplete)?v.get(Constants.VAR_listenerPacientVnosComplete):new PatientEntersACTCompleteListener());
			v.put(Constants.VAR_listenerPacientVnosCreate,v.containsKey(Constants.VAR_listenerPacientVnosCreate)?v.get(Constants.VAR_listenerPacientVnosCreate):new PatientEntersACTCreateListener());
			v.put(Constants.VAR_izracunKategorijeDelegate,v.containsKey(Constants.VAR_izracunKategorijeDelegate)?v.get(Constants.VAR_izracunKategorijeDelegate):new IzracunKategorijeACTDelegate());
			v.put(Constants.VAR_listenerCaremanagerObvestiloComplete,v.containsKey(Constants.VAR_listenerCaremanagerObvestiloComplete)?v.get(Constants.VAR_listenerCaremanagerObvestiloComplete):new CaremanagerObvestiloCompleteListener());
			v.put(Constants.VAR_listenerCaremanagerObvestiloCreate,v.containsKey(Constants.VAR_listenerCaremanagerObvestiloCreate)?v.get(Constants.VAR_listenerCaremanagerObvestiloCreate):new CaremanagerObvestiloCreateListener());
			v.put(Constants.VAR_listenerDoctorObvestiloCreate,v.containsKey(Constants.VAR_listenerDoctorObvestiloCreate)?v.get(Constants.VAR_listenerDoctorObvestiloCreate):new DoctorObvestiloCreateListener());
			v.put(Constants.VAR_listenerDoctorObvestiloComplete,v.containsKey(Constants.VAR_listenerDoctorObvestiloComplete)?v.get(Constants.VAR_listenerDoctorObvestiloComplete):new DoctorObvestiloCompleteListener());
			v.put(Constants.VAR_patientNotifySmsDelegate,v.containsKey(Constants.VAR_patientNotifySmsDelegate)?v.get(Constants.VAR_patientNotifySmsDelegate):new PatientNotifySmsDelegate());
			v.put(Constants.VAR_patientReminderSmsDelegate,v.containsKey(Constants.VAR_patientReminderSmsDelegate)?v.get(Constants.VAR_patientReminderSmsDelegate):new PatientReminderSmsDelegate());
			v.put(Constants.VAR_listenerPatientObvestiloComplete,v.containsKey(Constants.VAR_listenerPatientObvestiloComplete)?v.get(Constants.VAR_listenerPatientObvestiloComplete):new PatientObvestiloCompleteListener());
			v.put(Constants.VAR_listenerPatientObvestiloCreate,v.containsKey(Constants.VAR_listenerPatientObvestiloCreate)?v.get(Constants.VAR_listenerPatientObvestiloCreate):new PatientObvestiloCreateListener());
			e.setVariables(v);
			
//			User patient = (User)e.getVariable(Constants.VAR_PATIENT);
//			Email cmEmail = new Email(patient.getCmEmail(),"","");
//			e.setVariable(Constants.VAR_NOTIFY_CM_EMAIL, cmEmail);

	}

}
