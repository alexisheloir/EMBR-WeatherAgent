/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.experiment;

import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Youcef
 */
public class Map {
    private String weatherMapName;
    private HashSet<City> cityHashSet; 
    private HashSet<City> lookedAtCities;
                                 
    public Map(String weatherMapName) {
         cityHashSet = new HashSet<City>();   
         lookedAtCities = new HashSet<City>();
         this.weatherMapName=weatherMapName;
    }

    public String getWeatherMapName() {
        return weatherMapName;
    }    
    
    public void addCity (City City) {
        cityHashSet.add(City);
    }
    
    
    public HashSet<City> getCities() {
        return cityHashSet;
    }
}
