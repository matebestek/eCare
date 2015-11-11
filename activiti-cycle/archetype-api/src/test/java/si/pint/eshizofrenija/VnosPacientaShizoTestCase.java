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
package si.pint.eshizofrenija;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.VnosPacientaShizofrenijaArcheSrv;
import si.pint.database.exist.DBManager.SystemType;

public class VnosPacientaShizoTestCase extends TestCase {

	public void testPatientsFirstVisit() {

		VnosPacientaShizofrenijaArcheSrv service = new VnosPacientaShizofrenijaArcheSrv();

		try {
			// init template
			assertTrue("Archetype/templates did not initialise correctly!", 
					service.initInput()); 

			Map<ArcheDataPath, Object> userInputsMap = prepareDataMendatory();
			
			assertTrue("User data should be ok!", service.setAndValidataData(userInputsMap));
			assertTrue("Data did not save correctly into XML!",	service.saveInput(SystemType.EOSKRBATEST,"pacient.sh","pacient.sh"));
			
			userInputsMap = prepareDataAll(userInputsMap);
			assertTrue("User data should be ok!", service.setAndValidataData(userInputsMap));
			assertTrue("Data did not save correctly into XML!",	service.saveInput(SystemType.EOSKRBATEST,"pacient.sh","pacient.sh"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	


	private Map<ArcheDataPath, Object> prepareDataMendatory() {

		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		// visina
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.height.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), new Integer("185"));
			

		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> prepareDataAll(Map<ArcheDataPath, Object> userInputsMap) {

		if (userInputsMap == null)
			userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		//ime
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0002]/value"), "Janko");
		
		//priimek
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0003]/value"), "Novak");
		
		//bis
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0004]/value"), "bid.janko.novak");
		
		//spol
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0005]/value"), "at0013"); //moski
		
		//datum rojstva
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0006]/value"), "01.07.1980");
		
		//email
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0007]/value"), "EMAIL@DOMAIN.COM");
		
		//GSM
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0008]/value"), "PHONE-NUMBER");
		
		//zdravstvena ustanova
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0009]/value"), "ZD Novo Mesto");
		
		//poklic
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0010]/value"), "tesar");
		
		//teza
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value"), new Integer("80"));
		
		//indeks telesne mase
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_mass_index.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "8.5");
		
		//laboratorij - eritrociti
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.laboratorij_eo_sh.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0004]/value"), "30.5");
		
		//pogostost pitja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.alkohol_eo_pp.v1]/data[at0001]/items[at0002]/value"), "at0006");
		
		//kolicina pijace
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.alkohol_eo_pp.v1]/data[at0001]/items[at0008]/value"), "at0012");
		
		//Pogostost pitja vecjih kolicin
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.alkohol_eo_pp.v1]/data[at0001]/items[at0014]/value"), "at0016");
		
		//stevilo dosezenih tock
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.alkohol_eo_pp.v1]/data[at0001]/items[at0020]/value"), new Integer("9"));
		
		//ocena ogrozenosti
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.alkohol_eo_pp.v1]/data[at0001]/items[at0022]/value"), "at0024");
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.alkohol_eo_pp.v1]/data[at0001]/items[at0021]/value"), "komentar za alko");
		
		//kajenje ---------->>>>
		
		//kadilski status
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0002]/value"), "at0015");
		
		//dnevno stevilo cigaret
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0008]/items[at0007]/value"), new Integer("6"));
		
		//datum zacetka uzivanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0008]/items[at0009]/value"), "1990-01-01");
		
		//starost ob zacetku uzivanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0008]/items[at0011]/value"), new Integer("14"));
		
		//poskus prenehanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0016]/items[at0017]/value"), "at0019");
		
		//datum prenehanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0016]/items[at0010]/value"), "2000-01-01");
		
		//starost ob prenehanju
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0016]/items[at0012]/value"), new Integer("22"));
		
		//stopnja ogrozenosti
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0022]/value"), "at0023");
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0013]/value"), "kadilski komentar");
		
		//telesna dejavnost ---------->>>>
		
		//visoko intenzivna vadba
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.telesna_dejavnost_eo_pp.v1]/data[at0001]/items[at0002]/value"), "at0005");
		
		//zmerno intenzivna vadba
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.telesna_dejavnost_eo_pp.v1]/data[at0001]/items[at0012]/value"), "at0015");
		
		//stevilo dosezenih tock
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.telesna_dejavnost_eo_pp.v1]/data[at0001]/items[at0022]/value"), new Integer("8"));
		
		//ogrozenost
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.telesna_dejavnost_eo_pp.v1]/data[at0001]/items[at0024]/value"), "at0026");
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.telesna_dejavnost_eo_pp.v1]/data[at0001]/items[at0023]/value"), "komentar za telesno dejavnost");

		//diagnoza
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.diagnoza_eo.v1]/data[at0001]/items[at0002]/value"), "diagnoza umor");
		
		//diagnoza (MKB10)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.diagnoza_eo.v1]/data[at0001]/items[at0003]/value"), "diagnoza MKB10");
		
		// <----- 4. terapija ------->
		
		//opis terapije
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[at0004]/value"), "to je opis terapije");

		//ime zdravila
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0002]/value"), "zdravilo XY");
		
		//gen. ime
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0003]/value"), "gen. ime XY");
		
		//ATC koda
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0028]/value"), "ATC koda 1234XXX");
		
		//stevilo pakiranj
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0006]/items[at0018]/value"), new Integer("40"));
		
		//zdravila - kolicina (st/mg/ml/vdih)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0005]/items[at0008]/value"), new Integer("12"));
		
		//pogostost jemanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0005]/items[at0009]/value"), "at0012");

		//ste danes vzeli vsa zdravila
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0007]/items[at0019]/value"), "FALSE");
				
		//kako redno ste v zadnjih 14 dneh jemali zdravila
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0007]/items[at0020]/value"), "at0023");		
		
		return userInputsMap;
	}
}
