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
package si.pint.esport;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.EnterSpValuesArcheSrv;
import si.pint.database.exist.DBManager.SystemType;

public class EnterSpValuesTestCase extends TestCase {
	
	public void testOpomnik() {

		EnterSpValuesArcheSrv service = new EnterSpValuesArcheSrv();

		try {
			// init template
			assertTrue("Archetype/templates did not initialise correctly!", service.initInput()); 

			Map<ArcheDataPath, Object> userInputsMap = prepareDataAll();
			assertTrue("User data should be ok!", service.setAndValidataData(userInputsMap));
			assertTrue("Data did not save correctly into XML!",	service.saveInput(SystemType.EOSKRBATEST,"bobo@gugu.com","bobo@gugu.com"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	private Map<ArcheDataPath, Object> prepareDataAll() {

		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();

		// <----- 1. osnovni podatki ------->
		
		// Teža
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value"), "130");
		// Komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0024]/value"), "blb bla");
		// Stanje oblačil
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/state[at0008]/items[at0009]/value"), "at0011");
		// Drugi dejavniki
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/state[at0008]/items[at0025]/value"), "dddddd");
		// Datum vadbe
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0019]/value"), "12.05.2012");
		// Športna panoga
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "at0022");
		// Trajanje vadbe (format HH:MM)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/value"), "02:12");
		// Razdalja (v m)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0055]/value"), "600");
		// Porabljena energija (v kcal)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0057]/value"), "800");
		// Intenzivnost vadbe
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.intenzivnost_opisno_5st_eo.v1]/items[at0001]/value"), "at0002");
		// Počutje med vadbo
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.opis_pocutja.v1]/items[at0001]/value"), "at0002");
		// Vadba opravljena
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0021]/value"), "true");
		// Opombe
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0018]/value"), "Ni kaj za komentirat");

		
		return userInputsMap;
	}

}
