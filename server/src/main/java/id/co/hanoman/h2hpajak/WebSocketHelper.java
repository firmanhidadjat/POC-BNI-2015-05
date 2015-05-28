package id.co.hanoman.h2hpajak;

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
import java.util.WeakHashMap;

import org.apache.camel.Exchange;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WebSocketHelper extends BaseHelper {
	static Map<String, Map<String, Object>> sessions = new WeakHashMap<String, Map<String,Object>>();

	public final static long hashBlockSize = 1024 * 1024;
	
	@SuppressWarnings("unchecked")
	public void msg(Exchange exchange) throws Exception {
//		log.info("HEADER "+U.dump(exchange.getIn().getHeaders())+"BODY\n"+U.dump(exchange.getIn().getBody()));
		Map<String, Object> session;
		synchronized (sessions) {
			String key = (String) exchange.getIn().getHeader("websocket.connectionKey");
			session = sessions.get(key);
			if (session == null) {
				sessions.put(key, session = new HashMap<String, Object>());
			}
		}
		JSONObject msg = null;
		try {
			JSONParser jp = new JSONParser();
			msg = (JSONObject) jp.parse(exchange.getIn().getBody(String.class));
			String id = (String) msg.get("id");
			if ("file".equals(id)) {
				session.put("file", msg.get("file"));
				session.put("size", msg.get("size"));
			} else if ("hash".equals(id)) {
				ArrayList<String> hash;
				int idx = ((Long) msg.get("index")).intValue();
				if (idx == 0) {
					session.put("hash", hash = new ArrayList<String>());
				} else {
					hash = (ArrayList<String>) session.get("hash");
				}
				if (idx >= hash.size()) {
					hash.add(idx, (String) msg.get("hash"));
				} else {
					hash.set(idx, (String) msg.get("hash"));
				}
			} else if ("init".equals(id)) {
				ArrayList<String> hash = (ArrayList<String>) session.get("hash");
				StringBuilder sb = new StringBuilder();
				for (String h : hash) {
					if (sb.length() > 0) sb.append(" ");
					sb.append(h);
				}
				String hashs = sb.toString();
				session.put("hashs", hashs);
				
				File f = new File(new File("tmp"), ((String) session.get("file"))+".info");
				log.info("FILE INFO "+f.getCanonicalPath());
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
					if (info.getProperty("hash").equals(hashs) && info.getProperty("size").equals(session.get("size").toString())) {
						msg.put("offset", info.getProperty("offset"));
					} else {
						log.info("INVALID HASH ["+info.getProperty("hash")+"]   ["+hash+"]");
						info = null;
					}
				}
				if (info == null) {
					info = new Properties();
					info.setProperty("hash", hashs);
					info.setProperty("size", session.get("size").toString());
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
				session.put("info", info);
				String rply = msg.toString();
				exchange.getIn().setBody(rply);
			} else if ("data".equals(id)) {
				byte data[] = Base64.decodeBase64((String) msg.get("data"));

				Properties info = (Properties) session.get("info");
				
				File fd = new File(new File("tmp"), ((String) session.get("file"))+".part");
				RandomAccessFile raf = new RandomAccessFile(fd, "rw");
				try {
					long offset = (Long) msg.get("offset");
					raf.seek(offset);
					raf.write(data);
					msg.remove("data");
					offset += data.length;
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
						String chash = Hex.encodeHexString(md.digest());
						int part = (int) (coff / hashBlockSize);
						log.info("HASH "+part+"   "+offset+"  "+chash);
						ArrayList<String> hash = (ArrayList<String>) session.get("hash");
						if (chash.equals(hash.get(part))) {
							coff += hashBlockSize;
							info.setProperty("offset", String.valueOf(coff));
							File f = new File(new File("tmp"), ((String) session.get("file"))+".info");
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
				String rply = msg.toString();
				exchange.getIn().setBody(rply);
			} else if ("end".equals(id)) {
				Properties info = (Properties) session.get("info");
				
				File fd = new File(new File("tmp"), ((String) session.get("file"))+".part");
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
					chash = Hex.encodeHexString(md.digest());
				} finally {
					raf.close();
				}
				int part = (int) (coff / hashBlockSize);
				log.info("HASH "+part+"   "+chash);
				ArrayList<String> hash = (ArrayList<String>) session.get("hash");
				if (chash.equals(hash.get(part))) {
					File f = new File(new File("tmp"), ((String) session.get("file"))+".info");
					File of = new File(new File("tmp"), (String) session.get("file"));
					if (of.exists()) of.delete();
					if (of.exists()) throw new RuntimeException("Unable to delete "+of.getCanonicalPath());
					fd.renameTo(of);
					f.delete();
				} else {
					throw new Exception("Invalid hash "+part+"\n"+chash+"\n"+hash.get(part));
				}
				String rply = msg.toString();
				exchange.getIn().setBody(rply);
			}
//			msg.remove("data");
//			String rply = msg.toString();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			JSONObject mr = new JSONObject();
			mr.put("id", "error");
			if (msg != null) mr.put("file", msg.get("file"));
			mr.put("error", e.getMessage());
			String rply = mr.toString();
			exchange.getIn().setBody(rply);
			if (session != null) session.clear();
		}
	}
}
