var previousPosition = {x:0,y:0};
var currentPosition = {x:0,y:0};

var canvas;
var ctx;
var canvasDimensions = {width:930,height:420};

var markerSize = 28;

var isWriting = false;
var isInside = false;

var lineWidth;
var color;
var pencilWidth = 2;
var eraserWidth;

function init(){
	// configure the canvas
	canvas = document.querySelector("canvas");
	ctx = canvas.getContext("2d");
	canvas.width = canvasDimensions.width;
	canvas.height = canvasDimensions.height;
	// configure markers
	var markers = document.querySelectorAll(".marker-container *");
	markers.forEach(marker =>{
		marker.style.backgroundColor = marker.getAttribute("color");
		marker.style.width = markerSize;
		marker.style.height = markerSize;
		marker.addEventListener("click",e=>{
			color = marker.getAttribute("color");
			lineWidth = pencilWidth;
		});
	});
	// configure eraser
	var eraserInput = document.querySelector("[type='number']");
	eraserInput.addEventListener("change",e=>{
		eraserWidth = eraserInput.value;
	});
	
	var eraserButton = document.querySelector("#eraser");
	eraserButton.addEventListener("click",e=>{
		color = "white";
		lineWidth = eraserWidth;
	});
	// handle canvas events
	canvas.addEventListener("mousedown",down);
	canvas.addEventListener("mouseup",up);
	canvas.addEventListener("mouseenter",enter);
	canvas.addEventListener("mouseleave",leave);
	canvas.addEventListener("mousemove",move);
}