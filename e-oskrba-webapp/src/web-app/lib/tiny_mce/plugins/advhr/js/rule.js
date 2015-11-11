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
var AdvHRDialog = {
	init : function(ed) {
		var dom = ed.dom, f = document.forms[0], n = ed.selection.getNode(), w;

		w = dom.getAttrib(n, 'width');
		f.width.value = w ? parseInt(w) : (dom.getStyle('width') || '');
		f.size.value = dom.getAttrib(n, 'size') || parseInt(dom.getStyle('height')) || '';
		f.noshade.checked = !!dom.getAttrib(n, 'noshade') || !!dom.getStyle('border-width');
		selectByValue(f, 'width2', w.indexOf('%') != -1 ? '%' : 'px');
	},

	update : function() {
		var ed = tinyMCEPopup.editor, h, f = document.forms[0], st = '';

		h = '<hr';

		if (f.size.value) {
			h += ' size="' + f.size.value + '"';
			st += ' height:' + f.size.value + 'px;';
		}

		if (f.width.value) {
			h += ' width="' + f.width.value + (f.width2.value == '%' ? '%' : '') + '"';
			st += ' width:' + f.width.value + (f.width2.value == '%' ? '%' : 'px') + ';';
		}

		if (f.noshade.checked) {
			h += ' noshade="noshade"';
			st += ' border-width: 1px; border-style: solid; border-color: #CCCCCC; color: #ffffff;';
		}

		if (ed.settings.inline_styles)
			h += ' style="' + tinymce.trim(st) + '"';

		h += ' />';

		ed.execCommand("mceInsertContent", false, h);
		tinyMCEPopup.close();
	}
};

tinyMCEPopup.requireLangPack();
tinyMCEPopup.onInit.add(AdvHRDialog.init, AdvHRDialog);
