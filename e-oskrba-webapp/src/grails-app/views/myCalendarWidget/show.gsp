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
<%@page import="java.lang.Integer"%>
<eo:panelHead>
	<eo:panelHeadInfo>
		Koledar. ${monthLabel} ${year}.
	</eo:panelHeadInfo>
	<h1>Moja opravila za: <span style="font-variant: small-caps;text-decoration: underline;">${monthLabel}, ${year}</span></h1>
	<eo:options>
		&larr; <button onclick="myCalendar_show_previous();return false;">Nazaj</button>
		<button style="width:200px;" onclick="myCalendar_show_current();return false;">${today_day}. ${today_monthLabel}, ${today_year}, <span id="myCalendar_show_time"></span></button>
		<button onclick="myCalendar_show_next();return false;">Naprej</button> &rarr;
	</eo:options>
</eo:panelHead>

<eo:panelContent>
	<br />
	<table id="myCalendarWidget_show_table">
	
		<tr id="myCalendarWidget_show_header">
			<td>ponedeljek</td>
			<td>torek</td>
			<td>sreda</td>
			<td>četrtek</td>
			<td>petek</td>
			<td>sobota</td>
			<td>nedelja</td>
		</tr>
	
		<g:set var="stopMakingNewRows" value="${false}" />
		<g:each in="${1..6}" var="i">
			<g:if test="${!stopMakingNewRows}">
				<tr>
					<g:each in="${1..7}" var="j">
						<g:set var="curr" value="${(i-1)*7+j}" />
						<g:set var="act"  value="${curr-starting+1}" />
						<g:if test="${curr < starting}">
							<td>&nbsp;</td>
						</g:if>
						<g:elseif test="${act > days}">
							<td>&nbsp;</td>
						</g:elseif>
						<g:else>
							<g:if test="${act == days}">
								<g:set var="stopMakingNewRows" value="${true}" />
							</g:if>
							<g:if test="${notices[act] == null}">
								<g:set var="toolTip" value="Ni opravil" />
							</g:if>
							<g:else>
								<g:set var="toolTip" value="" />
								<g:each in="${noticesByDay[act]}" var="noticeByDay">
									<g:set var="toolTip" value="${toolTip}[${noticeByDay.content}${( ((noticeByDay.endHour-noticeByDay.startHour)*60+(noticeByDay.endMinutes-noticeByDay.startMinutes)) > 0 )?(" (" + ((noticeByDay.endHour-noticeByDay.startHour)*60+(noticeByDay.endMinutes-noticeByDay.startMinutes)).toString() + " min)"):""}] " />
								</g:each>
							</g:else>
							<td class="myCalendarWidget_show_day" id="myCalendarWidget_show_${year}_${month}_${act}" onclick="myCalendarWidget_show_onDayChosen(${year},${month},${act});changeSelectedDate(${year},${month},${act});" title="${toolTip}">
								<div class="myCalendarWidget_show_daynumber">
									${act}.
									<g:if test="${year == today_year && month == today_month && act == today_day}">
										<span id="myCalendarWidget_show_today">&larr; danes</span>
									</g:if>
								</div>
								<g:if test="${notices[act] == null && !(programNotices.contains(act))}">
									<div class="day-number">
										<p>&nbsp;</p>
									</div>
								</g:if>
								<g:else>
									<div class="day-number">
										<p class="myCalendarWidget_show_noticecount">
											<img class="image-icon" src="${g.resource(dir:'img',file:'postit.png')}" />
											<g:set var="totalNotices" value="${ (notices[act]?:0) + ((programNotices.contains(act))?1:0) }" />
											${totalNotices}
											<g:if test="${totalNotices == 1}">opravilo</g:if><g:elseif test="${totalNotices == 2}">opravili</g:elseif><g:elseif test="${totalNotices == 3 || totalNotices == 4}">opravila</g:elseif><g:else>opravil</g:else>
										</p>
									</div>
								</g:else>
							</td>
						</g:else>
					</g:each>
				</tr>
			</g:if>
		</g:each>
	</table>
	
	<eo:hr />
	
	<div id="myCalendarWidget_details_holder"></div>
	
	<g:javascript>
	
		var previouslySelected = "#nonexisting-element";
		function changeSelectedDate(y,m,d) {
			$(previouslySelected).css('background-color','transparent');
			var dateHolderSelector = "#myCalendarWidget_show_" + y + "_" + m + "_" + d;
			$(dateHolderSelector).css('background-color','#FBFDB6');
			previouslySelected = dateHolderSelector;
		}
		
		function myCalendar_show_previous() {
			var nyear = ${year};
			var nmonth = ${month};
			var nmonth = nmonth - 1;
			if(nmonth == 0) {
				nmonth = 12;
				nyear = nyear - 1;
			}
			panel_load(
				"myCalendarWidget",
				"show",
				{
					year: nyear,
					month: nmonth
				}
			);
		}
		
		function myCalendar_show_next() {
			var nyear = ${year};
			var nmonth = ${month};
			var nmonth = nmonth + 1;
			if(nmonth == 13) {
				nmonth = 1;
				nyear = nyear + 1;
			}
			panel_load(
				"myCalendarWidget",
				"show",
				{
					year: nyear,
					month: nmonth
				}
			);
		}
		
		function myCalendarWidget_show_onDayChosen(year,month,act) {
			loading = true;
			loadWidgetToElement(
				"${g.createLink(controller:'myCalendarWidget',action:'details')}",
				"#myCalendarWidget_details_holder",
				{ year:year, month:month, day:act }
			);
		}
		
		function myCalendar_show_current() {
			panel_load(
				"myCalendarWidget",
				"show",
				{
					year: (new Date()).getFullYear(),
					month: (new Date()).getMonth() + 1
				}
			);
		}
		
		$(document).ready(function() {
			myCalendar_show_updateTime();
			panel_resetCookie();
		});
		
		function myCalendar_show_updateTime() {
			var timeNow = new Date();
			var out = checkTime(timeNow.getHours()) + ":" + checkTime(timeNow.getMinutes()) + ":" + checkTime(timeNow.getSeconds());
			$("#myCalendar_show_time").html(out);
			setTimeout("myCalendar_show_updateTime()",1000);
		}
		
		function checkTime(i) {
			if (i<10) {
			  i="0" + i;
			}
			return i;
		}
		
	</g:javascript>
</eo:panelContent>
