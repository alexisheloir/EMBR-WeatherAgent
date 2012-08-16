package de.dfki.embots.modules.intent;

import de.dfki.scenemaker.util.event.Event;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import eu.semaine.components.Component;
import eu.semaine.jms.sender.Sender;
import de.dfki.embots.framework.EMBOTSConstants;
import de.dfki.scenemaker.editor.Editor;
import de.dfki.scenemaker.editor.event.NodeStartedEvent;
import de.dfki.scenemaker.output.sceneplayer.ScenePlayer;
import de.dfki.scenemaker.util.event.EventCaster;
import de.dfki.scenemaker.util.event.EventListener;
import eu.semaine.jms.message.SEMAINEMessage;
import eu.semaine.jms.receiver.Receiver;
import java.io.ByteArrayInputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Component which represents the Intent Planner, realized by the SceneMaker.
 *
 * @author Patrick Gebhard
 */
public class IntentPlannerModule extends Component implements EventListener {

  private static final String sNAME = "Intent Planner";
  private static final Editor sEditor = Editor.getInstance();
  private Sender mFMLSender;
  // SceneMaker event management system
  private final EventCaster mEventCaster = EventCaster.getInstance();
  // the used sceneplayer
  private ScenePlayer mScenePlayer;

  public IntentPlannerModule() throws JMSException {
    super(sNAME);
    mFMLSender = new Sender(EMBOTSConstants.FML_TYPE, "String", sNAME);
    // establisch sender for FML documents
    senders.add(mFMLSender);
    // establisch receiver fpr FML feedback documents
    receivers.add(new Receiver(EMBOTSConstants.FML_FEEDBACK_TYPE));

    // Start and show SceneMaker
    sEditor.setVisible(true);
    // add this component as listener to the scenemaker event queue
    mEventCaster.add(this);

//    // Test sending
//    TimerTask task = new TimerTask() {
//
//      @Override
//      public void run() {
//        String msg = "<FML><TURN agent=\"Amber\">Hello.</TURN></FML>";
//        try {
//          mFMLSender.sendTextMessage(msg, meta.getTime());
//        } catch (JMSException ex) {
//          Logger.getLogger(IntentPlannerModule.class.getName()).log(Level.SEVERE, null, ex);
//        }
//      }
//    };

//    new Timer().schedule(task, 5000);
  }

  /*
   * Sends an FML document to all receiving EMBOTS modules
   */
  public void sendToBehaviorPlanner(String fmlDoc) {
    try {
      mFMLSender.sendTextMessage(fmlDoc, meta.getTime());
    } catch (JMSException ex) {
      Logger.getLogger(IntentPlannerModule.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  @Override
  /*
   * Reacts on messages. FML status messages are passed to the FML creation
   * thread for synchronisation.
   *
   */
  public void react(SEMAINEMessage m) throws Exception {
    if (m.getTopicName().equals(EMBOTSConstants.FML_FEEDBACK_TYPE)) {
      String fmlFeedback = m.getText();

      // parse as XML
      SAXBuilder builder = new SAXBuilder();
      Document doc = builder.build(new ByteArrayInputStream(fmlFeedback.getBytes()));

      // process content
      Element root = doc.getRootElement();

      if (root.getName().equals("STATUS")) {
        String fmlID = root.getChild("FML-ID").getTextTrim();
        String type = root.getAttributeValue("type");

        // case: got a "finished" message
        if ("finished".equals(type)) {
          log.info("FINSHED FML " + fmlID);
        }
        if (mScenePlayer != null) {
          if (mScenePlayer instanceof EMBOTSFMLScenePlayer) {
            EMBOTSFMLScenePlayer esp = (EMBOTSFMLScenePlayer) mScenePlayer;
            if (esp.ready) {
              esp.notifyFMLScriptExecution(fmlID);
            }
          } else {
            log.error("SceneMaker project does not use a FMLScenePlayer! See project config.smap.");
          }
        } else {
          log.error("No Intent Planer available - ignoring FML feedback. This should not happen.");
        }
      }
    }
  }

  @Override
  /*
   * Receives SceneMaker events. Reacts on NodeStarted events in order to assign
   * the used scene player.
   */
  public void update(Event event) {
    if (event instanceof NodeStartedEvent) {
      // get SceneMaker scene player -
      mScenePlayer = sEditor.getSelectedProjectEditor().getProject().getScenePlayer();
      // set sender to relevant scene player
      if (mScenePlayer instanceof EMBOTSFMLScenePlayer) {
        EMBOTSFMLScenePlayer esp = (EMBOTSFMLScenePlayer) mScenePlayer;
        if (!esp.ready) {
          esp.setPlanner(this);
        }
      } else {
        log.error("SceneMaker project does not use a FMLScenePlayer! See project config.smap.");
      }
    }

    //System.out.println("Intent Planer - current scenemaker action is" + event.toString());
  }
}
