package de.dfki.embots.framework.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Michael Kipp
 */
public class FrontendFeedbackView extends JFrame
{
    private static final int TEXTWIDTH = 60, TEXTHEIGHT = 40;
    private static final int FONT_SIZE = 12;
    private JTextArea _text;

    public FrontendFeedbackView() {
        super();
        setTitle("BML Feedback");
        init();
        pack();
    }

    private void init() {
        _text = new JTextArea(TEXTHEIGHT, TEXTWIDTH);
        _text.setBackground(Color.BLACK);
        _text.setForeground(Color.GREEN.brighter());
        _text.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
        JScrollPane textScrollPane = new JScrollPane(_text);

        JPanel buttonPane = new JPanel();
        JButton clearButton = new JButton("clear");
        clearButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae)
            {
                _text.setText("");
            }
        });

        buttonPane.add(clearButton);

        // layout
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, textScrollPane);
        add(BorderLayout.SOUTH, buttonPane);
    }

    public void addMessage(String msg) {
        _text.insert(msg + "\n\n", _text.getText().length());
        _text.setCaretPosition(_text.getText().length());
    }
}
