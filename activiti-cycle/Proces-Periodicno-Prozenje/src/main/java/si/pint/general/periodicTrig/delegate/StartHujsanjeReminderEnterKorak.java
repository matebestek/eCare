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
package si.pint.general.periodicTrig.delegate;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.util.json.JSONException;
import org.activiti.engine.impl.util.json.JSONObject;

import si.pint.activiti.rest.utils.HttpClientUtils;
import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.User;

@Log4j
public class StartHujsanjeReminderEnterKorak implements JavaDelegate {

	// process diabetes (enter values) definition id
	public static final String PROCESS_ID = "Hujsanje-Proces-Opomnik-Korak";
	
//	public static final String KORAKI_HUJSANJE = "openEHR-EHR-SECTION.eo_hu_korak.v1";
//	//preštejem elemente v cluster-u ker bo več od nič samo pri enem :)
//	public static final String HUJSANJE_COUNT = "//*[contains(name(),'items') and @archetype_node_id and count(./data/items[contains(@xsi:type,'CLUSTER')]/items) > 0]/archetype_details/archetype_id/value/text()";
	
	//root archetype name
	public static final String HU_STEP_NUMBER_TEMPLATE_ROOT = "openEHR-EHR-EVALUATION.hu_stevilka_koraka.v1";
	
	//path to data (last step completed)
	public static final String PATH_TO_STEP_NUMBER = "//items[@archetype_node_id='openEHR-EHR-EVALUATION.hu_stevilka_koraka.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0002']/value/magnitude/text()";
	
	public void execute(DelegateExecution execution) throws Exception {


		// get LDAP patients
		List<User> patientsInStudyList = UserRegistryFactory.getPatientsFromLDAP(EMPLOYEE_TYPE_ENUM.HUJSANJE);

		// find last process definition ID with key 'ProcesOpomnikVnosPEF'
		String procDefList = HttpClientUtils.doGet("process-definitions?sort=version&order=desc&size=10000");
		JSONObject procDefListJSON = new JSONObject(procDefList);

		String procId = StartProcessUtils.findProcId(PROCESS_ID, procDefListJSON);

		Iterator<User> it = patientsInStudyList.iterator();
		while (it.hasNext()) {
			User u = it.next();
			log.info("Prozenje procesa izobraževanja pacienta: " + u.getUsername() + ", opomnik: " + PROCESS_ID);
						
			//1.trenutno aktiven korak
			int currentStep = 0;			
			try{
				currentStep = Integer.parseInt(u.getProcessStep());	
			} catch(NumberFormatException nfe){
				continue;				
			}

			//1.1 skip if patient already finished all steps
			if (currentStep > Constants.MAX_STEP)
				continue;
			
			/*
			//2. expected step
			int stepExpected = findStepExpected(u); //#719: ni vec dohitevanja nalog
						
			//2.1 skip patient who's start date is in future time
			if (stepExpected < 1)
				continue;
			*/
			//check study start date
			if (!startDateOk(u))
				continue;
			
			//3.last step completed
			int lastStepCompleted = getLastStepCompleted(DBManager.ENGINE_USER_ID, u.getUsername());
			
			//3.1 patient finished with program
			if (lastStepCompleted >= Constants.MAX_STEP)
				continue;
			
			//start process only in one condition
//			if (!(currentStep == lastStepCompleted && lastStepCompleted < stepExpected)) { //look for rules in excel file
//				continue;
//			}
			if (currentStep != lastStepCompleted)
				continue;
					
			//increase process step
			u.setProcessStep(++currentStep + "");
			UserRegistryFactory.updatePatientsProcessStep(u);

			u = StartProcessUtils.getWeightAndHeightFromDB(u, EMPLOYEE_TYPE_ENUM.HUJSANJE);


			JSONObject externalObj = new JSONObject();
			try {
				externalObj.put("processDefinitionId", procId);
				externalObj.put("businessKey", PROCESS_ID);
				externalObj.put(Constants.VAR_USERNAME, u.getUsername());
				externalObj.put(Constants.VAR_WEIGHT, u.getWeight().floatValue() + "");
				externalObj.put(Constants.VAR_HEIGHT, u.getHeight().floatValue() + "");

			} catch (JSONException e) {
				e.printStackTrace();
			}

			@SuppressWarnings("unused")
			String result = HttpClientUtils.doPost("process-instance",	externalObj.toString());

		}

	}

	/**
	 * Checks if it is already time to start new process (new step)
	 * 
	 *@param u
	 * @return
	 */
	private boolean startDateOk(User u) {
		
		Calendar today = Calendar.getInstance(); 
		
		//find closest MONDAY to patients start day
		Calendar startDayEntered = Calendar.getInstance();
		startDayEntered.setTime(u.getStartDate());
		
		
		return today.compareTo(startDayEntered) >= 0;
	}


	/**
	 * Looks for last entry of template 'VkljucitevVStudijo'.
	 * 
	 * @param u
	 * @return
	 */
	public static int getLastStepCompleted(String userId, String patientId) {
		try {
			//dobim id arhetipa, ki vsebuje podatke
//			String arhetip = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(userId, patientId, HU_STEP_NUMBER_TEMPLATE_ROOT, PATH_TO_STEP_NUMBER);
			String arhetip = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID, patientId, HU_STEP_NUMBER_TEMPLATE_ROOT, PATH_TO_STEP_NUMBER);

			if(arhetip != null /* && arhetip.indexOf("openEHR")>=0 */){
//				String poz = arhetip.substring(33, 35);
				return Integer.parseInt(arhetip);
			} else {
				return 0;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	

	/**
	 * Find step that patient should be doing at current date.
	 * 
	 * @param u
	 * @return
	 *//*
	private int findStepExpected(User u) {
		
		Calendar today = Calendar.getInstance(); 
		
		//find closest MONDAY to patients start day
		Calendar startDayEntered = Calendar.getInstance();
		startDayEntered.setTime(u.getStartDate());
		int daysToSubstract = 0;

		if (startDayEntered.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
			daysToSubstract = -1;
		}
		else if (startDayEntered.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
			daysToSubstract = -2;
		}
		else if (startDayEntered.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
			daysToSubstract = -3;
		}
		else if (startDayEntered.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
			daysToSubstract = -4;
		}
		else if (startDayEntered.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			daysToSubstract = -5;
		}
		else if (startDayEntered.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			daysToSubstract = -6;
		}
		
		if (daysToSubstract != 0)
			startDayEntered.add(Calendar.DAY_OF_MONTH, daysToSubstract); //TODO: kaj ce odstevamo od 1-ega v mesecu ?? TEST!!
		
		//difference
		long diff = today.getTimeInMillis() - startDayEntered.getTimeInMillis();
		
		//start day is yet to come -> return;
		if (diff < 0)
			return -1;
		
		long daysL = diff/((long) 1000 * (long) 60 * 60 * 24);
		int days = (int) daysL;
		
		int expectedWeek = days/7; //integer division (floor), values: 0,1,...
		expectedWeek++; //if 0: expected week is 1, ...
		
		return expectedWeek;
		
	}*/

}
