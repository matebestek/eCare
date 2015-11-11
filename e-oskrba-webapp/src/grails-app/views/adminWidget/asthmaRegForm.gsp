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
		Administacija. Nov postopek vključitve.
	</eo:panelHeadInfo>
	<h1>Obrazec za vključitev novega pacienta v sistem</h1>
</eo:panelHead>
<eo:panelContent>

	<g:form name="form-widget-form" controller="adminWidget" action="asthmaRegFormDo" method="POST">
		${form}
		<eo:hr/>
		<g:hiddenField name="redirectToController" value="${params.redirectToController}" />
		<g:hiddenField name="redirectToAction" value="${params.redirectToAction}" />
		<p>Po izpolnitvi vseh podatkov kliknite na spodnji gumb.</p>
		<g:submitButton name="adminWidget_asthmaRegForm_submit" value="Oddaj prijavni obrazec" /> &rarr;
		<br /><br />
	</g:form>
	
	<g:if test="${session.employeeType=='D'}">
		<g:javascript>
			var medsTable;
			$(document).ready(function() {
				medsTable = new Array();
				$("#adminWidget_regFormMeds_add").click(function(event) {
					event.preventDefault();
					// Collect data about new med
					var newMedGen = $("#adminWidget_regFormMeds_gen").val();
					var newMedGenDesc = $("#adminWidget_regFormMeds_gen option:selected").html();
					var newMedNmb = $("#adminWidget_regFormMeds_nmb").val();
					var newMedQnt = $("#adminWidget_regFormMeds_qnt").val();
					var newMedFrq = $("#adminWidget_regFormMeds_frq").val();
					var newMedFrqDesc = $("#adminWidget_regFormMeds_frq option:selected").html();
					var fieldsValid = true;
					if(newMedGen == "") fieldsValid = false;
					if(newMedNmb == "") fieldsValid = false;
					if(newMedQnt == "") fieldsValid = false;
					if(newMedFrq == "") fieldsValid = false;
					if(fieldsValid) {
						medsTable.push([newMedGen,newMedGenDesc,newMedNmb,newMedQnt,newMedFrq,newMedFrqDesc]);
						adminWidget_regFormMeds_refreshTable();
					} else {
						alert("POZOR!\n\nPri vnosu zdravil je treba izpolniti vsa polja (ime, število, količina, pogostost).\nProsimo, izpolnite prazna polja!");
					}
				});
			});
			function adminWidget_regFormMeds_refreshTable() {
				var newHtml = "";
				for(var i = 0; i < medsTable.length; i++) {
					var newHtmlPart = "";
					newHtmlPart += "<tr><td><p>";
					newHtmlPart += medsTable[i][1];
					newHtmlPart += "</p></td><td><p>";
					newHtmlPart += medsTable[i][2];
					newHtmlPart += "</p></td><td><p>";
					newHtmlPart += medsTable[i][3];
					newHtmlPart += "</p></td><td><p>";
					newHtmlPart += medsTable[i][5];
					newHtmlPart += "</p></td><td>";
					newHtmlPart += "<button title=\"Odstrani\" onclick=\"adminWidget_regFormMeds_removeMed(" + i + ");return false;\">X</button>";
					newHtmlPart += "<input type=\"hidden\" name=\"?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0003]?value?" + i + "\" value=\"" + medsTable[i][0] + "\" />";
					newHtmlPart += "<input type=\"hidden\" name=\"?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0006]?items[at0018]?value?" + i + "\" value=\"" + medsTable[i][2] + "\" />";
					newHtmlPart += "<input type=\"hidden\" name=\"?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0005]?items[at0008]?value?" + i + "\" value=\"" + medsTable[i][3] + "\" />";
					newHtmlPart += "<input type=\"hidden\" name=\"?items[openEHR-EHR-INSTRUCTION.terapija!eo.v1]?activities[at0002]?description[at0003]?items[openEHR-EHR-CLUSTER.zdravila!eo.v1]?items[at0005]?items[at0009]?value?" + i + "\" value=\"" + medsTable[i][4] + "\" />";
					newHtmlPart += "</td></tr>";
					newHtml += newHtmlPart;
				}
				$("#adminWidget_regFormMeds_table tr:not(#adminWidget_regFormMeds_firstRow):not(#adminWidget_regFormMeds_lastRow)").remove();
				$("#adminWidget_regFormMeds_firstRow").after(newHtml);
				// reset inner form
				$("#adminWidget_regFormMeds_gen").val($("#adminWidget_regFormMeds_gen option:first").val());
				$("#adminWidget_regFormMeds_nmb").val("");
				$("#adminWidget_regFormMeds_qnt").val("");
				$("#adminWidget_regFormMeds_frq").val($("#adminWidget_regFormMeds_frq option:first").val());
			}
			function adminWidget_regFormMeds_removeMed(medNum) {
				medsTable.splice(medNum,1);
				adminWidget_regFormMeds_refreshTable();
			}
		</g:javascript>
	</g:if>
	
	<g:if test="${session.employeeType=='P'}">
		<g:javascript>
			var eSport_indeksFunkcionalnostiGibanja_polja = [
				"?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0002]?value",
				"?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0007]?value",
				"?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0016]?value",
				"?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0017]?value",
				"?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0018]?value",
				"?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0019]?value",
				"?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0020]?value"
			];
			var eSport_indeksFunkcionalnostiGibanja_poljeZaVsoto = "?items[openEHR-EHR-OBSERVATION.funkcionalna!zmogljivost.v1]?data[at0001]?events[at0002]?data[at0003]?items[openEHR-EHR-CLUSTER.test!gibljivost.v1]?items[at0052]?items[at0051]?value"
			$(document).ready(function() {
				$('input[name="'+eSport_indeksFunkcionalnostiGibanja_poljeZaVsoto+'"]').attr("readonly","readonly");
				eSport_indeksFunkcionalnostiGibanja_refresh();
				for(var i = 0; i < eSport_indeksFunkcionalnostiGibanja_polja.length; i++) {
					var selector = 'select[name="'+eSport_indeksFunkcionalnostiGibanja_polja[i]+'"]';
					$(selector).change(function(){
						eSport_indeksFunkcionalnostiGibanja_refresh();
					});
				}
			});

			function eSport_indeksFunkcionalnostiGibanja_refresh() {
				var novaVsota = 0;
				for(var i = 0; i < eSport_indeksFunkcionalnostiGibanja_polja.length; i++) {
					var selector = 'select[name="'+eSport_indeksFunkcionalnostiGibanja_polja[i]+'"]';
					var pristejVsoti = $(selector).children(":selected").index()-1;
					if(pristejVsoti < 0) pristejVsoti = 0;
					novaVsota += pristejVsoti;
				}
				$('input[name="'+eSport_indeksFunkcionalnostiGibanja_poljeZaVsoto+'"]').val(novaVsota);
			}
		</g:javascript>
	</g:if>
	
	<g:javascript>
		
		$(document).ready(function() {
			var form_data = [ <g:each in="${formData}" var="fd">["${fd.name}","${fd.val}"],</g:each>["dummy","dummy"]];
			loading = true;
			activiti_form_fillWithData(form_data);
			loading = false;
		});
	
	</g:javascript>

</eo:panelContent>
