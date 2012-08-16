package de.dfki.embots.behaviorbuilder.utility;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.logging.*;
import javax.swing.Timer;

/**
 * Handles the network connection to the agent for the EMBRGui.
 *
 * @author Oliver Schoenleben
 * @author Michael Kipp
 */
public class AgentNetworker implements BBConstants
{

    /**
     * The success state of a send action.
     * When something
     */
    public static enum SendStatus
    {

        ERROR, QUEUED, OK
    }
    protected Socket _socket;
    protected Timer _timer;
    protected Logger _l = Logger.getLogger("Networking");
    protected boolean _acceptSend;
    protected String _wannaSend;
    /** The port on which EMBR is supposed to listen. */
    protected int networkTimerDelay;
    protected int port;
    protected int sleepBetweenLines;
    protected int connectionCheckInterval;

    public AgentNetworker(Properties p)
    {
        networkTimerDelay = Integer.parseInt(p.getProperty("network.timerdelay"));
        port = Integer.parseInt(p.getProperty("network.port"));
        sleepBetweenLines = Integer.parseInt(p.getProperty("network.sleepBetweenLines"));
        connectionCheckInterval = Integer.parseInt(p.getProperty("network.connectionCheckInterval"));

        _timer = new Timer(networkTimerDelay, new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ev)
            {
                _acceptSend = true;
                if (null != _wannaSend) {
                    send(_wannaSend, false);
                }
            }
        });
        _acceptSend = true;
        _wannaSend = null;
    }

    /**
     * Checks whether the connection is still alive.
     * Unspecific things to do can be put into this method
     * (see commented region).
     *
     * Note: The connection can fail also temporarily. But often enough,
     *       it will not be re-established.
     *
     * @return whether or not the connection is currently alive
     */
    public boolean checkConnection()
    {
        try {
            Thread.sleep(connectionCheckInterval);
        } catch (InterruptedException ex) {
            _l.log(Level.FINE, "Network thread stopped");
        }
        connect('.');
        boolean connected = _socket.isConnected();

        if (!connected) {
            // what to do if connection failed.
        }

        disconnect();
        return connected;
    }

    /**
     * Create a temporary connection to send data to the EMBR engine.
     *
     * @param pulseSign a character used to display the network pulse
     * @return whether the connection could be established
     *
     * @see #disconnect() to close the connection
     */
    protected boolean connect(char pulseSign)
    {
        try {
            _socket = new Socket();
            _socket.connect(new InetSocketAddress("localhost", port));
            return true;
        } catch (UnknownHostException ex) {
            _l.log(Level.WARNING, "Unknown host", ex);
        } catch (SocketException ex) {
//            System.out.println("Socket establishment (connect) failed: " + ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Stops the connection to the agent.
     * The connection is kept only so long as needed to transport scripts
     * to the agent. Thus, most of the time the socket is unused, which
     * seems to be crucial for the robustness of the communication
     * (otherwise, the agent's process is killed when this application
     * terminates, for example).
     * @return the success of the socket operation
     * @see #connect(char) to establish a socket connection
     */
    private boolean disconnect()
    {
        try {
            _socket.close();
            return true;
        } catch (UnknownHostException ex) {
            _l.log(Level.WARNING, "", ex);
        } catch (SocketException ex) {
            _l.log(Level.WARNING, "Disconnect failed.", ex);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Does the actual (technical side of the) sending.
     *
     * The socket will be opened for writing, and closed after the
     * EMBRScript is delivered.
     */
    public SendStatus actuallySend(String str)
    {
        if (!connect('S')) {
            disconnect();
            return SendStatus.ERROR;
        }
        BufferedWriter wr = null;
        try {
            wr = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream()));
//            System.out.println("::send:");
        } catch (IOException ex) {
            _l.log(Level.WARNING, "Could not create writer.", ex);
            return SendStatus.ERROR;
        }
        try {
            wr = new BufferedWriter(
                    new OutputStreamWriter(_socket.getOutputStream()));
            Thread.sleep(sleepBetweenLines); //~throw out completely?
            wr.write(str);
            wr.newLine();
            wr.flush();
        } catch (IOException ex) {
            _l.log(Level.WARNING, "Could not write to socket.", ex);
            return SendStatus.ERROR;
        } catch (InterruptedException ex) {
            _l.log(Level.WARNING, "Interrupted.", ex);
            return SendStatus.ERROR;
        }
        disconnect();
        return SendStatus.OK;
    }

    /**
     * Sends the pose sequence to the agent.
     *
     * Usually, a timer is invoked to determine whether the send
     * should be instantly carried out, or rather be scheduled for
     * delivery on the next tick.
     * The timer shall prevent network traffic overload and socket.
     *
     * @param script The EMBRScript to be sent
     * @param timed whether the timer should be used
     * @return status message
     */
    public SendStatus send(String script, boolean timed)
    {
        if (timed && !_acceptSend) {
            _wannaSend = script;
            return SendStatus.QUEUED;
        }
        _acceptSend = false;
        _wannaSend = null;
        _timer.restart();
        return actuallySend(script);
    }
}
