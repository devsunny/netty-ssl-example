package com.asksunny.ssl.client;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class LengthFrameUtil 
{
	
	public static ByteBuf convertRequest(SecureMessage msg)
	{
		byte[] bs = msg.getPayload().getBytes(Charset.defaultCharset());
		int len = bs.length + 1;
		ByteBuf ret = Unpooled.buffer(len+4);
		ret.writeInt(len);
		ret.writeByte(msg.getCode());
		ret.writeBytes(bs);
		return ret;
	}
	
	private LengthFrameUtil(){}
}
