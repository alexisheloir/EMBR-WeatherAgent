/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.embots.framework.ui.eyetracking;

/**
 *
 * @author Max
 */
public class GazeObject {
    
    protected final float CITY_RANGE = (float) 0.10;
    protected String gazeObjectName;
    protected float x_pos;
    protected float y_pos;

    public GazeObject(String gazeObjectName, float x_pos, float y_pos) {
        this.gazeObjectName = gazeObjectName;
        this.x_pos = x_pos;
        this.y_pos = y_pos;
    }

    public String getGazeObjectName() {
        return gazeObjectName;
    }
    
    public boolean isInRange(float x, float y) {
        if ((this.getX_pos() < (x + CITY_RANGE)) && (this.getX_pos() > (x - CITY_RANGE)) && (this.getY_pos() < (y + CITY_RANGE)) && (this.getY_pos() > (y - CITY_RANGE))) {
            return true;
        } else {
            return false;
        }
    }

    public float getX_pos() {
        return x_pos;
    }

    public float getY_pos() {
        return y_pos;
    }
    
    public String toString() {
        return gazeObjectName;
    }
    
}
