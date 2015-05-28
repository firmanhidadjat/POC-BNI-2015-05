package id.co.hanoman.codex;

import id.co.hanoman.config.Config;

import com.google.gson.JsonObject;

public class NumericCodex extends StringCodex {

	public void init(Codex parent, Config config) throws Exception {
		DEFAULT_PADDING_CHAR = "0";
		DEFAULT_PADDING_MODE = "left";
		DEFAULT_ignoreTruncatedString = false; 
		super.init(parent, config);
	}

	@Override
	public void setCodexValue(CodexContext ctx, JsonObject msg, String text) {
		if (text == null || text.length() == 0) {
			super.setCodexValue(ctx, msg, "0");
		} else {
			super.setCodexValue(ctx, msg, text);
		}
	}
}