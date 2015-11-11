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
(function(){var a=tinymce.each;tinymce.create("tinymce.plugins.TemplatePlugin",{init:function(b,c){var d=this;d.editor=b;b.addCommand("mceTemplate",function(e){b.windowManager.open({file:c+"/template.htm",width:b.getParam("template_popup_width",750),height:b.getParam("template_popup_height",600),inline:1},{plugin_url:c})});b.addCommand("mceInsertTemplate",d._insertTemplate,d);b.addButton("template",{title:"template.desc",cmd:"mceTemplate"});b.onPreProcess.add(function(e,g){var f=e.dom;a(f.select("div",g.node),function(h){if(f.hasClass(h,"mceTmpl")){a(f.select("*",h),function(i){if(f.hasClass(i,e.getParam("template_mdate_classes","mdate").replace(/\s+/g,"|"))){i.innerHTML=d._getDateTime(new Date(),e.getParam("template_mdate_format",e.getLang("template.mdate_format")))}});d._replaceVals(h)}})})},getInfo:function(){return{longname:"Template plugin",author:"Moxiecode Systems AB",authorurl:"http://www.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/template",version:tinymce.majorVersion+"."+tinymce.minorVersion}},_insertTemplate:function(i,j){var k=this,g=k.editor,f,c,d=g.dom,b=g.selection.getContent();f=j.content;a(k.editor.getParam("template_replace_values"),function(l,h){if(typeof(l)!="function"){f=f.replace(new RegExp("\\{\\$"+h+"\\}","g"),l)}});c=d.create("div",null,f);n=d.select(".mceTmpl",c);if(n&&n.length>0){c=d.create("div",null);c.appendChild(n[0].cloneNode(true))}function e(l,h){return new RegExp("\\b"+h+"\\b","g").test(l.className)}a(d.select("*",c),function(h){if(e(h,g.getParam("template_cdate_classes","cdate").replace(/\s+/g,"|"))){h.innerHTML=k._getDateTime(new Date(),g.getParam("template_cdate_format",g.getLang("template.cdate_format")))}if(e(h,g.getParam("template_mdate_classes","mdate").replace(/\s+/g,"|"))){h.innerHTML=k._getDateTime(new Date(),g.getParam("template_mdate_format",g.getLang("template.mdate_format")))}if(e(h,g.getParam("template_selected_content_classes","selcontent").replace(/\s+/g,"|"))){h.innerHTML=b}});k._replaceVals(c);g.execCommand("mceInsertContent",false,c.innerHTML);g.addVisual()},_replaceVals:function(c){var d=this.editor.dom,b=this.editor.getParam("template_replace_values");a(d.select("*",c),function(f){a(b,function(g,e){if(d.hasClass(f,e)){if(typeof(b[e])=="function"){b[e](f)}}})})},_getDateTime:function(e,b){if(!b){return""}function c(g,d){var f;g=""+g;if(g.length<d){for(f=0;f<(d-g.length);f++){g="0"+g}}return g}b=b.replace("%D","%m/%d/%y");b=b.replace("%r","%I:%M:%S %p");b=b.replace("%Y",""+e.getFullYear());b=b.replace("%y",""+e.getYear());b=b.replace("%m",c(e.getMonth()+1,2));b=b.replace("%d",c(e.getDate(),2));b=b.replace("%H",""+c(e.getHours(),2));b=b.replace("%M",""+c(e.getMinutes(),2));b=b.replace("%S",""+c(e.getSeconds(),2));b=b.replace("%I",""+((e.getHours()+11)%12+1));b=b.replace("%p",""+(e.getHours()<12?"AM":"PM"));b=b.replace("%B",""+this.editor.getLang("template_months_long").split(",")[e.getMonth()]);b=b.replace("%b",""+this.editor.getLang("template_months_short").split(",")[e.getMonth()]);b=b.replace("%A",""+this.editor.getLang("template_day_long").split(",")[e.getDay()]);b=b.replace("%a",""+this.editor.getLang("template_day_short").split(",")[e.getDay()]);b=b.replace("%%","%");return b}});tinymce.PluginManager.add("template",tinymce.plugins.TemplatePlugin)})();
