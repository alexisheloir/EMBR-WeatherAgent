package de.dfki.embots.framework.ui;

import de.dfki.embots.framework.EMBOTSConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import net.boplicity.xmleditor.XmlTextPane;

/**
 * Frontend window
 * 
 * @author Michael Kipp
 */
public class FrontendView extends JFrame
{
    private XmlTextPane _textPane;
    private ActionListener _controller;
    private JComboBox languageSelect;
    private JButton _insertSync, _startBB;
    private JLabel _statusLabel;
    
    public FrontendView(ActionListener controller) {
        super();
        _controller = controller;
        setTitle(Frontend.NAME + " " + Frontend.VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // header
        JLabel label = new JLabel(Frontend.NAME + " " + Frontend.VERSION);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        label.setFont(new Font("Arial", Font.PLAIN, 24));

        // File content display
        _textPane = new XmlTextPane();
//        _textPane.setEditable(false);
        JScrollPane scroller = new JScrollPane(_textPane);

        // layout
        add(label, BorderLayout.NORTH);
        add(createButtonPane(), BorderLayout.SOUTH);
        add(createSidePane(), BorderLayout.EAST);
        add(scroller, BorderLayout.CENTER);

        _textPane.addCaretListener(new CaretListener() {

            @Override
            public void caretUpdate(CaretEvent e)
            {
                _insertSync.setEnabled(caretInBracket("text", e.getDot()));

            }
        });

        
    }

    private boolean caretInBracket(String tag, int pos) {
        String startTag = "<" + tag + ">";
        String endTag = "</" + tag + ">";
        int posStart = _textPane.getText().indexOf(startTag, pos);
        int posEnd = _textPane.getText().indexOf(endTag, pos);
        if (posEnd > -1 && (posStart < 0 || posStart > posEnd)) {
            // ok, we're in the text brackets
            int pos1 = _textPane.getText().indexOf("<", pos);
            int pos2 = _textPane.getText().indexOf(">", pos);
            return (pos2 < 0 || (pos1 > -1 && pos1 < pos2));
        }
        return false;
    }

    private JPanel createSidePane() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0,1));
        
        // insert behavior button
        JButton gestureButton = new JButton("Add gesture");
        gestureButton.setActionCommand(Frontend.INSERT_GESTURE_COMMAND);
        gestureButton.addActionListener(_controller);

        // insert behavior button
        JButton faceButton = new JButton("Add face");
        faceButton.setActionCommand(Frontend.INSERT_FACE_COMMAND);
        faceButton.addActionListener(_controller);
        faceButton.setEnabled(false);

        // insert behavior button
        JButton headButton = new JButton("Add head");
        headButton.setActionCommand(Frontend.INSERT_HEAD_COMMAND);
        headButton.addActionListener(_controller);
        headButton.setEnabled(false);

        // insert speech sync point button
        _insertSync = new JButton("Insert Sync Point");
        _insertSync.setActionCommand(Frontend.INSERT_SYNC_POINT_COMMAND);
        _insertSync.addActionListener(_controller);

        
        panel.add(_insertSync);
        panel.add(gestureButton);
        panel.add(faceButton);
        panel.add(headButton);
        return panel;
    }



    private JPanel createButtonPane()
    {
        JPanel pane = new JPanel();
        JPanel upper = new JPanel();
        JPanel middle = new JPanel();
        JPanel lower = new JPanel();
        pane.setLayout(new GridLayout(3, 1));
        pane.add(upper);
        pane.add(middle);
        pane.add(lower);

        // status
        _statusLabel = new JLabel("---");
        _statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        _statusLabel.setForeground(Color.blue);
        lower.add(_statusLabel);


        // send button
        JButton send = new JButton("START");
        send.setActionCommand(Frontend.SEND_COMMAND);
        send.addActionListener(_controller);

        // start BB button
        _startBB = new JButton("BehaviorBuilder");
        _startBB.setActionCommand(Frontend.START_BEHAVIOR_BUILDER);
        _startBB.addActionListener(_controller);

        // start signing button
        JButton startSign = new JButton("Sign Language");
        startSign.setActionCommand(Frontend.START_SIGN_LANGUAGE);
        startSign.addActionListener(_controller);

        // reload lexicon
        JButton reloadLexicon = new JButton("Reload lexicon");
        reloadLexicon.setActionCommand(Frontend.RELOAD_LEXICON);
        reloadLexicon.addActionListener(_controller);

        // open feedback window
        JButton feedbackWin = new JButton("Feedback Win");
        feedbackWin.setActionCommand(Frontend.FEEDBACK_WIN);
        feedbackWin.addActionListener(_controller);

        // file selection
        JLabel selectLabel = new JLabel("BML File: ");
        File bmlDir = new File(EMBOTSConstants.BML_DIR);
        File[] files = bmlDir.listFiles(new FilenameFilter()
        {

            @Override
            public boolean accept(File dir, String name)
            {
                return name.toLowerCase().endsWith(".bml");
            }
        });
        final JComboBox fileSelect = new JComboBox(files);
        fileSelect.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                BufferedReader rd = null;
                try {
                    File file = (File) fileSelect.getSelectedItem();
                    rd = new BufferedReader(new FileReader(file));
                    StringBuilder buf = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        buf.append(line + "\n");
                    }
                    rd.close();
                    _textPane.setText(buf.toString());
                } catch (IOException ex) {
                    Logger.getLogger(Frontend.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        fileSelect.setSelectedIndex(0);

        // voice selection
        JLabel voiceSelectLabel = new JLabel("Voice: ");
        
        languageSelect = new JComboBox(Frontend.VOICES);
        languageSelect.setActionCommand(Frontend.VOICE_SELECT_COMMAND);

        // set to default
        for (int i = 0; i < Frontend.VOICES.length; i++) {
            if (Frontend.VOICES[i].equals(Frontend.DEFAULT_VOICE)) {
                languageSelect.setSelectedIndex(i);
            }
        }
        languageSelect.addActionListener(_controller);

        // layout

        upper.add(selectLabel);
        upper.add(fileSelect);
        upper.add(voiceSelectLabel);
        upper.add(languageSelect);
        

        middle.add(send);
        middle.add(_startBB);
        middle.add(reloadLexicon);
        middle.add(startSign);
        middle.add(feedbackWin);

        return pane;
    }

    public String getSelectedVoice() {
        return (String)languageSelect.getSelectedItem();
    }

    public String getText() {
        return _textPane.getText();
    }

    public void insert(String text) {
        try {
            // inserts text into text pane 
            _textPane.getDocument().insertString(_textPane.getCaretPosition(), text, null);
        } catch (BadLocationException ex) {
            Logger.getLogger(FrontendView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void focusText() {
        _textPane.requestFocusInWindow();
    }

    public void setBBButton(boolean val) {
        _startBB.setEnabled(val);
    }

    public void setStatusText(String text) {
        _statusLabel.setText(text);
    }
}
