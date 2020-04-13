package main;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/main")
public class MainWebSocket {
	
	public static Set<Session> sessions = new HashSet<Session>();
	private static final int MAX_TEXT_MESSAGE_SIZE = 1024*1024*4;
	private static final int TIME_OUT_PER_MILI_SECONDS = 10 * 1000;
	
	@OnOpen
	public void onOpen(Session session) {
		sessions.add(session);
		
		session.setMaxTextMessageBufferSize(MAX_TEXT_MESSAGE_SIZE);
		session.setMaxIdleTimeout(TIME_OUT_PER_MILI_SECONDS);
	}
	
	@OnMessage
	public void onMessage(Session session,String arr) {
		sessions.stream().filter(s -> !s.equals(session)).forEach(s->{
			try {
				s.getBasicRemote().sendText(arr);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	@OnClose
	public void onClose(Session session) {
		sessions.remove(session);
	}
}
