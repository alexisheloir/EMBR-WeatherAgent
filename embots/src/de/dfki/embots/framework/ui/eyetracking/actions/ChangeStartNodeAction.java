/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.embots.framework.ui.eyetracking.actions;

import de.dfki.visp.actions.Action;
import de.dfki.visp.exception.NodeNotExistException;
import de.dfki.visp.graph.Node;
import de.dfki.visp.graph.Supernode;
import de.dfki.visp.graph.impl.MachineImpl;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dapu01
 */
public class ChangeStartNodeAction implements Action{

    Supernode superNode;
    Node startNode;

    public ChangeStartNodeAction(Supernode superNode){
        this.superNode = superNode;
    }

    @Override
    public void run() {
        try {
            Node lookAt = superNode.getNode("Looked_At_Init");
            Node notLookAt = superNode.getNode("Not_Looked_At_Init");
            if((Boolean)MachineImpl._variables.get("LookedAt")){
                startNode = lookAt;
            }else{
                startNode = notLookAt;
            }
        } catch (NodeNotExistException ex) {
            Logger.getLogger(ChangeStartNodeAction.class.getName()).log(Level.SEVERE, null, ex);
        }

        superNode.setStartNode(startNode);
        System.out.println("Now we should start with "+ startNode.getName());
    }

}
