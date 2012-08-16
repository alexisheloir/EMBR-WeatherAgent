package de.dfki.embots.embrscript;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Oliver Schoenleben
 * @author Michael Kipp
 */
public enum EMBRTimeWarpKey {

    //UNDEFINED("undefined"),
    TIMEWARP_TAN("TAN"),
    TIMEWARP_EXP("EXP");

    private String _symbol;

    EMBRTimeWarpKey(String s) {
        _symbol = s;
    }

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
    
    public String toScript() {
        return _symbol;
    }

    @Override
    public String toString() {
        return _symbol;
    }
}
