package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public StringCodex(CodexFactory factory) {
		super(factory);
	}

	public String getName() {
		return id;
	}

	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		super.init(parent, id, config);
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
    public Object decode(CodexContext ctx, Object msg) {
		ChannelBuffer buffer = ctx.buffer();
        int pos = buffer.readerIndex();
        if (LOG.isTraceEnabled()) {
        	LOG.trace("DECODE [{}] @{} readable {}", getFullName(), pos, buffer.readableBytes());
        }
    	try {
	        if (varType == 0) {
	            if (buffer.readableBytes() < length) {
	            	if (allowPartial) {
		            	if (buffer.readableBytes() > 0) {
		    	            byte bx[] = new byte[buffer.readableBytes()];
		    	            buffer.readBytes(bx);
		    	            if (LOG.isTraceEnabled()) {
		    	            	LOG.trace("DECODE ["+getFullName()+"]  ["+U.dump(bx)+"]");
		    	            }
		    	            if (padMode != 0) {
		    	                if (padMode == 2) {
		    	                    int i = bx.length - 1;
		    	                    while (i >= 0 && bx[i] == padChar)
		    	                        i--;
		    	                    if (i >= 0) setCodexValue(ctx, msg, new String(bx, 0, i + 1, charset));
		    	            		return msg;
		    	                } else if (padMode == 1) {
		    	                    int i = 0, il = bx.length;
		    	                    while (i < il && bx[i] == padChar)
		    	                        i++;
		    	                    if (il > i) setCodexValue(ctx, msg, new String(bx, i, il-i, charset));
		    	            		return msg;
		    	                } else throw new RuntimeException("Padding mode '"+padMode+"' is not supported.");
		    	            } else {
		                		setCodexValue(ctx, msg, new String(bx, charset));
		                		return msg;
		    	            }
		            	} else {
		            		return msg;
		            	}
	            	} else if (LOG.isDebugEnabled()) {
	            		LOG.debug("NO MORE DATA ("+getFullName()+") "+buffer.readerIndex()+"   REMAINING "+buffer.readableBytes()+" "+length);
	            	}
	            	return null;
	            }
	            byte bx[] = new byte[length];
	            buffer.readBytes(bx);
	            if (LOG.isTraceEnabled()) {
	            	LOG.trace("DECODE ["+getFullName()+"]  ["+U.dump(bx)+"]");
	            }
	            if (padMode != 0) {
	                if (padMode == 2) {
	                    int i = bx.length - 1;
	                    while (i >= 0 && bx[i] == padChar)
	                        i--;
	                    if (i >= 0) setCodexValue(ctx, msg, new String(bx, 0, i + 1, charset));
	            		return msg;
	                } else if (padMode == 1) {
	                    int i = 0, il = bx.length;
	                    while (i < il && bx[i] == padChar)
	                        i++;
	                    if (il > i) setCodexValue(ctx, msg, new String(bx, i, il-i, charset));
	            		return msg;
	                } else throw new RuntimeException("Padding mode '"+padMode+"' is not supported.");
	            } else {
            		setCodexValue(ctx, msg, new String(bx, charset));
            		return msg;
	            }
	        } else if (varType < 10) {
	            int len = 0;
	            if (buffer.readableBytes() < varType) return null;
	            byte bx[] = new byte[varType];
	            buffer.readBytes(bx);
	            if (LOG.isTraceEnabled()) {
	            	LOG.trace("DECODE ["+getFullName()+"]  LENGTH ["+U.dump(bx)+"]  ["+U.dump(new String(bx, charset))+"]");
	            }
	            bx = U.getBytes(new String(bx, charset));
	            for (int i=0; i<varType; i++) {
	            	if (bx[i] < '0' || bx[i] > '9') throw new RuntimeException("Invalid string length");
	                len = len * 10 + (bx[i] - '0');
	            }
	            if (buffer.readableBytes() < len) {
		            if (LOG.isTraceEnabled()) {
		            	LOG.trace("BUFFER REMAINING >> "+buffer.readableBytes()+"  "+len);
		            }
	                buffer.readerIndex(pos);
	                return null;
	            }
	            bx = new byte[len];
	            buffer.readBytes(bx);
	            if (LOG.isTraceEnabled()) {
	            	LOG.trace("DECODE ["+getFullName()+"]  ["+U.dump(bx)+"]");
	            }
	            if (padMode != 0) {
	                if (padMode == 2) {
	                    int i = bx.length - 1;
	                    while (i >= 0 && bx[i] == padChar)
	                        i--;
	                    if (i >= 0) setCodexValue(ctx, msg, new String(bx, 0, i + 1, charset));
	            		return msg;
	                } else if (padMode == 1) {
	                    int i = 0, il = bx.length;
	                    while (i < il && bx[i] == padChar)
	                        i++;
	            		if (il > i) setCodexValue(ctx, msg, new String(bx, i, il-i, charset));
	            		return msg;
	                } else throw new RuntimeException("Padding mode '"+padMode+"' is not supported.");
	            } else {
            		setCodexValue(ctx, msg, new String(bx, charset));
            		return msg;
	            }
	        } else {
	            int len = 0;
	            int vtl = (varType - 9) / 2;
	            if (buffer.readableBytes() < vtl) return null;
	            byte bx[] = new byte[vtl];
	            buffer.readBytes(bx);
	            for (int i=0; i<vtl; i++) {
	            	int bi = bx[i];
	            	if (bi < 0) bi += 256;
	            	len = len * 100 + (bi / 16) * 10 + (bi & 0x0F);
	            }
	            if (LOG.isTraceEnabled()) {
	            	LOG.trace("DECODE ["+getFullName()+"]  LENGTH ["+U.dump(bx)+"]  ["+len+"]");
	            }
	            if (buffer.readableBytes() < len) {
	                buffer.readerIndex(pos);
	                return null;
	            }
	            bx = new byte[len];
	            buffer.readBytes(bx);
	            if (LOG.isTraceEnabled()) {
	            	LOG.trace("DECODE ["+getFullName()+"]  ["+U.dump(bx)+"]");
	            }
	            if (padMode != 0) {
	                if (padMode == 2) {
	                    int i = bx.length - 1;
	                    while (i >= 0 && bx[i] == padChar)
	                        i--;
	                    if (i >= 0) setCodexValue(ctx, msg, new String(bx, 0, i + 1, charset));
	            		return msg;
	                } else if (padMode == 1) {
	                    int i = 0, il = bx.length;
	                    while (i < il && bx[i] == padChar)
	                        i++;
	                    if (il > i) setCodexValue(ctx, msg, new String(bx, i, il-i, charset));
	            		return msg;
	                } else throw new RuntimeException("Padding mode '"+padMode+"' is not supported.");
	            } else {
            		setCodexValue(ctx, msg, new String(bx, charset));
            		return msg;
	            }
	        }
    	} catch (Throwable ex) {
    		throw new RuntimeException("["+getFullName()+"] Error parsing ["+U.dump(buffer.array(), pos, Math.min(20, buffer.writerIndex()-pos))+ "] "+ ex.getMessage(), ex);
    	} finally {
    		if (LOG.isTraceEnabled()) {
    			LOG.trace("DECODE ["+getFullName()+"] -- "+pos+"  "+buffer.readerIndex()+"  ["+U.dump(buffer.array(), pos, buffer.readerIndex()-pos)+"]");
    		}
    	}
    }
    
    @Override
	public void encode(CodexContext ctx, Object msg) throws Exception {
		String val = ctx.getValueHandler().getCodexValue(ctx, msg, getName(), String.class);
		if (val == null) val  = valueCheck;
		encodeValue(ctx, val);
	}
	
	public void encodeValue(CodexContext ctx, String data) throws Exception {
    	try {
    		ChannelBuffer buf = ctx.buffer();
        	int p = buf.writerIndex();
    		if (data == null) data = "";
    		byte b[] = data.getBytes(charset);
	        int blen = b.length;
	        if (varType == 0) {
	        	if (blen > length && !ignoreTruncatedString) throw new Exception("Data to large ("+getFullName()+") ["+data+"]");
	            if (padMode == 1) {
	            	for (int i=blen; i < length; i++) buf.writeByte(padChar);
	            	buf.writeBytes(b, 0, Math.min(blen, length));
	            } else if (padMode == 0 && allowPartial) {
                	buf.writeBytes(b, 0, Math.min(blen, length));
	            } else {
                	buf.writeBytes(b, 0, Math.min(blen, length));
	            	for (int i=blen; i < length; i++) buf.writeByte(padChar);
	            }
	        } else if (varType < 10) {
	            int tlen = blen;
	            byte bx[] = new byte[varType];
	            for (int i=0, j=varType-1; i<varType; i++, j--) {
	                bx[j] = (byte) ('0' + (tlen % 10));
	                tlen /= 10;
	            }
	            bx = U.toString(bx).getBytes(charset);
            	buf.writeBytes(bx, 0, varType);
            	buf.writeBytes(b, 0, blen);
	        } else {
	            int tlen = blen;
	            int vtl = (varType - 9) / 2;
	            byte bx[] = new byte[vtl];
	            for (int i=0, j=vtl-1; i<vtl; i++, j--) {
	                bx[j] = (byte) ((tlen % 10) + ((tlen / 10) % 10)*16);
	                tlen /= 100;
	            }
            	buf.writeBytes(bx, 0, vtl);
            	buf.writeBytes(b, 0, blen);
	        }
        	if (LOG.isTraceEnabled()) {
        		LOG.trace("ENCODE ["+getFullName()+"] @"+p+"-"+buf.writerIndex()+" WRITE ["+U.dump(buf.array(), p, buf.writerIndex()-p)+"]");
        	}
    	} catch (UnsupportedEncodingException ex) {
    		throw new RuntimeException(ex.getMessage(), ex);
    	}
	}

	protected void setCodexValue(CodexContext ctx, Object msg, Object value) {
		if (valueCheck != null) {
			if (!valueCheck.equals(value)) throw new RuntimeException("Failed value check '"+value+"' with '"+valueCheck+"'");
		} else {
			ctx.getValueHandler().setCodexValue(ctx, msg, getName(), value);
		}
	}

}
