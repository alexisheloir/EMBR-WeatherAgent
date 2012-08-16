package de.dfki.embots.modules.intent;

import de.dfki.scenemaker.content.Project;
import de.dfki.scenemaker.content.scenes.Abbreviation;
import de.dfki.scenemaker.content.scenes.Action;
import de.dfki.scenemaker.content.scenes.Group;
import de.dfki.scenemaker.content.scenes.Param;
import de.dfki.scenemaker.content.scenes.Scene;
import de.dfki.scenemaker.content.scenes.Script;
import de.dfki.scenemaker.content.scenes.Turn;
import de.dfki.scenemaker.content.scenes.Utterance;
import de.dfki.scenemaker.content.scenes.Vocable;
import de.dfki.scenemaker.content.scenes.Word;
import de.dfki.scenemaker.editor.event.SceneExecutedEvent;
import de.dfki.scenemaker.editor.event.TurnExecutedEvent;
import de.dfki.scenemaker.editor.event.UtteranceExecutedEvent;
import de.dfki.scenemaker.interpreter.Thread;
import de.dfki.scenemaker.interpreter.value.StringValue;
import de.dfki.scenemaker.interpreter.value.StructValue;
import de.dfki.scenemaker.interpreter.value.Value;
import de.dfki.scenemaker.interpreter.value.Value.Type;
import de.dfki.scenemaker.output.sceneplayer.ScenePlayer;
import de.dfki.scenemaker.output.sceneplayer.ScenePlayer.Task;
import de.dfki.scenemaker.util.event.EventCaster;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Creates a FML script document based on a SceneMaker Scene and sends it to a 
 * behavior planning module.
 *
 * @author Patrick Gebhard
 */
public class EMBOTSFMLScenePlayer extends ScenePlayer {

  // The properties of this sceneplayer from the configuration file
  private final Properties mProperties = new Properties();
  // private Sender
  private IntentPlannerModule mPlanner;
  // ready flag
  public boolean ready = false;
  // synchronisation with behavior planner
  private BlockingQueue<String> mMessageQueue = new LinkedBlockingQueue<String>();
  // FML ids
  private static final IDManager mFMLDocID = new IDManager();

  // FML id manager
  private static class IDManager {
    private List<Integer> mFMLDocIDs = new LinkedList<Integer>();

    public String getID() {
      int freeID = 1;
      for (int i = 0; i < mFMLDocIDs.size(); i++) {
        if (freeID == mFMLDocIDs.get(i)) {
          freeID++;
        } else {
          break;
        }
      }
      mFMLDocIDs.add(new Integer(freeID));
      Collections.sort(mFMLDocIDs);
      return "f" + freeID;
  }

  }

  public EMBOTSFMLScenePlayer(Project project) {
    super(project);
  }

  public void unload() {
    mLogger.info("Finalizing EMBOTS FML ScenePlayer");
  }

  public void setPlanner(IntentPlannerModule p) {
    mPlanner = p;
    ready = true;
    mLogger.info("EMBOTSFMLScenePlayer ready. Scenes will be send to EMBOTS Behavior Planner");
  }

  private void waitforFMLScriptExecution(String fmlID) {
    mLogger.info("Waiting for FML script " + fmlID + " is executed");
    try {
      String message = mMessageQueue.take();
      if (message.equalsIgnoreCase(fmlID)) {
        mLogger.info("FML script " + message + " has been executed. Proceeding");
      } else{
        mLogger.info("FML script " + message + " has been executed.");
        waitforFMLScriptExecution(fmlID);
      }
    } catch(InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }

  public void notifyFMLScriptExecution(String fmlID) {
    try {
      mMessageQueue.put(fmlID);
    } catch(InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }

  public void play(final String sceneName, final LinkedList<Value> sceneArgList) {
    final Thread mInterpreterThread = ((Thread) java.lang.Thread.currentThread());
    final HashMap<String, String> mSceneParamMap = new HashMap<String, String>();

    /**
     *  Process the scene parameters
     */
    if (sceneArgList != null) {
      if (!sceneArgList.isEmpty()) {
        Value value = sceneArgList.getFirst();
        if (value.getType().equals(Type.STRUCT)) {
          // Process scene arguments
          for (Entry<String, Value> entry : ((StructValue) value).getValueMap().entrySet()) {
            if (entry.getValue().getType() == Type.STRING) {
              mSceneParamMap.put(entry.getKey(), ((StringValue) entry.getValue()).getValue());
            }
          }
        }
      }
    }

    /**
     *
     */
    Task task = new Task(Thread.currentThread().getName() + "Player") {

      @Override
      public void run() {
        if (!ready) {
          mLogger.info("EMBOTSFMLScenePlayer not ready. Scene will be not executed.");
          return;
        }

        // Select a scene
        final Script sceneScript = mProject.getSceneScript();
        final Group sceneGroup = sceneScript.getGroup(sceneName);
        final Scene selectedScene = sceneGroup.select();
        // Visualization
        //mLogger.info("Executing scene:\r\n" + selectedScene.getText());
        EventCaster.getInstance().multicast(new SceneExecutedEvent(this, selectedScene));
        // Process the turns in the body of the scene
        for (Turn turn : selectedScene.getBody()) {
          // Visualization
          //mLogger.info("Executing turn:" + turn.getText());
          EventCaster.getInstance().multicast(new TurnExecutedEvent(this, turn));

          // Get the speaker of this turn
          final String speaker = turn.getId();
          if (speaker == null) {
            // Get the default speaker
          }

          //
          int wordCount = 0;

          StringBuilder fmlDoc = new StringBuilder();
          String currentFMLID = mFMLDocID.getID();
          fmlDoc.append("<FML id=\"").append(currentFMLID).append("\">").append("<TURN agent=\"");
          // Quick and dirty speaker abbreviation on speaker/character name mapping
          if (speaker.equalsIgnoreCase("A")) {
            fmlDoc.append("Amber");
          } else if (speaker.equalsIgnoreCase("B")) { // TODO: This should be something other
            fmlDoc.append("Amber");
          }
          fmlDoc.append("\">");
          // Process the utterances of the turn
          for (Utterance utt : turn.getUttList()) {
            // Visualization
            //mLogger.info("Executing utterance:" + utt.getText());
            EventCaster.getInstance().multicast(new UtteranceExecutedEvent(this, utt));
            // Process the words of this utterance
            for (Word word : utt.getWordList()) {
              if (word instanceof Vocable) {
                // Visualization
                //mLogger.info("Executing vocable:" + ((Vocable) word).getText());
                wordCount = ((Vocable) word).getContent().length();
                fmlDoc.append(((Vocable) word).getText()).append(" ");

              } else if (word instanceof Param) {
                // Visualization
                //mLogger.info("Executing param:" + ((Param) word).getText());
              } else if (word instanceof Action) {
                // Visualization
                //mLogger.info("Executing action:" + ((Action) word).getText());
                fmlDoc.append("<").append(((Action) word).getName().toUpperCase()).append("/>");
              } else if (word instanceof Abbreviation) {
                // Visualization
                //mLogger.info("Executing abbreviation:" + ((Abbreviation) word).getText());
              }
            }
            String u = fmlDoc.toString().trim();;
            fmlDoc = new StringBuilder();
            fmlDoc.append(u);
            if (turn.getUttList().lastElement().equals(utt)) {
              fmlDoc.append(utt.getPunct());
            } else {
              fmlDoc.append(utt.getPunct()).append(" ");
            }
          }
          fmlDoc.append("</TURN></FML>");

          // send to behavior planer
          mLogger.info("FMLized turn: " + fmlDoc.toString());
          mPlanner.sendToBehaviorPlanner(fmlDoc.toString());

          // wait until utterance has been played!
          waitforFMLScriptExecution(currentFMLID);

          //String finalUtterance = utterance.toString();
          //mLogger.info(speaker + ": " + finalUtterance.toString());

          // Exit if the scene has been interrupted
          if (mIsDone) {
            return;
          }
        }
      }
    };
    // Start the scene player thread
    task.start();
    // Wait until the current thread has been interrupted
    boolean finished = false;
    while (!finished) {
      try {
        task.join();
        finished = true;
      } catch (Exception e) {
        task.mIsDone = true;
      }
    }
  }
}
