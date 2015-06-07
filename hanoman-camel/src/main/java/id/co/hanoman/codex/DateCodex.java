package id.co.hanoman.codex;

import id.co.hanoman.config.Config;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateCodex extends StringCodex {
	String dateFormat;

	public DateCodex(CodexFactory factory) {
		super(factory);
	}
	
	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		dateFormat = config.getStringValue("@date-format");
		super.init(parent, id, config);
	}
	
    @Override
	public void encode(CodexContext ctx, Object msg) throws Exception {
    	Date date = ctx.getValueHandler().getCodexValue(ctx, msg, getName(), Date.class);
		encodeValue(ctx, new SimpleDateFormat(dateFormat).format(date));
	}
	
	@Override
	protected void setCodexValue(CodexContext ctx, Object msg, Object value) {
		try {
			if (value instanceof String) {
				super.setCodexValue(ctx, msg, new SimpleDateFormat(dateFormat).parse((String) value));
			} else if (value instanceof Date) {
				super.setCodexValue(ctx, msg, (Date) value);
			} else if (value == null) {
				super.setCodexValue(ctx, msg, null);
			} else {
				super.setCodexValue(ctx, msg, new SimpleDateFormat(dateFormat).parse(value.toString()));
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
