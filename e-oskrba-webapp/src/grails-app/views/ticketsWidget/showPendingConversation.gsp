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
		Vprašanja. <b>Čakajoče.</b>
	</eo:panelHeadInfo>
	<h1>${ticket.topic}</h1>
</eo:panelHead>
<eo:panelContent>

	<g:each in="${entries}" var="entry">
		<div>
			<p><span class="date">Napisal-a <eo:personLink id="${entry.authorId}">${entry.authorFullName}</eo:personLink>, dne <eo:niceDate value="${entry.dateAdded}" scope="min" />:</span></p>
			<div style="margin-left:50px;">${entry.content}</div>
		</div>
	</g:each>

	<eo:hr />

	<div id="ticketsWidget_showPendingConversation_optionsHolder">
		<button onclick="ticketsWidget_showPendingConversation_onReplyClick();">Odgovori</button> <g:if test="${session.role == 'caremanager'}"><button onclick="ticketsWidget_showPendingConversation_onAssignToDoctor(${ticket.id});">Dodeli zdravniku</button></g:if> <button onclick="ticketsWidget_showPendingConversation_onCloseTicket(${ticket.id});">Prisilno zapri vprašanje</button> 
	</div>
	
	<div id="ticketsWidget_showPendingConversation_replyFormHolder" style="display:none;">
		<eo:tfjs lwidth="100px" onSubmit="ticketsWidget_showPendingConversation_replyFormOnSubmit">
			<eo:tff label="Odgovor:">
				<eo:ckeditorConfig />
				<ckeditor:editor name="ticketsWidget_showPendingConversation_input_content" removeInstance="true"></ckeditor:editor>
			</eo:tff>
			<eo:tff label="&nbsp;">
				<g:submitButton name="ticketsWidget_showPendingConversation_input_submit" value="Potrdi" />
			</eo:tff>
			<g:hiddenField name="ticketsWidget_showPendingConversation_input_id" value="${ticket.id}" />
		</eo:tfjs>
	</div>
	
	<g:javascript>
	function ticketsWidget_showPendingConversation_onReplyClick() {
		$('#ticketsWidget_showPendingConversation_optionsHolder').hide();
		$('#ticketsWidget_showPendingConversation_replyFormHolder').show();
	}
	function ticketsWidget_showPendingConversation_replyFormOnSubmit() {
		ticketsWidget_showPendingConversation_onSubmitReply(
			$('#ticketsWidget_showPendingConversation_input_id').val(),
			$('#ticketsWidget_showPendingConversation_input_content').val()
		);
	}
	function ticketsWidget_showPendingConversation_onCloseTicket(id) {
		var reason = prompt("Prosimo, navedite razlog prisilnega zapiranja vprašanja:","");
		ticketsWidget_showPendingConversation_onForceClose(
			id,
			reason
		);
	}
	</g:javascript>

</eo:panelContent>
