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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
	<head>
		<title>e-Oskrba &raquo; <g:layoutTitle /></title>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<link href="${g.resource(dir:'css',file:'style-1.2.4.css')}" rel="stylesheet" type="text/css" />
		<link href="${g.resource(dir:'css/custom-theme',file:'jquery-ui-1.8.16.custom.css')}" rel="stylesheet" type="text/css" />
		
		<script type="text/javascript" src="${g.resource(dir:'lib',file:'jquery-1.6.1.min.js')}"></script>
		<script type="text/javascript" src="${g.resource(dir:'lib',file:'jquery-ui-1.8.16.custom.min.js')}"></script>
		<script type="text/javascript" src="${g.resource(dir:'lib',file:'jquery.tablesorter.min.js')}"></script>
		<script type="text/javascript" src="${g.resource(dir:'lib',file:'jquery.scrollTo-1.4.2-min.js')}"></script>
		<script type="text/javascript" src="${g.resource(dir:'lib',file:'jquery.maskedinput-1.3.min.js')}"></script>
		<script type="text/javascript" src="${g.resource(dir:'lib',file:'jquery.cookie.js')}"></script>
		<script type="text/javascript" src="${g.resource(dir:'lib/tiny_mce',file:'tiny_mce.js')}"></script>
		<script type="text/javascript" src="${g.resource(dir:'js',file:'eoskrba-1.5.3.js')}"></script>
		<script type="text/javascript" src="${g.resource(dir:'js',file:'style-1.5.js')}"></script>
		<script type="text/javascript" src="${g.resource(dir:'js',file:'activiti-1.5.7.js')}"></script>
		<script type="text/javascript" src="${g.resource(dir:'js',file:'eoskrba.print-1.1.js')}"></script>
		<script type="text/javascript">
			$(document).ready(function() {
				tinyMCE.init({
					mode : "none",
					theme : "advanced",
	
					plugins: "table,fullscreen",
	
					theme_advanced_buttons1 : "fullscreen,|,bold,italic,underline,|,justifyleft,justifycenter,justifyright,justifyfull,|,fontsizeselect",
					theme_advanced_buttons2 : "bullist,|,outdent,indent,|,undo,redo,|,link,unlink,anchor,image",
					theme_advanced_buttons3 : "tablecontrols,|,hr",
					theme_advanced_buttons4 : ""
						
				});
			});

		</script>
		<g:layoutHead />
		<flot:resources includeJQueryLib="false" plugins="['stack','resize']" />
		<script type="text/javascript" src="${g.resource(dir:'lib',file:'jquery.flot.threshold.multiple.js')}"></script>
		<ckeditor:resources />
	</head>
	<body>
	
		<fb:init />
	
		<audio controls="controls" id="beep">
		  <source src="${g.resource(dir:'sound',file:'beep.mp3')}" type="audio/mpeg" />
		  <source src="${g.resource(dir:'sound',file:'beep.ogv')}" type="audio/ogg" />
		</audio>
	
		<div id="layout-currentWidgetSelector"     style="display:none;">${params.w}</div>
		<div id="layout-currentControllerSelector" style="display:none;">${params.controller}</div>
		<div id="layout-currentActionSelector"     style="display:none;">${params.action}</div>
		<div id="layout-currentTabSelector"        style="display:none;">${params.controller}_${params.action}</div>
		<div id="layout-loadToPanelSelector-pw"    style="display:none;">${params.pw}</div>
		<div id="layout-loadToPanelSelector-pa"    style="display:none;">${params.pa}</div>
		<div id="layout-loadToPanelSelector-pp"    style="display:none;">${params.pp}</div>
	
		<div id="layout-app">
		</div>
		
		<div id="layout-mainmenu">
			<eo:menu />
		</div>
		
		<div id="layout-printbox">
			<a href="#" onclick="panel_print();return false;"><img src="${g.resource(dir:'img',file:'print.png')}" alt="TISK" title="Natisni vsebino" /></a> &nbsp;
			<a href="#" onclick="panel_pdf();return false;"><img src="${g.resource(dir:'img',file:'pdf.png')}" alt="PDF" title="Izvozi v PDF" /></a>
			<form target="_new" id="layout-printbox-form" method="post" action="${g.createLink(controller:'printWidget',action:'print')}">
				<input type="hidden" name="layout-printbox-content"   id="layout-printbox-content" />
				<input type="hidden" name="layout-printbox-device"    id="layout-printbox-device" />
				<input type="hidden" name="layout-printbox-timestamp" id="layout-printbox-timestamp" />
			</form>
		</div>
		
		<div id="layout-infobox">
			<img id="layout-infobox-more" src="${g.resource(dir:'img',file:'label_more.png')}" />
			<img id="layout-infobox-avatar" src="${eo.avatarLink(id:session.user)}" />
			<h2>${session.firstName + " " + session.lastName}</h2>
			<div id="layout-infobox-content">
				<g:if test="${session.role == 'patient'}">
					<g:include controller="sessionInfoWidget" action="show" />
					
					<g:include controller="myCalendarWidget" action="showInfoBox" />
					<g:javascript>
						function myCalendarWidget_showInfoBox_onClick() {
							panel_load(
								"myCalendarWidget",
								"show",
								{
									year: (new Date()).getFullYear(),
									month: (new Date()).getMonth() + 1
								}
							);
						}
					</g:javascript>
					
					<eo:hr />
					
					<g:javascript>
						function refresh_layout_infobox_patientProgress(url) {
							$("#layout-infobox-patientProgress").load( url, {timestamp: timestamp()} );
						}
					</g:javascript>
					<div id="layout-infobox-patientProgress">
					
						<g:if test="${session.updatePatientProgress == true}">
						<center>
							<g:img dir="images" file="loader.gif" />
						</center>
						</g:if>
						
						<g:if test="${session.employeeType == 'A'}">
							<g:if test="${session.updatePatientProgress != true}">
								<g:include controller="patientProgressWidget" action="showAs" />
							</g:if>
							<g:else>
								<g:javascript>
									refresh_layout_infobox_patientProgress("${g.createLink(controller:'patientProgressWidget',action:'showAs')}");
								</g:javascript>
							</g:else>
						</g:if>
						<g:elseif test="${session.employeeType == 'D'}">
							<g:if test="${session.updatePatientProgress != true}">
								<g:include controller="patientProgressWidget" action="showDi" />
							</g:if>
							<g:else>
								<g:javascript>
									refresh_layout_infobox_patientProgress("${g.createLink(controller:'patientProgressWidget',action:'showDi')}");
								</g:javascript>
							</g:else>
						</g:elseif>
						<g:elseif test="${session.employeeType == 'S'}">
							<g:if test="${session.updatePatientProgress != true}">
								<g:include controller="patientProgressWidget" action="showSh" />
							</g:if>
							<g:else>
								<g:javascript>
									refresh_layout_infobox_patientProgress("${g.createLink(controller:'patientProgressWidget',action:'showSh')}");
								</g:javascript>
							</g:else>
						</g:elseif>
						<g:elseif test="${session.employeeType == 'H'}">
							<g:if test="${session.updatePatientProgress != true}">
								<g:include controller="patientProgressWidget" action="showHu" />
							</g:if>
							<g:else>
								<g:javascript>
									refresh_layout_infobox_patientProgress("${g.createLink(controller:'patientProgressWidget',action:'showHu')}");
								</g:javascript>
							</g:else>
						</g:elseif>
						<g:elseif test="${session.employeeType == 'P'}">
							<g:if test="${session.updatePatientProgress != true}">
								<g:include controller="patientProgressWidget" action="showSp" />
							</g:if>
							<g:else>
								<g:javascript>
									refresh_layout_infobox_patientProgress("${g.createLink(controller:'patientProgressWidget',action:'showSp')}");
								</g:javascript>
							</g:else>
						</g:elseif>
						<eo:hr />
					</div>
					
					<div class="layout-infobox-field" onclick="sessionInfoWidget_show_myDocClick();return false;">
						<g:if test="${session.docMale}">
							<img class="layout-infobox-arrow" src="${g.resource(dir:'img/mi/min',file:'DoctorMale-35x30.png')}" />
						</g:if>
						<g:else>
							<img class="layout-infobox-arrow" src="${g.resource(dir:'img/mi/min',file:'DoctorFemale-35x30.png')}" />
						</g:else>
						<div class="layout-infobox-value-blue">${session.docNameShort}</div>
						<div class="layout-infobox-what">
							DODELJENI ZDRAVNIK
						</div>
						<g:javascript>
						function sessionInfoWidget_show_myDocClick() {
							showPerson("${session.docUid}");
						}
						</g:javascript>
					</div>					
					
				</g:if>
				<g:else>
					<g:include controller="sessionInfoWidget" action="show" />
					<g:include controller="myCalendarWidget" action="showInfoBox" />
					<g:javascript>
						function myCalendarWidget_showInfoBox_onClick() {
							panel_load(
								"myCalendarWidget",
								"show",
								{
									year: (new Date()).getFullYear(),
									month: (new Date()).getMonth() + 1
								}
							);
						}
					</g:javascript>
				</g:else>
				<hr style="margin-top:3px;" />
				<p style="text-align:right;"><a href="#" onclick="showSettings();return false;">Nastavitve</a> &nbsp; <b><g:link controller="login" action="logout" onclick="panel_resetAllCookies();">Odjava</g:link></b> &nbsp;</p>
			</div>
		</div>
		
		<div id="layout-mainframe">
			<div id="layout-mainframe-wait"><p><img src="../images/pleaseWaitBar.gif" /></p></div>
			<g:layoutBody />
		</div>
		
		<div id="layout-showperson">
			<div id="layout-showperson-under" onclick="hidePerson();return false;">
			</div>
			<div id="layout-showperson-frame">
			</div>
		</div>
		
		<div id="layout-settings">
			<div id="layout-settings-under" onclick="hideSettings();return false;">
			</div>
			<div id="layout-settings-frame">
			</div>
		</div>
		
		<g:if test="${flash.afterLogin}">
			<div id="layout-afterlogin">
				<div id="layout-afterlogin-under" onclick="layout_afterlogin_dismiss();return false;">
				</div>
				<div id="layout-afterlogin-frame">
					<h1>Dobrodošli!</h1>
					<p>Uspešno ste se prijavili v spletni uporabniški vmesnik eOskrbe.</p>
										
					<eo:hr />
					<p>Zaradi Vaše varnosti smo zabeležili zadnji dostop do vašega računa. Če se zabeleženi podatki zdijo sumljivi, nas prosimo o tem takoj obvestite.</p>
					<eo:tfjs lwidth="120px">
						<eo:tff label="datum in čas">
							${session.lastLoginTime}
						</eo:tff>
						<eo:tff label="IP naslov">
							${session.lastLoginIP}
						</eo:tff>
						<eo:tff label="osebno pozdravno sporočilo">
							<g:textArea name="layout-afterlogin-personalMessage">${session.personalMessage}</g:textArea>
						</eo:tff>
					</eo:tfjs>
					<eo:hr />
					<button onclick="layout_afterlogin_dismiss();layout_afterlogin_updatePersonalMessage();return false;">V redu</button>
					<g:javascript>
						function layout_afterlogin_dismiss() {
							$("#layout-afterlogin").fadeOut();
						}
						function layout_afterlogin_updatePersonalMessage() {
							var newMessage = $("#layout-afterlogin-personalMessage").val();
							$.post("${g.createLink(controller:'commonSettingsWidget',action:'updatePersonalMessage')}",{
								msg: newMessage
							});
						}
					</g:javascript>
				</div>
			</div>
		</g:if>
		
		<div id="layout-sessionLost">
			<div id="layout-sessionLost-under" onclick="document.location.href='${g.createLink(controller:"login",action:"form")}';">
			</div>
			<div id="layout-sessionLost-frame">
				<h1>Seja je potekla</h1>
				<eo:hr />
				<p>Vaša seja v aplikaciji je potekla. To se je lahko zgodilo zaradi večjega časa neaktivnosti ali zaradi vzdrževalnih del na strežniku.</p>
				<p>Za nadeljevanje uporabe aplikacije prosimo pritisnite na spodnji gumb in se ponovno prijavite.</p>
				<eo:hr />
				<button onclick="document.location.href='${g.createLink(controller:"login",action:"form")}';">Nadaljuj</button>
			</div>
		</div>
		
		<div id="layout-ajaxResponseHolder" onclick="hideFlashMessage();return false;">
			<div id="layout-ajaxResponseHolder-frame">
			</div>
		</div>
		
		<g:if test="${(session.logged && (session.role != 'patient'))}">
			<div id="liveChatNotifier_notice">
				<div id="liveChatNotifier_notice_frame">
					<p>
						<span id="liveChatNotifier_notice_pendingNumber"></span> pacientov čaka na pogovor v živo<br />
						<a href="${g.createLink(controller:session.role+'Tabs',action:'chat',params:[w:'liveChatWidget_showPending'])}">Pokaži &raquo;</a>
					</p>
				</div>
			</div>
		</g:if>
		
		<g:if test="${session.role != 'patient'}">
			<g:javascript>
				$(document).ready(function() {
					liveChatNotifier_startLoop();
				});
			</g:javascript>
		</g:if>
		
		<g:javascript>
			$(document).ready(function() {
				startDataLoop();
			});
		</g:javascript>
		
		<g:javascript>
			$(document).ready(function() {
				
				var flashSucces =  false;
				var flashError  =  false;
				var flashWarning = false;
				
				<g:if test="${flash.success != null}">
					flashSucces  = [ "${flash.success[0]}", "${flash.success[1]}" ];
				</g:if>
				<g:if test="${flash.error != null}">
					flashError   = [ "${flash.error[0]}", "${flash.error[1]}" ];
				</g:if>
				<g:if test="${flash.warning != null}">
					flashWarning = [ "${flash.warning[0]}", "${flash.warning[1]}" ];
				</g:if>
				
				if(flashSucces  != false) showFlashSuccess(flashSucces[0], flashSucces[1]);
				if(flashError   != false) showFlashError(flashError[0], flashError[1]);
				if(flashWarning != false) showFlashWarning(flashWarning[0], flashWarning[1]);
				
			});
		</g:javascript>
		
	</body>
</html>
