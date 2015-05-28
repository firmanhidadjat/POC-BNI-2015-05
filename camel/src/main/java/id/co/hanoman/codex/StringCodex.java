package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class StringCodex extends BaseCodex {
	private static final Logger LOG = LoggerFactory.getLogger(StringCodex.class);
	
	protected int length;
	protected int varType;
	protected byte padChar;
	protected int padMode;
	protected String valueCheck;
	protected boolean allowPartial;
	protected boolean ignoreTruncatedString;
	
	protected String DEFAULT_PADDING_CHAR = " ";
	protected String DEFAULT_PADDING_MODE = null; 
	protected boolean DEFAULT_ignoreTruncatedString = true; 

	public String getName() {
		return id;
	}

	public void init(Codex parent, Config config) throws Exception {
		super.init(parent, config);
		length = config.getIntegerValue("@length", 0);
        String str = config.getStringValue("@padding-char", DEFAULT_PADDING_CHAR);
        try {
        	this.padChar = str != null && str.length() > 0 ? (charset != null ? str.getBytes(charset)[0] : str.getBytes()[0]) : charset != null ? " ".getBytes(charset)[0] : (byte) ' ';
        } catch (Exception ex) {
        	throw new RuntimeException(ex.getMessage(), ex);
        }
		String padModeStr = config.getStringValue("@padding-mode", DEFAULT_PADDING_MODE);
		if (padModeStr == null) {
			padMode = 0;
		} else if ("left".equalsIgnoreCase(padModeStr)) {
			padMode = 1;
		} else if ("right".equalsIgnoreCase(padModeStr)) {
			padMode = 2;
		} else throw new Exception("Padding ["+padModeStr+"] not supported for "+getFullName());
		valueCheck = config.getStringValue("@value", null);
		allowPartial = config.getBooleanValue("@allow-partial", false);
		this.ignoreTruncatedString = config.getBooleanValue("@ignore-truncated", DEFAULT_ignoreTruncatedString);
        String format = config.getStringValue("@format", null);
        if ("lvar".equals(format)) {
            varType = 1;
        } else if ("llvar".equals(format)) {
            varType = 2;
        } else if ("lllvar".equals(format)) {
            varType = 3;
        } else if ("bcd-lvar".equals(format)) {
            varType = 11;
        } else if ("bcd-llvar".equals(format)) {
            varType = 12;
        } else if ("bcd-lllvar".equals(format)) {
            varType = 13;
        } else varType = 0;
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT ["+getFullName()+"] length = ["+length+"]");
			LOG.trace("INIT ["+getFullName()+"] padChar = ["+padChar+"]");
			LOG.trace("INIT ["+getFullName()+"] padMode = ["+padMode+"]");
			LOG.trace("INIT ["+getFullName()+"] ignoreTruncatedString = ["+ignoreTruncatedString+"]");
			LOG.trace("INIT ["+getFullName()+"] format = ["+format+"]");
			LOG.trace("INIT ["+getFullName()+"] varType = ["+varType+"]");
			LOG.trace("INIT ["+getFullName()+"] valueCheck = ["+valueCheck+"]");
		}
	}

	@Override
    public boolean decode(CodexContext ctx, JsonObject msg, ByteBuffer buffer) {
        int pos = buffer.position();
    	try {
	        if (varType == 0) {
	            if (buffer.remaining() < length) {
	            	if (allowPartial) {
		            	if (buffer.remaining() > 0) {
		    	            byte bx[] = new byte[buffer.remaining()];
		    	            buffer.get(bx);
		    	            if (LOG.isTraceEnabled()) {
		    	            	LOG.trace("DECODE ["+getFullName()+"]  ["+U.dump(bx)+"]");
		    	            }
		    	            if (padMode != 0) {
		    	                if (padMode == 2) {
		    	                    int i = bx.length - 1;
		    	                    while (i > 0 && bx[i] == padChar)
		    	                        i--;
		    	            		setCodexValue(ctx, msg, new String(bx, 0, i + 1, charset));
		    	            		return true;
		    	                } else if (padMode == 1) {
		    	                    int i = 0, il = bx.length;
		    	                    while (i < il && bx[i] == padChar)
		    	                        i++;
		    	            		setCodexValue(ctx, msg, new String(bx, i, il-i, charset));
		    	            		return true;
		    	                } else throw new RuntimeException("Padding mode '"+padMode+"' is not supported.");
		    	            } else {
		                		setCodexValue(ctx, msg, new String(bx, charset));
		                		return true;
		    	            }
		            	} else {
		            		return true;
		            	}
	            	} else if (LOG.isDebugEnabled()) {
	            		LOG.warn("NO MORE DATA ("+getFullName()+") "+buffer.position()+"   REMAINING "+buffer.remaining()+" "+length);
	            	}
	            	return false;
	            }
	            byte bx[] = new byte[length];
	            buffer.get(bx);
	            if (LOG.isTraceEnabled()) {
	            	LOG.trace("DECODE ["+getFullName()+"]  ["+U.dump(bx)+"]");
	            }
	            if (padMode != 0) {
	                if (padMode == 2) {
	                    int i = bx.length - 1;
	                    while (i > 0 && bx[i] == padChar)
	                        i--;
	            		setCodexValue(ctx, msg, new String(bx, 0, i + 1, charset));
	            		return true;
	                } else if (padMode == 1) {
	                    int i = 0, il = bx.length;
	                    while (i < il && bx[i] == padChar)
	                        i++;
	            		setCodexValue(ctx, msg, new String(bx, i, il-i, charset));
	            		return true;
	                } else throw new RuntimeException("Padding mode '"+padMode+"' is not supported.");
	            } else {
            		setCodexValue(ctx, msg, new String(bx, charset));
            		return true;
	            }
	        } else if (varType < 10) {
	            int len = 0;
	            if (buffer.remaining() < varType) return false;
	            byte bx[] = new byte[varType];
	            buffer.get(bx);
	            if (LOG.isTraceEnabled()) {
	            	LOG.trace("DECODE ["+getFullName()+"]  LENGTH ["+U.dump(bx)+"]  ["+U.dump(new String(bx, charset))+"]");
	            }
	            bx = U.getBytes(new String(bx, charset));
	            for (int i=0; i<varType; i++) {
	            	if (bx[i] < '0' || bx[i] > '9') throw new RuntimeException("Invalid string length");
	                len = len * 10 + (bx[i] - '0');
	            }
	            if (buffer.remaining() < len) {
		            if (LOG.isTraceEnabled()) {
		            	LOG.trace("BUFFER REMAINING >> "+buffer.remaining()+"  "+len);
		            }
	                buffer.position(pos);
	                return false;
	            }
	            bx = new byte[len];
	            buffer.get(bx);
	            if (LOG.isTraceEnabled()) {
	            	LOG.trace("DECODE ["+getFullName()+"]  ["+U.dump(bx)+"]");
	            }
	            if (padMode != 0) {
	                if (padMode == 2) {
	                    int i = bx.length - 1;
	                    while (i > 0 && bx[i] == padChar)
	                        i--;
	            		setCodexValue(ctx, msg, new String(bx, 0, i + 1, charset));
	            		return true;
	                } else if (padMode == 1) {
	                    int i = 0, il = bx.length;
	                    while (i < il && bx[i] == padChar)
	                        i++;
	            		setCodexValue(ctx, msg, new String(bx, i, il-i, charset));
	            		return true;
	                } else throw new RuntimeException("Padding mode '"+padMode+"' is not supported.");
	            } else {
            		setCodexValue(ctx, msg, new String(bx, charset));
            		return true;
	            }
	        } else {
	            int len = 0;
	            int vtl = (varType - 9) / 2;
	            if (buffer.remaining() < vtl) return false;
	            byte bx[] = new byte[vtl];
	            buffer.get(bx);
	            for (int i=0; i<vtl; i++) {
	            	int bi = bx[i];
	            	if (bi < 0) bi += 256;
	            	len = len * 100 + (bi / 16) * 10 + (bi & 0x0F);
	            }
	            if (LOG.isTraceEnabled()) {
	            	LOG.trace("DECODE ["+getFullName()+"]  LENGTH ["+U.dump(bx)+"]  ["+len+"]");
	            }
	            if (buffer.remaining() < len) {
	                buffer.position(pos);
	                return false;
	            }
	            bx = new byte[len];
	            buffer.get(bx);
	            if (LOG.isTraceEnabled()) {
	            	LOG.trace("DECODE ["+getFullName()+"]  ["+U.dump(bx)+"]");
	            }
	            if (padMode != 0) {
	                if (padMode == 2) {
	                    int i = bx.length - 1;
	                    while (i > 0 && bx[i] == padChar)
	                        i--;
	            		setCodexValue(ctx, msg, new String(bx, 0, i + 1, charset));
	            		return true;
	                } else if (padMode == 1) {
	                    int i = 0, il = bx.length;
	                    while (i < il && bx[i] == padChar)
	                        i++;
	            		setCodexValue(ctx, msg, new String(bx, i, il-i, charset));
	            		return true;
	                } else throw new RuntimeException("Padding mode '"+padMode+"' is not supported.");
	            } else {
            		setCodexValue(ctx, msg, new String(bx, charset));
            		return true;
	            }
	        }
    	} catch (Throwable ex) {
    		throw new RuntimeException("["+getFullName()+"] Error parsing ["+U.dump(buffer.array(), pos, Math.min(20, buffer.limit()-pos))+ "] "+ ex.getMessage(), ex);
    	} finally {
    		if (LOG.isTraceEnabled()) {
    			LOG.trace("DECODE ["+getFullName()+"] -- "+pos+"  "+buffer.position()+"  ["+U.dump(buffer.array(), pos, buffer.position()-pos)+"]");
    		}
    	}
    }
    
    @Override
	public void encode(CodexContext ctx, JsonObject msg) throws Exception {
		JsonElement obj = getCodexValue(ctx, msg);
		encode(ctx, obj != null ? obj.getAsString() : null);
	}
	
	public void encode(CodexContext ctx, String data) throws Exception {
    	try {
    		ByteBuffer buf = ctx.buf;
        	int p = buf.position();
    		if (data == null) data = "";
    		byte b[] = data.getBytes(charset);
	        int blen = b.length;
	        if (varType == 0) {
	        	if (blen > length && !ignoreTruncatedString) throw new Exception("Data to large ("+getFullName()+") ["+data+"]");
	            if (padMode == 1) {
	            	for (int i=blen; i < length; i++) buf.put(padChar);
	            	buf.put(b, 0, Math.min(blen, length));
	            } else {
                	buf.put(b, 0, Math.min(blen, length));
	            	for (int i=blen; i < length; i++) buf.put(padChar);
	            }
	        } else if (varType < 10) {
	            int tlen = blen;
	            byte bx[] = new byte[varType];
	            for (int i=0, j=varType-1; i<varType; i++, j--) {
	                bx[j] = (byte) ('0' + (tlen % 10));
	                tlen /= 10;
	            }
	            bx = U.toString(bx).getBytes(charset);
            	buf.put(bx, 0, varType);
            	buf.put(b, 0, blen);
	        } else {
	            int tlen = blen;
	            int vtl = (varType - 9) / 2;
	            byte bx[] = new byte[vtl];
	            for (int i=0, j=vtl-1; i<vtl; i++, j--) {
	                bx[j] = (byte) ((tlen % 10) + ((tlen / 10) % 10)*16);
	                tlen /= 100;
	            }
            	buf.put(bx, 0, vtl);
            	buf.put(b, 0, blen);
	        }
        	if (LOG.isTraceEnabled()) {
        		LOG.trace("ENCODE ["+getFullName()+"] @"+p+" WRITE ["+U.dump(buf.array(), p, buf.position()-p)+"]");
        	}
    	} catch (UnsupportedEncodingException ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
	}

	@Override
	public void setCodexValue(CodexContext ctx, JsonObject msg, String value) {
		if (valueCheck != null && !valueCheck.equals(value)) throw new RuntimeException("Failed value check '"+value+"' with '"+valueCheck+"'");
		super.setCodexValue(ctx, msg, value);
	}

}
