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
	<eo:panelHeadInfo>Stanje. Zadnje meritve.</eo:panelHeadInfo>
	<h1>Zadnje meritve vrednosti ${params.what1} in ${params.what2}</h1>
</eo:panelHead>
<eo:panelContent>

	<br />
	<table class="layout-sortable-table">
		<thead>
			<tr>
				<th class="layout-sortable-table-header">id</th>
				<th class="layout-sortable-table-header">${params.what1}</th>
				<th class="layout-sortable-table-header" >${params.what2}</th>
				<th class="layout-sortable-table-header">datum meritve</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${data}" var="entry">
				<tr>
					<td>
						<g:if test="${entry.value.patientData != null}">
							<eo:personLink id="${entry.value.patientData.uidatt}">${entry.value.patientData.cnPatient}</eo:personLink>
						</g:if>
						<g:else>
							(neznana oseba)
						</g:else>
					</td>
					<td>${entry.value.value1 != null ? entry.value.value1 : ""}</td>
					<td>${entry.value.value2 != null ? entry.value.value2 : ""}</td>
					<td> <eo:niceDate scope="min" value="${(entry.value.timestamp2 != null ? entry.value.timestamp2 : entry.value.timestamp1)}" /></td>
				</tr>
			</g:each>
		</tbody>
	</table>

</eo:panelContent>
