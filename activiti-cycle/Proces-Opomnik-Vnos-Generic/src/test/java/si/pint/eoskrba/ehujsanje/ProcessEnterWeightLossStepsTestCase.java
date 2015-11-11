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
package si.pint.eoskrba.ehujsanje;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.messages.MessageRepo;

@Log4j
public class ProcessEnterWeightLossStepsTestCase extends ActivitiTestCase {

	private Map<String, Object> getTestSubject(Map<String, Object> variables, int step) {

		if (step < 1 || step > 16)
			return null;

		// 'external' variable from eOskrba-webApp
//		variables
//				.put(Constants.VAR_PATIENT_OBJ,
//						"{\"sex\":\"MALE\",\"height\":\"183\",\"weight\":\"85\",\"username\":\"pacient.hu\",\"processStep\":\""
//								+ step
//								+ "\",\"startDate\":\"15.02.2012\",\"firstNameLastName\":\"Pacient Hujsanje\",\"eMail\":\"EMAIL@DOMAIN.COM\",\"role\":\"1\",\"birthDate\":\"01.01.1980\",\"birpisId\":\"ivan.pacient\",\"mobilePhoneNum\":\"PHONE-NUMBER\"}");
		variables.put(Constants.VAR_WEIGHT, "85");
		variables.put(Constants.VAR_HEIGHT, "183");
		variables.put(Constants.VAR_USERNAME, "pacient.hu");
		variables.put(Constants.VAR_USERNAME_APP, "pacient.hu");
		
		//ta test pride ta podaten v proces od zunaj in ne iz LDAP
		variables.put(Constants.VAR_PROCESS_STEP, step + "");

		return variables;
	}

	public Map<String, Object> getProcessVarsOk(Map<String, Object> variables, int step) {

		if (step < 1 || step > 16)
			return null;

		if (step == 1) {
			// <----- Korak 1------->
			// Q1_F
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0009]?items[at0017]?value", "at0020");
			// Q2_F
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0009]?items[at0018]?value", "at0025");
			// Q3_F
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0009]?items[at0019]?value", "at0023");
			// Q1_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0028]?items[at0029]?value", "at0043");
			// Q2_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0028]?items[at0030]?value", "at0040");
			// Q3_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0028]?items[at0031]?value", "at0037");
			// Q4_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0028]?items[at0032]?value", "at0033");			
			
			// Komentar
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0014]?value", "ni komentarja");

			return variables;
		} else if (step == 2) {
			// <------Korak 2 -------->
			// Q1
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0010]?items[at0005]?value", "at0020");
			// Q2
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0010]?items[at0009]?value", "at0023");
			// Q3
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0010]?items[at0014]?value", "at0026");
			
			//Q1_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0019]?items[at0029]?value", "at0030");
			
			//Q2_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0019]?items[at0033]?value", "at0036");
			
			//Q3_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0019]?items[at0034]?value", "at0039");
			
			//Q4_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0019]?items[at0035]?value", "at0042");
			
			// Komentar
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0018]?value", "tudi tu ni komentarja");

			return variables;
		} else if (step == 3) {
			// <------Korak 3 -------->
			// Q1
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!03.v1]?data[at0001]?items[at0003]?items[at0006]?value", "at0012");
			// Q2
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!03.v1]?data[at0001]?items[at0003]?items[at0007]?value", "at0014");
			// Q3
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!03.v1]?data[at0001]?items[at0003]?items[at0011]?value", "at0016");	
			// Q1_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!03.v1]?data[at0001]?items[at0004]?items[at0008]?value", "at0020");
			// Q2_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!03.v1]?data[at0001]?items[at0004]?items[at0009]?value", "at0023");
			// Q3_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!03.v1]?data[at0001]?items[at0004]?items[at0010]?value", "at0026");
			// Q4_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!03.v1]?data[at0001]?items[at0004]?items[at0019]?value", "at0029");
			
			return variables;
		} else if (step == 4) {
			// <------Korak 4 -------->
			// Q1
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!04.v1]?data[at0001]?items[at0003]?items[at0006]?value", "at0013");
			// Q2
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!04.v1]?data[at0001]?items[at0003]?items[at0007]?value", "at0011");
			// Q3
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!04.v1]?data[at0001]?items[at0003]?items[at0008]?value", "at0009");	
			// Q1_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!04.v1]?data[at0001]?items[at0004]?items[at0015]?value", "at0018");
			// Q2_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!04.v1]?data[at0001]?items[at0004]?items[at0016]?value", "at0021");
			// Q3_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!04.v1]?data[at0001]?items[at0004]?items[at0017]?value", "at0024");
			
			return variables;
		}
		else if (step == 5) {
			// <------Korak 5 -------->
						
			//Katerim maščobnim živilom se moramo izogibati?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!05.v1]?data[at0001]?items[at0003]?items[at0005]?value", "at0017");
			//V katerih živilih se nahajajo škodljive trans maščobe?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!05.v1]?data[at0001]?items[at0003]?items[at0006]?value", "at0014");
			//Katerim živilom so dodani enostavni sladkorji?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!05.v1]?data[at0001]?items[at0003]?items[at0007]?value", "at0012");
			//Katera trditev NE velja za aerobno vadbo?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!05.v1]?data[at0001]?items[at0004]?items[at0008]?value", "at0020");
			//Katera trditev NE velja za samoocenjevanje intenzivnosti vadbe?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!05.v1]?data[at0001]?items[at0004]?items[at0009]?value", "at0023");
			
			return variables;
		}		
		else if (step == 6) {
			// <------Korak 6 -------->

			//Načelo uravnotežene vadbe vključuje:	
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!06.v1]?data[at0001]?items[at0004]?items[at0008]?value", "at0009");
			//Katera trditev velja za hujšanje: 	
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!06.v1]?data[at0001]?items[at0004]?items[at0007]?value", "at0012");
			//Katera trditev drži?	
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!06.v1]?data[at0001]?items[at0004]?items[at0015]?value", "at0018");
			//Katera trditev drži za uravnoteženo vadbo?	
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!06.v1]?data[at0001]?items[at0004]?items[at0016]?value", "at0022");
			//Katera trditev velja za uravnavanje telesne mase?	
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!06.v1]?data[at0001]?items[at0004]?items[at0017]?value", "at0025");
			
			return variables;
		}
		else if (step == 10) {
			// <------Korak 10-------->

			//Kakšno mleko in mlečne izdelke je bolje izbrati?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!10.v1]?data[at0001]?items[at0003]?items[at0006]?value", "at0009");
			//Katera živila so bogat vir beljakovin?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!10.v1]?data[at0001]?items[at0003]?items[at0007]?value", "at0011");
			//Zakaj je priporočeno uživati mastne morske ribe, meso in mesni izdelki pa morajo biti čim manj mastni?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!10.v1]?data[at0001]?items[at0003]?items[at0008]?value", "at0013");	
			
			return variables;
		}		
		else if (step == 11) {
			// <------Korak 11-------->

			//Kuhanje v majhni količini vode, v vodni kopeli, v sopari in v aluminijasti foliji
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0006]?value", "TRUE");
			//Cvrenje
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0007]?value", "FALSE");
			//Kuhanje pod zvišanim pritiskom
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0008]?value", "TRUE");
			//Dušenje v lastnem soku ter z malo maščob in vode
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0009]?value", "TRUE");
			//Kuhanje z veliko soli
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0010]?value", "FALSE");
			//Pečenje v ponvi, pečici, v foliji za pečenje in v aluminijasti foliji
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0011]?value", "TRUE");
			//Pečenje v pečici na vroči zrak
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0012]?value", "TRUE");
			//Kuhanje z maslom in smetano
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0013]?value", "FALSE");
			
			return variables;
		}
		else if (step == 12) {
			// <------Korak 11-------->

			//Koliko gramov prehranske vlaknine mora vsebovati izdelek ali servirna porcija, da nam zagotovi bogat vir prehranske vlaknine in je navedena na deklaraciji?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!12.v1]?data[at0001]?items[at0003]?items[at0006]?value", "at0008");
			//Katera izbira je bolj zdrava?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!12.v1]?data[at0001]?items[at0003]?items[at0007]?value", "at0011");
			
			return variables;
		}		
		else if (step == 13) {
			// <------Korak 13-------->

			//Za izgubo telesna mase je najbolje, če:
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!13.v1]?data[at0001]?items[at0003]?items[at0006]?value", "at0008");
			//Ali je vegeterjanska prehrana, ki vključuje mleko, mlečne izdelke in jajca zdrava, če je skrbno načrtovana?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!13.v1]?data[at0001]?items[at0003]?items[at0007]?value", "at0011");
			
			return variables;
		}
		else if (step == 14) {
			// <------Korak 14-------->

			//Ali je ob uravnoteženi prehrani priporočeno uživati tudi preparate za hujšanje?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!14.v1]?data[at0001]?items[at0005]?items[at0003]?value", "at0007");
			
			return variables;
		}
		else if (step == 15) {
			// <------Korak 15-------->

			//Ali imajo zaščitne snovi (vitamini, minerali...) v prehranskih dopolnilih enak učinek v preventivi pred boleznimi kot te snovi iz hrane?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!15.v1]?data[at0001]?items[at0003]?items[at0006]?value", "FALSE");
			
			return variables;
		}		
		
		return null;

	}

	public Map<String, Object> getProcessVarsNotOk(Map<String, Object> variables, int step) {

		if (step == 1) {
			// <----- Korak 1------->
			// Q1_F
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0009]?items[at0017]?value", "at0021");
			// Q2_F
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0009]?items[at0018]?value", "at0025");
			// Q3_F
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0009]?items[at0019]?value", "at0023");
			// Q1_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0028]?items[at0029]?value", "at0043");
			// Q2_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0028]?items[at0030]?value", "at0040");
			// Q3_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0028]?items[at0031]?value", "at0037");
			// Q4_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0028]?items[at0032]?value", "at0033");			
			
			// Komentar
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!01.v1]?data[at0001]?items[at0014]?value", "ni komentarja");

			return variables;
		} else if (step == 2) {
			// <------Korak 2 -------->
			// Q1
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0010]?items[at0005]?value", "at0021");
			// Q2
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0010]?items[at0009]?value", "at0023");
			// Q3
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0010]?items[at0014]?value", "at0026");
			
			//Q1_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0019]?items[at0029]?value", "at0030");
			
			//Q2_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0019]?items[at0033]?value", "at0036");
			
			//Q3_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0019]?items[at0034]?value", "at0039");
			
			//Q4_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0019]?items[at0035]?value", "at0042");
			
			// Komentar
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!02.v1]?data[at0001]?items[at0018]?value", "tudi tu ni komentarja");

			return variables;
		} else if (step == 3) {
			// <------Korak 3 -------->
			// Q1
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!03.v1]?data[at0001]?items[at0003]?items[at0006]?value", "at0012");
			// Q2
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!03.v1]?data[at0001]?items[at0003]?items[at0007]?value", "at0014");
			// Q3
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!03.v1]?data[at0001]?items[at0003]?items[at0011]?value", "at0016");	
			// Q1_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!03.v1]?data[at0001]?items[at0004]?items[at0008]?value", "at0020");
			// Q2_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!03.v1]?data[at0001]?items[at0004]?items[at0009]?value", "at0023");
			// Q3_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!03.v1]?data[at0001]?items[at0004]?items[at0010]?value", "at0026");
			// Q4_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!03.v1]?data[at0001]?items[at0004]?items[at0019]?value", "at0030");
			
			return variables;
		} else if (step == 4) {
			// <------Korak 4 -------->
			// Q1
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!04.v1]?data[at0001]?items[at0003]?items[at0006]?value", "at0014");
			// Q2
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!04.v1]?data[at0001]?items[at0003]?items[at0007]?value", "at0011");
			// Q3
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!04.v1]?data[at0001]?items[at0003]?items[at0008]?value", "at0009");	
			// Q1_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!04.v1]?data[at0001]?items[at0004]?items[at0015]?value", "at0018");
			// Q2_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!04.v1]?data[at0001]?items[at0004]?items[at0016]?value", "at0021");
			// Q3_E
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!04.v1]?data[at0001]?items[at0004]?items[at0017]?value", "at0024");
			
			return variables;
		}
		else if (step == 5) {
			// <------Korak 5 -------->
						
			//Katerim maščobnim živilom se moramo izogibati?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!05.v1]?data[at0001]?items[at0003]?items[at0005]?value", "at0018");
			//V katerih živilih se nahajajo škodljive trans maščobe?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!05.v1]?data[at0001]?items[at0003]?items[at0006]?value", "at0014");
			//Katerim živilom so dodani enostavni sladkorji?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!05.v1]?data[at0001]?items[at0003]?items[at0007]?value", "at0012");
			//Katera trditev NE velja za aerobno vadbo?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!05.v1]?data[at0001]?items[at0004]?items[at0008]?value", "at0020");
			//Katera trditev NE velja za samoocenjevanje intenzivnosti vadbe?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!05.v1]?data[at0001]?items[at0004]?items[at0009]?value", "at0023");
			
			return variables;
		}
		else if (step == 6) {
			// <------Korak 6 -------->

			//Načelo uravnotežene vadbe vključuje:	
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!06.v1]?data[at0001]?items[at0004]?items[at0008]?value", "at0009");
			//Katera trditev velja za hujšanje: 	
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!06.v1]?data[at0001]?items[at0004]?items[at0007]?value", "at0012");
			//Katera trditev drži?	
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!06.v1]?data[at0001]?items[at0004]?items[at0015]?value", "at0019");
			//Katera trditev drži za uravnoteženo vadbo?	
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!06.v1]?data[at0001]?items[at0004]?items[at0016]?value", "at0022");
			//Katera trditev velja za uravnavanje telesne mase?	
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!06.v1]?data[at0001]?items[at0004]?items[at0017]?value", "at0026");
			
			return variables;
		}
		else if (step == 10) {
			// <------Korak 10-------->

			//Kakšno mleko in mlečne izdelke je bolje izbrati?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!10.v1]?data[at0001]?items[at0003]?items[at0006]?value", "at0010");
			//Katera živila so bogat vir beljakovin?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!10.v1]?data[at0001]?items[at0003]?items[at0007]?value", "at0012");
			//Zakaj je priporočeno uživati mastne morske ribe, meso in mesni izdelki pa morajo biti čim manj mastni?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!10.v1]?data[at0001]?items[at0003]?items[at0008]?value", "at0013");
			
			return variables;
		}		
		else if (step == 11) {
			// <------Korak 11-------->

			//Kuhanje v majhni količini vode, v vodni kopeli, v sopari in v aluminijasti foliji
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0006]?value", "TRUE");
			//Cvrenje
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0007]?value", "FALSE");
			//Kuhanje pod zvišanim pritiskom
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0008]?value", "TRUE");
			//Dušenje v lastnem soku ter z malo maščob in vode
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0009]?value", "TRUE");
			//Kuhanje z veliko soli
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0010]?value", "FALSE");
			//Pečenje v ponvi, pečici, v foliji za pečenje in v aluminijasti foliji
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0011]?value", "TRUE");
			//Pečenje v pečici na vroči zrak
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0012]?value", "TRUE");
			//Kuhanje z maslom in smetano
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!11.v1]?data[at0001]?items[at0003]?items[at0013]?value", "TRUE");
			
			return variables;		
		}		
		else if (step == 12) {
			// <------Korak 11-------->

			//Koliko gramov prehranske vlaknine mora vsebovati izdelek ali servirna porcija, da nam zagotovi bogat vir prehranske vlaknine in je navedena na deklaraciji?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!12.v1]?data[at0001]?items[at0003]?items[at0006]?value", "at0009");
			//Katera izbira je bolj zdrava?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!12.v1]?data[at0001]?items[at0003]?items[at0007]?value", "at0011");
			
			return variables;			
		}		
		else if (step == 13) {
			// <------Korak 13-------->

			//Za izgubo telesna mase je najbolje, če:
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!13.v1]?data[at0001]?items[at0003]?items[at0006]?value", "at0008");
			//Ali je vegeterjanska prehrana, ki vključuje mleko, mlečne izdelke in jajca zdrava, če je skrbno načrtovana?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!13.v1]?data[at0001]?items[at0003]?items[at0007]?value", "at0012");
			
			return variables;
		}
		else if (step == 14) {
			// <------Korak 14-------->

			//Ali je ob uravnoteženi prehrani priporočeno uživati tudi preparate za hujšanje?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!14.v1]?data[at0001]?items[at0005]?items[at0003]?value", "at0008");
			
			return variables;
		}
		else if (step == 15) {
			// <------Korak 15-------->

			//Ali imajo zaščitne snovi (vitamini, minerali...) v prehranskih dopolnilih enak učinek v preventivi pred boleznimi kot te snovi iz hrane?
			variables.put("?items[openEHR-EHR-EVALUATION.hu!koraki!15.v1]?data[at0001]?items[at0003]?items[at0006]?value", "TRUE");
			
			return variables;
		}			
		
		return null;

	}

	public Map<String, Object> getPatientsEnteredValuesOk(Map<String, Object> variables) {

		// teza
		variables.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value", "88");

		// obseg pasu
		variables.put("?items[openEHR-EHR-OBSERVATION.obseg!pasu!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "110");

		// <<<----telesna dejavnost---->>>>>>

		// datum vadbe
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0020]?items[at0043]?value",
				"01.07.1980");
		// sportna panoga
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0020]?items[at0004]?value",
				"at0045");
		// intenzivnost vadbe
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0020]?items[at0012]?value",
				"at0014");
		// trajanje vadbe
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0020]?items[at0006]?value",
				"00:20");
		// komentar
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0020]?items[at0018]?value",
				"komentar dif");
		// Kolikokrat na teden ste bili v zadnjih 14 dnevih gibalno aktivni (30
		// min zmerne ali visoke intenzivnosti)?
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0023]?value", "at0027");

		return variables;
	}

	public Map<String, Object> getPatientsEnteredValuesNotOk(Map<String, Object> variables) {

		int weight = 93 + Integer.parseInt((String) variables.get(Constants.VAR_PROCESS_STEP));
		
		// teza
		variables.put("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value", weight + "");

		// obseg pasu
		variables.put("?items[openEHR-EHR-OBSERVATION.obseg!pasu!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "110");

		// <<<----telesna dejavnost---->>>>>>

		// datum vadbe
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0020]?items[at0043]?value",
				"01.07.1980");
		// sportna panoga
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0020]?items[at0004]?value",
				"at0045");
		// intenzivnost vadbe
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0020]?items[at0012]?value",
				"at0014");
		// trajanje vadbe
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0020]?items[at0006]?value",
				"00:20");
		// komentar
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0020]?items[at0018]?value",
				"komentar dif");
		// Kolikokrat na teden ste bili v zadnjih 14 dnevih gibalno aktivni (30
		// min zmerne ali visoke intenzivnosti)?
		variables.put("?items[openEHR-EHR-OBSERVATION.telesna!dejavnost!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0023]?value", "at0027");

		return variables;
	}
	
	/**
	 * Delay for SMTP server (too many requests error might occur).
	 */
//	@Before
	public void runBeforeEachTest()
	{
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void assertInActivity(String processInstanceId, String activityId) {
		List<String> activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
		assertTrue("Current activities (" + activeActivityIds + ") does not contain " + activityId, activeActivityIds.contains(activityId));
	}

	/**
	 * Pozitiven scenarij vsi koraki - pacient pravilno odgovori
	 */
	@Deployment(resources = { "diagrams/Hujsanje-Proces-Opomnik-Korak.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			                  "diagrams/REMINDER-Pacient.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient.bpmn20.xml", 
			                  "diagrams/VnosMeritev7.bpmn20.xml" })
	public void  testWeightOkAnswersOkAllSteps() {  
 
		for (int i = 1; i <= Constants.MAX_STEP; i++) {
			runBeforeEachTest();
			
			Map<String, Object> variables = new HashMap<String, Object>();

			// get user information
			variables = getTestSubject(variables, i);

			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Hujsanje-Proces-Opomnik-Korak", variables);
			
			String id = processInstance.getId();
			log.info("Started process instance id " + id);
			
			Boolean[] visitArray = (Boolean[]) runtimeService.getVariable(id, Constants.VAR_SUBTASK_VISIT_ARRAY);

			System.out.println("Started process instance id " + id);
			assertInActivity(id, "CallActivity-VnosVrednostiPacienta7");

			Task task = null; 
					
			if (visitArray[0]) {
				// 1. inspect first task name - introduction
				task = taskService.createTaskQuery().singleResult();
				assertNotNull("Task shouldn't be null!", task);
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO + " " + i, task.getName().trim());

				// 1.1 claim & complete
				taskService.complete(task.getId());
			}
			
			if (visitArray[1]) {
				//2. inspect second task - OPKP
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SECOND_SUBTASK_INFO + " " + i, task.getName().trim());

				//2.1 claim & complete
				taskService.complete(task.getId());
			}
			
			if (visitArray[2]) {
				//3. inspect third task - patient enters masurements
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_THIRD_SUBTASK_INFO + " " + i, task.getName().trim());

				//3. claim & complete
				variables = getPatientsEnteredValuesOk(variables);
				taskService.complete(task.getId(), variables);
			}
			
			if (visitArray[2]) {
				// 3.1 notify patient - weight loss OK!!
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_TASK_WEIGHT_NORMAL, task.getName().trim());
				taskService.complete(task.getId());
			}
			
			if (visitArray[3]) {
				// 4. inspect fourth task - read content
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FOURTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 4. claim & complete (with correct answers)
				taskService.complete(task.getId());
			}
			
			if (visitArray[4]) {
				// 5. inspect fifth task - questionary
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIFTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 5. claim & complete 
				taskService.complete(task.getId());
			}
			
			
			if (visitArray[5]) {
				// 6. inspect sixth task - questionary
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SIXTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 6. claim & complete 
				variables = getProcessVarsOk(variables, i);	
				taskService.complete(task.getId(), variables);
			}

			if (visitArray[6]) {
				// 7. inspect fifth task - keep up the good work & weekly advice
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SEVENTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 7. claim & complete (with correct answers)
				taskService.complete(task.getId(), variables);
			}

			assertProcessEnded(id);

			HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
			assertNotNull(historicProcessInstance);
		}
	}

	/**
	 * Negativen scenarij vsi koraki - pacient v prvo ne odgovori pravilno
	 */
	@Deployment(resources = { "diagrams/Hujsanje-Proces-Opomnik-Korak.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			                  "diagrams/REMINDER-Pacient.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient.bpmn20.xml", 
			                  "diagrams/VnosMeritev7.bpmn20.xml" })
	public void testWeightOkAnswersNotOkAllSteps() {

		for (int i = 1; i <= Constants.MAX_STEP; i++) {
			if (i == 7 || i == 8 ||	i == 9 || i == 16) { //wrong anwsers are not possible in this steps
				continue;
			}
			runBeforeEachTest();
			
			Map<String, Object> variables = new HashMap<String, Object>();

			// get user information
			variables = getTestSubject(variables, i);

			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Hujsanje-Proces-Opomnik-Korak", variables);
			variables.remove(Constants.VAR_PATIENT_OBJ);

			String id = processInstance.getId();
			log.info("Started process instance id " + id);
			
			Boolean[] visitArray = (Boolean[]) runtimeService.getVariable(id, Constants.VAR_SUBTASK_VISIT_ARRAY);

			System.out.println("Started process instance id " + id);
			assertInActivity(id, "CallActivity-VnosVrednostiPacienta7");

			Task task = null;
			
			if (visitArray[0]) {
				// 1. inspect first task name - introduction
				task = taskService.createTaskQuery().singleResult();
				assertNotNull("Task shouldn't be null!", task);
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO + " " + i, task.getName().trim());

				// 1.1 claim & complete
				taskService.complete(task.getId());
			}

			if (visitArray[1]) {
				// 2. inspect second task - OPKP
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SECOND_SUBTASK_INFO + " " + i, task.getName().trim());

				//2.1 claim & complete
				taskService.complete(task.getId());
			}

			if (visitArray[2]) {
				// 3. inspect third task - patient enters masurements
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_THIRD_SUBTASK_INFO + " " + i, task.getName().trim());

				//3.1 claim & complete
				variables = getPatientsEnteredValuesOk(variables);
				taskService.complete(task.getId(), variables);
			}
			
			if (visitArray[2]) {
				// 3.2. notify patient - weight loss OK!!
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_TASK_WEIGHT_NORMAL, task.getName().trim());
				taskService.complete(task.getId());
			}
				
			if (visitArray[3]) {
				// 4. inspect fourth task - patient enters questioneer
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FOURTH_SUBTASK_INFO + " " + i, task.getName().trim());
			

				// 4. claim & complete (with incorrect answers)
				taskService.complete(task.getId());
			}
			
			
			if (visitArray[4]) {
				// 5. inspect fifth task - content
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIFTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 5.1 claim & complete (with incorrect answers)
				taskService.complete(task.getId(), variables);
			}
			
			if (visitArray[5]) {
				// 6. inspect fifth task - keep up the good work & weekly advice
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SIXTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 6.1 claim & complete (with incorrect answers)
				variables = getProcessVarsNotOk(variables, i);
				taskService.complete(task.getId(), variables);
			
				// 6.R inspect fifth task - WRONG answers!
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_WRONG_ANSWER_SUBTASK_INFO, task.getName().trim());

				// 6.R.1 claim & complete (with correct answers)
				taskService.complete(task.getId(), variables);
			}
			
			if (visitArray[3]) {
				// 7. inspect fourth task - questionary
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FOURTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 7.1 claim & complete (with correct answers)
				variables = getProcessVarsOk(variables, i);
				taskService.complete(task.getId(), variables);
			}
			
			if (visitArray[5]) {
				// 8. inspect sixth task - keep up the good work & weekly advice
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SIXTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 8.1 claim & complete (with correct answers)
				taskService.complete(task.getId(), variables);
			}
			
			if (visitArray[6]) {
				// 9. inspect fifth task - keep up the good work & weekly advice
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SEVENTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 9. claim & complete (with correct answers)
				taskService.complete(task.getId(), variables);
			}

			assertProcessEnded(id);

			HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
			assertNotNull(historicProcessInstance);
		}

	}
	
	/**
	 * Teza neustrezna:vsi koraki - pacient pravilno odgovori
	 */
	@Deployment(resources = { "diagrams/Hujsanje-Proces-Opomnik-Korak.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			                  "diagrams/REMINDER-Pacient.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient.bpmn20.xml", 
			                  "diagrams/VnosMeritev7.bpmn20.xml",
			                  "diagrams/NOTIFIER-CM-Pacient.bpmn20.xml",
			                  "diagrams/NOTIFIER-CM-Ext.bpmn20.xml",
			                  "diagrams/NOTIFIER-CM.bpmn20.xml"})
	public void testWeightNotOkAnswersOkAllSteps() {

		for (int i = 1; i <= Constants.MAX_STEP; i++) {
			runBeforeEachTest();
			
			Map<String, Object> variables = new HashMap<String, Object>();

			// get user information
			variables = getTestSubject(variables, i);

			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Hujsanje-Proces-Opomnik-Korak", variables);
			variables.remove(Constants.VAR_PATIENT_OBJ); // we only need this at the start of the process

			String id = processInstance.getId();
			log.info("Started process instance id " + id);
			
			Boolean[] visitArray = (Boolean[]) runtimeService.getVariable(id, Constants.VAR_SUBTASK_VISIT_ARRAY);

			System.out.println("Started process instance id " + id);
			assertInActivity(id, "CallActivity-VnosVrednostiPacienta7");

			
			Task task = null;
			
			if (visitArray[0]) {
				// 1. inspect first task name - introduction
				task = taskService.createTaskQuery().singleResult();
				assertNotNull("Task shouldn't be null!", task);
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO + " " + i, task.getName().trim());

				// 1.1 claim & complete
				taskService.complete(task.getId());
			}

			if (visitArray[1]) {
				// 2. inspect second task - OPKP
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SECOND_SUBTASK_INFO + " " + i, task.getName().trim());

				//2.1 claim & complete
				taskService.complete(task.getId());
			}

			if (visitArray[2]) {
				// 3. inspect third task - patient enters masurements
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_THIRD_SUBTASK_INFO + " " + i, task.getName().trim());

				//3. claim & complete
				variables = getPatientsEnteredValuesNotOk(variables);
				taskService.complete(task.getId(), variables);
			}
			
			if (visitArray[2]) {
				// 3.1 notify patient - weight loss NOT OK!!
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_TASK_WEIGHT_NOT_OK, task.getName().trim());
				taskService.complete(task.getId());
			}
			
			if (visitArray[3]) {
				// 4. inspect fourth task - read content
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FOURTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 4. claim & complete (with correct answers)
				taskService.complete(task.getId());
			}
			
			if (visitArray[4]) {
				// 5. inspect fifth task - questionary
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIFTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 5. claim & complete 
				taskService.complete(task.getId());
			}

			if (visitArray[5]) {
				// 6. inspect sixth task - questionary
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SIXTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 6. claim & complete 
				variables = getProcessVarsOk(variables, i);
				taskService.complete(task.getId(), variables);
			}

			if (visitArray[6]) {
				// 7. inspect seventh task - keep up the good work & weekly advice
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SEVENTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 7. claim & complete (with correct answers)
				taskService.complete(task.getId(), variables);
			}
			
			// 8. inspect task for caremanager (weight not OK)
			task = taskService.createTaskQuery().singleResult();
			assertEquals("Wrong task name!", "Bolnik Pacient Hujsanje ima spremenjeno telesno težo", task.getName().trim());
				
			//8.1 claim & complete
			taskService.complete(task.getId());

			assertProcessEnded(id);

			HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
			assertNotNull(historicProcessInstance);
		}
	}
	
	
	/**
	 * Teza OK, OPKP ni OK :vsi koraki - pacient pravilno odgovori
	 */
	@Deployment(resources = { "diagrams/Hujsanje-Proces-Opomnik-Korak.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			                  "diagrams/REMINDER-Pacient.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient.bpmn20.xml", 
			                  "diagrams/VnosMeritev7.bpmn20.xml",
			                  "diagrams/NOTIFIER-CM-Pacient.bpmn20.xml",
			                  "diagrams/NOTIFIER-CM-Ext.bpmn20.xml",
			                  "diagrams/NOTIFIER-CM.bpmn20.xml"})
	public void testWeightOkOpkpNokAnswersOkAllSteps() {

		for (int i = 1; i <= Constants.MAX_STEP; i++) {
			runBeforeEachTest();
			
			Map<String, Object> variables = new HashMap<String, Object>();

			// get user information
			variables = getTestSubject(variables, i);

			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Hujsanje-Proces-Opomnik-Korak", variables);
			variables.remove(Constants.VAR_PATIENT_OBJ); // we only need this at the start of the process

			String id = processInstance.getId();
			log.info("Started process instance id " + id);
			
			Boolean[] visitArray = (Boolean[]) runtimeService.getVariable(id, Constants.VAR_SUBTASK_VISIT_ARRAY);

			System.out.println("Started process instance id " + id);
			assertInActivity(id, "CallActivity-VnosVrednostiPacienta7");

			
			Task task = null;
			
			if (visitArray[0]) {
				// 1. inspect first task name - introduction
				task = taskService.createTaskQuery().singleResult();
				assertNotNull("Task shouldn't be null!", task);
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO + " " + i, task.getName().trim());

				// 1.1 claim & complete
				taskService.complete(task.getId());
			}

			if (visitArray[1]) {
				// 2. inspect second task - OPKP
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SECOND_SUBTASK_INFO + " " + i, task.getName().trim());

				//2.1 claim & complete
				taskService.complete(task.getId());
			}

			if (visitArray[2]) {
				// 3. inspect third task - patient enters masurements
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_THIRD_SUBTASK_INFO + " " + i, task.getName().trim());

				//3. claim & complete
				variables = getPatientsEnteredValuesOk(variables);
				taskService.complete(task.getId(), variables);
			}
			
			if (visitArray[2]) {
				// 3.1 notify patient - weight loss NOT OK!!
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_TASK_WEIGHT_OPKP_NOK, task.getName().trim());
				taskService.complete(task.getId());
			}
			
			if (visitArray[3]) {
				// 4. inspect fourth task - read content
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FOURTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 4. claim & complete (with correct answers)
				taskService.complete(task.getId());
			}
			
			if (visitArray[4]) {
				// 5. inspect fifth task - questionary
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIFTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 5. claim & complete 
				taskService.complete(task.getId());
			}

			if (visitArray[5]) {
				// 6. inspect sixth task - questionary
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SIXTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 6. claim & complete 
				variables = getProcessVarsOk(variables, i);
				taskService.complete(task.getId(), variables);
			}

			if (visitArray[6]) {
				// 7. inspect seventh task - keep up the good work & weekly advice
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SEVENTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 7. claim & complete (with correct answers)
				taskService.complete(task.getId(), variables);
			}
			
			// 8. inspect task for caremanager (weight not OK)
			task = taskService.createTaskQuery().singleResult();
			assertEquals("Wrong task name!", "Neustrezen vnos živil pri bolniku Pacient Hujsanje", task.getName().trim());
				
			//8.1 claim & complete
			taskService.complete(task.getId());

			assertProcessEnded(id);

			HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
			assertNotNull(historicProcessInstance);
		}
	}	
	
	
	/**
	 * Teza neustrezna && OPKP ni OK :vsi koraki - pacient pravilno odgovori
	 */
	@Deployment(resources = { "diagrams/Hujsanje-Proces-Opomnik-Korak.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			                  "diagrams/REMINDER-Pacient.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient.bpmn20.xml", 
			                  "diagrams/VnosMeritev7.bpmn20.xml",
			                  "diagrams/NOTIFIER-CM-Pacient.bpmn20.xml",
			                  "diagrams/NOTIFIER-CM-Ext.bpmn20.xml",
			                  "diagrams/NOTIFIER-CM.bpmn20.xml"})
	public void testWeightNotOkOpkpNokAnswersOkAllSteps() {

		for (int i = 1; i <= Constants.MAX_STEP; i++) {
			runBeforeEachTest();
			
			Map<String, Object> variables = new HashMap<String, Object>();

			// get user information
			variables = getTestSubject(variables, i);

			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Hujsanje-Proces-Opomnik-Korak", variables);
			variables.remove(Constants.VAR_PATIENT_OBJ); // we only need this at the start of the process

			String id = processInstance.getId();
			log.info("Started process instance id " + id);
			
			Boolean[] visitArray = (Boolean[]) runtimeService.getVariable(id, Constants.VAR_SUBTASK_VISIT_ARRAY);

			System.out.println("Started process instance id " + id);
			assertInActivity(id, "CallActivity-VnosVrednostiPacienta7");

			
			Task task = null;
			
			if (visitArray[0]) {
				// 1. inspect first task name - introduction
				task = taskService.createTaskQuery().singleResult();
				assertNotNull("Task shouldn't be null!", task);
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO + " " + i, task.getName().trim());

				// 1.1 claim & complete
				taskService.complete(task.getId());
			}

			if (visitArray[1]) {
				// 2. inspect second task - OPKP
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SECOND_SUBTASK_INFO + " " + i, task.getName().trim());

				//2.1 claim & complete
				taskService.complete(task.getId());
			}

			if (visitArray[2]) {
				// 3. inspect third task - patient enters masurements
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_THIRD_SUBTASK_INFO + " " + i, task.getName().trim());

				//3. claim & complete
				variables = getPatientsEnteredValuesNotOk(variables);
				taskService.complete(task.getId(), variables);
			}
			
			if (visitArray[2]) {
				// 3.1 notify patient - weight loss NOT OK!!
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_TASK_WEIGHT_NOT_OK_OPKP_NOK_RESULTS, task.getName().trim());
				taskService.complete(task.getId());
			}
			
			if (visitArray[3]) {
				// 4. inspect fourth task - read content
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FOURTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 4. claim & complete (with correct answers)
				taskService.complete(task.getId());
			}
			
			if (visitArray[4]) {
				// 5. inspect fifth task - questionary
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIFTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 5. claim & complete 
				taskService.complete(task.getId());
			}

			if (visitArray[5]) {
				// 6. inspect sixth task - questionary
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SIXTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 6. claim & complete 
				variables = getProcessVarsOk(variables, i);
				taskService.complete(task.getId(), variables);
			}

			if (visitArray[6]) {
				// 7. inspect seventh task - keep up the good work & weekly advice
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SEVENTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 7. claim & complete (with correct answers)
				taskService.complete(task.getId(), variables);
			}
			
			// 8. inspect task for caremanager (weight not OK)
			task = taskService.createTaskQuery().singleResult();
			assertEquals("Wrong task name!", "Bolnik Pacient Hujsanje ima spremenjeno telesno težo in neustrezen vnos živil", task.getName().trim());
				
			//8.1 claim & complete
			taskService.complete(task.getId());

			assertProcessEnded(id);

			HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
			assertNotNull(historicProcessInstance);
		}
	}
	
	
	/**
	 * Teza neustrezna:vsi koraki - pacient narobe odgovori
	 */
	@Deployment(resources = { "diagrams/Hujsanje-Proces-Opomnik-Korak.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			                  "diagrams/REMINDER-Pacient.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient.bpmn20.xml", 
			                  "diagrams/VnosMeritev7.bpmn20.xml",
			                  "diagrams/NOTIFIER-CM-Pacient.bpmn20.xml",
			                  "diagrams/NOTIFIER-CM-Ext.bpmn20.xml",
			                  "diagrams/NOTIFIER-CM.bpmn20.xml"})
	public void testWeightNotOkAnswersNotOkAllSteps() {

		for (int i = 1; i <= Constants.MAX_STEP; i++) {
			if (i == 7 || i == 8 ||	i == 9 || i == 16) { //wrong anwsers are not possible in this steps
				continue;
			}			
			runBeforeEachTest();
			
			Map<String, Object> variables = new HashMap<String, Object>();

			// get user information
			variables = getTestSubject(variables, i);

			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Hujsanje-Proces-Opomnik-Korak", variables);
			variables.remove(Constants.VAR_PATIENT_OBJ); // we only need this at the start of the process

			String id = processInstance.getId();
			log.info("Started process instance id " + id);
			
			Boolean[] visitArray = (Boolean[]) runtimeService.getVariable(id, Constants.VAR_SUBTASK_VISIT_ARRAY);

			System.out.println("Started process instance id " + id);
			assertInActivity(id, "CallActivity-VnosVrednostiPacienta7");

			Task task = null;
			
			if (visitArray[0]) {
				// 1. inspect first task name - introduction
				task = taskService.createTaskQuery().singleResult();
				assertNotNull("Task shouldn't be null!", task);
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO + " " + i, task.getName().trim());

				// 1.1 claim & complete
				taskService.complete(task.getId());
			}

			if (visitArray[1]) {
				// 2. inspect second task - OPKP
				task = taskService.createTaskQuery().singleResult();	
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SECOND_SUBTASK_INFO + " " + i, task.getName().trim());

				//2.1 claim & complete
				taskService.complete(task.getId());
			}

			if (visitArray[2]) {
				// 3. inspect third task - patient enters masurements
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_THIRD_SUBTASK_INFO + " " + i, task.getName().trim());

				//3.1 claim & complete
				variables = getPatientsEnteredValuesNotOk(variables);
				taskService.complete(task.getId(), variables);
			
			
				// 3.2. notify patient - weight loss not OK!!
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_TASK_WEIGHT_NOT_OK_OPKP_RESULTS, task.getName().trim());
				taskService.complete(task.getId());
			}

			if (visitArray[3]) {
				// 4. inspect fourth task - content
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FOURTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 4. claim & complete
				taskService.complete(task.getId());
			}

			if (visitArray[4]) {
				// 5. inspect fifth task - working sheets
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIFTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 5.1 claim & complete (working sheets)
				taskService.complete(task.getId(), variables);
			}

			if (visitArray[5]) {
				// 6. inspect sixth task - questioneer
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SIXTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 6.1 claim & complete (with incorrect answers)
				variables = getProcessVarsNotOk(variables, i);
				taskService.complete(task.getId(), variables);
			
			
				// 6.R inspect sixth R task - WRONG answers!
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_WRONG_ANSWER_SUBTASK_INFO, task.getName().trim());

				// 6.R.1 claim & complete (with correct answers)
				taskService.complete(task.getId(), variables);
			}
			
			if (visitArray[3]) {
				// 7. inspect fourth task - questionary
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FOURTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 7.1 claim & complete (with correct answers)
				variables = getProcessVarsOk(variables, i);
				taskService.complete(task.getId(), variables);
			}
			
			if (visitArray[5]) {
				// 8. inspect fifth task - keep up the good work & weekly advice
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SIXTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 8.1 claim & complete (with correct answers)
				taskService.complete(task.getId(), variables);
			}
			
			if (visitArray[6]) {
				// 9. inspect fifth task - keep up the good work & weekly advice
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SEVENTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 9. claim & complete (with correct answers)
				taskService.complete(task.getId(), variables);
			}
			
			// 10. inspect 'weight not ok' task
			task = taskService.createTaskQuery().singleResult();
			assertEquals("Wrong task name!", "Bolnik Pacient Hujsanje ima spremenjeno telesno težo/neustrezen vnos živil", task.getName().trim());

			// 10. claim & complete (with correct answers)
			taskService.complete(task.getId(), variables);

			assertProcessEnded(id);

			HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
			assertNotNull(historicProcessInstance);
		}
	}
	
	
	/**
	 * Pozitiven scenarij: vsi koraki - pacient pravilno odgovori, pacient 3x ne odgovori in nadaljuje s sodelovanjem. Predpostavljamo, da je na vsakem koraku vsaj prvi subtask (uvod v korak).
	 */
	@Deployment(resources = { "diagrams/Hujsanje-Proces-Opomnik-Korak.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			                  "diagrams/REMINDER-Pacient.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient.bpmn20.xml", 
			                  "diagrams/VnosMeritev7.bpmn20.xml", "diagrams/NOTIFIER-CM-Ext.bpmn20.xml", "diagrams/NOTIFIER-CM.bpmn20.xml" })
	public void testPatientDoesntRespondinTreeDaysAndContinues() {

		for (int i = 1; i <= Constants.MAX_STEP; i++) {
			runBeforeEachTest();
			
			Map<String, Object> variables = new HashMap<String, Object>();

			// get user information
			variables = getTestSubject(variables, i);

			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Hujsanje-Proces-Opomnik-Korak", variables);
			variables.remove(Constants.VAR_PATIENT_OBJ); // we only need this at the start of the process

			String id = processInstance.getId();
			log.info("Started process instance id " + id);
			
			Boolean[] visitArray = (Boolean[]) runtimeService.getVariable(id, Constants.VAR_SUBTASK_VISIT_ARRAY);

			System.out.println("Started process instance id " + id);
			assertInActivity(id, "CallActivity-VnosVrednostiPacienta7");

			Task task = null;
			
			// A.1. inspect first task name - introduction
			task = taskService.createTaskQuery().singleResult();
			assertNotNull("Task shouldn't be null!", task);	
			assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO + " " + i, task.getName().trim());
	
			
		    //A. 1.1 Manually execute the job - 1.pass
			Job timer = managementService.createJobQuery().singleResult();
		    managementService.executeJob(timer.getId());
		    
			// A.2. inspect first task name - introduction
			task = taskService.createTaskQuery().singleResult();
			assertNotNull("Task shouldn't be null!", task);
			assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO + " " + i, task.getName().trim());
			
		    //A.2.1 Manually execute the job - 1.pass
			timer = managementService.createJobQuery().singleResult();
		    managementService.executeJob(timer.getId());		    
		    
			// A.3. inspect first task name - introduction
			task = taskService.createTaskQuery().singleResult();
			assertNotNull("Task shouldn't be null!", task);
			assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO + " " + i, task.getName().trim());
			
		    //A.3.1 Manually execute the job - 1.pass
			timer = managementService.createJobQuery().singleResult();
		    managementService.executeJob(timer.getId());		
		    
		    //4. inspect task name (Obvesti_CM_o_nevnosu_vrednosti)
		    task = taskService.createTaskQuery().singleResult();
			assertNotNull("Task shouldn't be null!", task);
			assertEquals("Wrong task name!", "Pacient Hujsanje " + MessageRepo.HUJSANJE_PATIENT_KORAKI_DOESNT_RESPOND, task.getName().trim());
			variables.put(Constants.VAR_PATIENT_COOPERATES, "true");
			variables.put("?description[at0001]?items[at0002]?value", "dopust"); //razlog nesodelovanja
			taskService.complete(task.getId(), variables);	  
			
			//-------
			
			// 1. inspect first task name - introduction
			task = taskService.createTaskQuery().singleResult();
			assertNotNull("Task shouldn't be null!", task);
			assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO + " " + i, task.getName().trim());

			// 1.1 claim & complete
			taskService.complete(task.getId());

			if (visitArray[1]) {
				// 2. inspect second task - OPKP
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SECOND_SUBTASK_INFO + " " + i, task.getName().trim());

				//2.1 claim & complete
				taskService.complete(task.getId());
			}

			if (visitArray[2]) {
				// 3. inspect third task - patient enters masurements
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_THIRD_SUBTASK_INFO + " " + i, task.getName().trim());

				//3. claim & complete
				variables = getPatientsEnteredValuesOk(variables);
				taskService.complete(task.getId(), variables);
			
			
				// 3.1 notify patient - weight loss OK!!
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_TASK_WEIGHT_NORMAL, task.getName().trim());
				taskService.complete(task.getId());
			}
			
			if (visitArray[3]) {
				// 4. inspect fourth task - read content
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FOURTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 4. claim & complete (with correct answers)
				taskService.complete(task.getId());
			}
			
			if (visitArray[4]) {
				// 5. inspect fifth task - questionary
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIFTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 5. claim & complete 
				taskService.complete(task.getId());
			}

			if (visitArray[5]) {
				// 6. inspect sixth task - questionary
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SIXTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 6. claim & complete 
				variables = getProcessVarsOk(variables, i);	
				taskService.complete(task.getId(), variables);
			}

			if (visitArray[6]) {
				// 7. inspect fifth task - keep up the good work & weekly advice
				task = taskService.createTaskQuery().singleResult();
				assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_SEVENTH_SUBTASK_INFO + " " + i, task.getName().trim());

				// 7. claim & complete (with correct answers)
				taskService.complete(task.getId(), variables);
			}

			assertProcessEnded(id);

			HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
			assertNotNull(historicProcessInstance);
		}
	}
	
	
	/**
	 * Pozitiven scenarij: vsi koraki - pacient pravilno odgovori, pacient 3x ne odgovori in NE nadaljuje s sodelovanjem. Predpostavljamo, da je na vsakem koraku vsaj prvi subtask (uvod v korak).
	 */
	@Deployment(resources = { "diagrams/Hujsanje-Proces-Opomnik-Korak.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient-Ext.bpmn20.xml",
			                  "diagrams/REMINDER-Pacient.bpmn20.xml", 
			                  "diagrams/NOTIFIER-Pacient.bpmn20.xml", 
			                  "diagrams/VnosMeritev7.bpmn20.xml", 
			                  "diagrams/NOTIFIER-CM-Ext.bpmn20.xml", 
			                  "diagrams/NOTIFIER-CM.bpmn20.xml" })
	public void testPatientDoesntRespondinTreeDaysAndStops() {

		for (int i = 1; i <= Constants.MAX_STEP; i++) {
			runBeforeEachTest();
			
			Map<String, Object> variables = new HashMap<String, Object>();

			// get user information
			variables = getTestSubject(variables, i);

			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Hujsanje-Proces-Opomnik-Korak", variables);
			variables.remove(Constants.VAR_PATIENT_OBJ); // we only need this at the start of the process

			String id = processInstance.getId();
			log.info("Started process instance id " + id);

			System.out.println("Started process instance id " + id);
			assertInActivity(id, "CallActivity-VnosVrednostiPacienta7");
			
			Boolean[] visitArray = (Boolean[]) runtimeService.getVariable(id, Constants.VAR_SUBTASK_VISIT_ARRAY);

			Task task = null;
			
			// A.1. inspect first task name - introduction
			task = taskService.createTaskQuery().singleResult();
			assertNotNull("Task shouldn't be null!", task);
			assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO + " " + i, task.getName().trim());
			
		    //A. 1.1 Manually execute the job - 1.pass
			Job timer = managementService.createJobQuery().singleResult();
		    managementService.executeJob(timer.getId());
		    
			// A.2. inspect first task name - introduction
			task = taskService.createTaskQuery().singleResult();
			assertNotNull("Task shouldn't be null!", task);
			assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO + " " + i, task.getName().trim());
			
		    //A.2.1 Manually execute the job - 1.pass
			timer = managementService.createJobQuery().singleResult();
		    managementService.executeJob(timer.getId());		    
		    
			// A.3. inspect first task name - introduction
			task = taskService.createTaskQuery().singleResult();
			assertNotNull("Task shouldn't be null!", task);
			assertEquals("Wrong task name!", MessageRepo.HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO + " " + i, task.getName().trim());
			
		    //A.3.1 Manually execute the job - 1.pass
			timer = managementService.createJobQuery().singleResult();
		    managementService.executeJob(timer.getId());		
		    
		    //4. inspect task name (Obvesti_CM_o_nevnosu_vrednosti_PEF)
		    task = taskService.createTaskQuery().singleResult();
			assertNotNull("Task shouldn't be null!", task);
			assertEquals("Wrong task name!", "Pacient Hujsanje " + MessageRepo.HUJSANJE_PATIENT_KORAKI_DOESNT_RESPOND, task.getName().trim());
			variables.put(Constants.VAR_PATIENT_COOPERATES, "false");
			variables.put("?description[at0001]?items[at0002]?value", "Konec je"); //razlog nesodelovanja
			taskService.complete(task.getId(), variables);	  
		    
			assertProcessEnded(id);

			HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
			assertNotNull(historicProcessInstance);
		}
	}

}
