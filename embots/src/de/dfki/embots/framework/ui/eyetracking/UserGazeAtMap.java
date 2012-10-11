/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.embots.framework.ui.eyetracking;

import de.dfki.carmina.eyeTrackerLogger.dataProcessor.LogData;
import java.io.IOException;
import de.dfki.embots.embrscript.Triple;
import de.dfki.experiment.ConfigForExperiment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author Youcef Amine Benabbas
 */
public class UserGazeAtMap implements ObservableWeatherMap {

    private static final String EXPERIMENT_PATH = ConfigForExperiment.getProperties("Experiment.Setup.Path");
    private LogDataSingleton data;
    private WeatherMap weatherMap;
    //private UserGazeAtMapController userGazeAtMapController = new UserGazeAtMapController();
    private ArrayList<WeatherMapObserver> weatherMapObservers = new ArrayList<WeatherMapObserver>();
    boolean weightedLastInterval;
    private String selectedCity;
            
    public UserGazeAtMap(String mapName) /*throws JMSException*/ {
        data = LogDataSingleton.getInstance();
        weatherMap = new WeatherMap(mapName);
        // first change
        //weatherMapName = userGazeAtMapController.getMapName();

        try {
            this.setMap(weatherMap.getWeatherMapName());
        } catch (IOException ioe) {
        }
        //second cahnge
        //this.addObserver(userGazeAtMapController.getMainWindow());
        this.weightedLastInterval = false;
    }

    public Triple computeTriple(LogDataSingleton logdata) {
        /*double y = -2.0;
        double x = -(range / 2.0 - logdata.x_eyepos_lefteye * range);
        double z = range / 2.0 - logdata.y_eyepos_lefteye * range;*/
        //System.out.println("Eyetracking data: " + logdata);
        float x = logdata.x_gazepos_lefteye;
        float y = logdata.y_gazepos_lefteye;

        float z = 0;
        return new Triple(x, y, z);
    }

    public WeatherMap getWeatherMap() {
        return weatherMap;
    }

    public void setWeatherMap(WeatherMap weatherMap) {
        this.weatherMap = weatherMap;
    }

    public void act() throws IOException/*act() throws JMSException*/ {
        float x = (float) data.x_gazepos_lefteye;
        float y = (float) data.y_gazepos_lefteye;
        //third change
        /*if (!userGazeAtMapController.getMapName().equals(this.weatherMapName)) {
        try {
        //third change
        this.setCities(userGazeAtMapController.getMapName());
        } catch (IOException ioe) {
        
        }            
        }*/

        this.updateObservers(x, y);
        weatherMap.updateLookedAtCities(x, y);
        //4 change
        //userGazeAtMapController.setSelectedCity(weatherMap.getMostLookedAtCityWeather().getCityWeatherName());
        this.selectedCity = weatherMap.getMostLookedAtCityWeather().getCityWeatherName();
    }

    public void addObserver(WeatherMapObserver obs) {

        this.weatherMapObservers.add(obs);
    }

    public void deleteObservers() {

        this.weatherMapObservers = new ArrayList<WeatherMapObserver>();
    }

    public void updateObservers(float x, float y) {

        for (WeatherMapObserver obs : this.weatherMapObservers) {

            obs.updateCoordinatesLookedAt(computeTriple(data).toString());
            obs.updateCityLookedAt(weatherMap.getCityWeather(x, y).getCityWeatherName());
            obs.updateMostLookedAtCityWeather(getWeatherMap().getMostLookedAtCityWeather().getCityWeatherName());
        }
    }

    public void setMap(String mapName) throws IOException {

        WeatherMap newWeatherMap = new WeatherMap(mapName);
        File mapFile = new File(EXPERIMENT_PATH + mapName + ".map");
        BufferedReader mapInput = new BufferedReader(new FileReader(mapFile));
        String line2;
        while ((line2 = mapInput.readLine()) != null) {
            int i1, i2 = 0;
            i1 = line2.indexOf(",", i2);
            String city = line2.substring(i2, i1);
            i2 = i1 + 2;
            i1 = line2.indexOf(",", i2);
            i2 = i1 + 2;
            float x1, x2;
            i1 = line2.indexOf(",", i2);
            x1 = Float.parseFloat(line2.substring(i2, i1));
            i2 = i1 + 2;
            x2 = Float.parseFloat(line2.substring(i2));
            //this.getWeatherMap().addCityWeather(new CityWeather(city, 0, CityWeather.WeatherCondition.CLOUDY, x1, x2));
            newWeatherMap.addGazeObject(new CityWeather(city, 0, CityWeather.WeatherCondition.CLOUDY, x1, x2));

            //currentMap.addCity(new City(city, x1, x2));
        }

        //create GazeObject for Agent
        newWeatherMap.addGazeObject(new GazeObject("Agent", 0,0));
                
        this.setWeatherMap(newWeatherMap);
    }

    public String getWeatherMapName() {
        return this.getWeatherMap().getWeatherMapName();
    }

    /*public void setWeatherMapName(String weatherMapName) {
    this.weatherMapName = weatherMapName;
    }*/
    public String getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(String selectedCity) {
        this.selectedCity = selectedCity;
    }
    
    public String getLogLine(){

       float xCurrentPos= data.x_eyepos_lefteye;
        float yCurrentPos= data.y_eyepos_lefteye;
        String logLine = data.timestamp+","+weatherMap.getGazeObject(xCurrentPos, yCurrentPos) +","+this.getWeatherMapName()+", co-ordinates ["+xCurrentPos+" , "+yCurrentPos+"]";
        return logLine;
    }
}
