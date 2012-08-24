/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.tracker;

import de.dfki.embots.embrscript.Triple;

/**
 *
 * @author Mahendiran
 */
public class GazeZone {

     private static int totalXZoneCount=5;
     private static int totalYZoneCount=4;
     public static float zoneXUnitDist=1f/(float)totalXZoneCount;
     public static float zoneYUnitDist=1f/(float)totalYZoneCount;

    private static float[][] xZoneCentres={{-1.0f,-.8f,-.76f,-.24f,0.05f}
                                            ,{-1.0f,-.8f,-.76f,-.24f,0.05f}
                                            ,{-1.0f,-.8f,-.76f,-.24f,0.05f}
                                            ,{-1.0f,-.8f,-.76f,-.24f,0.05f}};

    private static float[][] yZoneCentres={{-1.8f,-1.08f,-1.2f,-.32f,-.12f}
                                            ,{-1.8f,-1.08f,-1.2f,-.32f,-.12f}
                                            ,{-1.8f,-1.08f,-1.2f,-.32f,-.12f}
                                            ,{-1.8f,-1.08f,-1.2f,-.32f,-.12f}};

    private static float[][] zZoneCentres={{.40f,.30f,.20f,.04f,.02f}
                                            ,{0.24f,-.28f,-.4f,-.44f,-.34f}
                                            ,{-.28f,-.12f,-.36f,-.64f,-.54f}
                                            ,{-.4f,-1.16f,-1.68f,-.132f,-.112f}};


      private static float[][] xZoneHandCentres={{-1.0f,-.8f,-.76f,-.24f,-.12f}
                                            ,{-1.0f,-.8f,-.76f,-.24f,-.12f}
                                            ,{-1.0f,-.8f,-.76f,-.24f,-.12f}
                                            ,{-1.0f,-.8f,-.76f,-.24f,-.12f}};

    private static float[][] yZoneHandCentres={{-1.8f,-1.08f,-1.2f,-.32f,-.22f}
                                            ,{-1.8f,-1.08f,-1.2f,-.32f,-.22f}
                                            ,{-1.8f,-1.08f,-1.2f,-.32f,-.22f}
                                            ,{-1.8f,-1.08f,-1.2f,-.32f,-.22f}};

    private static float[][] zZoneHandCentres={{.40f,.30f,.20f,.04f,-1.68f}
                                            ,{0.24f,-.28f,-.4f,-.44f,-1.68f}
                                            ,{-.15f,-.17f,-.30f,-.36f,-1.68f}
                                            ,{-.17f,-.3f,-1.16f,-1.68f,-1.68f}};

    public static ZoneCentre getZone(float xPos,float yPos)
    {

        if(xPos<0 || xPos>1 || yPos<0 ||yPos >1)
        {
            throw new IllegalArgumentException("Invalid parameters(s) - xPos:" +xPos+" yPos:"+yPos);
        }

    
        int xZoneID=getXZoneID(xPos);
        int yZoneID=getYZoneID(yPos);

       
       // System.out.println("### xPos: "+xPos+"  yPos:"+yPos);
        System.out.println("###~~ xZoneID: "+xZoneID+"  yZoneID:"+yZoneID);
        ZoneCentre zoneCentre=new ZoneCentre(xZoneCentres[yZoneID][xZoneID],yZoneCentres[yZoneID][xZoneID],zZoneCentres[yZoneID][xZoneID],xZoneHandCentres[yZoneID][xZoneID],yZoneHandCentres[yZoneID][xZoneID],zZoneHandCentres[yZoneID][xZoneID]);
        return zoneCentre;
    }

  

   public static int getXZoneID(float xPos)
   {
        if(xPos<0 || xPos>1 )
        {
            throw new IllegalArgumentException("Invalid parameters(s) - xPos:" +xPos);
        }
       
      
        int xZoneID=(int)(xPos/zoneXUnitDist);
        if(xZoneID==totalXZoneCount)
        {
            xZoneID-=1;
        }

         //System.out.println("### xPos: "+xPos);
         //System.out.println("###~~ zoneXUnitDist: "+zoneXUnitDist);
         return xZoneID;
   }

     public static int getYZoneID(float yPos)
   {
          if(yPos<0 ||yPos >1)
        {
            throw new IllegalArgumentException("Invalid parameters(s) - yPos:"+yPos);
        }
       
        
        int yZoneID=(int)(yPos/zoneYUnitDist);
         if(yZoneID==totalYZoneCount)
        {
            yZoneID-=1;
        }

        // System.out.println("### yPos:"+yPos);
        // System.out.println("###~~ zoneYUnitDist:"+zoneYUnitDist);
         return yZoneID;
   }

    public static int getZoneIndex(float xPos,float yPos)
    {
        return getZoneIndex(getXZoneID(xPos),getYZoneID(yPos));
    }
    public static int getZoneIndex(int xZoneID,int yZoneID )
    {

         if(xZoneID<0 || yZoneID<0 || xZoneID>=totalXZoneCount || yZoneID>=totalYZoneCount  )
        {
            throw new IllegalArgumentException("Invalid parameters(s) - xZoneID:" +xZoneID+" yZoneID:"+yZoneID);
        }
        int zoneIndex=xZoneID;
        if(yZoneID>0)
        {
            zoneIndex+=((yZoneID)*totalXZoneCount);
        }
        return zoneIndex;
    }

     public static int getXZoneID(int zoneIndex )
    {

         if(zoneIndex<0 || zoneIndex>=(totalXZoneCount*totalYZoneCount)  )
        {
            throw new IllegalArgumentException("Invalid parameters(s) - zoneIndex:" +zoneIndex);
        }
        return zoneIndex%totalXZoneCount;
    }

      public static int getYZoneID(int zoneIndex )
    {

         if(zoneIndex<0 || zoneIndex>=(totalXZoneCount*totalYZoneCount)  )
        {
            throw new IllegalArgumentException("Invalid parameters(s) - zoneIndex:" +zoneIndex);
        }
        return (int)(zoneIndex/totalXZoneCount);
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