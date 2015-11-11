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

import groovy.util.slurpersupport.GPathResult
import javax.xml.xpath.*
import javax.xml.parsers.DocumentBuilderFactory

import org.xml.sax.InputSource;

import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM
import si.pint.database.exist.*
import si.pint.database.exist.DBManager.SystemType;

class ExistService {

    static transactional = true

    // myMeds services
	Map getMyMeds(String userId,String patientId) {
		
		def returnMap = [:]
		returnMap.med1 = [:]
		returnMap.med2 = [:]
		returnMap.med3 = [:]
		
		Map xmlData = getSelectedDataFromSingleXml(
			userId,
			EMPLOYEE_TYPE_ENUM.ASTMA.toDomainName(),
			patientId,
			"openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1",
			[
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0138']/items[@archetype_node_id='at0439']/value/value/text()",                                        label: "med1_name" ],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0138']/items[@archetype_node_id='at0037']/value/value/text()",                                        label: "med1_genname" ],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0138']/items[@archetype_node_id='at0129']/value/magnitude/text()",                                    label: "med1_packnum"],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0138']/items[@archetype_node_id='at0130']/items[@archetype_node_id='at0479']/value/magnitude/text()", label: "med1_qnt"],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0138']/items[@archetype_node_id='at0130']/items[@archetype_node_id='at0480']/value/value/text()",     label: "med1_frequency"],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0142']/items[@archetype_node_id='at0223']/value/value/text()",                                        label: "med2_name" ],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0142']/items[@archetype_node_id='at0145']/value/value/text()",                                        label: "med2_genname" ],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0142']/items[@archetype_node_id='at0146']/value/magnitude/text()",                                    label: "med2_packnum"],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0142']/items[@archetype_node_id='at0147']/items[@archetype_node_id='at0486']/value/magnitude/text()", label: "med2_qnt"],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0142']/items[@archetype_node_id='at0147']/items[@archetype_node_id='at0487']/value/value/text()",     label: "med2_frequency"],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0143']/items[@archetype_node_id='at0149']/value/value/text()",                                        label: "med3_name" ],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0143']/items[@archetype_node_id='at0148']/value/value/text()",                                        label: "med3_genname" ],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0143']/items[@archetype_node_id='at0150']/value/magnitude/text()",                                    label: "med3_packnum"],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0143']/items[@archetype_node_id='at0151']/items[@archetype_node_id='at0488']/value/magnitude/text()", label: "med3_qnt"],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0143']/items[@archetype_node_id='at0151']/items[@archetype_node_id='at0489']/value/value/text()",     label: "med3_frequency"],
				[ query: "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-EVALUATION.terapija.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0478']/value/value/text()",                                                                           label: "other_meds"]
			]
		)
		
		if(xmlData["med1_name"] == "") returnMap.med1 = null
		else {
			returnMap.med1.name      = xmlData["med1_name"]
			returnMap.med1.genname   = xmlData["med1_genname"]
			returnMap.med1.packnum   = xmlData["med1_packnum"]
			returnMap.med1.qnt       = xmlData["med1_qnt"]
			returnMap.med1.frequency = xmlData["med1_frequency"]
		}

		if(xmlData["med2_name"] == "") returnMap.med2 = null
		else {
			returnMap.med2.name      = xmlData["med2_name"]
			returnMap.med2.genname   = xmlData["med2_genname"]
			returnMap.med2.packnum   = xmlData["med2_packnum"]
			returnMap.med2.qnt       = xmlData["med2_qnt"]
			returnMap.med2.frequency = xmlData["med2_frequency"]
		}
		
		if(xmlData["med3_name"] == "") returnMap.med3 = null
		else {
			returnMap.med3.name      = xmlData["med3_name"]
			returnMap.med3.genname   = xmlData["med3_genname"]
			returnMap.med3.packnum   = xmlData["med3_packnum"]
			returnMap.med3.qnt       = xmlData["med3_qnt"]
			returnMap.med3.frequency = xmlData["med3_frequency"]
		}
		
		if(xmlData["other_meds"] == "") returnMap.otherMeds = "Ni dodatnih zdravil."
		else returnMap.otherMeds     = xmlData["other_meds"]

		return returnMap
		
	}
	
	/**
	 * @param fileName ime xml datoteke, ki že obstaja v exist bazi
	 * */
	Map getParsedXML(String userId, String domain, String patientId, String fileName, String templateId) {
		
		String xmlr = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readWholeXml(userId, domain, patientId,fileName,templateId)
		List pairs = []
		
		def content = new XmlSlurper().parseText(xmlr)
		def nodes = content.depthFirst().each {

			if(it.@"xsi:type".text() != "") {
				
				def xt = it.@"xsi:type".text()
				def nd = it
				String name = ""
				String val  = ""
				
				if(xt == "v1:DV_CODED_TEXT") {
					val = nd.defining_code.code_string.text()
					name = "?" + nd.name()
				} else if (xt == "v1:DV_ORDINAL") {
					val = nd.symbol.defining_code.code_string.text()
					name = "?" + nd.name()
				} else if (xt == "v1:DV_QUANTITY" || xt == "v1:DV_COUNT"){
					val = nd.magnitude.text()
					name = "?" + nd.name()
				} else { // v1:DV_TEXT, v1:DV_BOOLEAN
					val = nd.value.text()
					name = "?" + nd.name()
				}
				
				while(nd.parent() != content) {
					nd = nd.parent()
					name =  "?" + nd.name() + ( (nd.@archetype_node_id.text() != "") ? "["+nd.@archetype_node_id.text()+"]" : "" ) + name
				}
				name = name.replace("_", "!")
				pairs << [name:name,val:val]
			}
			
		}
		
		// Zdravila
		def meds = []
		def n = content.items.find{it.@archetype_node_id.text()=="openEHR-EHR-INSTRUCTION.terapija_eo.v1"}.activities.description.items.findAll{it.@archetype_node_id.text()=="openEHR-EHR-CLUSTER.zdravila_eo.v1"}.each {
			if(!it.items.isEmpty()) {
				if(!it.items.find{it.@archetype_node_id.text()=="at0003"}.isEmpty()) {
					meds << [
						gen:     it.items.find{it.@archetype_node_id.text()=="at0003"}.value.value.text(),
						genDesc: it.items.find{it.@archetype_node_id.text()=="at0003"}.value.value.text(),
						nmb:     it.items.find{it.@archetype_node_id.text()=="at0006"}.items.find{it.@archetype_node_id.text()=="at0018"}.value.magnitude.text(),
						qnt:     it.items.find{it.@archetype_node_id.text()=="at0005"}.items.find{it.@archetype_node_id.text()=="at0008"}.value.magnitude.text(),
						frq:     it.items.find{it.@archetype_node_id.text()=="at0005"}.items.find{it.@archetype_node_id.text()=="at0009"}.value.defining_code.code_string.text(),
						frqDesc: it.items.find{it.@archetype_node_id.text()=="at0005"}.items.find{it.@archetype_node_id.text()=="at0009"}.value.value.text()
					]
				}
			}
		}
		
		return [pairs:pairs,meds:meds];
		
	}
	
	Map getParsedXML(String userId, String domain, String patientId, String templateId) {
		
		String xmlr = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readLastWholeXml(userId, domain, patientId, templateId)
		List pairs = []
		
		def content = new XmlSlurper().parseText(xmlr)
		def nodes = content.depthFirst().each {

			if(it.@"xsi:type".text() != "") {
				
				def xt = it.@"xsi:type".text()
				def nd = it
				String name = ""
				String val  = ""
				
				if(xt == "v1:DV_CODED_TEXT") {
					val = nd.defining_code.code_string.text()
					name = "?" + nd.name()
				} else if (xt == "v1:DV_ORDINAL") {
					val = nd.symbol.defining_code.code_string.text()
					name = "?" + nd.name()
				} else if (xt == "v1:DV_QUANTITY" || xt == "v1:DV_COUNT"){
					val = nd.magnitude.text()
					name = "?" + nd.name()
				} else { // v1:DV_TEXT, v1:DV_BOOLEAN
					val = nd.value.text()
					name = "?" + nd.name()
				}
				
				while(nd.parent() != content) {
					nd = nd.parent()
					name =  "?" + nd.name() + ( (nd.@archetype_node_id.text() != "") ? "["+nd.@archetype_node_id.text()+"]" : "" ) + name
				}
				name = name.replace("_", "!")
				pairs << [name:name,val:val]
			}
			
		}
		
		// Zdravila
		def meds = []
		def n = content.items.find{it.@archetype_node_id.text()=="openEHR-EHR-INSTRUCTION.terapija_eo.v1"}.activities.description.items.findAll{it.@archetype_node_id.text()=="openEHR-EHR-CLUSTER.zdravila_eo.v1"}.each {
			if(!it.items.isEmpty()) {
				if(!it.items.find{it.@archetype_node_id.text()=="at0003"}.isEmpty()) {
					meds << [
						gen:     it.items.find{it.@archetype_node_id.text()=="at0003"}.value.value.text(),
						genDesc: it.items.find{it.@archetype_node_id.text()=="at0003"}.value.value.text(),
						nmb:     it.items.find{it.@archetype_node_id.text()=="at0006"}.items.find{it.@archetype_node_id.text()=="at0018"}.value.magnitude.text(),
						qnt:     it.items.find{it.@archetype_node_id.text()=="at0005"}.items.find{it.@archetype_node_id.text()=="at0008"}.value.magnitude.text(),
						frq:     it.items.find{it.@archetype_node_id.text()=="at0005"}.items.find{it.@archetype_node_id.text()=="at0009"}.value.defining_code.code_string.text(),
						frqDesc: it.items.find{it.@archetype_node_id.text()=="at0005"}.items.find{it.@archetype_node_id.text()=="at0009"}.value.value.text()
					]
				}
			}
		}
		
		return [pairs:pairs,meds:meds];
		
	}
	
	List parseXMLForMeds(String userId,String domain, String patientId, String templateId) {
		
		String xmlr = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readLastWholeXml(userId, domain, patientId, templateId)
		def content = new XmlSlurper().parseText(xmlr)
		
		def meds = []
		content.items.find{it.@archetype_node_id.text()=="openEHR-EHR-INSTRUCTION.terapija_eo.v1"}.activities.description.items.findAll{it.@archetype_node_id.text()=="openEHR-EHR-CLUSTER.zdravila_eo.v1"}.each {
			if(!it.items.isEmpty()) {
				if(!it.items.find{it.@archetype_node_id.text()=="at0003"}.isEmpty()) {
					meds << [
						gen:     it.items.find{it.@archetype_node_id.text()=="at0003"}.value.value.text(),
						genDesc: it.items.find{it.@archetype_node_id.text()=="at0003"}.value.value.text(),
						nmb:     it.items.find{it.@archetype_node_id.text()=="at0006"}.items.find{it.@archetype_node_id.text()=="at0018"}.value.magnitude.text(),
						qnt:     it.items.find{it.@archetype_node_id.text()=="at0005"}.items.find{it.@archetype_node_id.text()=="at0008"}.value.magnitude.text(),
						frq:     it.items.find{it.@archetype_node_id.text()=="at0005"}.items.find{it.@archetype_node_id.text()=="at0009"}.value.defining_code.code_string.text(),
						frqDesc: it.items.find{it.@archetype_node_id.text()=="at0005"}.items.find{it.@archetype_node_id.text()=="at0009"}.value.value.text()
					]
				}
			}
		}
		
		return meds
		
	}
	
	List getAllEmails(String userId, String patientId) {
		
		List allEmails = []
		
		String[][] astmaAllEmails = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readLastAll(userId,patientId,
			"openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1",
			"//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1']/data[@archetype_node_id='at0003']/items[@archetype_node_id='at0081']/value/value/text()",
			EMPLOYEE_TYPE_ENUM.ASTMA
		);
		astmaAllEmails.each {
			allEmails << it[1]
		}
		
		String[][] diabetesAllEmails = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readLastAll(userId,patientId,
			"openEHR-EHR-SECTION.vkljucitev_eo.v1",
			"//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0007']/value/value/text()",
			EMPLOYEE_TYPE_ENUM.DIABETES
		);
		diabetesAllEmails.each {
			allEmails << it[1]
		}
		
		String[][] hujsanjeAllEmails = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readLastAll(userId,patientId,
			"openEHR-EHR-SECTION.vkljucitev_eo.v1",
			"//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0007']/value/value/text()",
			EMPLOYEE_TYPE_ENUM.HUJSANJE
		);
		hujsanjeAllEmails.each {
			allEmails << it[1]
		}
		
		String[][] shizofrenijaAllEmails = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readLastAll(userId,patientId,
			"openEHR-EHR-SECTION.vkljucitev_eo.v1",
			"//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0007']/value/value/text()",
			EMPLOYEE_TYPE_ENUM.HUJSANJE
		);
		shizofrenijaAllEmails.each {
			allEmails << it[1]
		}
		
		String[][] sportAllEmails = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readLastAll(userId,patientId,
			"openEHR-EHR-SECTION.vkljucitev_eo.v1",
			"//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0007']/value/value/text()",
			EMPLOYEE_TYPE_ENUM.SPORT
		);
		sportAllEmails.each {
			allEmails << it[1]
		}
		
		return allEmails;
		
	}
	
	/*
	 * Parameter selectors mora biti List, v katerem mora biti poljubno število objekotv Map
	 * naslednje oblike:
	 * 
	 * 	[ query:"XPathQuery", label:"PoljubnoImeZaVrnjenoVrednost" ]
	 * 
	 * Od oblike parametra selectors je odvisna tudi zgradba rezultata, ki ga funkcija vrne.
	 * 
	 * Primer: 
	 * 
	 *  selectors = [
	 *  	[ query: "//content[@id='posta']/item[0]/value", label: "obcina" ],
	 *  	[ query: "//content[@id='posta']/item[1]/value", label: "postnaStevilka" ]
	 *  ]
	 *  
	 * Zgornji primer pri uporabi v funkciji vrne rezultat, ki je npr. takšen:
	 * 
	 * 	selectedData = [
	 * 		[ obcina: "Ljubljana", postnaStevilka: "1000", xmlName: "1.xml", xmlTimestamp:"1" ],
	 * 		[ obcina: "Portoroz",  postnaStevilka: "6300", xmlName: "2.xml", xmlTimestamp:"2" ],
	 * 		[ obcina: "Secovlje",  postnaStevilka: "6333", xmlName: "3.xml", xmlTimestamp:"3" ]
	 *  ]
	 *  
	 * Rezultat je torej objekt tipa List, v katerem so objekti tipa Map, ki za ključe
	 * uporabljajo niz-e, ki so bili navedeni kot label za vsak query iz parametra
	 * selectors.
	 * 
	 */
	List getSelectedData(String userId,String domain, String patientId, String templateId, List selectors, int maxNumOfXmls = 0) {
		
		// Inicializiraj strukturo za vračanje podatkov
		List selectedData = []
		
		// Pridobi vse XML-je iz baze, ki se nahajajo na poti domain/patientId/templateId
		String[] xmls = null
		xmls = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readAllXmls(
			userId,
			domain,
			patientId,
			templateId,
			maxNumOfXmls
		)
		if(xmls == [] || xmls == null) return []
		
		// Pojdi čez vse XML-je
		for( xmlData in xmls ) {
			
			// Odstrani namespaces-e
			xmlData = xmlData.replaceAll(/xmlns=\"[^\"]*\"/,"")
			xmlData = xmlData.replaceAll(/xmlns:xsi=\"[^\"]*\"/, "")
			xmlData = xmlData.replaceAll(/xsi:/,"")
			
			// Izlušči timestamp
			def xmlName = null
			def xmlTimestamp = null
			def xmlMatcher = xmlData =~ /<!-- *xml="(.*)" *-->/
			if(xmlMatcher) {
				xmlName = xmlMatcher[0][1]
				xmlTimestamp = xmlName.reverse().replaceFirst("lmx.", "").reverse()
			}
			
			// System.out.println(xmlData)
			
			// Pripravi 'vrstico' za rezultat
			Map selectedDataEntry = [:]
			selectedDataEntry.xmlName = xmlName
			selectedDataEntry.xmlTimestamp = xmlTimestamp
			
			// Za vsak selector v selectors izlušči podatek iz XML-ja in ga dodaj
			// v 'vrstico' za rezultat
			def xpath = XPathFactory.newInstance().newXPath();
			for (selector in selectors) {
				def xpathResult = xpath.evaluate( selector.query, new InputSource(new StringReader(xmlData)) )
				selectedDataEntry[selector.label] = xpathResult
			}
			
			// 'Vrstico' za rezultat dodaj k rezultatu
			selectedData.add(selectedDataEntry)
			
		}
		
		// Vrni rezultat
		return selectedData
		
	}
	
	/*
	* Parameter selectors mora biti List, v katerem mora biti poljubno število objekotv Map
	* naslednje oblike:
	*
	* 	[ query:"XPathQuery", label:"PoljubnoImeZaVrnjenoVrednost" ]
	*
	* Od oblike parametra selectors je odvisna tudi zgradba rezultata, ki ga funkcija vrne.
	*
	* Primer:
	*
	*  selectors = [
	*  	[ query: "//content[@id='posta']/item[0]/value", label: "obcina" ],
	*  	[ query: "//content[@id='posta']/item[1]/value", label: "postnaStevilka" ]
	*  ]
	*
	* Zgornji primer pri uporabi v funkciji vrne rezultat, ki je npr. takšen:
	*
	* 	selectedData = [ obcina: "Ljubljana", postnaStevilka: "1000" ]
	*
	* Rezultat je torej objekt tipa map, ki za ključe
	* uporablja niz-e, ki so bili navedeni kot label za vsak query iz parametra
	* selectors.
	*
	*/
	
   Map getSelectedDataFromSingleXml(String userId,String domain, String patientId, String templateId, List selectors) {
	   
	   // Inicializiraj strukturo za vračanje podatkov
	   Map selectedData = [:]
	   
	   // Pridobi vse XML-je iz baze, ki se nahajajo na poti domain/patientId/templateId
	   String xml = null
	   xml = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readLastWholeXml(
		   userId,
		   domain,
		   patientId,
		   templateId
	   )
	   if(xml == "" || xml == null) return [:]
	   
	   // Odstrani namespaces-e
	   xml = xml.replaceAll(/xmlns=\"[^\"]*\"/,"")
	   xml = xml.replaceAll(/xmlns:xsi=\"[^\"]*\"/, "")
	   xml = xml.replaceAll(/xsi:/,"")
	   
	   // System.out.println(xml)
	   
	   // Za vsak selector v selectors izlušči podatek iz XML-ja in ga dodaj
	   // v 'vrstico' za rezultat
	   def xpath = XPathFactory.newInstance().newXPath();
	   for (selector in selectors) {
		   def xpathResult = xpath.evaluate( selector.query, new InputSource(new StringReader(xml)) )
		   selectedData[selector.label] = xpathResult
	   }
	   
	   // Vrni rezultat
	   return selectedData
	   
   }
   
   /**
    * Prebere uporabnikov koledar (program vadb) in izlušči podatke o vadbi, ki je na dan exerciseDate
    * 
    * @param userId Uporabniško ime pacient-a
    * @param exerciseDate Datum planirane vadbe (v obliki '2012-08-17T00')
    * @return Podatki o vadbi ali null, če na izbran dan ni planirane vadbe
    */
	Map getExerciseData(String userId,String exerciseDate) {
	   
		// Pridobi xml z vadbami po pacientovem programu
		String xml = ""
		
		try {
			xml = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readLastWholeXml(
				userId,
				"eSport",
				userId,
				"openEHR-EHR-OBSERVATION.sport_koledar.v1"
			)
		} catch(Exception e) {
			println("[eOskrba-webapp] [ExistService] [getTodaysExerciseData] Branje koledarja pacienta " +userId+ " je spodletelo. [1]")
			return null
		}
		
		if(xml == "" || xml == null) {
			println("[eOskrba-webapp] [ExistService] [getTodaysExerciseData] Branje koledarja pacienta " +userId+ " je spodletelo. [2]")
			return null
		}
		
		// Poišči datum vadbe v xml-ju
		xml = xml.replace("\n", "").replace("\r", "")
		int idx1 = xml.indexOf(exerciseDate);
		
		// Če vadba ni najdena vrnemo null
		if(idx1 < 0) {
			return null
		}
		
		// Počisti vsebino xml-ja tako, da ostanejo samo podatki vadbe
		xml = xml.substring(idx1)
		int idx2 = xml.indexOf("<items archetype_node_id=\"openEHR-EHR-CLUSTER.sport_koledar.v1\" xsi:type=\"v1:CLUSTER\">")
		if(idx2 >= 0) {
			xml = xml.substring(0, idx2)
		} else {
			idx2 = xml.indexOf("</data>")
			xml = xml.substring(0, idx2)
		}
		
		// popravi vsebino xml-ja
		xml = "<items><items archetype_node_id=\"at0002\"><name><value>Datum vadbe</value></name><value><value>" + xml
		
		// Odstrani namespaces-e
		xml = xml.replaceAll(/xmlns=\"[^\"]*\"/,"")
		xml = xml.replaceAll(/xmlns:xsi=\"[^\"]*\"/, "")
		xml = xml.replaceAll(/xsi:/,"")
		
		// Izlušči podatke in jih spravi v map
		Map dataToReturn = [:]
		XmlSlurper xsl = new XmlSlurper()
		GPathResult rn = xsl.parseText(xml)
		
		// Datum
		dataToReturn.date          = exerciseDate[8..9] + "." + exerciseDate[5..6] + "." + exerciseDate[0..3]
		
		// Podtip
		dataToReturn.subtype       = rn.items.find { it.@archetype_node_id.text() == "at0003" }.value.value.text()
		dataToReturn.subtypeCode   = rn.items.find { it.@archetype_node_id.text() == "at0003" }.value.defining_code.code_string.text()
		
		// Trajanje
		dataToReturn.duration      = rn.items.find { it.@archetype_node_id.text() == "at0004" }.value.value.text()[0..4]
		
		// Discipina
		dataToReturn.type          = rn.items.find { it.@archetype_node_id.text() == "at0005" }.value.value.text()
		dataToReturn.typeCode      = rn.items.find { it.@archetype_node_id.text() == "at0005" }.value.defining_code.code_string.text()
		
		// Intentivnost
		dataToReturn.intensity     = rn.items.find { it.@archetype_node_id.text() == "at0006" }.value.value.text()
		dataToReturn.intensityCode = rn.items.find { it.@archetype_node_id.text() == "at0006" }.value.defining_code.code_string.text()
		
		// Vrni map
		return dataToReturn
		
	}
	
	/**
	 * Za izbranega uporabnika vrne seznam dni v izbranem mesecu, v katerih ima uporabnik planirano vadbo
	 * po programu (eŠport).
	 * 
	 * @param userId ID uporabnika
	 * @param month Mesec
	 * @param year Leto
	 * @return Seznam dni, v katerih je planirana vadba po programu, npr. [1,4,8,...] ali null, če pride do napake
	 */
	List getExerciseDatesInMonth(String userId, int month, int year) {
		
		// Pridobi xml z vadbami po pacientovem programu
		String xml = ""
		
		try {
			xml = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readLastWholeXml(
				userId,
				"eSport",
				userId,
				"openEHR-EHR-OBSERVATION.sport_koledar.v1"
			)
		} catch(Exception e) {
			println("[eOskrba-webapp] [ExistService] [getTodaysExerciseData] Branje koledarja pacienta " +userId+ " je spodletelo. [1]")
			return null
		}
		
		if(xml == "" || xml == null) {
			println("[eOskrba-webapp] [ExistService] [getTodaysExerciseData] Branje koledarja pacienta " +userId+ " je spodletelo. [2]")
			return null
		}
		
		// Počisti
		xml = xml.replace("\n", "").replace("\r", "")
		
		// Za vsak dan v mesecu preglej, ali je planirana vadba
		List returnList = []
		for(int day = 1; day < 32; day++) {
			
			// Sestavi datum (timestamp)
			String dateStamp = year + "-" + ((month<10)?("0"+month):(month)) + "-" + ((day<10)?("0"+day):(day)) + "T00"
			
			// Poišči datum v programu
			if( xml.indexOf(dateStamp) >= 0 ) returnList.add(day)
			
		}
		
		// Vrni seznam
		return returnList
		
	}
	
	List getAllPatients(String userId,EMPLOYEE_TYPE_ENUM employeeType) {
		
		def patientsList = []
		
		def patientsUsernames = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).getAllUsernames(
			userId,
			employeeType.toDomainName()
		)
		
		def templateName = "openEHR-EHR-SECTION.vkljucitev_eo.v1"
		if(employeeType == EMPLOYEE_TYPE_ENUM.ASTMA) templateName = "openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1"
		
		def selectors = [
			[label:"priimek",  query:"//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1']/data/items[@archetype_node_id='at0003']/value/value"],
			[label:"ime",      query:"//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1']/data/items[@archetype_node_id='at0002']/value/value"],
			[label:"ustanova", query:"//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1']/data/items[@archetype_node_id='at0009']/value/value"],
			[label:"pogoji",   query:"//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.osnovni_podatki_eo.v1']/data/items[@archetype_node_id='at0011']/value/value"]
		]
		if(employeeType == EMPLOYEE_TYPE_ENUM.ASTMA) selectors = [
			[label:"priimek",  query:"//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1']/data/items[@archetype_node_id='at0075']/value/value"],
			[label:"ime",      query:"//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1']/data/items[@archetype_node_id='at0004']/value/value"],
			[label:"ustanova", query:"//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1']/data/items[@archetype_node_id='at0083']/value/value"],
			[label:"pogoji",   query:"//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1']/items[@archetype_node_id='openEHR-EHR-ADMIN_ENTRY.admission_details_eo_as.v1']/data/items[@archetype_node_id='at0094']/value/value"]
		]

		patientsUsernames.each { usrnam ->
			def patientEntry = [:]
			patientEntry.uid = usrnam
			
			// Pridobi ostale podatke
			Map patientData = getSelectedDataFromSingleXml(
				userId,
				employeeType.toDomainName(),
				usrnam,
				templateName,
				selectors
			)
			
			patientEntry.firstName = patientData.ime
			patientEntry.lastName = patientData.priimek
			patientEntry.fullName = patientData.ime + " " + patientData.priimek
			patientEntry.fullNameReverse = patientData.priimek + " " + patientData.ime
			patientEntry.healthcareInstitution = patientData.ustanova
			patientEntry.otherRequirements = patientData.pogoji
			
			patientsList.add(patientEntry)
		}
		
		return patientsList
		
	}
	
	/*
	 * Iz seznama List, ki je sestavjen iz objektov Map vrne samo tiste,
	 * ki za določen ključ key imajo vrednost, ki ni null ali prazen string.
	 * 
	 * S paramterom maxItems lahko določimo, koliko največ objektov Map metoda
	 * vrne.
	 */
	List getNonEmptyItemsFromListOfMaps(List listOfMaps, String key , int maxItems = 0) {
		
		if(maxItems == 0) maxItems = listOfMaps.size()
		List result = []
		
		listOfMaps.each{ map ->
			if(result.size() < maxItems) if(map[key] != null) if( !(map[key].toString().isEmpty()) ) result.add(map)
		}
		
		return result
		
	}
	
	public static final String DOMAIN_SPORT_CALENDAR_EXERCISE_INTENSITY = "DOMAIN_SPORT_CALENDAR_EXERCISE_INTENSITY"
	public static final String DOMAIN_SPORT_CALENDAR_EXERCISE_TYPE      = "DOMAIN_SPORT_CALENDAR_EXERCISE_TYPE"
	public static final String DOMAIN_SPORT_INPUT_EXERCISE_INTENSITY    = "DOMAIN_SPORT_INPUT_EXERCISE_INTENSITY"
	public static final String DOMAIN_SPORT_INPUT_EXERCISE_TYPE         = "DOMAIN_SPORT_INPUT_EXERCISE_TYPE"
	public static final String DOMAIN_SPORT_DESC_EXERCISE_TYPE          = "DOMAIN_SPORT_DESC_EXERCISE_TYPE"
	public static final String DOMAIN_SPORT_DESC_EXERCISE_INTENSITY     = "DOMAIN_SPORT_DESC_EXERCISE_INTENSITY"
	/**
	 * Pomožna metoda ki neko vrednost (ponavadi oblike 'at0001') prevede v drugo vrednost
	 * (npr. v 'at0002'() glede na izbrano domeno vrednosti in ciljno domeno
	 *
	 * @param fromDomain Kodno ime domene vrednosti, ki jo želimo preslikati
	 * @param toDomain Kodno ime ciljne domene
	 * @param value Vrednost, ki jo želimo preslikati
	 * @return Preslikana vrednost ali enaka vrednost, kot parameter value, če metoda ne zna prevajati izbrane kombinacije domen
	 */
	public static String domainTranslate (String fromDomain, String toDomain, String value) {
		
		if(fromDomain == DOMAIN_SPORT_CALENDAR_EXERCISE_INTENSITY && toDomain == DOMAIN_SPORT_INPUT_EXERCISE_INTENSITY) {
			if(value == "at0015") return "at0002" // Zelo nizka intenzivnost
			if(value == "at0016") return "at0003" // Nizka intenzivnost
			if(value == "at0017") return "at0004" // Zmerna intenzivnost
			if(value == "at0018") return "at0005" // Visoka intenzivnost
			if(value == "at0019") return "at0006" // Zelo visoka intenzivnost
		}
		
		if(fromDomain == DOMAIN_SPORT_CALENDAR_EXERCISE_TYPE && toDomain == DOMAIN_SPORT_INPUT_EXERCISE_TYPE) {
			if(value == "at0010") return "at0040" // Vaje za koordinacijo
		}
		
		if(fromDomain == DOMAIN_SPORT_INPUT_EXERCISE_TYPE && toDomain == DOMAIN_SPORT_DESC_EXERCISE_TYPE) {
			if(value == "at0022") return "aerobika (različne oblike)"
			if(value == "at0023") return "Cooperjev test - 2400 m"
			if(value == "at0024") return "Cooperjev test - 12 min"
			if(value == "at0025") return "fFitnes (delo z utežmi)"
			if(value == "at0026") return "hoja"
			if(value == "at0027") return "igre z loparjem (tenis, badminton, squash)"
			if(value == "at0028") return "igre z žogo (nogomet, košarka, odbojka)"
			if(value == "at0029") return "kolesarjenje (cestno, gorsko)"
			if(value == "at0030") return "kolesarjenje (sobno)"
			if(value == "at0031") return "plavanje"
			if(value == "at0032") return "pohodništvo"
			if(value == "at0033") return "rolanje"
			if(value == "at0034") return "smučanje"
			if(value == "at0035") return "sproščanje"
			if(value == "at0036") return "tek"
			if(value == "at0037") return "tek na smučeh"
			if(value == "at0038") return "test hoje 2 km"
			if(value == "at0039") return "vaje za gibljivost"
			if(value == "at0040") return "vaje za koordinacijo"
			if(value == "at0041") return "vaje za moč"
			if(value == "at0042") return "drugo"
			return "neznano"
		}
		
		return value
		
	}
	
}
