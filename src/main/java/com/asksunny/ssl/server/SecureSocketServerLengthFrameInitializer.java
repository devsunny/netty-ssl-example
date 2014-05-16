/**
 * 
 */
package com.asksunny.ssl.server;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
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

        // Add SSL handler first to encrypt and decrypt everything.
        // In this example, we use a bogus certificate in the server side
        // and accept any invalid certificates in the client side.
        // You will need something more complicated to identify both
        // and server in the real world.
        //
        // Read SecureChatSslContextFactory
        // if you need client certificate authentication.

        SSLEngine engine =
            SecureSocketSslContextFactory.getServerContext().createSSLEngine();
        engine.setUseClientMode(false);

        pipeline.addLast("ssl", new SslHandler(engine));

        // On top of the SSL handler, add the text line codec.
//        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(
//                8192, Delimiters.lineDelimiter()));
//        pipeline.addLast("decoder", new StringDecoder());
//        pipeline.addLast("encoder", new StringEncoder());
        
        pipeline.addLast("length-decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        pipeline.addLast("String-decoder", new StringDecoder());        
        pipeline.addLast("String-encoder", new StringEncoder());
        pipeline.addLast("length-encoder", new LengthFieldPrepender(4));

        // and then business logic.
        pipeline.addLast("handler", new SecureSocketServerHandler());
    }
}
