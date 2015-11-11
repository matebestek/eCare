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
	public class SpatialSearch extends EOskrbaGameGeneric
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
		public var coinDisplayTime:int;
		
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
		
		// Round vars
		public var grid:SpatialSearchGrid;
		public var grid_found:int;
		public var helper_found:MovieClip;
		public var helper_found_boxes:Array;
		
		// Stopwatches
		public var reactionStopwatch_start:Date;
		public var reactionStopwatch_stop:Date;
		
		// Results
		public var results_good:int;
		public var results_bad:int;
		
		public function SpatialSearch(mainSprite:Sprite)
		{
			super(mainSprite);
		}
		
		override public function select_media():void {
			
			img["navodila"] = new EOskrbaImage("img/navodila.png");
			img["info"] = new EOskrbaImage("img/info.png");
			img["ok"] = new EOskrbaImage("img/ok.png");
			img["nok"] = new EOskrbaImage("img/nok.png");
			
			img["live"] = new EOskrbaImage("img/live.png");
			img["nlive"] = new EOskrbaImage("img/nlive.png");
			
			img["box_1"] = new EOskrbaImage("img/box_1.png");
			img["box_2"] = new EOskrbaImage("img/box_2.png");
			img["box_3"] = new EOskrbaImage("img/box_3.png");
			img["box_4"] = new EOskrbaImage("img/box_4.png");
			img["box_5"] = new EOskrbaImage("img/box_5.png");
			img["box_6"] = new EOskrbaImage("img/box_6.png");
			
			sfx["flip"] = new EOskrbaSoundEffect("sfx/flip.mp3");
			sfx["drag"] = new EOskrbaSoundEffect("sfx/drag.mp3");
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
			lifeCount = parseInt(optionsHolder.lives[0]);
			roundGapTime = parseInt(optionsHolder.roundGapTime[0]);
			coinDisplayTime = parseInt(optionsHolder.coinDisplayTime[0]);
			
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
			
			helper_found = new MovieClip();
			gameScreen.addChild(helper_found);
			
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
			for ( n = 0; n < lifeCount; n++) {
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
			main.addChild(gameScreenOverlay);
			game_round();
		}
		
		public function game_round():void {
			
			if (lifeCount > 0) {
				
				if(grid != null) {
					// destroy current grid
					gameScreen.removeChild(grid);
				}
				
				grid_found = 0;
				level = levelStart + Math.floor((results_good - results_bad) / levelUp);
				if (level < minLevel) level = minLevel;
				if (level > maxLevel) level = maxLevel;
				info_level.text = level.toString();
				
				// helper
				gameScreen.removeChild(helper_found);
				helper_found = new MovieClip();
				helper_found_boxes = new Array();
				for (var l:int = 0; l < level; l++) {
					var helper_box_empty:Bitmap = new Bitmap( Bitmap(EOskrbaImage(img["box_6"]).content).bitmapData );
					var helper_box_coin:Bitmap = new Bitmap( Bitmap(EOskrbaImage(img["box_5"]).content).bitmapData );
					helper_box_empty.x = l * 30;
					helper_box_coin.x = l * 30;
					helper_box_coin.alpha = 0;
					helper_found.addChild(helper_box_empty);
					helper_found.addChild(helper_box_coin);
					helper_found_boxes[l] = helper_box_coin;
				}
				helper_found.x = (800 - helper_found.width) / 2;
				helper_found.y = 450;
				gameScreen.addChild(helper_found);
				
				game_round_new_grid();
				
			} else {
				game_end();
			}
			
		}
		
		public function game_round_new_grid():void {
			
			EOskrbaSoundEffect(sfx["flip"]).play();
			grid = new SpatialSearchGrid(level, img["box_1"], img["box_3"], img["box_2"], img["box_4"]);
			
			for each(var box:SpatialSearchBox in grid.boxes) {
				box.button.addEventListener(MouseEvent.CLICK, onPatientReadtion);
			}
			
			grid.x = 240;
			grid.y = 75;
			gameScreen.addChild(grid);
			
		}
		
		public function onPatientReadtion(event:MouseEvent):void {
			
			grid.disableAllBoxes();
			var flipResult:int = SpatialSearchBox(SimpleButton(event.target).parent).flip(coinDisplayTime, sfx["drag"], sfx["flip"], sfx["success"], sfx["fail"]);
			var boxesLeft:Array = new Array();
			
			if (flipResult == SpatialSearchBoxFlipResult.COIN) {
				grid_found++;
				Tweener.addTween(Bitmap(helper_found_boxes[grid_found - 1]), {
					alpha:1,
					time: 0.1
				});
				if(grid_found < level) {
					for each(var box:SpatialSearchBox in this.grid.boxes) {
						if ( !box.hasCoin ) {
							box.alreadyClicked = false;
							boxesLeft.push(box);
						}
					}
					boxesLeft.sort(function(a:SpatialSearchBox, b:SpatialSearchBox):Number {
						if (Math.random() < 0.5) return -1;
						return 1;
					});
					SpatialSearchBox(boxesLeft[0]).hasCoin = true;
				} else {
					grid.disableAllBoxesPermanently();
					setTimeout(game_round_ok,1000)
				}
			} else if (flipResult == SpatialSearchBoxFlipResult.ALREADY_CLICKED) {
				grid.disableAllBoxesPermanently();
				setTimeout(game_round_nok, 1000);
			}

		}
		
		public function game_round_ok():void {
			
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
		
		public function game_round_nok():void {
			
			results_bad++;
			lifeCount--;
			game_looseLife();
			gameScreenOverlay.visible = true;
			Tweener.addTween(nok, {
				alpha:1,
				time:0.25,
				onComplete: function():void {
					Tweener.addTween(nok, {
						alpha:1,
						time:0.5,
						onComplete: function():void {
							Tweener.addTween(nok, {
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
		
		public function game_looseLife():void {
			Tweener.addTween(info_nlives[lifeCount], {
				alpha:1,
				time:0.5
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
				
				trace(json);
				ExternalInterface.call("alert", json);
				
			}, 1000);
		}
		
	}

}
