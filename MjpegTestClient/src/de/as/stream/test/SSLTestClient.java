package de.as.stream.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Test class for an MjpegInputStream including a simple SSL client.
 * @author alex
 */
public class SSLTestClient {

    /**
	* Test function to be used for a SSL client connection to an
	* Mjpeg server.
	* @throws IOException
     * @throws  
	*/
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			String urlString = "http://192.168.178.36:8080/";
			String path = "C:\\temp\\";
			InputStream in = new FileInputStream(new File("D:\\keystore.jks"));
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(in, "test123".toCharArray());
			in.close();
			in = new FileInputStream(new File("D:\\ca.jks"));
			KeyStore ts = KeyStore.getInstance(KeyStore.getDefaultType());
			ts.load(in, "test123".toCharArray());
			in.close();
			TrustManagerFactory tmf = TrustManagerFactory
	                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ts);
			KeyManagerFactory kmf = KeyManagerFactory
			                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, "test123".toCharArray());
			SSLContext sslCtx = SSLContext.getInstance("TLS");
			sslCtx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
			URL url = new URL(urlString);
		    HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			sslCtx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
		    SSLSocketFactory sockFact = sslCtx.getSocketFactory();
		    con.setSSLSocketFactory(sockFact);
		    con.setHostnameVerifier(new HostnameVerifier() {        
	            public boolean verify(String hostname, SSLSession session)  
	            {  
	                return true;  
	            }
	        });	    
		    con.connect();
			System.out.println();
			System.out.println(urlString);
			System.out.println("Content-Type " + con.getContentType());
			System.out.println("Content-Length " + con.getContentLength());
			System.out.println("Content-Encoding " + con.getContentEncoding());
			String contentType = con.getContentType();
			String boundary = contentType.substring(contentType.indexOf("boundary=") + 9);
			System.out.println(">> " + boundary);
			MjpegInputStream dis = new MjpegInputStream(con.getInputStream(), boundary);
			int i = 0;
			while (true) {
				byte[] b = dis.readJpegAsByteArray();
				(new FileOutputStream(path + (++i) + ".jpg")).write(b);
				
			}
		} catch (IOException e) {
			Logger.getLogger(MjpegInputStream.class.getName()).log(Level.SEVERE, null, e);
		} catch (KeyStoreException e) {
			Logger.getLogger(MjpegInputStream.class.getName()).log(Level.SEVERE, null, e);
		} catch (KeyManagementException e) {
			Logger.getLogger(MjpegInputStream.class.getName()).log(Level.SEVERE, null, e);
		} catch (NoSuchAlgorithmException e) {
			Logger.getLogger(MjpegInputStream.class.getName()).log(Level.SEVERE, null, e);
		} catch (CertificateException e) {
			Logger.getLogger(MjpegInputStream.class.getName()).log(Level.SEVERE, null, e);
		} catch (UnrecoverableKeyException e) {
			Logger.getLogger(MjpegInputStream.class.getName()).log(Level.SEVERE, null, e);
		}		
	}
}