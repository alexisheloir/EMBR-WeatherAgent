/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.experiment;

import eu.semaine.jms.sender.Sender;
import javax.jms.JMSException;

/**
 *
 * @author Max
 */
public class MapChanger {
    
    private Sender embrSender;
    private Experiment exp;
    private int agent = 2;
    private int map;
    int weathervalue = 60;

    private int [] hot  = new int [] {61,62,63,64};
    private int [] cold = new int [] {71,72,73,74};
    private int [] warm = new int [] {81,82,83,84};

    public MapChanger(Experiment exp, Sender embrSender) {
        this.exp = exp;
        this.embrSender = embrSender;
    }
    
    public void loadMap(String country) throws JMSException {
        map = country.equals("Spain") ? 1 : country.equals("France") ? 2 : 3;
        embrSender.sendTextMessage("COMMAND_MESSAGE:\"" + agent + map + "0000\"", exp.getTime());
        System.out.println("COMMAND_MESSAGE:\"" + agent + map + "0000\"");
    }
    
    public void showCityWeather(Item item) throws JMSException {
   //     embrSender.sendTextMessage("COMMAND_MESSAGE:\"" + agent + map + item.getCity().getOrdinalString() + item.getWeatherCondition().getNumberString() + "\"", exp.getTime());
   //     System.out.println("COMMAND_MESSAGE:\"" + agent + map + item.getCity().getOrdinalString() + item.getWeatherCondition().getNumberString() + "\"");

        if(item.getWeatherCondition().getTemperature()==1)
        { // hot
            if(item.getWeatherCondition().getClimate()=="SUNNY")
            {
                embrSender.sendTextMessage("COMMAND_MESSAGE:\"" + agent + map + item.getCity().getOrdinalString() + hot[0] + "\"", exp.getTime());
                System.out.println("COMMAND_MESSAGE:\"" + agent + map + item.getCity().getOrdinalString() + hot [0] + "\"");
            }

            else if(item.getWeatherCondition().getClimate() == "CLOUDY")
            {
                 embrSender.sendTextMessage("COMMAND_MESSAGE:\"" + agent + map + item.getCity().getOrdinalString() + hot[1] + "\"", exp.getTime());
                System.out.println("COMMAND_MESSAGE:\"" + agent + map + item.getCity().getOrdinalString() + hot[1] + "\"");
            }
        }
        else if(item.getWeatherCondition().getTemperature()==2)
        { // cold
           if(item.getWeatherCondition().getClimate()=="SUNNY")
            {
                embrSender.sendTextMessage("COMMAND_MESSAGE:\"" + agent + map + item.getCity().getOrdinalString() + cold[0] + "\"", exp.getTime());
                System.out.println("COMMAND_MESSAGE:\"" + agent + map + item.getCity().getOrdinalString() + cold[0] + "\"");
            }

            else if(item.getWeatherCondition().getClimate() == "CLOUDY")
            {
                embrSender.sendTextMessage("COMMAND_MESSAGE:\"" + agent + map + item.getCity().getOrdinalString() + cold[1] + "\"", exp.getTime());
                System.out.println("COMMAND_MESSAGE:\"" + agent + map + item.getCity().getOrdinalString() + cold[1] + "\"");
            }
        }
        else if(item.getWeatherCondition().getTemperature()==3)
        { //warm
            if(item.getWeatherCondition().getClimate()=="SUNNY")
            {
                  embrSender.sendTextMessage("COMMAND_MESSAGE:\"" + agent + map + item.getCity().getOrdinalString() + warm[0] + "\"", exp.getTime());
                System.out.println("COMMAND_MESSAGE:\"" + agent + map + item.getCity().getOrdinalString() + warm[0] + "\"");
            }

            else if(item.getWeatherCondition().getClimate() == "CLOUDY")
            {
                embrSender.sendTextMessage("COMMAND_MESSAGE:\"" + agent + map + item.getCity().getOrdinalString() + warm[1] + "\"", exp.getTime());
                System.out.println("COMMAND_MESSAGE:\"" + agent + map + item.getCity().getOrdinalString() + warm[1] + "\"");
            }
        }

    }
    
    public void clearMap() throws JMSException {
        embrSender.sendTextMessage("COMMAND_MESSAGE:\"" + agent + map + "0000\"", exp.getTime());
        System.out.println("COMMAND_MESSAGE:\"" + agent + map + "0000\"");
    }
    
    public void hideMap() throws JMSException {
        map = 4;
        embrSender.sendTextMessage("COMMAND_MESSAGE:\"" + agent + "40000\"", exp.getTime());
        System.out.println("COMMAND_MESSAGE:\"" + agent + "40000\"");
    }
    
    public void setAgent(int agent) {
        this.agent = agent;
    }
            
}
