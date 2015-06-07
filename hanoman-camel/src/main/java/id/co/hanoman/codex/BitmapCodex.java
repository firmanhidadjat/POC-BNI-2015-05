package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import java.util.BitSet;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitmapCodex extends BaseCodex {
	private static final Logger LOG = LoggerFactory.getLogger(BitmapCodex.class);
	protected int format;
	protected Codex codex[];
	
	public BitmapCodex(CodexFactory factory) {
		super(factory);
	}
	
	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		super.init(parent, id, config);
        String str = config.getStringValue("@format", "hex");
        if ("hex".equalsIgnoreCase(str)) {
        	format = 0;
        } else if ("binary".equalsIgnoreCase(str)) {
        	format = 1;
        } else if ("ascii".equalsIgnoreCase(str)) {
        	format = 2;
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
	}

	@Override
	public Object decode(CodexContext ctx, Object msg) throws Exception {
		ChannelBuffer buf = ctx.buffer();
        BitSet bitmap = new BitSet();
        if (format == 1) {
            if (buf.readableBytes() < 8) return null;
            int ch = buf.readByte();
            for (int i=0, si = 0, j = 128, sil=8; si < sil; i++) {
                bitmap.set(i, (ch & j) > 0);
                if (i == 0 && bitmap.get(i)) {
                	sil = 16;
                    if (buf.readableBytes() < 15) return null;
                }
                if (j == 1) {
                    j = 128;
                    si ++;
                    if (si < sil) {
                        ch = buf.readByte();
                    } else {
                        ch = 0;
                    }
                } else j >>= 1;
            }
        } else if (format == 2) {
        	if (buf.readableBytes() < 64) return null;
        	byte bb[] = new byte[64];
        	buf.readBytes(bb);
        	char ch[] = new String(bb, charset).toCharArray();
        	if (LOG.isTraceEnabled()) {
            	LOG.trace("DECODE ["+getFullName()+"] BITMAP STR ["+new String(ch)+"]");
        	}
        	for (int i=0, il=ch.length; i<il; i++) {
        		char c = ch[i];
        		if (c != '0' && c != '1') throw new Exception("Invalid code");
        		bitmap.set(i, c == '1');
        	}
            if (bitmap.get(0)) {
            	if (buf.readableBytes() < 64) return null;
            	buf.readBytes(bb);
            	ch = new String(bb, charset).toCharArray();
            	if (LOG.isTraceEnabled()) {
                	LOG.trace("DECODE ["+getFullName()+"] BITMAP2 STR ["+new String(ch)+"]");
            	}
            	for (int i=0, il=ch.length; i<il; i++) {
            		char c = ch[i];
            		if (c != '0' && c != '1') throw new Exception("Invalid code");
            		bitmap.set(i+64, c == '1');
            	}
            }
        } else {
            if (buf.readableBytes() < 16) return null;
            byte bb[] = new byte[16];
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
            int i = 0, j = 128;
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
            if (bitmap.get(0)) {
                if (buf.readableBytes() < 16) return null;
                buf.readBytes(bb);
            	str = new String(bb, charset);
            	if (LOG.isTraceEnabled()) {
                	LOG.trace("DECODE ["+getFullName()+"] BITMAP STR ["+str+"]");
            	}
            	bx = U.getBytes(str);
            	if (LOG.isTraceEnabled()) {
            		LOG.trace("DECODE ["+getFullName()+"] BITMAP BYTES ["+U.dump(bx)+"]");
            	}
                ch = (bx[0] >= 'A' ? bx[0] - 'A' + 10 : bx[0] - '0') * 16 + (bx[1] >= 'A' ? bx[1] - 'A' + 10 : bx[1] - '0');
                i = 0;
                j = 128;
                for (int si = 0, sil=bx.length; si < sil; i++) {
                    bitmap.set(i+64, (ch & j) > 0);
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
            }
        }
        if (LOG.isTraceEnabled()) {
        	LOG.trace("DECODE ["+getFullName()+"] BITMAP @"+buf.readerIndex()+" "+U.dump(bitmap));
        }
        if (msg == null) msg = ctx.getValueHandler().newObject(this);
        for (int i=0, il=codex.length; i<il; i++) {
            Codex c = codex[i];
            try {
	            if (bitmap.get(i+1)) {
	                if (LOG.isTraceEnabled()) {
	                	LOG.trace("DECODE ["+c.getFullName()+"] BITMAP-INDEX "+(i+1));
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
        if (format == 1) {
        	int len;
            if (bitmap.length() > 64) {
            	bitmap.set(0, true);
            	len = 16;
            } else {
            	len = 8;
            }
	        for (int i=0, bi=0, il=len; i<il; i++) {
	            int v = 0;
	            for (int bb=128; bb>0; bb >>= 1, bi++) {
	                v |= (bitmap.get(bi) ? bb : 0);
	            }
            	if (LOG.isTraceEnabled()) {
            		LOG.trace("ENCODE ["+getFullName()+"] WRITE-BITMAP ["+U.dump(v)+"]");
            	}
	            buf.writeByte(v);
	        }
        } else if (format == 2) {
        	byte b[];
            if (bitmap.length() > 64) {
            	bitmap.set(0, true);
            	b = new byte[128];
            	for (int i=0; i<128; i++) {
            		b[i] = (byte) (bitmap.get(i) ? '1' : '0');
            	}
            } else {
            	b = new byte[64];
            	for (int i=0; i<64; i++) {
            		b[i] = (byte) (bitmap.get(i) ? '1' : '0');
            	}
            }
	        String str = U.toString(b);
        	if (LOG.isTraceEnabled()) {
        		LOG.trace("ENCODE ["+getFullName()+"] WRITE-BITMAP ["+U.dump(str.getBytes(charset))+"]");
        	}
        	buf.writeBytes(str.getBytes(charset));
        } else {
        	byte bx[];
	        if (bitmap.length() > 64) {
	            bitmap.set(0, true);
            	bx = new byte[32];
	        } else {
            	bx = new byte[16];
	        }
	        for (int i=0, bi=0, il=bx.length; i<il; i++) {
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
        }
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
