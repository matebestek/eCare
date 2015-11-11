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

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.ArchetypeService;
import si.pint.archetypes.service.HujsanjeStep01ArcheSrv;
import si.pint.archetypes.service.HujsanjeStep02ArcheSrv;
import si.pint.archetypes.service.HujsanjeStep03ArcheSrv;
import si.pint.archetypes.service.HujsanjeStep04ArcheSrv;
import si.pint.database.exist.DBManager.SystemType;

public class EnterKorakiValuesTestCase extends TestCase {
	
	//class name
	public static final String PACKAGE = "si.pint.archetypes.service";
	public static final String SERVICE_CLASS_NAME_START = "HujsanjeStep";
	public static final String SERVICE_CLASS_NAME_END = "ArcheSrv";	

	public void testEnterValuesKorak1Archetype() {

		HujsanjeStep01ArcheSrv service = new HujsanjeStep01ArcheSrv(); 

		try {

			Map<ArcheDataPath, Object> userInputsMap = prepareDataStep1();

			assertTrue("Archetype/templates did not initialise correctly!",	service.initInput());
			boolean validateData = service.setAndValidataData(userInputsMap);
			assertTrue("User data was not set correctly!", validateData);
			assertTrue("Data did not save correctly into XML!",
					service.saveInput(SystemType.EOSKRBATEST,"pacient.hu","pacient.hu"));

			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void testEnterValuesKorak2Archetype() {

		HujsanjeStep02ArcheSrv service = new HujsanjeStep02ArcheSrv(); 

		try {

			Map<ArcheDataPath, Object> userInputsMap = prepareDataStep2();

			assertTrue("Archetype/templates did not initialise correctly!",	service.initInput());
			boolean validateData = service.setAndValidataData(userInputsMap);
			assertTrue("User data was not set correctly!", validateData);
			assertTrue("Data did not save correctly into XML!",service.saveInput(SystemType.EOSKRBATEST,"pacient.hu","pacient.hu"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public void testEnterValuesKorak3Archetype() {

		HujsanjeStep03ArcheSrv service = new HujsanjeStep03ArcheSrv(); 

		try {
			
			Map<ArcheDataPath, Object> userInputsMap = prepareDataStep3();

			assertTrue("Archetype/templates did not initialise correctly!",	service.initInput());
			boolean validateData = service.setAndValidataData(userInputsMap);
			assertTrue("User data was not set correctly!", validateData);
			assertTrue("Data did not save correctly into XML!", service.saveInput(SystemType.EOSKRBATEST,"pacient.hu","pacient.hu"));
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void testEnterValuesKorak4Archetype() {

		HujsanjeStep04ArcheSrv service = new HujsanjeStep04ArcheSrv(); 

		try {
			
			Map<ArcheDataPath, Object> userInputsMap = prepareDataStep4();

			assertTrue("Archetype/templates did not initialise correctly!",	service.initInput());
			boolean validateData = service.setAndValidataData(userInputsMap);
			assertTrue("User data was not set correctly!", validateData);
			assertTrue("Data did not save correctly into XML!",	service.saveInput(SystemType.EOSKRBATEST,"pacient.hu","pacient.hu"));
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void testEnterValuesKorakArchetype() {

		//modify number of digits for process step '9' -> '09'
		String processStep = "16"; 
		if (Integer.parseInt(processStep) < 10)
			processStep = "0" + processStep;		
		
		//compose service class name
		String className = PACKAGE + "." + SERVICE_CLASS_NAME_START + processStep + SERVICE_CLASS_NAME_END;
		Class c = null;
		ArchetypeService service = null;
		try {
			c = Class.forName(className);
			service = (ArchetypeService) c.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} 

		try {
			
//			Map<ArcheDataPath, Object> userInputsMap = prepareDataStep4();
			Method method = this.getClass().getDeclaredMethod("prepareDataStep" + processStep);
			Map<ArcheDataPath, Object> userInputsMap = (Map<ArcheDataPath, Object>) method.invoke(this);

			
			
			assertTrue("Archetype/templates did not initialise correctly!",	service.initInput());
			
			Integer anwserCorect = (Integer) service.getValue("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0005]", "test");
			Object test = service.getOntologyValue("openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1", "at0005");
			
			boolean validateData = service.setAndValidataData(userInputsMap);
			assertTrue("User data was not set correctly!", validateData);
			assertTrue("Data did not save correctly into XML!",	service.saveInput(SystemType.EOSKRBATEST,"pacient.hu","pacient.hu"));
			

		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	private Map<ArcheDataPath, Object> prepareDataStep1() {
		
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		//<----vprasalnik HU pot k spremembi----->
		
		//Zakaj želite zmanjšati svojo telesno težo?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pot_k_spremembi.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0005]/value"), "z1");
		
		//Kakšne cilje ste si postavili? Kakšno težo/videz želite doseči?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pot_k_spremembi.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0006]/value"), "z2");
		
		//Kaj (kdo) menite, da vam bo v času hujšanja v oporo in kaj vas lahko ovira?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pot_k_spremembi.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0007]/value"), "z3");
		
		//Kako dolgo že doživljate težave s telesno težo? Kakšna je bila vaša teža v otroštvu?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pot_k_spremembi.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0008]/value"), "z4");
		
		//Kakšne so vaše dosedanje izkušnje s hujšanjem (na kakšen način ste hujšali, kolikokrat, s kakšnim uspehom)?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pot_k_spremembi.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0009]/value"), "z5");
		
		//<----raven telesne dejavnosti----->
		
		//trditev, ki najbolje opisuje vaš odnos do telesne dejavnosti.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_raven_td.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), "at0008");
		
		
		//<---- Vprasalnik TD kontraindikacija zacetni ----->
		
		//Ali vam je zdravnik kdaj rekel, da imate bolno srce ali sladkorno	bolezen?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_kontraindikacije_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0007]/value"), "at0009");
		
		//Ali so vam kdaj namerili previsok krvni tlak?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_kontraindikacije_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0011]/value"), "k1");		
		
		//Ali jemljete kakšna zdravila (npr. za krvni tlak, srce)?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_kontraindikacije_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0013]/value"), "k2");
		
		//Vas kdaj boli v prsih?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_kontraindikacije_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0014]/value"), "k3");
		
		//Ali se vam kdaj vrti, imate omedlevico, izgube zavesti?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_kontraindikacije_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0017]/value"), "k4");
		
		//Ali ste imeli v zadnjem letu kakšno resno obolenje ali bili hospitalizirani?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_kontraindikacije_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0021]/value"), "k5");
		
		//Imate težave s sklepi ali kostmi, ki jih telesna dejavnost poslabša
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_kontraindikacije_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0024]/value"), "k6");		
		
		//Imate sedaj ali ste pred kratkim imeli virusno obolenje, prehlad?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_kontraindikacije_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0023]/value"), "k7");
		
		//Imate astmo, bronhitis, težko sapo pri hoji po ravnem?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_kontraindikacije_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0022]/value"), "k8");
		
		//Ali menite, da obstaja kakšen razlog, da ste za telesno dejavnost manj sposobni (npr. bolečina v hrbtu, nosečnost ali poporodno stanje) ali da potrebujete zdravniški nadzor?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_kontraindikacije_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0020]/value"), "k9");
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_kontraindikacije_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), "komentar1");
		
		//datum
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_kontraindikacije_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/value"), "2012-02-28");
		
		//<---- Znanje ----->
		
		//Ali se strinjate, da redno gibanje koristi vašemu zdravju?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0007]/value"), "at0023");
		
		//Ali poznate razliko med izrazoma telesna dejavnost in telesna vadba?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0008]/value"), "at0025");		
		
		//Ali poznate pomen izraza »aktiven življenjski slog«?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0009]/value"), "at0028");
		
		//Ali je pol ure zmerno hitrega gibanja vsaj 5-krat tedensko dovolj za krepitev zdravja?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0011]/value"), "at0031");
		
		//Ali zmerna telesna dejavnost pomeni takšen telesni napor, pri katerem se ogrejemo in začnemo nekoliko pospešeno dihati?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0012]/value"), "at0034");
		
		//Ali intenzivna telesna dejavnost pomeni takšen telesni napor, pri katerem se zadihamo in spotimo?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0020]/value"), "at0037");
		
		//Ali se moramo pred bolj intenzivno telesno dejavnostjo vedno postopno ogreti in po njej ohladiti?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0021]/value"), "at0041");
		
		//Ali s preizkusom hoje na 2 kilometra ocenjujemo telesno zmogljivost posameznika?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0017]/value"), "at0043");
		
		//Ali veste kakšno gibanje je primerno za vas glede pogostosti, intenzivnosti, trajanja in tipa (vrste) telesne dejavnosti ali telesne vadbe?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0019]/value"), "at0046");
		
		//Ali uravnoteženo vadbo sestavlja: 50% vaj za vzdržljivost, 25% vaj za moč in 25% vaj za gibljivost?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0018]/value"), "at0051");
		
		//Ali redna aerobna vadba varuje srce, ožilje in dihala pred mnogimi obolenji, če jo izvajamo v ustreznem ciljnem območju srčnega utripa?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0016]/value"), "at0052");
		
		//Ali krepimo mišice in kosti, če izvajamo vaje za krepitev mišic 2 do 3-krat tedensko od 20 do 30 minut?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0014]/value"), "at0055");
		
		//Ali veste, kdaj moramo izvajati vaje za raztezanje mišic, s katerimi ohranjamo in izboljšujemo gibljivost mišic in sklepov?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0015]/value"), "at0059");
		
		//Ali poznate previdnostne ukrepe za varno telesno vadbo?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0013]/value"), "at0061");
		
		//Ali veste kateri so znaki pretirane vadbe?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0010]/value"), "at0066");
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), "komentar 1.2");
		
		//datum
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_znanje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/value"), "2012-03-01");
		
		//<---- Vprasanja - prehrana----->
		
		//Kakšno je priporočeno število dnevnih obrokov?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_01.v1]/data[at0001]/items[at0009]/items[at0017]/value"), "at0020");		
		
		//Kateri je najpomembnejši obrok v dnevni prehrani?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_01.v1]/data[at0001]/items[at0009]/items[at0018]/value"), "at0025");
		
		//Kaj so glavni viri soli v naši prehrani s katero presežemo priporočen dnevni vnos soli?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_01.v1]/data[at0001]/items[at0009]/items[at0019]/value"), "at0023");
		
		//<---- Vprasanja - gibanje----->
		
		//Katera tditev drži za dnevnik vodenja telesne dejavnosti?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_01.v1]/data[at0001]/items[at0028]/items[at0029]/value"), "at0043");
		
		//Katera trditev drzi za vaje mimogrede?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_01.v1]/data[at0001]/items[at0028]/items[at0030]/value"), "at0041");
		
		//Kaj pomeni aktivni življenjski slog?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_01.v1]/data[at0001]/items[at0028]/items[at0031]/value"), "at0038");
		
		//Katera trditev NE velja za varno in učinkovito vadbo?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_01.v1]/data[at0001]/items[at0028]/items[at0032]/value"), "at0034");		
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_01.v1]/data[at0001]/items[at0014]/value"), "komentar 1.2");
		
		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> prepareDataStep2() {
		
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		//<----Vprasalnik PR kaj jem eo hu----->
		
		//Datum izpolnjevanja
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "2012-03-01");		
		
		//<----Škrobna živila----->
		
		//žitnih kosmičev za zajtrk (3 jedilne žlice)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0006]/items[at0007]/value"), "1");		
		
		//kruh (½ kosa)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0006]/items[at0008]/value"), "2");		
		
		//toast (1 kos)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0006]/items[at0009]/value"), "3");		
		
		//žemljica (½ kosa)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0006]/items[at0010]/value"), "4");		
		
		//krekerji (3 kosi)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0006]/items[at0011]/value"), "5");		
		
		//krompir (velik kot jajce)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0006]/items[at0012]/value"), "6");		
		
		//kuhan riž, testenine (2 jušni žlici)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0006]/items[at0013]/value"), "7");		
		
		//Skupaj enot
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0006]/items[at0014]/value"), "8");		
		
		//<----Sadje in zelenjava----->
		
		//zelenjava (sveža in zamrznjena) (1 lonček)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0015]/items[at0016]/value"), "9");		
		
		//solata (1 porcija = 1 solatni krožnik)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0015]/items[at0017]/value"), "10");		
		
		//sveže sadje (jabolko, banana, pomaranče, kos melone,…)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0015]/items[at0018]/value"), "11");		
		
		//kuhano sadje (1 lonček)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0015]/items[at0019]/value"), "12");		
		
		//sadni sok (1 dcl)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0015]/items[at0020]/value"), "13");		
		
		//Skupno število enot za sadje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0015]/items[at0021]/value"), "14");				
		
		//<----Mleko in mlečni izdelki (enota: 2 dcl mleka, jogurta----->
		
		//polnomastno ali >3,2% m.m.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0022]/items[at0024]/value"), "15");		
		
		//delno posneto < 1,6% m.m.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0022]/items[at0025]/value"), "16");		
		
		//posneto 0,5% m.m.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0022]/items[at0026]/value"), "17");		
		
		//sir (košček, velik kot škatlica vžigalic)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0022]/items[at0027]/value"), "18");		
		
		//jogurt
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0022]/items[at0028]/value"), "19");		
		
		//skuta (3 velike žlice)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0022]/items[at0029]/value"), "20");
		
		//skupaj (new element)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0022]/items[at0030]/value"), "21");		
		
		//<----Meso, ribe in zamenjave----->
		
		//govedine, svinjine, jetra, perutnina, ribe (večji košček, ½ manjšega zrezka)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0031]/items[at0032]/value"), "22");		
		
		//jajce (1)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0031]/items[at0033]/value"), "23");		
		
		//leča, fižol, soja (3 jedilne žlice)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0031]/items[at0034]/value"), "24");		
		
		//skupaj (new element)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0031]/items[at0035]/value"), "25");		
		
		//<---- Maščobe ----->
		
		//Katero vrsto olja oz. maščobe uporabljate pri kuhanju?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0036]/items[at0046]/value"), "at0049");
		
		//Katero vrsto maščob uporabljate za mazanje?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0036]/items[at0047]/value"), "at0051");		
		
		//Koliko maščob zaužijete običajno na dan (kuhanje, namazi, zabele,…)?
		
		//masti (1 čajna žlička)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0036]/items[at0043]/items[at0037]/value"), "26");		
		
		//masla, margarine, ocvirkov (1 čajna žička)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0036]/items[at0043]/items[at0038]/value"), "27");		
		
		//olja za kuhanje (1 čajna žlička)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0036]/items[at0043]/items[at0041]/value"), "28");		
		
		//majoneze, solatnih prelivov (1 čajna žlička)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0036]/items[at0043]/items[at0042]/value"), "29");		
		
		//sladka smetana (30 %) (1 jedilna žlica)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0036]/items[at0043]/items[at0040]/value"), "30");
		
		//kisla smetana (20%) (2 jedilni žlici)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0036]/items[at0043]/items[at0039]/value"), "31");		
		
		//lešniki (3 komadi), orehi (5 polovičk), arašidi (6 komadov)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0036]/items[at0043]/items[at0044]/value"), "32");		
		
		//skupaj
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0036]/items[at0043]/items[at0045]/value"), "33");		
		
		//<---- Sladkorji ----->
		
		//sladkorja v pijačah (1 čajna žlička)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0055]/items[at0056]/value"), "34");		
		
		//krofov (1)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0055]/items[at0057]/value"), "35");		
		
		//kolačev, pit (1 kozarec)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0055]/items[at0058]/value"), "36");
		
		//sladkorja (1 žlička, kocka)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0055]/items[at0059]/value"), "37");		
		
		//čokolade (1 tablica)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0055]/items[at0060]/value"), "38");		
		
		//biskvita (3 koščki)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0055]/items[at0061]/value"), "39");		
		
		//skupaj
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0055]/items[at0062]/value"), "40");		
		
		//<---- Pijača ----->
		
		//kave (1 skodelica)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0063]/items[at0064]/value"), "41");		
		
		//čaja (1 skodelica)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0063]/items[at0065]/value"), "42");
		
		//gaziranih pijač (1 kozarec)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0063]/items[at0066]/value"), "43");		
		
		//brezalkoholnih, nesladkanih pijač (1 kozarec)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0063]/items[at0068]/value"), "44");		
		
		//vode (1 kozarec)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0063]/items[at0067]/value"), "45");		
		
		//skupaj
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0063]/items[at0069]/value"), "46");		
		
		//<---- Alkohol ----->
		
		//piva (3 dcl)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0070]/items[at0073]/value"), "47");		
		
		//vina (1 dcl)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0070]/items[at0074]/value"), "48");	
		
		//žganja (0,3 dcl)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0070]/items[at0075]/value"), "49");
		
		//likerjev, koktajlov (0,5 dcl)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0070]/items[at0076]/value"), "50");		
		
		//skupaj
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0070]/items[at0077]/value"), "51");		
		
		//<---- Druga jedila ----->
		
		//Ali uživate katerekoli druge jedi, ki jih nismo našteli (npr.: pizzo, baklavo, pečenice, kranjske klobase, hrenovke, paštete,…)?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0072]/items[at0080]/value"), "TRUE");		
		
		//Če DA, katera?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_pr_kaj_jem_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[at0072]/items[at0081]/value"), "vse sorte");		
		
		//<----Vprasalnik TD test hoje eo hu----->
		
		//Naziv testa
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_test_hoje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/value"), "t1");		
		
		//datum
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_test_hoje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "2012-03-01");	
		
		//trajanje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_test_hoje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[at0005]/value"), "00:20");
		
		//Srčni utrip na koncu
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_test_hoje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[at0011]/value"), "122");		
		
		//Indeks telesne zmogljivosti (ITZ)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_test_hoje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0014]/items[at0010]/value"), "t2");		
		
		//Kategorija telesne zmogljivosti
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_test_hoje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0014]/items[at0012]/value"), "t3");		
		
		//Stanje prehranjenosti
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_test_hoje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0014]/items[at0013]/value"), "t4");		
		
		//<----Vprasalnik TD stopnja spreminjanja----->
		
		
		//Označite za vaš trenutni življenjski slog najbolj ustrezno trditev:
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "at0008");		
		
		//Redno se gibljem (hoja, kolesarjenje, delo na vrtu, gospodinjska opravila, sprehajanje s psom) večino dni v tednu, vsaj pol ure na dan.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0021]/value"), "FALSE");	
		
		//Hodim peš ali se vozim s kolesom v službo / po opravkih.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0022]/value"), "TRUE");
		
		//3-5x tedensko sem najmanj 20 minut skupaj intenzivneje telesno dejaven tako, da se zadiham in prepotim.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0023]/value"), "FALSE");		
		
		//2-3x tedensko izvajam najmanj 20 minut skupaj vaje za mišično moč in gibljivost sklepov ter hrbtenice.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0024]/value"), "FALSE");		
		
		//V prostem času presedim več kot 3 ure dnevno pred televizijo, osebnim računalnikom, časopisi ipd.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0025]/value"), "FALSE");		
		
		//Redno hodim po stopnicah namesto, da se peljem z dvigalom ali s tekočimi stopnicami.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0026]/value"), "FALSE");		

		//<----Vprasalnik TD sportna anamneza eo hu----->		
		
		//Poklic in delo, ki ga opravljate
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0007]/value"), "v1");		
		
		//Vaši hobiji
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0008]/value"), "v2");			
		
		//Vaše športno-rekreativne dejavnosti
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0009]/value"), "v3");		
		
		//Zdravstveno stanje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0010]/value"), "v4");	
		
		//Vas Vaše zdravstveno stanje s čimerkoli omejuje pri gibanju in izvajanju športnih dejavnosti?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0014]/value"), "v5");
		
		//Ali vam je zdravnik zaradi zdravja svetoval, da se ukvarjate s telesno dejavnostjo?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0020]/value"), "FALSE");		
		
		//Če da, katere športno-rekreativne dejavnosti vam je svetoval?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0019]/value"), "v6");		
		
		//Katere telesne dejavnosti so vam blizu in katere športe obvladate?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0018]/value"), "v7");		
		
		//S katerimi športi in telesnimi dejavnostmi ste se doslej ukvarjali?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0017]/value"), "v8");		
		
		//Kolikokrat ste vadili na teden/mesec in koliko časa?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0015]/value"), "v9");		
		
		//S katerimi oblikami gibanja in športi se trenutno ukvarjate?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0016]/value"), "v10");	
		
		//Kolikokrat na teden/mesec in koliko časa?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0011]/value"), "v11");		
		
		//S katerimi telesnimi dejavnostmi (vključno s hojo) in športi bi se želeli ukvarjati?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0012]/value"), "v12");	
		
		//Imate dovolj prostega časa za šport?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0024]/value"), "TRUE");
		
		//Imate dovolj denarja za športne potrebe?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0025]/value"), "TRUE");		
		
		//Imate ustrezno športno ponudbo v svojem okolju (npr. programe, objekte, opremo)?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0026]/value"), "TRUE");		
		
		//Zakaj se ukvarjate s športno-rekreativnimi dejavnostmi?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0023]/value"), "v13");		
		
		//Menite, da imate možnosti za vsakodnevno športno-rekreativno razvedrilo (npr. sprehajanje, kolesarjenje, vrtnarjenje)?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0027]/value"), "FALSE");		
		
		//Vaš odnos do telesne dejavnosti
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[at0028]/value"), "at0032");		
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), "v14");		
		
		//datum
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_sportna_anamneza_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/value"), "2012-03-01");	
		
		//<----Hu koraki 02----->
		
		//prehrana
		//Katera skupina živil je čisto na vrhu piramide in jo moramo uživati čim manj?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_02.v1]/data[at0001]/items[at0010]/items[at0005]/value"), "at0021");		
		
		//Koliko enot škrobnega (ogljikohidratnega) živila predstavljata 2 kosa kruha (120 g)?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_02.v1]/data[at0001]/items[at0010]/items[at0009]/value"), "at0023");	
		
		//Kakšna je optimalna izguba telesne mase v enem mesecu?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_02.v1]/data[at0001]/items[at0010]/items[at0014]/value"), "at0027");
		
		//gibanje
		//Katera trditev velja za hojo?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_02.v1]/data[at0001]/items[at0019]/items[at0029]/value"), "at0031");		
		
		//Individualni program telesne dejavnosti je pomemben:
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_02.v1]/data[at0001]/items[at0019]/items[at0033]/value"), "at0037");		
		
		//Mavrični program hoje je:
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_02.v1]/data[at0001]/items[at0019]/items[at0034]/value"), "at0041");		
		
		//Stopnjo telesne pripravljenosti lahko ocenimo/izmerimo:
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_02.v1]/data[at0001]/items[at0019]/items[at0035]/value"), "at0043");		
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_02.v1]/data[at0001]/items[at0018]/value"), "v15");		
				
		
		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> prepareDataStep3() {
		
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		//<---- Vprasalnik TD slog eo hu ----->
		
		//Izberite za vaš trenutni življenjski slog najbolj ustrezno trditev
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_slog_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "at0010");
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_slog_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0020]/value"), "v1");
		
		//datum 
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_slog_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0021]/value"), "2012-03-01");
		
		//<---- Dogodek EO ----->
		
		//Zajtrk.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0001]/value"), "z1");
		
		//Dopoldanska malica.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0002]/value"), "m1");
		
		//Kosilo.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0003]/value"), "k1");
		
		//pop. malica
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0004]/value"), "m2");
		
		//Večerja.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0005]/value"), "v2");
		
		//datum
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/value"), "2012-03-01");
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), "komentar");
		
		//<---- HU Koraki 03 ----->
		
		//vprašanja prehrana
		//Ali naše telo za normalno delovanje potrebuje kuhinjski sladkor?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_03.v1]/data[at0001]/items[at0003]/items[at0006]/value"), "at0012");
		
		//Kakšna žita in žitne izdelke je priporočeno uživati?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_03.v1]/data[at0001]/items[at0003]/items[at0007]/value"), "at0014");		
		
		//Približno koliko žlic kuhanega riža predstavlja 4 enote škrobnega (ogljikohidratnega) živila?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_03.v1]/data[at0001]/items[at0003]/items[at0011]/value"), "at0017");
		
		//vprašanja gibanje
		//Katera trditev velja za osnovno strukturo vadbene enote?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_03.v1]/data[at0001]/items[at0004]/items[at0008]/value"), "at0022");
		
		//Katera trditev NE velja za držo in dihanje med vadbo?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_03.v1]/data[at0001]/items[at0004]/items[at0009]/value"), "at0024");
		
		//Katera trditev velja za ogrevanje?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_03.v1]/data[at0001]/items[at0004]/items[at0010]/value"), "at0026");
		
		//Za vaje za raztezanje velja naslednja trditev:
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_03.v1]/data[at0001]/items[at0004]/items[at0019]/value"), "at0030");
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_03.v1]/data[at0001]/items[at0005]/value"), "komentar 55");
		
		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> prepareDataStep4() {
		
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		//<---- Dogodek eo ----->
		
		//Zajtrk.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0001]/value"), "z1");
											 		
		//Dopoldanska malica.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0002]/value"), "m1");
		
		//Kosilo.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0003]/value"), "k1");
		
		//pop. malica
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0004]/value"), "m2");
		
		//Večerja.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0005]/value"), "v2");

		//datum
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/value"), "2012-03-01");
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), "komentar");
		
		//<---- Hu koraki 04 ----->
		
		//vprasanja prehrana
		//Česa je potrebno na dan zaužiti več, sadja ali zelenjave?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_04.v1]/data[at0001]/items[at0003]/items[at0006]/value"), "Sadja");
		
		//Kaj predstavljajo 4 enote zelenjave?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_04.v1]/data[at0001]/items[at0003]/items[at0007]/value"), "at0012");		
		
		//Sadje in zelenjava...
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_04.v1]/data[at0001]/items[at0003]/items[at0008]/value"), "at0010");
		
		//vprasanja gibanje
		//Katera trditev velja za ohlajanje?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_04.v1]/data[at0001]/items[at0004]/items[at0015]/value"), "at0019");		
		
		//Katera trditev velja za ravnotežje?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_04.v1]/data[at0001]/items[at0004]/items[at0016]/value"), "at0022");
		
		//Sproščanje:
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_04.v1]/data[at0001]/items[at0004]/items[at0017]/value"), "at0024");		
		
		//komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_04.v1]/data[at0001]/items[at0005]/value"), "komentar 5");
		
		//<---- Vprasalnik PS pogodba s seboj ----->
		
		//Podpis udeleženca.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_ps_pogodba_s_seboj.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "p1");
		
		//Podpis podpornika
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_ps_pogodba_s_seboj.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), "p2");
		
		//Datum
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_ps_pogodba_s_seboj.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/value"), "2012-03-01");		
			
		
		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> prepareDataStep05() {
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		userInputsMap = fillWeeklyPlan(userInputsMap);
		
//			Katere tri stvari lahko počnem vsak dan, da bom povečal svojo telesno dejavnost?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_premostitev_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[at0004]/value"), "t78");
//			Kaj zame predstavljajo glavne ovire za redno telesno dejavnost?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_premostitev_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[at0005]/value"), "t79");
//			Katere so tri vrste dejavnosti, pri katerih dovolj uživam, da bi jih lahko počel redno?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_premostitev_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[at0006]/value"), "t80");
//			Komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_premostitev_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/value"), "t81");
//			questionnaire_wl_05
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_05.v1]/data[at0001]/items[at0002]/value"), "t82");
//			Katerim maščobnim živilom se moramo izogibati?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_05.v1]/data[at0001]/items[at0003]/items[at0005]/value"), "at0018");
//			V katerih živilih se nahajajo škodljive trans maščobe?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_05.v1]/data[at0001]/items[at0003]/items[at0006]/value"), "at0016");
//			Katerim živilom so dodani enostavni sladkorji?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_05.v1]/data[at0001]/items[at0003]/items[at0007]/value"), "at0013");
//			Katera trditev NE velja za aerobno vadbo?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_05.v1]/data[at0001]/items[at0004]/items[at0008]/value"), "at0020");
//			Katera trditev NE velja za samoocenjevanje intenzivnosti vadbe?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_05.v1]/data[at0001]/items[at0004]/items[at0009]/value"), "at0023");
//			Komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_05.v1]/data[at0001]/items[at0011]/value"), "t88");
		
		
		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> prepareDataStep06() {
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		userInputsMap = fillWeeklyPlan(userInputsMap);
		
		//questionnaire!wl!06	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_06.v1]/data[at0001]/items[at0002]/value"), "test");
		//Načelo uravnotežene vadbe vključuje:	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_06.v1]/data[at0001]/items[at0004]/items[at0008]/value"), "at0009");
		//Katera trditev velja za hujšanje: 	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_06.v1]/data[at0001]/items[at0004]/items[at0007]/value"), "at0013");
		//Katera trditev drži?	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_06.v1]/data[at0001]/items[at0004]/items[at0015]/value"), "at0020");
		//Katera trditev drži za uravnoteženo vadbo?	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_06.v1]/data[at0001]/items[at0004]/items[at0016]/value"), "at0024");
		//Katera trditev velja za uravnavanje telesne mase?	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_06.v1]/data[at0001]/items[at0004]/items[at0017]/value"), "at0026");
		//Komentar	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_06.v1]/data[at0001]/items[at0005]/value"), "91");
		
		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> prepareDataStep07() {
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		//Negativna misel
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.vprasalnik_negativne_misli_eo_hu.v1]/items[at0001]/value"), "1");
		//Alternativna misel	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.vprasalnik_negativne_misli_eo_hu.v1]/items[at0002]/value"), "2");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.vprasalnik_negativne_misli_eo_hu.v1]/items[at0001]/value"), "11");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.vprasalnik_negativne_misli_eo_hu.v1]/items[at0002]/value"), "22");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.vprasalnik_negativne_misli_eo_hu.v1]/items[at0001]/value"), "111");
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.vprasalnik_negativne_misli_eo_hu.v1]/items[at0002]/value"), "222");
		
		userInputsMap = fillWeeklyPlan(userInputsMap);
		
		//Izpolnjen delovni list Negativne misli
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_07.v1]/data[at0001]/items[at0006]/value"), "TRUE");
		//Komentar	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_07.v1]/data[at0001]/items[at0005]/value"), "test");
		
		return userInputsMap;
	}

	private Map<ArcheDataPath, Object> prepareDataStep08() {
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		//Označite za vaš trenutni življenjski slog najbolj ustrezno trditev:
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "at0009");
		//Redno se gibljem (hoja, kolesarjenje, delo na vrtu, gospodinjska opravila, sprehajanje s psom) večino dni v tednu, vsaj pol ure na dan.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0021]/value"), "TRUE");
		//Hodim peš ali se vozim s kolesom v službo / po opravkih.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0022]/value"), "TRUE");
		//3-5x tedensko sem najmanj 20 minut skupaj intenzivneje telesno dejaven tako, da se zadiham in prepotim.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0023]/value"), "TRUE");
		//2-3x tedensko izvajam najmanj 20 minut skupaj vaje za mišično moč in gibljivost sklepov ter hrbtenice.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0024]/value"), "TRUE");
		//V prostem času presedim več kot 3 ure dnevno pred televizijo, osebnim računalnikom, časopisi ipd.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0025]/value"), "TRUE");
		//Redno hodim po stopnicah namesto, da se peljem z dvigalom ali s tekočimi stopnicami.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0026]/value"), "FALSE");
		//Samopodoba
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_ps_krepitev_samopodobe.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "8");
		//Datum in čas
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_ps_krepitev_samopodobe.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value"), "2012-02-28");

		userInputsMap = fillWeeklyPlan(userInputsMap);
		
//		//questionnaire_wl_08
//		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_08.v1]/data[at0001]/items[at0002]/value"), "94");
		//Izpolnjen delovni list Krepitev samopodobe
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_08.v1]/data[at0001]/items[at0006]/value"), "TRUE");
		//Komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_08.v1]/data[at0001]/items[at0005]/value"), "test");		
		
		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> prepareDataStep09() {
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		//Dogodek
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.vprasalnik_stres_eo_hu.v1]/items[at0001]/value"), "1");
		//Doživljanje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.vprasalnik_stres_eo_hu.v1]/items[at0002]/value"), "2");
		//Odziv
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.vprasalnik_stres_eo_hu.v1]/items[at0003]/value"), "3");
		//Sprememba odziva
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.vprasalnik_stres_eo_hu.v1]/items[at0004]/value"), "4");
		
		//Dogodek
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.vprasalnik_stres_eo_hu.v1]/items[at0001]/value"), "5");
		//Doživljanje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.vprasalnik_stres_eo_hu.v1]/items[at0002]/value"), "6");
		//Odziv
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.vprasalnik_stres_eo_hu.v1]/items[at0003]/value"), "7");
		//Sprememba odziva
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.dogodek_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.vprasalnik_stres_eo_hu.v1]/items[at0004]/value"), "8");
		
		
		userInputsMap = fillWeeklyPlan(userInputsMap);
		
		//Izpolnjen delovni list Spoprijemanje s stresom
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_09.v1]/data[at0001]/items[at0007]/value"), "TRUE");
		//Komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_09.v1]/data[at0001]/items[at0006]/value"), "6");
		
		return userInputsMap;
	}

	private Map<ArcheDataPath, Object> prepareDataStep10() {
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		userInputsMap = fillWeeklyPlan(userInputsMap);
		
		//Kakšno mleko in mlečne izdelke je bolje izbrati?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_10.v1]/data[at0001]/items[at0003]/items[at0006]/value"), "at0009");
		//Katera živila so bogat vir beljakovin?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_10.v1]/data[at0001]/items[at0003]/items[at0007]/value"), "at0012");
		//Zakaj je priporočeno uživati mastne morske ribe, meso in mesni izdelki pa morajo biti čim manj mastni?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_10.v1]/data[at0001]/items[at0003]/items[at0008]/value"), "at0013");	
	
		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> prepareDataStep11() {
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		userInputsMap = fillWeeklyPlan(userInputsMap);
		
		//Kuhanje v majhni količini vode, v vodni kopeli, v sopari in v aluminijasti foliji
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_11.v1]/data[at0001]/items[at0003]/items[at0006]/value"), "TRUE");
		//Cvrenje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_11.v1]/data[at0001]/items[at0003]/items[at0007]/value"), "TRUE");
		//Kuhanje pod zvišanim pritiskom
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_11.v1]/data[at0001]/items[at0003]/items[at0008]/value"), "TRUE");
		//Dušenje v lastnem soku ter z malo maščob in vode
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_11.v1]/data[at0001]/items[at0003]/items[at0009]/value"), "TRUE");
		//Kuhanje z veliko soli
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_11.v1]/data[at0001]/items[at0003]/items[at0010]/value"), "TRUE");
		//Pečenje v ponvi, pečici, v foliji za pečenje in v aluminijasti foliji
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_11.v1]/data[at0001]/items[at0003]/items[at0011]/value"), "TRUE");
		//Pečenje v pečici na vroči zrak
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_11.v1]/data[at0001]/items[at0003]/items[at0012]/value"), "TRUE");
		//Kuhanje z maslom in smetano
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_11.v1]/data[at0001]/items[at0003]/items[at0013]/value"), "FALSE");
		//Komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_11.v1]/data[at0001]/items[at0005]/value"), "9");

		
		
		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> prepareDataStep12() {
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		//Označite za vaš trenutni življenjski slog najbolj ustrezno trditev:
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "at0006");
		//Redno se gibljem (hoja, kolesarjenje, delo na vrtu, gospodinjska opravila, sprehajanje s psom) večino dni v tednu, vsaj pol ure na dan.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0021]/value"), "FALSE");
		//Hodim peš ali se vozim s kolesom v službo / po opravkih.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0022]/value"), "FALSE");
		//3-5x tedensko sem najmanj 20 minut skupaj intenzivneje telesno dejaven tako, da se zadiham in prepotim.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0023]/value"), "FALSE");
		//2-3x tedensko izvajam najmanj 20 minut skupaj vaje za mišično moč in gibljivost sklepov ter hrbtenice.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0024]/value"), "FALSE");
		//V prostem času presedim več kot 3 ure dnevno pred televizijo, osebnim računalnikom, časopisi ipd.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0025]/value"), "FALSE");
		//Redno hodim po stopnicah namesto, da se peljem z dvigalom ali s tekočimi stopnicami.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0026]/value"), "FALSE");

		userInputsMap = fillWeeklyPlan(userInputsMap);
		
		//Koliko gramov prehranske vlaknine mora vsebovati izdelek ali servirna porcija, da nam zagotovi bogat vir prehranske vlaknine in je navedena na deklaraciji?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_12.v1]/data[at0001]/items[at0003]/items[at0006]/value"), "at0009");
		//Katera izbira je bolj zdrava?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_12.v1]/data[at0001]/items[at0003]/items[at0007]/value"), "at0011");
		//Komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_12.v1]/data[at0001]/items[at0005]/value"), "10");

		
		
		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> prepareDataStep13() {
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		userInputsMap = fillWeeklyPlan(userInputsMap);
		
		//Za izgubo telesna mase je najbolje, če:
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_13.v1]/data[at0001]/items[at0003]/items[at0006]/value"), "at0009");
		//Ali je vegeterjanska prehrana, ki vključuje mleko, mlečne izdelke in jajca zdrava, če je skrbno načrtovana?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_13.v1]/data[at0001]/items[at0003]/items[at0007]/value"), "at0011");

		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> prepareDataStep14() {
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		userInputsMap = fillWeeklyPlan(userInputsMap);
		
		//Ali je ob uravnoteženi prehrani priporočeno uživati tudi preparate za hujšanje?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_14.v1]/data[at0001]/items[at0005]/items[at0003]/value"), "at0007");
		//Komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_14.v1]/data[at0001]/items[at0004]/value"), "test");
		
		
		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> prepareDataStep15() {
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		userInputsMap = fillWeeklyPlan(userInputsMap);
		
		//Ali imajo zaščitne snovi (vitamini, minerali...) v prehranskih dopolnilih enak učinek v preventivi pred boleznimi kot te snovi iz hrane?
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_15.v1]/data[at0001]/items[at0003]/items[at0006]/value"), "FALSE");
		//Komentar
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-EVALUATION.hu_koraki_15.v1]/data[at0001]/items[at0005]/value"), "test");
		
		
		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> prepareDataStep16() {
		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		//Označite za vaš trenutni življenjski slog najbolj ustrezno trditev:
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "at0009");
		//Redno se gibljem (hoja, kolesarjenje, delo na vrtu, gospodinjska opravila, sprehajanje s psom) večino dni v tednu, vsaj pol ure na dan.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0021]/value"), "TRUE");
		//Hodim peš ali se vozim s kolesom v službo / po opravkih.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0022]/value"), "TRUE");
		//3-5x tedensko sem najmanj 20 minut skupaj intenzivneje telesno dejaven tako, da se zadiham in prepotim.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0023]/value"), "TRUE");
		//2-3x tedensko izvajam najmanj 20 minut skupaj vaje za mišično moč in gibljivost sklepov ter hrbtenice.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0024]/value"), "TRUE");
		//V prostem času presedim več kot 3 ure dnevno pred televizijo, osebnim računalnikom, časopisi ipd.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0025]/value"), "TRUE");
		//Redno hodim po stopnicah namesto, da se peljem z dvigalom ali s tekočimi stopnicami.
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_stopnja_spreminjanja.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0026]/value"), "FALSE");
		
		
		//Naziv testa
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_test_hoje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/value"), "t1");		
		
		//datum
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_test_hoje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value"), "2012-03-01");	
		
		//trajanje
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_test_hoje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[at0005]/value"), "00:20");
		
		//Srčni utrip na koncu
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_test_hoje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[at0011]/value"), "122");		
		
		//Indeks telesne zmogljivosti (ITZ)
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_test_hoje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0014]/items[at0010]/value"), "t2");		
		
		//Kategorija telesne zmogljivosti
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_test_hoje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0014]/items[at0012]/value"), "t3");		
		
		//Stanje prehranjenosti
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.vprasalnik_td_test_hoje_eo_hu.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0014]/items[at0013]/value"), "t4");		
		
		userInputsMap = fillWeeklyPlan(userInputsMap);
		
		return userInputsMap;
	}
	
	private Map<ArcheDataPath, Object> fillWeeklyPlan(Map<ArcheDataPath, Object> userInputsMap) {
		
		if (userInputsMap == null)
			userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		//Zajtrk	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0001]/value"), "1");
		//Dop. malica	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0002]/value"), "2");
		//Kosilo	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0003]/value"), "3");
		//Pop. malica	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0004]/value"), "4");
		//Večerja	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0005]/value"), "5");
		//Drugo	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0006]/value"), "6");
		//Datum	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0007]/value"), "2012-02-28");
		//Datum	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0001]/value"), "2012-02-28");
		//Panoga	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0002]/value"), "at0029");
		//Trajanje	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0004]/value"), "00:20");
		//Komentar	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0005]/value"), "11");
		//Intenzivnost vadbe 5 st	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0059]/value"), "at0062");
		//Zajtrk	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0001]/value"), "13");
		//Dop. malica	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0002]/value"), "14");
		//Kosilo	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0003]/value"), "15");
		//Pop. malica	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0004]/value"), "16");
		//Večerja	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0005]/value"), "17");
		//Drugo	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0006]/value"), "18");
		//Datum	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0007]/value"), "2012-02-28");
		//Datum	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0001]/value"), "2012-02-28");
		//Panoga	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0002]/value"), "at0029");
		//Trajanje	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0004]/value"), "00:20");
		//Komentar	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0005]/value"), "23");
		//Intenzivnost vadbe 5 st	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0005]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0059]/value"), "at0062");
		//Zajtrk	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0001]/value"), "25");
		//Dop. malica	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0002]/value"), "26");
		//Kosilo	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0003]/value"), "27");
		//Pop. malica	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0004]/value"), "28");
		//Večerja	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0005]/value"), "29");
		//Drugo	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0006]/value"), "30");
		//Datum	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0007]/value"), "2012-02-28");
		//Datum	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0001]/value"), "2012-02-28");
		//Panoga	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0002]/value"), "at0029");
		//Trajanje	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0004]/value"), "00:20");
		//Komentar	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0005]/value"), "35");
		//Intenzivnost vadbe 5 st	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0006]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0059]/value"), "at0062");
		//Zajtrk	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0001]/value"), "37");
		//Dop. malica	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0002]/value"), "38");
		//Kosilo	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0003]/value"), "39");
		//Pop. malica	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0004]/value"), "40");
		//Večerja	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0005]/value"), "41");
		//Drugo	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0006]/value"), "42");
		//Datum	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0007]/value"), "2012-02-28");
		//Datum	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0001]/value"), "2012-02-28");
		//Panoga	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0002]/value"), "at0029");
		//Trajanje	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0004]/value"), "00:20");
		//Komentar	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0005]/value"), "47");
		//Intenzivnost vadbe 5 st	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0007]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0059]/value"), "at0062");
		//Zajtrk	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0001]/value"), "49");
		//Dop. malica	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0002]/value"), "50");
		//Kosilo	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0003]/value"), "51");
		//Pop. malica	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0004]/value"), "52");
		//Večerja	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0005]/value"), "53");
		//Drugo	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0006]/value"), "54");
		//Datum	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0007]/value"), "2012-02-28");
		//Datum	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0001]/value"), "2012-02-28");
		//Panoga	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0002]/value"), "at0029");
		//Trajanje	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0004]/value"), "00:20");
		//Komentar	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0005]/value"), "59");
		//Intenzivnost vadbe 5 st	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0008]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0059]/value"), "at0062");
		//Zajtrk	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0001]/value"), "61");
		//Dop. malica	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0002]/value"), "62");
		//Kosilo	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0003]/value"), "63");
		//Pop. malica	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0004]/value"), "64");
		//Večerja	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0005]/value"), "65");
		//Drugo	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0006]/value"), "66");
		//Datum	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0007]/value"), "2012-02-28");
		//Datum	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0001]/value"), "2012-02-28");
		//Panoga	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0002]/value"), "at0029");
		//Trajanje	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0004]/value"), "00:20");
		//Komentar	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0005]/value"), "71");
		//Intenzivnost vadbe 5 st	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0009]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0059]/value"), "at0062");
		//Zajtrk	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0010]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0001]/value"), "73");
		//Dop. malica	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0010]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0002]/value"), "74");
		//Kosilo	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0010]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0003]/value"), "75");
		//Pop. malica	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0010]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0004]/value"), "76");
		//Večerja	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0010]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0005]/value"), "77");
		//Drugo	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0010]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0006]/value"), "78");
		//Datum	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0010]/items[openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1]/items[at0007]/value"), "2012-02-28");
		//Datum	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0010]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0001]/value"), "2012-02-28");
		//Panoga	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0010]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0002]/value"), "at0029");
		//Trajanje	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0010]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0004]/value"), "00:20");
		//Komentar	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0010]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0005]/value"), "83");
		//Intenzivnost vadbe 5 st	
		userInputsMap.put(new ArcheDataPath("/items[openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0010]/items[openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1]/items[at0059]/value"), "at0062");
		
		return userInputsMap;
	}



}
