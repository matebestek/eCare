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
<!-- KS -->
<div class="layout-infobox-field" onclick="patientProgressWidget_showForDiabetes_KSClick();return false;">
		
	<g:if test="${session.markKS == null || session.risenKS == null}">
		<!-- Ni dovolj meritev -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_unknown.png')}" />
		<div class="layout-infobox-value-neutral">
			<span class="layout-infobox-value-prev">ni dovolj meritev</span>
		</div>
	</g:if>
	
	<g:else>
		<!-- Obstajata obe meritvi + izklicna -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_'+((session.risenKS==1)?'up':((session.risenKS==0)?'stable':'down'))+'_'+((session.markKS==1)?'green':((session.markKS==0)?'yellow':'red'))+'.png')}" />
		<div class="layout-infobox-value-${((session.markKS==1)?'good':((session.markKS==0)?'neutral':'bad'))}">
			${session.currKS}
			<span class="layout-infobox-value-prev"> iz ${session.prevKS}</span>
		</div>
	</g:else>
	
	<div class="layout-infobox-what">KRVNI SLADKOR</div>
	
	<g:javascript>
		function patientProgressWidget_showForDiabetes_KSClick() {
			window.location.href = "${g.createLink(controller:'redirects',action:'toReportingGeneric',id:'KRVNI_SLADKOR')}";
		}
	</g:javascript>
	
</div>
<!-- /KS -->

<!-- TT -->
<div class="layout-infobox-field" onclick="patientProgressWidget_showForDiabetes_TTClick();return false;">
		
	<g:if test="${session.markTT == null}">
		<!-- Ni dovolj meritev -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_unknown.png')}" />
		<div class="layout-infobox-value-neutral">
			<span class="layout-infobox-value-prev">ni meritev</span>
		</div>
	</g:if>
	
	<g:elseif test="${session.prevTT == null}">
		<!-- Obstaja samo 1 meritev + izklicna -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_unknown_'+((session.markTT==1)?'green':((session.markTT==0)?'yellow':'red'))+'.png')}" />
		<div class="layout-infobox-value-${((session.markTT==1)?'good':((session.markTT==0)?'neutral':'bad'))}">
			${session.currTT} kg
			<span class="layout-infobox-value-prev"> iz ${session.firstTT}</span>
		</div>
	</g:elseif>
	
	<g:else>
		<!-- Obstajata obe meritvi + izklicna -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_'+((session.risenTT==1)?'up':((session.risenTT==0)?'stable':'down'))+'_'+((session.markTT==1)?'green':((session.markTT==0)?'yellow':'red'))+'.png')}" />
		<div class="layout-infobox-value-${((session.markTT==1)?'good':((session.markTT==0)?'neutral':'bad'))}">
			${session.currTT}
			<span class="layout-infobox-value-prev"> iz ${session.prevTT}</span>
		</div>
	</g:else>
	
	<div class="layout-infobox-what">TELESNA TEŽA</div>
	
	<g:javascript>
		function patientProgressWidget_showForDiabetes_TTClick() {
			window.location.href = "${g.createLink(controller:'redirects',action:'toReportingGeneric',id:'TELESNA_MASA')}";
		}
	</g:javascript>
	
</div>
<!-- /TT -->

<!-- KP -->
<div class="layout-infobox-field" onclick="patientProgressWidget_showForDiabetes_KPClick();return false;">
		
	<g:if test="${session.markKP == null || session.risenKP == null}">
		<!-- Ni dovolj meritev -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_unknown.png')}" />
		<div class="layout-infobox-value-neutral">
			<span class="layout-infobox-value-prev">ni dovolj meritev</span>
		</div>
	</g:if>
	
	<g:else>
		<!-- Obstajata obe meritvi + izklicna -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_'+((session.risenKP==1)?'up':((session.risenKP==0)?'stable':'down'))+'_'+((session.markKP==1)?'green':((session.markKP==0)?'yellow':'red'))+'.png')}" />
		<div class="layout-infobox-value-${((session.markKP==1)?'good':((session.markKP==0)?'neutral':'bad'))}">
			${session.currKP[0]} / ${session.currKP[1]}
			<span class="layout-infobox-value-prev"></span>
		</div>
	</g:else>
	
	<div class="layout-infobox-what">KRVNI PRITISK</div>
	
	<g:javascript>
		function patientProgressWidget_showForDiabetes_KPClick() {
			window.location.href = "${g.createLink(controller:'redirects',action:'toReportingGeneric',id:'EDIABETES_KRVNI_TLAK')}";
		}
	</g:javascript>
	
</div>
<!-- /KP -->
