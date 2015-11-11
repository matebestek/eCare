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
		Vprašanja.
	</eo:panelHeadInfo>
	<h1>Pregled ne-prevzetih vprašanj</h1>
</eo:panelHead>
<eo:panelContent>

	<p>Pacienti Vaše institucije so postavili naslednja vprašanja, na katera niso prejeli še nobenega
	odgovora.</p>
	
	<p>Prosimo, <b>prevzemite in odgovorite</b> na naslednja vprašanja ali poskrbite za to, da jih
	bo prevzel in nanje odgovoril kdo drugi iz zdravstvenega osebja Vaše zdravstvene ustanove.</p>

	<table class="layout-sortable-table">
	
		<thead>
			<tr>
				<th class="layout-sortable-table-header">Avtor</th>
				<th class="layout-sortable-table-header">Datum</th>
				<th class="layout-sortable-table-header">Tema</th>
				<th class="layout-sortable-table-header">&nbsp;</th>
			</tr>
		</thead>
		
		<tbody>
			<g:each in="${tickets}" var="ticket">
				<tr>
					<td><a href="#" onclick="showPerson('${ticket.authorId}');return false;">${ticket.authorFullName}</a></td>
					<td>${ticket.dateAdded.format("dd.MM.yyyy")}</td>
					<td>${ticket.topic}</td>
					<td><button onclick="panel_load('ticketsWidget','showPendingConversation',{id:${ticket.id}});return false;">Prikaži</button></td>
				</tr>
			</g:each>
		</tbody>
	
	</table>

</eo:panelContent>
