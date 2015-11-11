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
/**
 * editor_plugin_src.js
 *
 * Copyright 2009, Moxiecode Systems AB
 * Released under LGPL License.
 *
 * License: http://tinymce.moxiecode.com/license
 * Contributing: http://tinymce.moxiecode.com/contributing
 */

(function() {
	tinymce.create('tinymce.plugins.InsertDateTime', {
		init : function(ed, url) {
			var t = this;

			t.editor = ed;

			ed.addCommand('mceInsertDate', function() {
				var str = t._getDateTime(new Date(), ed.getParam("plugin_insertdate_dateFormat", ed.getLang('insertdatetime.date_fmt')));

				ed.execCommand('mceInsertContent', false, str);
			});

			ed.addCommand('mceInsertTime', function() {
				var str = t._getDateTime(new Date(), ed.getParam("plugin_insertdate_timeFormat", ed.getLang('insertdatetime.time_fmt')));

				ed.execCommand('mceInsertContent', false, str);
			});

			ed.addButton('insertdate', {title : 'insertdatetime.insertdate_desc', cmd : 'mceInsertDate'});
			ed.addButton('inserttime', {title : 'insertdatetime.inserttime_desc', cmd : 'mceInsertTime'});
		},

		getInfo : function() {
			return {
				longname : 'Insert date/time',
				author : 'Moxiecode Systems AB',
				authorurl : 'http://tinymce.moxiecode.com',
				infourl : 'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/insertdatetime',
				version : tinymce.majorVersion + "." + tinymce.minorVersion
			};
		},

		// Private methods

		_getDateTime : function(d, fmt) {
			var ed = this.editor;

			function addZeros(value, len) {
				value = "" + value;

				if (value.length < len) {
					for (var i=0; i<(len-value.length); i++)
						value = "0" + value;
				}

				return value;
			};

			fmt = fmt.replace("%D", "%m/%d/%y");
			fmt = fmt.replace("%r", "%I:%M:%S %p");
			fmt = fmt.replace("%Y", "" + d.getFullYear());
			fmt = fmt.replace("%y", "" + d.getYear());
			fmt = fmt.replace("%m", addZeros(d.getMonth()+1, 2));
			fmt = fmt.replace("%d", addZeros(d.getDate(), 2));
			fmt = fmt.replace("%H", "" + addZeros(d.getHours(), 2));
			fmt = fmt.replace("%M", "" + addZeros(d.getMinutes(), 2));
			fmt = fmt.replace("%S", "" + addZeros(d.getSeconds(), 2));
			fmt = fmt.replace("%I", "" + ((d.getHours() + 11) % 12 + 1));
			fmt = fmt.replace("%p", "" + (d.getHours() < 12 ? "AM" : "PM"));
			fmt = fmt.replace("%B", "" + ed.getLang("insertdatetime.months_long").split(',')[d.getMonth()]);
			fmt = fmt.replace("%b", "" + ed.getLang("insertdatetime.months_short").split(',')[d.getMonth()]);
			fmt = fmt.replace("%A", "" + ed.getLang("insertdatetime.day_long").split(',')[d.getDay()]);
			fmt = fmt.replace("%a", "" + ed.getLang("insertdatetime.day_short").split(',')[d.getDay()]);
			fmt = fmt.replace("%%", "%");

			return fmt;
		}
	});

	// Register plugin
	tinymce.PluginManager.add('insertdatetime', tinymce.plugins.InsertDateTime);
})();
