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
package eoskrba.webapp.utils


import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType
import java.text.SimpleDateFormat

class UtilFunctions {

	static main(args) {
	
	}
	
	public static double roundToSignificantFigures(double num, int n) {
		if(num == 0) {
			return 0;
		}
	
		final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
		final int power = n - (int) d;
	
		final double magnitude = Math.pow(10, power);
		final long shifted = Math.round(num*magnitude);
		return shifted/magnitude;
	}
	public static String showSemaphore(val,guide) {
		val = val;
		double factor = 0;
		if (val != 0 && guide != 0) {
			factor = val/guide;
			if (factor < 1) factor = guide/val;
		}
		
		double maxAlarmFactor = 1.6;
		double okAlarmFactor = 1.15;
		if (factor <=okAlarmFactor) {
			return "#00ff00"; // zelena
		} else if (factor >=maxAlarmFactor) {
			return "#ff0000"; // rdeča
		} else { // odtenki rumeno oranžnih
			double factorDiff = 1-((factor-okAlarmFactor)/(maxAlarmFactor-okAlarmFactor));
			int gperc = (int)(255*factorDiff);
			Integer.toHexString(gperc);
			return "#ff" + Integer.toHexString(gperc) + "05";
		}
	}
	public static Object sortMap(map) {
		try {
			LinkedHashMap m = new LinkedHashMap();
			for (int i = 1; i < map.size()+10; i++) {
				String is = String.valueOf(i);
				if (map.containsKey(is)) {
					//System.out.println(is);
					//System.out.println(map.get(is));
					m.put(is,map.get(is));
				}
			}
			//System.out.println(m);
			return m;
		} catch(e) {

		}
		return map;
	}
	public static Object getWarnings(opkpResponse) {
		Object warnings = opkpResponse.get("warnings");
		//Object units = opkpResponse.get("units");
		HashMap w = new HashMap();
		Object ws;
		Object un; 
		try {
			//System.out.println(warnings.getClass());
			//System.out.println(warnings);
			for (int i = 0; i < warnings.size(); i++) {
				ws = warnings.get(i);
				//System.out.println(ws);
				if (ws.containsKey("type")) {
					if (ws.get("type")=="energy_density") {
						w.put("energy_density", String.valueOf(i+1));
					} else if (ws.get("type")=="unit") {
						un = String.valueOf(ws.get("unit"));
						//w.put(units.get(un).get("name"), String.valueOf(i+1));
						if (w.containsKey("u"+un)) {
							w.put("u"+un, w.get("u"+un)+","+String.valueOf(i+1));
						} else {
							w.put("u"+un, String.valueOf(i+1));
						}
					}
					//System.out.println(ws.get("type"));
				}
			}
		} catch(e) {

		}
		//System.out.println(w);
		return w;
	}
	
	public static final String LABELA_OPKP_VPRASALNIKA = "OPKP priporočila od dne ";
	public static final String LABELA_DELOVNI_LIST_ZGODOVINA = "Delovni list za korak ";
	
	public static final String TEMPLATE_ID_OPKP_RESULTS = "openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1";
	public static final String TEMPLATE_ID_WORKING_SHEETS_HISTORY = "openEHR-EHR-SECTION.eo_hu_korak.v1";
	
	
	
	public static Object listReports(userId) {
		returnTemplateList(userId, TEMPLATE_ID_OPKP_RESULTS, LABELA_OPKP_VPRASALNIKA);
	}
	
	public static Object listWorkingSheets(userId) {
		returnTemplateList(userId, TEMPLATE_ID_WORKING_SHEETS_HISTORY, LABELA_DELOVNI_LIST_ZGODOVINA);
	}

	private static Object returnTemplateList(userId, templateId, elementLabel) {
				
		LinkedHashMap map = new LinkedHashMap();

		if (userId == null || userId.equals(""))
			return map;
		
		Map smap = new TreeMap(Collections.reverseOrder());
		HashMap map1 = new HashMap();
		

		try {
		
			String[] ss = new String[2];
			ss[0] = templateId;
			ss[1] = "//name/value/text()";
			String[][] result = null;
			String ky;
			try {
				result = DBManager.getInstance(SystemType.EOSKRBAWEBAPP)
					.readTimespan(userId,userId,userId
						,ss[0],new Date(1300000000000),new Date(),ss[1] );
			} catch (Exception e) {
				return map;
			}

			/* filanje po zaporedju */
			for (int i = 0; i<result.length; i++) {
				ky = String.valueOf(((long)Double.parseDouble(result[i][0])));
				map1.put(ky,result[i][1]);
			}
			
			for (Iterator it = map1.keySet().iterator(); it.hasNext(); ) {
				ky = it.next();
				smap.put(ky, ky); // sort po imenu
			}
			
			int n = smap.size();
			for (Iterator it = smap.values().iterator(); it.hasNext(); ) {
				ky = it.next();
				Date date = new Date(Long.parseLong(ky));
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
				
				map.put(ky, [n, elementLabel + (elementLabel == LABELA_OPKP_VPRASALNIKA ? sdf.format(date) : n)]);
				n--;
			}

		} catch(Exception e) {
		}
		return map;
	}
}
