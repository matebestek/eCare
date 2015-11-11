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
	<title>Pogovor v živo</title>
	<meta name="layout" content="main" />
</head>
<body>

	<eo:widgetFrame>
	
		<eo:staticWidget title="O pogovoru v živo" id="doctorTabs_patients_intro">
			<p>Če je pacient mnenja, da ima nujno vprašanje, za katerega bi potreboval odgovor čimprej, lahko zahteva pogovor v živo. Spodaj je seznam čakajočih zahtevkov, ki jih lahko prevzamete.</p>
		</eo:staticWidget>
		
		<eo:widget title="Čakajoče zahteve za pogovor" load="liveChatWidget" action="showPending">
			<g:javascript>
				var chatStarted = false;
				function liveChatWidget_showPending_onAccept(chatSession_id) {
					panel_load(
						'liveChatWidget',
						'startChat',
						{ id: chatSession_id }
					);
					chatStarted = true;
					liveChatWidget_showPending_reload({block: chatStarted});
				}
				function liveChatWidget_showPending_onTick() {
					liveChatWidget_showPending_reload({block: chatStarted});
				}
			</g:javascript>
		</eo:widget>
		
		<eo:widget title="Zgodovina pogovorov v živo" load="liveChatWidget" action="history">
			<g:javascript>
				function liveChatWidget_history_onShowConversation( id ) {
					panel_load(
						"liveChatWidget",
						"showConversation",
						{ id:id }
					);
				}
				function liveChatWidget_history_onShowMore() {
					liveChatWidget_history_reload({showMore: true});
				}
			</g:javascript>
		</eo:widget>
	
	</eo:widgetFrame>
	
	<eo:panelFrame>
		<g:javascript>
			function liveChatWidget_startChat_onEndChat() {
				document.location = "${g.createLink(controller:'doctorTabs',action:'chat')}";
			}
		</g:javascript>	
	</eo:panelFrame>
	
</body>
</html>
