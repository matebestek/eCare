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
package si.pint.eoskrba.eastma.inputGeneric.config;

public class Constants {
	
	public static final String VAR_USER_APP = "user_app";
	public static int MAX_STEP = 16;
	//JSON properties
	public static final String VAR_eMAIL = "eMail"; //TODO: store user credentials (id, email, ...) at start of the process
	public static final String VAR_BID = "birpisId"; //BIRPIS id
	public static final String VAR_MOBILE_TEL_NUM = "mobilePhoneNum"; //mobile tel. number
	public static final String VAR_FIRST_NAME_LAST_NAME = "firstNameLastName"; //first & last name
	public static final String VAR_SEX = "sex"; //sex
	public static final String VAR_BIRTH_DATE = "birthDate"; //date of birth
	public static final String VAR_WEIGHT = "weight"; //patient's weight (kg)
	public static final String VAR_HEIGHT = "height"; //patient's height (cm)
	public static final String VAR_ROLE = "role"; //role (patient, doctor, caremanager)
	public static final String VAR_DOCTOR_OBJ = "doctorObj";
	public static final String VAR_CM_OBJ = "cmObj";
	public static final String VAR_FORM_KEY = "formKey";
	public static final String VAR_HC_INSTITUTION = "healthcareInstitution";
	public static final String VAR_PROCESS_STEP = "processStep";
	public static final String VAR_START_DATE = "startDate";
	public static final String VAR_ASSIGNED_COORDINATOR = "assignedCoordinator"; //for drop-down selection from list of weigh loss coordinators
	
	//patient's first visit form constants - ASTMA
	public static final String VAR_eMAIL_PATH = "?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0081]?value"; //TODO: store user credentials (id, email, ...) at start of the process
	public static final String VAR_BID_PATH = "?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0011]?value"; //BIRPIS id
	public static final String VAR_SEX_PATH = "?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0076]?value"; //sex
	public static final String VAR_BIRTH_DATE_PATH = "?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0080]?value"; //date of birth
	public static final String VAR_FIRST_NAME_PATH = "?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0004]?value";
	public static final String VAR_LAST_NAME_PATH = "?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0075]?value";
	public static final String VAR_MOBILE_TEL_NUM_PATH = "?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0082]?value";
	public static final String VAR_HC_INSTITUTION_PATH = "?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0083]?value";
	public static final String VAR_OCCUPATION_PATH = "?items[openEHR-EHR-ADMIN!ENTRY.admission!details!eo!as.v1]?data[at0003]?items[at0084]?value";
	
	//patient's first visit form constants - DIABETES
	public static final String VAR_FIRST_NAME_PATH_DI = "?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0002]?value";
	public static final String VAR_LAST_NAME_PATH_DI = "?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0003]?value";
	public static final String VAR_eMAIL_PATH_DI = "?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0007]?value"; //
	public static final String VAR_BID_PATH_DI = "?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0004]?value"; //BIRPIS id
	public static final String VAR_SEX_PATH_DI = "?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0005]?value"; //sex
	public static final String VAR_MOBILE_TEL_NUM_PATH_DI = "?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0008]?value";
	public static final String VAR_BIRTH_DATE_PATH_DI = "?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0006]?value"; //date of birth
	public static final String VAR_HC_INSTITUTION_PATH_DI = "?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0009]?value";	
	public static final String VAR_START_DATE_HU = "?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0016]?value";
	
	//other
	public static final String VAR_PATIENT_OBJ = "patientObj";//samo
	public static final String VAR_USERNAME = "username";
	public static final String VAR_USERNAME_APP = "usernameApp"; //uporabnik, ki opravlja postopek vnosa pacienta (zdravnik/sestra)
	public static final String VAR_PROCESS_TTL = "processTTL"; // največji dovoljeni čas življenja procesa - format timeDuration

	//process session variables for patient
	public static final String VAR_PROCES_GENERIC_INSTANCE_CREATION_TIMESTAMP = "procesGenericInstanceCreationTimestamp"; // Objekt tipa GregorianCalendar z datumom nastanka instance procesa Generic
	public static final String VAR_PERIODA_REMINDER = "periodaOpomnika";//npr. 24h
	public static final String VAR_NUMBER_OF_SENT_REMINDERS = "numberOfSentReminders"; //< 3
	public static final String VAR_CATEGORY_OK = "categoryOk"; //stanje pacienta po meritvi, ki ga ugotovi sistem
	public static final String VAR_CATEGORY_NOT_OK = "categoryNotOk"; //stanje pacienta po meritvi, ki ga ugotovi sistem
	public static final String VAR_CATEGORY_CRITICAL = "categoryCritical"; //stanje pacienta po meritvi, ki ga ugotovi sistem
	public static final String VAR_CATEGORY_INVALID = "categoryInvalid"; //neveljavna kategorija
	public static final String VAR_NOTIFICATION = "notification";//sporo�ilo, ki naj se po�lje uporabnikom
	public static final String VAR_EMAIL = "email";
	public static final String VAR_izracunKategorijeDelegate = "izracunKategorijeDelegate";
	public static final String VAR_listenerPacientVnosComplete = "listenerPacientVnosComplete"; 
	public static final String VAR_listenerPacientVnosCreate = "listenerPacientVnosCreate";
	public static final String VAR_listenerCaremanagerObvestiloComplete = "listenerCaremanagerObvestiloComplete";
	public static final String VAR_listenerCaremanagerObvestiloCreate = "listenerCaremanagerObvestiloCreate";
	public static final String VAR_CAREMANAGER = "caremanager";
	public static final String VAR_DOCTOR = "doctor";
	public static final String VAR_PATIENT = "patient";
	public static final String VAR_listenerDoctorObvestiloCreate = "listenerDoctorObvestiloCreate";
	public static final String VAR_listenerDoctorObvestiloComplete = "listenerDoctorObvestiloComplete";
	public static final String VAR_patientNotifySmsDelegate = "patientNotifySmsDelegate";
	public static final String VAR_listenerPatientObvestiloComplete = "listenerPatientObvestiloComplete";
	public static final String VAR_listenerPatientObvestiloCreate = "listenerPatientObvestiloCreate";
	public static final String VAR_caremanagerEmail = "caremanagerEmail";	
	public static final String VAR_initProcessDelegate = "initProcessDelegate";
	public static final String VAR_processExecutionListener = "processExecutionListener";
	public static final String VAR_cmProcessExecutionListener = "notifyCmExecutionListener";
	public static final String VAR_genericProcId = "genericProcId";
	public static final String VAR_patientReminderSmsDelegate = "patientReminderSmsDelegate";
	public static final String VAR_patientEmail = "patientEmail";
	public static final String VAR_maxNumberOfSentReminders = "maxNumberOfSentReminders";
	public static final String VAR_reminderPacientIntervalDuration = "reminderPacientIntervalDuration";
	public static final String VAR_USER_INPUTS_MAP = "userInputsMap";
	public static final String VAR_ACT_RESULT = "resultACT";
	public static final String VAR_GENERIC_RESULT = "resultGeneric"; //HashMap for values entered by patient. Used in Process-Opomnik-Vnos-Generic
	public static final String VAR_NOTIFY_CM_EMAIL = "notifyCmEmail";
	public static final String VAR_PATIENT_COOPERATES = "patientCooperates";
	public static final String VAR_doctorEmail = "doctorEmail";
	public static final String VAR_patientReminderEmail = "patientReminderEmail";
	public static final String VAR_VELOCITY_ENGINE = "velocityEngine";
	public static final String VAR_vnosVrednostiForm = "vnosVrednostiForm";
	public static final String VAR_notifyPatientForm = "notifyPatientForm";
	public static final String VAR_notifyDoctorForm = "notifyDoctorForm";
	public static final String VAR_notifyDoctorForm_1 = "notifyDoctorForm_1";
	public static final String VAR_notifyCaremanagerForm = "notifyCaremanagerForm";
	public static final String VAR_notSatisfyingConditions = "notSatisfyingConditions";
	public static final String VAR_skupinaEksperiment = "skupinaEksperiment";
	public static final String VAR_skupinaKontrolna = "skupinaKontrolna";
	public static final String VAR_notifyBoth = "notifyBoth";
	public static final String VAR_enterActTaskName = "enterActTaskName";
	public static final String VAR_userTaskName = "userTaskName";
	public static final String VAR_userTaskNameMeasurements = "userTaskNameMsrmnts";
	public static final String VAR_patientStopCoopTaskName = "patientStopCoopTaskName";
	public static final String VAR_patientUserTaskName = "patientUserTaskName";
	public static final String VAR_cmUserTaskName = "cmUserTaskName";
	public static final String VAR_doctorUserTaskName = "doctorUserTaskName";
	public static final String VAR_notifyDoctorWhenCritical = "notifyDoctorWhenCritical";
	
	public static final String VAR_MESSAGE_SMS_TEXT = "messageSmsText";
	public static final String VAR_MESSAGE_EMAIL_TEXT = "messageEmailText";
	public static final String VAR_MESSAGE_EMAIL_GREET = "messageEmailTextGreet";
	
	public static final String VAR_INPUT_SUGAR = "inputSugar";
	public static final String VAR_INPUT_DIASTOLIC_BP = "inputDiastolicBP";
	public static final String VAR_INPUT_SISTOLIC_BP = "inputSistolicBP";
	public static final String VAR_INPUT_BODY_WEIGHT = "inputBodyWeight";
	public static final String VAR_INPUT_ENERGY_INTAKE = "inputEnergyIntake";
	
	//---->>>> zaenkrat se nimajo pravih vrednosti
	//OPKP results
	public static final String VAR_INPUT_PROTEINS = "inputProteins";
	public static final String VAR_INPUT_PROTEINS_LOW = "inputProteinsLow"; //lower recomended kcal value for chot
	public static final String VAR_INPUT_PROTEINS_HIGH = "inputProteinsHigh"; //...
	public static final String VAR_INPUT_FAT = "inputFat";
	public static final String VAR_INPUT_FAT_HIGH = "inputFatHigh";
	public static final String VAR_INPUT_CARBON_HYDRATES = "inputCarbonHydrates";
	public static final String VAR_INPUT_CARBON_HYDRATES_LOW = "inputCarbonHydratesLow";
	public static final String VAR_INPUT_SIMPLE_SUGAR = "inputSimpleSugar";
	public static final String VAR_INPUT_SIMPLE_SUGAR_HIGH = "inputSimpleSugarHigh";
	
	public static final String VAR_INPUT_PROTEINS_OK = "inputProteinsOk";
	public static final String VAR_INPUT_FAT_OK = "inputFatOk";
	public static final String VAR_INPUT_CARBON_HYDRATES_OK = "inputCarbonHydratesOk";
	public static final String VAR_INPUT_SIMPLE_SUGAR_OK = "inputSimpleSugarOk";
	public static final String VAR_INPUT_BODY_MASS_OK = "bodyMassOk";
	public static final String VAR_INPUT_ENERGY_INTAKE_OK = "inputEnergyIntakeOk";
	
	//<<<<<-----
	
	public static final String VAR_INPUT_EQSW = "inputEQSWSum";
	public static final String VAR_INPUT_EQSW_VAL_LIST = "inputEQSWVals";
	
	//call activity - vnos meritev - eHujsanje
	public static final String VAR_ANSWERS_CORRECT = "answersCorrect";	
	public static final String VAR_SUBTASK_INDEX = "subTaskIndex"; 
	public static final String VAR_SUBTASK_VISIT_ARRAY = "subTaskVisitArray"; 
	public static final String VAR_CONTINUE_TO_SUBTASK = "continueToSubtask"; 
	public static final String VAR_SKIP_WORKING_SHEETS = "skipWorkingSheets";
	public static final String VAR_HUJSANJE_ANSWERS_CONTEXT = "hujsanjeAnswersContext";
	
	//eHujsanje - zgodovina delovnih listov
	public static final String VAR_HUJSANJE_HIS_WORKING_SHEETS = "hujsanjeDelovniList";
	
	
	
}
