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
	
	public class SpatialSliderGrid extends MovieClip
	{
	
		private static var oneToNineChars:Array = new Array("1", "2", "3", "4", "5", "6", "7", "8", "9");
		
		public var grid:Array;
		public var gridSolution:Array;
		public var boxImage:EOskrbaImage;
		public var boxes:Array;
		public var emptyBoxes:Array;
		
		public var mouseDownX:Number;
		public var mouseDownY:Number;
		public var mouseUpX:Number;
		public var mouseUpY:Number;
		
		public var pickSFX:EOskrbaSoundEffect;
		public var dropSFX:EOskrbaSoundEffect;
		
		public var onSuccess:Function;
		public var alreadyShuffled:Boolean;
		
		public var level:int;
		
		public function SpatialSliderGrid(gridDescription:String, onSuccess:Function, boxImage:EOskrbaImage, boxOverImage:EOskrbaImage, boxEmptyImage:EOskrbaImage, pickSFX:EOskrbaSoundEffect, dropSFX:EOskrbaSoundEffect) 
		{
			
			this.onSuccess = onSuccess;

			this.grid = new Array();
			this.gridSolution = new Array();
			this.boxImage = boxImage;
			this.level = level;
			alreadyShuffled = false;
			
			this.pickSFX = pickSFX;
			this.dropSFX = dropSFX;
			
			var gridDescriptionLength:int = gridDescription.length;
			var j:int = 0;
			for (var i:int = 0; i < gridDescriptionLength; i++) {
				var char:String = gridDescription.charAt(i);
				if (char == "#") {
					this.grid[j] = -1;
					this.gridSolution[j] = -1;
					j++;
				} else if (char == "*") {
					this.grid[j] = 0;
					this.gridSolution[j] = 0;
					j++;
				} else if (char == "0") {
					this.grid[j] = 10;
					this.gridSolution[j] = 10;
					j++;
				} else if (oneToNineChars.indexOf(char) >= 0) {
					this.grid[j] = parseInt(char);
					this.gridSolution[j] = parseInt(char);
					j++;
				}
			}
			
			this.boxes = new Array();
			this.boxes[0] = false;
			this.emptyBoxes = new Array();
			for (var k:int = 0; k < grid.length; k++) {
				if (grid[k] == 0) {
					this.emptyBoxes.push(new SpatialSliderBox(0, boxEmptyImage, boxEmptyImage));
				} else if (grid[k] > 0) {
					this.boxes[grid[k]] = new SpatialSliderBox(grid[k], boxImage, boxOverImage);
				}
			}
			
			for (var l:int = 1; l < this.boxes.length; l++ ) {
				this.addChild(this.boxes[l]);
			}
			for (var m:int = 0; m < this.emptyBoxes.length; m++ ) {
				this.addChild(this.emptyBoxes[m]);
			}
			
			this.refresh();
			
		}
		
		public function enableMoves():void {
			this.addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown);
			this.addEventListener(MouseEvent.MOUSE_UP, onMouseUp);
		}
		
		public function disableMoves():void {
			this.removeEventListener(MouseEvent.MOUSE_DOWN, onMouseDown);
			this.removeEventListener(MouseEvent.MOUSE_UP, onMouseUp);
		}
		
		public function refresh():void {
			
			var emptyBoxIter:int = 0;
			var box:SpatialSliderBox;
			var oks:int = 1;
			
			for (var i:int = 0; i < grid.length; i++) {
				
				if (grid[i] == 0) {
					box = emptyBoxes[emptyBoxIter];
					emptyBoxIter++;
					box.x = (i % 5) * 80;
					box.y = (Math.floor(i / 5) * 80);
				} else if (grid[i] > 0) {
					box = boxes[grid[i]];
					box.x = (i % 5) * 80;
					box.y = (Math.floor(i / 5) * 80);
					if (grid[i] == gridSolution[i]) {
						box.setOk();
						oks++;
					}
					else box.setNok();
				}
			}
			
			if (oks == boxes.length && alreadyShuffled) {
				this.onSuccess();
			}
			
		}
		
		public function getPossibleMovesForBoxAt(boxIndex:int):Array {
			
			var result:Array = new Array();
			result["right"] = false;
			result["left"] = false;
			result["up"] = false;
			result["down"] = false;
			
			if (grid[boxIndex] < 0) return result;
			
			var boxRow:int = Math.floor(boxIndex/5);
			var boxColumn:int = boxIndex % 5;
			
			// left
			if (boxColumn - 1 >= 0) if (grid[boxRow * 5 + boxColumn - 1] == 0) result["left"] = true;
			
			// right
			if (boxColumn + 1 < 5) if (grid[boxRow * 5 + boxColumn + 1] == 0) result["right"] = true;
			
			// up
			if (boxRow - 1 >= 0) if (grid[(boxRow - 1) * 5 + boxColumn] == 0) result["up"] = true;
			
			// down
			if (boxRow + 1 < 5) if (grid[(boxRow + 1) * 5 + boxColumn] == 0) result["down"] = true;
			
			return result;
			
		}
		
		public function getPossibleMovesCountForBoxAt(boxIndex:int):int {
			
			var moves:Array = getPossibleMovesForBoxAt(boxIndex);
			var result:int = 0;
			if (moves["left"]) result++;
			if (moves["right"]) result++;
			if (moves["up"]) result++;
			if (moves["down"]) result++;
			return result;
			
		}
		
		public function getAllMovableBoxesIndexes():Array {
			
			var result:Array = new Array;
			
			for (var i:int = 0; i < grid.length; i++) {
				var moves:Array = getPossibleMovesForBoxAt(i);
				if ( (moves["left"] || moves["right"] || moves["up"] || moves["down"]) && grid[i] > 0) {
					result.push(i);
				}
			}
			
			return result;
			
		}
		
		public function getAllMovableBoxesIndexesExcept(exceptIndex:int):Array {
			
			var result:Array = new Array;
			var movableBoxesIndexes:Array = getAllMovableBoxesIndexes();
			
			for (var i:int = 0; i < movableBoxesIndexes.length; i++) {
				if (movableBoxesIndexes[i] != exceptIndex) {
					result.push(movableBoxesIndexes[i]);
				}
			}
			
			return result;
			
		}
		
		public function shuffle(level:int):void {
			
			var movableBoxesIndexes:Array = getAllMovableBoxesIndexes();
			var boxToMoveIndex:int = movableBoxesIndexes[ Math.floor( Math.random() * movableBoxesIndexes.length ) ];
			var movedToIndex:int = moveRandomlyBoxAt(boxToMoveIndex);
			
			for (var i:int = 1; i < level; i++) {
				
				movableBoxesIndexes = getAllMovableBoxesIndexesExcept(movedToIndex);
				
				var movableBoxesPicker:Array = new Array;
				var maxTimesMoved:int = 0;
				for (var j:int = 0; j < movableBoxesIndexes.length; j++) {
					var timesMoved:int = SpatialSliderBox(boxes[grid[movableBoxesIndexes[j]]]).timesMoved;
					if (timesMoved > maxTimesMoved) maxTimesMoved = timesMoved;
				}
				for (var k:int = 0; k < movableBoxesIndexes.length; k++ ) {
					for (var l:int = 0; l < maxTimesMoved - SpatialSliderBox(boxes[grid[movableBoxesIndexes[k]]]).timesMoved; l++) {
						movableBoxesPicker.push(movableBoxesIndexes[k]);
					}
				}
				if (movableBoxesPicker.length == 0) {
					maxTimesMoved++;
					for (var m:int = 0; m < movableBoxesIndexes.length; m++ ) {
						for (var n:int = 0; n < maxTimesMoved - SpatialSliderBox(boxes[grid[movableBoxesIndexes[m]]]).timesMoved; n++) {
							movableBoxesPicker.push(movableBoxesIndexes[m]);
						}
					}
				}
				trace(movableBoxesPicker);
				boxToMoveIndex = movableBoxesPicker[ Math.floor( Math.random() * movableBoxesPicker.length ) ];
				movedToIndex = moveRandomlyBoxAt(boxToMoveIndex);
				
			}
			
			dropSFX.play();
			alreadyShuffled = true;
			this.refresh();
			
		}
		
		public function moveRandomlyBoxAt(boxIndex:int):int {
			
			var moves:Array = getPossibleMovesForBoxAt(boxIndex);
			moves.sort(function(a:Boolean, b:Boolean):Number {
				if (Math.random() < 0.5) return -1;
				else return 1;
			});
			var direction:String;
			for (var key:String in moves) {
				if (moves[key] == true) direction = key;
			}
			return moveBoxAtInDirection(boxIndex, direction);
			
		}
		
		public function moveBoxAtInDirection(boxIndex:int, direction:String):int {
			
			SpatialSliderBox(boxes[grid[boxIndex]]).timesMoved++;
			if (direction == "up") { grid[boxIndex - 5] = grid[boxIndex]; grid[boxIndex] = 0; return boxIndex - 5; }
			else if (direction == "down") { grid[boxIndex + 5] = grid[boxIndex]; grid[boxIndex] = 0; return boxIndex + 5; }
			else if (direction == "left") { grid[boxIndex - 1] = grid[boxIndex]; grid[boxIndex] = 0; return boxIndex - 1; }
			else if (direction == "right") { grid[boxIndex + 1] = grid[boxIndex]; grid[boxIndex] = 0; return boxIndex + 1; }
			else return -1;
		}
		
		public function onMouseDown(event:MouseEvent):void {
			
			this.mouseDownX = event.stageX - this.x;
			this.mouseDownY = event.stageY - this.y;
			
		}
		
		public function onMouseUp(event:MouseEvent):void {
			
			this.mouseUpX = event.stageX - this.x;
			this.mouseUpY = event.stageY - this.y;
			
			var boxDownIndex:int = Math.floor(mouseDownY / 80) * 5 + Math.floor(mouseDownX / 80);
			var boxUpIndex:int = Math.floor(mouseUpY / 80) * 5 + Math.floor(mouseUpX / 80);
			
			if ( (boxDownIndex == boxUpIndex) && (grid[boxDownIndex] > 0) ) {
				if (getPossibleMovesCountForBoxAt(boxDownIndex) == 1) {
					var moves:Array = getPossibleMovesForBoxAt(boxDownIndex);
					var direction:String;
					for (var key:String in moves) {
						if (moves[key] == true) direction = key;
					}
					moveBoxAtInDirection(boxDownIndex, direction);
					this.refresh();
				}
			} else if (grid[boxDownIndex] > 0 && grid[boxUpIndex] == 0) {
				
				var boxDownRow:int = Math.floor(boxDownIndex / 5);
				var boxDownColumn:int = boxDownIndex % 5;
				var boxUpRow:int = Math.floor(boxUpIndex / 5);
				var boxUpColumn:int =  boxUpIndex % 5;
				
				if(boxUpRow == boxDownRow && boxUpColumn-boxDownColumn == 1) moveBoxAtInDirection(boxDownIndex, "right");
				else if (boxUpRow == boxDownRow && boxUpColumn - boxDownColumn == -1) moveBoxAtInDirection(boxDownIndex, "left");
				else if (boxUpColumn == boxDownColumn && boxUpRow - boxDownRow == 1) moveBoxAtInDirection(boxDownIndex, "down");
				else if (boxUpColumn == boxDownColumn && boxUpRow - boxDownRow == -1) moveBoxAtInDirection(boxDownIndex, "up");
				this.refresh();
			}
			pickSFX.play();
			
		}
		
	}

}
