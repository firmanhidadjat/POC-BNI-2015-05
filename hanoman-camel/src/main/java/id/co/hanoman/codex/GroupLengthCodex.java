package id.co.hanoman.codex;

import id.co.hanoman.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupLengthCodex extends StringCodex implements GroupLength {
	private final static Logger LOG = LoggerFactory.getLogger(GroupLengthCodex.class);
	protected int base;
	protected byte bzf[];

	public GroupLengthCodex(CodexFactory factory) {
		super(factory);
	}

	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		DEFAULT_PADDING_CHAR = "0";
		DEFAULT_PADDING_MODE = "left";
		DEFAULT_ignoreTruncatedString = false; 
		super.init(parent, id, config);
		base = config.getIntegerValue("@base", 0);
		bzf = new byte[length];
		for (int i=0; i<length; i++) bzf[i] = padChar;
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT ["+getFullName()+"] base = ["+base+"]");
		}
	}

	@Override
	public void encode(CodexContext ctx, Object msg) throws Exception {
		ctx.buffer().writeBytes(bzf);
	}
	
	@Override
	public void encodeLength(CodexContext ctx, int length) throws Exception {
		super.encodeValue(ctx, String.valueOf(length+base-this.length));
	}
	
	@Override
	public void setCodexValue(CodexContext ctx, Object msg, Object value) {
		if (LOG.isTraceEnabled()) LOG.trace("SET CODEX VALUE ["+value+"]");
		int vl;
		if (value instanceof String) {
			String str = (String) value;
			if (str.length() > 0) {
				vl = Integer.parseInt(str);
			} else {
				vl = 0;
			}
		} else if (value instanceof Number) {
			vl = ((Number) value).intValue();
		} else {
			vl = Integer.parseInt(value.toString());
		}
		if (vl <= 0) throw new RuntimeException("invalid group length "+vl);
		super.setCodexValue(ctx, msg, vl-base+this.length);
	}
}
