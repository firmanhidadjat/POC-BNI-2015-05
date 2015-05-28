package id.co.hanoman.bni.sms;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class NullDecoder extends FrameDecoder {

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buf) throws Exception {
		if (buf.readableBytes() < 4) return null;
		byte b[] = new byte[buf.readableBytes()];
		buf.readBytes(b);
		return new String(b);
	}

}
