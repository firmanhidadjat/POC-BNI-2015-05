package id.co.hanoman.bni;

import id.co.hanoman.U;
import id.co.hanoman.bni.message.Iso8583;
import id.co.hanoman.codex.Codex;
import id.co.hanoman.codex.CodexContext;
import id.co.hanoman.codex.CodexFactory;
import id.co.hanoman.codex.FileCodexFactory;
import id.co.hanoman.codex.PojoValueHandler;

import java.io.File;
import java.io.InputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestTCP {
	static Logger log = LoggerFactory.getLogger(TestTCP.class);

	public static void main(String[] args) {
		try {
			CodexFactory cf = new FileCodexFactory(new File("src/main/resources/META-INF/codex"));
			
			PojoValueHandler valueHandler = new PojoValueHandler(cf);
			
			Codex c = cf.getCodex("iso8583");
			
			Iso8583 msg = new Iso8583();
			msg.setMessageType("0200");
			msg.setSystemsTraceAuditNumber("123456");
			
			CodexContext cc = new CodexContext(valueHandler);
			c.encode(cc, msg);
			byte b1[] = cc.readBytes();
			
			log.info("DATA ["+U.dump(b1)+"]");
			
			Socket sock = new Socket("localhost", 5050);
			sock.getOutputStream().write(b1);
			InputStream in = sock.getInputStream();
			
			Thread.sleep(1000);
			byte b[] = new byte[8192];
			int len = in.read(b);
			
			log.info("READ "+len+"  "+U.dump(b, 0, len));
			log.info("REPLY "+U.dump(c.decode(new CodexContext(valueHandler, b), null)));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
