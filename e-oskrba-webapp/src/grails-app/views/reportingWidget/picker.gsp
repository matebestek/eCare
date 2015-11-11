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
<eo:tfjs lwidth="60px" onSubmit="reportingWidget_picker_input_submit">
	<eo:tff label="Pacient (up. ime)">
		<g:textField name="reportingWidget_picker_input_who" value="${params.who}" />
		<span class="ac-spinner"><img src="${g.resource(dir:'images',file:'spinner.gif')}" /> <i>Iščem...</i></span>
	</eo:tff>
	
	<div id="layout-show-no-user-warning">	
	</div>
	
	<eo:tff label="Meritve">
		<table>
			<g:if test="${session.employeeType == 'A' }">
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="PEF" /></td><td style="padding:3px;"> PEF</td></tr>
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="ACT" /></td><td style="padding:3px;"> ACT</td></tr>
			</g:if>
			<g:elseif test="${session.employeeType == 'D' }">
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="KRVNI_SLADKOR" /></td><td style="padding:3px;"> Krvni sladkor</td></tr>
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="TELESNA_MASA" /></td><td style="padding:3px;"> Telesna teža</td></tr>
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="EDIABETES_KRVNI_TLAK" /></td><td style="padding:3px;"> Krvni tlak</td></tr>
			</g:elseif>
			<g:elseif test="${session.employeeType == 'S' }">
				TODO
			</g:elseif>
			<g:elseif test="${session.employeeType == 'H' }">			
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="TELESNA_MASA_EHUJSANJE" onclick="reportingWidget_radioSelect(this,1);" /></td><td style="padding:3px;"> Telesna teža</td></tr>
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="OBSEG_PASU_EHUJSANJE" onclick="reportingWidget_radioSelect(this,1);" /></td><td style="padding:3px;"> Obseg pasu</td></tr>
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="INDEKS_TELESNE_MASE_EHUJSANJE" onclick="reportingWidget_radioSelect(this,1);" /></td><td style="padding:3px;"> Indeks telesne mase</td></tr>
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="KOLICINA_MINUT_GIBALNE_AKTVNOSTI" onclick="reportingWidget_radioSelect(this,1);" /></td><td style="padding:3px;"> Gibalna aktivnost</td></tr>
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="POROCILO_PO_KORAKIH" onclick="reportingWidget_radioSelect(this,0);" /></td><td style="padding:3px;"> Poročila po korakih</td></tr>
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="ENERGIJSKI_DIAGRAMI" onclick="reportingWidget_radioSelect(this,1);" /></td><td style="padding:3px;"> Energijski diagrami</td></tr>
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="ZIVILSKI_DIAGRAMI" onclick="reportingWidget_radioSelect(this,1);" /></td><td style="padding:3px;"> Živilski diagrami</td></tr>
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="DELOVNI_LIST_PO_KORAKIH" onclick="reportingWidget_radioSelect(this,2);" /></td><td style="padding:3px;"> Delovni listi</td></tr>
			</g:elseif>
			<g:elseif test="${session.employeeType == 'P' }">	
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="TELESNA_MASA_ESPORT" onclick="reportingWidget_radioSelect(this,1);" /></td><td style="padding:3px;"> Telesna teža<br />
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="GIBALNA_AKTIVNOST_ESPORT" onclick="reportingWidget_radioSelect(this,1);" /></td><td style="padding:3px;"> Gibalna aktivnost - trajanje</td></tr>
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="PORABLJENE_PKCAL" onclick="reportingWidget_radioSelect(this,1);" /></td><td style="padding:3px;"> Gibalna aktivnost - porabljena energija</td></tr>
				<tr><td><g:radio name="reportingWidget_picker_input_what" value="RAZDALJA_ESPORT" onclick="reportingWidget_radioSelect(this,1);" /></td><td style="padding:3px;"> Gibalna aktivnost - opravljena razdalja</td></tr>
			</g:elseif>
		</table>
	</eo:tff>
</eo:tfjs>
<eo:tfjs id="reportingWidget_picker_form" lwidth="60px" onSubmit="reportingWidget_picker_input_submit()">
	<eo:tff label="Od">
		<g:datePicker name="reportingWidget_picker_input_from" precision="month" years="${yearRange}"  />
	</eo:tff>
	<eo:tff label="Od">
		<g:datePicker name="reportingWidget_picker_input_till" precision="month" years="${yearRange}" />
	</eo:tff>
	<eo:tff label="&nbsp;">
		<button id="reportingWidget_picker_input_submitBtn" onclick="reportingWidget_picker_input_submit();return false;">Prikaži</button>
	</eo:tff>
</eo:tfjs>

<form id="reportingWidget_reportList_form" action="javascript:reportingWidget_reportList_input_submit()">
<div id="layout-show-reports">
</div>
</form>
			

<g:javascript>
	$(document).ready(function() {
		$("#reportingWidget_picker_input_who").autocomplete({
			source: "<g:createLink controller="patientListAjax" action="listDoctorsPatients"/>",
			minLength: 0,
			select: function( event, ui ) {
				event.preventDefault();
				if(ui.item.id == "all") {
					$("#reportingWidget_picker_input_who").autocomplete("search","");
				} else {
					$("#reportingWidget_picker_input_who").val(ui.item.id);
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
	function reportingWidget_picker_input_submit() {
	
		//var what = $("#reportingWidget_picker_input_what:checked").val(); //ne dela v IE8
		var what = $('input[@name="reportingWidget_picker_input_what"]:checked').val()
		var who = $("#reportingWidget_picker_input_who").val();
		
		if (who == null || who == '') {
			$("#layout-show-no-user-warning").html("<p style=\"color:FireBrick;text-align:left\">V spodnje polje niste vnesli uporabniškega imena pacienta!</p>");
			return false;		
		}
		else {
			$("#layout-show-no-user-warning").html("");
		}
		
		if (what == 'POROCILO_PO_KORAKIH' || what == 'DELOVNI_LIST_PO_KORAKIH') {
					$.ajax({
						type: 'POST',
						cache: false,
						url: "../reportingWidget/list",
						data: {what: what, who : who},
						success: function(data, textStatus, jqXHR) {
							$("#layout-show-reports").html(data);
						}
					});				
			return true;
		}
		
		reportingWidget_picker_onPick(
			who,
			what,
			parseInt($("#reportingWidget_picker_input_from_month").val()),
			parseInt($("#reportingWidget_picker_input_from_year").val()),
			parseInt($("#reportingWidget_picker_input_till_month").val()),
			parseInt($("#reportingWidget_picker_input_till_year").val())
		);
	}
	
		function reportingWidget_reportList_input_submit() {
		var what = $('input[@name="reportingWidget_picker_input_what"]:checked').val();
		reportingWidget_picker_onPick(
			$('input[@name="reportingWidget_picker_input_what"]:checked').val(),
			parseInt($("#reportingWidget_picker_input_from_month").val()),
			parseInt($("#reportingWidget_picker_input_from_year").val()),
			parseInt($("#reportingWidget_picker_input_till_month").val()),
			parseInt($("#reportingWidget_picker_input_till_year").val())
		);
	}

	function  reportingWidget_radioSelect(re,pfshow) {
		var ppForm = $('#reportingWidget_reportList_form').prev('form').eq(0);
		var who = $("#reportingWidget_picker_input_who").val();
		var what = $('input[@name="reportingWidget_picker_input_what"]:checked').val();
		
		if (who == null || who == '') {
			$("#layout-show-no-user-warning").html("<p style=\"color:FireBrick;text-align:left\">V spodnje polje niste vnesli uporabniškega imena pacienta!</p>");		
		}
		else {
			$("#layout-show-no-user-warning").html("");
		}
		$("#layout-show-reports").html("");
		
		if (pfshow == 1) {
			$('#opkpHistory').css("display", "none"); 
			$('#workingSheetsHistory').css("display", "none"); 
			ppForm.show();
		}
		else if (pfshow == 0) {
			ppForm.hide();
			reportingWidget_picker_input_submit();
			$('#opkpHistory').css("display", "inline"); 
			$('#workingSheetsHistory').css("display", "none");			
		}		
		else if (pfshow == 2) {
			ppForm.hide();		
			reportingWidget_picker_input_submit();
			$('#opkpHistory').css("display", "none"); 
			$('#workingSheetsHistory').css("display", "inline");
		}

	}	
	
	function reportingWidget_showReport(xmlName) {
		var userid = $('#reportingWidget_picker_input_who').attr('value');
		panel_load(
			"reportingWidget",
			"reportShow",
			{ xmlName: xmlName, userid: userid}
		);
	}
	
	function reportingWidget_showWorkingSheet(xmlName, processStep) {
		var userid = $('#reportingWidget_picker_input_who').attr('value');
		panel_load(
			"reportingWidget",
			"showWorkingSheets",
			{ xmlName: xmlName, userid: userid, processStep : processStep }
		);
	}	
</g:javascript>
