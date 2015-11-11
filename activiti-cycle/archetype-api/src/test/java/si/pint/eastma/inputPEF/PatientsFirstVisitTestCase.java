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

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.VnosPacientaArcheSrv;
import si.pint.database.exist.DBManager.SystemType;

public class PatientsFirstVisitTestCase extends TestCase {

	public void testPatientsFirstVisit() {
		
		VnosPacientaArcheSrv service = new VnosPacientaArcheSrv(); 
		
		try{
			//init template
			assertTrue("Archetype/templates did not initialise correctly!", service.initInput());  
			
			//test with only mendatory fields
			assertTrue("User data should be ok!", service.setAndValidataData(prepareDataMendatory()));
			assertTrue("Data did not save correctly into XML!", service.saveInput(SystemType.EOSKRBATEST, "zdravnik.as","pacient.as"));
			
			//test with missing mendatory fields
			assertFalse("User data should not be ok!", service.setAndValidataData(prepareDataMisiing()));
			
			//test with all (mendatory & optional) fields
			assertTrue("User data should be ok!", service.setAndValidataData(prepareDataAll()));
			assertTrue("Data did not save correctly into XML!", service.saveInput(SystemType.EOSKRBATEST,"zdravnik.as","pacient.as"));			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private Map<ArcheDataPath, Object> prepareDataMendatory() {
		
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		//ime
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0004]/value"), "Janko");
		//priimek
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0075]/value"), "Novak");
		//bid
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0011]/value"), "bid.janko.novak");
		//spol
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0076]/value"), "at0077");
		//datum rojstva
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0080]/value"), "01.06.1980");
		//email
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0081]/value"), "EMAIL@DOMAIN.COM");
		//GSM
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0082]/value"), "054845754");
		//zdravstvena ustanova
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0083]/value"), "KC-A02");
		//vlazno stanovanje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0086]/items[at0087]/value"), "at0088");
		//vsakodnevno delo v hlevu
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0086]/items[at0090]/value"), "at0092");
		
		// <------ ACT ----->
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0034]/value"), "at0041");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0035]/value"), "at0045");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0036]/value"), "at0052");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0037]/value"), "at0054");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0038]/value"), "at0062");
		//stevilo tock
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0066]/value"), new Integer("18"));
		//kriterij urejenosti
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0073]/value"), "at0087");
		
		// <------ telesna masa ----->
		//teza
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_weight.v1 and name/value='Telesna masa']/data[at0002]/events[at0003]/data[at0001]/items[at0004 and name/value='Teža']/value"), new Integer("85"));
		
		// <------ telesna visina ----->
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Telesna višina']/data[at0001]/events[at0002]/data[at0003]/items[at0004 and name/value='Višina']/value"), new Integer("191"));
		
		// <------ pljucne funkcije ----->
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.pljucne_funkcije_eo_as.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), new Integer("740"));
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.pljucne_funkcije_eo_as.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), new Integer("5"));
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.pljucne_funkcije_eo_as.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/value"), new Integer("5"));
		
		//vkljucitveni kriteriji
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0094]/value"), "FALSE");
		//vkljucen v studijo - rabimo, da kasneje aplikacija med izvajanjem deluje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0095]/value"), "FALSE");
		
		
		return userInputsMap;
	}
	
	
	private Map<ArcheDataPath, Object> prepareDataAll() {
		
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		//<----- osnovni podatki ------->
		
		//ime
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0004]/value"), "Janko");
		//priimek
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0075]/value"), "Novak");
		//bid
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0011]/value"), "bid.janko.novak");
		//spol
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0076]/value"), "at0077");
		//datum rojstva
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0080]/value"), "01.06.1980");
		//email
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0081]/value"), "EMAIL@DOMAIN.COM");
		//GSM
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0082]/value"), "054845754");
		//zdravstvena ustanova
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0083]/value"), "KC-A02");
		//vlazno stanovanje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0086]/items[at0087]/value"), "at0088");
		//vsakodnevno delo v hlevu
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0086]/items[at0090]/value"), "at0092");
		//vkljucitveni kriteriji
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0094]/value"), "FALSE");
		//vkljucen v studijo - rabimo, da kasneje aplikacija med izvajanjem deluje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0095]/value"), "FALSE");	
		
		// <------ druzinska anamneza ----->
		
		//astma v druzini
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.druzinska_anamneza_eo_as.v1]/data[at0001]/items[at0032]/value"), "Ne");
		//alergije v druzini
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.druzinska_anamneza_eo_as.v1]/data[at0001]/items[at0033]/value"), "Da");
		
		// <------ alkohol ----->
		
		//pogostost pitja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.alkohol_eo_pp.v1]/data[at0001]/items[at0002]/value"), "at0004");
		//kolicina pijace ob obicajnem pitju
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.alkohol_eo_pp.v1]/data[at0001]/items[at0008]/value"), "at0012");
		//pogostost pitja vecjih kolicin
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.alkohol_eo_pp.v1]/data[at0001]/items[at0014]/value"), "at0019");
		//stevilo dosezenih tock
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.alkohol_eo_pp.v1]/data[at0001]/items[at0020]/value"), new Integer("7"));
		
		// <------ kajenje ----->
		
		//kadilski status
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0002]/value"), "at0003");
		//dnevno stevilo cigaret
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0008]/items[at0007]/value"), new Integer("38"));
		//datum zacetka uzivanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0008]/items[at0009]/value"), "1950-08-04");
		//starost ob zacetku uzivanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0008]/items[at0011]/value"), new Integer("14"));
		//poskus prenehanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0016]/items[at0017]/value"), "at0018");
		//datum prenehanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0016]/items[at0010]/value"), "1951-09-03");
		//starost ob prenehanju
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0016]/items[at0012]/value"), new Integer("3"));
		
		// <------ telesna dejavnost ----->
		
		//visoko intenzivna vadba
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.telesna_dejavnost_eo_pp.v1]/data[at0001]/items[at0002]/value"), "at0006");
		//zmerno intenzivna vadba
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.telesna_dejavnost_eo_pp.v1]/data[at0001]/items[at0012]/value"), "at0017");
		//stevilo dosezenih tock
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.telesna_dejavnost_eo_pp.v1]/data[at0001]/items[at0022]/value"), new Integer("8"));
		
		// <------ telesna masa ----->
		//teza
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_weight.v1 and name/value='Telesna masa']/data[at0002]/events[at0003]/data[at0001]/items[at0004 and name/value='Teža']/value"), new Integer("85"));
		
		// <------ telesna visina ----->
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.height.v1 and name/value='Telesna višina']/data[at0001]/events[at0002]/data[at0003]/items[at0004 and name/value='Višina']/value"), new Integer("191"));
		
		// <------ anamneza ostalo ----->
		
		//domace zivali
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_as.v1]/data[at0001]/items[at0014]/value"), "FALSE");
		
		//alergije
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_as.v1]/data[at0001]/items[at0004]/value"), "at0007");			
		
		// <------ ACT ----->
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0034]/value"), "at0041");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0035]/value"), "at0045");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0036]/value"), "at0052");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0037]/value"), "at0054");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0038]/value"), "at0060");
		//stevilo tock
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0066]/value"), new Integer("18"));
		//kriterij urejenosti
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0073]/value"), "at0087");		
		
		// <------ pljucne funkcije ----->
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.pljucne_funkcije_eo_as.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), new Integer("740"));
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.pljucne_funkcije_eo_as.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), new Integer("5"));
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.pljucne_funkcije_eo_as.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/value"), new Integer("5"));
		
		// <------ terapija ----->
		
		//diagnoza
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0002]/value"), "Pacient je bolan");

		//opis terapije
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0137]/value"), "Terapija: pacient naj jemlje cimvec tablet!");

		//ime zdravila
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0138]/items[at0439]/value"), "at0441");
		
		//genericno ime
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0138]/items[at0037]/value"), "at0042");
		
		//stevilo pakiranj
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0138]/items[at0129]/value"), new Integer("2"));
		
		//kolicina
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0138]/items[at0130]/items[at0479]/value"), new Integer("2"));
		
		//pogostost jemanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0138]/items[at0130]/items[at0480]/value"), "at0482");
		
		//ime zdravila 2
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0142]/items[at0223]/value"), "at0232");
		
		//genericno ime 2
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0142]/items[at0145]/value"), "at0262");
		
		//stevilo pakiranj 2
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0142]/items[at0146]/value"), new Integer("3"));
		
		//kolicina 2 
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0142]/items[at0147]/items[at0486]/value"), new Integer("2"));
		
		//pogostost jemanja 2
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0142]/items[at0147]/items[at0487]/value"), "at0492");	
		
		//ime zdravila 3
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0143]/items[at0149]/value"), "at0203");
		
		//genericno ime 3
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0143]/items[at0148]/value"), "at0352");
		
		//stevilo pakiranj 3
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0143]/items[at0150]/value"), new Integer("4"));
		
		//kolicina 3
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0143]/items[at0151]/items[at0488]/value"), new Integer("4"));
		
		//pogostost jemanja 3
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0143]/items[at0151]/items[at0489]/value"), "at0498");
		
		//druga zdravila
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0478]/value"), "tudi ostalo");
		
		return userInputsMap;
	}
	
	
	private Map<ArcheDataPath, Object> prepareDataMisiing() {
		
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();

		//ime
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0004]/value"), "Janko");
		//priimek
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0075]/value"), "Novak");
		//bid
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0011]/value"), "bid.janko.novak");
		//spol
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0076]/value"), "at0077");
		//datum rojstva
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0080]/value"), "01.06.1980");
		//email
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0081]/value"), "EMAIL@DOMAIN.COM");
		//GSM
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0082]/value"), "054845754");
		//zdravstvena ustanova
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0083]/value"), "KC-A02");
		//vlazno stanovanje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0086]/items[at0087]/value"), "at0088");
		//vsakodnevno delo v hlevu
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1]/data[at0003]/items[at0086]/items[at0090]/value"), "at0092");
		
		// <------ ACT ----->
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0034]/value"), "at0041");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0035]/value"), "at0045");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0036]/value"), "at0052");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0065]/items[at0038]/value"), "at0060");
		//stevilo tock
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0066]/value"), new Integer("18"));
		//kriterij urejenosti
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0073]/value"), "at0087");			
		
		// <------ terapija ----->
		
		//diagnoza
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0002]/value"), "Pacient je bolan");
		
		//ime zdravila 2
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0142]/items[at0223]/value"), "at0232");
		
		//genericno ime 2
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0142]/items[at0145]/value"), "at0262");
		
		//stevilo pakiranj 2
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0142]/items[at0146]/value"), new Integer("3"));
				
		//opis terapije
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.terapija.v1]/data[at0001]/items[at0137]/value"), "Terapija: pacient naj jemlje cimvec tablet!");
		
		return userInputsMap;
	}	
	
	
}
