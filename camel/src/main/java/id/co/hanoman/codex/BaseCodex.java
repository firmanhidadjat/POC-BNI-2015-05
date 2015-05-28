package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class BaseCodex implements Codex {
	private static final Logger LOG = LoggerFactory.getLogger(BaseCodex.class);
	protected String charset;
	protected final String DEFAULT_CHARSET = "ISO-8859-1";

	protected Codex parent;
	protected Config config;
	protected String id;
	protected String fid;
	
	public void init(Codex parent, Config config) throws Exception {
		this.parent = parent;
		this.config = config;
		id = config.getStringValue("@id", null);
		charset = config.getStringValue("@charset", parent instanceof BaseCodex ? ((BaseCodex) parent).charset : DEFAULT_CHARSET);
		if (charset == null) charset = DEFAULT_CHARSET;
		fid = config.getPath();
		if (fid.length() > 0) {
			fid = fid + "/" + id;
		} else {
			fid = id;
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT ["+getFullName()+"] charset = ["+charset+"]");
		}
	}
	
	@Override
	public String getName() {
		return id;
	}
	
	@Override
	public String getFullName() {
		if (parent != null) return parent.getFullName() + "/" + getName();
		return getName();
	}
	
	public void setCodexValue(CodexContext ctx, JsonObject msg, Object value) {
		if (value instanceof String) {
			setCodexValue(ctx, msg, (String) value); 
		} else if (value != null) {
			setCodexValue(ctx, msg, value.toString());
		} else {
			if (LOG.isTraceEnabled()) {
				LOG.trace("DECODE ["+getFullName()+"] VALUE NULL");
			}
			msg.remove(getName());
		}
	}
	
	public void setCodexValue(CodexContext ctx, JsonObject msg, String value) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("DECODE ["+getFullName()+"] VALUE ["+U.dump(value)+"]");
		}
		msg.addProperty(getName(), value);
	}
	
	public void setCodexValue(CodexContext ctx, JsonObject msg, Boolean value) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("DECODE ["+getFullName()+"] VALUE ["+U.dump(value)+"]");
		}
		msg.addProperty(getName(), value);
	}
	
	public void setCodexValue(CodexContext ctx, JsonObject msg, Character value) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("DECODE ["+getFullName()+"] VALUE ["+U.dump(value)+"]");
		}
		msg.addProperty(getName(), value);
	}
	
	public void setCodexValue(CodexContext ctx, JsonObject msg, Number value) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("DECODE ["+getFullName()+"] VALUE ["+U.dump(value)+"]");
		}
		msg.addProperty(getName(), value);
	}
	
	public void setCodexError(CodexContext ctx, JsonObject msg, String message) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("DECODE ["+getFullName()+"] ERROR ["+message+"]");
		}
		JsonObject err = (JsonObject) msg.get("@error");
		if (err == null) msg.add("@error", err = new JsonObject());
		err.addProperty(getFullName(), message);
	}

	public JsonElement getCodexValue(CodexContext ctx, JsonObject msg) {
		return msg.get(getName());
	}

}
