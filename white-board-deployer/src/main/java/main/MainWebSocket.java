package main;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONObject;

@ServerEndpoint("/main")
public class MainWebSocket {
	
	public static Set<Session> sessions = new HashSet<Session>();
	private static final int MAX_TEXT_MESSAGE_SIZE = 40 * 1024;// max gained text message is 35 kB
	private static final int TIME_OUT_PER_MILI_SECONDS = 10 * 1000;// sync time is 5
	
	private static final String CANVAS_TYPE = "canvas";
	private static final String CLEAR_TYPE = "clear";
	
	private static String canvasTypeObject;
	
	private static StringBuilder objects = new StringBuilder();// contain all objects have been gained yet
	
	@OnOpen
	public void onOpen(Session session) throws Exception {
		// add session to sessions  set
		sessions.add(session);
		// configure session
		session.setMaxTextMessageBufferSize(MAX_TEXT_MESSAGE_SIZE);
		session.setMaxIdleTimeout(TIME_OUT_PER_MILI_SECONDS);
		try {
			// send canvas size if exists
			if(canvasTypeObject != null) {
				session.getBasicRemote().sendText(canvasTypeObject);
			}
			// send all objects
			session.getBasicRemote().sendText(objects.toString());
		} catch (IOException e) {
			System.out.println("ERROR:"+e.getMessage());
		}
	}
	
	@OnMessage
	public void onMessage(Session session,String message) {
		// broadcast to other cients
		sessions.stream().filter(s -> !s.equals(session)).forEach(s->{
			try {
				s.getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		// read message
		String stringifiedArray = message.replaceAll("\\},\\{", "}#{");
		String[] array = stringifiedArray.split("#");
		// read objects
		for (String stringifiedObject : array) {
			if(!stringifiedObject.isBlank()) {
				JSONObject object = new JSONObject(stringifiedObject);
				// add current Object to objectsContainer set
				objects.append(stringifiedObject);
				objects.append(",");
				// get object type
				String type = (String) object.get("type");
				// if type = canvas save it as canvas object
				if(type.equals(CANVAS_TYPE)) {
					canvasTypeObject = stringifiedObject;
				}
				// if type = clear clear the set of objects saved until now
				if(type.equals(CLEAR_TYPE)) {
					objects = new StringBuilder();
				}
			}
		}
	}
	
	@OnError
	public void error(Throwable th) {
		System.out.println("ERROR:"+th.getMessage());
	}
	
	@OnClose
	public void onClose(Session session) {
		sessions.remove(session);
	}
}
