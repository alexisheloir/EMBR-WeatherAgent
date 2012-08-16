/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.embots.framework.ui.eyetracking;

/**
 *
 * @author Youcef
 */
public class CityWeather extends GazeObject {

    public static enum WeatherCondition {

        SUNNY,
        CLOUDY,};
    
    private final double LOOKED_AT_UNIT = (double) 1;
    private float temperature;
    private WeatherCondition weatherCondition;
    private double timeLookedAt;

    public CityWeather(String cityWeatherName, float temperature, WeatherCondition weatherCondition, float x_pos, float y_pos) {
        super(cityWeatherName, x_pos, y_pos);
        this.temperature = temperature;
        this.weatherCondition = weatherCondition;
        this.timeLookedAt = (double) 0;
    }

    public void setTimeLookedAt(double timeLookedAt) {
        this.timeLookedAt = timeLookedAt;
    }

    public double getTimeLookedAt() {
        return this.timeLookedAt;
    }

    public void cityWeatherLookedAtByUser() {
        timeLookedAt = timeLookedAt + LOOKED_AT_UNIT;
    }

    public void setCityWeatherName(String cityWeatherName) {
        this.gazeObjectName = cityWeatherName;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void setWeatherCondition(WeatherCondition weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public void setX_pos(float x_pos) {
        this.x_pos = x_pos;
    }

    public void setY_pos(float y_pos) {
        this.y_pos = y_pos;
    }

    public String getCityWeatherName() {
        return gazeObjectName;
    }

    public float getTemperature() {
        return temperature;
    }

    public WeatherCondition getWeatherCondition() {
        return weatherCondition;
    }

    public float getX_pos() {
        return x_pos;
    }

    public float getY_pos() {
        return y_pos;
    }
}
