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
package si.pint.test.ldap;

import java.util.Date;

import junit.framework.TestCase;
import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.archetypes.exceptions.LdapException;
import si.pint.eoskrba.conf.SystemConstant;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;

public class UserRegistryTestCase extends TestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetPatients() {
		try {
			assertNotNull(UserRegistryFactory.getPatientsFromLDAP(EMPLOYEE_TYPE_ENUM.ASTMA));
		} catch (LdapException e) {
			e.printStackTrace();
			fail();
		}		
	}
	
	
	public void testEditStopCooperationFlag() {
		try {
			String username = UserRegistryFactory.addPatient(generateUser()); 
			assertNotNull(username);
			assertTrue(UserRegistryFactory.editStopCooperationFlag(username, true));
			assertTrue(UserRegistryFactory.removeUser(username));
			
		} catch (LdapException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	


	
	public void testFindPatiensCmsEmail() {
		try {
			assertEquals("zveci.orbit@gmail.com", UserRegistryFactory.findPatientsCMEmail("ivan.pacient"));
		} catch (LdapException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Test if we can find patient's cm and doctor. 
	 */
	public void testUserGetCM() {
		try {
			User u = generateUser();
			u.setUsername("ivan.pacient");
			
			assertEquals("bojana.oskrbovalec", u.findCM());
			assertEquals("miha.zdravnik", u.findDoctor());			
		} catch (NullPointerException e) {
			e.printStackTrace();
			fail();
		}
	}	
	
	/**
	 * Add & remove user to 'user' group.
	 */
	public void testAddRemoveGroup() {
		
		try {
			assertTrue(UserRegistryFactory.addUserToActivitiGroup("janez.test@test.com"));
			assertTrue(UserRegistryFactory.deleteUserFromActivitiGroup("janez.test@test.com"));
		} catch (LdapException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * Add & remove user.
	 */
	public void testAddRemoveUser() {
		
		try {
			String username = UserRegistryFactory.addPatient(generateUser()); 
			assertNotNull(username);
			assertTrue(UserRegistryFactory.removeUser(username));
			
		} catch (LdapException e) {
			e.printStackTrace();
		}
		
	}
	
	public void testSystemConstants() {
		assertNotNull(SystemConstant.ACTIVITI_REST_HOST_NAME);
	}
		
	private User generateUser() {
		User u = new User();
		
		u.setBid("hufsduhfs");
		u.setEmail("bkurent@gmai.com");
		u.setMobilePhone("030333777");
		u.setSex(User.Sex.MALE);
		
		Date date = null;		
		try {
			date = UserRegistryFactory.parseTimestamp("10.10.1910").getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		u.setDateOfBirth(date);
		u.setRole(Role.PATIENT);
		u.setUsername("bkurent@gmai.com");
		u.setPassword(UserRegistryFactory.randomstring(6, 8));
		u.setFirstNameLastName("NAME");
		
		u.setHealthcareInstitution("KC-0A");
		u.setPatientsDoctor("miha.zdravnik");
		
		return u;
	}
	
	public void testGetUserFromLDAP() {
		try {
			assertNotNull(UserRegistryFactory.getUserFromLDAP("miha.zdravnik", Role.DOCTOR));
		} catch (LdapException e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
