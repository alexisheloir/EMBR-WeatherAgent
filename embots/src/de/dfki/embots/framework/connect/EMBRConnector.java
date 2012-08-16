/*
 * EMBRConnector.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 21.01.2010, 13:26:17
 */
package de.dfki.embots.framework.connect;

import de.dfki.embots.bml.feedback.BMLFeedback;
import de.dfki.embots.framework.EMBOTSConstants;
import eu.semaine.components.Component;
import eu.semaine.jms.message.SEMAINEMessage;
import eu.semaine.jms.receiver.Receiver;
import eu.semaine.jms.sender.Sender;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;

/**
 * This module sends incoming EMBRScripts to EMBR via socket.
 * Gets incoming messages from the modules GestureGenerator and
 * AffectRealizer.
 *
 * @author Michael Kipp
 */
public class EMBRConnector extends Component
{

    private static final String NAME = "EMBR Connector";
    private static final int PORT = 5555;
    private Sender _bmlFeedbackSender;
    private Socket _socket;
    private Boolean remote = true;

    public EMBRConnector() throws JMSException
    {
        super(NAME);
        receivers.add(new Receiver(EMBOTSConstants.EMBRSCRIPT_TYPE));
        _bmlFeedbackSender = new Sender(EMBOTSConstants.BML_FEEDBACK_TYPE, "String", NAME);
        senders.add(_bmlFeedbackSender);
    }

    @Override
    public void react(SEMAINEMessage m) throws Exception
    {
        if (m.getTopicName().equals(EMBOTSConstants.EMBRSCRIPT_TYPE)) {
            sendToEMBR(m.getText());
        }
    }

    private void connect() throws IOException
    {
        _socket = new Socket();
        String ipAdress = remote ? "134.96.105.69" : "localhost";
        _socket.connect(new InetSocketAddress(ipAdress, PORT));
        log.debug("Connected with EMBR");
    }

    private void disconnect() throws IOException
    {
        _socket.close();
        log.debug("Disconnected from EMBR");
    }

    /**
     * Sends EMBRScript to EMBR if a connection can be established. Sends
     * a warning feedback if the script could not be sent.
     *
     * @param script EMBRScript to be sent
     */
    private void sendToEMBR(String script)
    {
        try {
            connect();
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream()));
            wr.write(script);
            wr.newLine();
            wr.flush();
            disconnect();
        } catch (IOException ex) {
            BMLFeedback fb = new BMLFeedback(BMLFeedback.Type.WARNING, NAME,
                    "No connection to EMBR: " + ex.getMessage());
            try {
                _bmlFeedbackSender.sendTextMessage(fb.toXML(), meta.getTime());
            } catch (JMSException ex1) {
                Logger.getLogger(EMBRConnector.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }
}
