package GUI;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

/*
 * Creates a new Window with all its components (Panels, Buttons, etc...) and
 * includes some functions to handle with them.
 */

public class Window {
	
	// dimension of the window
	private final int winWidth, winHeight ;
	
	// the frame of the window
	private JFrame frame ;
	
	// the pointer to the menu of the window
	private JMenuBar menu ;
	
	// the content panel
	private Container contentPane ;
	
	
	/*
	 * Parameters:
	 * 	AppTitle = the name of the window
	 *	winWidth = width of the window
	 *	winHeight = height of the window
	 * 
	 * Returns:
	 * 	-Constructor-
	 */
	public Window (String appTitle, int winWidth, int winHeight) {
		// fill fields
		this.winWidth = winWidth ;
		this.winHeight = winHeight ;
		// create the frame of the window using "Swing"
		this.frame = new JFrame (appTitle) ;
			// setting its dimension
			this.frame.setPreferredSize(new Dimension (winWidth, winHeight)) ;
			// setting the 'x' to close
			this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE) ;
			// moving the window into the middle of the screen
			this.frame.setLocation( ((int)getMiddleOfDesktop ().getX() - (winWidth / 2)) , ((int)getMiddleOfDesktop ().getY() - (winHeight / 2)) ) ;
			// setting the menu to null
			this.menu = null ;
			// getting the content pane
			this.contentPane = frame.getContentPane() ;			
			// packing the frame and making it visible
			this.frame.pack () ;
			this.frame.setVisible (true) ;			
	}
	
	/*
	 * returns where the window has to be moved to to be
	 * centralized according to winWidth and winHeight
	 */
	
	private Point getMiddleOfDesktop () {
		// get screen-size
		Toolkit toolkit = Toolkit.getDefaultToolkit() ;
			Dimension dimension = toolkit.getScreenSize() ;
		// casting dimension to a point and return the middle of the desktop
			return (new Point (dimension.width / 2, dimension.height / 2)) ;
	}
	
	/*
	 * pure getters and setters
	 */
	
	public int getWinWidth () {
		return this.winWidth ;
	}
	
	public int getWinHeight () {
		return this.winHeight ;
	}
	
	public Container getCP () {
		return this.contentPane ;
	}
	
	public void setMenu (JMenuBar menuBar) {
		this.menu = menuBar ;
		this.frame.setJMenuBar(menuBar) ;
	}

}
