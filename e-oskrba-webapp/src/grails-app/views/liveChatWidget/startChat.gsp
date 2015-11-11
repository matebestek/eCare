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
		Pogovor v živo.
	</eo:panelHeadInfo>
	<h1>Pogovor v živo z osebo <eo:personLink id="${chatOtherId}">${chatOtherFullName}</eo:personLink></h1>
	<eo:adminOptions>
		<button onclick="liveChatWidget_startChat_auto_end();return false;">Končaj pogovor</button>
	</eo:adminOptions>
</eo:panelHead>
<eo:panelContent>

	<div id="liveChatWidget_startChat_chatContainer">
		<p>
			<span class="liveChatWidget_startChat_output_name"></span>
			<span class="liveChatWidget_startChat_output_status">${startingMessage}</span>
		</p>
	</div>
	<div id="liveChatWidget_startChat_formContainer">
		<g:textArea name="liveChatWidget_startChat_input_text"></g:textArea>
		<button id="liveChatWidget_startChat_input_endBtn" onclick="liveChatWidget_startChat_auto_end();return false;">Končaj pogovor</button>
		<button id="liveChatWidget_startChat_input_submitBtn" onclick="liveChatWidget_startChat_auto_sendMsg();return false;">Pošlji</button>
	</div>
	
	<g:javascript>
		
		liveChatWidget_startChat_auto_init();
		
		function liveChatWidget_startChat_auto_init() {
			panel_resetCookie();
			// layout
			$('#liveChatWidget_startChat_input_text').keypress(function(e){
				if(e.which == 13){
					e.preventDefault();
					liveChatWidget_startChat_auto_sendMsg();
					return false;
				}
			});
			liveChatWidget_startChat_auto_layout_textarea = $("#liveChatWidget_startChat_input_text");
			liveChatWidget_startChat_auto_layout_chatContainer = $("#liveChatWidget_startChat_chatContainer");
			liveChatWidget_startChat_auto_layoutLoop();
			// messages
			liveChatWidget_startChat_auto_lastMsgTimestamp = "0";
			liveChatWidget_startChat_auto_msgsLoop();
			// pinging
			liveChatWidget_startChat_auto_pingLoop();
			// Sound
			//$("#beep")[0].play();
		}
		
		var liveChatWidget_startChat_auto_layout_textarea;
		var liveChatWidget_startChat_auto_layout_chatContainer;
		function liveChatWidget_startChat_auto_layoutLoop() {
			// liveChatWidget_startChat_auto_layout_textarea.width(liveChatWidget_startChat_auto_layout_chatContainer.width() - 10);
			liveChatWidget_startChat_auto_layout_chatContainer.height($("#layout-mainframe").height() - 250);
			setTimeout("liveChatWidget_startChat_auto_layoutLoop()",1000);
		}
		
		var liveChatWidget_startChat_auto_lastMsgTimestamp;
		function liveChatWidget_startChat_auto_msgsLoop() {
			getJSON("${g.createLink(controller:'liveChatWidget',action:'jsonGetMessages')}", {id:${chatSessionId}, after:liveChatWidget_startChat_auto_lastMsgTimestamp}, function(json) {
				for(var i = 1; i < json.msgs.length; i++) {
					var msg = json.msgs[i];
					liveChatWidget_startChat_auto_layout_chatContainer.append("<p><span class=\"liveChatWidget_startChat_output_name\">" + msg.authorFullName + "</span><span class=\"liveChatWidget_startChat_output_text\">" + msg.content + "</span></p>");
					liveChatWidget_startChat_auto_lastMsgTimestamp = msg.timestamp;
					liveChatWidget_startChat_auto_layout_chatContainer.scrollTo('max',{
						duration:200
					});
					//$("#beep")[0].play();
				}
			    setTimeout("liveChatWidget_startChat_auto_msgsLoop()",500);
			});
		}
		
		function liveChatWidget_startChat_auto_sendMsg() {
			var message = liveChatWidget_startChat_auto_layout_textarea.val();
			$.ajax({
				type: 'POST',
				url: "${g.createLink(controller:'liveChatWidget',action:'postMessage')}",
			  	data: {id:${chatSessionId}, content: message, timestamp: timestamp() },
			  	success: function(json) {
					liveChatWidget_startChat_auto_layout_textarea.val("");
			  	}
			});
		}
		
		function liveChatWidget_startChat_auto_pingLoop() {
			// send ping
			getJSON("${g.createLink(controller:'liveChatWidget',action:'sendPing')}", {}, function(json) { });
			// check if conversation ended
			var chatSessionEnded;
			getJSON("${g.createLink(controller:'liveChatWidget',action:'checkIfEnded')}", {id:${chatSessionId}}, function(json) {
				chatSessionEnded = json.assigned;
				if(chatSessionEnded) {
					// chat session ended
					liveChatWidget_startChat_auto_layout_chatContainer.append("<p><span class=\"liveChatWidget_startChat_output_name\"></span><span class=\"liveChatWidget_startChat_output_status\">Vaš sogovornik je zapustil pogovor. <a href=\"#\" onclick=\"liveChatWidget_startChat_onEndChat();return false;\">Kliknite tukaj za nadeljevanje.</a></span></p>");
					$("#liveChatWidget_startChat_formContainer").fadeOut();
					liveChatWidget_startChat_auto_layout_chatContainer.scrollTo('max',{
						duration:200
					});
				} else {
					// keep checking - new loop iteration
					setTimeout("liveChatWidget_startChat_auto_pingLoop()",3000);
				}
			});
		}
		
		function liveChatWidget_startChat_auto_end() {
			getJSON("${g.createLink(controller:'liveChatWidget',action:'endChatSession')}", {id: ${chatSessionId}}, function(json) {
				liveChatWidget_startChat_onEndChat();
			});
		}
		
	</g:javascript>

</eo:panelContent>
