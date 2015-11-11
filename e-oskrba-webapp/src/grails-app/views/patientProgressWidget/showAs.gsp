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
<!-- PEF -->
<div class="layout-infobox-field" onclick="patientProgressWidget_showForAsthma_PEFClick();return false;">
		
	<g:if test="${session.markPEF == null}">
		<!-- Ni dovolj meritev -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_unknown.png')}" />
		<div class="layout-infobox-value-neutral">
			<span class="layout-infobox-value-prev">ni meritev</span>
		</div>
	</g:if>
	
	<g:elseif test="${session.prevPEF == null}">
		<!-- Obstaja samo 1 meritev + izklicna -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_unknown_'+((session.markPEF==1)?'green':((session.markPEF==0)?'yellow':'red'))+'.png')}" />
		<div class="layout-infobox-value-${((session.markPEF==1)?'good':((session.markPEF==0)?'neutral':'bad'))}">
			${session.currPEF}
			<span class="layout-infobox-value-prev"> iz ${session.firstPEF}</span>
		</div>
	</g:elseif>
	
	<g:else>
		<!-- Obstajata obe meritvi + izklicna -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_'+((session.risenPEF==1)?'up':((session.risenPEF==0)?'stable':'down'))+'_'+((session.markPEF==1)?'green':((session.markPEF==0)?'yellow':'red'))+'.png')}" />
		<div class="layout-infobox-value-${((session.markPEF==1)?'good':((session.markPEF==0)?'neutral':'bad'))}">
			${session.currPEF}
			<span class="layout-infobox-value-prev"> iz ${session.prevPEF}</span>
		</div>
	</g:else>
	
	<div class="layout-infobox-what">PEF</div>
	
	<g:javascript>
		function patientProgressWidget_showForAsthma_PEFClick() {
			window.location.href = "${g.createLink(controller:'redirects',action:'toReportingGeneric',id:'PEF')}";
		}
	</g:javascript>
	
</div>
<!-- /PEF -->

<!-- ACT -->
<div class="layout-infobox-field" onclick="patientProgressWidget_showForAsthma_ACTClick();return false;">
		
	<g:if test="${session.markACT == null || session.risenACT == null}">
		<!-- Ni dovolj meritev -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_unknown.png')}" />
		<div class="layout-infobox-value-neutral">
			<span class="layout-infobox-value-prev">ni dovolj meritev</span>
		</div>
	</g:if>
	
	<g:else>
		<!-- Obstajata obe meritvi + izklicna -->
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'arrow_'+((session.risenACT==1)?'up':((session.risenACT==0)?'stable':'down'))+'_'+((session.markACT==1)?'green':((session.markACT==0)?'yellow':'red'))+'.png')}" />
		<div class="layout-infobox-value-${((session.markACT==1)?'good':((session.markACT==0)?'neutral':'bad'))}">
			${session.currACT}
			<span class="layout-infobox-value-prev"> iz ${session.prevACT}</span>
		</div>
	</g:else>
	
	<div class="layout-infobox-what">ACT</div>
	
	<g:javascript>
		function patientProgressWidget_showForAsthma_ACTClick() {
			window.location.href = "${g.createLink(controller:'redirects',action:'toReportingGeneric',id:'ACT')}";
		}
	</g:javascript>
	
</div>
<!-- /ACT -->
