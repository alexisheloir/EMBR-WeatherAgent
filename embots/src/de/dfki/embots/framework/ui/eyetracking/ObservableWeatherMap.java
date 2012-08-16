/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.embots.framework.ui.eyetracking;

/**
 *
 * @author Youcef
 */
public interface ObservableWeatherMap {
    
    public void addObserver(WeatherMapObserver obs);
    public void updateObservers (float x,float y);
    public void deleteObservers ();
    
}
