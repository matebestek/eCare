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


import si.pint.utils.SecureCodec;
import grails.test.*

import java.util.Map
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.*

import si.pint.archetypes.service.OpkpReturnArcheSrv
import si.pint.database.exist.DBManager.SystemType
import si.pint.utils.SecureCodec
import si.pint.archetypes.model.ArcheDataPath;
import org.apache.commons.codec.binary.Base64;

class PatientTabsControllerTests extends ControllerUnitTestCase {
	    protected void setUp() {
	        super.setUp()
	    }

	    protected void tearDown() {
	        super.tearDown()
	    }

	void testData(){
		String data =
		"{"+
		"	\"energy\":\"1200\","+
		"	\"activity_energy\":\"1000\","+
		"	\"units\":{"+
		"				\"koda enote 1\":{"+
		"								val:\"12\","+
		"								guide:\"priporočilo\","+
		"								name:\"ime enote\""+
		"							 },"+
		"				\"koda enote 2\":{"+
		"								val:\"33\","+
		"								guide:\"priporočilo 2\","+
		"								name:\"ime enote 2\""+
		"							 }"+
		"			 },"+
		"	\"warnings\":{"+
		"		\"result\":\"kratek opis\","+
		"		\"desc\":\"daljši opis\","+
		"		\"high\":{"+
		"					\"items\":\"array živil iz opkp\","+
		"					\"freq\":\"pogostost\","+
		"					\"weight\":\"tipična količina\""+
		"			   }"+
		"		"+
		"	}"+
		""+
		"}";


		SecureCodec codec = new SecureCodec();
		String signedData = codec.encode(data);
		System.out.println("ŠIFRIRAN:"+signedData);
		//še dešifriram
		codec = new SecureCodec();
		String s = codec.decode(signedData);
		System.out.println("DEŠIFRIRAN:"+s);
		assertNotNull(s);
		assertEquals(data.trim(), s.trim());
		//		j = new JSONObject(s.trim());
		//		assertNotNull(j);
	}

	void testSomething() {
		def taskId = 1234;
		def data = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADjlTTHMnNMNwyGuJmRbKYiO116e2rSLxtpo8R2rvrmEuQ6iM6PsXdghvMfRlUYYytxj6Te6lXGIrJYxx3dRHNr9coJ-w9I8V7TNnPrTKbavA5to-b6ZykorPhW9DtJs9gFqnWetKlc87Ukcsj028yyqCy79cAc_eAvbBjZAY0Szfsk0DmkWuwg6fv8jqiBh1clHRpYYfy_xGlhKCSJgDuvP-2aYt_FmNJyFNJUySmPcwv1nx4CPfWcWAOcFvORFoCeIb9wFjNhS4DImGruvqLelRmMOdKCPl_ZGcGOOZd3inrA3NZCNiG8kIphFrrPj5rnxLRr9PNaQhClCKo5fTuiOCM0SUgODmRn-mk74Ym65Ckr7gdIpbBwxl5LnBU-THN1o-FnrjXndSM_CnvxY_U7A52rhhuMZW4NQD1VfAtJRSH2AZ6tuI4iYtJhMmeAy7wm0xV9tMGTPKCf00hPwuX1xWomonBDOJ_T1G0dMKyA2OGP2OItcnlCUHb2r3NduZQFb4ac0S7B-T4CIS1eGt3e58t49SUSbgWzPl4hgqBQx7rp317F5r4jhnISCvA1J34URHxZJyXS5J4IRcNvcSS2";
		SecureCodec sc = new SecureCodec();
		def s = sc.decode(data);
		def json = JSON.parse(s);//JSONElement
//		def temp = Opkpresponse.findByTaskId(taskId);
//		if(temp != null){
//			temp.delete();
//		}
//		Opkpresponse res = new Opkpresponse(taskId:taskId,json:Base64.encodeBase64String(s.getBytes("UTF-8")));
//		res.save();

		//		{"energy":926884.82538246,
		//			"activity_energy":211590.85714286,
		//			"units":{"1":{"val":0,"guide":9.7647533068783,"name":"\u0160krobna \u017eivila"},
		//					 "2":{"val":0.18061224489736,"guide":2.4411883267196,"name":"Mleko in mle\u010dni izdelki"},
		//					 "3":{"val":0,"guide":2.4411883267196,"name":"Meso in zamenjave"},
		//					 "4":{"val":0.80866285714016,"guide":0.48823766534392,"name":"Stro\u010dnice"},
		//					 "5":{"val":0.5294813513469,"guide":3.9059013227513,"name":"Zelenjava"},
		//					 "6":{"val":2.4383945704427,"guide":2.9294259920635,"name":"Sadje"},
		//					 "7":{"val":0.033061224489686,"guide":1.4647129960317,"name":"Ma\u0161\u010dobe in ma\u0161\u010dobna \u017eivila"},
		//					 "8":{"val":0.24098878448692,"guide":8.7882779761905,"name":"Sladkor in sladka \u017eivila"}},
		//			"warnings":[
		//						   {"result":"prenizek energijski vnos","desc":"Opa\u017eamo nezadosten vnos hrane in s tem energije, kar lahko predstavlja tveganje za va\u0161e zdravje in uspe\u0161nost huj\u0161anja. LINK"},
		//						   {"result":"premalo mleka in mle\u010dnih izdelkov ter zamenjav","desc":"Ugotavljamo, da u\u017eivate premalo mleka in mle\u010dnih izdelkov."},
		//						   {"result":"premalo rib","desc":"Ugotavljamo, da je v va\u0161i prehrani premalo rib. Z u\u017eivanjem rib zmanj\u0161amo tveganje za nastanek raka debelega \u010drevesa. Priporo\u010damo vam, da enkrat do dvakrat na teden u\u017eivate kvalitetne mastne morske ribe (tuna, sku\u0161a, losos\u2026). Morske ribe vsebujejo ve\u010d esencialnih ma\u0161\u010dobnih kislin (omega 3), vitamina A in D ter joda kot sladkovodne ribe."},
		//						   {"result":"premalo zelenjave","desc":"Obrokom dodajte 200 g VE\u010c sve\u017ee ali kuhane sezonske zelenjave, po mo\u017enosti z va\u0161ega vrta ali lokalne oskrbe. LINK"},
		//						   {"result":"premalo sadja","desc":"Predlagamo, da si ta teden privo\u0161\u010dite 100 g VE\u010c  sve\u017eega sezonskega sadja. LINK\n\nSadje in zelenjava imata velik pomen v zdravi prehrani, saj sta biolo\u0161ko visokovredni skupini \u017eivil, in sicer z nizko energijsko gostoto. Sadje in zelenjava vsebujeta veliko vitaminov, mineralov, antioksidantov, prehranske vlaknine in veliko drugih za\u0161\u010ditnih snovi. \n\nZaradi vsebnosti posameznih za\u0161\u010ditnih snovi, ki so v tipi\u010dni barvi sadja in zelenjave, priporo\u010damo u\u017eivanje razli\u010dnih barv sadja in zelenjave (zelene, bele, oran\u017eno-rumene, rde\u010de in modro-vijoli\u010dne). V dnevno prehrano moramo vklju\u010diti od 150 do 250 gramov sadja in od 250 do 400 gramov zelenjave, tj. odvisno od dnevnih energijskih potreb. \n\nPriporo\u010dljivo je, da ve\u010dji del teh \u017eivil zau\u017eijemo kot sve\u017eo, presno hrano, drugi del pa v kuhani obliki.\n"},
		//						   {"result":"premalo OH \u017eivil","desc":"Ta teden ste zau\u017eili devet enot \u0161krobnih \u017eivil. Svetujemo, da dodate v dnevni jedilnik \u0161e tri enote (npr. kos kruha, \u017elica ovsenih kosmi\u010dev, skodelica ri\u017ea). LINK na u\u010dno poglavje o \u0161krobnih \u017eivilih!"}
		//					   ]
		//
		//		   }
		Map m = new HashMap();
		if(json.containsKey("energy")){
			m.put(new ArcheDataPath("/data[at0001]/items[at0003]/value"), new String(json.energy));
		}
		if(json.containsKey("activity_energy")){
			m.put(new ArcheDataPath("/data[at0001]/items[at0004]/value"), new String(json.activity_energy));
		}

		for (unit in json.units) {
			def val = unit.value;
			if(val.containsKey("name")){
				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0001]/value"), new String(val.name));//koda
			}
			if(val.containsKey("name")){
				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0002]/value"), new String(val.name));//ime
			}
			if(val.containsKey("val")){
				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0003]/value"),new String(val.val));//zaužite enote
			}
			if(val.containsKey("guide")){
				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0004]/value"), new String(val.guide));//priporočilo
			}
		}
		def w =json.warnings
			//rezultat
			if(w.containsKey("result")){
				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0004]/value"),new String(w.result));
			}
			//opis
			if(w.containsKey("desc")){
				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0005]/value"),new String(w.desc));
			}
			//high:
			if(w.containsKey("high")){
				def high = w.high;
				//items
				if(high.containsKey("items")){
					m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0001]/value"),new String(high.items));
				}
				//freq
				if(high.containsKey("freq")){
					m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0002]/value"),new String(high.freq));
				}
				//weight
				if(high.containsKey("weight")){
					m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0003]/value"),new String(high.weight));
				}
			}
		
		OpkpReturnArcheSrv service = new OpkpReturnArcheSrv();		
		assertTrue("Archetype/templates did not initialise correctly!", service.initInput());
		boolean validateData = service.setAndValidataData(m);
		assertTrue("Data did not validate correctly",validateData);
		service.saveInput(SystemType.EOSKRBAWEBAPP, userId,userId);
		

	}
	
	void testArcheSrv() {
		
		String data = null;
		try {
			data = new String("eyJlbmVyZ3kiOjAsImFjdGl2aXR5X2VuZXJneSI6MCwicmVjb21tZW5kZWRfZW5lcmd5Ijo3MTU5OTk1LjUyLCJ1bml0cyI6eyIxIjp7InZhbCI6MCwiZ3VpZGUiOjkuNTA3MTExMTExMTExMSwibmFtZSI6Ilx1MDE2MGtyb2JuYSBcdTAxN2VpdmlsYSJ9LCIyIjp7InZhbCI6MCwiZ3VpZGUiOjIuMzc2Nzc3Nzc3Nzc3OCwibmFtZSI6Ik1sZWtvIGluIG1sZVx1MDEwZG5pIGl6ZGVsa2kifSwiMyI6eyJ2YWwiOjAsImd1aWRlIjoyLjM3Njc3Nzc3Nzc3NzgsIm5hbWUiOiJNZXNvIGluIHphbWVuamF2ZSJ9LCI0Ijp7InZhbCI6MCwiZ3VpZGUiOjAuNDc1MzU1NTU1NTU1NTYsIm5hbWUiOiJTdHJvXHUwMTBkbmljZSJ9LCI1Ijp7InZhbCI6MCwiZ3VpZGUiOjMuODAyODQ0NDQ0NDQ0NCwibmFtZSI6IlplbGVuamF2YSJ9LCI2Ijp7InZhbCI6MCwiZ3VpZGUiOjIuODUyMTMzMzMzMzMzMywibmFtZSI6IlNhZGplIn0sIjciOnsidmFsIjowLCJndWlkZSI6MS40MjYwNjY2NjY2NjY3LCJuYW1lIjoiTWFcdTAxNjFcdTAxMGRvYmUgaW4gbWFcdTAxNjFcdTAxMGRvYm5hIFx1MDE3ZWl2aWxhIn0sIjgiOnsidmFsIjowLCJndWlkZSI6OC41NTY0LCJuYW1lIjoiU2xhZGtvciBpbiBzbGFka2EgXHUwMTdlaXZpbGEifX0sIndhcm5pbmdzIjpbeyJ0eXBlIjoidW5pdCIsInVuaXQiOjIsInRvb19sb3ciOnRydWUsInJlc3VsdCI6InByZW1hbG8gbWxla2EgaW4gbWxlXHUwMTBkbmloIGl6ZGVsa292IHRlciB6YW1lbmphdiIsImRlc2MiOiJVZ290YXZsamFtbywgZGEgdVx1MDE3ZWl2YXRlIHByZW1hbG8gbWxla2EgaW4gbWxlXHUwMTBkbmloIGl6ZGVsa292LiJ9LHsidHlwZSI6InNwZWNpYWwiLCJzcGVjaWFsIjoiZmlzaCIsInRvb19sb3ciOnRydWUsInJlc3VsdCI6InByZW1hbG8gcmliIiwiZGVzYyI6IlVnb3RhdmxqYW1vLCBkYSBqZSB2IHZhXHUwMTYxaSBwcmVocmFuaSBwcmVtYWxvIHJpYi4gWiB1XHUwMTdlaXZhbmplbSByaWIgem1hbmpcdTAxNjFhbW8gdHZlZ2FuamUgemEgbmFzdGFuZWsgcmFrYSBkZWJlbGVnYSBcdTAxMGRyZXZlc2EuIFByaXBvcm9cdTAxMGRhbW8gdmFtLCBkYSBlbmtyYXQgZG8gZHZha3JhdCBuYSB0ZWRlbiB1XHUwMTdlaXZhdGUga3ZhbGl0ZXRuZSBtYXN0bmUgbW9yc2tlIHJpYmUgKHR1bmEsIHNrdVx1MDE2MWEsIGxvc29zXHUyMDI2KS4gTW9yc2tlIHJpYmUgdnNlYnVqZWpvIHZlXHUwMTBkIGVzZW5jaWFsbmloIG1hXHUwMTYxXHUwMTBkb2JuaWgga2lzbGluIChvbWVnYSAzKSwgdml0YW1pbmEgQSBpbiBEIHRlciBqb2RhIGtvdCBzbGFka292b2RuZSByaWJlLiJ9LHsidHlwZSI6InVuaXQiLCJ1bml0Ijo1LCJ0b29fbG93Ijp0cnVlLCJyZXN1bHQiOiJwcmVtYWxvIHplbGVuamF2ZSIsImRlc2MiOiJPYnJva29tIGRvZGFqdGUgMjAwIGcgVkVcdTAxMGMgc3ZlXHUwMTdlZSBhbGkga3VoYW5lIHNlem9uc2tlIHplbGVuamF2ZSwgcG8gbW9cdTAxN2Vub3N0aSB6IHZhXHUwMTYxZWdhIHZydGEgYWxpIGxva2FsbmUgb3NrcmJlLiBMSU5LIn0seyJ0eXBlIjoidW5pdCIsInVuaXQiOjYsInRvb19sb3ciOnRydWUsInJlc3VsdCI6InByZW1hbG8gc2FkamEiLCJkZXNjIjoiUHJlZGxhZ2FtbywgZGEgc2kgdGEgdGVkZW4gcHJpdm9cdTAxNjFcdTAxMGRpdGUgMTAwIGcgVkVcdTAxMGMgIHN2ZVx1MDE3ZWVnYSBzZXpvbnNrZWdhIHNhZGphLiBMSU5LXHJcblxyXG5TYWRqZSBpbiB6ZWxlbmphdmEgaW1hdGEgdmVsaWsgcG9tZW4gdiB6ZHJhdmkgcHJlaHJhbmksIHNhaiBzdGEgYmlvbG9cdTAxNjFrbyB2aXNva292cmVkbmkgc2t1cGluaSBcdTAxN2VpdmlsLCBpbiBzaWNlciB6IG5pemtvIGVuZXJnaWpza28gZ29zdG90by4gU2FkamUgaW4gemVsZW5qYXZhIHZzZWJ1amV0YSB2ZWxpa28gdml0YW1pbm92LCBtaW5lcmFsb3YsIGFudGlva3NpZGFudG92LCBwcmVocmFuc2tlIHZsYWtuaW5lIGluIHZlbGlrbyBkcnVnaWggemFcdTAxNjFcdTAxMGRpdG5paCBzbm92aS4gXHJcblxyXG5aYXJhZGkgdnNlYm5vc3RpIHBvc2FtZXpuaWggemFcdTAxNjFcdTAxMGRpdG5paCBzbm92aSwga2kgc28gdiB0aXBpXHUwMTBkbmkgYmFydmkgc2FkamEgaW4gemVsZW5qYXZlLCBwcmlwb3JvXHUwMTBkYW1vIHVcdTAxN2VpdmFuamUgcmF6bGlcdTAxMGRuaWggYmFydiBzYWRqYSBpbiB6ZWxlbmphdmUgKHplbGVuZSwgYmVsZSwgb3Jhblx1MDE3ZW5vLXJ1bWVuZSwgcmRlXHUwMTBkZSBpbiBtb2Ryby12aWpvbGlcdTAxMGRuZSkuIFYgZG5ldm5vIHByZWhyYW5vIG1vcmFtbyB2a2xqdVx1MDEwZGl0aSBvZCAxNTAgZG8gMjUwIGdyYW1vdiBzYWRqYSBpbiBvZCAyNTAgZG8gNDAwIGdyYW1vdiB6ZWxlbmphdmUsIHRqLiBvZHZpc25vIG9kIGRuZXZuaWggZW5lcmdpanNraWggcG90cmViLiBcclxuXHJcblByaXBvcm9cdTAxMGRsaml2byBqZSwgZGEgdmVcdTAxMGRqaSBkZWwgdGVoIFx1MDE3ZWl2aWwgemF1XHUwMTdlaWplbW8ga290IHN2ZVx1MDE3ZW8sIHByZXNubyBocmFubywgZHJ1Z2kgZGVsIHBhIHYga3VoYW5pIG9ibGlraS4ifSx7InR5cGUiOiJ1bml0IiwidW5pdCI6MSwidG9vX2xvdyI6dHJ1ZSwicmVzdWx0IjoicHJlbWFsbyBPSCBcdTAxN2VpdmlsIiwiZGVzYyI6IlRhIHRlZGVuIHN0ZSB6YXVcdTAxN2VpbGkgZGV2ZXQgZW5vdCBcdTAxNjFrcm9ibmloIFx1MDE3ZWl2aWwuIFN2ZXR1amVtbywgZGEgZG9kYXRlIHYgZG5ldm5pIGplZGlsbmlrIFx1MDE2MWUgdHJpIGVub3RlIChucHIuIGtvcyBrcnVoYSwgXHUwMTdlbGljYSBvdnNlbmloIGtvc21pXHUwMTBkZXYsIHNrb2RlbGljYSByaVx1MDE3ZWEpLiBMSU5LIG5hIHVcdTAxMGRubyBwb2dsYXZqZSBvIFx1MDE2MWtyb2JuaWggXHUwMTdlaXZpbGloISJ9LHsidHlwZSI6InVuaXQiLCJ1bml0Ijo0LCJ0b29fbG93Ijp0cnVlLCJyZXN1bHQiOiJwcmVtYWxvIHN0cm9cdTAxMGRuaWMiLCJkZXNjIjoiVE9ETzogb3BpcyJ9XSwiYmFzZV9lbmVyZ3kiOjY2MzQxNTAuNH0AAAAAAAAAAAAAAAAA".getBytes("UTF-8"), "UTF-8");
		} catch (Exception e) {
			
		}
		byte[] result = Base64.decodeBase64(data);
		String tmp = (new String(result)).trim();
		
		def json = JSON.parse(tmp);

		Map<ArcheDataPath, Object> m = new LinkedHashMap<ArcheDataPath, Object>();
		if(json.containsKey("energy")){
			m.put(new ArcheDataPath("/data[at0001]/items[at0003]/value"), json.energy.toString());
		}
		if(json.containsKey("activity_energy")){
			m.put(new ArcheDataPath("/data[at0001]/items[at0004]/value"), json.activity_energy.toString());
		}
		if(json.containsKey("recommended_energy")){
			m.put(new ArcheDataPath("/data[at0001]/items[at0018]/value"), json.recommended_energy.toString());
		}
		
		for (unit in json.units) {
			def val = unit.value;
			m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0001]/value"), unit.key);  //koda
			
			if(val.containsKey("name")){
				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0002]/value"), val.name);  //ime
			}
			if(val.containsKey("val")){
				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0003]/value"),((int)val.val).toString());  //zaužite enote
			}
			if(val.containsKey("guide")){
				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1]/items[at0004]/value"), val.guide.toString());  //priporočilo
			}
		}
		for(w in json.warnings){
			  //rezultat
			if(w.containsKey("result")){
				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0004]/value"),w.result);
			}
			  //opis
			if(w.containsKey("desc")){
				m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0005]/value"),w.desc);
			}
			  //high:
			if(w.containsKey("high")){
				def high = w.high;
				  //items
				if(high.contains("items")){
					m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0001]/value"),high.items);
				}
				  //freq
				if(high.contains("freq")){
					m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0002]/value"),high.freq);
				}
				  //weight
				if(high.contains("weight")){
					m.put(new ArcheDataPath("/data[at0001]/items[openEHR-EHR-CLUSTER.opkp_skodljiva_zivila.v1]/items[at0003]/value"),high.weight);
				}
			}
		}
	  m.put(new ArcheDataPath("/data[at0001]/items[at0019]/value"), tmp);
		try {
		OpkpReturnArcheSrv service = new OpkpReturnArcheSrv();
		service.initInput();
		boolean validateData = service.setAndValidataData(m);
		if(!validateData){
			//System.out0.println("not validated");
			doLogInfo(log, "OpkpSave NAPAKA json=" +tmp ,"OPKP","OPKP","","","H");
			def errors = service.getErrorList();
			if(errors != null){
				for(e in errors){
					  doLogInfo(log,"NAPAKA: " + e.getMessage(), "OPKP","OPKP","","","H");
				}
			}
		} else {
		//System.out0.println(session.user);
			//doLogInfo(log, "OpkpSave","OPKP","OPKP","json","","H");
			
			service.saveInput(SystemType.EOSKRBAWEBAPP, "sestra.hu","pacient.hu");

		}
		}
		catch (Exception e) {
			doLogInfo(log, e.getMessage(),"OPKP","OPKP","json",tmp,"H");
		}
	}
}
