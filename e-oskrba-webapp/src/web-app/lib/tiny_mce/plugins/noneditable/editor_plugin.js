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
(function(){var a=tinymce.dom.Event;tinymce.create("tinymce.plugins.NonEditablePlugin",{init:function(d,e){var f=this,c,b,g;f.editor=d;c=d.getParam("noneditable_editable_class","mceEditable");b=d.getParam("noneditable_noneditable_class","mceNonEditable");d.onNodeChange.addToTop(function(i,h,l){var k,j;k=i.dom.getParent(i.selection.getStart(),function(m){return i.dom.hasClass(m,b)});j=i.dom.getParent(i.selection.getEnd(),function(m){return i.dom.hasClass(m,b)});if(k||j){g=1;f._setDisabled(1);return false}else{if(g==1){f._setDisabled(0);g=0}}})},getInfo:function(){return{longname:"Non editable elements",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/noneditable",version:tinymce.majorVersion+"."+tinymce.minorVersion}},_block:function(c,d){var b=d.keyCode;if((b>32&&b<41)||(b>111&&b<124)){return}return a.cancel(d)},_setDisabled:function(d){var c=this,b=c.editor;tinymce.each(b.controlManager.controls,function(e){e.setDisabled(d)});if(d!==c.disabled){if(d){b.onKeyDown.addToTop(c._block);b.onKeyPress.addToTop(c._block);b.onKeyUp.addToTop(c._block);b.onPaste.addToTop(c._block);b.onContextMenu.addToTop(c._block)}else{b.onKeyDown.remove(c._block);b.onKeyPress.remove(c._block);b.onKeyUp.remove(c._block);b.onPaste.remove(c._block);b.onContextMenu.remove(c._block)}c.disabled=d}}});tinymce.PluginManager.add("noneditable",tinymce.plugins.NonEditablePlugin)})();
