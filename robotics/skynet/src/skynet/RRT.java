package skynet;

import java.awt.Color;
import java.util.ArrayList;
import renderables.RenderablePoint;
import dataStructures.RRNode;
import dataStructures.RRTree;
import easyGui.EasyGui;
import geometry.IntPoint;
import java.util.Random;

public class RRT {

	private final EasyGui gui;
	private RRTree tree;
	
	private final int startFieldIdX;
	private final int startFieldIdY;
	private final int goalFieldIdX;
	private final int goalFieldIdY;
	private final int goalSizeId;
	
	private int startX,startY,goalX,goalY,goalSize;
	
	private final int statusLabelId;
	
	private boolean atGoal;
	
	private Random randGen;
	private int pixelX,pixelY,stepSize;
	
	public RRT(int x,int y,int step){
		// Create a new EasyGui instance with a 500x500pixel graphics panel.
		pixelX=x;
		pixelY=y;
		stepSize=step;
		gui = new EasyGui(pixelX, pixelY);
		
		// New Random generator
		randGen = new Random();
		
		// Add text field for start x coord in row 1 and column 0. The returned ID is
		// stored in startFieldIdX to allow access to the field later on.
		startFieldIdX = gui.addTextField(1, 0, "0");
		// Add label above it
		gui.addLabel(0, 0, "Starting X");
		
		// Add text field for start y coord in row 1 and column 1. The returned ID is
		// stored in startFieldIdY to allow access to the field later on.
		startFieldIdY = gui.addTextField(1, 1, "0");
		// Add label above it
		gui.addLabel(0,1,"Starting Y");
		
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
		
		// Add a move button to the right of start
		gui.addButton(4,1,"Move",this,"move");
		
		// Status label
		statusLabelId = gui.addLabel(5,3,"Enter in coordinates and click start to begin.");
	}
	
	public void show(){
		// Displays GUI
		gui.show();
	}
	
	public void start(){
		atGoal=false;
		
		startX=Integer.parseInt(gui.getTextFieldContent(startFieldIdX));
		startY=Integer.parseInt(gui.getTextFieldContent(startFieldIdY));
		goalX=Integer.parseInt(gui.getTextFieldContent(goalFieldIdX));
		goalY=Integer.parseInt(gui.getTextFieldContent(goalFieldIdY));
		goalSize=Integer.parseInt(gui.getTextFieldContent(goalSizeId));
		
		if(dist(goalX-startX, goalY-startY)<=stepSize){
			atGoal = true;
			gui.setLabelText(statusLabelId, "You are already at the goal.");
		}

		gui.clearGraphicsPanel();
		
		// Create an RRTree to be used later.
		tree = new RRTree(Color.BLACK);
		
		// Tell the GUI to draw this tree. Because the tree has only just been created
		// there is nothing to draw yet but there will be later on.
		gui.draw(tree);
		
		// Refresh the GUI
		gui.update();
		
		// Set the start and goal positions based on input.
		// The goal radius is 40. A path that ends in the circle of this radius around
		// the goal is considered to have attained the goal position.
		tree.setStartAndGoal(new IntPoint(startX, startY), new IntPoint(goalX, goalY), goalSize);
		
		// Tell the GUI to draw this tree. Because the tree has only just been created
		// there is nothing to draw yet but there will be later on.
		gui.draw(tree);
		
		// User instructions
		if(!atGoal) gui.setLabelText(statusLabelId,"Please press move to move the robot");
		
		// Refresh the GUI
		gui.update();
	}
	
	public void move(){
		if(atGoal) return;
		else{
			// Random  point
			int randomX = randGen.nextInt(pixelX+1);
			int randomY = randGen.nextInt(pixelY+1);
			
			// Returns the nearest node to the random point
			RRNode nearest = tree.getNearestNeighbour(new IntPoint(randomX, randomY));
			
			// Get point to move to
			IntPoint moveTo = step(randomX,randomY,nearest.x,nearest.y);
			
			tree.addNode(nearest, moveTo);
			
			// Draws a red dot at random point
			RenderablePoint randPoint = new RenderablePoint(randomX,randomY);
			randPoint.setProperties(Color.RED, 7.5f);
			gui.draw(randPoint);
			
			
			// Update GUI
			gui.update();
			System.out.println(dist(goalX-moveTo.x, goalY-moveTo.y));
			if(dist(goalX-moveTo.x, goalY-moveTo.y)<=goalSize) atGoal = true;
			System.out.println(atGoal);
		}
		
	}
	
	public double dist(int distX, int distY){
		return Math.sqrt(distX*distX+distY*distY);
	}
	
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
		RRT test = new RRT(500,500,10);
		test.show();
	}

}