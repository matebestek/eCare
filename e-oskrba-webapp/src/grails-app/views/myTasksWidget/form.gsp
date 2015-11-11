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
	<eo:panelHead>
		
	<eo:panelHeadInfo>
		Moje naloge.
	</eo:panelHeadInfo>
	<h1>${(taskInfo != null ? taskInfo.name : "")}</h1>
					
		    
		    
</eo:panelHead>
<eo:panelContent>
	<g:if test="${params.showNoticeOldTask}">
		<div id="showNoticeOldTask" style="border:1px solid #215968;color: #215968;background-color:#DAEEF3;width:95%;text-align:center;padding:5px;margin-bottom:15px;font-weight:bold;">
			<g:img dir="img" file="info.png" style="position:relative;top:1px;" />  Prosimo, opravite tudi spodnjo nalogo, ki je še niste oddali
		</div>
	</g:if>
	<g:if test="${params.showNoticeNewTask}">
		<div id="showNoticeNewTask" style="border:1px solid #215968;color: #215968;background-color:#DAEEF3;width:95%;text-align:center;padding:5px;margin-bottom:15px;font-weight:bold;">
			<g:img dir="img" file="info.png" style="position:relative;top:1px;" /> &nbsp; Prosimo, nadaljujte z opravljanjem spodnje naloge
		</div>
	</g:if>
	<g:if test="${raw == 404}">
		<p>Če želite zaključiti to nalogo, pritisnite spodnji gumb.</p>
		<center>
			<button onclick="document.location='${g.createLink(controller:'myTasksWidget',action:'completeWithForm',id:params.id,params:['redirectToController':params.redirectToController,'redirectToAction':params.redirectToAction])}'">Zaključi to nalogo</button>
		</center>
	</g:if>
	<g:elseif test="${raw == 'taskIdNotFound'}"><!-- if: taskId (for OPKP) stored on session, but not in DB anymore -->
		<!-- show nothing -->
	</g:elseif>
	
	<g:else>
		
		<g:form name="form-widget-form" controller="myTasksWidget" action="completeWithForm">
			
			<g:if test="${(taskInfo != null && taskInfo.description=='TaskIzpolniOPKP')}">		
			
				
				<g:if test="${session.showResults}">
					<g:set var="jToCalConst" value="${0.239005736}"/>

					<% ww = UtilFunctions.getWarnings(taskInfo.opkpResponse) %>
					<table class="reportingWidget_dataTable">
						<thead>
							<tr>
								<th colspan="3">
									1. OCENA ENERGIJSKEGA IN HRANILNEGA VNOSA ŽIVIL
								</th>
							</tr>
							<tr>
								<th>pojasnilo</th>
								<th>vrednost (MJ)</th>
								<th>vrednost (kCal)</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td>Energijska vrednost:</td>
								<td style="text-align:right;padding-right:30px;">
									<g:formatNumber number="${UtilFunctions.roundToSignificantFigures((taskInfo.opkpResponse.energy / 1000000),3)}" type="number" roundingMode="HALF_DOWN"  maxFractionDigits="2" minFractionDigits="2" format="#0.00" locale="sl_SI" />
								</td>
								<td style="text-align:right;padding-right:30px;">
									<g:formatNumber number="${UtilFunctions.roundToSignificantFigures(((taskInfo.opkpResponse.energy / 1000) * jToCalConst),3)}" type="number" roundingMode="HALF_DOWN" maxFractionDigits="2" minFractionDigits="2"  format="#0.00" locale="sl_SI" />
								</td>
							<tr>		
							<tr>
								<td>Energijska vrednost iz aktivnosti:</td>
								<td style="text-align:right;padding-right:30px;">
									<g:formatNumber number="${UtilFunctions.roundToSignificantFigures((taskInfo.opkpResponse.activity_energy / 1000000),3)}" type="number" maxFractionDigits="3" roundingMode="HALF_DOWN" maxFractionDigits="2" minFractionDigits="2"  format="#0.00" locale="sl_SI" />
								</td>
								<td style="text-align:right;padding-right:30px;">
									<g:formatNumber number="${UtilFunctions.roundToSignificantFigures(((taskInfo.opkpResponse.activity_energy / 1000) * jToCalConst),3)}" type="number" roundingMode="HALF_DOWN" maxFractionDigits="2" minFractionDigits="2"  format="#0.00" locale="sl_SI"/>
								</td>
							<tr>		
							<tr>
								<td>Priporočena energijska vrednost:</td>
								<td style="text-align:right;padding-right:30px;">
									<g:formatNumber number="${UtilFunctions.roundToSignificantFigures((taskInfo.opkpResponse.recommended_energy / 1000000),3)}" type="number" maxFractionDigits="3" roundingMode="HALF_DOWN"  maxFractionDigits="2" minFractionDigits="2"  format="#0.00" locale="sl_SI" />
								</td>
								<td style="text-align:right;padding-right:30px;">
									<g:formatNumber number="${UtilFunctions.roundToSignificantFigures(((taskInfo.opkpResponse.recommended_energy / 1000) * jToCalConst),3)}" type="number" maxFractionDigits="3" roundingMode="HALF_DOWN" maxFractionDigits="2" minFractionDigits="2"  format="#0.00" locale="sl_SI"/>
								</td>
							<tr>
							<tr>
								<td colspan="3">
								
									Na podlagi izpolnjenega vprašalnika, ki ste ga izpolnili za pretekli teden, ocenjujemo, da je vaš povprečni dnevni energijski vnos znašal
									<g:formatNumber number="${UtilFunctions.roundToSignificantFigures((taskInfo.opkpResponse.energy / 1000000),3)}" type="number" roundingMode="HALF_DOWN"  maxFractionDigits="2" minFractionDigits="2" format="#0.00" locale="sl_SI" /> MJ (<g:formatNumber number="${UtilFunctions.roundToSignificantFigures(((taskInfo.opkpResponse.energy / 1000) * jToCalConst),3)}" type="number" roundingMode="HALF_DOWN" maxFractionDigits="2" minFractionDigits="2"  format="#0.00" locale="sl_SI" /> kCal).
									Glede na vaše dejanske potrebe po energiji s ciljem zmanjšanja telesne mase za 0,5 kg na teden, vam priporočamo povprečen dnevni energijski vnos <g:formatNumber number="${UtilFunctions.roundToSignificantFigures((taskInfo.opkpResponse.recommended_energy / 1000000),3)}" type="number" maxFractionDigits="3" roundingMode="HALF_DOWN"  maxFractionDigits="2" minFractionDigits="2"  format="#0.00" locale="sl_SI" /> MJ(<g:formatNumber number="${UtilFunctions.roundToSignificantFigures(((taskInfo.opkpResponse.recommended_energy / 1000) * jToCalConst),3)}" type="number" maxFractionDigits="3" roundingMode="HALF_DOWN" maxFractionDigits="2" minFractionDigits="2"  format="#0.00" locale="sl_SI"/> kCal).
									<br/>
									Na podlagi odgovorov v vprašalniku ocenjujemo, da ste s telesno aktivnostjo povprečno dnevno potrošili <g:formatNumber number="${UtilFunctions.roundToSignificantFigures((taskInfo.opkpResponse.activity_energy / 1000000),3)}" type="number" maxFractionDigits="3" roundingMode="HALF_DOWN" maxFractionDigits="2" minFractionDigits="2"  format="#0.00" locale="sl_SI" /> MJ (<g:formatNumber number="${UtilFunctions.roundToSignificantFigures(((taskInfo.opkpResponse.activity_energy / 1000) * jToCalConst),3)}" type="number" roundingMode="HALF_DOWN" maxFractionDigits="2" minFractionDigits="2"  format="#0.00" locale="sl_SI"/> kCal).
									
								</td>
							</tr>	
							
							<g:if test="${ww.containsKey('energy_density')}">	
							<tr>
								<td colspan="3">
									Glejte opozorilo št. ${ww.get('energy_density')} v spodnji tabeli opozoril.
								</td>
							</tr>	
							</g:if>
						</tbody>
					</table>
															
					<br/>
					<br/>
					
					<table class="reportingWidget_dataTable">
						<thead>
							<tr>
								<th colspan="5">
									2. PREHRANSKA PRIPOROČILA
								</th>
							</tr>
							<tr>	
								<th>&nbsp;</th>							
								<th>naziv</th>
								<th>dosežena vrednost</th>								
								<th>priporočena vrednost</th>	
								<th>glej opozorilo</th>							
							</tr>
						</thead>
						<tbody>
							<g:each in="${UtilFunctions.sortMap(taskInfo.opkpResponse.units)}" var="unit">
								<tr>
									<td>${unit.key}</td>									
									<td>${unit.value.name}</td>
									<td style="text-align:right;padding-right:70px;">
										<div style="width:30px;height:16px;float:left;background-color:${UtilFunctions.showSemaphore(unit.value.val,unit.value.guide)};"></div>
										<g:formatNumber number="${unit.value.val}" type="number" maxFractionDigits="1" minFractionDigits="1" roundingMode="HALF_DOWN" />
									</td>
									<td style="text-align:right;padding-right:70px;">
										<g:formatNumber number="${unit.value.guide}" type="number" maxFractionDigits="1" minFractionDigits="1" roundingMode="HALF_DOWN" />
									</td>	
									<td style="text-align:right;padding-right:10px;">
										<g:if test="${ww.containsKey('u'+unit.key)}">	
											${ww.get('u'+unit.key)}
										</g:if>
									</td>								
								</tr>				
							</g:each>
						</tbody>
					</table>
					<br/>
					<br/>	
					<table class="reportingWidget_dataTable">
						<thead>
							<tr>
								<th colspan="4">
									3. OPOZORILA
								</th>
							</tr>
							<tr>
								<%--<th>Skupina živil za katera veljajo opozorila</th>--%>
								<th>&nbsp;</th>
								<th>prekoračitev priporočil</th>
								<th>rezultat</th>
								<th>opis</th>
								<%--<th>Skupina škodljivih živil</th>								
								<th>Vprašanje</th>
								<th>Pogostost</th>
								<th>Teža</th>
								<th>Odgovor</th>--%>
							</tr>
						</thead>
						<tbody>
							<% n=1 %>
							<g:each in="${taskInfo.opkpResponse.warnings}" var="w">
								<tr>
									<%--<td>
										<g:if test="${w.containsKey('unit')}">		
											<g:findAll in="${taskInfo.opkpResponse.units}" expr="${it.key == w.unit + ""}">
												${it.value.name}
											</g:findAll>
										</g:if>																			
									</td>--%>
									<td>${n++}</td>
									<td>
										<g:if test="${w.containsKey('too_high')}">
											DA
										</g:if>
										<g:elseif test="${w.containsKey('too_low')}">
											NE
										</g:elseif>										
									</td>
									<td>${w.result}</td>
									<td>${w.desc}</td>
									<g:if test="${w.containsKey('high')}">
										</tr>
										<g:each in="${w.high}" var="el">
											<%--<tr>
												<td>&nbsp;</td>
												<td>&nbsp;</td>
												<td>${el.name}</td>
												<td>
													<g:each in="${el.items}" var="z">
														${z},
													</g:each>																									
												</td>
												<td>${el.freq}</td>
												<td><g:formatNumber number="${el.weight}" type="number" roundingMode="HALF_DOWN" format="#0" locale="en_US"/>g</td>
											</tr>
										--%>
										<tr>
												<td>&nbsp;</td>
												<td>&nbsp;</td>		
												<td>&nbsp;</td>
												<td><i>Na vprašanje</i> ${el.question} <i>ste za</i> ${el.name} <i>odgovorili</i> ${el.freq} <i>po</i> 
												<g:formatNumber number="${el.weight}" type="number" roundingMode="HALF_DOWN" format="#0" locale="en_US"/>g
												</td>												
												<%--<td>${el.question}</td>												
												<td>${el.freq}</td>
												<td><g:formatNumber number="${el.weight}" type="number" roundingMode="HALF_DOWN" format="#0" locale="en_US"/>g</td>
												<td>${el.name}</td>--%>
											</tr>
										</g:each>
									</g:if>					
									<g:else>
										<%--<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>
										<td>&nbsp;</td>--%>
										</tr>
									</g:else>				
									
								<tr>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<%--<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
									<td>&nbsp;</td>--%>
								</tr>			
							</g:each>
						</tbody>
					</table>
					<br/>
					<g:if test="${taskInfo.opkpResponse.components != null && taskInfo.opkpResponse.components.containsKey('ALC') && taskInfo.opkpResponse.components.get('ALC').val != null}">
						<g:set var="alcGrams" value="${taskInfo.opkpResponse.components.get('ALC').val}"/>
						<g:set var="isGenderMale" value="${session.ldap.sexAtt == 'MALE' || session.ldap.sexAtt == 'M'}"/>

						<g:if test="${isGenderMale ? (alcGrams > 20) : (alcGrams > 10)}">						
						<table class="reportingWidget_dataTable">
							<thead>
								<tr>
									<th colspan="4">
										4. PREKOMEREN VNOS ALKOHOLA
									</th>
								</tr>						
							</thead>
							<tbody>
								<tr>
									<td>Iz vašega poročanja o pitju alkoholnih pijač v preteklem tednu je razvidno, da količina presega meje manj tveganega pitja.</td>
								</tr>							
								<tr>
									<td>Zaužili ste <b>${UtilFunctions.roundToSignificantFigures(alcGrams,3)} g</b> alkohola.</td>
								</tr>
								<tr>
									<td>Ker lahko tvegano pitje alkoholnih pijač sčasoma privede do posledic , priporočamo, da ne presegate spodaj navedenih mej.</td>
								</tr>
								<tr>
									<td>Priporočene meje manj tveganega pitja:<p/>
											&nbsp;&nbsp;za MOŠKE: do 14 enot alkohola na teden (npr. do 2 dl vina na dan) ali do 5 enot ob eni pivski priložnosti (ob vsaj enem dnevu v tednu brez alkohola)<p/>
											&nbsp;&nbsp;za ŽENSKE do 7 enot alkohola na teden ali do 3 enote alkohola ob eni priložnosti (pod pogojem, da ni presežena tedenska količina)<p/>
									</td>
								</tr>	
								<tr><td>&nbsp;</td></tr>
								<tr>
									<td>1 enota vsebuje približno 10 gramov čistega alkohola, kar je na primer: 1 dl vina ali 2,5 dl piva ali 0,3 dl (''štamprl'') žgane pijače ali 2,5 dl sadjevca, tolkovca.</td>
								</tr>
								<tr>
									<td>Koliko enot vsebujejo nekatere najpogostejše alkoholne pijače?</td>
								</tr>
								<tr>
									<td>
										<table>
											<tr>
												<td>1 steklenica piva Union, Laško (0,5 l)</td><td>2 enoti</td>
											</tr>
											<tr>
												<td>1 pločevinka piva Union, Laško (0,33 l)</td><td>1,3 enote</td>
											</tr>
											<tr>												
												<td>1 pločevinka Radlerja (0,33 l)</td><td>1 enota</td>
											</tr>
											<tr>																							
												<td>1 steklenica Bandidosa (0,33 l)</td><td>1,3 enote</td>
											</tr>
											<tr>																							
												<td>1 buteljka vina (7 dl)</td><td>7 enot</td>
											</tr>
										</table>
									</td>
								</tr>
							</tbody>
						</table>
						<br/>
						</g:if>
					</g:if>
				</g:if>
				<g:else>					
					<div id="activiti_form_holder">
						<div id="opkp-info-msg" style="border:1px solid #215968;color: #215968;background-color:#DAEEF3;width:95%;text-align:center;padding:5px;margin-bottom:15px;font-weight:bold;">
							<g:img dir="img" file="info.png" style="position:relative;top:1px;" /> &nbsp; Vprašalnik boste morali v celoti izpolniti samo pri prvem izpolnjevanju. V vsakem naslednjem izpolnjevanju boste imeli že označene stare vrednosti in boste popravili samo tiste vrednosti, ki so se spremenile oz. so nove.
						</div>
						<%
							response.setHeader("P3P","CP='IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT'");
							String opkpSrc = "${grailsApplication.config.hostServerProtocol}://opkp.si/sl_SI/ivz?data=${data}&return_to=${(grailsApplication.config.hostServerProtocol + '://'+ grailsApplication.config.hostServer +'/eOskrba-webapp/patientTabs/opkpSave?taskId='+taskId).encodeAsURL()}";
						 %>
						 <!--[if IE]>
							 <p>Prosimo, da za pričetek reševanja vprašalnika o pogostosti uživanja živil kliknete na spodnji gumb. Vprašalnik bo prikazan celozaslonsko, zato ostale funkcionalnosti eOskrbe med reševanjem vprašalnika ne bodo na voljo.</p>
							 <eo:elem icon="Sheet" name="Vprašalnik OPKP" onclick="document.location.href='${opkpSrc}'">Kliknite za pričetek reševanja</eo:elem>
						 <![endif]-->
						 <!--[if !IE]><!-->
							 <iframe id="framex" width="100%" height="500" src="${opkpSrc}">  
							 </iframe>
						 <!--[endif]><!-->			
					</div>
					<g:javascript>
						activiti_form_onLoad();
					</g:javascript>
				</g:else>
					
			</g:if>
			<g:else>
				${raw}
			</g:else>
			
			<eo:hr/>
			<g:hiddenField name="id" value="${params.id}" />
			<g:hiddenField name="redirectToController" value="${params.redirectToController}" />
			<g:hiddenField name="redirectToAction" value="${params.redirectToAction}" />
			
			<g:if test="${session.employeeType == 'P'}">
				<g:if test="${taskOriginalName.startsWith('[WEBAPP_RENAME:eSport-enterValues:')}">
					<fb:publishCheckbox name="myTasks_form_fbpublish" enabled="${session.facebookConnected}">
						Opravljeno telesno dejavnost objavi tudi na mojem Facebook zidu. <i>Objavljena bo le opravljena športna aktivnost, ne pa tudi Vaša teža in ostale meritve.</i>
					</fb:publishCheckbox>
				</g:if>
			</g:if>
			
			<g:if test="${(taskInfo != null && taskInfo.description=='TaskIzpolniOPKP' && (session.showResults == null || session.showResults == false))}">
			</g:if>
			<g:else>
				<g:if test="${isHistoric != null && isHistoric}">
				</g:if>
				<g:else>
				<g:if test="${isHujsanjeTask != null && isHujsanjeTask}">
					<g:submitButton name="form-widget-submit" value="Nadaljuj" />
				</g:if>
				<g:else>
					<p>Po izpolnitvi vseh podatkov kliknite na spodnji gumb.</p>
					<g:submitButton name="form-widget-submit" value="Zaključi to nalogo" />
				</g:else>
				</g:else>
			</g:else>
			
		</g:form>
		<g:javascript>
			$("#form-widget-submit").click( function() {
				layout_forceLoading();
				panel_resetCookie();
			});
			$("#framex").load(function(){
				$("#layout-panel-content").css("overflow-y","hidden");
				var minusHeight = 0;
				$("#activiti_form_holder").children().not("#framex").each(function(){
					minusHeight += $(this).outerHeight(true);
				});
				minusHeight += $("#showNoticeNewTask").outerHeight(true);
				minusHeight += $("#showNoticeOldTask").outerHeight(true);
				minusHeight += 5;
				$("#layout-panel-content #framex").height( $("#layout-panel-content").height() - minusHeight );
			});
			<g:if test="${shouldFillWithData}">
				$(document).ready(function() {
					loading = true;
					activiti_form_fillWithData(${dataToFillFormWith});
					loading = false;
				});
			</g:if>
		</g:javascript>
	</g:else>

</eo:panelContent>

		
