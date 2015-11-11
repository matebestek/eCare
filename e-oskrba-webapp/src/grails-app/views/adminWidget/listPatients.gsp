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
		Administacija. Seznam pacientov.
	</eo:panelHeadInfo>
	<h1>Seznam vseh pacientov obeh skupin</h1>
</eo:panelHead>
<eo:panelContent>

	<br />
	<h2>Eksperimentalna skupina</h2>
	<br />

	<table class="layout-sortable-table">
		<thead>
			<tr>
				<th class="layout-sortable-table-header" width="200px"><strong>Priimek</strong></th>
				<th class="layout-sortable-table-header" width="200px"><strong>Ime</strong></th>
				<th class="layout-sortable-table-header" width="200px"><strong>UID</strong></th>
				<th class="layout-sortable-table-header" width="200px">&nbsp;</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${patientsTestGroup}" var="pat">
				<tr>			
	
					<td>${pat.lastName}</td>
					<td>${pat.firstName}</td>
					<td>${pat.uid}</td>
					<td>
						<button class="adminWidget_showVisit_submitBtn" onclick="showPerson('${pat.uid}');return false;">Ogled</button>
						<button class="adminWidget_showVisit_submitBtn" onclick="panel_load('adminWidget','asthmaEditForm',{ uid: '${pat.uid}' });return false;">Urejanje</button>
					</td>
				</tr>
			</g:each>
		</tbody>
	</table>
	<g:if test="${patientsTestGroup.size() == 0}">
		<center>
			<p>V testni skupini ni pacientov.</p>
		</center>
	</g:if>
	
	<br />
	<h2>Kontrolna skupina</h2>
	<br />
	
	<table class="layout-sortable-table">
		<thead>
			<tr>
				<th class="layout-sortable-table-header" width="200px"><strong>Priimek</strong></th>
				<th class="layout-sortable-table-header" width="200px"><strong>Ime</strong></th>
				<th class="layout-sortable-table-header" width="200px"><strong>UID</strong></th>
				<th class="layout-sortable-table-header" width="200px">&nbsp;</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${patientsControlGroup}" var="pat">
				<tr>			
	
					<td>${pat.lastName}</td>
					<td>${pat.firstName}</td>
					<td>${pat.uid}</td>
					<td>
						<button disabled="disabled" class="adminWidget_showVisit_submitBtn" onclick="return false;">Ogled</button>
						<button class="adminWidget_showVisit_submitBtn" onclick="panel_load('adminWidget','asthmaEditForm',{ uid: '${pat.uid}' });return false;">Urejanje</button>
					</td>
				</tr>
			</g:each>
		</tbody>
	</table>
	<g:if test="${patientsControlGroup.size() == 0}">
		<center>
			<p>V kontrolni skupini ni pacientov.</p>
		</center>
	</g:if>
	
</eo:panelContent>
