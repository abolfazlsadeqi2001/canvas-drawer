package main;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

@ServerEndpoint("/streamer")
public class StreamerWebSocket {

	private static final String CANVAS_TYPE = "canvas";
	private static final String CLEAR_TYPE = "clear";

	public static String canvasObject;

	public static StringBuilder objects = new StringBuilder();// contain all objects have been gained yet

	@OnOpen
	public void onOpen(Session session) {
		if(canvasObject != null) {
			try {
				session.close();
			} catch (IOException e) {
				System.out.println("ERROR:"+e.getMessage());
			}
		}
		// configure session
		session.setMaxTextMessageBufferSize(ClientWebSocket.MAX_TEXT_MESSAGE_SIZE);
		session.setMaxIdleTimeout(ClientWebSocket.TIME_OUT_PER_MILI_SECONDS);
	}

	@OnMessage
	public void onMessage(Session session, String message) {
		// broadcast message
		ClientWebSocket.broadcastMessage(message);
		// read message
		String stringifiedArray = message.replaceAll("\\},\\{", "}#{");
		String[] array = stringifiedArray.split("#");
		// read objects
		for (String stringifiedObject : array) {
			if (!stringifiedObject.isBlank()) {
				JSONObject object = new JSONObject(stringifiedObject);
				// add current Object to objectsContainer set
				objects.append(stringifiedObject);
				objects.append(",");
				// get object type
				String type = (String) object.get("type");
				// if type = canvas save it as canvas object
				if (type.equals(CANVAS_TYPE)) {
					canvasObject = stringifiedObject;
				}
				// if type = clear clear the set of objects saved until now
				if (type.equals(CLEAR_TYPE)) {
					objects = new StringBuilder();
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
	public void onClose(Session session) {
		canvasObject = null;
		ClientWebSocket.closeAllClients();
		objects = new StringBuilder();
	}
}
