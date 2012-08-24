/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.tracker;

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;
import javax.jms.JMSException;

/**
 * 
 * @author Mahendiran
 */
public class AgentReactor {

    private static final String PROP_LEXEME_DIR = "lexeme.dir";
  
    private Properties _userconfig = new Properties();
    private ETracker etracker;

    public AgentReactor(ETracker etracker)
    {
        this.etracker=etracker;
    }
  
    
    public void execute(Location location,int presentationStyle) throws JMSException {
      
        //create BML for presentation
        String textString1 = "i";
//        String textString2 = item.getCity().toSpeech() + " the weather is " + item.getWeatherCondition().getSymbol().toString() + " with a temperature of " + item.getWeatherCondition().getTemperature();
        String textString2 ="";
    

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

        if (presentationStyle != 1) {


            xPos = (float)location.getX_pos();
            yPos = (float)location.getY_pos();
            float xPosOrg=xPos;
            float yPosOrg=yPos;
      

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

            if (presentationStyle == 3) {
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
                if(xPosOrg>0.8)
                {
                    yfinger=zZoneHandCenter;
                    ystep1=zZoneHandCenter;
                    yvalue=zZoneHandCenter;
                }


                //lexeme for gaze and gesture
                //lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.22;-0.16;0.22\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:-0.84;0;0.2\n    JOINT:rhand\n    NORMAL:Zaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\n END\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1500\n  HOLD:0\nBEGIN POSE_TARGET\n    BODY_GROUP:rhand\n    POSE_KEY:hands_index\n  END\n  BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xstep1 + ";-0.06;" + ystep1 + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:" + xfinger + ";0;" + yfinger + "\n    JOINT:rhand\n    NORMAL:Yaxis\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:-0.02;1;-0.1\n    JOINT:rhand\n    NORMAL:Zaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:3.5999999999999943\n  END\nEND\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:1750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xvalue + ";-0.16;" + yvalue + "\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xvalue + ";-0.06;" + yvalue + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n END\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:2000\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\nEND\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2050\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\nEND\nEND";
//                lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:-0.84;0;0.2\n    JOINT:rhand\n    NORMAL:Zaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:0.0\n  END\n END\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1500\n  HOLD:0\nBEGIN POSE_TARGET\n    BODY_GROUP:rhand\n    POSE_KEY:hands_index\n  END\n  BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xstep1 + ";-0.06;" + ystep1 + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:" + xfinger + ";0;" + yfinger + "\n    JOINT:rhand\n    NORMAL:Yaxis\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:-0.02;1;-0.1\n    JOINT:rhand\n    NORMAL:Zaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:0.0\n  END\nEND\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:1750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter

  //                      +"\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END"
  //                      + "\n BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xvalue + ";0;" + yvalue + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n END\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:2250\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.08;-0.32;-0.20\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:0.0\n  END\nEND\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2500\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:0.0\n  END\nEND\nEND";


                //latest --blw
                /*
               lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:500\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:1400\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1900\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n  END\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:2500\n  HOLD:0\nBEGIN POSE_TARGET\n    BODY_GROUP:rhand\n    POSE_KEY:hands_index\n  END \n  HOLD:10\n  BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + -(xZoneCenter-xstep1) + ";"+yZoneCenter+";" + ystep1 + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  HOLD:30\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:" + -(xZoneCenter-xfinger) + ";"+yZoneCenter+";" + yfinger + "\n    JOINT:rhand\n    NORMAL:Yaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:69.0\n  END\n HOLD:50\n  END\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:3400\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter

                        +"\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n HOLD:50\n"
                        + " BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xvalue + ";"+(yZoneCenter)+";" + yvalue + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n END\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:3900\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.08;-0.32;-0.20\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\nEND\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:4300\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN POSE_TARGET\nBODY_GROUP:lhand\nPOSE_KEY:hands_open-relaxed\n END\nBEGIN POSITION_CONSTRAINT\nBODY_GROUP:larm\nTARGET:0.18;-0.04;0\nJOINT:lhand\nOFFSET:0;0;0\nEND\nBEGIN SWIVEL_CONSTRAINT\nBODY_GROUP:larm\nSWIVEL_ANGLE:75.0\nEND\nBEGIN POSE_TARGET\nBODY_GROUP:rhand\nPOSE_KEY:hands_open-relaxed\nEND\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n   TARGET:-0.18;-0.04;0\n JOINT:rhand\n OFFSET:0;0;0\n END\n BEGIN SWIVEL_CONSTRAINT\n BODY_GROUP:rarm\n SWIVEL_ANGLE:75.0\n  END\n END\nEND";
                  */
                //

               /*
                 lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:100\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n  END\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1300\n  HOLD:50\nBEGIN POSE_TARGET\n    BODY_GROUP:rhand\n    POSE_KEY:hands_index\n  END \n  HOLD:10\n  BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + -(xZoneCenter-xstep1) + ";"+yZoneCenter+";" + ystep1 + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  HOLD:30\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:" + -(xZoneCenter-xfinger) + ";"+yZoneCenter+";" + yfinger + "\n    JOINT:rhand\n    NORMAL:Yaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:69.0\n  END\n HOLD:50\n  END\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:1600\n  HOLD:30\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter

                        +"\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n HOLD:150\n"
                        + " BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xvalue + ";"+(yZoneCenter)+";" + yvalue + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n END\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:2000\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.08;-0.32;-0.20\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\nEND\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2300\n  HOLD:100\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN POSE_TARGET\n    BODY_GROUP:rhand\n    POSE_KEY:hands_open-relaxed\n  END \n BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\nEND\nEND";
*/

                 lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:100\n "
              + "BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n END\n "
              + "BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:200\n  HOLD:300\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n  END\n "
              + "BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:800\n  HOLD:300\nBEGIN POSE_TARGET\n    BODY_GROUP:rhand\n    POSE_KEY:hands_index\n  END \n  HOLD:200\n  BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + -(xZoneCenter-xstep1) + ";"+yZoneCenter+";" + ystep1 + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  HOLD:300\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:" + -(xZoneCenter-xfinger) + ";"+yZoneCenter+";" + yfinger + "\n    JOINT:rhand\n    NORMAL:Yaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:69.0\n  END\n HOLD:250\n  END\n "
              + "BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:2200\n  HOLD:150\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n HOLD:250\n" + " BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xvalue + ";"+(yZoneCenter)+";" + yvalue + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n END\n "
              //+ "BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:1800\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.08;-0.32;-0.20\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\nEND\n"
              //+ "BEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2000\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN POSE_TARGET\n    BODY_GROUP:rhand\n    POSE_KEY:hands_open-relaxed\n  END \n BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\nEND"
              + "\nEND";


                ///latest tried --blw
                /* lexeme = "\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:100\n   BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:300\n   HOLD:100\n  BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + -(xZoneCenter-xstep1) + ";"+yZoneCenter+";" + ystep1 + "\n    JOINT:rhand\n      END\n  HOLD:800\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:" + -(xZoneCenter-xfinger) + ";"+yZoneCenter+";" + yfinger + "\n    JOINT:rhand\n    NORMAL:Yaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:69.0\n  END\n HOLD:850\n  END\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:2300\n  HOLD:50\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter

                        +"\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n HOLD:550\n"
                        + " END\n END";

                        */
                ///
                System.out.println("\n xvalue "+xvalue+"yvalue "+yvalue +" xPosOrg "+xPosOrg+" yPosOrg "+yPosOrg);
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
                 lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:500\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:900\n  HOLD:10\n  BEGIN POSE_TARGET\nBODY_GROUP:lhand\nPOSE_KEY:hands_open-relaxed\n END\nBEGIN POSITION_CONSTRAINT\nBODY_GROUP:larm\nTARGET:0.18;-0.04;0\nJOINT:lhand\nOFFSET:0;0;0\nEND\nBEGIN SWIVEL_CONSTRAINT\nBODY_GROUP:larm\nSWIVEL_ANGLE:75.0\nEND\nBEGIN POSE_TARGET\nBODY_GROUP:rhand\nPOSE_KEY:hands_open-relaxed\nEND\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n   TARGET:-0.18;-0.04;0\n JOINT:rhand\n OFFSET:0;0;0\n END\n BEGIN SWIVEL_CONSTRAINT\n BODY_GROUP:rarm\n SWIVEL_ANGLE:75.0\n  END\n BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.28;-0.36;-0.24\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1400\n  HOLD:10\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nEND\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1950\n  HOLD:30\n END\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:2600\n  HOLD:10\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nEND\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:3200\n  HOLD:10\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.08;-0.32;-0.20\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:3500\n  HOLD:10\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.16;-1.76;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.04;-0.28;-0.16\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\nEND";
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
            etracker.reloadLexicon();
            bmldoc = bmldoc.concat("<gesture id=\"g1\" type=\"lexicalized\" lexeme=\"personal\" stroke_start=\"s:0\" stroke_end=\"s:1\"/>");
        }
        bmldoc = bmldoc.concat("</bml>");
        //send to BML realizer
        etracker.sendBML(bmldoc);
    }

  

}
