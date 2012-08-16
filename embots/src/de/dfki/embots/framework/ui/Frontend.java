/**
 * Copyright (C) 2009-10 DKFI-EMBOTS
 */
package de.dfki.embots.framework.ui;

import de.dfki.embots.behaviorbuilder.BehaviorBuilder;
import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.framework.EMBOTSConstants;
import de.dfki.embots.bml.lex.BehaviorLexicon;
import de.dfki.embots.bml.reader.BMLReader;
import de.dfki.embots.bml.reader.BMLReaderException;
import de.dfki.embots.embrscript.EMBRScriptReader;
import de.dfki.embots.framework.ui.signlang.SigningFrontend;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import eu.semaine.components.Component;
import eu.semaine.datatypes.stateinfo.ContextStateInfo;
import eu.semaine.datatypes.stateinfo.StateInfo;
import eu.semaine.jms.message.SEMAINEMessage;
import eu.semaine.jms.receiver.Receiver;
import eu.semaine.jms.sender.Sender;
import eu.semaine.jms.sender.StateSender;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * EMBOTS front-end. User can input BML code for controlling EMBR. Files
 * in the data/bml directory can be selected. Behaviors can be inserted,
 * supported by a behavior specification dialog.
 * 
 * @author Michael Kipp
 */
public class Frontend extends Component implements ActionListener
{

    public static final String NAME = "GUI";
    protected static final String VERSION = "0.9.6";

    private static final int DEFAULT_HEIGHT = 500, DEFAULT_WIDTH = 700;
    private static final int FEEDBACK_WIN_X = 700, FEEDBACK_WIN_Y = 0;
    protected static final String SEND_COMMAND = "send";
    protected static final String START_BEHAVIOR_BUILDER = "start BB";
    protected static final String FEEDBACK_WIN = "feedback window";
    protected static final String START_SIGN_LANGUAGE = "START SL";
    protected static final String RELOAD_LEXICON = "reload lexicon";
    protected static final String VOICE_SELECT_COMMAND = "voiceselect";
    protected static final String INSERT_GESTURE_COMMAND = "insertgesture";
    protected static final String INSERT_HEAD_COMMAND = "inserthead";
    protected static final String INSERT_FACE_COMMAND = "insertface";
    protected static final String INSERT_SYNC_POINT_COMMAND = "insertsync";
    protected static final String DEFAULT_VOICE = "Prudence";
    private final static String SYNC_POINT_PREFIX = "sp";
    private static int SYNC_POINT_COUNT = 1;
    protected static String[] VOICES = {"Poppy", "Prudence", "Obadiah", "Spike",
        "Gundula", "Walter"};
    private FrontendView _frontendView;
    private Sender _bmlFileSender;
    private Sender _embrSender;
    private Sender _reloadLexiconSender;
    private StateSender _contextSender;
    private String _selectedVoice;
    private Map<String, String> _voiceMap;
    private Random r = new Random();
    private BehaviorLexicon _lexicon;
    private FrontendFeedbackView _feedbackView;

    /**
     * Creates a new GUI (frontend view) and acts as a controller to this view.
     *
     * @throws JMSException
     */
    public Frontend() throws JMSException
    {
        super(NAME, true, false);
        initVoiceMap();
        initSenders();
        initReceivers();
        initView();
        initFeedbackView();
        loadLexicon(false);
    }

    private void initView()
    {
        _frontendView = new FrontendView(this);
        _frontendView.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        _frontendView.pack();
        _frontendView.setVisible(true);
    }

    private void initFeedbackView()
    {
        _feedbackView = new FrontendFeedbackView();
        _feedbackView.setBounds(FEEDBACK_WIN_X, FEEDBACK_WIN_Y,
                _feedbackView.getWidth(), _feedbackView.getHeight());
//        _feedbackView.setVisible(true);
    }

    private void initSenders() throws JMSException
    {
        // sender for dispaching input BML
        _bmlFileSender = new Sender(EMBOTSConstants.BML_INPUT_TYPE, "String", NAME);

        // sender for managing voice change
        _contextSender = new StateSender(EMBOTSConstants.CONTEXT_TYPE,
                StateInfo.Type.ContextState, getName());

        // sender to signal lexicon reload
        _reloadLexiconSender = new Sender(EMBOTSConstants.RELOAD_TYPE, "String", NAME);

        // sender for sign language
        _embrSender = new Sender(EMBOTSConstants.EMBRSCRIPT_TYPE, "String", NAME);

        senders.add(_bmlFileSender);
        senders.add(_contextSender);
        senders.add(_reloadLexiconSender);
        senders.add(_embrSender);
    }

    private void initReceivers() throws JMSException
    {
        receivers.add(new Receiver(EMBOTSConstants.BML_FEEDBACK_TYPE));
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

    @Override
    public void react(SEMAINEMessage m) throws Exception
    {
        if (m.getTopicName().equals(EMBOTSConstants.BML_FEEDBACK_TYPE)) {
//            log.info("Received BML feedback: \n" + m.getText());
            if (_feedbackView != null) {
                _feedbackView.addMessage(m.getText());
            }
        }
    }

    /**
     * Loads lexicon and signals this to gesture generator module to do the same.
     */
    private void loadLexicon(boolean isReload)
    {
        try {
            EMBRScriptReader rd1 = new EMBRScriptReader();
            log.info("Reading lexicon from " + EMBOTSConstants.EMBRSCRIPT_LEXICON_DIR);
            _lexicon = rd1.readLexicon(new File(EMBOTSConstants.EMBRSCRIPT_LEXICON_DIR));
            _frontendView.setStatusText("Lexicon: " + EMBOTSConstants.EMBRSCRIPT_LEXICON_DIR
                    + " [" + _lexicon.size() + " entries]");
            if (isReload) {
                _reloadLexiconSender.sendTextMessage("RELOAD", meta.getTime());
            }
        } catch (IOException ex) {
            log.warn("Could not read LEXICON. " + ex.getMessage());
        } catch (Exception e) {
            log.warn("Could not send RELOAD message. " + e.getMessage());
        }
    }

    private ContextStateInfo constructContextStateInfo(String currentCharacter)
    {
        Map<String, String> info = new HashMap<String, String>();
        info.put("character", currentCharacter);
        ContextStateInfo context = new ContextStateInfo(info);
        return context;
    }

    /**
     * Parses current text as BML.
     *
     * @return BML block
     */
    private BMLBlock parseBML()
    {
        try {
            BMLReader reader = new BMLReader();
            return reader.getBMLBlock(_frontendView.getText());
        } catch (BMLReaderException ex) {
            log.error("Couldn't parse BML! " + ex.getMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return null;
    }

    /**
     * Called when user presses "SEND". Sends BML file to gesture generator.
     * Sends text in the speech block to the speech synthesis component.
     */
    private void sendAction()
    {
        try {
            log.info("User pressed SEND");

            // Send input BML to BML realizer
            _bmlFileSender.sendTextMessage(_frontendView.getText(), meta.getTime());
        } catch (JMSException ex) {
            Logger.getLogger(Frontend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sends the given utterance to the userstate using a BMLMessage
     * @param line
     */
//    public void sendUtterance(String line)
//    {
//        /* If speech signals are not continuously send, send a speaking signal to indicate that an utterance has been spoken */
//        sendSpeaking();
//        String id = "s1";
//        Document doc = XMLTool.newDocument("fml-apml", null, FML.version);
//        Element root = doc.getDocumentElement();
//
//        Element bml = XMLTool.appendChildElement(root, BML.E_BML, BML.namespaceURI);
//        bml.setAttribute(BML.A_ID, "bml1");
//        Element fml = XMLTool.appendChildElement(root, FML.E_FML, FML.namespaceURI);
//        fml.setAttribute(FML.A_ID, "fml1");
//        Element speech = XMLTool.appendChildElement(bml, BML.E_SPEECH);
//        speech.setAttribute(BML.A_ID, id);
//        speech.setAttribute(BML.E_TEXT, line);
//        speech.setAttribute(BML.E_LANGUAGE, _voiceMap.get(_selectedVoice));
//
//        int counter = 1;
//        for (String word : line.split(" ")) {
//            Element mark = XMLTool.appendChildElement(speech, SSML.E_MARK, SSML.namespaceURI);
//            mark.setAttribute(SSML.A_NAME, id + ":tm" + counter);
//            Node text = doc.createTextNode(word);
//            speech.appendChild(text);
//            counter++;
//        }
//        Element mark = XMLTool.appendChildElement(speech, SSML.E_MARK, SSML.namespaceURI);
//        mark.setAttribute(SSML.A_NAME, id + ":tm" + counter);
//        try {
//            _fmlSender.sendXML(doc, meta.getTime(), Event.single);
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
//
//        /* If speech signals are not continuously send, send a silence signal to indicate that the utterance is over */
//        sendSilent();
//    }
//    public void sendSpeaking()
//    {
//        try {
//            Document document = XMLTool.newDocument(EMMA.E_EMMA, EMMA.namespaceURI, EMMA.version);
//            Element interpretation = XMLTool.appendChildElement(document.getDocumentElement(), EMMA.E_INTERPRETATION);
//            Element behaviour = XMLTool.appendChildElement(interpretation, SemaineML.E_BEHAVIOUR, SemaineML.namespaceURI);
//            behaviour.setAttribute(SemaineML.A_NAME, "speaking");
//            _fmlSender.sendXML(document, meta.getTime(), Event.single);
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void sendSilent()
//    {
//        try {
//            Document document = XMLTool.newDocument(EMMA.E_EMMA, EMMA.namespaceURI, EMMA.version);
//            Element interpretation = XMLTool.appendChildElement(document.getDocumentElement(), EMMA.E_INTERPRETATION);
//            Element behaviour = XMLTool.appendChildElement(interpretation, SemaineML.E_BEHAVIOUR, SemaineML.namespaceURI);
//            behaviour.setAttribute(SemaineML.A_NAME, "silent");
//            _fmlSender.sendXML(document, meta.getTime(), Event.single);
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
//    }
    public static String syncPointID()
    {
        return SYNC_POINT_PREFIX + SYNC_POINT_COUNT++;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();
        if (cmd.equals(SEND_COMMAND)) {
            sendAction();
        } else if (cmd.equals(VOICE_SELECT_COMMAND)) {

            try {
                _selectedVoice = _frontendView.getSelectedVoice();
                ContextStateInfo context = constructContextStateInfo(_selectedVoice);
                _contextSender.sendStateInfo(context, meta.getTime());
            } catch (JMSException ex) {
                Logger.getLogger(Frontend.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (cmd.equals(INSERT_GESTURE_COMMAND)) {
            String b = InsertGestureDialog.showDialog(_frontendView, parseBML(), _lexicon);
            _frontendView.insert(b);
            _frontendView.focusText();
        } else if (cmd.equals(INSERT_FACE_COMMAND)) {
//            String b = InsertGestureDialog.showDialog(_frontendView, parseBML(), _lexicon);
//            _frontendView.insert(b);
//            _frontendView.focusText();
        } else if (cmd.equals(INSERT_HEAD_COMMAND)) {
//            String b = InsertGestureDialog.showDialog(_frontendView, parseBML(), _lexicon);
//            _frontendView.insert(b);
//            _frontendView.focusText();
        } else if (cmd.equals(INSERT_SYNC_POINT_COMMAND)) {
            _frontendView.insert("<sync id=\"" + syncPointID() + "\"/>");
            _frontendView.focusText();
        } else if (cmd.equals(START_BEHAVIOR_BUILDER)) {
            _frontendView.setAlwaysOnTop(false);
            new BehaviorBuilder();
            _frontendView.setBBButton(false);
        } else if (cmd.equals(START_SIGN_LANGUAGE)) {
            _frontendView.setAlwaysOnTop(false);
            SigningFrontend sf = new SigningFrontend(_embrSender, log);
            sf.setVisible(true);
        } else if (cmd.equals(RELOAD_LEXICON)) {
            loadLexicon(true);
        } else if (cmd.equals(FEEDBACK_WIN)) {
            toggleFeedbackWin();
        }
    }

    private void toggleFeedbackWin()
    {
        if (_feedbackView == null) {
            _feedbackView = new FrontendFeedbackView();
            _feedbackView.setVisible(true);
        } else {
            _feedbackView.setVisible(!_feedbackView.isVisible());
        }
    }

    /**
     * For testing.
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        System.out.println("*** Frontend...");
        try {
            EMBRScriptReader rd1 = new EMBRScriptReader();
            BehaviorLexicon lexicon = rd1.readLexicon(new File(EMBOTSConstants.EMBRSCRIPT_LEXICON_DIR));
            System.out.println("Lexicon loaded: " + lexicon.size() + " lexemes");
            String lexeme = "dismiss_LH";

            System.out.println("lexeme " + lexeme + " = " + lexicon.getLexeme(lexeme));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
