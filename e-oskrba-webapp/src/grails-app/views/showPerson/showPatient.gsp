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
<p>Pacient</p>
<h1>${personLdap.cnPatient}</h1>
<p><b>BID: ${personLdap.bidAtt}</b></p>
<eo:hr />
<eo:tfjs lwidth="120px;">
	<eo:tff label="e-poštni naslov">
		<p><a href="mailto:${personLdap.eMailAtt}">${personLdap.eMailAtt}</a></p>
	</eo:tff>
	<eo:tff label="telefonska številka">
		<p>${personLdap.mobilePhoneAtt}</p>
	</eo:tff>
</eo:tfjs>
<eo:hr />
<eo:tfjs lwidth="120px;">
	<eo:tff label="datum rojstva">
		<p>${personLdap.dateOfBirthAtt}</p>
	</eo:tff>
</eo:tfjs>
<eo:hr />
<g:if test="${session.role != 'patient'}">
	<button onclick="document.location.href='reports?w=reportingWidget_picker&who=${personLdap.uidatt}';return false;">Meritve</button>
	<button onclick="$('#showPerson_patientShow_sendMessageForm').slideDown();return false;">Pošlji sporočilo</button>
	<button onclick="$('#showPerson_patientShow_addCalendarNoticeForm').slideDown();return false;">Dodaj opravilo v koledar</button>
	<div id="showPerson_patientShow_sendMessageForm" style="display:none;">
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
	<div id="showPerson_patientShow_addCalendarNoticeForm" style="display:none;">
		<eo:hr />
		<eo:tf lwidth="120px" controller="myCalendarWidget" action="addNoticeToPatient">
			<g:hiddenField name="to" value="${personLdap.uidatt}" />
			<eo:tff label="Datum"><eo:datePicker precision="day" name="myCalendarWidget_addNoticeToPatient_date" /></eo:tff>
			<eo:tff label="Od">
				<span class="datePicker"><g:select name="myCalendarWidget_addNoticeToPatient_from_hour" noSelection="${[no:"--"]}"  from="${doubleDigitsHours}"/>:<g:select name="myCalendarWidget_addNoticeToPatient_from_minutes" noSelection="${[no:"--"]}"  from="${doubleDigitsMinutes}"/></span>
				<g:javascript>
					$(function(){
						$("#myCalendarWidget_addNoticeToPatient_from_hour").change(function(){
							$("#myCalendarWidget_addNoticeToPatient_from_minutes").val("00");
						});
					});
				</g:javascript>
			</eo:tff>
			<eo:tff label="Do">
				<span class="datePicker"><g:select name="myCalendarWidget_addNoticeToPatient_till_hour" noSelection="${[no:"--"]}"  from="${doubleDigitsHours}"/>:<g:select name="myCalendarWidget_addNoticeToPatient_till_minutes" noSelection="${[no:"--"]}"  from="${doubleDigitsMinutes}"/></span>
				<g:javascript>
					$(function(){
						$("#myCalendarWidget_addNoticeToPatient_till_hour").change(function(){
							$("#myCalendarWidget_addNoticeToPatient_till_minutes").val("00");
						});
					});
				</g:javascript>
			</eo:tff>
			<eo:tff label="Vrsta">
				<g:select name="myCalendarWidget_addNoticeToPatient_type" from="${noticeTypes}" optionKey="key" optionValue="value"/>
			</eo:tff>
			<eo:tff label="Opis"><g:textField name="myCalendarWidget_addNoticeToPatient_content" style="width:300px;" /></eo:tff>
			<eo:tff label="&nbsp;"><g:submitButton name="myCalendarWidget_addNoticeToPatient_submit" value="Dodaj" /></eo:tff>
		</eo:tf>
	</div>
</g:if>
