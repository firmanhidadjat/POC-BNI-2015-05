package id.co.hanoman.codex;

import org.apache.velocity.context.Context;

public class VelocityContextValueHandler implements Context {
	final CodexContext ctx;
	final Object obj;
	final ValueHandler valueHandler;
	
	public VelocityContextValueHandler(CodexContext ctx, Object obj) {
		this.ctx = ctx;
		this.obj = obj;
		this.valueHandler = ctx.valueHandler;
	}

	@Override
	public boolean containsKey(Object key) {
		return valueHandler.hasCodexValue(ctx, obj, (String) key);
	}

	@Override
	public Object get(String key) {
		return valueHandler.getCodexValue(ctx, obj, (String) key, Object.class);
	}

	@Override
	public Object[] getKeys() {
		return null;
	}

	@Override
	public Object put(String key, Object value) {
		valueHandler.setCodexValue(ctx, obj, (String) key, value);
		return value;
	}

	@Override
	public Object remove(Object key) {
		Object value = valueHandler.getCodexValue(ctx, obj, (String) key, Object.class);
		valueHandler.setCodexValue(ctx, obj, (String) key, null);
		return value;
	}

}
