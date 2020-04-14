<%@page import="configurations.drawing.streamer.DefaultDrawingStreamingVariables"%>
<html>
<head>
<meta charset="utf-8" />
<link href="index.css" rel="stylesheet" />
<script>
var host = <% String serverHost = request.getLocalAddr();
if(serverHost.equals("127.0.0.1") || serverHost.equals("0:0:0:0:0:0:0:1")){
	serverHost = "localhost";
}

out.print("'");
out.print(serverHost);
out.print("'");
%>;
var port = <% out.print(request.getLocalPort()); %>;
var url = "wss://"+host+":"+port+"/white-board-deployer/streamer";

var delay = <%out.print(DefaultDrawingStreamingVariables.getDelay()); %>;
</script>
<script type="text/javascript" src="index.js"></script>
</head>
<body onload="init()">
	<canvas></canvas>
	<div class="marker-container">
		<span color="red"></span>
		<span color="green"></span>
		<span color="blue"></span>
		<span color="black"></span>
	</div>
	<div class="erasers-container">
		<button id="eraser" >eraser</button>
		<input type="number" min="2" max="100" value="10" />
		<button id="clear">clear</button>
	</div>
</body>
</html>