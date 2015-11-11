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
<%@page import="si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM"%>
<%@page import="si.pint.activiti.standalone.ldap.UserRegistryFactory"%>
<html>
<head>
	<title>
		<g:if test="${session.employeeType == si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM.ASTMA.eval()}">Astma in jaz</g:if>
		<g:elseif test="${session.employeeType == si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM.DIABETES.eval()}">Diabetes in jaz</g:elseif>
		<g:elseif test="${session.employeeType == si.pint.activiti.standalone.ldap.UserRegistryFactory.EMPLOYEE_TYPE_ENUM.HUJSANJE.eval()}">Hujšanje in jaz</g:elseif>
		<g:else>eOskrba in jaz</g:else>				
	</title>
	<meta name="layout" content="main" />
</head>
<body>

	<eo:widgetFrame>
	
		<g:if test="${session.employeeType == 'A'}">
			<eo:staticWidget title="O eAstmi" id="patientTabs_asthmaAndMe_intro">
				<p>TODO: Kratek opis aplikacije in programa.</p>
			</eo:staticWidget>
		</g:if>
		<g:elseif test="${session.employeeType == 'D'}">
			<eo:staticWidget title="O eDiabetesu" id="patientTabs_asthmaAndMe_intro">
				<p>TODO: Kratek opis aplikacije in programa.</p>
			</eo:staticWidget>
		</g:elseif>
		<g:elseif test="${session.employeeType == 'S'}">
			<eo:staticWidget title="O eShizofreniji" id="patientTabs_asthmaAndMe_intro">
				<p>TODO: Kratek opis aplikacije in programa.</p>
			</eo:staticWidget>
		</g:elseif>
		<g:elseif test="${session.employeeType == 'H'}">
			<eo:staticWidget title="O eHujšanju" id="patientTabs_asthmaAndMe_intro">
				<p>TODO: Kratek opis aplikacije in programa.</p>
			</eo:staticWidget>
		</g:elseif>
		<g:elseif test="${session.employeeType == 'P'}">
			<eo:staticWidget title="O eŠportu" id="patientTabs_asthmaAndMe_intro">
				<p>TODO: Kratek opis aplikacije in programa.</p>
			</eo:staticWidget>
		</g:elseif>
		<g:else>
			<eo:staticWidget title="O eAstmi" id="patientTabs_asthmaAndMe_intro">
				<p>TODO: Kratek opis aplikacije in programa.</p>
			</eo:staticWidget>
		</g:else>
		
		<eo:widget title="Moje naloge" load="myTasksWidget" action="show">
			<g:javascript>
				function myTasksWidget_show_onShowForm(task_id) {
					panel_load(
						"myTasksWidget",
						"form", {
							id: task_id,
							redirectToController: 'patientTabs',
							redirectToAction: 'tasks'
						}
					);
				}
				function myTasksWidget_show_onShowTask(task_id) {
					myTasksWidget_show_onShowForm(task_id);
				}
			</g:javascript>
		</eo:widget>

		<eo:widget title="Moja poročila" load="reportingWidget" action="patientPicker">
			<g:javascript>
				function reportingWidget_patientPicker_onPick(what, fromMonth, fromYear, tillMonth, tillYear) {
					panel_load(
						"reportingWidget",
						"patientShow",
						{ what: what, fromMonth: fromMonth, fromYear: fromYear, tillMonth: tillMonth, tillYear: tillYear }
					);
				}
			</g:javascript>
		</eo:widget>
		
		<g:if test="${session.employeeType == 'A' || session.employeeType == 'D'}">
			<eo:widget title="Moja zdravila" load="myMedsWidget" action="show">
				<g:javascript>
					function myMedsWidget_show_onShowAll() {
						panel_load(
							"myMedsWidget",
							"showAll",
							{}
						);
					}
				</g:javascript>
			</eo:widget>
		</g:if>
		
		<g:if test="${session.employeeType in ["D","H","P"]}">
			<eo:widget title="Moje meritve" load="myMeasurementsWidget" action="show">
				<g:javascript>
					function myMeasurementsWidget_show_onShowInputForm() {
						panel_load(
							"myMeasurementsWidget",
							"showInputForm",
							{
								redirectToController:'patientTabs', 
								redirectToAction:'asthmaAndMe'						      								
							}
						);
					}
				</g:javascript>
			</eo:widget>	
		</g:if>	
		
	</eo:widgetFrame>
	
	<eo:panelFrame />
	
	<g:if test="${params.id}">
		<g:javascript>
			$(document).ready( function() {
				panel_load(
					"myTasksWidget",
					"form", {
						id: ${params.id},
						redirectToController: 'patientTabs',
						redirectToAction: 'asthmaAndMe'
					}
				);
			});
		</g:javascript>
	</g:if>
	
</body>
</html>
