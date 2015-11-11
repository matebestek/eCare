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
	<title>Vprašanja pacientov</title>
	<meta name="layout" content="main" />
</head>
<body>

	<eo:widgetFrame>

		<eo:staticWidget title="O vprašanjih" id="caremanagerTabs_tickets_intro">
			<p>Tu imate pregled nad vprašanji, ki ste jih prevzeli ter vprašanji, ki še čakajo na prevzem. Ko je pacient z odgovorom zadovoljen, mora vprašanje zapreti - zaprta vprašanja spodaj niso prikazana.</p>
		</eo:staticWidget>
		
		<eo:widget title="Moja odprta vprašanja" load="ticketsWidget" action="showMyOpened">
			<g:javascript>
				function ticketsWidget_showMyOpened_onShowConversation(id, title) {
					panel_load(
						"ticketsWidget",
						"showConversation",
						{id: id}
					);
				}
			</g:javascript>
		</eo:widget>
		
		<eo:widget title="Čakajoča vprašanja" load="ticketsWidget" action="showPending">
			<g:javascript>
				function ticketsWidget_showPending_onShowConversation(id, title) {
					panel_load(
						"ticketsWidget",
						"showPendingConversation",
						{id: id}
					);
				}
			</g:javascript>
		</eo:widget>
		
	
	</eo:widgetFrame>
	
	<eo:panelFrame>
		<g:javascript>
			function ticketsWidget_showPendingConversation_onForceClose(id, reason) {
				if(reason != null) {
					postWithFlash(
						'${g.createLink(controller:'ticketsWidget',action:'forceCloseTicket')}',
						{ id: id, reason: reason },
						function() {
							panel_unload();
							ticketsWidget_showPending_refresh();
						}
					);
				}
			}
			function ticketsWidget_showPendingConversation_onSubmitReply(id, content) {
				postWithFlash(
					'${g.createLink(controller:'ticketsWidget',action:'addReply')}',
					{ id: id, content: content },
					function() {
						panel_load(
							"ticketsWidget",
							"showConversation",
							{ id: id }
						);
						ticketsWidget_showPending_refresh();
						ticketsWidget_showMyOpened_refresh();
					}
				);
			}
			function ticketsWidget_showConversation_onForceClose(id, reason) {
				if(reason != null) {
					postWithFlash(
						'${g.createLink(controller:'ticketsWidget',action:'forceCloseTicket')}',
						{ id: id, reason: reason },
						function() {
							panel_unload();
							ticketsWidget_showMyOpened_refresh();
						}
					);
				}
			}
			function ticketsWidget_showConversation_onSubmitReply(id, content) {
				postWithFlash(
					'${g.createLink(controller:'ticketsWidget',action:'addReply')}',
					{ id: id, content: content },
					function() {
						panel_load(
							"ticketsWidget",
							"showConversation",
							{ id: id }
						);
						ticketsWidget_showMyOpened_refresh();
					}
				);
			}
			function ticketsWidget_showConversation_onAssignToDoctor(id) {
				var docUid = "";
				docUid = prompt("Prosimo, vpišite UID zdravnika, kateremu želite vprašanje dodeliti:","${session.ldap.patientsDoctorAtt}");
				getJSON(
					"../showPerson/doctorExistsJSON",
					{ uid: docUid },
					function(jsonr) {
						if(jsonr.docExists) {
							postWithFlash(
								'${g.createLink(controller:'ticketsWidget',action:'reassignToDoctor')}',
								{ id: id, uid: docUid },
								function() {
									panel_unload();
									ticketsWidget_showMyOpened_refresh();
								}
							);
						}
						else showFlashError(
							"Zdravnik ne obstaja!",
							"Vneseni UID žal ne pripada nobenemu zdravniku v sistemu."
						);
					}
				);
			}
			function ticketsWidget_showPendingConversation_onAssignToDoctor(id) {
				var docUid = "";
				docUid = prompt("Prosimo, vpišite UID zdravnika, kateremu želite vprašanje dodeliti:","${session.ldap.patientsDoctorAtt}");
				getJSON(
					"../showPerson/doctorExistsJSON",
					{ uid: docUid },
					function(jsonr) {
						if(jsonr.docExists) {
							postWithFlash(
								'${g.createLink(controller:'ticketsWidget',action:'reassignToDoctor')}',
								{ id: id, uid: docUid },
								function() {
									panel_unload();
									ticketsWidget_showMyOpened_refresh();
								}
							);
						}
						else showFlashError(
							"Zdravnik ne obstaja!",
							"Vneseni UID žal ne pripada nobenemu zdravniku v sistemu."
						);
					}
				);
			}
		</g:javascript>
	</eo:panelFrame>
	
</body>
</html>
