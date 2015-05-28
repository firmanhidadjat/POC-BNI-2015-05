package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class NumericBinaryValidCodex extends BinaryCodex {
	private static final Logger LOG = LoggerFactory.getLogger(NumericBinaryValidCodex.class);
	protected int type;
	protected int packedPos;
	protected boolean halfed = false;
	protected boolean ignoreParseError;
	protected BigDecimal packedDiv;
	protected BigDecimal multiplicand;
	protected BigDecimal nullValue;

	@Override
	public void init(Codex parent, Config config) throws Exception {
		super.init(parent, config);
        String str = config.getStringValue("@decimal-format");
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
        multiplicand = config.getDecimalValue("@multiplicand", BigDecimal.ONE);
       	nullValue = config.getDecimalValue("@null-value", BigDecimal.ZERO);
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT ["+getFullName()+"] type = ["+type+"]");
			LOG.trace("INIT ["+getFullName()+"] packedPos = ["+packedPos+"]");
			LOG.trace("INIT ["+getFullName()+"] packedDiv = ["+packedDiv+"]");
			LOG.trace("INIT ["+getFullName()+"] ignoreParseError = ["+ignoreParseError+"]");
			LOG.trace("INIT ["+getFullName()+"] multiplicand = ["+multiplicand+"]");
			LOG.trace("INIT ["+getFullName()+"] nullValue = ["+nullValue+"]");
		}
	}

	public static String convert(String type, byte b[]) throws Exception {
		int itype;
		int packedPos;
		BigDecimal packedDiv;
        Pattern packedp = Pattern.compile("P\\((\\d+)\\)");
        Pattern zonedp = Pattern.compile("Z\\((\\d+)\\)");
        Matcher m;
        if ("binary".equals(type) || type == null) {
        	itype = 1;
        } else if ("packed".equals(type)) {
            itype = 2;
            packedPos = 0;
            packedDiv = BigDecimal.ONE;
        } else if ("zoned".equals(type)) {
            itype = 4;
            packedPos = 0;
            packedDiv = BigDecimal.ONE;
        } else if ("bcd".equals(type)) {
            itype = 3;
        } else if ((m = packedp.matcher(type)).matches()) {
        	itype = 2;
        	packedPos = Integer.parseInt(m.group(1));
        	StringBuffer sb = new StringBuffer();
        	sb.append('1');
        	for (int i=0; i<packedPos; i++) sb.append('0');
            packedDiv = new BigDecimal(sb.toString());
            if (packedDiv.compareTo(BigDecimal.ZERO) == 0) throw new Exception("Invalid parameter "+packedPos+"  ["+sb.toString()+"]");
        } else if ((m = zonedp.matcher(type)).matches()) {
        	itype = 4;
        	packedPos = Integer.parseInt(m.group(1));
        	StringBuffer sb = new StringBuffer();
        	sb.append('1');
        	for (int i=0; i<packedPos; i++) sb.append('0');
            packedDiv = new BigDecimal(sb.toString());
            if (packedDiv.compareTo(BigDecimal.ZERO) == 0) throw new Exception("Invalid parameter "+packedPos+"  ["+sb.toString()+"]");
        } else throw new RuntimeException("Invalid decimal-format ["+type+"]");
    	if (b.length == 0) {
    		return "0";
    	} else if (itype == 1) {
    	    long l = 0;
    	    for (int i=0, il=b.length; i<il; i++) {
    	        int d = b[i];
    	        if (d < 0) d += 256;
    	        l = (l * 256) + d;
    	    }
    	    return String.valueOf(l);
    	} else if (itype == 2) {
    		BigDecimal v = BigDecimal.ZERO;
    		int i=0;
    		for (int il=b.length-1; i<il; i++) {
    	        int d = b[i];
    			if (d < 0) d += 256;
    			int di = d / 16;
    			v = v.multiply(BigDecimal.TEN).add(new BigDecimal(di));
    			di = d % 16;
    			v = v.multiply(BigDecimal.TEN).add(new BigDecimal(di));
    		}
    		int d = b[i];
    		if (d < 0) d += 256;
    		int di = d / 16;
    		v = v.multiply(BigDecimal.TEN).add(new BigDecimal(di));
    		if ((d & 0x0F) != 0x0F) v = v.negate();
    		return v.toString();
    	} else if (itype == 3) {
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
    		return v.toString();
    	} else if (itype == 4) {
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
    		return v.toString();
    	} else throw new RuntimeException("Type '"+type+"' not suported.");
	}
	
	public static String convert(String type, Object value, int length) throws Exception {
		int itype;
		int packedPos;
		BigDecimal packedDiv = BigDecimal.ONE;
        Pattern packedp = Pattern.compile("P\\((\\d+)\\)");
        Pattern zonedp = Pattern.compile("Z\\((\\d+)\\)");
        Matcher m;
        if ("binary".equals(type) || type == null) {
        	itype = 1;
        } else if ("packed".equals(type)) {
            itype = 2;
            packedPos = 0;
            packedDiv = BigDecimal.ONE;
        } else if ("zoned".equals(type)) {
            itype = 4;
            packedPos = 0;
            packedDiv = BigDecimal.ONE;
        } else if ("bcd".equals(type)) {
            itype = 3;
        } else if ((m = packedp.matcher(type)).matches()) {
        	itype = 2;
        	packedPos = Integer.parseInt(m.group(1));
        	StringBuffer sb = new StringBuffer();
        	sb.append('1');
        	for (int i=0; i<packedPos; i++) sb.append('0');
            packedDiv = new BigDecimal(sb.toString());
            if (packedDiv.compareTo(BigDecimal.ZERO) == 0) throw new Exception("Invalid parameter "+packedPos+"  ["+sb.toString()+"]");
        } else if ((m = zonedp.matcher(type)).matches()) {
        	itype = 4;
        	packedPos = Integer.parseInt(m.group(1));
        	StringBuffer sb = new StringBuffer();
        	sb.append('1');
        	for (int i=0; i<packedPos; i++) sb.append('0');
            packedDiv = new BigDecimal(sb.toString());
            if (packedDiv.compareTo(BigDecimal.ZERO) == 0) throw new Exception("Invalid parameter "+packedPos+"  ["+sb.toString()+"]");
        } else throw new RuntimeException("Invalid decimal-format ["+type+"]");
        BigDecimal bd;
        if (value instanceof BigDecimal) {
        	bd = (BigDecimal) value;
        } else if (value instanceof String) {
        	bd = new BigDecimal((String) value);
        } else {
        	bd = new BigDecimal(value.toString());
        }
		byte b[] = new byte[length];
        if (itype == 1) {
			BigDecimal bd256 = new BigDecimal(256);
    		for (int i=length-1; i>=0; i--) {
				BigDecimal bx[] = bd.divideAndRemainder(bd256);
    		    b[i] = bd.byteValue();
				bd = bx[0];
			}
        } else if (itype == 2) {
    		boolean negative = bd.signum() == -1;
    		if (negative) bd = bd.negate();
    		bd = bd.multiply(packedDiv).setScale(0, BigDecimal.ROUND_HALF_DOWN);
    		String str = bd.toString();
    		b[length-1] = (byte) (negative ? 0x0D : 0x0F);
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
        } else if (itype == 3) {
    		boolean negative = bd.signum() == -1;
    		if (negative) bd = bd.negate();
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
        } else if (itype == 4) {
    		boolean negative = bd.signum() == -1;
    		if (negative) bd = bd.negate();
    		bd = bd.multiply(packedDiv).setScale(0, BigDecimal.ROUND_HALF_DOWN);
			BigDecimal dx[] = bd.divideAndRemainder(BigDecimal.TEN);
			b[length-1] = (byte) ((negative ? 0xD0 : 0xC0) + dx[1].intValue());
			bd = dx[0];
    		for (int i=length-2; i>=0; i--) {
    			dx = bd.divideAndRemainder(BigDecimal.TEN);
    			b[i] = (byte) (0xF0 + dx[1].intValue());
				bd = dx[0];
    		}
    	} else throw new RuntimeException("Type '"+type+"' not suported.");
		return U.toHex(b);
	}
	
	@Override
    public boolean decode(CodexContext ctx, JsonObject msg, ByteBuffer buffer) {
    	int pos = buffer.position();
        byte b[] = new byte[length];
    	try {
            if (buffer.remaining() < length) return false;
            buffer.get(b);
    	} catch (Exception ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
    	try {
        	if (b.length == 0) {
        		setCodexValue(ctx, msg, BigDecimal.ZERO);
        	} else if (type == 1) {
        	    long l = 0;
        	    for (int i=0, il=b.length; i<il; i++) {
        	        int d = b[i];
        	        if (d < 0) d += 256;
        	        l = (l * 256) + d;
        	    }
                BigDecimal bd = new BigDecimal(l).multiply(multiplicand);
        		setCodexValue(ctx, msg, bd);
        	} else if (type == 2) {
        		BigDecimal v = BigDecimal.ZERO;
        		int i=0;
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
        		v = v.multiply(BigDecimal.TEN).add(new BigDecimal(di));
        		if ((d & 0x0F) != 0x0F) v = v.negate();
                v = v.multiply(multiplicand);
                v = v.divide(packedDiv);
        		setCodexValue(ctx, msg, v);
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
                v = v.multiply(multiplicand);
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
                v = v.multiply(multiplicand);
                v = v.divide(packedDiv);
        		setCodexValue(ctx, msg, v);
        	}
    		return true;
    	} catch (Exception ex) {
    		setCodexError(ctx, msg, "Error "+getFullName()+" "+U.dump(b)+" "+ex.getMessage());
    		if (ignoreParseError) {
    			return true;
    		} else {
    			throw new RuntimeException(ex.getMessage(), ex);
    		}
    	} finally {
    		if (LOG.isTraceEnabled()) {
    			LOG.trace("DECODE ["+getFullName()+"] -- "+pos+"  "+buffer.position()+"  ["+U.dump(buffer.array(), pos, buffer.position()-pos)+"]");
    		}
    	}
    }

	public void encode(CodexContext ctx, JsonObject msg) throws Exception {
		String data = getCodexValue(ctx, msg).getAsString();
		encode(ctx, data);
	}
	
	public void encode(CodexContext ctx, String data) throws Exception {
		try {
	        BigDecimal bd = data != null ? new BigDecimal(data) : nullValue;
	        bd = bd.divide(multiplicand);
	        byte b[] = new byte[length];
	        if (type == 1) {
				BigDecimal bd256 = new BigDecimal(256);
	    		for (int i=length-1; i>=0; i--) {
					BigDecimal bx[] = bd.divideAndRemainder(bd256);
	    		    b[i] = bd.byteValue();
					bd = bx[0];
				}
	        } else if (type == 2) {
	    		boolean negative = bd.signum() == -1;
	    		if (negative) bd = bd.negate();
	    		bd = bd.divide(multiplicand);
	    		bd = bd.multiply(packedDiv).setScale(0, BigDecimal.ROUND_HALF_DOWN);
	    		String str = bd.toString();
	    		b[length-1] = (byte) (negative ? 0x0D : 0x0F);
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
	    		bd = bd.divide(multiplicand);
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
	    		bd = bd.divide(multiplicand);
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
	        ByteBuffer buf = ctx.buf;
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
			throw new Exception("Codex error '"+getFullName()+"' "+ex.getMessage(), ex);
		}
    }
}
