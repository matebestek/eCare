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
package si.pint.eoskrba.model;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import lombok.AccessLevel;

@Data
public class Email implements Serializable{
	
	private static final long serialVersionUID = -2891104341597418945L;
	public static final String EMAIL_SIG = "<br /><br />---<br />Raziskovalni projekt \"E-podpora procesa zdravstvene oskrbe\" (eOskrba)<br />Financirata (številka projekta L7-3653):<br />&bull; <a href=\"http://www.arrs.gov.si\" style=\"text-decoration:none;\">Javna agencija za raziskovalno dejavnost RS</a> in<br />&bull; <a href=\"http://www.mz.gov.si\" style=\"text-decoration:none;\">Ministrstvo za zdravje RS</a><br />Spletni naslov: <a href=\"https://eoskrba.pint.upr.si\">eoskrba.pint.upr.si</a><br />";
	
	public enum ContentType {html,text};
	
	private String from;
	@NonNull private String to;
	private String cc;
	private String bcc;
	private String contentType = ContentType.html.toString();
	@Setter(AccessLevel.NONE) @NonNull private String content;
	private String charset = "UTF-8";
	@NonNull private String subject;
	
	// Konstruktor, ki nastavi tudi podpis
	public Email(String to, String content, String subject) {
		this.to = to;
		this.subject = subject;
		this.content = translateSpecialChars(content + EMAIL_SIG);
	}
	
	// Setter, ki nastavi tudi podpis
	public void setContent(String content) {
		this.content = translateSpecialChars(content + EMAIL_SIG);
	}
	
	// Šumnike prevedi v ustrezne html entitete
	private static String translateSpecialChars(String content) {
		content = content.replaceAll("Š", "&Scaron;");
		content = content.replaceAll("š", "&scaron;");
		content = content.replaceAll("Č", "&#268;");
		content = content.replaceAll("č", "&#269;");
		content = content.replaceAll("Ž", "&#381;");
		content = content.replaceAll("ž", "&#382;");
		content = content.replaceAll("Đ", "&#272;");
		content = content.replaceAll("đ", "&#273;");
		content = content.replaceAll("Ć", "&#262;");
		content = content.replaceAll("ć", "&#263;");
		return content;
	}

}


