package de.as.stream.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test class for an MjpegInputStream including a simple client
 * @author alex
 */
public class TestClient {

    /**
	* Test function to be used for a client connection to an Mjpeg server.
	* @throws IOException
	*/
	@SuppressWarnings("resource")
	public static void main() throws InterruptedException {
		String urlString = "http://192.168.178.36:8080/";
		String path = "C:\\Users\\alex\\Downloads\\test\\";
		try {
				URL url = new URL(urlString);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.connect();
				System.out.println();
				System.out.println(urlString);
				System.out.println("Content-Type " + con.getContentType());
				System.out.println("Content-Length " + con.getContentLength());
				System.out.println("Content-Encoding " + con.getContentEncoding());
				String contentType = con.getContentType();
				String boundary = contentType.substring(contentType.indexOf("boundary=") + 9);
				System.out.println(">> " + boundary);
				int i = 0;
				MjpegInputStream dis = new MjpegInputStream(con.getInputStream(), boundary);
				while (true) {
					byte[] b = dis.readJpegAsByteArray();
					(new FileOutputStream(path + (++i) + ".jpg")).write(b);
				}
		} catch (IOException ex) {
			Logger.getLogger(MjpegInputStream.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
