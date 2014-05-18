package com.asksunny.ssl.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SecureSocketServerHandler extends SimpleChannelInboundHandler<String> {

	final static Logger LOG = LoggerFactory.getLogger(SecureSocketServerHandler.class);

   

	@Override
	public boolean acceptInboundMessage(Object msg) throws Exception {
		LOG.debug("acceptInboundMessage:" + msg.getClass().getName());
		return super.acceptInboundMessage(msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		LOG.debug("channelActive:");
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOG.debug("channelInactive:");
		super.channelInactive(ctx);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		LOG.debug("channelReadComplete:");
		super.channelReadComplete(ctx);
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		LOG.debug("channelRegistered:");
		super.channelRegistered(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		LOG.debug("channelUnregistered:");
		super.channelUnregistered(ctx);
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx)
			throws Exception {
		LOG.debug("channelWritabilityChanged:");
		super.channelWritabilityChanged(ctx);
	}

		@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		LOG.debug("userEventTriggered:");
		super.userEventTriggered(ctx, evt);
	}
	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	LOG.debug(
                "Unexpected exception from downstream.", cause);
        ctx.close();
    }




	@Override
	protected void channelRead0(ChannelHandlerContext arg0, String arg1)
			throws Exception {		
		arg0.writeAndFlush(arg1);
	}
	
	
}