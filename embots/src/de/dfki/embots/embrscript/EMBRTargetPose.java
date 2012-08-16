package de.dfki.embots.embrscript;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Michael Kipp
 */
public enum EMBRTargetPose
{
    SHRUG("shrug");

    private String _symbol;

    private static final Map<String,EMBRTargetPose> lookup
          = new HashMap<String,EMBRTargetPose>();

    static
    {
        for(EMBRTargetPose s : EnumSet.allOf(EMBRTargetPose.class))
            lookup.put(s.toScript(), s);
    }

    public static EMBRTargetPose get(String scriptChunck)
    {
          return lookup.get(scriptChunck);
    }

    EMBRTargetPose(String s) {
        _symbol = s;
    }

    public String toScript() {
        return _symbol;
    }
}
