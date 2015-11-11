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
	 * @author Alex B.
	 */
	public class PairedAssociatesBox extends MovieClip
	{
		
		public var gridPosition_x:int;
		public var gridPosition_y:int;
		
		public var button:SimpleButton;
		public var image:Bitmap;
		public var box:Bitmap;
		public var boxEmpty:Bitmap;
		public var tile:MovieClip;
		
		public function PairedAssociatesBox( gridPosition_x:int, gridPosition_y:int, image:EOskrbaImage, box:EOskrbaImage, boxEmpty:EOskrbaImage ) 
		{
			this.gridPosition_x = gridPosition_x;
			this.gridPosition_y = gridPosition_y;
			
			this.image = new Bitmap( Bitmap(image.content).bitmapData );
			this.box = new Bitmap( Bitmap(box.content).bitmapData );
			this.boxEmpty = new Bitmap( Bitmap(boxEmpty.content).bitmapData );
			
			this.button = new SimpleButton(this.box, this.box, this.box, this.box);
			this.button.useHandCursor  = true;
			this.addChild(button);
			
			this.tile = new MovieClip();
			this.tile.addChild(this.boxEmpty);
			this.tile.addChild(this.image);
			this.image.x = 5;
			this.image.y = 5;
			this.tile.alpha = 0;
			this.tile.visible = false;
			this.addChild(this.tile);
		}
		
		public function flip(duration:int, flipSfx:EOskrbaSoundEffect, flipBackSfx:EOskrbaSoundEffect):void {
			
			var ref_this:PairedAssociatesBox = this;
			ref_this.tile.visible = true;
			Tweener.addTween(ref_this.tile, {
				alpha:1,
				time: 0.1,
				onComplete: function():void {
					Tweener.addTween(ref_this.tile, {
						alpha:1,
						time: (duration-200) / 1000,
						onComplete: function():void {
							Tweener.addTween(ref_this.tile, {
								alpha:0,
								time:0.1,
								onComplete: function():void {
									ref_this.tile.visible = false;
								}
							});
							flipBackSfx.play();
						}
					});
				}
			});
			flipSfx.play();
			
		}
		
		public function flipPermanently(flipSfx:EOskrbaSoundEffect):void {
			
			var ref_this:PairedAssociatesBox = this;
			ref_this.tile.visible = true;
			Tweener.addTween(ref_this.tile, {
				alpha:1,
				time: 0.1
			});
			flipSfx.play();
			
		}
		
	}

}
