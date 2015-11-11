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
(function(){tinymce.create("tinymce.plugins.InsertDateTime",{init:function(a,b){var c=this;c.editor=a;a.addCommand("mceInsertDate",function(){var d=c._getDateTime(new Date(),a.getParam("plugin_insertdate_dateFormat",a.getLang("insertdatetime.date_fmt")));a.execCommand("mceInsertContent",false,d)});a.addCommand("mceInsertTime",function(){var d=c._getDateTime(new Date(),a.getParam("plugin_insertdate_timeFormat",a.getLang("insertdatetime.time_fmt")));a.execCommand("mceInsertContent",false,d)});a.addButton("insertdate",{title:"insertdatetime.insertdate_desc",cmd:"mceInsertDate"});a.addButton("inserttime",{title:"insertdatetime.inserttime_desc",cmd:"mceInsertTime"})},getInfo:function(){return{longname:"Insert date/time",author:"Moxiecode Systems AB",authorurl:"http://tinymce.moxiecode.com",infourl:"http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/insertdatetime",version:tinymce.majorVersion+"."+tinymce.minorVersion}},_getDateTime:function(e,a){var c=this.editor;function b(g,d){g=""+g;if(g.length<d){for(var f=0;f<(d-g.length);f++){g="0"+g}}return g}a=a.replace("%D","%m/%d/%y");a=a.replace("%r","%I:%M:%S %p");a=a.replace("%Y",""+e.getFullYear());a=a.replace("%y",""+e.getYear());a=a.replace("%m",b(e.getMonth()+1,2));a=a.replace("%d",b(e.getDate(),2));a=a.replace("%H",""+b(e.getHours(),2));a=a.replace("%M",""+b(e.getMinutes(),2));a=a.replace("%S",""+b(e.getSeconds(),2));a=a.replace("%I",""+((e.getHours()+11)%12+1));a=a.replace("%p",""+(e.getHours()<12?"AM":"PM"));a=a.replace("%B",""+c.getLang("insertdatetime.months_long").split(",")[e.getMonth()]);a=a.replace("%b",""+c.getLang("insertdatetime.months_short").split(",")[e.getMonth()]);a=a.replace("%A",""+c.getLang("insertdatetime.day_long").split(",")[e.getDay()]);a=a.replace("%a",""+c.getLang("insertdatetime.day_short").split(",")[e.getDay()]);a=a.replace("%%","%");return a}});tinymce.PluginManager.add("insertdatetime",tinymce.plugins.InsertDateTime)})();
