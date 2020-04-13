package main;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/client")
public class ClientWebSocket {
	
	public static Set<Session> sessions = new HashSet<Session>();
	public static final int MAX_TEXT_MESSAGE_SIZE = 40 * 1024;// max gained text message is 35 kB
	public static final int TIME_OUT_PER_MILI_SECONDS = 10 * 1000;// sync time is 5	
	@OnOpen
	public void onOpen(Session session) throws Exception {
		// add session to sessions  set
		sessions.add(session);
		// configure session
		session.setMaxTextMessageBufferSize(MAX_TEXT_MESSAGE_SIZE);
		session.setMaxIdleTimeout(TIME_OUT_PER_MILI_SECONDS);
		try {
			// send canvas size if exists
			if(StreamerWebSocket.canvasObject != null) {
				session.getBasicRemote().sendText(StreamerWebSocket.canvasObject);
			}
			// send all objects
			session.getBasicRemote().sendText(StreamerWebSocket.objects.toString());
		} catch (IOException e) {
			System.out.println("ERROR:"+e.getMessage());
		}
	}
	
	public static void broadcastMessage(String message) {
		sessions.forEach(se ->{
			try {
				se.getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	public static void closeAllClients() {
		sessions.forEach(session->{
			try {
				session.close();
			} catch (IOException e) {
				System.out.println("ERROR:"+e.getMessage());
			}
		});
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
