package com.asksunny.ssl.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javax.net.ssl.SSLContext;

import com.asksunny.ssl.SecureSocketSslContextFactory;

public class SecureSocketPlainClient {
	private final String host;
	private final int port;
	Socket clientSocket;
	OutputStream clientOut;
	InputStream clientIn;
	
	public SecureSocketPlainClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void open() throws IOException
	{
		SSLContext clientSslContext = SecureSocketSslContextFactory.getClientContext();
		clientSocket = clientSslContext.getSocketFactory().createSocket(host, port);
		clientOut = clientSocket.getOutputStream();
		clientIn = clientSocket.getInputStream();
	}
	
	
	public SecureMessage makeRequest(SecureMessage request) throws IOException
	{
		SecureMessage response = null;
		int initSize =  request.getPayload()==null?12:request.getPayload().length();
		ByteBuf buf = Unpooled.directBuffer(initSize);
		buf.writeInt(0);
		buf.writeByte(request.getCode());
		if( request.getPayload()!=null){			
			buf.writeBytes(request.getPayload().getBytes(Charset.defaultCharset()));
		}
		System.out.println(buf.readableBytes());
		buf.setInt(0, buf.readableBytes()-4);
		buf.readBytes(this.clientOut, buf.readableBytes());
		this.clientOut.flush();
				
		readFull(buf, 4);
		int len = buf.readInt();
		buf.capacity(len+4);
		readFull(buf, len);
		response = new SecureMessage();
		response.setCode(buf.readByte());
		response.setPayload(buf.toString(Charset.defaultCharset()));		
		return response;
	}
	
	
	protected void readFull(ByteBuf buf, int length) throws IOException
	{
		int left = length;
		while(left>0){
			int r = buf.writeBytes(this.clientIn, left);
			left -= r;
		}
	}
	
	
	public void close() throws IOException
	{
		try{
			if(clientIn!=null) clientIn.close();
		}finally{
			try{
				if(clientOut!=null) clientOut.close();
			}finally{
				if(clientSocket!=null) clientSocket.close();
			}
		}
	}
	
	
	
	public static void main(String[] args) throws Exception 
	{
		
		String host = args.length>0?args[0]:"localhost";
		int port = args.length>1?Integer.parseInt(args[1]):8443;
		SecureSocketPlainClient client = new SecureSocketPlainClient(host, port);
		client.open();
		SecureMessage req = new SecureMessage();
		req.setCode(12);
		req.setPayload("Hello World");
		SecureMessage resp = client.makeRequest(req);
		System.out.println(resp.getCode());
		System.out.println(resp.getPayload());
		client.close();
	}
}
