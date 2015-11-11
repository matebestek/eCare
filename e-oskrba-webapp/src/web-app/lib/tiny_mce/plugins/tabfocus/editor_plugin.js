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
(function(){var c=tinymce.DOM,a=tinymce.dom.Event,d=tinymce.each,b=tinymce.explode;tinymce.create("tinymce.plugins.TabFocusPlugin",{init:function(f,g){function e(i,j){if(j.keyCode===9){return a.cancel(j)}}function h(l,p){var j,m,o,n,k;function q(r){n=c.select(":input:enabled,*[tabindex]");function i(s){return s.type!="hidden"&&s.tabIndex!="-1"&&!(n[m].style.display=="none")&&!(n[m].style.visibility=="hidden")}d(n,function(t,s){if(t.id==l.id){j=s;return false}});if(r>0){for(m=j+1;m<n.length;m++){if(i(n[m])){return n[m]}}}else{for(m=j-1;m>=0;m--){if(i(n[m])){return n[m]}}}return null}if(p.keyCode===9){k=b(l.getParam("tab_focus",l.getParam("tabfocus_elements",":prev,:next")));if(k.length==1){k[1]=k[0];k[0]=":prev"}if(p.shiftKey){if(k[0]==":prev"){n=q(-1)}else{n=c.get(k[0])}}else{if(k[1]==":next"){n=q(1)}else{n=c.get(k[1])}}if(n){if(n.id&&(l=tinymce.get(n.id||n.name))){l.focus()}else{window.setTimeout(function(){if(!tinymce.isWebKit){window.focus()}n.focus()},10)}return a.cancel(p)}}}f.onKeyUp.add(e);if(tinymce.isGecko){f.onKeyPress.add(h);f.onKeyDown.add(e)}else{f.onKeyDown.add(h)}},getInfo:function(){return{longname:"Tabfocus",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/tabfocus",version:tinymce.majorVersion+"."+tinymce.minorVersion}}});tinymce.PluginManager.add("tabfocus",tinymce.plugins.TabFocusPlugin)})();
