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
(function(){var a=tinymce.each;tinymce.create("tinymce.plugins.AdvListPlugin",{init:function(b,c){var d=this;d.editor=b;function e(g){var f=[];a(g.split(/,/),function(h){f.push({title:"advlist."+(h=="default"?"def":h.replace(/-/g,"_")),styles:{listStyleType:h=="default"?"":h}})});return f}d.numlist=b.getParam("advlist_number_styles")||e("default,lower-alpha,lower-greek,lower-roman,upper-alpha,upper-roman");d.bullist=b.getParam("advlist_bullet_styles")||e("default,circle,disc,square");if(tinymce.isIE&&/MSIE [2-7]/.test(navigator.userAgent)){d.isIE7=true}},createControl:function(d,b){var f=this,e,i,g=f.editor;if(d=="numlist"||d=="bullist"){if(f[d][0].title=="advlist.def"){i=f[d][0]}function c(j,l){var k=true;a(l.styles,function(n,m){if(g.dom.getStyle(j,m)!=n){k=false;return false}});return k}function h(){var k,l=g.dom,j=g.selection;k=l.getParent(j.getNode(),"ol,ul");if(!k||k.nodeName==(d=="bullist"?"OL":"UL")||c(k,i)){g.execCommand(d=="bullist"?"InsertUnorderedList":"InsertOrderedList")}if(i){k=l.getParent(j.getNode(),"ol,ul");if(k){l.setStyles(k,i.styles);k.removeAttribute("data-mce-style")}}g.focus()}e=b.createSplitButton(d,{title:"advanced."+d+"_desc","class":"mce_"+d,onclick:function(){h()}});e.onRenderMenu.add(function(j,k){k.onHideMenu.add(function(){if(f.bookmark){g.selection.moveToBookmark(f.bookmark);f.bookmark=0}});k.onShowMenu.add(function(){var n=g.dom,m=n.getParent(g.selection.getNode(),"ol,ul"),l;if(m||i){l=f[d];a(k.items,function(o){var p=true;o.setSelected(0);if(m&&!o.isDisabled()){a(l,function(q){if(q.id==o.id){if(!c(m,q)){p=false;return false}}});if(p){o.setSelected(1)}}});if(!m){k.items[i.id].setSelected(1)}}g.focus();if(tinymce.isIE){f.bookmark=g.selection.getBookmark(1)}});k.add({id:g.dom.uniqueId(),title:"advlist.types","class":"mceMenuItemTitle",titleItem:true}).setDisabled(1);a(f[d],function(l){if(f.isIE7&&l.styles.listStyleType=="lower-greek"){return}l.id=g.dom.uniqueId();k.add({id:l.id,title:l.title,onclick:function(){i=l;h()}})})});return e}},getInfo:function(){return{longname:"Advanced lists",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/advlist",version:tinymce.majorVersion+"."+tinymce.minorVersion}}});tinymce.PluginManager.add("advlist",tinymce.plugins.AdvListPlugin)})();
