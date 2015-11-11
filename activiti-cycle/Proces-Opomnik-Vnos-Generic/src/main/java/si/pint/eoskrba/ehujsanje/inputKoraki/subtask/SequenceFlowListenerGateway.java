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
package si.pint.eoskrba.ehujsanje.inputKoraki.subtask;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.model.ValueObject;
import si.pint.eoskrba.messages.MessageRepo;
import si.pint.eoskrba.model.User;
import si.pint.utils.SecureCodec;

@Log4j
public class SequenceFlowListenerGateway implements ExecutionListener {

	public static final int subTask5Idx = 4;
	
	public void notify(DelegateExecution e) throws Exception {

		//get current index
		Object obj = null;
		Integer idx = (obj = e.getVariable(Constants.VAR_SUBTASK_INDEX)) == null ? 0 : (Integer) obj;	
		Boolean[] visitArray = (Boolean[]) e.getVariable(Constants.VAR_SUBTASK_VISIT_ARRAY);
		
		//default: skip all subtasks
		if (visitArray == null || visitArray.length < idx + 1)
			e.setVariable(Constants.VAR_CONTINUE_TO_SUBTASK, false);
		else {
			e.setVariable(Constants.VAR_CONTINUE_TO_SUBTASK, visitArray[idx]);
		}
		
		//skip working sheets (if already filled)		 
		if (idx == subTask5Idx) {
			Boolean skip =  (Boolean) e.getVariable(Constants.VAR_SKIP_WORKING_SHEETS);
			if (skip != null && skip) {
				e.setVariable(Constants.VAR_CONTINUE_TO_SUBTASK, false);
			}
		}
		
		//set subtask content
		User patient = (User) e.getVariable(Constants.VAR_PATIENT);
		switch (idx) {
			case 0 :
				setFirstSubTask(e, patient);
				break;
			case 1 :
				setSecondSubTask(e, patient);
				break;
			case 2 :
				setThirdSubTask(e, patient);
				break;
			case 3 :
				setFourthSubTask(e, patient);
				break;
			case 4 :
				setFifthSubTask(e, patient);
				break;
			case 5 :
				setSixthSubTask(e, patient);
				break;
			case 6 :
				Boolean answersCorrect = (Boolean) e.getVariable(Constants.VAR_ANSWERS_CORRECT);
				if (answersCorrect == null || answersCorrect)
					setSeventhSubTask(e, patient);
				else
					setSixthReturnTask(e, patient);
				break;
			default :
				break;		
		}

		
		//increase subtask number
		idx++;
		e.setVariable(Constants.VAR_SUBTASK_INDEX, idx);
		
	}
	
	
	/**
	 * Set first subtask configuration.
	 * 
	 * @param execution
	 * @param patient
	 */
	private void setFirstSubTask(DelegateExecution execution, User patient) {
		//UserTask template
		VelocityContext context = initVelocityContext(patient);
		VelocityEngine ve = initVelocityEngine();
		
		Template templateACT = null;
		try {
			templateACT = ve.getTemplate("hujsanje/hujsanje-uvod-korak-" + patient.getProcessStep().trim() +".vm","UTF-8"); 
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter swACT = new StringWriter();
		templateACT.merge(context, swACT);
		
		execution.setVariable(Constants.VAR_vnosVrednostiForm, new ValueObject(swACT.toString()));
		execution.setVariable(Constants.VAR_userTaskNameMeasurements, MessageRepo.HUJSANJE_PATIENT_KORAKI_FIRST_SUBTASK_INFO + " " + patient.getProcessStep()); //setiramo prvega od treh taskov
		
	}
	
	/**
	 * Set second subtask configuration.
	 * 
	 * @param execution
	 * @param patient
	 */	
	private void setSecondSubTask(DelegateExecution execution, User patient) {
		//UserTask template
		VelocityContext context = initVelocityContext(patient);
		VelocityEngine ve = initVelocityEngine();	
		
		//prepare second subtask
		prepareOpkpUserTask(execution, patient);
				
		
//		//zdej pripravim template še za UserTask
//		Template template = null;
//		try {
//			template = ve.getTemplate("hujsanje/hujsanje-opkp-korak-" + patient.getProcessStep().trim() +".vm","UTF-8");
//		} catch (Exception e) {
//			log.error("Velocity napaka", e);
//		}
//		StringWriter swACT = new StringWriter();
//		template.merge(context, swACT);
//		
//		execution.setVariable(Constants.VAR_vnosVrednostiForm, new ValueObject(swACT.toString()));
//		execution.setVariable(Constants.VAR_userTaskNameMeasurements, MessageRepo.HUJSANJE_PATIENT_KORAKI_SECOND_SUBTASK_INFO + " " + patient.getProcessStep()); //setiramo prvi task

	}
	
	/**
	 * Set third subtask configuration.
	 * 
	 * @param execution
	 * @param patient
	 */	
	private void setThirdSubTask(DelegateExecution execution, User patient) {
		//UserTask template
		VelocityContext context = initVelocityContext(patient);
		VelocityEngine ve = initVelocityEngine();
		
		//UserTask template
		Template template = null;
		try {
			template = ve.getTemplate("hujsanje/hujsanje-vnos-meritev.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter swACT = new StringWriter();
		template.merge(context, swACT);
		
		execution.setVariable(Constants.VAR_vnosVrednostiForm, new ValueObject(swACT.toString()));
		execution.setVariable(Constants.VAR_userTaskNameMeasurements, MessageRepo.HUJSANJE_PATIENT_KORAKI_THIRD_SUBTASK_INFO + " " + patient.getProcessStep());
	}
	
	/**
	 * Set fourth subtask configuration.
	 * 
	 * @param execution
	 * @param patient
	 */	
	private void setFourthSubTask(DelegateExecution execution, User patient) {
		//UserTask template
		VelocityContext context = initVelocityContext(patient);
		VelocityEngine ve = initVelocityEngine();		
		
		//zdej pripravim template še za UserTask
		Template template = null;
		try {
			template = ve.getTemplate("hujsanje/hujsanje-vsebina-korak-" + patient.getProcessStep().trim() +".vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter swACT = new StringWriter();
		template.merge(context, swACT);
		
		execution.setVariable(Constants.VAR_vnosVrednostiForm, new ValueObject(swACT.toString()));
		execution.setVariable(Constants.VAR_userTaskNameMeasurements, MessageRepo.HUJSANJE_PATIENT_KORAKI_FOURTH_SUBTASK_INFO + " " + patient.getProcessStep()); //setiramo prvega od treh taskov

	}
	
	/**
	 * Set fifth subtask configuration.
	 * 
	 * @param execution
	 * @param patient
	 */	
	private void setFifthSubTask(DelegateExecution execution, User patient) {
		//UserTask template
		VelocityContext context = initVelocityContext(patient);
		VelocityEngine ve = initVelocityEngine();		
		
		//UserTask template
		Template template = null;
		try {
			template = ve.getTemplate("hujsanje/hujsanje-delovni-listi-korak-" + patient.getProcessStep().trim() +".vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter swACT = new StringWriter();
		template.merge(context, swACT);
		
		execution.setVariable(Constants.VAR_vnosVrednostiForm, new ValueObject(swACT.toString()));
		execution.setVariable(Constants.VAR_userTaskNameMeasurements, MessageRepo.HUJSANJE_PATIENT_KORAKI_FIFTH_SUBTASK_INFO + " " + patient.getProcessStep()); //setiramo prvega od treh taskov

	}
	
	/**
	 * Set sixth subtask configuration.
	 * 
	 * @param execution
	 * @param patient
	 */	
	private void setSixthSubTask(DelegateExecution execution, User patient) {
		//UserTask template
		VelocityContext context = initVelocityContext(patient);
		VelocityEngine ve = initVelocityEngine();		
		
		//UserTask template
		Template template = null;
		try {
			template = ve.getTemplate("hujsanje/hujsanje-vprasalnik-korak-" + patient.getProcessStep().trim() +".vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter swACT = new StringWriter();
		template.merge(context, swACT);
		
		execution.setVariable(Constants.VAR_vnosVrednostiForm, new ValueObject(swACT.toString()));
		execution.setVariable(Constants.VAR_userTaskNameMeasurements, MessageRepo.HUJSANJE_PATIENT_KORAKI_SIXTH_SUBTASK_INFO + " " + patient.getProcessStep()); //setiramo prvega od treh taskov

	}
	
	
	/**
	 * Set sixth return  subtask configuration.
	 * 
	 * @param execution
	 * @param patient
	 */	
	private void setSixthReturnTask(DelegateExecution execution, User patient) {
		//UserTask template
		VelocityContext context = (VelocityContext) execution.getVariable(Constants.VAR_HUJSANJE_ANSWERS_CONTEXT); 
				
		VelocityEngine ve = initVelocityEngine();		
		
		//prepare UserTask template
		Template template = null;
		try {
			template = ve.getTemplate("hujsanje/hujsanje-napacen-odgovor-korak-" + patient.getProcessStep().trim() +".vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter swACT = new StringWriter();
		template.merge(context, swACT);
		
		execution.setVariable(Constants.VAR_vnosVrednostiForm, new ValueObject(swACT.toString()));
		execution.setVariable(Constants.VAR_userTaskNameMeasurements, MessageRepo.HUJSANJE_PATIENT_KORAKI_WRONG_ANSWER_SUBTASK_INFO); //setiramo subtask za napacen odgovor

	}
	
	
	
	
	/**
	 * Set seventh subtask configuration.
	 * 
	 * @param execution
	 * @param patient
	 */	
	private void setSeventhSubTask(DelegateExecution execution, User patient) {
		//UserTask template
		VelocityContext context = initVelocityContext(patient);
		VelocityEngine ve = initVelocityEngine();		
		
		//prepare UserTask template
		Template template = null;
		try {
			template = ve.getTemplate("hujsanje/hujsanje-spodbuda-korak-" + patient.getProcessStep().trim() +".vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter swACT = new StringWriter();
		template.merge(context, swACT);
		
		execution.setVariable(Constants.VAR_vnosVrednostiForm, new ValueObject(swACT.toString()));
		execution.setVariable(Constants.VAR_userTaskNameMeasurements, MessageRepo.HUJSANJE_PATIENT_KORAKI_SEVENTH_SUBTASK_INFO + " " + patient.getProcessStep()); //setiramo zadnji (peti) subtask

	}
	
	/**
	 * Init Velocity engine.
	 * 
	 * @param patient
	 * @return
	 */
	private VelocityContext initVelocityContext(User patient) {
		
		VelocityContext context = new VelocityContext();
		context.put("patient",patient);		
		
		return context;
	}
	
	private VelocityEngine initVelocityEngine() {
		
		VelocityEngine ve = new VelocityEngine();
		Properties p = new Properties();
		p.setProperty("resource.loader","class,jar");
		p.setProperty("class.resource.loader.description","Velocity Classpath Resource Loader");
		p.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);	
		
		return ve;
	}
	
	
	
	private void prepareOpkpUserTask(DelegateExecution execution, User patient) {
		VelocityEngine ve = new VelocityEngine();

		Properties p = new Properties();
		p.setProperty("resource.loader","class,jar");
		p.setProperty("class.resource.loader.description","Velocity Classpath Resource Loader");
		p.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);			
		
		VelocityContext context = new VelocityContext();
		context.put("patient",patient);
		
		//za integracijo z OPKP pripravim podatke o trenutnem uporabniku in jih podpišem z dogovorjenim
		//simetričnim ključem
		//v template-u potem imam samo nek include stavek, ki bo izvedel http klic, ki bo imel te podpisane
		//podatke zakodirane v url query parameter data
		//rezultat bo potem kar obrazec kot ga pošlje opkp
		String urlData = prepareOPKPUrlData(patient);
		context.put("data",urlData);
		//ko uporabnik poklika na oddajo vprašalnika, se mu prikaže samo obvestilo, da bo dobil v nekaj sekundah
		//rezultate vprašalnik kar med Moje naloge
		
		//zdej pripravim template še za UserTask
		Template template = null;
		try {
			template = ve.getTemplate("hujsanje/hujsanje-opkp.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter swACT = new StringWriter();
		template.merge(context, swACT);
		
		execution.setVariable(Constants.VAR_vnosVrednostiForm, new ValueObject(swACT.toString()));
		execution.setVariable(Constants.VAR_userTaskNameMeasurements, MessageRepo.HUJSANJE_PATIENT_KORAKI_SECOND_SUBTASK_INFO + " " + patient.getProcessStep()); //setiramo prvi task
		
	}
	
	public static String prepareOPKPUrlData(User patient){
		String data = getJSON(patient);
		SecureCodec codec = new SecureCodec();
		String signedData = codec.encode(data);
		String urlData = "";
		try {
			urlData = URLEncoder.encode(signedData,"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		return urlData;
	}
	
	public static String getJSON(User patient){
		//vzamem najvišjo vrednost med vsemi različnimi vrednostmi - ponavadi bo itak vedno ista vrednost TODO: popravi da bo vzel zadnjo meritev
		String query = "max(collection(\"/db/eHujsanje/" + patient.getUsername() + "\")//*[name()=\"content\" and @archetype_node_id=\"openEHR-EHR-SECTION.vkljucitev_eo.v1\"]/*[name()=\"items\" and @archetype_node_id=\"openEHR-EHR-OBSERVATION.telesna_sestava.v1\"]/*[name()=\"data\" and @archetype_node_id=\"at0001\"]/*[name()=\"events\" and @archetype_node_id=\"at0002\"]/*[name()= \"data\" and @archetype_node_id=\"at0003\"]/*[name()=\"items\" and @archetype_node_id=\"at0007\"]/*[name() = \"value\"]/*[name()=\"magnitude\"]/text())";//bazalni metabolizem
		String bm = null;
		try {
			bm = (String)DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).getSingleValue(patient.getUsername(),patient.getUsername(),EMPLOYEE_TYPE_ENUM.DIABETES,query);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Napaka pri pridobivanju bazalnega metabolizma za pacienta:"+patient.getUsername() +": " + e.getMessage());			
		}
		
		
		String json = 
		"{\"ID\":\"" + patient.getUsername()+ "\"," +
		"\"EMAIL\":\" "+ patient.getEmail() +" \"," +
		"\"FIRST_NAME\":\" " + patient.getFirstNameLastName() + "\"," +
		"\"LAST_NAME\":\" " + patient.getFirstNameLastName() + "\"," +
		"\"SEX\":\"" +patient.getSex().toString() + "\"," +
		"\"BDATE\":\"" + patient.getDateOfBirth() + "\"," +
		"\"HEIGHT\":\"" +patient.getHeight() + "\"," +
		"\"WEIGHT\":\"" + patient.getWeight() + "\"," +
		"\"BM\":\"" + bm + "\"" +
		"}";
		
		return json;
	}


}
