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
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;

public class EhrTest extends TestCase {

	public void atestVisit() throws Throwable {
		Connection con = null;
		Statement statement = null;
		PreparedStatement insertStatement = null;
		ResultSet rs = null;
		File file = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;

		try {
			System.out.println("PostgreSQL Demo started");

			Class.forName("org.postgresql.Driver");

			con = DriverManager.getConnection(
					"jdbc:postgresql://88.200.63.187:5432/ehr", "ehr", "ehr");
			con.setAutoCommit(true);

			//statement = con.createStatement();

			// cleanup
			//statement.executeUpdate("delete from blobdemo");

			// insert one record
			System.out.println("preparing statement");
			/*"insert into \"VISIT\" (\"appUserId\", \"appUserRole\", \"patientId\", \"visitFileName\",\"timestamp\") values (?,?,?,?,?)"*/
			insertStatement = con.prepareStatement("insert into \"visit\" (\"appUserId\", \"appUserRole\", \"patientId\", \"visitFileName\",\"timestamp\") values (?,?,?,?,?)");
			insertStatement.setString(1, "sestra.di");
			insertStatement.setString(2, "CAREMANAGER");						
			insertStatement.setString(3, "pacient.di");
			insertStatement.setString(4, "dadasddasd.xml");
			insertStatement.setString(5, new SimpleDateFormat("dd.MM.yyyy:hh.mm.ss").format(new Date(System.currentTimeMillis())));	
			

			System.out.println("executing update");
			insertStatement.executeUpdate();

		
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
