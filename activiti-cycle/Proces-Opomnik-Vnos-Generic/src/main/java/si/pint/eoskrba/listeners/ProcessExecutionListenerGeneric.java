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
package si.pint.eoskrba.listeners;

import java.util.GregorianCalendar;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;

import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.GenericProcessId.GENERIC_PROCESS_ID;


public class ProcessExecutionListenerGeneric implements ExecutionListener {

	
	public void notify(DelegateExecution e) throws Exception {
		
		//skip if ProcessExecutionListener already on session
		if (e.getVariable(Constants.VAR_processExecutionListener) != null)
			return;
		
		String genericProcId = (String) e.getVariable(Constants.VAR_genericProcId);
		GENERIC_PROCESS_ID genProcIdEnum = GENERIC_PROCESS_ID.eval(genericProcId);
		
		// V sejo zapiši datum nastanka instance procesa. Rabimo zaradi eŠporta, da lahko
		// kasneje webapp ve, za kateri dan vadbe gre
		GregorianCalendar gc = new GregorianCalendar();
		e.setVariable(Constants.VAR_PROCES_GENERIC_INSTANCE_CREATION_TIMESTAMP, gc);
		
		switch (genProcIdEnum) {
			case ASTMA_ACT :
				e.setVariable(Constants.VAR_notifyDoctorWhenCritical, true);
				e.setVariable(Constants.VAR_processExecutionListener, new si.pint.eoskrba.eastma.inputGeneric.listeners.ProcessExecutionListener(e));
				e.setVariable(Constants.VAR_cmProcessExecutionListener, new si.pint.eoskrba.eastma.inputACT.listeners.SequenceFlowNotifyCmExecutionListener());
				e.setVariable(Constants.VAR_PROCESS_TTL, "P21D"); // 4 tedni
				break;
			case DIABETES_DSCA :
				e.setVariable(Constants.VAR_notifyDoctorWhenCritical, false );
				e.setVariable(Constants.VAR_processExecutionListener, new si.pint.eoskrba.ediabetes.dsca.listeners.ProcessDscaExecutionListener(e));
				e.setVariable(Constants.VAR_cmProcessExecutionListener, new si.pint.eoskrba.ediabetes.dsca.listeners.NotifyCmDscaExecutionListener());
				e.setVariable(Constants.VAR_PROCESS_TTL, "P21D"); // 4 tedni
				break;
			case DIABETES_VALUES :
				e.setVariable(Constants.VAR_notifyDoctorWhenCritical, false );
				e.setVariable(Constants.VAR_processExecutionListener, new si.pint.eoskrba.ediabetes.inputDiab.listeners.ProcessExecutionListenerInputDiab(e));
				e.setVariable(Constants.VAR_cmProcessExecutionListener, new si.pint.eoskrba.ediabetes.inputDiab.listeners.NotifyCmDiabExecutionListener());
				e.setVariable(Constants.VAR_PROCESS_TTL, "P14D"); // 2 tedna
				break;
			case SHIZOFRENIJA_VALUES :
				e.setVariable(Constants.VAR_notifyDoctorWhenCritical, true );
				e.setVariable(Constants.VAR_processExecutionListener, new si.pint.eoskrba.eshizofrenija.inputShiz.listeners.ProcessExecutionListener(e));
				e.setVariable(Constants.VAR_cmProcessExecutionListener, new si.pint.eoskrba.eshizofrenija.inputShiz.listeners.NotifyCmExecutionListener());
				e.setVariable(Constants.VAR_PROCESS_TTL, "P14D"); // 2 tedna
				break;
			case SHIZOFRENIJA_EWSQ :
				e.setVariable(Constants.VAR_notifyDoctorWhenCritical, true );
				e.setVariable(Constants.VAR_processExecutionListener, new si.pint.eoskrba.eshizofrenija.ewsq.listeners.ProcessExecutionListenerEQWS(e));
				e.setVariable(Constants.VAR_cmProcessExecutionListener, new si.pint.eoskrba.eshizofrenija.ewsq.listeners.NotifyCmExecutionListenerEQWS());
				e.setVariable(Constants.VAR_PROCESS_TTL, "P14D"); // 2 tedna
				break;		
			case SPORT_VALUES :
				e.setVariable(Constants.VAR_notifyDoctorWhenCritical, true );
				e.setVariable(Constants.VAR_processExecutionListener, new si.pint.eoskrba.esport.inputSport.listeners.ProcessExecutionListenerInputSport(e));
				e.setVariable(Constants.VAR_cmProcessExecutionListener, new si.pint.eoskrba.esport.inputSport.listeners.NotifyCmSportExecutionListener());
				e.setVariable(Constants.VAR_PROCESS_TTL, "P3D"); // TODO: Nastavi (trenutno 3 dni)
				break;
			default :
				e.setVariable(Constants.VAR_PROCESS_TTL, "P1Y"); // Tega koda ne bi smela nikoli doseči
				break;
		}
		
	}

}
