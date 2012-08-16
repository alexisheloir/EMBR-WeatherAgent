/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dfki.experiment;

/**
 *
 * @author Youcef
 */
public class City {
    private String cityName;
    private float x_pos;
    private float y_pos;
    private int ordinal;
    private String pronounciation;
    
    public City(String name, String pronounciation, float x_pos, float y_pos, int ordinal) {
        this.cityName = name;
        this.x_pos = x_pos;
        this.y_pos = y_pos;
        this.ordinal = ordinal;
        this.pronounciation = pronounciation;
    }
    
    public void setX_pos(float x_pos) {
        this.x_pos = x_pos;
    }

    public void setY_pos(float y_pos) {
        this.y_pos = y_pos;
    }

    public String toString() {
        return cityName;
    }
    
    public String toSpeech() {
        return pronounciation;
    }

    public float getX_pos() {
        return x_pos;
    }

    public float getY_pos() {
        return y_pos;
    }
    
    public String getOrdinalString() {
        if (ordinal >= 10) return Integer.toString(ordinal);
        return "0" + Integer.toString(ordinal);
    }
}
