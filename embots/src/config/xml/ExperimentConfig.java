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
public class ExperimentConfig {

    private String userId;
    private ArrayList<CountryConfig> countries;



    public ExperimentConfig(String userId)
    {
        this.userId=userId;
        countries=new ArrayList<CountryConfig>();
    }

   public void addCountry(CountryConfig countryConfig)
   {
       countries.add(countryConfig);
   }

    public ArrayList<CountryConfig> getCountries() {
        return countries;
    }

    public int getNoOfCountries()
    {
        return countries.size();
    }

    public String getUserId() {
        return userId;
    }


}
