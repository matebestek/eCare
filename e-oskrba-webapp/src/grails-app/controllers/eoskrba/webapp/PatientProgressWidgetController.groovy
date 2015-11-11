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

import org.apache.log4j.Logger;

import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import si.pint.database.exist.DBManager.SystemType
import si.pint.activiti.standalone.ldap.UserRegistryFactory;
import si.pint.database.exist.*
import eoskrba.webapp.common.LogEnabledController

class PatientProgressWidgetController extends LogEnabledController{

	// Konstantne poti
	public static final String AS_VKLJUCITVENI_KRITERIJI_TEMPLATE  = "openEHR-EHR-SECTION.vkljucitveni_kriteriji.v1"
	public static final String AS_VKLJUCITVENI_KRITERIJI_PEF_XPATH = "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.pljucne_funkcije_eo_as.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()"
	public static final String AS_VKLJUCITVENI_KRITERIJI_ACT_XPATH = "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1']/data/events/data/items[@archetype_node_id='at0066']/value/magnitude/text()"
	public static final String AS_PLJUCNE_TEMPLATE  = "openEHR-EHR-OBSERVATION.pulmonary_function.v1"
	public static final String AS_PLJUCNE_PEF_XPATH = "//items[@archetype_node_id='at0058']/value/magnitude/text()"
	public static final String AS_VPRASALNIK_TEMPLATE  = "openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1"
	public static final String AS_VPRASALNIK_ACT_XPATH = "//items[@archetype_node_id='openEHR-EHR-OBSERVATION.asthma_control_test_questionary.v1']/data/events/data/items[@archetype_node_id='at0066']/value/magnitude/text()"
	
	public static final String DI_VKLJUCITEV_TEMPLATE = "openEHR-EHR-SECTION.vkljucitev_eo.v1"
	public static final String DI_VKLJUCITEV_TT_XPATH = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()"
	public static final String DI_OPOMNIK_TEMPATE     = "openEHR-EHR-SECTION.opomnik_eo_di.v1"
	public static final String DI_OPOMNIK_KS_XPATH    = "//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_di.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.krvni_sladkor_eo_di.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()"
	public static final String DI_OPOMNIK_TT_XPATH    = "//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_di.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()"
	public static final String DI_OPOMNIK_KP_S_XPATH  = "//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_di.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.krvni_tlak_eo.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()"
	public static final String DI_OPOMNIK_KP_D_XPATH  = "//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_di.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.krvni_tlak_eo.v1']/data/events/data/items[@archetype_node_id='at0005']/value/magnitude/text()"
	
	public static final String HU_VKLJUCITEV_TEMPLATE = "openEHR-EHR-SECTION.vkljucitev_eo.v1"
	public static final String HU_VKLJUCITEV_TT_XPATH = "//content[@archetype_node_id='openEHR-EHR-SECTION.vkljucitev_eo.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()"
	public static final String HU_OPOMNIK_TEMPLATE    = "openEHR-EHR-SECTION.opomnik_eo_hu.v1"
	public static final String HU_OPOMNIK_TT_KORAK_XPATH    = "//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_hu.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()"
	public static final String HU_OPOMNIK_TT_PV_XPATH    = "//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_hu_pv.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()"
	public static final String HU_OPOMNIK_OP_XPATH    = "//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_hu.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.obseg_pasu_eo.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()"
	public static final String HU_OPOMNIK_ITM_XPATH    = "//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_hu.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_mass_index.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()"
	public static final String HU_OPOMNIK_PV_TEMPLATE    = "openEHR-EHR-SECTION.opomnik_eo_hu_pv.v1"
	public static final String HU_OPOMNIK_PV_TT_XPATH    = "//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_hu_pv.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_weight.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()"
	public static final String HU_OPOMNIK_PV_ITM_XPATH   = "//content[@archetype_node_id='openEHR-EHR-SECTION.opomnik_eo_hu_pv.v1']/items[@archetype_node_id='openEHR-EHR-OBSERVATION.body_mass_index.v1']/data/events/data/items[@archetype_node_id='at0004']/value/magnitude/text()"
	
	public static final long MILLISECONDS_IN_ONE_DAY  = 86400000
	
	// Services
	def existService
	
	// eAstma
    def showAs = {
		
		// Log
		doLogInfo(
			log, "Prikaz statusa za pacienta",session.user,
			session.role,"","",session.employeeType
		);
	
		// Če update ni zahtevan gremo takoj v view. Vse vrednosti so shranjene v seji
		if(session.updatePatientProgress != true) return [:]
		
		/* ************************************************************************
		 *                                  PEF                                   *
		 **************************************************************************/
		
		// Nastavimo spremenljivke za PEF
		def currPEF   // <-- zadnji izmerjen PEF
		def prevPEF   // <-- predzadnji izmerjen PEF
		def firstPEF  // <-- prvi vnešen PEF, izklicna vrednost
		def markPEF   // <-- ocena PEF-a: -1 slab, 0 nevtralno, 1 dober, null neznano
		def risenPEF  // <-- rast PEF-a: -1 padec, 0 enako, 1 porast, null neznano
		
		// Pridobi izklicno vrednost PEF
		// Če pef ni mogel biti prebran, bo firstPEF == null
		firstPEF  = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readLast(
			session.user,
			session.user,
			AS_VKLJUCITVENI_KRITERIJI_TEMPLATE,
			AS_VKLJUCITVENI_KRITERIJI_PEF_XPATH
		)
		
		// Pridobi zadnji dve izmerjeni vrednosti PEF
		List lastTwoPEF  = existService.getSelectedData(
			session.user,
			EMPLOYEE_TYPE_ENUM.ASTMA.toDomainName(),
			session.user,
			AS_PLJUCNE_TEMPLATE,
			[ [ query: AS_PLJUCNE_PEF_XPATH, label: "pef" ] ]
		).sort { it.xmlTimestamp }.reverse()
		lastTwoPEF = existService.getNonEmptyItemsFromListOfMaps(lastTwoPEF, "pef", 2)
		
		// Obdelaj podatke
		if (lastTwoPEF.size() == 0)     { prevPEF = null; currPEF = null; }
		else if(lastTwoPEF.size() == 1) { prevPEF = null; currPEF = lastTwoPEF[0].pef}
		else                            { prevPEF = lastTwoPEF[1].pef; currPEF = lastTwoPEF[0].pef}
		
		// Parseaj v števila
		if(firstPEF != null) firstPEF = Double.parseDouble(firstPEF)
		if(currPEF != null ) currPEF  = Double.parseDouble(currPEF)
		if(prevPEF != null ) prevPEF  = Double.parseDouble(prevPEF)
		
		// Določi ali PEF dober
		if(firstPEF != null && currPEF != null) {
			if(currPEF >= (0.8*firstPEF)) markPEF = 1
			else if(currPEF >= (0.6*firstPEF)) markPEF = 0
			else markPEF = -1
		} else markPEF = null
	
		// Določi, ali je PEF zrasel od predzadnje meritve
		if(prevPEF != null && currPEF != null) {
			if(currPEF > prevPEF) risenPEF = 1
			else if(currPEF == prevPEF) risenPEF = 0
			else risenPEF = -1
		} else risenPEF = null
		
		// Shrani v sejo
		session.currPEF = currPEF
		session.prevPEF = prevPEF
		session.firstPEF = firstPEF
		session.markPEF = markPEF
		session.risenPEF = risenPEF
		
		/* ************************************************************************
		 *                                  ACT                                   *
		 **************************************************************************/
		
		// Nastavimo spremenljivke za ACT
		def currACT   // <-- zadnji izmerjen ACT
		def prevACT   // <-- predzadnji izmerjen ACT
		def markACT   // <-- ocena ACT-a: -1 slab, 0 nevtralno, 1 dober, null neznano
		def risenACT  // <-- rast ACT-a: -1 padec, 0 enako, 1 porast, null neznano
		
		// Pridobi zadnji dve izmerjeni vrednosti ACT
		List lastTwoACT  = existService.getSelectedData(
			session.user,
			EMPLOYEE_TYPE_ENUM.ASTMA.toDomainName(),
			session.user,
			AS_VPRASALNIK_TEMPLATE,
			[ [ query: AS_VPRASALNIK_ACT_XPATH, label: "act" ] ]
		).sort { it.xmlTimestamp }.reverse()
		lastTwoACT = existService.getNonEmptyItemsFromListOfMaps(lastTwoACT, "act", 2)
		
		// Obdelaj podatke
		if (lastTwoACT.size() == 0)     { prevACT = null; currACT = null; }
		else if(lastTwoACT.size() == 1) { prevACT = null; currACT = lastTwoACT[0].act}
		else                            { prevACT = lastTwoACT[1].act; currACT = lastTwoACT[0].act}
		
		// Parseaj v števila
		if(currACT != null ) currACT  = Double.parseDouble(currACT)
		if(prevACT != null ) prevACT  = Double.parseDouble(prevACT)
		
		// Določi ali ACT dober
		if(currACT != null) {
			if(currACT >= 15) markACT = 1
			else if(currACT >= 10) markACT = 0
			else markACT = -1
		} else markACT = null
	
		// Določi, ali je ACT zrasel od predzadnje meritve
		if(prevACT != null && currACT != null) {
			if(currACT > prevACT) risenACT = 1
			else if(currACT == prevACT) risenACT = 0
			else risenACT = -1
		} else risenACT = null
		
		// Shrani v sejo
		session.currACT = currACT
		session.prevACT = prevACT
		session.markACT = markACT
		session.risenACT = risenACT
		
		// Ker vršimo update, naslednjič le ta ni potreben
		session.updatePatientProgress = false
		
	}
	
	// eDiabetes
	def showDi = {

		// Log
		doLogInfo(
			log, "Prikaz statusa za pacienta",session.user,
			session.role,"","",session.employeeType
		);
	
		// Če update ni zahtevan gremo takoj v view. Vse vrednosti so shranjene v seji
		if(session.updatePatientProgress != true) return [:]

		/* ************************************************************************
		*                               KRVNI SLADKOR                             *
		**************************************************************************/
		
		// Nastavimo spremenljivke za KS
		def currKS   // <-- zadnji izmerjen KS
		def prevKS   // <-- predzadnji izmerjen KS
		def markKS   // <-- ocena KS-a: -1 slab, 0 nevtralno, 1 dober, null neznano
		def risenKS  // <-- rast KS-a: -1 padec, 0 enako, 1 porast, null neznano
		
		// Pridobi zadnji dve izmerjeni vrednosti KS
		List lastTwoKS  = existService.getSelectedData(
			session.user,
			EMPLOYEE_TYPE_ENUM.DIABETES.toDomainName(),
			session.user,
			DI_OPOMNIK_TEMPATE,
			[ [ query: DI_OPOMNIK_KS_XPATH, label: "ks" ] ]
		).sort { it.xmlTimestamp }.reverse()
		lastTwoKS = existService.getNonEmptyItemsFromListOfMaps(lastTwoKS, "ks", 2)
		
		// Obdelaj podatke
		if (lastTwoKS.size() == 0)     { prevKS = null; currKS = null; }
		else if(lastTwoKS.size() == 1) { prevKS = null; currKS = lastTwoKS[0].ks}
		else                            { prevKS = lastTwoKS[1].ks; currKS = lastTwoKS[0].ks}
		
		// Parseaj v števila
		if(currKS != null ) currKS  = Double.parseDouble(currKS)
		if(prevKS != null ) prevKS  = Double.parseDouble(prevKS)
		
		// Določi ali KS dober
		if(currKS != null) {
			if(currKS < 6.1) markKS = 1
			else if(currKS <= 7.1) markKS = 0
			else markKS = -1
		} else markKS = null
	
		// Določi, ali je KS zrasel od predzadnje meritve
		if(prevKS != null && currKS != null) {
			if(currKS > prevKS) risenKS = 1
			else if(currKS == prevKS) risenKS = 0
			else risenKS = -1
		} else risenKS = null
		
		// Shrani v sejo
		session.currKS = currKS
		session.prevKS = prevKS
		session.markKS = markKS
		session.risenKS = risenKS
		
		/* ************************************************************************
		*                              TELSESNA TEŽA                              *
		**************************************************************************/
	   
	   // Nastavimo spremenljivke za TT
	   def currTT   // <-- zadnji izmerjen TT
	   def prevTT   // <-- predzadnji izmerjen TT
	   def firstTT  // <-- prvi vnešen TT, izklicna vrednost
	   def markTT   // <-- ocena TT-a: -1 slab, 0 nevtralno, 1 dober, null neznano
	   def risenTT  // <-- rast TT-a: -1 padec, 0 enako, 1 porast, null neznano
	   
	   // Pridobi izklicno vrednost TT
	   // Če TT ni mogel biti prebran, bo firstTT == null
	   firstTT  = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readLast(
		   session.user,
		   session.user,
		   DI_VKLJUCITEV_TEMPLATE,
		   DI_VKLJUCITEV_TT_XPATH
	   )
	   
	   // Pridobi zadnji dve izmerjeni vrednosti TT
	   List lastTwoTT  = existService.getSelectedData(
		   session.user,
		   EMPLOYEE_TYPE_ENUM.DIABETES.toDomainName(),
		   session.user,
		   DI_OPOMNIK_TEMPATE,
		   [ [ query: DI_OPOMNIK_TT_XPATH, label: "tt" ] ]
	   ).sort { it.xmlTimestamp }.reverse()
	   lastTwoTT = existService.getNonEmptyItemsFromListOfMaps(lastTwoTT, "tt", 2)
	   
	   // Obdelaj podatke
	   if (lastTwoTT.size() == 0)     { prevTT = null; currTT = null; }
	   else if(lastTwoTT.size() == 1) { prevTT = null; currTT = lastTwoTT[0].tt}
	   else                            { prevTT = lastTwoTT[1].tt; currTT = lastTwoTT[0].tt}
	   
	   // Parseaj v števila
	   if(firstTT != null) firstTT = Double.parseDouble(firstTT)
	   if(currTT != null ) currTT  = Double.parseDouble(currTT)
	   if(prevTT != null ) prevTT  = Double.parseDouble(prevTT)
	   
	   // Določi ali TT dober
	   if(firstTT != null && currTT != null) {
		   if(currTT > (0.95*firstTT) && currTT < (1*firstTT)) markTT = 1
		   else if(currTT > (1.03*firstTT)) markTT = -1
		   else markTT = 0
	   } else markTT = null
   
	   // Določi, ali je TT zrasel od predzadnje meritve
	   if(prevTT != null && currTT != null) {
		   if(currTT > prevTT) risenTT = 1
		   else if(currTT == prevTT) risenTT = 0
		   else risenTT = -1
	   } else risenTT = null
	   
	   // Shrani v sejo
	   session.currTT = currTT
	   session.prevTT = prevTT
	   session.firstTT = firstTT
	   session.markTT = markTT
	   session.risenTT = risenTT
	   
	   /* ************************************************************************
	   *                              KRVNI PRITISK                              *
	   **************************************************************************/

		// Nastavimo spremenljivke za KP
		def currKP  // <-- zadnji izmerjen KS
		def prevKP  // <-- predzadnji izmerjen KS
		def markKP   // <-- ocena KP-a: -1 slab, 0 nevtralno, 1 dober, null neznano
		def risenKP  // <-- rast KP-a: -1 padec, 0 enako, 1 porast, null neznano
		
		// Pridobi zadnji dve izmerjeni vrednosti KP
		List lastTwoKP  = existService.getSelectedData(
			session.user,
			EMPLOYEE_TYPE_ENUM.DIABETES.toDomainName(),
			session.user,
			DI_OPOMNIK_TEMPATE,
			[ [ query: DI_OPOMNIK_KP_D_XPATH, label: "kpd" ],
			  [ query: DI_OPOMNIK_KP_S_XPATH, label: "kps" ] ]
		).sort { it.xmlTimestamp }.reverse()
		lastTwoKP = existService.getNonEmptyItemsFromListOfMaps(lastTwoKP, "kpd", 2)
		lastTwoKP = existService.getNonEmptyItemsFromListOfMaps(lastTwoKP, "kps", 2)
		
		// Obdelaj podatke
		if (lastTwoKP.size() == 0)     { prevKP = null; currKP = null; }
		else if(lastTwoKP.size() == 1) { prevKP = null; currKP = [ lastTwoKP[0].kps, lastTwoKP[0].kpd ]}
		else                            { prevKP = [ lastTwoKP[1].kps, lastTwoKP[1].kpd ]; currKP = [ lastTwoKP[0].kps, lastTwoKP[0].kpd ]}
		
		// Parseaj v števila
		if(currKP != null ) currKP  = [ Double.parseDouble(currKP[0]), Double.parseDouble(currKP[1]) ]
		if(prevKP != null ) prevKP  = [ Double.parseDouble(prevKP[0]), Double.parseDouble(prevKP[1]) ]
		
		// Določi ali KP dober
		if(currKP != null) {
			if(currKP[0] < 129 && currKP[1] < 84) markKP = 1
			else if(currKP[0] >= 160 && currKP[1] >= 100) markKP = 0
			else markKP = -1
		} else markKP = null
	
		// Določi, ali je KS zrasel od predzadnje meritve
		if(prevKP != null && currKP != null) {
			if(currKP[0] > prevKP[0] && currKP[1] > prevKP[1]) risenKP = 1
			else if(currKP[0] < prevKP[0] && currKP[1] < prevKP[1]) risenKP = -1
			else risenKP = 0
		} else risenKP = null
		
		// Shrani v sejo
		session.currKP = currKP
		session.prevKP = prevKP
		session.markKP = markKP
		session.risenKP = risenKP
		
		// Ker vršimo update, naslednjič le ta ni potreben
		session.updatePatientProgress = false
	   
	}
	
	// eHujsanje
	def showHu = {
		
		// Log
		doLogInfo(
			log, "Prikaz statusa za pacienta",session.user,
			session.role,"","",session.employeeType
		);
	
		// Če update ni zahtevan gremo takoj v view. Vse vrednosti so shranjene v seji
		if(session.updatePatientProgress != true) return [:]
		
		/* ************************************************************************
		*                              TELSESNA TEŽA                              *
		**************************************************************************/
	   
	   // Nastavimo spremenljivke za TT
	   def currTT   // <-- zadnji izmerjen TT
	   def prevTT   // <-- predzadnji izmerjen TT
	   def firstTT  // <-- prvi vnešen TT, izklicna vrednost
	   def markTT   // <-- ocena TT-a: -1 slab, 0 nevtralno, 1 dober, null neznano
	   def risenTT  // <-- rast TT-a: -1 padec, 0 enako, 1 porast, null neznano
	   def firstTT_time // <-- epoch timestamp prve meritve
	   def currTT_time  // <-- epoch timestamp trenutne meritve
	   
	   // Pridobi izklicno vrednost TT
	   // Če TT ni mogel biti prebran, bo firstTT == null
	   firstTT  = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readLast(
		   session.user,
		   session.user,
		   HU_VKLJUCITEV_TEMPLATE,
		   HU_VKLJUCITEV_TT_XPATH
	   )
	   firstTT_time = DBManager.getInstance(SystemType.EOSKRBAWEBAPP).readFirstTimeStamp(
		   session.user,
		   session.user,
		   HU_VKLJUCITEV_TEMPLATE
	   )
	   
	   // Pridobi zadnji dve izmerjeni vrednosti TT - iz tedenskega vnosa koraka
	   List lastTwoTT_korak  = existService.getSelectedData(
		   session.user,
		   EMPLOYEE_TYPE_ENUM.HUJSANJE.toDomainName(),
		   session.user,
		   HU_OPOMNIK_TEMPLATE,
		   [ [ query: HU_OPOMNIK_TT_KORAK_XPATH, label: "tt" ] ]
	   ).sort { it.xmlTimestamp }.reverse()
	   lastTwoTT_korak = existService.getNonEmptyItemsFromListOfMaps(lastTwoTT_korak, "tt", 2)
	   
	   
	   // Pridobi zadnji dve izmerjeni vrednosti TT - iz prostega vnosa
	   List lastTwoTT_pv  = existService.getSelectedData(
		   session.user,
		   EMPLOYEE_TYPE_ENUM.HUJSANJE.toDomainName(),
		   session.user,
		   HU_OPOMNIK_PV_TEMPLATE,
		   [ [ query: HU_OPOMNIK_TT_PV_XPATH, label: "tt" ] ]
	   ).sort { it.xmlTimestamp }.reverse()
	   lastTwoTT_pv = existService.getNonEmptyItemsFromListOfMaps(lastTwoTT_pv, "tt", 2)
	   
	   //merge lists
	   List lastTwoTT = (lastTwoTT_korak << lastTwoTT_pv).flatten().sort { it.xmlTimestamp }.reverse()
	   
	   // Obdelaj podatke
	   if (lastTwoTT.size() == 0)     { prevTT = null; currTT = null; }
	   else if(lastTwoTT.size() == 1) { prevTT = null; currTT = lastTwoTT[0].tt; currTT_time = lastTwoTT[0].xmlTimestamp}
	   else                            { prevTT = lastTwoTT[1].tt; currTT = lastTwoTT[0].tt; currTT_time = lastTwoTT[0].xmlTimestamp}
	   
	   // Parseaj v števila
	   if(firstTT != null) firstTT = Double.parseDouble(firstTT)
	   if(currTT != null ) currTT  = Double.parseDouble(currTT)
	   if(prevTT != null ) prevTT  = Double.parseDouble(prevTT)
	   if(firstTT_time != null) firstTT_time = Long.parseLong(firstTT_time)
	   if(currTT_time != null) currTT_time = Long.parseLong(currTT_time)
	   
	   // Določi ali TT dober
	   if(firstTT != null && currTT != null) {
		   long timeframe = currTT_time - firstTT_time
		   long days = timeframe / MILLISECONDS_IN_ONE_DAY
		   double weightLossPerDay = (firstTT-currTT) / (days as double)
		   if(weightLossPerDay >= 0.05 && weightLossPerDay <= 0.1) markTT = 1
		   else if(weightLossPerDay < -0.1) markTT = -1
		   else markTT = 0
	   } else markTT = null
   
	   // Določi, ali je TT zrasel od predzadnje meritve
	   if(prevTT != null && currTT != null) {
		   if(currTT > prevTT) risenTT = 1
		   else if(currTT == prevTT) risenTT = 0
		   else risenTT = -1
	   } else risenTT = null
	   
	   // Shrani v sejo
	   session.currTT = currTT
	   session.prevTT = prevTT
	   session.firstTT = firstTT
	   session.markTT = markTT
	   session.risenTT = risenTT
	   
	   /* ************************************************************************
	   *                           INDEKS TELESNE MASE                           *
	   **************************************************************************/
	   
		// Nastavimo spremenljivke za ITM
		def currITM   // <-- zadnji izmerjen ITM
		def prevITM   // <-- predzadnji izmerjen ITM
		def markITM   // <-- ocena ITM-a: -1 slab, 0 nevtralno, 1 dober, null neznano
		def risenITM  // <-- rast ITM-a: -1 padec, 0 enako, 1 porast, null neznano
		
		// Pridobi zadnji dve izmerjeni vrednosti ITM
		List lastTwoITM  = existService.getSelectedData(
			session.user,
			EMPLOYEE_TYPE_ENUM.HUJSANJE.toDomainName(),
			session.user,
			HU_OPOMNIK_TEMPLATE,
			[ [ query: HU_OPOMNIK_ITM_XPATH, label: "itm" ] ]
		).sort { it.xmlTimestamp }.reverse()
		lastTwoITM = existService.getNonEmptyItemsFromListOfMaps(lastTwoITM, "itm", 2)
		
		// Obdelaj podatke
		if (lastTwoITM.size() == 0)     { prevITM = null; currITM = null; }
		else if(lastTwoITM.size() == 1) { prevITM = null; currITM = lastTwoITM[0].itm}
		else                            { prevITM = lastTwoITM[1].itm; currITM = lastTwoITM[0].itm}
		
		// Parseaj v števila
		if(currITM != null ) currITM  = Double.parseDouble(currITM)
		if(prevITM != null ) prevITM  = Double.parseDouble(prevITM)
		
		// Določi ali ITM dober
		if(currITM != null) {
			if(currITM >= 18.5 && currITM <  25) markITM = 1;
			else if(currITM >= 30) markITM = -1;
			else markITM = 0;
		} else markITM = null
	
		// Določi, ali je ITM zrasel od predzadnje meritve
		if(prevITM != null && currITM != null) {
			if(currITM > prevITM) risenITM = 1
			else if(currITM == prevITM) risenITM = 0
			else risenITM = -1
		} else risenITM = null
		
		// Shrani v sejo
		session.currITM = currITM
		session.prevITM = prevITM
		session.markITM = markITM
		session.risenITM = risenITM
		
		/* ************************************************************************
		*                                OBSEG PASU                               *
		**************************************************************************/
		
		 // Nastavimo spremenljivke za OP
		 def currOP   // <-- zadnji izmerjen OP
		 def prevOP   // <-- predzadnji izmerjen OP
		 def markOP   // <-- ocena OP-a: -1 slab, 0 nevtralno, 1 dober, null neznano
		 def risenOP  // <-- rast OP-a: -1 padec, 0 enako, 1 porast, null neznano
		 
		 // Pridobi zadnji dve izmerjeni vrednosti OP
		 List lastTwoOP  = existService.getSelectedData(
			 session.user,
			 EMPLOYEE_TYPE_ENUM.HUJSANJE.toDomainName(),
			 session.user,
			 HU_OPOMNIK_TEMPLATE,
			 [ [ query: HU_OPOMNIK_OP_XPATH, label: "op" ] ]
		 ).sort { it.xmlTimestamp }.reverse()
		 lastTwoOP = existService.getNonEmptyItemsFromListOfMaps(lastTwoOP, "op", 2)
		 
		 // Obdelaj podatke
		 if (lastTwoOP.size() == 0)     { prevOP = null; currOP = null; }
		 else if(lastTwoOP.size() == 1) { prevOP = null; currOP = lastTwoOP[0].op}
		 else                            { prevOP = lastTwoOP[1].op; currOP = lastTwoOP[0].op}
		 
		 // Parseaj v števila
		 if(currOP != null ) currOP  = Double.parseDouble(currOP)
		 if(prevOP != null ) prevOP  = Double.parseDouble(prevOP)
		 
		 // Določi ali OP dober
		 if(currOP != null) {
			 if(session.ldap.sexAtt == "M" || session.ldap.sexAtt == "MALE") {
				 if(currOP < 94) markOP = 1
				 else if(currOP < 102) markOP = 0
				 else markOP = -1
			 } else {
				 if(currOP < 80) markOP = 1
				 else if(currOP < 88) markOP = 0
				 else markOP = -1
			 }
		 } else markOP = null
	 
		 // Določi, ali je OP zrasel od predzadnje meritve
		 if(prevOP != null && currOP != null) {
			 if(currOP > prevOP) risenOP = 1
			 else if(currOP == prevOP) risenOP = 0
			 else risenOP = -1
		 } else risenOP = null
		 
		 // Shrani v sejo
		 session.currOP = currOP
		 session.prevOP = prevOP
		 session.markOP = markOP
		 session.risenOP = risenOP

	   // Ker vršimo update, naslednjič le ta ni potreben
	   session.updatePatientProgress = false
		
	}
	
	// eShizofrenija
	def showSh = { }
	
	// eSport
	def showSp = { }
	
}
