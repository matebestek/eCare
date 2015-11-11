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
<p>${session.role == "doctor" ? "Zdravnik" : session.role == "caremanager" ? "Oskrbovalec" : "Neveljavna oseba"}</p>
<h1>${session.firstName} ${session.lastName}</h1>

<eo:hr />

<g:form id="settings_showCaremanagerAndDoctor_form" name="settings_showCaremanagerAndDoctor_form" controller="settings" action="submitCaremanagerAndDoctor"  enctype="multipart/form-data">

	<eo:t lwidth="120px;">
		<eo:tff label="osebna slika">
			<img id="layout-settings-avatar" src="${eo.avatarLink(id:session.user)}" />
		</eo:tff>
		<eo:tff label="zamenjaj sliko">
			<p>
				<input type="file" onchange="settings_showCaremanagerAndDoctor_onFileChosen();return false;" name="userPicture" id="userPicture" />
			</p>
		</eo:tff>
	</eo:t>
	
	<eo:hr />

	<eo:t lwidth="120px;">
		<eo:tff label="e-poštni naslov">
			<p>
				<g:textField name="eMailAtt" value="${session.ldap.eMailAtt}" />
			</p>
		</eo:tff>
		<eo:tff label="telefonska številka (GSM)">
			<p>
				<g:textField name="mobilePhoneAtt" value="${session.ldap.mobilePhoneAtt}" />
			</p>
		</eo:tff>
	</eo:t>
	
	<eo:hr />
	
	<eo:t lwidth="120px;">
		<eo:tff label="novo geslo">
			<p>
				<g:passwordField name="userPassword" onkeyup="settings_showCaremanagerAndDoctor_onUserPasswordChange();return false;" />
			</p>
		</eo:tff>
	</eo:t>
	
	<div id="settings_showCaremanagerAndDoctor_form_passwordMore" style="display:none;">
		<eo:t lwidth="120px;">
			<eo:tff label="novo geslo  &nbsp;(ponovno)">
				<p>
					<g:passwordField name="userPasswordCheck" />
				</p>
			</eo:tff>
			<eo:tff label="staro geslo">
				<p>
					<g:passwordField name="userPasswordOld" />
				</p>
			</eo:tff>
		</eo:t>
	</div>
	<g:javascript>
		var settings_showCaremanagerAndDoctor_form_passwordMore_visible = false;
		function settings_showCaremanagerAndDoctor_onUserPasswordChange() {
			if(!settings_showCaremanagerAndDoctor_form_passwordMore_visible) {
				$("#settings_showCaremanagerAndDoctor_form_passwordMore").slideDown();
			}
		}
	</g:javascript>
	
	<eo:hr />
	
	<eo:t lwidth="120px;">
		<eo:tff label="lokacija">
			<p>
				<g:textArea name="cmdLocation">${ud.location}</g:textArea>
			</p>
		</eo:tff>
		<eo:tff label="delovne ure">
			<p>
				<g:textArea name="cmdWorkingHours">${ud.workingHours}</g:textArea>
			</p>
		</eo:tff>
		<eo:tff label="dodatne informacije">
			<p>
				<g:textArea name="cmdAdditional">${ud.additional}</g:textArea>
			</p>
		</eo:tff>
	</eo:t>
	
	<eo:hr />
	
	<g:submitButton name="settings_showCaremanagerAndDoctor_submit" value="shrani spremembe" />
	
</g:form>

<g:javascript>
	function settings_showCaremanagerAndDoctor_onFileChosen() {
		document.forms["settings_showCaremanagerAndDoctor_form"].submit();
	}
</g:javascript>