package de.dfki.embots.behaviorbuilder.view;

import de.dfki.embots.embrscript.Triple;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A component (JPanel) featuring 3 sliders allowing for adjusting Tripels.
 *
 * Objects of this class offer their callers a convenient way to read and
 * manipulate the underlying data by asking for (and delivering) "actual"
 * values, so they do not have to care about the internal representation,
 * Slider-wise, that is handled transparently in the background.
 *
 * @author Oliver Schoenleben
 */
@SuppressWarnings("serial")
public class Slider3d extends JPanel implements ChangeListener
{

    /** The GUI sliders to show and let adjust the 3d-value */
    Slider1d x, y, z;
    /** The represented three-dimensional value */
    double _x, _y, _z;

    /**
     * Constructs the object "the simple way".
     *
     * @param minVal minimum possible (actual) value for every slider
     * @param maxVal maximum possible (actual) value for every slider
     * @param xVal the initial (actual) value for the X value
     * @param yVal the initial (actual) value for the Y value
     * @param zVal the initial (actual) value for the Z value
     */
    public Slider3d(
            double minVal, double maxVal,
            double xVal, double yVal, double zVal)
    {

        this(
                new Triple(minVal, minVal, minVal),
                new Triple(maxVal, maxVal, maxVal),
                new Triple(xVal, yVal, zVal));
    }

    /**
     * Constructs the object "the powerful" way.
     *
     * @param minVal the minimum possible (actual) value for each slider, given as a Triple
     * @param maxVal the maximum possible (actual) value for each slider, given as a Triple
     * @param iniVal the initial (actual) value for each of the sliders
     */
    public Slider3d(Triple minVal, Triple maxVal, Triple iniVal)
    {
        this("X", "Y", "Z", minVal, maxVal, iniVal);
    }

    /**
     * Constructs the object "the powerful" way. Also allows labels.
     *
     * @param minVal the minimum possible (actual) value for each slider, given as a Triple
     * @param maxVal the maximum possible (actual) value for each slider, given as a Triple
     * @param iniVal the initial (actual) value for each of the sliders
     */
    public Slider3d(String labelX, String labelY, String labelZ,
            Triple minVal, Triple maxVal, Triple iniVal)
    {

//        setLayout(new BorderLayout(0,0));
        setLayout(new GridLayout(3, 1));

        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(80, 60));

        x = new Slider1d(minVal.x, maxVal.x, iniVal.x);
        y = new Slider1d(minVal.x, maxVal.y, iniVal.y);
        z = new Slider1d(minVal.y, maxVal.z, iniVal.z);

        x.addChangeListener(this);
        y.addChangeListener(this);
        z.addChangeListener(this);

        JPanel px = new JPanel(new BorderLayout());
        JPanel py = new JPanel(new BorderLayout());
        JPanel pz = new JPanel(new BorderLayout());

        px.add(new JLabel(" " + labelX), BorderLayout.WEST);
        py.add(new JLabel(" " + labelY), BorderLayout.WEST);
        pz.add(new JLabel(" " + labelZ), BorderLayout.WEST);

        px.add(x, BorderLayout.CENTER);
        py.add(y, BorderLayout.CENTER);
        pz.add(z, BorderLayout.CENTER);

        add(px);
        add(py);
        add(pz);

//        add(px, BorderLayout.NORTH);
//        add(py, BorderLayout.CENTER);
//        add(pz, BorderLayout.SOUTH);

        setPreferredSize(
                new Dimension(
                Math.round(getPreferredSize().width * 2.5f),
                getPreferredSize().height));
    }

    /**
     * Gets notified when any of the sliders gets changed, and updates
     * the internal (actual) Triple representation according to all 3
     * sliders.
     * Implements ChangeListener's stateChanged().
     *
     * @param e the event received from Swing on slider change
     */
    @Override
    public void stateChanged(ChangeEvent e)
    {
        _x = x.getActualValue();
        _y = y.getActualValue();
        _z = z.getActualValue();
    }

    /**
     * Allows for setting the sliders "from the outside", by giving
     * "actual" values, from which the internal representations are
     * computed.
     *
     * @param t the actual Triple value to be set
     */
    public void setActualValue(Triple t)
    {
        x.setActualValue(t.x);
        y.setActualValue(t.y);
        z.setActualValue(t.z);
    }

    /**
     * Get the currently set value of the Triple, in "actual" form
     * (the correct value in the range specified in on construction).
     *
     * @return the Triple in the form you want it to be
     */
    public Triple getActualValue()
    {
        return new Triple(
                x.getActualValue(),
                y.getActualValue(),
                z.getActualValue());
    }

    /**
     * Registers a listener at each slider, so that after the change
     * of a slider, this class's {@link #getActualValue()} can be called.
     * This is actually a bit of a hack; a scalable class would register
     * itself as listener at the controls, and then fire a property change
     * on its own.
     * @param l
     */
    public void addChangeListener(ChangeListener l)
    {
        x.addChangeListener(l);
        y.addChangeListener(l);
        z.addChangeListener(l);
    }

    /**
     * Lets the caller enable or disable all the sliders at once.
     * To be used like JComponent's setEnabled(), which is overridden here.
     *
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled)
    {
        // dunn werk: for ( Component c : getComponents() ) c.setEnabled(enabled);
        x.setEnabled(enabled);
        y.setEnabled(enabled);
        z.setEnabled(enabled);
    }
}
