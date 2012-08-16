package de.dfki.carmina.eyeTrackerLogger;

import java.io.*;

/**
 * Main class of the EyeTracker Logger
 *
 * @author Rafael Math
 * @version 1.0
 */
public class Logger {

	// Settings
	public static final int UDP_PORT = 2010;
	public static final int PACKET_SIZE = 4048;


	public static void main(String[] args) {
		UDPThread logthread;
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String line = "";

		// create log-thread
//		logthread = new UDPThread();

		// start log-thread
//		logthread.start();

		System.out.println("press 'e' + <enter> to terminate: ");

		try {
			// read input stream while input != e
			while (!line.equalsIgnoreCase("e")) {
				line = input.readLine();
			}
		} catch (IOException e) {
			System.err.println(e.toString());
		}

		// stop log-thread
//		logthread.requestStop();
	}
}
