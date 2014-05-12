package com.asksunny.ssl.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import javax.net.ssl.SSLContext;

import com.asksunny.ssl.SecureSocketSslContextFactory;

public class SecureSocketPlainLengthFrameClient {
	private final String host;
	private final int port;
	Socket clientSocket;
	OutputStream clientOut;
	InputStream clientIn;
	
	public SecureSocketPlainLengthFrameClient(String host, int port) {
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
		ByteBuf buf = LengthFrameUtil.convertRequest(request);
		buf.readBytes(this.clientOut, buf.readableBytes());
		this.clientOut.flush();
		buf = Unpooled.buffer();
		buf.writeBytes(this.clientIn, 4);
		int len = buf.readInt();
		buf.writeBytes(this.clientIn, len);
		response = new SecureMessage();
		response.setCode(buf.readByte());
		response.setPayload(buf.toString(Charset.defaultCharset()));
		return response;
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
	
	public void run() throws Exception
	{
		SSLContext clientSslContext = SecureSocketSslContextFactory.getClientContext();
		Socket clientSocket = clientSslContext.getSocketFactory().createSocket(host, port);
		OutputStream clientOut = clientSocket.getOutputStream();
		InputStream clientIn = clientSocket.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				System.in));
		for(;;){
			String line =in.readLine();
			clientOut.write((line+"\n").getBytes());
			clientOut.flush();
		}
		
		
	}
	
	public static void main(String[] args) throws Exception 
	{
		
		String host = args.length>0?args[0]:"localhost";
		int port = args.length>1?Integer.parseInt(args[1]):8443;
		new SecureSocketPlainLengthFrameClient(host, port).run();
	}
}
