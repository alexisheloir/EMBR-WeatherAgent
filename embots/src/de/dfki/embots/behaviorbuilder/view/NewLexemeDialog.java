package de.dfki.embots.behaviorbuilder.view;

import de.dfki.embots.behaviorbuilder.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A dialog to create and name a new lexeme.
 * 
 * @author Oliver Schoenleben
 */
public class NewLexemeDialog extends JDialog
{

    Properties _conf;
    BehaviorBuilder _owner;
    JTextField nameT;
    JComboBox handB;

    public NewLexemeDialog(BehaviorBuilder owner)
    {
        super(owner, ModalityType.DOCUMENT_MODAL);

        _owner = owner;
        _conf = _owner._conf;

        setTitle(_conf.getProperty("newLex.dialogTitle"));

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JLabel helpL = new JLabel(_conf.getProperty("newLex.decription"));

        JLabel nameL = new JLabel(_conf.getProperty("newLex.name.caption"));
        JLabel handL = new JLabel(_conf.getProperty("newLex.hand.caption"));

        int width1 = nameL.getPreferredSize().width;
        int width2 = handL.getPreferredSize().width;
        int width = width1 < width2 ? width2 : width1;
        nameL.setPreferredSize(new Dimension(width, nameL.getPreferredSize().height));
        handL.setPreferredSize(new Dimension(width, handL.getPreferredSize().height));

        nameL.setToolTipText(_conf.getProperty("newLex.name.ttText"));

        nameT = new JTextField(_conf.getProperty("newLex.name.preText"));
        nameT.setColumns(16);
        nameT.setMargin(new Insets(3, 3, 3, 3));
        nameT.setToolTipText(_conf.getProperty("newLex.name.ttText"));
        nameT.selectAll();

        handL.setToolTipText(_conf.getProperty("newLex.hand.ttText"));

        handB = new JComboBox(
                new String[]{
                    _conf.getProperty("newLex.hand.left.cbText"),
                    _conf.getProperty("newLex.hand.rite.cbText"),
                    _conf.getProperty("newLex.hand.both.cbText")
                });
        handB.setToolTipText(_conf.getProperty("newLex.hand.ttText"));

        JButton cancelButton = new JButton(_conf.getProperty("newLex.cancelButton.caption"));
        cancelButton.setToolTipText(_conf.getProperty("newLex.cancelButton.tooltip"));
        cancelButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                cancelled();
            }
        });

        JButton acceptButton = new JButton(_conf.getProperty("newLex.acceptButton.caption"));
        acceptButton.setToolTipText(_conf.getProperty("newLex.acceptButton.tooltip"));
        acceptButton.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                NewLexemeDialog.this.finished();
            }
        });

        JPanel helpP = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel nameP = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JPanel handP = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JPanel buttP = new JPanel(new FlowLayout(FlowLayout.TRAILING));

        nameP.add(nameL);
        nameP.add(nameT);

        handP.add(handL);
        handP.add(handB);

//        buttP.add(openButton);
        buttP.add(acceptButton);
        buttP.add(cancelButton);

        helpP.add(helpL);

        add(helpP);
        add(nameP);
        add(handP);
        add(buttP);

        pack();

        int x = Math.round(owner.getX() + (owner.getWidth() - getWidth()) / 2);
        int y = Math.round(owner.getY() + (owner.getHeight() - getHeight()) / 2);
        setLocation(x, y);
    }

    public void appear()
    {
        setVisible(true);
    }

    public void dismissed()
    {
        setVisible(false);
    }

    public void cancelled()
    {
//        System.out.println("Cancelled");
        dismissed();
    }

    protected void finished()
    {
        _owner.newLexemeInitialized(getLexemeName(), getHandedness());
        dismissed();
    }

    /**
     * Implement this method to apply some filtering/cleansing to a
     * user-chosen lexeme name.
     * 
     * @param rawString the string as the user provided it
     * @return the corresponding string that complies to some convention
     */
    public static String cleanseName(String rawString)
    {
        return rawString;
    }

    public String getLexemeName()
    {
        return cleanseName(nameT.getText());
    }

    public String getHandedness()
    {
        switch (handB.getSelectedIndex()) {
            case 0:
                return _conf.getProperty("newLex.hand.left.fnExt");
            case 1:
                return _conf.getProperty("newLex.hand.rite.fnExt");
            default:
                return _conf.getProperty("newLex.hand.both.fnExt");
        }
    }
}
