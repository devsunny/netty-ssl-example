package com.asksunny.ssl.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.ssl.SslHandler;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLEngine;

import com.asksunny.ssl.SecureSocketSslContextFactory;

public class NettySocketClient {

	private EventLoopGroup eventloopGroop = null;
	private String remoteHost;
	private int remotePort;
	private AtomicBoolean openned = new AtomicBoolean(false);
	private ChannelFuture channelFuture = null;
	private BlockingByteArrayClientHandler clientHandler = null;

	public NettySocketClient(String remotehost, int port) {
		this.remoteHost = remotehost;
		this.remotePort = port;
	}

	public void open(EventLoopGroup eventLoopGroup) throws Exception {
		if (openned.compareAndSet(false, true)) {
			eventloopGroop = eventLoopGroup == null ? new NioEventLoopGroup()
					: eventLoopGroup;
			Bootstrap bootstrap = new Bootstrap();
			final BlockingByteArrayClientHandler handler = new BlockingByteArrayClientHandler(
					this);
			this.clientHandler = handler;
			bootstrap.group(eventloopGroop).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch)
								throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							SSLEngine engine = SecureSocketSslContextFactory
									.getClientContext().createSSLEngine();
							engine.setUseClientMode(true);
							pipeline.addLast("ssl", new SslHandler(engine));
							pipeline.addLast("length-decoder",
									new LengthFieldBasedFrameDecoder(
											Integer.MAX_VALUE, 0, 4, 0, 4));
							pipeline.addLast("bytearray-decoder",
									new ByteArrayDecoder());
							pipeline.addLast("length-encoder",
									new LengthFieldPrepender(4));
							pipeline.addLast("bytearray-encoder",
									new ByteArrayEncoder());
							pipeline.addLast("handler", handler);
						}

					});
			channelFuture = bootstrap.connect(this.remoteHost, this.remotePort)
					.sync();
		}
	}

	public void open() throws Exception {
		open(null);
	}

	public void close() {
		if (eventloopGroop != null && openned.compareAndSet(true, false)) {
			eventloopGroop.shutdownGracefully();
		}
	}
	
	void exceptionCaught(Throwable cause)
			throws Exception 
	{		
		this.close();
		throw new IOException("Disconnected unpextectly.", cause);
	}

	public byte[] sendMessage(byte[] message) {
		CountDownLatch latch = new CountDownLatch(1);
		this.clientHandler.setLatch(latch);
		channelFuture.channel().writeAndFlush(message);
		try {
			latch.await();
		} catch (InterruptedException e) {
			;
		}
		return this.clientHandler.getResponse();
	}

	protected static class BlockingByteArrayClientHandler extends
			SimpleChannelInboundHandler<byte[]> {

		NettySocketClient nettySocketClient;
		CountDownLatch latch;
		byte[] response = null;

		public BlockingByteArrayClientHandler(
				NettySocketClient nettySocketClient) {
			this.nettySocketClient = nettySocketClient;
		}

		public CountDownLatch getLatch() {
			return latch;
		}

		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}

		public NettySocketClient getNettySocketClient() {
			return nettySocketClient;
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception 
		{
			
			ctx.close();
			this.nettySocketClient.exceptionCaught(cause);
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, byte[] msg)
				throws Exception {
			response = msg;
			if (latch != null)
				latch.countDown();
		}

		public byte[] getResponse() {
			return response;
		}

	}

	public static void main(String[] args) throws Exception {

		String host = args.length > 0 ? args[0] : "localhost";
		int port = args.length > 1 ? Integer.parseInt(args[1]) : 8443;
		NettySocketClient socketClient = new NettySocketClient(host, port);
		socketClient.open();
		byte[] response  = socketClient.sendMessage("Hellloooooooooooooooooo".getBytes());
		System.out.println(new String(response));
		socketClient.close();
	}

}
