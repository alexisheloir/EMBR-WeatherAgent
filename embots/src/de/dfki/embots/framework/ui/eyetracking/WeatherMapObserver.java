/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.embots.framework.ui.eyetracking;

/**
 *
 * @author Youcef
 */
public interface WeatherMapObserver {
    
    public void updateCoordinatesLookedAt(String s);
    public void updateCityLookedAt(String s);
    public void updateMostLookedAtCityWeather(String s);
    
}
