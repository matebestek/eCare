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
/**
 * editable_selects.js
 *
 * Copyright 2009, Moxiecode Systems AB
 * Released under LGPL License.
 *
 * License: http://tinymce.moxiecode.com/license
 * Contributing: http://tinymce.moxiecode.com/contributing
 */

var TinyMCE_EditableSelects = {
	editSelectElm : null,

	init : function() {
		var nl = document.getElementsByTagName("select"), i, d = document, o;

		for (i=0; i<nl.length; i++) {
			if (nl[i].className.indexOf('mceEditableSelect') != -1) {
				o = new Option('(value)', '__mce_add_custom__');

				o.className = 'mceAddSelectValue';

				nl[i].options[nl[i].options.length] = o;
				nl[i].onchange = TinyMCE_EditableSelects.onChangeEditableSelect;
			}
		}
	},

	onChangeEditableSelect : function(e) {
		var d = document, ne, se = window.event ? window.event.srcElement : e.target;

		if (se.options[se.selectedIndex].value == '__mce_add_custom__') {
			ne = d.createElement("input");
			ne.id = se.id + "_custom";
			ne.name = se.name + "_custom";
			ne.type = "text";

			ne.style.width = se.offsetWidth + 'px';
			se.parentNode.insertBefore(ne, se);
			se.style.display = 'none';
			ne.focus();
			ne.onblur = TinyMCE_EditableSelects.onBlurEditableSelectInput;
			ne.onkeydown = TinyMCE_EditableSelects.onKeyDown;
			TinyMCE_EditableSelects.editSelectElm = se;
		}
	},

	onBlurEditableSelectInput : function() {
		var se = TinyMCE_EditableSelects.editSelectElm;

		if (se) {
			if (se.previousSibling.value != '') {
				addSelectValue(document.forms[0], se.id, se.previousSibling.value, se.previousSibling.value);
				selectByValue(document.forms[0], se.id, se.previousSibling.value);
			} else
				selectByValue(document.forms[0], se.id, '');

			se.style.display = 'inline';
			se.parentNode.removeChild(se.previousSibling);
			TinyMCE_EditableSelects.editSelectElm = null;
		}
	},

	onKeyDown : function(e) {
		e = e || window.event;

		if (e.keyCode == 13)
			TinyMCE_EditableSelects.onBlurEditableSelectInput();
	}
};
