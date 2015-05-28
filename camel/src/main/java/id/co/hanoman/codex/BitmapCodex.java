package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class BitmapCodex extends BaseCodex {
	private static final Logger LOG = LoggerFactory.getLogger(BitmapCodex.class);
	protected int format;
	protected Codex codex[];
	
	public String getName() {
		return "bitmap";
	}
	
	public void init(Codex parent, Config config) throws Exception {
		super.init(parent, config);
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
			String cn = type.indexOf(".") == -1 ? "id.co.hanoman.codex." + type.substring(0, 1).toUpperCase() + type.substring(1) + "Codex" : type;
			if (LOG.isTraceEnabled()) LOG.trace("Load codex "+i+" - ["+cn+"]");
			codex[i] = (Codex) Class.forName(cn).newInstance();
			codex[i].init(this, e);
		}
	}

	@Override
	public boolean decode(CodexContext ctx, JsonObject msg, ByteBuffer buf) throws Exception {
        BitSet bitmap = new BitSet();
        if (format == 1) {
            if (buf.remaining() < 8) return false;
            int ch = buf.get();
            for (int i=0, si = 0, j = 128, sil=8; si < sil; i++) {
                bitmap.set(i, (ch & j) > 0);
                if (i == 0 && bitmap.get(i)) {
                	sil = 16;
                    if (buf.remaining() < 15) return false;
                }
                if (j == 1) {
                    j = 128;
                    si ++;
                    if (si < sil) {
                        ch = buf.get();
                    } else {
                        ch = 0;
                    }
                } else j >>= 1;
            }
        } else if (format == 2) {
        	if (buf.remaining() < 64) return false;
        	char ch[] = new String(buf.array(), buf.position(), 64, charset).toCharArray();
        	if (LOG.isTraceEnabled()) {
            	LOG.trace("DECODE ["+getFullName()+"] BITMAP STR ["+new String(ch)+"]");
        	}
        	for (int i=0, il=ch.length; i<il; i++) {
        		char c = ch[i];
        		if (c != '0' && c != '1') throw new Exception("Invalid code");
        		bitmap.set(i, c == '1');
        	}
        	buf.position(buf.position()+64);
            if (bitmap.get(0)) {
            	if (buf.remaining() < 64) return false;
            	ch = new String(buf.array(), buf.position(), 64, charset).toCharArray();
            	if (LOG.isTraceEnabled()) {
                	LOG.trace("DECODE ["+getFullName()+"] BITMAP2 STR ["+new String(ch)+"]");
            	}
            	for (int i=0, il=ch.length; i<il; i++) {
            		char c = ch[i];
            		if (c != '0' && c != '1') throw new Exception("Invalid code");
            		bitmap.set(i+64, c == '1');
            	}
            	buf.position(buf.position()+64);
            }
        } else {
            if (buf.remaining() < 16) return false;
        	String str = new String(buf.array(), buf.position(), 16, charset);
        	if (LOG.isTraceEnabled()) {
            	LOG.trace("DECODE ["+getFullName()+"] BITMAP STR ["+str+"]");
        	}
        	buf.position(buf.position()+16);
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
                if (buf.remaining() < 16) return false;
            	str = new String(buf.array(), buf.position(), 16, charset);
            	if (LOG.isTraceEnabled()) {
                	LOG.trace("DECODE ["+getFullName()+"] BITMAP STR ["+str+"]");
            	}
            	buf.position(buf.position()+16);
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
        	LOG.trace("DECODE ["+getFullName()+"] BITMAP "+U.dump(bitmap));
        }
        for (int i=0, il=codex.length; i<il; i++) {
            Codex c = codex[i];
            try {
	            if (bitmap.get(i+1)) {
	                if (LOG.isTraceEnabled()) {
	                	LOG.trace("DECODE ["+c.getFullName()+"] BITMAP-INDEX "+(i+1));
	                }
	            	if (!c.decode(ctx, msg, buf)) {
	            		return false;
	            	}
	            }
            } catch (Exception ex) {
            	throw new Exception("["+c.getFullName()+"] "+ex.getMessage(), ex);
            }
        }
		return true;
	}
	
	@Override
	public void encode(CodexContext ctx, JsonObject msg) throws Exception {
		ByteBuffer buf = ctx.buf;
        BitSet bitmap = new BitSet();
        for (int i=0, il=codex.length; i<il; i++) {
            Codex c = codex[i];
            if (c != null && msg.has(c.getName())) {
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
	            buf.put((byte) v);
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
        	buf.put(str.getBytes(charset));
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
        	buf.put(str.getBytes(charset));
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
