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
package si.pint.eoskrba.ehujsanje;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;
import lombok.extern.log4j.Log4j;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.archetypes.exceptions.LdapException;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.OpkpReturnArcheSrv;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.ehujsanje.inputKoraki.subtask.SequenceFlowListenerGateway;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;
import si.pint.utils.SecureCodec;

@Log4j
public class OpkpTestCase extends TestCase {
	
	//template name
	public static final String FIRST_ENCOUNTER_TEMPLATE = "openEHR-EHR-SECTION.vkljucitev_eo.v1";
	
	// path to weight & height in eXist XML
	public static final String BODY_WEIGHT_EXIST_PATH = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";

	// height path in eXist
	public static final String BODY_HEIGHT_EXIST_PATH = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.height.v1']/data[@archetype_node_id='at0001']/events[@archetype_node_id='at0002']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0004']/value/magnitude/text()";

	public void testSignature() {
		User pacient = null;
		try {
			pacient = UserRegistryFactory.getUserFromLDAP("pacient.hu",
					Role.PATIENT);
			//dobim še višino in težo
			try {
				String s = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast("pacient.hu", "pacient.hu", FIRST_ENCOUNTER_TEMPLATE, BODY_WEIGHT_EXIST_PATH);
				pacient.setWeight(new BigDecimal(s));
				
				s = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).readLast("pacient.hu", "pacient.hu", FIRST_ENCOUNTER_TEMPLATE, BODY_HEIGHT_EXIST_PATH);
				pacient.setHeight(new BigDecimal(s));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (LdapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String data = SequenceFlowListenerGateway.getJSON(pacient);
		SecureCodec codec = new SecureCodec();
		String signedData = codec.encode(data);
		String urlData = "";
		try {
			urlData = URLEncoder.encode(signedData, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String url = "http://opkp.si/sl_SI/ivz?data=" + urlData;
		assertNotNull(urlData);
		System.out.println("ŠIFRIRAN:"+signedData);
		//še dešifriram
		String s = codec.decode(signedData);
		assertNotNull(s);
		assertEquals(data.trim(), s.trim());
		JSONObject j = new JSONObject(s.trim());
		assertNotNull(j);
		System.out.println("URL:"+url);
	}
	
	public void testDecode(){
		String data = null;
		try {
			data = new String("eyJlbmVyZ3kiOjM2NzM2MTQuMTUwMDk3OSwiYWN0aXZpdHlfZW5lcmd5Ijo4MDY5MS40Mjg1NzE0MjksInJlY29tbWVuZGVkX2VuZXJneSI6NzM3NTcwNC42Mjg1NzE0LCJ1bml0cyI6eyIxIjp7InZhbCI6NC42Mzc4NzE2NDU4MjkzLCJndWlkZSI6OS43OTM1MzE3NDYwMzE3LCJuYW1lIjoiXHUwMTYwa3JvYm5hIFx1MDE3ZWl2aWxhIn0sIjIiOnsidmFsIjowLjM1NDE2ODAzNzUwMzg5LCJndWlkZSI6Mi40NDgzODI5MzY1MDc5LCJuYW1lIjoiTWxla28gaW4gbWxlXHUwMTBkbmkgaXpkZWxraSJ9LCIzIjp7InZhbCI6MC4xODczODQzNDE2ODIxMywiZ3VpZGUiOjIuNDQ4MzgyOTM2NTA3OSwibmFtZSI6Ik1lc28gaW4gemFtZW5qYXZlIn0sIjQiOnsidmFsIjowLjgwNTExNDI4NTcxMTYsImd1aWRlIjowLjQ4OTY3NjU4NzMwMTU5LCJuYW1lIjoiU3Ryb1x1MDEwZG5pY2UifSwiNSI6eyJ2YWwiOjEuNzM5NzUxNDg0MzQyOSwiZ3VpZGUiOjMuOTE3NDEyNjk4NDEyNywibmFtZSI6IlplbGVuamF2YSJ9LCI2Ijp7InZhbCI6My4wNTUyMjgzOTgxNTc4LCJndWlkZSI6Mi45MzgwNTk1MjM4MDk1LCJuYW1lIjoiU2FkamUifSwiNyI6eyJ2YWwiOjIuNzY3MTQ4NjkzNTg1NiwiZ3VpZGUiOjEuNDY5MDI5NzYxOTA0OCwibmFtZSI6Ik1hXHUwMTYxXHUwMTBkb2JlIGluIG1hXHUwMTYxXHUwMTBkb2JuYSBcdTAxN2VpdmlsYSJ9LCI4Ijp7InZhbCI6MC45MjM3ODkzOTk1NjQyMSwiZ3VpZGUiOjguODE0MTc4NTcxNDI4NiwibmFtZSI6IlNsYWRrb3IgaW4gc2xhZGthIFx1MDE3ZWl2aWxhIn19LCJ3YXJuaW5ncyI6W3sidHlwZSI6ImNvbXBvbmVudCIsImNvbXBvbmVudCI6IkVORVJBIiwidG9vX2xvdyI6dHJ1ZSwicmVzdWx0IjoicHJlbml6ZWsgZW5lcmdpanNraSB2bm9zIiwiZGVzYyI6Ik9wYVx1MDE3ZWFtbyBuZXphZG9zdGVuIHZub3MgaHJhbmUgaW4gcyB0ZW0gZW5lcmdpamUsIGthciBsYWhrbyBwcmVkc3RhdmxqYSB0dmVnYW5qZSB6YSB2YVx1MDE2MWUgemRyYXZqZSBpbiB1c3BlXHUwMTYxbm9zdCBodWpcdTAxNjFhbmphLiAiLCJ1cmwiOiIyIn0seyJ0eXBlIjoidW5pdCIsInVuaXQiOjIsInRvb19sb3ciOnRydWUsInJlc3VsdCI6InByZW1hbG8gbWxla2EgaW4gbWxlXHUwMTBkbmloIGl6ZGVsa292IHRlciB6YW1lbmphdiIsImRlc2MiOiJVZ290YXZsamFtbywgZGEgdVx1MDE3ZWl2YXRlIHByZW1hbG8gbWxla2EgaW4gbWxlXHUwMTBkbmloIGl6ZGVsa292LiJ9LHsidHlwZSI6InNwZWNpYWwiLCJzcGVjaWFsIjoiZmlzaCIsInRvb19sb3ciOnRydWUsInJlc3VsdCI6InByZW1hbG8gcmliIiwiZGVzYyI6IlVnb3RhdmxqYW1vLCBkYSBqZSB2IHZhXHUwMTYxaSBwcmVocmFuaSBwcmVtYWxvIHJpYi4gWiB1XHUwMTdlaXZhbmplbSByaWIgem1hbmpcdTAxNjFhbW8gdHZlZ2FuamUgemEgbmFzdGFuZWsgcmFrYSBkZWJlbGVnYSBcdTAxMGRyZXZlc2EuIFByaXBvcm9cdTAxMGRhbW8gdmFtLCBkYSBlbmtyYXQgZG8gZHZha3JhdCBuYSB0ZWRlbiB1XHUwMTdlaXZhdGUga3ZhbGl0ZXRuZSBtYXN0bmUgbW9yc2tlIHJpYmUgKHR1bmEsIHNrdVx1MDE2MWEsIGxvc29zXHUyMDI2KS4gTW9yc2tlIHJpYmUgdnNlYnVqZWpvIHZlXHUwMTBkIGVzZW5jaWFsbmloIG1hXHUwMTYxXHUwMTBkb2JuaWgga2lzbGluIChvbWVnYSAzKSwgdml0YW1pbmEgQSBpbiBEIHRlciBqb2RhIGtvdCBzbGFka292b2RuZSByaWJlLiJ9LHsidHlwZSI6InVuaXQiLCJ1bml0Ijo1LCJ0b29fbG93Ijp0cnVlLCJyZXN1bHQiOiJwcmVtYWxvIHplbGVuamF2ZSIsImRlc2MiOiJPYnJva29tIGRvZGFqdGUgMjAwIGcgVkVcdTAxMGMgc3ZlXHUwMTdlZSBhbGkga3VoYW5lIHNlem9uc2tlIHplbGVuamF2ZSwgcG8gbW9cdTAxN2Vub3N0aSB6IHZhXHUwMTYxZWdhIHZydGEgYWxpIGxva2FsbmUgb3NrcmJlLiBMSU5LIiwidXJsIjoiNCJ9LHsidHlwZSI6InVuaXQiLCJ1bml0IjoxLCJ0b29fbG93Ijp0cnVlLCJyZXN1bHQiOiJwcmVtYWxvIE9IIFx1MDE3ZWl2aWwiLCJkZXNjIjoiVGEgdGVkZW4gc3RlIHphdVx1MDE3ZWlsaSBkZXZldCBlbm90IFx1MDE2MWtyb2JuaWggXHUwMTdlaXZpbC4gU3ZldHVqZW1vLCBkYSBkb2RhdGUgdiBkbmV2bmkgamVkaWxuaWsgXHUwMTYxZSB0cmkgZW5vdGUgKG5wci4ga29zIGtydWhhLCBcdTAxN2VsaWNhIG92c2VuaWgga29zbWlcdTAxMGRldiwgc2tvZGVsaWNhIHJpXHUwMTdlYSkuIExJTksgbmEgdVx1MDEwZG5vIHBvZ2xhdmplIG8gXHUwMTYxa3JvYm5paCBcdTAxN2VpdmlsaWghIn1dLCJiYXNlX2VuZXJneSI6Njg3MDEyOH0AAAAAAAAAAAAAAAAAAAAAAAAAAAAA".getBytes("UTF-8"), "UTF-8");
		} catch (Exception e) {
			
		}
		byte[] result = Base64.decodeBase64(data);
		String tmp = new String(result);
//		SecureCodec sc = new SecureCodec();
//		String decoded = sc.decode(tmp);
		System.out.println("DATA:"+tmp);
	}
	
	public void testResponseJSON() {
		
		String data = 
				"{"+
						"	\"energy\":\"1200\","+
						"	\"activity_energy\":\"1000\","+
						"	\"units\":{"+
						"				\"koda enote 1\":{"+
						"								val:\"12\","+
						"								guide:\"priporočilo\","+
						"								name:\"ime enote\""+
						"							 },"+
						"				\"koda enote 2\":{"+
						"								val:\"33\","+
						"								guide:\"priporočilo 2\","+
						"								name:\"ime enote 2\""+
						"							 }"+
						"			 },"+
						"	\"warnings\":{"+
						"		\"result\":\"kratek opis\","+
						"		\"desc\":\"daljši opis\","+
						"		\"high\":{"+
						"					\"items\":\"array živil iz opkp\","+
						"					\"freq\":\"pogostost\","+
						"					\"weight\":\"tipična količina\""+
						"			   }"+
						"		"+
						"	}"+
						""+
						"}";		
				
		 JSONObject j = new JSONObject(data);
		 data = j.toString();
		 System.out.println("SOURCE:" + data);
				
				
		SecureCodec codec = new SecureCodec();
		String signedData = codec.encode(data);
		System.out.println("ŠIFRIRAN:"+signedData);
		//še dešifriram
		codec = new SecureCodec();
		String s = codec.decode(signedData);
		System.out.println("DEŠIFRIRAN:"+s);
		assertNotNull(s);
		assertEquals(data.trim(), s.trim());
//		j = new JSONObject(s.trim());
//		assertNotNull(j);
	}
	
	public void testArchetypeSave() {
		String data = null;
		try {
			data = new String("eyJlbmVyZ3kiOjAsImFjdGl2aXR5X2VuZXJneSI6MCwicmVjb21tZW5kZWRfZW5lcmd5Ijo3MTU5OTk1LjUyLCJ1bml0cyI6eyIxIjp7InZhbCI6MCwiZ3VpZGUiOjkuNTA3MTExMTExMTExMSwibmFtZSI6Ilx1MDE2MGtyb2JuYSBcdTAxN2VpdmlsYSJ9LCIyIjp7InZhbCI6MCwiZ3VpZGUiOjIuMzc2Nzc3Nzc3Nzc3OCwibmFtZSI6Ik1sZWtvIGluIG1sZVx1MDEwZG5pIGl6ZGVsa2kifSwiMyI6eyJ2YWwiOjAsImd1aWRlIjoyLjM3Njc3Nzc3Nzc3NzgsIm5hbWUiOiJNZXNvIGluIHphbWVuamF2ZSJ9LCI0Ijp7InZhbCI6MCwiZ3VpZGUiOjAuNDc1MzU1NTU1NTU1NTYsIm5hbWUiOiJTdHJvXHUwMTBkbmljZSJ9LCI1Ijp7InZhbCI6MCwiZ3VpZGUiOjMuODAyODQ0NDQ0NDQ0NCwibmFtZSI6IlplbGVuamF2YSJ9LCI2Ijp7InZhbCI6MCwiZ3VpZGUiOjIuODUyMTMzMzMzMzMzMywibmFtZSI6IlNhZGplIn0sIjciOnsidmFsIjowLCJndWlkZSI6MS40MjYwNjY2NjY2NjY3LCJuYW1lIjoiTWFcdTAxNjFcdTAxMGRvYmUgaW4gbWFcdTAxNjFcdTAxMGRvYm5hIFx1MDE3ZWl2aWxhIn0sIjgiOnsidmFsIjowLCJndWlkZSI6OC41NTY0LCJuYW1lIjoiU2xhZGtvciBpbiBzbGFka2EgXHUwMTdlaXZpbGEifX0sIndhcm5pbmdzIjpbeyJ0eXBlIjoidW5pdCIsInVuaXQiOjIsInRvb19sb3ciOnRydWUsInJlc3VsdCI6InByZW1hbG8gbWxla2EgaW4gbWxlXHUwMTBkbmloIGl6ZGVsa292IHRlciB6YW1lbmphdiIsImRlc2MiOiJVZ290YXZsamFtbywgZGEgdVx1MDE3ZWl2YXRlIHByZW1hbG8gbWxla2EgaW4gbWxlXHUwMTBkbmloIGl6ZGVsa292LiJ9LHsidHlwZSI6InNwZWNpYWwiLCJzcGVjaWFsIjoiZmlzaCIsInRvb19sb3ciOnRydWUsInJlc3VsdCI6InByZW1hbG8gcmliIiwiZGVzYyI6IlVnb3RhdmxqYW1vLCBkYSBqZSB2IHZhXHUwMTYxaSBwcmVocmFuaSBwcmVtYWxvIHJpYi4gWiB1XHUwMTdlaXZhbmplbSByaWIgem1hbmpcdTAxNjFhbW8gdHZlZ2FuamUgemEgbmFzdGFuZWsgcmFrYSBkZWJlbGVnYSBcdTAxMGRyZXZlc2EuIFByaXBvcm9cdTAxMGRhbW8gdmFtLCBkYSBlbmtyYXQgZG8gZHZha3JhdCBuYSB0ZWRlbiB1XHUwMTdlaXZhdGUga3ZhbGl0ZXRuZSBtYXN0bmUgbW9yc2tlIHJpYmUgKHR1bmEsIHNrdVx1MDE2MWEsIGxvc29zXHUyMDI2KS4gTW9yc2tlIHJpYmUgdnNlYnVqZWpvIHZlXHUwMTBkIGVzZW5jaWFsbmloIG1hXHUwMTYxXHUwMTBkb2JuaWgga2lzbGluIChvbWVnYSAzKSwgdml0YW1pbmEgQSBpbiBEIHRlciBqb2RhIGtvdCBzbGFka292b2RuZSByaWJlLiJ9LHsidHlwZSI6InVuaXQiLCJ1bml0Ijo1LCJ0b29fbG93Ijp0cnVlLCJyZXN1bHQiOiJwcmVtYWxvIHplbGVuamF2ZSIsImRlc2MiOiJPYnJva29tIGRvZGFqdGUgMjAwIGcgVkVcdTAxMGMgc3ZlXHUwMTdlZSBhbGkga3VoYW5lIHNlem9uc2tlIHplbGVuamF2ZSwgcG8gbW9cdTAxN2Vub3N0aSB6IHZhXHUwMTYxZWdhIHZydGEgYWxpIGxva2FsbmUgb3NrcmJlLiBMSU5LIn0seyJ0eXBlIjoidW5pdCIsInVuaXQiOjYsInRvb19sb3ciOnRydWUsInJlc3VsdCI6InByZW1hbG8gc2FkamEiLCJkZXNjIjoiUHJlZGxhZ2FtbywgZGEgc2kgdGEgdGVkZW4gcHJpdm9cdTAxNjFcdTAxMGRpdGUgMTAwIGcgVkVcdTAxMGMgIHN2ZVx1MDE3ZWVnYSBzZXpvbnNrZWdhIHNhZGphLiBMSU5LXHJcblxyXG5TYWRqZSBpbiB6ZWxlbmphdmEgaW1hdGEgdmVsaWsgcG9tZW4gdiB6ZHJhdmkgcHJlaHJhbmksIHNhaiBzdGEgYmlvbG9cdTAxNjFrbyB2aXNva292cmVkbmkgc2t1cGluaSBcdTAxN2VpdmlsLCBpbiBzaWNlciB6IG5pemtvIGVuZXJnaWpza28gZ29zdG90by4gU2FkamUgaW4gemVsZW5qYXZhIHZzZWJ1amV0YSB2ZWxpa28gdml0YW1pbm92LCBtaW5lcmFsb3YsIGFudGlva3NpZGFudG92LCBwcmVocmFuc2tlIHZsYWtuaW5lIGluIHZlbGlrbyBkcnVnaWggemFcdTAxNjFcdTAxMGRpdG5paCBzbm92aS4gXHJcblxyXG5aYXJhZGkgdnNlYm5vc3RpIHBvc2FtZXpuaWggemFcdTAxNjFcdTAxMGRpdG5paCBzbm92aSwga2kgc28gdiB0aXBpXHUwMTBkbmkgYmFydmkgc2FkamEgaW4gemVsZW5qYXZlLCBwcmlwb3JvXHUwMTBkYW1vIHVcdTAxN2VpdmFuamUgcmF6bGlcdTAxMGRuaWggYmFydiBzYWRqYSBpbiB6ZWxlbmphdmUgKHplbGVuZSwgYmVsZSwgb3Jhblx1MDE3ZW5vLXJ1bWVuZSwgcmRlXHUwMTBkZSBpbiBtb2Ryby12aWpvbGlcdTAxMGRuZSkuIFYgZG5ldm5vIHByZWhyYW5vIG1vcmFtbyB2a2xqdVx1MDEwZGl0aSBvZCAxNTAgZG8gMjUwIGdyYW1vdiBzYWRqYSBpbiBvZCAyNTAgZG8gNDAwIGdyYW1vdiB6ZWxlbmphdmUsIHRqLiBvZHZpc25vIG9kIGRuZXZuaWggZW5lcmdpanNraWggcG90cmViLiBcclxuXHJcblByaXBvcm9cdTAxMGRsaml2byBqZSwgZGEgdmVcdTAxMGRqaSBkZWwgdGVoIFx1MDE3ZWl2aWwgemF1XHUwMTdlaWplbW8ga290IHN2ZVx1MDE3ZW8sIHByZXNubyBocmFubywgZHJ1Z2kgZGVsIHBhIHYga3VoYW5pIG9ibGlraS4ifSx7InR5cGUiOiJ1bml0IiwidW5pdCI6MSwidG9vX2xvdyI6dHJ1ZSwicmVzdWx0IjoicHJlbWFsbyBPSCBcdTAxN2VpdmlsIiwiZGVzYyI6IlRhIHRlZGVuIHN0ZSB6YXVcdTAxN2VpbGkgZGV2ZXQgZW5vdCBcdTAxNjFrcm9ibmloIFx1MDE3ZWl2aWwuIFN2ZXR1amVtbywgZGEgZG9kYXRlIHYgZG5ldm5pIGplZGlsbmlrIFx1MDE2MWUgdHJpIGVub3RlIChucHIuIGtvcyBrcnVoYSwgXHUwMTdlbGljYSBvdnNlbmloIGtvc21pXHUwMTBkZXYsIHNrb2RlbGljYSByaVx1MDE3ZWEpLiBMSU5LIG5hIHVcdTAxMGRubyBwb2dsYXZqZSBvIFx1MDE2MWtyb2JuaWggXHUwMTdlaXZpbGloISJ9LHsidHlwZSI6InVuaXQiLCJ1bml0Ijo0LCJ0b29fbG93Ijp0cnVlLCJyZXN1bHQiOiJwcmVtYWxvIHN0cm9cdTAxMGRuaWMiLCJkZXNjIjoiVE9ETzogb3BpcyJ9XSwiYmFzZV9lbmVyZ3kiOjY2MzQxNTAuNH0AAAAAAAAAAAAAAAAA".getBytes("UTF-8"), "UTF-8");
		} catch (Exception e) {
			
		}
		byte[] result = Base64.decodeBase64(data);
		String tmp = new String(result);
		
		JSONObject json = new JSONObject(tmp);
		
//		def json = JSON.parse(tmp);

		Map<ArcheDataPath, Object> m = new LinkedHashMap<ArcheDataPath, Object>();
		if(json.has("energy")){
			m.put(new ArcheDataPath("/data[at0001]/items[at0003]/value"), json.get("energy").toString());
		}
		if(json.has("activity_energy")){
			m.put(new ArcheDataPath("/data[at0001]/items[at0004]/value"), json.get("activity_energy").toString());
		}
		if(json.has("recommended_energy")){
			m.put(new ArcheDataPath("/data[at0001]/items[at0018]/value"), json.get("recommended_energy").toString());
		}
		
		JSONObject units = (JSONObject) json.get("units");
		String[] ja = JSONObject.getNames(units);
		Iterator it = units.keys();
		
			
			//for (unit in json.units) {
		    while (it.hasNext()) {
		    	String unit = (String) it.next();
		    	JSONObject jo = (JSONObject) units.get(unit);
		    	
		    	Integer o = (Integer) jo.get("val");
		    	
				//def val = unit.value;
				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0001]/value"), unit);  //koda
				
				if(jo.has("name")){
					m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0002]/value"),  jo.get("name"));  //ime
				}
				if(jo.has("val")){
					m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0003]/value"),o.toString());  //zaužite enote
				}
				if(jo.has("guide")){
					Double oG = (Double) jo.get("guide");
					DecimalFormat twoDForm = new DecimalFormat("#,#");
					
					m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0004]/value"), Double.valueOf(twoDForm.format(oG.doubleValue())).toString());  //priporočilo
				}
			}
		    
		    JSONArray warnings = (JSONArray) json.get("warnings");
		    for (int i = 0; i < warnings.length(); i++) {
//			for(w in json.warnings){
//				  //rezultat
		    	JSONObject w = warnings.getJSONObject(i);
				if(w.has("result")){
					m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0004]/value"),w.get("result"));
				}
				  //opis
				if(w.has("desc")){
					m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0005]/value"),w.get("desc"));
				}
				  //high:
				if(w.has("high")){
					JSONObject high = (JSONObject) w.get("high");
//					def high = w.high;
					  //items
					if(high.has("items")){
						m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0001]/value"),high.get("items"));
					}
					  //freq
					if(high.has("freq")){
						m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0002]/value"),high.get("freq"));
					}
					  //weight
					if(high.has("weight")){
						m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0003]/value"),high.get("weight"));
					}
				}
			}
		    String newString = "";
			try {
				newString = new String(tmp.trim().getBytes(), "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    System.out.println(tmp);
	  m.put(new ArcheDataPath("/data[at0001]/items[at0019]/value"), newString);
	  m.put(new ArcheDataPath("/data[at0001]/items[at0020]/value"), "304506");
		try {
		OpkpReturnArcheSrv service = new OpkpReturnArcheSrv();
		service.initInput();
		boolean validateData = service.setAndValidataData(m);
		if(!validateData){
			//System.out0.println("not validated");
//			doLogInfo(log, "OpkpSave NAPAKA json=" +tmp ,"OPKP","OPKP","","","H");
//			def errors = service.getErrorList();
//			if(errors != null){
//				for(e in errors){
//					  doLogInfo(log,"NAPAKA: " + e.getMessage(), "OPKP","OPKP","","","H");
//				}
//			}
		} else {
		//System.out0.println(session.user);
			//doLogInfo(log, "OpkpSave","OPKP","OPKP","json","","H");
			
			service.saveInput(SystemType.EOSKRBAWEBAPP, "sestra.hu","EMAIL@DOMAIN.COM");

		}
		}
		catch (Exception e) {
//			doLogInfo(log, e.getMessage(),"OPKP","OPKP","json",json.toString(),"H");
		}		
	}
	
	public void testGetXmlTaskId() {
		try {
			Object result = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).getSingleValue(
					"EMAIL@DOMAIN.COM", 
					"EMAIL@DOMAIN.COM", 
					EMPLOYEE_TYPE_ENUM.HUJSANJE, 
					"let $d := collection('/db/eHujsanje/EMAIL@DOMAIN.COM')//*[name()=\"items\" and @archetype_node_id=\"openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1\"]/*[name()=\"data\" and @archetype_node_id=\"at0001\"]/*[name()=\"items\" and @archetype_node_id=\"at0020\"]/*[name() = \"value\"]/*[name()=\"value\"]/text()[contains(., \"7373\")]" + 
					"let $foonode := $d/../../../../*[name()=\"items\" and @archetype_node_id=\"at0019\"]/*[name() = \"value\"]/*[name()=\"value\"]/text()" + 
					"return $foonode ");
			System.out.println("blaz1");
			System.out.println(result);
			System.out.println("blaz2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
