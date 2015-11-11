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

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.archetypes.exceptions.LdapException;

/**
 * 
 * Class for storing all patients (as well doctors & caremanagers) data
 * 
 * @author Blaz
 *
 */
@Deprecated public class Subject implements Serializable {

	//process session variables for patient
	@Deprecated 
	public static final String VAR_PATIENT_OBJ = "patientObj"; 
	
	//JSON properties
	@Deprecated public static final String VAR_EMAIL = "eMail"; //TODO: store user credentials (id, email, ...) at start of the process
	@Deprecated public static final String VAR_BID = "bid"; //BIRPIS id
	@Deprecated public static final String VAR_MOBILE_TEL_NUM = "mobilePhoneNum"; //mobile tel. number
	@Deprecated public static final String VAR_SEX = "sex"; //sex
	@Deprecated public static final String VAR_BIRTH_DATE = "birthDate"; //date of birth
	@Deprecated public static final String VAR_WEIGHT = "weight"; //patient's weight (kg)
	@Deprecated public static final String VAR_HEIGHT = "height"; //patient's height (cm)
	@Deprecated public static final String VAR_ROLE = "role"; //role (patient, doctor, caremanager)
	@Deprecated public static final String VAR_USERNAME = "username"; //username 
	

	@Deprecated public enum Sex {MALE, FEMALE}
	
	//subjects data
	private String eMail;
	private String bid;
	private String mobilePhone;
	private Sex sex;
	private Date dateOfBirth;
	private BigDecimal weight;
	private BigDecimal height;
	private Role role;
	private String healthcareInstitution;
	private String username;
	
	public String geteMail() {
		return eMail;
	}
	public void seteMail(String eMail) {
		this.eMail = eMail;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public Sex getSex() {
		return sex;
	}
	public void setSex(Sex sex) {
		this.sex = sex;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public BigDecimal getWeight() {
		return weight;
	}
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
	public BigDecimal getHeight() {
		return height;
	}
	public void setHeight(BigDecimal height) {
		this.height = height;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}	
	
	public String getHealthcareInstitution() {
		return healthcareInstitution;
	}
	public void setHealthcareInstitution(String healthcareInstitution) {
		this.healthcareInstitution = healthcareInstitution;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * Calculate age from patients birthday.
	 */
	public int getAge() {
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
	public String getCMsEmail() {
		try {
			return UserRegistryFactory.findPatientsCMEmail(this.getUsername());
		} catch (LdapException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Look for CMs eMail in LDAP.
	 * 
	 * @return
	 */
	public String getDoctorsEmail() {
		try {
			return UserRegistryFactory.findPatientsDoctorEmail(this.getUsername());
		} catch (LdapException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * Look for Doctor's username.
	 * 
	 * @return
	 */
	public String getDoctor() {
		try {
			return UserRegistryFactory.findPatientsDoctor(this.getUsername());
		} catch (LdapException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Look for Cm's username.
	 * 
	 * @return
	 */	
	public String getCM() {
		try {
			return UserRegistryFactory.findPatientsCM(this.getUsername());
		} catch (LdapException e) {
			e.printStackTrace();
		}
		return null;
	}
}
