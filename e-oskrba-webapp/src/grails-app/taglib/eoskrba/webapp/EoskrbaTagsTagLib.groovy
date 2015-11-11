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

class EoskrbaTagsTagLib {
	
	static namespace = "eo"
	
	def opkpFrame = {attrs, body ->
		def url = attrs.url;//TODO: implementacija
		//1. Pripravi JSON strukturo s podatki
		//2. Podpiši JSON s podanim simetričnim ključem
		//3. Izvedi klic na OPKP in pošlji data element
		//4. Počakaj par sekund, da OPKP pošlje rezultat preko POST-a
		//5. Pripravi body v katerem bodo rezultati OPKP	
		redirect(url: url)	
	}
	
	def staticWidget = { attrs, body ->
		
		def widgetId = attrs.id
		
		out << "<div id=\"" + widgetId + "\" class=\"layout-widget\">"
		out << "<div class=\"layout-widget-header\" onclick=\"layout_widget_onClick('#" + widgetId + "');\">"
		out << "<h2 class=\"layout-widget-passive\">" + (attrs.title?:"Unnamed widget") + "</h2>"
		out << "</div>"
		out << "<div class=\"layout-widget-content\" id=\"" + widgetId + "_loader\">"
		out << body();
		out << "</div>"
		out << "</div>"
	}
	
	def widget = { attrs, body ->
		
		def widgetId = attrs.load + "_" + attrs.action
		def paramsToPass = "{ 'generatedBy': 'widget'"
		if(attrs.id) {
			paramsToPass += ", 'id': '" + attrs.id + "'"
		}
		attrs.params.each {
			paramsToPass += ", '" + it.key + "': '" + it.value + "'"
		}
		
		paramsToPass += "}"
		
		out << "<div id=\"" + widgetId + "\" class=\"layout-widget\">"
		out << "<div class=\"layout-widget-header\" onclick=\"layout_widget_onClick('#" + widgetId + "');\">"
		out << "<h2 class=\"layout-widget-passive\">" + (attrs.title?:"Unnamed widget") + "</h2>"
		out << "</div>"
		out << "<div class=\"layout-widget-content\" id=\"" + widgetId + "_loader\">"
		out << "<p>Nalagam...</p>"
		out << "</div>"
		out << "<div class=\"layout-widget-footer\">"
		out << body();
		out << "</div>"
		out << "<script type=\"text/javascript\">" + "\n"
		out << "\$('#" + widgetId + "_loader" + "').load('" + g.createLink(controller:attrs.load,action:attrs.action) + "'," + paramsToPass + ");" + "\n"
		out << "function " + widgetId + "_reload(params) {" + "\n"
		out << "   \$('#" + widgetId + "_loader" + "').load('" + g.createLink(controller:attrs.load,action:attrs.action) + "',params);" + "\n"
		out << "}" + "\n"
		out << "function " + widgetId + "_refresh() {" + "\n"
		out << "   \$('#" + widgetId + "_loader" + "').load('" + g.createLink(controller:attrs.load,action:attrs.action) + "'," + paramsToPass + ");" + "\n"
		out << "}" + "\n"
		out << "</script>"
		out << "</div>"
	}
	
	def widgetFrame = { attrs, body ->
		
		out << "<div id=\"layout-widgetframe\">"
		out << body()
		out << "</div>"
		
	}
	
	def panelFrame = { attrs, body ->
		
		out << "<div id=\"layout-panelframe\">"
		out << body()
		out << "</div>"
	}
	
	def panelHead = { attrs, body ->
		
		out << "<div id=\"layout-panel-header\">"
		out << body()
		out << "</div>"
		
	}
	
	def panelHeadInfo = { attrs, body ->
		
		out << "<p id=\"layout-panel-header-info\">"
		out << body()
		out << "</p>"
		
	}
	
	def panelContent = { attrs, body ->
		
		out << "<div id=\"layout-panel-content\">"
		out << "<div id=\"layout-panel-content-overlay-top\"></div>"
		out << body()
		out << "<br /><br />"
		out << "</div>"
		
	}
	
	def personLink = { attrs, body ->
		
		out << "<a href=\"#\" onclick=\"showPerson('" + attrs.id + "');return false;\">"
		out << body()
		out << "</a>"
		
	}
	
	def bigButton = { attrs, body ->
		
		out << "<button onclick=\"" + attrs.onclick +  "\" class=\"layout-bigbutton\">"
		out << body()
		out << "</button>"
		
	}
	
	def hr = {
		out << "<hr />"
	}

	def options = { attrs, body ->
		out << "<div id=\"layout-panel-options\">&nbsp;"
		out << body()
		out << "</div>"
	}
		
	def adminOptions = { attrs, body ->
		if(session.role != 'patient') {
			out << "<div id=\"layout-panel-options\">&nbsp;"
			out << body()
			out << "</div>"
		}
	}
	
	def datePicker = { attrs ->
		out << "<span class=\"datePicker\">"
		out << g.datePicker(attrs)
		out << "</span>"
	}
	
	def ajaxError = { attrs, body ->
		
		out << "<div id=\"layout-ajaxResponseHolder-msg-error\">"
		out << "<p><span>" + attrs.title + "</span><br /> " + body() + "</p>"
		out << "</div>"
	}
	
	def ajaxWarning = { attrs, body ->
	
		out << "<div id=\"layout-ajaxResponseHolder-msg-warning\">"
		out << "<p><span>" + attrs.title + "</span><br /> " + body() + "</p>"
		out << "</div>"
	}
	
	def ajaxSuccess = { attrs, body ->
	
		out << "<div id=\"layout-ajaxResponseHolder-msg-success\">"
		out << "<p><span>" + attrs.title + "</span><br /> " + body() + "</p>"
		out << "</div>"

	}
	
	def menu = { attrs, body ->
		
		def menuItems = []

		if(session.role == "patient") {
			def mainItem
			if(session.employeeType == "A") mainItem = ["eAstma in jaz","patientTabs","asthmaAndMe","inhaler.png"]
			else if(session.employeeType == "D") mainItem = ["eDiabetes in jaz","patientTabs","asthmaAndMe","diabetes.png"]
			else if(session.employeeType == "S") mainItem = ["eShizofrenija in jaz","patientTabs","asthmaAndMe","brain.png"]
			else if(session.employeeType == "H") mainItem = ["eHujšanje in jaz","patientTabs","asthmaAndMe","apple.png"]
			else if(session.employeeType == "P") mainItem = ["eŠport in jaz","patientTabs","asthmaAndMe","sport.png"]
			else mainItem = ["eOskrba","patientTabs","asthmaAndMe","Edit2.png"]
			menuItems = [
				mainItem,
				["Novice","patientTabs","news","News.png"],
				["Izobraževanje","patientTabs","learn","Archive.png"],
				["Vprašanja","patientTabs","tickets","Edit.png"]
			]
			if(session.employeeType == "P") {
				menuItems.add(1, ["Moj dnevnik","patientTabs","myPlanner","Calendar.png"])
			}
		} else if(session.role == "caremanager") {
			menuItems = [
				["Naloge","caremanagerTabs","tasks","Task.png"],
				["Stanje","caremanagerTabs","patients","PatientFile.png"],
				["Poročila","caremanagerTabs","reports","DefinedParameters.png"],
				["Administracija","caremanagerTabs","admin","PatientData.png"],
				["Vprašanja","caremanagerTabs","tickets","Edit.png"],
				["V živo","caremanagerTabs","chat","Setup.png"],
				["Vsebine","caremanagerTabs","content","News.png"]
			]
		} else if(session.role == "doctor") {
			menuItems = [
				["Naloge","doctorTabs","tasks","Task.png"],
				["Stanje","doctorTabs","patients","PatientFile.png"],
				["Poročila","doctorTabs","reports","DefinedParameters.png"],
				["Administracija","doctorTabs","admin","PatientData.png"],
				["Vprašanja","doctorTabs","tickets","Edit.png"],
				["V živo","doctorTabs","chat","Setup.png"],
				["Vsebine","doctorTabs","content","News.png"]
			]
		}

		menuItems.each { menuItem ->
			if(menuItem[1] == controllerName && menuItem[2] == actionName) {
				out << "<a href=\"" + g.createLink(controller:menuItem[1],action:menuItem[2]) + "\" class=\"layout-mainmenu-active\"><img src=\"" + g.resource(dir:'img/mi/min',file:menuItem[3]) +  "\" /> " + menuItem[0] + "</a>"
			} else {
				out << "<a href=\"" + g.createLink(controller:menuItem[1],action:menuItem[2]) + "\" class=\"layout-mainmenu-passive\"><img src=\"" + g.resource(dir:'img/mi/min',file:menuItem[3]) +  "\" /> " + menuItem[0] + "</a>"
			}
		}
	}
	
	// Table-styled form
	def tf = { attrs, body ->
		
		out << g.form(controller: attrs.controller, action: attrs.action) {
			out << "<table class=\"layout-tf\">"
			out	<< "<tr><td style=\"width:" + attrs.lwidth + ";\"></td><td></td></tr>"
			out << body()
			out << "</table>"
		}
		
	}
	
	// Table-styled form entry
	def tff = { attrs, body ->
		out << "<tr>"
		out << "<td>"
		out << "<p class=\"layout-tff-label\">" + attrs.label + "</p>"
		out << "</td>"
		out << "<td class=\"layout-tff\">"
		out << body()
		out << "</td>"
		out << "</tr>"
	}
	
	// Table-styled javascript-action form
	def tfjs = { attrs, body ->
		
		out << "<form action=\"javascript:" + attrs.onSubmit + "()\">"
		out << "<table class=\"layout-tf\">"
		out	<< "<tr><td style=\"width:" + attrs.lwidth + ";\"></td><td></td></tr>"
		out << body()
		out << "</table>"
		out << "</form>"
		
	}
	
	def t = { attrs, body ->
		
		out << "<table class=\"layout-tf\">"
		out	<< "<tr><td style=\"width:" + attrs.lwidth + ";\"></td><td></td></tr>"
		out << body()
		out << "</table>"
		
	}
	
	// Removes all HTML tags from body
	// Optionally can shorten output
	def withoutMarkup = { attrs, body ->
		
		String withoutMatkup = body()
		withoutMatkup = withoutMatkup.replaceAll(/[<][^<]*[>]/, " ")
		withoutMatkup = withoutMatkup.trim()
		
		if(attrs.length) {
			def lengthInt = attrs.length as int
			if(withoutMatkup.size() > lengthInt) {
				withoutMatkup = withoutMatkup.substring(0,lengthInt)
				withoutMatkup += "..."
			}
		}
		
		out << withoutMatkup
		
	}
	
	// Nice date output
	def niceDate = { attrs, body ->
		def scope = ''
		if(attrs.scope) {
			scope = attrs.scope
		}
		String dateString = attrs.value
		String year = dateString.substring(0, 4)
		String month = dateString.substring(5, 7)
		String day = dateString.substring(8, 10)
		String hours = dateString.substring(11, 13)
		String minutes = dateString.substring(14, 16)
		int monthInt = month as int
		def monthNames = [" ", "januar", "februar", "marec", "april", "maj", "junij", "julij", "avgust", "september", "oktober", "november", "december"]
		if(scope == '')       out << day + ". " + monthNames[monthInt] + " " + year
		else if(scope == 'min') out << day + ". " + monthNames[monthInt] + " " + year + " ob " + hours + ":" + minutes
		else if(scope == 'h') out << day + ". " + monthNames[monthInt] + " " + year + " ob " + hours + " uri"
		else if(scope == 'd') out << day + ". " + monthNames[monthInt] + " " + year
		else if(scope == 'm') out << monthNames[monthInt] + " " + year
		else if(scope == 'y') out << year
	}
	
	// Emphasize
	def emph = { attrs, body ->
		out << "<span style=\"font-variant:small-caps;\">"
		out << body()
		out << "</span>"
	}
	
	def ckeditorConfig = {
		
		out << ckeditor.config(var: 'toolbar_eo') {
			"[[ 'Maximize','-','Source','-','Save','NewPage','Preview','Print','-','Templates' ],[ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ],[ 'Find','Replace','-','SelectAll','-','SpellChecker' ],[ 'Link','Unlink','Anchor' ],[ 'Image','Table','HorizontalRule','SpecialChar','PageBreak' ],'/',[ 'Format' ],[ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ],[ 'NumberedList','BulletedList','-','Outdent','Indent','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock' ]]"
		}
		out << ckeditor.config (
			height: "300px",
			width:  "95%",
			toolbar: "eo"
		)
	}
	
	def avatarLink = { attrs, body ->
		out << "../publicContent/avatar?id=" + attrs.id
	}
	
	def elem = { attrs, body ->
		
		if(attrs.onclick != null) {
			out << "<div class=\"layout-element layout-element-clickable\" onclick=\"" + attrs.onclick + "\">"
		} else {
			out << "<div class=\"layout-element\">"
		}
		
		out << "<img class=\"layout-element-inner-img\" src=\"" + g.resource(dir:'img/mi/min',file: attrs.icon + '.png') + "\" />"
		out << "<div class=\"layout-element-inner-content\">"
		out << "<div class=\"layout-element-inner-name\">" + attrs.name + "</div>"
		out << "<div class=\"layout-element-inner-desc\">" + body() + "</div>"
		out << "</div>"
		out << "</div>"
		
	}
	
}
