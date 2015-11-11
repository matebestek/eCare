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
<div id="myCalendarWidget_details_frame">
	<div id="myCalendarWidget_details_title">
		<p>${day}. ${monthLabel} ${year}</p>
	</div>
	<g:if test="${noticesList.size() == 0 && programExercise == null}">
		<p>Za izbran datum nimate opomb.</p>
	</g:if>
	<g:else>
		<table class="layout-sortable-table" style="width:99%;">
			<thead>
				<tr>
					<th class="layout-sortable-table-header" style="width:25px;">&nbsp;</th>
					<th class="layout-sortable-table-header" style="width:50px;">Pričetek</th>
					<th class="layout-sortable-table-header" style="width:50px;">Konec</th>
					<th class="layout-sortable-table-header" style="width:75px;">Vrsta</th>
					<th class="layout-sortable-table-header">Opis</th>
					<th class="layout-sortable-table-header" style="width:25px;">&nbsp;</th>
				</tr>
			</thead>
			<tbody>
				<g:if test="${programExercise != null}">
					<tr>
						<td><img class="image-icon" src="${g.resource(dir:'img',file:'postit_s.png')}" /></td>
						<td>/</td>
						<td>/</td>
						<td>Vadba po programu</td>
						<td>Z intenzivnostjo <b>${programExercise.intensity}</b> opravite vadbo tipa <b>${programExercise.type}</b> (<b>${programExercise.subtype}</b>), ki naj traja <b>${programExercise.duration}</b> (hh:mm).</td>
						<td>&nbsp;</td>
					</tr>
				</g:if>
				<g:each in="${noticesList}" var="notice">
					<tr>
						<td><img class="image-icon" src="${g.resource(dir:'img',file:'postit.png')}" /></td>
						<g:if test="${notice.startHour == notice.endHour && notice.startMinutes == notice.endMinutes}">
							<td>/</td>
							<td>/</td>
						</g:if>
						<g:else>
							<td>${(notice.startHour<10)?("0"+notice.startHour):(notice.startHour)}:${(notice.startMinutes<10)?("0"+notice.startMinutes):(notice.startMinutes)}</td>
							<td>${(notice.endHour<10)?("0"+notice.endHour):(notice.endHour)}:${(notice.endMinutes<10)?("0"+notice.endMinutes):(notice.endMinutes)}</td>
						</g:else>
						<td>
							<g:if test="${notice.noticeType == 0}">Opravilo</g:if>
							<g:if test="${notice.noticeType == 1}">Vadba</g:if>
							<g:if test="${notice.noticeType == 2}">Prehranski napotek</g:if>
						</td>
						<td>${notice.content}</td>
						<td><a href="#" title="Odstrani" onclick="myCalendarWidget_details_onRemoveNotice(${notice.id});return false;"><img src="${g.resource(dir:'img',file:'delete.png')}" /></a></td>
					</tr>
				</g:each>
			</tbody>
		</table>
	</g:else>
	<eo:hr />
	<div id="calendar-add-notice-button-holder">
		<button onclick="showAddNoticeForm();">Dodaj opravilo</button>
	</div>
	<div id="calendar-add-notice-form-holder" style="display:none;">
		<eo:tfjs onSubmit="calendar_add_notice_form_submit" lwidth="200px">
			<eo:tff label="Datum"><eo:datePicker precision="day" name="myCalendarWidget_details_addNotice_date" value="${dayMonthYear}" /></eo:tff>
			<eo:tff label="Od">
				<span class="datePicker"><g:select name="myCalendarWidget_details_addNotice_from_hour" noSelection="${[no:"--"]}"  from="${doubleDigitsHours}"/>:<g:select name="myCalendarWidget_details_addNotice_from_minutes" noSelection="${[no:"--"]}"  from="${doubleDigitsMinutes}"/></span>
				<g:javascript>
					$(function(){
						$("#myCalendarWidget_details_addNotice_from_hour").change(function(){
							$("#myCalendarWidget_details_addNotice_from_minutes").val("00");
						});
					});
				</g:javascript>
			</eo:tff>
			<eo:tff label="Do">
				<span class="datePicker"><g:select name="myCalendarWidget_details_addNotice_till_hour" noSelection="${[no:"--"]}"  from="${doubleDigitsHours}"/>:<g:select name="myCalendarWidget_details_addNotice_till_minutes" noSelection="${[no:"--"]}"  from="${doubleDigitsMinutes}"/></span>
				<g:javascript>
					$(function(){
						$("#myCalendarWidget_details_addNotice_till_hour").change(function(){
							$("#myCalendarWidget_details_addNotice_till_minutes").val("00");
						});
					});
				</g:javascript>
			</eo:tff>
			<eo:tff label="Vrsta">
				<g:select name="myCalendarWidget_details_addNotice_type" from="${noticeTypes}" optionKey="key" optionValue="value"/>
			</eo:tff>
			<eo:tff label="Opis"><g:textField name="myCalendarWidget_details_addNotice_content" /></eo:tff>
			<eo:tff label="&nbsp;"><g:submitButton name="myCalendarWidget_details_addNotice_submit" value="Dodaj" /></eo:tff>
		</eo:tfjs>
	</div>
</div>
<g:javascript>
	function showAddNoticeForm() {
		$("#calendar-add-notice-button-holder").hide();
		$("#calendar-add-notice-form-holder").show();
	}
	function calendar_add_notice_form_submit() {
		postWithFlash(
			"${g.createLink(controller:'myCalendarWidget',action:'addNotice')}",
			{
				datumDay:    $("#myCalendarWidget_details_addNotice_date_day").val(), 
				datumMonth:  $("#myCalendarWidget_details_addNotice_date_month").val(), 
				datumYear:   $("#myCalendarWidget_details_addNotice_date_year").val(),
				fromHour:    $("#myCalendarWidget_details_addNotice_from_hour").val(),
				fromMinutes: $("#myCalendarWidget_details_addNotice_from_minutes").val(),
				tillHour:    $("#myCalendarWidget_details_addNotice_till_hour").val(),
				tillMinutes: $("#myCalendarWidget_details_addNotice_till_minutes").val(),
				noticeType:  $("#myCalendarWidget_details_addNotice_type").val(),
				content:     $("#myCalendarWidget_details_addNotice_content").val() 
			},
			function() {
				panel_load(
					"myCalendarWidget",
					"show",
					{year: ${year}, month: ${month}}
				);
			}
		);
	}
	function myCalendarWidget_details_onRemoveNotice(key) {
		postWithFlash(
			"${g.createLink(controller:'myCalendarWidget',action:'removeNotice')}",
			{id: key },
			function() {
				panel_load(
					"myCalendarWidget",
					"show",
					{year: ${year}, month: ${month}}
				);
			}
		);
	}
</g:javascript>
