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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import junit.framework.TestCase;

import org.openehr.validation.ValidationError;

import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.EnterDiValuesArcheSrc;
import si.pint.database.exist.DBManager.SystemType;

public class EnterDiabetesValuesTestCase extends TestCase {
	

	public void testEnterDiValuesArchetype() {
		
		EnterDiValuesArcheSrc service = new EnterDiValuesArcheSrc();
		
		try {

			Map<ArcheDataPath, Object>  userInputsMap = new HashMap<ArcheDataPath, Object>();
			
			assertTrue("Archetype/templates did not initialise correctly!", service.initInput());
			boolean validateData = service.setAndValidataData(userInputsMap);
			assertTrue("User data was not set correctly!", validateData);
			assertTrue("Data did not save correctly into XML!", service.saveInput(SystemType.EOSKRBATEST,"sestra.di","pacient.di"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	
	public void testEnterDiValuesArchetypeAll() {
		
		EnterDiValuesArcheSrc service = new EnterDiValuesArcheSrc();
		
		try {

			Map<ArcheDataPath, Object>  userInputsMap = new HashMap<ArcheDataPath, Object>();
			
			userInputsMap = fillUserInputs(userInputsMap);
			
			assertTrue("Archetype/templates did not initialise correctly!", service.initInput());
			boolean validateData = service.setAndValidataData(userInputsMap);
			if(!validateData){
				List<ValidationError> vel = service.getErrorList();
				if(vel != null){
					for(ListIterator<ValidationError> li = vel.listIterator(); li.hasNext();){
						ValidationError ve = li.next();
						System.out.println("ERROR: " + ve.getArchetypePath() + " type="+ve.getErrorType().name());
					}
				}
			}
			assertTrue("User data was not set correctly!", validateData);
			assertTrue("Data did not save correctly into XML!", service.saveInput(SystemType.EOSKRBATEST,"sestra.di","pacient.di"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	private Map<ArcheDataPath, Object> fillUserInputs(Map<ArcheDataPath, Object> userInputsMap) {

		
		// --- teza ---
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value"),	new Integer("85"));
		
		// --- krvni tlak ---
		//sistolicni		
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.krvni_tlak_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "80");		
		
		//diastolicni		
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.krvni_tlak_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), "120");
		
		//opomba
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.krvni_tlak_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/value"), "to je opomba");
		
		//--- krvni sladkor ----
		//krvni sladkor
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.krvni_sladkor_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "5");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.krvni_sladkor_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/value"), "02:15");//minute: 15, cel čas: 2:15
		//opomba
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.krvni_sladkor_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), "to je opomba za krvni sladkor");
		
		
		// ---- dietni preksrki ----
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.prehrana_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), "torta");
		
		// ---- prehrana
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.prehrana_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "kruh");
		
//		// Kako pogosto si oseba sama meri telesno težo
//		userInputsMap
//				.put(new ArcheDataPath(
//						"/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0006]/items[at0011]/value"),
//						"at0027");
//		// Kako pogosto si oseba sama meri krvni sladkor
//		userInputsMap
//				.put(new ArcheDataPath(
//						"/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0006]/items[at0007]/value"),
//						"at0020");
//		// Kako pogosto si oseba sama meri krvni tlak/</p></td>
//		userInputsMap
//				.put(new ArcheDataPath(
//						"/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0006]/items[at0015]/value"),
//						"at0032");		
		
		// <------ WONCA vprašalnik------>
		userInputsMap
				// čustva
				.put(new ArcheDataPath(
						"/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0005]/value"),
						"at0025");
		userInputsMap
				// Telesno počutje
				.put(new ArcheDataPath(
						"/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0006]/value"),
						"at0018");

		userInputsMap
				// Družabno življenje
				.put(new ArcheDataPath(
						"/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0007]/value"),
						"at0036");

		userInputsMap
				// Vsakodenevna opravila
				.put(new ArcheDataPath(
						"/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0008]/value"),
						"at0030");

		userInputsMap
				// Bolečina
				.put(new ArcheDataPath(
						"/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0009]/value"),
						"at0016");

		userInputsMap
				// sprememba zdravja
				.put(new ArcheDataPath(
						"/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0010]/value"),
						"at0038");
		userInputsMap
				// Splošno stanje
				.put(new ArcheDataPath(
						"/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0011]/value"),
						"at0045");
		// število točk
		userInputsMap
				.put(new ArcheDataPath(
						"/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0047]/value"),
						"15");
		// komentar
		userInputsMap
				.put(new ArcheDataPath(
						"/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0048]/value"),
						"tole je komentar za wonca vprašalanik");
		
		//terapija
		//ali ste danes vzeli vsa predpisana zdravila
		userInputsMap
		.put(new ArcheDataPath(
				"/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0007]/items[at0019]/value"),
				"TRUE");
		
		
		//telesna dejavnost
		
		//sportna panoga
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0020]/items[at0004]/value"), "at0053");
		
		//intenzivnost vadbe
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0020]/items[at0012]/value"), "at0014");
		
		//trajanje vadbe
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0020]/items[at0006]/value"), "00:20");
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0020]/items[at0018]/value"), "komentar dif");
		
		//Kolikokrat na teden ste bili v zadnjih 14 dnevih gibalno aktivni (30 min zmerne ali visoke intenzivnosti)?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_dejavnost_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0023]/value"), "at0027");

		return userInputsMap;
	}
}
