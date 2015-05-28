package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.codex.GroupCodex.GroupCodexContext;
import id.co.hanoman.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class GroupLengthCodex extends StringCodex implements GroupLength {
	private final static Logger LOG = LoggerFactory.getLogger(GroupLengthCodex.class);
	protected int base;
	protected byte bzf[];

	public void init(Codex parent, Config config) throws Exception {
		DEFAULT_PADDING_CHAR = "0";
		DEFAULT_PADDING_MODE = "left";
		DEFAULT_ignoreTruncatedString = false; 
		super.init(parent, config);
		base = config.getIntegerValue("@base", 0);
		bzf = new byte[length];
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT ["+getFullName()+"] base = ["+base+"]");
		}
	}

	@Override
	public void encode(CodexContext ctx, JsonObject msg) throws Exception {
		if (((GroupCodexContext) ctx).lengthCodex != null) throw new Exception("Duplicate lengthCodex '"+((GroupCodexContext) ctx).lengthCodex.getFullName()+"' with '"+getFullName()+"'");
		((GroupCodexContext) ctx).lengthCodex = this;
		((GroupCodexContext) ctx).lengthPos = ctx.buf.position();
		ctx.buf.put(bzf);
	}
	
	@Override
	public void encodeLength(CodexContext ctx, int length) throws Exception {
		super.encode(ctx, String.valueOf(length+base-this.length));
	}
	
	@Override
	public void setCodexValue(CodexContext ctx, JsonObject msg, String text) {
		LOG.info("SET CODEX VALUE ["+text+"]");
		int vl = Integer.parseInt(text);
		if (vl <= 0) throw new RuntimeException("invalid group length "+vl);
		((GroupCodexContext) ctx).setLength(vl-base);
		LOG.info("CTX >>> "+U.dump(ctx));
		super.setCodexValue(ctx, msg, text);
	}
}
