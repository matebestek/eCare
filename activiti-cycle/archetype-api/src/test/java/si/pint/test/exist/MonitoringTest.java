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
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class MonitoringTest extends TestCase {

	/*
	 * 
	 * drop table blobdemo;
	 * 
	 * drop sequence blobdemo_id_seq;
	 * 
	 * create table blobdemo( id serial not null primary key, name varchar(50),
	 * content bytea);
	 */

	private final static String NAME = "TEST";
	// private final static String FILE_NAME = "/tmp/blob/2mb.xxx";
	private final static String FILE_NAME = "C:/Users/NAME/Downloads/openEHR_adoption_framework.pdf";

	public void AtestBlob() throws Throwable {
		Connection con = null;
		Statement statement = null;
		PreparedStatement insertStatement = null;
		ResultSet rs = null;
		File file = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;

		try {
			System.out.println("BLOB/PostgreSQL Demo started");

			Class.forName("org.postgresql.Driver");

			con = DriverManager.getConnection(
					"jdbc:postgresql://88.200.63.187:5432/monitoring", "monitor", "monitor");
			con.setAutoCommit(true);

			//statement = con.createStatement();

			// cleanup
			//statement.executeUpdate("delete from blobdemo");

			// file
			file = new File(FILE_NAME);
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);

			// insert one record
			System.out.println("preparing statement");
			/*INSERT INTO "EVENTS"(
		            id, "type", "system", "timestamp", method, data, "applicationUser", 
		            "accessedUser")
		    VALUES (?, ?, ?, ?, ?, ?, ?, 
		            ?);*/
			insertStatement = con.prepareStatement("insert into \"EVENTS\" (\"type\", \"system\", \"timestamp\", method, data, \"applicationUser\", \"accessedUser\") values (?,?,?,?,?,?,? )");
			insertStatement.setInt(1, 1);
			insertStatement.setString(2, "MonitoringTest");
			GregorianCalendar gc = new GregorianCalendar();			
			insertStatement.setString(3, "datum");
			insertStatement.setString(4, "testBlob");
			insertStatement.setBinaryStream(5, bis, (int) file.length());
			insertStatement.setString(6, "applicationUser");
			insertStatement.setString(7, "accessedUser");
			

			System.out.println("executing update");
			insertStatement.executeUpdate();

			// retrieve
//			rs = statement
//					.executeQuery("select id, data from EVENTS where id = " + id);
//
//			while (rs.next()) {
//				System.out.println("id=" + rs.getObject(1));
//				
//				byte[] bytes = rs.getBytes(2);
//				String content = new String(bytes);
//
//				// System.out.println(content);
//				System.out.println("retrieved " + bytes.length + " bytes");
//			}
		} finally {

			if (rs != null)
				rs.close();
			if (statement != null)
				statement.close();
			if (insertStatement != null)
				insertStatement.close();
			if (con != null)
				con.close();

			if (fis != null)
				fis.close();
			if (bis != null)
				bis.close();
		}

		System.out.println("BLOB/PostgreSQL Demo complete");
	}

}
