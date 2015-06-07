package id.co.hanoman.codex;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.app.Velocity;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class CodexContext {
	final CodexContext parent;
	final ValueHandler valueHandler;
	Map<String, Object> params;
	ChannelBuffer buf;
	int base = 0;
	
	public CodexContext(CodexContext parent) {
		this.parent = parent;
		this.valueHandler = parent.valueHandler;
		buf = ChannelBuffers.buffer(parent != null ? parent.buf.capacity() : 8192);
	}

	public CodexContext(CodexContext parent, ChannelBuffer buf) {
		this.parent = parent;
		this.valueHandler = parent.valueHandler;
		this.buf = buf;
	}

	public CodexContext(ValueHandler valueHandler, ChannelBuffer buf) {
		this.parent = null;
		this.valueHandler = valueHandler;
		this.buf = buf;
	}

	public CodexContext(ValueHandler valueHandler, byte bb[]) {
		this.parent = null;
		this.valueHandler = valueHandler;
		this.buf = ChannelBuffers.wrappedBuffer(bb);
	}

	public CodexContext(CodexContext parent, ValueHandler valueHandler) {
		this.parent = parent;
		this.valueHandler = valueHandler;
		buf = ChannelBuffers.buffer(parent != null ? parent.buf.capacity() : 8192);
	}

	public CodexContext(ValueHandler valueHandler) {
		this(null, valueHandler);
	}
	
	public ValueHandler getValueHandler() {
		return valueHandler;
	}

	public ChannelBuffer buffer() {
		return buf;
	}
	
	public String eval(Object msg, String script) {
		StringWriter resp = new StringWriter();
		Velocity.evaluate(new VelocityContextValueHandler(this, msg), resp, "eval", script);
		return resp.toString();
	}
	
	public void close(boolean encode) throws Exception {
	}
	
	public byte[] readBytes() {
		byte b[] = new byte[buf.readableBytes()];
		buf.readBytes(b);
		return b;
	}
	
	public Object getParam(String key) {
		if (params == null) return null;
		return params.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getParam(String key, Class<T> type) {
		if (params == null) return null;
		return (T) params.get(key);
	}
	
	public void setParam(String key, Object value) {
		if (params == null) params = new HashMap<String, Object>();
		params.put(key, value);
	}
	
	public byte[] getBytes() {
		byte b[] = new byte[buf.readableBytes()];
		int p = buf.readerIndex();
		buf.readBytes(b);
		buf.readerIndex(p);
		return b;
	}

}
