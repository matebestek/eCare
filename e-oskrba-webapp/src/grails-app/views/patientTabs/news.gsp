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
<html>
<head>
	<title>Novice in priporočila</title>
	<meta name="layout" content="main" />
</head>
<body>

	<eo:widgetFrame>
	
		<eo:staticWidget title="O novicah in priporočilih" id="patientTabs_news_intro">
			<p>Tukaj si lahko ogledate novice in priporočila zdravnikov.</p>
		</eo:staticWidget>
		
		<eo:widget title="Priporočila" load="adviceReaderWidget" action="show">
			<g:javascript>
				function adviceReaderWidget_show_onReadMore(adviceitem_id, title) {
					panel_load(
						"adviceReaderWidget",
						"readMore",
						{ id: adviceitem_id }
					);
				}
			</g:javascript>
		</eo:widget>
		
		<eo:widget title="Novice" load="newsReaderWidget" action="show">
			<g:javascript>
				function newsReaderWidget_show_onReadMore(newsitem_id, title) {
					panel_load(
						"newsReaderWidget",
						"readMore",
						{ id: newsitem_id }
					);
				}
			</g:javascript>
		</eo:widget>
		
	</eo:widgetFrame>
	
	<eo:panelFrame />
	
	<g:if test="${show != null}">
		<g:if test="${show == 'newsitem'}">
			<g:javascript>
				$(document).ready( function() {
					panel_load(
						"newsReaderWidget",
						"readMore",
						{ id: ${id} }
					);
				});
			</g:javascript>
		</g:if>
		<g:elseif test="${show == 'adviceitem'}">
			<g:javascript>
				$(document).ready( function() {
					panel_load(
						"adviceReaderWidget",
						"readMore",
						{ id: ${id} }
					);
				});
			</g:javascript>
		</g:elseif>
	</g:if>
	
</body>
</html>
