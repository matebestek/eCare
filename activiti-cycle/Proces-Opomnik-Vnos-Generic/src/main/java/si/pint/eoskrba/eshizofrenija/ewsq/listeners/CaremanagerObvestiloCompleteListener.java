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
/**
 * 
 */
package si.pint.eoskrba.eshizofrenija.ewsq.listeners;

import java.io.Serializable;

import lombok.extern.apachecommons.CommonsLog;

import org.activiti.engine.delegate.DelegateTask;
import org.apache.log4j.MDC;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.archetypes.exceptions.LdapException;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.listeners.ACaremanagerObvestiloCompleteListener;
import si.pint.eoskrba.model.User;
import si.pint.eoskrba.utils.StoreStopCoopComentHelper;

/**
 * @author mate
 * 
 */
@CommonsLog
public class CaremanagerObvestiloCompleteListener extends
		ACaremanagerObvestiloCompleteListener implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8192976120084966891L;

	@Override
	public void notify(DelegateTask delegateTask) {
		
		log.info("Caremanager obveščen");
		//ko obravnavam Reminder pacientu v povezavi s Timer-jem
		if (delegateTask.getVariable(Constants.VAR_PATIENT_COOPERATES) != null) {
			//gui controls work only with String types (class: size, type, mendatory)
			String patientCooperates = (String) delegateTask.getVariable(Constants.VAR_PATIENT_COOPERATES);
			
			Boolean patCoopBool = Boolean.parseBoolean(patientCooperates);
			if (!patCoopBool) {
				User patient = (User) delegateTask
						.getVariable(Constants.VAR_PATIENT);
				try {
					UserRegistryFactory.editStopCooperationFlag(
							patient.getUsername(), true);
					
					MDC.put("user", patient.getCaremanager().getUsername());
					MDC.put("userRole", patient.getCaremanager().getRole().toString());
					MDC.put("task", "CaremanagerObvestiloCompleteListener");
					MDC.put("taskContent", "prekinitev sodelovanja pacienta");
					MDC.put("taskType", "S");
					log.info("Caremanager je ugotovil, da pacient " + patient.getUsername()+" istopa iz klinicne studije");
				} catch (LdapException e) {
					log.error(
							"napaka pri spremembi statusa pacienta v nekooperativnega:",
							e);
				}
			} else {
				delegateTask
						.removeVariable(Constants.VAR_NUMBER_OF_SENT_REMINDERS);
			}
			StoreStopCoopComentHelper.storeComment(delegateTask.getExecution(), EMPLOYEE_TYPE_ENUM.SHIZOFRENIJA);
		}
	}

}
