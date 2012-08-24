/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.experiment;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;
import javax.jms.JMSException;
import de.dfki.experiment.GazeZone;

/**
 * This class is responsible for giving the weather presentation for a certain city
 * @author Max
 */
public class WeatherPresentation {

    private static final String PROP_LEXEME_DIR = "lexeme.dir";
    private Experiment exp;
    private Properties _userconfig = new Properties();

    /*
     * Create a new WeatherPresentation
     * @param exp: The calling experiment object
     */
    public WeatherPresentation(Experiment exp) {
        this.exp = exp;
    }

    /*
     * Do the presentation for a certain item
     * @param item: The item to use for the presentation
     */
    public void execute(Item item) throws JMSException {
        //show city weather on map
        exp.getMapChanger().showCityWeather(item);

        //create BML for presentation
        String textString1 = "In ";
//        String textString2 = item.getCity().toSpeech() + " the weather is " + item.getWeatherCondition().getSymbol().toString() + " with a temperature of " + item.getWeatherCondition().getTemperature();
        String textString2 ="";
        if(item.getWeatherCondition().getTemperature()==1)
        { //hot
         textString2 = item.getCity().toSpeech() + " the weather is    " + item.getWeatherCondition().getSymbol().toString() + "   and temperature iss haawt";
        }
        else if (item.getWeatherCondition().getTemperature()==2)
        { //cold
         textString2 = item.getCity().toSpeech() + " the weather is   " + item.getWeatherCondition().getSymbol().toString() + "    and temperature iss Cold";
        }
        else if (item.getWeatherCondition().getTemperature()==3)
        { //warm
          textString2 = item.getCity().toSpeech() + " the weather is   " + item.getWeatherCondition().getSymbol().toString() + "   and temperature iss warm ";
        }

    String bmldoc = "";
        String lexeme;
        bmldoc = bmldoc.concat("<bml id=\"b1\">");
        bmldoc = bmldoc.concat("<speech id=\"s\">");
        bmldoc = bmldoc.concat("<text>" + textString1 + "<sync id=\"0\" />" + textString2 + "<sync id=\"1\" /></text>");
        bmldoc = bmldoc.concat("</speech>");
        bmldoc = bmldoc.concat("<face id=\"f0\" type=\"neutral\" stroke_start=\"s:0\" />");
        //create and use lexeme for non-speech-only strategies

        float xPos= 0;
        float yPos= 0;

        if (item.getPresentationStyle() != 1) {


            xPos = (float)item.getCity().getX_pos();
            yPos = (float)item.getCity().getY_pos();
            float xPosOrg=xPos;
            float yPosOrg=yPos;
            //calculate target values out of city coordinates
           // float xvalue = (float) (-(1 - item.getCity().getX_pos() * 2.0) - 1.5);
            //float yvalue = (float) (1 - item.getCity().getY_pos() * 2.0);

            float xvalue = (float) (-(1 - xPos * 2.0) - 1.5);

            float yvalue = 0;

            if(yPos>(float)(0.49))
            {
               yPos = (float) (yPos + 0.4);

            }

            yvalue = (float) (1 - yPos * 2.0);

            float xfinger = (float) (xvalue - 0.4);
            float yfinger = yvalue > 0 ? (float) (yvalue + 0.2) : (float) (yvalue - 0.2);
            float xstep1 = xvalue;
            float ystep1 = yvalue;

            if (item.getPresentationStyle() == 3) {
                ZoneCentre zoneCenter=null;
                try
                {

                zoneCenter=GazeZone.getZone(xPosOrg, yPosOrg);

                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                float xZoneCenter=zoneCenter.getX(),yZoneCenter=zoneCenter.getY(),zZoneCenter=zoneCenter.getZ();
                float xZoneHandCenter=zoneCenter.getxHand(),yZoneHandCenter=zoneCenter.getyHand(),zZoneHandCenter=zoneCenter.getzHand();
                if(yPos>(float)(0.499))
                {
                
                    yfinger=zZoneHandCenter;
                    ystep1=zZoneHandCenter;
                }


                //lexeme for gaze and gesture
                //lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.22;-0.16;0.22\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:-0.84;0;0.2\n    JOINT:rhand\n    NORMAL:Zaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\n END\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1500\n  HOLD:0\nBEGIN POSE_TARGET\n    BODY_GROUP:rhand\n    POSE_KEY:hands_index\n  END\n  BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xstep1 + ";-0.06;" + ystep1 + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:" + xfinger + ";0;" + yfinger + "\n    JOINT:rhand\n    NORMAL:Yaxis\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:-0.02;1;-0.1\n    JOINT:rhand\n    NORMAL:Zaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:3.5999999999999943\n  END\nEND\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:1750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xvalue + ";-0.16;" + yvalue + "\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xvalue + ";-0.06;" + yvalue + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n END\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:2000\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\nEND\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2050\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\nEND\nEND";
//                lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:-0.84;0;0.2\n    JOINT:rhand\n    NORMAL:Zaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:0.0\n  END\n END\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1500\n  HOLD:0\nBEGIN POSE_TARGET\n    BODY_GROUP:rhand\n    POSE_KEY:hands_index\n  END\n  BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xstep1 + ";-0.06;" + ystep1 + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:" + xfinger + ";0;" + yfinger + "\n    JOINT:rhand\n    NORMAL:Yaxis\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:-0.02;1;-0.1\n    JOINT:rhand\n    NORMAL:Zaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:0.0\n  END\nEND\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:1750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter

  //                      +"\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END"
  //                      + "\n BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xvalue + ";0;" + yvalue + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n END\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:2250\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.08;-0.32;-0.20\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:0.0\n  END\nEND\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2500\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:0.0\n  END\nEND\nEND";

      lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n "
              + "BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n END\n "
              + "BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n  END\n "
              + "BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1300\n  HOLD:0\nBEGIN POSE_TARGET\n    BODY_GROUP:rhand\n    POSE_KEY:hands_index\n  END \n  HOLD:10\n  BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + -(xZoneCenter-xstep1) + ";"+yZoneCenter+";" + ystep1 + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  HOLD:30\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:" + -(xZoneCenter-xfinger) + ";"+yZoneCenter+";" + yfinger + "\n    JOINT:rhand\n    NORMAL:Yaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:69.0\n  END\n HOLD:50\n  END\n "
              + "BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:1400\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n HOLD:50\n" + " BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xvalue + ";"+(yZoneCenter)+";" + yvalue + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n END\n "
              + "BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:1800\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.08;-0.32;-0.20\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\nEND\n"
              + "BEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2000\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN POSE_TARGET\n    BODY_GROUP:rhand\n    POSE_KEY:hands_open-relaxed\n  END \n BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\nEND"
              + "\nEND";


                System.out.println("\n xvalue "+xvalue+"yvalue "+yvalue);
            } else {
                //lexeme for gaze
//                lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:-0.96;0;0.04\n  END\n END\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1500\n  HOLD:0\n END\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:1750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xvalue + ";-0.16;" + yvalue + "\n  END\n END\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:2000\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n END\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2020\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.16;-1.76;0.8\n  END\n END\nEND";
                //   previous working
                //lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.28;-0.36;-0.24\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:-0.96;0;0.04\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.28;-0.36;-0.24\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nEND\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1500\n  HOLD:0\n END\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:1750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xvalue + ";-0.16;" + yvalue + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.14;-0.36;-0.24\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nEND\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:2000\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.08;-0.32;-0.20\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2020\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.16;-1.76;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.04;-0.28;-0.16\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\nEND";


                ZoneCentre zoneCenter=null;
                try
                {

                zoneCenter=GazeZone.getZone(xPosOrg, yPosOrg);

                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                float xZoneCenter=zoneCenter.getX(),yZoneCenter=zoneCenter.getY(),zZoneCenter=zoneCenter.getZ();
                //--- lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.28;-0.36;-0.24\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:-0.96;0;0.04\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.28;-0.36;-0.24\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nEND\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1500\n  HOLD:0\n END\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:1750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.14;-0.36;-0.24\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nEND\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:2000\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.08;-0.32;-0.20\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2020\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.16;-1.76;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.04;-0.28;-0.16\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\nEND";
                 lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:500\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:900\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.28;-0.36;-0.24\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1450\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nEND\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1850\n  HOLD:0\n END\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:2280\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nEND\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:2900\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.08;-0.32;-0.20\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:3150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.16;-1.76;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.04;-0.28;-0.16\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\nEND";
            //lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.28;-0.36;-0.24\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nEND\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1800\n  HOLD:0\n END\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:2200\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nEND\n BEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2800\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.16;-1.76;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.04;-0.28;-0.16\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\nEND";
            }

            //determine lexicon directory
            String defDir = _userconfig.getProperty(PROP_LEXEME_DIR, System.getProperty("user.dir").replace("bin", "data\\lexicon"));
            String defFile = "personal_RH.embr";
            File myFile = new File(defDir + "\\" + defFile);
            try {
                FileWriter fw = new FileWriter(myFile);
                fw.append(lexeme);
                fw.close();
            } catch (Exception e) { System.out.println("File could not be written. Exception: " + e); }
            //reload lexicon so the new lexeme can be used
            exp.reloadLexicon();
            bmldoc = bmldoc.concat("<gesture id=\"g1\" type=\"lexicalized\" lexeme=\"personal\" stroke_start=\"s:0\" stroke_end=\"s:1\"/>");
        }
        bmldoc = bmldoc.concat("</bml>");
        //send to BML realizer
        exp.sendBML(bmldoc);
    }

    /*
     * This method is called after the presentation was given
     */
    public void end() throws JMSException {
        //take weather information away
        exp.getMapChanger().clearMap();
    }

}
