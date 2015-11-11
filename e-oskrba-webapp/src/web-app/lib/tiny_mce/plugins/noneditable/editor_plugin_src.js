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
	var Event = tinymce.dom.Event;

	tinymce.create('tinymce.plugins.NonEditablePlugin', {
		init : function(ed, url) {
			var t = this, editClass, nonEditClass, state;

			t.editor = ed;
			editClass = ed.getParam("noneditable_editable_class", "mceEditable");
			nonEditClass = ed.getParam("noneditable_noneditable_class", "mceNonEditable");

			ed.onNodeChange.addToTop(function(ed, cm, n) {
				var sc, ec;

				// Block if start or end is inside a non editable element
				sc = ed.dom.getParent(ed.selection.getStart(), function(n) {
					return ed.dom.hasClass(n, nonEditClass);
				});

				ec = ed.dom.getParent(ed.selection.getEnd(), function(n) {
					return ed.dom.hasClass(n, nonEditClass);
				});

				// Block or unblock
				if (sc || ec) {
					state = 1;
					t._setDisabled(1);
					return false;
				} else if (state == 1) {
					t._setDisabled(0);
					state = 0;
				}
			});
		},

		getInfo : function() {
			return {
				longname : 'Non editable elements',
				author : 'Moxiecode Systems AB',
				authorurl : 'http://tinymce.moxiecode.com',
				infourl : 'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/noneditable',
				version : tinymce.majorVersion + "." + tinymce.minorVersion
			};
		},

		_block : function(ed, e) {
			var k = e.keyCode;

			// Don't block arrow keys, pg up/down, and F1-F12
			if ((k > 32 && k < 41) || (k > 111 && k < 124))
				return;

			return Event.cancel(e);
		},

		_setDisabled : function(s) {
			var t = this, ed = t.editor;

			tinymce.each(ed.controlManager.controls, function(c) {
				c.setDisabled(s);
			});

			if (s !== t.disabled) {
				if (s) {
					ed.onKeyDown.addToTop(t._block);
					ed.onKeyPress.addToTop(t._block);
					ed.onKeyUp.addToTop(t._block);
					ed.onPaste.addToTop(t._block);
					ed.onContextMenu.addToTop(t._block);
				} else {
					ed.onKeyDown.remove(t._block);
					ed.onKeyPress.remove(t._block);
					ed.onKeyUp.remove(t._block);
					ed.onPaste.remove(t._block);
					ed.onContextMenu.remove(t._block);
				}

				t.disabled = s;
			}
		}
	});

	// Register plugin
	tinymce.PluginManager.add('noneditable', tinymce.plugins.NonEditablePlugin);
})();
