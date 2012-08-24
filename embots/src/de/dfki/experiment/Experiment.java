/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.experiment;

import eu.semaine.jms.sender.Sender;
import javax.jms.JMSException;
import GUI.GraphicComponent;
import GUI.Window;
import config.xml.CityConfig;
import config.xml.CountryConfig;
import config.xml.ExperimentConfig;
import config.xml.ExperimentConfigXMLParser;
import de.dfki.embots.embrscript.EMBRLookAtConstraint;
import de.dfki.embots.embrscript.EMBRNormal;
import de.dfki.embots.embrscript.EMBROrientationConstraint;
import de.dfki.embots.embrscript.EMBRPose;
import de.dfki.embots.embrscript.EMBRPoseSequence;
import de.dfki.embots.embrscript.EMBRScript;
import de.dfki.embots.embrscript.Triple;
import de.dfki.embots.framework.EMBOTSConstants;
import de.dfki.embots.framework.ui.eyetracking.Gaze;
import de.dfki.embots.framework.ui.eyetracking.UserGazeAtMapController;
import eu.semaine.components.Component;
import eu.semaine.jms.message.SEMAINEMessage;
import eu.semaine.jms.receiver.Receiver;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Our experiment component
 * @author Max
 * @additions Patrick
 * @additions Mahendiran
 */
public class Experiment extends Component {

    public static final String NAME = "Experiment";
    //Folder for config and map files; must contain subfolders soundlogs and textlogs
    private static final String EXPERIMENT_PATH = ConfigForExperiment.getProperties("Experiment.Setup.Path");
    //public static final int CITIES_PER_MAP = 5;
    public static ArrayList<Integer> CITYCOUNTLIST = new ArrayList<Integer>();
    public static int COUNTRIES = -1;

    enum State {

        OFF, INTRO, SELECTION, PRESENTATION, QUESTIONS, UNDEFINED
    }
    private State state = State.OFF;
    private int cityCounter = 0;
    private int countryCounter = 0;
    private int itemCounter = 0;
    private int questionCounter = 0;
    private int currentAgent;
    private Item currentItem;
    private Sender bmlSender;
    private Sender embrSender;
    private Sender reloadLexiconSender;
    private MapChanger mapChanger;
    private WeatherPresentation weatherPresentation;
    private QuestionPosing questionPosing;
    private ArrayList<Item> items;
    private LinkedList<Map> countries;
    private int currentParticipant;
    private City currentCity;
    private HashMap<String, City> cityMap;
    private ArrayList<City> choosenCities;
    private String currentConfigFile;
    private long syncTime = System.currentTimeMillis(); // this stamp has to be used to synchronize mic and eyetracking log data
    private GraphicComponent output = null;
    private Window hWnd;
    private JComboBox cityList = null;
    private MicrophoneRecorder microphoneRecorder = null; // public so you can handle it from other classes like eyetracking
    private final JButton startButton = new JButton("Start");
    private final JButton okButton = new JButton("OK");
    private JComboBox currentCityList;
    private UserGazeAtMapController userGazeAtMapController = new UserGazeAtMapController("Spain");
    private static final int TIME_TO_ASSUME_POSE = 1000;
    private EMBRScript embrScript;
    private int actCounter = 0;
    private Random rand = new Random();
    private Logger logger;
    private StateChanger stateChanger = new StateChanger();
    private static final String stateChangeLoggerText = "The State Changed to :";
    private LinkedList<String> countryNamesForLogging;

    /*
     * Constructor for Experiment
     */
    public Experiment() throws JMSException {
        super(NAME, false, false);

        initSenders();
        initReceivers();
        mapChanger = new MapChanger(this, embrSender);
        createStartWindow();
    }

    /*
     * Initializes the Microphone Recorder
     * Creates a new subfolder for the current participant
     */
    private void initMicRecorder() {
        microphoneRecorder = new MicrophoneRecorder(this, EXPERIMENT_PATH + "soundlogs\\", Integer.toString(currentParticipant), syncTime, output);
        microphoneRecorder.setRecordState(false);
        microphoneRecorder.start();
        // now the mr has to be started/paused whenever needed (eyetracking, record answers of participants)
    }

    /*
     * Creates the control window for the wizard
     */
    private void createStartWindow() {
        hWnd = new Window("EMBOTS Experiment Starter", 700, 120);
        // add and save the graphics component (needed for microphonerecorder)
        GraphicComponent output = new GraphicComponent();
        hWnd.getCP().add(output);
        this.output = output;
        // read config files from experiment path
        File configDir = new File(EXPERIMENT_PATH);
        File[] configFiles = configDir.listFiles();

        ArrayList<String> configStrings = new ArrayList();

        for (int i = 0; i < configFiles.length; i++) {
            if (isXMLFile(configFiles[i])) {
                configStrings.add(configFiles[i].getName());
            }
        }

        final String[] configStringsArray = configStrings.toArray(new String[configStrings.size()]);

        // create the combobox and fill it with the strings

        final JComboBox configList = new JComboBox(configStringsArray);
        configList.setSelectedIndex(0);
        output.add(configList, BorderLayout.PAGE_START);

        // create the start button
        output.add(startButton, BorderLayout.PAGE_START);


        // this will be performed when the button is pushed
        ActionListener myButtonListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                currentConfigFile = configStringsArray[configList.getSelectedIndex()];
                System.out.println(currentConfigFile);

                startButton.setEnabled(false);
                try {
                    execute();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }
        };

        startButton.addActionListener(myButtonListener);

        output.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        hWnd.getCP().repaint();
        output.repaint();
    }

    /*
     * returns true if the given file has extension .txt
     */
    private boolean isTextFile(File f) {
        return (f.isFile() && f.getName().endsWith(".txt"));
    }

    /*
     * returns true if the given file has extension .txt
     */
    private boolean isXMLFile(File f) {
        return (f.isFile() && f.getName().endsWith(".xml"));
    }

    /*
     * Adds a dropdown menu for city selection to the wizard control window
     */
    private void addCitySelection() {
        if (currentCityList != null) {
            output.remove(currentCityList);
        }
        cityList = new JComboBox(countries.get(countryCounter).getCities().toArray());
        currentCityList = cityList;
        cityList.setSelectedIndex(0);
        output.add(cityList, BorderLayout.PAGE_START);
        // create the ok button
        okButton.setEnabled(false);
        output.add(okButton, BorderLayout.SOUTH);
        // this will be performed when the button is pushed
        ActionListener myButtonListener2 = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                currentCity = (City) countries.get(countryCounter).getCities().toArray()[cityList.getSelectedIndex()];
                okButton.setEnabled(false);
                try {
                    citySelected();
                } catch (Exception e2) {
                }

            }
        };

        //Remove old action listener
        if (okButton.getActionListeners().length > 0) {
            okButton.removeActionListener(okButton.getActionListeners()[0]);
        }
        //Add the new one
        okButton.addActionListener(myButtonListener2);

        output.setBorder(null);
        output.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        hWnd.getCP().repaint();
        output.repaint();

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
    @Override
    protected void act() throws IOException, JMSException {
        //get fresh eyetracking data and interprete it
        userGazeAtMapController.act();
        //log data if an experiment is running
        if (state != State.OFF) {
            
            if (stateChanger.isStateChanged(state)) {

                String prefixString=stateChangeLoggerText + state.toString();
                String suffixString=null;
                
                if (state == State.SELECTION || state == State.QUESTIONS) {

                    suffixString="  Country:"+countryNamesForLogging.get(countryCounter);

                } else if (state == State.PRESENTATION) {

                    suffixString="  Country:"+countryNamesForLogging.get(countryCounter)+ "   City:"+currentCity.toString();
                }

                if(suffixString!=null)
                {
                    logger.act(prefixString+suffixString);
                }
                else
                {
                    logger.act(prefixString);
                }

            }

            logger.act();
        }

        //propose city to the wizard when user releases space in selection phase
        if (state == State.SELECTION && getMicrophoneRecorder().buttonWasReleased()) {
            getMicrophoneRecorder().flush();
            currentCity = cityMap.get(userGazeAtMapController.getSelectedCity());
            proposeCity();
        }
        //random poses from time to time
        /*if (state != State.PRESENTATION) {
        if (++actCounter == 50) {
        sendRandomLexeme();
        actCounter = 0;
        }
        }*/
    }

    /*
     * Start a new experiment round
     */
    void execute() throws JMSException, IOException {
        weatherPresentation = new WeatherPresentation(this);
        questionPosing = new QuestionPosing(this);
        items = new ArrayList<Item>();
        countries = new LinkedList<Map>();
        cityMap = new HashMap<String, City>();
        choosenCities = new ArrayList<City>();
        countryCounter = 0;
        itemCounter = 0;
        cityCounter = 0;
        countryNamesForLogging = new LinkedList<String>();

        //Read in config file
        ExperimentConfigXMLParser experimentConfigXMLParser = null;
        try {
            experimentConfigXMLParser = new ExperimentConfigXMLParser(EXPERIMENT_PATH + currentConfigFile);
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
        ExperimentConfig experimentConfig = experimentConfigXMLParser.extractParameters();
        COUNTRIES = experimentConfig.getNoOfCountries();
        //Setting the participant ID
        if (experimentConfig != null) {
            currentParticipant = Integer.parseInt(experimentConfig.getUserId());
        }

        initMicRecorder();
        //Read in countries and cities

        int counter = 0;
        for (CountryConfig countryConfig : experimentConfig.getCountries()) {
            int ordinal = 1;
            String country = countryConfig.getCountryName();
            String presentationStyleString = countryConfig.getPresentationStyle();
            int presentationStyle = Integer.parseInt(presentationStyleString);


            Map currentMap = new Map(country);
            File mapFile = new File(EXPERIMENT_PATH + country + ".map");
            BufferedReader mapInput = new BufferedReader(new FileReader(mapFile));
            String line2;
            int factor = country.equals("Spain") ? 0 : country.equals("France") ? 1 : 2;
            while ((line2 = mapInput.readLine()) != null) {
                int i1, i2 = 0;
                i1 = line2.indexOf(",", i2);
                String city = line2.substring(i2, i1);
                i2 = i1 + 2;
                i1 = line2.indexOf(",", i2);
                String pronounciation = line2.substring(i2, i1);
                i2 = i1 + 2;
                float x1, x2;
                i1 = line2.indexOf(",", i2);
                x1 = Float.parseFloat(line2.substring(i2, i1));
                i2 = i1 + 2;
                x2 = Float.parseFloat(line2.substring(i2));
                City newCity = new City(city, pronounciation, x1, x2, factor * 9 + ordinal++);
                currentMap.addCity(newCity);
                cityMap.put(city, newCity);
            }
            countries.add(currentMap);
            countryNamesForLogging.add(country);

            //Read in weather conditions
            ArrayList<CityConfig> cityConfigList = countryConfig.getCities();
            if (cityConfigList != null && countryConfig.getNoOfCities() > 0) {
                for (CityConfig cityConfig : cityConfigList) {
                    String weatherCondition = cityConfig.getWeatherCondition();
                    String climateType = cityConfig.getClimate();
                    if (weatherCondition != null && climateType != null) {
                        int wc = Integer.parseInt(weatherCondition);
                        int ct = Integer.parseInt(climateType);
                        items.add(new Item(new WeatherCondition(wc, WeatherCondition.Symbol.values()[ct]), presentationStyle));
                    }

                }
                CITYCOUNTLIST.add(countryConfig.getNoOfCities());
            }

            counter++;
        }



        //create logger for current exeriment
        logger = new Logger(userGazeAtMapController, currentParticipant);
        logger.logStart();
        //Start the introduction
        state = State.INTRO;
        introduction();
    }

    /*
     * This method gets called whenever a Semaine message is received
     */
    @Override
    protected void react(SEMAINEMessage m) throws JMSException {
        //We are only interested in bml feedback
        if (m.getTopicName().equals("semaine.data.bml.feedback")) {
            if (state == State.PRESENTATION) {
                //check message text for finished presentation
                if (m.getText().contains("finished")) {
                    //city weather was presented -> go on
                    cityCounter++;
                    itemCounter++;
                    cityLoop();
                }
            } else if (state == State.QUESTIONS) {
                //wait for user input (push to talk), then go on
                getMicrophoneRecorder().setRecordState(true);
                getMicrophoneRecorder().awaitInput(350);
                getMicrophoneRecorder().setRecordState(false);
                getMicrophoneRecorder().flush();
                cityLoop();
            } else if (state == State.INTRO) {
                //wait a few seconds for synchronization issues
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                countryLoop();
            } else if (state == State.SELECTION) {
                //agent asked for a city -> reactivate selection
                okButton.setEnabled(true);
            }
        }

    }

    /*
     * This method is called for each presented country
     */
    private void countryLoop() throws JMSException {
        if (countryCounter < COUNTRIES) {
            //load new country
            choosenCities.clear();
            mapChanger.loadMap(countries.get(countryCounter).getWeatherMapName());
            userGazeAtMapController.loadMap(countries.get(countryCounter).getWeatherMapName());
            addCitySelection();
            //go on with city selection
            cityLoop();
        } else {
            //experiment is over
            outro();
        }
    }

    /*
     * This method is called for each city one the map
     */
    private void cityLoop() throws JMSException {

        int currentCountryCitiesCount = CITYCOUNTLIST.get(countryCounter);
        if (cityCounter < currentCountryCitiesCount) {
            //ask for a city
            state = State.SELECTION;
            // stateSelected=true;
            askForCity();
        } else {
            //all cities were presented -> now ask about them
            if (questionCounter < currentCountryCitiesCount) {
                if (questionCounter == 0) {
                    //wait a few seconds for synchronization issues
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                    }
                    //clear map
                    getMapChanger().clearMap();
                    //questionPosing.intro();//Question Posing Intro phase
                }
                //pose a question
                state = State.QUESTIONS;
                synchronized (this) {
                    questionPosing.question(choosenCities.get(questionCounter++).toSpeech());
                }
            } else {
                //all questions were posed -> go on with next country
                cityCounter = 0;
                synchronized (this) {
                    questionCounter = 0;
                }
                choosenCities.clear();
                countryCounter++;
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                countryLoop();
            }
        }
    }

    /*
     * This method is called after a city was selected
     */
    private void citySelected() throws JMSException {
        if (choosenCities.contains(currentCity)) {
            //city was already taken
            chooseAnotherCity();
        } else {
            //add city to choosenCities
            choosenCities.add(currentCity);
            Item item = items.get(itemCounter);
            //connect city with item
            item.setCity(currentCity);
            state = State.PRESENTATION;
            //do the presentation
            weatherPresentation.execute(item);
        }
    }

    /*
     * Preselects the user-chosen city for the wizard
     */
    private void proposeCity() {
        // find this city
        int i;
        for (i = 0; i < cityList.getItemCount(); i++) {
            if (cityList.getItemAt(i).toString().equals(currentCity.toString())) {
                break;
            }
        }
        // select this city
        cityList.setSelectedIndex(i);
        output.repaint();
    }

    public LinkedList<Map> getCountries() {
        return countries;
    }

    public int getCountryCounter() {
        return countryCounter;
    }

    public int getItemCounter() {
        return itemCounter;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public MapChanger getMapChanger() {
        return mapChanger;
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
        String textString = "Hello, I'm Amber, the weather agent";
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
        String textString = "You may not choose the same city twice   Please choose another city";
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
     * Called after the end of the experiment
     */
    private void outro() {
        logger.logStop();
        //reactivate start button -> new round can be started
        startButton.setEnabled(true);
        //take out city selection of the wizard interface
        if (currentCityList != null) {
            output.remove(currentCityList);
            output.repaint();
        }
        state = State.INTRO;
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

    public MicrophoneRecorder getMicrophoneRecorder() {
        return microphoneRecorder;
    }

    public int getCurrentAgent() {
        return currentAgent;
    }

    /*
     * returns the next item to ask for during questioning phase
     */
    synchronized public Item getCurrentItem() {
        int currentCountryCitiesCount = CITYCOUNTLIST.get(countryCounter);
        return getItems().get(countryCounter * currentCountryCitiesCount + questionCounter - 1);
    }

    /*
     * Returns the cities which were chosen at the current map
     */
    public ArrayList<City> getChoosenCities() {
        return choosenCities;
    }

    public int getQuestionCounter() {
        return questionCounter;
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

    public Logger getLogger() {
        return logger;
    }

    private class StateChanger {

        State prevState = Experiment.State.UNDEFINED;

        private boolean isStateChanged(State newState) {
            boolean result = (newState == prevState) ? false : true;
            prevState = newState;
            return result;
        }
    }
}
