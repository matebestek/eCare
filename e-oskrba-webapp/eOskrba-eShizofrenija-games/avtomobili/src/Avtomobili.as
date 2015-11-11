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
	 * Avtomobili
	 * @author Alex B.
	 */
	public class Avtomobili extends EOskrbaGameGeneric
	{
		
		// Constants
		public const LEFT_CARS_START_X:int    = -150;
		public const LEFT_CARS_START_Y_1:int  = 190;
		public const LEFT_CARS_START_Y_2:int  = 240;
		public const LEFT_CARS_DEST_X:int     = 800;
		public const RIGHT_CARS_START_X:int   = 800;
		public const RIGHT_CARS_START_Y_1:int = 190;
		public const RIGHT_CARS_START_Y_2:int = 140;
		public const RIGHT_CARS_DEST_X:int    = -150;
		
		// Options
		public var optionsLoader:URLLoader;
		public var optionsHolder:XML;
		public var level:int;
		public var speed:int;
		public var delay_min:int;
		public var delay_max:int;
		public var target_probability:Number;
	
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
		
		// Cars
		public var redCar:EOskrbaImage;
		public var redCarR:EOskrbaImage;
		public var yellowCar:EOskrbaImage;
		public var blueCar:EOskrbaImage;
		public var blueCarR:EOskrbaImage;
		public var greenCar:EOskrbaImage;
		public var orangeCar:EOskrbaImage;
		public var redLorry:EOskrbaImage;
		public var redLorryR:EOskrbaImage;
		public var yellowLorry:EOskrbaImage;
		public var yellowLorryR:EOskrbaImage;
		public var currentCar:EOskrbaImage;
		
		// Stopwatches
		public var reactionStopwatch_start:Date;
		public var reactionStopwatch_stop:Date;
		
		// Results
		public var results_good:int;
		public var results_good_clicks:int;
		public var results_bad:int;
		public var results_goodTimes:Array;
		
		public function Avtomobili(mainSprite:Sprite)
		{
			super(mainSprite);
		}
		
		override public function select_media():void {
			
			img["cesta_enosmerna"] = new EOskrbaImage("img/cesta_enosmerna.png");
			img["cesta_dvosmerna"] = new EOskrbaImage("img/cesta_dvosmerna.png");
			img["cesta_overlay"] = new EOskrbaImage("img/cesta_overlay.png");
			img["info"] = new EOskrbaImage("img/info.png");
			img["avto_rdec"] = new EOskrbaImage("img/avto_rdec.png");
			img["avto_rdec_r"] = new EOskrbaImage("img/avto_rdec_r.png");
			img["avto_rumen"] = new EOskrbaImage("img/avto_rumen.png");
			img["avto_oranzen"] = new EOskrbaImage("img/avto_oranzen.png");
			img["avto_moder"] = new EOskrbaImage("img/avto_moder.png");
			img["avto_moder_r"] = new EOskrbaImage("img/avto_moder_r.png");
			img["avto_zelen"] = new EOskrbaImage("img/avto_zelen.png");
			img["kamion_rdec"] = new EOskrbaImage("img/kamion_rdec.png");
			img["kamion_rdec_r"] = new EOskrbaImage("img/kamion_rdec_r.png");
			img["kamion_rumen"] = new EOskrbaImage("img/kamion_rumen.png");
			img["kamion_rumen_r"] = new EOskrbaImage("img/kamion_rumen_r.png");
			img["navodila_1"] = new EOskrbaImage("img/navodila_1.png");
			img["navodila_2"] = new EOskrbaImage("img/navodila_2.png");
			img["navodila_3"] = new EOskrbaImage("img/navodila_3.png");
			img["navodila_4"] = new EOskrbaImage("img/navodila_4.png");
			img["navodila_5"] = new EOskrbaImage("img/navodila_5.png");
			
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
				if (fvLevel == null) level = 1;
				else level = parseInt(fvLevel);
				trace("[Avtomobili] Level: " + level);
				
				speed = optionsHolder.level.(@id == level).speed;
				trace("[Avtomobili] Speed: " + speed);
				
				delay_min = optionsHolder.level.(@id == level).delay.min;
				trace("[Avtomobili] Delay min: " + delay_min);
				
				delay_max = optionsHolder.level.(@id == level).delay.max;
				trace("[Avtomobili] Delay max: " + delay_max);

				target_probability = optionsHolder.level.(@id == level).targetProbability;
				trace("[Avtomobili] Target probability: " + delay_max);
				
				// TODO: change
				secondsToGo = 60;
				results_good = 0;
				results_good_clicks = 0;
				results_bad = 0;
				results_goodTimes = new Array();
				
				gameScreen = new MovieClip();
				if (level < 4) gameScreen.addChild(img["cesta_enosmerna"]);
				else gameScreen.addChild(img["cesta_dvosmerna"]);
				
				var left_start_y:int = LEFT_CARS_START_Y_1;
				var right_start_y:int = RIGHT_CARS_START_Y_1;
				if (level > 3) {
					left_start_y = LEFT_CARS_START_Y_2;
					right_start_y = RIGHT_CARS_START_Y_2;
				}
				
				redCar = img["avto_rdec"];
				redCar.x = LEFT_CARS_START_X;
				redCar.y = left_start_y;
				gameScreen.addChild(redCar);
				
				yellowCar = img["avto_rumen"];
				yellowCar.x = LEFT_CARS_START_X;
				yellowCar.y = left_start_y;
				gameScreen.addChild(yellowCar);
				
				greenCar = img["avto_zelen"];
				greenCar.x = LEFT_CARS_START_X;
				greenCar.y = left_start_y;
				gameScreen.addChild(greenCar);
				
				orangeCar = img["avto_oranzen"];
				orangeCar.x = LEFT_CARS_START_X;
				orangeCar.y = left_start_y;
				gameScreen.addChild(orangeCar);
				
				blueCar = img["avto_moder"];
				blueCar.x = LEFT_CARS_START_X;
				blueCar.y = left_start_y;
				gameScreen.addChild(blueCar);
				
				redLorry = img["kamion_rdec"];
				redLorry.x = LEFT_CARS_START_X;
				redLorry.y = left_start_y;
				gameScreen.addChild(redLorry);
				
				yellowLorry = img["kamion_rumen"];
				yellowLorry.x = LEFT_CARS_START_X;
				yellowLorry.y = left_start_y;
				gameScreen.addChild(yellowLorry);
				
				redCarR = img["avto_rdec_r"];
				redCarR.x = RIGHT_CARS_START_X;
				redCarR.y = right_start_y;
				gameScreen.addChild(redCarR);
				
				blueCarR = img["avto_moder_r"];
				blueCarR.x = RIGHT_CARS_START_X;
				blueCarR.y = right_start_y;
				gameScreen.addChild(blueCarR);
				
				redLorryR = img["kamion_rdec_r"];
				redLorryR.x = RIGHT_CARS_START_X;
				redLorryR.y = right_start_y;
				gameScreen.addChild(redLorryR);
				
				yellowLorryR = img["kamion_rumen_r"];
				yellowLorryR.x = RIGHT_CARS_START_X;
				yellowLorryR.y = right_start_y;
				gameScreen.addChild(yellowLorryR);
		
				gameScreen.addChild(img["cesta_overlay"]);
				
				// info
				info = img["info"];
				gameScreen.addChild(info);
				info.y = 485;
				
				info_time = new EOskrbaLabel("1:00", 18);
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
				img["navodila_" + level.toString()],
				game_countdown
			);
			this.rules = simpleRules;
			main.addChild(simpleRules);
		}
		
		override public function game_start():void {
			
			main.addChild(gameScreen);
			game_play();
			
		}
		
		public function game_play():void {
			
			time = new Timer(1000);
			time.addEventListener(TimerEvent.TIMER, function(event:TimerEvent):void {
				secondsToGo--;
				if (secondsToGo < 10 && secondsToGo > 0) info_time.text = "0:0" + secondsToGo.toString();
				else if (secondsToGo <= 0) info_time.text = "0:00";
				else info_time.text = "0:" + secondsToGo.toString();
				if (secondsToGo <= 5 && secondsToGo > 0) {
					(sfx["int_beep"] as EOskrbaSoundEffect).play();
				}
			});
			time.start();
			
			turn = 0;
			game_play_turn();
			
		}
		
		public function game_play_turn():void {
			
			if (secondsToGo <= 0) {
				
				game_play_end();
				
			} else {
			
				turn++;
				
				if(level < 4) {
					redCar.x = LEFT_CARS_START_X;
					redCar.alpha = 1;
					yellowCar.x = LEFT_CARS_START_X;
					yellowCar.alpha = 1;
					greenCar.x = LEFT_CARS_START_X;
					greenCar.alpha = 1;
					blueCar.x = LEFT_CARS_START_X;
					blueCar.alpha = 1;
					orangeCar.x = LEFT_CARS_START_X;
					orangeCar.alpha = 1;
					redLorry.x = LEFT_CARS_START_X;
					redLorry.alpha = 1;
					yellowLorry.x = LEFT_CARS_START_X;
					yellowLorry.alpha = 1;
				} else {
					redCar.x = LEFT_CARS_START_X;
					redCar.alpha = 1;
					redCarR.x = RIGHT_CARS_START_X;
					redCarR.alpha = 1;
					yellowCar.x = LEFT_CARS_START_X;
					yellowCar.alpha = 1;
					greenCar.x = LEFT_CARS_START_X;
					greenCar.alpha = 1;
					blueCar.x = LEFT_CARS_START_X;
					blueCar.alpha = 1;
					blueCarR.x = RIGHT_CARS_START_X;
					blueCarR.alpha = 1;
					orangeCar.x = LEFT_CARS_START_X;
					orangeCar.alpha = 1;
					redLorry.x = LEFT_CARS_START_X;
					redLorry.alpha = 1;
					redLorryR.x = RIGHT_CARS_START_X;
					redLorryR.alpha = 1;
					yellowLorry.x = LEFT_CARS_START_X;
					yellowLorry.alpha = 1;
					yellowLorryR.x = RIGHT_CARS_START_X;
					yellowLorryR.alpha = 1;
				}
				
				var rnd:Number = Math.random();
				if (rnd < target_probability) {
					if ( level < 4 ) currentCar = redCar;
					else {
						if (rnd < (target_probability / 2)) currentCar = redCar;
						else currentCar = redCarR;
					}
				}
				else {
					if(level == 1 || level == 2) currentCar = yellowCar;
					else if (level == 3) {
						if ( rnd < ( target_probability + 1 * ((1 - target_probability) / 3) ) ) currentCar = greenCar;
						else if ( rnd < ( target_probability + 2 * ((1 - target_probability) / 3) ) ) currentCar = orangeCar;
						else currentCar = redLorry;
					} else if (level == 4 || level == 5) {
						if ( rnd < ( target_probability + 1 * ((1 - target_probability) / 6) ) ) currentCar = blueCar;
						else if ( rnd < ( target_probability + 2 * ((1 - target_probability) / 6) ) ) currentCar = blueCarR;
						else if ( rnd < ( target_probability + 3 * ((1 - target_probability) / 6) ) ) currentCar = redLorry;
						else if ( rnd < ( target_probability + 4 * ((1 - target_probability) / 6) ) ) currentCar = redLorryR;
						else if ( rnd < ( target_probability + 5 * ((1 - target_probability) / 6) ) ) currentCar = yellowLorry;
						else currentCar = yellowLorryR;
					}
				}
				
				var delay:int = Math.round( delay_min + (Math.random()*(delay_max-delay_min)) );
				setTimeout(game_play_inner, delay);
				
			}
			
		}
		
		public function game_play_inner():void {
			
			var dest:int = LEFT_CARS_DEST_X;
			if ( currentCar == redCarR || currentCar == blueCarR || currentCar == redLorryR || currentCar == yellowLorryR ) {
				dest = RIGHT_CARS_DEST_X;
			}
			
			main.stage.addEventListener(KeyboardEvent.KEY_DOWN, game_play_turn_onSpaceDown);
			reactionStopwatch_start = new Date();
			
			Tweener.addTween(currentCar, {
				x: dest,
				time:( speed/1000.0),
				transition: "linear",
				onComplete: game_play_turn_onCarPassed
			});
			
			
		}
		
		public function game_play_turn_onCarPassed():void {
			reactionStopwatch_stop = new Date();
			main.stage.removeEventListener(KeyboardEvent.KEY_DOWN, game_play_turn_onSpaceDown);
			if (currentCar != redCar && currentCar != redCarR) {
				(sfx["success"] as Sound).play();
				results_good++;
				info_good.text = results_good.toString();
			} else {
				(sfx["fail"] as Sound).play();
				results_bad++;
				info_bad.text = results_bad.toString();
			}
			setTimeout(game_play_turn, 500);
		}
		
		public function game_play_turn_onSpaceDown(event:KeyboardEvent):void {
			if (event.keyCode == Keyboard.SPACE) {
				reactionStopwatch_stop = new Date();
				main.stage.removeEventListener(KeyboardEvent.KEY_DOWN, game_play_turn_onSpaceDown);
				Tweener.removeTweens(currentCar);
				Tweener.addTween(currentCar, {
					alpha:0,
					time:0.5,
					transition:"linear"
				});
				if (currentCar == redCar || currentCar == redCarR) {
					(sfx["success"] as Sound).play();
					results_good++;
					results_good_clicks++;
					results_goodTimes[results_good_clicks - 1] = reactionStopwatch_stop.time - reactionStopwatch_start.time;
					info_good.text = results_good.toString();
				} else {
					(sfx["fail"] as Sound).play();
					results_bad++;
					info_bad.text = results_bad.toString();
				}
				setTimeout(game_play_turn, 500);
			}
		}
		
		public function game_play_end():void {
			Tweener.addTween(gameScreen, {
				alpha:0,
				time:1
			});
			setTimeout(function():void {
				
				// Calculate average time
				var averageTime:Number = 0;
				for (var i:int = 0; i < results_good_clicks; i++) {
					averageTime += results_goodTimes[i];
				}
				averageTime = averageTime / results_good_clicks;
				
				var json:String = "{ ";
				json += "\"gameId\": \"avtomobili\", ";
				json += "\"level\": " + level.toString() + ", ";
				json += "\"good\": " + results_good.toString() + ", ";
				json += "\"bad\": " + results_bad.toString() + ", ";
				json += "\"average\": " + averageTime.toString() + " }";
				
				trace(json);
				ExternalInterface.call("alert", json);
				
			}, 1000);
		}
		
	}

}
