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
		Priporočila. Urejanje.
	</eo:panelHeadInfo>
	<h1>${item.title} (urejanje)</h1>
</eo:panelHead>
<eo:panelContent>

	<eo:tfjs lwidth="120px" onSubmit="adviceWriterWidget_edit_formSubmit">	
		<eo:tff label="Naslov (kratek opis)">
			<g:textField name="adviceWriterWidget_edit_input_title" value="${item.title}" />
		</eo:tff>
		<eo:tff label="Besedilo">
			<eo:ckeditorConfig />
			<ckeditor:editor name="adviceWriterWidget_edit_input_content" removeInstance="true">${item.content}</ckeditor:editor>
		</eo:tff>
		<eo:tff label="Veljavnost">
			od <eo:datePicker name="adviceWriterWidget_edit_input_dateStart" value="${item.dateStart}" precision="day"/><br />
			do <eo:datePicker name="adviceWriterWidget_edit_input_dateEnd" value="${item.dateEnd}" precision="day" />
		</eo:tff>
		<eo:tff label="Pomembnost">
			<g:select name="adviceWriterWidget_edit_input_priority" from="${1..100}" value="${item.priority}" />
		</eo:tff>
		<eo:tff label="&nbsp;">
			<g:submitButton name="adviceWriterWidget_edit_input_submit" value="Shrani spremembe" />
		</eo:tff>
	
		<g:javascript>
			function adviceWriterWidget_edit_formSubmit(){
				
				var dateStart_day = $('#adviceWriterWidget_edit_input_dateStart_day').val();
				if(dateStart_day < 10) dateStart_day = "0" + dateStart_day;
				var dateStart_month = $('#adviceWriterWidget_edit_input_dateStart_month').val();
				if(dateStart_month < 10) dateStart_month = "0" + dateStart_month;
				
				var dateEnd_day = $('#adviceWriterWidget_edit_input_dateEnd_day').val();
				if(dateEnd_day < 10) dateEnd_day = "0" + dateEnd_day;
				var dateEnd_month = $('#adviceWriterWidget_edit_input_dateEnd_month').val();
				if(dateEnd_month < 10) dateEnd_month = "0" + dateEnd_month;
				
				adviceWriterWidget_edit_onSubmit(
					${item.id},
					$('#adviceWriterWidget_edit_input_title').val(),
					$('#adviceWriterWidget_edit_input_content').val(),
					dateStart_day + "-" + dateStart_month + "-" + $('#adviceWriterWidget_edit_input_dateStart_year').val(),
					dateEnd_day + "-" + dateEnd_month + "-" + $('#adviceWriterWidget_edit_input_dateEnd_year').val(),
					$('#adviceWriterWidget_edit_input_priority').val()
				);
				
			}
		</g:javascript>
	</eo:tfjs>

</eo:panelContent>
