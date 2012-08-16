/*
 * BMLMARYFusion.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 09.07.2010, 16:54:28
 */
package de.dfki.embots.framework.translate;

import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.bml.behavior.BMLBehavior;
import de.dfki.embots.bml.behavior.BMLSpeechBehavior;
import de.dfki.embots.bml.sync.BMLSpeechSyncPoint;
import de.dfki.embots.bml.sync.BMLSyncPoint;
import java.util.ArrayList;
import java.util.List;
import javax.jms.JMSException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Static utitliy methods fo fusing BML and MARY data.
 *
 * @author Michael Kipp
 */
public abstract class BMLMARYFusion
{

    /**
     * Extract MARY time stamps and stores them in an array.
     *
     * @param d XML message from MARY with phoneme timings
     * @throws JMSException
     * @return Array with word end times
     */
    public static double[] extractWordTimings(Document d)
    {
        List<String> words = new ArrayList<String>();
        List<Double> endTimes = new ArrayList<Double>();
        Node bml = d.getElementsByTagName("bml:speech").item(0);
        double lastPhoneme = 0;
        for (int i = 0; i < bml.getChildNodes().getLength(); i++) {
            Node n = bml.getChildNodes().item(i);
            if (n.getNodeType() == Node.TEXT_NODE) {
                if (n.getNodeValue().trim().length() > 0) {
                    words.add(n.getNodeValue().trim());
                    if (lastPhoneme > 0) {
                        endTimes.add(new Double(lastPhoneme));
                    }
                }
            } else if (n.getNodeName().equals("mary:syllable")) {
                for (int j = 0; j < n.getChildNodes().getLength(); j++) {
                    if (n.getChildNodes().item(j).getNodeName().equals("mary:ph")) {
                        lastPhoneme = Double.parseDouble(n.getChildNodes().item(j).getAttributes().getNamedItem("end").getNodeValue());
                    }
                }
            }
        }

        // add last timing
        endTimes.add(new Double(lastPhoneme));

        // store results
        double[] result = new double[endTimes.size()];
        int i = 0;
        for (Double ti : endTimes) {
            result[i++] = ti;
        }
        return result;
    }

    /**
     * Fills in word timings for a single speech block.
     *
     * @param beh Speech block in the BML
     * @param wordEndTimings Array of word end times (in sec.)
     * @param offset Global offset to apply
     */
    public static void completeSpeechSyncPoints(BMLSpeechBehavior beh,
            double[] wordEndTimings,
            double offset)
    {
//        System.out.println("Sync times for " + beh.getID());
        for (BMLSyncPoint sy : ((BMLSpeechBehavior) beh).getSyncPoints()) {
            int i = ((BMLSpeechSyncPoint) sy).getPosition();
            if (i == 0) {
                sy.setTime(offset);
            } else {
                sy.setTime(offset + wordEndTimings[i - 1]);
            }
            System.out.println("  -> " + sy);
        }
    }

    /**
     * Fills in word times into the BML block.
     *
     * @return The found speech element
     */
    public static BMLSpeechBehavior completeSpeechSyncPoints(BMLBlock bml, Document d)
    {
        double[] wordEndTimings = extractWordTimings(d);
//        if (bml != null && embrScript.getElements().size() > 0) {
        for (BMLBehavior beh : bml.getBehaviors()) {
            if (beh instanceof BMLSpeechBehavior) {
                for (BMLSyncPoint sy : ((BMLSpeechBehavior) beh).getSyncPoints()) {

                    int i = ((BMLSpeechSyncPoint) sy).getPosition();
//                    System.out.println("sync point " + sy.getID() + " has pos " + i);
                    if (i == 0) {
                        sy.setTime(0d);
                    } else {
                        sy.setTime(wordEndTimings[i - 1]);
                    }
                }
                return (BMLSpeechBehavior) beh;
            }
        }
        return null;
    }
}
