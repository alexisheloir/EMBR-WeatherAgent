/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.embots.framework.ui.eyetracking;

import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Youcef
 */
class LastIntervalLookedAtCities {
    
    protected LinkedList<CityWeather> lastIntervalLookedAtCitiesLinkedList = new LinkedList<CityWeather>();
    protected final int MAX_LAST_INTERVAL_LIST = 10;
    
    public LastIntervalLookedAtCities () {
        
    }
    
    public void update (CityWeather cw) {
        
        if (lastIntervalLookedAtCitiesLinkedList.size() < MAX_LAST_INTERVAL_LIST) {
            cw.cityWeatherLookedAtByUser();
            lastIntervalLookedAtCitiesLinkedList.add(cw);
        } else {
            cw.cityWeatherLookedAtByUser();
            lastIntervalLookedAtCitiesLinkedList.remove();
            lastIntervalLookedAtCitiesLinkedList.add(cw);
        }
    }
    
    public CityWeather getLastIntervalMostLookedAtCity () {
        
        CityWeather mlc = new CityWeather("NO_CITY", 0, CityWeather.WeatherCondition.CLOUDY, 0, 0);
        CityWeather wcb = new CityWeather("buffer", 0, CityWeather.WeatherCondition.CLOUDY, 0, 0);
        LinkedList <CityWeather> bufferList = new LinkedList<CityWeather>();
        float weightUnit = (1/MAX_LAST_INTERVAL_LIST);
        int weightFactor = 1;
        for ( Iterator<CityWeather> it = lastIntervalLookedAtCitiesLinkedList.iterator();it.hasNext();) {
            wcb = it.next();
            weightFactor*=2;
            wcb.setTimeLookedAt(weightFactor*weightUnit);
            // Algorithm to get CityWeather with most appearance.
            if (wcb.getCityWeatherName()=="NO_CITY")
                continue;
            else if (bufferList.contains(new CityWeather(wcb.getCityWeatherName(), 0, CityWeather.WeatherCondition.CLOUDY, 0, 0))) {
                mlc = bufferList.remove(bufferList.indexOf(wcb));
                mlc.cityWeatherLookedAtByUser();
                bufferList.add(mlc);
            } else {
                mlc = new CityWeather(wcb.getCityWeatherName(), 0, CityWeather.WeatherCondition.CLOUDY, 0, 0);
                mlc.cityWeatherLookedAtByUser();
                bufferList.add(mlc);
            }
        }
        
        mlc = new CityWeather("User looking at no city", 0, CityWeather.WeatherCondition.CLOUDY, 0, 0);
        wcb = new CityWeather("buffer", 0, CityWeather.WeatherCondition.CLOUDY, 0, 0);        
        for (Iterator<CityWeather> it = bufferList.iterator();it.hasNext();) {
            wcb = it.next();                       
            if (wcb.getTimeLookedAt()>mlc.getTimeLookedAt())
                mlc = wcb;
        }
        return mlc;   
        //return lastIntervalLookedAtCitiesLinkedList.element();
        
    }
    
}
