/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.embots.framework.ui.eyetracking;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Youcef
 */
public class WeatherMap {
    
    
    private String weatherMapName;
    private HashSet<CityWeather> weatherCityHashSet;
    private HashSet<GazeObject> gazeObjectHashSet;
    private HashSet<CityWeather> lookedAtCities;
    private LastIntervalLookedAtCities lastIntervalLookedAtCities;    
                                 
    public WeatherMap(String weatherMapName) {
         weatherCityHashSet = new HashSet<CityWeather>();
         gazeObjectHashSet = new HashSet<GazeObject>();
         lookedAtCities = new HashSet<CityWeather>();
         this.weatherMapName=weatherMapName;  
         //lastIntervalLookedAtCities = new LastIntervalLookedAtCities();
         lastIntervalLookedAtCities  = new LastIntervalLookedAtCities();
    }
    
    /*public WeatherMap() {
         weatherCityHashSet = new HashSet<CityWeather>();   
         lookedAtCities = new HashSet<CityWeather>();
         this.weatherMapName="Empty map";         
    }*/
    
    public void setWeatherMapName(String weatherMapName) {
        this.weatherMapName = weatherMapName;
    }

    public String getWeatherMapName() {
        return weatherMapName;
    }    
    
    public void addGazeObject (GazeObject gazeObject) {
        gazeObjectHashSet.add(gazeObject);
        if (gazeObject instanceof CityWeather) weatherCityHashSet.add((CityWeather) gazeObject);
    }
    
    public CityWeather getCityWeather (String name) {
        
      CityWeather wc = new CityWeather("NO_CITY", 0, CityWeather.WeatherCondition.CLOUDY, 0, 0);
      CityWeather wcb = new CityWeather("buffer", 0, CityWeather.WeatherCondition.CLOUDY, 0, 0);
      
      Iterator<CityWeather> it = weatherCityHashSet.iterator();
                while(it.hasNext()) {
                    wcb = it.next();
                     if (wcb.getCityWeatherName()==name)
                            wc = wcb;
                }
                       
      return wc;             
    }
    
    public boolean isValidCityWeather(float x, float y) {
        
        boolean isValid = false;
        CityWeather wcb = new CityWeather("buffer", 0, CityWeather.WeatherCondition.CLOUDY, 0, 0);
        Iterator<CityWeather> it = weatherCityHashSet.iterator();
                while(it.hasNext()) {
                    wcb = it.next();                       
                    if (wcb.isInRange(x, y))
                        isValid = true;                     
                }   
        return isValid;
    }
    
    public CityWeather getCityWeather (float x, float y) {
        
      CityWeather wc = new CityWeather("NO_CITY", 0, CityWeather.WeatherCondition.CLOUDY, 0, 0);
      CityWeather wcb = new CityWeather("buffer", 0, CityWeather.WeatherCondition.CLOUDY, 0, 0);
      
      Iterator<CityWeather> it = weatherCityHashSet.iterator();
                while(it.hasNext()) {
                    wcb = it.next();                       
                    if (wcb.isInRange(x, y))
                        wc = wcb;
                }                       
        return wc; 
    }
    
    public GazeObject getGazeObject (float x, float y) {
        
      GazeObject wc = new GazeObject("NOTHING", 0, 0);
      GazeObject wcb;
      
      Iterator<GazeObject> it = gazeObjectHashSet.iterator();
                while(it.hasNext()) {
                    wcb = it.next();                       
                    if (wcb.isInRange(x, y))
                        wc = wcb;
                }
        return wc; 
    }
    
    // include new last interval functionality
    public void updateLookedAtCities (float x, float y) {
        
        if(isValidCityWeather(x, y)) {         
            lastIntervalLookedAtCities.update(getCityWeather(x, y));
        } else {
            //better do nothing!
            //lastIntervalLookedAtCities.update(new CityWeather("NO_CITY", 0, CityWeather.WeatherCondition.CLOUDY, 0, 0));
        }
        
    }
   
    public CityWeather getMostLookedAtCityWeather () {        
       
        return lastIntervalLookedAtCities.getLastIntervalMostLookedAtCity();
    }

    public LastIntervalLookedAtCities getLastIntervalLookedAtCities() {
        return lastIntervalLookedAtCities;
    }

    public void setLastIntervalLookedAtCities(LastIntervalLookedAtCities lastIntervalLookedAtCities) {
        this.lastIntervalLookedAtCities = lastIntervalLookedAtCities;
    }
      
}
