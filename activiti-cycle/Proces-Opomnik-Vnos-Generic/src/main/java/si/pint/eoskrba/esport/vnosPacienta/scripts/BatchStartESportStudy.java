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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//package si.pint.eoskrba.esport.vnosPacienta.scripts;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.SportReminderCalendarArcheSrv;
import si.pint.database.exist.DBManager;
import si.pint.utils.SportArchSrvUtils;

/**
 *
 * @author Mate
 */

public class BatchStartESportStudy {

    public static List<String[]> readData(String fileName){

        List<String[]> list = new ArrayList<String[]>();

        list.add(new String[]{"niko.colneric1@gmail.com", "at0003","at0006" });
	 
        return list;

    }
    
    public static void main(String[] args){
        String csvFile = "TODO: VPIŠI POT DO DATOTEKE";
        BatchStartESportStudy b = new BatchStartESportStudy();
        String username = null;
        String firstDay = null;
        String trainingProgram = null;
        List<String[]> data = readData(csvFile);

        //1. V zanki preberi csv datoteko
        for(ListIterator<String[]> li = data.listIterator();li.hasNext();){
            String[] s = li.next();
            username = s[0];
            firstDay = s[2];
            trainingProgram = s[1];
                //For each line in csv
                b.calculateCalendar(username, firstDay, trainingProgram);             
        }
    }    

    private static final String TRAINING_PROGRAM_LOW_ARCHETYPE_CODE = "at0003";

    private static final String TRAINING_PROGRAM_HIGH_ARCHETYPE_CODE = "at0004";

    /**
    * Metoda sestavi koledar za pacienta glede na izbran tip vadbe
    * @author BoĹˇtjan Povh (original), Alex (prenos), Mate(skripta)
    * 
    * @param patientUsername Uporabnisko ime pacienta
    * @param firstDayArchetypeCode Vrednost kode prvega dne vadbe: PON = at0006, TOR = at0007, SRE = at0008
    * @param trainingProgramArchetypeCode Vrednost kode vrste programa: LAZJI = at0003, TEZJI = at0004
    */

    public void calculateCalendar(String patientUsername, String firstDayArchetypeCode, String trainingProgramArchetypeCode) {
		// ZaÄŤetek je danaĹˇnji datum
		String zacetek = SportArchSrvUtils.sdf.format(new Date());

		// Ugotovi izbrani program vadb
		SportArchSrvUtils.TRAINING_PROGRAM programe = (trainingProgramArchetypeCode.equals(TRAINING_PROGRAM_LOW_ARCHETYPE_CODE) ? SportArchSrvUtils.TRAINING_PROGRAM.LOW : (trainingProgramArchetypeCode.equals(TRAINING_PROGRAM_HIGH_ARCHETYPE_CODE) ? SportArchSrvUtils.TRAINING_PROGRAM.HIGH : SportArchSrvUtils.TRAINING_PROGRAM.LOW));

		// Preberi vadbe iz izbranega programa (ki je zapisan v XML)
		@SuppressWarnings("rawtypes")
		List vadbe = SportArchSrvUtils.readExerciseListFromXML(programe, "admin");

		// PreraÄŤunaj koledar
		SportArchSrvUtils.doCalculateCalendar(vadbe,zacetek,firstDayArchetypeCode);
		@SuppressWarnings("unchecked")
		Map<ArcheDataPath, Object> userInputsMap = SportArchSrvUtils.prepareCalendarTemplateXml(vadbe,true);
		
		// Shrani
		SportReminderCalendarArcheSrv service = new SportReminderCalendarArcheSrv();
		try {
			service.initInput(); 
			service.setAndValidataData(userInputsMap);
			service.saveInput(DBManager.SystemType.EOSKRBATEST,patientUsername,patientUsername);
		} catch (Exception e) {
			System.out.println("Napaka pri shranjevanju koledarja vadb za pacienta: " + patientUsername);
			e.printStackTrace();
		}
	}
 }
