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
	<title>Stanje pacientov</title>
	<meta name="layout" content="main" />
</head>
<body>

	<eo:widgetFrame>
	
		<eo:staticWidget title="O stanju" id="caremanagerTabs_patients_intro">
			<p>Tukaj lahko pregledujete trenutno stanje pacientov.</p>
		</eo:staticWidget>
		
		<eo:staticWidget title="Zadnje meritve" id="caremanagerTabs_patients_wip">
			<g:if test="${session.employeeType == 'A'}">
			
				<eo:elem icon="DefinedParameters" name="PEF" onclick="caremanagerTabs_patients_wip_onClickShowLastPEF();">
					Prikaz zadnjih meritev
				</eo:elem>
				
				<eo:elem icon="DefinedParameters" name="ACT" onclick="caremanagerTabs_patients_wip_onClickShowLastACT();">
					Prikaz zadnjih meritev
				</eo:elem>
			
				<g:javascript>
					function caremanagerTabs_patients_wip_onClickShowLastPEF() {
						panel_load(
							"patientStatusWidget",
							"showLast",
							{ what: "PEF" }
						);
					}
					function caremanagerTabs_patients_wip_onClickShowLastACT() {
						panel_load(
							"patientStatusWidget",
							"showLast",
							{ what: "ACT" }
						);
					}
				</g:javascript>
			</g:if>
			<g:elseif test="${session.employeeType == 'D' }">
			
				<eo:elem icon="DefinedParameters" name="Krvni sladkor" onclick="caremanagerTabs_patients_wip_onClickShowLastKrvniSladkor();">
					Prikaz zadnjih meritev
				</eo:elem>

				<eo:elem icon="DefinedParameters" name="Telesna teža" onclick="caremanagerTabs_patients_wip_onClickShowLastTelesnaMasa();">
					Prikaz zadnjih meritev
				</eo:elem>
				
				<eo:elem icon="DefinedParameters" name="Krvni tlak" onclick="caremanagerTabs_patients_wip_onClickShowLastKrvniTlak();">
					Prikaz zadnjih meritev
				</eo:elem>
						
					<g:javascript>
					function caremanagerTabs_patients_wip_onClickShowLastKrvniSladkor() {
						panel_load(
							"patientStatusWidget",
							"showLast",
							{ what: "KRVNI_SLADKOR" }
						);
					}
					function caremanagerTabs_patients_wip_onClickShowLastTelesnaMasa() {
						panel_load(
							"patientStatusWidget",
							"showLast",
							{ what: "TELESNA_MASA" }
						);
					}
					function caremanagerTabs_patients_wip_onClickShowLastKrvniTlak() {
						panel_load(
							"patientStatusWidget",
							"showLastTwin",
							{ what1: "SISTOLICNI_KRVNI_TLAK", what2: "DIASTOLICNI_KRVNI_TLAK" }
						);
					}
				</g:javascript>
			</g:elseif>
			<g:elseif test="${session.employeeType == 'H' }">
			
				<eo:elem icon="DefinedParameters" name="Telesna teža" onclick="caremanagerTabs_patients_wip_onClickShowLastTelesnaTeza();">
					Prikaz zadnjih meritev
				</eo:elem>

				<eo:elem icon="DefinedParameters" name="Obseg pasu" onclick="caremanagerTabs_patients_wip_onClickShowLastObsegPasu();">
					Prikaz zadnjih meritev
				</eo:elem>
				
				<eo:elem icon="DefinedParameters" name="Indeks telesne mase" onclick="caremanagerTabs_patients_wip_onClickShowLastKrvniTlak();">
					Prikaz zadnjih meritev
				</eo:elem>
						
					<g:javascript>
					function caremanagerTabs_patients_wip_onClickShowLastTelesnaTeza() {
						panel_load(
							"patientStatusWidget",
							"showLastTwin",
							{ what1: "TELESNA_MASA_EHUJSANJE_KORAK", what2: "TELESNA_MASA_EHUJSANJE_PV" }
						);
					}
					function caremanagerTabs_patients_wip_onClickShowLastObsegPasu() {
						panel_load(
							"patientStatusWidget",
							"showLast",
							{ what: "OBSEG_PASU_EHUJSANJE" }
						);
					}
					function caremanagerTabs_patients_wip_onClickShowLastKrvniTlak() {
						panel_load(
							"patientStatusWidget",
							"showLast",
							{ what: "INDEKS_TELESNE_MASE_EHUJSANJE" }
						);
					}
				</g:javascript>
			</g:elseif>			
			<g:elseif test="${session.employeeType == 'S' }">
			TODO
			</g:elseif>			
		</eo:staticWidget>
	
	</eo:widgetFrame>
	
	<eo:panelFrame />
	
</body>
</html>
