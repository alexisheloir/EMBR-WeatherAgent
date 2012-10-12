/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.experiment;

/**
 *
 * @author Mahendiran
 */
public class GazeZone {

    //range between -2 to 2
    private static float[][] xZoneCentres={{-1.0f,-.8f,-.76f,-.24f,-.24f}
                                            ,{-1.0f,-.8f,-.76f,-.24f,-.24f}
                                            ,{-1.0f,-.8f,-.76f,-.24f,-.24f}
                                            ,{-1.0f,-.8f,-.76f,-.24f,-.24f}};

    private static float[][] yZoneCentres={{-1.8f,-1.08f,-1.2f,-.32f,-.32f}
                                            ,{-1.8f,-1.08f,-1.2f,-.32f,-.32f}
                                            ,{-1.8f,-1.08f,-1.2f,-.32f,-.32f}
                                            ,{-1.8f,-1.08f,-1.2f,-.32f,-.32f}};

    private static float[][] zZoneCentres={{.40f,.30f,.20f,.04f,.02f}
                                            ,{0.24f,-.28f,-.4f,-.44f,-.44f}
                                            ,{-.28f,-.12f,-.36f,-.64f,-.64f}
                                            ,{-.4f,-1.16f,-1.68f,-.132f,-.132f}};


      private static float[][] xZoneHandCentres={{-1.0f,-.8f,-.76f,-.24f,-.24f}
                                            ,{-1.0f,-.8f,-.76f,-.24f,-.24f}
                                            ,{-1.0f,-.8f,-.76f,-.24f,-.24f}
                                            ,{-1.0f,-.8f,-.76f,-.24f,-.24f}};

    private static float[][] yZoneHandCentres={{-1.8f,-1.08f,-1.2f,-.32f,-.32f}
                                            ,{-1.8f,-1.08f,-1.2f,-.32f,-.32f}
                                            ,{-1.8f,-1.08f,-1.2f,-.32f,-.32f}
                                            ,{-1.8f,-1.08f,-1.2f,-.32f,-.32f}};

    private static float[][] zZoneHandCentres={{.40f,.30f,.20f,.04f,.02f}
                                            ,{0.24f,-.28f,-.4f,-.44f,-.44f}
                                            ,{-.15f,-.17f,-.30f,-.36f,-.36f}
                                            ,{-.17f,-.3f,-1.16f,-1.68f,-1.68f}};

    public static ZoneCentre getZone(float xPos,float yPos)
    {

        if(xPos<0 || xPos>1 || yPos<0 ||yPos >1)
        {
            throw new IllegalArgumentException("Invalid parameters(s) - xPos:" +xPos+" yPos:"+yPos);
        }

        int totalXZoneCount=5;
        int totalYZoneCount=4;
        float zoneXUnitDist=1f/(float)totalXZoneCount;
        float zoneYUnitDist=1f/(float)totalYZoneCount;

        int xZoneID=(int)(xPos/zoneXUnitDist);
        int yZoneID=(int)(yPos/zoneYUnitDist);

        if(xZoneID==totalXZoneCount)
        {
            xZoneID-=1;
        }

        if(yZoneID==totalYZoneCount)
        {
            yZoneID-=1;
        }

        System.out.println("### xPos: "+xPos+"  yPos:"+yPos);
         System.out.println("###~~ zoneXUnitDist: "+zoneXUnitDist+"  zoneYUnitDist:"+zoneYUnitDist);
         System.out.println("###~~ xZoneID: "+xZoneID+"  yZoneID:"+yZoneID);
        ZoneCentre zoneCentre=new ZoneCentre(xZoneCentres[yZoneID][xZoneID],yZoneCentres[yZoneID][xZoneID],zZoneCentres[yZoneID][xZoneID],xZoneHandCentres[yZoneID][xZoneID],yZoneHandCentres[yZoneID][xZoneID],zZoneHandCentres[yZoneID][xZoneID]);
        return zoneCentre;
    }

   

}
 class ZoneCentre
    {
        float x;
        float y;
        float z;
        float xHand;
        float yHand;
        float zHand;


    public ZoneCentre(float x, float y, float z,float xHand,float yHand,float zHand) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xHand=xHand;
        this.yHand=yHand;
        this.zHand=zHand;
    }


        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getZ() {
            return z;
        }

         public float getxHand() {
        return xHand;
    }

    public float getyHand() {
        return yHand;
    }

    public float getzHand() {
        return zHand;
    }

    }