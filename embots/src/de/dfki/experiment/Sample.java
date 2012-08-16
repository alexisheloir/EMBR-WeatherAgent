/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.experiment;

/**
 *
 * @author Administrator
 */
public class Sample {

   public static void main(String args[])
    {
       String lexeme;
       float xPos=0.85f;
       float yPos=0.1f;
        float xvalue = (float) (-(1 - xPos * 2.0) - 1.5);

            float yvalue = 0;

         /*( if (yPos > (float) (0.49)) {
            yPos = (float) (yPos + 0.4);

        }*/

            yvalue = (float) (1 - yPos * 2.0);

            float xfinger = (float) (xvalue - 0.4);
            float yfinger = yvalue > 0 ? (float) (yvalue + 0.2) : (float) (yvalue - 0.2);
            float xstep1 = xvalue;
            float ystep1 = yvalue;
            if (true) {
                //lexeme for gaze and gesture
             //   lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.22;-0.16;0.22\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:-0.84;0;0.2\n    JOINT:rhand\n    NORMAL:Zaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\n END\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1500\n  HOLD:0\nBEGIN POSE_TARGET\n    BODY_GROUP:rhand\n    POSE_KEY:hands_index\n  END\n  BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xstep1 + ";-0.06;" + ystep1 + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:" + xfinger + ";0;" + yfinger + "\n    JOINT:rhand\n    NORMAL:Yaxis\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:-0.02;1;-0.1\n    JOINT:rhand\n    NORMAL:Zaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:3.5999999999999943\n  END\nEND\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:1750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xvalue + ";-0.16;" + yvalue + "\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xvalue + ";-0.06;" + yvalue + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n END\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:2000\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\nEND\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2050\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:72.0\n  END\nEND\nEND";
           lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + 0 + ";"+0+";" + 0 + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + 0 + ";"+0+";" + 0 + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:-0.84;0;0.2\n    JOINT:rhand\n    NORMAL:Zaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:0.0\n  END\n END\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1500\n  HOLD:0\nBEGIN POSE_TARGET\n    BODY_GROUP:rhand\n    POSE_KEY:hands_index\n  END\n  BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xstep1 + ";-0.06;" + ystep1 + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:" + xfinger + ";0;" + yfinger + "\n    JOINT:rhand\n    NORMAL:Yaxis\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n    BODY_GROUP:rarm\n    DIRECTION:-0.02;1;-0.1\n    JOINT:rhand\n    NORMAL:Zaxis\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:0.0\n  END\nEND\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:1750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + 0 + ";"+0+";" + 0

                        +"\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + 0 + ";"+0+";" + 0 + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END"
                        + "\n BEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:" + xvalue + ";0;" + yvalue + "\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n END\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:2250\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.08;-0.32;-0.20\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:0.0\n  END\nEND\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2500\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\nBEGIN POSITION_CONSTRAINT\n    BODY_GROUP:rarm\n    TARGET:-0.18;-0.04;0\n    JOINT:rhand\n    OFFSET:0;0;0\n  END\n  BEGIN SWIVEL_CONSTRAINT\n    BODY_GROUP:rarm\n    SWIVEL_ANGLE:0.0\n  END\nEND\nEND";
         System.out.println("Gaze + Gesture \n\n");
                System.out.println(lexeme);

            } else {
                //lexeme for gaze
//                lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:-0.96;0;0.04\n  END\n END\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1500\n  HOLD:0\n END\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:1750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xvalue + ";-0.16;" + yvalue + "\n  END\n END\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:2000\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n END\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2020\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.16;-1.76;0.8\n  END\n END\nEND";
               //   lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.28;-0.36;-0.24\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:-0.96;0;0.04\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.28;-0.36;-0.24\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nEND\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1500\n  HOLD:0\n END\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:1750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xvalue + ";-0.16;" + yvalue + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.14;-0.36;-0.24\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nEND\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:2000\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.08;-0.32;-0.20\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2020\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.16;-1.76;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.04;-0.28;-0.16\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\nEND";


                ZoneCentre zoneCenter=null;
                try
                {

                zoneCenter=GazeZone.getZone(xPos, yPos);

                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                float xZoneCenter=zoneCenter.getX(),yZoneCenter=zoneCenter.getY(),zZoneCenter=zoneCenter.getZ();
                lexeme = "TIME_RESET\n\nBEGIN K_POSE_SEQUENCE  # --- LEXEME:personal\n CHARACTER:Amber\n START:300\n BEGIN K_POSE  # --- Pose 0 --- SYNC:start\n  TIME_POINT:750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.28;-0.36;-0.24\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\n BEGIN K_POSE  # --- Pose 1 --- SYNC:ready\n  TIME_POINT:1150\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nEND\n BEGIN K_POSE  # --- Pose 2\n  TIME_POINT:1500\n  HOLD:0\n END\n BEGIN K_POSE  # --- Pose 3\n  TIME_POINT:2000\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n  END\n BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:" + xZoneCenter + ";"+yZoneCenter+";" + zZoneCenter + "\n JOINT:spine4\n    NORMAL:Zaxis\n  END\nEND\n BEGIN K_POSE  # --- Pose 4 --- SYNC:stroke_end\n  TIME_POINT:2500\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.24;-0.96;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.08;-0.32;-0.20\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\nBEGIN K_POSE  # --- Pose 5 --- SYNC:end\n  TIME_POINT:2750\n  HOLD:0\n  BEGIN LOOK_AT_CONSTRAINT\n    BODY_GROUP:headAbdomen\n    TARGET:0.16;-1.76;0.8\n  END\n  BEGIN ORIENTATION_CONSTRAINT\n  BODY_GROUP:spine\n    DIRECTION:-0.04;-0.28;-0.16\n JOINT:spine4\n    NORMAL:Zaxis\n  END\n END\nEND";

                System.out.println("Only Gaze \n\n");
                System.out.println(lexeme);

            }
   }

}
