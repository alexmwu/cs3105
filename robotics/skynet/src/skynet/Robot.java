package skynet;

import easyGui.EasyGui;

public class Robot {

	private final GUI gui;
	private int xPixels,yPixels;
	
	//should hold gui and pass to RRT and potfields classes
	Robot(int x, int y, int buffer, boolean startType){
		// Create a new EasyGui instance with a 500x500pixel graphics panel.
		xPixels=x;
		yPixels=y;
		gui = new GUI(xPixels, yPixels);
	}
	
	// MAIN
	public static void main(String[] args)
	{
		Robot r =new Robot(500,500,10);
		
	}

}
