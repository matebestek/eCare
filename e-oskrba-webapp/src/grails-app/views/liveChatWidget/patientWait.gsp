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
<eo:panelHead>
	<eo:panelHeadInfo>
		Pogovor v živo. Zahtevam...
	</eo:panelHeadInfo>
	<h1>Pogovor v živo</h1>
</eo:panelHead>
<eo:panelContent>

	<center>
		<p><b>Zahtevali ste pogovor v živo</b><br />Prosimo, počakajte...</p>
		<p><img src="${g.resource(dir:'images',file:'pleaseWaitBar.gif')}" /></p>
		<p>Vaša zahteva sedaj čaka, da jo prevzame nekdo od oskrbovalcev ali zdravnikov. To lahko traja nekaj minut, zato Vas vljudno prosimo, da počakate.</p>
		<p>Če Vaše zahteve v daljšem času nihče ne prevzame, lahko zahtevo <a href="#" onclick="liveChatWidget_patientWait_auto_cancel();return false;">prekličete</a> in raje postavite novo vprašanje.</p>
	</center>
	<g:javascript>
		// Start main loop
		liveChatWidget_patientWait_auto_init();
		
		var liveChatWidget_patientWait_auto_stopMainLoop;
		var liveChatWidget_patientWait_auto_pingFailCount = 0;
		
		// init
		function liveChatWidget_patientWait_auto_init() {
			liveChatWidget_patientWait_auto_stopMainLoop = false;
			panel_resetCookie();
			liveChatWidget_patientWait_auto_mainLoop();
		}
		
		// Main loop
		function liveChatWidget_patientWait_auto_mainLoop() {
			if(liveChatWidget_patientWait_auto_stopMainLoop != true) {
				// Send ping
				getJSON("${g.createLink(controller:'liveChatWidget',action:'sendPing')}", {}, function(json) {
					if(!json.success) {
						liveChatWidget_patientWait_auto_pingFailCount++;
					}
					if(liveChatWidget_patientWait_auto_pingFailCount == 3) {
						alert("Opozorilo: Zaradi težav z internetnim dostopom pogovora v živo morda ne bo možno vzpostaviti");
					}
				});
				// Check if chat session assigned
				var chatSessionAssigned = false;
				getJSON("${g.createLink(controller:'liveChatWidget',action:'checkIfAssigned')}", {id:${chatSessionId}}, function(json) {
					chatSessionAssigned = json.assigned;
					if(chatSessionAssigned) {
						// chat session assigned
						liveChatWidget_patientWait_auto_stopMainLoop = true;
						liveChatWidget_patientWait_onAccept(${chatSessionId});
					} else {
						// keep trying - new loop iteration
						setTimeout("liveChatWidget_patientWait_auto_mainLoop()",3000);
					}
				});
			}
		}
		
		function liveChatWidget_patientWait_auto_cancel() {
			liveChatWidget_patientWait_auto_stopMainLoop = true;
			getJSON("${g.createLink(controller:'liveChatWidget',action:'cancelChatSession')}", {id:${chatSessionId}}, function(json) {
				if(!json.success) {
					alert("Opozorilo: Pri preklicevanju zahteve za pogovor je prišlo do napake.");
				}
			});
			liveChatWidget_patientWait_onCancel();
		}
	</g:javascript>

</eo:panelContent>
