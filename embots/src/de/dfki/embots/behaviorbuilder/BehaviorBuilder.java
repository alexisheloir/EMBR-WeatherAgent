package de.dfki.embots.behaviorbuilder;

import de.dfki.embots.behaviorbuilder.model.BehaviorSequenceModel;
import de.dfki.embots.behaviorbuilder.model.PoseModel;
import de.dfki.embots.behaviorbuilder.view.PoseControlPanel;
import de.dfki.embots.behaviorbuilder.model.PoseSequenceModel;
import de.dfki.embots.behaviorbuilder.view.PoseSequenceTable;
import de.dfki.embots.behaviorbuilder.utility.AgentNetworker;
import de.dfki.embots.behaviorbuilder.utility.BBConstants;
import de.dfki.embots.behaviorbuilder.view.ExportPosesDialog;
import de.dfki.embots.behaviorbuilder.view.ExportSubsequenceDialog;
import de.dfki.embots.behaviorbuilder.view.HeadPane;
import de.dfki.embots.behaviorbuilder.view.MovePosesDialog;
import de.dfki.embots.behaviorbuilder.view.TimeWarpPane;
import de.dfki.embots.embrscript.EMBRElement;
import de.dfki.embots.embrscript.EMBRMorphKey;
import de.dfki.embots.embrscript.EMBRMorphTargetConstraint;
import de.dfki.embots.embrscript.EMBRPoseSequence;
import de.dfki.embots.embrscript.EMBRScript;
import de.dfki.embots.embrscript.VirtualCharacter;
import de.dfki.embots.embrscript.EMBRScriptReader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * The <strong>Behavior Builder</strong> is an interactive tool to quickly create
 * <em>behaviors</em> like gestures from a sequence of poses that are formally
 * defined in <em>EMBRScript</em>.
 *
 * @author Oliver Schoenleben
 * @author Michael Kipp
 */
@SuppressWarnings("serial")
public final class BehaviorBuilder extends JFrame implements Runnable, BBConstants
{

    public static final String VERSION = "0.9.7 Spring";
    private static final int BEHAVIOR_PLAYBACK_START = 20;
    private static final String BUTTON_ADD_BEHAVIOR = "Add";
    private static final String BUTTON_DUPLICATE_BEHAVIOR = "Dupl";
    private static final String POSE_SEQUENCE_TITLE_PREFIX = "Behavior: ";
    private static final String BEHAVIOR_SEQUENCE_TITLE_PREFIX = "File: ";
    private static final String SCRIPT_VIEW_ALL = "all";
    private static final String SCRIPT_VIEW_BEHAVIOR = "behavior";
    private static final String SCRIPT_VIEW_POSE = "pose";
    private static final String BEHAVIOR_SEQUENCE_EMPTY = "<no file>";
    private static final String TITLE_EMBRSCRIPT_PANE = "EMBRScript";
    private static final String TITLE = "Behavior Builder";
    private static final String PROPERTIES_FILE = "behaviorbuilder-user.config";
    private static final String PROP_LEXEME_DIR = "lexeme.dir";
    protected static final Logger _logger = Logger.getLogger("BehaviorBuilder");
    protected static final boolean EMBR_STATUS_OK = false;
    protected static final boolean EMBR_STATUS_BAD = true;
    private static final int TOP_LEVEL_DIVIDER_LOCATION = 280;
    protected Thread _thread;
    private AgentNetworker _agentNetworker;
    private JLabel _embrStatus = new JLabel("");
    private JLabel _modifiedStatus = new JLabel("");
    private JLabel _appStatus = new JLabel("");
    private PoseSequenceTable _behaviorTable;
    private PoseModel _currentPose;
    private PoseSequenceModel _currentBehavior;
    private BehaviorSequenceModel _behaviorSequenceModel = new BehaviorSequenceModel();
    private JLabel _embrScriptLabel = HeadPane.createTitleLabel("");
    private JTextArea _embrScriptArea = new JTextArea(TEXT_ROWS, TEXT_COLS);
    private Properties _userconfig = new Properties();
    private ButtonGroup _agentSelect;
    public boolean _wholeSeqShown, _continuous;
    private JToggleButton _wholeSeqSwitch, _contSwitch;
    private JButton _defPoseButton, _assumePoseButton;
    private JTextField _lexemeText = new JTextField();
    public PoseControlPanel _poseControlPane;
    private boolean _reindexNeeded = false;
    public Properties _conf = new Properties();
    public Action exportAction,
            quitAction,
            switchContAction,
            switchWholeseqAction,
            restructAction,
            updateLexemeAction,
            assumeAction,
            defaultPoseAction,
            sendRawAction,
            playBehaviorAction,
            addPoseAction,
            delPoseAction,
            //            newAction,
            openAction,
            saveAllAction,
            saveBehaviorAction;
    private TimeWarpPane _timeWarpPane;
    private JList _behaviorList;
    private ButtonGroup _scriptViewGroup = new ButtonGroup();
    private HeadPane _behaviorHead, _behaviorSequenceHead;
    private boolean _modified = false;

    public BehaviorBuilder()
    {
        System.out.println("\n*** Starting " + TITLE + " " + VERSION + " ***");
        _logger.log(Level.FINE, "EMBR-GUI init");
        URL url = getClass().getResource("/" + BBConstants.CONFIG_FILENAME);
        if (url != null) {
            try {
                _conf.load(url.openStream());
            } catch (IOException ex) {
            }
        } else {
            try {
                File f = new File("../config/" + BBConstants.CONFIG_FILENAME);
                _conf.load(new FileReader(f));
            } catch (Exception ex1) {
                System.out.println("ERROR: Could not read config file: " + BBConstants.CONFIG_FILENAME);
                System.exit(0);
            }
        }

        // read user properties
        String homedir = System.getProperty("user.home");
        File configFile = new File(homedir, PROPERTIES_FILE);
        try {
            _userconfig.load(new FileReader(configFile));
        } catch (IOException ex) {
            System.out.println("WARNING: Could not read user config: " + configFile);
//            Logger.getLogger(BehaviorBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }

        _behaviorTable = new PoseSequenceTable(this);
        _continuous = Boolean.parseBoolean(_conf.getProperty("default.continuous"));
        _wholeSeqShown = Boolean.parseBoolean(_conf.getProperty("default.wholeSeqShown"));
        _agentNetworker = new AgentNetworker(_conf);
        _poseControlPane = new PoseControlPanel(this, _conf);
        _currentBehavior = new PoseSequenceModel(VirtualCharacter.getDefault().getName());

        addNewPose(DEFAULT_DURATION_TO_NEW_POSE);
        createWindow();
        setEmbrStatus(
                EMBR_STATUS_BAD,
                _conf.getProperty("statusMessage.notConnected"));
        startEMBRThread();
        setAppStatus("OK");
        if (System.getProperty("os.name").toLowerCase().indexOf("mac") > -1) {
            initMac();
        }
        setVisible(true);
        clearModifed();
    }

    public PoseModel getCurrentPose()
    {
        return _currentPose;
    }

    public PoseSequenceModel getCurrentSequence()
    {
        return _currentBehavior;
    }

    private void newAction()
    {
        System.out.println("newAction");
        _currentBehavior.removeAllPoses();
//                    _currentBehavior.setLexeme(name + handedness);
        _currentPose = _currentBehavior.getPoseById(0);
        updatePose();
        _behaviorTable.updateTable();
        _behaviorSequenceModel.clear();
        setModified(false);
    }

    /**
     * Actions for a behavior sequence.
     */
    private JToolBar createBehaviorSequenceToolbar()
    {
        JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);

        // Create buttons
        JButton loadButton = new JButton(BUTTON_ADD_BEHAVIOR);
        loadButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                openAction(true);
            }
        });

        JButton duplicateButton = new JButton(BUTTON_DUPLICATE_BEHAVIOR);
        duplicateButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                System.out.println("duplicate");

                // step 1: create new EMBR pose sequence
                EMBRPoseSequence seq = new EMBRPoseSequence();
                for (PoseModel p : _currentBehavior.getPoses()) {
                    seq.addPose(p);
                }

                // step 2: create new model
                PoseSequenceModel model =
                        new PoseSequenceModel(getSelectedAgent());
                model.importSequence(seq);
                model.setLexeme(_currentBehavior.getLexeme() + "_copy");

                // step 3: adjust pose timings
                model.offset(-BehaviorSequenceModel.DEFAULT_START_TIME);
                model.setStartTime(0);

                // step 4: add to behavior list
                _behaviorSequenceModel.add(model);
                _behaviorSequenceModel.adjustTiming();
                _behaviorTable.updateTable();

                setModified(true);
            }
        });

        /*
        JButton renameButton = new JButton("Rename");
        renameButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                System.out.println("rename ...");
                String newName = JOptionPane.showInputDialog(BehaviorBuilder.this, "Please specify new behavior name:", "Behavior name",
                        JOptionPane.PLAIN_MESSAGE);
                if (newName != null) {
                    _currentBehavior.setLexeme(newName);
                    updateScript();
                    setModified(true);
                }
            }
        });
         *
         */

        // Create new sequence
        JButton newButton = new JButton("New");
        newButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                System.out.println("NEW ...");
                if (!askForSave()) {
                    newAction();
                }
            }
        });

        // Play whole sequence
        JButton playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                System.out.println("PLAY ...");
                if (_currentBehavior.size() > 0) {
                    playAll();
                }
            }
        });

        // Move behavior one up
        JButton upButton = new JButton("Up");
        upButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                System.out.println("UP");

                int index = _behaviorList.getSelectedIndex();
                if (index > 0) {
                    _behaviorSequenceModel.move(index, index - 1);
                    _behaviorSequenceModel.adjustTiming();
                }
                _behaviorList.setSelectedIndex(index - 1);
                updateScript();
                setModified(true);
            }
        });

        // Move behavior one down
        JButton downButton = new JButton("Down");
        downButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                System.out.println("DOWN");

                int index = _behaviorList.getSelectedIndex();
                if (index < _behaviorSequenceModel.getBehaviorModels().size() - 1) {
                    _behaviorSequenceModel.move(index, index + 1);
                    _behaviorSequenceModel.adjustTiming();
                }
                _behaviorList.setSelectedIndex(index + 1);
                updateScript();
                setModified(true);
            }
        });

        // Delete behavior
        JButton deleteButton = new JButton("Del");
        deleteButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                System.out.println("DEL");
                int index = _behaviorList.getSelectedIndex();
                if (index >= 0 && index < _behaviorSequenceModel.getBehaviorModels().size()) {
                    int choice =
                            JOptionPane.showConfirmDialog(BehaviorBuilder.this,
                            "Really delete this behavior?", "Delete behavior",
                            JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        if (_behaviorSequenceModel.getBehaviorModels().size() == 1) {
                            // case: only one behavior => like new
                            newAction();
                            updateScript();
                        } else {
                            // case: 2+ behaviors => delete current
                            _behaviorSequenceModel.delete(index);
                            updateScript();
                            setModified(true);
                        }
                    }
                }
            }
        });

        // Save whole behavior sequence as EMBRScript
        JButton saveButton = new JButton(saveAllAction);

        // Add buttons
        toolbar.add(playButton);
        toolbar.add(createSeparator());
        toolbar.add(newButton);
        toolbar.add(loadButton);
        toolbar.add(duplicateButton);
//        toolbar.add(renameButton);
        toolbar.add(upButton);
        toolbar.add(downButton);
        toolbar.add(deleteButton);
        toolbar.add(createSeparator());
        toolbar.add(saveButton);
        return toolbar;
    }

    private Border createTopPaneBorder()
    {
        return BorderFactory.createBevelBorder(BevelBorder.LOWERED);
    }

    private void createActions(int mask)
    {
        final JFrame win = this;
        exportAction = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // use current working directory if no property is found
                String defDir = _userconfig.getProperty(PROP_LEXEME_DIR, System.getProperty("user.dir"));
                int fromPose = _currentPose.getId();
                int toPose = _currentBehavior.size() - 1;
                String defFile = _currentBehavior.getLexeme() + "_" + fromPose + "-" + toPose + ".embr";
                ExportSubsequenceDialog d = ExportSubsequenceDialog.show(win,
                        fromPose, toPose,
                        defDir, defFile);
                if (!d.cancelled) {
                    if (d.saveFileName.exists()) {
                        int owr = JOptionPane.showConfirmDialog(win, ""
                                + "The file you chose does already exist."
                                + "Do you want this file to be overwritten?",
                                "File exists",
                                JOptionPane.YES_NO_OPTION);
                        if (owr == JOptionPane.NO_OPTION) {
                            System.out.println("Export cancelled");
                            return;
                        }
                    }
                    // ready for saving: remember directory
                    _userconfig.put(PROP_LEXEME_DIR, d.saveFileName.getParent());
                    saveBehavior(d.saveFileName, d.fromPose, d.toPose);
                }
            }
        };
        enrichAction(exportAction, "Export",
                KeyStroke.getKeyStroke(KeyEvent.VK_E,
                mask));

        quitAction = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                exit();
            }
        };
        enrichActionFromProperties(quitAction, "QuitApp");
        quitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, mask));

        switchContAction = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                _continuous = !_continuous;
                _contSwitch.setSelected(_continuous);
                _assumePoseButton.setEnabled(!_continuous);
            }
        };
        enrichActionFromProperties(switchContAction, "ContSwitch");
        assumeAction = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                assumePose(false);
            }
        };
        enrichActionFromProperties(assumeAction, "AssumePose");
        defaultPoseAction = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                _currentPose.initToDefaults();
                _currentPose.useAll();
                assumePose(false);
                _currentPose.useDefaults();
                _poseControlPane.updateControlPanel(_currentPose);
                //send(_currentPose.toPoseScript(DEFAULT_AGENT_CHARACTER), true);
            }
            //~TODO one could maybe use (also) a version that just sends the default
            //      pose to the agent, but does not manipulate the sequence.
            //~TODO one could maybe use (also) a version that just sends the default
            //      pose to the agent, but does not manipulate the sequence.
        };
        enrichActionFromProperties(defaultPoseAction, "DefPose");
        addPoseAction = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                addNewPose(1);
            }
        };
        addPoseAction.putValue(Action.NAME, "Duplicate pose");

        restructAction = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                _currentBehavior.reindexSequence();
                _behaviorTable.updateTable();
            }
        };
        enrichActionFromProperties(restructAction, "Restructure");

        playBehaviorAction = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                BehaviorBuilder.this.sendBehavior();
            }
        };
        enrichActionFromProperties(playBehaviorAction, "PerformSeq");
        playBehaviorAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, mask));
        playBehaviorAction.setEnabled(true);

        saveBehaviorAction = new AbstractAction("Save behavior")
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                saveBehaviorDialog();
            }
        };

        saveAllAction = new AbstractAction("Save")
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                saveAllDialog();
            }
        };
        saveAllAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, mask));


        openAction = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                openAction(false);
            }
        };
        enrichActionFromProperties(openAction, "OpenES");
        openAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, mask));

        /*
        newAction = new AbstractAction()
        {

        @Override
        public void actionPerformed(ActionEvent e)
        {
        newAction();
        }
        };
        enrichActionFromProperties(newAction, "NewLex");
        newAction.putValue(Action.ACCELERATOR_KEY,
        KeyStroke.getKeyStroke(KeyEvent.VK_N, mask));
         *
         */

        updateLexemeAction = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                _currentBehavior.setLexeme(_lexemeText.getText());
            }
        };
        enrichActionFromProperties(updateLexemeAction,
                "LexemeTF");

        sendRawAction = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                BehaviorBuilder.this.sendText();
            }
        };
        enrichActionFromProperties(sendRawAction,
                "SendRaw");

        delPoseAction = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                delCurrentPose();
            }
        };
        enrichActionFromProperties(delPoseAction,
                "DelPose");
    }

    /**
     * Pane where the raw EMBRScript is shown and can be edited.
     */
    private JPanel createEMBRScriptPane()
    {
        JPanel scriptPanel = new JPanel(new BorderLayout());
        scriptPanel.setBorder(createTopPaneBorder());


        JScrollPane scriptPane = new JScrollPane(_embrScriptArea);
        _embrScriptArea.setFont(TEXT_FONT);
        _embrScriptArea.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        scriptPanel.add(_embrScriptLabel, BorderLayout.NORTH);
        scriptPanel.add(scriptPane, BorderLayout.CENTER);
        scriptPane.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        // toolbar
        JToolBar embrScriptToolbar = new JToolBar("Script Toolbar", JToolBar.HORIZONTAL);
        JRadioButton allButton = new JRadioButton(SCRIPT_VIEW_ALL);
        JRadioButton behaviorButton = new JRadioButton(SCRIPT_VIEW_BEHAVIOR, true); // default
        JRadioButton poseButton = new JRadioButton(SCRIPT_VIEW_POSE);
        allButton.setActionCommand(SCRIPT_VIEW_ALL);
        behaviorButton.setActionCommand(SCRIPT_VIEW_BEHAVIOR);
        poseButton.setActionCommand(SCRIPT_VIEW_POSE);
        _scriptViewGroup.add(allButton);
        _scriptViewGroup.add(behaviorButton);
        _scriptViewGroup.add(poseButton);

        ActionListener viewScriptListener = new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                System.out.println("selection: "
                        + _scriptViewGroup.getSelection().getActionCommand());
                updateScript();
            }
        };

        allButton.addActionListener(viewScriptListener);
        behaviorButton.addActionListener(viewScriptListener);
        poseButton.addActionListener(viewScriptListener);


        _wholeSeqSwitch = new JCheckBox(switchWholeseqAction);
        _wholeSeqSwitch.setOpaque(false);
        _wholeSeqSwitch.setSelected(_wholeSeqShown);


        JButton sendRawButton = new JButton(sendRawAction);
        sendRawButton.setOpaque(false);

        //embrScriptToolbar.add(_wholeSeqSwitch);

        embrScriptToolbar.add(allButton);
        embrScriptToolbar.add(behaviorButton);
        embrScriptToolbar.add(poseButton);

        embrScriptToolbar.add(sendRawButton);
        scriptPanel.add(new HeadPane(TITLE_EMBRSCRIPT_PANE, embrScriptToolbar), BorderLayout.NORTH);
        return scriptPanel;
    }

    private JPanel createBehaviorSequencePane()
    {
        JPanel p = new JPanel();
        p.setBorder(createTopPaneBorder());


        // Behavior list
        _behaviorList = new JList(_behaviorSequenceModel);
        _behaviorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _behaviorList.addListSelectionListener(new ListSelectionListener()
        {

            @Override
            public void valueChanged(ListSelectionEvent lse)
            {
                if (!lse.getValueIsAdjusting()) {
                    int index = _behaviorList.getSelectedIndex();
                    if (index >= _behaviorSequenceModel.getBehaviorModels().size()) {
                        index = Math.max(0, _behaviorSequenceModel.getBehaviorModels().size() - 1);
                    }
                    _currentBehavior = _behaviorSequenceModel.get(index);
                    selectPose(_currentBehavior.getPoseById(0));
                    _behaviorTable.updateTable();

                    _behaviorHead.setTitle(POSE_SEQUENCE_TITLE_PREFIX + _currentBehavior.getLexeme());
                }
            }
        });
        JScrollPane lexemeScrollPane = new JScrollPane(_behaviorList);
        lexemeScrollPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        JToolBar toolbar = createBehaviorSequenceToolbar();

        // Head
        _behaviorSequenceHead = new HeadPane(BEHAVIOR_SEQUENCE_TITLE_PREFIX
                + BEHAVIOR_SEQUENCE_EMPTY, toolbar);

        // Layout
        p.setLayout(new BorderLayout());
        p.add(BorderLayout.NORTH, _behaviorSequenceHead);
        p.add(BorderLayout.CENTER, lexemeScrollPane);
        return p;
    }

    private JPanel createPoseControlPane()
    {
        // Pose control panel
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(createTopPaneBorder());
        _poseControlPane.updateControlPanel(_currentPose);
        _poseControlPane.setBorder(BorderFactory.createEmptyBorder(4, 5, 4, 5));
        p.add(_poseControlPane, BorderLayout.CENTER);
        JToolBar poseToolbar = new JToolBar("Pose Toolbar", JToolBar.HORIZONTAL);
        _assumePoseButton = new JButton(assumeAction);
        _assumePoseButton.setOpaque(false);
        _assumePoseButton.setEnabled(false);
        _contSwitch = new JCheckBox(switchContAction);
        _contSwitch.setOpaque(false);
        _contSwitch.setSelected(_continuous);
        _defPoseButton = new JButton(defaultPoseAction);
        _defPoseButton.setForeground(new Color(0xE05010));
        _defPoseButton.setOpaque(false);
        poseToolbar.add(_contSwitch);
        poseToolbar.add(_assumePoseButton);
        poseToolbar.add(createSeparator());
        poseToolbar.add(_defPoseButton);
        p.add(createHeadPane(_conf.getProperty("pane.controls.title"), poseToolbar), BorderLayout.NORTH);
        p.setPreferredSize(new Dimension(650, 400));
        p.setMinimumSize(p.getPreferredSize());

        return p;
    }

    private JPanel createStatusBar()
    {
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 12, 2));
        statusPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        statusPanel.add(_appStatus);
        statusPanel.add(_embrStatus);
        statusPanel.add(_modifiedStatus);
        return statusPanel;
    }

    private void initMac()
    {
        // Catch CMD+Q short-cut to make sure program exits cleanly
        try {
            OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod(
                    "exit", (Class[]) null));
//            OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod(
//                    "info", (Class[]) null));
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    protected void startEMBRThread()
    {
        _thread = new Thread(this);
        _thread.start();
    }

    /**
     * Runs the interactive Program in a loop, also constantly checking the
     * health of the socket connection to the agent.
     */
    @Override
    public void run()
    {
        // create update action
        Runnable reindexAction = new Runnable()
        {

            @Override
            public void run()
            {
                System.out.println("THREAD START: reindexing");
                _currentBehavior.reindexSequence();
                _behaviorSequenceModel.adjustTiming();
                _behaviorTable.updateTable();
                _reindexNeeded = false;
                updateScript();
                System.out.println("THREAD END: reindexing");
            }
        };

        // Checking the connection (pulse)
        while (true) {
            if (_reindexNeeded) {
                // make sure this is running in Swing's event-dispatch thread
                EventQueue.invokeLater(reindexAction);
                /*
                _currentBehavior.reindexSequence();
                _behaviorTable.updateTable();
                _reindexNeeded = false;
                updateScript();
                 *
                 */

            }
            boolean connected = _agentNetworker.checkConnection();
            setEmbrStatus(
                    connected ? EMBR_STATUS_OK : EMBR_STATUS_BAD,
                    connected
                    ? _conf.getProperty("statusMessage.isConnected")
                    : _conf.getProperty("statusMessage.notConnected"));
//            playBehaviorAction.setEnabled(connected);
        }
    }

    /**
     * Cleans up before leaving: quits the thread and disposes the window.
     */
    protected void exit()
    {
        if (askForSave()) {
            return;
        }
        String homedir = System.getProperty("user.home");
        File file = new File(homedir, PROPERTIES_FILE);
        try {
            _userconfig.store(new FileWriter(file), TITLE + " " + VERSION
                    + " User configuration");
            System.out.println("Saved user config in " + file);
        } catch (IOException ex) {
            Logger.getLogger(BehaviorBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        _thread.interrupt();
        dispose();
        System.out.println("\n*** Terminated " + TITLE + " " + VERSION + " ***");
        System.exit(0);
    }

    /**
     * Sends a script in correct string representation to the agent,
     * and updates the pose in the Poser GUI, if specified.
     *
     * @param text EMBRScript as text
     */
    public void send(String text)
    {
        //System.out.println("+++ SENDING +++\n\n" + text);
        _agentNetworker.send(text, _continuous);
    }

    /**
     * Sends a specific EMBRElement to the agent.
     *
     * @param obj EMBRElement object
     */
    public void send(EMBRElement obj)
    {
        send(obj.toScript());
    }

    /**
     * Sends the current pose to the agent.
     */
    public void assumePose(boolean quickly)
    {

        if (quickly && _agentSelect != null) {
            send(_currentPose.toSendableSequenceScript(getSelectedAgent()));
        } else {
            send(_currentBehavior.assembleQuickSequence(_currentPose));
        }
    }

    private String createOverallScript()
    {
//        long now = System.currentTimeMillis();
        StringBuilder buf = new StringBuilder();
        buf.append("TIME_RESET\n");
        for (PoseSequenceModel ps : _behaviorSequenceModel.getBehaviorModels()) {
            EMBRPoseSequence seq = ps.createPoseSequence(0, -1);
            seq.setRelativeTimes(false);
            buf.append(seq.toScript());
            buf.append("\n");
        }
//        System.out.println("DELTA createOverallScript: " + (System.currentTimeMillis()-now));
        return buf.toString();
    }

    private void playAll()
    {
        send(createOverallScript());
    }

    /**
     * Creates a script with timings moved to front.
     *
     * @param fromPose first pose (starts with 0)
     * @param toPose last pose (use -1 for "all")
     * @return
     */
    private String getSingleBehavior(int fromPose, int toPose)
    {
        EMBRPoseSequence seq = _currentBehavior.createPoseSequence(fromPose, toPose);
        EMBRScript script = new EMBRScript(seq);

        // make sure to start right away:
        long offset = -seq.startTime + BEHAVIOR_PLAYBACK_START;
        script.offset(offset);
        String result = script.createScript(true);

        // revert offset (because this would affect the contained poses)
        script.offset(-offset);
        return result;
    }

    /**
     * Sends the single behavior to the agent, so it be carried out instantly.
     */
    public void sendBehavior()
    {
//        EMBRScript script = getSingleBehavior(0, -1);
//        String scriptString = script.createScript(true);
//        System.out.println("++++++\n" + scriptString);
        send(getSingleBehavior(0, -1));
    }

    /**
     * Send the content of the EMBRScript textarea to the agent,
     * so it be carried out instantly.
     * The textarea is editable, so this can be used for experimenting
     * and debugging.
     */
    public void sendText()
    {
        send(_embrScriptArea.getText());
    }

    /**
     * Generates and fetches the sequence script out of the sequence
     * table and the there defined poses.
     *
     * Possible manual editings of the script in the EMBRScript textarea
     * will be ignored in this operation.
     *
     * @param forSaving if true, applies time warp that is set in the GUI to
     * start and hold times of poses
     */
    protected String getSequenceScript(boolean forSaving, int fromPose, int toPose)
    {
        if (!forSaving) {
            return _currentBehavior.createPoseSequence(fromPose, toPose).toScript();
        } else {
            // ignore time warp
            _currentBehavior.setUseTimeWarpFactors(false);
            EMBRPoseSequence seq = _currentBehavior.createPoseSequence(fromPose, toPose);

            // compute offset
            int offset = 0;
            if (fromPose > 0) {
                offset = (int) (_currentBehavior.getPoseById(0).getTime()
                        - _currentBehavior.getPoseById(fromPose).getTime());
            }
            seq.offset(offset);
            String script = seq.toScript();

            // correct offset
            seq.offset(-offset);

            // reassert time warp
            _currentBehavior.setUseTimeWarpFactors(true);
            return script;
        }
    }

    /**
     * Updates the EMBRScript in the TextArea of the GUI.
     *
     * @param showSequence whether the script should be re-assembled beforehand
     */
    public void updateScript()
    {
        long now = System.currentTimeMillis();
        if (_scriptViewGroup.getSelection() != null) {
            String viewType = _scriptViewGroup.getSelection().getActionCommand();
            String text;
            if (viewType.equals(SCRIPT_VIEW_ALL)) {
                text = createOverallScript();
            } else if (viewType.equals(SCRIPT_VIEW_BEHAVIOR)) {
                text = getSequenceScript(false, 0, -1);
            } else {
                text = _currentPose.toScript();
            }
            _embrScriptArea.setText(text);
            _embrScriptArea.setCaretPosition(0);
        }
        System.out.println("DELTA updateScript: " + (System.currentTimeMillis() - now));
    }

    /**
     * Save single behavior (lexeme/gloss). Allows to specify start/end
     * poses for partial export.
     *
     * @param file Target file.
     */
    protected void saveBehavior(File file, int fromPose, int toPose)
    {
        FileWriter w = null;
        try {
            w = new FileWriter(file);
            //w.write(getSequenceScript(true, fromPose, toPose));
            w.write(getSingleBehavior(fromPose, toPose));
            clearModifed();
            System.out.println("Wrote single behavior to " + file);
        } catch (IOException x) {
            alert("File could NOT be saved (IOException).");
        } finally {
            if (null != w) {
                try {
                    w.close();
                } catch (IOException x) {
                    x.printStackTrace();
                }
            }
        }
    }

    /**
     * Save whole behavior list.
     *
     * @param file Target file.
     */
    protected void saveAll(File file)
    {
        FileWriter w = null;
        try {
            w = new FileWriter(file);
            w.write(createOverallScript());
            clearModifed();
            System.out.println("Wrote script to " + file);
        } catch (IOException x) {
            alert("File could NOT be saved (IOException).");
        } finally {
            if (null != w) {
                try {
                    w.close();
                } catch (IOException x) {
                    x.printStackTrace();
                }
            }
        }
    }

    /**
     * Show a save dialog to let the user destin the file to
     * write the generated EMBRScript to.
     * Asks user permission before overriding an existing file.
     *
     * @return True is dialog was cancelled!
     */
    protected boolean saveBehaviorDialog()
    {
        File selectedFile = null;
        while (true) {
            JFileChooser fc = new JFileChooser();

            // use current working directory if no property is found
            String dir = _userconfig.getProperty(PROP_LEXEME_DIR, System.getProperty("user.dir"));
            fc.setCurrentDirectory(new File(dir));

            fc.setDialogTitle(_conf.getProperty("filechooser.saveDialog.title"));
            fc.setSelectedFile(new File(_currentBehavior.getLexeme() + ".embr"));


            int res = fc.showSaveDialog(this);


            if (res != JFileChooser.APPROVE_OPTION) {
                return true;


            }
            selectedFile = fc.getSelectedFile();


            if (!selectedFile.exists()) {
                break;


            }
            int owr = JOptionPane.showConfirmDialog(this, ""
                    + "The file you chose does already exist."
                    + "Do you want this file to be overwritten?",
                    "File exists",
                    JOptionPane.YES_NO_OPTION);

            if (owr == JOptionPane.YES_OPTION) {
                break;
            }
        }

        // ready for saving: remember directory
        _userconfig.put(PROP_LEXEME_DIR, selectedFile.getParent());

        // save script
        saveBehavior(selectedFile, 0, -1);
        return false;
    }

    protected boolean saveAllDialog()
    {
        File selectedFile = null;


        while (true) {
            JFileChooser fc = new JFileChooser();

            // use current working directory if no property is found
            String dir = _userconfig.getProperty(PROP_LEXEME_DIR, System.getProperty("user.dir"));
            fc.setCurrentDirectory(new File(dir));

            fc.setDialogTitle(_conf.getProperty("Save all"));
            fc.setSelectedFile(new File(dir, _behaviorSequenceModel.getName() + ".embr"));
            int res = fc.showSaveDialog(this);
            if (res != JFileChooser.APPROVE_OPTION) {
                return true;
            }
            selectedFile = fc.getSelectedFile();

            if (!selectedFile.exists()) {
                break;
            }
            int owr = JOptionPane.showConfirmDialog(this, ""
                    + "The file you chose does already exist."
                    + "Do you want this file to be overwritten?",
                    "File exists",
                    JOptionPane.YES_NO_OPTION);
            if (owr == JOptionPane.YES_OPTION) {
                break;
            }
        }
        // ready for saving: remember directory
        _userconfig.put(PROP_LEXEME_DIR, selectedFile.getParent());

        // save script
        saveAll(selectedFile);
        return false;
    }

    /*
    protected void newAction()
    {
    new NewLexemeDialog(this).appear();


    }
     *

     *
     *
     */
    /**
     * @deprecated 
     * @param name
     * @param handedness
     */
    public void newLexemeInitialized(String name, String handedness)
    {
        System.out.println("newLexeme");
        _currentBehavior.removeAllPoses();
        _currentBehavior.setLexeme(name + handedness);
        _currentPose = _currentBehavior.getPoseById(0);
        updatePose();
        _behaviorTable.updateTable();


    }

    /**
     * @return true if cancelled, otherwise false
     */
    private boolean askForSave()
    {
        if (isModified()) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Your behavior sequence is modified. Do you want to save it first?", "Modified poses",
                    JOptionPane.YES_NO_CANCEL_OPTION);


            if (choice == JOptionPane.CANCEL_OPTION) {
                return true;


            }
            if (choice == JOptionPane.YES_OPTION) {
                boolean cancelled = saveAllDialog();


                if (cancelled) {
                    return true;


                }
            }
        }
        return false;


    }

    /**
     * Shows open dialog and opens script.
     *
     * @param add if true, lexemes are added instead of asserted as new
     */
    protected void openAction(boolean add)
    {
        if (!add) {
            // only ask if not adding
            if (askForSave()) {
                return;


            }
        }
        File selectedFile = null;


        while (true) {
            JFileChooser fc = new JFileChooser();

            // use current working directory if no property is found
            String dir = _userconfig.getProperty(PROP_LEXEME_DIR,
                    System.getProperty("user.dir"));
            fc.setDialogTitle(_conf.getProperty("filechooser.openDialog.title"));
            fc.setCurrentDirectory(new File(dir));


            int res = fc.showOpenDialog(this);


            if (res != JFileChooser.APPROVE_OPTION) {
                return;


            }
            selectedFile = fc.getSelectedFile();


            if (!selectedFile.canRead()) {
                alert("Cannot read file " + selectedFile);


            } else {
                _userconfig.setProperty(PROP_LEXEME_DIR,
                        selectedFile.getParent().toString());


                break;


            }

        }
        openEMBRScriptFile(selectedFile, add);


    }

    /**
     * Reads an EMBRScript file into the editor so the gesture can be edited.
     */
    protected void openEMBRScriptFile(File f, boolean add)
    {
        EMBRScriptReader r = new EMBRScriptReader();
        List<EMBRPoseSequence> lexemes;


        try {
            lexemes = r.readLexemesToList(f);
            if (!add) {
                _behaviorSequenceModel.clear();
                _currentBehavior = null;


            }

            for (EMBRPoseSequence seq : lexemes) {
//                System.out.println("process lexeme");
                PoseSequenceModel lexemeModel =
                        new PoseSequenceModel(getSelectedAgent());
                lexemeModel.importSequence(seq);
                _behaviorSequenceModel.add(lexemeModel);
            }
            _behaviorSequenceModel.adjustTiming();

            if (add) {
                _behaviorList.setSelectedIndex(_behaviorSequenceModel.getSize() - 1);
                _currentBehavior =
                        _behaviorSequenceModel.getBehaviorModels().get(_behaviorSequenceModel.getSize() - 1);
            } else {
                _behaviorList.setSelectedIndex(0);
                _currentBehavior =
                        _behaviorSequenceModel.getBehaviorModels().get(0);
            }
        } catch (IOException x) {
            alert("Can't read file. Sorry!");
            x.printStackTrace(System.err); //~ log better?


            return;


        }

        // file has read successfully
        _behaviorTable.updateTable();
        selectPose(_currentBehavior.getPoseById(0));
        setModified(true);
        updateModfiedStatus();
        _behaviorSequenceHead.setTitle(BEHAVIOR_SEQUENCE_TITLE_PREFIX + f.getName());
    }

    /**
     * Reads an EMBRScript file into the editor so the gesture can be edited.
     * @deprecated
     */
    protected void openScriptOld(File f)
    {
        EMBRScriptReader r = new EMBRScriptReader();
        List<EMBRPoseSequence> lexemes;
        EMBRPoseSequence poseSequence = null;


        try {
            lexemes = r.readLexemesToList(f);
            poseSequence = lexemes.get(lexemes.size() - 1);
            poseSequence.character = _agentSelect.getSelection().getActionCommand();



            if (lexemes.size() != 1) {
                alert("Error:"
                        + " Currently, only files are supported, that contain"
                        + " exactly one pose sequence."
                        + " Aborting load operation.");


                return;


            }

        } catch (IOException x) {
            alert("Can't read file. Sorry!");
            x.printStackTrace(System.err); //~ log better?
            return;


        }
        if (null == poseSequence) {
            alert("Error:"
                    + " Could not extract the pose sequence for some reason.");


            return;


        }

        // file has read successfully
        _currentBehavior.importSequence(poseSequence);
        _behaviorTable.updateTable();
        selectPose(
                _currentBehavior.getPoseById(0));
        clearModifed();


    }

    private void setModified(boolean val)
    {
        _modified = val;
    }

    private boolean isModified()
    {
        return _modified;
    }

    /**
     * Apply the changes made in the UI to the PoseModel. In continuous
     * mode, also send it to the agent.
     */
    public void updatePose()
    {
//        System.out.println("updatePose");
        _currentPose.extractCurrentPoseConstraints();
        _currentPose.setModified(true);
        setModified(true);
        updateModfiedStatus();

        if (_continuous) {
            assumePose(true); //~ should probably imply updatePose()


        }
        updateScript();
        _lexemeText.setText(_currentBehavior.getLexeme());


    }

    /**
     * Called when table selection is changed by the user.
     */
    public void selectPose(PoseModel pose)
    {
//        System.out.println("selectPose");
        _currentPose = pose;
        _poseControlPane.setCurrentPose(_currentPose);
        updatePose();
        _poseControlPane.updateControlPanel(_currentPose);
//        _behaviorTable.updateTable();
        restoreMorphs();


    }

    protected void addNewPose(long dur)
    {
        PoseModel newPose = _currentBehavior.createPoseModel(_currentPose, dur);
        selectPose(newPose);
        _behaviorTable.updateTable();
    }

    protected void delCurrentPose()
    {
        int id = _currentPose.getId();

        if (!_currentBehavior.removePose(_currentPose)) {
            //throw new RuntimeException("Could not remove pose with ID " + id);
            System.err.println("Could not remove pose with ID " + id);
            System.err.println(_currentBehavior);
        } //_seqTable.updateTable();
        if (_currentBehavior.size() <= id) {
            id--;
        }
        selectPose(_currentBehavior.getPoseById(id));
        _behaviorTable.updateTable();
    }

    public void scheduleReindexing()
    {
        _reindexNeeded = true;
    }

    /** Show a popup alert message. */
    public void alert(Object msg)
    {
        JOptionPane.showMessageDialog(this, msg);
    }

    /**
     * Set the message for the EMBR status field.
     * @param status whether it is good news (displayed green)
     * @param msg the EMBR status text
     */
    protected void setEmbrStatus(boolean status, String msg)
    {
        _embrStatus.setForeground(status == EMBR_STATUS_OK ? COLOR_STATUSLINE_GOOD : COLOR_STATUSLINE_BAD);
        _embrStatus.setText(msg);


    }

    /** Gets the message of the EMBR status field. */
    protected String getEmbrStatus()
    {
        return _embrStatus.getText();


    }

    /** Sets the message for the general purpose status field.
     * @param msg the message to show
     */
    protected void setAppStatus(String msg)
    {
        _appStatus.setForeground(Color.BLUE);
        _appStatus.setText(msg);


    }

    /** Gets the message for the general purpose status field. */
    protected String getAppStatus()
    {
        return _appStatus.getText();


    }

    protected void updateModfiedStatus()
    {
        _modifiedStatus.setForeground(Color.RED);
        _modifiedStatus.setText(
                _currentBehavior.isModified()
                ? _conf.getProperty("statusMessage.posesModified")
                : "");


    }

    private void clearModifed()
    {
        _currentBehavior.setModified(false);
        updateModfiedStatus();
        setModified(false);


    }

    /**
     * Sets up window.
     * Calls {@link #createUI}
     */
    protected void createWindow()
    {
        setTitle(TITLE + " " + VERSION);
        setDefaultCloseOperation(
                DO_NOTHING_ON_CLOSE); //~ use listener!
        addWindowListener(
                new WindowAdapter()
                {

                    @Override
                    public void windowClosing(WindowEvent ev)
                    {
                        exit();


                    }
                });

        setLayout(
                new BorderLayout());
        createUI();

        pack();


    }

    /**
     * On-the-fly creation of a pane's header field comprising
     * - a title
     * - a toolbar
     *
     * Recommended use (as by {@link #createUI(java.awt.Container)}):
     *   <code>aPanel.add(createHeadPane(paneTitle, predefinedToolbar), BorderLayout.NORTH)</code>
     *
     * If you want to change the appearence/layout of the panes headings,
     * you shoeld most probably change it here.
     *
     * @param title desired title
     * @param toolbar an existing toolbar
     */
    public static JComponent createHeadPane(String title, JToolBar toolbar)
    {
        return new HeadPane(title, toolbar);
        /*
        JComponent r;

        r = new JPanel(new BorderLayout());
        r.add(createTitleLabel(title), BorderLayout.NORTH);
        if (toolbar != null) {
        r.add(toolbar, BorderLayout.CENTER);
        }

        if (toolbar != null) {
        toolbar.setBorderPainted(HEAD_PANE_DRAW_TB_BORDER);
        }
        return r;
         * 
         */
    }

    /**
     * On-the-fly creation of a horizontal separator, to use e.g. in toolbars.
     *
     * @param width the separation gap width
     * @return the created separator
     */
    public static JComponent createSeparator()
    {
        JLabel label = new JLabel("   ");
        return label;
    }

    /**
     * Attaches attributes to action.
     *
     * The attributes are determined by entries in the configuration file.
     * The following are set:
     * - title: the title (also the caption) of the corresponding control
     * - descr: a short description, typically displayed as a hover tooltip
     * - image: an image icon (if file exists) to be displayed at controls
     * - mnemo: a hotkey (a standard modifier plus the defined character)
     *
     * @param a the previously defined action to be enriched
     * @param key the decisive part of the key in the properties file
     */
    public void enrichActionFromProperties(Action a, String key)
    {
        String title = _conf.getProperty("action." + key + ".title");
        String descr = _conf.getProperty("action." + key + ".descr");

        String image = _conf.getProperty("action." + key + ".image");
        String mnemo = _conf.getProperty("action." + key + ".mnemo", "~");

        ImageIcon icon = null;//new ImageIcon(iconFileName);


        int hotKey = mnemo.length() > 0 ? (int) mnemo.charAt(0) : (int) '~';

        a.putValue(Action.NAME, title);
        a.putValue(Action.SHORT_DESCRIPTION, descr);
        a.putValue(Action.MNEMONIC_KEY, hotKey);
        a.putValue(Action.SMALL_ICON, icon);


    }

    public void enrichAction(Action a, String name, KeyStroke keyStroke)
    {
        a.putValue(Action.NAME, name);


        if (keyStroke != null) {
            a.putValue(Action.ACCELERATOR_KEY, keyStroke);


        }
    }

    private String getSelectedAgent()
    {
        return _agentSelect.getSelection().getActionCommand();
    }

    public void createMenubar()
    {

        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {

            @Override
            public void run()
            {
                JMenuBar menuBar = new JMenuBar();

                JMenu fileMenu = new JMenu("File");
                //fileMenu.add(newAction);
                fileMenu.add(openAction);
                fileMenu.add(saveAllAction);
                fileMenu.add(saveBehaviorAction);
                fileMenu.add(new JSeparator());
                fileMenu.add(exportAction);
                fileMenu.add(new JSeparator());
                fileMenu.add(quitAction);
                menuBar.add(fileMenu);

                JMenu poseMenu = new JMenu("Pose");
                poseMenu.add(defaultPoseAction);
                poseMenu.addSeparator();
                poseMenu.add(switchContAction);
                poseMenu.addSeparator();
                poseMenu.add(assumeAction);
                poseMenu.add(sendRawAction);
                menuBar.add(poseMenu);

                JMenu seqMenu = new JMenu("Sequence");
                seqMenu.add(addPoseAction);
                seqMenu.add(delPoseAction);
                seqMenu.addSeparator();
                seqMenu.add(playBehaviorAction);
                menuBar.add(seqMenu);

                JMenu viewMenu = new JMenu("View");
                viewMenu.add(switchWholeseqAction);
                menuBar.add(viewMenu);

                JMenu agentMenu = new JMenu("Agent");

                ActionListener agentSelectListener = new ActionListener()
                {

                    @Override
                    public void actionPerformed(ActionEvent ae)
                    {
                        for (PoseSequenceModel ps : _behaviorSequenceModel.getBehaviorModels()) {
                            ps.setAgentName(getSelectedAgent());


                        }
                        updateScript();


                    }
                };
                _agentSelect = new ButtonGroup();
                JRadioButtonMenuItem mi = new JRadioButtonMenuItem(VirtualCharacter.AMBER.getName());
                mi.setActionCommand(VirtualCharacter.AMBER.getName());
                agentMenu.add(mi);
                mi.addActionListener(agentSelectListener);
                _agentSelect.add(mi);
                mi = new JRadioButtonMenuItem(VirtualCharacter.ALFONSE.getName(), true);
                mi.setActionCommand(VirtualCharacter.ALFONSE.getName());
                agentMenu.add(mi);
                mi.addActionListener(agentSelectListener);
                _agentSelect.add(mi);
                mi.addActionListener(agentSelectListener);
                agentSelectListener.actionPerformed(null);

                menuBar.add(agentMenu);
                setJMenuBar(
                        menuBar);
                getJMenuBar().revalidate();


            }
        });


    }

    /**
     * Restores the "neutral" face by setting all morph targets
     * to the zero sigma.
     */
    public void clearMorphs()
    {
        PoseModel p = new PoseModel(_currentPose);
        p.morphV.clear();


        for (EMBRMorphKey k : EMBRMorphKey.values()) {
            p.morphV.add(new EMBRMorphTargetConstraint(k, 0.0));


        }
        p.extractCurrentPoseConstraints();
        send(
                p.toSendableSequenceScript(getSelectedAgent()));


    }

    public void restoreMorphs()
    {
        PoseModel p = new PoseModel(_currentPose);
        HashMap<EMBRMorphKey, Double> morphs = new HashMap<EMBRMorphKey, Double>(EMBRMorphKey.values().length);


        for (EMBRMorphKey k : EMBRMorphKey.values()) {
            morphs.put(k, 0.0);


        }
        for (int i = 0; i
                <= _currentPose.getId(); i++) {
            // ~ emtc umbenennen nach emc (Target raus!)
            for (EMBRMorphTargetConstraint c : _currentBehavior.getPoseById(i).morphV) {
                morphs.put(c.key, c.value);


            }
        }
        for (EMBRMorphKey k : morphs.keySet()) {
            p.morphV.add(new EMBRMorphTargetConstraint(k, morphs.get(k)));


        }
        p.extractCurrentPoseConstraints();


        if (_agentSelect != null) {
            send(p.toSendableSequenceScript(getSelectedAgent()));


        }
    }

    /**
     * Top method for UI creation
     */
    private void createUI()
    {
        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        createActions(
                mask);

        // Pose control pane
        JPanel posePane = createPoseControlPane();

        // EMBRScript pane
        JPanel scriptPanel = createEMBRScriptPane();

        // Lexeme pane
        JPanel lexemePanel = createBehaviorSequencePane();

        // Sequence pane
        JPanel poseSequencePane = createPoseSequencePane();

        // Status bar
        JPanel statusPanel = createStatusBar();

        // Layout
        createMenubar();

        // Bottom split pane
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                posePane, scriptPanel);
        bottomSplitPane.setContinuousLayout(true);
        bottomSplitPane.setDividerLocation(.8);

        // Top split pane
        JSplitPane topJSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                lexemePanel, poseSequencePane);
        topJSplitPane.setContinuousLayout(true);
        topJSplitPane.setDividerLocation(.4);

        // Overall split pane
        JSplitPane centerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                topJSplitPane, bottomSplitPane);
        centerPane.setContinuousLayout(true);
        centerPane.setDividerLocation(TOP_LEVEL_DIVIDER_LOCATION);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);
        getContentPane().add(centerPane, BorderLayout.CENTER);


    }

    private JToolBar createBehaviorToolbar()
    {
        JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
        final JFrame win = this;

        // Action for moving pose(s) in time
        Action pushBackAction = new AbstractAction("Push..")
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                MovePosesDialog d = new MovePosesDialog(win, true);
                d.setVisible(true);
                if (!d.cancelled) {
                    _currentBehavior.push(_currentPose, d.numPoses, d.moveDuration);
                    _behaviorSequenceModel.adjustTiming();
                    _behaviorTable.updateTable();
                }
            }
        };


        Action chunkAction = new AbstractAction("Chunk..")
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                ExportPosesDialog d = new ExportPosesDialog(win, true);
                d.init(_currentPose.getId(), _currentBehavior.size() - 1);
                d.setVisible(true);
                if (!d.cancelled) {
                    if (d.fromPose < d.toPose && d.fromPose >= 0
                            && d.toPose < _currentBehavior.size()) {

                        long previousEndTime = 0;
                        if (d.fromPose > 0) {
                            PoseModel previousPose =
                                    _currentBehavior.getPoseById(d.fromPose - 1);
                            previousEndTime = previousPose.getTime() + previousPose.getHoldDuration();
                        }

                        // step 1: create new EMBR pose sequence
                        EMBRPoseSequence seq = new EMBRPoseSequence();
                        for (int i = d.fromPose; i < d.toPose + 1; i++) {
                            seq.addPose(_currentBehavior.getPoseById(i));
                        }

                        // step 2: create new model
                        PoseSequenceModel model =
                                new PoseSequenceModel(getSelectedAgent());
                        model.importSequence(seq);
                        model.setLexeme(d.lexemeName);

                        // step 3: adjust pose timings
                        if (d.fromPose == 0) {
                            model.offset(-BehaviorSequenceModel.DEFAULT_START_TIME);
                        } else {
                            model.offset(-previousEndTime);
                        }
                        model.setStartTime(0);

                        // step 4: add to behavior list
                        _behaviorSequenceModel.add(model);

                        _behaviorSequenceModel.adjustTiming();
                        _behaviorTable.updateTable();
                    } else {
                        alert("Your parameters were not valid... Try again!");
                    }
                }
            }
        };

        Action insertPoseAction = new AbstractAction("Add pose")
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                addNewPose(DEFAULT_DURATION_TO_NEW_POSE);
            }
        };

        Action renameAction = new AbstractAction("Rename")
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                String newName = JOptionPane.showInputDialog(win, "Please specify the lexeme name",
                        _currentBehavior.getLexeme());
                if (newName != null) {
                    _currentBehavior.setLexeme(newName);
                    updateScript();
                    _behaviorHead.setTitle(POSE_SEQUENCE_TITLE_PREFIX + newName);

                    // If user has not started a sequence it is time to do so now...
                    if (_behaviorSequenceModel.getSize() == 0) {
                        _behaviorSequenceModel.add(_currentBehavior);
                    }
                }
            }
        };

        toolbar.add(playBehaviorAction);
        toolbar.add(createSeparator());
        toolbar.add(renameAction);
        toolbar.add(addPoseAction);
        toolbar.add(insertPoseAction);
        toolbar.add(pushBackAction);
        toolbar.add(chunkAction);
        toolbar.add(delPoseAction);
        toolbar.add(createSeparator());
        toolbar.add(saveBehaviorAction);
        return toolbar;
    }

    /**
     * Displays sequence of poses.
     */
    private JPanel createPoseSequencePane()
    {
        // Sequence table
        JScrollPane scrollPane = new JScrollPane(_behaviorTable);
        scrollPane.setPreferredSize(
                new Dimension(scrollPane.getPreferredSize().width,
                300));

        // Timing pane
        _timeWarpPane = new TimeWarpPane();
        _timeWarpPane.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent ce)
            {
                _currentBehavior.setTimeWarpFactors(_timeWarpPane.getTimeWarp(),
                        _timeWarpPane.getHoldFactor());
                updateScript();


            }
        });

        // Head pane
        _behaviorHead = new HeadPane(POSE_SEQUENCE_TITLE_PREFIX + "<NEW>",
                createBehaviorToolbar());

        // Layout
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(createTopPaneBorder());
        p.add(_behaviorHead, BorderLayout.NORTH);
        p.add(scrollPane, BorderLayout.CENTER);
        p.add(_timeWarpPane, BorderLayout.SOUTH);
        return p;
    }

    /**
     * Starts application.
     *
     * @param args the command line arguments (passed by the start script)
     */
    public static void main(String[] args)
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {

            @Override
            public void run()
            {
                new BehaviorBuilder();




            }
        });


    }
}
