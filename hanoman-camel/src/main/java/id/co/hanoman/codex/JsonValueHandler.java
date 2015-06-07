package id.co.hanoman.codex;

import java.math.BigDecimal;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonValueHandler implements ValueHandler {
//	private static final Logger LOG = LoggerFactory.getLogger(JsonValueHandler.class);

	public JsonValueHandler(CodexFactory codexFactory) {
	}
	
	@Override
	public void setCodexValue(CodexContext ctx, Object obj, String field, Object value) {
		JsonObject jo = (JsonObject) obj;
		if (value instanceof String) {
			jo.addProperty(field, (String) value);
		} else if (value instanceof Number) {
			jo.addProperty(field, (Number) value);
		} else if (value instanceof Boolean) {
			jo.addProperty(field, (Boolean) value);
		} else if (value instanceof Character) {
			jo.addProperty(field, (Character) value);
		} else if (value instanceof JsonElement) {
			jo.add(field, (JsonElement) value);
		} else if (value == null) {
			jo.remove(field);
		} else {
			jo.addProperty(field, value.toString());
		}
	}

	@Override
	public void setCodexError(CodexContext ctx, Object obj, String field, String message) {
		JsonObject jo = (JsonObject) obj;
		JsonObject err;
		if (jo.has("@error")) {
			err = jo.getAsJsonObject("@error");
		} else {
			jo.add("@error", err = new JsonObject());
		}
		err.addProperty(field, message);
	}

	@Override
	public boolean hasCodexValue(CodexContext ctx, Object obj, String field) {
		JsonObject jo = (JsonObject) obj;
		return jo.has(field);
	}


	@SuppressWarnings("unchecked")
	public <T> T getCodexValue(CodexContext ctx, Object obj, String field, Class<T> type) {
		JsonObject jo = (JsonObject) obj;
		JsonElement val = jo.get(field);
		if (val == null) {
			return null;
		} else if (type.isAssignableFrom(String.class)) {
			return (T) val.getAsString();
		} else if (type.isAssignableFrom(BigDecimal.class)) {
			return (T) val.getAsBigDecimal();
		} else if (type.isAssignableFrom(Long.class)) {
			return (T) Long.valueOf(val.getAsLong());
		} else if (type.isAssignableFrom(Double.class)) {
			return (T) Double.valueOf(val.getAsDouble());
		} else if (type.isAssignableFrom(Integer.class)) {
			return (T) Integer.valueOf(val.getAsInt());
		}
		throw new RuntimeException("Not supported "+type.getName()+" "+val);
	}
	
	@Override
	public Object newObject(Codex codex) {
		return new JsonObject();
	}
}
