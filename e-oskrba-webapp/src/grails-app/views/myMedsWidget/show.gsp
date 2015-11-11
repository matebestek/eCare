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

<g:if test="${session.employeeType == 'A'}">
	<g:if test="${myMeds.med1 != null}">
		<eo:elem icon="Pills" name="${myMeds.med1.genname}" onclick="alert('Generično ime: \\t\\t\\t\\t${myMeds.med1.genname}\\nŠt. pakiranj: \\t\\t\\t\\t\\t${myMeds.med1.packnum}\\nKoličina (št/mg/ml/vpih): \\t\\t${myMeds.med1.qnt}\\nPogostost jemanja: \\t\\t\\t${myMeds.med1.frequency}');return false;">${myMeds.med1.frequency}</eo:elem>
	</g:if>
	<g:if test="${myMeds.med2 != null}">
		<eo:elem icon="Pills" name="${myMeds.med2.genname}" onclick="alert('Generično ime: \\t\\t\\t\\t${myMeds.med2.genname}\\nŠt. pakiranj: \\t\\t\\t\\t\\t${myMeds.med2.packnum}\\nKoličina (št/mg/ml/vpih): \\t\\t${myMeds.med2.qnt}\\nPogostost jemanja: \\t\\t\\t${myMeds.med2.frequency}');return false;">${myMeds.med2.frequency}</eo:elem>
	</g:if>
	<g:if test="${myMeds.med3 != null}">
		<eo:elem icon="Pills" name="${myMeds.med3.genname}" onclick="alert('Generično ime: \\t\\t\\t\\t${myMeds.med3.genname}\\nŠt. pakiranj: \\t\\t\\t\\t\\t${myMeds.med3.packnum}\\nKoličina (št/mg/ml/vpih): \\t\\t${myMeds.med3.qnt}\\nPogostost jemanja: \\t\\t\\t${myMeds.med3.frequency}');return false;">${myMeds.med3.frequency}</eo:elem>
	</g:if>
	<g:if test="${myMeds.med1 == null && myMeds.med2 == null && myMeds.med3 == null}">
		<p>Nobena glavna zdravila Vam niso bila predpisana. Kliknite na spodnji gumb za prikaz morebitnih dodatnih zdravil.</p>
	</g:if>
	<g:else>
		<p>Kliknite na spodnji gumb za prikaz morebitnih dodatnih zdravil.</p>
	</g:else>
</g:if>
<g:elseif test="${session.employeeType == 'D'}">
	<g:each in="${myMeds}" var="med">
	
		<eo:elem icon="Pills" name="${med.genDesc}" onclick="alert('Generično ime: \\t\\t\\t\\t${med.genDesc}\\nŠt. pakiranj: \\t\\t\\t\\t\\t${med.nmb}\\nKoličina (št/mg/ml/vpih): \\t\\t${med.qnt}\\nPogostost jemanja: \\t\\t\\t${med.frqDesc}');return false;">
			${med.frqDesc}
		</eo:elem>
	
	</g:each>
</g:elseif>

<p>
	<eo:bigButton onclick="myMedsWidget_show_onShowAll();return false;">Prikaži vse</eo:bigButton>
</p>
