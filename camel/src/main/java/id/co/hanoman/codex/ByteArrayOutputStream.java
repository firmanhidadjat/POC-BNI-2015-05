package id.co.hanoman.codex;

import java.io.IOException;
import java.io.OutputStream;

public class ByteArrayOutputStream extends OutputStream {
	byte buf[];
	byte bx[] = null;
	int start=0, pos=0, size=0;
	ByteArrayOutputStream parent;
	
	public ByteArrayOutputStream() {
		buf = new byte[8192];
	}
	
	public ByteArrayOutputStream(ByteArrayOutputStream parent) {
		this.parent = parent;
		this.buf = parent.buf;
		this.start = parent.pos;
	}
	

	@Override
	public void write(int b) {
		if (parent == null) {
//			if (pos == 337) Logger.getLogger(getClass()).info("WRITE @"+pos+"   "+Util.dumpBean((char) b)+"   "+Util.getLocationTrace());
			buf[pos++] = (byte) b;
		} else {
			parent.write(b);
			pos ++;
		}
		bx = null;
		if (size < pos) size = pos;
	}
	
	public void position(int pos) {
		this.pos = pos;
		pos = 0;
		bx = null;
		if (parent != null) parent.position(start+pos);
	}
	
	public void reset() {
		position(0);
	}
	
	public void skip(int len) {
		pos += len;
		if (size < pos) size = pos;
		if (parent != null) parent.skip(len);
		bx = null;
	}

	public byte[] toByteArray() {
		if (bx != null) return bx;
		bx = new byte[size];
		System.arraycopy(buf, start, bx, 0, size);
		return bx;
	}
	
	public void writeTo(ByteArrayOutputStream out, int off) throws IOException {
		out.write(buf, off, size-off);
	}
	
	public int size() {
		return size;
	}
}
