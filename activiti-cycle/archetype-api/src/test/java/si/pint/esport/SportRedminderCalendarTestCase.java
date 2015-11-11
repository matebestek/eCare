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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.SportReminderCalendarArcheSrv;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.utils.SportArchSrvUtils;
import si.pint.utils.SportArchSrvUtils.TRAINING_PROGRAM;


public class SportRedminderCalendarTestCase extends TestCase {
	
	public static void main(String[] args) {
		importCalendarFromCsv();
	}
	public static LinkedList<Vadba> importCalendarFromCsv() {
		LinkedList<Vadba> vadbe = new LinkedList<Vadba>();
		File f = new File("C:/Projects/eOskrba/Implementation/Snapshots/eOskrba_1.0_STS/archetype-api/src/test/java/si/pint/esport/vadbe_nizka.csv");
		vadbe.addAll(SportArchSrvUtils.doImport(f,"utf-8",",",1));
		f = new File("C:/Projects/eOskrba/Implementation/Snapshots/eOskrba_1.0_STS/archetype-api/src/test/java/si/pint/esport/vadbe_visoka.csv");
		vadbe.addAll(SportArchSrvUtils.doImport(f,"utf-8",",",2));
		System.out.println(vadbe);
		
		return vadbe;
	}
	
	// calculating calendar from patient parameters
	public void testCalculateCalendar() {
		//String zacetek = "2012-08-01";
		String zacetek = SportArchSrvUtils.sdf.format(new Date());
		String prviDan = "at0006";
		String programa = "at0003";
		int program = (programa.equals("at0003") ? 0 : (programa.equals("at0004") ? 1 : 0));
		TRAINING_PROGRAM programe = (programa.equals("at0003") ? TRAINING_PROGRAM.LOW : (programa.equals("at0004") ? TRAINING_PROGRAM.HIGH : TRAINING_PROGRAM.LOW));
		//LinkedList<Vadba> vadbetemplate = new LinkedList<Vadba>();
		//File f = new File("d:/dl/vadbe_nizka.csv");
		//vadbe.addAll(doImport(f,"utf-8",",",program));
		List vadbe = SportArchSrvUtils.readExerciseListFromXML(programe, "admin");
		//System.out.println(vadbe);
		SportArchSrvUtils.doCalculateCalendar(vadbe,zacetek,prviDan);
		Map<ArcheDataPath, Object> userInputsMap = SportArchSrvUtils.prepareCalendarTemplateXml(vadbe,true);
		
		// shranim
		SportReminderCalendarArcheSrv service = new SportReminderCalendarArcheSrv();

		try {
			// init template
			assertTrue("Archetype/templates did not initialise correctly!", service.initInput()); 
			
			assertTrue("User data should be ok!", service.setAndValidataData(userInputsMap));
			assertTrue("Data did not save correctly into XML!",	service.saveInput(SystemType.EOSKRBATEST,"bobo@gugu.com","bobo@gugu.com"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void testOpomnik() {

		SportReminderCalendarArcheSrv service = new SportReminderCalendarArcheSrv();

		try {
			// init template
			assertTrue("Archetype/templates did not initialise correctly!", service.initInput()); 

//			Map<ArcheDataPath, Object> userInputsMap = prepareDataAll();
			Map<ArcheDataPath, Object> userInputsMap = testFillData();
			
			assertTrue("User data should be ok!", service.setAndValidataData(userInputsMap));
			assertTrue("Data did not save correctly into XML!",	service.saveInput(SystemType.EOSKRBATEST,"bobo@gugu.com","bobo@gugu.com"));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void testReadFromXML() {
		List vadbe = SportArchSrvUtils.readExerciseListFromXML(TRAINING_PROGRAM.HIGH, "admin");
	}
	
	public Map<ArcheDataPath, Object> testFillData() {
		Vadba v1 = new Vadba();
		v1.setDan(1);
		v1.setDisciplina(1);
		v1.setIntenzivnost(1);
		v1.setPodtip(1);
		v1.setProgram(1);
		v1.setTeden(1);
		v1.setTrajanje(1);
		
		Vadba v2 = new Vadba();
		v2.setDan(7);
		v2.setDisciplina(2);
		v2.setIntenzivnost(5);
		v2.setPodtip(4);
		v2.setProgram(2);
		v2.setTeden(24);
		v2.setTrajanje(1000);
		
		List exerciseList = new ArrayList();
		exerciseList.add(v1);
		exerciseList.add(v2);
		
		return SportArchSrvUtils.prepareCalendarTemplateXml(importCalendarFromCsv(),false);
		
	}
	

	private Map<ArcheDataPath, Object> prepareDataAll() {

		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();
		
		// program 1
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0001]/value"), "at0007");
		
		// Datum vadbe 1
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0002]/value"), "1990-01-01");
		
		// Podtip vadbe 1
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0003]/value"), "at0009");
		
		// Trajanje vadbe 1
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0004]/value"), "01:15:00");
		
		// Disciplina 1
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0005]/value"), "at0013");
		
		// Intenzivnost vadbe 1
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0006]/value"), "at0015");
		
		//-------------------
		// program 2
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0001]/value"), "at0008");
		
		// Datum vadbe 2
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0002]/value"), "2000-01-01");
		
		// Podtip vadbe 2
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0003]/value"), "at0010");
		
		// Trajanje vadbe 2
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0004]/value"), "01:30:00");
		
		// Disciplina 2
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0005]/value"), "at0014");
		
		// Intenzivnost vadbe 2
		userInputsMap.put(new ArcheDataPath("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.sport_koledar.v1]/items[at0006]/value"), "at0016");
		
		
		
		
		return userInputsMap;
	}

}
