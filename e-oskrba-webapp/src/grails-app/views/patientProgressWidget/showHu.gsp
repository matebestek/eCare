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
<!-- TT -->
<div class="layout-infobox-field" onclick="patientProgressWidget_showForHujsanje_TTClick();return false;">
		
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
		function patientProgressWidget_showForHujsanje_TTClick() {
			window.location.href = "${g.createLink(controller:'redirects',action:'toReportingGeneric',id:'TELESNA_MASA_EHUJSANJE')}";
		}
	</g:javascript>
	
</div>
<!-- /TT -->

<!-- ITM -->
<div class="layout-infobox-field" onclick="patientProgressWidget_showForHujsanje_ITMClick();return false;">
		
	<g:if test="${session.markITM == null || session.risenITM == null}">
		<!-- Ni dovolj meritev -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_unknown.png')}" />
		<div class="layout-infobox-value-neutral">
			<span class="layout-infobox-value-prev">ni dovolj meritev</span>
		</div>
	</g:if>
	
	<g:else>
		<!-- Obstajata obe meritvi + izklicna -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_'+((session.risenITM==1)?'up':((session.risenITM==0)?'stable':'down'))+'_'+((session.markITM==1)?'green':((session.markITM==0)?'yellow':'red'))+'.png')}" />
		<div class="layout-infobox-value-${((session.markITM==1)?'good':((session.markITM==0)?'neutral':'bad'))}">
			${session.currITM}
			<span class="layout-infobox-value-prev"> iz ${session.prevITM}</span>
		</div>
	</g:else>
	
	<div class="layout-infobox-what">INDEKS TELESNE MASE</div>
	
	<g:javascript>
		function patientProgressWidget_showForHujsanje_ITMClick() {
			window.location.href = "${g.createLink(controller:'redirects',action:'toReportingGeneric',id:'INDEKS_TELESNE_MASE_EHUJSANJE')}";
		}
	</g:javascript>
	
</div>
<!-- /ITM -->

<!-- OP -->
<div class="layout-infobox-field" onclick="patientProgressWidget_showForHujsanje_OPClick();return false;">
		
	<g:if test="${session.markOP == null || session.risenOP == null}">
		<!-- Ni dovolj meritev -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_unknown.png')}" />
		<div class="layout-infobox-value-neutral">
			<span class="layout-infobox-value-prev">ni dovolj meritev</span>
		</div>
	</g:if>
	
	<g:else>
		<!-- Obstajata obe meritvi + izklicna -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_'+((session.risenOP==1)?'up':((session.risenOP==0)?'stable':'down'))+'_'+((session.markOP==1)?'green':((session.markOP==0)?'yellow':'red'))+'.png')}" />
		<div class="layout-infobox-value-${((session.markOP==1)?'good':((session.markOP==0)?'neutral':'bad'))}">
			${session.currOP}
			<span class="layout-infobox-value-prev"> iz ${session.prevOP}</span>
		</div>
	</g:else>
	
	<div class="layout-infobox-what">OBSEG PASU</div>
	
	<g:javascript>
		function patientProgressWidget_showForHujsanje_OPClick() {
			window.location.href = "${g.createLink(controller:'redirects',action:'toReportingGeneric',id:'OBSEG_PASU_EHUJSANJE')}";
		}
	</g:javascript>
	
</div>
<!-- /OP -->
