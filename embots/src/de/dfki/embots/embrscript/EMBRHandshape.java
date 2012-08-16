package de.dfki.embots.embrscript;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Michael Kipp
 * @author Oliver Schoenleben
 */
public enum EMBRHandshape {
 
    UNDEFINED("undefined"), //~ needed?
    CLAW("hands_claw"),
    FIST("hands_fist"),
    INDEXFINGER("hands_index"),
    OPEN_RELAXED("hands_open-relaxed"),
    OPEN_SPREAD("hands_open-spread"),
    OPEN_STRAIGHT("hands_open-straight"),
    PURSE("hands_purse"),
    FINGERRING("hands_ring"),
    DGS_A("hands_DGS_A"),
    DGS_B("hands_DGS_B"),
    DGS_C("hands_DGS_C"),
    DGS_D("hands_DGS_D"),
    DGS_E("hands_DGS_E"),
    DGS_F("hands_DGS_F"),
    DGS_G("hands_DGS_G"),
    DGS_H("hands_DGS_H"),
    DGS_I("hands_DGS_I"),
    DGS_J("hands_DGS_J"),
    DGS_K("hands_DGS_K"),
    DGS_L("hands_DGS_L"),
    DGS_M("hands_DGS_M"),
    DGS_N("hands_DGS_N"),
    DGS_O("hands_DGS_O"),
    DGS_P("hands_DGS_P"),
    DGS_Q("hands_DGS_Q"),
    DGS_R("hands_DGS_R"),
    DGS_T("hands_DGS_T"),
    DGS_U("hands_DGS_U"),
    DGS_V("hands_DGS_V"),
    DGS_W("hands_DGS_W"),
    DGS_X("hands_DGS_X"),
    DGS_Y("hands_DGS_Y"),
    DGS_Z("hands_DGS_Z"),
    ASL_G("hands_ASL_G"),
    ASL_M("hands_ASL_M"),
    ASL_N("hands_ASL_N"),
    ASL_T("hands_ASL_T"),
    DGS_SCH("hands_DGS_SCH"),
    ASL_1CL("hands_ASL_1CL"),
    ASL_2CL("hands_ASL_2CL"),
    ASL_3CL("hands_ASL_3CL"),
    ASL_4CL("hands_ASL_4CL"),
    ASL_5aCL("hands_ASL_5aCL"),
    ASL_5bCL("hands_ASL_5bCL"),
    ASL_ACL("hands_ASL_ACL"),
    ASL_BCL("hands_ASL_BCL"),
    ASL_CCaL("hands_ASL_CCL"),
    ASL_CCbL("hands_ASL_CbCL"),
    ASL_FCL("hands_ASL_FCL"),
    ASL_ICL("hands_ASL_ICL"),
    ASL_ILYCL("hands_ASL_ILYCL"),
    ASL_MCL("hands_ASL_M"),
    ASL_NCL("hands_ASL_N"),
    ASL_SCL("hands_ASL_SCL"),
    ASL_TCL("hands_ASL_TCL"),
    ASL_VaCL("hands_ASL_VaCL"),
    ASL_VbCL("hands_ASL_VbCL"),
    ASL_YCL("hands_ASL_YCL");

    private String _symbol;

    private static final Map<String,EMBRHandshape> lookup
          = new HashMap<String,EMBRHandshape>();

    static
    {
        for(EMBRHandshape s : EnumSet.allOf(EMBRHandshape.class))
            lookup.put(s.toScript(), s);
    }

    public static EMBRHandshape get(String scriptChunck)
    {
          return lookup.get(scriptChunck);
    }

    EMBRHandshape(String s) {
        _symbol = s;
    }

    public String toScript() {
        return _symbol;
    }
}
