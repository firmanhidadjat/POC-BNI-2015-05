package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NumericBinaryCodex extends BinaryCodex {
	private static final Logger LOG = LoggerFactory.getLogger(NumericBinaryCodex.class);
	
	protected int DEFAULT_DECIMAL = 0;
	
	protected int type;
	protected int packedPos;
	boolean bigEndian;
	protected boolean halfed = false;
	protected boolean ignoreParseError;
	protected BigDecimal packedDiv;
	protected int decimal;
	protected BigDecimal multiplier;
	protected BigDecimal nullValue;

	public NumericBinaryCodex(CodexFactory factory) {
		super(factory);
	}

	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		super.init(parent, id, config);
        String str = config.getStringValue("@decimal-format");
        bigEndian = config.getBooleanValue("@big-endian", true);
        Pattern packedp = Pattern.compile("P\\((\\d+)\\)");
        Pattern zonedp = Pattern.compile("Z\\((\\d+)\\)");
        Matcher m;
        if ("binary".equals(str) || str == null) {
        	type = 1;
        } else if ("packed".equals(str)) {
            type = 2;
            packedPos = 0;
            packedDiv = BigDecimal.ONE;
        } else if ("zoned".equals(str)) {
            type = 4;
            packedPos = 0;
            packedDiv = BigDecimal.ONE;
        } else if ("bcd".equals(str)) {
            type = 3;
        } else if ((m = packedp.matcher(str)).matches()) {
        	type = 2;
        	packedPos = Integer.parseInt(m.group(1));
        	StringBuffer sb = new StringBuffer();
        	sb.append('1');
        	for (int i=0; i<packedPos; i++) sb.append('0');
            packedDiv = new BigDecimal(sb.toString());
            if (packedDiv.compareTo(BigDecimal.ZERO) == 0) throw new Exception("Invalid parameter "+packedPos+"  ["+sb.toString()+"]");
        } else if ((m = zonedp.matcher(str)).matches()) {
        	type = 4;
        	packedPos = Integer.parseInt(m.group(1));
        	StringBuffer sb = new StringBuffer();
        	sb.append('1');
        	for (int i=0; i<packedPos; i++) sb.append('0');
            packedDiv = new BigDecimal(sb.toString());
            if (packedDiv.compareTo(BigDecimal.ZERO) == 0) throw new Exception("Invalid parameter "+packedPos+"  ["+sb.toString()+"]");
        } else throw new RuntimeException("Invalid decimal-format ["+str+"]");
        ignoreParseError = config.getBooleanValue("@ignore-parse-error", false);
		decimal = config.getIntegerValue("@decimal", DEFAULT_DECIMAL);
		multiplier = BigDecimal.ONE.movePointRight(decimal);
       	nullValue = config.getDecimalValue("@null-value", BigDecimal.ZERO);
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT ["+getFullName()+"] type = ["+type+"]");
			LOG.trace("INIT ["+getFullName()+"] packedPos = ["+packedPos+"]");
			LOG.trace("INIT ["+getFullName()+"] packedDiv = ["+packedDiv+"]");
			LOG.trace("INIT ["+getFullName()+"] ignoreParseError = ["+ignoreParseError+"]");
			LOG.trace("INIT ["+getFullName()+"] multiplier = ["+multiplier+"]");
			LOG.trace("INIT ["+getFullName()+"] nullValue = ["+nullValue+"]");
		}
	}

	@Override
    public Object decode(CodexContext ctx, Object msg) {
		ChannelBuffer buf = ctx.buffer();
    	int pos = buf.readerIndex();
        byte b[] = new byte[length];
    	try {
            if (buf.readableBytes() < length) return null;
            buf.readBytes(b);
    	} catch (Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
    	try {
        	if (b.length == 0) {
        		setCodexValue(ctx, msg, BigDecimal.ZERO);
        	} else if (type == 1) {
        	    long l = 0;
        	    if (bigEndian) {
	        	    for (int i=0, il=b.length; i<il; i++) {
	        	        int d = b[i];
	        	        if (d < 0) d += 256;
	        	        l = (l * 256) + d;
	        	    }
        	    } else {
	        	    for (int i=0, il=b.length; i<il; i+=2) {
	        	        int d = b[i+1];
	        	        if (d < 0) d += 256;
	        	        l = (l * 256) + d;
	        	        d = b[i];
	        	        if (d < 0) d += 256;
	        	        l = (l * 256) + d;
	        	    }
        	    }
                BigDecimal bd = new BigDecimal(l).multiply(multiplier);
        		setCodexValue(ctx, msg, bd);
        	} else if (type == 2) {
        		BigDecimal v = BigDecimal.ZERO;
        		Object ov;
        		int i=0;
        		try {
	        		for (int il=b.length-1; i<il; i++) {
	        	        int d = b[i];
	        			if (d < 0) d += 256;
	        			int di = d / 16;
	        			if (di > 9) throw new RuntimeException("Invalid packed "+i+"   "+U.toHex(b));
	        			v = v.multiply(BigDecimal.TEN).add(new BigDecimal(di));
	        			di = d % 16;
	        			if (di > 9) throw new RuntimeException("Invalid packed "+i+"   "+U.toHex(b));
	        			v = v.multiply(BigDecimal.TEN).add(new BigDecimal(di));
	        		}
	        		int d = b[i];
	        		if (d < 0) d += 256;
	        		int di = d / 16;
        			if (di > 9) throw new RuntimeException("Invalid packed "+i+"   "+U.toHex(b));
	        		v = v.multiply(BigDecimal.TEN).add(new BigDecimal(di));
	                v = v.multiply(multiplier);
	                v = v.divide(packedDiv);
	        		if ((d & 0x0F) == 0x0C) {
	        			ov = "+"+v.toString();
	        		} else if ((d & 0x0F) == 0x0F) {
	        			ov = v.toString();
	        		} else if ((d & 0x0F) == 0x0D) {
	        			ov = "-"+v.toString();
	        		} else {
	        			for (int j=0, jl=b.length; j<jl; j++) {
	        				if (b[j] != 0) throw new RuntimeException("Invalid packed "+i+"   "+U.toHex(b));
	        			}
	        			ov = nullValue;
	        		}
	        		setCodexValue(ctx, msg, ov);
        		} catch (Exception ex) {
        			if (LOG.isDebugEnabled()) {
        				LOG.debug("ERROR ["+getFullName()+"] "+ex.getMessage(), ex);
        			}
        			ctx.getValueHandler().setCodexError(ctx, msg, getName(), "Invalid data "+U.dump(b)+" "+ex.getMessage());
        		}
        	} else if (type == 3) {
        		BigDecimal v = new BigDecimal(0);
        		BigDecimal v10 = new BigDecimal(10);
        		int i=0;
        		for (int il=b.length; i<il; i++) {
        	        int d = b[i];
        			if (d < 0) d += 256;
        			int di = d / 16;
        			v = v.multiply(v10).add(new BigDecimal(di));
        			di = d % 16;
        			v = v.multiply(v10).add(new BigDecimal(di));
        		}
                v = v.multiply(multiplier);
        		setCodexValue(ctx, msg, v);
        	} else if (type == 4) {
        		BigDecimal v = BigDecimal.ZERO;
        		int i=0;
        		for (int il=b.length-1; i<il; i++) {
        	        int d = b[i];
        			if (d < 0) d += 256;
        			d = d & 0xF;
        			v = v.multiply(BigDecimal.TEN).add(new BigDecimal(d));
        		}
        		int d = b[i];
        		if (d < 0) d += 256;
        		boolean neg = (d & 0xB0) != 0 || (d & 0xD0) != 0;
        		d = d & 0x0F;
        		v = v.multiply(BigDecimal.TEN).add(new BigDecimal(d));
        		if (neg) v.negate();
                v = v.multiply(multiplier);
                v = v.divide(packedDiv);
        		setCodexValue(ctx, msg, v);
        	}
    		return msg;
    	} catch (Exception ex) {
    		ctx.getValueHandler().setCodexError(ctx, msg, getName(), "Invalid data "+U.dump(b)+" "+ex.getMessage());
    		if (ignoreParseError) {
    			return msg;
    		} else {
    			throw new RuntimeException(ex.getMessage(), ex);
    		}
    	} finally {
    		if (LOG.isTraceEnabled()) {
    			LOG.trace("DECODE ["+getFullName()+"] -- "+pos+"  "+buf.readerIndex()+"  ["+U.dump(buf.array(), pos, buf.readerIndex()-pos)+"]");
    		}
    	}
    }

	@Override
	public void encode(CodexContext ctx, Object msg) throws Exception {
		encode(ctx, ctx.getValueHandler().getCodexValue(ctx, msg, getName(), String.class));
	}
	
	public void encode(CodexContext ctx, String data) throws Exception {
		try {
	        BigDecimal bd = data != null ? new BigDecimal(data) : nullValue;
	        bd = bd.divide(multiplier);
	        byte b[] = new byte[length];
	        if (type == 1) {
				BigDecimal bd256 = new BigDecimal(256);
				if (bigEndian) {
		    		for (int i=length-1; i>=0; i--) {
						BigDecimal bx[] = bd.divideAndRemainder(bd256);
		    		    b[i] = bd.byteValue();
						bd = bx[0];
					}
				} else {
					byte b1[] = new byte[length];
		    		for (int i=length-1; i>=0; i--) {
						BigDecimal bx[] = bd.divideAndRemainder(bd256);
		    		    b1[i] = bd.byteValue();
						bd = bx[0];
					}
		    		for (int i=0, il=b.length; i<il; i+=2) {
		    			b[i+1] = b1[i];
		    			b[i] = b1[i+1];
		    		}
				}
	        } else if (type == 2) {
	    		boolean negative = bd.signum() == -1;
	    		if (negative) bd = bd.negate();
	    		bd = bd.divide(multiplier);
	    		bd = bd.multiply(packedDiv).setScale(0, BigDecimal.ROUND_HALF_DOWN);
	    		String str = bd.toString();
	    		if (data != null) {
	    			if (data.charAt(0) == '+') {
		    			b[length-1] = (byte) 0x0C;
	    			} else if (data.charAt(0) == '-') {
		    			b[length-1] = (byte) 0x0D;
	    			} else {
		    			b[length-1] = (byte) 0x0F;
	    			}
	    		} else if (negative) {
	    			b[length-1] = (byte) 0x0D;
	    		} else {
	    			b[length-1] = (byte) 0x0F;
	    		}
	    		for (int pi=1, p=b.length-1, cp=str.length()-1; p >= 0 && cp >= 0; cp--) {
	    			int c = str.charAt(cp) - '0';
	    			if (pi == 1) {
	    				b[p] |= (c << 4);
	    				pi = 0;
	    				p --;
	    			} else {
	    				b[p] = (byte) c;
	    				pi = 1;
	    			}
	    		}
	        } else if (type == 3) {
	    		boolean negative = bd.signum() == -1;
	    		if (negative) bd = bd.negate();
	    		bd = bd.divide(multiplier);
	    		String str = bd.setScale(0, BigDecimal.ROUND_HALF_DOWN).toString();
	    		for (int pi=0, p=b.length-1, cp=str.length()-1; p >= 0 && cp >= 0; cp--) {
	    			int c = str.charAt(cp) - '0';
	    			if (pi == 1) {
	    				b[p] |= (c << 4);
	    				pi = 0;
	    				p --;
	    			} else {
	    				b[p] = (byte) c;
	    				pi = 1;
	    			}
	    		}
	        } else if (type == 4) {
	    		boolean negative = bd.signum() == -1;
	    		if (negative) bd = bd.negate();
	    		bd = bd.divide(multiplier);
	    		bd = bd.multiply(packedDiv).setScale(0, BigDecimal.ROUND_HALF_DOWN);
				BigDecimal dx[] = bd.divideAndRemainder(BigDecimal.TEN);
				b[length-1] = (byte) ((negative ? 0xD0 : 0xC0) + dx[1].intValue());
				bd = dx[0];
	    		for (int i=length-2; i>=0; i--) {
	    			dx = bd.divideAndRemainder(BigDecimal.TEN);
	    			b[i] = (byte) (0xF0 + dx[1].intValue());
					bd = dx[0];
	    		}
	        } else {
	        	throw new RuntimeException("Not supported..");
	        }
	        ChannelBuffer buf = ctx.buffer();
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
			throw new Exception("Codex error '"+getFullName()+"' "+ex.getMessage(), ex);
		}
    }
}
