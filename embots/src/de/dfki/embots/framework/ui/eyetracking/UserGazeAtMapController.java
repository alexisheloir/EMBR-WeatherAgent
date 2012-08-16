/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.embots.framework.ui.eyetracking;

import de.dfki.embots.embrscript.Triple;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 *
 * @author Youcef
 */
public class UserGazeAtMapController implements ActionListener {

    private UserGazeAtMap userGazeAtMap;
    private UserGazeAtMapMainWindow ugammw = new UserGazeAtMapMainWindow();
    //private ArrayList<WeatherMapObserver> weatherMapObservers = new ArrayList<WeatherMapObserver>();    

    public UserGazeAtMapController() {

        userGazeAtMap = new UserGazeAtMap("default");
        ugammw.userLookingAtButton.addActionListener(this);
        ugammw.mapNameTextField.addActionListener(this);
        ugammw.loadMapButton.addActionListener(this);
        userGazeAtMap.addObserver(ugammw);
        ugammw.setVisible(true);


    }

    public UserGazeAtMapController(String mapName) {

        userGazeAtMap = new UserGazeAtMap(mapName);
        ugammw.userLookingAtButton.addActionListener(this);
        ugammw.mapNameTextField.addActionListener(this);
        ugammw.loadMapButton.addActionListener(this);
        userGazeAtMap.addObserver(ugammw);
        ugammw.setVisible(true);

    }

    public void act() {
        try {
            userGazeAtMap.act();
        } catch (Exception e) {
            
        }
    }
    public UserGazeAtMapMainWindow getMainWindow() {
        return ugammw;
    }

    public void setMainWindow(UserGazeAtMapMainWindow ugammw) {
        this.ugammw = ugammw;
    }

    public String getSelectedCity() {
        return userGazeAtMap.getSelectedCity();
    }

    public void loadMap(String mapName) {
        try {
            userGazeAtMap.setMap(mapName);
        } catch (IOException ioe) {
        }
    }

    public void actionPerformed(ActionEvent actionEvent) {

        if (actionEvent.getSource() == ugammw.userLookingAtButton) {
            ugammw.manualMostLookedAtCityLabel.setText("The city the user has beeen looking at most (Manual): " + this.getSelectedCity());
        } else if (actionEvent.getSource() == ugammw.mapNameTextField) {
            this.loadMap(ugammw.mapNameTextField.getText());
        } else if (actionEvent.getSource()==ugammw.loadMapButton) {
            this.loadMap(ugammw.mapNameTextField.getText());
        }
    }
    
    public String getLogline(){
        return userGazeAtMap.getLogLine();
    }
}
