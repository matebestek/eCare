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
<%! import eoskrba.webapp.utils.* %> 
	<g:if test="${params.what == 'POROCILO_PO_KORAKIH'}">
	<%	Map reports = UtilFunctions.listReports(params.who); %>
		<g:if test="${reports != null && reports.size() > 0}">
			<div id="opkpHistory" style="display:inline;">
				<table>
					<tr>
						<td><p>Spisek po korakih:</p></td>
					</tr>
					<tr>
						<td>
							<g:each in="${reports}" var="unit">
								<eo:elem icon="Note" name="${unit.value[1]}" onclick="reportingWidget_showReport('${unit.key}');">Klinite za prikaz</eo:elem>
							</g:each>
						</td>
					</tr>
				</table>
			</div>
		</g:if>
		<g:else>
			<div><table><tr><td>Za uporabnika ${params.who} ni podatkov!</td></tr></table><br/></div>
		</g:else>		
	</g:if>
	<g:elseif test="${params.what == 'DELOVNI_LIST_PO_KORAKIH'}">
	<%	Map sheets = UtilFunctions.listWorkingSheets(params.who); %>
		<g:if test="${sheets != null && sheets.size() > 0}">	
			<div id="workingSheetsHistory" style="display:inline;">
				<table>
					<tr>
						<td><p>Spisek po korakih:</p></td>
					</tr>
					<tr>
						<td>
							<g:each in="${sheets}" var="unit"> 
								<eo:elem icon="Sheet" name="${unit.value[1]}" onclick="reportingWidget_showWorkingSheet('${unit.key}', '${unit.value[0]}');">Kliknite za prikaz</eo:elem>
							</g:each>
						</td>
					</tr>			
				</table>	
			</div>
		</g:if>
		<g:else>
			<div>
				<table><tr><td>Za uporabnika ${params.who} ni podatkov!</td></tr></table>
				<br/>
			</div>
		</g:else>
	</g:elseif>
