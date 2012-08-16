/**
 * Copyright (C) 2008 DFKI GmbH. All rights reserved.
 * Use is subject to license terms -- see license.txt.
 */
package de.dfki.embots.modules.audio;

import de.dfki.embots.framework.EMBOTSConstants;
import java.io.ByteArrayInputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import marytts.util.data.audio.AudioPlayer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import eu.semaine.components.Component;
import eu.semaine.datatypes.xml.SemaineML;
import eu.semaine.jms.message.SEMAINEBytesMessage;
import eu.semaine.jms.message.SEMAINEMessage;
import eu.semaine.jms.receiver.BytesReceiver;
import eu.semaine.jms.receiver.Receiver;
import eu.semaine.jms.sender.Sender;
import eu.semaine.jms.sender.XMLSender;
import eu.semaine.util.XMLTool;

/**
 * Audio player for playing the speech audio produced by the TTS.
 *
 * Receives audio byte stream from the TTS, then sends a READY
 * signal and waits for a GO signal to play the audio.
 *
 * MK: Changed this so that multiple audio pieces can be played even with
 * overlap.
 *
 * @author Michael Kipp
 * @author Alexis Heloir
 * @author Sathish Chandra Pammi
 */
public class EMBOTSAudioPlayer extends Component
{

    private static final String NAME = "Audio Player";
//    private BytesReceiver _audioDataReceiver;
    private XMLSender _callbackSender;
    private Sender _readytoPlaySender;
//    private Receiver _playSignalReceiver;
    private BlockingQueue<SEMAINEBytesMessage> _byteMessageQueue;
    private boolean _playNow = false;
    private Playloop player;
//    private Sender _bmlFeedbackSender;

    /**
     * Light wrapper around the AudioPlayer class which includes sending
     * a callback message as soon as playback is finished. This is necessary
     * to enable the potentially parallel playback of incoming audio streams.
     */
    class MyPlayer extends Thread
    {

        AudioPlayer _audioPlayer;
        String _id;

        public MyPlayer(AudioPlayer player, String id)
        {
            _audioPlayer = player;
            _id = id;
        }

        @Override
        public void run()
        {
            try {
                // Send feedback
                sendCallbackMessage("start", _id);

                // Start playback
                _audioPlayer.start();

                // Wait until finished
                _audioPlayer.join();
                sendCallbackMessage("end", _id);

            } catch (JMSException ex) {
                Logger.getLogger(EMBOTSAudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(EMBOTSAudioPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @throws JMSException
     */
    public EMBOTSAudioPlayer() throws JMSException
    {
        super(NAME, false, true);
        
        //audioReceiver = new BytesReceiver("semaine.data.synthesis.lowlevel.audio");
        //_playReceiver = new Receiver("semaine.callback.embr.audio.play");
//        _audioDataReceiver = new BytesReceiver(EMBOTSConstants.AUDIO_LOWLEVEL_DATA_TYPE);
//        _playSignalReceiver = new Receiver(EMBOTSConstants.AUDIO_PLAY_TYPE);

        // init receivers
        receivers.add(new BytesReceiver(EMBOTSConstants.AUDIO_LOWLEVEL_DATA_TYPE));
        receivers.add(new Receiver(EMBOTSConstants.AUDIO_PLAY_TYPE));

        // init senders
        _callbackSender = new XMLSender(EMBOTSConstants.AUDIO_CALLBACK_TYPE, "SemaineML", getName());
        _readytoPlaySender = new Sender(EMBOTSConstants.AUDIO_READY_TYPE, "String", getName());
//        _bmlFeedbackSender = new Sender(EMBOTSConstants.BML_FEEDBACK_TYPE, "String", NAME);
//        senders.add(_bmlFeedbackSender);
        senders.add(_readytoPlaySender);
        senders.add(_callbackSender);

        // init queue and player
        _byteMessageQueue = new LinkedBlockingQueue<SEMAINEBytesMessage>();
        player = new Playloop(_byteMessageQueue);
        player.start();
    }

    @Override
    public void react(SEMAINEMessage m) throws JMSException
    {
        if (m.isBytesMessage()) {
            SEMAINEBytesMessage bm = (SEMAINEBytesMessage) m;
            try {
                _byteMessageQueue.put(bm);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (m.getTopicName().equals(EMBOTSConstants.AUDIO_PLAY_TYPE)) {
            _playNow = true;
        }
    }

    /**
     * Play loop starts running in the background as soon as this module is
     * instanciated. When input is available, the player sends a ready signal
     * and waits for a go signal. As soon as the go signal arrives, the player
     * plays the audio. 
     */
    public class Playloop extends Thread
    {

        protected BlockingQueue<SEMAINEBytesMessage> inputWaiting;
        protected boolean playing = false;

        public Playloop(BlockingQueue<SEMAINEBytesMessage> inputWaiting)
        {
            this.inputWaiting = inputWaiting;
        }

        @Override
        public void run()
        {
            while (true) {
                SEMAINEBytesMessage bm = null;
                try {
                    // block until input becomes available
                    bm = inputWaiting.poll(waitingTime, TimeUnit.MILLISECONDS);
                    if (bm == null) {
                        continue;
                    }
                } catch (InterruptedException ie) {
                    // if we have no input, we'll keep on waiting
                    continue;
                }
                ByteArrayInputStream bais = new ByteArrayInputStream(bm.getBytes());

                try {
                    // BML feedback message
//                    BMLFeedback feebackStart = new BMLFeedback(BMLFeedback.Type.STATUS,
//                            NAME, "Starting to speak");

                    log.debug(bm.getContentID() + " started playing " + (meta.getTime() - bm.getContentCreationTime()) + " ms after creation");
                    AudioInputStream ais = AudioSystem.getAudioInputStream(bais);
                    AudioPlayer player = new AudioPlayer(ais);
                    _playNow = false;
                    _readytoPlaySender.sendTextMessage("Audio is ready", meta.getTime());

                    // set up wrapper class
                    MyPlayer myPlayer = new MyPlayer(player, bm.getContentID());

                    // wait for go
                    while (!_playNow) {
                        sleep(20);
                    }

                    // now play
                    myPlayer.start();

//                    // Send feedback
//                    sendCallbackMessage("start", bm.getContentID());
//
//                    // Start playback
//                    player.start();
//
//                    // Wait until finished
//                    player.join();
//
//                    sendCallbackMessage("end", bm.getContentID());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendCallbackMessage(String type, String contentID) throws JMSException
    {
        Document doc = XMLTool.newDocument("callback", SemaineML.namespaceURI);
        Element root = doc.getDocumentElement();
        Element callback = XMLTool.appendChildElement(root, SemaineML.E_EVENT, SemaineML.namespaceURI);
        callback.setAttribute("type", type);
        callback.setAttribute("data", "audio");
        callback.setAttribute("id", contentID != null ? contentID : "unknown");
        callback.setAttribute(SemaineML.A_TIME, String.valueOf(meta.getTime()));
        _callbackSender.sendXML(doc, meta.getTime());
    }
}
