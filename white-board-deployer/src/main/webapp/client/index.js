var ws = new WebSocket("wss://localhost:8443/white-board-deployer/main");
var currentTime = 0;
var points = [];
var canvas;
var ctx;

function init(){
	canvas = document.querySelector("canvas");
	ctx = canvas.getContext("2d");
}

setInterval(function(){
	currentTime += 100;
	points.forEach(obj=>{
		if(obj.currentTime <= currentTime){
			points.shift();
			eventsHandler(obj)
		}
	})
},100);

ws.onmessage = function(msg){
	var data = msg.data;
	while(data.indexOf("},{") != -1){
		data = data.replace("},{","}#{");
	}
	var array = data.split("#")
	array.forEach(element =>{
		if(element != ""){
			points.push(JSON.parse(element))
		}
	})
}

function eventsHandler(obj){
	console.log("new handle")
	if(obj.type == "canvas"){
		handleCanvas(obj)
	}else if(obj.type == "clear"){
		handleClear()
	}else if(obj.type == "point"){
		handlePoint(obj);
	}else if(obj.type == "line"){
		handleLine(obj);
	}else{
		console.log(obj.type +"not defined")
	}
}

function handleCanvas(obj){
	canvas.width = obj.dimensions.width;
	canvas.height = obj.dimensions.height;
}

function handleClear(){
	ctx.clearRect(0,0,canvas.width,canvas.height)
}

function handlePoint(obj){
	ctx.beginPath();
    ctx.fillStyle = obj.color;
    ctx.fillRect(obj.currentPoint.x, obj.currentPoint.y, obj.lineWidth, obj.lineWidth);
    ctx.closePath();
}

function handleLine(obj){
	ctx.beginPath();
    ctx.moveTo(obj.previousPoint.x, obj.previousPoint.y);
    ctx.lineTo(obj.currentPoint.x, obj.currentPoint.y);
    ctx.strokeStyle = obj.color;
    ctx.lineWidth = obj.lineWidth;
    ctx.stroke();
    ctx.closePath();
}