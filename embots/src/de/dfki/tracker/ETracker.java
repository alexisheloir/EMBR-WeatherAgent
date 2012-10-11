/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.tracker;

import eu.semaine.jms.sender.Sender;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import de.dfki.embots.embrscript.EMBRLookAtConstraint;
import de.dfki.embots.embrscript.EMBRNormal;
import de.dfki.embots.embrscript.EMBROrientationConstraint;
import de.dfki.embots.embrscript.EMBRPose;
import de.dfki.embots.embrscript.EMBRPoseSequence;
import de.dfki.embots.embrscript.EMBRScript;
import de.dfki.embots.embrscript.Triple;
import de.dfki.embots.framework.EMBOTSConstants;
import de.dfki.embots.framework.ui.eyetracking.Gaze;
import eu.semaine.components.Component;
import eu.semaine.jms.message.SEMAINEMessage;
import eu.semaine.jms.receiver.Receiver;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JButton;

/**
 * 
 * @author Mahendiran

 */
public class ETracker extends Component {

    public static final String NAME = "ETracker";
    private static final int presentation_mode = Integer.parseInt(ConfigForETracker.getProperties("ETracker.Presentation.Style"));
  
   
    private Sender bmlSender;
    private Sender embrSender;
    private Sender reloadLexiconSender;
 
    private final JButton okButton = new JButton("OK");
    private static final int TIME_TO_ASSUME_POSE = 1000;
    private EMBRScript embrScript;
    private Random rand = new Random();

    int counter1=0;
    int counter2=0;

    private boolean initialized=false;

    
    private EyeTrackerStrategy eyeTrackerStrategy=new EyeTrackerStrategy();
    private BlockingQueue<Location> locationQueue=new LinkedBlockingQueue<Location>();
    private AgentAnimator agentAnimator;

    public ETracker() throws JMSException, IOException {
        super(NAME, false, false);

        initSenders();
        initReceivers();
     
        agentAnimator=new AgentAnimator(this, locationQueue,presentation_mode);
        agentAnimator.start();
    }

   
   
 

    /*
     * Initialize senders for our component
     */
    private void initSenders() throws JMSException {
        bmlSender = new Sender("semaine.data.bml.input", "String", NAME);
        embrSender = new Sender("semaine.data.embrscript", "String", NAME);
        reloadLexiconSender = new Sender(EMBOTSConstants.RELOAD_TYPE, "String", NAME);
        senders.add(bmlSender);
        senders.add(embrSender);
        senders.add(reloadLexiconSender);
    }

    /*
     * Initialize receivers for our component
     */
    private void initReceivers() throws JMSException {
        //receivers.add(new Receiver("semaine.data.gaze.coordinates"));
        receivers.add(new Receiver("semaine.data.bml.feedback"));
    }

    /*
     * This method is called frequently by the component runner
     */
    synchronized 
    @Override
    protected void act() throws IOException, JMSException {

        if(!initialized)
        {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ETracker.class.getName()).log(Level.SEVERE, null, ex);
            }finally
            {
                initialized=true;
            }
        }
        //get fresh eyetracking data and interprete it
        try
        {
            if(locationQueue.size()<2 && agentAnimator.isConsumed())
               {
                Location mostViewedLocation=eyeTrackerStrategy.getMostViewedLocation();
                locationQueue.add(mostViewedLocation);
               }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
               
    }

   

    /*
     * This method gets called whenever a Semaine message is received
     */
    @Override
    protected void react(SEMAINEMessage m) throws JMSException {
         if (m.getTopicName().equals("semaine.data.bml.feedback")) {

           
        }

         


    }


  

    /*
     * Returns current time, needed for Semaine message system
     */
    public long getTime() {
        return meta.getTime();
    }

    /*
     * create and send bml for introduction
     */
    private void introduction() throws JMSException {
        String textString = "Hello, I'm Amber, the weather agent.";
        String bmldoc = "";
        bmldoc = bmldoc.concat("<bml id=\"b1\">");
        bmldoc = bmldoc.concat("<speech id=\"s\">");
        bmldoc = bmldoc.concat("<text><sync id=\"0\" /><sync id=\"1\" />" + textString + "</text>");
        bmldoc = bmldoc.concat("</speech>");
        bmldoc = bmldoc.concat("<face id=\"f0\" type=\"neutral\" stroke_start=\"s:0\" />");
        bmldoc = bmldoc.concat("<gesture id=\"g1\" type=\"lexicalized\" lexeme=\"MORE-OR-LESS_LH\" stroke_start=\"s:0\"/>");
        bmldoc = bmldoc.concat("</bml>");
        sendBML(bmldoc);
    }

    /*
     * create and send bml for asking for city
     */
    private void askForCity() throws JMSException {
        String textString = "For which city do you want to hear the weather forecast?";
        String bmldoc = "";
        bmldoc = bmldoc.concat("<bml id=\"b1\">");
        bmldoc = bmldoc.concat("<speech id=\"s\">");
        bmldoc = bmldoc.concat("<text><sync id=\"0\" /><sync id=\"1\" />" + textString + "</text>");
        bmldoc = bmldoc.concat("</speech>");
        bmldoc = bmldoc.concat("<face id=\"f0\" type=\"neutral\" stroke_start=\"s:0\" />");
        bmldoc = bmldoc.concat("</bml>");
        sendBML(bmldoc);
    }

    /*
     * Create and send bml that forbids to choose the same city again
     */
    private void chooseAnotherCity() throws JMSException {
        String textString = "You may not choose the same city twice. Please choose another city.";
        String bmldoc = "";
        bmldoc = bmldoc.concat("<bml id=\"b1\">");
        bmldoc = bmldoc.concat("<speech id=\"s\">");
        bmldoc = bmldoc.concat("<text><sync id=\"0\" /><sync id=\"1\" />" + textString + "</text>");
        bmldoc = bmldoc.concat("</speech>");
        bmldoc = bmldoc.concat("<face id=\"f0\" type=\"neutral\" stroke_start=\"s:0\" />");
        bmldoc = bmldoc.concat("</bml>");
        sendBML(bmldoc);
    }



    /*
     * Send bml to the BML Realizer
     */
    public void sendBML(String bml) throws JMSException {
        bmlSender.sendTextMessage(bml, getTime());
    }

    /*
     * Reload the lexeme lexicon
     */
    public void reloadLexicon() throws JMSException {
        reloadLexiconSender.sendTextMessage("RELOAD", getTime());
    }


  
    private void sendRandomLexeme() throws JMSException {
        createRandomLexeme();
        embrSender.sendTextMessage(embrScript.createScript(false), getTime());
    }

    private void createRandomLexeme() {
        embrScript = new EMBRScript();
        EMBRPoseSequence seq = new EMBRPoseSequence(Gaze.AGENT);
        seq.setASAP(true);
        seq.fadeIn = 200;
        seq.fadeOut = 200;
        float xvalue = (rand.nextFloat() % (float) 2.0) - (float) 1.0;
        float yvalue = (rand.nextFloat() % (float) 2.0) - (float) 1.0;
        EMBRLookAtConstraint el = new EMBRLookAtConstraint(de.dfki.embots.embrscript.EMBRBodyGroup.EYES, new Triple(xvalue, 0.0, yvalue));
        EMBROrientationConstraint ec = new EMBROrientationConstraint(de.dfki.embots.embrscript.EMBRBodyGroup.HEAD_ABDOMEN, de.dfki.embots.embrscript.EMBRJoint.HEAD, EMBRNormal.Z_AXIS, new Triple(xvalue, 0, yvalue));
        EMBROrientationConstraint et = new EMBROrientationConstraint(de.dfki.embots.embrscript.EMBRBodyGroup.HEAD_NECK, de.dfki.embots.embrscript.EMBRJoint.HEAD, EMBRNormal.Y_AXIS, new Triple(xvalue, 0, yvalue));
        EMBRPose pose = new EMBRPose(TIME_TO_ASSUME_POSE);
        pose.constraints.add(ec);
        pose.constraints.add(el);
        pose.constraints.add(et);
        pose.relativeTime = true;
        seq.addPose(pose);
        embrScript.addElement(seq);
    }


  
}
