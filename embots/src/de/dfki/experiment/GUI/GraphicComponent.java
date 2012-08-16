package GUI;

import java.awt.Graphics;

import javax.swing.JPanel;

/*
 * graphics component of the game.
 * draws the fields of the gamefield
 */

public class GraphicComponent extends JPanel {
	
	public GraphicComponent () {

	}
	
	@Override
	public void paintComponent (Graphics g) {
		// call constructor of JPanel
		super.paintComponent(g) ;
		paintBoard (g) ;
	}
	
	public void paintBoard (Graphics g) {
		
	}
	
}
