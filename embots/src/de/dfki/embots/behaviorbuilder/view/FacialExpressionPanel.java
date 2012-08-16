package de.dfki.embots.behaviorbuilder.view;

import de.dfki.embots.behaviorbuilder.BehaviorBuilder;
import de.dfki.embots.behaviorbuilder.utility.BBConstants;
import de.dfki.embots.embrscript.EMBRMorphKey;
import de.dfki.embots.embrscript.EMBRMorphTargetConstraint;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Properties;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Allows to add and combine morph targets for facial expression configuration.
 */
public class FacialExpressionPanel extends JPanel
{

    private boolean _blockPoseUpdate = false;

    class MorphRow extends JPanel
    {

        private static final double MIN_INTENSITY = 0d;
        private static final double MAX_INTENSITY = 1.5d;
        private JComboBox morphTargetComboBox;
        private JLabel intensityLabel;
        private Slider1d intensitySlider;
        private EMBRMorphTargetConstraint morphTargetConstraint;

        public MorphRow(final FacialExpressionPanel owner, EMBRMorphTargetConstraint m)
        {

            super(new FlowLayout(FlowLayout.LEFT));
            this.morphTargetConstraint = m; // ~ yes, really ref, do not copy here!

            morphTargetComboBox = new JComboBox(EMBRMorphKey.values());
            morphTargetComboBox.setSelectedItem(BBConstants.DEFAULT_MORPH_KEY);
            intensityLabel = new JLabel("" + BBConstants.DEFAULT_MORPH_VALUE);
            intensitySlider = new Slider1d(MIN_INTENSITY, MAX_INTENSITY,
                    BBConstants.DEFAULT_MORPH_VALUE);

            JButton delMorphButton = new JButton(_conf.getProperty("ctrl.caption.del_faceMorph"));
            delMorphButton.addActionListener(new ActionListener()
            {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                    owner.delRow(MorphRow.this);
                }
            });
            delMorphButton.setToolTipText(_conf.getProperty("ctrl.tooltip.del_faceMorph"));
            //add(delMorphButton);

            morphTargetComboBox.addItemListener(new ItemListener()
            {

                @Override
                public void itemStateChanged(ItemEvent e)
                {
                    morphTargetConstraint.key = (EMBRMorphKey) morphTargetComboBox.getSelectedItem();
                    updatePose();
                }
            });
            add(morphTargetComboBox);
            add(PoseControlPanel.createLineSeparator());

            JPanel morphValPanel = new JPanel(new BorderLayout());

            intensitySlider.addChangeListener(new ChangeListener()
            {

                @Override
                public void stateChanged(ChangeEvent e)
                {
                    //_cp.morphV.get(ix).sigma = intensitySlider.getActualValue();
                    morphTargetConstraint.value = intensitySlider.getActualValue();
                    updateRow();
                    updatePose();
                }
            });
            morphValPanel.add(intensitySlider, BorderLayout.SOUTH);
            morphValPanel.add(intensityLabel, BorderLayout.EAST);
            add(morphValPanel);

            add(PoseControlPanel.createLineSeparator());
            add(delMorphButton);

            updateRow();
        }

        public void updateRow()
        {
            morphTargetComboBox.setSelectedItem(morphTargetConstraint.key);
            //updateLabel(intensityLabel);
            intensityLabel.setText(_conf.getProperty("ctrl.caption.gen_intensity")
                    + " " + PoseControlPanel.format(100 * morphTargetConstraint.value) + "%");
            intensitySlider.setActualValue(morphTargetConstraint.value);
            //System.out.println("Row " + this + " updated");
            revalidate();
            repaint();
        }
    }
    protected JPanel _rowsPanel;
    private JScrollPane sp;
    private BehaviorBuilder _owner;
    Properties _conf;

    public FacialExpressionPanel(BehaviorBuilder owner, Properties conf)
    {
        _owner = owner;
        _conf = conf;
        this.setLayout(new BorderLayout());

        JToolBar morphTB = new JToolBar("Morphs", JToolBar.HORIZONTAL);

        _rowsPanel = new JPanel();
        _rowsPanel.setLayout(new BoxLayout(_rowsPanel, BoxLayout.Y_AXIS));

        //~this.setBorder(BorderFactory.createTitledBorder(CAPTION_FACE_MORPH));

        sp = new JScrollPane(_rowsPanel);
        sp.setCorner(JScrollPane.LOWER_LEFT_CORNER, new JLabel("Corner"));
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //debug: _rowsPanel.setBackground(Color.GREEN);
        int spMinHeight;
        try {
            spMinHeight = Integer.parseInt(_conf.getProperty("ctrl.faceMorphs.minHeight"));
        } catch (NumberFormatException x) {
            System.err.println("WARNING: Configuration error: The key \"ctrl.faceMorphs.minHeight\" must hold an integer value in the config file.");
            spMinHeight = 80;
        }
        sp.setMinimumSize(new Dimension(
                sp.getMinimumSize().width,
                spMinHeight));

        this.add(sp, BorderLayout.CENTER);

        JButton addMorphButton = new JButton(_conf.getProperty("ctrl.caption.add_faceMorph"));
        addMorphButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                createRow();
            }
        });
        addMorphButton.setToolTipText(_conf.getProperty("ctrl.tooltip.add_faceMorph"));
        addMorphButton.setOpaque(false);
        JPanel p = new JPanel(new FlowLayout());
        p.add(addMorphButton); // centers it, but generates padding
        //this.add(p, BorderLayout.NORTH);

        Action clrMorphsAction = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                //_cp.clearAllMorphs();
                //_owner.assumePose(true);
                // ^old / new:
                _owner.clearMorphs();
            }
        };
        _owner.enrichActionFromProperties(clrMorphsAction, "ClearFace");

        JButton clrMorphsButton = new JButton(clrMorphsAction);
        clrMorphsButton.setForeground(new Color(0xC07010));
        //_clrMorphsButton.setToolTipText(TOOLTIP_BUTTON_CLRMORPHS);
        clrMorphsButton.setOpaque(false);

        Action applyMorphsAction = new AbstractAction()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                //_cp.clearAllMorphs();
                //_owner.assumePose(false);
                _owner.restoreMorphs();
            }
        };
        _owner.enrichActionFromProperties(applyMorphsAction, "ApplyFace");

        JButton applyMorphsButton = new JButton(applyMorphsAction);
        applyMorphsButton.setForeground(new Color(0xC07010));
        //_clrMorphsButton.setToolTipText(TOOLTIP_BUTTON_CLRMORPHS);
        applyMorphsButton.setOpaque(false);

        morphTB.add(BehaviorBuilder.createSeparator());
        morphTB.add(addMorphButton);
        morphTB.add(BehaviorBuilder.createSeparator());
        morphTB.add(clrMorphsButton);
        morphTB.add(BehaviorBuilder.createSeparator());
        morphTB.add(applyMorphsButton);

        this.add(
                BehaviorBuilder.createHeadPane(
                _conf.getProperty("pane.morphs.title"),
                morphTB),
                BorderLayout.NORTH);
    }

    protected void restoreRow(EMBRMorphTargetConstraint c)
    {
        MorphRow row = new MorphRow(FacialExpressionPanel.this, c);
        // ~ describe "evil" assumption here and above at constructor!
        _rowsPanel.add(row);
        _rowsPanel.setPreferredSize(new Dimension(
                sp.getViewport().getExtentSize().width,
                _rowsPanel.getComponentCount() * row.getPreferredSize().height));
        _rowsPanel.revalidate();
        _rowsPanel.repaint();
        sp.revalidate();
        sp.repaint();
        //sp.invalidate(); sp.validate();
        //sp.setViewportView(_rowsPanel);
        //System.out.println("Morphs: " + morphs);
        updatePose();
        //return morphs.add(row);
    }

    protected void createRow()
    {
        //Pose.Morph morph = _cp.addMorph(DEFAULT_MORPH_KEY, DEFAULT_MORPH_VALUE);
        EMBRMorphTargetConstraint c = _owner.getCurrentPose().addMorph(BBConstants.DEFAULT_MORPH_KEY,
                BBConstants.DEFAULT_MORPH_VALUE);
        assert (null != c);
        restoreRow(c);
    }

    protected void delRow(MorphRow row)
    {
        _rowsPanel.remove(row);
        //System.out.println("Morphs: " + morphs);
        _rowsPanel.revalidate();
        this.repaint();
        _owner.getCurrentPose().delMorph(row.morphTargetConstraint);
        updatePose();
        //~ updatePose passiert zu oft, lieber nur script neu schreiben?
        //~ aber zum server muss auch uebertag genn, auf neue Weise sogar!
        //return morphs.remove(row);
    }

    public void updateMorphPanel()
    {
        _rowsPanel.removeAll();
        //morphs.clear();
        // ~ ?clumsy:
        for (EMBRMorphTargetConstraint m : _owner.getCurrentPose().morphV) {
            restoreRow(m);
        }
        //System.out.println("updateMorphPanel()... morphs: " + morphs);
        //for (MorphRow r : morphs) r.updateRow();
        revalidate();
        repaint();
    }

    private void updatePose()
    {
        if (!_blockPoseUpdate) {
//            System.out.println("face: updatePose");
            _owner.updatePose();
        }
    }

    /**
     * @return the _blockPoseUpdate
     */
    public boolean isBlockPoseUpdate()
    {
        return _blockPoseUpdate;
    }

    /**
     * @param blockPoseUpdate the _blockPoseUpdate to set
     */
    public void setBlockPoseUpdate(boolean blockPoseUpdate)
    {
        this._blockPoseUpdate = blockPoseUpdate;
    }
}
