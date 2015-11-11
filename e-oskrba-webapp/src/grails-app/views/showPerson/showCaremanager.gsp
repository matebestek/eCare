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
<img id="layout-showperson-avatar" src="${eo.avatarLink(id:personLdap.uidatt)}" />
<p>Oskrbovalec</p>
<h1>${personLdap.cnPatient}</h1>
<p><b>BID: ${personLdap.bidAtt}</b></p>
<eo:hr />
<eo:tfjs lwidth="120px;">
	<eo:tff label="e-poštni naslov">
		<p><a href="${personLdap.eMailAtt}">${personLdap.eMailAtt}</a></p>
	</eo:tff>
	<eo:tff label="telefonska številka">
		<p>${personLdap.mobilePhoneAtt}</p>
	</eo:tff>
	<eo:tff label="lokacija">
		<p>${ud.location ? ud.location.replace('\n','<br />') : "/" }</p>
	</eo:tff>
	<eo:tff label="uradne ure">
		<p>${ud.workingHours ? ud.workingHours.replace('\n','<br />') : "/" }</p>
	</eo:tff>
</eo:tfjs>
<eo:hr />
<eo:tfjs lwidth="120px;">
	<eo:tff label="dodatne informacije">
		<p>${ud.additional ? ud.additional.replace('\n','<br />') : "/" }</p>
	</eo:tff>
</eo:tfjs>
<eo:hr/>
<g:if test="${session.id != personLdap.uidatt}">
	<button onclick="$('#showPerson_showDoctor_sendMessageForm').slideDown();return false;">Pošlji sporočilo</button>
	<div id="showPerson_showDoctor_sendMessageForm" style="display:none;">
		<eo:hr />
		<eo:tf lwidth="120px" controller="mailer" action="sendPerosnalMessage">
			<g:hiddenField name="to" value="${personLdap.uidatt}" />
			<eo:tff label="sporočilo">
				<g:textArea name="content"></g:textArea>
			</eo:tff>
			<eo:tff label=" ">
				<g:submitButton name="Pošlji"/>
			</eo:tff>
		</eo:tf>
	</div>
</g:if>
