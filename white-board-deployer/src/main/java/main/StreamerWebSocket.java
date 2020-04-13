package main;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

@ServerEndpoint("/streamer")
public class StreamerWebSocket extends WebSocketParent {	
	private static final String CANVAS_TYPE = "canvas";
	private static final String CLEAR_TYPE = "clear";

	private static String canvasStringifiedObject;
	private static StringBuilder objects = new StringBuilder();// contain all objects have been gained yet

	private static boolean isStreamerConnected;
	
	private static int streamerCurrentTime;
	
	public static String getCanvasObject() {
		return canvasStringifiedObject;
	}
	
	public static String getPointsObjects () {
		return objects.toString();
	}
	
	public static int getStreamerCurrentTime() {
		return streamerCurrentTime;
	}
	
	@OnOpen
	public void onOpen(Session session) {
		if(isStreamerConnected) {
			try {
				CloseReason reason = new CloseReason(CloseCodes.CANNOT_ACCEPT,"another streamer is using this server");
				session.close(reason);
			} catch (IOException e) {
				System.out.println("ERROR:"+e.getMessage());
			}
		}else {
			isStreamerConnected = true;
		}
		// configure session
		session.setMaxTextMessageBufferSize(MAX_TEXT_MESSAGE_SIZE);
		session.setMaxIdleTimeout(TIME_OUT_PER_MILI_SECONDS);
	}

	@OnMessage
	public void onMessage(Session session, String message) {
		// broadcast message
		ClientWebSocket.broadcastMessage(message);
		// read message
		String stringifiedArray = message.replaceAll("\\},\\{", "}#{");
		String[] stringifiedJsonArray = stringifiedArray.split("#");
		// read objects
		for (int i=0; i<stringifiedJsonArray.length; i++) {
			String stringifiedObject = stringifiedJsonArray[i];
			if (!stringifiedObject.isBlank()) {
				JSONObject jsonObject = new JSONObject(stringifiedObject);
				// add current Object to objectsContainer set
				objects.append(stringifiedObject);
				objects.append(",");
				// get object type
				String type = (String) jsonObject.get("type");
				// if type = canvas save it as canvas object
				if (type.equals(CANVAS_TYPE)) {
					canvasStringifiedObject = stringifiedObject;
				}
				// if type = clear clear the set of objects saved until now
				if (type.equals(CLEAR_TYPE)) {
					objects = new StringBuilder();
				}
				// if it is the last json object (contain the last time) set the last time on it
				if(i == stringifiedJsonArray.length-1) {
					streamerCurrentTime = jsonObject.getInt("time");
				}
			}
		}
	}
	
	@OnError
	public void error(Throwable th) {
		System.out.println("ERROR:"+th.getMessage());
	}
	
	/**
	 * TODO add clear event on points.txt
	 * @param session
	 */
	@OnClose
	public void onClose(Session session,CloseReason reason) {
		if(reason.getCloseCode() != CloseCodes.CANNOT_ACCEPT) {
			canvasStringifiedObject = null;
			isStreamerConnected = false;
			objects = new StringBuilder();
			ClientWebSocket.closeAllClients();
		}
	}
}
