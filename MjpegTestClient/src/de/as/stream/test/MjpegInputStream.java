package de.as.stream.test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * Test class for an MjpegInputStream.
 * @author a. schoessow
 * @version 0.0.1
 * 
 * Change log:
 * 30.09.2014: First implementation
 */
public class MjpegInputStream extends DataInputStream {

    public static int MAX_HEADER_SIZE = 1920 * 1800 * 3;
    private String boundary = null;

    /**
     * Default Constructor
     * @param inStream The input stream needed for the client connection
     * @param boundary The boundary split ID for the data content
     */
    public MjpegInputStream(InputStream inStream, String boundary) {
        super(inStream);
        this.boundary = boundary;
    }

    /**
     * Advance a byte stream to a specific position
     * @param sequence The sequence the byte stream shall be forwarded to
     * @return length The stream length
     * @throws IOException
     */
    protected int advanceToSequence(byte[] sequence) throws IOException {
    	int length = 0;
    	byte b = -1;
        int posSequence = 0;
        for (int i = 0; i < MAX_HEADER_SIZE; i++) {
            b = readByte();
            if (b == sequence[posSequence]) {
                posSequence+=1;
                if (posSequence == sequence.length) {
                    return ++length;
                }
            } else {
                posSequence = 0;
            }
            length+=1;
        }
        return -1;
    }

    /**
     * Read exactly one line from the stream.
     * @return String The received line as string
     * @throws IOException
     */
    public String readStreamLine() throws IOException {
        StringBuffer sbuf = new StringBuffer();
        boolean isEnd = false;
        boolean isReturn = false;
        while (!isEnd) {
            char ch;
            ch = (char)read();
            if (ch == '\r') {
                isReturn = true;
            } else if (ch == '\n') {
                isEnd = true;
            } else {
                sbuf.append(ch);
            }
        }
        return sbuf.toString();
    }

    /**
     * 
     * @return byte[] The received Jpeg as byte array.
     * @throws IOException
     */
    public byte[] readJpegAsByteArray() throws IOException {
        byte[] buffer = null;
        byte[] BOUNDARY = (boundary + "\r\n").getBytes();
        // Search for the stream boundary
        advanceToSequence(BOUNDARY);
        String typeLine = null;
        String lengthLine = null;
        String line = null;
        while ((line = readStreamLine()) != null) {
            if (line.length() != 0) {
                if (line.toLowerCase().startsWith("content-type")) {
                    typeLine = line;
                } else if (line.toLowerCase().startsWith("content-length")) {
                    lengthLine = line;
                } else {
                    System.err.println("Unknown line type: " + line);
                }

            } else {
                break;
            }
        }
        if (typeLine == null || lengthLine == null) {
            System.err.println("ERROR READING ... the header was corrupt.");
        }
        String[] typePieces = typeLine.split(": ");
        String[] lengthPieces = lengthLine.split(": ");
        if (typePieces.length != 2 || lengthPieces.length != 2) {
            System.err.println("Error Reading .... ");
        }
        int contentLength = Integer.parseInt(lengthPieces[1]);
        buffer = new byte[contentLength];
        // Blocking read of the full stream
        readFully(buffer);
        return buffer;
    }
    
    /**
     * Read the Jpeg as buffered image.
     * @return BufferedImage The image
     * @throws IOException
     */
    public BufferedImage readJpegAsBufferedImage() throws IOException {
        byte[] b = readJpegAsByteArray();
        return ImageIO.read(new ByteArrayInputStream(b));
    }
}