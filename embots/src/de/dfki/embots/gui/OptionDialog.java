package de.dfki.embots.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * This is taken from the Swing tutorial (ListDialog.java) and 
 * slightly modified.
 *
 * Use this modal dialog to let the user choose one string from a long
 * list.  See the main method for an example of using OptionDialog.  The
 * basics:
 * <pre>
String[] choices = {"A", "long", "array", "of", "strings"};
OptionDialog.initialize(componentInControllingFrame, choices,
"Dialog Title",
"A description of the list:");
String selectedName = OptionDialog.showDialog(locatorComponent,
initialSelection);
 * </pre>
 */
public class OptionDialog extends JDialog {

    private static OptionDialog _dialog;
    private static String _selectedString = "";
    private static int _selectedIndex = -1;
    private JComboBox _list;

    /**
     * Set up the dialog.  The first argument can be null,
     * but it really should be a component in the dialog's
     * controlling frame.
     */
    public static void initialize(Component comp, String[] possibleValues,
            String title, String labelText) {
        Frame frame = JOptionPane.getFrameForComponent(comp);
        _dialog = new OptionDialog(frame, possibleValues, title, labelText);
    }

    /**
     * Show the initialized dialog.  The first argument should
     * be null if you want the dialog to come up in the center
     * of the screen.  Otherwise, the argument should be the
     * component on top of which the dialog should appear.
     */
    public static String showDialog(Component comp, String initialValue) {
        if (_dialog != null) {
            if (initialValue != null) {
                _dialog.setValue(initialValue);
            }
            _dialog.setLocationRelativeTo(comp);
            _dialog.setVisible(true);
        } else {
            System.err.println("OptionDialog requires you to call initialize " + "before calling showDialog.");
        }
        return _selectedString;
    }

    /**
     * Shows an option dialog and returns the index number of the selected 
     * choice. Dialog must be initialized using the initialize(..) method.
     * Returns -1 if user cancelled.
     */
    public static int showDialogForIndex(Component comp) {
        if (_dialog != null) {
            _dialog.setLocationRelativeTo(comp);
            _dialog.setVisible(true);
        } else {
            System.err.println("OptionDialog requires you to call initialize " + "before calling showDialog.");
        }
        return _selectedIndex;
    }

    private void setValue(String newValue) {
        _selectedString = newValue;
        _list.setSelectedItem(_selectedString);
    }

    private OptionDialog(Frame frame, Object[] data, String title,
            String labelText) {
        super(frame, title, true);

        //buttons
        JButton cancelButton = new JButton("Cancel");
        final JButton setButton = new JButton("OK");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OptionDialog._selectedString = null;
                OptionDialog._selectedIndex = -1;
                OptionDialog._dialog.setVisible(false);
            }
        });
        setButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OptionDialog._selectedString = (String) (_list.getSelectedItem());
                OptionDialog._selectedIndex = _list.getSelectedIndex();
                OptionDialog._dialog.setVisible(false);
            }
        });
        getRootPane().setDefaultButton(setButton);

        //main part of the dialog
        _list = new JComboBox(data);
        _list.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    setButton.doClick();
                }
            }
        });

        //Create a container so that we can add a title around
        //the scroll pane.  Can't add a title directly to the 
        //scroll pane because its background would be white.
        //Lay out the label and scroll pane from top to button.
        JPanel labelPane = new JPanel();
        JLabel label = new JLabel(labelText);
        labelPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        labelPane.add(label);

        JPanel listPane = new JPanel();
        listPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        listPane.add(_list);

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        buttonPane.add(setButton);
        buttonPane.add(cancelButton);

        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(labelPane, BorderLayout.NORTH);
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.SOUTH);

        pack();
    }
}
