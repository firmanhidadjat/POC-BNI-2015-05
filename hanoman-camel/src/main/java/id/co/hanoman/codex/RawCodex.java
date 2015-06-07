package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public void init(Codex parent, String id, Config config) throws Exception {
		code = config.getStringValue("field[@id = '!code']");
		type = config.getStringValue("field[@id = '!type']");
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT ["+getFullName()+"] code = ["+code+"]");
			LOG.trace("INIT ["+getFullName()+"] type = ["+type+"]");
		}
	}
	
	@Override
	public Object decode(CodexContext ctx, Object msg) throws Exception {
		ChannelBuffer buf = ctx.buffer();
		if (buf.readableBytes() > 0) {
			ctx.getValueHandler().setCodexValue(ctx, msg, "@raw", U.encode64(buf.array(), buf.readerIndex(), buf.writerIndex()));
			if (code != null && code.length() > 0) ctx.getValueHandler().setCodexValue(ctx, msg, "@code", code);
			if (type != null && type.length() > 0) ctx.getValueHandler().setCodexValue(ctx, msg, "@type", type);
			buf.readerIndex(buf.writerIndex());
			return msg;
		}
		return null;
	}
	
	@Override
	public void encode(CodexContext ctx, Object msg) throws Exception {
		String brs = ctx.getValueHandler().getCodexValue(ctx, msg, "@raw", String.class);
		byte br[] = brs != null ? U.decode64(brs) : null;
		if (br != null) {
        	if (LOG.isTraceEnabled()) {
        		LOG.trace("ENCODE ["+getFullName()+"] @"+ctx.buffer().writerIndex()+" WRITE ["+U.dump(br)+"]");
        	}
			ctx.buffer().writeBytes(br);
		}
	}

}
