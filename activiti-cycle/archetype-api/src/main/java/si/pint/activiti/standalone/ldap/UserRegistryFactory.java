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
package si.pint.activiti.standalone.ldap;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import si.pint.activiti.standalone.identity.LdapConstants;
import si.pint.archetypes.exceptions.LdapException;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;

/**
 * Methods for adding & editing users in LDAP registry.
 * 
 * @author Blaz
 *
 */
/**
 * @author Owner
 *
 */
public class UserRegistryFactory {
	
	//flag: 1 if patient isn't cooperating, if 0 he cooperates 
	public static final String PATIENT_STOPPED_COOP = "FALSE";
	public static final String PATIENT_COOPERATING = "TRUE";
	
	//Subject LDAP attributes ---->>>>>>>>>>>>
	//LDAP attribute for patient processStep - nucamo pri hujšanju
	public static final String PATIENT_PROCESS_STEP_ATT = "roomNumber";
	
	//LDAP attribute for patient cooperation
	public static final String PATIENT_COOP_FLAG_ATT = "patientCoopFlagAtt";
	
	//LDAP attribute for patient's doctor
	public static final String PATIENTS_DOCTOR_ATT = "patientsDoctorAtt";
	
	//LDAP attribute for eMail
	public static final String E_MAIL_ATT = "eMailAtt";
	
	//patients doctor
	public static final String E_PATIENTS_DOCTOR = "patientsDoctorAtt";	
	
	//date of birth
	public static final String DATE_OF_BIRTH_ATT = "dateOfBirthAtt";
	
	//sex
	public static final String SEX_ATT = "sexAtt";
	
	//Birpis ID
	public static final String BID_ATT = "bidAtt";
	
	//mobile phone
	public static final String MOBILE_PHONE_ATT = "mobilePhoneAtt";
	
	//healthcare institution
	public static final String HEALTHCARE_INSTITUTION_ATT = "healthcareInstitutionAtt";
	
	//username
	public static final String UID_ATT = "uidAtt";	
	
	//surname
	public static final String SN_ATT = "sn";
	
	//common name
	public static final String CN_ATT = "cn";
	
	//group 
	public static final String MEMBER_IN_ATT = "memberAtt";
	
	//common name
	public static final String COMMON_NAME_PAT_ATT = "cnPatient";
	
	//password
	public static final String PASSWORD_ATT = "userPassword";
	
	//object class 
	public static final String OBJECT_CLASS_ATT = "objectClass";
	
	//eOskrba object attribute
	public static final String EOSKRBA_PERSON_OBJ_ATT = "eOskrbaPerson";	
	
	//eOskrba object attribute
	public static final String INET_ORG_PERSON_ATT = "inetOrgPerson";	
	
	//pager - eHujsanje - if patient is late with his program 
	public static final String PATIENT_STEPS_START_DATE = "pager";	
	

	//group attibutes ---->>>>>>>>>>>>
	//member attribute
	public static final String MEMBER_ATT = "member";
	
	//cn attribute value
	public static final String CN_PATIENT = "patient";
	
	//member attribute
	public static final String CN_USER = "user";	
	
	public static final String EMPLOYEE_TYPE = "employeeType";
	
	public enum EMPLOYEE_TYPE_ENUM {
	    ASTMA, DIABETES, SHIZOFRENIJA, HUJSANJE,SPORT;

	    public String eval(){
	        switch(this) {
	            case ASTMA:   return "A";
	            case DIABETES:  return "D";
	            case SHIZOFRENIJA:  return "S";
	            case HUJSANJE:  return "H";
	            case SPORT:  return "P";
	        }
	        throw new AssertionError("Unknown employeeType: " + this);
	    }
	    
	    public static EMPLOYEE_TYPE_ENUM eval(String _id){
	    	if (_id.equals("A"))
	    		return ASTMA;
	    	if (_id.equals("D"))
	    		return DIABETES;
	    	if (_id.equals("S"))
	    		return SHIZOFRENIJA;
	    	if (_id.equals("H"))
	    		return HUJSANJE;	
	    	if (_id.equals("P"))
	    		return SPORT;	
	    	else
	    		throw new AssertionError("Unknown employeeType: " + _id);
	    }
	    
	    public String toDomainName() {
	        switch(this) {
	        	case ASTMA:   return "eAstma";
	        	case DIABETES:  return "eDiabetes";
	        	case SHIZOFRENIJA:  return "eShizofrenija";
	        	case HUJSANJE:  return "eHujsanje";
	        	case SPORT:  return "eSport";
	        	default: return null;
	        }
	    }
	}
	
	// static variables
	private static InitialDirContext initialDirContext_instance_1 = null;
	private static long initialDirContext_instance_1_creationTime = 0;
	
	/**
	 * @param userId id uporabnika aplikacije
	 * @param patientId id pacienta za katerega uporabnik želi vpogledovat podatke
	 * @return true če sta v isti domeni (hujšanje,astma,diabetes,...)
	 * */
	public static boolean isSameDomain(String userId, String patientId){
		try {
			return findDomain(userId)==findDomain(patientId);
		} catch (LdapException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @param userId
	 * @param patientStoppedCooperating
	 * @return
	 * @throws LdapException
	 */
	public static boolean editStopCooperationFlag(String userId, boolean patientStoppedCooperating) throws LdapException {		
		
		InitialDirContext lContext = null;

		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String lSearchString = "(&(" + UID_ATT + "=" + userId + ")(objectclass=" + EOSKRBA_PERSON_OBJ_ATT + "))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);
			

			Attributes atts = null;
			if (lResults.hasMore()) {
				atts = lResults.next().getAttributes();
				Attribute attPatientStoppedCoop = atts.get(PATIENT_COOP_FLAG_ATT);
				if (attPatientStoppedCoop == null)
					return false;
					
				//int currentFlag = Integer.parseInt(attPatientStoppedCoop.toString().substring(attPatientStoppedCoop.toString().indexOf(' ') + 1));
				String isCooperating = attPatientStoppedCoop.toString().substring(attPatientStoppedCoop.toString().indexOf(' ') + 1);
				
				if (isCooperating.equals(PATIENT_COOPERATING) && !patientStoppedCooperating ||
					isCooperating.equals(PATIENT_STOPPED_COOP) && patientStoppedCooperating) //already set
					return true;
				
				attPatientStoppedCoop.set(0, patientStoppedCooperating ? PATIENT_STOPPED_COOP : PATIENT_COOPERATING);
				
				//modification item
				ModificationItem[] mods = new ModificationItem[1];
				mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attPatientStoppedCoop);
				
				//modify
				lContext.modifyAttributes(UID_ATT + "=" + userId + "," + LdapConstants.LDAP_OU_PEOPLE, mods);
				
				
			}			

		} catch (Throwable e) {
			throw new LdapException(e.getMessage());
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}			
		}			
		
		return true;
	}
	
	
	/**
	 * 
	 * @param userId
	 * @param escalateNextStep
	 * @return
	 * @throws LdapException
	 */
	public static boolean writePatientsStartDate(String userId, Date startDate) throws LdapException {		
		
		InitialDirContext lContext = null;

		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String lSearchString = "(&(" + UID_ATT + "=" + userId + ")(objectclass=" + EOSKRBA_PERSON_OBJ_ATT + "))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);
			

			Attributes atts = null;
			if (lResults.hasMore()) {
				atts = lResults.next().getAttributes();
				Attribute attPatientEscalateStep = atts.get(PATIENT_STEPS_START_DATE);
				if (attPatientEscalateStep == null)
					return false;
											

				attPatientEscalateStep.set(0, parseDate(startDate));
				
				//modification item
				ModificationItem[] mods = new ModificationItem[1];
				mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attPatientEscalateStep);
				
				//modify
				lContext.modifyAttributes(UID_ATT + "=" + userId + "," + LdapConstants.LDAP_OU_PEOPLE, mods);
				
				
			}			

		} catch (Throwable e) {
			throw new LdapException(e.getMessage());
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}			
		}			
		
		return true;
	}	
	
	
	/**
	 * Adds LDAP user to 'user' group.
	 * 
	 * @param userId
	 * @param patientStoppedCooperating
	 * @return
	 * @throws LdapException
	 */
	public static boolean addUserToActivitiGroup(String userId) throws LdapException {		
		

			InitialDirContext lContext = null;
			String patientDN = UID_ATT + "="+ userId + "," + LdapConstants.LDAP_USERS_DN_BASE;

			try {
				lContext = getLDAPContext();	
				
				// Set up Search Controls
				SearchControls lSearchControls = new SearchControls();
				lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

				// set time limit for query. Useful for preventing the application
				// from being blocked
				lSearchControls.setTimeLimit(3000);
				lSearchControls.setReturningObjFlag(true);

				// set filter
				String lSearchString = "(&(cn=" + CN_USER + ")(objectclass=groupOfNames))";

				// perform search on directory
				NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);
				
				
				Attributes atts = null;
				if (lResults.hasMore()) {
					atts = lResults.next().getAttributes();
					Attribute att = atts.get(MEMBER_ATT);
					
					boolean attNull = false;
					if (att == null) {
						att = new BasicAttribute(MEMBER_ATT);
						attNull = true;
					}
					
					att.add(patientDN);
					//modification item
					ModificationItem[] mods = new ModificationItem[1];
					if (!attNull)
						mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, att);
					else
						mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, att);
					
					//modify
					lContext.modifyAttributes(LdapConstants.LDAP_CN_USER + "," + LdapConstants.LDAP_OU_GROUPS, mods);		
			
				}			

		} catch (Throwable e) {
			throw new LdapException(e.getMessage());
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}			
		}			
		
		return true;
	}	
	
	
	/**
	 * Deletes LDAP user from 'user' group.
	 * 
	 * @param userId
	 * @param patientStoppedCooperating
	 * @return
	 * @throws LdapException
	 */
	public static boolean deleteUserFromActivitiGroup(String userId) throws LdapException {		
		

			InitialDirContext lContext = null;
			String patientDN = UID_ATT + "="+ userId + "," + LdapConstants.LDAP_USERS_DN_BASE;

			try {
				lContext = getLDAPContext();	
				
				// Set up Search Controls
				SearchControls lSearchControls = new SearchControls();
				lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

				// set time limit for query. Useful for preventing the application
				// from being blocked
				lSearchControls.setTimeLimit(3000);
				lSearchControls.setReturningObjFlag(true);

				// set filter
				String lSearchString = "(&(cn=" + CN_USER + ")(objectclass=groupOfNames))";

				// perform search on directory
				NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);
				
				
				Attributes atts = null;
				if (lResults.hasMore()) {
					atts = lResults.next().getAttributes();
					Attribute att = atts.get(MEMBER_ATT);
					
					if (att == null) {
						return true;
					}
					
					if (att.contains(patientDN)) {
						att.remove(patientDN);
						//modification item
						ModificationItem[] mods = new ModificationItem[1];
						mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, att);
						
						//modify
						lContext.modifyAttributes(LdapConstants.LDAP_CN_USER + "," + LdapConstants.LDAP_OU_GROUPS, mods);		
					}					
				}			

		} catch (Throwable e) {
			throw new LdapException(e.getMessage());
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}			
		}			
		
		return true;
	}	
	
	/**
	 * Find patient's doctor and then look for CM for this doctor.
	 * 
	 * @param patientId
	 * @return
	 * @throws LdapException 
	 */
	public static String findPatientsCMEmail(String patientId) throws LdapException {
		Attribute doctor = findRefPerson(patientId);
		String doctorUid = doctor.toString().substring(doctor.toString().indexOf(':') + 1, doctor.toString().length());
				
		return findPatientsDoctorEmail(doctorUid);
		
	}
	
	
	/**
	 * Find patient's doctor and then look for his/hers email.
	 * 
	 * @param patientId
	 * @return
	 * @throws LdapException 
	 */
	public static String findPatientsDoctorEmail(String patientId) throws LdapException {
		
		Attribute doctor = findRefPerson(patientId);
		String doctorUid = doctor.toString().substring(doctor.toString().indexOf(':') + 1, doctor.toString().length());
		
		InitialDirContext lContext = null;

		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String lSearchString = "(&(" + UID_ATT + "=" + doctorUid + ")(objectclass=" + EOSKRBA_PERSON_OBJ_ATT + "))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);
			

			Attributes atts = null;
			if (lResults.hasMore()) {
				atts = lResults.next().getAttributes();
				Attribute att = atts.get(E_MAIL_ATT);
				String mail = att.toString();
				return mail.substring(mail.indexOf(' ') + 1);
			}			

		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}			
		}			
		
		return null;
	}
	
	
	/**
	 * Find patients doctor.
	 * 
	 * @param patientId
	 * @return
	 * @throws LdapException 
	 */
	public static String findPatientsDoctor(String patientId) throws LdapException {
		Attribute doctor = findRefPerson(patientId);
		if (doctor == null)
			return null;
		
		String doctorUid = doctor.toString().substring(doctor.toString().indexOf(':') + 2, doctor.toString().length());
		return doctorUid;
	}
	
	/**
	 * Find patients CM.
	 * 
	 * @param patientId
	 * @return
	 * @throws LdapException 
	 */
	public static String findPatientsCM(String patientId) throws LdapException {
		String doctorUid = findPatientsDoctor(patientId);
		
		if (doctorUid == null)
			return null;
		
		Attribute cm = findRefPerson(doctorUid);
		String cmUid = cm.toString().substring(cm.toString().indexOf(':') + 2, cm.toString().length());
		return cmUid;
	}

	/**
	 * Find the person who is declared as 'reference person'.
	 * 
	 * patient: has a doctor
	 * caremanager: has a doctor
	 * doctor: has a caremanager
	 * 
	 * @param patientId
	 * @return
	 * @throws LdapException 
	 */
	private static Attribute findRefPerson(String userId) throws LdapException {
		InitialDirContext lContext = null;

		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String lSearchString = "(&(" + UID_ATT + "=" + userId + ")(objectclass=" + EOSKRBA_PERSON_OBJ_ATT +"))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);
			

			Attributes atts = null;
			if (lResults.hasMore()) {
				atts = lResults.next().getAttributes();
				return atts.get(PATIENTS_DOCTOR_ATT);				
			}			

		} catch (Throwable e) {
			throw new LdapException(e.getMessage());
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}			
		}			
		
		return null;
	}
	
	/**
	 * @param userId
	 * @return
	 * @throws LdapException 
	 */
	private static EMPLOYEE_TYPE_ENUM findDomain(String userId) throws LdapException {
		InitialDirContext lContext = null;

		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String lSearchString = "(&(" + UID_ATT + "=" + userId + ")(objectclass=" + EOSKRBA_PERSON_OBJ_ATT +"))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);
			

			Attributes atts = null;
			if (lResults.hasMore()) {
				atts = lResults.next().getAttributes();
				return EMPLOYEE_TYPE_ENUM.eval((String)atts.get(EMPLOYEE_TYPE).get());				
			}			

		} catch (Throwable e) {
			e.printStackTrace();
			throw new LdapException(e.getMessage());
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}			
		}			
		
		return null;
	}
		
	
	
	/**
	 * Find all patiens, who are still participating in study.
	 * 
	 * 
	 * @param patientId
	 * @return
	 * @throws LdapException 
	 */
	public static List<User> getPatientsFromLDAP(EMPLOYEE_TYPE_ENUM employeeType) throws LdapException {
		InitialDirContext lContext = null;
		List<User> subjects = new ArrayList<User>();
		
		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String lSearchString = "(&(" + MEMBER_IN_ATT + "=" + CN_PATIENT + ")(objectclass=" + EOSKRBA_PERSON_OBJ_ATT + ")(employeeType=" + employeeType.eval() + "))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);
			
			
			Attributes atts = null;
			while (lResults.hasMore()) {
				atts = lResults.next().getAttributes();
				
				//first check if cooperation flag is OK
				User u = new User();
				String coopFlag = (String) atts.get(PATIENT_COOP_FLAG_ATT).get();
				if (coopFlag.equalsIgnoreCase(PATIENT_STOPPED_COOP + ""))
					continue;
				
				String dateOfBirth = (String) atts.get(DATE_OF_BIRTH_ATT).get();
				u.setDateOfBirth(parseTimestamp(dateOfBirth).getTime());
				u.setBid((String) atts.get(BID_ATT).get());
				u.setEmail((String) atts.get(E_MAIL_ATT).get());
				u.setMobilePhone((String) atts.get(MOBILE_PHONE_ATT).get());
				u.setRole(Role.PATIENT); //function looks up only patients 
				String sex = (String) atts.get(SEX_ATT).get();
				u.setSex(sex.equals("MALE") ? User.Sex.MALE : User.Sex.FEMALE);
				u.setHealthcareInstitution((String) atts.get(HEALTHCARE_INSTITUTION_ATT).get());
				u.setUsername((String) atts.get(UID_ATT).get());		
				u.setFirstNameLastName((String) atts.get(COMMON_NAME_PAT_ATT).get());
				if(atts.get(PATIENT_PROCESS_STEP_ATT) != null){
					u.setProcessStep((String) atts.get(PATIENT_PROCESS_STEP_ATT).get());
				}
				if (atts.get(PATIENT_STEPS_START_DATE) != null) {
				    String startDateTxt = (String) atts.get(PATIENT_STEPS_START_DATE).get();
					u.setStartDate(parseTimestamp(startDateTxt).getTime());
				}
								
				subjects.add(u);

			}			

		} catch (Throwable e) {
			throw new LdapException(e.getMessage());
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}			
		}			
		
		return subjects;
	}	
	
	/**
	 * Find all patiens, who are still participating in study.
	 * 
	 * 
	 * @param doctorDN
	 * @return
	 * @throws LdapException 
	 */
	public static List<User> getDoctorsPatientsFromLDAP(String doctorDN) throws LdapException {
		InitialDirContext lContext = null;
		List<User> subjects = new ArrayList<User>();
		
		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String lSearchString = "(&(" + MEMBER_IN_ATT + "=" + CN_PATIENT + ")(objectclass=" + EOSKRBA_PERSON_OBJ_ATT + ")(patientsDoctorAtt=" + doctorDN + "))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);
			
			
			Attributes atts = null;
			while (lResults.hasMore()) {
				atts = lResults.next().getAttributes();
				
				User u = new User();
				String dateOfBirth = (String) atts.get(DATE_OF_BIRTH_ATT).get();
				u.setDateOfBirth(parseTimestamp(dateOfBirth).getTime());
				u.setBid((String) atts.get(BID_ATT).get());
				u.setEmail((String) atts.get(E_MAIL_ATT).get());
				u.setMobilePhone((String) atts.get(MOBILE_PHONE_ATT).get());
				u.setRole(Role.PATIENT); //function looks up only patients 
				String sex = (String) atts.get(SEX_ATT).get();
				u.setSex(sex.equals("MALE") ? User.Sex.MALE : User.Sex.FEMALE);
				u.setHealthcareInstitution((String) atts.get(HEALTHCARE_INSTITUTION_ATT).get());
				u.setUsername((String) atts.get(UID_ATT).get());		
				u.setFirstNameLastName((String) atts.get(COMMON_NAME_PAT_ATT).get());
				if(atts.get(PATIENT_PROCESS_STEP_ATT) != null){
					u.setProcessStep((String) atts.get(PATIENT_PROCESS_STEP_ATT).get());
				}
				if (atts.get(PATIENT_STEPS_START_DATE) != null) {
				    String startDateTxt = (String) atts.get(PATIENT_STEPS_START_DATE).get();
					u.setStartDate(parseTimestamp(startDateTxt).getTime());
				}
								
				subjects.add(u);

			}			

		} catch (Throwable e) {
			throw new LdapException(e.getMessage());
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}			
		}			
		
		return subjects;
	}	
	
	
	
	/**
	 * Find patient based on distinguished name and return Subject object.
	 * 
	 * 
	 * @param patientId
	 * @return
	 * @throws LdapException 
	 */
	public static User getUserFromLDAP(String userDN, si.pint.eoskrba.model.Role role) throws LdapException {
		InitialDirContext lContext = null;
		User s = null;
		
		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String uid = userDN.substring(0, userDN.indexOf(',') >= 0 ? userDN.indexOf(",") : userDN.length());
			String lSearchString = "(&(" + UID_ATT + "=" + uid + ")(objectclass=" + EOSKRBA_PERSON_OBJ_ATT + ")(" + MEMBER_IN_ATT + "=" + role.getName() + "))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);
			

			Attributes atts = null;
			if (lResults.hasMore()) {
				s = new User();
				atts = lResults.next().getAttributes();				
				
				if(atts.get(DATE_OF_BIRTH_ATT) != null){
					String dateOfBirth = (String) atts.get(DATE_OF_BIRTH_ATT).get();	
					s.setDateOfBirth(parseTimestamp(dateOfBirth).getTime());
				}
				if(atts.get(BID_ATT) != null){
					s.setBid((String) atts.get(BID_ATT).get());
				}
				
				s.setEmail((String) atts.get(E_MAIL_ATT).get());
				if(atts.get(MOBILE_PHONE_ATT) != null){
					s.setMobilePhone((String) atts.get(MOBILE_PHONE_ATT).get());
				}
				
				s.setRole(role); //function looks up only patients
				if(atts.get(SEX_ATT) != null){
					String sex = (String) atts.get(SEX_ATT).get();
					s.setSex(sex.equals("MALE") ? User.Sex.MALE : User.Sex.FEMALE);
				}
				if(atts.get(HEALTHCARE_INSTITUTION_ATT) != null){
					s.setHealthcareInstitution((String) atts.get(HEALTHCARE_INSTITUTION_ATT).get());
				}
				
				if (atts.get(COMMON_NAME_PAT_ATT) != null) {
					s.setFirstNameLastName((String) atts.get(COMMON_NAME_PAT_ATT).get());
				}				
				if(atts.get(EMPLOYEE_TYPE) != null){
					s.setEmployeeType(EMPLOYEE_TYPE_ENUM.eval((String) atts.get(EMPLOYEE_TYPE).get()));
				}
				if (atts.get(PATIENT_PROCESS_STEP_ATT) != null) {
					s.setProcessStep((String) atts.get(PATIENT_PROCESS_STEP_ATT).get());
				}
				if (atts.get(PATIENT_STEPS_START_DATE) != null) {
				    String startDateTxt = (String) atts.get(PATIENT_STEPS_START_DATE).get();
					s.setStartDate(parseTimestamp(startDateTxt).getTime());					
				}
				s.setUsername((String) atts.get(UID_ATT).get());
				
			}			

		} catch (Throwable e) {
			throw new LdapException("Bad attributes for " + userDN + ": " + e.getMessage());
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}			
		}			
		
		return s;
	}
	
	/**
	 * Find patient based on distinguished name and return Subject object.
	 * 
	 * @param userDN
	 * @return
	 * @throws LdapException
	 */
	public static User getUserFromLDAP(String userDN) throws LdapException {
		InitialDirContext lContext = null;
		User s = null;
		
		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String uid = userDN.substring(0, userDN.indexOf(',') >= 0 ? userDN.indexOf(",") : userDN.length());
			String lSearchString = "(&(" + UID_ATT + "=" + uid + ")(objectclass=" + EOSKRBA_PERSON_OBJ_ATT + "))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);
			

			Attributes atts = null;
			if (lResults.hasMore()) {
				s = new User();
				atts = lResults.next().getAttributes();				
				
				if(atts.get(DATE_OF_BIRTH_ATT) != null){
					String dateOfBirth = (String) atts.get(DATE_OF_BIRTH_ATT).get();	
					s.setDateOfBirth(parseTimestamp(dateOfBirth).getTime());
				}
				if(atts.get(BID_ATT) != null){
					s.setBid((String) atts.get(BID_ATT).get());
				}
				
				s.setEmail((String) atts.get(E_MAIL_ATT).get());
				if(atts.get(MOBILE_PHONE_ATT) != null){
					s.setMobilePhone((String) atts.get(MOBILE_PHONE_ATT).get());
				}
				
				if (atts.get(MEMBER_IN_ATT) != null) {
					String role = (String) atts.get(MEMBER_IN_ATT).get();
					s.setRole(Role.getRoleFromName(role)); 
				}
				
				if(atts.get(SEX_ATT) != null) {
					String sex = (String) atts.get(SEX_ATT).get();
					s.setSex(sex.equals("MALE") ? User.Sex.MALE : User.Sex.FEMALE);
				}
				if(atts.get(HEALTHCARE_INSTITUTION_ATT) != null){
					s.setHealthcareInstitution((String) atts.get(HEALTHCARE_INSTITUTION_ATT).get());
				}
				
				if (atts.get(COMMON_NAME_PAT_ATT) != null) {
					s.setFirstNameLastName((String) atts.get(COMMON_NAME_PAT_ATT).get());
				}				
				if(atts.get(EMPLOYEE_TYPE) != null){
					s.setEmployeeType(EMPLOYEE_TYPE_ENUM.eval((String) atts.get(EMPLOYEE_TYPE).get()));
				}
				if (atts.get(PATIENT_PROCESS_STEP_ATT) != null) {
					s.setProcessStep((String) atts.get(PATIENT_PROCESS_STEP_ATT).get());
				}
				if (atts.get(PATIENT_STEPS_START_DATE) != null) {
				    String startDateTxt = (String) atts.get(PATIENT_STEPS_START_DATE).get();
					s.setStartDate(parseTimestamp(startDateTxt).getTime());					
				}				
				s.setUsername((String) atts.get(UID_ATT).get());
				
			}			

		} catch (Throwable e) {
			throw new LdapException("Bad attributes for " + userDN + ": " + e.getMessage());
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}			
		}			
		
		return s;
	}
	
	
	/**
	 * Fetch the static instance of the LDAP Initial Context. All methods performed on this
	 * instance share the same connection. If static instance hasn't been created yet, a new
	 * one is instantiated. If the static instance is older than 1 minute, a new one is
	 * created.
	 * 
	 * This method is thread-safe.
	 * 
	 * @return The InitialDirContext
	 */
	private static synchronized InitialDirContext getLDAPContext() throws IOException {
		
		// check if context already exists
		if(initialDirContext_instance_1 != null) {
			
			// close context
			try {
				initialDirContext_instance_1.close();
			} catch(NamingException ne) {
				System.out.println("WARNING: [UserRegistryFactory] [getLDAPContext] NamingException:");
				ne.printStackTrace();
				initialDirContext_instance_1 = null;
			}
			
		}
		
		// Create new context
		initialDirContext_instance_1 = createNewLDAPContext();
		initialDirContext_instance_1_creationTime = new Date().getTime();

		return initialDirContext_instance_1;
		
	}
	
	/**
	 * 
	 */
	private static InitialDirContext createNewLDAPContext() {
		
		// Prepare result
		InitialDirContext lLdapCtx = null;
		
		// Set up LDAP configuration settings
		Hashtable<String, String> lContextValues = new Hashtable<String, String>();

		// Authentication
		lContextValues.put("java.naming.ldap.version", "3");
		lContextValues.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
		lContextValues.put("java.naming.security.authentication", "Simple");
		lContextValues.put("java.naming.referral", "follow");
		lContextValues.put("java.naming.provider.url", LdapConstants.LDAP_PROVIDER_URL);
		lContextValues.put("java.naming.security.principal", LdapConstants.LDAP_CONN_USERNAME);
		lContextValues.put("java.naming.security.credentials", LdapConstants.LDAP_CONN_PASSWORD);
		
		// Connection pooling
		lContextValues.put("com.sun.jndi.ldap.connect.pool", "true");
		
		// Make LDAP connection
		try {
			lLdapCtx = new InitialDirContext(lContextValues);
		} catch(NamingException ne) {
			System.out.println("[UserRegistryFactory] [createNewLDAPContext] NamingException");
			ne.printStackTrace();
			return null;
		}
		
		// return result
		return lLdapCtx;
		
	}
	
	/**
	 * Adds patient to LDAP directory.
	 * 
	 * @param s  Subject object witch contains patient data.
	 * @return
	 * @throws LdapException
	 */
	public static String addPatient(User u) throws LdapException {		
		
		InitialDirContext lContext = null;

		try {
			lContext = getLDAPContext();	
			
		    // entry's Distinguished Name
			String entryDN = UID_ATT + "=" + u.getUsername() + "," + LdapConstants.LDAP_OU_PEOPLE;
			
		    // entry's attributes
		    Attribute uid = new BasicAttribute(UID_ATT, u.getUsername()); 
		    Attribute coopFlag = new BasicAttribute(PATIENT_COOP_FLAG_ATT, PATIENT_COOPERATING + ""); //default value - patient is included in study
		    Attribute sex = new BasicAttribute(SEX_ATT, u.getSex().equals(User.Sex.MALE) ? "MALE" : "FEMALE");
		    Attribute dateOfBirth = new BasicAttribute(DATE_OF_BIRTH_ATT, parseDate(u.getDateOfBirth()));
		    Attribute bid = new BasicAttribute(BID_ATT, u.getBid());
		    Attribute eMail = new BasicAttribute(E_MAIL_ATT, u.getEmail());
		    Attribute patiensDoctor = null;
	    	patiensDoctor = new BasicAttribute(E_PATIENTS_DOCTOR, u.getPatientsDoctor());
		    Attribute mobilePhone = new BasicAttribute(MOBILE_PHONE_ATT, u.getMobilePhone());
		    Attribute healthcareInstitution = new BasicAttribute(HEALTHCARE_INSTITUTION_ATT, u.getHealthcareInstitution());
		    Attribute password = new BasicAttribute(PASSWORD_ATT, u.getPassword());
		    Attribute employeeType = new BasicAttribute(EMPLOYEE_TYPE, u.getEmployeeType().eval());
		    Attribute processStep = new BasicAttribute(PATIENT_PROCESS_STEP_ATT, "0"); //default value
	    
		    Attribute procStartDate = new BasicAttribute(PATIENT_STEPS_START_DATE, u.getStartDate() != null ? parseDate(u.getStartDate()) : parseDate(Calendar.getInstance().getTime())); //default value
		    
		    Attribute member = new BasicAttribute(MEMBER_IN_ATT, "patient"); //TODO: define java class for groups
		    Attribute commonName = new BasicAttribute(COMMON_NAME_PAT_ATT, u.getFirstNameLastName());
		    
		    Attribute cn = new BasicAttribute(CN_ATT, "dummy"); //mendatory atts for schema 'inetOrgPerson'
		    Attribute sn = new BasicAttribute(SN_ATT, "dummy");
		    		    
		    Attribute oc = new BasicAttribute(OBJECT_CLASS_ATT);
		    oc.add(EOSKRBA_PERSON_OBJ_ATT);
		    oc.add(INET_ORG_PERSON_ATT);		    
			
	        // build the entry
	        Attributes entry = new BasicAttributes();
	        entry.put(uid);
	        if (coopFlag != null) entry.put(coopFlag);
	        entry.put(sex);
	        if (dateOfBirth != null) entry.put(dateOfBirth);
	        if (bid != null) entry.put(bid);
	        entry.put(eMail);
	        if (mobilePhone != null) entry.put(mobilePhone);		    
	        if (healthcareInstitution != null) entry.put(healthcareInstitution);
	        entry.put(password);
        	entry.put(patiensDoctor);
	        entry.put(oc);
	        entry.put(member);
	        entry.put(commonName);
	        entry.put(sn);
	        entry.put(cn);
	        entry.put(employeeType);
	        if (u.getEmployeeType().equals(EMPLOYEE_TYPE_ENUM.HUJSANJE)) {
	        	entry.put(processStep);
	        	entry.put(procStartDate);
	        }	        
	        
	        // Add the entry
	        lContext.createSubcontext(entryDN, entry);

		} catch (Throwable e) {
			throw new LdapException(e.getMessage());
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}			
		}			
		
		//add to 'user' group
		UserRegistryFactory.addUserToActivitiGroup(u.getUsername());
		
		return u.getUsername();
	}
	
	
	/**
	 * Removes patient from LDAP directory.
	 * 
	 * @param username  Subject object witch contains patient data (e.g. janez.novak@gmail.com)
	 * @return
	 * @throws LdapException
	 */
	public static boolean removeUser(String username) throws LdapException {		
		InitialDirContext lContext = null;
		
		String entryDN = UID_ATT + "=" + username + "," + LdapConstants.LDAP_OU_PEOPLE;
		
			try {
				lContext = getLDAPContext();
				lContext.destroySubcontext(entryDN);				
			} catch (NamingException e) {				
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		UserRegistryFactory.deleteUserFromActivitiGroup(username);
		
		return true;
	}
	
	
	/**
	 * Date of birth is stored in LDAP as string in format yyyyMMdd.
	 * 
	 * @param timestamp
	 * @return
	 * @throws Exception
	 */
	public static Calendar parseTimestamp(String timestamp) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Date d = sdf.parse(timestamp);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return cal;
	}
	
	/**
	 * Convert Date object to String in format yyyyMMdd.
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static String parseDate(Date dateOfBirth) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		return sdf.format(dateOfBirth);
	}	
	
	
	/**
	 * 
	 * @param lo
	 * @param hi
	 * @return
	 */
    private static int rand(int lo, int hi)
    {
            int n = hi - lo + 1;
            int i = (new Random()).nextInt() % n;
            if (i < 0)
                    i = -i;
            return lo + i;
    }

    /**
     * Function returns random string.
     * 
     * @param lo
     * @param hi
     * @return
     */
    public static String randomstring(int lo, int hi)
    {
            int n = rand(lo, hi);
            byte b[] = new byte[n];
            for (int i = 0; i < n; i++)
                    b[i] = (byte)rand('a', 'z');
            return new String(b, 0);
    }


	public static void updatePatientsProcessStep(User patient) throws LdapException {

		InitialDirContext lContext = null;

		try {
			lContext = getLDAPContext();

			// Set up Search Controls
			SearchControls lSearchControls = new SearchControls();
			lSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

			// set time limit for query. Useful for preventing the application
			// from being blocked
			lSearchControls.setTimeLimit(3000);
			lSearchControls.setReturningObjFlag(true);

			// set filter
			String lSearchString = "(&(" + UID_ATT + "=" + patient.getUsername() + ")(objectclass=" + EOSKRBA_PERSON_OBJ_ATT + "))";

			// perform search on directory
			NamingEnumeration<SearchResult> lResults = lContext.search(LdapConstants.LDAP_PROVIDER_URL, lSearchString, lSearchControls);
			

			Attributes atts = null;
			if (lResults.hasMore()) {
				atts = lResults.next().getAttributes();
				Attribute attPatientProcessStep = atts.get(PATIENT_PROCESS_STEP_ATT);
				attPatientProcessStep.set(0, patient.getProcessStep());
				
				//modification item
				ModificationItem[] mods = new ModificationItem[1];
				mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attPatientProcessStep);
				
				//modify
				lContext.modifyAttributes(UID_ATT + "=" + patient.getUsername() + "," + LdapConstants.LDAP_OU_PEOPLE, mods);
				//Cannot add duplicate to unordered attribute
				
			}			

		} catch (Throwable e) {
			throw new LdapException(e.getMessage());
		} finally {
			try {
				if (lContext != null)
					lContext.close();
			} catch (NamingException e) {
			}			
		}			
		
		
	}	

}
