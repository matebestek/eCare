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
<g:javascript>
	var reportingWidget_quickGraph_plot_data = [
		{
			color: "green",
   			data: ${dataString},
   			lines: { show: true },
			points: { show: false },
   			constraints: [
				{
				    threshold: ${threshold.bad},
				    color: "red",
				    evaluate : function(y,threshold){ return y ${threshold.higherIsBetter ? "<" : ">"} threshold; }
				},
				{
				    threshold: ${threshold.neutral},
				    color: "yellow",
				    evaluate : function(y,threshold){ return y ${threshold.higherIsBetter ? "<" : ">"} threshold; }
				}
			]
		},
		{
			color: "black",
   			data: ${dataString},
   			lines: { show: false },
			points: { show: true }
		}
	];
	var reportingWidget_quickGraph_plot_opts = {
		xaxis: {
			mode: "time",
			timeformat: "%d/%m/%y"
		}
	};
</g:javascript>
	
<flot:plot id="reportingWidget_quickGraph_plot_placeholder" data="reportingWidget_quickGraph_plot_data" options="reportingWidget_quickGraph_plot_opts" style="width: 450px; height: 200px;  margin-left: auto;margin-right: auto;" />
