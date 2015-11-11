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
	<title>Vprašanja</title>
	<meta name="layout" content="main" />
</head>
<body>

	<eo:widgetFrame>
	
		<eo:staticWidget title="O vprašanjih" id="patientTabs_tickets_intro">
			<p>Tu si lahko ogledate svoja vprašanja ter odgovore nanje ali pa postavite novo.</p>
		</eo:staticWidget>
		
		<eo:staticWidget title="Novo vprašanje" id="patientTabs_tickets_newTicketBtn">
			<eo:elem icon="New" name="Novo vprašanje" onclick="showNewTicketForm();return false;">
				Odprite novo vprašanje
			</eo:elem>
			<g:javascript>
				function showNewTicketForm() {
					panel_load(
						'myTicketsWidget',
						'newTicketForm',
						{ }
					);
				}
			</g:javascript>
		</eo:staticWidget>

		<eo:widget title="Moja odprta vprašanja" load="myTicketsWidget" action="showOpen" params="['quantity':-1]">
			<g:javascript>
				function myTicketsWidget_showOpen_onShowConversation( ticket_id, topic ) {
					panel_load(
						'myTicketsWidget',
						'showConversation',
						{ id: ticket_id }
					);
				}
    			function myTicketsWidget_showOpen_onCloseTicket( ticket_id ) {
    				if(confirm("Ali res želite zapreti to vprašanje?")) {
	    				postWithFlash("${g.createLink(controller:'myTicketsWidget',action:'closeSubmit')}",{
	    					id: ticket_id
	    				}, function() {
	    					myTicketsWidget_showOpen_refresh();
	    					myTicketsWidget_showClosed_refresh();
	    					panel_unload();
	    				});
    				}
    			}
			</g:javascript>
		</eo:widget>
		
		<eo:widget title="Moja zaprta vprašanja" load="myTicketsWidget" action="showClosed" id="${params.id}" params="['quantity': 10]">
			<g:javascript>
				function myTicketsWidget_showClosed_onShowConversation( ticket_id, topic ) {
					panel_load(
						'myTicketsWidget',
						'showConversation',
						{ id: ticket_id }
					);
				}
			</g:javascript>		
		</eo:widget>

		<eo:widget title="Pogovor v živo" load="liveChatWidget" action="patientButton">
			<g:javascript>
				function liveChatWidget_patientButton_onStartChat() {
					panel_load(
						"liveChatWidget",
						"patientWait",
						{ }
					);
				}
			</g:javascript>
		</eo:widget>
		
		<eo:widget title="Zgodovina pogovorov v živo" load="liveChatWidget" action="patientHistory">
			<g:javascript>
				function liveChatWidget_patientHistory_onShowConversation( id ) {
					panel_load(
						"liveChatWidget",
						"showConversation",
						{ id:id }
					);
				}
				function liveChatWidget_patientHistory_onShowMore() {
					liveChatWidget_patientHistory_reload({showMore: true});
				}
			</g:javascript>
		</eo:widget>
		
	</eo:widgetFrame>
	
	<eo:panelFrame>
		<g:javascript>
			function myTicketsWidget_showConversation_onSubmitReply(ticket_id, content) {
				postWithFlash('${g.createLink(controller:'myTicketsWidget',action:'replySubmit')}',{
					id: ticket_id,
					content: content
				}, function() {
					panel_load(
						'myTicketsWidget',
						'showConversation',
						{ id: ticket_id }
					);
					myTicketsWidget_showOpen_refresh();
				});
			}
			function myTicketsWidget_showConversation_onCloseTicket(ticket_id) {
			    if(confirm("Ali res želite zapreti to vprašanje?")) {
	   				postWithFlash("${g.createLink(controller:'myTicketsWidget',action:'closeSubmit')}",{
	   					id: ticket_id
	   				}, function() {
	   					myTicketsWidget_showOpen_refresh();
	   					myTicketsWidget_showClosed_refresh();
	   					panel_unload();
	   				});
	  			}
			}
			function myTicketsWidget_newTicketForm_onSubmitNewTicket(topic, question) {
				postWithFlash('${g.createLink(controller:'myTicketsWidget',action:'newTicketSubmit')}',{
					topic: topic,
					question: question
				}, function() {
					panel_unload();
					myTicketsWidget_showOpen_refresh();
				});
			}
			function liveChatWidget_patientWait_onAccept(chatSession_id) {
				panel_load(
					'liveChatWidget',
					'startChat',
					{ id: chatSession_id }
				);
			}
			function liveChatWidget_patientWait_onCancel() {
				document.location = "${g.createLink(controller:'patientTabs',action:'tickets')}";
			}
			function liveChatWidget_startChat_onEndChat() {
				document.location = "${g.createLink(controller:'patientTabs',action:'tickets')}";
			}
		</g:javascript>
	</eo:panelFrame>
	
	<g:if test="${params.id != null}">
		<g:javascript>
			$(document).ready( function() {
				panel_load(
					"myTicketsWidget",
					"showConversation",
					{ id: ${params.id} }
				);
			});
		</g:javascript>
	</g:if>
	<g:elseif test="${params.startLiveChat != null}">
		<g:javascript>
			$(document).ready( function() {
				panel_load(
					"liveChatWidget",
					"patientWait",
					{ }
				);
			});
		</g:javascript>
	</g:elseif>
	
</body>
</html>
