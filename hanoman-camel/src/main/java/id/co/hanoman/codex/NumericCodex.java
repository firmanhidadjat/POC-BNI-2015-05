package id.co.hanoman.codex;

import id.co.hanoman.config.Config;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumericCodex extends StringCodex {
	private static final Logger LOG = LoggerFactory.getLogger(NumericCodex.class);
	protected int DEFAULT_DECIMAL = 0;
	protected String DEFAULT_PREFIX = "";
	protected String DEFAULT_SUFFIX = "";
	
	protected int decimal;
	protected String prefix;
	protected String suffix;
	protected BigDecimal multiplier;

	public NumericCodex(CodexFactory factory) {
		super(factory);
		DEFAULT_PADDING_CHAR = "0";
		DEFAULT_PADDING_MODE = "left";
		DEFAULT_ignoreTruncatedString = false; 
	}

	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		super.init(parent, id, config);
		decimal = config.getIntegerValue("@decimal", DEFAULT_DECIMAL);
		multiplier = BigDecimal.ONE.movePointRight(decimal);
		prefix = config.getStringValue("@prefix", DEFAULT_PREFIX);
		suffix = config.getStringValue("@suffix", DEFAULT_SUFFIX);
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT ["+getFullName()+"] decimal = ["+decimal+"]");
			LOG.trace("INIT ["+getFullName()+"] multiplier = ["+multiplier+"]");
			LOG.trace("INIT ["+getFullName()+"] prefix = ["+prefix+"]");
			LOG.trace("INIT ["+getFullName()+"] suffix = ["+suffix+"]");
		}
	}
    
    @Override
	public void encode(CodexContext ctx, Object msg) throws Exception {
    	BigDecimal val = ctx.getValueHandler().getCodexValue(ctx, msg, getName(), BigDecimal.class).multiply(multiplier);
    	if (prefix.length() > 0 || suffix.length() > 0) {
    		encodeValue(ctx, prefix+val.toString()+suffix);
    	} else {
    		encodeValue(ctx, val.toString());
    	}
	}

	@Override
	public void setCodexValue(CodexContext ctx, Object msg, Object value) {
		BigDecimal v;
		if (prefix.length() > 0 || suffix.length() > 0) {
			if (value instanceof BigDecimal) {
				v = (BigDecimal) value;
			} else if (value == null) {
				v = BigDecimal.ZERO;
			} else {
				String str;
				if (value instanceof String) {
					str = (String) value;
				} else {
					str = value.toString();
				}
				if (str.startsWith(prefix) && str.endsWith(prefix)) {
					str = str.substring(prefix.length(), str.length()-prefix.length()-suffix.length());
					if (str.length() == 0) {
						v = BigDecimal.ZERO;
					} else {
						v = new BigDecimal(str);
					}
				} else {
					throw new RuntimeException("Invalid value '"+str+"'");
				}
			}
		} else {
			if (value instanceof String) {
				String str = (String) value;
				if (str.length() == 0) {
					v = BigDecimal.ZERO;
				} else {
					v = new BigDecimal((String) value);
				}
			} else if (value instanceof BigDecimal) {
				v = (BigDecimal) value;
			} else if (value == null) {
				v = BigDecimal.ZERO;
			} else {
				v = new BigDecimal(value.toString());
			}
		}
		super.setCodexValue(ctx, msg, v.divide(multiplier));
	}
}