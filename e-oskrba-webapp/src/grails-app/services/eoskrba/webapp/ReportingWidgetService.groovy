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
package eoskrba.webapp
import java.util.Date

import si.pint.database.exist.*
import si.pint.database.exist.DBManager.SystemType

class ReportingWidgetService {

    static transactional = true

    def getData(String type, String who, String what, int fromMonth, int fromYear, int tillMonth, int tillYear) {
		
		// Process calendars
		GregorianCalendar fromCal = new GregorianCalendar(fromYear, fromMonth - 1, 1)
		Date fromLong = fromCal.time
		tillMonth++;
		if(tillMonth == 13) {
			tillMonth = 1;
			tillYear++;
		}
		GregorianCalendar tillCal = new GregorianCalendar(tillYear, tillMonth - 1, 1)
		Date tillLong = tillCal.time
		
		def data = new ArrayList()

		// Get data from database
		// System.out.println("DBRead.readTimeSpan('asthma', '" + what + "' , '" + who + "', " + fromLong + ", " + tillLong + ")")
		
		//SystemType sys, String type, String id,String userId,
		//String patient_id, Date start, Date finish
		Report report = DBRead.readTimeSpan(SystemType.EOSKRBAWEBAPP,type, what , who , who, fromLong, tillLong)
		if(report != null) {
			String[][] dataTable = report.getTable()
			for(int i = 0; i < dataTable.length; i++) {
				long time = Double.valueOf(dataTable[i][0]).longValue()
				def value = dataTable[i][1]
				if(value != null && value != "") {
					data << [time: time, value: value]
				}
			}
			
			// Order data by time ascending
			data = data.sort { it.time }
	
			return [data: data, max: report.max, min: report.min, avg: report.avg]
		} else {
			return [data: null, max: "neznano", min: "neznano", avg: "neznano"]
		}
		
    }
	
	String dataToString(ArrayList data) {
		String output = "["
		int dataSize = data.size()
		for(int i = 0; i < dataSize; i++) {
			if(data[i].value != null && data[i].value != "") {
				output += "[" + data[i].time + "," + data[i].value + "]"
			}
			if(i != (dataSize-1)) output += ","
		}
		output += "]"
		return output
	}
	
}
