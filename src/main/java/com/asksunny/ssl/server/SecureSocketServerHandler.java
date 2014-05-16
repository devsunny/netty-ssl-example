package com.asksunny.ssl.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;


public class SecureSocketServerHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = Logger.getLogger(
    		SecureSocketServerHandler.class.getName());

   

    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(
                Level.WARNING,
                "Unexpected exception from downstream.", cause);
        ctx.close();
    }




	@Override
	protected void channelRead0(ChannelHandlerContext arg0, String arg1)
			throws Exception {
		System.out.println("&&&&&&&&&&&&");
		System.out.println(arg1);
		arg0.writeAndFlush(arg1);
	}
}