<%--
  Copyright (c) 2013.
  University of Primorska, Andrej Marušič Institute. All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met: 

  1. Redistributions of source code must retain the above copyright notice, this
     list of conditions and the following disclaimer. 
  2. Redistributions in binary form must reproduce the above copyright notice,
     this list of conditions and the following disclaimer in the documentation
     and/or other materials provided with the distribution. 

  Project eOskrba (http://eOskrba.si) was financed by Slovenian Research
  Agency and Ministry of Health of Republic of Slovenia.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
--%>
<html>
<head>
	<title>Vsebina za paciente</title>
	<meta name="layout" content="main" />
</head>
<body>
	
	<eo:widgetFrame>
	
		<eo:staticWidget title="O vsebinah" id="caremanagerTabs_content_intro">
			<p>Tu lahko dodajate in urejate vsebine namenjene pacientom, kot so <b>novice</b>, <b>priporočila</b> in <b>izobraževalne vsebine</b>.</p>
		</eo:staticWidget>
	
		<eo:staticWidget title="Dodajanje vsebin" type="normal">

			<eo:elem icon="NoteNew" name="Dodaj priporočilo" onclick="newAdviceitem();"></eo:elem>
			<eo:elem icon="NewsNew" name="Dodaj novico" onclick="newNewsitem();"></eo:elem>
			<eo:elem icon="ArchiveNew" name="Dodaj izobraževalno vsebino" onclick="newLearningContent();"></eo:elem>

			<g:javascript>
				function newAdviceitem() {
					panel_load(
						"adviceWriterWidget",
						"form",
						{ }
					);
				}
				function newNewsitem() {
					panel_load(
						"newsWriterWidget",
						"form",
						{ }
					);
				}
				function newLearningContent() {
					panel_load(
						"learningContentWriterWidget",
						"form",
						{ }
					);
				}
			</g:javascript>
		</eo:staticWidget>
		
		<eo:widget title="Priporočila" load="adviceWriterWidget" action="show">
			<g:javascript>
				function adviceWriterWidget_show_onReadMore(adviceitem_id,title) {
					panel_load(
						"adviceReaderWidget",
						"readMore",
						{ id: adviceitem_id }
					);
				}
			</g:javascript>
		</eo:widget>
		
		<eo:widget title="Novice" load="newsWriterWidget" action="show">
			<g:javascript>
				function newsWriterWidget_show_onReadMore(newsitem_id,title) {
					panel_load(
						"newsReaderWidget",
						"readMore",
						{ id: newsitem_id }
					);
				}
			</g:javascript>
		</eo:widget>
		
		<eo:widget title="Izobraževalne vsebine" load="learningContentWriterWidget" action="show">
			<g:javascript>
				function learningContentWriterWidget_show_onReadMore(item_id,title) {
					panel_load(
						"learningContentReaderWidget",
						"readMore",
						{ id: item_id }
					);
				}
			</g:javascript>
		</eo:widget>
	
	</eo:widgetFrame>
	
	<eo:panelFrame>

		<g:javascript>
			function newsWriterWidget_form_onSubmit(title, content) {
				postWithFlash(
					'${g.createLink(controller:'newsWriterWidget',action:'addNewsitem')}',
					{ title: title, content: content },
					function() {
						newsWriterWidget_show_refresh();
						panel_unload();
					}
				);
			}
			function newsWriterWidget_edit_onSubmit(id, title, content) {
				postWithFlash(
					'${g.createLink(controller:'newsWriterWidget',action:'updateNewsitem')}',
					{ id:id, title: title, content: content },
					function() {
						newsWriterWidget_show_refresh();
						panel_unload();
					}
				);
			}
			function adviceWriterWidget_form_onSubmit(title, content, dateStart, dateEnd, priority) {
				postWithFlash(
					'${g.createLink(controller:'adviceWriterWidget',action:'addAdviceitem')}',
					{
						title: title,
						content: content,
						dateStart: dateStart,
						dateEnd: dateEnd,
						priority: priority
					},
					function() {
						adviceWriterWidget_show_refresh();
						panel_unload();
					}
				);
			}
			function adviceWriterWidget_edit_onSubmit(id, title, content, dateStart, dateEnd, priority) {
				postWithFlash(
					'${g.createLink(controller:'adviceWriterWidget',action:'updateAdviceitem')}',
					{
						id: id,
						title: title,
						content: content,
						dateStart: dateStart,
						dateEnd: dateEnd,
						priority: priority
					},
					function() {
						adviceWriterWidget_show_refresh();
						panel_unload();
					}
				);
			}
			function learningContentWriterWidget_form_onSubmit(title, content) {
				postWithFlash(
					'${g.createLink(controller:'learningContentWriterWidget',action:'addLearningContent')}',
					{ title: title, content: content },
					function() {
						learningContentWriterWidget_show_refresh();
						panel_unload();
					}
				);
			}
			function learningContentWriterWidget_edit_onSubmit(id, title, content) {
				postWithFlash(
					'${g.createLink(controller:'learningContentWriterWidget',action:'updateLearningContent')}',
					{ id:id, title: title, content: content },
					function() {
						learningContentWriterWidget_show_refresh();
						panel_unload();
					}
				);
			}
			function adviceReaderWidget_readMore_onRemove(id) {
				if(confirm("Ali res želite izbrisati izbrano priporočilo?")) {
					postWithFlash(
						'${g.createLink(controller:'adviceWriterWidget',action:'removeAdviceitem')}',
						{ id: id },
						function() {
							adviceWriterWidget_show_refresh();
							panel_unload();
						}
					);
				}
			}
			function adviceReaderWidget_readMore_onEdit(adviceitem_id) {
				panel_load(
					"adviceWriterWidget",
					"edit",
					{ id: adviceitem_id }
				);
			}
			function newsReaderWidget_readMore_onRemove(id) {
				if(confirm("Ali res želite izbrisati izbrano novico?")) {
					postWithFlash(
						'${g.createLink(controller:'newsWriterWidget',action:'removeNewsitem')}',
						{ id: id },
						function() {
							newsWriterWidget_show_refresh();
							panel_unload();
						}
					);
				}
			}
			function newsReaderWidget_readMore_onEdit(newsitem_id) {
				panel_load(
					"newsWriterWidget",
					"edit",
					{ id: newsitem_id }
				);
			}
			function learningContentReaderWidget_readMore_onRemove(id) {
				if(confirm("Ali res želite izbrisati izbrano vsebino?")) {
					postWithFlash(
						'${g.createLink(controller:'learningContentWriterWidget',action:'removeLearningContent')}',
						{ id: id },
						function() {
							learningContentWriterWidget_show_refresh();
							panel_unload();
						}
					);
				}
			}
			function learningContentReaderWidget_readMore_onEdit(item_id) {
				panel_load(
					"learningContentWriterWidget",
					"edit",
					{ id: item_id }
				);
			}
		</g:javascript>

	</eo:panelFrame>
	
</body>
</html>
