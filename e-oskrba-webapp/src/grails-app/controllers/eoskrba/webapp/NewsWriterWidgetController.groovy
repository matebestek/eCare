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

class NewsWriterWidgetController extends LogEnabledController{
//	Logger log4j = Logger.getLogger(NewsWriterWidgetController.class);
    def show = {
		doLogInfo(log, "Prikaz novic", session.user, session.role, "","",session.employeeType);
		def c = Newsitem.createCriteria()
		def items = c {
			eq("patientGroup",session.employeeType)
			order("submittedOn","desc")
		}
		[items:items]
		
	}
	
	def form = { }
	
	def edit = {
		doLogInfo(log, "Edit novice:"+params.id,session.user,session.role,"","",session.employeeType);
		def newsitem = Newsitem.get(params.id)
		[item:newsitem]
		
	}
	
	def addNewsitem = {
		doLogInfo(log, "Nova novica",session.user,session.role,"","",session.employeeType);
		Newsitem newNewsitem = new Newsitem(
			title: params.title,
			content: params.content,
			submittedOn: new Date(),
			authorId: session.user,
			authorFullName: session.firstName + " " + session.lastName,
			patientGroup: session.employeeType
		);
		
		if( !newNewsitem.validate() ) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Novica ni bila objavljena. Nekatera obvezna polja niso bila izpolnjena pravilno."]])
			return false
		}
		newNewsitem.save()
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Novica je bila objavljena!"]])
	}
	
	def updateNewsitem = {
		doLogInfo(log, "Update novice",session.user,session.role,"","",session.employeeType);
		Newsitem itemToUpdate = Newsitem.get(params.id)
		itemToUpdate.title = params.title
		itemToUpdate.content = params.content
		if( !itemToUpdate.validate() ) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Spremembe niso bile shranjene. Nekatera obvezna polja niso bila izpolnjena pravilno."]])
			return false
		}
		itemToUpdate.save()
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Novica je bila urejena!"]])
	}
	
	def removeNewsitem = {
		Newsitem itemToRemove = Newsitem.get(params.id)
		doLogInfo(log, "Brisanje novice",session.user,session.role,"","",session.employeeType);
		itemToRemove.delete()
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Novica je bila izbrisana!"]])
	}
}
