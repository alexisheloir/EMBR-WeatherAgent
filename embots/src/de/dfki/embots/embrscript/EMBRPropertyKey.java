package de.dfki.embots.embrscript;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Possible key words for meta-information on pose sequences and single poses.
 *
 * @author Michael Kipp
 */
public enum EMBRPropertyKey {
    
    MODALITY("MODALITY"),
    COMMENT("COMMENT");

    private String _symbol;

    private static final Map<String,EMBRPropertyKey> lookup
          = new HashMap<String,EMBRPropertyKey>();

    static
    {
        for(EMBRPropertyKey s : EnumSet.allOf(EMBRPropertyKey.class))
            lookup.put(s.toScript(), s);
    }

    public static EMBRPropertyKey get(String str)
    {
          return lookup.get(str);
    }

    private EMBRPropertyKey(String s) {
        _symbol = s;
    }

    public String toScript() {
        return _symbol;
    }
}
