

package de.dfki.embots.framework.ui.eyetracking;
import de.dfki.embots.embrscript.EMBRScript;



/**
 * Implement this interface for defining which gaze behavior will be used in
 * which state.
 * @author Daniel Puschmann
 */
public interface GazeStrategy {


    /**
     * Sets the offset between the direction of the eyes and the direction of the head.
     */
    public abstract void setOffset();

    /**
     * @return the time to wait before sending the next EMBRScript.
     */
    public long timeToWait();


    /**
     * @return if Amber is currently looking at the user
     */
    public boolean isLookingAtUser();

    /**
     * Sets the minimum for the random time in which no new EMBRScript is generated
     */
    public void setLowerRandomBound(int min);

    /**
     * Sets the interval for the random time in which no new EMBRScript is generated
     */
    public void setUpperRandomBound(int max);

    public void setEMBRScript(EMBRScript s);

    public void checkIfEMBRisLookedAt();

    public void setBMLInput(String bml);


}
