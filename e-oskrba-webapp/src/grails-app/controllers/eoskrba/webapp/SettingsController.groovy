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

import eoskrba.webapp.common.LogEnabledController;

class SettingsController extends LogEnabledController{

	// Service-i
	def imageUploadService
	
    def show = {
		doLogInfo(log, "Prikaz nastavitev za uporabnika", session.user,session.role,"","",session.employeeType);
		String action = null;
		if(session.role == 'patient') {
			forward(controller:'settings',action:'showPatient')
			action = "showPatient"
		} else {
			forward(controller:'settings',action:'showCaremanagerAndDoctor')
			action = "showCaremanagerAndDoctor"
		}
		
		return false;
			
	}
	
	def showPatient = {
		
	}
	
	def submitPatient = {
		
		// Log
		doLogInfo(
			log,
			"Submit pacienta:"+session.user,
			session.user,
			session.role,
			"",
			"",
			session.employeeType
		);
		
		// Pridobi pacientove podatke
		def ud = UserData.findByUserId(session.user)
		
		// Pridobi naloženo sliko
		def f = request.getFile('userPicture')
		
		if(f.getSize() > 0) {
			
			def okcontents = ['image/png', 'image/jpeg', 'image/gif']
			if (! okcontents.contains(f.getContentType())) {
				flash.error = ["Napaka!","Izbrana slika je neveljavnega formata!"]
				redirect(controller:'redirects',action:'toMyTasks')
				return false
			}
			
			byte[] imageBytes = imageUploadService.prepareAvatar(f)
			
			ud.avatar = imageBytes
			ud.avatarType = f.getContentType()
			
			if (!ud.save()) {
				flash.error = ["Napaka!","Pri prenosu slike je prišlo do težave!"]
				redirect(controller:'redirects',action:'toMyTasks')
				return false
			}
			
		}
		
		if(params.eMailAtt != "") {
			session.ldap.eMailAtt = params.eMailAtt
			session.ldap.update()
			session.email = params.eMailAtt
		}
		
		if(params.mobilePhoneAtt != "") {
			session.ldap.mobilePhoneAtt = params.mobilePhoneAtt
			session.ldap.update()
		}
		
		if(params.userPassword != "") {
			if(params.userPassword == params.userPasswordCheck) {
				if(params.userPasswordOld == session.ldap.userPassword) {
					session.ldap.userPassword = params.userPassword
					session.ldap.update()
				} else {
					flash.error = ["Napaka!","Staro gelso ni pravilno!"]
					redirect(controller:'redirects',action:'toMyTasks')
					return false
				}
			} else {
				flash.error = ["Napaka!","Novo geslo in njegova ponovitev se ne ujemata!"]
				redirect(controller:'redirects',action:'toMyTasks')
				return false
			}
		}
		
		flash.success = ["Hvala!","Spremembe so bile shranjene!"]
		redirect(controller:'redirects',action:'toMyTasks')
		
	}
	
	def showCaremanagerAndDoctor = {
		doLogInfo(log, "Prikaz CM in zdravnika:"+session.user,session.user,session.role,"","",session.employeeType);
		def ud = UserData.findByUserId(session.user)
		[ud:ud]
		
	}
	
	def submitCaremanagerAndDoctor = {
		doLogInfo(log, "Submit CM in zdravnika:"+session.user,session.user,session.role,"","",session.employeeType);
		def ud = UserData.findByUserId(session.user)
		
		def f = request.getFile('userPicture')
		
		if(f.getSize() > 0) {
			
			def okcontents = ['image/png', 'image/jpeg', 'image/gif']
			if (! okcontents.contains(f.getContentType())) {
				flash.error = ["Napaka!","Izbrana slika je neveljavnega formata!"]
				redirect(controller:'redirects',action:'toMyTasks')
				return false
			}
			
			byte[] imageBytes = imageUploadService.prepareAvatar(f)
			
			ud.avatar = imageBytes
			ud.avatarType = f.getContentType()
			
			if (!ud.save()) {
				flash.error = ["Napaka!","Pri prenosu slike je prišlo do težave!"]
				redirect(controller:'redirects',action:'toMyTasks')
				return false
			}
		}
		
		if(params.eMailAtt != "") {
			session.ldap.eMailAtt = params.eMailAtt
			session.ldap.update()
			session.email = params.eMailAtt
		}
		
		if(params.mobilePhoneAtt != "") {
			session.ldap.mobilePhoneAtt = params.mobilePhoneAtt
			session.ldap.update()
		}
		
		if(params.userPassword != "") {
			if(params.userPassword == params.userPasswordCheck) {
				if(params.userPasswordOld == session.ldap.userPassword) {
					session.ldap.userPassword = params.userPassword
					session.ldap.update()
				} else {
					flash.error = ["Napaka!","Staro gelso ni pravilno!"]
					redirect(controller:'redirects',action:'toMyTasks')
					return false
				}
			} else {
				flash.error = ["Napaka!","Novo geslo in njegova ponovitev se ne ujemata!"]
				redirect(controller:'redirects',action:'toMyTasks')
				return false
			}
		}
		
		ud.location = params.cmdLocation
		ud.workingHours = params.cmdWorkingHours
		ud.additional = params.cmdAdditional
		ud.save()
		
		flash.success = ["Hvala!","Spremembe so bile shranjene!"]
		redirect(controller:'redirects',action:'toMyTasks')
		
	}
	
}
