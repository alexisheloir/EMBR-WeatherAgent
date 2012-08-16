package de.dfki.embots.framework.ui.eyetracking;

import de.dfki.carmina.eyeTrackerLogger.UDPThread;
import de.dfki.carmina.eyeTrackerLogger.dataProcessor.LogData;
import eu.semaine.components.Component;
import javax.jms.JMSException;
import java.io.File;
import de.dfki.embots.embrscript.EMBRScript;
import eu.semaine.jms.sender.Sender;
import de.dfki.embots.bml.lex.BehaviorLexicon;
import de.dfki.embots.embrscript.EMBRScriptReader;
import de.dfki.embots.framework.EMBOTSConstants;
import de.dfki.embots.framework.behavior.GestureGenerator;
import de.dfki.embots.framework.gaze.eytrackersim.GazeInputWindow;
import de.dfki.embots.framework.gaze.eytrackersim.RandomSetting;
import de.dfki.embots.framework.ui.ModuleErrorDialog;
import de.dfki.embots.framework.ui.eyetracking.actions.ChangeStartNodeAction;
import de.dfki.visp.exception.ChannelNotExistException;
import de.dfki.visp.exception.NodeNotExistException;
import de.dfki.visp.graph.Condition;
import de.dfki.visp.graph.Machine;
import de.dfki.visp.graph.Supernode;
import de.dfki.visp.graph.Node;
import de.dfki.visp.graph.impl.MachineImpl;
import de.dfki.visp.graph.impl.ReadChannel;
import de.dfki.visp.graph.impl.condition.ConditionAnd;
import de.dfki.visp.graph.impl.condition.ConditionEquals;
import eu.semaine.jms.message.SEMAINEMessage;
import eu.semaine.jms.receiver.Receiver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main Class for the gaze control
 * @author Daniel Puschmann
 */
public class Gaze extends Component {

    public static final String AGENT = "Amber";
    private static final String NAME = "Gaze";
    private static final String LOOKED_AT_VARIABLE = "LookedAt";
    private Sender embrSender;
    private EMBRScript currentEmbrScript;
    private BehaviorLexicon _lexicon;
    private static final File TEMP_DIR = new File("../temp");
    private static final String LAST_EMBR_COMMAND_FILE = "last_embrscript.embr";
    private GazeInputWindow simulator;
    private GazeStrategy currentGazeStrat;
    private Hashtable<String,GazeStrategy> strategies = new Hashtable<String,GazeStrategy>();
    private RandomSetting randSet;
    private Machine _fsm;
    private LogDataSingleton data;
    private UserStartsLookingAtEvent lookAtEvent = new UserStartsLookingAtEvent();
    private UserStartsLookingAwayEvent lookAwayEvent = new UserStartsLookingAwayEvent();
    private boolean startUp = true;
    private boolean firstTime = true;
    private boolean lookedAt = true;
    private Sender bmlSender;
    private UDPThread udpThread;
    private static final Boolean SIMULATOR = false;


    public Gaze() throws JMSException {
        super(NAME);
        embrSender = new Sender(EMBOTSConstants.EMBRSCRIPT_TYPE, "String", NAME);
        senders.add(embrSender);
        currentEmbrScript = new EMBRScript();
        data = LogDataSingleton.getInstance();
        /*_fsm = new MachineImpl("Gaze");
        _fsm.createChannel("State");
        _fsm.createChannel("Strategy");
        Supernode shy = _fsm.createSupernode("Shy");
        Supernode dom = _fsm.createSupernode("Dom");
        _fsm.setStartNode(dom);
        ((MachineImpl)_fsm).addVariable(LOOKED_AT_VARIABLE,true);
        strategies.put(shy.getName(),new ShyGazeStrategy(this,_fsm, shy));
        strategies.put(dom.getName(),new DominantGazeStrategy(this,_fsm,dom));
        try{
            ConditionAnd domLook = new ConditionAnd();
            domLook.addCondition(new ConditionEquals(new ReadChannel(_fsm, "Strategy"), new StrategyChangeEvent("Dom")));
            domLook.addCondition(new ConditionEquals(MachineImpl._variables.get(LOOKED_AT_VARIABLE), true));
            ConditionAnd domNotLook = new ConditionAnd();
            domNotLook.addCondition(new ConditionEquals(new ReadChannel(_fsm, "Strategy"), new StrategyChangeEvent("Dom")));
            domNotLook.addCondition(new ConditionEquals(MachineImpl._variables.get(LOOKED_AT_VARIABLE), false));
            ConditionAnd shyLook = new ConditionAnd();
            shyLook.addCondition(new ConditionEquals(new ReadChannel(_fsm, "Strategy"), new StrategyChangeEvent("Shy")));
            shyLook.addCondition(new ConditionEquals(MachineImpl._variables.get(LOOKED_AT_VARIABLE), true));
            ConditionAnd shyNotLook = new ConditionAnd();
            shyNotLook.addCondition(new ConditionEquals(new ReadChannel(_fsm, "Strategy"), new StrategyChangeEvent("Shy")));
            shyNotLook.addCondition(new ConditionEquals(MachineImpl._variables.get(LOOKED_AT_VARIABLE), false));
            _fsm.createInterruptEdge(dom, shy, shyLook).addAction(new ChangeStartNodeAction(shy));
            _fsm.createInterruptEdge(dom, shy, shyNotLook).addAction(new ChangeStartNodeAction(shy));
            _fsm.createInterruptEdge(shy,dom,domLook).addAction(new ChangeStartNodeAction(dom));
            _fsm.createInterruptEdge(shy,dom,domNotLook).addAction(new ChangeStartNodeAction(dom));
        }catch (NodeNotExistException e){
            e.printStackTrace();
        }


        // Needed?
        currentGazeStrat = strategies.get(dom.getName());*/


     }

    @Override
    public void act() throws JMSException {
        if(startUp){
            //_fsm.start();
            if (!SIMULATOR) udpThread.start();
            startUp = false;
            return;
        }
        if (SIMULATOR) simulator.run();
        /*if(!currentEmbrScript.getElements().isEmpty()){
            sendCurrentScriptToEMBR();
        }*/
    }

    @Override
    public void react(SEMAINEMessage m) throws Exception{
        /*We don't need this
         String help="";
        String strategy="";
        try{
            if(m.getTopicName().equals(EMBOTSConstants.BML_INPUT_TYPE)){
                String bml = m.getText();
                help = bml.substring(bml.indexOf("strategy=\"")+10);
                strategy = help.substring(0,help.indexOf("\""));
                if(strategy!=null&&!strategy.equals("")){
                    System.out.println("ULALA!");
                    changeStrategy(strategy);
                }
            }
        }catch(RuntimeException e){
            ModuleErrorDialog.show(NAME, e.getStackTrace());
            log.error(NAME + " failed! " + e.getMessage());
            e.printStackTrace();
        }*/

    }

    @Override
    protected void customStartIO() throws Exception {

        if (SIMULATOR) {
            simulator = new GazeInputWindow(this);
            simulator.setVisible(true);
        }
        else {
             udpThread = new UDPThread(this);
        }
        
        /*randSet = new RandomSetting(this);
            randSet.setVisible(true);*/
       
        try {
            bmlSender = new Sender(EMBOTSConstants.BML_INPUT_TYPE, "String", NAME);
            receivers.add(new Receiver(EMBOTSConstants.BML_INPUT_TYPE));
        } catch (JMSException ex) {
            Logger.getLogger(Gaze.class.getName()).log(Level.SEVERE, null, ex);
        }
        senders.add(bmlSender);

        // read lexicon
        EMBRScriptReader rd1 = new EMBRScriptReader();
        _lexicon = rd1.readLexicon(new File(EMBOTSConstants.EMBRSCRIPT_LEXICON_DIR));

        // print all lexemes
        log.info("Loaded lexicon [" + _lexicon.getLexemes().size() + " entries]");
    }

    /**
     * sendToEMBR sends current script to EMBR and clears current script.
     */
    private void sendCurrentScriptToEMBR() {
        sendCurrentScriptToEMBR(true);
    }

    /**
     * sendToEMBR sends current script to EMBR
     * @param absolute true = clears current script
     */
    private void sendCurrentScriptToEMBR(boolean absolute) {

        /*log.debug("EMBR message dispatched " + ((absolute) ? "(absolute)" : "(relative)"));
        try {

//            String script = (absolute) ? currentEmbrScript.toScript() : currentEmbrScript.toRelScript();
//            String script = (absolute) ? embrScript.toScript() : embrScript.toRelScript();
            String script = currentEmbrScript.createScript(absolute);

            // send to EMBR component
            try {
                embrSender.sendTextMessage(script, meta.getTime());
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

            currentEmbrScript.clear();
        } catch (IOException ex) {
            Logger.getLogger(GestureGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    /**
     * Is cyclically called to transmit the current outside state
     * (usually user position and gaze).
     */
    public void setLogData(LogData logdata) {
        data.setData(logdata);
    }

    public void changeStrategy(String strategy) {
        /*System.out.println("HALLO!");
        this.currentGazeStrat = strategies.get(strategy);
        currentGazeStrat.setOffset();
        randSet.setToInitialValues();
        try{
            _fsm.addToChannel("Strategy", new StrategyChangeEvent(strategy));
            System.out.println("FFS!");
        }catch (ChannelNotExistException e){
            e.printStackTrace();
        }*/
    }

    public GazeStrategy getGazeStrategy(){
        return currentGazeStrat;
    }

    public void setEMBRScript(EMBRScript e){
        currentEmbrScript = e;
    }

    public void setState(boolean lookedAt){
        /*if(lookedAt){
            if (_fsm != null) {
                try {
                    this.lookedAt = lookedAt;
                    _fsm.addToChannel("State", lookAtEvent);
                    ((MachineImpl)_fsm).assignVariable(LOOKED_AT_VARIABLE, lookedAt);
                } catch (ChannelNotExistException ex) {
                    ex.printStackTrace();
                }
            }
        }else{
            if (_fsm != null) {
                try {
                    this.lookedAt = lookedAt;
                    _fsm.addToChannel("State", lookAwayEvent);
                    ((MachineImpl)_fsm).assignVariable(LOOKED_AT_VARIABLE, lookedAt);
                } catch (ChannelNotExistException ex) {
                    ex.printStackTrace();
                }
            }
        }*/
    }

    public void checkIfEMBRisLookedAt(){
        /*if(lookedAt){
            if (_fsm != null) {
                try {
                    _fsm.addToChannel("State", lookAtEvent);
                } catch (ChannelNotExistException ex) {
                    ex.printStackTrace();
                }
            }
        }else{
            if (_fsm != null) {
                try {
                    _fsm.addToChannel("State", lookAwayEvent);
                } catch (ChannelNotExistException ex) {
                    ex.printStackTrace();
                }
            }
        }*/
    }

    public void setBMLInput(String bml){
        /*try {
            bmlSender.sendTextMessage(bml, System.currentTimeMillis());
        } catch (JMSException ex) {
            Logger.getLogger(Gaze.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }




}
