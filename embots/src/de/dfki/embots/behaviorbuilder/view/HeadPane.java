package de.dfki.embots.behaviorbuilder.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 *
 * @author Michael Kipp
 */
public class HeadPane extends JPanel
{
    private JLabel _title;

    public HeadPane(String title, JToolBar toolbar) {
        super();
        setLayout(new BorderLayout());
        _title = createTitleLabel(title);
        add(_title, BorderLayout.NORTH);
        if (toolbar != null) {
            add(toolbar, BorderLayout.CENTER);
            toolbar.setBorderPainted(false);
        }
    }

    public void setTitle(String title) {
        _title.setText(title);
    }

    public static JLabel createTitleLabel(String title)
    {
        JLabel label = new JLabel(title);
        label.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        label.setFont(new Font("Helvetica", Font.BOLD, 16));
        label.setForeground(Color.BLUE);
        return label;
    }
}
