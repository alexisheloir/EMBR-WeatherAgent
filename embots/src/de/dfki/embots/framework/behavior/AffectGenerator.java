/**
 * Copyright (C) 2008 DFKI GmbH. All rights reserved.
 * Use is subject to license terms -- see license.txt.
 */
package de.dfki.embots.framework.behavior;

import de.dfki.embots.framework.EMBOTSConstants;
import de.dfki.embots.embrscript.EMBRAutonomousBehaviorConstraint;
import de.dfki.embots.embrscript.EMBRAutonomousBehaviorKey;
import de.dfki.embots.embrscript.EMBRBodyGroup;
import de.dfki.embots.embrscript.EMBRJoint;
import de.dfki.embots.embrscript.EMBRMorphKey;
import de.dfki.embots.embrscript.EMBRMorphTargetConstraint;
import de.dfki.embots.embrscript.EMBROrientationConstraint;
import de.dfki.embots.embrscript.EMBRPose;
import de.dfki.embots.embrscript.EMBRPoseSequence;
import de.dfki.embots.embrscript.EMBRScript;
import de.dfki.embots.embrscript.EMBRShaderConstraint;
import de.dfki.embots.embrscript.EMBRShaderKey;
import de.dfki.embots.embrscript.Triple;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import eu.semaine.components.Component;
import eu.semaine.jms.message.SEMAINEMessage;
import eu.semaine.jms.receiver.Receiver;
import eu.semaine.jms.sender.Sender;

/**
 * Maps ALMA emotion output to EMBR actions and sends them directly to
 * the EMBR connector.
 * 
 * @author Patrick Gebhard
 */
public class AffectGenerator extends Component
{

    private static final String NAME = "Affect Generator";
    private EMBRScript _embrScript;
    private Sender _embrSender;

    // affect output
    enum BROWMOVE
    {

        SYMMETRICAL, ASYMMETRICAL
    }
    double mDefaultBlushingIntensity = 0.0d;
    private double sDEFAULTMORPHINT = 0.0d;
    private final EMBRMorphKey sDEFAULTMORPH = EMBRMorphKey.EXP_SMILE_CLOSED;

    /**
     * @throws JMSException
     */
    public AffectGenerator() throws JMSException
    {
        super(NAME);

        // receivers
        receivers.add(new Receiver(EMBOTSConstants.AFFECT_TYPE));

        // senders
        _embrSender = new Sender(EMBOTSConstants.EMBRSCRIPT_TYPE, "String", NAME);
        senders.add(_embrSender);
        _embrScript = new EMBRScript();
    }

    @Override
    protected void customStartIO() throws Exception
    {
    }

    @Override
    public void react(SEMAINEMessage m) throws Exception
    {
        if (m.getTopicName().equals(EMBOTSConstants.AFFECT_TYPE)) {
            // format of affect is: <emotion/arousal>:<intensity>:{<duration>}
            String affect = m.getText();

            if (affect.contains("arousal")) {
                //log.info("change breathing frequency ...");
                String[] result = affect.split(":");
                String intensityString = result[1];
                _embrScript = new EMBRScript();

                //compute the breathing amplitude
                double intensity = (Double.parseDouble(intensityString));

                // relate the default blushing intensity to the arousal intensity
                mDefaultBlushingIntensity = intensity;

                double amp = (intensity <= 0.0d) ? 0.15d : 0.15d * (0.8 + intensity * 0.75);
                // limit max value
                amp = (amp > 0.18d) ? 0.18d : amp;
                //compute the breathing frequencey
                double freq = (intensity <= 0.0d) ? 0.3d : 0.3d * (intensity * 5.0);
                // limit max freq value
                freq = (freq > 0.9d) ? 0.9d : freq;

                //log.info("breathing frequency: " + freq + " and breathing amplitude: " + amp);

                EMBRAutonomousBehaviorConstraint breathingAmp = new EMBRAutonomousBehaviorConstraint(EMBRAutonomousBehaviorKey.BREATHING_FREQUENCY, freq);
                EMBRAutonomousBehaviorConstraint breathingFreq = new EMBRAutonomousBehaviorConstraint(EMBRAutonomousBehaviorKey.BREATHING_AMPLITUDE, amp);

                EMBRPoseSequence ps = new EMBRPoseSequence();
                ps.setASAP(true);
                ps.character = EMBOTSConstants.EMBR_CHARACTER;
                ps.fadeIn = 100;
                ps.fadeOut = 100;

                // begin morph pose
                EMBRPose p = new EMBRPose();
                p.constraints.add(breathingAmp);
                p.constraints.add(breathingFreq);
                p.relativeTime = true;
                p.offset(400);

                setBlushing(p, intensity);

                // build script
                ps.addPose(p);
                _embrScript.addElement(ps);

                // debug
//                log.info("---");
//                log.info(_embrScript.createScript(false));
//                log.info("---");
                // play
                sendToEMBR(false);
            } else if (affect.contains(":")) { // dirty ;-)

                String[] result = affect.split(":");
                String type = result[0];
                String intensity = result[1];
                String duration = result[2];
                // build script
                _embrScript = new EMBRScript();

                if ((type.equalsIgnoreCase("pride"))
                        || (type.equalsIgnoreCase("gratification"))) {
                    affectivePrideRealization(Double.parseDouble(intensity), Long.parseLong(duration));
                    sendToEMBR(false);
                }
                if ((type.equalsIgnoreCase("joy"))
                        || (type.equalsIgnoreCase("satisfaction"))
                        || (type.equalsIgnoreCase("happyfor"))
                        || (type.equalsIgnoreCase("liking"))) {
                    affectiveSmileRealization(Double.parseDouble(intensity), Long.parseLong(duration));
                    // run it
                    sendToEMBR(false);
                }
                if (type.equalsIgnoreCase("gloating")) {
                    affectiveGloatingRealization(Double.parseDouble(intensity), Long.parseLong(duration));
                    // run it
                    sendToEMBR(false);
                }
                if ((type.equalsIgnoreCase("admiration"))
                        || (type.equalsIgnoreCase("gratitude"))) {
                    affectiveGratitudeRealization(Double.parseDouble(intensity), Long.parseLong(duration));
                    // run it
                    sendToEMBR(false);
                }
                if ((type.equalsIgnoreCase("resentment"))
                        || (type.equalsIgnoreCase("disliking"))) {
                    affectiveDisgustRealization(Double.parseDouble(intensity), Long.parseLong(duration));
                    // run it
                    sendToEMBR(false);
                }
                if ((type.equalsIgnoreCase("distress"))) {
                    affectiveDistressRealization(Double.parseDouble(intensity), Long.parseLong(duration));
                    // run it
                    sendToEMBR(false);
                }
                if ((type.equalsIgnoreCase("reproach"))
                        || (type.equalsIgnoreCase("anger"))) {
                    affectiveAngerRealization(Double.parseDouble(intensity), Long.parseLong(duration));
                    // run it
                    sendToEMBR(false);
                }
                if ((type.equalsIgnoreCase("hate"))) {
                    affectiveHateRealization(Double.parseDouble(intensity), Long.parseLong(duration));
                    // run it
                    sendToEMBR(false);
                }
                if ((type.equalsIgnoreCase("disappointment"))) {
                    affectiveDisappointmentRealization(Double.parseDouble(intensity), Long.parseLong(duration));
                    // run it
                    sendToEMBR(false);
                }
                if ((type.equalsIgnoreCase("hope"))) {
                    affectiveHopeRealization(Double.parseDouble(intensity), Long.parseLong(duration));
                    // run it
                    sendToEMBR(false);
                }
                if ((type.equalsIgnoreCase("relief"))) {
                    affectiveReliefRealization(Double.parseDouble(intensity), Long.parseLong(duration));
                    // run it
                    sendToEMBR(false);
                }
                if ((type.equalsIgnoreCase("shame"))
                        || (type.equalsIgnoreCase("remorse"))) {
                    affectiveShameRealization(Double.parseDouble(intensity), Long.parseLong(duration));
                    // run it
                    sendToEMBR(false);
                }
                if ((type.equalsIgnoreCase("fearsconfirmed"))
                        || (type.equalsIgnoreCase("fear"))) {
                    affectiveFearRealization(Double.parseDouble(intensity), Long.parseLong(duration));
                    // run it
                    sendToEMBR(false);
                }
                if (type.equalsIgnoreCase("pity")) {
                    affectiveSorryForRealization(Double.parseDouble(intensity), Long.parseLong(duration));
                    // run it
                    sendToEMBR(false);
                }

            }
        }
    }

    private void raiseEyeBrowPose(EMBRPose p, Double intensity, BROWMOVE bm)
    {
        // morph constraint
        EMBRMorphTargetConstraint rightRaiseEyeMTC = new EMBRMorphTargetConstraint(EMBRMorphKey.MOD_BROW_UP_RIGHT, (bm == BROWMOVE.ASYMMETRICAL) ? intensity * 1.5 : intensity);
        EMBRMorphTargetConstraint leftRaiseEyeMTC = new EMBRMorphTargetConstraint(EMBRMorphKey.MOD_BROW_UP_LEFT, intensity);

        p.constraints.add(leftRaiseEyeMTC);
        p.constraints.add(rightRaiseEyeMTC);
    }

    private void defaultEyeBrowPose(EMBRPose p)
    {
        // morph constraint
        EMBRMorphTargetConstraint rightDefaultEyeMTC = new EMBRMorphTargetConstraint(EMBRMorphKey.MOD_BROW_UP_RIGHT, 0.0d);
        EMBRMorphTargetConstraint leftDefaultEyeMTC = new EMBRMorphTargetConstraint(EMBRMorphKey.MOD_BROW_UP_LEFT, 0.0d);

        p.constraints.add(rightDefaultEyeMTC);
        p.constraints.add(leftDefaultEyeMTC);
    }

    private void openEyeLid(EMBRPose p, Double intensity)
    {

        double openInt = 1.0 - intensity;

        // morph constraint
        EMBRMorphTargetConstraint rightOpenEyeMTC = new EMBRMorphTargetConstraint(EMBRMorphKey.MOD_SQUINT_RIGHT, (openInt < 0.5) ? openInt : 0.5);
        EMBRMorphTargetConstraint leftOpenEyeMTC = new EMBRMorphTargetConstraint(EMBRMorphKey.MOD_SQUINT_LEFT, (openInt < 0.5) ? openInt : 0.5);

        p.constraints.add(rightOpenEyeMTC);
        p.constraints.add(leftOpenEyeMTC);
    }

    private void closeEyeLid(EMBRPose p, Double intensity)
    {

        // morph constraint
        EMBRMorphTargetConstraint rightOpenEyeMTC = new EMBRMorphTargetConstraint(EMBRMorphKey.MOD_SQUINT_RIGHT, (intensity > 0.5) ? intensity : 0.5);
        EMBRMorphTargetConstraint leftOpenEyeMTC = new EMBRMorphTargetConstraint(EMBRMorphKey.MOD_SQUINT_LEFT, (intensity > 0.5) ? intensity : 0.5);

        p.constraints.add(rightOpenEyeMTC);
        p.constraints.add(leftOpenEyeMTC);
    }

    private void defaultEyeLid(EMBRPose p)
    {

        // morph constraint
        EMBRMorphTargetConstraint rightOpenEyeMTC = new EMBRMorphTargetConstraint(EMBRMorphKey.MOD_SQUINT_RIGHT, 0.5);
        EMBRMorphTargetConstraint leftOpenEyeMTC = new EMBRMorphTargetConstraint(EMBRMorphKey.MOD_SQUINT_LEFT, 0.5);

        p.constraints.add(rightOpenEyeMTC);
        p.constraints.add(leftOpenEyeMTC);
    }

    private void tiltHeadLeft(EMBRPose p)
    {
        Triple t = new Triple(-1.0, 0.0, 0.0);
        EMBROrientationConstraint tiltHeadLeftOC = new EMBROrientationConstraint(EMBRBodyGroup.HEAD_NECK, EMBRJoint.HEADDIR, t);
        p.constraints.add(tiltHeadLeftOC);
    }

    private void tiltHeadRight(EMBRPose p)
    {
        Triple t = new Triple(1.0, 0.0, 0.0);
        EMBROrientationConstraint tiltHeadLeftOC = new EMBROrientationConstraint(EMBRBodyGroup.HEAD_NECK, EMBRJoint.HEADDIR, t);
        p.constraints.add(tiltHeadLeftOC);
    }

    private void tiltHeadDefault(EMBRPose p)
    {
        Triple t = new Triple(0.0, 0.0, 0.0);
        EMBROrientationConstraint tiltHeadLeftOC = new EMBROrientationConstraint(EMBRBodyGroup.HEAD_NECK, EMBRJoint.HEADDIR, t);
        p.constraints.add(tiltHeadLeftOC);
    }

    private void rollHeadFront(EMBRPose p)
    {
        Triple t = new Triple(0.0, -1.0, 0.0);
        EMBROrientationConstraint tiltHeadLeftOC = new EMBROrientationConstraint(EMBRBodyGroup.HEAD_NECK, EMBRJoint.HEADDIR, t);
        p.constraints.add(tiltHeadLeftOC);
    }

    private void rollHeadBack(EMBRPose p)
    {
        Triple t = new Triple(1.0, 1.0, 0.0);
        EMBROrientationConstraint tiltHeadLeftOC = new EMBROrientationConstraint(EMBRBodyGroup.HEAD_NECK, EMBRJoint.HEADDIR, t);
        p.constraints.add(tiltHeadLeftOC);
    }

    private void rollHeadDefault(EMBRPose p)
    {
        Triple t = new Triple(0.0, 0.0, 0.0);
        EMBROrientationConstraint tiltHeadLeftOC = new EMBROrientationConstraint(EMBRBodyGroup.HEAD_NECK, EMBRJoint.HEADDIR, t);
        p.constraints.add(tiltHeadLeftOC);
    }

    private void setBlushing(EMBRPose p, double intensity)
    {
        EMBRShaderConstraint sc = new EMBRShaderConstraint(EMBRShaderKey.BLUSHING, mDefaultBlushingIntensity + intensity);

        p.constraints.add(sc);
    }

    private void setDefaultBlushing(EMBRPose p)
    {
        EMBRShaderConstraint sc = new EMBRShaderConstraint(EMBRShaderKey.BLUSHING, mDefaultBlushingIntensity);

        p.constraints.add(sc);
    }

    private EMBRPoseSequence makePoseSequence()
    {
        EMBRPoseSequence ps = new EMBRPoseSequence();
        ps.setASAP(true);
        ps.character = EMBOTSConstants.EMBR_CHARACTER;
//        ps.startTime = EMBRPoseSequence.ASAP;
        ps.fadeIn = 100;
        ps.fadeOut = 100;

        return ps;
    }

    private void affectivePrideRealization(Double intensity, long duration)
    {

        // start morph constraint
        EMBRMorphTargetConstraint mtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_SMILE_OPEN, intensity);
        // stop morph constraint
        EMBRMorphTargetConstraint smtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_SMILE_OPEN, sDEFAULTMORPHINT);

        EMBRPoseSequence ps = makePoseSequence();

        // begin morph pose
        EMBRPose bp = new EMBRPose();
        bp.constraints.add(mtc);
        bp.relativeTime = true;
        bp.offset(600);

        // stop morph pose
        EMBRPose sp = new EMBRPose();
        sp.constraints.add(smtc);
        sp.relativeTime = true;
        sp.offset(duration + 1600);

        // eyes and brows
        raiseEyeBrowPose(bp, intensity, BROWMOVE.ASYMMETRICAL);
        defaultEyeBrowPose(sp);

        openEyeLid(bp, intensity);
        defaultEyeLid(sp);

        // roll head beack - signalling domiance over the situation
        rollHeadBack(bp);
        rollHeadDefault(sp);

        // build
        ps.addPose(bp);
        ps.addPose(sp);

        _embrScript.addElement(ps);

        // debug
        log.info("---");
        log.info(_embrScript.createScript(false));
        log.info("---");
    }

    private void affectiveSmileRealization(Double intensity, long duration)
    {

        // start morph constraint
        EMBRMorphTargetConstraint mtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_SMILE_OPEN, intensity);
        // stop morph constraint
        EMBRMorphTargetConstraint smtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_SMILE_OPEN, sDEFAULTMORPHINT);

        EMBRPoseSequence ps = makePoseSequence();

        // begin morph pose
        EMBRPose bp = new EMBRPose();
        bp.constraints.add(mtc);
        bp.relativeTime = true;
        bp.offset(600);

        // stop morph pose
        EMBRPose sp = new EMBRPose();
        sp.constraints.add(smtc);
        sp.relativeTime = true;
        sp.offset(duration + 600);

        // eyes and brows
        raiseEyeBrowPose(bp, intensity, BROWMOVE.SYMMETRICAL);
        defaultEyeBrowPose(sp);

        openEyeLid(bp, intensity);
        defaultEyeLid(sp);

        // build
        ps.addPose(bp);
        ps.addPose(sp);

        _embrScript.addElement(ps);

        // debug
        log.info("---");
        log.info(_embrScript.createScript(false));
        log.info("---");
    }

    private void affectiveGloatingRealization(Double intensity, long duration)
    {

        // start morph constraint
        EMBRMorphTargetConstraint mtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_SMILE_CLOSED, intensity);
        // stop morph constraint
        EMBRMorphTargetConstraint smtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_SMILE_CLOSED, sDEFAULTMORPHINT);

        EMBRPoseSequence ps = makePoseSequence();

        // begin morph pose
        EMBRPose bp = new EMBRPose();
        bp.constraints.add(mtc);
        bp.relativeTime = true;
        bp.offset(600);

        // stop morph pose
        EMBRPose sp = new EMBRPose();
        sp.constraints.add(smtc);
        sp.relativeTime = true;
        sp.offset(duration + 600);

        // eyes and brows
        raiseEyeBrowPose(bp, intensity, BROWMOVE.ASYMMETRICAL);
        defaultEyeBrowPose(sp);

        openEyeLid(bp, intensity);
        defaultEyeLid(sp);

        // tilt head to side signalling doubt (towards the other person)
        tiltHeadLeft(bp);
        tiltHeadDefault(sp);

        // build
        ps.addPose(bp);
        ps.addPose(sp);

        _embrScript.addElement(ps);

        // debug
        log.info("---");
        log.info(_embrScript.createScript(false));
        log.info("---");
    }

    private void affectiveGratitudeRealization(Double intensity, long duration)
    {

        // start morph constraint
        EMBRMorphTargetConstraint mtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_SMILE_CLOSED, intensity);
        // stop morph constraint
        EMBRMorphTargetConstraint smtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_SMILE_CLOSED, sDEFAULTMORPHINT);

        EMBRPoseSequence ps = makePoseSequence();

        // begin morph pose
        EMBRPose bp = new EMBRPose();
        bp.constraints.add(mtc);
        bp.relativeTime = true;
        bp.offset(600);

        // stop morph pose
        EMBRPose sp = new EMBRPose();
        sp.constraints.add(smtc);
        sp.relativeTime = true;
        sp.offset(duration);

        // eyes and brows
        raiseEyeBrowPose(bp, intensity, BROWMOVE.SYMMETRICAL);
        defaultEyeBrowPose(sp);

        // roll head to front signalling dependence
        rollHeadFront(bp);
        rollHeadDefault(sp);

        // build
        ps.addPose(bp);
        ps.addPose(sp);

        _embrScript.addElement(ps);

        // debug
        log.info("---");
        log.info(_embrScript.createScript(false));
        log.info("---");
    }

    private void affectiveDistressRealization(Double intensity, long duration)
    {
        // start morph constraint
        EMBRMorphTargetConstraint mtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_ANGER, intensity * 0.5);
        // stop morph constraint
        EMBRMorphTargetConstraint smtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_ANGER, sDEFAULTMORPHINT);

        EMBRPoseSequence ps = makePoseSequence();

        // begin morph pose
        EMBRPose bp = new EMBRPose();
        bp.constraints.add(mtc);
        bp.relativeTime = true;
        bp.offset(500);

        // stop morph pose
        EMBRPose sp = new EMBRPose();
        sp.constraints.add(smtc);
        sp.relativeTime = true;
        sp.offset(duration);

        // roll head to back - signaling moving back tendency
        rollHeadBack(bp);
        rollHeadDefault(sp);

        //  build
        ps.addPose(bp);
        ps.addPose(sp);

        _embrScript.addElement(ps);

        // debug
        log.info("---");
        log.info(_embrScript.createScript(false));
        log.info("---");
    }

    private void affectiveAngerRealization(Double intensity, long duration)
    {
        // start morph constraint
        EMBRMorphTargetConstraint mtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_ANGER, intensity);
        // stop morph constraint
        EMBRMorphTargetConstraint smtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_ANGER, sDEFAULTMORPHINT);

        EMBRPoseSequence ps = makePoseSequence();

        // begin morph pose
        EMBRPose bp = new EMBRPose();
        bp.constraints.add(mtc);
        bp.relativeTime = true;
        bp.offset(500);

        // stop morph pose
        EMBRPose sp = new EMBRPose();
        sp.constraints.add(smtc);
        sp.relativeTime = true;
        sp.offset(duration);

        // blushing
        setBlushing(bp, intensity);
        setDefaultBlushing(sp);

        //  build
        ps.addPose(bp);
        ps.addPose(sp);

        _embrScript.addElement(ps);

        // debug
        log.info("---");
        log.info(_embrScript.createScript(false));
        log.info("---");
    }

    private void affectiveHateRealization(Double intensity, long duration)
    {
        // start morph constraint
        EMBRMorphTargetConstraint mtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_ANGER, intensity * 0.5);
        EMBRMorphTargetConstraint mtc1 = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_DISGUST, intensity * 0.5);
        // stop morph constraint
        EMBRMorphTargetConstraint smtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_ANGER, sDEFAULTMORPHINT);
        EMBRMorphTargetConstraint smtc1 = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_DISGUST, sDEFAULTMORPHINT);

        EMBRPoseSequence ps = makePoseSequence();

        // begin morph pose
        EMBRPose bp = new EMBRPose();
        bp.constraints.add(mtc);
        bp.constraints.add(mtc1);
        bp.relativeTime = true;
        bp.offset(500);

        // stop morph pose
        EMBRPose sp = new EMBRPose();
        sp.constraints.add(smtc);
        sp.constraints.add(smtc1);
        sp.relativeTime = true;
        sp.offset(duration + 600);

        // blushing
        setBlushing(bp, intensity);
        setDefaultBlushing(sp);

        // roll head to front - signaling aggressiveness
        rollHeadFront(bp);
        rollHeadDefault(sp);

        //  build
        ps.addPose(bp);
        ps.addPose(sp);

        _embrScript.addElement(ps);

        // debug
        log.info(_embrScript.createScript(false));
    }

    private void affectiveDisappointmentRealization(Double intensity, long duration)
    {
        // start morph constraint
        EMBRMorphTargetConstraint mtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_ANGER, intensity * 0.5d);
        // stop morph constraint
        EMBRMorphTargetConstraint smtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_ANGER, sDEFAULTMORPHINT);

        EMBRPoseSequence ps = makePoseSequence();

        // begin morph pose
        EMBRPose bp = new EMBRPose();
        bp.constraints.add(mtc);
        bp.relativeTime = true;
        bp.offset(500);

        // stop morph pose
        EMBRPose sp = new EMBRPose();
        sp.constraints.add(smtc);
        sp.relativeTime = true;
        sp.offset(duration);

        // blushing
        setBlushing(bp, intensity);
        setDefaultBlushing(sp);

        // head tilting - signaling doubt
        tiltHeadLeft(bp);
        tiltHeadDefault(sp);

        //  build
        ps.addPose(bp);
        ps.addPose(sp);

        _embrScript.addElement(ps);

        // debug
        log.info("---");
        log.info(_embrScript.createScript(false));
        log.info("---");
    }

    private void affectiveDisgustRealization(Double intensity, long duration)
    {
        // start morph constraint
        EMBRMorphTargetConstraint mtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_DISGUST, intensity);
        // stop morph constraint
        EMBRMorphTargetConstraint smtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_DISGUST, sDEFAULTMORPHINT);

        EMBRPoseSequence ps = makePoseSequence();

        // begin morph pose
        EMBRPose bp = new EMBRPose();
        bp.constraints.add(mtc);
        bp.relativeTime = true;
        bp.offset(500);

        // stop morph pose
        EMBRPose sp = new EMBRPose();
        sp.constraints.add(smtc);
        sp.relativeTime = true;
        sp.offset(duration);

        // head tilting - signaling doubt
        tiltHeadLeft(bp);
        tiltHeadDefault(sp);

        //  build
        ps.addPose(bp);
        ps.addPose(sp);

        _embrScript.addElement(ps);

        // debug
        log.info("---");
        log.info(_embrScript.createScript(false));
        log.info("---");
    }

    /*
     * Builds an EMBRScript that let's a character express a given emotion. Parameters are
     * inentsity and duration
     */
    private void affectiveShameRealization(Double intensity, long duration)
    {
        // start morph constraint
        EMBRMorphTargetConstraint mtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_DISGUST, intensity * 0.3);
        // stop morph constraint
        EMBRMorphTargetConstraint smtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_DISGUST, sDEFAULTMORPHINT);


        EMBRPoseSequence ps = makePoseSequence();

        // begin morph pose
        EMBRPose bp = new EMBRPose();
        bp.constraints.add(mtc);
        bp.relativeTime = true;
        bp.offset(500);

        // stop morph pose
        EMBRPose sp = new EMBRPose();
        sp.constraints.add(smtc);
        sp.relativeTime = true;
        sp.offset(duration + 1500);

        // blushing
        setBlushing(bp, intensity);
        setDefaultBlushing(sp);

        // roll head to front - signaling dependence
        rollHeadFront(bp);
        rollHeadDefault(sp);

        //  build
        ps.addPose(bp);
        ps.addPose(sp);

        _embrScript.addElement(ps);

        // debug
        log.info("---");
        log.info(_embrScript.createScript(false));
        log.info("---");
    }

    private void affectiveFearRealization(Double intensity, long duration)
    {
        // start morph constraint
        EMBRMorphTargetConstraint mtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_FEAR, intensity);
        // stop morph constraint
        EMBRMorphTargetConstraint smtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_FEAR, sDEFAULTMORPHINT);

        EMBRPoseSequence ps = makePoseSequence();

        // begin morph pose
        EMBRPose bp = new EMBRPose();
        bp.constraints.add(mtc);
        bp.relativeTime = true;
        bp.offset(500);

        // stop morph pose
        EMBRPose sp = new EMBRPose();
        sp.constraints.add(smtc);
        sp.relativeTime = true;
        sp.offset(duration + 500);

        // eyebrows
        raiseEyeBrowPose(bp, intensity, BROWMOVE.SYMMETRICAL);
        defaultEyeBrowPose(sp);

        //eyes
        openEyeLid(bp, intensity);
        defaultEyeLid(sp);

        // build
        ps.addPose(bp);
        ps.addPose(sp);

        _embrScript.addElement(ps);

        // debug
        log.info("---");
        log.info(_embrScript.createScript(false));
        log.info("---");
    }

    private void affectiveHopeRealization(Double intensity, long duration)
    {
        // start morph constraint
        EMBRMorphTargetConstraint mtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_SURPRISE, intensity * 0.5);
        // stop morph constraint
        EMBRMorphTargetConstraint smtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_SURPRISE, sDEFAULTMORPHINT);

        EMBRPoseSequence ps = makePoseSequence();

        // begin morph pose
        EMBRPose bp = new EMBRPose();
        bp.constraints.add(mtc);
        bp.relativeTime = true;
        bp.offset(500);

        // stop morph pose
        EMBRPose sp = new EMBRPose();
        sp.constraints.add(smtc);
        sp.relativeTime = true;
        sp.offset(duration + 500);

        // head tilting - signaling doubt
        tiltHeadLeft(bp);
        tiltHeadDefault(sp);

        // eyebrows
        raiseEyeBrowPose(bp, intensity, BROWMOVE.SYMMETRICAL);
        defaultEyeBrowPose(sp);

        //eyes
        openEyeLid(bp, intensity);
        defaultEyeLid(sp);

        // build
        ps.addPose(bp);
        ps.addPose(sp);

        _embrScript.addElement(ps);

        // debug
        log.info("---");
        log.info(_embrScript.createScript(false));
        log.info("---");
    }

    private void affectiveReliefRealization(Double intensity, long duration)
    {
        // start morph constraint
        EMBRMorphTargetConstraint mtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_SURPRISE, intensity * 0.5);
        // stop morph constraint
        EMBRMorphTargetConstraint smtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_SURPRISE, sDEFAULTMORPHINT);

        EMBRPoseSequence ps = makePoseSequence();

        // begin morph pose
        EMBRPose bp = new EMBRPose();
        bp.constraints.add(mtc);
        bp.relativeTime = true;
        bp.offset(500);

        // stop morph pose
        EMBRPose sp = new EMBRPose();
        sp.constraints.add(smtc);
        sp.relativeTime = true;
        sp.offset(duration + 500);

        // head tilting - signaling doubt
        rollHeadFront(bp);
        rollHeadDefault(sp);

        //eyes
        closeEyeLid(bp, intensity);
        defaultEyeLid(sp);

        // build
        ps.addPose(bp);
        ps.addPose(sp);

        _embrScript.addElement(ps);

        // debug
        log.info("---");
        log.info(_embrScript.createScript(false));
        log.info("---");
    }

    private void affectiveSorryForRealization(Double intensity, long duration)
    {
        // start morph constraint
        EMBRMorphTargetConstraint mtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_SAD, intensity);
        // stop morph constraint
        EMBRMorphTargetConstraint smtc = new EMBRMorphTargetConstraint(EMBRMorphKey.EXP_SAD, sDEFAULTMORPHINT);

        EMBRPoseSequence ps = makePoseSequence();

        // begin morph pose
        EMBRPose bp = new EMBRPose();
        bp.constraints.add(mtc);
        bp.relativeTime = true;
        bp.offset(500);

        // stop morph pose
        EMBRPose sp = new EMBRPose();
        sp.constraints.add(smtc);
        sp.relativeTime = true;
        sp.offset(duration + 500);

        // roll head to front - signaling dependence
        rollHeadFront(bp);
        rollHeadDefault(sp);

        // raise eyebrows
        raiseEyeBrowPose(bp, intensity, BROWMOVE.SYMMETRICAL);
        defaultEyeLid(sp);

        // build
        ps.addPose(bp);
        ps.addPose(sp);

        _embrScript.addElement(ps);

        // debug
        log.info("---");
        log.info(_embrScript.createScript(false));
        log.info("---");
    }


    /**
     * sendToEMBR sends current script to EMBR
     * @param absolute true = clears current script
     */
    private void sendToEMBR(boolean absolute)
    {

        log.info((absolute) ? "ABSOLUTE" : "RELATIVE");
        String script = (absolute) ? _embrScript.toScript() : _embrScript.createScript(false);

        // send to EMBR component
        try {
            _embrSender.sendTextMessage(script, meta.getTime());
        } catch (JMSException ex) {
            Logger.getLogger(AffectGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        _embrScript.clear();
    }
}
