package id.co.hanoman.bni.sms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class ConnectionInfo extends SimpleChannelHandler {
	private static final Log log = LogFactory.getLog(ConnectionInfo.class);
	
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		log.info("ChannelOpen "+e.getClass().getName()+" "+e.toString());
		super.channelOpen(ctx, e);
	}
	
	@Override
	public void channelBound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		log.info("ChannelBound "+e.getClass().getName()+" "+e.toString());
		super.channelBound(ctx, e);
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		log.info("ChannelConnected "+e.getClass().getName()+" "+e.toString());
		super.channelConnected(ctx, e);
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		log.info("ChannelDisconnected "+e.getClass().getName()+" "+e.toString());
		super.channelDisconnected(ctx, e);
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		log.info("ChannelClosed "+e.getClass().getName()+" "+e.toString());
		super.channelClosed(ctx, e);
	}

}
