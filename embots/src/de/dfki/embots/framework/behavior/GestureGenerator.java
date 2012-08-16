/**
 * Copyright (C) 2008 DFKI GmbH. All rights reserved.
 * Use is subject to license terms -- see license.txt.
 */
package de.dfki.embots.framework.behavior;

import de.dfki.embots.bml.reader.BMLReaderException;
import de.dfki.embots.framework.EMBOTSConstants;
import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.bml.solver.BMLConstraintSolver;
import de.dfki.embots.bml.behavior.BMLBehavior;
import de.dfki.embots.bml.behavior.BMLFaceBehavior;
import de.dfki.embots.bml.behavior.BMLGestureBehavior;
import de.dfki.embots.bml.behavior.BMLHeadBehavior;
import de.dfki.embots.bml.behavior.BMLSpeechBehavior;
import de.dfki.embots.bml.exception.BMLBehaviorException;
import de.dfki.embots.bml.exception.BMLException;
import de.dfki.embots.bml.feedback.BMLFeedback;
import de.dfki.embots.bml.lex.BehaviorLexicon;
import de.dfki.embots.bml.reader.BMLReader;
import de.dfki.embots.bml.translate.BMLToEMBRScript;
import de.dfki.embots.bml.translate.FaceMapping;
import de.dfki.embots.embrscript.EMBRElement;
import de.dfki.embots.embrscript.EMBRMorphTargetConstraint;
import de.dfki.embots.embrscript.EMBRPose;
import de.dfki.embots.embrscript.EMBRPoseSequence;
import de.dfki.embots.embrscript.EMBRScript;
import de.dfki.embots.embrscript.EMBRScriptReader;
import de.dfki.embots.framework.translate.BMLMARYFusion;
import de.dfki.embots.framework.ui.ModuleErrorDialog;
import java.awt.HeadlessException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import java.io.IOException;
import java.io.BufferedWriter;
import eu.semaine.components.Component;
import eu.semaine.jms.message.SEMAINEMessage;
import eu.semaine.jms.message.SEMAINEXMLMessage;
import eu.semaine.jms.receiver.BMLReceiver;
import eu.semaine.jms.receiver.Receiver;
import eu.semaine.jms.receiver.XMLReceiver;
import eu.semaine.jms.sender.Sender;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import org.jdom.JDOMException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This modules translates the gesture and face parts of BML to EMBRScript.
 *
 * It involves (1) resolution of time constraints and (2) insertion of modified
 * lexemes from the EMBRScript lexicon.
 *
 * Note that in the BML, a speech sync tag must not occur *after* a punctuation
 * sign.
 *
 * TODO: quite urgent is a mechanism to buffer incoming BML blocks when
 *       in the PLAYING state.
 *
 * @see BMLToEMBRScript
 *
 * @author Michael Kipp
 *
 */
public class GestureGenerator extends Component
{

    private static final String NAME = "Gesture Generator";
    private static final String LAST_EMBR_COMMAND_FILE = "last_embrscript.embr";
    private static final String INITAL_POSE_FILE = "initialPose.embr";
    private static final File TEMP_DIR = new File("../temp");
    private VisemeGenerator _visemeGenerator;
    private EMBRScript _embrScript;
    private BMLBlock _bml;
    private Sender _playSender;
    private Sender _embrSender;
    private Sender _bmlFeedbackSender;
    private BehaviorLexicon _lexicon;
    private FaceMapping _faceMapping;
    private List<SpeechBlockData> _speechDataList = new ArrayList<SpeechBlockData>();
    private MyState _state = MyState.INITIAL;
    private int _numberOfRemainingSpeechBlocks = 0;

    /**
     * State information necessary to synchronize audio ready signal with
     * speech processing.
     */
    private enum MyState
    {

        INITIAL, SPEECH_BLOCKS_PROCESSED, AUDIO_READY, PLAYING;
    }

    /**
     * Store this data from MARY for a single speech block.
     */
    private class SpeechBlockData
    {

        BMLSpeechBehavior bmlSpeechBehavior;
        EMBRPoseSequence visemeScript;
        double[] wordTimings;
        long offset = 0;
        boolean ok = false;

        private SpeechBlockData(BMLSpeechBehavior b)
        {
            bmlSpeechBehavior = b;
        }
    }

    /**
     * @throws JMSException
     */
    public GestureGenerator() throws JMSException
    {
        super(NAME);

        // receivers
        receivers.add(new BMLReceiver(EMBOTSConstants.SYNTHESIS_READY_TYPE));
        receivers.add(new Receiver(EMBOTSConstants.AUDIO_READY_TYPE));
        receivers.add(new Receiver(EMBOTSConstants.BML_INTERNAL_TYPE));
        receivers.add(new Receiver(EMBOTSConstants.RELOAD_TYPE));
        receivers.add(new XMLReceiver(EMBOTSConstants.AUDIO_CALLBACK_TYPE));

        // senders
        _playSender = new Sender(EMBOTSConstants.AUDIO_PLAY_TYPE, "String", NAME);
        _embrSender = new Sender(EMBOTSConstants.EMBRSCRIPT_TYPE, "String", NAME);
        _bmlFeedbackSender = new Sender(EMBOTSConstants.BML_FEEDBACK_TYPE, "String", NAME);
        senders.add(_playSender);
        senders.add(_embrSender);
        senders.add(_bmlFeedbackSender);
        _visemeGenerator = new VisemeGenerator();
        _embrScript = new EMBRScript();


    }

    private void generateAndPlay() throws JMSException, InterruptedException
    {
        setState(MyState.PLAYING);
        long offset = generateGestures();
        playBehaviors(offset);
    }

    private void reset()
    {
        _numberOfRemainingSpeechBlocks = 0;
        setState(MyState.INITIAL);
    }

    /**
     * Use this method to set the state instead of setting it directly.
     *
     * @param newState Next state.
     */
    private void setState(MyState newState)
    {
        log.info("State changed: " + newState.name());
        _state = newState;
    }

    @Override
    protected void customStartIO() throws Exception
    {
        // initialize face mapping
        try {
            _faceMapping = new FaceMapping();
            log.info("Loaded face mapping [" + _faceMapping.size() + " entries]");
        } catch (IOException ex) {
            log.error("Could not load face mapping!");
        }
        try {
            readLexicon(false);
        } catch (IOException ex) {
            Logger.getLogger(GestureGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Main coordination point in this module. Incoming messages need to be
     * coordinated to generate the output EMBRScripts and signals to the
     * audio player.
     *
     * @param m SEMAINE message
     * @throws Exception
     */

    @Override
    public void react(SEMAINEMessage m) throws Exception
    {
        try {
            if (m.getTopicName().equals(EMBOTSConstants.BML_INTERNAL_TYPE)) {

                log.info("[GG INPUT] BML");

                // Should happen first: get BML file from front-end
                _bml = receiveBMLInput(m.getText());
                prepareSpeechData();

            } else if (m.getTopicName().equals(EMBOTSConstants.SYNTHESIS_READY_TYPE)) {

                // Should happen second: get phoneme data from MARY

                processPhonemeInput(m);
                if (checkIfSpeechComplete()) {
                    processSpeechBlocks();
                    if (_state == MyState.AUDIO_READY) {
                        generateAndPlay();
                    } else {
                        setState(MyState.SPEECH_BLOCKS_PROCESSED);
                    }
                }

            } else if (m.getTopicName().equals(EMBOTSConstants.AUDIO_READY_TYPE)) {

                log.info("[GG INPUT] Audio ready");

                // Should happen third: Get signal that audio player is ready to play
                // => solve constraints, generate behaviors and send them to EMBR
                // Resolve time constraints and generate EMBRScript

                if (_state == MyState.SPEECH_BLOCKS_PROCESSED) {
                    generateAndPlay();
                } else {
                    if (_state == MyState.PLAYING) {
                        if (_numberOfRemainingSpeechBlocks > 0) {
                            // this one was probably not triggered by the play method
                            // so fire off as quickly as possible
//                            log.info("Send audio play signal (react method)");
//                            _playSender.sendTextMessage("PLAY AUDIO", meta.getTime());
                        }
                    } else {
                        setState(MyState.AUDIO_READY);
                    }
                }

            } else if (m.getTopicName().equals(EMBOTSConstants.AUDIO_CALLBACK_TYPE)) {

                log.info("[GG INPUT] Audio feedback");
                // Feedback from the audio module
                processSpeakingFeedback(m);

            } else if (m.getTopicName().equals(EMBOTSConstants.RELOAD_TYPE)) {
                // signals that user has reloaded the behavior lexicon
                readLexicon(true);
                reset();
            }
        } catch (RuntimeException e) {
            ModuleErrorDialog.show(NAME, e.getStackTrace());
            log.error(NAME + " failed! " + e.getMessage());
            _embrScript = new EMBRScript();
            _bml = null;
            e.printStackTrace();
        }
    }

    /**
     * Inserts the absolute time for the speech block sync points. Requires
     * that the phoneme timings for all speech blocks have been sent by
     * the TTS module.
     */
    private void processSpeechBlocks()
    {
        // compute offsets
        long time = 0;
        for (SpeechBlockData dat : _speechDataList) {
            dat.offset = time + dat.bmlSpeechBehavior.getWait();

            // move viseme script
            dat.visemeScript.offset(dat.offset);

            // assign character
            log.debug("agent for embrscript (before)=" + dat.visemeScript.character);
            dat.visemeScript.character = dat.bmlSpeechBehavior.getAgentTrans(EMBOTSConstants.EMBR_CHARACTER);
            log.debug("agent for embrscript (after)=" + dat.visemeScript.character);

            // add viseme animation to embrscript
            _embrScript.addElement(dat.visemeScript);
            log.info("Inserted visemes to EMBRScript");

            // put in sync point times
            BMLMARYFusion.completeSpeechSyncPoints(dat.bmlSpeechBehavior,
                    dat.wordTimings, dat.offset / 1000d);
            log.info("Inserted speech sync point timings");

            // update current time
            time += dat.bmlSpeechBehavior.getWait()
                    + (long) (dat.wordTimings[dat.wordTimings.length - 1] * 1000);
        }
    }

    private boolean checkIfSpeechComplete()
    {
        for (SpeechBlockData dat : _speechDataList) {
            if (!dat.ok) {
                return false;
            }
        }
        log.info("Received all " + _speechDataList.size() + " speech block(s).");
        return true;
    }

    private void prepareSpeechData()
    {
        _speechDataList.clear();
        for (BMLBehavior beh : _bml.getBehaviors()) {
            if (beh instanceof BMLSpeechBehavior) {
                _speechDataList.add(new SpeechBlockData((BMLSpeechBehavior) beh));
                log.info("found speech " + beh.getID() + " " + beh.getAgentTrans("foo"));
            }
        }
        _numberOfRemainingSpeechBlocks = _speechDataList.size();
    }

    /**
     * Currently not used. You can react to the fact that EMBR finished speaking
     * here.
     */
    private void processSpeakingFeedback(SEMAINEMessage m) throws DOMException, JMSException
    {
        Document d = ((SEMAINEXMLMessage) m).getDocument();
        Node eventNode = d.getElementsByTagName("event").item(0);
        String eventType = eventNode.getAttributes().getNamedItem("type").getNodeValue();
        if ("start".equals(eventType)) {
            BMLFeedback fb = new BMLFeedback(BMLFeedback.Type.STATUS,
                    BMLFeedback.Subtype.STARTED, NAME,
                    _bml.getID());
            _bmlFeedbackSender.sendTextMessage(fb.toXML(), meta.getTime());
            log.info("Received audio START signal for block " + _bml.getID()
                    + " - " + _numberOfRemainingSpeechBlocks + " speech blocks pending.");
        } else if ("end".equals(eventType)) {

            BMLFeedback fb = new BMLFeedback(BMLFeedback.Type.STATUS,
                    BMLFeedback.Subtype.FINISHED, NAME,
                    _bml.getID());
            _bmlFeedbackSender.sendTextMessage(fb.toXML(), meta.getTime());

            // count back remaining speech segments
            _numberOfRemainingSpeechBlocks--;

            log.info("Received audio END signal for block " + _bml.getID()
                    + " - " + _numberOfRemainingSpeechBlocks + " speech blocks pending.");

            if (_numberOfRemainingSpeechBlocks <= 0) {
                setState(MyState.INITIAL);
            }
        }
    }

    /**
     * Sends EMBRScript to animation engine and sends trigger to audio module
     * for playing the voice track(s).
     *
     * @param offset A global time offset milliseconds
     * 
     * @throws InterruptedException
     * @throws JMSException
     */
    private void playBehaviors(long offset) throws InterruptedException, JMSException
    {
        log.info("Sending EMBRScript to EMBR: offset = " + offset + " ms");

        // send final script to EMBR
        sendToEMBR();

        // wait global offset (if positive)
        if (offset > 0) {
            sleep(offset);
        }

        log.info("Ready to play speech audio");

        // play speech blocks
        for (SpeechBlockData dat : _speechDataList) {
            log.info("Waiting for speech block "
                    + dat.bmlSpeechBehavior.getID() + " (" + dat.offset + " ms).");

            if (dat.offset > 0) {
                sleep(dat.offset);
            }
            
            log.info("Sending play signal for speech block " + dat.bmlSpeechBehavior.getID());
            _playSender.sendTextMessage("PLAY AUDIO", meta.getTime());
        }
    }

    /**
     * Does two things: (1) create viseme script, (2) fill in sync point times
     * in the BML speech block.
     *
     * @param m Message containing phoneme timings
     * 
     * @throws HeadlessException
     * @throws Exception
     */
    private void processPhonemeInput(SEMAINEMessage m) throws HeadlessException, Exception
    {
        SEMAINEXMLMessage xm = (SEMAINEXMLMessage) m;

        // extract ID
        Node speechNode = xm.getDocument().getElementsByTagName("bml:speech").item(0);
        NamedNodeMap speechAttributes = speechNode.getAttributes();
        String id = (speechAttributes.getNamedItem("id")).getNodeValue();

        SpeechBlockData dat = null;
        for (SpeechBlockData d : _speechDataList) {
            if (d.bmlSpeechBehavior.getID().equals(id)) {
                dat = d;
            }
        }

        if (dat == null) {
            log.warn("Cannot process speech block, unknown id \"" + id + "\".");
        } else {
            log.info("Received MARY phoneme timings for speech block \"" + id + "\".");

            // Create viseme animation script
            dat.visemeScript = _visemeGenerator.createEMBRScript(m, EMBOTSConstants.EMBR_CHARACTER);
            dat.wordTimings = BMLMARYFusion.extractWordTimings(xm.getDocument());
            dat.ok = true;

            //EMBRPoseSequence visemescript = _visemeGenerator.createEMBRScript(m, EMBOTSConstants.EMBR_CHARACTER);

//        _embrScript.addElement(visemescript);

            // Fill speech sync points based on word timings
//            if (_bml == null || _embrScript.getElements().size() > 0) {
//                BMLSpeechBehavior beh = BMLMARYFusion.completeSpeechSyncPoints(_bml, xm.getDocument());
//                if (beh != null) {
//                    visemescript.character = beh.getAgentTrans(EMBOTSConstants.EMBR_CHARACTER);
//                }
//            } else {
//                JOptionPane.showMessageDialog(null, "Audio player was ready before BML script was parsed!" + "[bml null " + (_bml == null) + ", embr: " + _embrScript.getElements().size() + "]");
//            }
        }
    }

    private BMLBlock receiveBMLInput(String bmlText)
            throws IOException, BMLReaderException, SAXException, JDOMException
    {
//        System.out.println("#### GG: received BML: " + bmlText);
        log.debug("Trying to read with lexicon: " + _lexicon.size());
        BMLReader rd2 = new BMLReader(_lexicon);
        BMLBlock bml = rd2.getBMLBlock(bmlText);
        log.debug("Successfully parsed BML");
        _embrScript.clear();
        readInitialPose();
        return bml;
    }

    private void readLexicon(boolean sendFeedback) throws IOException
    {
        EMBRScriptReader rd1 = new EMBRScriptReader();
        _lexicon = rd1.readLexicon(new File(EMBOTSConstants.EMBRSCRIPT_LEXICON_DIR));

        // print all lexemes
        log.info("Loaded lexicon [" + _lexicon.getLexemes().size() + " entries]");

        // send status message
        if (sendFeedback) {
            BMLFeedback fb = new BMLFeedback(BMLFeedback.Type.STATUS, NAME, "Reloaded lexicon");
            try {
                _bmlFeedbackSender.sendTextMessage(fb.toXML(), meta.getTime());
            } catch (JMSException ex) {
                Logger.getLogger(GestureGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Adds gestures to the EMBRScript, moving them to the correct spot on the
     * timeline, given the constraints.
     */
    private long generateGestures()
    {
        // Solve timing constraints
        BMLConstraintSolver solver = new BMLConstraintSolver(_bml);

        // In case of success the resulting BML should now be in BMLS state (solved!)
//        log.info("**** BMLS BEFORE ****\n" + _bml.toXML());
        boolean success = solver.solveTiming();
        log.info("Constraint solving: " + (success ? "OK" : "FAILURE"));
//        solver = null;
//        System.gc();

        BMLFeedback fbConstraints = success
                ? new BMLFeedback(BMLFeedback.Type.STATUS, NAME, _bml.getID(), "Temporal constraints successfully solved")
                : new BMLFeedback(BMLFeedback.Type.ERROR, NAME, _bml.getID(), "Failed to solve temporal constraints");
        try {
            _bmlFeedbackSender.sendTextMessage(fbConstraints.toXML(), meta.getTime());

        } catch (JMSException ex) {
            Logger.getLogger(GestureGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        //        log.info("**** BMLS AFTER RESOLVING GESTURES ****\n" + _bml.toXML());
//        log.info("**** BMLS AFTER RESOLVING GESTURES ****\n" + _bml.toXML());

        // Pass on warnings
        try {
            for (BMLException ex : _bml.getExceptions()) {
                BMLFeedback fb =
                        ex instanceof BMLBehaviorException
                        ? new BMLFeedback(BMLFeedback.Type.WARNING, NAME, ((BMLBehaviorException) ex).getBehaviorID(), ex.getMessage())
                        : new BMLFeedback(BMLFeedback.Type.WARNING, NAME, _bml.getID(), ex.getMessage());
                _bmlFeedbackSender.sendTextMessage(fb.toXML(), meta.getTime());
            }
        } catch (JMSException je) {
            je.printStackTrace();
        }


        // In this loop, behaviors are instanciated
        for (BMLBehavior beh : _bml.getBehaviors()) {

            if (beh instanceof BMLGestureBehavior || beh instanceof BMLHeadBehavior) {

                // only consider gestures with a lexeme
                if (beh.getLexeme() != null) {

                    // add EMBRScript
                    _embrScript.addElement(BMLToEMBRScript.toEMBRScript(beh));
                    log.info("Inserted EMBRScript (behavior): " + beh.getLexemeName());

                    // BML feedback
                    try {
                        _bmlFeedbackSender.sendTextMessage(new BMLFeedback(BMLFeedback.Type.STATUS, NAME, beh.getID(),
                                "Solved: " + beh.toScript()).toXML(), meta.getTime());
                    } catch (JMSException ex) {
                        Logger.getLogger(GestureGenerator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {

                    // do nothing if no lexeme present (just feedback)
                    try {
                        _bmlFeedbackSender.sendTextMessage(new BMLFeedback(BMLFeedback.Type.ERROR, NAME,
                                beh.getID(), beh.getBMLTag() + " lexeme (EMBRScript) not found").toXML(), meta.getTime());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }

            } else if (beh instanceof BMLFaceBehavior) {
                List<EMBRMorphTargetConstraint> cons =
                        _faceMapping.getMorphConstraints(((BMLFaceBehavior) beh).getFaceType());
                if (cons.size() > 0) {
                    String agent = beh.getAgentTrans(EMBOTSConstants.EMBR_CHARACTER);
                    EMBRPoseSequence facial =
                            FacialExpressionGenerator.createEMBRScript(agent,
                            (BMLFaceBehavior) beh, cons);
                    _embrScript.addElement(facial);
                    log.info("Inserted EMBRScript (face): " + beh.getLexemeName());

                    try {
                        _bmlFeedbackSender.sendTextMessage(new BMLFeedback(BMLFeedback.Type.STATUS,
                                NAME, beh.getID(), "Solved: " + beh.toScript()).toXML(), meta.getTime());
                    } catch (JMSException ex) {
                        Logger.getLogger(GestureGenerator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        _bmlFeedbackSender.sendTextMessage(new BMLFeedback(BMLFeedback.Type.ERROR, NAME,
                                beh.getID(), "No temporal constraints found: dropping behavior").toXML(), meta.getTime());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Find smallest start time for the computation of overall offset
        long start = 0;
        for (EMBRElement el : _embrScript.getElements()) {
            if (el instanceof EMBRPoseSequence) {

                // sequence start
                if (((EMBRPoseSequence) el).startTime < start) {
                    start = ((EMBRPoseSequence) el).startTime;
                }
                // first pose
                EMBRPose p = ((EMBRPoseSequence) el).getPoses().iterator().next();
                if (p.getTime() < start) {
                    start = p.getTime();
                }
            }
        }

        // Apply offset if earliest start time is negative
        if (start < 0) {
            _embrScript.offset(-start);

            try {
                _bmlFeedbackSender.sendTextMessage(new BMLFeedback(BMLFeedback.Type.STATUS, NAME, _bml.getID(),
                        "Applying temporal offset of " + -start + " ms").toXML(), meta.getTime());
            } catch (JMSException ex) {
                Logger.getLogger(GestureGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }

            return -start;
        }

        return 0;
    }

    private void readInitialPose() throws IOException
    {
        EMBRScriptReader reader = new EMBRScriptReader();
        File poseFile = new File(EMBOTSConstants.EMBRSCRIPT_LEXICON_DIR + "/"
                + INITAL_POSE_FILE);
        List<EMBRPoseSequence> list = reader.readLexemesToList(poseFile);
        _embrScript.addAllElements(list);
    }

    /**
     * Sends current script to EMBR and clears current script.
     */
    private void sendToEMBR()
    {
        sendToEMBR(true);
    }

    /**
     * Sends current script to EMBR
     * 
     * @param absolute true = clears current script
     */
    private void sendToEMBR(boolean absolute)
    {

        log.info("EMBR message dispatched " + ((absolute) ? "(absolute)" : "(relative)"));
        try {
            //String script = (absolute) ? _embrScript.toScript() : _embrScript.toRelScript();
            String script = _embrScript.createScript(absolute);

            // send to EMBR component
            try {
                _embrSender.sendTextMessage(script, meta.getTime());
            } catch (JMSException ex) {
                Logger.getLogger(GestureGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }

            // also write to file for debugging
            if (!TEMP_DIR.exists()) {
                TEMP_DIR.mkdir();
            }

            BufferedWriter wr = new BufferedWriter(new FileWriter(new File(TEMP_DIR, LAST_EMBR_COMMAND_FILE)));
            wr.write(script);
            wr.newLine();
            wr.flush();
            wr.close();

            _embrScript.clear();
        } catch (IOException ex) {
            Logger.getLogger(GestureGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
