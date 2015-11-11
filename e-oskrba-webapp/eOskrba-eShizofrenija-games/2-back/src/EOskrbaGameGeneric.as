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
	
	/**
	 * eOskrba-eShizofrenija-games
	 * EOskrbaGameGeneric
	 * @author Alex B.
	 */
	public class EOskrbaGameGeneric 
	{
		
		// Flashvars
		public var fvLevel:String;
		
		// Embedded
		[Embed(source = 'file://C:/Users/Alex/Projects/e-Oskrba/repository/e-oskrba/trunk/e-oskrba-webapp/eOskrba-eShizofrenija-games/generic/src/embed/arial.ttf', fontName = 'ArialFont',embedAsCFF="false")]
		public var ArialFont:Class;
		
		// Main
		public var main:Sprite;
		public var loadingScreen:MovieClip;
		public var loaderBar:EOskrbaLoaderBar;
		
		// Media
		public static var img:Dictionary;
		public static var sfx:Dictionary;
		
		// Rules
		public var rules:DisplayObject;
		
		public function EOskrbaGameGeneric(mainSprite:Sprite) 
		{
			trace("[EOskrbaGameGeneric] Creating new EOskrbaGameGeneric object instance...");
			main = mainSprite;
			
			run();
			
		}
		
		public function run():void {
			
			load_init();
		}
		
		public function load_init():void {
			
			// Get flashvars
			fvLevel = main.root.loaderInfo.parameters.level;
			trace("[EOskrbaGameGeneric] fvLevel = " + fvLevel);
			
			trace("[EOskrbaGameGeneric] Initializing loader...");

			img = new Dictionary();
			sfx = new Dictionary();
			
			loadingScreen = new MovieClip();
			var logo:Loader = new Loader();
			logo.load(new URLRequest("img/load/1_200.png"));
			loadingScreen.addChild(logo);
			logo.x = 300;
			logo.y = 200;
			
			// Draw loader bar
			loaderBar = new EOskrbaLoaderBar();
			loaderBar.x = 200;
			loaderBar.y = 275;
			loadingScreen.addChild(loaderBar);
			
			main.addChild(loadingScreen);
			
			select_media();
			
		}
		
		// select_media() - to be overriden
		public function select_media():void {
			
			trace("[EOskrbaGameGeneric] Warning: select_media() function has not been overridden.");
			load_media();
			
		}
		
		public function load_media():void {
			
			img["int_simple_rules_frame"] = new EOskrbaImage("img/load/simple_rules_frame.png");
			img["int_simple_rules_button"] = new EOskrbaImage("img/load/simple_rules_button.png");
			img["int_simple_rules_button_over"] = new EOskrbaImage("img/load/simple_rules_button_over.png");
			
			sfx["int_beep"] = new EOskrbaSoundEffect("sfx/beep.mp3");
			sfx["int_biip"] = new EOskrbaSoundEffect("sfx/biip.mp3");
			
			var total:Number = 0;
			var image:EOskrbaImage;
			var sound:EOskrbaSoundEffect;
			var loadedCount:int = 0;
			
			for each(image in img) {
				total++;
			}
			for each(sound in sfx) {
				total++;
			}
			
			for each(image in img) {
				image.autoload();
				image.contentLoaderInfo.addEventListener(Event.COMPLETE, function():void {
					loaderBar.addPercent(100.0 / total);
					loadedCount++;
					if (loadedCount == total) {
						setTimeout(game_prepare, 2000);
					}
				});
			}
			
			for each(sound in sfx) {
				sound.autoload();
				sound.addEventListener(Event.COMPLETE, function():void {
					loaderBar.addPercent(100.0 / total);
					loadedCount++;
					if (loadedCount == total) {
						setTimeout(game_prepare, 2000);
					}
				});
			}
			
		}
		
		// game_prepare() - to be overriden
		public function game_prepare():void {
			trace("[EOskrbaGameGeneric] Warning: game_prepare() function has not been overridden.");
			game_prepared();
		}
		
		public function game_prepared():void {
			remove_load_screen();
		}
		
		public function remove_load_screen():void {
			
			Tweener.addTween(loadingScreen, {
				alpha:0,
				time:1,
				onComplete: game_beforeRules
			});
			
		}
		
		public function game_beforeRules():void {
			main.removeChild(loadingScreen);
			game_rules();
		}
		
		// game_rules() - to be overriden
		public function game_rules():void {
			trace("[EOskrbaGameGeneric] Warning: game_rules() function has not been overridden.");
			var simpleRules:EOskrbaSimpleRules = new EOskrbaSimpleRules(
				new EOskrbaRectangle(0, 0, 100, 100, 0xc00000),
				game_countdown
			);
			this.rules = simpleRules;
			main.addChild(simpleRules);
		}
		
		public function game_countdown():void {
			
			main.removeChild(this.rules);
			
			var cd3:EOskrbaLabel = new EOskrbaLabel("3", 136, 0x000000);
			cd3.x = 350;
			cd3.y = 200;
			var cd2:EOskrbaLabel = new EOskrbaLabel("2", 136, 0x000000);
			cd2.x = 350;
			cd2.y = 200;
			var cd1:EOskrbaLabel = new EOskrbaLabel("1", 136, 0x000000);
			cd1.x = 350;
			cd1.y = 200;
			
			setTimeout(function():void {
				(sfx["int_beep"] as EOskrbaSoundEffect).play();
				main.addChild(cd3);
				Tweener.addTween(cd3, {
					alpha:0,
					time:1,
					transition:"easeInQuart"
				});
			}, 0);
			
			setTimeout(function():void {
				main.removeChild(cd3);
				(sfx["int_beep"] as EOskrbaSoundEffect).play();
				main.addChild(cd2);
				Tweener.addTween(cd2, {
					alpha:0,
					time:1,
					transition:"easeInQuart"
				});
			}, 1500);
			
			setTimeout(function():void {
				main.removeChild(cd2);
				(sfx["int_beep"] as EOskrbaSoundEffect).play();
				main.addChild(cd1);
				Tweener.addTween(cd1, {
					alpha:0,
					time:1,
					transition:"easeInQuart"
				});
			}, 3000);
			
			setTimeout(function():void {
				main.removeChild(cd1);
				(sfx["int_biip"] as EOskrbaSoundEffect).play();
				main.stage.focus = main;
				game_start();
			}, 4500);
			
		}
		
		// game_start() - to be overriden
		public function game_start():void {
			trace("[EOskrbaGameGeneric] Warning: game_start() function has not been overridden.");
		}
		
	}

}
