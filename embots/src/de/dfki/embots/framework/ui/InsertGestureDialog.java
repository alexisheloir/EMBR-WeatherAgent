package de.dfki.embots.framework.ui;

import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.bml.behavior.BMLBehavior;
import de.dfki.embots.bml.behavior.BMLNonverbalBehavior;
import de.dfki.embots.bml.lex.BehaviorLexicon;
import de.dfki.embots.bml.sync.BMLSpeechSyncPoint;
import de.dfki.embots.bml.sync.BMLSyncLabel;
import de.dfki.embots.bml.sync.BMLSyncPoint;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.String;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Modal Dialog for adding a single gesture tag,
 * including sync point constraints.
 *
 * @author Michael Kipp
 */
public class InsertGestureDialog extends JDialog implements ActionListener
{

    private static final String BEHAVIOR_ID_PREFIX = "g";
    private static int BEHAVIOR_ID_COUNT = 1;
    private BMLBlock _bml;
    private BehaviorLexicon _lexicon;
    private String _behavior = null;
    private JComboBox _lexemeCombo;
    private JPanel _syncPointPanel;
    private List<JComboBox> _singleSyncID = new ArrayList<JComboBox>();
    private List<BMLSyncLabel> _singleSyncType = new ArrayList<BMLSyncLabel>();
    private List<JTextField> _singleSyncVal = new ArrayList<JTextField>();
    private int _paneHeight;
    private String[] _syncPointIDArray;

    class SyncPointModel extends DefaultComboBoxModel
    {

        public SyncPointModel(String[] options)
        {
            super(options);
        }
    }

    private InsertGestureDialog(Frame owner, BMLBlock bml, BehaviorLexicon lexicon)
    {
        super(owner, true);
        _bml = bml;
        _lexicon = lexicon;
        createGUI();
        pack();
        _paneHeight = _syncPointPanel.getHeight();

        // collect ID's for sync point constraints
        List<String> syncPointIDs = new ArrayList<String>();
        for (BMLSyncPoint sp : _bml.collectSyncPoints()) {
            if (sp instanceof BMLSpeechSyncPoint) {
                syncPointIDs.add(sp.getOwner().getID() + ":" + sp.getID());
            }
        }
        for (BMLBehavior b : _bml.getBehaviors()) {
            if (b instanceof BMLNonverbalBehavior) {
                syncPointIDs.add(b.getID());
            }
        }
        _syncPointIDArray = (String[])syncPointIDs.toArray(new String[1]);
    }

    private void createGUI()
    {
        JPanel headPane = new JPanel();
        JLabel head = new JLabel("Specify Gesture", JLabel.CENTER);
        head.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        head.setFont(new Font("Helvetica", Font.BOLD, 20));
        headPane.setLayout(new GridLayout(0, 1));
        headPane.add(head);

        // lexeme panel
        JPanel lexemePane = new JPanel();
        JLabel lexemeLabel = new JLabel("Lexeme: ");
        String[] lexemes = (String[]) _lexicon.getLexemeNames().toArray(new String[1]);
        _lexemeCombo = new JComboBox(lexemes);
        lexemePane.add(lexemeLabel);
        lexemePane.add(_lexemeCombo);

        // sync point panel
        _syncPointPanel = new JPanel();
        _syncPointPanel.setLayout(new GridLayout(0, 1));
        _syncPointPanel.add(lexemePane);

        // buttons
        JPanel buttonPane = new JPanel();
        JButton addSync = new JButton("Add sync point");
        JButton doneButton = new JButton("Done");
        JButton cancelButton = new JButton("Cancel");
        addSync.addActionListener(this);
        doneButton.addActionListener(this);
        cancelButton.addActionListener(this);

        // add combobox button
        String[] addOptions = new String[BMLSyncLabel.getAllLabels().length + 1];
        addOptions[0] = "Add sync point..";
        int i = 1;
        for (BMLSyncLabel l : BMLSyncLabel.getAllLabels()) {
            addOptions[i++] = l.toString();
        }
        final JComboBox addComboBox = new JComboBox(new SyncPointModel(addOptions));
//        addComboBox.addPopupMenuListener(new PopupMenuListener()
//        {
//
//            @Override
//            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
//            {
//                addComboBox.removeItemAt(0);
//            }
//
//            @Override
//            public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
//            {
//                addComboBox.insertItemAt("Add sync point", 0);
//            }
//
//            @Override
//            public void popupMenuCanceled(PopupMenuEvent e)
//            {
//            }
//        });
        addComboBox.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (addComboBox.getSelectedIndex() > 0) {
                    addSyncPointAction((String) addComboBox.getSelectedItem());
                }
                addComboBox.setSelectedIndex(0);
            }
        });

        // add Flamingo button
//        JCommandButton addButton = new JCommandButton("Add sync point");
//        addButton.setCommandButtonKind(JCommandButton.CommandButtonKind.POPUP_ONLY);
//        addButton.setPopupCallback(new PopupPanelCallback()
//        {
//
//            @Override
//            public JPopupPanel getPopupPanel(JCommandButton commandButton)
//            {
////                return new SamplePopupMenu();
//            }
//        });

        // layout
//        buttonPane.add(addSync);


        buttonPane.add(addComboBox);
        buttonPane.add(doneButton);
        buttonPane.add(cancelButton);
        setLayout(new BorderLayout());
        add(head, BorderLayout.NORTH);
        add(_syncPointPanel, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.SOUTH);
    }

    private String getBehavior()
    {
        return _behavior;
    }

    public static String showDialog(Frame owner, BMLBlock bml, BehaviorLexicon lexicon)
    {
        System.out.println("show dialog called");
        InsertGestureDialog dialog = new InsertGestureDialog(owner, bml, lexicon);
        dialog.setVisible(true);
        String result = dialog.getBehavior();
        dialog.dispose();
        System.out.println("dialog finished");
        return result;
    }

    private static String behaviorID()
    {
        return BEHAVIOR_ID_PREFIX + BEHAVIOR_ID_COUNT++;
    }

    private void addSyncPointAction(String syncLabel)
    {
        JPanel p = new JPanel();

//        String[] syncLabels = new String[BMLSyncLabel.getAllLabels().length];
//        for (int i = 0; i < syncLabels.length; i++) {
//            syncLabels[i] = BMLSyncLabel.getAllLabels()[i].toString();
//        }

        JLabel syncLab = new JLabel(syncLabel);
//        JComboBox syncCombo = new JComboBox(syncLabels);
        JLabel syncValLabel = new JLabel(" = ");
        JComboBox syncCombo = new JComboBox(_syncPointIDArray);
        JTextField syncValue = new JTextField(20);
        p.add(syncLab);
//        p.add(syncCombo);
        p.add(syncValLabel);
        p.add(syncCombo);
        _syncPointPanel.add(p);
//        _singleSyncPoint.add(syncCombo);
        _singleSyncType.add(BMLSyncLabel.getSyncLabel(syncLabel));
        _singleSyncVal.add(syncValue);
        _singleSyncID.add(syncCombo);
        setSize(new Dimension(600, getHeight() + _paneHeight));
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();
        if (cmd.equals("Done")) {
            StringBuilder sb = new StringBuilder("<gesture ");
            sb.append("id=\"" + behaviorID()
                    + "\" type=\"lexicalized\" lexeme=\""
                    + ((String) _lexemeCombo.getSelectedItem())
                    + "\"");
            for (int i = 0; i < _singleSyncType.size(); i++) {
                sb.append(" " + _singleSyncType.get(i).toString()
                        + "=\"" + _singleSyncID.get(i).getSelectedItem().toString() + "\"");
            }
            sb.append("/>");
            _behavior = sb.toString();
            setVisible(false);
        } else if (cmd.equals("Cancel")) {
            _behavior = null;
            setVisible(false);
        }
    }
}
