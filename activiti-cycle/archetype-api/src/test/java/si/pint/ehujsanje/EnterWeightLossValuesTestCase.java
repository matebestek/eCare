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
import si.pint.archetypes.service.EnterWeightLossValusArcheSrv;
import si.pint.database.exist.DBManager.SystemType;

public class EnterWeightLossValuesTestCase extends TestCase {

	public void testEnterDiValuesArchetype() {
		
		EnterWeightLossValusArcheSrv service = new EnterWeightLossValusArcheSrv();
		
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

		// <----- 3. teza ------->
		//teza
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value"), "88.5");
		
		//indeks telesne mase
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_mass_index.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), new Integer("9"));

		//obseg pasu
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.obseg_pasu_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), new Integer("110"));
		
		//<<<----telesna dejavnost---->>>>>>
		
		//datum vadbe
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0019]/value"), "2011-02-07");
		//sportna panoga
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "at0035");
		//intenzivnost vadbe
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.intenzivnost_opisno_3st_eo.v1]/items[at0001]/value"), "at0003");
		//trajanje vadbe
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/value"), "00:20");
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0018]/value"), "komentar dif");
		
		return userInputsMap;
	}
	
}
