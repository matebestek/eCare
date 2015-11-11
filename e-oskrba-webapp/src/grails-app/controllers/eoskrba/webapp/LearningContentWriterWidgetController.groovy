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

class LearningContentWriterWidgetController extends LogEnabledController{
//	Logger log4j = Logger.getLogger(LearningContentWriterWidgetController.class);
    def show = {
		doLogInfo(log, "",session.user,session.role,
			"","",session.employeeType);
		def c = Learnitem.createCriteria()
		def items = c {
			eq("patientGroup",session.employeeType)
			order("submittedOn","desc")
		}
		[items:items]
		
	}
	
	def form = { }
	
	def edit = {
		doLogInfo(log, "",session.user,session.role,
			"","",session.employeeType);
		def learnitem = Learnitem.get(params.id)
		[item:learnitem]
		
	}
	
	def addLearningContent = {
		doLogInfo(log, "",session.user,session.role,
			"","",session.employeeType);
		Learnitem newLearnitem = new Learnitem(
			title: params.title,
			content: params.content,
			submittedOn: new Date(),
			authorId: session.user,
			authorFullName: session.firstName + " " + session.lastName,
			patientGroup: session.employeeType
		);
		if( !newLearnitem.validate() ) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Vsebina ni bila objavljena. Nekatera obvezna polja niso bila izpolnjena pravilno."]])
			return false
		}
		newLearnitem.save()
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Vsebina je bila objavljena!"]])
		
		}
	
	def updateLearningContent = {
		doLogInfo(log, "",session.user,session.role,
			"","",session.employeeType);
		Learnitem itemToUpdate = Learnitem.get(params.id)
		itemToUpdate.title = params.title
		itemToUpdate.content = params.content
		if( !itemToUpdate.validate() ) {
			redirect(controller:"publicContent",action:"actionReport",params:[type:"error",message:["Napaka!","Spremembe niso bile shranjene. Nekatera obvezna polja niso bila izpolnjena pravilno."]])
			return false
		}
		itemToUpdate.save()
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Vsebina je bila urejena!"]])
		
	}
	
	def removeLearningContent = {
		doLogInfo(log, "",session.user,session.role,
			"","",session.employeeType);
		Learnitem itemToRemove = Learnitem.get(params.id)
		itemToRemove.delete()
		redirect(controller:"publicContent",action:"actionReport",params:[type:"success",message:["Hvala!","Vsebina je bila izbrisana!"]])
		
	}
	
}
