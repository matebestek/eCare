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
		<title>e-Oskrba &raquo; Podrobnosti o aktivnosti</title>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<meta property="og:title" content="${fbuser.me_name}: ${typen}" /> 
		<meta property="og:type" content="sport" /> 
		<meta property="og:site_name" content="eOskrba" />
		<meta property="og:description" content="${fbuser.me_name} je ${date} opravil/a ${time} minut dolgo vadbo športne panoge ${typen}" /> 
		<meta property="og:image" content="${ogimage}" />
		<meta property="og:url" content="${ogurl}" />
		<fb:metaAppId />
		<style>
			body { font-family:'lucida grande',tahoma,verdana,arial,sans-serif; color: #333; font-size: 13px; line-height: 1.38; }
			a { text-decoration:none; color:#3B5998; }
		</style>
	</head>
	<body style="margin:0px;padding:0px;background-color:#E7EBF2;">
	
		<fb:init />
		
		<div style="background-color:#3B5998;border-bottom: 1px solid #133783;width:100%;height:38px;">
			<img src="${g.resource(dir:'img/logo/1',file:'1_100_fb.png')}" style="position:relative;top:3px;left:50px;" />
		</div>
		
		<div style="background-color:white;border:1px solid #ccc;border-top:none;margin-left:50px;margin-right:50px; padding:15px;">
			<div>
				<table>
					<tr>
						<td style="vertical-align:top;"><img style="border:1px solid #A6A6A6;" src="//graph.facebook.com/${fbuser.me_id}/picture" />&nbsp;&nbsp;</td>
						<td style="vertical-align:top;">
							<b><a href="${fbuser.me_link}">${fbuser.me_name}</a></b><br />
							${come}
						</td>
					</tr>
				</table>
			</div>
			<div style="padding-left:30px;">
				<table>
					<tr>
						<td style="vertical-align:top;"><img style="border:1px solid #ccc;" src="${g.resource(dir:'img/sports/1',file:type+'.png')}" />&nbsp;&nbsp;</td>
						<td style="vertical-align:top;">
							<b><a href="#">${typen.capitalize()}</a></b><br />
							<table>
								<tr>
									<td style="color:#acacac;">Datum vadbe:</td>
									<td>${date}</td>
								</tr>
								<tr>
									<td style="color:#acacac;">Trajanje vadbe:</td>
									<td>${time} min</td>
								</tr>
								<tr>
									<td style="color:#acacac;">Intenzivnost:</td>
									<td>${inte} intenzivnost</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
				<div class="fb-like" data-send="true" data-width="450" data-show-faces="true"></div>
			</div>
		</div>
		<div style="margin-left:50px;margin-right:50px; padding:15px;padding-top:5px;font-size:11px;">
			<span style="color:#acacac;">&copy; eOskrba, eHujšanje, eŠport 2012</span>
			&nbsp;&middot;&nbsp;
			<a href="https://eoskrba.pint.upr.si">Prijava v spletno aplikacijo eOskrba</a>
		</div>
		
	</body>
</html>
