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
(function(){tinymce.create("tinymce.plugins.AdvancedImagePlugin",{init:function(a,b){a.addCommand("mceAdvImage",function(){if(a.dom.getAttrib(a.selection.getNode(),"class","").indexOf("mceItem")!=-1){return}a.windowManager.open({file:b+"/image.htm",width:480+parseInt(a.getLang("advimage.delta_width",0)),height:385+parseInt(a.getLang("advimage.delta_height",0)),inline:1},{plugin_url:b})});a.addButton("image",{title:"advimage.image_desc",cmd:"mceAdvImage"})},getInfo:function(){return{longname:"Advanced image",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/advimage",version:tinymce.majorVersion+"."+tinymce.minorVersion}}});tinymce.PluginManager.add("advimage",tinymce.plugins.AdvancedImagePlugin)})();
