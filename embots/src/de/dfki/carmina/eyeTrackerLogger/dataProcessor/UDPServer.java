package de.dfki.carmina.eyeTrackerLogger.dataProcessor;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;

/**
 * This class provides a UDP server that connects to a given IP address
 * and port in order transmit the position and direction of the current
 * camera view, annotated by the current time stamp.<br>
 * The required frame rate can be set in the constructor.
 */
public class UDPServer {
	Calendar timeOfLastFire = new GregorianCalendar();
	InetAddress ipAddress = null;
	int port;
	DatagramSocket serverSocket;
	int framerate;


	/**
	 * The constructor
	 *
	 * @param IP
	 *            The client's IP address to connect to
	 * @param port
	 *            The target port on the client machine
	 * @param framerate
	 *            number of times the camera data will be sent within 1 second
	 */
	public UDPServer(String IP, int port, int framerate) {

		this.port = port;
		this.framerate = framerate;

		try {
			// lookup IP address
			ipAddress = InetAddress.getByName(IP);

			// open socket connection
			serverSocket = new DatagramSocket();

		} catch (Exception e) {
			System.err.println("UDPServer_constructor: " + e.toString());
		}
	}


	/**
	 * The default constructor setting up the following defaults:<br>
	 * IP = "localhost"<br>
	 * port = 2000<br>
	 * framerate = 40
	 */
	public UDPServer() {

		port = 2000;
		framerate = 40; // choose between 1 - 200 frames per second

		try {
			// lookup IP address
			ipAddress = InetAddress.getLocalHost();

			// open socket connection
			serverSocket = new DatagramSocket();

		} catch (Exception e) {
			System.err.println("UDPServer_constructor: " + e.toString());
		}
	}


	/**
	 * This method sends the given camera data (as XML string) to the client machine,
	 * regarding the frame rate. If not enough time has passed by since last fire,
	 * the camera data might be rejected
	 *
	 * @param camera
	 * 			The current camera view
	 */
	public void send() {

		// generate time stamp
		Calendar currentTime = new GregorianCalendar();

		// if enough time has passed by since last fire, the event will be forwarded
		if(forwardEvent(currentTime))
		{
			String sendstring = "testfdiogjsdgklfdklfdhkljdfhkljsklhjfdl123123123121shjdhlhfdh";

			sendstring = zip(sendstring);

			System.out.println(sendstring);

			// build packet
			byte[] sendData = sendstring.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);

			try {
				serverSocket.send(sendPacket);
			} catch (IOException e) {
				System.err.println("UDPServer_send(): " + e.toString());
			}
		}
	}


	/**
	 * This method checks whether the incoming camera information should
	 * be sent to the client machine at the current time complying with
	 * the given frame rate
	 *
	 * @param now
	 * 			The current time stamp
	 *
	 * @return true if enough time has passed by since last fire, false otherwise
	 */
    private boolean forwardEvent(Calendar now)
    {
        // fire an event every x milliseconds
    	int fireInterval = 1000 / framerate;

        // subtract time of last event from current time to get time elapsed since last fire
        long elapsedMillisecs = timeDiff(now,timeOfLastFire);

        if (elapsedMillisecs >= fireInterval)
        {
            // update time of last fire
            timeOfLastFire.add(Calendar.MILLISECOND, fireInterval);

            //fire
            return true;
        }
        else
            // do not fire
            return false;
    }


	/**
	 * This method computes the difference between two given time stamps
	 *
	 * @param timestamp1
	 * 			First time stamp value to compare
	 *
	 * @param timestamp2
	 * 			Second time stamp value to compare
	 *
	 * @return difference between the given time stamps in milliseconds (long)
	 */
    private static long timeDiff(Calendar timestamp1, Calendar timestamp2)
    {
        return Math.abs(timestamp1.getTimeInMillis() - timestamp2.getTimeInMillis());
    }


    public static void main(String[] args)
    {
    	UDPServer udp = new UDPServer();

    	for(int i = 0; i<100; i++)
    	{
    		udp.send();
    		try {
    			Thread.sleep(1000);

			} catch (InterruptedException e){
			// the VM doesn't want us to sleep anymore,
			// so get back to work
			}

    	}
    }



    public static String zip(String unzipped)
    {
    	byte[] retval = null;
		try {
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    BufferedOutputStream bufos = new BufferedOutputStream(new GZIPOutputStream(bos));

			bufos.write(unzipped.getBytes());
		    bufos.close();
		    retval = bos.toByteArray();
		    bos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return new String(retval);
	}
}
