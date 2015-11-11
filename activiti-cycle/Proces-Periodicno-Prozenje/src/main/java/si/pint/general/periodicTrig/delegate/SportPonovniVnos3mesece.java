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
import java.util.Date;
import java.util.GregorianCalendar;
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
public class SportPonovniVnos3mesece implements JavaDelegate {

	// process diabetes (enter values) definition id
	public static final String PROCESS_ID = "Sport-Proces-Ponovni-Pacienta-V-Studijo";
	
	public void execute(DelegateExecution execution) throws Exception {

		// get LDAP patients
		List<User> patientsInStudyList = UserRegistryFactory.getPatientsFromLDAP(EMPLOYEE_TYPE_ENUM.SPORT);

		// find last process definition ID with key 'ProcesOpomnikVnosPEF'
		String procDefList = HttpClientUtils.doGet("process-definitions?sort=version&order=desc&size=10000");
		JSONObject procDefListJSON = new JSONObject(procDefList);

		String procId = StartProcessUtils.findProcId(PROCESS_ID, procDefListJSON);

		Iterator<User> it = patientsInStudyList.iterator();
		while (it.hasNext()) {
			
			User u = it.next();

			//UserRegistryFactory.updatePatientsProcessStep(u);
			//u = StartProcessUtils.getWeightAndHeightFromDB(u, EMPLOYEE_TYPE_ENUM.SPORT);

			if (isTimeForReminder(u.getUsername())) {
				log.info("[SportPonovniVnos3mesece] Prozenje procesa ponovnega vnosa: " + u.getUsername() + ", opomnik: " + PROCESS_ID);
				log.info("[SportPonovniVnos3mesece] Dodeljeno skrbniku: " + UserRegistryFactory.findPatientsDoctor(u.getUsername()) );
				
				JSONObject externalObj = new JSONObject();
				try {
					externalObj.put("processDefinitionId", procId);
					externalObj.put("businessKey", PROCESS_ID);
					externalObj.put(Constants.VAR_USERNAME, u.getUsername());
					externalObj.put(Constants.VAR_USERNAME_APP, UserRegistryFactory.findPatientsDoctor(u.getUsername()));
					//externalObj.put(Constants.VAR_WEIGHT, u.getWeight().floatValue() + "");
					//externalObj.put(Constants.VAR_HEIGHT, u.getHeight().floatValue() + "");
	
				} catch (JSONException e) {
					e.printStackTrace();
				}
	
				@SuppressWarnings("unused")
				String result = HttpClientUtils.doPost("process-instance",	externalObj.toString());
			}
		}
	}


	public static final String VNOS_TEMPLATE_SPORT = "openEHR-EHR-SECTION.vkljucitev_eo.v1";

	/**
	 * Looks for last entry of template 'VkljucitevVStudijo'.
	 * 
	 * @param u
	 * @return
	 */
	// BSF ta je za test naslednja zares
	//public static boolean isTimeForReminder(String username) {
	//	return true;
	//}
	public static boolean isTimeForReminder(String username) {
		try {
			String timestampFirstVisit = null;
			timestampFirstVisit = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readFirstTimeStamp(DBManager.ENGINE_USER_ID,username, VNOS_TEMPLATE_SPORT);

			
			if (timestampFirstVisit == null || timestampFirstVisit.equals("")) {
				throw new NullPointerException("[SportPonovniVnos3mesece] Za uporabnika " + username + " manjkajo podatki o prvem pregledu!");
			}
			long tsFirstVisitLong = Long.parseLong(timestampFirstVisit);
			long nowTS = System.currentTimeMillis();
			
			long dayMilis = (1000*60*60*24);
			
			GregorianCalendar calVnosa = new GregorianCalendar();
			calVnosa.setTimeInMillis(tsFirstVisitLong);
			
			calVnosa.add(Calendar.MONTH, 3);
			long after3m = calVnosa.getTimeInMillis();
			calVnosa.add(Calendar.MONTH, 3);
			long after6m = calVnosa.getTimeInMillis();
			
			if (nowTS>after3m && nowTS<(after3m+dayMilis)) {
				return true;
			}
			if (nowTS>after6m && nowTS<(after6m+dayMilis)) {
				return true;
			}						
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void main(String[] args) {
		String timestampFirstVisit = null;
		timestampFirstVisit = "1341232653671";

		long tsFirstVisitLong = Long.parseLong(timestampFirstVisit);
		long nowTS = System.currentTimeMillis();
		
		long dayMilis = (long)1000*60*60*24;
		
		GregorianCalendar calVnosa = new GregorianCalendar();
		
		calVnosa.setTimeInMillis(tsFirstVisitLong);
		calVnosa.add(Calendar.DAY_OF_YEAR, -1);
		System.out.println(new Date(calVnosa.getTimeInMillis()));
		
		calVnosa.add(Calendar.DAY_OF_YEAR, 1);
		long after3m = calVnosa.getTimeInMillis();
		System.out.println(new Date(calVnosa.getTimeInMillis()));
		calVnosa.add(Calendar.DAY_OF_YEAR, 1);
		long after6m = calVnosa.getTimeInMillis();
		System.out.println(new Date(calVnosa.getTimeInMillis()));
		
		if (nowTS>after3m && nowTS<(after3m+dayMilis)) {
			System.out.println("3m");
		}
		if (nowTS>after6m && nowTS<(after6m+dayMilis)) {
			System.out.println("6m");
		}	
		System.out.println("nič");
	}

}
