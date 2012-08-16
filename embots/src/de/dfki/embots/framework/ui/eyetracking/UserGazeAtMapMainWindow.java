/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.embots.framework.ui.eyetracking;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Youcef
 */
public class UserGazeAtMapMainWindow extends JFrame implements /*ActionListener,*/ WeatherMapObserver {

    private JLabel coordinatesLookedAtLabel;
    private JLabel cityLookedAtLabel;
    private JLabel mostLookedAtCityLabel;
    public JLabel manualMostLookedAtCityLabel;   
    private JPanel center;
    private JPanel south;
    public JButton userLookingAtButton ;
    private String mostLookedAtCityName;
    public JTextField mapNameTextField;
    public JButton loadMapButton;

    public UserGazeAtMapMainWindow() {
        
        setMainWindow();
    }
    
    private void setMainWindow() {
        
        coordinatesLookedAtLabel = new JLabel("The user is looking at the following coordinates: ");
        cityLookedAtLabel = new JLabel("The user is looking at the following city: ");
        mostLookedAtCityLabel = new JLabel("The city the user has beeen looking at most: ");
        manualMostLookedAtCityLabel = new JLabel("The city the user has beeen looking at most (Manual): ");
        userLookingAtButton = new JButton("User Looking At?"); 
        mapNameTextField = new JTextField(8);
        loadMapButton = new JButton("Load Map");
        
        
        center = new JPanel();
        center.setLayout(new BoxLayout(this.center, BoxLayout.PAGE_AXIS));
        center.add(coordinatesLookedAtLabel);
        center.add(cityLookedAtLabel);
        center.add(mostLookedAtCityLabel);
        center.add(manualMostLookedAtCityLabel);
        center.add(userLookingAtButton);
        
        south = new JPanel();
        south.setLayout(new BoxLayout(this.south, BoxLayout.LINE_AXIS));
        south.setBorder(BorderFactory.createEmptyBorder(0, 50, 10, 10));
        south.add(Box.createHorizontalGlue());
        south.add(mapNameTextField); 
        south.add(loadMapButton);
        //userLookingAtButton.addActionListener(this);
        
        //south.add(userLookingAtButton);
        this.setTitle("My window");
        this.setSize(400, 200);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.getContentPane().add(center, BorderLayout.CENTER);
        this.getContentPane().add(south, BorderLayout.SOUTH);
    }

    public String getMostLookedAtCityName() {
        return mostLookedAtCityName;
    }

    public void setMostLookedAtCityName(String mostLookedAtCityName) {
        this.mostLookedAtCityName = mostLookedAtCityName;
    }
          
    public void updateCoordinatesLookedAtLabel (String newCoordinates) {
        
        coordinatesLookedAtLabel.setText("The user is looking at the following coordinates: "+newCoordinates);
    }
    
    public void updateCityLookedAtLabel(String newCity) {
        
        cityLookedAtLabel.setText("The user is looking at the following city: "+newCity);
    }
    
    public void updateMostLookedAtCityLabel(String newCity) {
        
        mostLookedAtCityLabel.setText("The city the user has beeen looking at most is: "+newCity);
    }
    
    public void updateCoordinatesLookedAt(String s) {
        
         this.updateCoordinatesLookedAtLabel(s);
    }
   
    public void updateCityLookedAt(String s) {
        
        this.updateCityLookedAtLabel(s);        
    }
    
    public void updateMostLookedAtCityWeather(String s) {
        this.updateMostLookedAtCityLabel(s);
        this.mostLookedAtCityName=s;
        
    }
}
