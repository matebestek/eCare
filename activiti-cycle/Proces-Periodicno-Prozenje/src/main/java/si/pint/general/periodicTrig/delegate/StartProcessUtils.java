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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONException;
import org.activiti.engine.impl.util.json.JSONObject;

import si.pint.activiti.rest.utils.HttpClientUtils;
import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.GenericProcessId.GENERIC_PROCESS_ID;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;

/**
 * 
 * @author Blaz,Mate(dodal nov proces in posplo?il metode)
 *
 */
@Log4j
public class StartProcessUtils {

	//path to weight & height in eXist XML (ASTMA)
	public static final String WEIGHT_EXIST_PATH_ASTMA = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	public static final String HEIGHT_EXIST_PATH_ASTMA = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.height.v1']/data[@archetype_node_id='at0001']/events[@archetype_node_id='at0002']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0004']/value/magnitude/text()";

	//template name for first visit (ASTMA)
	public static final String FIRST_ENCOUNTER_TEMPLATE_ASTMA = "openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1";
	
	//template name for first visit (DIABETES)
	public static final String FIRST_ENCOUNTER_TEMPLATE_DIABETES = "openEHR-EHR-SECTION.vkljucitev_eo.v1";

	//path to weight & height in eXist XML (DIABETES)
	public static final String WEIGHT_EXIST_PATH_DIABETES = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	public static final String HEIGHT_EXIST_PATH_DIABETES = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.height.v1']/data[@archetype_node_id='at0001']/events[@archetype_node_id='at0002']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	
	//template name for first visit (SHIZOFRENIJA)
	public static final String FIRST_ENCOUNTER_TEMPLATE_SHIZOFRENIJA = "openEHR-EHR-SECTION.vkljucitev_eo.v1";
	
	//path to weight & height in eXist XML (SHIZOFRENIJA)
	public static final String WEIGHT_EXIST_PATH_SHIZOFRENIJA = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	public static final String HEIGHT_EXIST_PATH_SHIZOFRENIJA = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.height.v1']/data[@archetype_node_id='at0001']/events[@archetype_node_id='at0002']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0004']/value/magnitude/text()";

	//template name for first visit (DIABETES)
	public static final String FIRST_ENCOUNTER_TEMPLATE_HUJSANJE = "openEHR-EHR-SECTION.vkljucitev_eo.v1";
	//template name for first visit (DIABETES)
	public static final String FIRST_ENCOUNTER_TEMPLATE_SPORT = "openEHR-EHR-SECTION.vkljucitev_eo.v1";
	
	//path to weight & height in eXist XML (HUJSANJE)
	public static final String WEIGHT_EXIST_PATH_HUJSANJE = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	public static final String HEIGHT_EXIST_PATH_HUJSANJE = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.height.v1']/data[@archetype_node_id='at0001']/events[@archetype_node_id='at0002']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	
	//path to weight & height in eXist XML (SPORT)
	public static final String WEIGHT_EXIST_PATH_SPORT = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	public static final String HEIGHT_EXIST_PATH_SPORT = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.height.v1']/data[@archetype_node_id='at0001']/events[@archetype_node_id='at0002']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	
	
	public static boolean postPatients(String key, EMPLOYEE_TYPE_ENUM employeeType) throws Exception {
		return postPatients(key, employeeType, null);
	}
	
	/**
	 * 
	 * @param key
	 * @param employeeType
	 * @param processExecutionListener  fqn of class that implements ExecutionListener (for processes with notifications (ACT, enterDiabValues, ....))
	 * @param cmProcessExecutionListener  fqn of class that implements ExecutionListener for notifying CM (after 3 days)
	 * @return
	 * @throws Exception
	 */
	public static boolean postPatients(String key, EMPLOYEE_TYPE_ENUM employeeType, GENERIC_PROCESS_ID genProcIdEnum) throws Exception {

		//get LDAP patients
		List<User> patientsInStudyList = UserRegistryFactory.getPatientsFromLDAP(employeeType);  

		//find last process definition ID with key 'ProcesOpomnikVnosPEF'
		String procDefList = HttpClientUtils.doGet("process-definitions?sort=version&order=desc&size=10000");
		JSONObject procDefListJSON = new JSONObject(procDefList);
		
		String procId = findProcId(key, procDefListJSON);
		
		Iterator<User> it = patientsInStudyList.iterator();
		while (it.hasNext()) {
			
			User u = it.next();
			
			// zdravila prozimo samo na 3 mesece (vendar je treba za vsakega pacienta vsak dan
			// izracunati ali je cas za opomnik)
			if (key.equals(StartReminderZdravilaDelegate.PROCESS_ID)) {
				if (!isTimeForReminder(u.getUsername(), employeeType))
					continue;
			}
			

			if(genProcIdEnum != null) {
				
				// Pri opomnikih za vnos vrednosti pri eŠportu (periodični proces se štarta vsako jutro
				// ob 7:00) moramo preveriti, ali ima pacient na današnji dan kako vadbo. Če jo ima, potem
				// sprožimo opomnik
				if(genProcIdEnum.equals(GENERIC_PROCESS_ID.SPORT_VALUES)) {
					if(!patientShouldExerciseToday(u.getUsername())) continue;
				}
				
			}
			
			log.info("Prozenje procesa obvescanja za pacienta: " + u.getUsername() + ", opomnik: " + key);
			
			u = getWeightAndHeightFromDB(u, employeeType);
			
			JSONObject obj = new JSONObject();
			JSONObject externalObj = new JSONObject();
			
			//date of birth
			Calendar c = Calendar.getInstance();
			c.setTime(u.getDateOfBirth());
			
			try {
				obj.put(Constants.VAR_BID, u.getBid());
				obj.put(Constants.VAR_eMAIL, u.getEmail());
			    obj.put(Constants.VAR_MOBILE_TEL_NUM, u.getMobilePhone());
			    obj.put(Constants.VAR_SEX, u.getSex().toString());
			    obj.put(Constants.VAR_BIRTH_DATE, UserRegistryFactory.parseDate(u.getDateOfBirth()));
			    obj.put(Constants.VAR_WEIGHT, u.getWeight().intValue() + ""); 
			    obj.put(Constants.VAR_HEIGHT, u.getHeight().intValue() + ""); 
			    obj.put(Constants.VAR_ROLE, Role.getIdFromRole(u.getRole()));
			    obj.put(Constants.VAR_USERNAME, u.getUsername());
			    obj.put(Constants.VAR_FIRST_NAME_LAST_NAME, u.getFirstNameLastName());
			    obj.put(Constants.VAR_PROCESS_STEP, u.getProcessStep());
			    
			    //class name for process execution listener
			    if (genProcIdEnum != null) {
			    	externalObj.put(Constants.VAR_genericProcId, genProcIdEnum.eval());			    	
			    }
			     
			    externalObj.put("processDefinitionId", procId);
			    externalObj.put("businessKey", key);
			    externalObj.put(Constants.VAR_PATIENT_OBJ, obj.toString());
			      
			} catch (JSONException e) {
				e.printStackTrace();
			}		
			  
			// zalaufa proces
			String result = HttpClientUtils.doPost("process-instance", externalObj.toString());
			
		}		
		return true;
	}
	
	/**
	 * Find proces ID that is the latest version of proces deployed on server.
	 * 
	 * @param procDefListJSON
	 * @return
	 */
	public static String findProcId(String key, JSONObject procDefListJSON) {
		JSONArray data = procDefListJSON.getJSONArray("data");
		String lastId = null;
		for (int i = 0; i < data.length();  i++) {
			JSONObject obj = (JSONObject) data.get(i);
			if (key.equals(obj.getString("key"))) {
				lastId = obj.getString("id");
				// Debug TODO: Odstrani
				System.out.println("[Proces-Periodicno-Prozenje] [StartProcessUtils] ID najnovejse verzije procesa '" + key + "' je " + lastId);
				// Najnovejsa verzija procesa je najdena, nehaj s preiskovanjem
				break;
			}
		}
		return lastId;
	}
	
	
	/**
	 * Looks for last entry of template 'VkljucitevVStudijo'.
	 * 
	 * @param u
	 * @return
	 */
	public static User getWeightAndHeightFromDB(User u, EMPLOYEE_TYPE_ENUM employeeType) {
		try {
			String weightString = null;
			String heightString = null;
			
			if (employeeType.equals(EMPLOYEE_TYPE_ENUM.ASTMA)) {
				weightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_ASTMA, WEIGHT_EXIST_PATH_ASTMA);
				heightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_ASTMA, HEIGHT_EXIST_PATH_ASTMA);
			}
			else if (employeeType.equals(EMPLOYEE_TYPE_ENUM.DIABETES)) {
				weightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_DIABETES, WEIGHT_EXIST_PATH_DIABETES);
				heightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_DIABETES, HEIGHT_EXIST_PATH_DIABETES);				
			}
			else if (employeeType.equals(EMPLOYEE_TYPE_ENUM.SHIZOFRENIJA)){
				weightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_SHIZOFRENIJA, WEIGHT_EXIST_PATH_SHIZOFRENIJA);
				heightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_SHIZOFRENIJA, HEIGHT_EXIST_PATH_SHIZOFRENIJA);
			} else if (employeeType.equals(EMPLOYEE_TYPE_ENUM.HUJSANJE)){
				weightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_HUJSANJE, WEIGHT_EXIST_PATH_HUJSANJE);
				heightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_HUJSANJE, HEIGHT_EXIST_PATH_HUJSANJE);
			} else if (employeeType.equals(EMPLOYEE_TYPE_ENUM.SPORT)){
				weightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_SPORT, WEIGHT_EXIST_PATH_SPORT);
				heightString = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast(DBManager.ENGINE_USER_ID,u.getUsername(), FIRST_ENCOUNTER_TEMPLATE_SPORT, HEIGHT_EXIST_PATH_SPORT);
			}

			
			if (weightString == null || heightString == null) {
				throw new NullPointerException("Za uporabnika " + u.getUsername() + " manjkajo podatki o tezi ali visini!");
			}
			u.setWeight(new BigDecimal(weightString));
			u.setHeight(new BigDecimal(heightString));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return u;
	}	
	
	/**
	 * Finds user in eXist DB and check if it is time for reminder. This method call sets time for
	 * reminder to 90 days (3 months) and sends reminder 2 weeks prior to the last day of cycle.
	 * 
	 * @deprecated Use isTimeForReminder with daysInCycle and sendReminderAfterDays parameters
	 */
	@Deprecated
	protected static boolean isTimeForReminder(String username, EMPLOYEE_TYPE_ENUM employeeType) {
		return isTimeForReminder(username, employeeType, 90, 75);
	}
	
	/**
	 * Finds user in eXist DB and check if it is time for reminder. (Depending of the time of first visit)
	 * 
	 * @param username
	 * @return
	 */
	protected static boolean isTimeForReminder(String username, EMPLOYEE_TYPE_ENUM employeeType, int daysInCycle, int sendReminderAfterDays) {
		try {
			String timestampFirstVisit = null;
			if (employeeType.equals(EMPLOYEE_TYPE_ENUM.ASTMA))
				timestampFirstVisit = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readFirstTimeStamp(DBManager.ENGINE_USER_ID,username, StartProcessUtils.FIRST_ENCOUNTER_TEMPLATE_ASTMA);
			else if (employeeType.equals(EMPLOYEE_TYPE_ENUM.DIABETES))
				timestampFirstVisit = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readFirstTimeStamp(DBManager.ENGINE_USER_ID,username, StartProcessUtils.FIRST_ENCOUNTER_TEMPLATE_DIABETES);
			else if (employeeType.equals(EMPLOYEE_TYPE_ENUM.SHIZOFRENIJA)){
				timestampFirstVisit = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readFirstTimeStamp(DBManager.ENGINE_USER_ID,username, StartProcessUtils.FIRST_ENCOUNTER_TEMPLATE_SHIZOFRENIJA);
			}else if (employeeType.equals(EMPLOYEE_TYPE_ENUM.HUJSANJE)){
				timestampFirstVisit = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readFirstTimeStamp(DBManager.ENGINE_USER_ID,username, StartProcessUtils.FIRST_ENCOUNTER_TEMPLATE_HUJSANJE);
			}else if (employeeType.equals(EMPLOYEE_TYPE_ENUM.SPORT)){
				timestampFirstVisit = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readFirstTimeStamp(DBManager.ENGINE_USER_ID,username, StartProcessUtils.FIRST_ENCOUNTER_TEMPLATE_SPORT);
			}
			
			if (timestampFirstVisit == null || timestampFirstVisit.equals("")) {
				throw new NullPointerException("Za uporabnika " + username + " manjkajo podatki o prvem pregledu!");
			}
			long tsFirstVisitLong = Long.parseLong(timestampFirstVisit);
			long nowTS = System.currentTimeMillis();
			long diff = nowTS - tsFirstVisitLong;
			
			long daysL = diff/((long) 1000 * (long) 60 * 60 * 24);
			int days = (int) daysL;
			
			if (days != 0 && (days - sendReminderAfterDays) % daysInCycle == 0) {
				return true;
			}						

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
		
	}	
	
	/**
	 * Za pacienta preveri, ali ima za današnji dan načrtovano kako vadbo v koledarju vadb
	 * (eŠport).
	 * 
	 * @author Alex B. (alex.besir@gmail.com)
	 * @param username Uporabniško ime pacienta
	 * @return true, če ima; false, če nima ali če pride do napake pri branju
	 */
	protected static boolean patientShouldExerciseToday(String username) {
		
		// Pripravi vrednost za vračanje
		boolean returnVal = false;
		
		// Pridobi koledar vadb
		String xml = "";
		try {
			xml = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLastWholeXml(
				username,
				"eSport",
				username,
				"openEHR-EHR-OBSERVATION.sport_koledar.v1"
			);
		} catch(Exception e) {
			System.out.println("[StartProcessUtils] Pacient " + username + " bodisi nima koledarja vadb ali pa je prislo do napake pri branju. [ERRCODE 1]");
			return false;
		}
		
		// Če je koledar prazen xml, potem je prišlo do napake
		if(xml == null || xml.isEmpty()) {
			System.out.println("[StartProcessUtils] Pacient " + username + " bodisi nima koledarja vadb ali pa je prislo do napake pri branju. [ERRCODE 2]");
			return false;
		}
		
		// Preoblikuj vsebino xml-ja
		xml = xml.replace("\n", "").replace("\r", "");
		xml = xml.replaceFirst(".*<data archetype_node_id=\"at0003\" xsi:type=\"v1:ITEM_TREE\">", "");
		
		// Sestavi današnji datum
		GregorianCalendar gc = new GregorianCalendar();
		int year  = gc.get(Calendar.YEAR);
		int month = gc.get(Calendar.MONTH) + 1;
		int day   = gc.get(Calendar.DAY_OF_MONTH);
		String dv_date_time = year + "-" + ((month<10)?("0"+month):(month)) + "-" + ((day<10)?("0"+day):(day)) + "T00";
		
		// Poišči, ali obstaja današnji datum v xml-ju
		if( xml.indexOf(dv_date_time) >= 0) returnVal = true;
		
		// Vrni
		return returnVal;
		
	}
	
	/**
	 * Generate new ExecutionListener from fully qualified name.
	 * 
	 * @param fqn
	 * @return
	 */
	public static Object getObjectFromClassName(String fqn) {
		Class c = null;
		try {
			c = Class.forName(fqn);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Object cObj = null;
		try {
			cObj = c.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return cObj;
	}
}
