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
(function(){tinymce.create("tinymce.plugins.VisualChars",{init:function(a,b){var c=this;c.editor=a;a.addCommand("mceVisualChars",c._toggleVisualChars,c);a.addButton("visualchars",{title:"visualchars.desc",cmd:"mceVisualChars"});a.onBeforeGetContent.add(function(d,e){if(c.state&&e.format!="raw"&&!e.draft){c.state=true;c._toggleVisualChars(false)}})},getInfo:function(){return{longname:"Visual characters",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/visualchars",version:tinymce.majorVersion+"."+tinymce.minorVersion}},_toggleVisualChars:function(m){var p=this,k=p.editor,a,g,j,n=k.getDoc(),o=k.getBody(),l,q=k.selection,e,c,f;p.state=!p.state;k.controlManager.setActive("visualchars",p.state);if(m){f=q.getBookmark()}if(p.state){a=[];tinymce.walk(o,function(b){if(b.nodeType==3&&b.nodeValue&&b.nodeValue.indexOf("\u00a0")!=-1){a.push(b)}},"childNodes");for(g=0;g<a.length;g++){l=a[g].nodeValue;l=l.replace(/(\u00a0)/g,'<span data-mce-bogus="1" class="mceItemHidden mceItemNbsp">$1</span>');c=k.dom.create("div",null,l);while(node=c.lastChild){k.dom.insertAfter(node,a[g])}k.dom.remove(a[g])}}else{a=k.dom.select("span.mceItemNbsp",o);for(g=a.length-1;g>=0;g--){k.dom.remove(a[g],1)}}q.moveToBookmark(f)}});tinymce.PluginManager.add("visualchars",tinymce.plugins.VisualChars)})();
