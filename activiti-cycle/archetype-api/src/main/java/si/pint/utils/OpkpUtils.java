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
package si.pint.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import lombok.extern.log4j.Log4j;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.model.User;

@Log4j
public class OpkpUtils {
	public static String prepareOPKPUrlData(User patient){
		String data = getJSON(patient);
		SecureCodec codec = new SecureCodec();
		String signedData = codec.encode(data);
		String urlData = "";
		try {
			urlData = URLEncoder.encode(signedData,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		return urlData;
	}
	
	public static String getJSON(User patient){
		//vzamem najvišjo vrednost med vsemi različnimi vrednostmi - ponavadi bo itak vedno ista vrednost TODO: popravi da bo vzel zadnjo meritev
		String query = "max(collection(\"/db/eHujsanje/" + patient.getUsername() + "\")//*[name()=\"content\" and @archetype_node_id=\"openEHR-EHR-SECTION.vkljucitev_eo.v1\"]/*[name()=\"items\" and @archetype_node_id=\"openEHR-EHR-OBSERVATION.telesna_sestava.v1\"]/*[name()=\"data\" and @archetype_node_id=\"at0001\"]/*[name()=\"events\" and @archetype_node_id=\"at0002\"]/*[name()= \"data\" and @archetype_node_id=\"at0003\"]/*[name()=\"items\" and @archetype_node_id=\"at0007\"]/*[name() = \"value\"]/*[name()=\"magnitude\"]/text())";//bazalni metabolizem
		String bm = null;
		try {
			bm = (String)DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).getSingleValue(patient.getUsername(),patient.getUsername(),EMPLOYEE_TYPE_ENUM.DIABETES,query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Napaka pri pridobivanju bazalnega metabolizma za pacienta:"+patient.getUsername() +": " + e.getMessage());			
		}
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateOfBirthText = sdf.format(patient.getDateOfBirth());
				
		String json = 
		"{\"ID\":\"" + patient.getUsername()+ "\"," +
		"\"EMAIL\":\" "+ patient.getEmail() +" \"," +
		"\"FIRST_NAME\":\" " + patient.getFirstNameLastName() + "\"," +
		"\"LAST_NAME\":\" " + patient.getFirstNameLastName() + "\"," +
		"\"SEX\":\"" +patient.getSex().toString() + "\"," +
		"\"BDATE\":\"" + dateOfBirthText + "\"," +
		"\"HEIGHT\":\"" +patient.getHeight() + "\"," +
		"\"WEIGHT\":\"" + patient.getWeight() + "\"," +
		"\"BM\":\"" + bm + "\"" +
		"}";
		
		return json;
	}
}
