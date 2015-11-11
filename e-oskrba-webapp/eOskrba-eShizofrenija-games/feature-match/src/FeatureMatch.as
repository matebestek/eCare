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
	 * Feature Match
	 * @author Alex B.
	 */
	public class FeatureMatch extends EOskrbaGameGeneric
	{
		
		// Constants
		public const DIFFERENT_SHAPES:int = 12;
		
		// Options
		public var optionsLoader:URLLoader;
		public var optionsHolder:XML;
		public var level:int;
		public var levelUp:int;
		public var sameProbability:Number;
		
		// Holder vars
		public var gameScreen:MovieClip;
		public var doubleFrame:MovieClip;
		public var info:EOskrbaImage;
		public var info_time:EOskrbaLabel;
		public var info_level:EOskrbaLabel;
		public var info_good:EOskrbaLabel;
		public var info_bad:EOskrbaLabel;
		public var secondsToGo:int;
		public var time:Timer;
		public var turn:int;
		
		// Round vars
		public var same:Boolean;
		public var left:MovieClip;
		public var right:MovieClip;
		
		// Buttons
		public var okBtn:SimpleButton;
		public var nokBtn:SimpleButton;
		
		// Stopwatches
		public var reactionStopwatch_start:Date;
		public var reactionStopwatch_stop:Date;
		
		// Results
		public var results_good:int;
		public var results_bad:int;
		
		public function FeatureMatch(mainSprite:Sprite)
		{
			super(mainSprite);
		}
		
		override public function select_media():void {
			
			img["ok"] = new EOskrbaImage("img/ok.png");
			img["okh"] = new EOskrbaImage("img/okh.png");
			img["nok"] = new EOskrbaImage("img/nok.png");
			img["nokh"] = new EOskrbaImage("img/nokh.png");
			img["frame"] = new EOskrbaImage("img/frame.png");
			img["navodila"] = new EOskrbaImage("img/navodila.png");
			for (var i:int = 1; i <= DIFFERENT_SHAPES; i++) {
				img["shape_" + i.toString()] = new EOskrbaImage("img/shape_" + i.toString() + ".png");
			}
			img["info"] = new EOskrbaImage("img/info.png");
			sfx["fail"] = new EOskrbaSoundEffect("sfx/fail.mp3");
			sfx["success"] = new EOskrbaSoundEffect("sfx/success.mp3");
			
			load_media();
		}
		
		override public function game_prepare():void {
			
			// Read from XML
			optionsLoader = new URLLoader();
			optionsLoader.addEventListener(Event.COMPLETE, function(e:Event):void {
				
				// options
				optionsHolder = new XML(e.target.data);
				
				levelUp = parseInt(optionsHolder.levelUp[0]);
				secondsToGo = parseInt(optionsHolder.gameTime[0]) + 1;
				sameProbability = parseFloat(optionsHolder.matchProbability[0]);
				results_good = 0;
				results_bad = 0;
				level = 1;
				
				okBtn = new SimpleButton(
					img["ok"],
					img["okh"],
					img["ok"],
					img["ok"]
				);
				
				nokBtn = new SimpleButton(
					img["nok"],
					img["nokh"],
					img["nok"],
					img["nok"]
				);
				
				gameScreen = new MovieClip();
				gameScreen.addChild(okBtn);
				gameScreen.addChild(nokBtn);
				okBtn.x  = 215;
				okBtn.y  = 325;
				nokBtn.x = 415;
				nokBtn.y = 325;
				
				// double frame
				doubleFrame = new MovieClip();
				doubleFrame.addChild(img["frame"]);
				doubleFrame.x = 208;
				doubleFrame.y = 125;
				doubleFrame.alpha = 0;
				left = new MovieClip();
				right = new MovieClip();
				doubleFrame.addChild(left);
				doubleFrame.addChild(right);
				gameScreen.addChild(doubleFrame);
				
				// shapes
				var allShapes:Array = new Array();
				for (var i:int = 1; i <= DIFFERENT_SHAPES; i++) {
					allShapes.push(img["shape_" + i]);
				}
				FeatureMatchProblem.init(allShapes);
				
				// info
				info = img["info"];
				gameScreen.addChild(info);
				info.y = 485;
				
				info_time = new EOskrbaLabel("??:??", 18);
				info_time.x = 28;
				info_time.y = 550;
				gameScreen.addChild(info_time);
				
				info_level = new EOskrbaLabel(level.toString(), 18);
				info_level.x = 143;
				info_level.y = 550;
				gameScreen.addChild(info_level);
				
				info_good = new EOskrbaLabel("0", 18, 0x71893f);
				info_good.x = 255;
				info_good.y = 550;
				gameScreen.addChild(info_good);
				
				info_bad = new EOskrbaLabel("0", 18, 0xc00000);
				info_bad.x = 320;
				info_bad.y = 550;
				gameScreen.addChild(info_bad);
				
				game_prepared();
				
			});
			
			optionsLoader.load(new URLRequest("./options.xml"));
			
		}
		
		override public function game_rules():void {
			var simpleRules:EOskrbaSimpleRules = new EOskrbaSimpleRules(
				img["navodila"],
				game_countdown
			);
			this.rules = simpleRules;
			main.addChild(simpleRules);
		}
		
		override public function game_start():void {
			
			main.addChild(gameScreen);
			game_round();
			time = new Timer(1000, secondsToGo);
			game_timer_tick(null);
			time.addEventListener(TimerEvent.TIMER, game_timer_tick);
			time.addEventListener(TimerEvent.TIMER_COMPLETE, function(event:TimerEvent):void {
				game_end();
			});
			time.start();
			
		}
		
		public function game_timer_tick(event:TimerEvent):void {
			secondsToGo--;
			if(secondsToGo >= 0) {
				var min:int = Math.floor(secondsToGo / 60);
				var sec:int = secondsToGo % 60;
				var txt:String = "";
				if (min < 10) txt += "0";
				txt += min.toString() + ":";
				if (sec < 10) txt += "0";
				txt += sec.toString();
				if (secondsToGo <= 10) (sfx["int_beep"] as EOskrbaSoundEffect).play();
				info_time.text = txt;
			}
		}
		
		public function game_round():void {
			
			if (secondsToGo > 0) {
				
				doubleFrame.removeChild(left);
				doubleFrame.removeChild(right);
				
				same = (Math.random() < sameProbability);
				
				var prob:FeatureMatchProblem = new FeatureMatchProblem(level, same);
				prob.left.x = 1;
				prob.left.y = 1;
				prob.right.x = 203;
				prob.right.y = 1;
				left = prob.left;
				right = prob.right;
				doubleFrame.addChild(left);
				doubleFrame.addChild(right);
				
				if (same) {
					okBtn.addEventListener(MouseEvent.MOUSE_DOWN, game_good);
					nokBtn.addEventListener(MouseEvent.MOUSE_DOWN, game_bad);
				} else {
					okBtn.addEventListener(MouseEvent.MOUSE_DOWN, game_bad);
					nokBtn.addEventListener(MouseEvent.MOUSE_DOWN, game_good);
				}
				
				Tweener.addTween(doubleFrame, {
					alpha: 1,
					time: 0.5
				});
				
			} else {
				game_end();
			}
			
		}
		
		public function game_good(event:MouseEvent):void {
			disable_buttons();
			(sfx["success"] as EOskrbaSoundEffect).play();
			results_good++;
			info_good.text = results_good.toString();
			game_set_next_level();
			game_next_round();
		}
		
		public function game_bad(event:MouseEvent):void {
			disable_buttons();
			(sfx["fail"] as EOskrbaSoundEffect).play();
			results_bad++;
			info_bad.text = results_bad.toString();
			game_set_next_level();
			game_next_round();
		}
		
		public function disable_buttons():void {
			if (same) {
				okBtn.removeEventListener(MouseEvent.MOUSE_DOWN, game_good);
				nokBtn.removeEventListener(MouseEvent.MOUSE_DOWN, game_bad);
			} else {
				okBtn.removeEventListener(MouseEvent.MOUSE_DOWN, game_bad);
				nokBtn.removeEventListener(MouseEvent.MOUSE_DOWN, game_good);
			}
		}
		
		public function game_set_next_level():void {
			var newLevel:int = Math.floor((results_good - results_bad) / levelUp);
			if (newLevel > 0 && newLevel <= 36) {
				level = newLevel;
			} else if (newLevel <= 0) {
				level = 1;
			} else if (newLevel > 36) {
				level = 36;
			}
			info_level.text = level.toString();
		}
		
		public function game_next_round():void {
			Tweener.addTween(doubleFrame, {
				alpha:0,
				time:0.5,
				onComplete: game_round
			});
		}
		
		public function game_end():void {
			Tweener.addTween(gameScreen, {
				alpha:0,
				time:1
			});
			setTimeout(function():void {
				
				// Calculate average time
				
				var json:String = "{ ";
				json += "\"gameId\": \"feature-match\", ";
				json += "\"level\": " + level.toString() + ", ";
				json += "\"good\": " + results_good.toString() + ", ";
				json += "\"bad\": " + results_bad.toString() + " }";
				
				trace(json);
				ExternalInterface.call("alert", json);
				
			}, 1000);
		}
		
	}

}
