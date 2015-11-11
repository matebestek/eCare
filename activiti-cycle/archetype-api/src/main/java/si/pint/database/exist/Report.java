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


/*
 * class for reporting data to GUI
 * 
 * author: Vanc
 * 
 */
public class Report {

//bid pacienta
private String patient_id;

//tabela meritev: cas meritve, vrednost
private String[][] table;

//max meritev
private double max;

//min meritev
private double min;

//povprecje meritev
private double avg;

/*
 * Constructor for report from data, calculates max, avg and min
 */

public static void main(String[] args) {
	String valStr = "03:12:56";
	String [] tok = valStr.split(":");
	double s = Double.valueOf(tok[0])*3600;
	s = s+Double.valueOf(tok[1])*60;
	s = s+Double.valueOf(tok[2]);
	System.out.println((int)(s/60.0));
}
public Report(String patient_id, String[][] table) {
	this.table=table;
	this.patient_id=patient_id;	
	max=Double.MIN_VALUE;
	min=Double.MAX_VALUE;
	double vsota=0;
	double temp;
	int numOfMesurements = 0;
	for(int i=0; i<table.length;i++){
		String valStr = table[i][1];
		if (valStr == null || valStr.equals(""))
			continue;
		
		if (valStr.indexOf(":")!=-1) {

			String [] tok = valStr.split(":");
			double s = Double.valueOf(tok[0])*3600;
			s = s+Double.valueOf(tok[1])*60;
			s = s+Double.valueOf(tok[2]);
			valStr = String.valueOf((int)(s/60.0));
			table[i][1] = valStr;
		}
		
		temp = Double.valueOf(valStr);
		if(temp>max)
			max=temp;
		if(temp<min)
			min=temp;
		vsota=vsota+temp;
		
		numOfMesurements++;
	}
	
	if (numOfMesurements > 0)
		avg=(vsota/numOfMesurements);
}

//getter methods

public String getId(){
	return patient_id;
}

public String[][] getTable() {
	return table;
}

public double getMax() {
	return max;
}

public double getMin() {
	return min;
}

public double getAvg() {
	return avg;
}
/*
 * method for making data relative to average
 */
public void makeRelative() {
	for(int i=0;i<table.length;i++){
		table[i][1] = Double.toString((Double.valueOf(table[i][1])-avg));
	}
}
}
