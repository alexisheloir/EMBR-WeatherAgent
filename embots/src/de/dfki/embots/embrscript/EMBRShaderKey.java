package de.dfki.embots.embrscript;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * An enumeration of the EMBR morph targets.
 *
 * @author Oliver Schoenleben
 * @author Michael Kipp
 */
public enum EMBRShaderKey {

    UNDEFINED("undefined"),
    BLUSHING("blushing");

    private String _symbol;

    private static final Map<String,EMBRShaderKey> lookup
          = new HashMap<String,EMBRShaderKey>();

    static
    {
        for(EMBRShaderKey s : EnumSet.allOf(EMBRShaderKey.class))
            lookup.put(s.toScript(), s);
    }

    public static EMBRShaderKey get(String scriptChunck)
    {
          return lookup.get(scriptChunck);
    }

    EMBRShaderKey(String s) {
        _symbol = s;
    }

    public String toScript() {
        return _symbol;
    }
}
