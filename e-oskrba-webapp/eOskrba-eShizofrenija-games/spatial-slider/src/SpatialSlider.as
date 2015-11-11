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
	public class SpatialSlider extends EOskrbaGameGeneric
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
		public var lifeCount:int;
		public var roundGapTime:int;
		public var cardDisplayTime:int;
		public var cardGapTime:int;
		public var targetShowTime:int;
		public var secondsToGo:int;
		
		// Holder vars
		public var gameScreen:MovieClip;
		public var gameScreenOverlay:MovieClip;
		public var frame:MovieClip;
		public var info:EOskrbaImage;
		public var info_time:EOskrbaLabel;
		public var info_level:EOskrbaLabel;
		public var turn:int;
		public var info_lives:Array;
		public var info_nlives:Array;
		public var ok:EOskrbaImage;
		public var nok:EOskrbaImage;
		public var time:Timer;
		
		// Problems vars
		public var gridDescriptions:Array;
		
		// Round vars
		public var grid:SpatialSliderGrid;
		
		// Stopwatches
		public var reactionStopwatch_start:Date;
		public var reactionStopwatch_stop:Date;
		
		// Results
		public var results_good:int;
		public var results_bad:int;
		
		public function SpatialSlider(mainSprite:Sprite)
		{
			super(mainSprite);
		}
		
		override public function select_media():void {
			
			img["navodila"] = new EOskrbaImage("img/navodila.png");
			img["info"] = new EOskrbaImage("img/info.png");
			img["ok"] = new EOskrbaImage("img/ok.png");
			img["nok"] = new EOskrbaImage("img/nok.png");
			
			img["box"] = new EOskrbaImage("img/box.png");
			img["boxOver"] = new EOskrbaImage("img/boxOver.png");
			img["box_empty"] = new EOskrbaImage("img/box_empty.png");
			
			sfx["flip"] = new EOskrbaSoundEffect("sfx/flip.mp3");
			sfx["drag"] = new EOskrbaSoundEffect("sfx/drag.mp3");
			sfx["fail"] = new EOskrbaSoundEffect("sfx/fail.mp3");
			sfx["success"] = new EOskrbaSoundEffect("sfx/success.mp3");
			
			// Read from XML
			optionsLoader = new URLLoader();
			optionsLoader.addEventListener(Event.COMPLETE, function(e:Event):void {
				
				// options
				optionsHolder = new XML(e.target.data);
				
				// load grids
				gridDescriptions = new Array();
				for (var i:int = 0; i < parseInt(optionsHolder.grids[0].grid.length()); i++) {
					gridDescriptions.push( optionsHolder.grids[0].grid[i] );
				}
				
				// Continue
				load_media();
				
			});
			
			optionsLoader.load(new URLRequest("./options.xml"));
			
		}
		
		override public function game_prepare():void {
			
			game_prepare_inner();
			
		}
		
		public function game_prepare_inner():void {
			
			results_good = 0;
			results_bad = 0;
			level = parseInt(optionsHolder.startLevel[0]);
			levelStart = level;
			levelUp = parseInt(optionsHolder.levelUp[0]);
			minLevel = parseInt(optionsHolder.minLevel[0]);
			maxLevel = parseInt(optionsHolder.maxLevel[0]);
			lifeCount = parseInt(optionsHolder.lives[0]);
			secondsToGo = parseInt(optionsHolder.secondsToGo[0]);
			roundGapTime = parseInt(optionsHolder.roundGapTime[0]);
			targetShowTime = parseInt(optionsHolder.targetShowTime[0]);
			
			gameScreen = new MovieClip();
			gameScreenOverlay = new MovieClip();
			
			ok = img["ok"];
			ok.x = 309;
			ok.y = 159;
			ok.alpha = 0;
			gameScreenOverlay.addChild(ok);
			nok = img["nok"];
			nok.x = 309;
			nok.y = 159;
			nok.alpha = 0;
			gameScreenOverlay.addChild(nok);
			gameScreenOverlay.visible = false;
			
			grid = null;
			
			// info
			info = img["info"];
			gameScreen.addChild(info);
			info.y = 485;
			
			info_time = new EOskrbaLabel("/", 18);
			info_time.x = 28;
			info_time.y = 550;
			gameScreen.addChild(info_time);
			
			info_level = new EOskrbaLabel(level.toString(), 18);
			info_level.x = 143;
			info_level.y = 550;
			gameScreen.addChild(info_level);
			
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
			main.addChild(gameScreenOverlay);
			
			time = new Timer(1000, secondsToGo);
			time.addEventListener(TimerEvent.TIMER, game_timer_tick);
			time.addEventListener(TimerEvent.TIMER_COMPLETE, function(event:TimerEvent):void{ game_end() });
			
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
				
				if(grid != null) {
					// destroy current grid
					gameScreen.removeChild(grid);
				}
				
				if (results_good % gridDescriptions.length == 0) {
					gridDescriptions.sort(function(a:String, b:String):Number {
						if (Math.random() < 0.5) return -1;
						else return 1;
					});
				}
				
				level = levelStart + Math.floor((results_good - results_bad) / levelUp);
				if (level < minLevel) level = minLevel;
				if (level > maxLevel) level = maxLevel;
				info_level.text = level.toString();
				
				game_round_new_grid();
				
			} else {
				game_end();
			}
			
		}
		
		public function game_round_new_grid():void {
			
			EOskrbaSoundEffect(sfx["flip"]).play();
			grid = new SpatialSliderGrid(gridDescriptions[results_good % gridDescriptions.length], game_round_ok, img["box"], img["boxOver"], img["box_empty"], sfx["drag"], sfx["flip"]);
			grid.x = 200;
			grid.y = 40;
			gameScreen.addChild(grid);
			setTimeout(game_round_shuffle_grid, targetShowTime);
			
		}
		
		public function game_round_shuffle_grid():void {
			
			grid.shuffle(level);
			grid.enableMoves();
			
		}
			
		public function game_round_ok():void {
			
			grid.disableMoves();
			setTimeout(game_round_ok_inner, 100);
			
		}
		
		public function game_round_ok_inner():void {
			
			EOskrbaSoundEffect(sfx["success"]).play();
			results_good++;
			gameScreenOverlay.visible = true;
			Tweener.addTween(ok, {
				alpha:1,
				time:0.25,
				onComplete: function():void {
					Tweener.addTween(ok, {
						alpha:1,
						time:0.5,
						onComplete: function():void {
							Tweener.addTween(ok, {
								alpha:0,
								time:0.25,
								onComplete: function():void {
									gameScreenOverlay.visible = false;
									game_round_next();
								}
							});
						}
					});
				}
			});
			
		}
			
		public function game_round_next():void {
			Tweener.addTween(grid, {
				alpha:0,
				time:0.5,
				onComplete:function():void {
					setTimeout(game_round, roundGapTime);
				}
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
				json += "\"gameId\": \"spatial-search\", ";
				json += "\"good\": " + results_good + ", ";
				json += "\"bad\": " + results_bad + ", ";
				json += "\"livesLeft\": " + lifeCount + ", ";
				json += "\"level\": " + level.toString() + " }";
				
				ExternalInterface.call("alert", json);
				
			}, 1000);
		}
		
	}

}
