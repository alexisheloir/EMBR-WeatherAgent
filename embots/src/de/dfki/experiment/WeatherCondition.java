/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.experiment;

/**
 *
 * @author Max
 */
public class WeatherCondition {
    
    enum Symbol {
        SUNNY,CLOUDY,RAINY,STORMY
    }
    
    private int temperature;
    private Symbol symbol;
    
    public WeatherCondition(int temp, Symbol symb) {
        temperature = temp;
        symbol = symb;
    }
    
    public int getTemperature() {
        return temperature;
    }

    public Symbol getSymbol() {
        return symbol;
    }
    
    public String getNumberString() {
        int value = symbol.ordinal() * 14 + temperature - 16;
        if (value >= 10) return Integer.toString(value);
        return "0" + Integer.toString(value);
    }

    public String getClimate()  {
       return symbol.toString();
    }

}

    

