package id.co.hanoman.codex.netty;

import java.util.Collection;

import id.co.hanoman.U;
import id.co.hanoman.codex.Codex;
import id.co.hanoman.codex.CodexContext;
import id.co.hanoman.codex.CodexFactory;
import id.co.hanoman.codex.ValueHandler;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.UpstreamMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class NettyCodex implements ChannelUpstreamHandler, ChannelDownstreamHandler {
	private final static Logger LOG = LoggerFactory.getLogger(NettyCodex.class);
	CodexFactory codexFactory;
	ValueHandler valueHandler;
	String codex;
	
	public CodexFactory getCodexFactory() {
		return codexFactory;
	}
	
	public void setCodexFactory(CodexFactory codexFactory) {
		this.codexFactory = codexFactory;
		if (codex != null) codexFactory.getCodex(codex);
	}
	
	public String getCodex() {
		return codex;
	}
	
	public void setCodex(String codex) {
		this.codex = codex;
		if (codexFactory != null) codexFactory.getCodex(codex);
	}
	
	public ValueHandler getValueHandler() {
		return valueHandler;
	}
	
	public void setValueHandler(ValueHandler valueHandler) {
		this.valueHandler = valueHandler;
	}
	

	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		if (e instanceof MessageEvent) {
			final MessageEvent me = (MessageEvent) e;
			if (me.getMessage() instanceof ChannelBuffer) {
				try {
					ChannelBuffer buf = (ChannelBuffer) me.getMessage();
					if (buf.readableBytes() == 0) return;
					if (LOG.isDebugEnabled()) LOG.debug("RECV ["+e.getChannel().getLocalAddress()+"--"+e.getChannel().getRemoteAddress()+"] "+U.dump(buf.array(), buf.readerIndex(), buf.readableBytes()));
					Codex c = codexFactory.getCodex(codex);
					int ri = buf.readerIndex();
					try {
						CodexContext cc = new CodexContext(valueHandler, buf);
						Object msg;
						do {
							msg = c.decode(cc, null);
							if (msg != null) {
								ctx.sendUpstream(new UpstreamMessageEvent(e.getChannel(), msg, me.getRemoteAddress()));
							}
						} while (msg != null && buf.readableBytes() > 0);
					} finally {
						buf.readerIndex(ri);
					}
				} catch (Exception ex) {
					LOG.warn("Error decoding with '"+codex+"'");
					throw ex;
				}
			} else {
				if (LOG.isDebugEnabled()) LOG.debug("RECV ["+e.getChannel().getLocalAddress()+"--"+e.getChannel().getRemoteAddress()+"] "+U.dump(((MessageEvent) e).getMessage()));
				ctx.sendUpstream(e);
			}
		} else {
			ctx.sendUpstream(e);
		}
	}

	@Override
	public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
		if (e instanceof MessageEvent) {
			MessageEvent msgEvent = (MessageEvent) e;
			try {
				Object msg = msgEvent.getMessage();
				if (msg instanceof ChannelBuffer) {
					ChannelBuffer buf = (ChannelBuffer) msg;
					if (LOG.isDebugEnabled()) LOG.debug("RPLY ["+e.getChannel().getLocalAddress()+"--"+e.getChannel().getRemoteAddress()+"] "+U.dump(buf.array(), buf.readerIndex(), buf.readableBytes()));
					ctx.sendDownstream(msgEvent);
				} else if (msg instanceof Collection<?>) {
					for (Object me : (Collection<?>) msg) {
						CodexContext cc = new CodexContext(valueHandler);
						Codex c = codexFactory.getCodex(codex);
						c.encode(cc, me);
						Channels.write(ctx, msgEvent.getFuture(), cc.buffer(), msgEvent.getRemoteAddress());
					}
				} else {
					if (LOG.isDebugEnabled()) LOG.debug("RPLY ["+e.getChannel().getLocalAddress()+"--"+e.getChannel().getRemoteAddress()+"] "+U.dump(msg));
					CodexContext cc = new CodexContext(valueHandler);
					Codex c = codexFactory.getCodex(codex);
					c.encode(cc, msg);
					Channels.write(ctx, msgEvent.getFuture(), cc.buffer(), msgEvent.getRemoteAddress());
				}
			} catch (Exception ex) {
				LOG.warn("Error encoding with '"+codex+"'");
				throw ex;
			}
		} else {
			ctx.sendDownstream(e);
		}
	}

}