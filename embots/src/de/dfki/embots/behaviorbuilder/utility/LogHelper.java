package de.dfki.embots.behaviorbuilder.utility;

import de.dfki.embots.behaviorbuilder.*;
import java.util.logging.*;

/**
 * Convenient utility class for everyday jobs (logging etc.)
 *
 * @author Oliver Schoenleben
 */
public class LogHelper {

    // HELPERS
    // =======

    /**
     * Gives out a simple log message.
     * @param msg the message to be logged
     */
    public static void log(String msg) {
        log(Level.INFO, msg, null);
    }

    /**
     * LogHelper for indicating an error.
     * @param msg A brief message describing what went wrong
     * @param param The logging parameter; usually an exception
     */
    public static void warn(String msg, Object param) {
        log(Level.SEVERE, msg, param);
    }

    /**
     * LogHelper for logging.
     * @param level The log level
     * @param msg A brief message describing what went wrong
     * @param param The logging parameter; usually an exception
     * @see Logger
     */
    public static void log(Level level, String msg, Object param) {
        String myClassName = BehaviorBuilder.class.getName();
        Logger.getLogger(myClassName).log(level, msg, param);
    }
}
