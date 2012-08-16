/*
 * SigningFrontend.java
 *
 * Created on 04.11.2010, 15:20:56
 */
package de.dfki.embots.framework.ui.signlang;

import de.dfki.embots.bml.lex.BehaviorLexeme;
import de.dfki.embots.bml.lex.BehaviorLexicon;
import de.dfki.embots.embrscript.EMBRPose;
import de.dfki.embots.embrscript.EMBRPoseSequence;
import de.dfki.embots.embrscript.EMBRScriptReader;
import de.dfki.embots.framework.EMBOTSConstants;
import eu.semaine.jms.JMSLogger;
import eu.semaine.jms.sender.Sender;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Editor for clicking together a sign language GLOSS sequence.
 * 
 * @author Michael Kipp
 */
public class SigningFrontend extends javax.swing.JFrame
{

    private static final long DEFAULT_TRANSITION_TIME = 350;
    private DefaultListModel _signSequence = new DefaultListModel();
    private HashMap<String, Sign> _nameToSignTable = new HashMap<String, Sign>();
    private BehaviorLexicon _lexicon;
    private Sender _embrSender;
    private JMSLogger _log;

    private class Sign
    {

        public String name;
        public String filename;
        public boolean doPrep = false;
        public boolean doRetract = false; // NOT IMPLEMENTED YET

        Sign(String n, String fn, boolean doP, boolean doR)
        {
            name = n;
            filename = fn;
            doPrep = doP;
            doRetract = doR;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    /** Creates new form SigningFrontend */
    public SigningFrontend(Sender embrSender, JMSLogger log)
    {
        _log = log;
        _embrSender = embrSender;
        initComponents();
        createTabs();
        _signSeqList.setModel(_signSequence);
        loadSigns();
        pack();
    }

    private void loadSigns()
    {
        try {
            // read lexicon
            EMBRScriptReader rd1 = new EMBRScriptReader();
            _lexicon = rd1.readLexicon(new File(EMBOTSConstants.EMBRSCRIPT_SIGN_LANGUAGE_DIR));
            // print all lexemes
            _propertiesTextField.setText("Loaded sign lexicon [" + _lexicon.getLexemes().size() + " entries]");
        } catch (IOException ex) {
            Logger.getLogger(SigningFrontend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createTabs()
    {
        _inputTabbedPane.addTab("Phrase", createSentenceTab());
        _inputTabbedPane.addTab("Fingerspell", createLetterTab());
    }

    private JPanel createSentenceTab()
    {
        JPanel p = new JPanel();
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        p.setLayout(new GridLayout(3,1));
        p.add(p1);
        p.add(p2);
        p.add(p3);

        p1.add(createSignButton("HELLO", "HELLO"));
        p1.add(createSignButton("BYE", "HELLO", true, false));
        p1.add(createSignButton("HOW ARE YOU", "WIE_GEHTS"));
        p1.add(createSignButton("MY NAME", "MY_NAME"));
        p2.add(createSignButton("YES", "JA_RH"));
        p2.add(createSignButton("NO", "NEIN_RH"));
        p2.add(createSignButton("THANK YOU", "THANK_YOU", true, false));
        p3.add(createSignButton("I (PERS)", "ICH_RH"));
        p3.add(createSignButton("BORN", "GEBOREN_2H"));
        p3.add(createSignButton("SAARBRÜCKEN", "SAARBRUECKEN_RH"));
        p3.add(createSignButton("FATHER", "VATER_RH"));
        
        
        return p;
    }

    private JPanel createLetterTab()
    {
        JPanel p = new JPanel();
        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();
        JPanel p3 = new JPanel();
        JPanel p4 = new JPanel();
        p1.add(createSignLetterButton("A"));
        p1.add(createSignLetterButton("B"));
        p1.add(createSignLetterButton("C"));
        p1.add(createSignLetterButton("D"));
        p1.add(createSignLetterButton("E"));
        p1.add(createSignLetterButton("F"));
        p1.add(createSignLetterButton("G"));
        p2.add(createSignLetterButton("H"));
        p2.add(createSignLetterButton("I"));
        p2.add(createSignLetterButton("J"));
        p2.add(createSignLetterButton("K"));
        p2.add(createSignLetterButton("L"));
        p2.add(createSignLetterButton("M"));
        p2.add(createSignLetterButton("N"));
        p3.add(createSignLetterButton("O"));
        p3.add(createSignLetterButton("P"));
        p3.add(createSignLetterButton("Q"));
        p3.add(createSignLetterButton("R"));
        p3.add(createSignLetterButton("S"));
        p3.add(createSignLetterButton("SCH"));
        p3.add(createSignLetterButton("T"));
        p4.add(createSignLetterButton("U"));
        p4.add(createSignLetterButton("V"));
        p4.add(createSignLetterButton("W"));
        p4.add(createSignLetterButton("X"));
        p4.add(createSignLetterButton("Y"));
        p4.add(createSignLetterButton("Z"));
        p.setLayout(new GridLayout(4, 1));
        p.add(p1);
        p.add(p2);
        p.add(p3);
        p.add(p4);
        return p;
    }

    private JButton createSignLetterButton(String name)
    {
        return createSignButton(name, "DGS_" + name);
    }

    private JButton createSignButton(String name, String filename) {
        return createSignButton(name, filename, false, false);
    }

    private JButton createSignButton(String name, String filename, 
            boolean doPrep, boolean keepRetract)
    {
        JButton b = new JButton(name);
        _nameToSignTable.put(name, new Sign(name, filename, doPrep, keepRetract));
        b.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                // when button is pressed, script is immediately sent to EMBR
                Sign s = _nameToSignTable.get(ae.getActionCommand());
                _signSequence.addElement(s);
                BehaviorLexeme lex = _lexicon.getLexeme(s.filename);
                _propertiesTextField.setText("Lexeme: " + lex);
                sendToEMBR(lex.getEMBRScript().toScript());
            }
        });
        return b;
    }

    private void sendToEMBR(String script)
    {
        try {
            _embrSender.sendTextMessage(script, 0);
        } catch (JMSException ex) {
            Logger.getLogger(SigningFrontend.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendSequenceAction()
    {
        if (_signSequence.size() > 0) {
            BehaviorLexeme lex = _lexicon.getLexeme(((Sign) _signSequence.firstElement()).filename);
            EMBRPoseSequence seq = new EMBRPoseSequence(lex.getEMBRScript());
            seq.setASAP(true);
            _log.info("FIRST HAS " + seq.getPoses().size() + " POSES");
            // chop off last pose
            seq.getPoses().remove(seq.getPoses().last());
            _log.info("AFTER CHOPPING FIRST HAS " + seq.getPoses().size() + " POSES");

            // more than one sign?
            if (_signSequence.size() > 1) {
                for (int i = 1; i < _signSequence.size(); i++) {

                    long endTime = seq.getPoses().last().getTime() + seq.getPoses().last().getHoldDuration();

                    // get next sign as embrscript
                    Sign currentSign = (Sign) _signSequence.get(i);
                    lex = _lexicon.getLexeme(currentSign.filename);
                    EMBRPoseSequence s = lex.getEMBRScript();
                    _log.info("FOUND " + lex.toString() + " POSES=" + s.getPoses().size());

                    // add poses to overall script
                    for (EMBRPose pOld : s.getPoses()) {

                        // skip first pose
                        if (pOld != s.getPoses().first() || currentSign.doPrep) {
                            // only add last pose if last sign
                            if (pOld != s.getPoses().last() || i == _signSequence.size()-1) {
                                EMBRPose p = new EMBRPose(pOld);
                                p.setTime(endTime + DEFAULT_TRANSITION_TIME);
                                p.comment = lex.getName();
                                seq.addPose(p);
                                endTime = p.getTime() + p.getHoldDuration();
                            }
                        }
                    }
                    _log.info("AFTER ADDING: " + seq.getPoses().size());
                }
            }

            // send to EMBR
            sendToEMBR(seq.toScript());
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        _inputTabbedPane = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        _signSeqList = new javax.swing.JList();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        _propertiesTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        _signSeqList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(_signSeqList);

        jButton1.setText("Animate");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("New Sequence");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        _propertiesTextField.setEditable(false);
        _propertiesTextField.setText("jTextField1");
        _propertiesTextField.setBorder(null);

        jLabel1.setText("Sign Sequence:");

        jLabel2.setText("Log:");

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 24));
        jLabel3.setText("Sign Language Composer");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(9, 9, 9)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 283, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                    .add(jLabel2)
                    .add(_propertiesTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 294, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .add(_inputTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(162, Short.MAX_VALUE)
                .add(jLabel3)
                .add(143, 143, 143))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(_inputTabbedPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 262, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(_propertiesTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 58, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(27, 27, 27)
                        .add(jButton2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
    {//GEN-HEADEREND:event_jButton1ActionPerformed
        // TODO add your handling code here:
        sendSequenceAction();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton2ActionPerformed
    {//GEN-HEADEREND:event_jButton2ActionPerformed
        // TODO add your handling code here:
        _signSequence.clear();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {

            public void run()
            {
                //new SigningFrontend().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane _inputTabbedPane;
    private javax.swing.JTextField _propertiesTextField;
    private javax.swing.JList _signSeqList;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
