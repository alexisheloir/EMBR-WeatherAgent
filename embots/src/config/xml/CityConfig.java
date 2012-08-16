package config.xml;

/**
 *
 * @author Mahendiran
 */
public class CityConfig {

    private String weatherCondition;
    private String climate;


    public CityConfig(String weatherCondition,String climate)
    {
        this.weatherCondition=weatherCondition;
        this.climate=climate;
        
    }

    public String getClimate() {
        return climate;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }



}
