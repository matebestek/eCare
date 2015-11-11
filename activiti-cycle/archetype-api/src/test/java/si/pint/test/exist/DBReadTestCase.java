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

import java.util.Date;

import junit.framework.TestCase;
import si.pint.database.exist.DBRead;
import si.pint.database.exist.Report;
import si.pint.database.exist.DBManager.SystemType;

public class DBReadTestCase extends TestCase {

	public void testDiabetesReports() {
		String[][] out1 = DBRead.readLast(SystemType.EOSKRBATEST,"zdravnik.di","D", "TELESNA_MASA");
		assertNotNull(out1);
		for (int i = 0; i < out1.length; i++) {
			System.out.println("out1: " + out1[i][0] + " " + out1[i][1] + " "
					+ out1[i][2]);
		}
		String[][] out2 = DBRead.readLast(SystemType.EOSKRBATEST,"zdravnik.di","D", "KRVNI_SLADKOR");
		assertNotNull(out2);
		for (int i = 0; i < out2.length; i++) {
			System.out.println("out1: " + out2[i][0] + " " + out2[i][1] + " "
					+ out2[i][2]);
		}
		out2 = DBRead.readLast(SystemType.EOSKRBATEST,"zdravnik.di","D", "SISTOLICNI_KRVNI_TLAK");
		assertNotNull(out2);
		for (int i = 0; i < out2.length; i++) {
			System.out.println("out1: " + out2[i][0] + " " + out2[i][1] + " "
					+ out2[i][2]);
		}
		out2 = DBRead.readLast(SystemType.EOSKRBATEST,"zdravnik.di","D", "DIASTOLICNI_KRVNI_TLAK");
		assertNotNull(out2);
		for (int i = 0; i < out2.length; i++) {
			System.out.println("out1: " + out2[i][0] + " " + out2[i][1] + " "
					+ out2[i][2]);
		}
		
		Report outReport1 = DBRead.readTimeSpan(SystemType.EOSKRBATEST,"D", "TELESNA_MASA",
				"ivan.pacient","ivan.pacient", new Date(Long.MIN_VALUE), new Date(
						Long.MAX_VALUE));
		assertNotNull(outReport1);
		System.out.println("TELESNA_MASA: ");
		for (int i = 0; i < outReport1.getTable().length; i++) {
			System.out.println(outReport1.getTable()[i][0] + " "
					+ outReport1.getTable()[i][1]);
		}
		
		outReport1 = DBRead.readTimeSpan(SystemType.EOSKRBATEST,"D", "KRVNI_SLADKOR",
				"ivan.pacient","ivan.pacient", new Date(Long.MIN_VALUE), new Date(
						Long.MAX_VALUE));
		assertNotNull(outReport1);
		System.out.println("KRVNI_SLADKOR: ");
		for (int i = 0; i < outReport1.getTable().length; i++) {
			System.out.println(outReport1.getTable()[i][0] + " "
					+ outReport1.getTable()[i][1]);
		}
		
		outReport1 = DBRead.readTimeSpan(SystemType.EOSKRBATEST,"D", "SISTOLICNI_KRVNI_TLAK",
				"ivan.pacient","ivan.pacient", new Date(Long.MIN_VALUE), new Date(
						Long.MAX_VALUE));
		assertNotNull(outReport1);
		System.out.println("SISTOLICNI_KRVNI_TLAK: ");
		for (int i = 0; i < outReport1.getTable().length; i++) {
			System.out.println(outReport1.getTable()[i][0] + " "
					+ outReport1.getTable()[i][1]);
		}
		
		outReport1 = DBRead.readTimeSpan(SystemType.EOSKRBATEST,"D", "DIASTOLICNI_KRVNI_TLAK",
				"ivan.pacient","ivan.pacient", new Date(Long.MIN_VALUE), new Date(
						Long.MAX_VALUE));
		assertNotNull(outReport1);
		System.out.println("DIASTOLICNI_KRVNI_TLAK: ");
		for (int i = 0; i < outReport1.getTable().length; i++) {
			System.out.println(outReport1.getTable()[i][0] + " "
					+ outReport1.getTable()[i][1]);
		}
		
		
	}

	public void testreadLast() {

		String[][] out1 = DBRead.readLast(SystemType.EOSKRBATEST,"zdravnik.di","astma", "ACT");
		String[][] out2 = DBRead.readLast(SystemType.EOSKRBATEST,"zdravnik.di","astma", "PEF");
		assertNotNull(out1);
		assertNotNull(out2);
		for (int i = 0; i < out1.length; i++) {
			System.out.println("out1: " + out1[i][0] + " " + out1[i][1] + " "
					+ out1[i][2]);
		}
		for (int i = 0; i < out2.length; i++) {
			System.out.println("out2: " + out2[i][0] + " " + out2[i][1] + " "
					+ out2[i][2]);
		}
	}

	public void testReadTimespan() {
		Report outReport1 = DBRead.readTimeSpan(SystemType.EOSKRBATEST,"astma", "PEF",
				"bid.ivan.pacient","bid.ivan.pacient", new Date(Long.MIN_VALUE), new Date(
						Long.MAX_VALUE));
		Report outReport2 = DBRead.readTimeSpan(SystemType.EOSKRBATEST,"astma", "ACT",
				"bid.ivan.pacient","bid.ivan.pacient", new Date(Long.MIN_VALUE), new Date(
						Long.MAX_VALUE));
		assertNotNull(outReport1);
		assertNotNull(outReport2);
		System.out.println("PEF: ");
		for (int i = 0; i < outReport1.getTable().length; i++) {
			System.out.println(outReport1.getTable()[i][0] + " "
					+ outReport1.getTable()[i][1]);
		}
		System.out.println("ACT: ");
		for (int i = 0; i < outReport2.getTable().length; i++) {
			System.out.println(outReport2.getTable()[i][0] + " "
					+ outReport2.getTable()[i][1]);
		}
	}

}
