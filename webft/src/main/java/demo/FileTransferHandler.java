package demo;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.boot.json.JsonSimpleJsonParser;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class FileTransferHandler extends TextWebSocketHandler {
	Logger log = Logger.getLogger(getClass());
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("CONNECTED");
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.info("CLOSED "+status);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		try {
			log.info("INCOMING TEXT MESSAGE "+message.getPayload());
			JsonSimpleJsonParser jp = new JsonSimpleJsonParser();
			Map<String, Object> msg = jp.parseMap(message.getPayload());
			msg.remove("data");
			String rply = new JSONObject(msg).toJSONString();
			log.info("REPLY "+rply);
			session.sendMessage(new TextMessage(rply));
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		log.info("INCOMING BINARY MESSAGE "+message.getPayload());
	}
	
	@Override
	protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
		log.info("INCOMING PONG MESSAGE "+message.getPayload());
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		log.error("TRANSPORT ERROR "+exception.getMessage(), exception);
	}
}
