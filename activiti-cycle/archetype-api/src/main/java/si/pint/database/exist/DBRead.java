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
package si.pint.database.exist;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.database.exist.DBManager.SystemType;

/*
 * Class for communication between GUI and exist DBManager
 * 
 * author: Vanc
 */

public class DBRead {

	private static Logger log = Logger.getLogger(DBRead.class);

	private static Map<String, String[]> querys = new HashMap<String, String[]>();
	static {
		querys.put("PEF", new String[] {"openEHR-EHR-OBSERVATION.pulmonary_function.v1", "//items[@archetype_node_id='at0058']/value/magnitude/text()" });
		querys.put("ACT", new String[] {"openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1", "//items[@archetype_node_id='at0066']/value/magnitude/text()" });
		querys.put("TELESNA_MASA", new String[] {"openEHR-EHR-SECTION.opomnik_eo_di.v1", "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()" });
		querys.put("KRVNI_SLADKOR", new String[] {"openEHR-EHR-SECTION.opomnik_eo_di.v1", "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.krvni_sladkor_eo_di.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()" });
		querys.put("SISTOLICNI_KRVNI_TLAK", new String[] {"openEHR-EHR-SECTION.opomnik_eo_di.v1", "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.krvni_tlak_eo.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()" });
		querys.put("DIASTOLICNI_KRVNI_TLAK", new String[] {"openEHR-EHR-SECTION.opomnik_eo_di.v1", "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.krvni_tlak_eo.v1']/data/events/data/items[@archetype_node_id='at0005']/value/magnitude/text()" });
		querys.put("TELESNA_MASA_EHUJSANJE_KORAK", new String[] {"openEHR-EHR-SECTION.opomnik_eo_hu.v1", "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()" });
		querys.put("TELESNA_MASA_EHUJSANJE_PV", new String[] {"openEHR-EHR-SECTION.opomnik_eo_hu_pv.v1", "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()" });
		querys.put("OBSEG_PASU_EHUJSANJE", new String[] {"openEHR-EHR-SECTION.opomnik_eo_hu.v1", "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.obseg_pasu_eo.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()" });
		querys.put("INDEKS_TELESNE_MASE_EHUJSANJE", new String[] {"openEHR-EHR-SECTION.opomnik_eo_hu.v1", "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_mass_index.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()" });
		
		querys.put("TELESNA_MASA_ESPORT", new String[] {"openEHR-EHR-SECTION.opomnik_eo_sp.v1", "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()" });
		querys.put("PANOGE_VADBE", new String[] {"openEHR-EHR-SECTION.opomnik_eo_sp.v1", "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0004']/value/value/text()" });
		querys.put("TRAJANJE_VADBE", new String[] {"openEHR-EHR-SECTION.opomnik_eo_sp.v1", "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0006']/value/value/text()" });
		querys.put("PORABLJENE_PKCAL", new String[] {"openEHR-EHR-SECTION.opomnik_eo_sp.v1", "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0057']/value/magnitude/text()" });

	}

	/*
	 * method for reading last measurement for all the patients, who have at
	 * least one measurement
	 * 
	 * type: template nameid: "PEF", "ACT"
	 */

	public static String[][] readLast(SystemType sys, String userId, String type, String id) {
		String[][] result = null;

		try {
			result = DBManager.getInstance(sys).readLastAll(userId,userId,querys.get(id)[0],
					querys.get(id)[1], EMPLOYEE_TYPE_ENUM.eval(type));
		} catch (NullPointerException e) {
			log.info("Ni pacientov!");
			return null;
		} catch (Exception e) {
			log.error("Napaka: " + e.getMessage(), e);
			return null;
		}

		return result;
	}

	/*
	 * method for reading measurements from patient in selected timespan. Also
	 * calculates min, max and avg
	 * 
	 * type: "A,S,D,H" id: "PEF", "ACT",...
	 */
	public static Report readTimeSpan(SystemType sys, String type, String id,String userId,
			String patient_id, Date start, Date finish) {

		Report result = null;
		try {
			String[][] data = DBManager.getInstance(sys).readTimespan(userId, patient_id,
					type, querys.get(id)[0], start, finish, querys.get(id)[1]);
			if (data == null)
				return null;
			result = new Report(patient_id, data);
		} catch (NullPointerException e) {
			log.info("Ni meritev v tem casu!");
		}

		catch (Exception e) {
			log.error("Napaka:" + e.getMessage(), e);
		}

		return result;
	}
}
