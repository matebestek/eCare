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
package si.pint.eoskrba.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.NotImplementedException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.archetypes.exceptions.LdapException;

/**
 * 
 * Class for storing users (patients, doctors, caremanagers) data
 * 
 * @author Blaž,Mate
 *
 */
@Log4j
public class User implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2105062586748588525L;
	
	//users data
	@Getter @Setter private String processStep;//pri hujšanju rabimo info o tem na katerem koraku je user od 16ih
	@Getter @Setter private String email;
	@Getter @Setter private String bid;
	@Getter @Setter private String mobilePhone;
	@Getter @Setter private Sex sex;
	@Getter @Setter private Date dateOfBirth;
	@Getter @Setter private BigDecimal weight;
	@Getter @Setter private BigDecimal height;
	@Getter @Setter private Role role;
	@Getter @Setter private String username;
	@Getter @Setter private String password;
	@Getter @Setter private String firstNameLastName;
	@Getter @Setter private String healthcareInstitution;
	@Getter @Setter private String patientsDoctor;
	@Getter @Setter private String patientsCM;
	@Getter @Setter private boolean isExperimentalGroup;
	@Getter @Setter private Date startDate; //eHujsanje: first day of weight loss workshop
	@Getter @Setter private EMPLOYEE_TYPE_ENUM employeeType;
	@Getter(lazy=true) private final int age = calcAge();//enkrat izračuna in potem cache-ira vrednost - dokler instanca objekta živi seveda:)
	@Getter(lazy=true) private final String cmEmail = calcCmEmail();
	@Getter(lazy=true) private final String doctorEmail = calcDoctorEmail();
	@Getter(lazy=true) private final User caremanager = loadCM();
	@Getter(lazy=true) private final User doctor = loadDoctor();
	
	public enum Sex {MALE, FEMALE}
	/**
	 * Calculate age from patients birthday.
	 */
	private int calcAge() {
		Calendar c = Calendar.getInstance();
		
		// Get msec from each, and subtract.
	    long diff = c.getTime().getTime() - this.getDateOfBirth().getTime();
	    long devideWith = (long) 1000 * (long) 60 * 60 * 24 * 365;
	    BigDecimal diffBD = new BigDecimal(diff);
	    BigDecimal devideWithBD = new BigDecimal(devideWith);
	    BigDecimal result = diffBD.divide(devideWithBD, RoundingMode.FLOOR);
	    return result.intValue();
	}
	
	/**
	 * Look for CMs eMail in LDAP.
	 * 
	 * @return
	 */
	private String calcCmEmail() {
		try {
			return UserRegistryFactory.findPatientsCMEmail(this.getUsername());
		} catch (LdapException e) {
			log.error("Napaka pri pridobivanju cm email-a za pacienta " + this.getUsername(), e);			
		}
		return null;
	}
	
	
	/**
	 * Look for CMs eMail in LDAP.
	 * 
	 * @return
	 */
	private String calcDoctorEmail() {
		try {
			return UserRegistryFactory.findPatientsDoctorEmail(this.getUsername());
		} catch (LdapException e) {
			log.error("Napaka pri pridobivanju doctor email-a za pacienta " + this.getUsername(), e);
		}
		return null;
	}
	

	/**
	 * Look for Doctor's username.
	 * 
	 * @return
	 */
	public String findDoctor() {
		try {
			return UserRegistryFactory.findPatientsDoctor(this.getUsername());
		} catch (LdapException e) {
			log.error("Napaka pri pridobivanju zdravnika za pacienta " + this.getUsername(), e);
		}
		return null;
	}
	
	/**
	 * Look for Cm's username.
	 * 
	 * @return
	 */	
	public String findCM() {
		try {
			return UserRegistryFactory.findPatientsCM(this.getUsername());
		} catch (LdapException e) {
			log.error("Napaka pri pridobivanju cm za pacienta " + this.getUsername(), e);
		}
		return null;
	}	
	
	private User loadCM(){
		try {
			String cm = null;
			if (this.role.equals(Role.PATIENT))
				cm = UserRegistryFactory.findPatientsCM(this.getUsername());
			else if (this.role.equals(Role.DOCTOR))
				cm = UserRegistryFactory.findPatientsDoctor(this.getUsername());
			else //cannot call 'loadCM' on CM object
				throw new NotImplementedException();
			
			if (cm != null) {
				User caremanager = UserRegistryFactory.getUserFromLDAP(cm, si.pint.eoskrba.model.Role.CAREMANAGER);
				return caremanager;
			}
		} catch (LdapException e) {
			log.error("Napaka pri pridobivanju cm za pacienta " + this.getUsername(), e);
		}
		
		//if patient not in LDAP
		if (this.getPatientsDoctor() != null) {
			try {
				String cm = UserRegistryFactory.findPatientsDoctor(this.getPatientsDoctor());
				return UserRegistryFactory.getUserFromLDAP(cm, Role.CAREMANAGER);
			} catch (LdapException e) {
				log.error("Napaka pri pridobivanju cm za pacienta " + this.getUsername(), e);
			}
		}
		return null;
	}
	
	private User loadDoctor() {
		try {
			String doctorDN = UserRegistryFactory.findPatientsDoctor(this.getUsername());
			User doctor = UserRegistryFactory.getUserFromLDAP(doctorDN, Role.DOCTOR);
			return doctor;
		} catch (LdapException e) {
			log.error("Napaka pri pridobivanju zdravnika za pacienta " + this.getUsername(), e);
		}
		return null;
	}
	
}
