/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.tracker;

/**
 *
 * @author Mahendiran
 */
public class Location {
   
    private float x_pos;
    private float y_pos;


    public Location(float x_pos,float y_pos)
    {
        this.x_pos=x_pos;
        this.y_pos=y_pos;
    }

     public float getX_pos() {
        return x_pos;
    }

    public float getY_pos() {
        return y_pos;
    }

}
