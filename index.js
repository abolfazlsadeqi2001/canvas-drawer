var previousPosition = {x:0,y:0};
var currentPosition = {x:0,y:0};

var canvas;
var ctx;
var canvasDimensions = {width:840,height:420};

var isWriting = false;

var lineWidth;
var color;

var pencilWidth = 2;
var eraserInput;

function init(){
	// configure the canvas
	canvas = document.querySelector("canvas");
	ctx = canvas.getContext("2d");
	canvas.width = canvasDimensions.width;
	canvas.height = canvasDimensions.height;
	// configure markers
	var containerOfMarkers = document.querySelector(".marker-container");
}