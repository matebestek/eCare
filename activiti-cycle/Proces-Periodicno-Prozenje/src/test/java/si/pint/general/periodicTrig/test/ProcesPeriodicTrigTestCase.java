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
package si.pint.general.periodicTrig.test;

import java.util.HashMap;
import java.util.List;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;

public class ProcesPeriodicTrigTestCase extends ActivitiTestCase {

	public void assertInActivity(String processInstanceId, String activityId) {
		List<String> activeActivityIds = runtimeService.getActiveActivityIds(processInstanceId);
		assertTrue("Current activities (" + activeActivityIds + ") does not contain " + activityId, activeActivityIds.contains(activityId));
	}

//	@Deployment(resources = {"diagrams/Proces-Periodicno-Prozenje-ACT.bpmn20.xml"})
//	public void testPeriodicTrigStartAct() {
//		HashMap<String, Object> variables = new HashMap<String, Object>();
//
//	
//		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ProcesPeriodicnoProzenjeACT", variables);
//		String id = processInstance.getId();
//		System.out.println("Started process instance id " + id);
//		    		
//		//runtimeService.signal(id); (za to rabimo waitState?)
//		assertProcessEnded(id);
//
//		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
//		assertNotNull(historicProcessInstance);
//
//		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
//    }
//	
//	@Deployment(resources = {"diagrams/Proces-Periodicno-Prozenje-PEF.bpmn20.xml"})
//	public void testPeriodicTrigStartPef() {
//		HashMap<String, Object> variables = new HashMap<String, Object>();
//
//	
//		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ProcesPeriodicnoProzenjePEF", variables);
//		String id = processInstance.getId();
//		System.out.println("Started process instance id " + id);
//		    		
//		//runtimeService.signal(id); (za to rabimo waitState?)
//		assertProcessEnded(id);
//
//		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
//		assertNotNull(historicProcessInstance);
//
//		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
//    }	
//	
//	@Deployment(resources = {"diagrams/Proces-Periodicno-Prozenje-Zdravila.bpmn20.xml"})
//	public void testPeriodicTrigStartZdravila() {
//		HashMap<String, Object> variables = new HashMap<String, Object>();
//
//	
//		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ProcesPeriodicnoProzenjeZdravila", variables);
//		String id = processInstance.getId();
//		System.out.println("Started process instance id " + id);
//		    		
//		//runtimeService.signal(id); (za to rabimo waitState?)
//		assertProcessEnded(id);
//
//		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
//		assertNotNull(historicProcessInstance);
//
//		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
//    }
//	
//	@Deployment(resources = {"diagrams/Diabetes-Proces-Periodicno-Prozenje-Opomnik-Redni-Pregled.bpmn20.xml"})
//	public void testDiabetesPeriodicTrigStartOpomnikRedniPregled() {
//		HashMap<String, Object> variables = new HashMap<String, Object>();
//
//	
//		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("DiabetesProcesPeriodicnoProzenjeOpomnikRedniPregled", variables);
//		String id = processInstance.getId();
//		System.out.println("Started process instance id " + id);
//		    		
//		//runtimeService.signal(id); (za to rabimo waitState?)
//		assertProcessEnded(id);
//
//		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
//		assertNotNull(historicProcessInstance);
//
//		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
//    }
//	
//	@Deployment(resources = {"diagrams/Diabetes-Proces-Periodicno-Prozenje-Opomnik-Vnos-Vrednosti.bpmn20.xml"})
//	public void testDiabetesPeriodicTrigStartOpomnikVnosVrednost() {
//		HashMap<String, Object> variables = new HashMap<String, Object>();
//
//	
//		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("DiabetesProcesPeriodicnoProzenjeVnosVrednosti", variables);
//		String id = processInstance.getId();
//		System.out.println("Started process instance id " + id);
//		    		
//		//runtimeService.signal(id); (za to rabimo waitState?)
//		assertProcessEnded(id);
//
//		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
//		assertNotNull(historicProcessInstance);
//
//		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
//    }	
//	
//	@Deployment(resources = {"diagrams/Diabetes-Proces-Periodicno-Prozenje-Dsca.bpmn20.xml"})
//	public void testDiabetesPeriodicTrigStartOpomnikDsca() {
//		HashMap<String, Object> variables = new HashMap<String, Object>();
//
//	
//		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("DiabetesProcesPeriodicnoProzenjeDsca", variables);
//		String id = processInstance.getId();
//		System.out.println("Started process instance id " + id);
//		    		
//		//runtimeService.signal(id); (za to rabimo waitState?)
//		assertProcessEnded(id);
//
//		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
//		assertNotNull(historicProcessInstance);
//
//		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
//    }
	
	@Deployment(resources = {"diagrams/Hujsanje-Proces-Periodicno-Prozenje-Korak.bpmn20.xml"}) 
	public void testHujsanjePeriodicTrigStartOpomnikKorak() {  
		HashMap<String, Object> variables = new HashMap<String, Object>(); 

	
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("HujsanjeProcesPeriodicnoProzenjeKorak", variables);
		String id = processInstance.getId(); 
		System.out.println("Started process instance id " + id);
		    		
		//runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Finished, took " + historicProcessInstance.getDurationInMillis() + " millis");
    }
	
}
