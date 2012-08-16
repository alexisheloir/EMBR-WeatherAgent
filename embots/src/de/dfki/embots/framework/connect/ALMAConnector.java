package de.dfki.embots.framework.connect;

import javax.jms.JMSException;
import eu.semaine.components.Component;
import eu.semaine.jms.message.SEMAINEMessage;
import eu.semaine.jms.sender.Sender;
import de.affect.manage.AffectManager;
import de.affect.manage.event.AffectUpdateEvent;
import de.affect.manage.event.AffectUpdateListener;
import de.affect.xml.AffectOutputDocument;
import de.affect.xml.AffectOutputDocument.AffectOutput.CharacterAffect;
import de.dfki.embots.framework.EMBOTSConstants;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import org.apache.xmlbeans.XmlException;

/**
 * Connects to ALMA and sends ALMA events to other components.
 *
 * @author Patrick Gebhard
 */
public class ALMAConnector extends Component implements AffectUpdateListener
{

    private static final String NAME = "ALMA Connector";
    public static AffectManager fAM = null;
    // ALMA configuration files
    private static String sALMACOMP = "../config/affect-computation.aml";
    private static String sALMADEF = "../config/affect-character.aml";
    // ALMA mode: 
    //     false - output on console 
    //     true - graphical user interface CharacterBuilder
    //            NOTE: No runtime windows (defined in AffectComputation or
    //                  AffectDefinition will be displayed!)
    private static final boolean sGUIMode = true;
    // Affect management variables
    private String mLastDominantEmotion = "";
    private String mLastArousal = "0";
    private Sender _affectRealizer;

    public ALMAConnector() throws JMSException
    {
        super(NAME);
        _affectRealizer = new Sender(EMBOTSConstants.AFFECT_TYPE, "AFFECT!", NAME);
        senders.add(_affectRealizer);

        try {
            fAM = new AffectManager(sALMACOMP, sALMADEF, sGUIMode);
            fAM.addAffectUpdateListener(this);
        } catch (IOException io) {
            log.error("Error during ALMA initialisation");
            io.printStackTrace();
            System.exit(-1);
        } catch (XmlException xmle) {
            log.error("Error in ALMA configuration");
            xmle.printStackTrace();
        }
    }

    @Override
    protected void react(SEMAINEMessage message)
            throws JMSException
    {
        String text = message.getText();
        log.info("Received message from '" + message.getSource() + "' of type '" + message.getDatatype() + "':");
        log.debug(text);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            log.debug("sleep interrupted", ie);
        }
        long time = meta.getTime();
        //senders.get(0).sendTextMessage("nop", time);
//        lastMessageTime = time;
    }

    /**
     * Listens to affect updates computed by ALMA. This
     * implements the AffectUpdateListener
     */
    @Override
    public synchronized void update(AffectUpdateEvent event)
    {
        AffectOutputDocument aod = event.getUpdate();

        try {
            for (Iterator<CharacterAffect> it = aod.getAffectOutput().getCharacterAffectList().iterator(); it.hasNext();) {
                CharacterAffect character = it.next();

                // access cached data or create new cache
                String name = character.getName();
                String emotion = character.getDominantEmotion().getName().toString();
                double eIntensity = Double.parseDouble(character.getDominantEmotion().getValue());
                String mood = character.getMood().getMoodword().toString();
                String mIntensity = character.getMood().getIntensity().toString();
                String mTendency = character.getMoodTendency().getMoodword().toString();
                String arousal = prettyPrint(character.getMood().getArousal());

                //TODO use affect for something!
                log.info(name + " has dominant emotion " + emotion + "(" + eIntensity + ")");

                // build information for EMBR
                String affectInfo = "";
                // only if emotion has changed - assume a fixed duration
                if (!emotion.equalsIgnoreCase(mLastDominantEmotion)) {
                    long duration = (new Double(eIntensity * 16000)).longValue();
                    affectInfo = emotion + ":" + eIntensity + ":" + duration;
                    // send message to BMLRealizer
                    _affectRealizer.sendTextMessage(affectInfo, meta.getTime());
                }
                if (!arousal.equalsIgnoreCase(mLastArousal)) {
                    affectInfo = "arousal:" + arousal;
                    _affectRealizer.sendTextMessage(affectInfo, meta.getTime());
                }
                // store values for next update
                mLastDominantEmotion = emotion;
                mLastArousal = arousal;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static String prettyPrint(double value)
    {
        DecimalFormat form = new DecimalFormat("0.0");
        DecimalFormatSymbols formSymbols = new DecimalFormatSymbols();
        formSymbols.setDecimalSeparator('.');
        form.setDecimalFormatSymbols(formSymbols);
        return form.format(value);
    }
}
