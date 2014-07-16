/**
 * 
 */
package com.asksunny.ssl.server;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

import com.asksunny.ssl.SecureSocketSslContextFactory;

/**
 * @author sunny
 *
 */
public class SecureSocketServerLengthFrameInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        SSLEngine engine =
            SecureSocketSslContextFactory.getServerContext().createSSLEngine();
        engine.setUseClientMode(false);
        pipeline.addLast("ssl", new SslHandler(engine));  
        pipeline.addLast("length-decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4)); 
        pipeline.addLast("bytearray-decoder", new ByteArrayDecoder());
        pipeline.addLast("length-encoder", new LengthFieldPrepender(4));  
        pipeline.addLast("bytearray-encoder", new ByteArrayEncoder());
        pipeline.addLast("handler", new SecureSocketServerhandler2());
    }
}
