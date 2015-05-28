package id.co.hanoman.bni.sms;

import id.co.hanoman.U;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class NullEncoder extends OneToOneEncoder {
	private static final Log log = LogFactory.getLog(NullEncoder.class);

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
		log.info("ENCODE "+U.dump(msg));
		byte bb[] = ((String) msg).getBytes();
		ChannelBuffer buf = ChannelBuffers.buffer(bb.length+4);
		String len = "0000"+bb.length;
		buf.writeBytes(len.substring(len.length()-4).getBytes());
		buf.writeBytes(bb);
		return buf;
	}

}
