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
	import flash.display.*;
	import flash.events.*;
	import flash.net.*;
	import flash.text.*;
	import caurina.transitions.*;
	import flash.utils.*;
	import flash.media.*;
	import flash.ui.*;
	import flash.external.*;
	/**
	 * eOskrba-eShizofrenija-games
	 * Feature Match (problem class)
	 * @author Alex B.
	 */
	public class FeatureMatchProblem 
	{
		
		// counters
		public var i:int, j:int, k:int;
		
		// static
		public static var shapes:Array;
		public static var shapesCount:int;
		
		// instance
		public var left:MovieClip;
		public var right:MovieClip;
		
		// init()
		public static function init(shparr:Array):void {
			shapes = new Array();
			shapesCount = shparr.length;
			for (var i:int = 0; i < shapesCount; i++) {
				shapes[i] = shparr[i];
			}
		}
		
		public function FeatureMatchProblem(lvl:int, same:Boolean) {
			
			this.left = new MovieClip();
			this.right = new MovieClip();
			
			var freeLocations:Array = new Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36);
			var chosenLocations:Array = new Array();
			
			// choose shape locations
			for (i = 0; i < lvl; i++) {
				var rnd: Number = Math.random();
				var divisor:Number = 1.0 / freeLocations.length;
				var choice:int = Math.ceil(rnd / divisor) - 1;
				chosenLocations[i] = freeLocations[choice];
				var newFreeLocations:Array = new Array();
				j = 0;
				for ( k = 0; k < freeLocations.length; k++) {
					if (k != choice) {
						newFreeLocations[j] = freeLocations[k];
						j++;
					}
				}
				freeLocations = newFreeLocations;
			}
			
			// Draw shapes
			for (i = 0; i < lvl; i++) {
				// choose a shape
				var rnd2: Number = Math.random();
				var divisor2:Number = 1.0 / shapesCount;
				var shapeIndex:int = Math.ceil(rnd2 / divisor2) - 1;
				var shape:EOskrbaImage = shapes[shapeIndex];
				var shape1:Bitmap = new Bitmap(Bitmap(shape.content).bitmapData);
				this.left.addChild(shape1);
				shape1.x = ((chosenLocations[i] - 1) % 6) * 30;
				shape1.y = Math.floor((chosenLocations[i] - 1) / 6) * 30;
				var shapeAlt:EOskrbaImage;
				if ( i != lvl - 1 || same) {
					shapeAlt = shape;
				} else {
					shapeAlt = shapes[
						( shapeIndex + ( 1 + Math.floor( Math.random() * ( shapesCount - 1 ) ) ) ) % shapesCount
					];
				}
				var shape2:Bitmap = new Bitmap(Bitmap(shapeAlt.content).bitmapData);
				this.right.addChild(shape2);
				shape2.x = shape1.x;
				shape2.y = shape1.y;
			}
			
		}
		
	}

}
