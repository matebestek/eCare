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
		Administacija. Pregled obiskov.
	</eo:panelHeadInfo>
	<h1>Seznam obiskov pacienta</h1>
</eo:panelHead>
<eo:panelContent>
	<table class="layout-sortable-table">
		<thead>
			<tr>
				<th class="layout-sortable-table-header" width="200px"><strong>Datum obiska</strong></th>
				<th class="layout-sortable-table-header" width="200px"><strong>Pacient UID</strong></th>
				<th class="layout-sortable-table-header" width="200px"><strong>ID obiska</strong></th>
				<th class="layout-sortable-table-header" width="200px">&nbsp;</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${visits}" var="visit">
				<tr>			
					<td>${(visit.timestamp.replaceAll(":"," ob "))[0..18].reverse().replaceFirst("\\.",":").reverse()}</td>
					<td>${visit.patientId}</td>
					<td>${visit.id}</td>
					<td>
						<button class="adminWidget_showVisit_submitBtn" onclick="adminWidget_showVisit_submit('${visit.patientId}','${visit.visitFileName}');return false;">Odpri</button>
					</td>
				</tr>			
			</g:each>
		</tbody>
	</table>
	<g:javascript>
		function adminWidget_showVisit_submit(patientId,fileName) {
			panel_load(
				'adminWidget',
				'showVisit',
				{
					patientId: patientId,
					visitFileName: fileName
				}
			);
		}
	</g:javascript>
</eo:panelContent>
