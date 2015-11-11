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
package si.pint.eastma.inputPEF;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.ACTArcheSrv;
import si.pint.archetypes.service.EnterPEFArcheSrv;
import si.pint.database.exist.DBManager.SystemType;

public class PulmonaryTemplateTestCase extends TestCase {

	public void testEnterPEFArche() {
		EnterPEFArcheSrv service = new EnterPEFArcheSrv();
		try {

			Map<ArcheDataPath, Object>  userInputsMap = new HashMap<ArcheDataPath, Object>();
						
			//PEF:
			userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0057]/items[at0058]/value"), new Integer("998"));
			
			assertTrue("Archetype/templates did not initialise correctly!", service.initInput());
			boolean validateData = service.setAndValidataData(userInputsMap);
			assertTrue("User data was not set correctly!", validateData);
			assertTrue("Data did not save correctly into XML!", service.saveInput(SystemType.EOSKRBATEST,"ivan.astma","ivan.astma"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void testACTArche() {
		ACTArcheSrv service = new ACTArcheSrv();
		try {
			Map<ArcheDataPath, Object> userInputsMap = new HashMap<ArcheDataPath, Object>();
						
			//ACT:			
			userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0034]/value"), "at0042");
			userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0035]/value"), "at0046");
			userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0036]/value"), "at0051");
			userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0037]/value"), "at0055");
			userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0038]/value"), "at0060");

			int vsotaACT = 15;
			//urejenost astme
			userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0073]/value"),"at00" + (73+vsotaACT));//at0073 dejansko ne obstaja, ke pa max at0098
			
//			//stevilo tock
			userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0066]/value"), vsotaACT);
//			
//			//komentar
			userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0064]/value"), "komentar je to");
			
		
			assertTrue("Archetype/templates did not initialise correctly!", service.initInput());
			assertTrue("User data was not set correctly!", service.setAndValidataData(userInputsMap));		
			assertTrue("Data did not save correctly into XML!", service.saveInput(SystemType.EOSKRBATEST,"ivan.astma","ivan.astma"));			
//			Date date= new Date(1309891297750l);
//			String temp = DBManager.read("pat12346","openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1",date,"//items/archetype_details/archetype_id/value/text()");
			
			//if(temp!=null)
				//System.out.println("Zahtevana vrednost: " + temp);
			
//			String[][] result = DBManager.readTimespan("pat12346","openEHR-EHR-OBSERVATION.pulmonary_function.v1",new Date(Long.MIN_VALUE), new Date(Long.MAX_VALUE),"//items[@archetype_node_id='at0058']/value/magnitude/text()");
			//if(result!=null)
				//for(int i=0; i<result.length;i++)
					//System.out.println("vrednost: " + result[i]);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

}
