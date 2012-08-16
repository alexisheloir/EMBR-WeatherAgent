/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.experiment;

/**
 *
 * @author Max
 */
public class Item {
    
    private WeatherCondition weatherCondition;
    private int presentationStyle;
    private City city;

    public City getCity() {
        return city;
    }

    public int getPresentationStyle() {
        return presentationStyle;
    }

    public WeatherCondition getWeatherCondition() {
        return weatherCondition;
    }
    
    public Item(WeatherCondition wc, int style) {
        weatherCondition = wc;
        presentationStyle = style;
    }
    
    public void setCity(City city) {
        this.city = city;
    }
    
}
