package id.co.hanoman.bni;

import id.co.hanoman.U;
import id.co.hanoman.bni.message.Iso8583;
import id.co.hanoman.codex.Codex;
import id.co.hanoman.codex.CodexContext;
import id.co.hanoman.codex.CodexFactory;
import id.co.hanoman.codex.FileCodexFactory;
import id.co.hanoman.codex.PojoValueHandler;

import java.io.File;
import java.util.Arrays;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCodex {
	static Logger log = LoggerFactory.getLogger(TestCodex.class);

	public static void main(String[] args) {
		try {
			CodexFactory cf = new FileCodexFactory(new File("codex"));
			PojoValueHandler valueHandler = new PojoValueHandler(cf);
			
			Codex c = cf.getCodex("iso8583");
			
			Iso8583 msg = new Iso8583();
			msg.setMessageType("0200");
			msg.setSystemsTraceAuditNumber("123456");
			msg.setResponseCode("00");
			
			CodexContext cc = new CodexContext(valueHandler);
			c.encode(cc, msg);
			byte b1[] = cc.readBytes();
			
			log.info("DATA ["+U.dump(b1)+"]");
			
			Object reply = c.decode(new CodexContext(valueHandler, b1), null);
			log.info("DATA "+U.dump(reply));
			
			cc = new CodexContext(valueHandler);
			c.encode(cc, reply);
			byte[] b2 = cc.readBytes();
			
			log.info("DATA "+U.dump(b2));
			
			log.info("EQ "+Arrays.equals(b1,  b2));
			
			ScriptEngineManager sem = new ScriptEngineManager();
			ScriptEngine scriptEngine = sem.getEngineByName("JavaScript");
			
			scriptEngine.getBindings(ScriptContext.ENGINE_SCOPE).put("msg", msg);
			log.info("DATA "+U.dump(scriptEngine.eval("msg.responseCode")));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
