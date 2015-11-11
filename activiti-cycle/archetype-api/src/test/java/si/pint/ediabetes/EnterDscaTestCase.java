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
package si.pint.ediabetes;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.DscaArcheSrv;
import si.pint.database.exist.DBManager.SystemType;

public class EnterDscaTestCase extends TestCase {
	
	public void testEnterDscaQuestionnaire() {
		DscaArcheSrv service = new DscaArcheSrv();
		
		try {

			Map<ArcheDataPath, Object>  userInputsMap = new HashMap<ArcheDataPath, Object>();
			
			userInputsMap = fillQuestionnaire(userInputsMap);
			
			assertTrue("Archetype/templates did not initialise correctly!", service.initInput());
			boolean validateData = service.setAndValidataData(userInputsMap);
			assertTrue("User data was not set correctly!", validateData);
			assertTrue("Data did not save correctly into XML!", service.saveInput(SystemType.EOSKRBATEST,"ivan.diabetes","ivan.diabetes"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private Map<ArcheDataPath, Object> fillQuestionnaire(Map<ArcheDataPath, Object> userInputsMap) {
		
		//Kolikokrat v zadnjih 7 dneh ste upoštevali priporočeno dieto?
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0014]/value"), "at0017");
		
		//Kolikokrat v zadnjih 7 dneh ste omejili vnos kalorij, kot je priporočeno v zdravi prehrani za sladkorne bolnike?
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0023]/value"), "at0018");
		
		//Kolikokrat v zadnjih 7 dneh ste jedli sadje in zelenjavo (vsaj 5 enot)?
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0032]/value"), "at0015");
		
		//Kolikokrat v zadnjih 7 dneh ste uživali hrano bogato z maščobami, kot je rdeče meso ipd.?
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0041]/value"), "at0016");
		
		//Kolikokrat v zadnjih 7 dneh ste opravljali aktivnost zmerne intenzivnosti vsaj 30 minut?
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0050]/value"), "at0066");
		
		//Kolikokrat v zadnjih 7 dneh ste opravljali aktivnost visoke intenzivnosti vsaj 30 minut?
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0051]/value"), "at0101");
		
		//Kolikokrat v zadnjih 7 dneh ste kontrolirali nivo vašega krvnega slakorja?
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[at0052]/value"), "at0110");
		
		//Kolikokrat na teden je priporočilo merjenja krvnega sladkorja s strani vašega zdravnika?
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[at0053]/value"), "at0111");
		
		//Kolikokrat v zadnjih 7 dneh ste kontrolirali  telesno težo?
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[at0054]/value"), "at0096");
		
		//Kolikokrat v zadnjih 7 dneh ste kontrolirali krvni pritisk?
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[at0055]/value"), "at0105");		
		
		//Kolikokrat v zadnjih 7 dneh ste natančno pregledali svoja stopala?
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[at0056]/value"), "at0090");
		
		//Kolikokrat v zadnjih 7 dneh ste vzeli predpisano število tablet za kontrolo sladkorne bolezni? 
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0008]/items[at0057]/value"), "at0091");
		
		//Kolikokrat v zadnjih 7 dneh ste prejeli predpisane inzulinske injekcije? (samo za bolnike z inzulinom!)
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0008]/items[at0058]/value"), "at0100");
		
		//Ali ste v zadnjih 7 dneh kadili, četudi minimalno?
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[at0059]/value"), "FALSE");
		
		//Če da, kolikšno je bilo v zadnjih 7 dnevih povprečje pokajenih cigaret (na dan)?
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[at0060]/value"), "6");
		
		//Komentar
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0133]/value"), "to je komentar");
		
		return userInputsMap;
	}
}
