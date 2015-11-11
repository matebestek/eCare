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
package si.pint.ehujsanje;

import junit.framework.TestCase;

import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType;

public class KorakiTestCase extends TestCase {

	//preštejem elemente v cluster-u ker bo več od nič samo pri enem :)
	String HUJSANJE_COUNT = "//*[contains(name(),'items') and @archetype_node_id and count(./data/items[contains(@xsi:type,'CLUSTER')]/items) > 0]/archetype_details/archetype_id/value/text()";
	public void test() {
		
		try {
			
//			ResourceSet res = DBManager.readLastObject("pacient.hu", "openEHR-EHR-SECTION.opomnik_eo_hu_koraki.v1", HUJSANJE_COUNT);
//			for(ResourceIterator iter = res.getIterator(); iter.hasMoreResources();){
//				Resource r = iter.nextResource();
//				Object obj = r.getContent();
//				if(obj instanceof String){
//					System.out.println(obj);
//				} else {
//					System.out.println(obj);
//				}
//			}
			String s = DBManager.getInstance(SystemType.EOSKRBATEST).readLast("pacient.hu","pacient.hu", "openEHR-EHR-SECTION.opomnik_eo_hu_koraki.v1", HUJSANJE_COUNT);
			System.out.println("PONOVNO:"+s);
			//dobim id arhetipa, ki vsebuje podatke
			//openEHR-EHR-EVALUATION.hu_koraki_02.v1
			String poz = s.substring(33, 35);
			System.out.println("SSSS:" + Integer.parseInt(poz));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
}
