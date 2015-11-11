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
		Poročila. Graf.
	</eo:panelHeadInfo>
	<h1>${naslov}</h1>
</eo:panelHead>
<eo:panelContent>

	<g:javascript>
		var reportingWidget_show_plot_resized = false;
		var reportingWidget_show_plot_css = "";
		var reportingWidget_show_plot_data = [
			${dataString}
		];
		var reportingWidget_show_plot_refData = { ${refDataString} };
		var reportingWidget_show_plot_opts = {
			xaxis: {
				mode: "time",
				timeformat: "%d/%m/%y",
				minTickSize: [1,"day"],
				min: ${from},
				max: ${till}
			},
            series: {
                stack: true,
                lines: { show: false },
                bars: {
                	show: true,
                	barWidth: ${sirinaStolpca},
                	align: 'center'
                }
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
	
	<table class="reportingWidget_dataTable">
		<thead>
			<tr>
				<th>${naslovGrafa}</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>
					<br />
					<flot:plot id="reportingWidget_show_plot_placeholder" data="reportingWidget_show_plot_data" options="reportingWidget_show_plot_opts" style="width: 500px; height: 300px;  margin-left: auto;margin-right: auto; z-index:250;" />
					<br />
				</td>
			</tr>
		</tbody>
	</table>
	
	<g:javascript>
		var previousPoint = null;
		$(function() {
		
			// Legenda
			$("#reportingWidget_show_plot_placeholder .legend").mouseenter(function(){
 				hideLegend("#reportingWidget_show_plot_placeholder .legend");
			});
		
			$("#reportingWidget_show_plot_placeholder").bind("plothover", function(event, pos, item) {
	            if (item) {
	                if (previousPoint != item.dataIndex) {
	                    previousPoint = item.dataIndex;
	                    
	                    $("#tooltip").remove();
	                    var x = new Date(item.datapoint[0]);
	                    x = x.getDate() + "/" + (x.getMonth()+1) + "/" + x.getFullYear();
	                    var y = reportingWidget_show_plot_refData[item.series.label][item.datapoint[0]];
	                    
	                    showTooltip(
	                    	item.pageX,
	                    	item.pageY,
	                        x + ": " + item.series.label + " (" + y + " ${unitName})"
	                    );
	                }
	            }
	            else {
	                $("#tooltip").remove();
	                previousPoint = null;          
	            }
			});
			$("#reportingWidget_show_plot_placeholder").hover(
				function(evo1) {
					var msg = "<span class=\"ui-icon ui-icon-newwin\" style=\"display:inline-block;\"></span> Kliknite na graf za celozaslonski prikaz";
					if(reportingWidget_show_plot_resized) msg = "<span class=\"ui-icon ui-icon-arrowreturnthick-1-s\" style=\"display:inline-block;\"></span> Kliknite na graf za vrnitev nazaj"
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
			        }).appendTo("#reportingWidget_show_plot_placeholder").fadeIn(200);
				}, function(evo2) {
					$("#fs_msg").remove();
				}
			);
			$("#reportingWidget_show_plot_placeholder").bind("plotclick", function(event, pos, item) {
				if(reportingWidget_show_plot_resized) {
					$(this).css(reportingWidget_show_plot_css);
					$(this).css("position","relative");
				} else {
					reportingWidget_show_plot_css = getCSS($(this));
					$("#reportingWidget_show_plot_placeholder").css({
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
				reportingWidget_show_plot_resized = !reportingWidget_show_plot_resized;
				$("#reportingWidget_show_plot_placeholder").mouseleave();
				$("#reportingWidget_show_plot_placeholder").mouseenter();
				setTimeout(function(){
					$("#reportingWidget_show_plot_placeholder .legend").mouseenter(function(){
		 				hideLegend("#reportingWidget_show_plot_placeholder .legend");
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
	    
	    function hideLegend(legend) {
			$(legend).children().fadeOut();
			$(legend).append("<button onclick=\"showLegend('"+legend+"');\" style=\"position:absolute;top:9px;right:9px;\">Legenda</button>");
	    }
	    function showLegend(legend) {
	    	$(legend).children().not("button").fadeIn();
	    	$(legend).children("button").fadeOut();
	    }
	</g:javascript>
	
	<table class="reportingWidget_dataTable">
		<thead>
			<tr>
				<th>Panoga</th>
				<th>Skupno ${unitName}</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${minutePoPanogah}" var="minutePoPanogi">
				<tr>
					<td>${minutePoPanogi.key}</td>
					<td>${minutePoPanogi.value} ${unitName}</td>
				</tr>
			</g:each>
		</tbody>
	</table>
	
	<table class="reportingWidget_dataTable">
		<thead>
			<tr>
				<th>Datum</th>
				<th>Skupno ${unitName}</th>
				<th>Aktivnosti</th>
			</tr>
		</thead>
		<tbody>
			<g:each in="${aktivnostiPoDatumih}" var="datumAkt">
				<tr>
					<td class="epochToDate">${datumAkt.key}</td>
					<g:set var="datumAktSkupajMin" value="0" />
					<g:set var="datumAktOpisAktivnosti" value="" />
					<g:each in="${datumAkt.value}" var="datumAktVnos">
						<g:set var="datumAktSkupajMin" value="${(datumAktSkupajMin as double)+(datumAktVnos.vrednost as double)}" />
						<g:set var="datumAktOpisAktivnosti" value="${datumAktOpisAktivnosti+"/liOpen/"+datumAktVnos.panoga+" ("+datumAktVnos.vrednost+" " + unitName + ")/liClose/"}" />
					</g:each>
					<td>${datumAktSkupajMin} ${unitName}</td>
					<td><ul>${datumAktOpisAktivnosti.replaceAll("/liOpen/","<li style=\"font-family: monospace;\">").replaceAll("/liClose/","</li>")}</ul></td>
				</tr>
			</g:each>
		</tbody>
	</table>
	
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
