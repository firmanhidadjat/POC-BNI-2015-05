package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import java.util.BitSet;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitmapGenericCodex extends BaseCodex {
	private static Logger LOG = LoggerFactory.getLogger(BitmapGenericCodex.class);
	protected String id;
	protected int bitmapLength;
	protected Codex codex[];
	
	public BitmapGenericCodex(CodexFactory factory) {
		super(factory);
	}

	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		super.init(parent, id, config);
        String str = config.getStringValue("@format", "hex");
        if ("hex".equalsIgnoreCase(str)) {
        } else {
        	throw new Exception("Format '"+str+"' not supported.");
        }
        List<Config> lc = config.getList("field");
		codex = new Codex[lc.size()];
		for (int i=0, il=codex.length; i<il; i++) {
			Config e = lc.get(i);
			String type = e.getStringValue("@type");
			codex[i] = factory.getCodexByType(this, type, e);
		}
		bitmapLength = config.getIntegerValue("@length", (codex.length + 3) / 4);
	}
	
	@Override
	public Object decode(CodexContext ctx, Object msg) throws Exception {
		ChannelBuffer buf = ctx.buffer();
        BitSet bitmap = new BitSet();
        if (buf.readableBytes() < bitmapLength) return null;
        byte bb[] = new byte[bitmapLength];
        buf.readBytes(bb);
    	String str = new String(bb, charset);
    	if (LOG.isTraceEnabled()) {
        	LOG.trace("DECODE ["+getFullName()+"] BITMAP STR ["+str+"]");
    	}
    	byte bx[] = U.getBytes(str);
    	if (LOG.isTraceEnabled()) {
    		LOG.trace("DECODE ["+getFullName()+"] BITMAP BYTES ["+U.dump(bx)+"]");
    	}
        int ch = (bx[0] >= 'A' ? bx[0] - 'A' + 10 : bx[0] - '0') * 16 + (bx[1] >= 'A' ? bx[1] - 'A' + 10 : bx[1] - '0');
        int i = 1, j = 128;
        for (int si = 0, sil=bx.length; si < sil; i++) {
            bitmap.set(i, (ch & j) > 0);
            if (j == 1) {
                j = 128;
                si += 2;
                if (si+1 < sil) {
                    ch = (bx[si] >= 'A' ? bx[si] - 'A' + 10 : bx[si] - '0') * 16 + (bx[si+1] >= 'A' ? bx[si+1] - 'A' + 10 : bx[si+1] - '0');
                } else {
                    ch = 0;
                }
            } else j >>= 1;
        }
        if (LOG.isTraceEnabled()) {
        	LOG.trace("DECODE ["+getFullName()+"] BITMAP "+U.dump(bitmap));
        }
        if (msg == null) msg = ctx.getValueHandler().newObject(this);
        for (int k=0, kl=codex.length; k<kl; k++) {
            Codex c = codex[k];
            try {
	            if (bitmap.get(k+1)) {
	                if (LOG.isTraceEnabled()) {
	                	LOG.trace("DECODE ["+c.getFullName()+"] BITMAP-INDEX "+(k+1));
	                }
	            	if (c.decode(ctx, msg) == null) {
	            		return null;
	            	}
	            }
            } catch (Exception ex) {
            	throw new Exception("["+c.getFullName()+"] "+ex.getMessage(), ex);
            }
        }
		return msg;
	}
	
	@Override
	public void encode(CodexContext ctx, Object msg) throws Exception {
		ChannelBuffer buf = ctx.buffer();
        BitSet bitmap = new BitSet();
        for (int i=0, il=codex.length; i<il; i++) {
            Codex c = codex[i];
            if (c != null && ctx.getValueHandler().hasCodexValue(ctx, msg, c.getName())) {
                bitmap.set(i+1, true);
            }
        }
    	byte bx[] = new byte[bitmapLength];
        for (int i=0, bi=1, il=bx.length; i<il; i++) {
            int v = 0;
            for (int bb=8; bb>0; bb >>= 1, bi++) {
                v |= (bitmap.get(bi) ? bb : 0);
            }
            if (v >= 10) {
            	bx[i] = (byte) ('A' + v - 10);
            } else {
            	bx[i] = (byte) ('0' + v);
            }
        }
        String str = U.toString(bx);
    	if (LOG.isTraceEnabled()) {
    		LOG.trace("ENCODE ["+getFullName()+"] WRITE-BITMAP ["+U.dump(str.getBytes(charset))+"]");
    	}
    	buf.writeBytes(str.getBytes(charset));
        if (LOG.isTraceEnabled()) {
        	LOG.trace("ENCODE ["+getFullName()+"] BITMAP "+U.dump(bitmap));
        }
		for (int i=0, il=codex.length; i<il; i++) {
			if (bitmap.get(i+1)) {
                if (LOG.isTraceEnabled()) {
                	LOG.trace("ENCODE ["+codex[i].getFullName()+"] BITMAP-INDEX "+(i+1));
                }
				codex[i].encode(ctx, msg);
			}
		}
	}

}
