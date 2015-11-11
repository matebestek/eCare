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
package  
{
	import flash.display.MovieClip;
	import flash.display.*;
	import flash.events.*;
	import flash.net.*;
	import flash.text.*;
	import caurina.transitions.*;
	import flash.utils.*;
	import flash.media.*;
	import flash.ui.*;
	import flash.external.*;
	
	public class SpatialSearchGrid extends MovieClip
	{
		
		public var boxes:Array;
		public var boxCount:int;
		private var boxesDisabledPermanently:Boolean;
		
		public function SpatialSearchGrid(boxCount:int, boxEmpty:EOskrbaImage, boxNoCoin:EOskrbaImage, boxCoin:EOskrbaImage, boxFail:EOskrbaImage) 
		{
			this.boxCount = boxCount;
			this.boxesDisabledPermanently = false;
			boxes = new Array();
			
			var locations:Array = new Array();
			for (var i:int = 0; i < 25; i++) {
				locations[i] = i;
			}
			locations.sort(function(a:int, b:int):Number {
				if (Math.random() < 0.5) return -1;
				else return 1;
			});
			
			for (var j:int = 0; j < boxCount; j++) {
				boxes[j] = new SpatialSearchBox(
					locations[j] % 5,
					Math.floor(locations[j] / 5),
					boxEmpty,
					boxNoCoin,
					boxCoin,
					boxFail
				);
			}
			SpatialSearchBox(boxes[0]).hasCoin = true;
			
			for (var k:int = 0; k < boxCount; k++) {
				var box:SpatialSearchBox = boxes[k];
				box.x = (box.gridPosition_x * 65);
				box.y = (box.gridPosition_y * 65);
				this.addChild(box);
			}
			
		}
		
		public function disableAllBoxes():void {
			
			for each(var box:SpatialSearchBox in this.boxes) {
				
				box.button.enabled = false;
				box.button.mouseEnabled = false;
				
			}
			
		}
		
		public function disableAllBoxesPermanently():void {
			
			this.boxesDisabledPermanently = true;
			disableAllBoxes();
			
		}
		
		public function enableAllBoxes():void {
			
			if( !boxesDisabledPermanently ) {
				for each(var box:SpatialSearchBox in this.boxes) {
					
					box.button.enabled = true;
					box.button.mouseEnabled = true;
					
				}
			}
			
		}
		
	}

}
