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

public class PrintDecimalCodex extends NumericCodex {
	private static final Logger LOG = LoggerFactory.getLogger(PrintDecimalCodex.class);
	protected String DEFAULT_FORMAT = "#,##0.00";
	String format;

	public PrintDecimalCodex(CodexFactory factory) {
		super(factory);
		DEFAULT_PADDING_CHAR = " ";
		DEFAULT_SUFFIX = " ";
	}
	
	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		if (LOG.isTraceEnabled()) LOG.trace("init");
		format = config.getStringValue("@format", DEFAULT_FORMAT);
		super.init(parent, id, config);
	}

    @Override
	public void encode(CodexContext ctx, Object msg) throws Exception {
    	BigDecimal val = ctx.getValueHandler().getCodexValue(ctx, msg, getName(), BigDecimal.class);
    	String str = new DecimalFormat(format).format(val);
		if (LOG.isTraceEnabled()) LOG.trace("encode["+getFullName()+"]  "+val+"  '"+str+"'");
    	if (prefix.length() > 0 || suffix.length() > 0) {
    		str = prefix + str.replaceAll("\\.", "_").replaceAll(",", ".").replaceAll("_", ",") + suffix;
    	} else {
    		str = str.replaceAll("\\.", "_").replaceAll(",", ".").replaceAll("_", ",");
    	}
		encodeValue(ctx, str);
	}

	@Override
	public void setCodexValue(CodexContext ctx, Object msg, Object value) {
		if (LOG.isTraceEnabled()) LOG.trace("setCodexValue ["+getFullName()+"]  "+U.dump(value));
		if (value instanceof String) {
			String str = ((String) value).replaceAll(",", "");
			if (LOG.isTraceEnabled()) LOG.trace("REPLACE '"+value+"'  '"+str+"'");
			super.setCodexValue(ctx, msg, str);
		} else {
			throw new RuntimeException("Not supported value "+U.dump(value));
		}
	}}
