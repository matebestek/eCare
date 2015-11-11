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
(function(a){a.onAddEditor.addToTop(function(c,b){b.settings.inline_styles=false});a.create("tinymce.plugins.LegacyOutput",{init:function(b){b.onInit.add(function(){var c="p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img",e=a.explode(b.settings.font_size_style_values),d=b.schema;b.formatter.register({alignleft:{selector:c,attributes:{align:"left"}},aligncenter:{selector:c,attributes:{align:"center"}},alignright:{selector:c,attributes:{align:"right"}},alignfull:{selector:c,attributes:{align:"justify"}},bold:[{inline:"b",remove:"all"},{inline:"strong",remove:"all"},{inline:"span",styles:{fontWeight:"bold"}}],italic:[{inline:"i",remove:"all"},{inline:"em",remove:"all"},{inline:"span",styles:{fontStyle:"italic"}}],underline:[{inline:"u",remove:"all"},{inline:"span",styles:{textDecoration:"underline"},exact:true}],strikethrough:[{inline:"strike",remove:"all"},{inline:"span",styles:{textDecoration:"line-through"},exact:true}],fontname:{inline:"font",attributes:{face:"%value"}},fontsize:{inline:"font",attributes:{size:function(f){return a.inArray(e,f.value)+1}}},forecolor:{inline:"font",styles:{color:"%value"}},hilitecolor:{inline:"font",styles:{backgroundColor:"%value"}}});a.each("b,i,u,strike".split(","),function(f){d.addValidElements(f+"[*]")});if(!d.getElementRule("font")){d.addValidElements("font[face|size|color|style]")}a.each(c.split(","),function(f){var h=d.getElementRule(f),g;if(h){if(!h.attributes.align){h.attributes.align={};h.attributesOrder.push("align")}}});b.onNodeChange.add(function(g,k){var j,f,h,i;f=g.dom.getParent(g.selection.getNode(),"font");if(f){h=f.face;i=f.size}if(j=k.get("fontselect")){j.select(function(l){return l==h})}if(j=k.get("fontsizeselect")){j.select(function(m){var l=a.inArray(e,m.fontSize);return l+1==i})}})})},getInfo:function(){return{longname:"LegacyOutput",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/legacyoutput",version:a.majorVersion+"."+a.minorVersion}}});a.PluginManager.add("legacyoutput",a.plugins.LegacyOutput)})(tinymce);
