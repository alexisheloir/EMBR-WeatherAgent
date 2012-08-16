/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.embots.framework.ui.eyetracking;

/**
 *
 * @author dapu01
 */
public class UserStartsLookingAwayEvent {


    @Override
    public boolean equals(Object o){
        return (o instanceof UserStartsLookingAwayEvent);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }
}
