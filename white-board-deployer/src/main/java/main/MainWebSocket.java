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
	
	Set<Session> sessions = new HashSet<Session>();
	
	@OnOpen
	public void onOpen(Session session) {
		sessions.add(session);
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
