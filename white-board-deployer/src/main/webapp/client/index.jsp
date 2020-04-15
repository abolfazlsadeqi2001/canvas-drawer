<html>
<head>
<meta charset="utf-8" />
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

var url = "wss://"+host+":"+port+"/white-board-deployer/client"
</script>
<script type="text/javascript" src="index.js"></script>
<style>
	canvas{
		border : black solid 2px;
	}
</style>
</head>
<body onload="init()">
	<canvas></canvas>
</body>
</html>