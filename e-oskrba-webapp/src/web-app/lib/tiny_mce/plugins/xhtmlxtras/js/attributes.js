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
 * attributes.js
 *
 * Copyright 2009, Moxiecode Systems AB
 * Released under LGPL License.
 *
 * License: http://tinymce.moxiecode.com/license
 * Contributing: http://tinymce.moxiecode.com/contributing
 */

function init() {
	tinyMCEPopup.resizeToInnerSize();
	var inst = tinyMCEPopup.editor;
	var dom = inst.dom;
	var elm = inst.selection.getNode();
	var f = document.forms[0];
	var onclick = dom.getAttrib(elm, 'onclick');

	setFormValue('title', dom.getAttrib(elm, 'title'));
	setFormValue('id', dom.getAttrib(elm, 'id'));
	setFormValue('style', dom.getAttrib(elm, "style"));
	setFormValue('dir', dom.getAttrib(elm, 'dir'));
	setFormValue('lang', dom.getAttrib(elm, 'lang'));
	setFormValue('tabindex', dom.getAttrib(elm, 'tabindex', typeof(elm.tabindex) != "undefined" ? elm.tabindex : ""));
	setFormValue('accesskey', dom.getAttrib(elm, 'accesskey', typeof(elm.accesskey) != "undefined" ? elm.accesskey : ""));
	setFormValue('onfocus', dom.getAttrib(elm, 'onfocus'));
	setFormValue('onblur', dom.getAttrib(elm, 'onblur'));
	setFormValue('onclick', onclick);
	setFormValue('ondblclick', dom.getAttrib(elm, 'ondblclick'));
	setFormValue('onmousedown', dom.getAttrib(elm, 'onmousedown'));
	setFormValue('onmouseup', dom.getAttrib(elm, 'onmouseup'));
	setFormValue('onmouseover', dom.getAttrib(elm, 'onmouseover'));
	setFormValue('onmousemove', dom.getAttrib(elm, 'onmousemove'));
	setFormValue('onmouseout', dom.getAttrib(elm, 'onmouseout'));
	setFormValue('onkeypress', dom.getAttrib(elm, 'onkeypress'));
	setFormValue('onkeydown', dom.getAttrib(elm, 'onkeydown'));
	setFormValue('onkeyup', dom.getAttrib(elm, 'onkeyup'));
	className = dom.getAttrib(elm, 'class');

	addClassesToList('classlist', 'advlink_styles');
	selectByValue(f, 'classlist', className, true);

	TinyMCE_EditableSelects.init();
}

function setFormValue(name, value) {
	if(value && document.forms[0].elements[name]){
		document.forms[0].elements[name].value = value;
	}
}

function insertAction() {
	var inst = tinyMCEPopup.editor;
	var elm = inst.selection.getNode();

	setAllAttribs(elm);
	tinyMCEPopup.execCommand("mceEndUndoLevel");
	tinyMCEPopup.close();
}

function setAttrib(elm, attrib, value) {
	var formObj = document.forms[0];
	var valueElm = formObj.elements[attrib.toLowerCase()];
	var inst = tinyMCEPopup.editor;
	var dom = inst.dom;

	if (typeof(value) == "undefined" || value == null) {
		value = "";

		if (valueElm)
			value = valueElm.value;
	}

	dom.setAttrib(elm, attrib.toLowerCase(), value);
}

function setAllAttribs(elm) {
	var f = document.forms[0];

	setAttrib(elm, 'title');
	setAttrib(elm, 'id');
	setAttrib(elm, 'style');
	setAttrib(elm, 'class', getSelectValue(f, 'classlist'));
	setAttrib(elm, 'dir');
	setAttrib(elm, 'lang');
	setAttrib(elm, 'tabindex');
	setAttrib(elm, 'accesskey');
	setAttrib(elm, 'onfocus');
	setAttrib(elm, 'onblur');
	setAttrib(elm, 'onclick');
	setAttrib(elm, 'ondblclick');
	setAttrib(elm, 'onmousedown');
	setAttrib(elm, 'onmouseup');
	setAttrib(elm, 'onmouseover');
	setAttrib(elm, 'onmousemove');
	setAttrib(elm, 'onmouseout');
	setAttrib(elm, 'onkeypress');
	setAttrib(elm, 'onkeydown');
	setAttrib(elm, 'onkeyup');

	// Refresh in old MSIE
//	if (tinyMCE.isMSIE5)
//		elm.outerHTML = elm.outerHTML;
}

function insertAttribute() {
	tinyMCEPopup.close();
}

tinyMCEPopup.onInit.add(init);
tinyMCEPopup.requireLangPack();
