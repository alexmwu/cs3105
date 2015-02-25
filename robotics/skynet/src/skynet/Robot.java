package skynet;

import easyGui.EasyGui;

public class Robot {

	private final EasyGui gui;
	private int xPixels,yPixels;
	
	//should hold gui and pass to RRT and potfields classes
	Robot(){
		// Create a new EasyGui instance with a 500x500pixel graphics panel.
		xPixels=x;
		yPixels=y;
		gui = new EasyGui(xPixels, yPixels);
	}
	
	// MAIN
	public static void main(String[] args)
	{
		RRT rrt = new RRT(500,500,10);
		rrt.show();
	}

}
