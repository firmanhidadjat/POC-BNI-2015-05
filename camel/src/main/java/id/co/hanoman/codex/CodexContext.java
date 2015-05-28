package id.co.hanoman.codex;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

public class CodexContext {
	protected final CodexContext parent;
	protected Logger log = Logger.getLogger(getClass());
	protected ByteBuffer buf;
	protected int base = 0;
	
	public CodexContext(CodexContext parent) {
		this.parent = parent;
		buf = ByteBuffer.allocate(8192);
	}

	public CodexContext() {
		this(null);
	}

	public ByteBuffer buffer() {
		return buf;
	}
	
	public void close() throws Exception {
	}
	
	public byte[] toByteArray() {
		byte b[] = new byte[buf.remaining()];
		buf.get(b);
		return b;
	}

}
