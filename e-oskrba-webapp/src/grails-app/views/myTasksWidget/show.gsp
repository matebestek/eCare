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
<g:if test="${jsonr.data.size() == 0 && !unassignedTicketsExist && ticketsToAnswer.size() == 0}">
	<p>Trenutno Vam ni treba opraviti nobenih nalog.</p>
</g:if>
<g:else>

	<g:each in="${jsonr.data}" var="task">
		<eo:elem name="${task.name}" icon="Task" onclick="myTasksWidget_show_onShowTask(${task.id});return false;">
			Kliknite za prikaz naloge
		</eo:elem>	
	</g:each>
	
	<g:if test="${unassignedTicketsExist}">
		<eo:elem name="Obstajajo neprevzeta vprašanja" icon="Edit" onclick="panel_loadWithRedirectToTab('tickets','ticketsWidget','showPendingDetails',{});">
			Kliknite za prikaz vprašanj
		</eo:elem>
	</g:if>
	
	<g:if test="${session.role == "patient"}">
		<g:each in="${ticketsToAnswer}" var="ticket">
			<eo:elem name="Odgovorite na (ali zaprite) vprašanje" icon="Edit" onclick="panel_loadWithRedirectToTab('tickets','myTicketsWidget','showConversation',{id:${ticket.id}});">
				Kliknite za prikaz vprašanja
			</eo:elem>
		</g:each>
	</g:if>
	<g:else>
		<g:each in="${ticketsToAnswer}" var="ticket">
			<eo:elem name="Odgovorite na (ali zaprite) vprašanje" icon="Edit" onclick="panel_loadWithRedirectToTab('tickets','ticketsWidget','showConversation',{id:${ticket.id}});">
				Kliknite za prikaz vprašanja
			</eo:elem>
		</g:each>
	</g:else>

</g:else>
