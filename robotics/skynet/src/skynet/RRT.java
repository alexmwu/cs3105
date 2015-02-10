package skynet;

import java.awt.Color;
import java.util.ArrayList;

import renderables.*;
import dataStructures.RRNode;
import dataStructures.RRTree;
import easyGui.EasyGui;
import geometry.IntPoint;

import java.util.Random;


public class RRT {

	private final EasyGui gui;

	private final int statusLabelId;
	
	private Robot explorer;
	
	private Goal goal;
	
	private Random randGen;
	private int xPixels,yPixels;
	
	private ArrayList<Obstacle> obstacles;
	
	private boolean started,atGoal;

	public RRT(int x,int y){
		
		// Create a new EasyGui instance with a 500x500pixel graphics panel.
		xPixels=x;
		yPixels=y;
		gui = new EasyGui(xPixels, yPixels);
		
		// Initialize the robot with ids and values for starting coordinates, radius, and step
		explorer = new Robot(gui,gui.addTextField(1, 0, "0"),gui.addTextField(1, 1, "0"),
				gui.addTextField(1,2,"10"),gui.addTextField(1,3,"10"));
		// Add labels above the gui text fields for robot x,y starting coords and size and step
		gui.addLabel(0,0,"Starting X");
		gui.addLabel(0,1,"Starting Y");
		gui.addLabel(0,2,"Robot Size");
		gui.addLabel(0,3,"Step Size");
		
		// Add text field for goal x coord in row 3 and column 0. The returned ID is
		// stored in goalFieldIdX to allow access to the field later on.
		goalFieldIdX = gui.addTextField(3, 0, "0");
		// Add label above it
		gui.addLabel(2,0,"Goal X");
		
		// Add text field for goal y coord in row 3 and column 1. The returned ID is
		// stored in goalFieldIdY to allow access to the field later on.
		goalFieldIdY = gui.addTextField(3, 1, "0");
		// Add label above it
		gui.addLabel(2, 1, "Goal Y");
		
		// Goal size text field at 3,2
		goalSizeId = gui.addTextField(3,2,"40");
		// Add label above it
		gui.addLabel(2, 2, "Goal Size");
		

		
		// Add a button in row 0 column 1. The button is labeled "Start" and
		// when pressed it will call the method called start in "this"
		// instance of the RRT class.
		gui.addButton(4, 0, "Start", this, "start");
		
		// So doesn't throw an error with move or goal button used before initialization
		started = false;
		
		// New Random generator
		randGen = new Random();
		

		
		// Status label
		statusLabelId = gui.addLabel(5,4,"Enter in coordinates and click start to begin.");
	}

	
	public void show(){
		// Displays GUI
		gui.show();
	}
	
	public void start(){
		if(!started){
			// Add a move and goal button to the right of start
			gui.addButton(4,1,"Move",this,"move");
			gui.addButton(4, 2, "Goal", this, "toGoal");
			started = true;
		}
		
		atGoal=false;
		
		// If already at goal
		if(explorer.didCollide(goal)){
			atGoal = true;
			gui.setLabelText(statusLabelId, "You are already at the goal.");
		}
		
		rob.start(gui,goal);
		
		// Initialize obstacles
		if(!atGoal)	obstacles = Obstacle.initObstacles();
		
		// User instructions
		if(!atGoal) gui.setLabelText(statusLabelId,"Please press Move for one step and Goal for solution");
		
		// Refresh the GUI
		gui.update();
		goalX=Integer.parseInt(gui.getTextFieldContent(goalFieldIdX));
		goalY=Integer.parseInt(gui.getTextFieldContent(goalFieldIdY));
		goalSize=Integer.parseInt(gui.getTextFieldContent(goalSizeId));
	}
	


	
	/*public double dist(int distX, int distY){
		return Math.sqrt(distX*distX+distY*distY);
	}*/
	
	public IntPoint step(int randX, int randY, int nearX, int nearY){
		// Point to move to given step
		int distX = randX-nearX;
		int distY = randY-nearY;
		double distance = dist(distX,distY);
		
		// Step / distance
		double stepRatio = stepSize/distance;
		
		// Point to move to in x and y direction
		int moveX = nearX + (int)Math.floor(distX*stepRatio);
		int moveY = nearY + (int)Math.floor(distY*stepRatio);
		return new IntPoint(moveX,moveY);
	}
	
	// MAIN
	public static void main(String[] args)
	{

		RRT test = new RRT(500,500);
		test.show();
	}
	
}
