package de.dfki.embots.embrscript;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * An enumeration of the autonomous behaviors (currently breathing).
 * 
 * @author Oliver Schoenleben
 * @author Michael Kipp
 */
public enum EMBRAutonomousBehaviorKey {

    UNDEFINED("undefined"),
    BREATHING_AMPLITUDE("breathingAmplitude"),
    BREATHING_FREQUENCY("breathingFrequency");
    private String _symbol;

    EMBRAutonomousBehaviorKey(String s) {
        _symbol = s;
    }

    private static final Map<String,EMBRAutonomousBehaviorKey> lookup
          = new HashMap<String,EMBRAutonomousBehaviorKey>();

    static
    {
        for(EMBRAutonomousBehaviorKey s : EnumSet.allOf(EMBRAutonomousBehaviorKey.class))
            lookup.put(s.toScript(), s);
    }

    public static EMBRAutonomousBehaviorKey get(String scriptChunck)
    {
          return lookup.get(scriptChunck);
    }

    public String toScript() {
        return _symbol;
    }
}
