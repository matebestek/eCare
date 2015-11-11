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
<p>Za pregled obiskov pacienta vpišite del pacientovega imena, priimka ali UID-ja in izberite enega izmed predlogov.</p>
<eo:tfjs lwidth="60px" onSubmit="adminWidget_listVisitsTab_submit()">
	<eo:tff label="Pacient">
		<g:textField name="adminWidget_listVisitsTab_uid" value="${params.who}" />
		<span class="ac-spinner"><img src="${g.resource(dir:'images',file:'spinner.gif')}" /> <i>Iščem...</i></span>
	</eo:tff>
	<eo:tff label="&nbsp;">
		<button id="adminWidget_listVisitsTab_submitBtn" onclick="adminWidget_listVisitsTab_submit();return false;">Potrdi</button>
	</eo:tff>
</eo:tfjs>
<g:javascript>
	$(document).ready(function() {
		$("#adminWidget_listVisitsTab_uid").autocomplete({
			source: "<g:createLink controller="patientListAjax" action="listDoctorsPatients"/>",
			minLength: 0,
			select: function( event, ui ) {
				event.preventDefault();
				if(ui.item.id == "all") {
					$("#adminWidget_listVisitsTab_uid").autocomplete("search","");
				} else {
					$("#adminWidget_listVisitsTab_uid").val(ui.item.id);
				}
			},
			search: function(event, ui) { 
			    $('.ac-spinner').show();
			},
			open: function(event, ui) {
			    $('.ac-spinner').hide();
			}
		});
	});
	function adminWidget_listVisitsTab_submit() {
		var patientUid = $("input[name='adminWidget_listVisitsTab_uid']").val();
	    adminWidget_listVisitsTab_onSubmit(patientUid);
	}
</g:javascript>
