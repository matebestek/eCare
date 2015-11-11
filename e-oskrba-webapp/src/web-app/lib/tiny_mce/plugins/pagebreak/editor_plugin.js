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
(function(){tinymce.create("tinymce.plugins.PageBreakPlugin",{init:function(b,d){var f='<img src="'+b.theme.url+'/img/trans.gif" class="mcePageBreak mceItemNoResize" />',a="mcePageBreak",c=b.getParam("pagebreak_separator","<!-- pagebreak -->"),e;e=new RegExp(c.replace(/[\?\.\*\[\]\(\)\{\}\+\^\$\:]/g,function(g){return"\\"+g}),"g");b.addCommand("mcePageBreak",function(){b.execCommand("mceInsertContent",0,f)});b.addButton("pagebreak",{title:"pagebreak.desc",cmd:a});b.onInit.add(function(){if(b.theme.onResolveName){b.theme.onResolveName.add(function(g,h){if(h.node.nodeName=="IMG"&&b.dom.hasClass(h.node,a)){h.name="pagebreak"}})}});b.onClick.add(function(g,h){h=h.target;if(h.nodeName==="IMG"&&g.dom.hasClass(h,a)){g.selection.select(h)}});b.onNodeChange.add(function(h,g,i){g.setActive("pagebreak",i.nodeName==="IMG"&&h.dom.hasClass(i,a))});b.onBeforeSetContent.add(function(g,h){h.content=h.content.replace(e,f)});b.onPostProcess.add(function(g,h){if(h.get){h.content=h.content.replace(/<img[^>]+>/g,function(i){if(i.indexOf('class="mcePageBreak')!==-1){i=c}return i})}})},getInfo:function(){return{longname:"PageBreak",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/pagebreak",version:tinymce.majorVersion+"."+tinymce.minorVersion}}});tinymce.PluginManager.add("pagebreak",tinymce.plugins.PageBreakPlugin)})();
