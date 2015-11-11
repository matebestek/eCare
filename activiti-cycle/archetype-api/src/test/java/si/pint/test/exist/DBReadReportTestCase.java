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
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import si.pint.database.exist.DBManager.SystemType;
import si.pint.database.exist.DBRead;
import si.pint.database.exist.Report;

public class DBReadReportTestCase extends TestCase {

	public static void main(String[] args) {
		
		getData();
	}
	 public static void getData() {
		 String type = "P";
		 String what = "TRAJANJE_VADBE";
		 String who = "bobo@gugu.com";
			int fromMonth = 7;
			int fromYear = 2012;
			int tillMonth = 7;
			int tillYear = 2012;
			
			// Process calendars
			GregorianCalendar fromCal = new GregorianCalendar(fromYear, fromMonth - 1, 1);
			Date fromLong = fromCal.getTime();
			tillMonth++;
			if(tillMonth == 13) {
				tillMonth = 1;
				tillYear++;
			}
			GregorianCalendar tillCal = new GregorianCalendar(tillYear, tillMonth - 1, 1);
			Date tillLong = tillCal.getTime();
			

			// Get data from database
			// System.out.println("DBRead.readTimeSpan('asthma', '" + what + "' , '" + who + "', " + fromLong + ", " + tillLong + ")")
			
			//SystemType sys, String type, String id,String userId,
			//String patient_id, Date start, Date finish
			
			Report report = DBRead.readTimeSpan(SystemType.EOSKRBAWEBAPP,type, what , who , who, fromLong, tillLong);
			if(report != null) {
				String[][] dataTable = report.getTable();
				for(int i = 0; i < dataTable.length; i++) {
					long time = Double.valueOf(dataTable[i][0]).longValue();
					String value = dataTable[i][1];
					System.out.println(time);
					System.out.println(value);
				}
				
			} else {

			}
			
	    }
		


}
