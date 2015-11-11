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
		Vprašanja.
	</eo:panelHeadInfo>
	<h1>Obrazec za postavljanje novega vprašanja</h1>
</eo:panelHead>
<eo:panelContent>

	<eo:tfjs lwidth="80px" onSubmit="newTicketFormSubmit">
		<eo:tff label="Tema">
			<g:textField name="myTicketsWidget_newTicketForm_input_topic" />
		</eo:tff>
		<eo:tff label="Vprašanje">
			<eo:ckeditorConfig />
			<ckeditor:editor name="myTicketsWidget_newTicketForm_input_content" removeInstance="true"></ckeditor:editor>
		</eo:tff>
		<eo:tff label="&nbsp;">
			<g:submitButton name="new-ticket-submit" value="Oddaj vprašanje" />
		</eo:tff>
		<g:javascript>
			function newTicketFormSubmit(){ myTicketsWidget_newTicketForm_onSubmitNewTicket( $('#myTicketsWidget_newTicketForm_input_topic').val(), $('#myTicketsWidget_newTicketForm_input_content').val() ); }
		</g:javascript>
	</eo:tfjs>

</eo:panelContent>