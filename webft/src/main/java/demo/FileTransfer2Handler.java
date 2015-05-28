package demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.springframework.boot.json.JsonSimpleJsonParser;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class FileTransfer2Handler extends TextWebSocketHandler {
	Logger log = Logger.getLogger(getClass());
	
	public final static long hashBlockSize = 1024 * 1024;

	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("CONNECTED");
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.info("CLOSED "+status);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		Map<String, Object> msg = null;
		try {
			JsonSimpleJsonParser jp = new JsonSimpleJsonParser();
			msg = jp.parseMap(message.getPayload());
			String id = (String) msg.get("id");
			if ("file".equals(id)) {
				session.getAttributes().put("file", msg.get("file"));
				session.getAttributes().put("size", msg.get("size"));
			} else if ("hash".equals(id)) {
				ArrayList<String> hash;
				int idx = (int) ((long) msg.get("index"));
				if (idx == 0) {
					session.getAttributes().put("hash", hash = new ArrayList<>());
				} else {
					hash = (ArrayList<String>) session.getAttributes().get("hash");
				}
				if (idx >= hash.size()) {
					hash.add(idx, (String) msg.get("hash"));
				} else {
					hash.set(idx, (String) msg.get("hash"));
				}
			} else if ("init".equals(id)) {
				ArrayList<String> hash = (ArrayList<String>) session.getAttributes().get("hash");
				StringBuilder sb = new StringBuilder();
				for (String h : hash) {
					if (sb.length() > 0) sb.append(" ");
					sb.append(h);
				}
				String hashs = sb.toString();
				session.getAttributes().put("hashs", hashs);
				
				File f = new File(new File("tmp"), ((String) session.getAttributes().get("file"))+".info");
				f.getParentFile().mkdirs();
				Properties info = null;
				if (f.exists()) {
					info = new Properties();
					InputStream in = new FileInputStream(f);
					try {
						info.load(in);
					} finally {
						in.close();
					}
					if (info.getProperty("hash").equals(hashs) && info.getProperty("size").equals(session.getAttributes().get("size").toString())) {
						msg.put("offset", info.getProperty("offset"));
					} else {
						log.info("INVALID HASH ["+info.getProperty("hash")+"]   ["+hash+"]");
						info = null;
					}
				}
				if (info == null) {
					info = new Properties();
					info.setProperty("hash", hashs);
					info.setProperty("size", session.getAttributes().get("size").toString());
					info.setProperty("offset", "0");
					OutputStream out = new FileOutputStream(f);
					try {
						info.store(out, "");
					} finally {
						out.close();
					}
					msg.put("offset", 0);
				} else {
					msg.put("offset", Long.parseLong(info.getProperty("offset")));
				}
				session.getAttributes().put("info", info);
				String rply = new JSONObject(msg).toJSONString();
				session.sendMessage(new TextMessage(rply));
			} else if ("data".equals(id)) {
				byte data[] = Base64.decodeBase64((String) msg.get("data"));

				Properties info = (Properties) session.getAttributes().get("info");
				
				File fd = new File(new File("tmp"), ((String) session.getAttributes().get("file"))+".part");
				RandomAccessFile raf = new RandomAccessFile(fd, "rw");
				try {
					long offset = (long) msg.get("offset");
					raf.seek(offset);
					raf.write(data);
					msg.remove("data");
					offset += data.length;
					// TODO wait until complete block check before set the next offset
					long coff = Long.parseLong(info.getProperty("offset"));
					if (offset >= coff+hashBlockSize) {
						log.info("HERE "+offset+"  "+coff+"   "+hashBlockSize);
						MessageDigest md = MessageDigest.getInstance("SHA-256");
						raf.seek(coff);
						byte bb[] = new byte[1024];
						for (int bl=0; bl<hashBlockSize; ) {
							raf.readFully(bb);
							md.update(bb);
							bl += bb.length;
						}
						String chash = new String(Hex.encode(md.digest()));
						int part = (int) (coff / hashBlockSize);
						log.info("HASH "+part+"   "+offset+"  "+chash);
						ArrayList<String> hash = (ArrayList<String>) session.getAttributes().get("hash");
						if (chash.equals(hash.get(part))) {
							coff += hashBlockSize;
							info.setProperty("offset", String.valueOf(coff));
							File f = new File(new File("tmp"), ((String) session.getAttributes().get("file"))+".info");
							OutputStream out = new FileOutputStream(f);
							try {
								info.store(out, "");
							} finally {
								out.close();
							}
						} else {
							throw new Exception("Invalid hash "+part+"\n"+chash+"\n"+hash.get(part));
						}
					}
					msg.put("offset", offset);
				} finally {
					raf.close();
				}
				String rply = new JSONObject(msg).toJSONString();
				session.sendMessage(new TextMessage(rply));
			} else if ("end".equals(id)) {
				Properties info = (Properties) session.getAttributes().get("info");
				
				File fd = new File(new File("tmp"), ((String) session.getAttributes().get("file"))+".part");
				RandomAccessFile raf = new RandomAccessFile(fd, "rw");
				String chash;
				long coff = Long.parseLong(info.getProperty("offset"));
				long size = Long.parseLong(info.getProperty("size"));
				try {
					byte bb[] = new byte[1024];
					MessageDigest md = MessageDigest.getInstance("SHA-256");
					raf.seek(coff);
					long bl = coff;
					while (bl < size) {
						int len = (int) Math.min(bb.length, size-bl);
						raf.readFully(bb, 0, len);
						md.update(bb, 0, len);
						bl += len;
					}
					chash = new String(Hex.encode(md.digest()));
				} finally {
					raf.close();
				}
				int part = (int) (coff / hashBlockSize);
				log.info("HASH "+part+"   "+chash);
				ArrayList<String> hash = (ArrayList<String>) session.getAttributes().get("hash");
				if (chash.equals(hash.get(part))) {
					File f = new File(new File("tmp"), ((String) session.getAttributes().get("file"))+".info");
					File of = new File(new File("tmp"), (String) session.getAttributes().get("file"));
					if (of.exists()) of.delete();
					if (of.exists()) throw new RuntimeException("Unable to delete "+of.getCanonicalPath());
					fd.renameTo(of);
					f.delete();
				} else {
					throw new Exception("Invalid hash "+part+"\n"+chash+"\n"+hash.get(part));
				}
				String rply = new JSONObject(msg).toJSONString();
				session.sendMessage(new TextMessage(rply));
			} else {
				log.info("UNSUPPORTED "+message.getPayload());
			}
			msg.remove("data");
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			Map<String, Object> mr = new HashMap<>();
			mr.put("id", "error");
			if (msg != null) mr.put("file", msg.get("file"));
			mr.put("error", e.getMessage());
			String rply = new JSONObject(mr).toJSONString();
			session.sendMessage(new TextMessage(rply));
		}
	}
	
	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		log.info("INCOMING BINARY MESSAGE "+message.getPayload());
	}
	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		log.error("TRANSPORT ERROR "+exception.getMessage(), exception);
	}
}
