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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
	<title>eOskrba - tisk</title>
	<link href="${g.resource(dir:'css',file:'print-1.1.css')}" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${g.resource(dir:'lib',file:'jquery-1.6.1.min.js')}"></script>
</head>
<body>
	${content}
	<g:javascript>
		$(document).ready(function(){
			window.print();
		});
	</g:javascript>
	<g:if test="${references.size() > 0}">
		<div>
			<h3>Reference na zunanje internetne povezave</h3>
			<table style="width:100%;">
				<g:each in="${references}" var="ref">
					<tr><td>[${ref.key}]</td><td>${ref.value}</td></tr>
				</g:each>
			</table>
		</div>
	</g:if>
</body>
</html>
