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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
<head>
	<title>e-Oskrba &raquo; Prijava v sistem</title>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
	<link href="${g.resource(dir:'css',file:'login-1.0.css')}" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${g.resource(dir:'lib',file:'jquery-1.6.1.min.js')}"></script>
</head>
<body>
	<div id="frame">
		<img src="${g.resource(dir:'img/logo/1',file:'1_200.png')}" />
		<eo:hr />
		<p><b>Za nadeljevanje je potrebna prijava.</b></p>
		<p>Prosimo, vpišite svoje uporabniško ime in geslo.</p>
		<br />
		
		<g:if test="${params.wrong}">
			<p style="color:#c00000;font-style:italic;">Vnešena kombinacija uporabniškega imena in gesla ni pravilna. Prosimo, poskusite ponovno.</p>
			<br />
		</g:if>
		
		<eo:tf lwidth="150px" controller="login" action="loginDo">
			<eo:tff label="uporabniško ime">
				<g:textField name="username" style="width:300px;padding:3px;" />
			</eo:tff>
			<eo:tff label="geslo">
				<g:passwordField name="password" style="width:300px;padding:3px;" />
			</eo:tff>
			<eo:tff label=" " >
				<g:submitButton name="submit" value="Prijavi se"/>
			</eo:tff>
		</eo:tf>
		<!--[if IE]>
			<eo:hr />
			<p style="color:#c00000;font-weight:bold;">Opozorilo!</p>
			<p>Uporabljate Internet Explorer. Čeprav gre za enega izmed podprtih brskalnikov, Vam priporočamo, da raje uporabljate enega izmed naslednjih brskalnikov:</p><br />
			<table style="width:100%;">
				<tr>
					<td style="width:33%">
						<div style="position:relative;">
							<img src="${g.resource(dir:'img',file:'chrome.png')}" />
							<p style="position:absolute;left:50px;top:5px;"><b>Google Chrome</b><br /><a href="https://www.google.com/chrome/eula.html?hl=sl">Namesti</a> | <a href="https://www.google.com/chrome?hl=sl">Več...</a></p>
						</div>
					</td>
					<td style="width:33%">
						<div style="position:relative;">
							<img src="${g.resource(dir:'img',file:'safari.png')}" />
							<p style="position:absolute;left:50px;top:5px;"><b>Apple Safari</b><br /><a href="http://support.apple.com/downloads/#safari">Namesti</a> | <a href="http://www.apple.com/safari/">Več...</a></p>
						</div>
					</td>
					<td style="width:33%">
						<div style="position:relative;">
							<img src="${g.resource(dir:'img',file:'ff.jpg')}" />
							<p style="position:absolute;left:50px;top:5px;"><b>Mozilla Firefox</b><br /><a href="http://www.mozilla.org/sl/firefox/new/">Namesti</a> | <a href="http://www.mozilla.org/sl/firefox/new/">Več...</a></p>
						</div>
					</td>
				</tr>
			</table>
			<br />
		<![endif]-->
	</div>
	<g:javascript>
		$("#username").keydown(function(event) {
			// Check if dot already enetered
			if( event.which == 32 ) {
				event.preventDefault();
			}
	
		});
	</g:javascript>
</body>
</html>
