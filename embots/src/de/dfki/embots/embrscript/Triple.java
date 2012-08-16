package de.dfki.embots.embrscript;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 *
 * @author Michael Kipp
 * @author Oliver Schoenleben
 */
public class Triple {

    public static final Triple ORIGIN = new Triple(0f,0f,0f);

    private DecimalFormat _d2 = new DecimalFormat("##.##", new DecimalFormatSymbols(Locale.US));
    public double x = 0f;
    public double y = 0f;
    public double z = 0f;

    public Triple() {}
    
    public Triple(double xx, double yy, double zz) {
        x = xx;
        y = yy;
        z = zz;
    }
    
    public Triple(Triple t) {
        x = t.x;
        y = t.y;
        z = t.z;
    }

    public void add(Triple t) {
        x += t.x;
        y += t.y;
        z += t.z;
    }
    
    public void scale(double s) {
        x *= s;
        y *= s;
        z *= s;
    }
    
    @Override
    public String toString() {
        return _d2.format(x) + ";" + _d2.format(y) + ";" + _d2.format(z);
    }
}
