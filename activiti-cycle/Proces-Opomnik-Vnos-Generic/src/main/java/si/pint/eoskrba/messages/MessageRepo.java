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
package si.pint.eoskrba.messages;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Getter constants for all text messages in eOskrba.
 * 
 * @author Blaz
 *
 */
public class MessageRepo {
		
	private static Properties properties = null; 

	public static String ASTMA_PATIENT_SMS_ACT_REMINDER = returnValue("astma-patient-sms-actReminder");
	public static String ASTMA_PATIENT_SMS_ACT_CRITICAL = returnValue("astma-patient-sms-actCritical");
	public static String ASTMA_PATIENT_SMS_REMINDER_CHECKUP = returnValue("astma-patient-sms-reminder-checkup");
	
	public static String DIABETES_PATIENT_MAIL_REMINDER = returnValue("diabetes-patient-mail-reminder");
	public static String DIABETES_PATIENT_MAIL_REMINDER_GREET = returnValue("diabetes-patient-mail-reminder-greet");
	public static String DIABETES_PATIENT_SMS_CRITICAL = returnValue("diabetes-patient-sms-critical");
	public static String DIABETES_PATIENT_TASK_NAME_NORMAL = returnValue("diabetes-patient-task-name-normal");
	public static String DIABETES_PATIENT_SMS_REMINDER = returnValue("diabetes-patient-sms-reminder");	
	public static String DIABETES_PATIENT_MAIL_VALUES_NOK_CONTENT = returnValue("diabetes-patient-mail-values-not-ok-content");
	public static String DIABETES_PATIENT_MAIL_VALUES_NOK_SUBJECT = returnValue("diabetes-patient-mail-values-not-ok-subject");
	public static String DIABETES_PATIENT_TASK_VALUES_NOK = returnValue("diabetes-patient-task-values-not-ok");	
	public static String DIABETES_PATIENT_MAIL_VALUES_CRITICAL_CONTENT = returnValue("diabetes-patient-mail-values-critical-content");
	public static String DIABETES_PATIENT_MAIL_VALUES_CRITICAL_SUBJECT = returnValue("diabetes-patient-mail-values-critical-subject");
	public static String DIABETES_PATIENT_TASK_VALUES_CRITICAL = returnValue("diabetes-patient-task-values-critical");
	public static String DIABETES_PATIENT_SMS_REMINDER_CHECKUP = returnValue("diabetes-patient-sms-reminder-checkup");
	public static String DIABETES_PATIENT_MAIL_REMINDER_CHECKUP = returnValue("diabetes-patient-mail-reminder-checkup");
	public static String DIABETES_PATIENT_TASK_REMINDER_DSCA = returnValue("diabetes-patient-task-reminder-dsca");
	public static String DIABETES_PATIENT_SMS_REMINDER_DSCA = returnValue("diabetes-patient-sms-reminder-dsca");
	public static String DIABETES_PATIENT_TASK_NAME_NORMAL_DSCA = returnValue("diabetes-patient-task-name-normal-dsca");
	  
	public static String SPORT_PATIENT_SMS_CRITICAL = returnValue("sport-patient-sms-critical");
	public static String SPORT_PATIENT_SMS_REMINDER = returnValue("sport-patient-sms-reminder");	
	public static String SPORT_PATIENT_MAIL_VALUES_NOK_CONTENT = returnValue("sport-patient-mail-values-not-ok-content");
	public static String SPORT_PATIENT_MAIL_VALUES_NOK_SUBJECT = returnValue("sport-patient-mail-values-not-ok-subject");
	public static String SPORT_PATIENT_TASK_VALUES_NOK = returnValue("sport-patient-task-values-not-ok");	
	public static String SPORT_PATIENT_TASK_NAME_NORMAL = returnValue("sport-patient-task-name-normal");
	public static String SPORT_PATIENT_MAIL_VALUES_CRITICAL_CONTENT = returnValue("sport-patient-mail-values-critical-content");
	public static String SPORT_PATIENT_MAIL_VALUES_CRITICAL_SUBJECT = returnValue("sport-patient-mail-values-critical-subject");
	public static String SPORT_PATIENT_TASK_VALUES_CRITICAL = returnValue("sport-patient-task-values-critical");
	
	public static String SHIZOFRENIJA_PATIENT_TASK_NAME_NORMAL = returnValue("shizofrenija-patient-task-name-normal");
	public static String SHIZOFRENIJA_PATIENT_MAIL_VALUES_NOK_CONTENT = returnValue("shizofrenija-patient-mail-values-not-ok-content");
	public static String SHIZOFRENIJA_PATIENT_MAIL_VALUES_NOK_SUBJECT = returnValue("shizofrenija-patient-mail-values-not-ok-subject");
	public static String SHIZOFRENIJA_PATIENT_TASK_VALUES_NOK = returnValue("shizofrenija-patient-task-values-not-ok");	
	public static String SHIZOFRENIJA_PATIENT_MAIL_VALUES_CRITICAL_CONTENT = returnValue("shizofrenija-patient-mail-values-critical-content");
	public static String SHIZOFRENIJA_PATIENT_MAIL_VALUES_CRITICAL_SUBJECT = returnValue("shizofrenija-patient-mail-values-critical-subject");
	public static String SHIZOFRENIJA_PATIENT_TASK_VALUES_CRITICAL = returnValue("shizofrenija-patient-task-values-critical");
	
	public static String SHIZOFRENIJA_EQWS_PATIENT_TASK_NAME_NORMAL = returnValue("shizofrenija-eqws-patient-task-name-normal");
	public static String SHIZOFRENIJA_EQWS_PATIENT_MAIL_VALUES_NOK_CONTENT = returnValue("shizofrenija-eqws-patient-mail-values-not-ok-content");
	public static String SHIZOFRENIJA_EQWS_PATIENT_MAIL_VALUES_NOK_SUBJECT = returnValue("shizofrenija-eqws-patient-mail-values-not-ok-subject");	
	public static String SHIZOFRENIJA_EQWS_PATIENT_TASK_VALUES_NOK = returnValue("shizofrenija-eqws-patient-task-values-not-not-ok");
	public static String SHIZOFRENIJA_EQWS_PATIENT_MAIL_VALUES_CRITICAL_CONTENT = returnValue("shizofrenija-eqws-patient-mail-values-critical-content");
	public static String SHIZOFRENIJA_EQWS_PATIENT_MAIL_VALUES_CRITICAL_SUBJECT = returnValue("shizofrenija-eqws-patient-mail-values-critical-subject");
	public static String SHIZOFRENIJA_EQWS_PATIENT_TASK_VALUES_CRITICAL = returnValue("shizofrenija-eqws-patient-task-values-critical");
	
	public static String HUJSANJE_KORAKI_PATIENT_TASK_NAME_NORMAL = returnValue("hujsanje-koraki-patient-task-name-normal");
	public static String HUJSANJE_KORAKI_PATIENT_TASK_NAME_COMPLETE = returnValue("hujsanje-koraki-patient-task-name-complete");
	public static String HUJSANJE_KORAKI_PATIENT_TASK_NAME_INVALID = returnValue("hujsanje-koraki-patient-task-name-invalid");
	
	public static String HUJSANJE_PATIENT_TASK_WEIGHT_NORMAL = returnValue("hujsanje-patient-task-weight-normal");
	public static String HUJSANJE_PATIENT_TASK_WEIGHT_NOT_OK = returnValue("hujsanje-patient-task-weight-not-ok");
	public static String HUJSANJE_PATIENT_TASK_WEIGHT_OPKP_NOK = returnValue("hujsanje-patient-task-weight-opkp-nok");
	public static String HUJSANJE_PATIENT_TASK_WEIGHT_NOT_OK_OPKP_RESULTS = returnValue("hujsanje-patient-task-weight-not-ok-opkp-results");
	public static String HUJSANJE_PATIENT_TASK_WEIGHT_NOT_OK_OPKP_NOK_RESULTS = returnValue("hujsanje-patient-task-weight-not-ok-opkp-nok-results");
	public static String HUJSANJE_PATIENT_MAIL_VALUES_NOK_CONTENT = returnValue("hujsanje-patient-mail-values-not-ok-content");
	public static String HUJSANJE_PATIENT_MAIL_VALUES_NOK_CONTENT_OPKP_RESULTS = returnValue("hujsanje-patient-mail-values-not-ok-content-opkp-results");
	public static String HUJSANJE_PATIENT_MAIL_VALUES_NOK_CONTENT_OPKP_NOK_RESULTS = returnValue("hujsanje-patient-mail-values-not-ok-content-opkp-nok-results");
	public static String HUJSANJE_PATIENT_MAIL_WEIGHT_OK_CONTENT_OPKP_NOK_RESULTS = returnValue("hujsanje-patient-mail-weight-ok-content-opkp-nok-results");
	
	public static String HUJSANJE_PATIENT_MAIL_VALUES_NOK_SUBJECT = returnValue("hujsanje-patient-mail-values-not-ok-subject");
	public static String HUJSANJE_PATIENT_MAIL_VALUES_NOK_SUBJECT_OPKP_RESULTS = returnValue("hujsanje-patient-mail-values-not-ok-subject-opkp-results");
	public static String HUJSANJE_PATIENT_MAIL_WEIGHT_OK_SUBJECT_OPKP_RESULTS = returnValue("hujsanje-patient-mail-weight-ok-subject-opkp-results");
	
	public static String HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO = returnValue("hujsanje-koraki-patient-first-subtask-info");
	public static String HUJSANJE_PATIENT_KORAKI_SECOND_SUBTASK_INFO = returnValue("hujsanje-koraki-patient-second-subtask-info");
	public static String HUJSANJE_PATIENT_KORAKI_THIRD_SUBTASK_INFO = returnValue("hujsanje-koraki-patient-third-subtask-info"); 	
	public static String HUJSANJE_PATIENT_KORAKI_FOURTH_SUBTASK_INFO = returnValue("hujsanje-koraki-patient-fourth-subtask-info");
	public static String HUJSANJE_PATIENT_KORAKI_FIFTH_SUBTASK_INFO = returnValue("hujsanje-koraki-patient-fifth-subtask-info");
	public static String HUJSANJE_PATIENT_KORAKI_SIXTH_SUBTASK_INFO = returnValue("hujsanje-koraki-patient-sixth-subtask-info");
	public static String HUJSANJE_PATIENT_KORAKI_SEVENTH_SUBTASK_INFO = returnValue("hujsanje-koraki-patient-seventh-subtask-info");
	public static String HUJSANJE_PATIENT_KORAKI_WRONG_ANSWER_SUBTASK_INFO = returnValue("hujsanje-koraki-patient-wrong-answer-subtask-info");
	
	public static String HUJSANJE_PATIENT_KORAKI_DOESNT_RESPOND = returnValue("hujsanje-koraki-patient-doesnt-respond");
	public static String HUJSANJE_HIS_WORKING_SHEETS_TASK_NAME = returnValue("hujsanje-his-working-sheets");
	
	
	
	
	private static String returnValue(String _key) {
		if (properties == null) {
			properties = new Properties();
	    	try {
	    		properties.loadFromXML(MessageRepo.class.getResourceAsStream("messages.xml"));	  
			} catch (InvalidPropertiesFormatException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}	
		}
		return properties.getProperty(_key);		
	}

}
