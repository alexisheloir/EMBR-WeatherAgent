package de.dfki.embots.framework.behavior;

import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.bml.behavior.BMLBehavior;
import de.dfki.embots.bml.behavior.BMLSpeechBehavior;
import de.dfki.embots.bml.feedback.BMLFeedback;
import de.dfki.embots.bml.reader.BMLReader;
import de.dfki.embots.bml.reader.BMLReaderException;
import de.dfki.embots.framework.EMBOTSConstants;
import de.dfki.embots.framework.ui.ModuleErrorDialog;
import eu.semaine.components.Component;
import eu.semaine.datatypes.stateinfo.ContextStateInfo;
import eu.semaine.datatypes.stateinfo.StateInfo;
import eu.semaine.datatypes.xml.BML;
import eu.semaine.datatypes.xml.EMMA;
import eu.semaine.datatypes.xml.FML;
import eu.semaine.datatypes.xml.SSML;
import eu.semaine.datatypes.xml.SemaineML;
import eu.semaine.jms.IOBase.Event;
import eu.semaine.jms.message.SEMAINEMessage;
import eu.semaine.jms.receiver.Receiver;
import eu.semaine.jms.sender.FMLSender;
import eu.semaine.jms.sender.Sender;
import eu.semaine.jms.sender.StateSender;
import eu.semaine.util.XMLTool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This module acts as a gateway from the outside to realize a piece
 * of BML code. It receives BML blocks and connects to TTS, gesture
 * generator etc. to realize the animation. In return, it dispatches
 * BML status and exception messages which are sent to the pool of
 * BML_FEEDBACK_TYPE.
 *
 * @author Michael Kipp
 */
public class BMLRealizer extends Component
{

    public static final String NAME = "BML Realizer";
    private static final String DEFAULT_VOICE = "Prudence";
    private FMLSender _ttsSender;
    private Sender _bmlsSender;
    private Sender _bmlFeedbackSender;
    private StateSender _contextSender;
    private Map<String, String> _voiceMap;
    private String _selectedVoice;

    public BMLRealizer() throws JMSException
    {
        super(NAME, true, false);
        initVoiceMap();
        initSenders();
        initReceivers();
    }

    private void initSenders() throws JMSException
    {
        _ttsSender = new FMLSender(EMBOTSConstants.SEMAINE_FML_TYPE, NAME);
        _bmlsSender = new Sender(EMBOTSConstants.BML_INTERNAL_TYPE, "String", NAME);
        _bmlFeedbackSender = new Sender(EMBOTSConstants.BML_FEEDBACK_TYPE, "String", NAME);
        _contextSender = new StateSender(EMBOTSConstants.CONTEXT_TYPE, StateInfo.Type.ContextState, getName());
        senders.add(_ttsSender);
        senders.add(_bmlsSender);
        senders.add(_bmlFeedbackSender);
        senders.add(_contextSender);
    }

    private void initReceivers() throws JMSException
    {
        receivers.add(new Receiver(EMBOTSConstants.BML_INPUT_TYPE));
    }

    @Override
    public void react(SEMAINEMessage m) throws Exception
    {
        try {
            if (m.getTopicName().equals(EMBOTSConstants.BML_INPUT_TYPE)) {

                // case: receives BML input
//            log.info("Received input BML");

                String bmlText = m.getText();

                // send BML file to gesture generator
                _bmlsSender.sendTextMessage(bmlText, meta.getTime());

                // extract speech part and send to speech synthesis
                BMLBlock block = parseBML(bmlText);

                if (block != null) {
                    ArrayList<BMLSpeechBehavior> behaviors = new ArrayList<BMLSpeechBehavior>();
                    for (BMLBehavior beh : block.getBehaviors()) {
                        if (beh instanceof BMLSpeechBehavior) {
                            behaviors.add((BMLSpeechBehavior) beh);
                        }
                    }
                    sendUtterance(behaviors);
                }
            }
        } catch (RuntimeException e) {
            ModuleErrorDialog.show(NAME, e.getStackTrace());
            log.error(NAME + " failed! " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Parses current text as BML.
     *
     * @return BML block
     */
    private BMLBlock parseBML(String bmlText)
    {
        try {
            BMLReader reader = new BMLReader();
            return reader.getBMLBlock(bmlText);
        } catch (BMLReaderException ex) {
            try {
                _bmlFeedbackSender.sendTextMessage(new BMLFeedback(BMLFeedback.Type.ERROR, NAME, "Could not parse BML block").toXML(), meta.getTime());
            } catch (JMSException ex1) {
                Logger.getLogger(BMLRealizer.class.getName()).log(Level.SEVERE, null, ex1);
            }
            log.error("Couldn't parse BML! " + ex.getMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    /**
     * Sends the given utterance to the userstate using a BMLMessage
     * @param line
     */
    public void sendUtterance(String line)
    {
        /* If speech signals are not continuously send, send a speaking signal to indicate that an utterance has been spoken */
        sendSpeaking();
        String id = "s1";
        Document doc = XMLTool.newDocument("fml-apml", null, FML.version);
        Element root = doc.getDocumentElement();

        Element bml = XMLTool.appendChildElement(root, BML.E_BML, BML.namespaceURI);
        bml.setAttribute(BML.A_ID, "bml1");
        Element fml = XMLTool.appendChildElement(root, FML.E_FML, FML.namespaceURI);
        fml.setAttribute(FML.A_ID, "fml1");
        Element speech = XMLTool.appendChildElement(bml, BML.E_SPEECH);
        speech.setAttribute(BML.A_ID, id);
        speech.setAttribute(BML.E_TEXT, line);
        speech.setAttribute(BML.E_LANGUAGE, _voiceMap.get(_selectedVoice));

        int counter = 1;
        for (String word : line.split(" ")) {
            Element mark = XMLTool.appendChildElement(speech, SSML.E_MARK, SSML.namespaceURI);
            mark.setAttribute(SSML.A_NAME, id + ":tm" + counter);
            Node text = doc.createTextNode(word);
            speech.appendChild(text);
            counter++;
        }
        Element mark = XMLTool.appendChildElement(speech, SSML.E_MARK, SSML.namespaceURI);
        mark.setAttribute(SSML.A_NAME, id + ":tm" + counter);
        try {
            _ttsSender.sendXML(doc, meta.getTime(), Event.single);
        } catch (JMSException e) {
            e.printStackTrace();
        }

        /* If speech signals are not continuously send, send a silence signal to indicate that the utterance is over */
        sendSilent();
    }

    public void sendUtterance(List<BMLSpeechBehavior> behaviors)
    {

        for (BMLSpeechBehavior b : behaviors) {
            String line = b.getText();
            String voice = b.getVoice();
//            long wait = b.getWait();

            /* If speech signals are not continuously send, send a speaking signal to indicate that an utterance has been spoken */
            sendSpeaking();
            String id = b.getID();
            Document doc = XMLTool.newDocument("fml-apml", null, FML.version);
            Element root = doc.getDocumentElement();

            Element bml = XMLTool.appendChildElement(root, BML.E_BML, BML.namespaceURI);
            bml.setAttribute(BML.A_ID, "bml1");
//            bml.setAttribute(BML.E_DESCRIPTION, agent);
            Element fml = XMLTool.appendChildElement(root, FML.E_FML, FML.namespaceURI);
            fml.setAttribute(FML.A_ID, "fml1");
            Element speech = XMLTool.appendChildElement(bml, BML.E_SPEECH);
            speech.setAttribute(BML.A_ID, id);
            speech.setAttribute(BML.E_TEXT, line);
            speech.setAttribute(BML.E_LANGUAGE, _voiceMap.get(voice));



            int counter = 1;
            for (String word : line.split(" ")) {
                Element mark = XMLTool.appendChildElement(speech, SSML.E_MARK, SSML.namespaceURI);
                mark.setAttribute(SSML.A_NAME, id + ":tm" + counter);
                Node text = doc.createTextNode(word);
                speech.appendChild(text);
                counter++;
            }
            Element mark = XMLTool.appendChildElement(speech, SSML.E_MARK, SSML.namespaceURI);
            mark.setAttribute(SSML.A_NAME, id + ":tm" + counter);
            try {
                if (b.getWait() > 0) {
                    long start = System.currentTimeMillis();
                    while (System.currentTimeMillis() < start + b.getWait()/2) {
                        try {
                            sleep(20);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(BMLRealizer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

//                try {
//                    sleep(2000);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(BMLRealizer.class.getName()).log(Level.SEVERE, null, ex);
//                }
                
                // change voice
                if (voice != null) {
                    try {
                        ContextStateInfo context = constructContextStateInfo(voice);
                        _contextSender.sendStateInfo(context, meta.getTime());
                    } catch (JMSException ex) {
                        Logger.getLogger(BMLRealizer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                

                // send utterance
                _ttsSender.sendXML(doc, meta.getTime(), Event.single);
            } catch (JMSException e) {
                e.printStackTrace();
            }

            /* If speech signals are not continuously send, send a silence signal to indicate that the utterance is over */
            sendSilent();
        }

    }

    private void initVoiceMap()
    {
        _voiceMap = new HashMap<String, String>();
        _voiceMap.put("Prudence", "en-GB");
        _voiceMap.put("Poppy", "en-GB");
        _voiceMap.put("Obadiah", "en-GB");
        _voiceMap.put("Spike", "en-GB");
        _voiceMap.put("Gundula", "de");
        _voiceMap.put("Walter", "de");
        _selectedVoice = DEFAULT_VOICE;
    }

    public void sendSpeaking()
    {
        try {
            Document document = XMLTool.newDocument(EMMA.E_EMMA, EMMA.namespaceURI, EMMA.version);
            Element interpretation = XMLTool.appendChildElement(document.getDocumentElement(), EMMA.E_INTERPRETATION);
            Element behaviour = XMLTool.appendChildElement(interpretation, SemaineML.E_BEHAVIOUR, SemaineML.namespaceURI);
            behaviour.setAttribute(SemaineML.A_NAME, "speaking");
            _ttsSender.sendXML(document, meta.getTime(), Event.single);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendSilent()
    {
        try {
            Document document = XMLTool.newDocument(EMMA.E_EMMA, EMMA.namespaceURI, EMMA.version);
            Element interpretation = XMLTool.appendChildElement(document.getDocumentElement(), EMMA.E_INTERPRETATION);
            Element behaviour = XMLTool.appendChildElement(interpretation, SemaineML.E_BEHAVIOUR, SemaineML.namespaceURI);
            behaviour.setAttribute(SemaineML.A_NAME, "silent");
            _ttsSender.sendXML(document, meta.getTime(), Event.single);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private ContextStateInfo constructContextStateInfo(String currentCharacter)
    {
        Map<String, String> info = new HashMap<String, String>();
        info.put("character", currentCharacter);
        ContextStateInfo context = new ContextStateInfo(info);
        return context;
    }
}
