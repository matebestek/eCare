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

import java.text.SimpleDateFormat

import org.apache.log4j.Logger;

import eoskrba.webapp.common.LogEnabledController

class AdviceWriterWidgetController extends LogEnabledController{
//	Logger log4j = Logger.getLogger(AdviceWriterWidgetController.class);
    def show = {
		doLogInfo(log, "Nasveti show",session.user,session.role,
			"","",session.employeeType);
		def c = Adviceitem.createCriteria()
		def items = c {
			eq("patientGroup",session.employeeType)
			order("priority","desc")
		}
		[items:items]
		
	}
	
	def form = { }
	
	def edit = {
		doLogInfo(log, "Nasveti edit",session.user,session.role,
			"","",session.employeeType);
		def adviceitem = Adviceitem.get(params.id)
		[item:adviceitem]		
	}
	
	def addAdviceitem = {
		doLogInfo(log, "Dodajam nasvet",session.user,session.role,
			"","",session.employeeType);
		def dateParser = new SimpleDateFormat("dd-MM-yyyy")
		Adviceitem newAdviceitem = new Adviceitem(
			title: params.title,
			content: params.content,
			dateStart: dateParser.parse(params.dateStart),
			dateEnd: dateParser.parse(params.dateEnd),
			priority: params.priority,
			authorId: session.user,
			authorFullName: session.firstName + " " + session.lastName,
			patientGroup: session.employeeType
		);
		if( !newAdviceitem.validate() ) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Priporočilo ni bilo dodano. Nekatera obvezna polja niso bila izpolnjena pravilno."]])
			return false
		}
		newAdviceitem.save()
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Priporočilo je bilo objavljeno!"]])
		
	}
	
	def updateAdviceitem = {
		doLogInfo(log, "Update nasveta",session.user,session.role,
			"","",session.employeeType);
		def dateParser = new SimpleDateFormat("dd-MM-yyyy")
		Adviceitem itemToUpdate = Adviceitem.get(params.id)
		itemToUpdate.title = params.title
		itemToUpdate.content = params.content
		itemToUpdate.dateStart = dateParser.parse(params.dateStart)
		itemToUpdate.dateEnd = dateParser.parse(params.dateEnd)
		itemToUpdate.priority = (params.priority as int)
		if( !itemToUpdate.validate() ) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Spremembe niso bile shranjene. Nekatera obvezna polja niso bila izpolnjena pravilno."]])
			return false
		}
		itemToUpdate.save()
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Priporočilo je bilo urejeno!"]])
		
	}
	
	def removeAdviceitem = {
		doLogInfo(log, "Brisanje nasveta",session.user,session.role,
			"","",session.employeeType);
		Adviceitem itemToRemove = Adviceitem.get(params.id)
		itemToRemove.delete()
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Priporočilo je bilo izbrisano!"]])
		
	}
	
}
