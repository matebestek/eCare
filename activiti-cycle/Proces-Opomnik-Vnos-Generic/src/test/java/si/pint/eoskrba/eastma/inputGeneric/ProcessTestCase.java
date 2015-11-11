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
package si.pint.eoskrba.eastma.inputGeneric;

import java.util.HashMap;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.engine.test.Deployment;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.archetypes.exceptions.LdapException;
import si.pint.eoskrba.eastma.inputACT.delegate.IzracunKategorijeACTDelegate;
import si.pint.eoskrba.eastma.inputACT.delegate.PatientNotifySmsDelegate;
import si.pint.eoskrba.eastma.inputACT.delegate.PatientReminderSmsDelegate;
import si.pint.eoskrba.eastma.inputACT.listeners.CaremanagerObvestiloCompleteListener;
import si.pint.eoskrba.eastma.inputACT.listeners.CaremanagerObvestiloCreateListener;
import si.pint.eoskrba.eastma.inputACT.listeners.DoctorObvestiloCompleteListener;
import si.pint.eoskrba.eastma.inputACT.listeners.DoctorObvestiloCreateListener;
import si.pint.eoskrba.eastma.inputACT.listeners.PatientEntersACTCompleteListener;
import si.pint.eoskrba.eastma.inputACT.listeners.PatientEntersACTCreateListener;
import si.pint.eoskrba.eastma.inputACT.listeners.PatientObvestiloCompleteListener;
import si.pint.eoskrba.eastma.inputACT.listeners.PatientObvestiloCreateListener;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.model.Email;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;

@Log4j
public class ProcessTestCase extends ActivitiTestCase {

	public void assertInActivity(String processInstanceId, String activityId) {
		List<String> activeActivityIds = runtimeService
				.getActiveActivityIds(processInstanceId);
		assertTrue("Current activities (" + activeActivityIds
				+ ") does not contain " + activityId,
				activeActivityIds.contains(activityId));
	}

	private HashMap<String, Object> getTestSubject() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
	
		User patient = null;
		try {
			patient = UserRegistryFactory.getUserFromLDAP("NAME", Role.PATIENT);			
		} catch (LdapException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail();
		}
		variables.put(Constants.VAR_PATIENT, patient);
		variables.put(Constants.VAR_DOCTOR, patient.getDoctor());
		
		
		Email e = new Email(patient.getEmail(),"Vsebina","ACT vprašalanik");
		variables.put(Constants.VAR_patientEmail, e);
		Email e1 = new Email(patient.getCmEmail(),"Obvestilo","Pacient ni izpolnil ACT vprašalnika kljub 3 opomnikom.");
		variables.put(Constants.VAR_caremanagerEmail, e1);
		variables.put("email", e1);
		
//		Email e = new Email("EMAIL@DOMAIN.COM","Tole je vsebina","Že nekaj časa niste izpolnili ACT vprašalnik.");
//		variables.put(Constants.VAR_EMAIL, e);
			//b.sms
		variables.put("notifySmsDelegateBean", new PatientNotifySmsDelegate());
		
		//Vnos vrednosti
		variables.put("listenerPacientVnosComplete", new PatientEntersACTCompleteListener());
		variables.put("listenerPacientVnosCreate", new PatientEntersACTCreateListener());
		variables.put("izracunKategorijeDelegate", new IzracunKategorijeACTDelegate());
		variables.put("listenerCaremanagerObvestiloComplete", new CaremanagerObvestiloCompleteListener());
		variables.put("listenerCaremanagerObvestiloCreate",new CaremanagerObvestiloCreateListener());
		variables.put("listenerDoctorObvestiloCreate", new DoctorObvestiloCreateListener());
		variables.put("listenerDoctorObvestiloComplete", new DoctorObvestiloCompleteListener());
		variables.put("patientNotifySmsDelegate", new PatientNotifySmsDelegate());
		variables.put("patientReminderSmsDelegate", new PatientReminderSmsDelegate());
		variables.put("listenerPatientObvestiloComplete", new PatientObvestiloCompleteListener());
		variables.put("listenerPatientObvestiloCreate", new PatientObvestiloCreateListener());
		
		variables.put(Constants.VAR_PATIENT_COOPERATES, true);
		
		return variables;
	}

	@Deployment(resources = { "diagrams/NOTIFIER-CM.bpmn20.xml" })
	public void testNotifierCM() {

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				"Proces-Opomnik-Vnos-Notifier-Cm", getTestSubject());
		String id = pi.getId();
		log.info("Started process instance id " + id);
		assertInActivity(id, "sid-93A0FB21-39B9-486A-B43D-A9C4754D81FA");

		// 1. imam samo en userTask saj sendTask prehitro izvede
		List<Task> tasks = taskService.createTaskQuery().list();
		assertNotNull(tasks);
		for (Task t : tasks) {
			log.info("Task name: " + t.getName() + " created:"
					+ t.getCreateTime() + " key:" + t.getTaskDefinitionKey()
					+ " assignee:" + t.getAssignee());
			// pogledam formo
			// ProcessEngines.getProcessEngine(configuredProcessEngineName).getTaskService().getRenderedTaskForm(t.getId());
			// Zaključim task; ker gre samo za obvestilo, ni potrebe po
			// spremenljivkah
			
			taskService.complete(t.getId(), null);
		}
		tasks = taskService.createTaskQuery().list();
		assertEquals(0, (tasks == null ? 0 : tasks.size()));

		// runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService
				.createHistoricProcessInstanceQuery().processInstanceId(id)
				.singleResult();
		assertNotNull(historicProcessInstance);

		log.info("Finished, took "
				+ historicProcessInstance.getDurationInMillis() + " millis");

	}

	@Deployment(resources = { "diagrams/NOTIFIER-Pacient.bpmn20.xml" })
	public void testNotifierPacient() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		try {
			ProcessInstance pi = runtimeService.startProcessInstanceByKey(
					"Proces-Opomnik-Vnos-Notifier-Pacient",
					getTestSubject());
		} catch (Exception e) {
			assertTrue(e.getCause() instanceof java.lang.InstantiationException);

		}
		// String id = pi.getId();
		// log.info("Started process instance id " + id);
		// assertInActivity(id, "sid-394191A4-2AF3-4669-B358-AFE6B37FAE9C");
		//
		// // 1. imam samo en userTask saj sendTask prehitro izvede
		// List<Task> tasks = taskService.createTaskQuery().list();
		// assertNotNull(tasks);
		// for (Task t : tasks) {
		// log.info("Task name: " + t.getName() + " created:"
		// + t.getCreateTime() + " key:" + t.getTaskDefinitionKey()
		// + " assignee:" + t.getAssignee());
		// // pogledam formo
		// //
		// ProcessEngines.getProcessEngine(configuredProcessEngineName).getTaskService().getRenderedTaskForm(t.getId());
		// // Zaključim task; ker gre samo za obvestilo, ni potrebe po
		// // spremenljivkah
		// taskService.complete(t.getId(), null);
		// }
		// tasks = taskService.createTaskQuery().list();
		// assertEquals(0, (tasks == null ? 0 : tasks.size()));
		//
		// // runtimeService.signal(id); (za to rabimo waitState?)
		// assertProcessEnded(id);
		//
		// HistoricProcessInstance historicProcessInstance = historicDataService
		// .createHistoricProcessInstanceQuery().processInstanceId(id)
		// .singleResult();
		// assertNotNull(historicProcessInstance);
		//
		// log.info("Finished, took "
		// + historicProcessInstance.getDurationInMillis() + " millis");

	}

	@Deployment(resources = { "diagrams/NOTIFIER-Zdravnik.bpmn20.xml" })
	public void testNotifierZdravnik() {
		HashMap<String, Object> variables = new HashMap<String, Object>();

		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
				"Proces-Opomnik-Vnos-Notifier-Zdravnik", getTestSubject());
		String id = pi.getId();
		log.info("Started process instance id " + id);
		assertInActivity(id, "sid-CDB15277-E877-42D2-BE16-87EA2048A119");

		// 1. imam samo en userTask saj sendTask prehitro izvede
		List<Task> tasks = taskService.createTaskQuery().list();
		assertNotNull(tasks);
		for (Task t : tasks) {
			log.info("Task name: " + t.getName() + " created:"
					+ t.getCreateTime() + " key:" + t.getTaskDefinitionKey()
					+ " assignee:" + t.getAssignee());
			// pogledam formo
			// ProcessEngines.getProcessEngine(configuredProcessEngineName).getTaskService().getRenderedTaskForm(t.getId());
			// Zaključim task; ker gre samo za obvestilo, ni potrebe po
			// spremenljivkah
			taskService.complete(t.getId(), null);
		}
		tasks = taskService.createTaskQuery().list();
		assertEquals(0, (tasks == null ? 0 : tasks.size()));

		// runtimeService.signal(id); (za to rabimo waitState?)
		assertProcessEnded(id);

		HistoricProcessInstance historicProcessInstance = historicDataService
				.createHistoricProcessInstanceQuery().processInstanceId(id)
				.singleResult();
		assertNotNull(historicProcessInstance);

		log.info("Finished, took "
				+ historicProcessInstance.getDurationInMillis() + " millis");
	}

	@Deployment(resources = { "diagrams/REMINDER-Pacient.bpmn20.xml" })
	public void testReminderPacient() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		try {
			ProcessInstance pi = runtimeService.startProcessInstanceByKey(
					"Proces-Opomnik-Vnos-Reminder-Pacient",
					getTestSubject());			
		} catch (Exception e) {
			assertTrue(e.getCause() instanceof java.lang.InstantiationException);
		}

		// String id = pi.getId();
		// log.info("Started process instance id " + id);
		//
		// // 1. imam samo en userTask saj sendTask prehitro izvede
		//
		// List<Task> tasks = taskService.createTaskQuery().list();
		// assertEquals(0, (tasks == null ? 0 : tasks.size()));
		//
		// // runtimeService.signal(id); (za to rabimo waitState?)
		// assertProcessEnded(id);
		//
		// HistoricProcessInstance historicProcessInstance = historicDataService
		// .createHistoricProcessInstanceQuery().processInstanceId(id)
		// .singleResult();
		// assertNotNull(historicProcessInstance);
		//
		// log.info("Finished, took "
		// + historicProcessInstance.getDurationInMillis() + " millis");
	}

	@Deployment(resources = { "diagrams/NOTIFIER-CM.bpmn20.xml",
			"diagrams/NOTIFIER-Pacient.bpmn20.xml",
			"diagrams/NOTIFIER-Zdravnik.bpmn20.xml",
			"diagrams/NOTIFIER-ALL.bpmn20.xml" })
	public void testNotifierAll() {
		try {
			ProcessInstance pi = runtimeService.startProcessInstanceByKey(
					"Proces-Opomnik-Vnos-Notifier-All",
					getTestSubject());
		} catch (Exception e) {
			assertTrue(e.getCause() instanceof java.lang.InstantiationException);
		}
		// String id = pi.getId();
		// log.info("Started process instance id " + id);
		//
		// // 1. imam samo več userTask
		// List<Task> tasks = taskService.createTaskQuery().list();
		// assertNotNull(tasks);
		// for (Task t : tasks) {
		// log.info("Task name: " + t.getName() + " created:"
		// + t.getCreateTime() + " key:" + t.getTaskDefinitionKey()
		// + " assignee:" + t.getAssignee());
		// // pogledam formo
		// //
		// ProcessEngines.getProcessEngine(configuredProcessEngineName).getTaskService().getRenderedTaskForm(t.getId());
		// // Zaključim task; ker gre samo za obvestilo, ni potrebe po
		// // spremenljivkah
		// taskService.complete(t.getId(), null);
		// }
		// tasks = taskService.createTaskQuery().list();
		// assertEquals(0, (tasks == null ? 0 : tasks.size()));
		//
		// // runtimeService.signal(id); (za to rabimo waitState?)
		// assertProcessEnded(id);
		//
		// HistoricProcessInstance historicProcessInstance = historicDataService
		// .createHistoricProcessInstanceQuery().processInstanceId(id)
		// .singleResult();
		// assertNotNull(historicProcessInstance);
		//
		// log.info("Finished, took "
		// + historicProcessInstance.getDurationInMillis() + " millis");
	}

	@Deployment(resources = { "diagrams/VnosMeritev.bpmn20.xml" })
	public void testVnosMeritev() {
		HashMap<String, Object> variables = getTestSubject();
		variables.put(Constants.VAR_FORM_KEY, "enterPEF.form");
		try {
			ProcessInstance pi = runtimeService.startProcessInstanceByKey(
					"Proces-Opomnik-Vnos-Meritev", variables);
		} catch (Exception e) {
			assertTrue(e.getCause() instanceof java.lang.InstantiationException);
		}
		//
		// String id = pi.getId();
		// log.info("Started process instance id " + id);
		//
		// // 1. imam samo več userTask
		// List<Task> tasks = taskService.createTaskQuery().list();
		// assertNotNull(tasks);
		// for (Task t : tasks) {
		// log.info("Task name: " + t.getName() + " created:"
		// + t.getCreateTime() + " key:" + t.getTaskDefinitionKey()
		// + " assignee:" + t.getAssignee());
		// // pogledam formo
		// //
		// ProcessEngines.getProcessEngine(configuredProcessEngineName).getTaskService().getRenderedTaskForm(t.getId());
		// // Zaključim task; ker gre samo za obvestilo, ni potrebe po
		// // spremenljivkah
		// taskService.complete(t.getId(), null);
		// }
		// tasks = taskService.createTaskQuery().list();
		// assertEquals(0, (tasks == null ? 0 : tasks.size()));
		//
		// // runtimeService.signal(id); (za to rabimo waitState?)
		// assertProcessEnded(id);
		//
		// HistoricProcessInstance historicProcessInstance = historicDataService
		// .createHistoricProcessInstanceQuery().processInstanceId(id)
		// .singleResult();
		// assertNotNull(historicProcessInstance);
		//
		// log.info("Finished, took "
		// + historicProcessInstance.getDurationInMillis() + " millis");
	}

	@Deployment(resources = { "diagrams/NOTIFIER-ALL.bpmn20.xml",			
			"diagrams/NOTIFIER-CM.bpmn20.xml",
			"diagrams/NOTIFIER-Pacient.bpmn20.xml",
			"diagrams/NOTIFIER-Zdravnik.bpmn20.xml",
			"diagrams/REMINDER-Pacient.bpmn20.xml",
			"diagrams/VnosMeritev.bpmn20.xml",
			"diagrams/Proces-Opomnik-Vnos-Generic.bpmn20.xml" })
	public void testGenericVnosProces() {
		HashMap<String, Object> variables = new HashMap<String, Object>();
		//try {
			ProcessInstance processInstance = runtimeService
					.startProcessInstanceByKey(
							"Proces-Opomnik-Vnos-Generic",
							getTestSubject());
//		} catch (Exception e) {
//			assertTrue(e.getCause() instanceof java.lang.InstantiationException);
//		}
		// String id = processInstance.getId();
		// log.info("Started process instance id " + id);
		// assertInActivity(id, "Pacient_naj_vnese_PEF_vrednost_");
		/*
		 * //1. inspect first task name Task task =
		 * taskService.createTaskQuery().singleResult();
		 * assertNotNull("Task shouldn't be null!", task);
		 * assertEquals("Wrong task name!", "Vnesite PEF vrednost",
		 * task.getName().trim());
		 * 
		 * //1.1 claim & complete variables.put(
		 * "?data[at0001]?events[at0002]?data[at0003]?items[at0057]?items[at0058]?value"
		 * , new Integer("700"));
		 * 
		 * taskService.complete(task.getId(), variables);
		 * 
		 * //2. inspect second task task =
		 * taskService.createTaskQuery().singleResult();
		 * assertEquals("Wrong task name!", "Imate dobro PEF vrednost",
		 * task.getName().trim());
		 * 
		 * // //2.1 claim & complete taskService.complete(task.getId());
		 * 
		 * //runtimeService.signal(id); (za to rabimo waitState?)
		 * assertProcessEnded(id);
		 * 
		 * HistoricProcessInstance historicProcessInstance =
		 * historicDataService.
		 * createHistoricProcessInstanceQuery().processInstanceId
		 * (id).singleResult(); assertNotNull(historicProcessInstance);
		 * 
		 * log.info("Finished, took " +
		 * historicProcessInstance.getDurationInMillis() + " millis");
		 */
	}
	
	@Deployment(resources = { "diagrams/NOTIFIER-CM.bpmn20.xml",
			"diagrams/NOTIFIER-Zdravnik.bpmn20.xml",
			"diagrams/NOTIFIER-CM-Zdravnik.bpmn20.xml" })
	public void testNotifierCmZdravnik() {
		try {
			ProcessInstance pi = runtimeService.startProcessInstanceByKey(
					"Proces-Opomnik-Notifier-CM-Zdravnik",
					getTestSubject());
		} catch (Exception e) {
			log.error("napaka",e);
			assertTrue(e.getCause() instanceof java.lang.InstantiationException);
		}
		// String id = pi.getId();
		// log.info("Started process instance id " + id);
		//
		// // 1. imam samo več userTask
		// List<Task> tasks = taskService.createTaskQuery().list();
		// assertNotNull(tasks);
		// for (Task t : tasks) {
		// log.info("Task name: " + t.getName() + " created:"
		// + t.getCreateTime() + " key:" + t.getTaskDefinitionKey()
		// + " assignee:" + t.getAssignee());
		// // pogledam formo
		// //
		// ProcessEngines.getProcessEngine(configuredProcessEngineName).getTaskService().getRenderedTaskForm(t.getId());
		// // Zaključim task; ker gre samo za obvestilo, ni potrebe po
		// // spremenljivkah
		// taskService.complete(t.getId(), null);
		// }
		// tasks = taskService.createTaskQuery().list();
		// assertEquals(0, (tasks == null ? 0 : tasks.size()));
		//
		// // runtimeService.signal(id); (za to rabimo waitState?)
		// assertProcessEnded(id);
		//
		// HistoricProcessInstance historicProcessInstance = historicDataService
		// .createHistoricProcessInstanceQuery().processInstanceId(id)
		// .singleResult();
		// assertNotNull(historicProcessInstance);
		//
		// log.info("Finished, took "
		// + historicProcessInstance.getDurationInMillis() + " millis");
	}
	



	
}
