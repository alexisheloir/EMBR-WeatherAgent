package de.dfki.embots.embrscript;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * An enumeration of the EMBR facial morph targets.
 * 
 * @author Oliver Schoenleben
 * @author Michael Kipp
 */
public enum EMBRMorphKey
{

    UNDEFINED("undefined"), //~ keep?
    BASIS("Basis"),
    EXP_SMILE_CLOSED("ExpSmileClosed"),
    EXP_ANGER("ExpAnger"),
    EXP_DISGUST("ExpDisgust"),
    EXP_FEAR("ExpFear"),
    EXP_SAD("ExpSad"),
    EXP_SURPRISE("ExpSurprise"),
    EXP_SMILE_OPEN("ExpSmileOpen"),
    MOD_BLINK_LEFT("ModBlinkLeft"),
    MOD_BLINK_RIGHT("ModBlinkRight"),
    MOD_SQUINT_RIGHT("ModEyeSquintRight"),
    MOD_SQUINT_LEFT("ModEyeSquintLeft"),
    MOD_BROW_DOWN_LEFT("ModBrowDownLeft"),
    MOD_BROW_DOWN_RIGHT("ModBrowDownRight"),
    MOD_BROW_IN_RIGHT("ModBrowInRight"),
    MOD_BROW_IN_LEFT("ModBrowInLeft"),
    MOD_BROW_UP_LEFT("ModBrowUpLeft"),
    MOD_BROW_UP_RIGHT("ModBrowUpRight"),
    MOD_LOOK_DOWN("ModLookDown"),
    MOD_LOOK_LEFT("ModLookLeft"),
    MOD_LOOK_RIGHT("ModLookRight"),
    MOD_LOOK_UP("ModLookUp"),
    PHON_AAH("Phonaah"),
    PHON_B_M_P("PhonB,M,P"),
    PHON_BIG_AAH("Phonbigaah"),
    PHON_CH_J_SH("Phonch,J,sh"),
    PHON_D_S_T("PhonD,S,T"),
    PHON_EE("Phonee"),
    PHON_EH("Phoneh"),
    PHON_F_V("PhonF,V"),
    PHON_I("Phoni"),
    PHON_K("PhonK"),
    PHON_N("PhonN"),
    PHON_OH("Phonoh"),
    PHON_OOH_Q("Phonooh,Q"),
    PHON_R("PhonR"),
    PHON_TH("Phonth"),
    PHON_W("PhonW");

    private String _symbol;

    private static final Map<String,EMBRMorphKey> lookup
          = new HashMap<String,EMBRMorphKey>();

    static
    {
        for(EMBRMorphKey s : EnumSet.allOf(EMBRMorphKey.class))
            lookup.put(s.toScript(), s);
    }

    EMBRMorphKey(String s)
    {
        _symbol = s;
    }

    public String toScript()
    {
        return _symbol;
    }

    /**
     * Maps string to enum entity.
     */
    public static EMBRMorphKey get(String scriptChunck)
    {
          return lookup.get(scriptChunck);
    }
    
}
