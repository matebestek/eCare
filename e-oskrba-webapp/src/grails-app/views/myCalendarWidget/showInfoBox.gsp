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
<div class="layout-infobox-field" onclick="myCalendarWidget_showInfoBox_onClick();return false;">
	<g:if test="${noticesCount > 0}">
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img/mi/min',file:'CalendarWithoNotice.png')}" />
		<div class="layout-infobox-value-bad">${noticesCount}</div>
	</g:if>
	<g:else>
		<img class="layout-infobox-arrow" src="${g.resource(dir:'img/mi/min',file:'Calendar-35x30.png')}" />
		<div class="layout-infobox-value-good">0</div>
	</g:else>
	<div class="layout-infobox-what">
		<g:if test="${noticesCount == 1}">OPRAVILO</g:if>
		<g:elseif test="${noticesCount == 2}">OPRAVILI</g:elseif>
		<g:elseif test="${noticesCount == 3 || noticesCount == 4}">OPRAVILA</g:elseif>
		<g:else>OPRAVIL</g:else>
		DANES
	</div>
</div>
