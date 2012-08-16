package de.dfki.embots.behaviorbuilder.view;

import de.dfki.embots.behaviorbuilder.model.PoseModel;
import de.dfki.embots.behaviorbuilder.utility.ArmMirroring;
import de.dfki.embots.behaviorbuilder.utility.BBConstants;
import de.dfki.embots.behaviorbuilder.*;
import de.dfki.embots.embrscript.*;
import java.text.DecimalFormat;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Everything concerning the control panel, i.e. the panel in the center
 * of the application's frame, concerning all settings of a single pose,
 * the so-called "current pose".
 *
 * The user can make a pose can be "current" by selecting its entry in the
 * pose sequence table.
 *
 *
 *
 * @author Oliver Schoenleben
 */
@SuppressWarnings("serial")
public class PoseControlPanel extends JPanel implements BBConstants
{

    protected String CAPTION_ROTATION, CAPTION_BREATH,
            CAPTION_GAZE, CAPTION_GAZE_TARGET, CAPTION_GAZE_BGROUP,
            CAPTION_FACE_SHADE, CAPTION_FACE_MORPH, CAPTION_ORIENTATION_NORMAL,
            CAPTION_HAND_SHAPE, CAPTION_SWIVEL,
            CAPTION_HAND_POSITION, CAPTION_HAND_MIRRORING,
            CAPTION_HAND_ORIENTATION,
            CAPTION_LEFT, CAPTION_RIGHT,
            CAPTION_FREQUENCY, CAPTION_AMPLITUDE, CAPTION_INTENSITY,
            CAPTION_ADD_FACEMORPH, TOOLTIP_ADD_FACEMORPH,
            CAPTION_DEL_FACEMORPH, TOOLTIP_DEL_FACEMORPH;
    protected String TITLE_SECTION_HEADFACE_NAME,
            TITLE_SECTION_HANDARM_NAME,
            TITLE_SECTION_AUTONOMOUS_NAME,
            TITLE_SECTION_HEADFACE_TTIP,
            TITLE_SECTION_HANDARM_TTIP,
            TITLE_SECTION_AUTONOMOUS_TTIP;
    protected ImageIcon TITLE_SECTION_HEADFACE_ICON,
            TITLE_SECTION_HANDARM_ICON,
            TITLE_SECTION_AUTONOMOUS_ICON;
    private JLabel _breathFreqLabel, _breathAmplLabel, _lookAtLabel,
            _lookAtBodyGroupLabel, _shadeLabel,
            _lswivelLabel, _rswivelLabel, _lhPositionLabel, _rhPositionLabel,
            _lhOrientYLabel, _rhOrientYLabel, _lhOrientZLabel, _rhOrientZLabel,
            _shoulderLabel;
    private JCheckBox _useBreathingCB, _useGazeDirCB, _useShaderCB,
            _useLSwivelCheck, _useRSwivelCheck, _useLPosCB, _useRPosCB;
    private JCheckBox _useLhOrientYCheck = new JCheckBox("Finger dir.");
    private JCheckBox _useLhOrientZCheck = new JCheckBox("Palmtop dir.");
    private JCheckBox _useRhOrientYCheck = new JCheckBox("Finger dir.");
    private JCheckBox _useRhOrientZCheck = new JCheckBox("Palmtop dir.");
    private JCheckBox _useHeadOrientZCheck = new JCheckBox("Nose dir.");
    private JCheckBox _useHeadOrientYCheck = new JCheckBox("Headtop dir.");
    private JCheckBox _useTorsoPositionCheck = new JCheckBox("position");
    private JCheckBox _useTorsoOrientCheck = new JCheckBox("orient.");
    private JCheckBox _useShoulderCheck;
    private JComboBox _lookAtBodyGroupCombo,
            _lhShapeCombo,
            _rhShapeCombo,
            _mirrorModeCBox;
    private Slider1d _breathFreqCtrl, _breathAmplCtrl, _shadeCtrl,
            _lSwivelSliders, _rSwivelSliders, _shoulderSlider;
    private Slider3d _lookAtCtrl, _lhPosSliders, _rhPosSliders,
            _lhOrientSliders, _rhOrientSliders, _lhOrientSliders2,
            _rhOrientSliders2;
    private ArmMirroring _mirroring = new ArmMirroring();
    private FacialExpressionPanel _facialExpressionPanel;
    protected BehaviorBuilder _owner;
    protected PoseModel _poseModel;
    protected Properties _conf;
    // MK: new stuff
    private Slider3d _headOrientSliders1 = new Slider3d(MIN_LOOK_AT, MAX_LOOK_AT, DEFAULT_LOOK_AT);
    private Slider3d _headOrientSliders2 = new Slider3d(MIN_LOOK_AT, MAX_LOOK_AT, DEFAULT_LOOK_AT);
    private Slider3d _torsoPositionSliders = new Slider3d(MIN_LOOK_AT, MAX_LOOK_AT, DEFAULT_LOOK_AT);
    private Slider3d _torsoOrientSliders = new Slider3d(MIN_LOOK_AT, MAX_LOOK_AT, DEFAULT_LOOK_AT);
    private boolean _blockUpdatePose = false;

    /**
     * Constructs and initializes the control panel.
     *
     * @param owner The application to hold this
     * @param conf The configuration for this applacition (and hence this control panel)
     */
    public PoseControlPanel(BehaviorBuilder owner, Properties conf)
    {
        _owner = owner;
        _conf = conf;

        TITLE_SECTION_HEADFACE_NAME = _conf.getProperty("ctrl.sectionTitle.headAndFace.title");
        TITLE_SECTION_HANDARM_NAME = _conf.getProperty("ctrl.sectionTitle.handsAndArms.title");
        TITLE_SECTION_AUTONOMOUS_NAME = _conf.getProperty("ctrl.sectionTitle.autonomousBehaviors.title");

        TITLE_SECTION_HEADFACE_TTIP = _conf.getProperty("ctrl.sectionTitle.headAndFace.toolt");
        TITLE_SECTION_HANDARM_TTIP = _conf.getProperty("ctrl.sectionTitle.handsAndArms.toolt");
        TITLE_SECTION_AUTONOMOUS_TTIP = _conf.getProperty("ctrl.sectionTitle.autonomousBehaviors.toolt");

        TITLE_SECTION_HEADFACE_ICON = new ImageIcon(_conf.getProperty("ctrl.sectionTitle.headAndFace.image"));
        TITLE_SECTION_HANDARM_ICON = new ImageIcon(_conf.getProperty("ctrl.sectionTitle.handsAndArms.image"));
        TITLE_SECTION_AUTONOMOUS_ICON = new ImageIcon(_conf.getProperty("ctrl.sectionTitle.autonomousBehaviors.image"));


        CAPTION_ROTATION = _conf.getProperty("ctrl.caption.rotation");
        CAPTION_BREATH = _conf.getProperty("ctrl.caption.use_breath");
        CAPTION_GAZE = _conf.getProperty("ctrl.caption.use_gaze");
        CAPTION_GAZE_TARGET = _conf.getProperty("ctrl.caption.gazeTarget");
        CAPTION_GAZE_BGROUP = _conf.getProperty("ctrl.caption.gazeBGroup");
        CAPTION_FACE_SHADE = _conf.getProperty("ctrl.caption.use_shader");
        CAPTION_FACE_MORPH = _conf.getProperty("ctrl.caption.faceMorph");
        CAPTION_ORIENTATION_NORMAL = _conf.getProperty("ctrl.caption.orientNormal");
        CAPTION_HAND_SHAPE = _conf.getProperty("ctrl.caption.handShape");
        CAPTION_SWIVEL = _conf.getProperty("ctrl.caption.use_swivel");
        CAPTION_HAND_POSITION = _conf.getProperty("ctrl.caption.use_handPos");
        CAPTION_HAND_MIRRORING = _conf.getProperty("ctrl.caption.use_handPosMirror");
        CAPTION_HAND_ORIENTATION = _conf.getProperty("ctrl.caption.use_handOri");
        CAPTION_LEFT = _conf.getProperty("ctrl.caption.gen_left");
        CAPTION_RIGHT = _conf.getProperty("ctrl.caption.gen_right");
        CAPTION_FREQUENCY = _conf.getProperty("ctrl.caption.breathFreq");
        CAPTION_AMPLITUDE = _conf.getProperty("ctrl.caption.breathAmpl");
        CAPTION_INTENSITY = _conf.getProperty("ctrl.caption.gen_intensity");
        CAPTION_ADD_FACEMORPH = _conf.getProperty("ctrl.caption.add_faceMorph");
        TOOLTIP_ADD_FACEMORPH = _conf.getProperty("ctrl.tooltip.add_faceMorph");
        CAPTION_DEL_FACEMORPH = _conf.getProperty("ctrl.caption.del_faceMorph");
        TOOLTIP_DEL_FACEMORPH = _conf.getProperty("ctrl.tooltip.del_faceMorph");

        _mirrorModeCBox = new JComboBox(_mirroring.getPossibleModes());
        _mirrorModeCBox.setSelectedItem(_mirroring.getMode());

        _facialExpressionPanel = new FacialExpressionPanel(_owner, _conf);
        _useBreathingCB = createCaptionCB(CAPTION_BREATH);
        _breathFreqLabel = new JLabel();
        _breathFreqCtrl = new Slider1d(0.0, 1.0, DEFAULT_BREATHING_FREQUENCY);
        _breathAmplLabel = new JLabel();
        _breathAmplCtrl = new Slider1d(0.0, 1.0, DEFAULT_BREATHING_AMPLITUDE);
        _useGazeDirCB = createCaptionCB(CAPTION_GAZE);
        _lookAtLabel = createCoordinateLabel();
        _lookAtCtrl = new Slider3d(MIN_LOOK_AT, MAX_LOOK_AT, DEFAULT_LOOK_AT);
        _lookAtBodyGroupLabel = new JLabel(DEFAULT_LOOK_AT_BODY_GROUP.toScript());
        _lookAtBodyGroupCombo = new JComboBox(EMBRBodyGroup.values());
        _useShaderCB = createCaptionCB(CAPTION_FACE_SHADE);
        _shadeLabel = new JLabel();
        _shadeCtrl = new Slider1d(0.0, 1.0, DEFAULT_SHADE);

        // Hand control
        _lhShapeCombo = new JComboBox(EMBRHandshape.values());
        _rhShapeCombo = new JComboBox(EMBRHandshape.values());
        _useLSwivelCheck = new JCheckBox(CAPTION_LEFT);
        _lswivelLabel = new JLabel();
        _lSwivelSliders = new Slider1d(MIN_LEFT_SWIVEL, MAX_LEFT_SWIVEL, DEFAULT_LEFT_SWIVEL);
        _useRSwivelCheck = new JCheckBox(CAPTION_RIGHT);
        _rswivelLabel = new JLabel();
        _rSwivelSliders = new Slider1d(MIN_RIGHT_SWIVEL, MAX_RIGHT_SWIVEL, DEFAULT_RIGHT_SWIVEL);

        _useLPosCB = new JCheckBox(CAPTION_LEFT);
        _lhPositionLabel = createCoordinateLabel();
        _lhPosSliders = new Slider3d("left-right", "forw-backw", "up-down",
                MIN_LEFT_HAND_POSITION, MAX_LEFT_HAND_POSITION, DEFAULT_LEFT_HAND_POSITION);
        _useRPosCB = new JCheckBox(CAPTION_RIGHT);
        _rhPositionLabel = createCoordinateLabel();
        _rhPosSliders = new Slider3d("left-right", "forw-backw", "up-down",
                MIN_RIGHT_HAND_POSITION, MAX_RIGHT_HAND_POSITION, DEFAULT_RIGHT_HAND_POSITION);

        _lhOrientYLabel = createCoordinateLabel();
        _lhOrientSliders = new Slider3d(MIN_LEFT_HAND_ORIENTATION, MAX_LEFT_HAND_ORIENTATION, DEFAULT_LEFT_HAND_ORIENTATION);
        _rhOrientYLabel = createCoordinateLabel();
        _rhOrientSliders = new Slider3d(MIN_RIGHT_HAND_ORIENTATION, MAX_RIGHT_HAND_ORIENTATION, DEFAULT_RIGHT_HAND_ORIENTATION);

        _lhOrientZLabel = createCoordinateLabel();
        _lhOrientSliders2 = new Slider3d(MIN_LEFT_HAND_ORIENTATION, MAX_LEFT_HAND_ORIENTATION, DEFAULT_LEFT_HAND_ORIENTATION);
        _rhOrientZLabel = createCoordinateLabel();
        _rhOrientSliders2 = new Slider3d(MIN_RIGHT_HAND_ORIENTATION, MAX_RIGHT_HAND_ORIENTATION, DEFAULT_RIGHT_HAND_ORIENTATION);

        createUI();
    }

    //~ Instead of formatting, one could also round the values; then,
    //  they will be round in the EMBRScript, also.
    protected static String format(double x)
    {
        DecimalFormat df = new DecimalFormat("+#.##;-#.##");
        return df.format(x);
    }

    protected static String format(Triple t)
    {
        if (true) {
            return "" + t;
        }
        return "x: " + format(t.x) + "\n"
                + "y: " + format(t.y) + "\n"
                + "z: " + format(t.z);
    }

    protected void updateLabel(JLabel l)
    {
        if (l == _breathFreqLabel) {
            _breathFreqLabel.setText(CAPTION_FREQUENCY + " " + _poseModel.breathFreq + "");
        }
        if (l == _breathAmplLabel) {
            _breathAmplLabel.setText(CAPTION_AMPLITUDE + " " + _poseModel.breathAmpl + "");
        }
        if (l == _lookAtLabel) {
            _lookAtLabel.setText("" + format(_poseModel.lookAt));
        }
        if (l == _shadeLabel) {
            _shadeLabel.setText("" + format(100 * _poseModel.shade) + "%");
        }
        // morphs were here
        if (l == _lswivelLabel) {
            _lswivelLabel.setText(CAPTION_ROTATION + format(_poseModel.lSwivel) + "°");
        }
        if (l == _rswivelLabel) {
            _rswivelLabel.setText(CAPTION_ROTATION + format(_poseModel.rSwivel) + "°");
        }
        if (l == _lhPositionLabel) {
            _lhPositionLabel.setText("" + _poseModel.lhand);
        }
        if (l == _rhPositionLabel) {
            _rhPositionLabel.setText("" + _poseModel.rhand);
        }
        if (l == _lhOrientYLabel) {
            _lhOrientYLabel.setText("" + _poseModel.lhOrientY);
        }
        if (l == _rhOrientYLabel) {
            _rhOrientYLabel.setText("" + _poseModel.rhOrientY);
        }
        if (l == _lhOrientZLabel) {
            _lhOrientZLabel.setText("" + _poseModel.lhOrientZ);
        }
        if (l == _rhOrientZLabel) {
            _rhOrientZLabel.setText("" + _poseModel.rhOrientZ);
        }
    }

    /**
     * Needed, for example, to automatically adjust the pose controls
     * after re-initializing or updating them.
     */
    public void updateControlPanel(PoseModel poseModel)
    {
        _blockUpdatePose = true;
        _facialExpressionPanel.setBlockPoseUpdate(true);


        _poseModel = poseModel;

        _useBreathingCB.setSelected(_poseModel.useBreathing);
        updateLabel(_breathFreqLabel);
        _breathFreqCtrl.setActualValue(_poseModel.breathFreq);
        updateLabel(_breathAmplLabel);
        _breathAmplCtrl.setActualValue(_poseModel.breathAmpl);

        // Gaze
        _useGazeDirCB.setSelected(_poseModel.useGazeDir);
        updateLabel(_lookAtLabel);
        _lookAtCtrl.setActualValue(_poseModel.lookAt);
        _lookAtBodyGroupLabel.setText(_poseModel.lookAtBodyGroup.toScript());
        _lookAtBodyGroupCombo.setSelectedItem(_poseModel.lookAtBodyGroup);

        // Head direction
        _useHeadOrientYCheck.setSelected(_poseModel.useHeadOrientY);
        _useHeadOrientZCheck.setSelected(_poseModel.useHeadOrientZ);
        _headOrientSliders1.setActualValue(_poseModel.headOrientZ);
        _headOrientSliders2.setActualValue(_poseModel.headOrientY);
        _torsoOrientSliders.setActualValue(_poseModel.torsoOrient);
        _torsoPositionSliders.setActualValue(_poseModel.torsoPosition);


        // Blushing
        _useShaderCB.setSelected(_poseModel.useShader);
        updateLabel(_shadeLabel);
        _shadeCtrl.setActualValue(_poseModel.shade);

        // Facial expression
        _facialExpressionPanel.updateMorphPanel();
        _facialExpressionPanel.revalidate();
        _facialExpressionPanel.repaint();


        // Hand shape
        _lhShapeCombo.setSelectedItem(_poseModel.lhShape);
        _rhShapeCombo.setSelectedItem(_poseModel.rhShape);

        // Arm swivel
        _useLSwivelCheck.setSelected(_poseModel.useLeftSwivel);
        updateLabel(_lswivelLabel);
        _lSwivelSliders.setActualValue(_poseModel.lSwivel);

        _useRSwivelCheck.setSelected(_poseModel.useRightSwivel);
        updateLabel(_rswivelLabel);
        _rSwivelSliders.setActualValue(_poseModel.rSwivel);

        // Hand position
        _useLPosCB.setSelected(_poseModel.useLPos);
        updateLabel(_lhPositionLabel);
        _lhPosSliders.setActualValue(_poseModel.lhand);

        _useRPosCB.setSelected(_poseModel.useRPos);
        updateLabel(_rhPositionLabel);
        _rhPosSliders.setActualValue(_poseModel.rhand);

        // Hand orientation
        _useLhOrientYCheck.setSelected(_poseModel.useLhOrientY);
        _useLhOrientZCheck.setSelected(_poseModel.useLhOrientZ);
        _useRhOrientYCheck.setSelected(_poseModel.useRhOrientY);
        _useRhOrientZCheck.setSelected(_poseModel.useRhOrientZ);

        updateLabel(_lhOrientYLabel);
        _lhOrientSliders.setActualValue(_poseModel.lhOrientY);

        updateLabel(_rhOrientYLabel);
        _rhOrientSliders.setActualValue(_poseModel.rhOrientY);

        updateLabel(_lhOrientZLabel);
        _lhOrientSliders2.setActualValue(_poseModel.lhOrientZ);

        updateLabel(_rhOrientZLabel);
        _rhOrientSliders2.setActualValue(_poseModel.rhOrientZ);

        // Torso
        _useTorsoOrientCheck.setSelected(_poseModel.useTorsoOrient);
        _useTorsoPositionCheck.setSelected(_poseModel.useTorsoPosition);

        // Shoulder
        _useShoulderCheck.setSelected(_poseModel.useShoulder);
        _shoulderSlider.setActualValue(_poseModel.shoulderValue);

        this.revalidate();
        this.repaint();
        _blockUpdatePose = false;
        _facialExpressionPanel.setBlockPoseUpdate(false);
        updatePose();
    }

    protected static JComponent createCaptionComponent(String caption, boolean cb)
    {
        JComponent r = cb ? new JCheckBox(caption) : new JLabel(caption);
        r.setPreferredSize(
                new Dimension(
                LINE_CAPTION_WIDTH,
                r.getPreferredSize().height));
        return r;
    }

    protected static JComponent createLeftrightSeparator()
    {
        return createSeparator(LEFTRIGHT_SEPARATOR_WIDTH);
    }

    protected static JComponent createLineSeparator()
    {
        return createSeparator(LINE_SEPARATOR_WIDTH);
    }

    protected static JComponent createSeparator(int width)
    {
        JLabel r = new JLabel();
        r.setPreferredSize(
                new Dimension(
                width, r.getPreferredSize().height));
        return r;
    }

    protected static JLabel createCaptionLabel(String caption)
    {
        return (JLabel) createCaptionComponent(caption, false);
    }

    protected static JCheckBox createCaptionCB(String caption)
    {
        return (JCheckBox) createCaptionComponent(caption, true);
    }

    public void setCurrentPose(PoseModel pose)
    {
        _poseModel = pose;
    }

    protected void createUI()
    {
        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);

        JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP);
        add(pane);
        pane.addTab(TITLE_SECTION_HANDARM_NAME, TITLE_SECTION_HANDARM_ICON,
                createArmTab(), TITLE_SECTION_HANDARM_TTIP);
        pane.addTab(TITLE_SECTION_AUTONOMOUS_NAME, TITLE_SECTION_AUTONOMOUS_ICON,
                createBehaviorTab(), TITLE_SECTION_AUTONOMOUS_TTIP);
        pane.addTab(TITLE_SECTION_HEADFACE_NAME, TITLE_SECTION_HEADFACE_ICON,
                createHeadTab(), TITLE_SECTION_HEADFACE_TTIP);
        pane.addTab("Body", null, createBodyTab(), "Controls for torso and shoulders");
    }

    private JPanel createBehaviorTab()
    {
        JPanel behaviorTab = new JPanel();
        BoxLayout autBehavBL = new BoxLayout(behaviorTab, BoxLayout.Y_AXIS);
        behaviorTab.setLayout(autBehavBL);
        // Autonomous Behaviour
        // Breathing
        JPanel breathingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        _useBreathingCB.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                _poseModel.useBreathing = _useBreathingCB.isSelected();
                updatePose();
            }
        });
        breathingPanel.add(_useBreathingCB);
        JPanel brFreqPanel = new JPanel(new BorderLayout());
        _breathFreqCtrl.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.breathFreq = _breathFreqCtrl.getActualValue();
                updateLabel(_breathFreqLabel);
                updatePose();
            }
        });
        brFreqPanel.add(_breathFreqCtrl, BorderLayout.SOUTH);
        brFreqPanel.add(_breathFreqLabel, BorderLayout.EAST);
        breathingPanel.add(brFreqPanel);
        breathingPanel.add(createLineSeparator());
        JPanel brAmplPanel = new JPanel(new BorderLayout());
        _breathAmplCtrl.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.breathAmpl = _breathAmplCtrl.getActualValue();
                updateLabel(_breathAmplLabel);
                updatePose();
            }
        });
        brAmplPanel.add(_breathAmplCtrl, BorderLayout.SOUTH);
        brAmplPanel.add(_breathAmplLabel, BorderLayout.EAST);
        breathingPanel.add(brAmplPanel);
        behaviorTab.add(breathingPanel);
        return behaviorTab;
    }

    private JPanel createArmTab()
    {
        JPanel armsTab = new JPanel();
        BoxLayout handArmBL = new BoxLayout(armsTab, BoxLayout.Y_AXIS);
        armsTab.setLayout(handArmBL);
        armsTab.setBorder(BorderFactory.createEmptyBorder(-8, 0, -8, 0));

        // Hand Position:
        JPanel handPositionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel hpLabelPanel = new JPanel(new BorderLayout(0, 4));
        // instead of:
        hpLabelPanel.add(createCaptionLabel(CAPTION_HAND_POSITION), BorderLayout.NORTH);
        hpLabelPanel.add(new JLabel(CAPTION_HAND_MIRRORING), BorderLayout.CENTER);
        hpLabelPanel.add(_mirrorModeCBox, BorderLayout.SOUTH);
        // We *could* (for all position-panels) do:
        //handPositionPanel.setBorder(BorderFactory.createTitledBorder("Der Häänd"));
        handPositionPanel.add(hpLabelPanel);
        JPanel hpCenteringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // (Left)
        JPanel lhPosPanel = new JPanel(new BorderLayout());
        _useLPosCB.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                _poseModel.useLPos = _useLPosCB.isSelected();
                updatePose();
            }
        });
        final ChangeListener leftHandChangeListener = new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.lhand = _lhPosSliders.getActualValue();
                updateLabel(_lhPositionLabel);
                if (_mirroring.getMode() != ArmMirroring.MirrorMode.NONE) {
                    _poseModel.rhand = _mirroring.correspond(_poseModel.lhand);
                    _rhPosSliders.setActualValue(_poseModel.rhand);
                }
                updatePose();
            }
        };
        _lhPosSliders.addChangeListener(leftHandChangeListener);

        // layout
        lhPosPanel.add(_useLPosCB, BorderLayout.WEST);
        lhPosPanel.add(_lhPosSliders, BorderLayout.SOUTH);
        lhPosPanel.add(_lhPositionLabel, BorderLayout.EAST);
        hpCenteringPanel.add(lhPosPanel);

        // (Mirror Lock)
        _mirrorModeCBox.addItemListener(new ItemListener()
        {

            @Override
            public void itemStateChanged(ItemEvent v)
            {
                switch ((ArmMirroring.MirrorMode) _mirrorModeCBox.getSelectedItem()) {
                    case MIRROR:
                        _mirroring.setModeToMirror(_owner.getCurrentPose().lhand, _owner.getCurrentPose().rhand);
                        break;
                    case ALTERNATE:
                        _mirroring.setModeToAlternate(_owner.getCurrentPose().lhand, _owner.getCurrentPose().rhand);
                        break;
                    case PARALLEL:
                        _mirroring.setModeToParallel(_owner.getCurrentPose().lhand, _owner.getCurrentPose().rhand);
                        break;
                    default:
                        _mirroring.setModeToNone();
                }
                _rhPosSliders.setEnabled(_mirroring.getMode() == ArmMirroring.MirrorMode.NONE);
                // make EMBR assume new pose if not NONE
                if (_mirroring.getMode() != ArmMirroring.MirrorMode.NONE) {
                    leftHandChangeListener.stateChanged(new ChangeEvent(this));
                }
            }
        });
        hpCenteringPanel.add(createLeftrightSeparator());

        // (Right)
        JPanel rhPosPanel = new JPanel(new BorderLayout());
        _useRPosCB.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                _poseModel.useRPos = _useRPosCB.isSelected();
                updatePose();
            }
        });
        rhPosPanel.add(_useRPosCB, BorderLayout.WEST);
        _rhPosSliders.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.rhand = _rhPosSliders.getActualValue();
                updateLabel(_rhPositionLabel);
                updatePose();
            }
        });
        rhPosPanel.add(_rhPosSliders, BorderLayout.SOUTH);
        rhPosPanel.add(_rhPositionLabel, BorderLayout.EAST);
        hpCenteringPanel.add(rhPosPanel);
        handPositionPanel.add(hpCenteringPanel);
        armsTab.add(handPositionPanel);

        // Hand shape
        JPanel handShapePanel = createHandShapePane(hpCenteringPanel);
        armsTab.add(handShapePanel);

        // Hand orientation
        armsTab.add(createHandOrientPane());
        armsTab.add(createHandOrientPane2());

        // Arm Swivel
        JPanel armSwivelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        armSwivelPanel.setBorder(BorderFactory.createEmptyBorder(-8, 0, -8, 0));
        JPanel asCenteringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // (Left)
        JPanel leftArmSwivelPanel = new JPanel(new BorderLayout());
        armSwivelPanel.add(createCaptionLabel(CAPTION_SWIVEL));
        _useLSwivelCheck.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                _poseModel.useLeftSwivel = _useLSwivelCheck.isSelected();
                updatePose();
            }
        });
        leftArmSwivelPanel.add(_useLSwivelCheck, BorderLayout.WEST);
        _lSwivelSliders.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.lSwivel = _lSwivelSliders.getActualValue();
                updateLabel(_lswivelLabel);
                updatePose();
            }
        });
        leftArmSwivelPanel.add(_lSwivelSliders, BorderLayout.SOUTH);
        leftArmSwivelPanel.add(_lswivelLabel, BorderLayout.EAST);
        asCenteringPanel.add(leftArmSwivelPanel);
        asCenteringPanel.add(createLeftrightSeparator());
        // (Right):
        JPanel rightArmSwivelPanel = new JPanel(new BorderLayout());
        _useRSwivelCheck.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                _poseModel.useRightSwivel = _useRSwivelCheck.isSelected();
                updatePose();
            }
        });
        rightArmSwivelPanel.add(_useRSwivelCheck, BorderLayout.WEST);
        _rSwivelSliders.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.rSwivel = _rSwivelSliders.getActualValue();
                updateLabel(_rswivelLabel);
                updatePose();
            }
        });
        rightArmSwivelPanel.add(_rSwivelSliders, BorderLayout.SOUTH);
        rightArmSwivelPanel.add(_rswivelLabel, BorderLayout.EAST);
        asCenteringPanel.add(rightArmSwivelPanel);
        armSwivelPanel.add(asCenteringPanel);
        armsTab.add(armSwivelPanel);
        return armsTab;
    }

    private JPanel createHandShapePane(JPanel hpCenteringPanel)
    {
        JPanel handShapePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        handShapePanel.setBorder(BorderFactory.createEmptyBorder(-8, 0, -8, 0));
        handShapePanel.add(createCaptionLabel(CAPTION_HAND_SHAPE));
        JPanel hsCenteringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        hsCenteringPanel.add(new JLabel(CAPTION_LEFT, JLabel.TRAILING));
        _lhShapeCombo.addItemListener(new ItemListener()
        {

            @Override
            public void itemStateChanged(ItemEvent e)
            {
                _poseModel.lhShape = (EMBRHandshape) _lhShapeCombo.getSelectedItem();
                updatePose();
            }
        });
        hsCenteringPanel.add(_lhShapeCombo);
        hsCenteringPanel.add(createLeftrightSeparator());
        _rhShapeCombo.addItemListener(new ItemListener()
        {

            @Override
            public void itemStateChanged(ItemEvent e)
            {
                _poseModel.rhShape = (EMBRHandshape) _rhShapeCombo.getSelectedItem();
                updatePose();
            }
        });
        hsCenteringPanel.add(new JLabel(CAPTION_RIGHT, JLabel.TRAILING));
        hsCenteringPanel.add(_rhShapeCombo);
        hsCenteringPanel.setPreferredSize(new Dimension(hpCenteringPanel.getPreferredSize().width, hsCenteringPanel.getPreferredSize().height));
        handShapePanel.add(hsCenteringPanel);
        return handShapePanel;
    }

    /**
     * Creates tab section for controlling:
     * - head orientation
     * - gaze orientation
     * - blushing
     */
    private JPanel createHeadTab()
    {
        JPanel p = new JPanel();
        BoxLayout headFaceBL = new BoxLayout(p, BoxLayout.Y_AXIS);
        p.setLayout(headFaceBL);

        // Gaze
        JPanel gazePane = createGazePane();
        p.add(gazePane);

        // Head orientation
        JPanel headOrientPane = createHeadOrientationPane();
        p.add(headOrientPane);

        // Blushing
        JPanel faceShadePanel = createBlushingPane();
        p.add(faceShadePanel);
        p.add(_facialExpressionPanel);
        return p;
    }

    /**
     * Creates tab section for controlling:
     * - torso orientation
     * -
     */
    private JPanel createBodyTab()
    {
        JPanel p = new JPanel();
        BoxLayout layout = new BoxLayout(p, BoxLayout.Y_AXIS);
        p.setLayout(layout);

        p.add(createTorsoPane());
        p.add(createShoulderPane());
        return p;
    }

    private JPanel createShoulderPane()
    {
        JPanel p = new JPanel();
        _useShoulderCheck = new JCheckBox("Shoulder");
        _useShoulderCheck.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent ce)
            {
                _poseModel.useShoulder = _useShoulderCheck.isSelected();
//                _poseModel.shoulderValue = _shoulderSlider.getActualValue();
                updatePose();
            }
        });
        _shoulderLabel = new JLabel("0.0");
        _shoulderSlider = new Slider1d(-1, 1, 0);
        _shoulderSlider.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent ce)
            {
                _shoulderLabel.setText(String.format("%2.2f", _shoulderSlider.getActualValue()));
                _poseModel.shoulderValue = (float) _shoulderSlider.getActualValue();
                updatePose();
            }
        });

        p.add(_useShoulderCheck);
        p.add(_shoulderSlider);
        p.add(_shoulderLabel);
        return p;
    }

    private JPanel createTorsoPane()
    {
        JPanel p = new JPanel();

        p.add(createCaptionLabel("Torso"));
        JPanel centerPane = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Position
        JPanel p1 = new JPanel(new BorderLayout());
        _useTorsoPositionCheck.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {

                _poseModel.useTorsoPosition = _useTorsoPositionCheck.isSelected();
//                _poseModel.torsoPosition = _torsoPositionSliders.getActualValue();
                updatePose();
            }
        });
        p1.add(_useTorsoPositionCheck);
        final JLabel positionLabel = createCoordinateLabel();
        _torsoPositionSliders.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.torsoPosition = _torsoPositionSliders.getActualValue();
                positionLabel.setText(_poseModel.torsoPosition.toString());
                updatePose();
            }
        });
        p1.add(_torsoPositionSliders, BorderLayout.SOUTH);
        p1.add(positionLabel, BorderLayout.EAST);
        centerPane.add(p1);
        centerPane.add(createLeftrightSeparator());

        // orientation
        JPanel p2 = new JPanel(new BorderLayout());
        _useTorsoOrientCheck.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                _poseModel.useTorsoOrient = _useTorsoOrientCheck.isSelected();
//                _poseModel.torsoOrient = _torsoOrientSliders.getActualValue();
                updatePose();
            }
        });

        p2.add(_useTorsoOrientCheck);
        final JLabel torsoOrientLabel = createCoordinateLabel();
        _torsoOrientSliders.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.torsoOrient = _torsoOrientSliders.getActualValue();
                torsoOrientLabel.setText(_poseModel.torsoOrient.toString());
                updatePose();
            }
        });
        p2.add(_torsoOrientSliders, BorderLayout.SOUTH);
        p2.add(torsoOrientLabel, BorderLayout.EAST);
        centerPane.add(p2);
        p.add(centerPane);

        return p;
    }

    private JPanel createBlushingPane()
    {
        JPanel faceShadePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        _useShaderCB.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                _poseModel.useShader = _useShaderCB.isSelected();
                updatePose();
            }
        });
        faceShadePanel.add(_useShaderCB);
        _shadeCtrl.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.shade = _shadeCtrl.getActualValue();
                updateLabel(_shadeLabel);
                updatePose();
            }
        });
        faceShadePanel.add(_shadeCtrl);
        faceShadePanel.add(_shadeLabel);
        return faceShadePanel;
    }

    private JLabel createCoordinateLabel()
    {
        JLabel l = new JLabel();
        l.setFont(new Font("Arial", Font.BOLD, 10));
        l.setPreferredSize(new Dimension(84, 14));
        return l;
    }

    /**
     * Control head orientation with two axis controls
     * @param gazePane
     * @return
     */
// SNIPPET FROM ALEXIS:
//
//  BEGIN ORIENTATION_CONSTRAINT
//    BODY_GROUP:headNeck
//    DIRECTION:1;0.0;0.0
//    JOINT:head
//    OFFSET:0;0;0
//    NORMAL:Zaxis
//  END
//  BEGIN ORIENTATION_CONSTRAINT
//    BODY_GROUP:headNeck
//    DIRECTION:0.0;0.0;1.0
//    JOINT:head
//    OFFSET:0;0;0
//    NORMAL:Yaxis
//  END
    private JPanel createHeadOrientationPane()
    {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(createCaptionLabel("Head Orientation"));
        JPanel centerPane = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // First axis
        JPanel p1 = new JPanel(new BorderLayout());
        _useHeadOrientZCheck.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                _poseModel.useHeadOrientZ = _useHeadOrientZCheck.isSelected();
                updatePose();
            }
        });

        p1.add(_useHeadOrientZCheck);
        final JLabel orientLabel1 = createCoordinateLabel();
        _headOrientSliders1.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.headOrientZ = _headOrientSliders1.getActualValue();
                orientLabel1.setText(_poseModel.headOrientZ.toString());
                updatePose();
            }
        });
        p1.add(_headOrientSliders1, BorderLayout.SOUTH);
        p1.add(orientLabel1, BorderLayout.EAST);
        centerPane.add(p1);
        centerPane.add(createLeftrightSeparator());

        // Second axis
        JPanel p2 = new JPanel(new BorderLayout());
        _useHeadOrientYCheck.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                _poseModel.useHeadOrientY = _useHeadOrientYCheck.isSelected();
                updatePose();
            }
        });

        p2.add(_useHeadOrientYCheck);
        final JLabel orientLabel2 = createCoordinateLabel();
        _headOrientSliders2.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.headOrientY = _headOrientSliders2.getActualValue();
                orientLabel2.setText(_poseModel.headOrientY.toString());
                updatePose();
            }
        });
        p2.add(_headOrientSliders2, BorderLayout.SOUTH);
        p2.add(orientLabel2, BorderLayout.EAST);
        centerPane.add(p2);
        p.add(centerPane);
        return p;
    }

    private JPanel createGazePane()
    {
        final JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel targetPane = new JPanel(new BorderLayout());
        _useGazeDirCB.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                _poseModel.useGazeDir = _useGazeDirCB.isSelected();
                updatePose();
            }
        });
        p.add(_useGazeDirCB);
        targetPane.add(new JLabel(CAPTION_GAZE_TARGET), BorderLayout.WEST);
        _lookAtCtrl.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.lookAt = _lookAtCtrl.getActualValue();
                updateLabel(_lookAtLabel);
                updatePose();
            }
        });
        targetPane.add(_lookAtCtrl, BorderLayout.SOUTH);
        targetPane.add(_lookAtLabel, BorderLayout.EAST);
        p.add(targetPane);
        p.add(createLineSeparator());
        JPanel bGrpPanel = new JPanel(new BorderLayout());
        bGrpPanel.add(new JLabel(CAPTION_GAZE_BGROUP), BorderLayout.EAST);
        _lookAtBodyGroupCombo.addItemListener(new ItemListener()
        {

            @Override
            public void itemStateChanged(ItemEvent e)
            {
                _poseModel.lookAtBodyGroup = (EMBRBodyGroup) _lookAtBodyGroupCombo.getSelectedItem();
                updatePose();
            }
        });
        bGrpPanel.add(_lookAtBodyGroupCombo, BorderLayout.SOUTH);
        p.add(bGrpPanel);
        return p;
    }

    private JPanel createHandOrientPane()
    {
        JPanel handOrientationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        handOrientationPanel.add(createCaptionLabel(CAPTION_HAND_ORIENTATION));
        JPanel hoCenteringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // LH
        JPanel lhOriPanel = new JPanel(new BorderLayout());
        _useLhOrientYCheck.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                _poseModel.useLhOrientY = _useLhOrientYCheck.isSelected();
                updatePose();
            }
        });

        JPanel lorp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        lorp.add(new JLabel(CAPTION_ORIENTATION_NORMAL + ""));
        lorp.add(_useLhOrientYCheck, BorderLayout.WEST);
        lhOriPanel.add(lorp);
        _lhOrientSliders.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.lhOrientY = _lhOrientSliders.getActualValue();
                updateLabel(_lhOrientYLabel);
                updatePose();
            }
        });
        lhOriPanel.add(_lhOrientSliders, BorderLayout.SOUTH);
        lhOriPanel.add(_lhOrientYLabel, BorderLayout.EAST);
        hoCenteringPanel.add(lhOriPanel);
        hoCenteringPanel.add(createLeftrightSeparator());

        // RH
        JPanel rhOriPanel = new JPanel(new BorderLayout());
        _useRhOrientYCheck.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                _poseModel.useRhOrientY = _useRhOrientYCheck.isSelected();
                updatePose();
            }
        });

        JPanel rorp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rorp.add(new JLabel(CAPTION_ORIENTATION_NORMAL + " "));
        rorp.add(_useRhOrientYCheck, BorderLayout.WEST);
        rhOriPanel.add(rorp);
        _rhOrientSliders.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.rhOrientY = _rhOrientSliders.getActualValue();
                updateLabel(_rhOrientYLabel);
                updatePose();
            }
        });
        rhOriPanel.add(_rhOrientSliders, BorderLayout.SOUTH);
        rhOriPanel.add(_rhOrientYLabel, BorderLayout.EAST);
        //handPositionPanel.add(new JLabel(CAPTION_EMPTY));
        //handPositionPanel.add(rhPosPanel);
        hoCenteringPanel.add(rhOriPanel);
        //hpCenteringPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
        handOrientationPanel.add(hoCenteringPanel);
        return handOrientationPanel;
    }

    private JPanel createHandOrientPane2()
    {
        // Hand Orientation:
        JPanel handOrientationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // instead of:
        handOrientationPanel.add(createCaptionLabel(CAPTION_HAND_ORIENTATION));
        JPanel hoCenteringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // (Left)
        JPanel lhOriPanel = new JPanel(new BorderLayout());
        _useLhOrientZCheck.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                _poseModel.useLhOrientZ = _useLhOrientZCheck.isSelected();
                updatePose();
            }
        });

        JPanel lorp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        lorp.add(new JLabel(CAPTION_ORIENTATION_NORMAL + ""));
        lorp.add(_useLhOrientZCheck, BorderLayout.WEST);
        lhOriPanel.add(lorp);
        _lhOrientSliders2.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.lhOrientZ = _lhOrientSliders2.getActualValue();
                updateLabel(_lhOrientZLabel);
                updatePose();
            }
        });
        lhOriPanel.add(_lhOrientSliders2, BorderLayout.SOUTH);
        lhOriPanel.add(_lhOrientZLabel, BorderLayout.EAST);
        hoCenteringPanel.add(lhOriPanel);
        hoCenteringPanel.add(createLeftrightSeparator());

        // RH
        JPanel rhOriPanel = new JPanel(new BorderLayout());
        _useRhOrientZCheck.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                _poseModel.useRhOrientZ = _useRhOrientZCheck.isSelected();
                updatePose();
            }
        });

        JPanel rorp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rorp.add(new JLabel(CAPTION_ORIENTATION_NORMAL + " "));
        rorp.add(_useRhOrientZCheck, BorderLayout.WEST);
        rhOriPanel.add(rorp);
        _rhOrientSliders2.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                _poseModel.rhOrientZ = _rhOrientSliders2.getActualValue();
                updateLabel(_rhOrientZLabel);
                updatePose();
            }
        });
        rhOriPanel.add(_rhOrientSliders2, BorderLayout.SOUTH);
        rhOriPanel.add(_rhOrientZLabel, BorderLayout.EAST);
        hoCenteringPanel.add(rhOriPanel);
        handOrientationPanel.add(hoCenteringPanel);
        return handOrientationPanel;
    }

    private void updatePose()
    {
        if (!_blockUpdatePose) {
//            System.out.println("GUI pose update");
            _owner.updatePose();
        }
    }
}
