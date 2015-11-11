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
/**
 * 
 */
package si.pint.eoskrba.ediabetes.inputFlat;

import junit.framework.TestCase;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.pvm.runtime.ExecutionImpl;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.ediabetes.vnosPacienta.delegate.SelectClinicalTrialGroupDelegate;
import si.pint.eoskrba.model.Role;
import si.pint.eoskrba.model.User;

/**
 * @author mate
 *
 */
public class DelegateTester extends TestCase {

	protected DelegateExecution execution;
	
	/**
	 * @param name
	 */
	public DelegateTester(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		User patient = UserRegistryFactory.getUserFromLDAP("mate", Role.PATIENT);
		//FromLDAP("uid=mate,ou=people,dc=eoskrba,dc=pint,dc=upr,dc=si");
		assertNotNull(patient);
		
		
		execution = new ExecutionImpl();
		execution.setVariable(Constants.VAR_PATIENT, patient);
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	public void testHappyPath(){
		SelectClinicalTrialGroupDelegate d = new SelectClinicalTrialGroupDelegate();
//		execution.setVariable(Constants.VAR_ACT_RESULT, vsotaACT);
		try {
			d.execute(execution);
			assertNotNull(execution.getVariable(Constants.VAR_caremanagerEmail));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	
}
