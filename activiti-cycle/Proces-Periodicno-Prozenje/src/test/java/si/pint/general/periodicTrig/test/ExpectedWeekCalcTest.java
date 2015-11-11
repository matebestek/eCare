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
package si.pint.general.periodicTrig.test;

import java.util.Calendar;
import java.util.Date;

import si.pint.activiti.standalone.ldap.UserRegistryFactory;

public class ExpectedWeekCalcTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Calendar today = Calendar.getInstance(); 
		Date startDate = null;
		try {
//			startDate = UserRegistryFactory.parseTimestamp("14.02.2011").getTime();
			
//			startDate = UserRegistryFactory.parseTimestamp("13.02.2012").getTime();
//			startDate = UserRegistryFactory.parseTimestamp("14.02.2012").getTime(); //torek
//			startDate = UserRegistryFactory.parseTimestamp("15.02.2012").getTime();
//			
//			startDate = UserRegistryFactory.parseTimestamp("19.02.2012").getTime(); //ned
//			startDate = UserRegistryFactory.parseTimestamp("20.02.2012").getTime(); //pon
//			startDate = UserRegistryFactory.parseTimestamp("21.02.2012").getTime(); //tor
//			startDate = UserRegistryFactory.parseTimestamp("01.03.2012").getTime(); //tor
			
//			startDate = UserRegistryFactory.parseTimestamp("30.12.2011").getTime(); 
//			startDate = UserRegistryFactory.parseTimestamp("29.1.2012").getTime();
//			startDate = UserRegistryFactory.parseTimestamp("30.1.2012").getTime();
//			startDate = UserRegistryFactory.parseTimestamp("31.1.2012").getTime();
//			startDate = UserRegistryFactory.parseTimestamp("5.2.2012").getTime();
//			startDate = UserRegistryFactory.parseTimestamp("6.2.2012").getTime();
//			startDate = UserRegistryFactory.parseTimestamp("7.2.2012").getTime();
//			startDate = UserRegistryFactory.parseTimestamp("11.2.2012").getTime();
			startDate = UserRegistryFactory.parseTimestamp("12.2.2012").getTime();
			
					
//			startDate = UserRegistryFactory.parseTimestamp("01.03.2013").getTime(); //??
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //1 leto nazaj

		
		
		int z = findStepExpected(startDate);
		System.out.println("datum pricetka: " + startDate + ", pricakovan korak za danes: " + z);

	}
	
	/**
	 * Find step that patient should be doing at current date.
	 * 
	 * @param u
	 * @return
	 */
	public static int findStepExpected(Date startDate) {
		
		Calendar today = Calendar.getInstance(); 
		
		//find closest MONDAY to patients start day
		Calendar startDayEntered = Calendar.getInstance();
		startDayEntered.setTime(startDate);
		int daysToSubstract = 0;

		if (startDayEntered.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
			daysToSubstract = -1;
		}
		else if (startDayEntered.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
			daysToSubstract = -2;
		}
		else if (startDayEntered.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
			daysToSubstract = -3;
		}
		else if (startDayEntered.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
			daysToSubstract = -4;
		}
		else if (startDayEntered.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			daysToSubstract = -5;
		}
		else if (startDayEntered.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			daysToSubstract = -6;
		}
		
		if (daysToSubstract != 0)
			startDayEntered.add(Calendar.DAY_OF_MONTH, daysToSubstract); 
		
		//difference
		long diff = today.getTimeInMillis() - startDayEntered.getTimeInMillis();
				
		//start day is yet to come -> return;
		if (diff < 0)
			return -1;
		
		long daysL = diff/((long) 1000 * (long) 60 * 60 * 24);
		int days = (int) daysL;
		
		int expectedWeek = days/7; //integer division (floor), values: 0,1,...
		expectedWeek++; //if 0: expected week is 1, ...
		
		return expectedWeek;
		
	}

}
