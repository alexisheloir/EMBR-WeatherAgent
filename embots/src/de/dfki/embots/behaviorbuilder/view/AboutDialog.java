package de.dfki.embots.behaviorbuilder.view;

import de.dfki.embots.behaviorbuilder.utility.BBConstants;
import de.dfki.embots.behaviorbuilder.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

/**
 * 
 * 
 * @author Oliver Schoenleben
 */
public class AboutDialog extends JDialog implements BBConstants {

    public AboutDialog(BehaviorBuilder owner) {
        super(owner, ModalityType.MODELESS);

        setLayout(new BorderLayout(12,12));
        add(new JLabel("EMBRPoser " + BehaviorBuilder.VERSION), BorderLayout.NORTH);

        JEditorPane content;
        try {
            content = new JEditorPane(new URL("http://www.schoenleben.de/embrposer.php?v=1.0"));
            //throw new IOException();
        } catch (IOException x) {
            x.printStackTrace(System.out);
            content = new JEditorPane("text/html", "<em>huha</em> bla");
        }
        content.setEditable(false);

        JScrollPane cp = new JScrollPane(content);
        cp.setPreferredSize(new Dimension(600,400));
        add(cp, BorderLayout.CENTER);

        JButton hideButton = new JButton("Hide");
        hideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        add(hideButton, BorderLayout.SOUTH);

        try {setAlwaysOnTop(true); }
        catch (SecurityException x) {}

        pack();

        int x = Math.round( owner.getX() + (owner.getWidth()-getWidth())/2 );
        int y = Math.round( owner.getY() + (owner.getHeight()-getHeight())/2 );
        setLocation(x,y);
    }
}
