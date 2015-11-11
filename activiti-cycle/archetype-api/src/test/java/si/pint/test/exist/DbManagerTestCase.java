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
package si.pint.test.exist;

import java.io.BufferedInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;

import junit.framework.TestCase;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.eoskrba.conf.SystemConstant;

public class DbManagerTestCase extends TestCase {

	// path to weight & height in eXist XML
	public static final String WEIGHT_EXIST_PATH = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data[@archetype_node_id='at0002']/events[@archetype_node_id='at0003']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude/text()";
	public static final String HEIGHT_EXIST_PATH = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.height.v1']/data[@archetype_node_id='at0001']/events[@archetype_node_id='at0002']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0004']/value/magnitude/text()";

	// template name
	public static final String FIRST_ENCOUNTER_TEMPLATE = "openEHR-EHR-SECTION.vkljucitev_eo.v1";

	public void testReadLastAll(){
		short numOfPatients = 0;
		String healthcareId = "ZD Novo Mesto";
		String query = "count(collection(\"/db/eDiabetes\")//*[name()=\"content\" and @archetype_node_id=\"openEHR-EHR-SECTION.vkljucitev_eo.v1\"]/*[name()=\"items\" and @archetype_node_id=\"openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1\"]/*[name()=\"data\" and @archetype_node_id=\"at0001\"]/*[name()=\"items\" and @archetype_node_id=\"at0012\" and preceding-sibling::*[@archetype_node_id=\"at0009\" and self::*/*[name()=\"value\"]/*[name()=\"value\"]/text()=\"{0}\"  ] ]/*[name() = \"value\"]/*[name()=\"value\"]/text())";
		
		Object ret = null;
		try {
			ret = DBManager.getInstance(
					SystemType.EOSKRBAPROCESSENGINE).getSingleValue(
					"sestra.di", "pacient.di",					
					EMPLOYEE_TYPE_ENUM.DIABETES, MessageFormat.format(query, new Object[]{healthcareId}) );
			numOfPatients = Short.parseShort((String)ret);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(numOfPatients > 0);
			
	}

	public void atestReadLastWeight() {

		try {

			// assertNotNull(DBManager.getInstance().readLast("zdravnik.di","EMAIL@DOMAIN.COM",
			// FIRST_ENCOUNTER_TEMPLATE, WEIGHT_EXIST_PATH));
			// assertNotNull(DBManager.getInstance().readLast("zdravnik.di","EMAIL@DOMAIN.COM",
			// FIRST_ENCOUNTER_TEMPLATE, HEIGHT_EXIST_PATH));
			String xml = DBManager.getInstance(SystemType.EOSKRBATEST)
					.readLastWholeXml("zdravnik.di",
							EMPLOYEE_TYPE_ENUM.DIABETES.toDomainName(),
							"EMAIL@DOMAIN.COM",
							"openEHR-EHR-SECTION.vkljucitev_eo.v1");
			assertNotNull(xml);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void atestBlob() throws Exception {
		String xml = DBManager.getInstance(SystemType.EOSKRBATEST)
				.readLastWholeXml("zdravnik.di",
						EMPLOYEE_TYPE_ENUM.DIABETES.toDomainName(),
						"EMAIL@DOMAIN.COM",
						"openEHR-EHR-SECTION.vkljucitev_eo.v1");

		Connection con = null;
		Statement statement = null;
		PreparedStatement select = null;
		BufferedInputStream bis = null;
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection("jdbc:postgresql://"
					+ SystemConstant.EXIST_HOST_NAME.toString()
					+ ":5432/monitoring", "monitor", "monitor");
			con.setAutoCommit(true);

			select = con
					.prepareStatement("select data from \"EVENTS\" where id=26");

			ResultSet rs = select.executeQuery();
			if (rs != null) {
				rs.next();

				String xml2 = new String(rs.getBytes("data"), "UTF-8");
				assertEquals(xml, xml2);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {

					bis.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (select != null) {
					select.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
