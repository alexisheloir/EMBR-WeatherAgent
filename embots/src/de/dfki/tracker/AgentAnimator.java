/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.tracker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;

/**
 *
 * @author Mahendiran
 */
public class AgentAnimator extends Thread {

    private AgentReactor agentReactor;
    private BlockingQueue<Location> locationQueue;
    private int presentationStyle;
    private boolean consumed=true;


    public AgentAnimator(ETracker eTracker,BlockingQueue<Location> locationQueue,int presentationStyle) {

    agentReactor=new AgentReactor(eTracker);
    this.locationQueue=locationQueue;
    this.presentationStyle=presentationStyle;
    }


   
   @Override
   public void run() {

    while(true)
    {     
               try {
                Location location = locationQueue.poll(365l, TimeUnit.DAYS);
                setConsumed(false);
                agentReactor.execute(location, presentationStyle);
                Thread.sleep(4000);
                setConsumed(true);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                 catch (JMSException ex) {
                    Logger.getLogger(AgentAnimator.class.getName()).log(Level.SEVERE, null, ex);
                }
               catch (Exception e)
               {
                   e.printStackTrace();
               }
          
    }

   }

   synchronized
   public boolean isConsumed() {
        return consumed;
    }

   synchronized 
    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }
   
}
