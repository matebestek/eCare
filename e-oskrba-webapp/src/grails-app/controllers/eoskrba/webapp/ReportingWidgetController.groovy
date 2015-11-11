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

import java.text.DateFormat;

import javax.tools.ForwardingFileObject;

import eoskrba.webapp.common.LogEnabledController
import si.pint.eoskrba.model.User
import si.pint.database.exist.DBManager;
import si.pint.database.exist.DBManager.SystemType
import grails.converters.JSON
import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.utils.OpkpUtils;
import org.apache.commons.codec.binary.Base64;
import eoskrba.webapp.utils.UtilFunctions;

class ReportingWidgetController extends LogEnabledController{

	
	def reportingWidgetService
	def activitiService
	def existService
	
    def patientPicker = { 
		doLogInfo(log, "Izbira pacienta",session.user,session.role,"","",session.employeeType);
		// years range
		def cal = new GregorianCalendar()
		cal.time
		
		def thisYear = cal.get(Calendar.YEAR)
		
		[yearRange: 2011..thisYear]
		
	}
	
	def patientShow = {
		
		// Ta akcija ne naredi nič drugega, kot da določi pacienta in posreduje
		// zahtevo naprej akciji show
		doLogInfo(log, "Prikaz pacienta",session.user,session.role,"","",session.employeeType);
		params.who = session.user
		forward action:"show", params: params
	}
	
	def patientHistory = {
		
	}
	
	def quickGraph = {
		doLogInfo(log, "Hitri graf",session.user,session.role,"","",session.employeeType);
		def who = params.id
		def what = params.what
		def today = new GregorianCalendar()
		today.time = new Date()
		def fromMonth = today.get(Calendar.MONTH) + 1
		def fromYear = today.get(Calendar.YEAR)
		def tillMonth = today.get(Calendar.MONTH) + 1
		def tillYear = today.get(Calendar.YEAR)
		
		// get raw data
		def report = reportingWidgetService.getData(session.employeeType,who,what,fromMonth,fromYear,tillMonth,tillYear)
		def data = report.data
		
		if (data == null || data == [] || data.value==[null]) {
			forward(controller:'reportingWidget',action:'noDataFound', params:[who: who]);
			return;
		}

		def dataString = reportingWidgetService.dataToString(data)
		
		def threshold = [bad:0,neutral:0,higherIsBetter:true]
		
		if(what == "PEF") threshold = [bad:480,neutral:520,higherIsBetter:true]
		else if(what == "ACT") threshold = [bad:18,neutral:20,higherIsBetter:true]
		else if(what == "KRVNI_SLADKOR") threshold = [bad:7,neutral:5,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti
		else if(what == "TELESNA_MASA") threshold = [bad:80,neutral:70,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		else if(what == "SISTOLICNI_KRVNI_TLAK") threshold = [bad:100,neutral:70,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		else if(what == "DIASTOLICNI_KRVNI_TLAK") threshold = [bad:140,neutral:120,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		else if(what == "TELESNA_MASA_EHUJSANJE") threshold = [bad:80,neutral:70,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		else if(what == "INDEKS_TELESNE_MASE_EHUJSANJE") threshold = [bad:29,neutral:20,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		else if(what == "OBSEG_PASU_EHUJSANJE") threshold = [bad:100,neutral:90,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		// šport
		else if(what == "TELESNA_MASA_ESPORT") threshold = [bad:80,neutral:70,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		else if(what == "PANOGE_VADBE") threshold = [bad:80,neutral:70,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		else if(what == "TRAJANJE_VADBE") threshold = [bad:80,neutral:70,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		else if(what == "PORABLJENE_PKCAL") threshold = [bad:80,neutral:70,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		
		def months = ["januar","februar","marec","april","maj","junij","julij","avgust","september","oktober","november","december"]
		def fromString = months[fromMonth - 1] + " " + fromYear
		def tillString = months[tillMonth - 1] + " " + tillYear
		
		[dataString:dataString,threshold:threshold]
		
	}
	
	def picker = {
		doLogInfo(log, "Izbor ",session.user,session.role,"","",session.employeeType);
		// years range
		def cal = new GregorianCalendar()
		cal.time
		
		def thisYear = cal.get(Calendar.YEAR)
		
		[yearRange: 2011..thisYear]
		
	}
	
	def show = {
		doLogInfo(log, "Prikaz",session.user,session.role,"","",session.employeeType);
		def who  = params.who
		def what = params.what
		def fromMonth = params.fromMonth as int
		def fromYear = params.fromYear as int
		def tillMonth = params.tillMonth as int
		def tillYear = params.tillYear as int
		
		if(what == "KOLICINA_MINUT_GIBALNE_AKTVNOSTI" || what == "GIBALNA_AKTIVNOST_ESPORT" || what == "PORABLJENE_PKCAL" || what == "RAZDALJA_ESPORT") {
			forward action:"showHistogram", params: params
			return;
		} else if(what == "EDIABETES_KRVNI_TLAK" || what == "TELESNA_MASA_EHUJSANJE") {
			forward action:"showDouble", params: params
		} else if(what == "ENERGIJSKI_DIAGRAMI" || what == "ZIVILSKI_DIAGRAMI") {
			forward action:"showEHujsanjeOPKPgraphs", params:params
		} 
		
		// Spremeni časovni okvir v pravi format
		def from = (new java.text.SimpleDateFormat("yyyy-MM-dd").parse(fromYear+"-"+fromMonth+"-01")).getTime()
		def till = (new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tillYear+"-"+tillMonth+"-31")).getTime()
		
		// get raw data
		def report = reportingWidgetService.getData(session.employeeType, who,what,fromMonth,fromYear,tillMonth,tillYear)
		def data = report.data
		
		if (data == null || data == [] || data.value==[null]) {
			forward(controller:'reportingWidget',action:'noDataFound', params:[who: who]);
			return;
		}

		def dataString = reportingWidgetService.dataToString(data)
		
		// Naslednje spremenljivke morajo biti določene pred prikazom
		def whatNice = what
		def threshold = [bad:0,neutral:0,higherIsBetter:true]
		
		if(what == "PEF") {
			whatNice = "PEF"
			threshold = [bad:480,neutral:520,higherIsBetter:true]
		} else if(what == "ACT") {
			whatNice = "ACT"
			threshold = [bad:18,neutral:20,higherIsBetter:true]
		} else if(what == "KRVNI_SLADKOR") {
			whatNice = "krvnega sladkorja"
			threshold = [bad:7,neutral:5,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti
		} else if(what == "TELESNA_MASA") {
			whatNice = "telesne mase"
			threshold = [bad:80,neutral:70,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		} else if(what == "SISTOLICNI_KRVNI_TLAK") {
			whatNice = "sistoličnega krvnega tlaka"
			threshold = [bad:100,neutral:70,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		} else if(what == "DIASTOLICNI_KRVNI_TLAK") {
			whatNice = "disatoličnega krvnega tlaka"
			threshold = [bad:140,neutral:120,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		} else if(what == "TELESNA_MASA_EHUJSANJE") {
			whatNice = "telesne mase"
			threshold = [bad:80,neutral:70,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		} else if(what == "INDEKS_TELESNE_MASE_EHUJSANJE") {
			whatNice = "indeksa telesne mase"
			threshold = [bad:29,neutral:20,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		} else if(what == "OBSEG_PASU_EHUJSANJE") {
			whatNice = "obsega pasu"
			threshold = [bad:100,neutral:90,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		} else if(what == "TELESNA_MASA_ESPORT") {
			whatNice = "telesne mase"
			threshold = [bad:80,neutral:70,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		} else if(what == "PANOGE_VADBE") {
			whatNice = "športnih panog"
			threshold = [bad:80,neutral:70,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		} else if(what == "TRAJANJE_VADBE") {
			whatNice = "trajanja vadbe"
			threshold = [bad:80,neutral:70,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		} else if(what == "PORABLJENE_PKCAL") {
			whatNice = "porabljenih kCal"
			threshold = [bad:80,neutral:70,higherIsBetter:false]//TODO: ugotovi kaj so prave mejne vrednosti; telesna masa je odvisna od višine posameznika
		}
		
		def months = ["januar","februar","marec","april","maj","junij","julij","avgust","september","oktober","november","december"]
		def fromString = months[fromMonth - 1] + " " + fromYear
		def tillString = months[tillMonth - 1] + " " + tillYear
		
		[who:who,data:data,dataString:dataString,threshold:threshold,what:what,whatNice:whatNice,fromString:fromString,from:from,tillString:tillString,till:till, max: report.max, avg: report.avg, min: report.min]
		
	}
	
	def showHistogram = {
		
		def who  = params.who
		def what = params.what
		def fromMonth = params.fromMonth as int
		def fromYear = params.fromYear as int
		def tillMonth = params.tillMonth as int
		def tillYear = params.tillYear as int
		
		// Spremeni časovni okvir v pravi format
		def from = (new java.text.SimpleDateFormat("yyyy-MM-dd").parse(fromYear+"-"+fromMonth+"-01")).getTime()
		def till = (new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tillYear+"-"+tillMonth+"-31")).getTime()
		
		// Spodnje spremenljivke morajo biti določene pred koncem funkcije
		List data = []
		String dataString = ""
		String refDataString = ""
		String naslov = "Neimenovano poročilo"
		String naslovGrafa = "Graf spreminjanja vrednosti " + what + " s časom"
		String sirinaStolpca = "0.8"
		String unitName = ""
		def minutePoPanogah = [:]
		def aktivnostiPoDatumih = [:]
		
		if(what == "KOLICINA_MINUT_GIBALNE_AKTVNOSTI" || what == "GIBALNA_AKTIVNOST_ESPORT" || what == "PORABLJENE_PKCAL" || what == "RAZDALJA_ESPORT") {
			
			// Nastavi lastnosti histograma
			naslov = "Prikaz opravljenih aktivnosti v izbranem času"
			naslovGrafa = "Histogram opravljenih aktivnosti v izbranem času"
			sirinaStolpca = "80000000"
			
			List selectors = []
			String domain = ""
			String templateId = ""
			
			// Nastavi za eHujšanje - Gibalne aktivnosti
			if(what == "KOLICINA_MINUT_GIBALNE_AKTVNOSTI") {
				domain = "eHujsanje"
				templateId = "openEHR-EHR-SECTION.opomnik_eo_hu_pv.v1"
				unitName = "min"
				selectors = [
					[label:"datum",    query:"//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_hu_pv.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0019']/value/value"],
					[label:"vrednost", query:"//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_hu_pv.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0006']/value/value"],
					[label:"panoga",   query:"//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_hu_pv.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0004']/value/value"]
				]
			}
			
			// Nastavi za eŠport - Gibalne aktivnosti
			else if(what == "GIBALNA_AKTIVNOST_ESPORT") {
				domain = "eSport"
				templateId = "openEHR-EHR-SECTION.opomnik_eo_sp.v1"
				unitName = "min"
				selectors = [
					[label:"datum",    query:"//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_sp.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0019']/value/value"],
					[label:"vrednost", query:"//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_sp.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0006']/value/value"],
					[label:"panoga",   query:"//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_sp.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0004']/value/value"]
				]
			}
			
			// Nastavi za eŠport - Porabljene kcal
			else if(what == "PORABLJENE_PKCAL") {
				domain = "eSport"
				templateId = "openEHR-EHR-SECTION.opomnik_eo_sp.v1"
				unitName = "kcal"
				selectors = [
					[label:"datum",    query:"//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_sp.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0019']/value/value"],
					[label:"vrednost", query:"//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_sp.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0057']/value/magnitude"],
					[label:"panoga",   query:"//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_sp.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0004']/value/value"]
				]
			}
			
			// Nastavi za eŠport - Razdalja
			else if(what == "RAZDALJA_ESPORT") {
				domain = "eSport"
				templateId = "openEHR-EHR-SECTION.opomnik_eo_sp.v1"
				unitName = "m"
				selectors = [
					[label:"datum",    query:"//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_sp.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0019']/value/value"],
					[label:"vrednost", query:"//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_sp.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0055']/value/magnitude"],
					[label:"panoga",   query:"//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_sp.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.telesna_dejavnost_enkratna_eo.v1']/data/events/data/items[@archetype_node_id='at0004']/value/value"]
				]
			}
			
			// Pridobi podatke
			data = existService.getSelectedData(
				session.user,
				domain, 
				who,
				templateId,
				selectors
			)
			if(data == [] || data == null) {
				forward(controller:'reportingWidget',action:'noDataFound', params:[who: who]);
				return;
			}
			
			// Popravi prejete podatke (odstrani neveljavne, uredi veljavne)
			List elementsToRemove = []
			data.each {
				if( it.datum == null || it.datum == "" || it.vrednost == null || it.vrednost == "") elementsToRemove.add(it)
				else {
					if(it.panoga == null || it.panoga == "") it.panoga = "Neznana aktivnost"
					// Spremeni datume v epoch-time
					def date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(it.datum)
					it.datum = date.getTime()
					
					if(what == "KOLICINA_MINUT_GIBALNE_AKTVNOSTI" || what == "GIBALNA_AKTIVNOST_ESPORT") {
						// Spremeni vrednost v stevilo minut
						int hh = 0
						int mm = 0
						if(it.vrednost.length() >= 5) {
							hh = Integer.parseInt( it.vrednost[0..1] )
							mm = Integer.parseInt( it.vrednost[3..4] )
						}
						it.vrednost = (hh*60) + mm
					}
					// Spremeni vrednost v stevilo
					else {
						it.vrednost = Double.parseDouble(it.vrednost)
					}
					
				}
			}
			elementsToRemove.each{ data -= it }
			
			// Izlusci vse vrste aktivnosti
			List panoge = []
			data.each {
				if(!(panoge.contains(it.panoga))) panoge.add(it.panoga)
			}
			
			// Sortiraj po datumih
			data = data.sort { it.datum }
			
			// Izlusci aktivnosti po datumih
			data.each{
				if(!(aktivnostiPoDatumih.containsKey(it.datum))) {
					aktivnostiPoDatumih[it.datum] = []
				}
				(aktivnostiPoDatumih[it.datum]).add(it)
			}
			
			// Pripravi dataString
			dataString = ""
			refDataString = ""
			def vsajEnaPanogaZapisana = false
			panoge.each { pan ->
				minutePoPanogah[pan] = 0
				def dataStringPart = ""
				def refDataStringPart = ""
				if(vsajEnaPanogaZapisana) {
					dataStringPart = ",{label:'" + pan + "',data:["
					refDataStringPart = ",\"" + pan + "\":{"
				}
				else {
					dataStringPart = "{label:'" + pan + "',data:["
					refDataStringPart = "\"" + pan + "\":{"
					vsajEnaPanogaZapisana = true
				}
				def vsajEnaVrednostZapisana = false
				aktivnostiPoDatumih.each{ dat ->
					// Sestej minute za isto panogo na isti dan
					def skupniCas = 0
					dat.value.each{ datd ->
						if(datd.panoga == pan) skupniCas += datd.vrednost
					}
					minutePoPanogah[pan] += skupniCas
					if(skupniCas > 0) {
						if(vsajEnaVrednostZapisana) {
							dataStringPart += ",[" + dat.key + "," + skupniCas + "]"
							refDataStringPart += ",\"" + dat.key + "\": \"" + skupniCas + "\""
						}
						else {
							dataStringPart += "[" +dat.key + "," + skupniCas + "]"
							refDataStringPart += "\"" + dat.key + "\": \"" + skupniCas + "\""
							vsajEnaVrednostZapisana = true
						}
					}

				}
				dataStringPart += "]}"
				dataString += dataStringPart
				refDataStringPart += "}"
				refDataString += refDataStringPart
			}
			
			// Slovenski zapis datuma
			def months = ["januar","februar","marec","april","maj","junij","julij","avgust","september","oktober","november","december"]
			def fromString = months[fromMonth - 1] + " " + fromYear
			def tillString = months[tillMonth - 1] + " " + tillYear
			
			// V view
			return [who:who,data:data,dataString:dataString,refDataString:refDataString,what:what,fromString:fromString,tillString:tillString,from:from,till:till,naslov:naslov,naslovGrafa:naslovGrafa,sirinaStolpca:sirinaStolpca,minutePoPanogah:minutePoPanogah,aktivnostiPoDatumih:aktivnostiPoDatumih,unitName:unitName]
			
		}
		
	}
	
	def showEHujsanjeOPKPgraphs = {
		
		def who  = params.who
		def what = params.what
		def fromMonth = params.fromMonth as int
		def fromYear = params.fromYear as int
		def tillMonth = params.tillMonth as int
		def tillYear = params.tillYear as int
		
		// Spremeni časovni okvir v pravi format
		def from = (new java.text.SimpleDateFormat("yyyy-MM-dd").parse(fromYear+"-"+fromMonth+"-01")).getTime()
		def till = (new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tillYear+"-"+tillMonth+"-31")).getTime()
		
		def data = existService.getSelectedData(
			session.user,
			"eHujsanje",
			who,
			"openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1",
			[
				[label:"EV",       query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0003']/value/magnitude"],
				[label:"AK",       query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0004']/value/magnitude"],
				[label:"PV",       query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='at0018']/value/magnitude"],
				[label:"ZE_1_IME", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][1]/items[@archetype_node_id='at0002']/value/value"],
				[label:"ZE_1_ZAU", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][1]/items[@archetype_node_id='at0003']/value/magnitude"],
				[label:"ZE_1_PRI", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][1]/items[@archetype_node_id='at0004']/value/value"],
				[label:"ZE_2_IME", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][2]/items[@archetype_node_id='at0002']/value/value"],
				[label:"ZE_2_ZAU", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][2]/items[@archetype_node_id='at0003']/value/magnitude"],
				[label:"ZE_2_PRI", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][2]/items[@archetype_node_id='at0004']/value/value"],
				[label:"ZE_3_IME", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][3]/items[@archetype_node_id='at0002']/value/value"],
				[label:"ZE_3_ZAU", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][3]/items[@archetype_node_id='at0003']/value/magnitude"],
				[label:"ZE_3_PRI", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][3]/items[@archetype_node_id='at0004']/value/value"],
				[label:"ZE_4_IME", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][4]/items[@archetype_node_id='at0002']/value/value"],
				[label:"ZE_4_ZAU", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][4]/items[@archetype_node_id='at0003']/value/magnitude"],
				[label:"ZE_4_PRI", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][4]/items[@archetype_node_id='at0004']/value/value"],
				[label:"ZE_5_IME", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][5]/items[@archetype_node_id='at0002']/value/value"],
				[label:"ZE_5_ZAU", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][5]/items[@archetype_node_id='at0003']/value/magnitude"],
				[label:"ZE_5_PRI", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][5]/items[@archetype_node_id='at0004']/value/value"],
				[label:"ZE_6_IME", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][6]/items[@archetype_node_id='at0002']/value/value"],
				[label:"ZE_6_ZAU", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][6]/items[@archetype_node_id='at0003']/value/magnitude"],
				[label:"ZE_6_PRI", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][6]/items[@archetype_node_id='at0004']/value/value"],
				[label:"ZE_7_IME", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][7]/items[@archetype_node_id='at0002']/value/value"],
				[label:"ZE_7_ZAU", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][7]/items[@archetype_node_id='at0003']/value/magnitude"],
				[label:"ZE_7_PRI", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][7]/items[@archetype_node_id='at0004']/value/value"],
				[label:"ZE_8_IME", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][8]/items[@archetype_node_id='at0002']/value/value"],
				[label:"ZE_8_ZAU", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][8]/items[@archetype_node_id='at0003']/value/magnitude"],
				[label:"ZE_8_PRI", query:"//items[@archetype_node_id='openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1']/data[@archetype_node_id='at0001']/items[@archetype_node_id='openEHR-EHR-CLUSTER.opkp_zauzite_enote.v1'][8]/items[@archetype_node_id='at0004']/value/value"],
			]
		)
		
		// Sortiraj po datumu
		data = data.sort{ it.xmlTimestamp }
		
		// TODO: Filtriraj data glede na datume, ki so bili izbrani (from / till)
		
		def months = ["januar","februar","marec","april","maj","junij","julij","avgust","september","oktober","november","december"]
		def fromString = months[fromMonth - 1] + " " + fromYear
		def tillString = months[tillMonth - 1] + " " + tillYear
		
		// Barve za 8 grafov (krat 2)
		def ZE_COLORS = ["#FF6600","#339966","#99CC00","#333300","#993300","#993366","#333399","#666699"]
		
		[who:who,data:data,what:what,fromString:fromString,tillString:tillString,from:from,till:till,ZE_COLORS:ZE_COLORS]
		
	}
	
	def showDouble = {
		
		doLogInfo(log, "Prikaz (dvojni)",session.user,session.role,"","",session.employeeType);
		
		def who  = params.who
		def what = params.what
		def fromMonth = params.fromMonth as int
		def fromYear = params.fromYear as int
		def tillMonth = params.tillMonth as int
		def tillYear = params.tillYear as int
		
		// Spremeni časovni okvir v pravi format
		def from = (new java.text.SimpleDateFormat("yyyy-MM-dd").parse(fromYear+"-"+fromMonth+"-01")).getTime()
		def till = (new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tillYear+"-"+tillMonth+"-31")).getTime()
		
		def what1 = ""
		def what2 = ""
		def whatNice = ""
		def what1Nice = ""
		def what2Nice = ""
		
		if(what == "EDIABETES_KRVNI_TLAK") {
			whatNice = "krvnega tlaka"
			what1 = "SISTOLICNI_KRVNI_TLAK"
			what2 = "DIASTOLICNI_KRVNI_TLAK"
			what1Nice = "Sistolični krvni tlak"
			what2Nice = "Diastolični krvni tlak"
		}
		else if (what == "TELESNA_MASA_EHUJSANJE") {			
			whatNice = "telesne teže"
			what1 = "TELESNA_MASA_EHUJSANJE_KORAK"
			what2 = "TELESNA_MASA_EHUJSANJE_PV"
			what1Nice = "Telesna teža"
			what2Nice = "Telesna teža (iz prostega vnosa)"
			
		}
		
		// get raw data
		def report1 = reportingWidgetService.getData(session.employeeType, who,what1,fromMonth,fromYear,tillMonth,tillYear)
		def report2 = reportingWidgetService.getData(session.employeeType, who,what2,fromMonth,fromYear,tillMonth,tillYear)
		
		def data1 = report1.data
		def data2 = report2.data
		
		boolean data1Null = data1 == null || data1 == [] || data1.value==[null]
		boolean data2Null = data2 == null || data2 == [] || data2.value==[null]
		if (data1Null && data2Null) {
			forward(controller:'reportingWidget',action:'noDataFound', params:[who: who]);
			return;
		}

		def dataString1 = !data1Null ? reportingWidgetService.dataToString(data1) : "[]"
		def dataString2 = !data2Null ? reportingWidgetService.dataToString(data2) : "[]"
		
		def months = ["januar","februar","marec","april","maj","junij","julij","avgust","september","oktober","november","december"]
		def fromString = months[fromMonth - 1] + " " + fromYear
		def tillString = months[tillMonth - 1] + " " + tillYear
		
		// Združi meritve za prikaz v tabeli
		def dataEnsemble = [:]
		data1.each{ dat1 ->
			dataEnsemble[dat1.time] = [
				value1: dat1.value,
				value2: "/"
			]
		}
		data2.each{ dat2 ->
			if(dataEnsemble.containsKey(dat2.time)) {
				dataEnsemble[dat2.time].value2 = dat2.value
			} else {
				dataEnsemble[dat2.time] = [
					value1: "/",
					value2: dat2.value
				]
			}
		}
		
		dataEnsemble = dataEnsemble.sort{ it.key }
		
		[who:who,data1:data1,data2:data2,dataEnsemble:dataEnsemble,dataString1:dataString1,dataString2:dataString2,what:what,whatNice:whatNice,what1:what1,what1Nice:what1Nice,what2:what2,what2Nice: what2Nice, from:from,fromString:fromString,till:till,tillString:tillString, max1: report1.max, avg1: report1.avg, min1: report1.min, max2: report2.max, avg2: report2.avg, min2: report2.min]
		
	}
	
	def reportShow = {
		try {
			def userid = params.userid;
			if (userid == null) userid = session.user;
			//System.out.println(userid);
			// Get task info
			//System.out.println(request);
			def xmln = Long.parseLong(request.getParameter("xmlName"));
			def taskInfo = ["name":"","description":"","opkpResponse":""]; // = activitiService.getUserTask(params.id)
			taskInfo.name = "Izpolnjen vprašalnik";
			taskInfo.description='TaskIzpolniOPKP';

			String[] ss = new String[2];
			ss[0] = "openEHR-EHR-EVALUATION.vprasalnik_OPKP.v1";
			ss[1] = "//data[@archetype_node_id='at0001']/items[@archetype_node_id='at0019']/value/value/text()";
			
			//System.out.println("iz xml");
			//System.out.println(session.user);
						/*result = DBManager.getInstance(SystemType.EOSKRBAWEBAPP)
							.readLastAll(session.user,session.user,ss[0],
								ss[1], EMPLOYEE_TYPE_ENUM.eval(session.employeeType));*/
			//System.out.println("result");
			String[][] result1 = null;
			//result = DBManager.getInstance(SystemType.EOSKRBAWEBAPP)
				//.readLast(session.user,session.user,ss[0],ss[1] );
			result1 = DBManager.getInstance(SystemType.EOSKRBAWEBAPP)
				.readTimespan(userid,userid,userid
					,ss[0],new Date(xmln),new Date(xmln),ss[1] );
			String result = result1[0][1];
				
			//System.out.println(result);
			if(result != null){
				taskInfo.opkpResponse = JSON.parse(result);
				//System.out.println(taskInfo.opkpResponse);
				session.showResults = true;
			}

			User patient = UserRegistryFactory.getUserFromLDAP(userid);
			patient = MyTasksWidgetController.getWeightAndHeightFromDB(patient, EMPLOYEE_TYPE_ENUM.HUJSANJE);
			
			String urlData = OpkpUtils.prepareOPKPUrlData(patient);
			
			return [taskInfo:taskInfo,taskId:0,data:urlData,isHujsanjeTask:true,isHistoric:true]
				
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
		
	}
	
	def showWorkingSheets = {
		
		doLogInfo(log, "Pridobivam formo za ogled zgodovine delovnih listov ",session.user,session.role, "","",session.employeeType);		
		
		def dataToSubmit = [:]
		//if (GLEDAMO ZASE ALI SESTRA GLEDA ZA NEKOGA DRUGEGA)
		def user = params.userid;
		if (user == null) user = session.user;
			
		dataToSubmit.usernameApp = user;
		dataToSubmit.processStep = params.processStep;
		
		activitiService.startProces("Hujsanje-Proces-Delovni-List-Prikaz", dataToSubmit)
		
		//get all user tasks
		def jsonr = activitiService.getUserTasks(user, 1000)
		def rightTask
		jsonr.data.each {
			if (it.name == 'Delovni list za korak ' + params.processStep) {
				rightTask = it
			}
		}

		if (rightTask == null) {
			doLogInfo(log, "Napaka pri pridobivanju forme za delovni list koraka " + params.processStep,session.user,session.role,"","",session.employeeType);
			return
		}
		
		//2. pridobi task form
		String form = activitiService.getForm(rightTask.id)
		activitiService.completeTask(rightTask.id, [:])
				
		//3. get eXist file for working sheets
		Map xmlData = existService.getParsedXML(session.user,
			EMPLOYEE_TYPE_ENUM.eval(session.employeeType).toDomainName(),
			user,
			params.xmlName + ".xml",
			UtilFunctions.TEMPLATE_ID_WORKING_SHEETS_HISTORY
		);
		
		List formData = xmlData.pairs
				
		
		[form:form,formData:formData]
		
	}
	
	def list = {
		forward(controller:'reportingWidget',action:'listReports')
		return false;
	}
	
	def listReports = {					
	}
	
	def noDataFound = {
		[who:params.who]
	}
	
}
