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

import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.apache.log4j.MDC;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.archetypes.exceptions.EmptyParametersException;
import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.EnterWeightLossValusArcheSrv;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.conf.SystemConstant;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.listeners.APatientEntersValueCompleteListener;
import si.pint.eoskrba.eastma.inputGeneric.model.ValueObject;
import si.pint.eoskrba.ehujsanje.inputFlat.delegate.CalcCategoryWeightLossValuesDelegate;
import si.pint.eoskrba.messages.MessageRepo;
import si.pint.eoskrba.model.Email;
import si.pint.eoskrba.model.User;
import si.pint.eoskrba.utils.ArchetypeServiceUtils;

@Log4j
public class PatientEntersMeasurementsCompleteListener extends
		APatientEntersValueCompleteListener implements Serializable {

	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = -652573758553452378L;
	
	//weight path in archetype
	private static final String BODY_WEIGHT_ARCH_ID = "/items[openEHR-EHR-OBSERVATION.body_weight.v1]/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value";
	
	//weight path in archetype
	private static final String BODY_MASS_INDEX_ARCH_ID = "/items[openEHR-EHR-OBSERVATION.body_mass_index.v1]/data[at0001]/events[at0002]/data[at0003]/items[at0004]/value";
		
	//xquery for getting last OPKP values (imeEnote, priporocena vred, dejanska vred.)
//	private static String XQUERY_GET_LAST_OPKP_RESULTS = 
//			  "let $coll := ( collection(\"/db/eHujsanje/{0}\")//*[name()=\"items\" and @archetype_node_id=\"openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1\"]/*[name()=\"data\" and @archetype_node_id=\"at0001\"]/*[name()=\"items\" and @archetype_node_id=\"openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1\"] )"
//			+ "let $maxFile := max(for $content in $coll return substring-before(substring-after(substring-after(fn:base-uri($content), \"xml\"), \"OPKP.v1/\"), \".xml\"))"
//			+ "for $contentMax in ( collection(\"/db/eHujsanje/{1}\")//*[name()=\"items\" and @archetype_node_id=\"openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1\"]/*[name()=\"data\" and @archetype_node_id=\"at0001\"]/*[name()=\"items\" and @archetype_node_id=\"openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1\"] )"
//			+ "where substring-before(substring-after(substring-after(fn:base-uri($contentMax), \"xml\"), \"OPKP.v1/\"), \".xml\") = $maxFile "
//			+ "return"
//			+ "<zivilo>"
//			+ 	"<imeEnote>'{'$contentMax//*[name()=\"items\" and @archetype_node_id=\"at0002\"]/*[name()=\"value\"]/*[name()=\"value\"]/text()'}'</imeEnote>"
//			+	"<priporocilo>'{'$contentMax//*[name()=\"items\" and @archetype_node_id=\"at0004\"]/*[name()=\"value\"]/*[name()=\"value\"]/text()'}'</priporocilo>"
//			+	"<zauzitoEnot>'{'$contentMax//*[name()=\"items\" and @archetype_node_id=\"at0003\"]/*[name()=\"value\"]/*[name()=\"magnitude\"]/text()'}'</zauzitoEnot>"
//			+	"<ev>'{'$contentMax/../*[name()=\"items\" and @archetype_node_id=\"at0003\"]/*[name()=\"value\"]/*[name()=\"magnitude\"]/text()'}'</ev>"
//			+   "<pev>'{'$contentMax/../*[name()=\"items\" and @archetype_node_id=\"at0018\"]/*[name()=\"value\"]/*[name()=\"magnitude\"]/text()'}'</pev>" 
//			+ "</zivilo>";
	
	//to vrne EV, PEV, prot, fat, chot, alc, sugar
	private static String XQUERY_GET_LAST_OPKP_RESULTS = 
					"let $coll := ( collection(\"/db/eHujsanje/{0}\")//*[name()=\"items\" and @archetype_node_id=\"openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1\"])"
					+ "let $maxFile := max(for $content in $coll return substring-before(substring-after(fn:base-uri($content), \"_OPKP.v1/\"), \".xml\"))"
					+ "for $contentMax in $coll "
					+ "where substring-before(substring-after(fn:base-uri($contentMax), \"_OPKP.v1/\"), \".xml\") = $maxFile "
					+ "return"
					+ "<result>"
					+ "<ev>'{'$contentMax/*[name()=\"data\" and @archetype_node_id=\"at0001\"]/*[name()=\"items\" and @archetype_node_id=\"at0003\"]/*[name()=\"value\"]/*[name()=\"magnitude\"]/text()'}'</ev>"
					+ "<pev>'{'$contentMax//*[name()=\"items\" and @archetype_node_id=\"at0018\"]/*[name()=\"value\"]/*[name()=\"magnitude\"]/text()'}'</pev>"
					+ "<prot>'{'$contentMax//*[name()=\"items\" and @archetype_node_id=\"at0021\"]/*[name()=\"value\"]/*[name()=\"magnitude\"]/text()'}'</prot>"
					+ "<fat>'{'$contentMax//*[name()=\"items\" and @archetype_node_id=\"at0022\"]/*[name()=\"value\"]/*[name()=\"magnitude\"]/text()'}'</fat>"
					+ "<chot>'{'$contentMax//*[name()=\"items\" and @archetype_node_id=\"at0023\"]/*[name()=\"value\"]/*[name()=\"magnitude\"]/text()'}'</chot>"
					+ "<alc>'{'$contentMax//*[name()=\"items\" and @archetype_node_id=\"at0024\"]/*[name()=\"value\"]/*[name()=\"magnitude\"]/text()'}'</alc>"					
					+ "<sugar>'{'$contentMax//*[name()=\"items\" and @archetype_node_id=\"at0025\"]/*[name()=\"value\"]/*[name()=\"magnitude\"]/text()'}'</sugar>"
					+ "</result>";
	
	
	@Override
	public void notify(DelegateTask delegateTask) {
		
		DelegateExecution execution = delegateTask.getExecution();
		
		EnterWeightLossValusArcheSrv service = new EnterWeightLossValusArcheSrv();
		if (!service.initInput())
			return;

		// user input data
		Map<ArcheDataPath, Object> userInputsMap = new HashMap<ArcheDataPath, Object>();
		
		//parameters for calculating ok/notOk/critical categories
		HashMap<String, String> map = new HashMap<String, String>();
		
		// look for all possible attributes on session
		Map allAtributes = service.getInputsMap();
		Iterator it = allAtributes.keySet().iterator();
		String bodyWeight = null;
		while (it.hasNext()) {
			String key = (String) it.next();
			String keyR = key.replaceAll("/", "?");
			keyR = keyR.replaceAll("_", "!");
			Object var = execution.getVariable(keyR);
			if (var != null && !var.equals("")) {
				userInputsMap.put(new ArcheDataPath(key), var);
				
				if (key.indexOf(BODY_WEIGHT_ARCH_ID) >= 0) {
					map.put(Constants.VAR_INPUT_BODY_WEIGHT, (String) var);
					bodyWeight = (String) var;
				}
			}
		}
		
		//save body weight
		execution.setVariable(Constants.VAR_GENERIC_RESULT, map);
		
		User patient = (User) execution.getVariable(Constants.VAR_PATIENT);
		String username = (String) execution.getVariable(Constants.VAR_USERNAME_APP);//username bo tu verjetno isti kot patientId. Drugače je nekaj zelo narobe (zaenkrat damo za username kar patient id. Na gui-ju vidi task itak samo izbrani pacient)
		
		
		//calculate body mass index
		BigDecimal bodyHeightFirst = null;
		if (bodyWeight != null && !bodyWeight.equals("")) {
			bodyHeightFirst = CalcCategoryWeightLossValuesDelegate.getFirstBodyWeightFromDB(username, patient.getUsername(), CalcCategoryWeightLossValuesDelegate.BODY_HEIGHT_EXIST_PATH);
			bodyHeightFirst = bodyHeightFirst.divide(new BigDecimal("100")); //convert to meters
			
			BigDecimal bodyHeightSquare = bodyHeightFirst.multiply(bodyHeightFirst);
			BigDecimal bodyWeightBD = new BigDecimal(bodyWeight); 
			BigDecimal bodyMassIndex = bodyWeightBD.divide(bodyHeightSquare, 2, RoundingMode.FLOOR);
			
			userInputsMap.put(new ArcheDataPath(BODY_MASS_INDEX_ARCH_ID), bodyMassIndex.doubleValue() + "");		
		}
		
		// data validation
		if (!service.setAndValidataData(userInputsMap))
			//throw new WrappedException(new EmptyParametersException("Validation for weight loss values was not ok!"));			
			new EmptyParametersException("Validation for weight loss values was not ok!");
		
		// save data in XML form to DB
		MDC.put("user", patient.getUsername());
		MDC.put("userRole",patient.getRole().toString());
		MDC.put("task", "PatientEntersMeasurementsCompleteListener");
		MDC.put("taskContent", "vnos vrednosti hujsanje, korak " + patient.getProcessStep());
		MDC.put("taskType", "H");		
		
		//save to eXist
		service.saveInput(SystemType.EOSKRBAPROCESSENGINE, username, patient.getUsername());
		
		//Calculate category & prepare to go to CA for notifiers (Patient or CM-Patient)
		CalcCategoryWeightLossValuesDelegate delegateCalc = new CalcCategoryWeightLossValuesDelegate(false);
		try {
			delegateCalc.execute(execution);
		} catch (Exception e) {
			log.error("Napaka pri izracunu kategorije za pacienta " + patient.getUsername());
			e.printStackTrace();
		}
		
		//check OPKP results for additional CM notifications
		checkOpkpResults(execution, delegateCalc);
		
		// Shrani podatek, da je podnalogo 3 dokončal
		ArchetypeServiceUtils.updateCompletedSubTasksNo(
			3,
			delegateTask.getExecution(),
			patient.getUsername(),
			patient.getUsername()
		);
						
		log.info("Pacient " + patient.getUsername() + " je izpolnil obrazec za vnos meritev");

	}
	
	/**
	 * 
	 * 	//preveri pogoje:
		//		- Beljakovin lahko od 10 do 15% EV (mleko in mlečni izdelki, meso in zamenjave , stročnice)
		//		- maščobe do 30%EV
		//		- ogljikovih hidratov nad 50% EV (škrobna živila, sadje in zelenjava)
		//		- enostavnih sladkorjev do 10% EV (Sladkor in sladka živila)		
	 * 
	 * @param execution
	 */
	private void checkOpkpResults(DelegateExecution execution, CalcCategoryWeightLossValuesDelegate delegateCalc) {
		//init
		User patient = (User) execution.getVariable(Constants.VAR_PATIENT);
		
		
		//pridobi stevilke za meso, sladkor, ogljikove iz zadnjega XML v eXist
		Object[] xqueryParams = {patient.getUsername()};
		String xqueryFinal = MessageFormat.format(XQUERY_GET_LAST_OPKP_RESULTS, xqueryParams);
		try {
			List results = DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).getMultiValue(
											patient.getUsername(), 
											patient.getUsername(), 
										    EMPLOYEE_TYPE_ENUM.HUJSANJE, 
										    xqueryFinal);
			
			Map m = (Map) execution.getVariable(Constants.VAR_GENERIC_RESULT);
			if (m == null)
				m = new HashMap();
			
			//stiri (oz.pet) skupin - ni nujno da podatki bodo - init na 0
			BigDecimal proteins = new BigDecimal("0");
			BigDecimal fat = new BigDecimal("0");
			BigDecimal carbonHydrates = new BigDecimal("0");
			BigDecimal simpleSugar = new BigDecimal("0");
			BigDecimal alc = new BigDecimal("0");

			//energijska vrednost in priporocena e.v.
			BigDecimal ev = new BigDecimal("0");
			BigDecimal pev = new BigDecimal("0");
			
//			Map foodGroupMap = new HashMap();
			Iterator it = results.iterator();
			while (it.hasNext()) {
				StringBuffer el = new StringBuffer((String) it.next());
				
				if (el.length() < 20)
					continue;
				
				//energijska vrednost
				CharSequence evStr = el.indexOf("<ev>") > 0 ? el.subSequence(el.indexOf("<ev>") + "<ev>".length(), el.indexOf("</ev>")) : null;
				ev = new BigDecimal(evStr != null ? evStr + "" : "0");
				
				//priporocena energijska vrednost
				CharSequence pevStr = el.indexOf("<pev>") > 0 ? el.subSequence(el.indexOf("<pev>") + "<pev>".length(), el.indexOf("</pev>")) : null;
				pev = new BigDecimal(pevStr != null ? pevStr + "" : "0");
				
				//prot, fat, chot, alc
				CharSequence protCS = el.indexOf("<prot>") > 0 ? el.subSequence(el.indexOf("<prot>") + "<prot>".length(), el.indexOf("</prot>")) : null;
				proteins = new BigDecimal(protCS != null ? protCS + "" : "0");
				
				CharSequence fatCS = el.indexOf("<fat>") > 0 ? el.subSequence(el.indexOf("<fat>") + "<fat>".length(), el.indexOf("</fat>")) : null;
				fat = new BigDecimal(fatCS != null ? fatCS + "" : "0");
				
				CharSequence carbonHydratesCS = el.indexOf("<chot>") > 0 ? el.subSequence(el.indexOf("<chot>") + "<chot>".length(), el.indexOf("</chot>")) : null;
				carbonHydrates = new BigDecimal(carbonHydratesCS != null ? carbonHydratesCS + "" : "0");
				
				CharSequence simpleSugarCS = el.indexOf("<sugar>") > 0 ? el.subSequence(el.indexOf("<sugar>") + "<sugar>".length(), el.indexOf("</sugar>")) : null;
				simpleSugar = new BigDecimal(simpleSugarCS != null ? simpleSugarCS + "" : "0");
				
				CharSequence alcCS =el.indexOf("<alc>") > 0 ? el.subSequence(el.indexOf("<alc>") + "<alc>".length(), el.indexOf("</alc>")) : null;
				alc = new BigDecimal(alcCS != null ? alcCS + "" : "0");				
				
				break;
				
			}			
			
			//converet to kCal
			BigDecimal proteinKcal = proteins.multiply(new BigDecimal("4"));
			BigDecimal fatKcal = fat.multiply(new BigDecimal("9"));
			BigDecimal chotKcal = carbonHydrates.multiply(new BigDecimal("4"));
			BigDecimal sugarKcal = simpleSugar.multiply(new BigDecimal("4"));
			BigDecimal alcKcal = alc.multiply(new BigDecimal("7"));
			BigDecimal sum = proteinKcal.add(fatKcal).add(chotKcal).add(alcKcal);
			

			//save to map - proteins
			m.put(Constants.VAR_INPUT_PROTEINS, proteinKcal.intValue() + "");
						
			//fats
			m.put(Constants.VAR_INPUT_FAT, fatKcal.intValue() + "");
			
			//carbonhydrates
			m.put(Constants.VAR_INPUT_CARBON_HYDRATES, chotKcal.intValue() + "");			
				
			//check sugars
			m.put(Constants.VAR_INPUT_SIMPLE_SUGAR, sugarKcal.intValue() + "");		
			
			//priporocen energiski vnos
			BigDecimal actualEnergyValuePerc = new BigDecimal("100");
			if (ev != null && pev != null && pev.compareTo(BigDecimal.ZERO) != 0)
				actualEnergyValuePerc = ev.multiply(new BigDecimal("100")).divide(pev, 0, RoundingMode.HALF_UP);
			
			m.put(Constants.VAR_INPUT_ENERGY_INTAKE, actualEnergyValuePerc + "");
			
			//calculate recomended food groups percentages (from pev)
			BigDecimal pevKcal = ev.divide(new BigDecimal("1000"), RoundingMode.HALF_UP).multiply(new BigDecimal("0.239005736")).setScale(2, RoundingMode.HALF_UP);
			
			//lower/upper boundaries (+/- 15% deviation)
			BigDecimal pProtLowStrict = pevKcal.multiply(new BigDecimal("10")).divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
			BigDecimal pProtHStrict = pevKcal.multiply(new BigDecimal("15")).divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
			BigDecimal pFatHighStrict = pevKcal.multiply(new BigDecimal("30")).divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
			BigDecimal pChot50LowStrict = pevKcal.multiply(new BigDecimal("50")).divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
			BigDecimal pSugar10HighStrict = pevKcal.multiply(new BigDecimal("10")).divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
			
			//lower/upper boundaries (+/- 15% deviation)
			BigDecimal pProtLow = pevKcal.multiply(new BigDecimal("8.5")).divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
			BigDecimal pProtH = pevKcal.multiply(new BigDecimal("17.25")).divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
			BigDecimal pFatHigh = pevKcal.multiply(new BigDecimal("34.5")).divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
			BigDecimal pChot50Low = pevKcal.multiply(new BigDecimal("42.5")).divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
			BigDecimal pSugar10High = pevKcal.multiply(new BigDecimal("11.5")).divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP);
			
			//save recomended values for presentation
			m.put(Constants.VAR_INPUT_PROTEINS_LOW, pProtLowStrict.intValue() + "");
			m.put(Constants.VAR_INPUT_PROTEINS_HIGH, pProtHStrict.intValue() + "");
			m.put(Constants.VAR_INPUT_CARBON_HYDRATES_LOW, pChot50LowStrict.intValue() + "");
			m.put(Constants.VAR_INPUT_FAT_HIGH, pFatHighStrict.intValue() + "");
			m.put(Constants.VAR_INPUT_SIMPLE_SUGAR_HIGH, pSugar10HighStrict.intValue() + "");
						
			//default values
			m.put(Constants.VAR_INPUT_PROTEINS_OK, true);
			m.put(Constants.VAR_INPUT_FAT_OK, true);
			m.put(Constants.VAR_INPUT_CARBON_HYDRATES_OK, true);
			m.put(Constants.VAR_INPUT_SIMPLE_SUGAR_OK, true);
			m.put(Constants.VAR_INPUT_ENERGY_INTAKE_OK, true);
			
			
			boolean foodIntakeOk = true;
			if (proteinKcal.intValue() < pProtLow.intValue() || proteinKcal.intValue() > pProtH.intValue()) {
				m.put(Constants.VAR_INPUT_PROTEINS_OK, false);
				foodIntakeOk = false;
			}
			if (fatKcal.intValue() > pFatHigh.intValue()) {
				m.put(Constants.VAR_INPUT_FAT_OK, false);
				foodIntakeOk = false;				
			}
			if (chotKcal.intValue() < pChot50Low.intValue()) {
				m.put(Constants.VAR_INPUT_CARBON_HYDRATES_OK, false);
				foodIntakeOk = false;						
			}
			if (sugarKcal.intValue() > pSugar10High.intValue()) {
				m.put(Constants.VAR_INPUT_SIMPLE_SUGAR_OK, false);
				foodIntakeOk = false;						
			}
			if (actualEnergyValuePerc.intValue() < 85) {
				m.put(Constants.VAR_INPUT_ENERGY_INTAKE_OK, false);
				foodIntakeOk = false;						
			}
			
			//prepare task context
			boolean bodyMassOk = (Boolean) m.get(Constants.VAR_INPUT_BODY_MASS_OK);
			
			if (!foodIntakeOk || !bodyMassOk) {
			
				execution.setVariable(Constants.VAR_CATEGORY_NOT_OK, true);
				execution.setVariable(Constants.VAR_CATEGORY_OK, false);
				
				//prepare context
				Object obj = null;
				VelocityEngine ve = new VelocityEngine();
				Properties p = new Properties();
				p.setProperty("resource.loader","class,jar");
				p.setProperty("class.resource.loader.description","Velocity Classpath Resource Loader");
				p.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
				ve.init(p);		
				VelocityContext context = new VelocityContext();	
				context.put("patient",patient);
				
				execution = prepareVarsValuesNotOK(execution, ve, context, patient, bodyMassOk, foodIntakeOk);
			}
			
			
			
			execution.setVariable(Constants.VAR_GENERIC_RESULT, m);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}	
		
	}
	
	
	public DelegateExecution prepareVarsValuesNotOK(DelegateExecution execution, VelocityEngine ve, VelocityContext context, User patient, boolean bodyMassOk, boolean foodIntakeOk) {
		
		String subject = "";
		String content = "";
		String taskName = "";
		String taskVmFile = "";
		
		if (bodyMassOk && !foodIntakeOk) {
			subject = MessageRepo.HUJSANJE_PATIENT_MAIL_WEIGHT_OK_SUBJECT_OPKP_RESULTS;
			content = MessageRepo.HUJSANJE_PATIENT_MAIL_WEIGHT_OK_CONTENT_OPKP_NOK_RESULTS;
			taskName = MessageRepo.HUJSANJE_PATIENT_TASK_WEIGHT_OPKP_NOK;
			taskVmFile = "notify-patient-opkp-NOK.vm";
		}
		else if (!bodyMassOk && foodIntakeOk) {
			subject = MessageRepo.HUJSANJE_PATIENT_MAIL_VALUES_NOK_SUBJECT;
			content = MessageRepo.HUJSANJE_PATIENT_MAIL_VALUES_NOK_CONTENT;
			taskName = MessageRepo.HUJSANJE_PATIENT_TASK_WEIGHT_NOT_OK;
			taskVmFile = "notify-patient-weight-NOK.vm";			
		}
		else { 
			subject = MessageRepo.HUJSANJE_PATIENT_MAIL_VALUES_NOK_SUBJECT_OPKP_RESULTS;
			content = MessageRepo.HUJSANJE_PATIENT_MAIL_VALUES_NOK_CONTENT_OPKP_NOK_RESULTS;
			taskName = MessageRepo.HUJSANJE_PATIENT_TASK_WEIGHT_NOT_OK_OPKP_NOK_RESULTS;
			taskVmFile = "notify-patient-weight-NOK-opkp-results-nok.vm";	
		}
		
		//patient
		Email patientEmail = new Email(patient.getEmail(), content, subject);
			
		patientEmail.setFrom(SystemConstant.EMAIL_HUJSANJE.toString());
		execution.setVariable(Constants.VAR_patientEmail, patientEmail);
		Template template = null;
		try {
			template = ve.getTemplate(taskVmFile,"UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		execution.setVariable(Constants.VAR_notifyPatientForm, new ValueObject(sw.toString()));
		execution.setVariable(Constants.VAR_patientUserTaskName, taskName);
		
		return execution;
		
	}
	
	

}
