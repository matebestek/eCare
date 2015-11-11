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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.User;

public class CalcCategoryDelegate implements JavaDelegate {
	
	//process session variable key for PEF
	public static final String VAR_PEF_NOT_OK = "pefValueNotOK";
	public static final String VAR_PEF_CRITICAL = "pefCritical";
	public static final String VAR_PEF_NOT_VALID = "pefValueNotValid";	
	public static final String VAR_PEF_VALUE_INT = "pefValueInt";
	
	//template name
	public static final String FIRST_ENCOUNTER_TEMPLATE = "openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1";

	//path to weight & height in eXist XML
	public static final String PEF_EXIST_PATH = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.pljucne_funkcije_eo_as.v1']/data[@archetype_node_id='at0001']/events[@archetype_node_id='at0002']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	
	
	@Override
	public void execute(DelegateExecution delExecution) throws Exception {
		
		//System.out.println("[si.pint.eastma.inputPEF.delegate.CalcCategoryDelegate] Calculating category...");
		
		//if user input data not valid -> deactivate sequnce flow 'notOk'
		boolean pefNotValid = (Boolean) delExecution.getVariable(VAR_PEF_NOT_VALID);
		if (pefNotValid) {
			//System.out.println("[si.pint.eastma.inputPEF.delegate.CalcCategoryDelegate] PEF not valid!");
			delExecution.setVariable(VAR_PEF_NOT_OK, Boolean.FALSE); 
			delExecution.setVariable(VAR_PEF_CRITICAL, Boolean.FALSE);
			return;
		}
		
		//System.out.println("[si.pint.eastma.inputPEF.delegate.CalcCategoryDelegate] Iterating through session variables...");
		
		Map<String,Object> variables = delExecution.getVariables();
		Set<String> variablesNames = variables.keySet();
		Iterator<String> variablesIterator = variablesNames.iterator();
		
		//System.out.println("[si.pint.eastma.inputPEF.delegate.CalcCategoryDelegate] Variables in session: " + variables.size());
		
		boolean pefNotOkValue = false;
		boolean pefCritical = false;
		int pefValue = -1;
		
		while(variablesIterator.hasNext()) {
			
			String variableKey = variablesIterator.next();
			
			//System.out.println("[si.pint.eastma.inputPEF.delegate.CalcCategoryDelegate] - key: " + variableKey);
			
			if(variableKey.indexOf("?data[at0001]?events[at0002]?data[at0003]?items[at0057]?items[at0058]?value") == -1) {
				continue;
			}
			
			//System.out.println("[si.pint.eastma.inputPEF.delegate.CalcCategoryDelegate] - Found key for PEF!");
			
			String pefValueTxt = (String) variables.get(variableKey);
			pefValue = Integer.parseInt(pefValueTxt);
			
			//System.out.println("[si.pint.eastma.inputPEF.delegate.CalcCategoryDelegate] User PEF: " + pefValue);
			
			User u = (User) delExecution.getVariable(Constants.VAR_PATIENT_OBJ);
			String username = (String)delExecution.getVariable(Constants.VAR_USERNAME_APP);
			BigDecimal pef = getFirstPefFromDB(username,u.getUsername());
			BigDecimal lowerLimit = (new BigDecimal("0.6")).multiply(pef);
			BigDecimal predictedValue = (new BigDecimal("0.8")).multiply(pef);
						
			//System.out.println("[si.pint.eastma.inputPEF.delegate.CalcCategoryDelegate] Lower limit: " + lowerLimit.toString());
			//System.out.println("[si.pint.eastma.inputPEF.delegate.CalcCategoryDelegate] Predicted: " + predictedValue.toString());
			
			//business logic: route process depending on user PEF data
			//critical
			if (pefValue < lowerLimit.intValue()) {
				pefCritical = true;
				//System.out.println("[si.pint.eastma.inputPEF.delegate.CalcCategoryDelegate] Calculated category: PEF is critical.");
			}
			//not OK
			else if (pefValue >= lowerLimit.intValue() && pefValue <= predictedValue.intValue()) {
				pefNotOkValue = true;
				//System.out.println("[si.pint.eastma.inputPEF.delegate.CalcCategoryDelegate] Calculated category: PEF is not ok.");
			}
			//OK
			else if (pefValue > predictedValue.intValue()) {
				//System.out.println("[si.pint.eastma.inputPEF.delegate.CalcCategoryDelegate] Calculated category: PEF is ok.");
			}
			
		}
		
		delExecution.setVariable(VAR_PEF_NOT_OK, pefNotOkValue);
		delExecution.setVariable(VAR_PEF_CRITICAL, pefCritical);
		
		//set PEF value for later
		
		delExecution.setVariable(VAR_PEF_VALUE_INT, pefValue + "");

		
		//System.out.println("[si.pint.eastma.inputPEF.delegate.CalcCategoryDelegate] End");
		//System.out.flush();
		
	}
	
	private BigDecimal getFirstPefFromDB(String username, String patientId) {
		String pefString;
		try {
			pefString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(username, patientId, FIRST_ENCOUNTER_TEMPLATE, PEF_EXIST_PATH);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigDecimal pef = null;
		try {
			pef = new BigDecimal(pefString);
		}
		catch (NumberFormatException nfe) {
		}
		
		return pef;
	}

}
