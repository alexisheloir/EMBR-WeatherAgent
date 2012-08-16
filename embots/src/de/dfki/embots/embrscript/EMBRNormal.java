/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.embots.embrscript;

import java.util.HashMap;

/**
 *
 * (should maybe renamed to EMBRAxis)
 *
 * @author Oliver Schoenleben
 * @author Michael Kipp
 */
public enum EMBRNormal
{

    UNDEFINED("undefined"),
    X_AXIS("Xaxis"),
    Y_AXIS("Yaxis"),
    Z_AXIS("Zaxis");
    private String _symbol;
    private static HashMap<String, EMBRNormal> _symbol2normal =
            new HashMap<String, EMBRNormal>();

    static {
        for (EMBRNormal g : values()) {
            _symbol2normal.put(g.toScript().toLowerCase(), g);
        }
    }

    EMBRNormal(String s)
    {
        _symbol = s;
    }

    public String toScript()
    {
        return _symbol;
    }

    /**
     * Not case-sensitive!
     */
    public static EMBRNormal parseNormal(String str) {
        return _symbol2normal.get(str.trim().toLowerCase());
    }
}
