package de.dfki.embots.framework;

import de.dfki.embots.embrscript.VirtualCharacter;

/**
 * Global constants
 *
 * @author Michael Kipp
 */
public abstract class EMBOTSConstants
{
    public final static String BML_INPUT_TYPE = "semaine.data.bml.input";
    public final static String BML_FEEDBACK_TYPE = "semaine.data.bml.feedback";
    public final static String BML_INTERNAL_TYPE = "semaine.data.bml.internal";
    public final static String FML_TYPE = "semaine.data.fml.input";
    public final static String FML_FEEDBACK_TYPE = "semaine.data.fml.feedback";
    public final static String SEMAINE_FML_TYPE = "semaine.data.action.selected.function";
    public final static String EMBRSCRIPT_TYPE = "semaine.data.embrscript";
    public final static String RELOAD_TYPE = "semaine.data.reload.lexicon";
    public final static String AUDIO_PLAY_TYPE = "semaine.callback.embr.audio.play";
    public final static String AUDIO_LOWLEVEL_DATA_TYPE = "semaine.data.synthesis.lowlevel.audio";
    public final static String AUDIO_READY_TYPE = "semaine.callback.playback.audio";
    public final static String SYNTHESIS_READY_TYPE = "semaine.data.synthesis.plan.speechtimings";
    public final static String CONTEXT_TYPE = "semaine.data.state.context";
    public final static String AFFECT_TYPE = "semaine.data.affect";
    public final static String AUDIO_CALLBACK_TYPE = "semaine.callback.output.audio";

//    public final static String EMBR_CHARACTER = "Amber";
    public final static String EMBR_CHARACTER = VirtualCharacter.AMBER.getName();

//    public static final String EMBRSCRIPT_LEXICON_DIR = "../data/embrscript/";
    public static final String EMBRSCRIPT_LEXICON_DIR = "../data/lexicon/";
    public static final String EMBRSCRIPT_SIGN_LANGUAGE_DIR = "../data/sign-language/";
    public static final String BML_DIR = "../data/bml/";
    public static final String CONFIG_DIR = "../config/";
    public static final String FACE_MAPPING = "embots-facemapping.config";
}
