/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.experiment;

import de.dfki.embots.framework.ui.eyetracking.UserGazeAtMapController;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;

/**
 *
 * @author Andrey
 */
public class Logger {

    private static final String EXPERIMENT_PATH = ConfigForExperiment.getProperties("Experiment.Setup.Path");
    private File logFile;
    private String logLine;
    private UserGazeAtMapController userGazeAtMapController;
    private Boolean started = false;
    
    public Logger(UserGazeAtMapController userGazeAtMapController, int participant){
        logFile = new File(EXPERIMENT_PATH+"textlogs\\log" + participant + ".txt");
        this.userGazeAtMapController = userGazeAtMapController;
    }
    
    public void act() {
        if (started) try {
            writeToLog();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("IOException " + ex + "in Logger\n");
        }
    }

    public void act(String newLine)
    {
       if (started) try {
        FileWriter outFile = new FileWriter(logFile, true);
        PrintWriter outWriter = new PrintWriter(outFile);
        outWriter.println(newLine);
        outWriter.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("IOException " + ex + "in Logger\n");
        }
        
    }
    
    public void getLogLine(){
        logLine = userGazeAtMapController.getLogline();
    }
    
    public void writeToLog() throws IOException{
        this.getLogLine();
        FileWriter outFile = new FileWriter(logFile, true);
        PrintWriter outWriter = new PrintWriter(outFile);
        outWriter.println(logLine);
        outWriter.close();
    }
    
    public void logStart(){
        started = true;
    }
    
    public void logStop(){
        started = false;
    }

    public void logMicStart() throws IOException {
        FileWriter outFile = new FileWriter(logFile, true);
        PrintWriter outWriter = new PrintWriter(outFile);
        outWriter.println("Mic started\n");
        outWriter.close();
    }

    public void logMicStop() throws IOException {
        FileWriter outFile = new FileWriter(logFile, true);
        PrintWriter outWriter = new PrintWriter(outFile);
        outWriter.println("Mic stopped\n");
        outWriter.close();
    }
}
