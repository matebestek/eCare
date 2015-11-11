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
(function(){tinymce.create("tinymce.plugins.Preview",{init:function(a,b){var d=this,c=tinymce.explode(a.settings.content_css);d.editor=a;tinymce.each(c,function(f,e){c[e]=a.documentBaseURI.toAbsolute(f)});a.addCommand("mcePreview",function(){a.windowManager.open({file:a.getParam("plugin_preview_pageurl",b+"/preview.html"),width:parseInt(a.getParam("plugin_preview_width","550")),height:parseInt(a.getParam("plugin_preview_height","600")),resizable:"yes",scrollbars:"yes",popup_css:c?c.join(","):a.baseURI.toAbsolute("themes/"+a.settings.theme+"/skins/"+a.settings.skin+"/content.css"),inline:a.getParam("plugin_preview_inline",1)},{base:a.documentBaseURI.getURI()})});a.addButton("preview",{title:"preview.preview_desc",cmd:"mcePreview"})},getInfo:function(){return{longname:"Preview",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/preview",version:tinymce.majorVersion+"."+tinymce.minorVersion}}});tinymce.PluginManager.add("preview",tinymce.plugins.Preview)})();
