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
 *   eOskrba-layout
 *   Version: 1.5
 *   
 *   Aleksander Bešir
 *   alex.besir(at)gmail.com
 *
 */
 
// constants
var layout_panelframe_safeslack = 5;
 
// dynamic vars
var layout_widgetframe_width = 250;
var layout_widgetcount;
var layout_currentWidget;
var layout_currentWidgetSelector;
var layout_currentTabSelector;
var layout_loadToPanelSelector_pw;
var layout_loadToPanelSelector_pa;
var layout_loadToPanelSelector_pp;
 
// holder vars
var layout_windowHeight;
var layout_windowWidth;
var layout_mainframe;
var layout_mainframe_wait;
var layout_widgetframe;
var layout_panelframe;
var layout_infobox;
 
$(document).ready(function() {
	layout_startMainLoop();
});

function layout_startMainLoop() {

	// Init.
	layout_widgetcount = $("#layout-widgetframe > div").size();
	
	layout_mainframe = $("#layout-mainframe");
	layout_mainframe_wait = $("#layout-mainframe-wait");
	layout_widgetframe = $("#layout-widgetframe");
	layout_panelframe = $("#layout-panelframe");
	layout_infobox = $("#layout-infobox");
	layout_currentWidgetSelector = "#" + $("#layout-currentWidgetSelector").html();
	layout_currentTabSelector = $("#layout-currentTabSelector").html();
	layout_loadToPanelSelector_pw = $("#layout-loadToPanelSelector-pw").html();
	layout_loadToPanelSelector_pa = $("#layout-loadToPanelSelector-pa").html();
	layout_loadToPanelSelector_pp = $("#layout-loadToPanelSelector-pp").html();
	
	// Store current tab in cookie
	$.cookie("eOskrba_layout_currentTab", layout_currentTabSelector, { path: '/' });
	
	// Get widgetToShow
	
	// Check if selected by params
	layout_currentWidget = layout_currentWidgetSelector;
	if(layout_currentWidget == "#") {
		// Check if selected by cookie
		layout_currentWidget = $.cookie(layout_currentTabSelector + '_w');
		if(layout_currentWidget == null) {
			// Select first
			layout_currentWidget = "#" + $("#layout-widgetframe div:first-child").attr('id');
		}
	} else {
		$.cookie(layout_currentTabSelector + '_w', layout_currentWidget, { path: '/' });
	}
	
	$(layout_currentWidget + " h2").removeClass("layout-widget-passive");
	$(layout_currentWidget + " h2").addClass("layout-widget-active");
	$(layout_currentWidget + " .layout-widget-content").show();
	
	// Load widget to panel if requested
	if(layout_loadToPanelSelector_pw != "" && layout_loadToPanelSelector_pa != "") {
		// get parameters
		var params = {};
		var paramsString = decodeURIComponent(layout_loadToPanelSelector_pp);
		var paramsStringSplit = paramsString.split('+');
		jQuery.each( paramsStringSplit, function(index, value){
			var paramSplit = value.split('=');
			params[paramSplit[0]] = paramSplit[1];
		});
		// load widget to panel
		panel_load(layout_loadToPanelSelector_pw, layout_loadToPanelSelector_pa, params);
	} else {
		// check if set in cookies
		var layout_loadToPanelSelector_pw = $.cookie(layout_currentTabSelector + "_pw");
		var layout_loadToPanelSelector_pa = $.cookie(layout_currentTabSelector + "_pa");
		var layout_loadToPanelSelector_pp = $.cookie(layout_currentTabSelector + "_pp");
		if(layout_loadToPanelSelector_pw != null) {
			// get parameters
			var params = {};
			var paramsString = decodeURIComponent(layout_loadToPanelSelector_pp);
			var paramsStringSplit = paramsString.split('+');
			jQuery.each( paramsStringSplit, function(index, value){
				var paramSplit = value.split('=');
				params[paramSplit[0]] = paramSplit[1];
			});
			// load widget to panel
			panel_load(
				layout_loadToPanelSelector_pw,
				layout_loadToPanelSelector_pa,
				params
			);
		} else {
			panel_load("panelWidget", "show", {});
		} 
	}
	
	// Init infobox
	//layout_infobox.hover(layout_infobox_onHoverIn,layout_infobox_onHoverOut); //comented by B.K. -> alternative below
	
	layout_infobox.mouseenter(function(){
		clearTimeout($(this).data('timeoutId'));
		layout_infobox_onHoverIn();
	});

	layout_infobox.mouseleave(function(){
		var someElement = this;
	    var timeoutId = setTimeout(function(){
	    	layout_infobox_onHoverOut();
	    }, 650);
	    //set the timeoutId, allowing us to clear this trigger if the mouse comes back over
	    $(this).data('timeoutId', timeoutId);		
	});	
	
	
	layout_executeMainLoop();
	
}

function layout_forceLoadingRefresh() {
	// Show hide 'please wait' layer
	if(loading) {
		layout_mainframe_wait.fadeIn('fast');
		$("html").css("cursor","progress");
	} else {
		layout_mainframe_wait.fadeOut('fast');
		$("html").css("cursor","auto");
	}
}

function layout_forceLoading() {
	loading = true;
	layout_forceLoadingRefresh();
}

function layout_executeMainLoop() {

	// Show hide 'please wait' layer
	if(loading) {
		layout_mainframe_wait.fadeIn('fast');
		$("html").css("cursor","progress");
	} else {
		layout_mainframe_wait.fadeOut('fast');
		$("html").css("cursor","auto");
	}
	
	// Get window height & width
	layout_windowHeight = $(window).height();
	layout_windowWidth = $(window).width();
	
	// Style layout's mainframe
	layout_mainframe.width(layout_windowWidth - 50);
	layout_mainframe.height(layout_windowHeight - 125);
	
	// Style widget and panel frames
	var mainframeHeight = layout_mainframe.height();
	layout_widgetframe.height(mainframeHeight);
	layout_panelframe.height(mainframeHeight);
	layout_widgetframe.width(layout_widgetframe_width);
	layout_panelframe.width(layout_mainframe.width() - layout_widgetframe_width - layout_panelframe_safeslack);
	
	// Style widgets
	var newMaxHeight = mainframeHeight - (layout_widgetcount*35 + 14);
	$(".layout-widget-content").css('max-height',newMaxHeight);
	
	// Style panel header
	var panelWidth = layout_panelframe.width();
	var panelHeight = layout_panelframe.height();
	$("#layout-panel-header").width(panelWidth - 220);
	$("#layout-panel-content").width(panelWidth - 14);
	$("#layout-panel-content").height(panelHeight - 144);
	
	// Style panel content overlay
	$("#layout-panel-content-overlay-top").width(panelWidth - 30);
	
	// Next iteration
	setTimeout("layout_executeMainLoop()",500);
	
}

function layout_widget_onClick(id) {
	if(id != layout_currentWidget) {
		$(layout_currentWidget + " .layout-widget-content").slideUp('fast');
		$(layout_currentWidget + " h2").removeClass("layout-widget-active");
		$(layout_currentWidget + " h2").addClass("layout-widget-passive");
		layout_currentWidget = id;
		$(layout_currentWidget + " h2").removeClass("layout-widget-passive");
		$(layout_currentWidget + " h2").addClass("layout-widget-active");
		$.cookie(layout_currentTabSelector + '_w', id, { path: '/' });
		$(id + " .layout-widget-content").slideDown('fast');
	}
}

function layout_infobox_onHoverIn() {
	var newHeight = $("#layout-infobox-content").height() + 125;
	layout_infobox.animate({
		height: newHeight + "px"
	},200);
	$("#layout-infobox-more").hide();
}

function layout_infobox_onHoverOut() {
	layout_infobox.height(198);
	$("#layout-infobox-more").show();
}
