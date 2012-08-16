package de.dfki.experiment;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * 
 * @author Patrick Kelleter<br>
 * <br>
 * The MicrophoneRecorder (in the following MR) works as a thread.<br>
 * Once instantiated it can be started  by .start() and will run as long as it is stopped by .stop().<br>
 * This can only be done once, afterwards the MR is dead.<br>
 * Alternatively the MR can be paused and resumed an infinity amount of time by the corresponding functions.<br>
 * Those won't kill the thread, but prevent that it is listening to any input while it is meant to be paused.<br>
 *<br>
 * Functionality:<br>
 * Whenever the MR is running and not paused it waits for the user to push and hold the 'Space' button.<br>
 * While the button is pushed the MR records incoming sounds from the microphone and saves it to the corresponding<br>
 * file specified by the current time, destination and proband values given to the constructor.<br>
 * It will be e.g. saved to "c:/soundlogs/proband1/pre_4020.wav" if the sound came in 4020ms after the startTime.<br>
 * 'pre' is a random prefix consisting of 3 characters so if you accidently forget to change the probands' name<br>
 * you will still be able to differentiate between the two resulting files.<br>
 * The soundfile will be finished and closed after the button is released and the MR will wait for the next time the<br>
 * button is pushed.<br>
 * <br>
 * If you just need the Push-To-Talk functionality, but not the actual recordings you can turn off recording changing<br>
 * the recordState variable.<br>
 * 
 */

public class MicrophoneRecorder {
	
	private static final String pttButton = "SPACE";
	private Thread recordingThread = null;
	private long startTime = 0;
	private File destFile = null;
	private boolean threadIsDead = false;
	private boolean threadIsPaused = false;
	private boolean recordState = false;
	private boolean buttonIsPressed = false;
	private boolean buttonWasPressed = false;
	private Mic myMic = null;
	private String randomPrefix = "";
        private boolean buttonWasReleased = false;
        private Experiment exp;

 
        public void flush () {
            buttonIsPressed = false;
            buttonWasPressed = false;
            buttonWasReleased = false;
        }
        
        public boolean buttonWasReleased () {
            return buttonWasReleased;
        }
        
	// intern class for the recorder thread (does the actual work)
	private Runnable recordingRunnable = new Runnable () {
		@Override
		public void run () {
			// thread stays alive until it is killed by .stop ()
			while (!threadIsDead) {
                                if (!buttonWasPressed && buttonIsPressed) {
                                    buttonWasPressed = true;
                                    try { exp.getLogger().logMicStart(); } catch(Exception e) {}
                                    if (!threadIsPaused) {
                                        if (recordState) {
                                            Item myItem = exp.getCurrentItem();; // (the LAST question)
                                            File currentFile = new File (destFile.getAbsolutePath() + "\\" + randomPrefix + "_" + (zxt ((System.currentTimeMillis() - startTime), 7) + "_" + myItem.getCity() + "_" + myItem.getWeatherCondition().getTemperature() + "_" + myItem.getWeatherCondition().getSymbol().toString() + ".wav"));
                                            System.out.println ("Recording to " + currentFile.getAbsolutePath());
                                            myMic.rec (currentFile);
					}
                                    }
                                }
                                if (buttonWasPressed && !buttonIsPressed) {
                                    try { exp.getLogger().logMicStop(); } catch(Exception e) {}
                                    buttonWasPressed = false;
                                    buttonWasReleased = true;
                                        if (!threadIsPaused) {
                                    		if (recordState) {
							myMic.stop ();
						}
                                        }
                                    try {
                                        exp.act();
                                    } catch(Exception e) {}
                                }
			}
			myMic.close();
			// kill off the thread...
		}
	};
	
        /*
         * helpmethod for zero-extension (342 -> 000342)
         */
        private String zxt (long inp, int length) {
            String result = Long.toString(inp);
            int dist = (length - result.length());
                for (int i = 0; i < dist; i++) {
                       result = "0" + result;
                }
            return result;
        }
        
	/**
	 * MicrophoneRecorder<br>
	 * <br>
	 * @param destination<br>
	 * 		Where on the HD shall the sound-files be written ? e.g. "c:/soundlogs/"<br>
	 * 		Care to use always '/' instead of '\' and end with a '/'<br>
	 * @param proband<br>
	 * 		This should be always the name or identifier for the current proband, so the<br>
	 * 		sound-files will be written to a sub-directory named by the proband<br>
	 * @param startTime<br>
	 * 		This time works as a zero-level-time for the EyeTracker and the MR.<br>
	 * 		The MR will name all sound-files after the time which has elapsed since this given<br>
	 * 		startTime and so should the EyeTracker do in its log-files.<br>
	 * @param corresComp<br>
	 * 		The window's output component where the keys should be listened to.<br>
	 */
	
	public MicrophoneRecorder (Experiment exp, String destination, String proband, long startTime, JComponent corresComp) {
                this.exp = exp;
            // check for valid a destination.
		File destFile = new File (destination);
		if (!destFile.isDirectory()) {
			System.out.println(destination + " is no valid directory.");
			System.exit (-1);
		}
		// create sub-directory if needed
		destFile = new File (destination + proband + "/");
		if (!destFile.exists()) {
			destFile.mkdir();
		}
		// save starting time
		this.startTime = startTime;
		// save current destination file
		this.destFile = destFile;
		// create the corresponding recorder thread
		this.recordingThread = new Thread (this.recordingRunnable);
		// define reactions to pressed button
			// what to do if the button is pressed
			Action buttonPressedAction = new AbstractAction() {
			    public void actionPerformed(ActionEvent e) {
			        buttonIsPressed = true;
			    }
			};
			// what to do if the button is released
			Action buttonReleasedAction = new AbstractAction() {
			    public void actionPerformed(ActionEvent e) {
			        buttonIsPressed = false;
			    }
			};
			// define corresponding key strokes to the actions
			corresComp.getInputMap().put (KeyStroke.getKeyStroke(this.pttButton), "buttonPressed");
			corresComp.getInputMap().put (KeyStroke.getKeyStroke("released " + this.pttButton), "buttonReleased");
			corresComp.getActionMap().put("buttonPressed", buttonPressedAction);
			corresComp.getActionMap().put("buttonReleased", buttonReleasedAction);
			// calc random prefix
			char a1 = (char)(Math.random() * 26 + 97);
			char a2 = (char)(Math.random() * 26 + 97);
			char a3 = (char)(Math.random() * 26 + 97);
			this.randomPrefix = "" + a1 + a2 + a3;
			// create the mic
			myMic = new Mic ();
	}
	
	/**
	 * start ()<br>
	 * <br>
	 * @return<br>
	 * 		returns whether the thread has been started<br>
	 *		(e.g. returns false if the thread already has been started before)<br>
	 * 
	 */
	
	public boolean start () {
		try {
			recordingThread.start();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * stop ()<br>
	 * <br>
	 * @return<br>
	 * 		returns whether the thread has been stopped/killed<br>
	 *		(e.g. returns false if the thread already has been started before)<br>
	 * 
	 */
	
	public boolean stop () {
		if (this.threadIsDead) {
			return false;	// Thread has been stopped already
		}
		this.threadIsDead = true;
		return true;
	}
	
	/**
	 * pause ()<br>
	 * <br>
	 * @return<br>
	 * 		returns whether the thread has been paused<br>
	 *		(e.g. returns false if the thread already has been paused before)<br>
	 * 
	 */
	
	public boolean pause () {
		if (this.threadIsPaused || this.threadIsDead) {
			return false;
		}
		this.threadIsPaused = true;
		return true;
	}
	
	/**
	 * resume ()<br>
	 * <br>
	 * @return<br>
	 * 		returns whether the thread has been resumed<br>
	 *		(e.g. returns false if the thread already has been resumed before)<br>
	 * 
	 */
	
	public boolean resume () {
		if (!this.threadIsPaused || this.threadIsDead) {
			return false;
		}
		this.threadIsPaused = false;
		return true;
	}
	
	
	/**
	 * awaitInput (minDuration)<br>
	 * <br>
	 * Pauses the calling thread until the current input has ended or,<br>
	 * if there has no input started yet, until a new input is started<br>
	 * PLUS finished.<br>
	 * An input will count as a valid input if it lasts at least minDuration milliseconds.<br>
	 * awaitInput () works like awaitInput (1)<br>
	 * <br>
	 * Returns whether the action was able to be performed.<br>
	 * 
	 */
	
	public boolean awaitInput (long minDuration) {
		if (minDuration < 1) {
			minDuration = 1;
		}
		// waiting for an input while paused or dead would force a deadlock -> return false
		if (this.threadIsPaused || this.threadIsDead) {
			return false;
		}
		/* wait until the following happened: 
		 * 1) if the button was pressed already ignore that and wait it to be released
		 * 2) wait for the button to be pressed (again)
		 * 3) wait for the button to be released (again)
		 * 
		 * after those 3 steps happened return to the caller
		 */
		while (buttonIsPressed) {
			try {
				Thread.currentThread().sleep (20);
			} catch (Exception e) {
			}
		}
		
		long startMS = 0, endMS = 0, diffMS = 0;
		
		while (diffMS < minDuration) {
		
			startMS = System.currentTimeMillis();
			
			while (!buttonIsPressed) {
				try {
					Thread.currentThread().sleep (20);
				} catch (Exception e) {
				}
			}
			
			while (buttonIsPressed) {
				try {
					Thread.currentThread().sleep (20);
				} catch (Exception e) {
				}
			}
			
			endMS = System.currentTimeMillis();
			
			diffMS = endMS - startMS;
		
		}
		
		return true;
	}
	
	public boolean awaitInput () {
		return awaitInput (1);
	}
	
	/**
	 * setRecordState ()<br>
	 * <br>
	 * @param state<br>
	 * 		Decides whether the MR is actually recording and saving the sound<br>
	 * 		to a file or just pretending to record (Push-To-Talk still works)<br>
	 * 		True means the MR will be recording.<br>
	 * 
	 */
	
	public void setRecordState (boolean state) {
		this.recordState = state;
	}
	
	/**
	 * getRecordState ()<br>
	 * <br>
	 * @return<br>
	 *	 	Returns whether the MR is actually recording sounds. True means it does.<br>
	 * 
	 */
	
	public boolean getRecordState () {
		return this.recordState;
	}
	
	/**
	 * isButtonPressed ()<br>
	 * <br>
	 * 	@return<br>
	 * 		Returns whether the PTT-Button is currently pressed or not<br>
	 */
	
	public boolean isButtonPressed () {
		if (this.threadIsDead) {
			return false;
		}
		return this.buttonIsPressed;
	}
	
}
