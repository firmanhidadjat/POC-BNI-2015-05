package id.co.hanoman.codex;

import id.co.hanoman.codex.GroupCodex.GroupCodexContext;
import id.co.hanoman.config.Config;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class GroupLengthBinaryCodex extends NumericBinaryCodex implements GroupLength {
	private static final Logger LOG = LoggerFactory.getLogger(GroupLengthBinaryCodex.class);
	int base;
	byte bzf[];

	public void init(Codex parent, Config config) throws Exception {
		super.init(parent, config);
		base = config.getIntegerValue("@base", 0);
		bzf = new byte[length];
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT ["+getFullName()+"] base = ["+base+"]");
		}
	}
	
	@Override
	public void setCodexValue(CodexContext ctx, JsonObject msg, Object value) {
		int vl = value instanceof BigDecimal ? ((BigDecimal) value).intValue() : value instanceof String ? Integer.parseInt((String) value) : Integer.parseInt(value.toString());
		if (vl <= 0) throw new RuntimeException("invalid group length "+vl);
		((GroupCodexContext) ctx).setLength(vl-base);
		super.setCodexValue(ctx, msg, value.toString());
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
}
