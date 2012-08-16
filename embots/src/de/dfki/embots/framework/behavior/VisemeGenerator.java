/*
 * VisemeGenerator.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 04.09.2009, 09:25:38
 */
package de.dfki.embots.framework.behavior;

import de.dfki.embots.embrscript.EMBRMorphTargetConstraint;
import de.dfki.embots.embrscript.EMBRPose;
import de.dfki.embots.embrscript.EMBRPoseSequence;
import de.dfki.embots.embrscript.EMBRMorphKey;
import eu.semaine.jms.message.SEMAINEMessage;
import eu.semaine.jms.message.SEMAINEXMLMessage;
import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import javax.lang.model.element.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Creates viseme script for EMBR based on MARY's output.
 *
 * @author Alexis Heloir
 * @author Michael Kipp
 */
public class VisemeGenerator
{

    private Map<String, EMBRMorphKey> _phonemeToViseme;

    public VisemeGenerator()
    {
        initVisemes();
    }

    /**
     * Given a SEMAINE message containing MARY phoneme timings, creates
     * EMBRScript for visemes.
     *
     * @param m MARY phoneme timing data.
     * @param character Name of EMBR character
     * @throws JMSException
     */
    public EMBRPoseSequence createEMBRScript(SEMAINEMessage m, String character) throws Exception
    {
        EMBRPoseSequence seq = new EMBRPoseSequence(character);
        seq.comment = "LIP SYNC ANIMATION (VISEMES)";
        SEMAINEXMLMessage xm = (SEMAINEXMLMessage) m;
        Document input = xm.getDocument();
        NodeList speechNodes = input.getElementsByTagName("bml:speech");
        Node speechNode = speechNodes.item(0);
        NamedNodeMap speechAttributes = speechNode.getAttributes();

        // Language key
        String language = (speechAttributes.getNamedItem("language")).getNodeValue();
        
        // Original text string
        String text = (speechAttributes.getNamedItem("text")).getNodeValue();
        seq.comment += " \"" + text + "\"";

        NodeList phonemes = input.getElementsByTagName("mary:ph");

        Integer previousTimePoint = 0;
        Integer timePoint = 0;
        Integer duration = 0;
        Integer hold = 0;
        String beforePreviousPhonemeKey = "";
        String previousPhonemeKey = "";
        String currentPhonemeKey = "";
        for (int i = 0; i < phonemes.getLength(); i++) {
            Node phonemeNode = phonemes.item(i);
            NamedNodeMap attributes = phonemeNode.getAttributes();
            String phonemeDuration = (attributes.getNamedItem("d")).getNodeValue();
            String phonemeEnd = (attributes.getNamedItem("end")).getNodeValue();
            currentPhonemeKey = (attributes.getNamedItem("p")).getNodeValue();

            Float phonDuration = new Float(phonemeDuration);
            Float phonEnd = new Float(phonemeEnd);
            //phonDuration *= 1000;
            //if (language.compareTo("en-GB") == 0) phonEnd *= 1000;
            phonEnd *= 1000;

            timePoint = phonEnd.intValue();
            duration = phonDuration.intValue();
            hold = 0; //(timePoint - duration) - previousTimePoint;

            if (previousTimePoint > 0) {
                EMBRPose pose = new EMBRPose(previousTimePoint);
                pose.setHoldDuration(hold);
                pose.relativeTime = false;
                seq.addPose(pose);
                if (beforePreviousPhonemeKey.compareTo("") != 0) {
                    pose.constraints.add(new EMBRMorphTargetConstraint(_phonemeToViseme.get(beforePreviousPhonemeKey), 0d));
                }
                pose.constraints.add(new EMBRMorphTargetConstraint(_phonemeToViseme.get(previousPhonemeKey), .4d));
            }
            previousTimePoint = timePoint;
            beforePreviousPhonemeKey = previousPhonemeKey;
            previousPhonemeKey = currentPhonemeKey;
        }
        if (previousTimePoint > 0) {
            EMBRPose pose = new EMBRPose(previousTimePoint);
            pose.setHoldDuration(hold);
            pose.relativeTime = false;
            seq.addPose(pose);
            if (beforePreviousPhonemeKey.compareTo("") != 0) {
                pose.constraints.add(new EMBRMorphTargetConstraint(_phonemeToViseme.get(beforePreviousPhonemeKey), 0d));
            }
            pose.constraints.add(new EMBRMorphTargetConstraint(_phonemeToViseme.get(previousPhonemeKey), .4d));
            pose = new EMBRPose(previousTimePoint + 10);
            pose.relativeTime = false;
            seq.addPose(pose);
            pose.constraints.add(new EMBRMorphTargetConstraint(_phonemeToViseme.get(previousPhonemeKey), 0d));
        }
        return seq;
    }

    private void initVisemes()
    {
        _phonemeToViseme = new HashMap<String, EMBRMorphKey>();
        _phonemeToViseme.put("A", EMBRMorphKey.PHON_BIG_AAH);
        _phonemeToViseme.put("a", EMBRMorphKey.PHON_BIG_AAH);
        _phonemeToViseme.put("O", EMBRMorphKey.PHON_OOH_Q);
        _phonemeToViseme.put("u", EMBRMorphKey.PHON_W);
        _phonemeToViseme.put("i", EMBRMorphKey.PHON_I);

        _phonemeToViseme.put("{", EMBRMorphKey.PHON_AAH);
        _phonemeToViseme.put("V", EMBRMorphKey.PHON_F_V);
        _phonemeToViseme.put("E", EMBRMorphKey.PHON_EH);
        _phonemeToViseme.put("I", EMBRMorphKey.PHON_I);
        _phonemeToViseme.put("U", EMBRMorphKey.PHON_W);

        _phonemeToViseme.put("@", EMBRMorphKey.PHON_AAH);
        _phonemeToViseme.put("r=", EMBRMorphKey.PHON_AAH);
        _phonemeToViseme.put("?", EMBRMorphKey.PHON_AAH);


        _phonemeToViseme.put("aU", EMBRMorphKey.PHON_W);
        _phonemeToViseme.put("OI", EMBRMorphKey.PHON_AAH);
        _phonemeToViseme.put("@U", EMBRMorphKey.PHON_W);
        _phonemeToViseme.put("EI", EMBRMorphKey.PHON_EE);
        _phonemeToViseme.put("AI", EMBRMorphKey.PHON_EE);

        _phonemeToViseme.put("p", EMBRMorphKey.PHON_B_M_P);
        _phonemeToViseme.put("t", EMBRMorphKey.PHON_D_S_T);
        _phonemeToViseme.put("k", EMBRMorphKey.PHON_K);
        _phonemeToViseme.put("b", EMBRMorphKey.PHON_B_M_P);
        _phonemeToViseme.put("d", EMBRMorphKey.PHON_D_S_T);
        _phonemeToViseme.put("g", EMBRMorphKey.PHON_B_M_P);

        _phonemeToViseme.put("tS", EMBRMorphKey.PHON_CH_J_SH);
        _phonemeToViseme.put("dZ", EMBRMorphKey.PHON_CH_J_SH);

        _phonemeToViseme.put("f", EMBRMorphKey.PHON_F_V);
        _phonemeToViseme.put("v", EMBRMorphKey.PHON_F_V);
        _phonemeToViseme.put("T", EMBRMorphKey.PHON_D_S_T);
        _phonemeToViseme.put("D", EMBRMorphKey.PHON_D_S_T);
        _phonemeToViseme.put("s", EMBRMorphKey.PHON_D_S_T);
        _phonemeToViseme.put("z", EMBRMorphKey.PHON_D_S_T);
        _phonemeToViseme.put("S", EMBRMorphKey.PHON_D_S_T);
        _phonemeToViseme.put("Z", EMBRMorphKey.PHON_D_S_T);
        _phonemeToViseme.put("h", EMBRMorphKey.PHON_AAH);

        _phonemeToViseme.put("l", EMBRMorphKey.PHON_D_S_T);
        _phonemeToViseme.put("m", EMBRMorphKey.PHON_D_S_T);
        _phonemeToViseme.put("n", EMBRMorphKey.PHON_N);
        _phonemeToViseme.put("N", EMBRMorphKey.PHON_N);
        _phonemeToViseme.put("r", EMBRMorphKey.PHON_R);
        _phonemeToViseme.put("6", EMBRMorphKey.PHON_R);
        _phonemeToViseme.put("w", EMBRMorphKey.PHON_W);
        _phonemeToViseme.put("j", EMBRMorphKey.PHON_CH_J_SH);
    }
}
