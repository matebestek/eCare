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
package si.pint.ediabetes.inputDiab;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.openehr.validation.ValidationError;

import junit.framework.TestCase;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.VnosPacientaDiabetesArcheSrv;
import si.pint.database.exist.DBManager.SystemType;

public class VnosPacientaDiabTestCase extends TestCase {

	public void testPatientsFirstVisit() {

		VnosPacientaDiabetesArcheSrv service = new VnosPacientaDiabetesArcheSrv();

		try {
			// init template
			assertTrue("Archetype/templates did not initialise correctly!", 
					service.initInput()); 

			Map<ArcheDataPath, Object> userInputsMap = prepareDataMendatory();
			
//			userInputsMap = prepareDataMedicine(null);
//			service.setAndValidataData(userInputsMap);
//			service.saveInput("pat12345");
			boolean ok = service.setAndValidataData(userInputsMap);
			List<ValidationError> ers = service.getErrorList();	
			if(ers != null){
				for(ListIterator<ValidationError> it = ers.listIterator();it.hasNext();){				
					System.out.println("ERROR: " + it.next().getArchetypePath());
				}
			}
			assertTrue("User data should be ok1111!", ok);
			assertTrue("Data did not save correctly into XML!",	service.saveInput(SystemType.EOSKRBATEST,"zdravnik.di","pacient.di"));
			
			userInputsMap = prepareDataAll(userInputsMap);
//			service.setAndValidataData(userInputsMap);
//			service.saveInput("pat12345");
			assertTrue("User data should be ok!", service.setAndValidataData(userInputsMap));
			assertTrue("Data did not save correctly into XML!",	service.saveInput(SystemType.EOSKRBATEST,"zdravnik.di","pacient.di"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private Map<ArcheDataPath, Object> prepareDataMedicine(Map<ArcheDataPath, Object> userInputsMap) {

		if (userInputsMap == null)
			userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
//		//kadilski status
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0002]/value"), "at0015");
		
		//dnevno stevilo cigaret
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0008]/items[at0007]/value"), new Integer("6"));
		
		//datum zacetka uzivanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0008]/items[at0009]/value"), "1990-01-01");
		
		//starost ob zacetku uzivanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0008]/items[at0011]/value"), new Integer("14:00"));
		
//		//poskus prenehanja
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0016]/items[at0017]/value"), "at0019");
//		
//		//datum prenehanja
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0016]/items[at0010]/value"), "2000-01-01");
//		
//		//starost ob prenehanju
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0016]/items[at0012]/value"), new Integer("22"));
//		
//		//stopnja ogrozenosti
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0022]/value"), "at0023");
//		
//		//komentar
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0013]/value"), "kadilski komentar");
		
		// <----- 6. zdravila ------->

//		//ime zdravila
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0002]/value"), "zdravilo XY");
//		
//		//gen. ime
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0003]/value"), "gen. ime XY");
//		
//		//ATC koda
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0028]/value"), "ATC koda 1234XXX");
//		
//		//stevilo pakiranj
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0006]/items[at0018]/value"), new Integer("40"));
//		
//		//zdravila - kolicina (st/mg/ml/vdih)
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0005]/items[at0008]/value"), new Integer("12"));
//		
//		//pogostost jemanja
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0005]/items[at0009]/value"), "at0012");
//
//		//ste danes vzeli vsa zdravila
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0007]/items[at0019]/value"), "FALSE");
//				
//		//kako redno ste v zadnjih 14 dneh jemali zdravila
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0007]/items[at0020]/value"), "at0023");
		
		// <----- 6.1 zdravila (2) ------->
//		//ime zdravila
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0002]/value"), "zdravilo XY2");
//		
//		//gen. ime
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0003]/value"), "gen. ime XY2");
//		
//		//ATC koda
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0028]/value"), "ATC koda 1234XXX2");
//		
//		//stevilo pakiranj
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0006]/items[at0018]/value"), new Integer("42"));
//		
//		//zdravila - kolicina (st/mg/ml/vdih)
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0005]/items[at0008]/value"), new Integer("22"));
//		
//		//pogostost jemanja
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0005]/items[at0009]/value"), "at0013");
//
//		//ste danes vzeli vsa zdravila
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0007]/items[at0019]/value"), "TRUE");
//				
//		//kako redno ste v zadnjih 14 dneh jemali zdravila
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0007]/items[at0020]/value"), "at0024");

		return userInputsMap;
	}	
	


	private Map<ArcheDataPath, Object> prepareDataMendatory() {

		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		// visina
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.height.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), new Integer("185"));
			
		//kako pogosto si oseba meri telesno tezo
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0006]/items[at0011]/value"), "at0025");
		
		//kako pogosto si oseba meri krvni sladkor
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0006]/items[at0007]/value"), "at0018");

		//kako pogosto si oseba meri krvni tlak
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0006]/items[at0015]/value"), "at0033");		

		//diagnoza (MKB10)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.diagnoza_eo.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.diagnoza_diabetes_stanje.v1]/items[at0001]/value"), "at0006");
		
		//starost ob zacetku uzivanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.kajenje_eo_pp.v1]/data[at0001]/items[at0008]/items[at0011]/value"), "P14Y");
		
		//datum začetka diabetesa
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0041]/value"), "1.3.2012");
		
		//tip diabetesa
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0037]/value"), "at0038");
		
		
		return userInputsMap;
	}
	

	private Map<ArcheDataPath, Object> prepareDataAll(Map<ArcheDataPath, Object> userInputsMap) {

		if (userInputsMap == null)
			userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();

		// <----- 1. osnovni podatki ------->
		
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
		
		//izpolnjuje ostale vkljucitvene pogoje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0011]/value"), "TRUE");
		
		//vkljucen v studijo
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0012]/value"), "FALSE");
		
		// <----- 2. druzinska anamneza ------->
		//povzetek
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.druzinska_anamneza_eo_di.v1]/data[at0001]/items[at0002]/value"), "to je povzetek");
		
		//diabetes v druzini
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.druzinska_anamneza_eo_di.v1]/data[at0001]/items[at0003]/value"), "ni prisoten");
		
		// <----- 3. anamneza ------->
		//teza
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value"), "88.5");
		
		//indeks telesne mase
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_mass_index.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), new Integer("9"));

		//alkohol ---------->>>>
		
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
		
		//anamneza ostalo ---------->>>>
		
		//datum začetka diabetesa
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0041]/value"), "1.3.2012");
		
		//tip diabetesa
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0037]/value"), "at0038");
				
		
		//ocesno ozadje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0002]/value"), "ok");
		
		//pregled nog
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0003]/value"), "noge ok");
		
		//zgodovina bolezni
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0005]/value"), "diabetes 4 life");
			
		//uporaba inzulina
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0035]/value"), "TRUE");
		
		//uporaba merilca krvnega sladkorja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.anamneza_ostalo_eo_di.v1]/data[at0001]/items[at0036]/value"), "FALSE");
		
		
		// <----- 3. diagnoza ------->
		
		//diagnoza
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.diagnoza_eo.v1]/data[at0001]/items[at0002]/value"), "diagnoza umor");
		
		//diagnoza (MKB10)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.diagnoza_eo.v1]/data[at0001]/items[openEHR-EHR-CLUSTER.diagnoza_diabetes_stanje.v1]/items[at0001]/value"), "at0006");
				
//		
		// <----- 4. terapija ------->
		
		//opis terapije
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[at0004]/value"), "to je opis terapije");
		
		// <----- 5. krvni tlak ------->
		
		//sistolicni
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.krvni_tlak_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), new Integer("120"));
		
		//diastolicni
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.krvni_tlak_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), new Integer("80"));
		
		//opombe
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.krvni_tlak_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/value"), "opomba za krvni tlak");

		// <----- 6. WONCA ------->
		
		//Katera fizična aktivnost, ki se jo lahko opravljali vsaj dve minuti je bila v zadnjih dveh tednih najtežja?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0006]/value"), "at0018");
		
		//Kako močno so vas v zadnjih dveh tednih pestile čustvene težave, kot so občutek tesnobe, depresije, razdražljivosti, potrtost ali žalost?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0005]/value"), "at0024");
		
		//Koliko težav ste imeli v zadnjih dveh tednih pri svojih običajnih dejavnosti doma in drugje zaradi svojega telesnega in duševnega zdravja?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0008]/value"), "at0030");
		
		//Koliko je v zadnjih dveh tednih vaše telesno in duševno zdravje omejevalo vaše družabne aktivnosti z družino, prijatelji, sosedi ali drugimi skupinami?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0007]/value"), "at0034");

		//Kako bi ocenili svoje trenutno telesno in duševno zdravstveno stanje glede na tisto pred dvemi tedni? 
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0010]/value"), "at0040");

		//Kako bi na splošno ocenili svoje telesno in duševno zdravstveno stanje zadnja dva tedna?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0011]/value"), "at0043");

		//Koliko telesnih bolečin ste imeli v zadnjih štirih tednih oziroma kako izrazite so bile?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0009]/value"), "at0014");

		//stevilo tock
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0047]/value"), new Integer("17"));

		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.wonca_vprasalnik_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0048]/value"), "komentar za wonco");

		
		// <----- 7. laboratorij ------->
		
		//Hb1ac
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.laboratorij_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0015]/value"), new Integer("15"));
		
		//glukoza v krvi na tesce
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.laboratorij_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), new Integer("16"));
		
		//albumin
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.laboratorij_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/value"), new Integer("17"));
		
		//aceton
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.laboratorij_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/value"), new Integer("18"));
		
		//holesterol
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.laboratorij_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/value"), new Integer("19"));
		
		//trigliceridi
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.laboratorij_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0010]/value"), new Integer("20"));
		
		//hdl
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.laboratorij_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0011]/value"), new Integer("21"));
		
		//ldl
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.laboratorij_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0012]/value"), new Integer("22"));
		
		//kreatinin
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.laboratorij_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0013]/value"), new Integer("23"));
		
		//alt
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.laboratorij_eo_di.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0014]/value"), new Integer("24"));
				
		// <----- 8. zdravila ------->

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
		
		// <----- 8.1 zdravila (2) ------->
		//ime zdravila
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0002]/value"), "zdravilo XY2");
		
		//gen. ime
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0003]/value"), "gen. ime XY2");
		
		//ATC koda
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0028]/value"), "ATC koda 1234XXX2");
		
		//stevilo pakiranj
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0006]/items[at0018]/value"), new Integer("42"));
		
		//zdravila - kolicina (st/mg/ml/vdih)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0005]/items[at0008]/value"), new Integer("22"));
		
		//pogostost jemanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0005]/items[at0009]/value"), "at0013");

		//ste danes vzeli vsa zdravila
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0007]/items[at0019]/value"), "TRUE");
				
		//kako redno ste v zadnjih 14 dneh jemali zdravila
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0007]/items[at0020]/value"), "at0024");	

		// <----- 8.2 zdravila (3) ------->
		
		//gen. ime
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0003]/value"), "gen. ime XY3");
		
		//ATC koda
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0028]/value"), "ATC koda 1234XXX_3");
				
		// <----- 8.2 zdravila (4) ------->
		//gen. ime
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0003]/value"), "gen. ime XY4");
		
		//pogostost jemanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-INSTRUCTION.terapija_eo.v1]/activities[at0002]/description[at0003]/items[openEHR-EHR-CLUSTER.zdravila_eo.v1]/items[at0005]/items[at0009]/value"), "at0014");		
		
		return userInputsMap;
	}

}
