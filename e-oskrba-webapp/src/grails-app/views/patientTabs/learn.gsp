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
<%! import si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM %>
<html>
<head>
	<title>Izobraževalne vsebine</title>
	<meta name="layout" content="main" />
</head>
<body>

	<eo:widgetFrame>
	
		<eo:staticWidget title="O izobraževalnih vsebinah" id="patientTabs_news_intro">
			<p>Tu lahko dostopate do vseh objavljenih izobraževalnih vsebin.</p>
		</eo:staticWidget>

		<g:if test="${session.employeeType == EMPLOYEE_TYPE_ENUM.HUJSANJE.eval()}">
		<eo:widget title="Vsebine po korakih" load="learningContentReaderWidget" action="showSteps">
			<g:javascript>
				function learningContentReaderWidget_showSteps_onPage(page_num) {
					learningContentReaderWidget_showSteps_reload({pageStep:page_num});
				}
				function learningContentReaderWidget_showSteps_onReadMore(stepNo) {
					panel_load(
						"learningContentReaderWidget",
						"readMoreSteps",
						{ stepNo: stepNo }
					);
				}
			</g:javascript>
		</eo:widget>
		</g:if>
				
		<eo:widget title="${session.employeeType == EMPLOYEE_TYPE_ENUM.HUJSANJE.eval() ? "Ostale izobraževalne vsebine" : "Izobraževalne vsebine"}" load="learningContentReaderWidget" action="show">
			<g:javascript>
				function learningContentReaderWidget_show_onPage(page_num) {
					learningContentReaderWidget_show_reload({page:page_num});
				}
				function learningContentReaderWidget_show_onReadMore(item_id, title) {
					panel_load(
						"learningContentReaderWidget",
						"readMore",
						{ id: item_id }
					);
				}
			</g:javascript>
		</eo:widget>
		
	</eo:widgetFrame>
	
	<eo:panelFrame />
	
	<g:if test="${params.id != null}">
		<g:javascript>
			$(document).ready( function() {
				panel_load(
					"learningContentReaderWidget",
					"readMore",
					{ id: ${params.id} }
				);
			});
		</g:javascript>
	</g:if>
	
</body>
</html>
