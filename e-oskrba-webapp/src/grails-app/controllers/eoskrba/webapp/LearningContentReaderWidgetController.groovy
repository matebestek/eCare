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

import org.apache.log4j.Logger

import eoskrba.webapp.common.LogEnabledController
import java.util.ArrayList;
import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM;
import org.codehaus.groovy.grails.commons.ApplicationHolder


class LearningContentReaderWidgetController extends LogEnabledController{
	
	private static final String HUJSANJE_CONTENT_BASE_NAME = "hujsanje-vsebina-korak-";

//	Logger log4j = Logger.getLogger(LearningContentReaderWidgetController.class);
    def show = {
		doLogInfo(log, "Prikaz izobrazevalne vsebine",session.user,session.role,
			"","",session.employeeType);
		def page = 1
		def itemsPerPage = 5
		if(params.page != null) page =  params.page as int
		if(params.itemsPerPage != null) itemsPerPage = params.itemsPerPage as int
		// Get learning content
		def c = Learnitem.createCriteria()
		def items

		items = c {
			eq("patientGroup",session.employeeType)
			order("submittedOn","desc")
			maxResults(itemsPerPage)
			firstResult((page-1)*itemsPerPage)
		}		
		
		[items:items,page:page, itemsPerPage:itemsPerPage]
		
	}
	
	def readMore = {
		doLogInfo(log, "Dodatna izobrazevalna vsebina",session.user,session.role,
			"","",session.employeeType);
		def learnitem = Learnitem.get(params.id)
		[item:learnitem]
		
	}
	
	def showSteps = {
		
		doLogInfo(log, "Prikaz izobrazevalne vsebine - koraki",session.user,session.role,
			"","",session.employeeType);
		def page = 1
		def itemsPerPage = 5
		def maxStep = session.ldap.roomNumber != null ? session.ldap.roomNumber.toInteger() : 0
		
		if(params.pageStep != null) page =  params.pageStep as int
		if(params.itemsPerPageStep != null) itemsPerPage = params.itemsPerPageStep as int
				 
		// Get learning content
		def items = []
		
		if (maxStep > 0 && maxStep - (page-1)*itemsPerPage >= 1) {
			for (i in ((maxStep - (page-1)*itemsPerPage)..(maxStep - (page-1)*itemsPerPage - itemsPerPage + 1 > 0 ? maxStep - (page-1)*itemsPerPage - itemsPerPage + 1 : 1))) {
				Learnitem item = new Learnitem(title:"Korak " + i)
				items.add(item)
			}
		}
		
		
		[itemsStep:items,pageStep:page, itemsPerPageStep:itemsPerPage]
	}
	
	def readMoreSteps = {
		doLogInfo(log, "Dodatna izobrazevalna vsebina - koraki",session.user,session.role,
			"","",session.employeeType);
		def maxStep = session.ldap.roomNumber != null ? session.ldap.roomNumber.toInteger() : 0
		def stepNo = params.stepNo as int;
		if(stepNo<= maxStep){
			def file = "resources/hujsanje-vsebina-korak-" + stepNo + ".vm"
			
			//String test = new File(file).text
			def test = ApplicationHolder.application.parentContext.getResource("classpath:$file").inputStream.text
		
			Learnitem item = new Learnitem(title:"Korak " + stepNo, content:test)
			[itemStep:item]
		} else {
			Learnitem item = new Learnitem(title:"Opozorilo", content:"Do vsebine koraka " + stepNo + " še nimate dostopa.")
			[itemStep:item]
		}
	}
	
	
}
