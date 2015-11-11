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
 *   eOskrba-eoskrba.js
 *   version 1.5.3
 *   
 *   Aleksander Bešir
 *   alex.besir@gmail.com
 */

// some dynamic vars
var loading = false;

// Data manager functions

function startDataLoop() {
	// Loop
	setTimeout("dataLoop()",15000);
}

function dataLoop() {
	checkNewTasks();
	setTimeout("dataLoop()",15000);
}

function checkNewTasks() {
	getJSON("../myTasksWidget/updateMyTasksNumberJSON", {}, function(json) {
		if(json.risen) {
			showFlashWarning(
				"Nova naloga!",
				"Nova naloga Vam je bila pravkar dodeljena."
			);
			//$("#beep")[0].play();
		}
		if(json.changed) updateInfoboxTasksNum();
	});
}

function updateInfoboxTasksNum() {
	$.ajax({
		url: "../sessionInfoWidget/show?timestamp=" + timestamp(),
		cache: false,
		success: function(res){
			$("#layout-infobox-myTasksNum").replaceWith(res);
		},
		error: function() {
			testSession();
		}
	});
}

// Functions for widget interaction

function postWithFlash(url,data,afterComplete) {
	loading = true;
	$.ajax({
		type: 'POST',
		cache: false,
		url: url,
		data: data,
		success: function(data, textStatus, jqXHR) {
			showFlashMessage(data);
		},
		complete: afterComplete,
		error: function() {
			testSession();
		}
	});
}

function showFlashMessage(content) {
	$('#layout-ajaxResponseHolder-frame').html(content);
	loading = false;
	$('#layout-ajaxResponseHolder').fadeIn();
}

function hideFlashMessage() {
	$('#layout-ajaxResponseHolder').fadeOut();
}

function showFlashError(title, message) {
	$.ajax({
		type: 'GET',
		url: "../publicContent/actionReport?" +
			"type=error&" +
			"message=" + encodeURIComponent(title) + "&" +
			"message=" + encodeURIComponent(message) + "&" +
			"timestamp=" + timestamp(),
		success: function(data, textStatus, jqXHR) {
			showFlashMessage(data);
		},
		error: function() {
			testSession();
		}
	});
}

function showFlashWarning(title, message) {
	$.ajax({
		type: 'GET',
		url: "../publicContent/actionReport?" +
			"type=warning&" +
			"message=" + encodeURIComponent(title) + "&" +
			"message=" + encodeURIComponent(message) + "&" +
			"timestamp=" + timestamp(),
		success: function(data, textStatus, jqXHR) {
			showFlashMessage(data);
		},
		error: function() {
			testSession();
		}
	});
}

function showFlashSuccess(title, message) {
	$.ajax({
		type: 'GET',
		url: "../publicContent/actionReport?" +
			"type=success&" +
			"message=" + encodeURIComponent(title) + "&" +
			"message=" + encodeURIComponent(message) + "&" +
			"timestamp=" + timestamp(),
		success: function(data, textStatus, jqXHR) {
			showFlashMessage(data);
		},
		error: function() {
			testSession();
		}
	});
}

function loadWidgetToElement(widgetPath, elementSelector, data) {
	$.ajax({
		type: 'POST',
		cache: false,
		url: widgetPath,
		data: data,
		success: function(data, textStatus, jqXHR) {
			$(elementSelector).html(data);
			loadWidgetToElement_callback(elementSelector);
		},
		error: function() {
			testSession();
		}
	});
}

//Funkcija, ki se pokliče zmeraj, ko se v element naloži do konca vsebina
function loadWidgetToElement_callback(elementSelector) {
	
	// Sortabilne tabele
	$(elementSelector + " .layout-sortable-table").tablesorter();
	
	// Vse dokoncano
	loading = false;
	
}

// Panel special functions

// internal variables holding reference to currently loaded
// inner widget (and widget's action).
var eoskrba_internal_panelInnerWidget = "_undefined";
var eoskrba_internal_panelInnerAction = "_undefined";
var eoskrba_internal_panelReadyText   = "_undefined";

function panel_load(widget, action, paramsToLoad) {
//alert("TOLE JE: " + paramsToLoad);
	// show wait bar
	loading = true;
	// save to cookie
	var tabSelector = $("#layout-currentTabSelector").html();
	$.cookie(tabSelector + "_pw",widget, { path: '/' });
	$.cookie(tabSelector + "_pa",action, { path: '/' });
		// Gather params into a string and encode
		var paramsString = "timestamp=" + timestamp();
		jQuery.each(paramsToLoad, function(key, val){
			paramsString += "+" + key + "=" + val
		});
		var paramsEncoded = encodeURIComponent(paramsString);
	$.cookie(tabSelector + "_pp",paramsEncoded, { path: '/' });
	
	// store reference
	eoskrba_internal_panelInnerWidget = widget;
	eoskrba_internal_panelInnerAction = action;
	// load to panel
	$.ajax({
		type: 'POST',
		cache: false,
		url: "../" + widget + "/" + action,
		data: paramsToLoad,
		success: function(data, textStatus, jqXHR) {
			$('#layout-panelframe').html(data);
			panel_load_callback();
		},
		error: function(jqXHR, textStatus, errorThrown) {
			panel_load("panelWidget","show",{});
			testSession();
		}
	});
}

// Funkcija, ki se pokliče zmeraj, ko se v pane naloži do konca vsebina
function panel_load_callback() {
	
	// Sortabilne tabele
	$(".layout-sortable-table").tablesorter();
	
	// Vse dokoncano
	loading = false;
	
}

function panel_resetCookie() {
	var tabSelector = $("#layout-currentTabSelector").html();
	$.cookie(tabSelector + "_pw",null, { path: '/' });
	$.cookie(tabSelector + "_pa",null, { path: '/' });
	$.cookie(tabSelector + "_pp",null, { path: '/' });
}

function panel_resetAllCookies() {
	var c = document.cookie.split(";");
	for(var i = 0; i < c.length; i++){
		var e = c[i].indexOf("=");
		var n = e > -1 ? c[i].substr(0,e) : c[i];
		if(n.match(/(.*_pw)|(.*_pa)|(.*_pp)|(.*_w)/g) != null)
		document.cookie = n + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
	}
}

function panel_unload() {
	
	// clear content
	$('#layout-panelframe').html("");
	// Clear internal
	eoskrba_internal_panelInnerWidget = "_undefined";
	eoskrba_internal_panelInnerAction = "_undefined";
}

function panel_loadWithRedirect(widget, action, params) {
	// Get current controller and action
	var currController = $("#layout-currentControllerSelector").html();
	var currAction     = $("#layout-currentActionSelector").html();
	// Gather params into a string
	var paramsString = "timestamp=" + timestamp();
	jQuery.each(params, function(key, val){
		paramsString += "+" + key + "=" + val
	});
	// Encode
	var paramsEncoded = encodeURIComponent(paramsString);
	// Redirect
	window.location = "../" + currController + "/" + currAction + "?pw=" + widget + "&pa=" + action + "&pp=" + paramsEncoded;
}

function panel_loadWithRedirectToTab(tabName,widget, action, params) {
	// Get current controller and action
	var currController = $("#layout-currentControllerSelector").html();
	// Gather params into a string
	var paramsString = "timestamp=" + timestamp();
	jQuery.each(params, function(key, val){
		paramsString += "+" + key + "=" + val
	});
	// Encode
	var paramsEncoded = encodeURIComponent(paramsString);
	// Redirect
	window.location = "../" + currController + "/" + tabName + "?pw=" + widget + "&pa=" + action + "&pp=" + paramsEncoded;
}

// Input mask functions

function registerMask(input_id, mask) {
	$("#" + input_id).mask(mask,{placeholder:""});
}

// JSON utilities

function timestamp() {
	return (new Date()).getTime();
}

// Enhanced jQuery.getJSON function to produce unique request, so that
// IE doesn't cache results
function getJSON(url, data, success ) {
	data.timestamp = timestamp();
	$.getJSON(url, data , success).error(function() { testSession(); });
}

//Enhanced jQuery.getJSON function to produce unique request, so that
//IE doesn't cache results, but synchronously
function getJSONsync(url, data, success, error ) {
	data.timestamp = timestamp();
	$.ajax({
	  url: url,
	  dataType: 'json',
	  async: false,
	  data: data,
	  success: success,
	  error: error
	});
}

// Show person module
function showPerson(id) {
	$("#layout-showperson").fadeIn('fast', function() {
		$.ajax({
			type: 'POST',
			cache: false,
			url: "../showPerson/show",
			data: {id: id},
			success: function(data, textStatus, jqXHR) {
				$("#layout-showperson-frame").html(data);
			},
			error: function() {
				testSession();
			}
		});
	});
}

function hidePerson() {
	$("#layout-showperson").fadeOut('fast', function() {
		$("#layout-showperson-frame").html("");
	});
}

//Settings module
function showSettings() {
	$("#layout-settings").fadeIn('fast', function() {
		$.ajax({
			type: 'GET',
			cache: false,
			url: "../settings/show",
			success: function(data, textStatus, jqXHR) {
				$("#layout-settings-frame").html(data);
			},
			error: function() {
				testSession();
			}
		});
	});
}

function hideSettings() {
	$("#layout-settings").fadeOut('fast', function() {
		$("#layout-settings-frame").html("");
	});
}

// liveChatWidget notifier

function liveChatNotifier_startLoop() {
	var pending;
	liveChatNotifier_mainLoop();
}

function liveChatNotifier_mainLoop() {
	var pending;
	// Check if anybody pending for live chat
	getJSON("../liveChatWidget/checkPending", {}, function(json) {
		pending = json.pending;
		// If pending
		if(pending > 0) {
			$("#liveChatNotifier_notice_pendingNumber").html(pending);
			$("#liveChatNotifier_notice").css('display','inline');
			//$("#beep")[0].play();
		} else {
			$("#liveChatNotifier_notice").css('display','none');
		}
		// iterate
		setTimeout("liveChatNotifier_mainLoop()",3000);
	});
}

// Flot utilities
function getCSS(a){
    var sheets = document.styleSheets, o = {};
    for(var i in sheets) {
        var rules = sheets[i].rules || sheets[i].cssRules;
        for(var r in rules) {
        	try {
	            if(a.is(rules[r].selectorText)) {
	                o = $.extend(o, css2json(rules[r].style), css2json(a.attr('style')));
	            }
        	} catch(exception) { continue; }
        }
    }
    return o;
}

function css2json(css){
    var s = {};
    if(!css) return s;
    if(css instanceof CSSStyleDeclaration) {
        for(var i in css) {
            if((css[i]).toLowerCase) {
                s[(css[i]).toLowerCase()] = (css[css[i]]);
            }
        }
    } else if(typeof css == "string") {
        css = css.split("; ");          
        for (var i in css) {
            var l = css[i].split(": ");
            s[l[0].toLowerCase()] = (l[1]);
        };
    }
    return s;
}

// Session control utilities
function testSession() {
	$.ajax({
	  url: "../sessionInfoWidget/test?timestamp=" + timestamp(),
	  dataType: 'json',
	  success: function(json){
		  if(json.lost == 1) showSessionLostMsg();
	  },
	  error: function() {
		  showSessionLostMsg();
	  }
	});
}

function showSessionLostMsg() {
	$("#layout-sessionLost").show();
}
