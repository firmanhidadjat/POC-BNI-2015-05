package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinaryCodex extends BaseCodex {
	private static final Logger LOG = LoggerFactory.getLogger(BinaryCodex.class);
	protected int length;
	protected final static byte zbyte[] = new byte[256];

	public BinaryCodex(CodexFactory factory) {
		super(factory);
	}

	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		super.init(parent, id, config);
		length = config.getIntegerValue("@length", 0);
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT ["+getFullName()+"] length = ["+length+"]");
		}
	}

	@Override
    public Object decode(CodexContext ctx, Object msg) {
		ChannelBuffer buf = ctx.buffer();
    	int pos = buf.readerIndex();
    	try {
            if (buf.readableBytes() < length) return false;
            byte bx[] = new byte[length];
            buf.readBytes(bx);
            setCodexValue(ctx, msg, bx);
    		return msg;
    	} catch (Exception ex) {
    		throw new RuntimeException("["+getFullName()+"] "+ex.getMessage(), ex);
    	} finally {
    		if (LOG.isTraceEnabled()) {
    			LOG.trace("DECODE ["+getFullName()+"] -- "+pos+"  "+buf.readerIndex()+"  ["+U.dump(buf.array(), pos, buf.readableBytes())+"]");
    		}
    	}
    }
    
	@Override
	public void encode(CodexContext ctx, Object msg) throws Exception {
		encode(ctx, ctx.getValueHandler().getCodexValue(ctx, msg, getName(), String.class));
	}
	
	public void encode(CodexContext ctx, String data) throws Exception {
    	try {
    		ChannelBuffer buf = ctx.buffer();
    		if (data == null) data = "";
    		byte b[] = U.fromHex(data);
	        int blen = Math.min(b.length, length);
        	if (LOG.isTraceEnabled()) {
        		LOG.trace("ENCODE ["+getFullName()+"] @"+buf.writerIndex()+" WRITE ["+U.dump(b, 0, blen)+"]");
        	}
        	buf.writeBytes(b, 0, blen);
        	int clen = length - blen;
        	while (clen > 0) {
        		int clx = Math.min(clen, zbyte.length);
            	if (LOG.isTraceEnabled()) {
            		LOG.trace("ENCODE ["+getFullName()+"] @"+buf.writerIndex()+" WRITE ["+U.dump(zbyte, 0, clx)+"]");
            	}
            	buf.writeBytes(zbyte, 0, clx);
            	clen -= clx;
        	}
    	} catch (Exception ex) {
    		throw new RuntimeException("["+getFullName()+"] "+ex.getMessage(), ex);
    	}
	}
	
	protected void setCodexValue(CodexContext ctx, Object msg, Object value) {
		if (value instanceof byte[]) {
			ctx.getValueHandler().setCodexValue(ctx, msg, getName(), U.toHex((byte[]) value));
		} else {
			ctx.getValueHandler().setCodexValue(ctx, msg, getName(), value);
		}
	}
}
