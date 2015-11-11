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
package si.pint.ehujsanje;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.PhysicalActivityQuesArcheSrv;
import si.pint.database.exist.DBManager.SystemType;

public class PhysicalActivityQuesTestCase extends TestCase {

	public void testEnterDiValuesArchetype() {
		
		PhysicalActivityQuesArcheSrv service = new PhysicalActivityQuesArcheSrv();
		
		try {
			
			Map<ArcheDataPath, Object>  userInputsMap = prepareDataAll();
			
			assertTrue("Archetype/templates did not initialise correctly!", service.initInput());
			boolean validateData = service.setAndValidataData(userInputsMap);
			assertTrue("User data was not set correctly!", validateData);
			assertTrue("Data did not save correctly into XML!", service.saveInput(SystemType.EOSKRBATEST,"pacient.hu","pacient.hu"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	private Map<ArcheDataPath, Object> prepareDataAll() {

		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		//Katere tri stvari lahko počnem vsak dan, da bom povečal svojo telesno dejavnost
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[at0004]/value"), "test1");
		
		//Kaj zame predstavljajo glavne ovire za redno telesno dejavnost
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[at0005]/value"), "test2");
		
		//Katere so tri vrste dejavnosti, pri katerih dovolj uživam, da bi jih lahko počel redno
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[at0006]/value"), "test3");
		return userInputsMap;
	}
}
