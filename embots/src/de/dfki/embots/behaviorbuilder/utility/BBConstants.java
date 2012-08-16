package de.dfki.embots.behaviorbuilder.utility;

import de.dfki.embots.embrscript.*;
import java.awt.*;

/**
 * @author Oliver Schoenleben
 * @author Michael Kipp
 */
public interface BBConstants {

    
    public static final String JAR_FILE_NAME = "EMBRGui"; //~ only used in the following
    public static final String USER_DIR = System.getProperty("user.dir").replaceAll("\\\\", "/");
    public static final String CONFIG_FILENAME = "behaviorbuilder.config";

    public static final Color COLOR_STATUSLINE_GOOD = Color.GREEN.darker();
    public static final Color COLOR_STATUSLINE_BAD = Color.GRAY;

    /** Width (in characters) of the EMBRScript textarea */
    public static final int TEXT_COLS = 60;

    /** Height (in characters) of the EMBRScript textarea */
    public static final int TEXT_ROWS = 50;

    /** Text for the EMBRScript textarea */
    public static final Font TEXT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 10);

    /** Width in px of the (left-most) "caption column" in the pose control panel */
    public static final int LINE_CAPTION_WIDTH = 100; // *Could* be processed dynamically

    /** How many vertical space (in px) between the rows in the pose control panel */
    public static final int LINE_SEPARATOR_WIDTH = 24; // *Could* be processed dynamically

    /**
     * How many horizontal space (in px) between left and right controls in a
     * row of the pose control panel
     */
    public static final int LEFTRIGHT_SEPARATOR_WIDTH = 20;

    // Mirroring

    // The following have the same dependency as field #_arg:
    public static final double DEFAULT_MIRROR_X_LOC = 0.0;
    public static final double DEFAULT_PARALLEL_DIST = 0.5;
    public static final double DEFAULT_ALTERNATE_Z_LOC = 1.0;

    public static final boolean DEFAULT_NEW_POSES_ARE_ACTIVE = true;

    /**
     * Start time of a new pose: predecessing pose's start time plus
     * how many times its hold time?
     */
    public static final long DEFAULT_START_FROM_HOLD_MULTIPLIER = 3;
    public static final long DEFAULT_DURATION_TO_NEW_POSE = 400;

    public static final long DEFAULT_NEW_POSE_HOLD_DURATION = 0;
    public static final EMBRTimeWarpKey  DEFAULT_TIME_WARP_KEY  = EMBRTimeWarpKey.TIMEWARP_TAN;
    public static final double DEFAULT_TIME_WARP_SIGMA = 1d;


    // Default pose parameters
    // -----------------------
    // These are probably best defined in the code (probably here).

    public static final boolean DEFAULT_USE_BREATHING = false;
    public static final double DEFAULT_BREATHING_FREQUENCY = 0.25;
    public static final double DEFAULT_BREATHING_AMPLITUDE = 0.33;

    public static final boolean DEFAULT_USE_LOOK_AT = false;
    public static final Triple MIN_LOOK_AT = new Triple(-2.0, -2.0, -2.0);
    public static final Triple DEFAULT_LOOK_AT = new Triple(.0, -2.0, .56);
    public static final Triple MAX_LOOK_AT = new Triple(+2.0, +2.0, +2.0);

    public static final EMBRBodyGroup DEFAULT_LOOK_AT_BODY_GROUP = EMBRBodyGroup.HEAD_NECK;

    public static final boolean DEFAULT_USE_SHADER = false;
    public static final double DEFAULT_SHADE = 0.5;

    public static final EMBRMorphKey DEFAULT_MORPH_KEY = EMBRMorphKey.UNDEFINED;
    public static final double DEFAULT_MORPH_VALUE = 0.67;

    public static final EMBRHandshape DEFAULT_LH_HANDSHAPE = EMBRHandshape.UNDEFINED;

    public static final EMBRNormal DEFAULT_LEFT_ORIENTATION_AXIS = EMBRNormal.UNDEFINED;
    public static final Triple MIN_LEFT_HAND_ORIENTATION = new Triple(-1.0, -1.0, -1.0);
    public static final Triple DEFAULT_LEFT_HAND_ORIENTATION = new Triple(.0, .0, .0);
    public static final Triple MAX_LEFT_HAND_ORIENTATION = new Triple(+1.0, +1.0, +1.0);

    public static final boolean DEFAULT_USE_LSW = true;
    public static final double MIN_LEFT_SWIVEL = -180f;
    public static final double DEFAULT_LEFT_SWIVEL = 75f;
    public static final double MAX_LEFT_SWIVEL = 180f;

    public static final boolean DEFAULT_USE_LPOS = true;
    public static final Triple MIN_LEFT_HAND_POSITION = new Triple(-1.0, -1.0, -1.0);
    public static final Triple DEFAULT_LEFT_HAND_POSITION = new Triple(+.18, -.04, .0);
    public static final Triple MAX_LEFT_HAND_POSITION = new Triple(+1.0, +1.0, +1.0);

    public static final boolean DEFAULT_USE_RPOS = true;
    public static final Triple MIN_RIGHT_HAND_POSITION = new Triple(-1.0, -1.0, -1.0);
    public static final Triple DEFAULT_RIGHT_HAND_POSITION = new Triple(-.18, -.04, .0);
    public static final Triple MAX_RIGHT_HAND_POSITION = new Triple(+1.0, +1.0, +1.0);

    public static final boolean DEFAULT_USE_RSW = true;
    public static final double MIN_RIGHT_SWIVEL = -180f;
    public static final double DEFAULT_RIGHT_SWIVEL = 75f;
    public static final double MAX_RIGHT_SWIVEL = 180f;

    public static final Triple MIN_RIGHT_HAND_ORIENTATION = new Triple(-1.0, -1.0, -1.0);
    public static final Triple DEFAULT_RIGHT_HAND_ORIENTATION = new Triple(.0, .0, .0);
    public static final Triple MAX_RIGHT_HAND_ORIENTATION = new Triple(+1.0, +1.0, +1.0);
    public static final EMBRNormal DEFAULT_RIGHT_ORIENTATION_AXIS = EMBRNormal.UNDEFINED;

    public static final EMBRHandshape DEFAULT_RH_HANDSHAPE = EMBRHandshape.UNDEFINED;
}
