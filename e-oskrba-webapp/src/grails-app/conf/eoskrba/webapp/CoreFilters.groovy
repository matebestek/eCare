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

class CoreFilters {
	
	def activitiService

    def filters = {
		
		initialization(controller:'*', action:'*') {
			before = {
				if(ActivitiService.initializing == true) {
					redirect(controller:'publicContent',action:'initializing')
					return false
				}
				if(ActivitiService.initialized == false) {
					ActivitiService.init()
				}
			}
		}
		
        loginRequired(controller:'*', action:'*') {
			before = {
				if(params.controller == "sessionInfoWidget" && params.action == "test"){
					return true; // ni treba biti prijavljen za dostop
				}
				
				// Omogoči vsem pogled javno objavljenih detailov uporabnikove športne aktivnosti
				if(params.controller == "facebookResources" && params.action == "activityDetails") {
					return true;
				}
				
				if(params.controller != "login") {
					if(session.logged != true) {
						flash.error = ["Napaka!","V sistem se morate najprej prijaviti!"]
						redirect(controller:'login',action:'form')
						return false
					}
				}
				
				if(session.role == "patient") {
					if(params.controller == "caremanagerTabs") {
						redirect(controller:'publicContent',action:'contentNotPermitted')
						return false
					}
					if(params.controller == "doctorTabs") {
						redirect(controller:'publicContent',action:'contentNotPermitted')
						return false
					}
				}
				
				if(session.role == "caremanager") {
					if(params.controller == "patientTabs") {
						redirect(controller:'publicContent',action:'contentNotPermitted')
						return false
					}
					if(params.controller == "doctorTabs") {
						redirect(controller:'publicContent',action:'contentNotPermitted')
						return false
					}
				}
				
				if(session.role == "doctor") {
					if(params.controller == "caremanagerTabs") {
						redirect(controller:'publicContent',action:'contentNotPermitted')
						return false
					}
					if(params.controller == "patientTabs") {
						redirect(controller:'publicContent',action:'contentNotPermitted')
						return false
					}
				}
				
				// Refresh my tasks
				if(params.controller == "patientTabs" || params.controller == "doctorTabs" || params.controller == "caremanagerTabs") {
					session.myTasksNumber = activitiService.getUserTasksNumber(session)
				}
				
			}
		}
    }
    
}
