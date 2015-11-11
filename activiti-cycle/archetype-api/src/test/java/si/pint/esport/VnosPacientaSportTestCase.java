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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import junit.framework.TestCase;

import org.openehr.validation.ValidationError;

import si.pint.archetypes.model.ArcheDataPath;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.archetypes.service.VnosPacientaSportArcheSrv;

public class VnosPacientaSportTestCase extends TestCase {

	public void testPatientsFirstVisit() {

		VnosPacientaSportArcheSrv service = new VnosPacientaSportArcheSrv();

		try {
			// init template
			assertTrue("Archetype/templates did not initialise correctly!", service.initInput()); 

			Map<ArcheDataPath, Object> userInputsMap = prepareDataMendatory();
			
//			userInputsMap = prepareDataMedicine(null);
//			service.setAndValidataData(userInputsMap);
//			service.saveInput("pat12345");
			boolean ok = true;
			ok = service.setAndValidataData(userInputsMap);
			List<ValidationError> ers = service.getErrorList();	
			if(ers != null){
				for(ListIterator<ValidationError> it = ers.listIterator();it.hasNext();){				
					System.out.println("ERROR: " + it.next().getArchetypePath());
				}
			}
			assertTrue("Data did not save correctly into XML!",	service.saveInput(SystemType.EOSKRBATEST,"sestra.sp","bobo@gugu.com"));

			
			userInputsMap = prepareDataAll(null);
//			service.setAndValidataData(userInputsMap);
//			service.saveInput("pat12345");
			assertTrue("User data should be ok!", service.setAndValidataData(userInputsMap));
			assertTrue("Data did not save correctly into XML!",	service.saveInput(SystemType.EOSKRBATEST,"sestra.sp","bobo@gugu.com"));


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
		
		//Datum vključitve v študijo
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1]/data[at0001]/items[at0016]/value"), "02.06.2012");
		

		// <----- 3. anamneza ------->
		//teza
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value"), "88.5");
		
		// visina
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.height.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), new Integer("185"));		
		
		//indeks telesne mase
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.body_mass_index.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), new Integer("9"));

		
		
		//Antropometrija ---------->>>>
		//Obseg pasu
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.antropometrija.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0007]/value"), new Integer(100));
		
		// Obseg bokov
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.antropometrija.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0008]/value"), new Integer(100));		
		
		//Telesna sestava ---------->>>>
		//-->>> Telesna sestava
		
		//odstotek telesne mascobe
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_sestava.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "6.5");
		
		//trebusna mascoba
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_sestava.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), "6.6");		
		
		//skeletno misicevje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_sestava.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/value"), "6.7");
		
		//poraba metabolizma v mirovanju
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.telesna_sestava.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/value"), "6.8");
			
		
		//dnevna aktivnost ------>>>>>>
		
		//Stopnja dnevne aktivnosti
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.dnevna_aktivnost_eo_hu.v1]/data[at0001]/items[at0003]/value"), "at0005");
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.dnevna_aktivnost_eo_hu.v1]/data[at0001]/items[at0009]/value"), "to je komentar");
		
		
		//Funkcionalna zmogljivost ------>>>>>>
		
		//Datum in ura testa
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/value"), "06.06.2012");
		
		//Interpretacija
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/value"), "to je interpretacija");
			//Funkcionalna zmogljivost - Test vzdrzljivost ------>>>>>>
		
		//Cooperjev test 12 min
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_vzdrzljivost.v1]/items[at0001]/value"), "6000");
		
		//Cooperjev test 2400 m
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_vzdrzljivost.v1]/items[at0002]/value"), "11");
		
			//Funkcionalna zmogljivost - Moč roke ------>>>>>>
		
		//Maksimalna sila stiska zapestja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_moc.v1]/items[at0001]/items[at0006]/value"), "250");
		
			//Funkcionalna zmogljivost - Moč noge ------>>>>>>
		//Maksimalni navor iztegovalk kolena
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_moc.v1]/items[at0002]/items[at0004]/value"), "2000");
		
		//Maksimalni navor upogibalk kolena
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_moc.v1]/items[at0002]/items[at0005]/value"), "2200");
		
		//Povprečna odrivna moč v prvih 50 ms
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_moc.v1]/items[at0002]/items[at0007]/value"), "650");
		
		//Maksimalna višina navpičnega skoka
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_moc.v1]/items[at0002]/items[at0008]/value"), "110");
		
		//Razmerje med impulzom sile v drugi in prvi polovici navpičnega odriva
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_moc.v1]/items[at0002]/items[at0009]/value"), "26");
		
			//Funkcionalna zmogljivost - Test ravnotežje ------>>>>>>
		
		//Dolžina nihanja telesa v mirovanju
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_ravnotezje.v1]/items[at0003]/items[at0004]/value"), "3");
		
		//Trajanje testa na tenziometrijski plošči
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_ravnotezje.v1]/items[at0003]/items[at0002]/value"), "20:20");
		
			//Funkcionalna zmogljivost - Test gibljivost ------>>>>>>
		
		//Počep s palico nad glavo
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_gibljivost.v1]/items[at0052]/items[at0002]/value"), "at0006");
		
		//Korak čez oviro
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_gibljivost.v1]/items[at0052]/items[at0007]/value"), "at0006");
		
		//Izpadni korak s palico za hrbtom
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_gibljivost.v1]/items[at0052]/items[at0016]/value"), "at0006");
		
		//Upogib kolka leže
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_gibljivost.v1]/items[at0052]/items[at0017]/value"), "at0006");
		
		//Mobilnost ramen
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_gibljivost.v1]/items[at0052]/items[at0018]/value"), "at0006");
		
		//Skleci
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_gibljivost.v1]/items[at0052]/items[at0019]/value"), "at0006");
		
		//Klek z dotikom in komolca kolen
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_gibljivost.v1]/items[at0052]/items[at0020]/value"), "at0006");
		
		//Indeks funkcionalnosti gibanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_gibljivost.v1]/items[at0052]/items[at0051]/value"), "20");
		
		//Bolečinski test za rame
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_gibljivost.v1]/items[at0053]/items[at0021]/value"), "at0006");
		
		//Bolečinski test za zgornji del hrbta
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_gibljivost.v1]/items[at0053]/items[at0054]/value"), "at0057");
		
		//Bolečinski test za spodnji del hrbta
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.funkcionalna_zmogljivost.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.test_gibljivost.v1]/items[at0053]/items[at0055]/value"), "at0059");
		
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
		
		//stopnja ogrozenosti
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.sport_program_vadbe.v1]/data[at0001]/items[at0002]/value"), "at0003");
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.sport_program_vadbe.v1]/data[at0001]/items[at0005]/value"), "at0006");
		
		return userInputsMap;
	}

}
