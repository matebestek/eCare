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
(function(){tinymce.create("tinymce.plugins.Layer",{init:function(a,b){var c=this;c.editor=a;a.addCommand("mceInsertLayer",c._insertLayer,c);a.addCommand("mceMoveForward",function(){c._move(1)});a.addCommand("mceMoveBackward",function(){c._move(-1)});a.addCommand("mceMakeAbsolute",function(){c._toggleAbsolute()});a.addButton("moveforward",{title:"layer.forward_desc",cmd:"mceMoveForward"});a.addButton("movebackward",{title:"layer.backward_desc",cmd:"mceMoveBackward"});a.addButton("absolute",{title:"layer.absolute_desc",cmd:"mceMakeAbsolute"});a.addButton("insertlayer",{title:"layer.insertlayer_desc",cmd:"mceInsertLayer"});a.onInit.add(function(){if(tinymce.isIE){a.getDoc().execCommand("2D-Position",false,true)}});a.onNodeChange.add(c._nodeChange,c);a.onVisualAid.add(c._visualAid,c)},getInfo:function(){return{longname:"Layer",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/layer",version:tinymce.majorVersion+"."+tinymce.minorVersion}},_nodeChange:function(b,a,e){var c,d;c=this._getParentLayer(e);d=b.dom.getParent(e,"DIV,P,IMG");if(!d){a.setDisabled("absolute",1);a.setDisabled("moveforward",1);a.setDisabled("movebackward",1)}else{a.setDisabled("absolute",0);a.setDisabled("moveforward",!c);a.setDisabled("movebackward",!c);a.setActive("absolute",c&&c.style.position.toLowerCase()=="absolute")}},_visualAid:function(a,c,b){var d=a.dom;tinymce.each(d.select("div,p",c),function(f){if(/^(absolute|relative|static)$/i.test(f.style.position)){if(b){d.addClass(f,"mceItemVisualAid")}else{d.removeClass(f,"mceItemVisualAid")}}})},_move:function(h){var b=this.editor,f,g=[],e=this._getParentLayer(b.selection.getNode()),c=-1,j=-1,a;a=[];tinymce.walk(b.getBody(),function(d){if(d.nodeType==1&&/^(absolute|relative|static)$/i.test(d.style.position)){a.push(d)}},"childNodes");for(f=0;f<a.length;f++){g[f]=a[f].style.zIndex?parseInt(a[f].style.zIndex):0;if(c<0&&a[f]==e){c=f}}if(h<0){for(f=0;f<g.length;f++){if(g[f]<g[c]){j=f;break}}if(j>-1){a[c].style.zIndex=g[j];a[j].style.zIndex=g[c]}else{if(g[c]>0){a[c].style.zIndex=g[c]-1}}}else{for(f=0;f<g.length;f++){if(g[f]>g[c]){j=f;break}}if(j>-1){a[c].style.zIndex=g[j];a[j].style.zIndex=g[c]}else{a[c].style.zIndex=g[c]+1}}b.execCommand("mceRepaint")},_getParentLayer:function(a){return this.editor.dom.getParent(a,function(b){return b.nodeType==1&&/^(absolute|relative|static)$/i.test(b.style.position)})},_insertLayer:function(){var a=this.editor,b=a.dom.getPos(a.dom.getParent(a.selection.getNode(),"*"));a.dom.add(a.getBody(),"div",{style:{position:"absolute",left:b.x,top:(b.y>20?b.y:20),width:100,height:100},"class":"mceItemVisualAid"},a.selection.getContent()||a.getLang("layer.content"))},_toggleAbsolute:function(){var a=this.editor,b=this._getParentLayer(a.selection.getNode());if(!b){b=a.dom.getParent(a.selection.getNode(),"DIV,P,IMG")}if(b){if(b.style.position.toLowerCase()=="absolute"){a.dom.setStyles(b,{position:"",left:"",top:"",width:"",height:""});a.dom.removeClass(b,"mceItemVisualAid")}else{if(b.style.left==""){b.style.left=20+"px"}if(b.style.top==""){b.style.top=20+"px"}if(b.style.width==""){b.style.width=b.width?(b.width+"px"):"100px"}if(b.style.height==""){b.style.height=b.height?(b.height+"px"):"100px"}b.style.position="absolute";a.dom.setAttrib(b,"data-mce-style","");a.addVisual(a.getBody())}a.execCommand("mceRepaint");a.nodeChanged()}}});tinymce.PluginManager.add("layer",tinymce.plugins.Layer)})();
