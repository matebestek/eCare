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
	public class GrammaticalReasoning2 extends EOskrbaGameGeneric
	{
		
		// Options
		public var optionsLoader:URLLoader;
		public var optionsHolder:XML;
		public var statementTrueProbability:Number;
		
		// Holder vars
		public var gameScreen:MovieClip;
		public var info:EOskrbaImage;
		public var info_time:EOskrbaLabel;
		public var info_level:EOskrbaLabel;
		public var info_good:EOskrbaLabel;
		public var info_bad:EOskrbaLabel;
		public var secondsToGo:int;
		public var time:Timer;
		public var turn:int;
		
		// Problems
		public var problems:Array;
		
		// Round vars
		public var round:int;
		public var statementTrue:Boolean;
		public var squareAndCircle:MovieClip;
		public var round_problem:GrammaticalReasoning2Problem;
		public var round_statement:EOskrbaLabel;
		
		// Buttons
		public var okBtn:SimpleButton;
		public var nokBtn:SimpleButton;
		
		// Stopwatches
		public var reactionStopwatch_start:Date;
		public var reactionStopwatch_stop:Date;
		
		// Results
		public var results_good:int;
		public var results_bad:int;
		
		public function GrammaticalReasoning2(mainSprite:Sprite)
		{
			super(mainSprite);
		}
		
		override public function select_media():void {
			
			img["ok"] = new EOskrbaImage("img/ok.png");
			img["okh"] = new EOskrbaImage("img/okh.png");
			img["nok"] = new EOskrbaImage("img/nok.png");
			img["nokh"] = new EOskrbaImage("img/nokh.png");
			img["cis"] = new EOskrbaImage("img/cis.png");
			img["sic"] = new EOskrbaImage("img/sic.png");
			img["navodila"] = new EOskrbaImage("img/navodila.png");
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
				
				secondsToGo = parseInt(optionsHolder.gameTime[0]);
				statementTrueProbability = parseFloat(optionsHolder.statementTrueProbability[0]);
				results_good = 0;
				results_bad = 0;
				round = 0;
				
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
				
				// Problems
				problems = new Array();
				for (var i:int = 0; i < parseInt(optionsHolder.problems[0].pr.length()); i++ ) {
					problems[i] = new GrammaticalReasoning2Problem(optionsHolder.problems[0].pr[i], optionsHolder.problems[0].pr[i].attribute("bigger"));
				}
				
				squareAndCircle = null;
				round_statement = null;
				
				// info
				info = img["info"];
				gameScreen.addChild(info);
				info.y = 485;
				
				info_time = new EOskrbaLabel("??:??", 18);
				info_time.x = 28;
				info_time.y = 550;
				gameScreen.addChild(info_time);
				
				info_level = new EOskrbaLabel("/", 18);
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
			time = new Timer(1000, secondsToGo);
			game_timer_tick(null);
			time.addEventListener(TimerEvent.TIMER, game_timer_tick);
			time.addEventListener(TimerEvent.TIMER_COMPLETE, function(event:TimerEvent):void {
				disable_buttons();
				game_end();
			});
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
			
			if (secondsToGo > 0) {
				
				if (round_statement != null) {
					gameScreen.removeChild(round_statement);
				}
				if (squareAndCircle != null) {
					gameScreen.removeChild(squareAndCircle);
				}
				
				// shuffle problems if already did them all
				if ((round % problems.length) == 0) {
					problems.sort(function(a:GrammaticalReasoning2Problem, b:GrammaticalReasoning2Problem):Number {
						if (Math.random() < 0.5) return -1;
						else return 1;
					});
				}
				
				statementTrue = (Math.random() < statementTrueProbability);
				round_problem = problems[round % problems.length];
				
				squareAndCircle = new MovieClip();
				squareAndCircle.x = 309;
				squareAndCircle.y = 75;
				squareAndCircle.alpha = 0;
				gameScreen.addChild(squareAndCircle);
				
				var pic:Bitmap;
				
				if (statementTrue) {
					okBtn.addEventListener(MouseEvent.MOUSE_DOWN, game_good);
					nokBtn.addEventListener(MouseEvent.MOUSE_DOWN, game_bad);
					if (round_problem.squareIsBigger) {
						pic = new Bitmap( Bitmap( EOskrbaImage(img["cis"]).content ).bitmapData );
						squareAndCircle.addChild(pic);
					} else {
						pic = new Bitmap( Bitmap( EOskrbaImage(img["sic"]).content ).bitmapData );
						squareAndCircle.addChild(pic);
					}
				} else {
					okBtn.addEventListener(MouseEvent.MOUSE_DOWN, game_bad);
					nokBtn.addEventListener(MouseEvent.MOUSE_DOWN, game_good);
					if (round_problem.squareIsBigger) {
						pic = new Bitmap( Bitmap( EOskrbaImage(img["sic"]).content ).bitmapData );
						squareAndCircle.addChild(pic);
					} else {
						pic = new Bitmap( Bitmap( EOskrbaImage(img["cis"]).content ).bitmapData );
						squareAndCircle.addChild(pic);
					}
				}
				
				round_statement = new EOskrbaLabel(round_problem.statement, 32);
				round_statement.x = (800 - round_statement.width) / 2;
				round_statement.y = 275;
				round_statement.alpha = 0;
				gameScreen.addChild(round_statement);
				
				Tweener.addTween(squareAndCircle, {
					alpha: 1,
					time: 0.25
				});
				Tweener.addTween(round_statement, {
					alpha: 1,
					time: 0.25
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
			game_next_round();
		}
		
		public function game_bad(event:MouseEvent):void {
			disable_buttons();
			(sfx["fail"] as EOskrbaSoundEffect).play();
			results_bad++;
			info_bad.text = results_bad.toString();
			game_next_round();
		}
		
		public function disable_buttons():void {
			if (statementTrue) {
				okBtn.removeEventListener(MouseEvent.MOUSE_DOWN, game_good);
				nokBtn.removeEventListener(MouseEvent.MOUSE_DOWN, game_bad);
			} else {
				okBtn.removeEventListener(MouseEvent.MOUSE_DOWN, game_bad);
				nokBtn.removeEventListener(MouseEvent.MOUSE_DOWN, game_good);
			}
		}
		
		public function game_next_round():void {
			round++;
			Tweener.addTween(round_statement, {
				alpha:0,
				time:0.25
			});
			Tweener.addTween(squareAndCircle, {
				alpha:0,
				time:0.25,
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
				json += "\"good\": " + results_good.toString() + ", ";
				json += "\"bad\": " + results_bad.toString() + " }";
				
				trace(json);
				ExternalInterface.call("alert", json);
				
			}, 1000);
		}
		
	}

}
