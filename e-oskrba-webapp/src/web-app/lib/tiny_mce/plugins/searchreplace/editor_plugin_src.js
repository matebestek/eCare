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
	tinymce.create('tinymce.plugins.SearchReplacePlugin', {
		init : function(ed, url) {
			function open(m) {
				// Keep IE from writing out the f/r character to the editor
				// instance while initializing a new dialog. See: #3131190
				window.focus();

				ed.windowManager.open({
					file : url + '/searchreplace.htm',
					width : 420 + parseInt(ed.getLang('searchreplace.delta_width', 0)),
					height : 170 + parseInt(ed.getLang('searchreplace.delta_height', 0)),
					inline : 1,
					auto_focus : 0
				}, {
					mode : m,
					search_string : ed.selection.getContent({format : 'text'}),
					plugin_url : url
				});
			};

			// Register commands
			ed.addCommand('mceSearch', function() {
				open('search');
			});

			ed.addCommand('mceReplace', function() {
				open('replace');
			});

			// Register buttons
			ed.addButton('search', {title : 'searchreplace.search_desc', cmd : 'mceSearch'});
			ed.addButton('replace', {title : 'searchreplace.replace_desc', cmd : 'mceReplace'});

			ed.addShortcut('ctrl+f', 'searchreplace.search_desc', 'mceSearch');
		},

		getInfo : function() {
			return {
				longname : 'Search/Replace',
				author : 'Moxiecode Systems AB',
				authorurl : 'http://tinymce.moxiecode.com',
				infourl : 'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/searchreplace',
				version : tinymce.majorVersion + "." + tinymce.minorVersion
			};
		}
	});

	// Register plugin
	tinymce.PluginManager.add('searchreplace', tinymce.plugins.SearchReplacePlugin);
})();
