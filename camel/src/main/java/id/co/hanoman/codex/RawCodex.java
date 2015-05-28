package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class RawCodex implements Codex {
	private static final Logger LOG = LoggerFactory.getLogger(RawCodex.class);
	String code;
	String type;
	
	public String getName() {
		return "raw";
	}
	
	public String getFullName() {
		return getName();
	}
	
	@Override
	public void init(Codex parent, Config config) throws Exception {
		code = config.getStringValue("field[@id = '!code']");
		type = config.getStringValue("field[@id = '!type']");
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT ["+getFullName()+"] code = ["+code+"]");
			LOG.trace("INIT ["+getFullName()+"] type = ["+type+"]");
		}
	}
	
	@Override
	public boolean decode(CodexContext ctx, JsonObject msg, ByteBuffer buf) throws Exception {
		if (buf.remaining() > 0) {
			msg.addProperty("@raw", U.encode64(buf.array(), buf.position(), buf.limit()));
			if (code != null && code.length() > 0) msg.addProperty("@code", code);
			if (type != null && type.length() > 0) msg.addProperty("@type", type);
			buf.position(buf.limit());
			return true;
		}
		return false;
	}
	
	@Override
	public void encode(CodexContext ctx, JsonObject msg) throws Exception {
		String brs = msg.get("@raw").getAsString();
		byte br[] = brs != null ? U.decode64(brs) : null;
		if (br != null) {
        	if (LOG.isTraceEnabled()) {
        		LOG.trace("ENCODE ["+getFullName()+"] @"+ctx.buf.position()+" WRITE ["+U.dump(br)+"]");
        	}
			ctx.buf.put(br);
		}
	}

}
