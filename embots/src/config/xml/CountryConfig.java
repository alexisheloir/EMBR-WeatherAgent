/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package config.xml;

import java.util.ArrayList;

/**
 *
 * @author Mahendiran
 */
public class CountryConfig {

    private ArrayList<CityConfig> cities;
    private String countryName;
    private String presentationStyle;




    public CountryConfig(String countryName,String presentationStyle)
    {
        cities=new ArrayList<CityConfig>();
        this.countryName=countryName;
        this.presentationStyle=presentationStyle;
    }

    public int getNoOfCities()
    {
        return cities.size();
    }

    public void addCity(CityConfig cityConfig)
    {
        cities.add(cityConfig);
    }

     public ArrayList<CityConfig> getCities() {
        return cities;
    }

      public String getCountryName() {
        return countryName;
    }

    public String getPresentationStyle() {
        return presentationStyle;
    }
}
