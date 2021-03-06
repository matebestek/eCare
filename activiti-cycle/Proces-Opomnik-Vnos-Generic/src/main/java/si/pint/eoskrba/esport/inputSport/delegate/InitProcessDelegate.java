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
package si.pint.eoskrba.esport.inputSport.delegate;

import java.io.Serializable;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Properties;

import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.eoskrba.conf.SystemConstant;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eastma.inputGeneric.delegate.AInitProcessDelegate;
import si.pint.eoskrba.eastma.inputGeneric.model.ValueObject;
import si.pint.eoskrba.esport.inputSport.utils.ExerciseDetails;
import si.pint.eoskrba.model.Email;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;

@Log4j
public class InitProcessDelegate extends AInitProcessDelegate implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7450885070699274734L;

	private GregorianCalendar exerciseDate;
	
	public InitProcessDelegate() {
		super();
		this.exerciseDate = new GregorianCalendar();
	}
	
	public InitProcessDelegate(GregorianCalendar gc) {
		super();
		this.exerciseDate = gc;
	}
	
	@Override
	@SuppressWarnings("unused")
	public void execute(DelegateExecution execution) throws Exception {
		
		log.info("");
		
		// get user (patient) from session
		String subjectString = (String) execution.getVariable(Constants.VAR_PATIENT_OBJ);
		if (subjectString == null) {
			throw new RuntimeException(
					"User on proces 'Sport-Opomnik-Vnos-Generic' not defined (sport)!");
		}
	
		
		JSONObject subjectJSON = new JSONObject(subjectString);

		User patient = new User();

		patient.setBid((String) subjectJSON.get(Constants.VAR_BID));
		patient.setEmail((String) subjectJSON.get(Constants.VAR_eMAIL));
		patient.setMobilePhone((String) subjectJSON.get(Constants.VAR_MOBILE_TEL_NUM));
		patient.setSex(subjectJSON.get(Constants.VAR_SEX).equals(User.Sex.MALE.toString()) ? User.Sex.MALE : User.Sex.FEMALE);
		patient.setDateOfBirth(UserRegistryFactory.parseTimestamp((String) (subjectJSON.get(Constants.VAR_BIRTH_DATE))).getTime());
		patient.setWeight(new BigDecimal((String) subjectJSON.get(Constants.VAR_WEIGHT)));
		patient.setHeight(new BigDecimal((String) subjectJSON.get(Constants.VAR_HEIGHT)));
		patient.setRole(Role.getRoleFromId((String) subjectJSON.get(Constants.VAR_ROLE)));
		patient.setUsername((String) subjectJSON.get(Constants.VAR_USERNAME));
		patient.setFirstNameLastName((String) subjectJSON.get(Constants.VAR_FIRST_NAME_LAST_NAME));
		patient.setPatientsCM(UserRegistryFactory.findPatientsDoctor(patient.getUsername()));
		execution.setVariable(Constants.VAR_PATIENT, patient);
		
		//cleanup
		execution.removeVariable(Constants.VAR_PATIENT_OBJ);
		
		HashMap<String, Object> variables = new HashMap<String, Object>();

		// Velocity uporabljan za template-e. Pretvorim jih v String in jih
		// podtaknem v .form datoteke
		VelocityEngine ve = new VelocityEngine();
		//ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
		Properties p = new Properties();
		p.setProperty("resource.loader","class,jar");
		p.setProperty("class.resource.loader.description","Velocity Classpath Resource Loader");
		p.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.init(p);		

		VelocityContext context = new VelocityContext();
		context.put("patient",patient);
		
		Template template = null;
		try {
			template = ve.getTemplate("sportPacientReminderEmail-Content.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw = new StringWriter();
		template.merge(context, sw);
		
		Template template1 = null;
		try {
			template1 = ve.getTemplate("sportPacientReminderEmail-Subject.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter sw1 = new StringWriter();
		template1.merge(context, sw1);
		
		// Pridobi timestamp
		int year1  = this.exerciseDate.get(Calendar.YEAR);
		int month1 = this.exerciseDate.get(Calendar.MONTH) + 1;
		int day1   = this.exerciseDate.get(Calendar.DAY_OF_MONTH);
		String niceDate = ((day1<10)?("0"+day1):day1) + "." + ((month1<10)?("0"+month1):month1) + "." + year1;
		
		// Pridobi podrobnosti o današnji aktivnosti
		ExerciseDetails exd = new ExerciseDetails(patient.getUsername());
		exd.setForDate(this.exerciseDate);
		
		Email patientEmail = new Email(
			patient.getEmail(),
			"Spoštovani!<br /><br />" + "Po programu vadb imate za dan " + niceDate + " v načrtu vadbo tipa <b>" + exd.getType() + " (" + exd.getSubtype() + ")</b>" + " z intenzivnostjo <b>" + exd.getIntensity() + "</b> in trajanjem <b>" + exd.getDuration() + "</b> (hh:mm).<br /><br />Po opravljeni vadbi se prosimo prijavite v spletno aplikacijo (<a href=\"https://eoskrba.si\">https://eoskrba.si</a>), kjer Vas čaka nova naloga za vnos podatkov o vadbi.<br /><br />Lep dan še naprej Vam želi ekipa eŠport!<br /><br /><a href=\"https://eoskrba.si\"><img src=\"https://dl.dropbox.com/u/6942875/eSport.png\"></a>",
			"eOskrba: Vadba po programu vadb za dan " + niceDate
		);
		
		patientEmail.setFrom(SystemConstant.EMAIL_SPORT.toString());
		variables.put(Constants.VAR_patientReminderEmail, patientEmail);
		
		//zdej pripravim template še za UserTask
		Template templateDiValues = null;
		try {
			templateDiValues = ve.getTemplate("enterSpValuesPeriodic.vm","UTF-8");
		} catch (Exception e) {
			log.error("Velocity napaka", e);
		}
		StringWriter swDiVal = new StringWriter();
		templateDiValues.merge(context, swDiVal);
		
		variables.put(Constants.VAR_vnosVrednostiForm, new ValueObject(swDiVal.toString()));
		
		// Ime task-a je oblike "[WEBAPP_RENAME:studija-opisProcesa:datum]" zato, da ga webapp
		// prepozna in lahko ustrezno preimenuje. S tem webapp tudi ve, da gre za ta proces
		// in tako dinamično zapolni formo s podatki iz vadbe v programu, ki je bila nacrtovana
		// za izbran datum
		int year  = this.exerciseDate.get(Calendar.YEAR);
		int month = this.exerciseDate.get(Calendar.MONTH) + 1;
		int day   = this.exerciseDate.get(Calendar.DAY_OF_MONTH);
		String exerciseDate = year + "-" + ((month<10)?("0"+month):(month)) + "-" + ((day<10)?("0"+day):(day)) + "T00";
		variables.put(Constants.VAR_enterActTaskName, "[WEBAPP_RENAME:eSport-enterValues:" + exerciseDate + "]");		

		variables.put(Constants.VAR_maxNumberOfSentReminders, 2);
		variables.put(Constants.VAR_reminderPacientIntervalDuration, "PT24H");// TODO:
																				// spremeni
																				// na
																				// PT24H
		
		execution.setVariables(variables);

	}

}
