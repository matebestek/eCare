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
 *   eOskrba-layout js/style.js
 *   RC version 11.07.21
 *   
 *   Aleksander Bešir
 *   alex.besir(at)gmail.com
 *
 */
 
// constants
var layout_panelframe_safeslack = 5;
 
// dynamic vars
var layout_widgetframe_width = 250;
var layout_widgetcount = 4;
var layout_currentWidget = "#widget3";
 
// holder vars
var layout_windowHeight;
var layout_windowWidth;
var layout_mainframe;
var layout_widgetframe;
var layout_panelframe;
 
$(document).ready(function() {
	layout_startMainLoop();
});

function layout_startMainLoop() {

	// Init.
	layout_mainframe = $("#layout-mainframe");
	layout_widgetframe = $("#layout-widgetframe");
	layout_panelframe = $("#layout-panelframe");
	
	$(layout_currentWidget + " .layout-widget-content").show();

	layout_executeMainLoop();
	
}

function layout_executeMainLoop() {

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
	var newMaxHeight = mainframeHeight - (layout_widgetcount*40);
	$(".layout-widget-content").height(newMaxHeight);
	
	// Style panel header
	var panelWidth = layout_panelframe.width();
	var panelHeight = layout_panelframe.height();
	$("#layout-panel-header").width(panelWidth - 220);
	$("#layout-panel-content").width(panelWidth - 14);
	$("#layout-panel-content").height(panelHeight - 144);
	
	// Next iteration
	setTimeout("layout_executeMainLoop()",500);
	
}

function layout_widget_onClick(id) {
	$(layout_currentWidget + " .layout-widget-content").slideUp(function() {
		layout_currentWidget = id;
		$(id + " .layout-widget-content").slideDown();
	});
}
