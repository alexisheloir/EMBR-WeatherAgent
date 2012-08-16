package de.dfki.carmina.eyeTrackerLogger;

import java.net.*;

import de.dfki.carmina.eyeTrackerLogger.dataProcessor.DataProcessor;
import de.dfki.carmina.eyeTrackerLogger.dataProcessor.LogData;
import de.dfki.embots.framework.ui.eyetracking.Gaze;
import javax.jms.JMSException;


/**
 * "UDPThread" provides a connection via UDP. DatagramPackets will be
 * received from the given port. This class extends the class "Thread".
 */
public class UDPThread extends Thread {
	private boolean stoprequested;
	private DatagramPacket packet;
	private DatagramSocket incomingSocket;
        private Gaze gaze;

	public UDPThread(Gaze gaze) {
		super();
		stoprequested = false;




		try {

			// open socket connection and listen for incoming packets
			incomingSocket = new DatagramSocket(Logger.UDP_PORT);
			byte data[] = new byte[Logger.PACKET_SIZE];
			packet = new DatagramPacket(data, Logger.PACKET_SIZE);

			// set time to wait after an unsuccessful receive attempt
			incomingSocket.setSoTimeout(300);

			System.out.println("UDP connection established");
                        this.gaze = gaze;

		} catch (SocketException e) {
			System.err.println("UDPThread_Constructor: " + e.toString());

	}
    }


	public synchronized void requestStop() {
		stoprequested = true;
	}

	@Override
	public void run() {
		DataProcessor processor = new DataProcessor();
		int packetsize;
		byte[] packetdata;
		String datastring;
		LogData logdata;

		while (!stoprequested) {
			try {
				// read data and get length
				incomingSocket.receive(packet);
				packetsize = packet.getLength();
				packetdata = packet.getData();

			} catch (SocketTimeoutException e) {
				// suppress error output if no data available at incomingSocket
				// wait 300ms as defined in SoTimeout and try again
				continue;
			} catch(NullPointerException e){
				// suppress error output if no data available at incomingSocket
				continue;
			} catch (Exception e) {
				System.err.println("UDPThread_run(): " + e.toString());
				continue;
			}

			// transform incoming data to string of variable length
			datastring = new String(packetdata, 0, packetsize);

			// decode string, parse XML, output log data
			logdata = processor.getLogData(datastring);
                        gaze.setLogData(logdata);

			//System.out.println(logdata.toString());
		}


		// close socket connection
		try {
			if (null != incomingSocket)
				incomingSocket.close();
		} catch (Exception ex) {
		}

		System.out.println("logging terminated");
	}
}