package com.asksunny.ssl.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecureSocketServerhandler2 extends
		SimpleChannelInboundHandler<byte[]> {

	final static Logger LOG = LoggerFactory.getLogger(SecureSocketServerhandler2.class);
	
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, byte[] arg1)
			throws Exception 
	{	
		
		String request = new String(arg1);		
		System.out.println("" + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>> + "+ request);
		arg0.writeAndFlush(request.getBytes());
	}

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
		
		super.channelRegistered(ctx);
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
	
		super.channelUnregistered(ctx);
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx)
			throws Exception {
		// TODO Auto-generated method stub
		super.channelWritabilityChanged(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {		
	}

	
}
