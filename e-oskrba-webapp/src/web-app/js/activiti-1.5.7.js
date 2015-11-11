/***
 * Copyright (c) 2013.
 * University of Primorska, Andrej Marušič Institute. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * Project eOskrba (http://eOskrba.si) was financed by Slovenian Research
 * Agency and Ministry of Health of Republic of Slovenia.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/* 
 *   eOskrba-activiti
 *   version 1.5.7
 *   
 *   Aleksander Bešir
 *   alex.besir@gmail.com

Polja izgledajo lahko na te načine:
-----------------------------------
	
	<input type="text"     name="..." class="..." value="..." />
	<input type="password" name="..." class="..." value="..." />
	<input type="checkbox" name="..." class="..." value="..." />
	<input type="radio"    name="..." class="..." value="..." />
	
	<select name="..." class="...">
		<option value="...">...</option>
		<option value="...">...</option>
		...
	</select>
	
	<textarea class="..."><!-- optional value --></textarea>
	
Možne vrednosti class atributa:
-------------------------------

Atribut class ima lahko več vrednosti. Če jih ima več, se jih navede eno za drugo, vmes pa se napiše po 1 presledek.
Primer class polja z več vrednostmi: class="activiti-validation-string activiti-validation-req"


	Vrednosti class atribut-a za določanje tipa:
	--------------------------------------------
	
	* activiti-validation-string            [Privzeto] Vrednost je lahko kakršen koli string
	* activiti-validation-int               Vrednost mora biti celo število (lahko predznačeno)
	* activiti-validation-float             Vrednost mora biti število z vejico ali celo število (lahko predznačeno)
	* activiti-validation-date              Vrednost mora biti datum. Doda datePicker za izbiro.
	* activiti-validation-email             Vrednost mora biti oblike nekaj@nekaj.nekaj
	* activiti-validation-emailUnique       Vrednost mora biti oblike nekaj@nekaj.nekaj in v LDAP-u ne sme obstajati uporabnik s tem email naslovom
	
	Opomba: polja <input type="checkbox" />, <input type="radio" />, <select></select> in <textarea></textarea> ne morejo imeti teh class atributov.
	
	Vrednosti class atribut-a za določanje obveznosti polja:
	--------------------------------------------------------

	* activiti-validation-opt               [Privzeto] Polje ni obvezno
	* activiti-validation-req               Polje je obvezno
	
	Opomba: polje <input type="checkbox" /> ne more biti obvezno, ker je vedno true ali false. Vsa ostala polja so lahko obvezna ali ne.

	Vrednosti class atribut-a za določanje velikosti polja:
	-------------------------------------------------------
	
	* activiti-size-short                   Polje je krajše od normalne velikosti
	* activiti-size-normal                  [Privzeto] Polje je normalno dolgo
	* activiti-size-long                    Polje je daljše kot normalno.
	
	Dodatno polje za ponoven vnos e-mail naslova
	--------------------------------------------
	
	Vsa polja s class-om 'activiti-validation-emailUnique' dobijo pod seboj še eno
	polje, ki služi temu, da mora uporabnik še enkrat vpisati e-mail naslov, ki mora
	biti enak temu (v nasportnem primeru dobi uporabnik opozorilo in validacija ne uspe).
	
	Poleg tega pa je možno tudi poljem s class-om 'activiti-validation-email' dodati
	tako polje. To se stori tako, da se polju dodeli še
	class 'activiti-validation-emailCheck'. Pozor: polja, ki imajo class
	'activiti-validation-emailCheck' morajo tako nujno imeti tudi class
	'activiti-validation-email'.
	
	Vrednosti class atribut-a za preverjanje obstoja poštnega strežnika
	-------------------------------------------------------------------
	
	Vsem poljem s class-om 'activiti-validation-email' lahko poželji dopišemo še
	calss 'activiti-validation-emailCheckServer', ki povzroči, da se pred oddajo
	obrazca preko nslookup orodja preveri, ali strežnik, na katerega kaže domena
	v vnesenem poštnem naslovu, res obstaja.
	
	To ne velja za polja class-a 'activiti-validation-emailUnique', pri katerih
	se to preverjanje že avtomatsko ZMERAJ počne.

Primer
------

Input polje normalne oblike, ki sprejme karkoli:

	<input type="text" name="imePacienta" />
	
Input polje, ki je kratko, obvezno in zahteva unikaten e-mail naslov:

	<input type="text" name="eMail" class="activiti-validation-emailUnique activiti-validation-req activiti-size-short" />
	
 */

var continueValidation = false;
function activiti_form_onLoad() {
	
	// Check if should make it accordion
	$("#activiti_form_holder").has("h3").accordion({
		autoHeight: false,
		change: function(event,ui) {
			if(ui.newHeader.html() != null) {
				$("#layout-panel-content").scrollTo( ui.newHeader, 500 );
			}
		}
	});
	
	// Style all fields
	$("#activiti_form_holder .activiti-size-short").not('input[type="radio"]').width(200);
	$('#activiti_form_holder input[type="radio"]').css("margin-left","50px");
	$('#activiti_form_holder input[type="text"].activiti-size-long').each(function(index){
		var obj = jQuery(this);
		obj.replaceWith(
				"<textarea name=\"" + obj.attr("name") + "\" id=\"" + obj.attr("name") + "\" class=\"" + obj.attr("class") + "\">" + obj.val() + "</textarea>"
		);
	});
	$("#activiti_form_holder .activiti-validation-perc").after('<span> &#37;</span>');
	
	// Style all tables
	$("#activiti_form_holder table").addClass("layout-tf");
	$("#activiti_form_holder table tr td:first-child").addClass("layout-tff-label").width(250);
	
	// Add null options to all select fields
	$("#activiti_form_holder select").prepend(
		"<option value=\"\" > -- izberite -- </option>"
	).val('');
	
	// Convert long textfields to textareas
	$('#activiti_form_holder textarea.activiti-size-long').each(function(index){
		var obj = jQuery(this);
		obj.height(20);
		obj.focusin(function() {
			obj.animate({height:'75px'},500);
		});
		obj.focusout(function() {
			obj.animate({height:'20px'},500);
		});
	});
	
	// Add mask - int
	// $("#activiti_form_holder .activiti-validation-int").mask("?99999999999999999999999999999999",{placeholder:""});
	$("#activiti_form_holder .activiti-validation-int").keydown(function(event) {
		if( !((event.which >= 48 && event.which <= 57) || (event.which >= 96 && event.which <= 105)) && !( event.which == 8 || event.which == 9 ) || event.shiftKey==1) {
			event.preventDefault();
		}
	});
	
	// Add mask - float
	$("#activiti_form_holder .activiti-validation-float").keydown(function(event) {
		// Check if dot already enetered
		var val = $('#activiti_form_holder input[name="' + event.target.name + '"]').val();
		if(event.which == 190 || event.which == 188) {
			if (val.indexOf(".") > -1) {
				event.preventDefault();
			}
		} else if( !((event.which >= 48 && event.which <= 57) || (event.which >= 96 && event.which <= 105)) && !( event.which == 8 || event.which == 9 ) || event.shiftKey==1) {
			event.preventDefault();
		}

	});
	$("#activiti_form_holder .activiti-validation-float").keyup(function(event) {
		var val = $('#activiti_form_holder input[name="' + event.target.name + '"]').val();
		$('#activiti_form_holder input[name="' + event.target.name + '"]').val( val.replace(/,/g,".") );
	});
	
	// Add mask - perc
	var activiti_validation_perc_oldVal = "";
	$("#activiti_form_holder .activiti-validation-perc").keydown(function(event) {
		// Check if dot already enetered
		var val = $('#activiti_form_holder input[name="' + event.target.name + '"]').val();
		if(event.which == 190 || event.which == 188) {
			if (val.indexOf(".") > -1) {
				event.preventDefault();
			}
		} else if( !((event.which >= 48 && event.which <= 57) || (event.which >= 96 && event.which <= 105)) && !( event.which == 8 || event.which == 9 )) {
			event.preventDefault();
		}
	});
	$("#activiti_form_holder .activiti-validation-perc").keyup(function(event) {
		var val = $('#activiti_form_holder input[name="' + event.target.name + '"]').val();
		$('#activiti_form_holder input[name="' + event.target.name + '"]').val( val.replace(/,/g,".") );
		var newVal = $('#activiti_form_holder input[name="' + event.target.name + '"]').val();
		var newValFloat = parseFloat(newVal);
		if(isNaN(newValFloat)) {
			$('#activiti_form_holder input[name="' + event.target.name + '"]').val(activiti_validation_perc_oldVal);
		} else if(newValFloat < 0 || newValFloat > 100) {
			$('#activiti_form_holder input[name="' + event.target.name + '"]').val(activiti_validation_perc_oldVal);
		} else if(newVal != "100.00" && newVal.length > 5) {
			$('#activiti_form_holder input[name="' + event.target.name + '"]').val(activiti_validation_perc_oldVal);
		} else {
			activiti_validation_perc_oldVal = $('#activiti_form_holder input[name="' + event.target.name + '"]').val();
		}
	});
	
	// Add mask - date
	$("#activiti_form_holder .activiti-validation-date").datepicker({ dateFormat: 'dd.mm.yy' });
	
	// Populate date fields
	var oneDayMillis = 86400000;
	var baseDate = new Date();
	var toDate = new Date();
	$("#activiti_form_holder .activiti-population-date-today").datepicker("setDate",toDate);
	var toDayInWeek = toDate.getDay(); // 0 = nedelja, 1 = ponedeljek
	var dateIncrement = 1 - toDayInWeek;
	if(toDayInWeek == 0) dateIncrement = -6;
	toDate = new Date(baseDate.getTime()+(dateIncrement*oneDayMillis));
	$("#activiti_form_holder .activiti-population-date-mon").datepicker("setDate",toDate);
	dateIncrement++;
	toDate = new Date(baseDate.getTime()+(dateIncrement*oneDayMillis));
	$("#activiti_form_holder .activiti-population-date-tue").datepicker("setDate",toDate);
	dateIncrement++;
	toDate = new Date(baseDate.getTime()+(dateIncrement*oneDayMillis));
	$("#activiti_form_holder .activiti-population-date-wed").datepicker("setDate",toDate);
	dateIncrement++;
	toDate = new Date(baseDate.getTime()+(dateIncrement*oneDayMillis));
	$("#activiti_form_holder .activiti-population-date-thu").datepicker("setDate",toDate);
	dateIncrement++;
	toDate = new Date(baseDate.getTime()+(dateIncrement*oneDayMillis));
	$("#activiti_form_holder .activiti-population-date-fri").datepicker("setDate",toDate);
	dateIncrement++;
	toDate = new Date(baseDate.getTime()+(dateIncrement*oneDayMillis));
	$("#activiti_form_holder .activiti-population-date-sat").datepicker("setDate",toDate);
	dateIncrement++;
	toDate = new Date(baseDate.getTime()+(dateIncrement*oneDayMillis));
	$("#activiti_form_holder .activiti-population-date-sun").datepicker("setDate",toDate);
	
	// Add mask - time
	$.mask.definitions['2']='[012]';
	$.mask.definitions['5']='[012345]';
	$("#activiti_form_holder .activiti-validation-time").mask("29:59",{placeholder:""}).width(40);
	$("#activiti_form_holder .activiti-validation-time").keyup(function(event){
		var v = $(this).val();
		var vArr = v.split(":");
		var v0 = parseInt(vArr[0]);
		if( !isNaN(v0) ) {
			if(vArr[0].length > 1) if(v0 > 23) {
				$("#activiti_form_holder .activiti-validation-time").unmask();
				$("#activiti_form_holder .activiti-validation-time").mask("29:59",{placeholder:""}).width(40);
			}
		}
	});
	
	// Add mask - phone number
	$("#activiti_form_holder .activiti-validation-phoneNum").mask("099-999-999",{placeholder:""});
	
	// Tag required fields
	$("#activiti_form_holder input.activiti-validation-req").each(function(index) {
		var elem = $(this);
		elem.css("background-color","#FFFBCA");
		if(elem.attr("type").toLowerCase() == "radio") {
			elem.parent().css("text-shadow","0 0 2px #FFF363");
		}
	});
	$("#activiti_form_holder select.activiti-validation-req").each(function(index) {
		var elem = $(this);
		
		elem.css("background-color","#FFFBCA");
	});
	$("#activiti_form_holder textarea.activiti-validation-req").each(function(index) {
		var elem = $(this);
		elem.css("background-color","#FFFBCA");
	});
	
	// WONCA prepare
	activiti_form_wonca_prepare();
	
	// Add live unique email checking
	$(".activiti-validation-emailUnique").each(function(index) {
		var elem = $(this);
		elem.blur(function() {
			getJSON(
				"../adminWidget/checkIfEmailAvailableJSON",
				{ email: elem.val() },
				function(jsonr) {
					if(!jsonr.available) {
						$("#activiti_form_holder").has("h3").accordion("activate",
							Math.floor($("#activiti_form_holder div").has(elem).index()/2)
						);
						$("#layout-panel-content").delay(500).scrollTo(elem,500);
						elem.delay(500).effect("highlight", { color: "#C00000" }, 5000);
						var currVal = elem.val();
						showFlashError('Registracija s poštnim naslovom "' + currVal + '" ni mogoča',jsonr.reason);
						elem.change(function(){ hideFlashMessage(); });
						continueValidation = false;
					}
				}
			);
		});
	});
	
	// Add re-enter-email-address check fields
	$(".activiti-validation-emailUnique,.activiti-validation-emailCheck").each(function(index) {
		var td1 = $(this).parent().parent().children("td:nth-child(1)");
		$(this).parent().parent().after("<tr><td class=\""+td1.attr("class")+"\" style=\""+td1.attr("style")+"\"><p style=\"color:#8A8A8A;\">&rsaquo; Ponovno vpišite naslov e-pošte</p></td><td><input type=\"text\" name=\""+$(this).attr("name")+"?check\" style=\""+$(this).attr("style")+"\" class=\"activiti-validation-emailCheckField\" activiti_id=\""+$(this).attr("activiti_id")+"?check\" /></td></tr>");
	});
	$(".activiti-validation-emailCheckField").each(function(index) {
		var elem = $(this);
		elem.blur(function() {
			var orig = $('input[name="' + (elem.attr("name")+"$").replace("?check$","") + '"]');
			if(elem.val() != orig.val()) {
				$("#activiti_form_holder").has("h3").accordion("activate",
					Math.floor($("#activiti_form_holder div").has(elem).index()/2)
				);
				$("#layout-panel-content").delay(500).scrollTo(elem,500);
				elem.delay(500).effect("highlight", { color: "#C00000" }, 5000);
				var currVal = elem.val();
				showFlashError('Vpisana e-poštna naslova se ne ujemata','Prosimo, popravite enega izmed vpisanh naslovov tako, da bosta enaka.');
				elem.change(function(){ hideFlashMessage(); });
				continueValidation = false;
			}
		});
	});
	
	// Add event listener to form
	$("form").has("#activiti_form_holder").submit(function(event) {
		
		event.preventDefault();

		loading = true;
		layout_forceLoadingRefresh();
		continueValidation = true;
		
		setTimeout(function(){
			
			// Replace new-line delimiters with HTML entities in text-areas
			$(".activiti-size-long").each(function() {
				$(this).val( $(this).val().replace(/\t*\r*\n\t*/g,"&#10;") );
			});
			
			var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/

			// Check email fields
			if(continueValidation) {
				$(".activiti-validation-email").each(function(index) {
					if( !$(this).val().match(re) && $($(this)).val() != "") {
						$("#activiti_form_holder").has("h3").accordion("activate",
							Math.floor($("#activiti_form_holder div").has($(this)).index()/2)
						);
						$("#layout-panel-content").delay(500).scrollTo($(this),500);
						$(this).delay(500).effect("highlight", { color: "#C00000" }, 5000);
						var currVal = $(this).val();
						showFlashError("Neveljavna vrednost",'"' + currVal + '" ni veljaven e-poštni naslov');
						$(this).change(function(){ hideFlashMessage(); });
						continueValidation = false;
						loading = false;
						return false;
					}
				});
			}
			
			// Check emailCheckField fields
			if(continueValidation) {
				$(".activiti-validation-emailCheckField").each(function(index) {
					var elem = $(this);
					var orig = $('input[name="' + (elem.attr("name")+"$").replace("?check$","") + '"]');
					if(elem.val() != orig.val()) {
						$("#activiti_form_holder").has("h3").accordion("activate",
							Math.floor($("#activiti_form_holder div").has(elem).index()/2)
						);
						$("#layout-panel-content").delay(500).scrollTo(elem,500);
						elem.delay(500).effect("highlight", { color: "#C00000" }, 5000);
						var currVal = elem.val();
						showFlashError('Vpisana e-poštna naslova se ne ujemata','Prosimo, popravite enega izmed vpisanh naslovov tako, da bosta enaka.');
						elem.change(function(){ hideFlashMessage(); });
						continueValidation = false;
						loading = false;
						return false;
					}
				});
			}
			
			// Check unique email fields if format ok
			if(continueValidation) {
				$(".activiti-validation-emailUnique").each(function(index) {
					if( !$(this).val().match(re) && $($(this)).val() != "") {
						$("#activiti_form_holder").has("h3").accordion("activate",
							Math.floor($("#activiti_form_holder div").has($(this)).index()/2)
						);
						$("#layout-panel-content").delay(500).scrollTo($(this),500);
						$(this).delay(500).effect("highlight", { color: "#C00000" }, 5000);
						var currVal = $(this).val();
						showFlashError("Neveljavna vrednost",'"' + currVal + '" ni veljaven e-poštni naslov');
						$(this).change(function(){ hideFlashMessage(); });
						continueValidation = false;
						loading = false;
						return false;
					}
				});
			}
			
			// Check if all required fields have value
			if(continueValidation) {
				$("#activiti_form_holder input.activiti-validation-req").each(function(index) {
					var elem = $(this);
					var elemType = elem.attr("type").toLowerCase();
					var elemName = elem.attr("name");
					if(elemType == "text" || elemType == "password") {
						if(elem.val() == "") {
							$("#activiti_form_holder").has("h3").accordion("activate",
								Math.floor($("#activiti_form_holder div").has(elem).index()/2)
							);
							$("#layout-panel-content").delay(500).scrollTo(elem,500);
							elem.delay(500).effect("highlight", { color: "#C00000" }, 5000);
							showFlashError("Manjkajoč podatek","Polja, ki je obvezno, niste izpolnili");
							elem.change(function(){ hideFlashMessage(); });
							continueValidation = false;
							loading = false;
							return false;
						}
					}
					else if(elemType == "radio") {
						var selectedCount = $('#activiti_form_holder input:radio[name="' + elemName + '"]:checked').length;
						if(selectedCount == 0) {
							$("#activiti_form_holder").has("h3").accordion("activate",
								Math.floor($("#activiti_form_holder div").has(elem).index()/2)
							);
							$("#layout-panel-content").delay(500).scrollTo(elem,500);
							elem.parent().delay(500).effect("highlight", { color: "#C00000" }, 5000);
							showFlashError("Manjkajoč podatek","Izbrati je treba natanko eno izmed možnosti");
							elem.change(function(){ hideFlashMessage(); });
							continueValidation = false;
							loading = false;
							return false;
						}
					}
				});
			}
			if(continueValidation) {
				$("#activiti_form_holder select.activiti-validation-req option:selected").each(function(index) {
					var elem = $(this);
					if(elem.val() == "") {
						$("#activiti_form_holder").has("h3").accordion("activate",
							Math.floor($("#activiti_form_holder div").has(elem).index()/2)
						);
						$("#layout-panel-content").delay(500).scrollTo(elem,500);
						elem.parent().delay(500).effect("highlight", { color: "#C00000" }, 5000);
						showFlashError("Manjkajoč podatek","V obveznem polju niste izbrali ničesar.");
						elem.change(function(){ hideFlashMessage(); });
						continueValidation = false;
						loading = false;
						return false;
					}
				});
			}
			
			// Check unique email fields if unique
			if(continueValidation) {
				$(".activiti-validation-emailUnique").each(function(index) {
					var field = this;
					if( $($(field)).val() != " ") {
						getJSONsync(
							"../adminWidget/checkIfEmailAvailableJSON",
							{ email: $(field).val() },
							function(jsonr) {
								if(!jsonr.available) {
									$("#activiti_form_holder").has("h3").accordion("activate",
										Math.floor($("#activiti_form_holder div").has($(field)).index()/2)
									);
									$("#layout-panel-content").delay(500).scrollTo($(field),500);
									$(field).delay(500).effect("highlight", { color: "#C00000" }, 5000);
									var currVal = $(field).val();
									showFlashError('Registracija s poštnim naslovom "' + currVal + '" ni mogoča',jsonr.reason);
									$(field).change(function(){ hideFlashMessage(); });
									continueValidation = false;
									loading = false;
									return false;
								}
							},
							function() {
								showFlashError('Validacija ni mogoča','Nekatere storitve, ki so potrebne za validacijo, trenutno niso na voljo.');
								$(field).change(function(){ hideFlashMessage(); });
								continueValidation = false;
								loading = false;
								return false;
							}
						);
					}
				});
			}
			
			// Check other email fields if server exists (only if 
			if(continueValidation) {
				$(".activiti-validation-emailCheckServer").each(function(index) {
					var field = this;
					if( $($(field)).val() != " ") {
						getJSONsync(
							"../adminWidget/checkIfEmailServerExistsJSON",
							{ email: $(field).val() },
							function(jsonr) {
								if(!jsonr.available) {
									$("#activiti_form_holder").has("h3").accordion("activate",
										Math.floor($("#activiti_form_holder div").has($(field)).index()/2)
									);
									$("#layout-panel-content").delay(500).scrollTo($(field),500);
									$(field).delay(500).effect("highlight", { color: "#C00000" }, 5000);
									var currVal = $(field).val();
									showFlashError('Uporaba poštnega naslova "' + currVal + '" ni mogoča',jsonr.reason);
									$(field).change(function(){ hideFlashMessage(); });
									continueValidation = false;
									loading = false;
									return false;
								}
							},
							function() {
								showFlashError('Validacija ni mogoča','Nekatere storitve, ki so potrebne za validacijo, trenutno niso na voljo.');
								$(field).change(function(){ hideFlashMessage(); });
								continueValidation = false;
								loading = false;
								return false;
							}
						);
					}
				});
			}
			
			//parse 'phoneNum' number
			if(true) {
				$("#activiti_form_holder input.activiti-validation-phoneNum").each(function(index) {
					var elem = $(this);
					elem.val(elem.val().replace("-",""));
					elem.val(elem.val().replace("-",""));
				});
			}
			
			// Wait and try to submit
			setTimeout("activiti_submit()",1000);
			
		},1000);
		
	});
	
}

function activiti_submit() {
	if(continueValidation) {
		$("form").has("#activiti_form_holder").unbind("submit").submit();
	} 
}

function activiti_form_assign_activiti_ids() {
	
	$("input").each(function(){
		$(this).attr("activiti_id",$(this).attr("name").replace(/ and [^\]]*/g,""));
	});
	
}

function activiti_form_fillWithData(data) {
	
	activiti_form_assign_activiti_ids();
	
	// Napolni s podatki
	for(var i = 0; i < data.length; i++) {
		$("input[type='text'][activiti_id='" + data[i][0] + "']").val(data[i][1]);
		$("input[type='radio'][activiti_id='" + data[i][0] + "'][value='" + data[i][1] + "']").prop("checked", true);
		$("select[name='" + data[i][0] + "'] option[value='" + data[i][1] + "']").attr("selected","selected");
		$("textarea[name='" + data[i][0] + "']").html(data[i][1]);
		
	}
	
	// wonca
	activiti_form_wonca_update();
	
}

function activiti_form_fillWithDataReadonly(data) {
	
	// Napolni s podatki
	activiti_form_fillWithData(data);
	
	// Naredi vsa polja readonly
	$("#activiti_form_holder").find("input").unmask();
	$("#activiti_form_holder input[type='text']").attr('readonly','readonly');
	$("#activiti_form_holder textarea").attr('readonly','readonly');
	$("#activiti_form_holder .activiti-validation-date").datepicker("destroy");
	$("#activiti_form_holder option").not(":selected").attr('disabled', 'disabled');
	$("#activiti_form_holder select").each(function(){
		$(this).children(":first").html(" ");
	});
	$("#activiti_form_holder input[type='radio']").not(":checked").attr('disabled', 'disabled');
	
}

function activiti_form_wonca_prepare() {
	
	//add images 
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0006]?value']").after("<span style=\"position:relative; height:1px; width:1px;\"><img id=\"pic6\" src=\"\" style=\"position:absolute; left:10px;\"/></span>");
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0005]?value']").after("<span style=\"position:relative; height:1px; width:1px;\"><img id=\"pic5\" src=\"\" style=\"position:absolute; left:10px;\"/></span>");
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0008]?value']").after("<span style=\"position:relative; height:1px; width:1px;\"><img id=\"pic8\" src=\"\" style=\"position:absolute; left:10px;\"/></span>");
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0007]?value']").after("<span style=\"position:relative; height:1px; width:1px;\"><img id=\"pic7\" src=\"\" style=\"position:absolute; left:10px;\"/></span>");
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0010]?value']").after("<span style=\"position:relative; height:1px; width:1px;\"><img id=\"pic10\" src=\"\" style=\"position:absolute; left:10px;\"/></span>");
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0011]?value']").after("<span style=\"position:relative; height:1px; width:1px;\"><img id=\"pic11\" src=\"\" style=\"position:absolute; left:10px;\"/></span>");
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0009]?value']").after("<span style=\"position:relative; height:1px; width:1px;\"><img id=\"pic9\" src=\"\" style=\"position:absolute; left:10px;\"/></span>");
		
	//add appropriate link to the images
	var subDir = "";		
	switch($('input[name="woncaImagesSubDir"]').val()){
		case "enterDiabetes": subDir = "../images/WONCA/"; break;
		case "vnosDiabetes": subDir = "/eOskrba-webapp/images/WONCA/"; break;
	}
	
	/********<select> pocutje***********/
	//set link to the image
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0006]?value']").change(function(){
		var newSrc = "";
		var height = "";
		
		switch($(this).val()){
			case "at0017": newSrc = subDir + "pocutje/Slika_01.png"; height = 57; break;
			case "at0018": newSrc = subDir + "pocutje/Slika_02.png"; height = 57; break;
			case "at0019": newSrc = subDir + "pocutje/Slika_03.png"; height = 57; break;
			case "at0020": newSrc = subDir + "pocutje/Slika_04.png"; height = 57; break;
			case "at0021": newSrc = subDir + "pocutje/Slika_05.png"; height = 57; break;
			default: newSrc = subDir + "no_picture.png"; height=0;
		}			
		$("#pic6").attr("src",newSrc).css("height",height);
	});
	
	/********<select> custva***********/
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0005]?value']").change(function(){
		var newSrc = "";
		var height ="";
		
		switch($(this).val()){
			case "at0022": newSrc = subDir + "custva/Slika_06.png"; height = 74; break;
			case "at0023": newSrc = subDir + "custva/Slika_07.png"; height = 74; break;
			case "at0024": newSrc = subDir + "custva/Slika_08.png"; height = 74; break;
			case "at0025": newSrc = subDir + "custva/Slika_09.png"; height = 74; break;
			case "at0026": newSrc = subDir + "custva/Slika_10.png"; height = 74; break;
			default: newSrc = subDir + "no_picture.png"; height=0;
		}
		$("#pic5").attr("src",newSrc).css("height",height);
	});
	
	/********<select> opravila***********/
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0008]?value']").change(function(){
		var newSrc = "";
		var height ="";
		
		switch($(this).val()){
			case "at0027": newSrc = subDir + "opravila/Slika_21.png"; height = 74; break;
			case "at0028": newSrc = subDir + "opravila/Slika_22.png"; height = 74; break;
			case "at0029": newSrc = subDir + "opravila/Slika_23.png"; height = 74; break;
			case "at0030": newSrc = subDir + "opravila/Slika_24.png"; height = 74; break;
			case "at0031": newSrc = subDir + "opravila/Slika_25.png"; height = 74; break;
			default: newSrc = subDir + "no_picture.png"; height=0;
		}
		$("#pic8").attr("src",newSrc).css("height",height);
	});
	
	/********<select> druzabno***********/
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0007]?value']").change(function(){
		var newSrc = "";
		var height ="";
		
		switch($(this).val()){
			case "at0032": newSrc = subDir + "druzba/Slika_16.png"; height = 74; break;
			case "at0033": newSrc = subDir + "druzba/Slika_17.png"; height = 74; break;
			case "at0034": newSrc = subDir + "druzba/Slika_18.png"; height = 74; break;
			case "at0035": newSrc = subDir + "druzba/Slika_19.png"; height = 74; break;
			case "at0036": newSrc = subDir + "druzba/Slika_20.png"; height = 74; break;
			default: newSrc = subDir + "no_picture.png"; height=0;
		}
		$("#pic7").attr("src",newSrc).css("height",height);
	});
	
	/********<select> zdravje***********/
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0010]?value']").change(function(){
		var newSrc = "";
		var height ="";
		
		switch($(this).val()){
			case "at0037": newSrc = subDir + "zdravje/Slika_15.png"; height = 57; break;
			case "at0038": newSrc = subDir + "zdravje/Slika_16.png"; height = 57; break;
			case "at0039": newSrc = subDir + "zdravje/Slika_17.png"; height = 57; break;
			case "at0040": newSrc = subDir + "zdravje/Slika_18.png"; height = 57; break;
			case "at0041": newSrc = subDir + "zdravje/Slika_19.png"; height = 57; break;
			default: newSrc = subDir + "no_picture.png"; height=0;
		}
		$("#pic10").attr("src",newSrc).css("height",height);
	});
	
	/********<select> splosno***********/
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0011]?value']").change(function(){
		var newSrc = "";
		var height ="";
		
		switch($(this).val()){
			case "at0042": newSrc = subDir + "custva/Slika_06.png"; height = 57; break;
			case "at0043": newSrc = subDir + "custva/Slika_07.png"; height = 57; break;
			case "at0044": newSrc = subDir + "custva/Slika_08.png"; height = 57; break;
			case "at0045": newSrc = subDir + "custva/Slika_09.png"; height = 57; break;
			case "at0046": newSrc = subDir + "custva/Slika_10.png"; height = 57; break;
			default: newSrc = subDir + "no_picture.png"; height=0;
		}
		$("#pic11").attr("src",newSrc).css("height",height);
	});
	
	/********<select> bolecina***********/
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0009]?value']").change(function(){
		var newSrc = "";
		var height ="";
		
		switch($(this).val()){
			case "at0012": newSrc = subDir + "custva/Slika_06.png"; height = 57; break;
			case "at0013": newSrc = subDir + "custva/Slika_07.png"; height = 57; break;
			case "at0014": newSrc = subDir + "custva/Slika_08.png"; height = 57; break;
			case "at0015": newSrc = subDir + "custva/Slika_09.png"; height = 57; break;
			case "at0016": newSrc = subDir + "custva/Slika_10.png"; height = 57; break;
			default: newSrc = subDir + "no_picture.png"; height=0;
		}
		$("#pic9").attr("src",newSrc).css("height",height);
	});
	
}

function activiti_form_wonca_update() {
	
	// Ko je vse to nastavljeno, sprožimo change evente še 'ročno' zato, da se
	// že na začetku prikažejo slike ob polji
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0006]?value']").change();
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0005]?value']").change();
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0008]?value']").change();
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0007]?value']").change();
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0010]?value']").change();
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0011]?value']").change();
	$("select[name='?items[openEHR-EHR-OBSERVATION.wonca!vprasalnik!eo!di.v1]?data[at0001]?events[at0002]?data[at0003]?items[at0004]?items[at0009]?value']").change();
	
}

/* 
 *  Function collapses accordions.
 */
function collapseAccordions() {
	// Check if should make it accordion
	$("#activiti_form_holder").has("h3").accordion({
    	collapsible: true, 
    	autoHeight: false, 
    	active: false 
	});
}

function callPrint(){
	$( "#activiti_form_holder" ).print();
}

//Create a jquery plugin that prints the given element.
jQuery.fn.print = function(){
	// NOTE: We are trimming the jQuery collection down to the
	// first element in the collection.
	if (this.size() > 1){
		this.eq( 0 ).print();
		return;
	} else if (!this.size()){
		return;
	}
 
	// ASSERT: At this point, we know that the current jQuery
	// collection (as defined by THIS), contains only one
	// printable element.
 
	// Create a random name for the print frame.
	var strFrameName = ("printer-" + (new Date()).getTime());
 
	// Create an iFrame with the new name.
	var jFrame = $( "<iframe name='" + strFrameName + "'>" );
 
	// Hide the frame (sort of) and attach to the body.
	jFrame
		.css( "width", "1px" )
		.css( "height", "1px" )
		.css( "position", "absolute" )
		.css( "left", "-9999px" )
		.appendTo( $( "body:first" ) )
	;
 
	// Get a FRAMES reference to the new frame.
	var objFrame = window.frames[ strFrameName ];
 
	// Get a reference to the DOM in the new frame.
	var objDoc = objFrame.document;
 
	// Grab all the style tags and copy to the new
	// document so that we capture look and feel of
	// the current document.
 
	// Create a temp document DIV to hold the style tags.
	// This is the only way I could find to get the style
	// tags into IE.
	/*var jStyleDiv = $( "<div>" ).append(
		$( "style" ).clone()
		);*/
	var i,k;
	var dnevi = new Array("Ponedeljek", "Torek", "Sreda", "Četrtek", "Petek", "Sobota", "Nedelja");
	var zajtrk = new Array();
	var dopMalica = new Array();
	var kosilo = new Array();
	var popMalica = new Array();
	var vecerja = new Array();
	var drugo = new Array();
	var datum = new Array();
	var trajanje = new Array();
	var komentar = new Array();
	
	var panoga = new Array();
	var intenzivnost = new Array();
	var getPanoga = new Array();
	var getIntenzivnost = new Array();
	
	for(i=0; i<7; i++){
		if(i<=5){
			zajtrk[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at000'+ (i+4) +']?items[openEHR-EHR-CLUSTER.prehrana!dnevna!eo.v1]?items[at0001]?value"]').val();
			dopMalica[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at000'+ (i+4) +']?items[openEHR-EHR-CLUSTER.prehrana!dnevna!eo.v1]?items[at0002]?value"]').val();
			kosilo[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at000'+ (i+4) +']?items[openEHR-EHR-CLUSTER.prehrana!dnevna!eo.v1]?items[at0003]?value"]').val();
			popMalica[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at000'+ (i+4) +']?items[openEHR-EHR-CLUSTER.prehrana!dnevna!eo.v1]?items[at0004]?value"]').val();
			vecerja[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at000'+ (i+4) +']?items[openEHR-EHR-CLUSTER.prehrana!dnevna!eo.v1]?items[at0005]?value"]').val();
			drugo[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at000'+ (i+4) +']?items[openEHR-EHR-CLUSTER.prehrana!dnevna!eo.v1]?items[at0006]?value"]').val();
			datum[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at000'+ (i+4) +']?items[openEHR-EHR-CLUSTER.prehrana!dnevna!eo.v1]?items[at0007]?value"]').val();
			getPanoga[i] = $('select[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at000'+ (i+4) +']?items[openEHR-EHR-CLUSTER.gibalna!aktivnost!dnevna!eo.v1]?items[at0002]?value"]').val();
			trajanje[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at000'+ (i+4) +']?items[openEHR-EHR-CLUSTER.gibalna!aktivnost!dnevna!eo.v1]?items[at0004]?value"]').val();
			getIntenzivnost[i] = $('select[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at000'+ (i+4) +']?items[openEHR-EHR-CLUSTER.gibalna!aktivnost!dnevna!eo.v1]?items[at0059]?value"]').val();			
			komentar[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at000'+ (i+4) +']?items[openEHR-EHR-CLUSTER.gibalna!aktivnost!dnevna!eo.v1]?items[at0005]?value"]').val();
		}
		if(i==6){
			zajtrk[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at00'+ (i+4) +']?items[openEHR-EHR-CLUSTER.prehrana!dnevna!eo.v1]?items[at0001]?value"]').val();
			dopMalica[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at00'+ (i+4) +']?items[openEHR-EHR-CLUSTER.prehrana!dnevna!eo.v1]?items[at0002]?value"]').val();
			kosilo[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at00'+ (i+4) +']?items[openEHR-EHR-CLUSTER.prehrana!dnevna!eo.v1]?items[at0003]?value"]').val();
			popMalica[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at00'+ (i+4) +']?items[openEHR-EHR-CLUSTER.prehrana!dnevna!eo.v1]?items[at0004]?value"]').val();
			vecerja[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at00'+ (i+4) +']?items[openEHR-EHR-CLUSTER.prehrana!dnevna!eo.v1]?items[at0005]?value"]').val();
			drugo[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at00'+ (i+4) +']?items[openEHR-EHR-CLUSTER.prehrana!dnevna!eo.v1]?items[at0006]?value"]').val();
			datum[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at00'+ (i+4) +']?items[openEHR-EHR-CLUSTER.prehrana!dnevna!eo.v1]?items[at0007]?value"]').val();
			getPanoga[i] = $('select[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at00'+ (i+4) +']?items[openEHR-EHR-CLUSTER.gibalna!aktivnost!dnevna!eo.v1]?items[at0002]?value"]').val();
			trajanje[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at00'+ (i+4) +']?items[openEHR-EHR-CLUSTER.gibalna!aktivnost!dnevna!eo.v1]?items[at0004]?value"]').val();
			getIntenzivnost[i] = $('select[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at00'+ (i+4) +']?items[openEHR-EHR-CLUSTER.gibalna!aktivnost!dnevna!eo.v1]?items[at0059]?value"]').val();
			komentar[i] = $('input[name="?items[openEHR-EHR-OBSERVATION.tedenski!nacrt!eo.v1]?data[at0001]?events[at0002]?data[at0003]?items[at00'+ (i+4) +']?items[openEHR-EHR-CLUSTER.gibalna!aktivnost!dnevna!eo.v1]?items[at0005]?value"]').val();
		}
	}
	
	for(i=0; i<7; i++){
		switch(getPanoga[i]){
			case "at0023": panoga[i] = "Aerobika (različne oblike)"; break;
			case "at0050": panoga[i] = "Cooperjev test - 2400 m"; break;
			case "at0051": panoga[i] = "Cooperjev test - 12 min"; break;
			case "at0024": panoga[i] = "Fitnes (delo z utežmi)"; break;
			case "at0025": panoga[i] = "Hoja"; break;
			case "at0026": panoga[i] = "Igre z loparjem (tenis, badminton, squash)"; break;
			case "at0027": panoga[i] = "Igre z žogo (nogomet, košarka, odbojka)"; break;
			case "at0028": panoga[i] = "Kolesarjenje (cestno, gorsko)"; break;
			case "at0032": panoga[i] = "Kolesarjenje (sobno)"; break;
			case "at0029": panoga[i] = "Plavanje"; break;
			case "at0030": panoga[i] = "Pohodništvo"; break;
			case "at0031": panoga[i] = "Rolanje"; break;
			case "at0054": panoga[i] = "Smučanje"; break;
			case "at0047": panoga[i] = "Sproščanje"; break;
			case "at0033": panoga[i] = "Tek"; break;
			case "at0055": panoga[i] = "Tek na smučeh"; break;
			case "at0049": panoga[i] = "Test hoje 2 km"; break;
			case "at0044": panoga[i] = "Vaje za gibljivost"; break;
			case "at0046": panoga[i] = "Vaje za koordinacijo"; break;
			case "at0045": panoga[i] = "Vaje za moč (doma, ne v fitnesu)"; break;
			case "at0034": panoga[i] = "Drugo"; break;
			default: panoga[i] = "Ni izbrana";
		}
	}
	
	for(i=0; i<7; i++){
		switch(getIntenzivnost[i]){
			case "at0061": intenzivnost[i] = "Zelo nizka intenzivnost"; break;
			case "at0062": intenzivnost[i] = "Nizka intenzivnost"; break;
			case "at0063": intenzivnost[i] = "Zmerna intenzivnost"; break;
			case "at0064": intenzivnost[i] = "Visoka intenzivnost"; break;
			case "at0065": intenzivnost[i] = "Zelo visoka intenzivnost"; break;
			default: intenzivnost[i] ="Ni izbrana";
		}
	}
	// Write the HTML for the document into <iframe>. 
	objDoc.open();
	objDoc.write( "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" );
	objDoc.write( "<html>" );
	objDoc.write( "<body>" );
	objDoc.write( "<head>" );
	objDoc.write( '<meta http-equiv="content-type" content="text/html; charset=UTF-8">' );
	objDoc.write( "<title>" );
	objDoc.write( "Tedenski načrt prehrane" );
	objDoc.write( "</title>" );
	objDoc.write( "<body>" );
	objDoc.write( '<span style="font-size:12px; text-weight: normal;">' );
	
	for(k=0; k<7; k++){
		objDoc.write( '<span style="font-size:14px; font-weight:bold">' + dnevi[k] +' (' + datum[k]+'):</span></br>' );
		objDoc.write( '<b>Prehrana:</b>' );
		objDoc.write( '<table border="0" width="100%">' );
		
		objDoc.write( '<tr><td width="20"></td><td width="70">Zajtrk:</td>' );
		objDoc.write( '<td><span style=\'color:#999999;\'>' +zajtrk[k] +'</span></td></tr>');
		
		objDoc.write( '<tr><td></td><td>Dop. malica:</td> ' );
		objDoc.write( '<td><span style=\'color:#999999;\'>' +dopMalica[k]+'</span></td></tr>');
		
		objDoc.write( '<tr><td></td><td>Kosilo:</td> ' );
		objDoc.write( '<td><span style=\'color:#999999;\'>' +kosilo[k]+'</span></td></tr>');
		
		objDoc.write( '<tr><td></td><td>Pop. malica:</td> ' );
		objDoc.write( '<td><span style=\'color:#999999;\'>' +popMalica[k]+'</span></td></tr>');
		
		objDoc.write( '<tr><td></td><td>Večerja: </td>' );
		objDoc.write( '<td><span style=\'color:#999999;\'>' +vecerja[k]+'</span></td></tr>');
		
		objDoc.write( '<tr><td></td><td>Drugo: </td>' );
		objDoc.write( '<td><span style=\'color:#999999;\'>' +drugo[k]+'</span></td></tr>');
		objDoc.write( '</table>');
		
		
		objDoc.write( '<b>Gibanje</b>:' );
		objDoc.write( '<table border="0" width="100%">' );
		
		objDoc.write( '<tr><td width="20"></td><td width="110">Panoga:</td> ' );
		objDoc.write( '<td><span style=\'color:#999999;\'>' +panoga[k] +'</span></td></tr>');
		
		objDoc.write( '<tr><td></td><td>Trajanje (HH:MM): </td>' );
		objDoc.write( '<td><span style=\'color:#999999;\'>' +trajanje[k] +'</span></td></tr>');
		
		objDoc.write( '<tr><td></td><td>Intenzivnost vadbe: </td>' );
		objDoc.write( '<td><span style=\'color:#999999;\'>' +intenzivnost[k] +'</span></td></tr>');
		
		objDoc.write( '<tr><td></td><td>Komentar: </td>' );
		objDoc.write( '<td><span style=\'color:#999999;\'>' +komentar[k] +'</span></td></tr>' );
		objDoc.write( '</table>');
		objDoc.write( '<hr>' );
	}
	objDoc.write( "</span>" );
	objDoc.write( "</body>" );
	objDoc.write( "</html>" );
	objDoc.close();
 
	// Print the document.
	objFrame.focus();
	objFrame.print();
 
	// Have the frame remove itself in about a minute so that
	// we don't build up too many of these frames.
	setTimeout(
		function(){
			jFrame.remove();
		},
		(60 * 1000)
		);
}
