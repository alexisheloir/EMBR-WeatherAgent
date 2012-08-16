/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.embots.framework.ui.eyetracking;

/**
 *
 * @author dapu01
 */
public class StrategyChangeEvent {

    private final String name;

    public StrategyChangeEvent(String name){
        this.name = name;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof StrategyChangeEvent){
            return name.equals(((StrategyChangeEvent)o).getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    public String getName(){
        return name;
    }
}
