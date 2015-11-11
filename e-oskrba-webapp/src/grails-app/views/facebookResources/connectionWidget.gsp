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
<p id="facebookResources_connectionWidget_info">Povezava vašega uporabniškega računa aplikacije eOskrba z
vašim računom na omrežju Facebook omogoči avtomatsko objavo izbranih vsebin na vašem Facebook zidu.</p>

<g:if test="${isFacebookConnected}">
	<p style="font-style:italic;color:#71893F;">Vaš račun je povezan s spodnjim Facebook računom.</p>
	<table style="border:1px solid #A6A6A6;background-color:#DAEEF3;min-width:350px;">
		<tr>
			<td style="padding:3px;width:35px;"><img style="border:1px solid #A6A6A6;height:35px;width:35px;" src="//graph.facebook.com/${facebookUserData.me_id}/picture" /></td>
			<td style="display:block;padding:3px;position:relative;">
				<div>
					<a href="${facebookUserData.me_link}" style="text-decoration:none;" target="_new"><b>${facebookUserData.me_name}</b></a><br />
					<span>${facebookUserData.me_username}</span>
				</div>
				<div style="position:absolute;right:3px;top:3px;">
					<a href="#" onclick="facebookResources_connectionWidget_deleteConnection();return false;" style="text-decoration:none;color:#A6A6A6;" title="Odstrani povezavo s Facebook računom"><img src="${g.resource(dir:'img',file:'delete.png')}" /></a>
				</div>
			</td>
		</tr>
	</table>
	<g:javascript>
		function facebookResources_connectionWidget_deleteConnection() {
			getJSON(
				"../facebookResources/deleteConnectionData",
				{ },
				function(){
					facebookResources_connectionWidget_reload();
				}
			);
		}
	</g:javascript>
</g:if>
<g:else>
	<p style="font-style:italic;color:#C00000;">Vaš račun še ni povezan z nobenim Facebook računom.</p>
	<eo:elem icon="facebook" name="Ustvari povezavo" onclick="facebookResources_connectionWidget_newConnection();">s Facebook računom</eo:elem>
	<g:javascript>
		function facebookResources_connectionWidget_newConnection() {
			FB.login(function(response) {
				if (response.authResponse) {
					FB.api('/me', function(response) {
						postWithFlash(
							"../facebookResources/saveConnectionData",
							{
								id:       response.id,
								name:     response.name,
								link:     response.link,
								username: response.username
							},
							function(){
								facebookResources_connectionWidget_reload();
							}
						);
					});
			  	} else {
			    	alert("Povezava računov je bila preklicana. Prosimo, poskusite ponovno.");
			  	}
			}, {scope: 'publish_stream'});
		}
	</g:javascript>
</g:else>
