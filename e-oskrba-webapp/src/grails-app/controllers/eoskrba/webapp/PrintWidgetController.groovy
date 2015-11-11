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

import java.net.Authenticator.RequestorType;

import org.htmlcleaner.*;
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

class PrintWidgetController {
	
	def imageUploadService

    def print = {
		
		// Pridobi parametre
		def device = params["layout-printbox-device"]
		def encodedContent = params["layout-printbox-content"]
		def timestamp = params["layout-printbox-timestamp"]
		
		// dekodiraj html
		URLDecoder ud = new URLDecoder()
		def content = ud.decode(encodedContent, "UTF-8")
		
		// Uredi html - korak 1
		content = content.replaceAll( /style=".*?"/, "")
		CleanerProperties htmlcProps = new CleanerProperties()
		HtmlCleaner htmlc = new HtmlCleaner(htmlcProps)
		TagNode cleanContent = htmlc.clean(content)
		
		// Uredi html - korak 2
		// XHTMLRenderer ne zna pretvoriti input polj v PDF obliko, zato jih moramo
		// nadomestiti z bolj primitivnimi elementi
		def references = [:]
		cleanContent.getAllElements(true).each {
			if(it.name == "input") {
				if(it.getAttributeByName("type") == "text") {
					it.setName("div")
					it.setAttribute("class", "oInputText")
					it.removeAllChildren()
					it.insertChild(0, new ContentNode(it.getAttributeByName("value")))
				} else if(it.getAttributeByName("type") == "radio") {
					it.setName("span")
					it.setAttribute("class", "oInputRadio")
					it.removeAllChildren()
					it.insertChild(0, new ContentNode(" &bull; "))
					if(it.getAttributeByName("checked") != null) {
						it.setAttribute("class", "oInputRadioChecked")
					}
				}
			} else if(it.name == "a") {
				it.setName("span")
				it.setAttribute("class", "oA")
				if(it.getAttributeByName("href") != null) {
					String href = it.getAttributeByName("href")
					if(href.matches("http.*")) {
						int refIndex = references.size() + 1
						references[refIndex] = href
						ContentNode cn = (ContentNode) it.children[0]
						def newContent = cn.getContent().append(" ["+refIndex+"]").toString()
						it.removeAllChildren()
						it.insertChild(0, new ContentNode(newContent))
					}
				}
			} else if(it.name == "option") {
				it.setName("li")
				it.setAttribute("class", "oOption")
				if(it.getAttributeByName("selected") != null) {
					it.setAttribute("class", "oOptionSelected")
				}
			} else if(it.name == "select") {
				it.setName("ul")
				it.setAttribute("class", "oSelect")
			} else if(it.name == "canvas") {
				def imageData = it.getAttributeByName("imagedata")
				if( imageData != null ) {
					it.removeAllChildren()
					it.setName("img")
					def fname = imageUploadService.saveImageFromCanvas(imageData)
					it.removeAttribute("imagedata")
					it.setAttribute("src", g.resource(dir:'temp',file:fname).toString() )
				} else {
					it.removeFromTree()
				}
			} else if(it.name == "textarea") {
				it.setName("div")
				it.setAttribute("class", "oTextarea")
			}
		}
		
		// Uredi html - korak 3
		SimpleHtmlSerializer htmlSer = new SimpleHtmlSerializer(htmlcProps)
		content = htmlSer.getAsString(cleanContent)
		content = content.replaceAll( /<\?xml.*?\?>/, "")
		content = content.replaceAll( /<html>.*?<body>/, "")
		content = content.replaceAll( /<\/body>.*?<\/html>/, "")
		content = content.replaceAll( /←/, "")
		content = content.replaceAll( /→/, "")
		content = content.replaceAll( /preserve=/, "style=")
		
		if(device == "printer") {
			return [content:content,references:references]
		} else if(device == "pdf") {
			def filename = "eOskrba-" + (new Date()).getTime() + ".pdf"
			renderPdf(template: "/printWidget/pdf",model: [content: content,references:references], filename: filename  )
		}
		
	}
	
}
