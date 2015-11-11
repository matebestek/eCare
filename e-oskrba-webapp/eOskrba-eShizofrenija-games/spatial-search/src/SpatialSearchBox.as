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
	public class SpatialSearchBox extends MovieClip
	{
		
		public var hasCoin:Boolean;
		public var alreadyClicked:Boolean;
		
		public var gridPosition_x:int;
		public var gridPosition_y:int;
		
		public var button:SimpleButton;
		public var boxEmpty:Bitmap;
		public var boxNoCoin:Bitmap;
		public var boxCoin:Bitmap;
		public var boxFail:Bitmap;
		
		public function SpatialSearchBox( gridPosition_x:int, gridPosition_y:int, boxEmpty:EOskrbaImage, boxNoCoin:EOskrbaImage, boxCoin:EOskrbaImage, boxFail:EOskrbaImage ) 
		{
			this.gridPosition_x = gridPosition_x;
			this.gridPosition_y = gridPosition_y;
			
			this.boxEmpty = new Bitmap( Bitmap(boxEmpty.content).bitmapData );
			
			this.button = new SimpleButton(this.boxEmpty, this.boxEmpty, this.boxEmpty, this.boxEmpty);
			this.button.useHandCursor  = true;
			this.addChild(button);
			
			this.hasCoin = false;
			this.alreadyClicked = false;
			
			this.boxNoCoin = new Bitmap( Bitmap(boxNoCoin.content).bitmapData );
			this.boxNoCoin.alpha = 0;
			this.addChild(this.boxNoCoin);
			this.boxCoin = new Bitmap( Bitmap(boxCoin.content).bitmapData );
			this.boxCoin.alpha = 0;
			this.addChild(this.boxCoin);
			this.boxFail = new Bitmap( Bitmap(boxFail.content).bitmapData );
			this.boxFail.alpha = 0;
			this.addChild(this.boxFail);
		}
		
		public function flip(duration:int, flipSfx:EOskrbaSoundEffect, flipBackSfx:EOskrbaSoundEffect, successSfx:EOskrbaSoundEffect, failSfx:EOskrbaSoundEffect):int {
			
			SpatialSearchGrid(this.parent).disableAllBoxes();
			var ref_this:SpatialSearchBox = this;
			
			if (this.alreadyClicked) {
				
				var ref_boxFail:Bitmap = this.boxFail;
				Tweener.addTween(ref_boxFail, {
					alpha:1,
					time: 0.1,
					onComplete: function():void {
						Tweener.addTween(ref_boxFail, {
							alpha:1,
							time: (duration-200) / 1000,
							onComplete: function():void {
								Tweener.addTween(ref_boxFail, {
									alpha:0,
									time:0.1,
									onComplete: function():void {
										SpatialSearchGrid(ref_this.parent).enableAllBoxes();
									}
								});
							}
						});
					}
				});
				failSfx.play();
				return SpatialSearchBoxFlipResult.ALREADY_CLICKED;
				
			} else if (this.hasCoin) {
				
				this.alreadyClicked = true;
				var ref_boxCoin:Bitmap = this.boxCoin;
				Tweener.addTween(ref_boxCoin, {
					alpha:1,
					time: 0.1,
					onComplete: function():void {
						Tweener.addTween(ref_boxCoin, {
							alpha:1,
							time: (duration-200) / 1000,
							onComplete: function():void {
								Tweener.addTween(ref_boxCoin, {
									alpha:0,
									time:0.1,
									onComplete: function():void {
										SpatialSearchGrid(ref_this.parent).enableAllBoxes();
									}
								});
							}
						});
					}
				});
				successSfx.play();
				return SpatialSearchBoxFlipResult.COIN;
				
			} else {
				
				this.alreadyClicked = true;
				var ref_boxNoCoin:Bitmap = this.boxNoCoin;
				Tweener.addTween(ref_boxNoCoin, {
					alpha:1,
					time: 0.1,
					onComplete: function():void {
						Tweener.addTween(ref_boxNoCoin, {
							alpha:1,
							time: (duration-200) / 1000,
							onComplete: function():void {
								Tweener.addTween(ref_boxNoCoin, {
									alpha:0,
									time:0.1,
									onComplete: function():void {
										SpatialSearchGrid(ref_this.parent).enableAllBoxes();
									}
								});
								flipBackSfx.play();
							}
						});
					}
				});
				flipSfx.play();
				return SpatialSearchBoxFlipResult.NO_COIN;
				
			}
			
		}
		
	}

}
