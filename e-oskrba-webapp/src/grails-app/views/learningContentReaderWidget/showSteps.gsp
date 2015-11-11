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

<g:if test="${session.ldap.roomNumber == null || session.ldap.roomNumber.toInteger() == 0}">
	<p>Nobene vsebine za korake še ni na voljo.</p>
</g:if>
<g:else>
	<g:each in="${itemsStep}" var="item">
		<g:if test="${item.title.split().length > 1}">
			<eo:elem icon="Archive" name="${item.title}" onclick="learningContentReaderWidget_showSteps_onReadMore('${item.title.split()[1]}');return false;">
			</eo:elem>
		</g:if>
	</g:each>

	<center>
		<p>stran 
			<g:if test="${pageStep > 1}">
				<g:each in="${1..(pageStep-1)}" var="num">
					<a href="#" onclick="learningContentReaderWidget_showSteps_onPage(${num});return false;">${num}</a> 
				</g:each>
			</g:if>
			<b>${pageStep}</b> 
			<g:if test="${itemsStep.size() == itemsPerPageStep}">
				<a href="#" onclick="learningContentReaderWidget_showSteps_onPage(${pageStep + 1});return false;">Starejše &raquo;</a>
			</g:if>
		</p>
	</center>
</g:else>