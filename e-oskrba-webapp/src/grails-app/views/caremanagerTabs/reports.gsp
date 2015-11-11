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
	<title>Poročila</title>
	<meta name="layout" content="main" />
</head>
<body>
	<eo:widgetFrame>
		<eo:staticWidget title="O poročilih" id="caremanagerTabs_reports_intro">
			<p>Tukaj si lahko pogledate pretekle meritve pacientov, jih grafično prikažete ali izvozite.</p>
		</eo:staticWidget>
		<eo:widget title="Zgodovina meritev" load="reportingWidget" action="picker" color="normal">
			<g:javascript>
				function reportingWidget_picker_onPick(who, what, fromMonth, fromYear, tillMonth, tillYear) {
						panel_load(
							"reportingWidget",
							"show",
							{ who: who, what: what, fromMonth: fromMonth, fromYear: fromYear, tillMonth: tillMonth, tillYear: tillYear }
						);

				}
				<g:if test="${params.who != null}">
					setTimeout("autoWho()",1000);
					function autoWho() {
						$("#reportingWidget_picker_input_who").val("${params.who}");
						$("#reportingWidget_picker_input_who").focus();
					}
				</g:if>
			</g:javascript>
		</eo:widget>
	</eo:widgetFrame>
	<eo:panelFrame />
</body>
</html>
