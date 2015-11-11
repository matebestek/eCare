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
	public class NBack0 extends EOskrbaGameGeneric
	{
		
		// Constants
		
		// Options
		public var optionsLoader:URLLoader;
		public var optionsHolder:XML;
		
		public var secondsToGo:int;
		public var lifeCount:int;
		public var targetCardShowTime:int;
		public var targetProbability:Number;
		public var cardShowTime:int;
		public var cardGapTime:int
		
		// Holder vars
		public var gameScreen:MovieClip;
		public var doubleFrame:MovieClip;
		public var info:EOskrbaImage;
		public var info_time:EOskrbaLabel;
		public var info_good:EOskrbaLabel;
		public var info_bad:EOskrbaLabel;
		public var info_level:EOskrbaLabel;
		public var info_lives:Array;
		public var info_nlives:Array;
		public var ok:EOskrbaImage;
		public var nok:EOskrbaImage;
		public var time:Timer;
		public var level:int;
		public var frame:MovieClip;
		
		public var cards:Array;
		public var cardsShown:int;
		public var targetCard:EOskrbaImage;
		public var nextCard:EOskrbaImage;
		public var deck:Array;
		public var currentDeck:EOskrbaImage;
		public var instruction:EOskrbaLabel;
		
		// Round vars
		
		// Buttons
		
		// Stopwatches
		public var reactionStopwatch_start:Date;
		public var reactionStopwatch_stop:Date;
		
		// Results
		public var results_good:int;
		public var results_bad:int;
		
		public function NBack0(mainSprite:Sprite)
		{
			super(mainSprite);
		}
		
		override public function select_media():void {
			
			img["ok"] = new EOskrbaImage("img/ok.png");
			img["nok"] = new EOskrbaImage("img/nok.png");
			img["navodila"] = new EOskrbaImage("img/navodila.png");
			img["frame"] = new EOskrbaImage("img/frame.png");
			img["info"] = new EOskrbaImage("img/info.png");
			img["live"] = new EOskrbaImage("img/live.png");
			img["nlive"] = new EOskrbaImage("img/nlive.png");
			
			// Cards
			cards = new Array();
			var i:int;
			var j:int;
			for (i = 0; i <= 3; i++) {
				for (j = 0; j < 13; j++) {
					cards[i*13 + j] = new EOskrbaImage("img/deck/" + i.toString() + "/" + j.toString() + ".png");
					img["card_" + i.toString() + "_" + j.toString()] = cards[i*13 + j];
				}
			}
			
			deck = new Array();
			deck[0] = new EOskrbaImage("img/deck/back.png");
			img["deck_0"] = deck[0];
			deck[1] = new EOskrbaImage("img/deck/deck_1.png");
			img["deck_1"] = deck[1];
			deck[2] = new EOskrbaImage("img/deck/deck_2.png");
			img["deck_2"] = deck[2];
			deck[3] = new EOskrbaImage("img/deck/deck_3.png");
			img["deck_3"] = deck[3];
			deck[4] = new EOskrbaImage("img/deck/deck_4.png");
			img["deck_4"] = deck[4];
			deck[5] = new EOskrbaImage("img/deck/deck_more.png");
			img["deck_5"] = deck[5];
			
			sfx["fail"] = new EOskrbaSoundEffect("sfx/fail.mp3");
			sfx["success"] = new EOskrbaSoundEffect("sfx/success.mp3");
			sfx["flip"] = new EOskrbaSoundEffect("sfx/flip.mp3");
			sfx["drag"] = new EOskrbaSoundEffect("sfx/drag.mp3");
			
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
			
			secondsToGo = parseInt(optionsHolder.secondsToGo[0]);
			lifeCount = parseInt(optionsHolder.lives[0]);
			targetCardShowTime = parseInt(optionsHolder.targetCardShowTime[0]);
			targetProbability = parseFloat(optionsHolder.targetProbability[0]);
			cardShowTime = parseInt(optionsHolder.cardShowTime[0]);
			cardGapTime = parseInt(optionsHolder.cardGapTime[0]);
			
			results_good = 0;
			results_bad = 0;
			cardsShown = 0;
			level = 1;
			currentDeck = deck[0];
			
			gameScreen = new MovieClip();
			
			// frame
			frame = new MovieClip();
			frame.addChild(img["frame"]);
			frame.x = 94;
			frame.y = 30;
			gameScreen.addChild(frame);
			
			// decks
			for each(var d:EOskrbaImage in deck) {
				d.x = 100;
				d.y = 77;
				frame.addChild(d);
				d.alpha = 0;
			}
			deck[0].x = 350;
			deck[0].y = 85;
			
			// cards
			for each(var card:EOskrbaImage in cards) {
				card.x = 350;
				card.y = 85;
				frame.addChild(card);
				card.alpha = 0;
			}
			
			// ok / nok
			ok = img["ok"];
			ok.x = 214;
			ok.y = 104;
			ok.alpha = 0;
			frame.addChild(ok);
			nok = img["nok"];
			nok.x = 214;
			nok.y = 104;
			nok.alpha = 0;
			frame.addChild(nok);
			
			// instruction
			instruction = new EOskrbaLabel("Zapomnite si to karto!\nVsakič, ko se ponovno pojavi, pritisnite tipko za presledek!", 20, 0xc00000);
			var a:TextFormat = instruction.defaultTextFormat;
			a.align = TextFormatAlign.CENTER;
			instruction.setTextFormat(a);
			instruction.x = (800 - instruction.width) / 2;
			instruction.y = 420;
			gameScreen.addChild(instruction);
			
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
			
			info_lives = new Array();
			info_nlives = new Array();
			var n:int = 0;
			for ( n = 0; n < lifeCount; n++) {
				info_lives[n] = new Bitmap(Bitmap((img["live"] as EOskrbaImage).content).bitmapData);
				info_lives[n].x = 370 + n*55;
				info_lives[n].y = 532;
				gameScreen.addChild(this.info_lives[n]);
				info_nlives[n] = new Bitmap(Bitmap((img["nlive"] as EOskrbaImage).content).bitmapData);
				info_nlives[n].x = 370 + n*55;
				info_nlives[n].y = 532;
				info_nlives[n].alpha = 0;
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
			
			game_select_target();
			
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
		
		public function game_select_target():void {
			
			// Shuffle cards
			cards.sort(function(a:EOskrbaImage, b:EOskrbaImage):Number {
				if (Math.random() < 0.5) return -1;
				else return 1;
			});
			targetCard = cards[0];
			
			setTimeout(game_show_target,cardGapTime);
			
		}
		
		public function game_show_target():void {
			
			(sfx["flip"] as EOskrbaSoundEffect).play();
			targetCard.x = 230;
			Tweener.addTween(targetCard, {
				alpha: 1,
				time: 0.5
			});
			setTimeout(game_hide_target,500+targetCardShowTime);
			
		}
		
		public function game_hide_target():void {
		
			instruction.alpha = 0;
			Tweener.addTween(targetCard, {
				alpha:0,
				time: 0.5,
				onComplete: game_start_drawing
			});
		
		}
		
		public function game_start_drawing():void {
			
			time.start();
			game_round();
			
		}
		
		public function game_round():void {
			
			if(secondsToGo > 0 && lifeCount > 0) {
				
				if (Math.random() < targetProbability) {
					nextCard = targetCard;
				} else {
					var rnd:Number = Math.floor(Math.random() * 51) + 1;
					nextCard = cards[rnd];
				}
				
				main.stage.addEventListener(KeyboardEvent.KEY_DOWN, onPatientReaction);
				
				nextCard.x = 350;
				Tweener.addTween(nextCard, {
					alpha: 1,
					time:cardShowTime/1000,
					onComplete: patientDidNotReact
				});
				(sfx["flip"] as EOskrbaSoundEffect).play();
			
			} else game_end();
			
		}
		
		public function onPatientReaction(event:KeyboardEvent):void {
			if (event.keyCode == Keyboard.SPACE) {
				main.stage.removeEventListener(KeyboardEvent.KEY_DOWN, onPatientReaction);
				Tweener.removeTweens(nextCard);
				nextCard.alpha = 1;
				if (nextCard == targetCard) {
					results_good++;
					(sfx["success"] as EOskrbaSoundEffect).play();
					ok.alpha = 1;
					setTimeout(function():void {
						Tweener.addTween(ok, {
							alpha:0,
							time:cardGapTime/1000
						});
					}, cardGapTime);
				} else {
					results_bad++;
					(sfx["fail"] as EOskrbaSoundEffect).play();
					lifeCount--;
					game_looseLife();
				}
				game_round_moveCard();
				setTimeout(game_round, 500 + cardGapTime);
			}
		}
		
		public function patientDidNotReact():void {
			main.stage.removeEventListener(KeyboardEvent.KEY_DOWN, onPatientReaction);
			if (nextCard == targetCard) {
				results_bad++;
				(sfx["fail"] as EOskrbaSoundEffect).play();
				lifeCount--;
				game_looseLife();
			} else {
				results_good++;
			}
			game_round_moveCard();
			setTimeout(game_round, 500 + cardGapTime);
		}
		
		public function game_round_moveCard():void {
			Tweener.addTween(nextCard, {
				x: 100,
				time: 0.5,
				onComplete: function():void {
					(sfx["drag"] as EOskrbaSoundEffect).play();
					nextCard.alpha = 0;
					cardsShown++;
					if (cardsShown < 6) {
						(deck[cardsShown] as EOskrbaImage).alpha = 1;
						currentDeck.alpha = 0;
						currentDeck = deck[cardsShown];
					}
				}
			});
		}
		
		public function game_looseLife():void {
			nok.alpha = 1;
			setTimeout(function():void {
				Tweener.addTween(nok, {
					alpha:0,
					time:cardGapTime/1000
				});
			}, cardGapTime);
			Tweener.addTween(info_nlives[lifeCount], {
				alpha:1,
				time:0.5
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
				json += "\"gameId\": \"0-back\", ";
				json += "\"good\": " + results_good.toString() + ", ";
				json += "\"bad\": " + results_bad.toString() + " }";
				
				trace(json);
				ExternalInterface.call("alert", json);
				
			}, 1000);
			
		}
		
	}

}
