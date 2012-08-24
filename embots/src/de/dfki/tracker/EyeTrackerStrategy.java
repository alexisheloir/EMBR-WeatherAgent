/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.tracker;

import de.dfki.embots.embrscript.Triple;
import de.dfki.embots.framework.ui.eyetracking.LogDataSingleton;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

/**
 *
 * @author Mahendiran
 */
public class EyeTrackerStrategy {
    private EPositionUpdatorThread ePositionUpdatorThread;

    public EyeTrackerStrategy() {
    ePositionUpdatorThread=new EPositionUpdatorThread();
    ePositionUpdatorThread.start();
    }
  
    synchronized
    public Location getMostViewedLocation()
    {
        TIntIntMap zoneIndexFrequencyMap=ePositionUpdatorThread.getXoneIndexFrequencyMap();
        int zoneIndexWithMaxFreq;
         TIntIntIterator iter=zoneIndexFrequencyMap.iterator();
         int maxFrequency=-1;
         int relatedZoneIndex=-1;
         while(iter.hasNext())
         {
             iter.advance();
            int zoneIndex= iter.key();
             int freq=iter.value();

             if(freq>maxFrequency)
             {
                 maxFrequency=freq;
                 relatedZoneIndex=zoneIndex;
             }
         }
         if(maxFrequency>0)
         {
             zoneIndexWithMaxFreq=relatedZoneIndex;
         }
            else
         {
             zoneIndexWithMaxFreq=4 ;//default head zone
         }


         Location maxFreqLocation=new Location(GazeZone.getXZoneID(zoneIndexWithMaxFreq)*GazeZone.zoneXUnitDist+GazeZone.zoneXUnitDist/2, GazeZone.getYZoneID(zoneIndexWithMaxFreq)*GazeZone.zoneYUnitDist+GazeZone.zoneYUnitDist/2);
         System.out.println(" zoneindexwith maxfreq: "+zoneIndexWithMaxFreq+" xzoneid:"+GazeZone.getXZoneID(zoneIndexWithMaxFreq)+" yzoneid:"+GazeZone.getYZoneID(zoneIndexWithMaxFreq));
         System.out.println(" maxfreq location : "+maxFreqLocation.toString());



         return maxFreqLocation;

    }

class EPositionUpdatorThread extends Thread {



    private int bucketSize=10;
    private EyeTrackerData eyeTrackerData=new EyeTrackerData();
    private TIntLinkedList zoneIndexBufferList=new TIntLinkedList();
    private TIntIntMap zoneIndexFrequencyMap=TCollections.synchronizedMap(new TIntIntHashMap());
    private int counter1=0;
    private LogDataSingleton liveData;

     public EPositionUpdatorThread() {
     liveData=LogDataSingleton.getInstance();
     }
   @Override
   public void run() {
         
    while(true)
    {
         if(counter1%10==0)
            {
                counter1=0;
                act();
            }
             else
            {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            counter1++;
    }
         
   }

    private void act()
    {
        try
        {
        float xLivePos=liveData.x_gazepos_lefteye;
        float yLivePos=liveData.y_gazepos_lefteye;

        //System.out.println("act- xlivepos:"+xLivePos+" ylivepos:"+yLivePos);
        int currentZoneIndex= GazeZone.getZoneIndex(xLivePos,yLivePos );
        if(zoneIndexBufferList.size()==bucketSize)
        {
            int removedZoneIndex=zoneIndexBufferList.removeAt(0);
            zoneIndexFrequencyMap.adjustValue(removedZoneIndex, -1);

            zoneIndexBufferList.add(currentZoneIndex);
            zoneIndexFrequencyMap.adjustOrPutValue(currentZoneIndex,1,1);
        }
        else
        {
            zoneIndexBufferList.add(currentZoneIndex);
            zoneIndexFrequencyMap.adjustOrPutValue(currentZoneIndex,1,1);
        }



        }catch(Exception e)
        {
            e.printStackTrace();
        }
      

    }

    public TIntIntMap getXoneIndexFrequencyMap()
    {
        return zoneIndexFrequencyMap;
    }
    }

}


