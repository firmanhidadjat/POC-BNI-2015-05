package id.co.hanoman.codex;

import id.co.hanoman.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecimalMessageLengthCodex extends StringCodex implements GroupLength {
	private static final Logger LOG = LoggerFactory.getLogger(DecimalMessageLengthCodex.class);
	protected int base;

	public DecimalMessageLengthCodex(CodexFactory factory) {
		super(factory);
	}

	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		DEFAULT_PADDING_CHAR = "0";
		DEFAULT_PADDING_MODE = "left";
		
		super.init(parent, id, config);
		base = config.getIntegerValue("@base", 0);
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT ["+getFullName()+"] base = ["+base+"]");
		}
	}

	@Override
	public void encode(CodexContext ctx, Object msg) throws Exception {
		super.encode(ctx, "");
	}

	@Override
	public void encodeLength(CodexContext ctx, int length) throws Exception {
		super.encode(ctx, String.valueOf(length+base-this.length));
	}
	
	@Override
	public void setCodexValue(CodexContext ctx, Object msg, Object value) {
		int vl;
		if (value instanceof String) {
			vl = Integer.parseInt((String) value);
		} else if (value instanceof Number) {
			vl = ((Number) value).intValue();
		} else {
			vl = Integer.parseInt(value.toString());
		}
		if (vl <= 0) throw new RuntimeException("invalid group length "+vl);
		super.setCodexValue(ctx, msg, vl-base+this.length);
	}
	
}
