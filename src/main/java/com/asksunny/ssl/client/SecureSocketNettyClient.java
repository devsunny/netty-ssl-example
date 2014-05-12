package com.asksunny.ssl.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SecureSocketNettyClient {

	private final String host;
	private final int port;

	public SecureSocketNettyClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.handler(new SecureSocketClientInitializer());

			// Start the connection attempt.
			Channel ch = b.connect(host, port).sync().channel();

			// Read commands from the stdin.
			ChannelFuture lastWriteFuture = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));
			for (;;) {
				String line = in.readLine();
				if (line == null) {
					break;
				}

				// Sends the received line to the server.
				lastWriteFuture = ch.writeAndFlush(line + "\r\n");

				// If user typed the 'bye' command, wait until the server closes
				// the connection.
				if ("bye".equals(line.toLowerCase())) {
					ch.closeFuture().sync();
					break;
				}
			}

			// Wait until all messages are flushed before closing the channel.
			if (lastWriteFuture != null) {
				lastWriteFuture.sync();
			}
		} finally {
			// The connection is closed automatically on shutdown.
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception 
	{
		
		String host = args.length>0?args[0]:"localhost";
		int port = args.length>1?Integer.parseInt(args[1]):8443;
		new SecureSocketNettyClient(host, port).run();
	}
}