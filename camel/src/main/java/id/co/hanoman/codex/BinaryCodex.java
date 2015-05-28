package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class BinaryCodex extends BaseCodex {
	private static final Logger LOG = LoggerFactory.getLogger(BinaryCodex.class);
	protected int length;
	protected final static byte zbyte[] = new byte[256];

	public String getName() {
		return id;
	}

	public void init(Codex parent, Config config) throws Exception {
		super.init(parent, config);
		length = config.getIntegerValue("@length", 0);
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT ["+getFullName()+"] length = ["+length+"]");
		}
	}

	@Override
    public boolean decode(CodexContext ctx, JsonObject msg, ByteBuffer buffer) {
    	int pos = buffer.position();
    	try {
            if (buffer.remaining() < length) return false;
            byte bx[] = new byte[length];
            buffer.get(bx);
            setCodexValue(ctx, msg, bx);
    		return true;
    	} catch (Exception ex) {
    		throw new RuntimeException("["+getFullName()+"] "+ex.getMessage(), ex);
    	} finally {
    		if (LOG.isTraceEnabled()) {
    			LOG.trace("DECODE ["+getFullName()+"] -- "+pos+"  "+buffer.position()+"  ["+U.dump(buffer.array(), pos, buffer.position()-pos)+"]");
    		}
    	}
    }
    
	@Override
	public void encode(CodexContext ctx, JsonObject msg) throws Exception {
		encode(ctx, getCodexValue(ctx, msg).getAsString());
	}
	
	public void encode(CodexContext ctx, String data) throws Exception {
    	try {
    		ByteBuffer buf = ctx.buf;
    		if (data == null) data = "";
    		byte b[] = U.fromHex(data);
	        int blen = Math.min(b.length, length);
        	if (LOG.isTraceEnabled()) {
        		LOG.trace("ENCODE ["+getFullName()+"] @"+buf.position()+" WRITE ["+U.dump(b, 0, blen)+"]");
        	}
        	buf.put(b, 0, blen);
        	int clen = length - blen;
        	while (clen > 0) {
        		int clx = Math.min(clen, zbyte.length);
            	if (LOG.isTraceEnabled()) {
            		LOG.trace("ENCODE ["+getFullName()+"] @"+buf.position()+" WRITE ["+U.dump(zbyte, 0, clx)+"]");
            	}
            	buf.put(zbyte, 0, clx);
            	clen -= clx;
        	}
    	} catch (Exception ex) {
    		throw new RuntimeException("["+getFullName()+"] "+ex.getMessage(), ex);
    	}
	}
	
	@Override
	public void setCodexValue(CodexContext ctx, JsonObject msg, Object value) {
		if (value instanceof byte[]) {
			setCodexValue(ctx, msg, U.toHex((byte[]) value));
		} else {
			super.setCodexValue(ctx, msg, value);
		}
	}
}
