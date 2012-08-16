package de.dfki.embots.behaviorbuilder.view;

import javax.swing.JSlider;

/**
 * An enhanced JSlider allowing to manipulate 1-dimensional double precision
 * values.
 *
 * Objects of this class offer their callers a convenient way to read and
 * manipulate the underlying data by asking for (and delivering) "actual"
 * values, so they do not have to care about the internal representation,
 * Slider-wise, that is handled transparently in the background.
 *
 * @author Oliver Schoenleben
 */
@SuppressWarnings("serial")
public class Slider1d extends JSlider {

    // The bounds, in the mentioned "actual" fashion
    protected double _minVal, _maxVal;

    /** Gets the minimum possible actual value of this slider. */
    public double getMinVal() { return _minVal; }

    /** Gets the maximum possible actual value of this slider. */
    public double getMaxVal() { return _maxVal; }
    
    /** Sets the minimum possible actual value of this slider. */
    public void setMinVal(double minVal) { _minVal = minVal; }

    /** Sets the maximum possible actual value of this slider. */
    public void setMaxVal(double maxVal) { _maxVal = maxVal; }

    /**
     * Constructs an object, setting the bounds and the initial value.
     *
     * @param minVal the minimum possible value, in "actual" form
     * @param maxVal the maximum possible value, in "actual" form
     * @param iniVal the initial value, also in "actual" form
     */
    public Slider1d(double minVal, double maxVal, double iniVal) {
        Slider1d.this.setMinVal(minVal);
        Slider1d.this.setMaxVal(maxVal);
        Slider1d.this.setActualValue(iniVal);
    }

    /**
     * Retrieves the underlying value, in the "actual"/"meant" form.
     * @return the actual value
     */
    public double getActualValue() {
        return i2a(super.getValue());
    }

    /**
     * Sets the underlying value, in the "meant" form.
     * @param val the value for the slider to be adjusted to
     */
    public void setActualValue(double val) {
        // TODO Not tested on negative values!
        setValue(a2i(val));
    }

    /**
     * Compute an actual (meant) value from its internal representation.
     *
     * The min and max values defined at construction time have to be
     * used here.
     *
     * @param internalValue a value in the JSlider-compliant form
     * @return the value translated to the actual form.
     */
    public double i2a(int internalValue) {
        return _minVal + (_maxVal - _minVal) * internalValue / 100;
    }

    /**
     * Compute the internal representation of a value from its actual form.
     *
     * @param actualValue the actual value to be translated
     * @return the value in a form to be used with JSlider operations
     */
    public int a2i(double actualValue) {
        return (int) ( 100 * (actualValue - _minVal) / (_maxVal - _minVal) );
    }
}
