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
package si.pint.archetypes.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.openehr.am.parser.ParseException;

import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;

public class HujsanjeStep05ArcheSrv extends ArchetypeService {

	@Override
	public void setEmployeeType() {
		super.employeeType = EMPLOYEE_TYPE_ENUM.HUJSANJE;
	}
	
	@Override
	public boolean loadAdlAndTemplateFiles() {
		try {
			
			archetypeManager.setTemplate("OpomnikEoHuKoraki05.oet");										   
			
			archetypeManager.addArchetype("openEHR-EHR-CLUSTER.gibalna_aktivnost_dnevna_eo.v1.adl");
			archetypeManager.addArchetype("openEHR-EHR-CLUSTER.intenzivnost_opisno_5st_eo.v1.adl");
			archetypeManager.addArchetype("openEHR-EHR-CLUSTER.prehrana_dnevna_eo.v1.adl");
			archetypeManager.addArchetype("openEHR-EHR-EVALUATION.hu_koraki_05.v1.adl");
			archetypeManager.addArchetype("openEHR-EHR-OBSERVATION.tedenski_nacrt_eo.v1.adl");
			archetypeManager.addArchetype("openEHR-EHR-OBSERVATION.vprasalnik_td_premostitev_eo_hu.v1.adl");
			archetypeManager.addArchetype("openEHR-EHR-SECTION.eo_hu_korak.v1.adl");
						
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		
		return true;
	}



}
