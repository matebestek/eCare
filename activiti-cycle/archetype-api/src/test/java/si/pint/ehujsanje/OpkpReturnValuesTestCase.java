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

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;

import si.pint.archetypes.model.ArcheDataPath;
import si.pint.archetypes.service.OpkpReturnArcheSrv;
import si.pint.database.exist.DBManager.SystemType;

public class OpkpReturnValuesTestCase extends TestCase {
	
	public void testNewData(){
		String data = "eyJlbmVyZ3kiOjM5NjU1NzguMjEwMTQ4NCwiYWN0aXZpdHlfZW5lcmd5IjowLCJ1bml0cyI6eyIxIjp7InZhbCI6MC4zOTQ3NDQxOTkwNDgxNywiZ3VpZGUiOjEuNDkyNzExMTExMTExMSwibmFtZSI6Ilx1MDE2MGtyb2JuYSBcdTAxN2VpdmlsYSJ9LCIyIjp7InZhbCI6MC4xMjEwODkxMDEwMDIzMywiZ3VpZGUiOjAuMTg2NTg4ODg4ODg4ODksIm5hbWUiOiJNbGVrbyBpbiBtbGVcdTAxMGRuaSBpemRlbGtpIn0sIjMiOnsidmFsIjowLCJndWlkZSI6MC4yNzk4ODMzMzMzMzMzMywibmFtZSI6Ik1lc28gaW4gemFtZW5qYXZlIn0sIjQiOnsidmFsIjowLjgwOTMsImd1aWRlIjowLjA1NTk3NjY2NjY2NjY2NywibmFtZSI6IlN0cm9cdTAxMGRuaWNlIn0sIjUiOnsidmFsIjo0LjI1ODAxNDI4NTcxMjcsImd1aWRlIjowLjU1OTc2NjY2NjY2NjY3LCJuYW1lIjoiWmVsZW5qYXZhIn0sIjYiOnsidmFsIjowLCJndWlkZSI6MC4zNzMxNzc3Nzc3Nzc3OCwibmFtZSI6IlNhZGplIn0sIjciOnsidmFsIjozLjY1NzQ4MTAzNzU3ODEsImd1aWRlIjowLCJuYW1lIjoiTWFcdTAxNjFcdTAxMGRvYmUgaW4gbWFcdTAxNjFcdTAxMGRvYm5hIFx1MDE3ZWl2aWxhIn0sIjgiOnsidmFsIjowLjgwNzg3ODYwOTM2NzQzLCJndWlkZSI6MS4xMTk1MzMzMzMzMzMzLCJuYW1lIjoiU2xhZGtvciBpbiBzbGFka2EgXHUwMTdlaXZpbGEifX0sIndhcm5pbmdzIjpbeyJ0eXBlIjoidW5pdCIsInVuaXQiOjIsInRvb19sb3ciOnRydWUsInJlc3VsdCI6InByZW1hbG8gbWxla2EgaW4gbWxlXHUwMTBkbmloIGl6ZGVsa292IHRlciB6YW1lbmphdiIsImRlc2MiOiJVZ290YXZsamFtbywgZGEgdVx1MDE3ZWl2YXRlIHByZW1hbG8gbWxla2EgaW4gbWxlXHUwMTBkbmloIGl6ZGVsa292LiJ9LHsidHlwZSI6InNwZWNpYWwiLCJzcGVjaWFsIjoiZmlzaCIsInRvb19sb3ciOnRydWUsInJlc3VsdCI6InByZW1hbG8gcmliIiwiZGVzYyI6IlVnb3RhdmxqYW1vLCBkYSBqZSB2IHZhXHUwMTYxaSBwcmVocmFuaSBwcmVtYWxvIHJpYi4gWiB1XHUwMTdlaXZhbmplbSByaWIgem1hbmpcdTAxNjFhbW8gdHZlZ2FuamUgemEgbmFzdGFuZWsgcmFrYSBkZWJlbGVnYSBcdTAxMGRyZXZlc2EuIFByaXBvcm9cdTAxMGRhbW8gdmFtLCBkYSBlbmtyYXQgZG8gZHZha3JhdCBuYSB0ZWRlbiB1XHUwMTdlaXZhdGUga3ZhbGl0ZXRuZSBtYXN0bmUgbW9yc2tlIHJpYmUgKHR1bmEsIHNrdVx1MDE2MWEsIGxvc29zXHUyMDI2KS4gTW9yc2tlIHJpYmUgdnNlYnVqZWpvIHZlXHUwMTBkIGVzZW5jaWFsbmloIG1hXHUwMTYxXHUwMTBkb2JuaWgga2lzbGluIChvbWVnYSAzKSwgdml0YW1pbmEgQSBpbiBEIHRlciBqb2RhIGtvdCBzbGFka292b2RuZSByaWJlLiJ9LHsidHlwZSI6InVuaXQiLCJ1bml0Ijo1LCJ0b29faGlnaCI6dHJ1ZSwiaGlnaCI6W3siaXRlbXMiOlsiS3VoYW4gZ3JhaCJdLCJmcmVxIjoiNS02eCBuYSB0ZWRlbiIsIndlaWdodCI6IjE2MC4wMDAwMCIsIm5hbWUiOiJHcmFoLCBrdWhhbiJ9LHsiaXRlbXMiOlsiS3VtYXJhIiwiUGFyYWRpXHUwMTdlbmlrIiwiU29sYXRhIGVuZGl2aWphIl0sImZyZXEiOiIzLTR4IG5hIHRlZGVuIiwid2VpZ2h0IjoiMTUwLjAwMDAwIiwibmFtZSI6IlN2ZVx1MDE3ZWEgemVsZW5qYXZhIn0seyJpdGVtcyI6WyJQcmlwcmF2bGplbmEgcnVtZW5hIGdvclx1MDEwZGljYSJdLCJmcmVxIjoiMS0yeCBuYSB0ZWRlbiIsIndlaWdodCI6IjIwLjAwMDAwIiwibmFtZSI6Ikdvclx1MDEwZGljYSwgc2VuZiJ9XSwicmVzdWx0IjoicHJldmVcdTAxMGQgemVsZW5qYXZlIiwiZGVzYyI6IlRhIHRlZGVuIHN0ZSB6YXVcdTAxN2VpbGkgemVsbyB2ZWxpa28gemVsZW5qYXZlLiBWbm9zIGxhaGtvIHpuaVx1MDE3ZWF0ZSBkbmV2bm8gemEgMTAwIGcuIEdsZWRhbW8gbmEgcmF6bm9saWtvc3Q/ICh2bG9cdTAxN2VlbmEsIHN2ZVx1MDE3ZWE/KSBMSU5LIn0seyJ0eXBlIjoidW5pdCIsInVuaXQiOjYsInRvb19sb3ciOnRydWUsInJlc3VsdCI6InByZW1hbG8gc2FkamEiLCJkZXNjIjoiUHJlZGxhZ2FtbywgZGEgc2kgdGEgdGVkZW4gcHJpdm9cdTAxNjFcdTAxMGRpdGUgMTAwIGcgVkVcdTAxMGMgIHN2ZVx1MDE3ZWVnYSBzZXpvbnNrZWdhIHNhZGphLiBMSU5LXHJcblxyXG5TYWRqZSBpbiB6ZWxlbmphdmEgaW1hdGEgdmVsaWsgcG9tZW4gdiB6ZHJhdmkgcHJlaHJhbmksIHNhaiBzdGEgYmlvbG9cdTAxNjFrbyB2aXNva292cmVkbmkgc2t1cGluaSBcdTAxN2VpdmlsLCBpbiBzaWNlciB6IG5pemtvIGVuZXJnaWpza28gZ29zdG90by4gU2FkamUgaW4gemVsZW5qYXZhIHZzZWJ1amV0YSB2ZWxpa28gdml0YW1pbm92LCBtaW5lcmFsb3YsIGFudGlva3NpZGFudG92LCBwcmVocmFuc2tlIHZsYWtuaW5lIGluIHZlbGlrbyBkcnVnaWggemFcdTAxNjFcdTAxMGRpdG5paCBzbm92aS4gXHJcblxyXG5aYXJhZGkgdnNlYm5vc3RpIHBvc2FtZXpuaWggemFcdTAxNjFcdTAxMGRpdG5paCBzbm92aSwga2kgc28gdiB0aXBpXHUwMTBkbmkgYmFydmkgc2FkamEgaW4gemVsZW5qYXZlLCBwcmlwb3JvXHUwMTBkYW1vIHVcdTAxN2VpdmFuamUgcmF6bGlcdTAxMGRuaWggYmFydiBzYWRqYSBpbiB6ZWxlbmphdmUgKHplbGVuZSwgYmVsZSwgb3Jhblx1MDE3ZW5vLXJ1bWVuZSwgcmRlXHUwMTBkZSBpbiBtb2Ryby12aWpvbGlcdTAxMGRuZSkuIFYgZG5ldm5vIHByZWhyYW5vIG1vcmFtbyB2a2xqdVx1MDEwZGl0aSBvZCAxNTAgZG8gMjUwIGdyYW1vdiBzYWRqYSBpbiBvZCAyNTAgZG8gNDAwIGdyYW1vdiB6ZWxlbmphdmUsIHRqLiBvZHZpc25vIG9kIGRuZXZuaWggZW5lcmdpanNraWggcG90cmViLiBcclxuXHJcblByaXBvcm9cdTAxMGRsaml2byBqZSwgZGEgdmVcdTAxMGRqaSBkZWwgdGVoIFx1MDE3ZWl2aWwgemF1XHUwMTdlaWplbW8ga290IHN2ZVx1MDE3ZW8sIHByZXNubyBocmFubywgZHJ1Z2kgZGVsIHBhIHYga3VoYW5pIG9ibGlraS4ifSx7InR5cGUiOiJ1bml0IiwidW5pdCI6MSwidG9vX2xvdyI6dHJ1ZSwicmVzdWx0IjoicHJlbWFsbyBPSCBcdTAxN2VpdmlsIiwiZGVzYyI6IlRhIHRlZGVuIHN0ZSB6YXVcdTAxN2VpbGkgZGV2ZXQgZW5vdCBcdTAxNjFrcm9ibmloIFx1MDE3ZWl2aWwuIFN2ZXR1amVtbywgZGEgZG9kYXRlIHYgZG5ldm5pIGplZGlsbmlrIFx1MDE2MWUgdHJpIGVub3RlIChucHIuIGtvcyBrcnVoYSwgXHUwMTdlbGljYSBvdnNlbmloIGtvc21pXHUwMTBkZXYsIHNrb2RlbGljYSByaVx1MDE3ZWEpLiBMSU5LIG5hIHVcdTAxMGRubyBwb2dsYXZqZSBvIFx1MDE2MWtyb2JuaWggXHUwMTdlaXZpbGloISJ9XX0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
		try {
			data = new String(Base64.decodeBase64(data.getBytes("UTF-8")),"UTF-8");
			System.out.println("JSON NEW:"+data);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	public void testEnterOpkpValuesArchetype() {
		
		OpkpReturnArcheSrv service = new OpkpReturnArcheSrv();
		
		try {
			
			Map<ArcheDataPath, Object>  userInputsMap = prepareDataAll2(); 
			
			assertTrue("Archetype/templates did not initialise correctly!", service.initInput());
			boolean validateData = service.setAndValidataData(userInputsMap);
			assertTrue("User data was not set correctly!", validateData);
			assertTrue("Data did not save correctly into XML!", service.saveInput(SystemType.EOSKRBATEST, "pacient.hu","pacient.hu"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
//	{"energy":926884.82538246,
//	"activity_energy":211590.85714286,
//	"units":{"1":{"val":0,"guide":9.7647533068783,"name":"\u0160krobna \u017eivila"},
//			 "2":{"val":0.18061224489736,"guide":2.4411883267196,"name":"Mleko in mle\u010dni izdelki"},
//			 "3":{"val":0,"guide":2.4411883267196,"name":"Meso in zamenjave"},
//			 "4":{"val":0.80866285714016,"guide":0.48823766534392,"name":"Stro\u010dnice"},
//			 "5":{"val":0.5294813513469,"guide":3.9059013227513,"name":"Zelenjava"},
//			 "6":{"val":2.4383945704427,"guide":2.9294259920635,"name":"Sadje"},
//			 "7":{"val":0.033061224489686,"guide":1.4647129960317,"name":"Ma\u0161\u010dobe in ma\u0161\u010dobna \u017eivila"},
//			 "8":{"val":0.24098878448692,"guide":8.7882779761905,"name":"Sladkor in sladka \u017eivila"}},
//	"warnings":[
//				   {"result":"prenizek energijski vnos","desc":"Opa\u017eamo nezadosten vnos hrane in s tem energije, kar lahko predstavlja tveganje za va\u0161e zdravje in uspe\u0161nost huj\u0161anja. LINK"},
//				   {"result":"premalo mleka in mle\u010dnih izdelkov ter zamenjav","desc":"Ugotavljamo, da u\u017eivate premalo mleka in mle\u010dnih izdelkov."},
//				   {"result":"premalo rib","desc":"Ugotavljamo, da je v va\u0161i prehrani premalo rib. Z u\u017eivanjem rib zmanj\u0161amo tveganje za nastanek raka debelega \u010drevesa. Priporo\u010damo vam, da enkrat do dvakrat na teden u\u017eivate kvalitetne mastne morske ribe (tuna, sku\u0161a, losos\u2026). Morske ribe vsebujejo ve\u010d esencialnih ma\u0161\u010dobnih kislin (omega 3), vitamina A in D ter joda kot sladkovodne ribe."},
//				   {"result":"premalo zelenjave","desc":"Obrokom dodajte 200 g VE\u010c sve\u017ee ali kuhane sezonske zelenjave, po mo\u017enosti z va\u0161ega vrta ali lokalne oskrbe. LINK"},
//				   {"result":"premalo sadja","desc":"Predlagamo, da si ta teden privo\u0161\u010dite 100 g VE\u010c  sve\u017eega sezonskega sadja. LINK\n\nSadje in zelenjava imata velik pomen v zdravi prehrani, saj sta biolo\u0161ko visokovredni skupini \u017eivil, in sicer z nizko energijsko gostoto. Sadje in zelenjava vsebujeta veliko vitaminov, mineralov, antioksidantov, prehranske vlaknine in veliko drugih za\u0161\u010ditnih snovi. \n\nZaradi vsebnosti posameznih za\u0161\u010ditnih snovi, ki so v tipi\u010dni barvi sadja in zelenjave, priporo\u010damo u\u017eivanje razli\u010dnih barv sadja in zelenjave (zelene, bele, oran\u017eno-rumene, rde\u010de in modro-vijoli\u010dne). V dnevno prehrano moramo vklju\u010diti od 150 do 250 gramov sadja in od 250 do 400 gramov zelenjave, tj. odvisno od dnevnih energijskih potreb. \n\nPriporo\u010dljivo je, da ve\u010dji del teh \u017eivil zau\u017eijemo kot sve\u017eo, presno hrano, drugi del pa v kuhani obliki.\n"},
//				   {"result":"premalo OH \u017eivil","desc":"Ta teden ste zau\u017eili devet enot \u0161krobnih \u017eivil. Svetujemo, da dodate v dnevni jedilnik \u0161e tri enote (npr. kos kruha, \u017elica ovsenih kosmi\u010dev, skodelica ri\u017ea). LINK na u\u010dno poglavje o \u0161krobnih \u017eivilih!"}
//			   ]
//	
//   }
	private Map<ArcheDataPath, Object> prepareDataAll2() {

		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();

		//Energijska vrednost zaužitih hranil
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[at0003]/value"), "926884.82538246");		
		//Energija porabljena z aktivnostmi
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[at0004]/value"), "211590.85714286");
		
		//units

		//zauzita enota 1: "1":{"val":0,"guide":9.7647533068783,"name":"\u0160krobna \u017eivila"}
		//koda enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0001]/value"), "1");				
		//ime enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0002]/value"), "\u0160krobna \u017eivila");		
//		//Število zaužitih enot
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0003]/value"), "0");		
		//Priporočilo
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0004]/value"), "9.7647533068783");
		//zauzita enota 1: "1":{"val":0,"guide":9.7647533068783,"name":"\u0160krobna \u017eivila"}
		//koda enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0001]/value"), "1");				
		//ime enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0002]/value"), "\u0160krobna \u017eivila");		
//				//Število zaužitih enot
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0003]/value"), "0");		
		//Priporočilo
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0004]/value"), "9.7647533068783");
		
		//zauzita enota 1: "1":{"val":0,"guide":9.7647533068783,"name":"\u0160krobna \u017eivila"}
		//koda enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0001]/value"), "1");				
		//ime enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0002]/value"), "\u0160krobna \u017eivila");		
//				//Število zaužitih enot
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0003]/value"), "0");		
		//Priporočilo
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0004]/value"), "9.7647533068783");
		
		//zauzita enota 1: "1":{"val":0,"guide":9.7647533068783,"name":"\u0160krobna \u017eivila"}
		//koda enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0001]/value"), "1");				
		//ime enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0002]/value"), "\u0160krobna \u017eivila");		
//				//Število zaužitih enot
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0003]/value"), "0");		
		//Priporočilo
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0004]/value"), "9.7647533068783");
		
		//zauzita enota 1: "1":{"val":0,"guide":9.7647533068783,"name":"\u0160krobna \u017eivila"}
		//koda enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0001]/value"), "1");				
		//ime enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0002]/value"), "\u0160krobna \u017eivila");		
//				//Število zaužitih enot
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0003]/value"), "0");		
		//Priporočilo
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0004]/value"), "9.7647533068783");
		
		//zauzita enota 1: "1":{"val":0,"guide":9.7647533068783,"name":"\u0160krobna \u017eivila"}
		//koda enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0001]/value"), "1");				
		//ime enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0002]/value"), "\u0160krobna \u017eivila");		
//				//Število zaužitih enot
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0003]/value"), "0");		
		//Priporočilo
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0004]/value"), "9.7647533068783");
		
		//zauzita enota 1: "1":{"val":0,"guide":9.7647533068783,"name":"\u0160krobna \u017eivila"}
		//koda enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0001]/value"), "1");				
		//ime enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0002]/value"), "\u0160krobna \u017eivila");		
//				//Število zaužitih enot
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0003]/value"), "0");		
		//Priporočilo
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0004]/value"), "9.7647533068783");
		
		//zauzita enota 1: "1":{"val":0,"guide":9.7647533068783,"name":"\u0160krobna \u017eivila"}
		//koda enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0001]/value"), "1");				
		//ime enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0002]/value"), "\u0160krobna \u017eivila");		
//				//Število zaužitih enot
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0003]/value"), "0");		
		//Priporočilo
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0004]/value"), "9.7647533068783");
		
		
		//opozorilo ---->>>>>
		//Rezultat
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[at0010]/value"), "to je to");
		
		//Opis
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[at0011]/value"), "o2");

		
		//opozorilo ---->>>>>
		//{"result":"prenizek energijski vnos","desc":"Opa\u017eamo nezadosten vnos hrane in s tem energije, kar lahko predstavlja tveganje za va\u0161e zdravje in uspe\u0161nost huj\u0161anja. LINK"},
		//seznam treh živil		
		//rezultat
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0004]/value"), "prenizek energijski vnos");
		//opis
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0005]/value"), "Opa\u017eamo nezadosten vnos hrane in s tem energije, kar lahko predstavlja tveganje za va\u0161e zdravje in uspe\u0161nost huj\u0161anja. LINK");
		
//		//ime
//		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0001]/value"), "s1");		
//		//pogostost
//		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0002]/value"), "s2");		
//		//količina
//		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0003]/value"), "s3");
		
		
		//{"result":"prenizek energijski vnos","desc":"Opa\u017eamo nezadosten vnos hrane in s tem energije, kar lahko predstavlja tveganje za va\u0161e zdravje in uspe\u0161nost huj\u0161anja. LINK"},
		//seznam treh živil		
		//rezultat
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0004]/value"), "prenizek energijski vnos");
		//opis
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0005]/value"), "Opa\u017eamo nezadosten vnos hrane in s tem energije, kar lahko predstavlja tveganje za va\u0161e zdravje in uspe\u0161nost huj\u0161anja. LINK");
		
//		//ime
//		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0001]/value"), "s1");		
//		//pogostost
//		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0002]/value"), "s2");		
//		//količina
//		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0003]/value"), "s3");
		
		
		
				
		return userInputsMap;
	}
	
	
	private Map<ArcheDataPath, Object> prepareDataAll() {

		Map<ArcheDataPath, Object> userInputsMap = new LinkedHashMap<ArcheDataPath, Object>();

		//Energijska vrednost zaužitih hranil
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[at0003]/value"), "80");
		
		//Energija porabljena z aktivnostmi
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[at0004]/value"), "88");

		//opozorilo ---->>>>>
		//Rezultat
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[at0010]/value"), "to je to");
		
		//Opis
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[at0011]/value"), "o2");

		
		//zauzite enote---->>>>>
		//koda enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0001]/value"), "k1");		
		
		//ime enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0002]/value"), "k2");
		
		//Število zaužitih enot
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0003]/value"), "17");
		
		//Priporočilo
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0004]/value"), "k3");
		
		//opozorilo ---->>>>>
		
		//seznam treh živil
		//ime
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0001]/value"), "s1");
		
		//pogostost
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0002]/value"), "s2");
		
		//količina
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0003]/value"), "s3");
		
		//rezultat
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0004]/value"), "s4");
		//opis
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0005]/value"), "s5");

		
		
		//zauzite enote---->>>>>
		//koda enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0001]/value"), "k11");		
		
		//ime enote
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0002]/value"), "k22");
		
		//Število zaužitih enot
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0003]/value"), "177");
		
		//Priporočilo
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0004]/value"), "k33");
		
		//opozorilo ---->>>>>
		
		//seznam treh živil
		//ime
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0001]/value"), "s11");
		
		//pogostost
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0002]/value"), "s22");
		
		//količina
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0003]/value"), "s33");
		//rezultat
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0004]/value"), "s33");
		//opis
		userInputsMap.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0005]/value"), "s33");

				
		return userInputsMap;
	}
	
}
