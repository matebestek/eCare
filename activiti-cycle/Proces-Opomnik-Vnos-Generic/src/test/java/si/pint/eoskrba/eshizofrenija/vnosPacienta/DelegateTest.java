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
package si.pint.eoskrba.eshizofrenija.vnosPacienta;

import junit.framework.TestCase;
import lombok.extern.log4j.Log4j;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.pvm.runtime.ExecutionImpl;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.archetypes.exceptions.LdapException;
import si.pint.archetypes.service.VnosPacientaShizofrenijaArcheSrv;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.eastma.inputGeneric.config.Constants;
import si.pint.eoskrba.eshizofrenija.vnosPacienta.delegate.SelectClinicalTrialGroupDelegate;
import si.pint.eoskrba.model.Role;

@Log4j
public class DelegateTest extends TestCase {

	protected DelegateExecution execution;
	protected static String TEST_UID = "EMAIL@DOMAIN.COM";

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		execution = new ExecutionImpl();
		
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

	/**
	 * Exceptions:
	 * 	LDAP:
	 * 		1.si.pint.archetypes.exceptions.LdapException: [LDAP: error code 20 - member: value #7 provided more than once]-->iz grupe je treba zbrisat userja, če si ga že zbrisal iz ldap-a. V grupi pa je
	 * 																														  predvsem zaradi activiti, ki uporablja grupe.
	 * */
	
	public void testSelectClinicalTrialGroupDelegate() {
		VnosPacientaShizofrenijaArcheSrv as = new VnosPacientaShizofrenijaArcheSrv();
		as.initInput();

		
		SelectClinicalTrialGroupDelegate d = new SelectClinicalTrialGroupDelegate();
		setUser();
		
		//ime
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0002]?value", "Janko");
		
		//priimek
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0003]?value", "Novak");
		
		//bis
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0004]?value", "bid.janko.novak");
		
		//spol
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0005]?value", "at0013"); //moski
		
		//datum rojstva
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0006]?value", "01.07.1980");
		
		//visina
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.height.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", new Integer("185"));
		
		//teza
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.body!weight.v1]?data[at0002]?events[at0003]?data[at0001]?items[at0004]?value", new Integer("80"));
		
		//indeks telesne mase
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.body!mass!index.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?value", "8.5");
		
		//email
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0007]?value", TEST_UID);
		
		//GSM
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0008]?value", "PHONE-NUMBER");
		
		//zdravstvena ustanova
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0009]?value", "ZD Novo Mesto");
		
		//poklic
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0010]?value", "tesar");
		
		//ostali pogoji
		execution.setVariable("?items[openEHR-EHR-ADMIN!ENTRY.osnovni!podatki!eo.v1]?data[at0001]?items[at0011]?value", "true");			
			
		//laboratorij - eritrociti
		execution.setVariable("?items[openEHR-EHR-OBSERVATION.laboratorij!eo!sh.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0005]?items[at0004]?value", "30.5");
		
		//pogostost pitja
		execution.setVariable("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0006");
		
		//kolicina pijace
		execution.setVariable("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0008]?value", "at0012");
		
		//Pogostost pitja vecjih kolicin
		execution.setVariable("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0014]?value", "at0016");
		
		//stevilo dosezenih tock
		execution.setVariable("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0020]?value", new Integer("9"));
		
		//ocena ogrozenosti
		execution.setVariable("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0022]?value", "at0024");
		
		//komentar
		execution.setVariable("?items[openEHR-EHR-EVALUATION.alkohol!eo!pp.v1]?data[at0001]?items[at0021]?value", "komentar za alko");
		
		//kajenje ---------->>>>
		
		//kadilski status
		execution.setVariable("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0015");
		
		//dnevno stevilo cigaret
		execution.setVariable("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0008]?items[at0007]?value", new Integer("6"));
		
		//datum zacetka uzivanja
		execution.setVariable("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0008]?items[at0009]?value", "1990-01-01");
		
		//starost ob zacetku uzivanja
		execution.setVariable("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0008]?items[at0011]?value", new Integer("14"));
		
		//poskus prenehanja
		execution.setVariable("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0016]?items[at0017]?value", "at0019");
		
		//datum prenehanja
		execution.setVariable("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0016]?items[at0010]?value", "2000-01-01");
		
		//starost ob prenehanju
		execution.setVariable("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0016]?items[at0012]?value", new Integer("22"));
		
		//stopnja ogrozenosti
		execution.setVariable("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0022]?value", "at0023");
		
		//komentar
		execution.setVariable("?items[openEHR-EHR-EVALUATION.kajenje!eo!pp.v1]?data[at0001]?items[at0013]?value", "kadilski komentar");
		
		//telesna dejavnost ---------->>>>
		
		//visoko intenzivna vadba
		execution.setVariable("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0002]?value", "at0005");
		
		//zmerno intenzivna vadba
		execution.setVariable("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0012]?value", "at0015");
		
		//stevilo dosezenih tock
		execution.setVariable("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0022]?value", new Integer("8"));
		
		//ogrozenost
		execution.setVariable("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0024]?value", "at0026");
		
		//komentar
		execution.setVariable("?items[openEHR-EHR-EVALUATION.telesna!dejavnost!eo!pp.v1]?data[at0001]?items[at0023]?value", "komentar za telesno dejavnost");

		//diagnoza
		execution.setVariable("?items[openEHR-EHR-EVALUATION.diagnoza!eo.v1]?data[at0001]?items[at0002]?value", "diagnoza umor");
		
		//diagnoza (MKB10)
		execution.setVariable("?items[openEHR-EHR-EVALUATION.diagnoza!eo.v1]?data[at0001]?items[at0003]?value", "diagnoza MKB10");
		
		// <----- 4. terapija ------->
		
		//opis terapije
		execution.setVariable("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[at0004]?value", "to je opis terapije");

		//ime zdravila
		execution.setVariable("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0002]?value", "zdravilo XY");
		
		//gen. ime
		execution.setVariable("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0003]?value", "gen. ime XY");
		
		//ATC koda
		execution.setVariable("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0028]?value", "ATC koda 1234XXX");
		
		//stevilo pakiranj
		execution.setVariable("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0006]?items[at0018]?value", new Integer("40"));
		
		//zdravila - kolicina (st/mg/ml/vdih)
		execution.setVariable("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0005]?items[at0008]?value", new Integer("12"));
		
		//pogostost jemanja
		execution.setVariable("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0005]?items[at0009]?value", "at0012");

		//ste danes vzeli vsa zdravila
		execution.setVariable("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0007]?items[at0019]?value", "FALSE");
				
		//kako redno ste v zadnjih 14 dneh jemali zdravila
		execution.setVariable("?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0007]?items[at0020]?value", "at0023");		
		


		try {
			d.execute(execution);
			assertFalse("Incorrect user data!", (Boolean) execution.getVariable(Constants.VAR_notSatisfyingConditions));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		//sedaj naredim cleanup
		//1. zbrišem uporabnika iz LDAP
		//delete user from LDAP
		try {
			//if (UserRegistryFactory.getUserFromLDAP(TEST_UID, Role.PATIENT) != null) {
			UserRegistryFactory.removeUser(TEST_UID);
			//}
			assertNull(UserRegistryFactory.getUserFromLDAP(TEST_UID, Role.PATIENT));
		} catch (LdapException e) {
			e.printStackTrace();
			fail();
		}
		
		//2. zbrišem zapis uporabnika v eXist
		try {
			EMPLOYEE_TYPE_ENUM et = EMPLOYEE_TYPE_ENUM.SHIZOFRENIJA;			
			DBManager.getInstance(SystemType.EOSKRBAPROCESSENGINE).deleteCollection(TEST_UID, et.toDomainName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		
	}
	
	
	
	private void setUser() {
		
		//set patient's doctor from session (eOskrba-webApp) 
		execution.setVariable(Constants.VAR_USERNAME_APP,"zdravnik.sh"); 
		
		execution.setVariable(Constants.VAR_BID, "fdsfdsa11111");
		execution.setVariable(Constants.VAR_SEX, "MALE");
		execution.setVariable(Constants.VAR_BIRTH_DATE, "01.01.1921" );
		execution.setVariable(Constants.VAR_eMAIL, TEST_UID);
		
		execution.setVariable(Constants.VAR_FIRST_NAME_PATH, "Mate");
		execution.setVariable(Constants.VAR_LAST_NAME_PATH, "Beštek");
		execution.setVariable(Constants.VAR_MOBILE_TEL_NUM, "PHONE-NUMBER");
		execution.setVariable(Constants.VAR_HC_INSTITUTION, "KC-0A");		
	}

}
