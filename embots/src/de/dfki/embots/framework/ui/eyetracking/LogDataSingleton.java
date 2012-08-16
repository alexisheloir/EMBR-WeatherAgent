package de.dfki.embots.framework.ui.eyetracking;

import de.dfki.carmina.eyeTrackerLogger.dataProcessor.LogData;

/**
 *
 * @author Daniel Puschmann
 */
public class LogDataSingleton {

    private static LogDataSingleton instance = new LogDataSingleton();

    // parameters provided by the EyeTracker
    public String datastring;
    public String timestamp;
    public float x_gazepos_lefteye;
    public float y_gazepos_lefteye;
    public float x_gazepos_righteye;
    public float y_gazepos_righteye;
    public float x_eyepos_lefteye;
    public float y_eyepos_lefteye;
    public float x_eyepos_righteye;
    public float y_eyepos_righteye;
    public float diameter_pupil_lefteye;
    public float diameter_pupil_righteye;
    public float distance_lefteye;
    public float distance_righteye;
    public long validity_lefteye;
    public long validity_righteye;

    private String output;


    /**
     * Should not be used from outside
     */
    private LogDataSingleton(){

    }

    public static LogDataSingleton getInstance() {
        return instance;
    }

    public void setData(LogData data){
            this.timestamp = data.timestamp;
            this.x_gazepos_lefteye = data.x_gazepos_lefteye;
            this.y_gazepos_lefteye = data.y_gazepos_lefteye;
            this.x_gazepos_righteye = data.x_gazepos_righteye;
            this.y_gazepos_righteye = data.y_gazepos_righteye;
            this.x_eyepos_lefteye = data.x_eyepos_lefteye;
            this.y_eyepos_lefteye = data.y_eyepos_lefteye;
            this.x_eyepos_righteye = data.x_eyepos_righteye;
            this.y_eyepos_righteye = data.y_eyepos_righteye;
            this.diameter_pupil_lefteye = data.diameter_pupil_lefteye;
            this.diameter_pupil_righteye = data.diameter_pupil_righteye;
            this.distance_lefteye = data.distance_lefteye;
            this.distance_righteye = data.distance_righteye;
            this.validity_lefteye = data.validity_lefteye;
            this.validity_righteye = data.validity_righteye;
            this.toString();
    }

    	/**
	 * generates an output string containing all parameters provided by the EyeTracker
	 */
        @Override
	public String toString()
	{
		output = "";
		addToOutput("timestamp", timestamp);
		addToOutput("x_gazepos_lefteye", x_gazepos_lefteye);
		addToOutput("y_gazepos_lefteye", y_gazepos_lefteye);
		addToOutput("x_gazepos_righteye", x_gazepos_righteye);
		addToOutput("y_gazepos_righteye", y_gazepos_righteye);
		addToOutput("x_eyepos_lefteye", x_eyepos_lefteye);
		addToOutput("y_eyepos_lefteye", y_eyepos_lefteye);
		addToOutput("x_eyepos_righteye", x_eyepos_righteye);
		addToOutput("y_eyepos_righteye", y_eyepos_righteye);
		addToOutput("diameter_pupil_lefteye", diameter_pupil_lefteye);
		addToOutput("diameter_pupil_righteye", diameter_pupil_righteye);
		addToOutput("distance_lefteye", distance_lefteye);
		addToOutput("distance_righteye", distance_righteye);
		addToOutput("validity_lefteye", validity_lefteye);
		addToOutput("validity_righteye", validity_righteye);

		return output;
	}


	private void addToOutput(String name, String value)
	{
		output += name + ": " + value + "\n";
	}

	private void addToOutput(String name, float value)
	{
		output += name + ": " + value + "\n";
	}

}
