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
package si.pint.eoskrba.conf;

import java.io.IOException;
import java.util.Properties;

/**
 * Text attributes that link to values in properites file.
 * 
 * @author Blaz
 *
 */
public class SystemConstant {

	
	public static final SystemConstant ACTIVITI_REST_HOST_NAME = new SystemConstant("activitiRestHostName"); 
	public static final SystemConstant ACTIVITI_REST_USERNAME = new SystemConstant("activitiRestUsername");
	public static final SystemConstant ACTIVITI_REST_PASSWORD = new SystemConstant("activitiRestPassword");
	public static final SystemConstant LDAP_HOST_NAME = new SystemConstant("ldapHostName");
	public static final SystemConstant LDAP_USERNAME = new SystemConstant("ldapUsername");
	public static final SystemConstant LDAP_PASSWORD = new SystemConstant("ldapPassword");
	public static final SystemConstant EXIST_HOST_NAME = new SystemConstant("existHostname");
	public static final SystemConstant EXIST_USERNAME = new SystemConstant("existUsername");
	public static final SystemConstant EXIST_PASSWORD = new SystemConstant("existPassword");
	public static final SystemConstant SMS_CERT_FILE_PATH = new SystemConstant("smsCertFilePath");
	public static final SystemConstant SMS_SECRET = new SystemConstant("smsSecret");
	public static final SystemConstant BLOCK_SMS = new SystemConstant("blockSms"); //blocks outgoing sms messages
	public static final SystemConstant EXIST_MEDS_PATH = new SystemConstant("existMedsPath");
	public static final SystemConstant EXIST_MEDS_XSD_NAME_DI = new SystemConstant("existMedsXsdNameDi");
	public static final SystemConstant EXIST_MEDS_XML_NAME_DI = new SystemConstant("existMedsXmlNameDi");
	public static final SystemConstant EXIST_SPORT_CALENDAR_DIR = new SystemConstant("existSportCalendarDir");
	public static final SystemConstant EXIST_SPORT_CALENDAR_XML_NAME = new SystemConstant("existSportCalendarXmlName");
	
	public static final SystemConstant EMAIL_ASTMA = new SystemConstant("emailAstma");
	public static final SystemConstant EMAIL_DIABETES = new SystemConstant("emailDiabetes");
	public static final SystemConstant EMAIL_SHIZOFRENIJA = new SystemConstant("emailShizofrenija");
	public static final SystemConstant EMAIL_HUJSANJE = new SystemConstant("emailHujsanje");
	public static final SystemConstant EMAIL_SPORT = new SystemConstant("emailSport");
	
//	public static final String JKS_FILE_PATH;
	
	private String value;
	
//	static {
//		JKS_FILE_PATH = SystemConstant.class.getResource("truststore.jks").getPath();
//	}
		
	private SystemConstant(String _key) {
		Properties properties = new Properties();
		try {
			properties.load(SystemConstant.class.getResourceAsStream("conf.properties"));
			this.value = properties.getProperty(_key);
		} catch (IOException e) {
			e.printStackTrace();
			this.value = null;
		}
	}

	@Override
	public String toString() {
		return this.value;
	}
		
}
