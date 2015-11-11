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
 * del.js
 *
 * Copyright 2009, Moxiecode Systems AB
 * Released under LGPL License.
 *
 * License: http://tinymce.moxiecode.com/license
 * Contributing: http://tinymce.moxiecode.com/contributing
 */

function init() {
	SXE.initElementDialog('del');
	if (SXE.currentAction == "update") {
		setFormValue('datetime', tinyMCEPopup.editor.dom.getAttrib(SXE.updateElement, 'datetime'));
		setFormValue('cite', tinyMCEPopup.editor.dom.getAttrib(SXE.updateElement, 'cite'));
		SXE.showRemoveButton();
	}
}

function setElementAttribs(elm) {
	setAllCommonAttribs(elm);
	setAttrib(elm, 'datetime');
	setAttrib(elm, 'cite');
	elm.removeAttribute('data-mce-new');
}

function insertDel() {
	var elm = tinyMCEPopup.editor.dom.getParent(SXE.focusElement, 'DEL');

	if (elm == null) {
		var s = SXE.inst.selection.getContent();
		if(s.length > 0) {
			insertInlineElement('del');
			var elementArray = SXE.inst.dom.select('del[data-mce-new]');
			for (var i=0; i<elementArray.length; i++) {
				var elm = elementArray[i];
				setElementAttribs(elm);
			}
		}
	} else {
		setElementAttribs(elm);
	}
	tinyMCEPopup.editor.nodeChanged();
	tinyMCEPopup.execCommand('mceEndUndoLevel');
	tinyMCEPopup.close();
}

function removeDel() {
	SXE.removeElement('del');
	tinyMCEPopup.close();
}

tinyMCEPopup.onInit.add(init);
