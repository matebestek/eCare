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
		Poročila. Graf.
	</eo:panelHeadInfo>
	<h1>Prikaz spreminjanja vrednosti izračunanih iz OPKP obrazcev</h1>
</eo:panelHead>
<eo:panelContent>

	<g:javascript>
		var reportingWidget_showEHujsanjeOPKPgraphs_plot1_resized = false;
		var reportingWidget_showEHujsanjeOPKPgraphs_plot1_css = "";
		var reportingWidget_showEHujsanjeOPKPgraphs_plot1_data = [
			{
				label: "Dnevni energijski vnos",
    			data: [<g:each in="${data}" var="dat" status="i"><g:if test="${i != 0}">,</g:if>[${dat.xmlTimestamp},<g:formatNumber number="${UtilFunctions.roundToSignificantFigures(((dat.EV as double)/1000000),3)}" type="number" roundingMode="HALF_DOWN"  maxFractionDigits="2" minFractionDigits="2" format="#0.00" locale="en_EN" />]</g:each>],
    			lines: { show: true },
				points: { show: true },
				unit: "MJ"
			},
			{
				label: "Dnevna energijska vrednost iz aktivnosti",
    			data: [<g:each in="${data}" var="dat" status="i"><g:if test="${i != 0}">,</g:if>[${dat.xmlTimestamp},<g:formatNumber number="${UtilFunctions.roundToSignificantFigures(((dat.AK as double)/1000000),3)}" type="number" roundingMode="HALF_DOWN"  maxFractionDigits="2" minFractionDigits="2" format="#0.00" locale="en_EN" />]</g:each>],
    			lines: { show: true },
				points: { show: true },
				unit: "MJ"
			},
			{
				label: "Priporočen dnevni energijski vnos",
    			data: [<g:each in="${data}" var="dat" status="i"><g:if test="${i != 0}">,</g:if>[${dat.xmlTimestamp},<g:formatNumber number="${UtilFunctions.roundToSignificantFigures(((dat.PV as double)/1000000),3)}" type="number" roundingMode="HALF_DOWN"  maxFractionDigits="2" minFractionDigits="2" format="#0.00" locale="en_EN" />]</g:each>],
    			lines: { show: true },
				points: { show: true },
				unit: "MJ"
			},
			{
				label: "Bazalni metabolizem",
    			data: [<g:each in="${data}" var="dat" status="i"><g:if test="${i != 0}">,</g:if>[${dat.xmlTimestamp},<g:formatNumber number="${UtilFunctions.roundToSignificantFigures((((dat.PV as double)-(dat.AK as double))/1000000),3)}" type="number" roundingMode="HALF_DOWN"  maxFractionDigits="2" minFractionDigits="2" format="#0.00" locale="en_EN" />]</g:each>],
    			lines: { show: true },
				points: { show: true },
				unit: "MJ"
			}
		];
		var reportingWidget_showEHujsanjeOPKPgraphs_plot1_opts = {
			xaxis: {
				mode: "time",
				timeformat: "%d/%m/%y",
				minTickSize: [1,"day"],
				min: ${from}-86400000,
				max: ${till}+86400000
			},
			legend: {
				show: true
			},
            grid: {
            	hoverable: true,
            	clickable: true
            }
		};
		var reportingWidget_showEHujsanjeOPKPgraphs_plot2_resized = false;
		var reportingWidget_showEHujsanjeOPKPgraphs_plot2_css = "";
		var reportingWidget_showEHujsanjeOPKPgraphs_plot2_data = [
			<g:each in="${(1..8)}" var="idx">
				{
					label: "${data[0]["ZE_"+idx+"_IME"]}",
		   			data: [<g:each in="${data}" var="dat" status="i"><g:if test="${i != 0}">,</g:if>[${dat.xmlTimestamp},${dat["ZE_"+idx+"_ZAU"]},${dat["ZE_"+idx+"_PRI"]}]</g:each>],
		   			lines: { show: true, fill: true },
					points: { show: true },
					color: "${ZE_COLORS[idx-1]}",
					unit: ""
				}<g:if test="${idx != 8}">,</g:if>
			</g:each>
		];
		var reportingWidget_showEHujsanjeOPKPgraphs_plot2_opts = {
			xaxis: {
				mode: "time",
				timeformat: "%d/%m/%y",
				minTickSize: [1,"day"],
				min: ${from}-86400000,
				max: ${till}+86400000
			},
			legend: {
				show: true
			},
            grid: {
            	hoverable: true,
            	clickable: true
            }
		};
	</g:javascript>
	
	<table class="reportingWidget_dataTable">
		<thead>
			<tr>
				<th>Pacient (up. ime)</th>
				<th>Od</th>
				<th>Do</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>${who}</td>
				<td>${fromString}</td>
				<td>${tillString}</td>
			</tr>
		</tbody>
	</table>
	
	<g:if test="${what == "ENERGIJSKI_DIAGRAMI"}">
		<table class="reportingWidget_dataTable">
			<thead>
				<tr>
					<th>Graf energijskih vrednosti (OPKP)</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>
						<br />
						<flot:plot id="reportingWidget_showEHujsanjeOPKPgraphs_plot1_placeholder" data="reportingWidget_showEHujsanjeOPKPgraphs_plot1_data" options="reportingWidget_showEHujsanjeOPKPgraphs_plot1_opts" style="width: 500px; height: 300px;  margin-left: auto;margin-right: auto;z-index:10;" />
						<br />
					</td>
				</tr>
			</tbody>
		</table>
		
		<table class="reportingWidget_dataTable">
			<thead>
				<tr>
					<th>Datum</th>
					<th>EV</th>
					<th>AK</th>
					<th>PV</th>
					<th>Bazalni metabolizem</th>
				</tr>
			</thead>
			<tbody>
				<g:each in="${data}" var="dat">
					<tr>
						<td class="epochToDate">${dat.xmlTimestamp}</td>
						<td><g:formatNumber number="${UtilFunctions.roundToSignificantFigures(((dat.EV as double)/1000000),3)}" type="number" roundingMode="HALF_DOWN"  maxFractionDigits="2" minFractionDigits="2" format="#0.00" locale="en_EN" /> MJ</td>
						<td><g:formatNumber number="${UtilFunctions.roundToSignificantFigures(((dat.AK as double)/1000000),3)}" type="number" roundingMode="HALF_DOWN"  maxFractionDigits="2" minFractionDigits="2" format="#0.00" locale="en_EN" /> MJ</td>
						<td><g:formatNumber number="${UtilFunctions.roundToSignificantFigures(((dat.PV as double)/1000000),3)}" type="number" roundingMode="HALF_DOWN"  maxFractionDigits="2" minFractionDigits="2" format="#0.00" locale="en_EN" /> MJ</td>
						<td><g:formatNumber number="${UtilFunctions.roundToSignificantFigures((((dat.PV as double)-(dat.AK as double))/1000000),3)}" type="number" roundingMode="HALF_DOWN"  maxFractionDigits="2" minFractionDigits="2" format="#0.00" locale="en_EN" /> MJ</td>
					</tr>
				</g:each>
			</tbody>
		</table>
	</g:if>
	
	<g:if test="${what == "ZIVILSKI_DIAGRAMI"}">
		<table class="reportingWidget_dataTable">
			<thead>
				<tr>
					<th>Graf zaužitih količin (OPKP)</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>
						<br />
							<table style="width:100%;">
								<tr>
									<td><input name="reportingWidget_showEHujsanjeOPKPgraphs_plot2_ZE1_on" type="checkbox" checked="checked" onchange="reportingWidget_showEHujsanjeOPKPgraphs_plot2_toggleSeries(1);" /></td><td>${data[0]["ZE_1_IME"]}</td>
									<td><input name="reportingWidget_showEHujsanjeOPKPgraphs_plot2_ZE2_on" type="checkbox" checked="checked" onchange="reportingWidget_showEHujsanjeOPKPgraphs_plot2_toggleSeries(2);" /></td><td>${data[0]["ZE_2_IME"]}</td>
									<td><input name="reportingWidget_showEHujsanjeOPKPgraphs_plot2_ZE3_on" type="checkbox" checked="checked" onchange="reportingWidget_showEHujsanjeOPKPgraphs_plot2_toggleSeries(3);" /></td><td>${data[0]["ZE_3_IME"]}</td>
									<td><input name="reportingWidget_showEHujsanjeOPKPgraphs_plot2_ZE4_on" type="checkbox" checked="checked" onchange="reportingWidget_showEHujsanjeOPKPgraphs_plot2_toggleSeries(4);" /></td><td>${data[0]["ZE_4_IME"]}</td>
								</tr>
								<tr>
									<td><input name="reportingWidget_showEHujsanjeOPKPgraphs_plot2_ZE5_on" type="checkbox" checked="checked" onchange="reportingWidget_showEHujsanjeOPKPgraphs_plot2_toggleSeries(5);" /></td><td>${data[0]["ZE_5_IME"]}</td>
									<td><input name="reportingWidget_showEHujsanjeOPKPgraphs_plot2_ZE6_on" type="checkbox" checked="checked" onchange="reportingWidget_showEHujsanjeOPKPgraphs_plot2_toggleSeries(6);" /></td><td>${data[0]["ZE_6_IME"]}</td>
									<td><input name="reportingWidget_showEHujsanjeOPKPgraphs_plot2_ZE7_on" type="checkbox" checked="checked" onchange="reportingWidget_showEHujsanjeOPKPgraphs_plot2_toggleSeries(7);" /></td><td>${data[0]["ZE_7_IME"]}</td>
									<td><input name="reportingWidget_showEHujsanjeOPKPgraphs_plot2_ZE8_on" type="checkbox" checked="checked" onchange="reportingWidget_showEHujsanjeOPKPgraphs_plot2_toggleSeries(8);" /></td><td>${data[0]["ZE_8_IME"]}</td>
								</tr>
							</table>
						<br />
						<flot:plot id="reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder" data="reportingWidget_showEHujsanjeOPKPgraphs_plot2_data" options="reportingWidget_showEHujsanjeOPKPgraphs_plot2_opts" style="width: 500px; height: 300px;  margin-left: auto;margin-right: auto;z-index:10;" />
						<br />
					</td>
				</tr>
			</tbody>
		</table>
		
		<table class="reportingWidget_dataTable">
			<thead>
				<tr>
					<th>Datum</th>
					<th>Živilo</th>
					<th>Zaužita / priporočena količina</th>
				</tr>
			</thead>
			<tbody>
				<g:each in="${data}" var="dat">
					<g:each in="${(1..8)}" var="idx">
						<tr>
							<g:if test="${idx==1}"><td class="epochToDate">${dat.xmlTimestamp}</td></g:if><g:else><td>&nbsp;</td></g:else>
							<td>${dat["ZE_"+idx+"_IME"]}</td>
							<td><g:formatNumber number="${dat["ZE_"+idx+"_ZAU"]}" type="number" roundingMode="HALF_DOWN"  maxFractionDigits="2" minFractionDigits="2" format="#0.00" locale="en_EN" /> / <g:formatNumber number="${dat["ZE_"+idx+"_PRI"]}" type="number" roundingMode="HALF_DOWN"  maxFractionDigits="2" minFractionDigits="2" format="#0.00" locale="en_EN" /></td>
						</tr>
					</g:each>
				</g:each>
			</tbody>
		</table>
	</g:if>
	
	<g:javascript>
		var previousPoint = null;
		$(function() {
		
			// Legenda
			$("#reportingWidget_showEHujsanjeOPKPgraphs_plot1_placeholder .legend").mouseenter(function(){
 				hideLegend("#reportingWidget_showEHujsanjeOPKPgraphs_plot1_placeholder .legend");
			});
			$("#reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder .legend").mouseenter(function(){
 				hideLegend("#reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder .legend");
			});
			
			// Hover
			$("#reportingWidget_showEHujsanjeOPKPgraphs_plot1_placeholder,#reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder").bind("plothover", function(event, pos, item) {
	            if (item) {
	                if (previousPoint != item.dataIndex) {
	                    previousPoint = item.dataIndex;
	                    
	                    $("#tooltip").remove();
	                    var x = new Date(item.datapoint[0]);
	                    x = x.getDate() + "/" + (x.getMonth()+1) + "/" + x.getFullYear();
	                    var y = item.datapoint[1];
	                    
	                    showTooltip(
	                    	item.pageX,
	                    	item.pageY,
	                        x + ": " + item.series.label + " = " + y + " " + item.series.unit
	                    );
	                }
	            }
	            else {
	                $("#tooltip").remove();
	                previousPoint = null;          
	            }
			});
			$("#reportingWidget_showEHujsanjeOPKPgraphs_plot1_placeholder").hover(
				function(evo1) {
					var msg = "<span class=\"ui-icon ui-icon-newwin\" style=\"display:inline-block;\"></span> Kliknite na graf za celozaslonski prikaz";
					if(reportingWidget_showEHujsanjeOPKPgraphs_plot1_resized) msg = "<span class=\"ui-icon ui-icon-arrowreturnthick-1-s\" style=\"display:inline-block;\"></span> Kliknite na graf za vrnitev nazaj"
			        $('<div id="fs_msg">' + msg + '</div>').css( {
			            position: 'absolute',
			            'z-index': '600',
			            display: 'none',	
			            top: -20,
			            right: 2,
			            'color': '#215968',
			            border: '1px solid #215968',
			            padding: '2px',
			            'background-color': '#DAEEF3',
			            opacity: 0.80
			        }).appendTo("#reportingWidget_showEHujsanjeOPKPgraphs_plot1_placeholder").fadeIn(200);
				}, function(evo2) {
					$("#fs_msg").remove();
				}
			);
			$("#reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder").hover(
				function(evo1) {
					var msg = "<span class=\"ui-icon ui-icon-newwin\" style=\"display:inline-block;\"></span> Kliknite na graf za celozaslonski prikaz";
					if(reportingWidget_showEHujsanjeOPKPgraphs_plot2_resized) msg = "<span class=\"ui-icon ui-icon-arrowreturnthick-1-s\" style=\"display:inline-block;\"></span> Kliknite na graf za vrnitev nazaj"
			        $('<div id="fs_msg">' + msg + '</div>').css( {
			            position: 'absolute',
			            'z-index': '600',
			            display: 'none',	
			            top: -20,
			            right: 2,
			            'color': '#215968',
			            border: '1px solid #215968',
			            padding: '2px',
			            'background-color': '#DAEEF3',
			            opacity: 0.80
			        }).appendTo("#reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder").fadeIn(200);
				}, function(evo2) {
					$("#fs_msg").remove();
				}
			);
			
			// Click
			$("#reportingWidget_showEHujsanjeOPKPgraphs_plot1_placeholder").bind("plotclick", function(event, pos, item) {
				if(reportingWidget_showEHujsanjeOPKPgraphs_plot1_resized) {
					$(this).css(reportingWidget_showEHujsanjeOPKPgraphs_plot1_css);
					$(this).css("position","relative");
				} else {
					reportingWidget_showEHujsanjeOPKPgraphs_plot1_css = getCSS($(this));
					$("#reportingWidget_showEHujsanjeOPKPgraphs_plot1_placeholder").css({
						"position": "fixed",
						"z-index": "600",
						"top": "0px",
						"left": "0px",
						"width": (layout_windowWidth-50) + "px",
						"height": (layout_windowHeight-50) + "px",
						"background-color": "white",
						"border": "25px solid white"
					});
				}
				reportingWidget_showEHujsanjeOPKPgraphs_plot1_resized = !reportingWidget_showEHujsanjeOPKPgraphs_plot1_resized;
				$("#reportingWidget_showEHujsanjeOPKPgraphs_plot1_placeholder").mouseleave();
				$("#reportingWidget_showEHujsanjeOPKPgraphs_plot1_placeholder").mouseenter();
				setTimeout(function(){
					$("#reportingWidget_showEHujsanjeOPKPgraphs_plot1_placeholder .legend").mouseenter(function(){
		 				hideLegend("#reportingWidget_showEHujsanjeOPKPgraphs_plot1_placeholder .legend");
					});
				},500);
			});
			$("#reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder").bind("plotclick", function(event, pos, item) {
				if(reportingWidget_showEHujsanjeOPKPgraphs_plot2_resized) {
					$(this).css(reportingWidget_showEHujsanjeOPKPgraphs_plot2_css);
					$(this).css("position","relative");
				} else {
					reportingWidget_showEHujsanjeOPKPgraphs_plot2_css = getCSS($(this));
					$("#reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder").css({
						"position": "fixed",
						"z-index": "600",
						"top": "0px",
						"left": "0px",
						"width": (layout_windowWidth-50) + "px",
						"height": (layout_windowHeight-50) + "px",
						"background-color": "white",
						"border": "25px solid white"
					});
				}
				reportingWidget_showEHujsanjeOPKPgraphs_plot2_resized = !reportingWidget_showEHujsanjeOPKPgraphs_plot2_resized;
				$("#reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder").mouseleave();
				$("#reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder").mouseenter();
				setTimeout(function(){
					$("#reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder .legend").mouseenter(function(){
		 				hideLegend("#reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder .legend");
					});
				},500);
			});
			
		});
		
	    function showTooltip(x, y, contents) {
	        $('<div id="tooltip">' + contents + '</div>').css( {
	            position: 'absolute',
	            'z-index': '600',
	            display: 'none',
	            top: y + 5,
	            left: x + 5,
	            border: '1px solid #fdd',
	            padding: '2px',
	            'background-color': '#fee',
	            opacity: 0.80
	        }).appendTo("body").fadeIn(200);
	    }
	    
	    function reportingWidget_showEHujsanjeOPKPgraphs_plot2_toggleSeries(idx) {
	    	var newData = [];
	    	for(var i = 1; i <= 8; i++) {
	    		if($('input[name="reportingWidget_showEHujsanjeOPKPgraphs_plot2_ZE'+i+'_on"]').is(':checked')) {
	    			newData.push(reportingWidget_showEHujsanjeOPKPgraphs_plot2_data[i-1]);
	    		}
	    	}
			$.plot(
				$("#reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder"),
				newData,
				reportingWidget_showEHujsanjeOPKPgraphs_plot2_opts
			);
			setTimeout(function(){
				$("#reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder .legend").mouseenter(function(){
	 				hideLegend("#reportingWidget_showEHujsanjeOPKPgraphs_plot2_placeholder .legend");
				});
			},500);
	    }
	    
	    function hideLegend(legend) {
			$(legend).children().fadeOut();
			$(legend).append("<button onclick=\"showLegend('"+legend+"');\" style=\"position:absolute;top:9px;right:9px;\">Legenda</button>");
	    }
	    function showLegend(legend) {
	    	$(legend).children().not("button").fadeIn();
	    	$(legend).children("button").fadeOut();
	    }
	</g:javascript>
	
	<g:javascript>
	$(function() {
		$(".epochToDate").each(function(){
			var epoch = parseInt($(this).html());
            var e2d = new Date(epoch);
            var dateFormatted = e2d.getDate() + "/" + (e2d.getMonth()+1) + "/" + e2d.getFullYear();
            $(this).html(dateFormatted);
		});
	});
	</g:javascript>

</eo:panelContent>
