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
/* 
 *   eOskrba-print.js
 *   version 1.1
 *   
 *   Aleksander Bešir
 *   alex.besir@gmail.com
 */

function panel_print() {
	export_to_device("printer");
}

function panel_pdf() {
	export_to_device("pdf");
}

// Pripravi vsebino za izvoz in pošlje vsebino naprej
function export_to_device(device) {
	var content = panel_grabContent();
	content = encodeURIComponent(content);
	post_to_controller(content,device);
}

// Vsebino pošlje v controller za izvoz
function post_to_controller(content,device) {
	$("#layout-printbox-content").val(content);
	$("#layout-printbox-device").val(device);
	$("#layout-printbox-timestamp").val(timestamp());
	$("#layout-printbox-form").submit();
}

// Iz vsebinskega okna prebere trenutno stanje html-ja
function panel_grabContent() {
	// Najprej vsem canvas-om dodaj atribut, ki nosi grafične podatke
	$("#layout-panelframe canvas.base").each(function(){
		var canvas = $(this)[0]
		var image = canvas.toDataURL("image/png");
        image = image.replace('data:image/png;base64,', '');
        $(this).attr("imagedata",encodeURIComponent(image));
	});
	// Nato vsemu kar pripada h canvasu (vse v placeholder-ju) ohrani style
	$("#layout-panelframe canvas.base").parent().each(function(){
		 $(this).attr("preserve",$(this).attr("style"));
	});
	$("#layout-panelframe canvas.base").parent().find("*").each(function(){
		 $(this).attr("preserve",$(this).attr("style"));
	});
	// Pridobi HTML vsebino
	var content = $("#layout-panelframe").formhtml();
	return content;
}

// jQuery plugin za pridobivanje HTML-ja z izpolnjenimi obrazci
(function($) {
	var oldHTML = $.fn.html;
	$.fn.formhtml = function() {
	    if (arguments.length) return oldHTML.apply(this,arguments);
	    $("input,button", this).each(function() {
	    	this.setAttribute('value',this.value);
	    });
	    $("textarea", this).each(function() {
	    	this.innerHTML = this.value;
	    });
	    $("input:radio,input:checkbox", this).each(function() {
	    	if (this.checked) this.setAttribute('checked', 'checked');
	    	else this.removeAttribute('checked');
	    });
	    $("option", this).each(function() {
	    	if (this.selected) this.setAttribute('selected', 'selected');
	    	else this.removeAttribute('selected');
	    });
	    return oldHTML.apply(this);
	};
})(jQuery);
