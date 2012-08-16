package de.dfki.embots.framework.gaze.eytrackersim;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


/**
 * Represents Position of the Users eyes and the position of Ambers head in the simulator.
 * @author Daniel Puschmann
 */
public class UserEyesEMBRHead extends Canvas implements MouseListener,MouseMotionListener{
    
    /*
     * 0 represents color black (meaning Amber is not looking at user),
     * 1 represents color red (Amber is looking at user)
     */
    private int userColor = 1;

    /*
     * 0 represents color black (meaning Amber is not looked at),
     * 1 represents color red (Amber is looked at)
     */
    int EMBRColor = 1;

    private int preX;
    private int preY;
    private Rectangle rect = new Rectangle(0,0,30,30);
    private Graphics2D g2;
    private boolean pressOut = false;
    private boolean isFirstTime = true;

    




    public UserEyesEMBRHead(){
        setBackground(Color.white);
        addMouseListener(this);
        addMouseMotionListener(this);
        setVisible(true);
        validate();
    }

    @Override
    public Dimension getPreferredSize(){
        return new Dimension(1680,1050);
    }

    @Override
    public Dimension getMinimumSize(){
        return new Dimension(1000,500);
    }

    @Override
    public void paint(Graphics g){
        update(g);
    }

    @Override
    public void update(Graphics g){
        g2 = (Graphics2D) g;
        Dimension dim = getSize();
        int h = (int) dim.getHeight();
        int w = (int) dim.getWidth();
        g2.setStroke(new BasicStroke(8.0f));

        if (isFirstTime) {
            rect.setLocation(w / 2 - 50, h / 2 - 25);
            isFirstTime = false;
        }

        // Clears the rectangle that was previously drawn.
        g2.setPaint(Color.white);
        g2.fillRect(0, 0, w, h);

        switch(userColor){
            case 0:
                g2.setColor(Color.black);
                break;
            case 1:
                g2.setColor(Color.red.darker());
                break;
        }
        g2.fill(rect);

        switch(EMBRColor){
            case 0:
                g2.setColor(Color.BLACK);
                break;
            case 1:
                g2.setColor(Color.RED.darker());
                break;
        }
        g.fillOval(getWidth()/2 - 45,getHeight()/2 - 220, 50, 50);

    }

    public void changeUserColor(boolean lookedAt){
        if(lookedAt){
            userColor = 1;
        }else{
            userColor = 0;
        }
        this.repaint();
    }

    public void changeEMBRColor(){
        EMBRColor = (EMBRColor+1)%2;
        this.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        preX = preX-e.getX();
        preY = preY-e.getY();

        if(rect.contains(e.getX(),e.getY())){
            updateLocation(e);
        }else{
            pressOut = true;
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(rect.contains(e.getX(),e.getY())){
            updateLocation(e);
        }else{
            pressOut = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(!pressOut){
            updateLocation(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    public void updateLocation(MouseEvent e){
        rect.setLocation(e.getX(),e.getY());
        this.repaint();
    }

    public Rectangle getRectangle(){
        return rect;
    }


}
