package id.co.hanoman;

import id.co.hanoman.codex.Codex;
import id.co.hanoman.codex.CodexContext;
import id.co.hanoman.codex.GroupCodex;
import id.co.hanoman.config.Config;
import id.co.hanoman.config.ConfigXML;

import java.io.File;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.JsonObject;

public class TestCodex {
	static Log log = LogFactory.getLog(TestCodex.class);

	public static void mainx(String[] args) {
		try {
			Config cfg = ConfigXML.load(new File("iso8583.xml"));
			
			Codex c = new GroupCodex();
			c.init(null, cfg);
			
			JsonObject msg = new JsonObject();
			msg.addProperty("messageType", "0200");
			msg.addProperty("systemsTraceAuditNumber", "123456");
			msg.addProperty("responseCode", "00");
			
			CodexContext cc = new CodexContext();
			c.encode(cc, msg);
			ByteBuffer buf = cc.buffer();
			buf.flip();
			byte b[] = new byte[buf.remaining()];
			buf.get(b);
			buf.compact();
			
			log.info("DATA "+new String(b));
			
			JsonObject reply = new JsonObject();
			c.decode(new CodexContext(), reply, ByteBuffer.wrap(b));
			log.info("DATA "+reply);
			
			cc = new CodexContext();
			c.encode(cc, reply);
			buf = cc.buffer();
			buf.flip();
			b = new byte[buf.remaining()];
			buf.get(b);
			buf.compact();
			
			log.info("DATA "+new String(b));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
