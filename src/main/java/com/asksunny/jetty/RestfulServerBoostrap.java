package com.asksunny.jetty;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import com.asksunny.ssl.SecureSocketKeyStore;

public class RestfulServerBoostrap {

	
	
	public void run()
	{
		try{
		// Create a basic jetty server object without declaring the port.  Since we are configuring connectors
        // directly we'll be setting ports on those connectors.
        Server server = new Server();
 
        // HTTP Configuration
        // HttpConfiguration is a collection of configuration information appropriate for http and https. The default
        // scheme for http is <code>http</code> of course, as the default for secured http is <code>https</code> but
        // we show setting the scheme to show it can be done.  The port for secured communication is also set here.
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);
        http_config.setOutputBufferSize(32768);
 
        // HTTP connector
        // The first server connector we create is the one for http, passing in the http configuration we configured
        // above so it can get things like the output buffer size, etc. We also set the port (8080) and configure an
        // idle timeout.
        ServerConnector http = new ServerConnector(server,new HttpConnectionFactory(http_config));        
        http.setPort(8080);
        http.setIdleTimeout(30000);
         
        // SSL Context Factory for HTTPS and SPDY
        // SSL requires a certificate so we configure a factory for ssl contents with information pointing to what
        // keystore the ssl connection needs to know about. Much more configuration is available the ssl context,
        // including things like choosing the particular certificate out of a keystore to be used.
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStore(SecureSocketKeyStore.getKeyStore());       
        sslContextFactory.setKeyStorePassword(SecureSocketKeyStore.getKeyStorePasswordString());
        sslContextFactory.setKeyManagerPassword(SecureSocketKeyStore.getCertificatePasswordString());
 
        // HTTPS Configuration
        // A new HttpConfiguration object is needed for the next connector and you can pass the old one as an
        // argument to effectively clone the contents. On this HttpConfiguration object we add a
        // SecureRequestCustomizer which is how a new connector is able to resolve the https connection before
        // handing control over to the Jetty Server.
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());
 
        // HTTPS connector
        // We create a second ServerConnector, passing in the http configuration we just made along with the
        // previously created ssl context factory. Next we set the port and a longer idle timeout.
        ServerConnector https = new ServerConnector(server,
            new SslConnectionFactory(sslContextFactory,"http/1.1"),
            new HttpConnectionFactory(https_config));
        https.setPort(8443);
        https.setIdleTimeout(500000);
 
        // Here you see the server having multiple connectors registered with it, now requests can flow into the server
        // from both http and https urls to their respective ports and be processed accordingly by jetty. A simple
        // handler is also registered with the server so the example has something to pass requests off to.
 
        // Set the connectors
        server.setConnectors(new Connector[] { https });
 
        server.setHandler(new RESTfulHandler());
 
        // Start the server
        server.start();
        server.join();
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RestfulServerBoostrap server = new RestfulServerBoostrap();
		server.run();

	}

}
