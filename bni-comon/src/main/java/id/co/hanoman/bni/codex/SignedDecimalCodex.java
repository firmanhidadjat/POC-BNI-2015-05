package id.co.hanoman.bni.codex;

import id.co.hanoman.U;
import id.co.hanoman.codex.Codex;
import id.co.hanoman.codex.CodexContext;
import id.co.hanoman.codex.CodexFactory;
import id.co.hanoman.codex.NumericCodex;
import id.co.hanoman.config.Config;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignedDecimalCodex extends NumericCodex {
	private static final Logger LOG = LoggerFactory.getLogger(SignedDecimalCodex.class);

	public SignedDecimalCodex(CodexFactory factory) {
		super(factory);
	}
	
	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		if (LOG.isTraceEnabled()) LOG.trace("init");
		super.init(parent, id, config);
		DEFAULT_PADDING_CHAR = "0";
		DEFAULT_PADDING_MODE = "left";
		DEFAULT_DECIMAL = 4;
	}

    @Override
	public void encode(CodexContext ctx, Object msg) throws Exception {
    	BigDecimal val = ctx.getValueHandler().getCodexValue(ctx, msg, getName(), BigDecimal.class);
    	if (val != null) {
    		encodeValue(ctx, new DecimalFormat("0").format(val) + "+");
    	} else {
    		encodeValue(ctx, "0+");
    	}
	}

	@Override
	public void setCodexValue(CodexContext ctx, Object msg, Object value) {
		if (value instanceof String) {
			String str = (String) value;
			BigDecimal v;
			if (str.length() == 1) {
				if (str.equals("+")) {
					v = BigDecimal.ZERO;
				} else if (str.equals("-")) {
					v = BigDecimal.ZERO;
				} else {
					throw new RuntimeException("Invalid value '"+str+"'");
				}
			} else if (str.endsWith("+")) {
				v = new BigDecimal("0"+str.substring(0, str.length()-1));
			} else if (str.endsWith("-")) {
				v = new BigDecimal("0"+str.substring(0, str.length()-1)).negate();
			} else {
				throw new RuntimeException("Invalid value '"+str+"'");
			}
			super.setCodexValue(ctx, msg, v);
		} else {
			throw new RuntimeException("Not supported value "+U.dump(value));
		}
	}}
