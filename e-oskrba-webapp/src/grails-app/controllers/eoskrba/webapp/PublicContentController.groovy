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

import eoskrba.webapp.common.LogEnabledController

class PublicContentController extends LogEnabledController{

//	Logger log4j = Logger.getLogger(PublicContentController.class);
    def widgetNotPermitted = { }
	
	def actionReport = {
		doLogInfo(log, "",session.user,session.role,
			"","",session.employeeType);
		
		//dekodiranje podatkov: kodiram z ISO-8859, nato dekodiram z UTF-8 (issue #521) 
		def decodedMsg = params.message.collect(conUtf2Iso)
		
		return [type: params.type, message: decodedMsg]
	}
	
	def contentNotPermitted = {
		doLogInfo(log, "",session.user,session.role,
			"","",session.employeeType);
		response.status = 403
		render("<h1>403 Nedovoljeno</h1><p>Za prikaz zahtevane vsebine si ne lastite ustreznih pravic.</p><p>Prosimo, preverite ali ste še zmeraj prijavljeni v sistem in ali ste prijavljeni kot pravi uporabnik.</p>")
		
		return false
	}
	
	def avatar = {
		def ud = UserData.findByUserId(params.id)		
		if (!ud || !ud.avatar || !ud.avatarType) {
			doLogInfo(log, "",session.user,session.role,
				"","",session.employeeType);
			redirect(url:'/img/avatar-sample.jpg')
		} else {
			doLogInfo(log, "",session.user,session.role,
			"","",session.employeeType);
			response.setContentType(ud.avatarType)
			response.setContentLength(ud.avatar.size())
			OutputStream out = response.getOutputStream();
			out.write(ud.avatar);
			out.close();
		}
	}
	
	def conUtf2Iso = {	
		return (new String(((String) it).getBytes("ISO-8859-1"), "UTF-8"));	
	 }
	
}
