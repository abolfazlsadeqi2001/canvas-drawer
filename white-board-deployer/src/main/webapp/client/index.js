var ws = new WebSocket("wss://localhost:8443/white-board-deployer/main");

var streamTime = undefined;
var currentTime = 0;
var points = [];

var canvas;
var ctx;

var currentIndex = 0;
// call on load
function init(){
	canvas = document.querySelector("canvas");
	ctx = canvas.getContext("2d");
}
// get the points as msg and convert them to array of objects
ws.onmessage = function(msg){
	// read array
	var data = msg.data;
	while(data.indexOf("},{") != -1){
		data = data.replace("},{","}#{");
	}
	var array = data.split("#");
	// parse array stringified objects to object
	array.forEach(element =>{
		if(element != ""){
			var elementObj = JSON.parse(element);
			// set streamTime
			if(streamTime == undefined && elementObj.type != "canvas"){
				streamTime = Number(elementObj.time);
			}
			// push parsed object to points
			points.push(elementObj);
		}
	});
}
//an interval for writing points based on time
setInterval(function(){
	currentTime += 100;
	for(var i=currentIndex; i<points.length;i++){
		if(points[i].time - streamTime <= currentTime){
			eventsHandler(points[i])
			currentIndex = i;
		}else{
			break;
		}
	}
},100);
// handle events based on their type
function eventsHandler(obj){
	if(obj.type == "canvas"){
		handleCanvas(obj)
	}else if(obj.type == "clear"){
		handleClear()
	}else if(obj.type == "point"){
		handlePoint(obj);
	}else if(obj.type == "line"){
		handleLine(obj);
	}
}
// handle canvas event to set the canvas size
function handleCanvas(obj){
	canvas.width = obj.dimensions.width;
	canvas.height = obj.dimensions.height;
}
// handle clear event to clear the canvas
function handleClear(){
	ctx.clearRect(0,0,canvas.width,canvas.height)
}
// handle point event to create a point
function handlePoint(obj){
	ctx.beginPath();
    ctx.fillStyle = obj.color;
    ctx.fillRect(obj.currentPoint.x, obj.currentPoint.y, obj.lineWidth, obj.lineWidth);
    ctx.closePath();
}
//handle point event to create a line
function handleLine(obj){
	ctx.beginPath();
    ctx.moveTo(obj.previousPoint.x, obj.previousPoint.y);
    ctx.lineTo(obj.currentPoint.x, obj.currentPoint.y);
    ctx.strokeStyle = obj.color;
    ctx.lineWidth = obj.lineWidth;
    ctx.stroke();
    ctx.closePath();
}