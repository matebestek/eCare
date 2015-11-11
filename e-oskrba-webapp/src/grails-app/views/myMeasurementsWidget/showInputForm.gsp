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
<eo:panelHead>
	<eo:panelHeadInfo>
		Eoskrba. Nov vnos meritev.
	</eo:panelHeadInfo>
	<h1>Obrazec za vnos novih meritev.</h1>
</eo:panelHead>
<eo:panelContent>		

	<g:form name="form-widget-form" controller="myMeasurementsWidget" action="showInputFormDo" method="POST">
		${form}
		<eo:hr/>
		<g:hiddenField name="redirectToController" value="${params.redirectToController}" />
		<g:hiddenField name="redirectToAction" value="${params.redirectToAction}" />
		
		<g:if test="${session.employeeType == 'P' || session.employeeType == 'H'}">
			<fb:publishCheckbox name="myMeasurements_showInputForm_fbpublish" enabled="${session.facebookConnected}">
				Opravljeno telesno dejavnost objavi tudi na mojem Facebook zidu.
			</fb:publishCheckbox>
		</g:if>
		
		<p>Po izpolnitvi vseh podatkov kliknite na spodnji gumb.</p>
		<g:submitButton name="myMeasurements_showInputForm_submit" value="Oddaj vnos meritev" />
		<br /><br />
		<g:javascript>
			$("#myMeasurements_showInputForm_submit").click( function() {
				panel_resetCookie();			
			});					
		</g:javascript>
	</g:form>

</eo:panelContent>
