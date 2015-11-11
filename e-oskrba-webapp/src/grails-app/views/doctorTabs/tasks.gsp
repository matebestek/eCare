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
<html>
<head>
	<title>Moje naloge</title>
	<meta name="layout" content="main" />
</head>
<body>

	<eo:widgetFrame>
	
		<eo:staticWidget title="O mojih nalogah" id="doctorTabs_tasks_intro">
			<p>Tukaj imate pregled nad vsemi nalogami, ki jih še morate opraviti.</p>
		</eo:staticWidget>
		
		<eo:widget title="Moje naloge" load="myTasksWidget" action="show">
			<g:javascript>
				function myTasksWidget_show_onShowForm(task_id) {
					panel_load(
						"myTasksWidget",
						"form", {
							id: task_id,
							redirectToController: 'doctorTabs',
							redirectToAction: 'tasks'
						}
					);
				}
				function myTasksWidget_show_onShowTask(task_id) {
					myTasksWidget_show_onShowForm(task_id);
				}
			</g:javascript>
		</eo:widget>
	
	</eo:widgetFrame>
	
	<eo:panelFrame />

</body>
</html>
