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
		Moja zdravila. Vsa.
	</eo:panelHeadInfo>
	<h1>Moja zdravila</h1>
</eo:panelHead>
<eo:panelContent>

	<p>V spodnji tabeli je seznam vseh zdravil, ki so Vam bila predpisana.</p>

	<g:if test="${session.employeeType == 'A'}">
		
		<table class="layout-sortable-table">
			
			<thead>
				<tr>
					<th class="layout-sortable-table-header">Generično ime</th>
					<th class="layout-sortable-table-header">Zdravilo</th>
					<th class="layout-sortable-table-header">Število pakiranj</th>
					<th class="layout-sortable-table-header">Količina (vdih/število/mg)</th>
					<th class="layout-sortable-table-header">Pogostost jemanja</th>
				</tr>
			</thead>
			<tbody>
			
				<g:if test="${myMeds.med1 != null}">
					<tr>
						<td style="font-variant:small-caps;font-weight:bold;">${myMeds.med1.genname}</td>
						<td>${myMeds.med1.name}</td>
						<td>${myMeds.med1.packnum}</td>
						<td>${myMeds.med1.qnt}</td>
						<td>${myMeds.med1.frequency}</td>
					</tr>
				</g:if>
		
				<g:if test="${myMeds.med2 != null}">
					<tr>
						<td style="font-variant:small-caps;font-weight:bold;">${myMeds.med2.genname}</td>
						<td>${myMeds.med2.name}</td>
						<td>${myMeds.med2.packnum}</td>
						<td>${myMeds.med2.qnt}</td>
						<td>${myMeds.med2.frequency}</td>
					</tr>
				</g:if>
				
				<g:if test="${myMeds.med3 != null}">
					<tr>
						<td style="font-variant:small-caps;font-weight:bold;">${myMeds.med3.genname}</td>
						<td>${myMeds.med3.name}</td>
						<td>${myMeds.med3.packnum}</td>
						<td>${myMeds.med3.qnt}</td>
						<td>${myMeds.med3.frequency}</td>
					</tr>
				</g:if>
			
			</tbody>
		
		</table>
		
		<g:if test="${myMeds.med1 == null && myMeds.med2 == null && myMeds.med3 == null}">
			<center><p>Nobeno glavno zdravilo Vam ni bilo predpisano.</p></center>
		</g:if>
		
		<eo:hr />
		
		<p>Dodatna zdravila, ki so Vam bila predpisana:</p>
		<div style="margin-left:25px;">${myMeds.otherMeds}</div>
	
	</g:if>
	<g:elseif test="${session.employeeType == 'D'}">
	
		<table class="layout-sortable-table">
		
			<thead>
				<tr>
					<th class="layout-sortable-table-header">Generično ime</th>
					<th class="layout-sortable-table-header">Število pakiranj</th>
					<th class="layout-sortable-table-header">Količina (št/mg/ml/vdih)</th>
					<th class="layout-sortable-table-header">Pogostost jemanja</th>
				</tr>
			</thead>
			
			<tbody>
				<g:each in="${myMeds}" var="med">
					<tr>
						<td style="font-variant:small-caps;font-weight:bold;">${med.genDesc}</td>
						<td>${med.nmb}</td>
						<td>${med.qnt}</td>
						<td>${med.frqDesc}</td>
					</tr>
				</g:each>
			</tbody>
		
		</table>
	
	</g:elseif>

</eo:panelContent>
