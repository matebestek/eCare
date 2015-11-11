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
package si.pint.eoskrba.eastma.vnosPacienta;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.pvm.runtime.ExecutionImpl;
import org.apache.commons.lang.math.RandomUtils;

import si.pint.archetypes.service.VnosPacientaArcheSrv;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.vnosPacienta.delegate.SelectClinicalTrialGroupDelegate;
import edu.emory.mathcs.backport.java.util.Arrays;

@Log4j
public class DelegateTest extends TestCase {

	protected DelegateExecution execution;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		execution = new ExecutionImpl();
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Exceptions:
	 * 	LDAP:
	 * 		1.si.pint.archetypes.exceptions.LdapException: [LDAP: error code 20 - member: value #7 provided more than once]-->iz grupe je treba zbrisat userja, če si ga že zbrisal iz ldap-a. V grupi pa je
	 * 																														  predvsem zaradi activiti, ki uporablja grupe.
	 * */
	
	public void testSelectClinicalTrialGroupDelegate() {
		VnosPacientaArcheSrv as = new VnosPacientaArcheSrv();
		as.initInput();
		Map m = as.getInputsMap();
		StringBuffer sb = new StringBuffer();
		Object value = null;
		final String usedTypes = "DvOrdinal,DV_TEXT,DV_CODED_TEXT,DV_COUNT,DV_BOOLEAN";
		List keys = Arrays.asList( m.keySet().toArray());
		Collections.sort(keys);
		for(Iterator<String> it = keys.iterator();it.hasNext();){
			value = "";
			String key = it.next();
			String type = (String)m.get(key);
//			log.info(key + " type="+m.get(key));
			
			if(usedTypes.contains(type)){
				if(type.equalsIgnoreCase("DV_COUNT")){				
					value = RandomUtils.nextInt();
				} 
				else if(type.equalsIgnoreCase("DV_TEXT")){
					value = "\"DV_TEXT"+RandomUtils.nextInt()+"\"";
				} 
				else if(type.equalsIgnoreCase("DvOrdinal")){
					value = "\"" + as.geDefaultCode(key) + "\"";
				}
				else if(type.equalsIgnoreCase("DV_CODED_TEXT")){
					value = "\"" + as.geDefaultCode(key) + "\"";
				}
				else if(type.equalsIgnoreCase("DV_BOOLEAN")){
					value = RandomUtils.nextBoolean();
				} 
//				else if(type.equalsIgnoreCase("DV_DURATION")){
//					value = "\"" + as.geDefaultCode(key) + "\"";
//				} 
//				else if(type.equals("DV_DURATION")){
//					value = "\"DV_DURATION"+RandomUtils.nextInt()+"\"";
//				} 
				
				if(!key.endsWith("name")){
				sb.append("execution.setVariable(\"" + key.replace('/','?').replace('_', '!') + "\", " +value + ");\n");
				}
			}
			
			
			
		}
//		log.info(sb.toString());
//		fail();
		
		
		SelectClinicalTrialGroupDelegate d = new SelectClinicalTrialGroupDelegate();
		setUser();
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0004]?value", "DV_TEXT1270757619");
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0011]?value", "DV_TEXT2071933553");
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0075]?value", "DV_TEXT1863978491");
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0076]?value", "at0078");
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0080]?value", "DV_TEXT1692723910");
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0081]?value", "DV_TEXT53466944");
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0082]?value", "DV_TEXT1097241726");
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0083]?value", "DV_TEXT1752096770");
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0084]?value", "DV_TEXT1983229616");
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0086]?items[at0087]?value", "at0089");
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0086]?items[at0090]?value", "at0092");
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0086]?items[at0093]?value", "DV_TEXT850567198");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0003");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0008]?value", "at0009");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0014]?value", "at0015");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0020]?value", 1838244218);
		execution.setVariable("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0021]?value", "DV_TEXT1576319409");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0022]?value", "at0026");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!as.v1]?data[at0001]?items[at0004]?value", "at0011");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!as.v1]?data[at0001]?items[at0012]?value", "DV_TEXT187451803");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!as.v1]?data[at0001]?items[at0014]?value", true);
		execution.setVariable("?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!as.v1]?data[at0001]?items[at0016]?value", "DV_TEXT551477922");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.anamneza!ostalo!eo!as.v1]?data[at0001]?items[at0019]?value", "DV_TEXT492840609");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.druzinska!anamneza!eo!as.v1]?data[at0001]?items[at0002]?value", "DV_TEXT875098798");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.druzinska!anamneza!eo!as.v1]?data[at0001]?items[at0031]?value", "DV_TEXT1493567670");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.druzinska!anamneza!eo!as.v1]?data[at0001]?items[at0032]?value", "DV_TEXT1566184900");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.druzinska!anamneza!eo!as.v1]?data[at0001]?items[at0033]?value", "DV_TEXT1240181278");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0005");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0008]?items[at0007]?value", 1773307655);
		execution.setVariable("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0013]?value", "DV_TEXT1434725238");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0016]?items[at0017]?value", "at0021");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0022]?value", "at0025");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0011");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0012]?value", "at0021");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0022]?value", 1097831181);
		execution.setVariable("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0023]?value", "DV_TEXT2019378572");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0024]?value", "at0027");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.terapija.v1]?data[at0001]?items[at0002]?value", "DV_TEXT1196163290");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.terapija.v1]?data[at0001]?items[at0003]?value", "at0036");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.terapija.v1]?data[at0001]?items[at0037]?value", "at0128");
		execution.setVariable("?items[openEHR-EHR-EVALUATION.terapija.v1]?data[at0001]?items[at0129]?value", 982189610);
		execution.setVariable("?items[openEHR-EHR-EVALUATION.terapija.v1]?data[at0001]?items[at0130]?items[at0134]?value", 350404096);
		execution.setVariable("?items[openEHR-EHR-EVALUATION.terapija.v1]?data[at0001]?items[at0130]?items[at0135]?value", 2021505909);
		execution.setVariable("?items[openEHR-EHR-EVALUATION.terapija.v1]?data[at0001]?items[at0137]?value", "DV_TEXT1924071091");
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0064]?value", "DV_TEXT27755168");
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0034]?value", "at0039");
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0035]?value", "at0044");
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0036]?value", "at0049");
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0037]?value", "at0054");
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0038]?value", "at0059");
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0066]?value", 165139331);
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0073]?value", "at0074");
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.body!weight.v1 and name?value='Telesna masa']?data[at0002]?events[at0003]?data[at0001]?items[at0024]?value", "DV_TEXT473447639");
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.body!weight.v1 and name?value='Telesna masa']?data[at0002]?events[at0003]?state[at0008]?items[at0009]?value", "at0017");
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.body!weight.v1 and name?value='Telesna masa']?data[at0002]?events[at0003]?state[at0008]?items[at0025]?value", "DV_TEXT669313958");
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.height.v1 and name?value='Telesna višina']?data[at0001]?events[at0002]?data[at0003]?items[at0018]?value", "DV_TEXT838795976");
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.height.v1 and name?value='Telesna višina']?data[at0001]?events[at0002]?state[at0013]?items[at0014]?value", "at0020");
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.height.v1 and name?value='Telesna višina']?data[at0001]?events[at0002]?state[at0013]?items[at0019]?value", "DV_TEXT721508705");

		
//		execution.setVariable("?items[openEHR-EHR-OBSERVATION.body!weight.v1 and name?value='Telesna masa']?data[at0002]?events[at0003]?data[at0001]?items[at0004 and name?value='Teža']?value", 100);
//		execution.setVariable("?items[openEHR-EHR-OBSERVATION.height.v1 and name?value='Višina?Dolžina']?data[at0001]?events[at0002]?data[at0003]?items[at0004 and name?value='Višina?Dolžina']?value", 186);
//		//uporaba tobaka
//		execution.setVariable("?items[openEHR-EHR-EVALUATION.substance!use!summary-tobacco.v1]?data[at0001]?items[at0002]?value", "at0016");//Never used
//		execution.setVariable("?items[openEHR-EHR-EVALUATION.substance!use!summary-tobacco.v1]?data[at0001]?items[at0007]?items[at0008]?value", "at0.21");//Pipe
//
//		//ACT vprašalnik							   
//		execution.setVariable("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0034]?value", "at0039");
//		execution.setVariable("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0035]?value", "at0045");
//		execution.setVariable("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0036]?value", "at0051");
//		execution.setVariable("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0037]?value", "at0057");
//		execution.setVariable("?items[openEHR-EHR-OBSERVATION.asthma!control!test!questionary.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0065]?items[at0038]?value", "at0063");
		
		try {
			d.execute(execution);
			assertFalse("Incorrect user data!", (Boolean) execution.getVariable(Constants.VAR_notSatisfyingConditions));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		
	}
	
	
	
	private void setUser() {
		
		//set patient's doctor from session (eOskrba-webApp) 
		execution.setVariable(Constants.VAR_USERNAME_APP,"miha.zdravnik"); 
		
		execution.setVariable(Constants.VAR_BID, "fdsfdsa111");
		execution.setVariable(Constants.VAR_SEX, "MALE");
		execution.setVariable(Constants.VAR_BIRTH_DATE, "01.01.1921" );
		execution.setVariable(Constants.VAR_eMAIL, "EMAIL@DOMAIN.COM");
		
		execution.setVariable(Constants.VAR_FIRST_NAME_PATH, "Blaž");
		execution.setVariable(Constants.VAR_LAST_NAME_PATH, "Kurent");
		execution.setVariable(Constants.VAR_MOBILE_TEL_NUM, "030555888");
		execution.setVariable(Constants.VAR_HC_INSTITUTION, "KC-0A");		
	}

}
