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
<div id="layout-infobox-myTasksNum" class="layout-infobox-field" onclick="sessionInfoWidget_show_tasksClick();return false;">
	<g:if test="${session.myTasksNumber > 0}">
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'nok.png')}" />
		<div class="layout-infobox-value-bad">${session.myTasksNumber}</div>
	</g:if>
	<g:else>
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img',file:'ok.png')}" />
		<div class="layout-infobox-value-good">${session.myTasksNumber}</div>
	</g:else>
	<div class="layout-infobox-what">
		<g:if test="${session.myTasksNumber == 1}">NALOGA</g:if>
		<g:elseif test="${session.myTasksNumber == 2}">NALOGI</g:elseif>
		<g:elseif test="${session.myTasksNumber == 3 || session.myTasksNumber == 4}">NALOGE</g:elseif>
		<g:else>NALOG</g:else>
	</div>
	<g:javascript>
	function sessionInfoWidget_show_tasksClick() {
		window.location.href = "${g.createLink(controller:'redirects',action:'toMyTasks')}";
	}
	</g:javascript>
</div>
