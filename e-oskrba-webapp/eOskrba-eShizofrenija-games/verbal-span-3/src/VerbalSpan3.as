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
	public class VerbalSpan3 extends EOskrbaGameGeneric
	{
		
		// Constants
		
		// Options
		public var optionsLoader:URLLoader;
		public var optionsHolder:XML;
		public var level:int;
		public var levelStart:int;
		public var levelUp:int;
		public var minLevel:int;
		public var maxLevel:int;
		public var liveCount:int;
		public var chars:Array;
		public var charsLength:int;
		public var charFlashTime:int;
		public var charGapTime:int;
		public var pauseTime:int;
		public var secondsToGo:int;
		
		// Holder vars
		public var gameScreen:MovieClip;
		public var frame:MovieClip;
		public var info:EOskrbaImage;
		public var info_time:EOskrbaLabel;
		public var info_level:EOskrbaLabel;
		public var turn:int;
		public var info_lives:Array;
		public var info_nlives:Array;
		public var ok:EOskrbaImage;
		public var nok:EOskrbaImage;
		public var wait:EOskrbaImage;
		public var go:EOskrbaImage;
		public var time:Timer;
		
		// Round vars
		public var round_chars:Array;
		public var round_input:Array;
		public var round_char:EOskrbaLabel;
		public var round_iter:int;
		
		// Stopwatches
		public var reactionStopwatch_start:Date;
		public var reactionStopwatch_stop:Date;
		
		// Results
		public var results_good:int;
		public var results_bad:int;
		
		public function VerbalSpan3(mainSprite:Sprite)
		{
			super(mainSprite);
		}
		
		override public function select_media():void {
			
			img["navodila"] = new EOskrbaImage("img/navodila.png");
			img["info"] = new EOskrbaImage("img/info.png");
			img["frame"] = new EOskrbaImage("img/frame.png");
			img["ok"] = new EOskrbaImage("img/ok.png");
			img["nok"] = new EOskrbaImage("img/nok.png");
			img["wait"] = new EOskrbaImage("img/wait.png");
			img["go"] = new EOskrbaImage("img/go.png");
			
			img["live"] = new EOskrbaImage("img/live.png");
			img["nlive"] = new EOskrbaImage("img/nlive.png");
			
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
				
				game_prepare_inner();
				
			});
			
			optionsLoader.load(new URLRequest("./options.xml"));
			
		}
		
		public function game_prepare_inner():void {
			
			results_good = 0;
			results_bad = 0;
			level = parseInt(optionsHolder.startLevel[0]);
			levelStart = level;
			levelUp = parseInt(optionsHolder.levelUp[0]);
			minLevel = parseInt(optionsHolder.minLevel[0]);
			maxLevel = parseInt(optionsHolder.maxLevel[0]);
			liveCount = parseInt(optionsHolder.lives[0]);
			charFlashTime = parseInt(optionsHolder.charFlashTime[0]);
			charGapTime = parseInt(optionsHolder.charGapTime[0]);
			pauseTime =  parseInt(optionsHolder.pauseTime[0]);
			secondsToGo = parseInt(optionsHolder.secondsToGo[0]);
			
			charsLength = optionsHolder.chars[0].char.length();
			chars = new Array();
			for (var i:int = 0; i < charsLength; i++) {
				chars[i] = optionsHolder.chars[0].char[i];
			}
			
			gameScreen = new MovieClip();
			
			// frame
			frame = new MovieClip();
			frame.addChild(img["frame"]);
			frame.x = 309;
			frame.y = 125;
			gameScreen.addChild(frame);
			
			round_char = new EOskrbaLabel("?", 240);
			round_char.x = 55;
			round_char.y = 20;
			round_char.alpha = 0;
			frame.addChild(round_char);
			
			ok = img["ok"];
			ok.alpha = 0;
			frame.addChild(ok);
			nok = img["nok"];
			nok.alpha = 0;
			frame.addChild(nok);
			
			wait = img["wait"];
			wait.alpha = 0;
			frame.addChild(wait);
			go = img["go"];
			go.alpha = 0;
			frame.addChild(go);
			
			// info
			info = img["info"];
			gameScreen.addChild(info);
			info.y = 485;
			
			info_time = new EOskrbaLabel("/", 18,0x7f7f7f);
			info_time.x = 28;
			info_time.y = 550;
			gameScreen.addChild(info_time);
			
			info_level = new EOskrbaLabel(level.toString(), 18);
			info_level.x = 143;
			info_level.y = 550;
			gameScreen.addChild(info_level);
			
			this.info_lives = new Array();
			this.info_nlives = new Array();
			
			var heart:EOskrbaImage = img["live"];
			var nheart:EOskrbaImage = img["nlive"];
			var n:int = 0;
			for ( n = 0; n < liveCount; n++) {
				this.info_lives[n] = new Bitmap(Bitmap(heart.content).bitmapData);
				this.info_lives[n].x = 370 + n*55;
				this.info_lives[n].y = 532;
				gameScreen.addChild(this.info_lives[n]);
				this.info_nlives[n] = new Bitmap(Bitmap(nheart.content).bitmapData);
				this.info_nlives[n].x = 370 + n*55;
				this.info_nlives[n].y = 532;
				this.info_nlives[n].alpha = 0;
				gameScreen.addChild(this.info_nlives[n]);
			}
			
			game_prepared();
			
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
			time = new Timer(1000, secondsToGo);
			time.addEventListener(TimerEvent.TIMER, game_timer_tick);
			time.addEventListener(TimerEvent.TIMER_COMPLETE, game_end);
			time.start();
			game_round();
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
			
			if (liveCount > 0) {
				
				info_level.text = level.toString();
				
				round_iter = 0;
				round_chars = new Array();
				var chars_copy:Array = new Array();
				for (var m:int = 0; m < chars.length; m++) {
					chars_copy[m] = chars[m];
				}
				for (var i:int = 0; i < level; i++) {
					var chosen:int = Math.floor(Math.random() * chars_copy.length);
					round_chars[i] = chars_copy[chosen];
					var new_chars_copy:Array = new Array();
					var p:int = 0;
					for (var r:int = 0; r < chars_copy.length; r++) {
						if (r != chosen) {
							new_chars_copy[p] = chars_copy[r];
							p++;
						}
					}
					chars_copy = new_chars_copy;
				}
				round_input = new Array();
				
				setTimeout(game_round_show_next_char, 1000);
				
			} else {
				game_end();
			}
			
		}
		
		public function game_round_show_next_char():void {
			
			round_char.text = round_chars[round_iter];
			round_char.x = Math.floor((182 - round_char.width) / 2);
			round_char.alpha = 1;
			setTimeout(function():void {
				round_char.alpha = 0;
				setTimeout(function():void {
					round_iter++;
					if (round_iter == round_chars.length) {
						round_iter = 0;
						game_round_get_next_input();
					} else {
						game_round_show_next_char()
					}
				},charGapTime);
			},charFlashTime);
			
		}
		
		public function game_round_get_next_input():void {
			if(round_iter == 0) {
				wait.alpha = 1;
				Tweener.addTween(wait, {
					alpha:0,
					time: pauseTime / 1000,
					transition: "linear",
					onComplete: game_round_get_next_input_go
				});
			} else {
				game_round_get_next_input_go();
			}
		}
		
		public function game_round_get_next_input_go():void {
			if(round_iter == 0) {
				(sfx["int_biip"] as EOskrbaSoundEffect).play();
				go.alpha = 1;
			} else {
				(sfx["int_beep"] as EOskrbaSoundEffect).play();
			}
			main.stage.addEventListener(KeyboardEvent.KEY_DOWN, game_round_get_next_input_onKeyDown);
		}
		
		public function game_round_get_next_input_onKeyDown(event:KeyboardEvent):void {
			if ( (event.charCode >= 48 && event.charCode <= 57) || (event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122)) {
				go.alpha = 0;
				var char:String = String.fromCharCode(event.charCode).toUpperCase();
				main.stage.removeEventListener(KeyboardEvent.KEY_DOWN, game_round_get_next_input_onKeyDown);
				round_input[round_iter] = char;
				round_char.text = round_input[round_iter];
				round_char.x = Math.floor((182 - round_char.width) / 2);
				round_char.alpha = 1;
				round_iter++;
				if (round_iter == round_chars.length) {
					game_round_evaluate();
				} else {
					game_round_get_next_input();
				}
			}
		}
		
		public function game_round_evaluate():void {
			round_char.alpha = 0;
			var good:Boolean = true;
			var round_chars_ordered:Array;
			round_chars.sort();
			for (var i:int = 0; i < level; i++) {
				if (round_chars[i] != round_input[i]) good = false;
			}
			if (good) {
				game_round_success();
			} else {
				game_round_fail();
			}
		}
		
		public function game_round_success():void {
			(sfx["success"] as EOskrbaSoundEffect).play();
			results_good++;
			level = levelStart + Math.floor((results_good - results_bad) / levelUp);
			if (level < minLevel) level = minLevel;
			if (level > maxLevel) level = maxLevel;
			Tweener.addTween(ok, {
				alpha:1,
				time:charFlashTime/1000,
				onComplete: function():void {
					Tweener.addTween(ok, {
						alpha:0,
						time:charFlashTime/1000,
						onComplete: game_round
					});
				}
			});
		}
		
		public function game_round_fail():void {
			(sfx["fail"] as EOskrbaSoundEffect).play();
			results_bad++;
			var levelPlus:int = Math.floor((results_good - results_bad) / levelUp);
			if (levelPlus < 0) levelPlus = 0;
			level = levelStart + levelPlus;
			if (level == 0) level = 1;
			this.liveCount--;
			Tweener.addTween(this.info_nlives[this.liveCount], {
				alpha: 1,
				time: charGapTime/1000
			});
			Tweener.addTween(nok, {
				alpha:1,
				time:charFlashTime/1000,
				onComplete: function():void {
					Tweener.addTween(nok, {
						alpha:0,
						time:charFlashTime/1000,
						onComplete: game_round
					});
				}
			});
		}
		
		public function game_end():void {
			time.stop();
			Tweener.addTween(gameScreen, {
				alpha:0,
				time:1
			});
			setTimeout(function():void {
				
				// Calculate average time
				
				var json:String = "{ ";
				json += "\"gameId\": \"verbal-span-2\", ";
				json += "\"good\": " + results_good + ", ";
				json += "\"bad\": " + results_bad + ", ";
				json += "\"livesLeft\": " + liveCount + ", ";
				json += "\"timeLeft\": " + secondsToGo + ", ";
				json += "\"level\": " + level.toString() + " }";
				
				trace(json);
				ExternalInterface.call("alert", json);
				
			}, 1000);
		}
		
	}

}
