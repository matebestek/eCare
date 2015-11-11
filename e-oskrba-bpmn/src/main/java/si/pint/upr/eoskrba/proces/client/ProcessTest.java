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
package si.pint.upr.eoskrba.proces.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.ProcessBuilderFactory;
import org.drools.impl.EnvironmentFactory;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.marshalling.impl.ProcessMarshallerFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.process.workitem.wsht.WSHumanTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import si.pint.eoskrba.proces.workitemhandlers.NotificationWorkItemHandler;

/**
 * This is a sample file to launch a process.
 */
public class ProcessTest {
	public static Logger log = LoggerFactory.getLogger(ProcessTest.class);

	public static final void main(String[] args) {
		try {
			// load up the knowledge base
			KnowledgeBase kbase = readKnowledgeBase();
			StatefulKnowledgeSession ksession = createSession(kbase);
			// KnowledgeRuntimeLogger logger =
			// KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory
					.newThreadedFileLogger(ksession, "eoskrba-test", 1000);
			//SystemOutWorkItemHandler so = new SystemOutWorkItemHandler();
			// th.setConnection("127.0.0.1", 9999);
			ksession.getWorkItemManager().registerWorkItemHandler(

			"Notification", new NotificationWorkItemHandler());

			ksession.getWorkItemManager().registerWorkItemHandler(
					"Vnos Glukoze", new WSHumanTaskHandler());
			ksession.getWorkItemManager().registerWorkItemHandler(
					"Human Task", new WSHumanTaskHandler());
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("pacient", "john");
			params.put("nurse", "mary");
			params.put("meritev", (float) 0.0);
			params.put("employee", "john");
			params.put("casPotekel", false);
			// start a new process instance
			ksession.startProcess("eoskrba-proces-1-vnos-glukoze", params);
//			 ksession.startProcess("eoskrba-diabetes",params);
//			ksession.startProcess("com.sample.evaluation");
			logger.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static KnowledgeBase readKnowledgeBase() throws Exception {
		ProcessBuilderFactory
				.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
		ProcessMarshallerFactory
				.setProcessMarshallerFactoryService(new ProcessMarshallerFactoryServiceImpl());
		ProcessRuntimeFactory
				.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
		BPMN2ProcessFactory
				.setBPMN2ProcessProvider(new BPMN2ProcessProviderImpl());
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		// kbuilder.add(ResourceFactory.newClassPathResource("EOskrba-Diabetes.bpmn2"),ResourceType.BPMN2);
		kbuilder.add(ResourceFactory.newClassPathResource("UseCase1-VnosGlukoze.bpmn2"),ResourceType.BPMN2);
		kbuilder.add(ResourceFactory.newClassPathResource("Evaluation.bpmn"),ResourceType.BPMN2);
		

		return kbuilder.newKnowledgeBase();
	}

	private static StatefulKnowledgeSession createSession(KnowledgeBase kbase) {
		Properties properties = new Properties();
		properties
				.put("drools.processInstanceManagerFactory",
						"org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
		properties.put("drools.processSignalManagerFactory",
				"org.jbpm.process.instance.event.DefaultSignalManagerFactory");
		KnowledgeSessionConfiguration config = KnowledgeBaseFactory
				.newKnowledgeSessionConfiguration(properties);
		return kbase.newStatefulKnowledgeSession(config,
				EnvironmentFactory.newEnvironment());
	}

	private static StatefulKnowledgeSession createPersistentSession(
			KnowledgeBase kbase) {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("org.jbpm.persistence.jpa");
		Environment env = KnowledgeBaseFactory.newEnvironment();
		env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
		// create a new knowledge session that uses JPA to store the runtime
		// state
		StatefulKnowledgeSession ksession = JPAKnowledgeService
				.newStatefulKnowledgeSession(kbase, null, env);
		return ksession;
	}

}
