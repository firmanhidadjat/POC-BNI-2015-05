package id.co.hanoman.netty;

import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class ConnectionInfo extends SimpleChannelHandler {
	private static final Logger log = LoggerFactory.getLogger(ConnectionInfo.class);
	
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		log.info("OPEN ["+e.getChannel().getLocalAddress()+"--"+e.getChannel().getRemoteAddress()+"]");
		super.channelOpen(ctx, e);
	}
	
	@Override
	public void channelBound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		log.info("BOUND ["+e.getChannel().getLocalAddress()+"--"+e.getChannel().getRemoteAddress()+"]");
		super.channelBound(ctx, e);
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		log.info("CONNECTED ["+e.getChannel().getLocalAddress()+"--"+e.getChannel().getRemoteAddress()+"]");
		super.channelConnected(ctx, e);
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		log.info("DISCONNECTED ["+e.getChannel().getLocalAddress()+"--"+e.getChannel().getRemoteAddress()+"]");
		super.channelDisconnected(ctx, e);
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		log.info("CLOSED ["+e.getChannel().getLocalAddress()+"--"+e.getChannel().getRemoteAddress()+"]");
		super.channelClosed(ctx, e);
	}

}
