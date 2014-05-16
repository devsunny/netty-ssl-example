package com.asksunny.ssl.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SecureSocketClientHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = Logger.getLogger(
    		SecureSocketClientHandler.class.getName());

   
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(
                Level.WARNING,
                "Unexpected exception from downstream.", cause);
        ctx.close();
    }

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, String msg)
			throws Exception {
		 System.err.println(msg);
		
	}
}