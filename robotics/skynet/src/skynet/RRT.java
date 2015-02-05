package skynet;

import java.awt.Color;

import dataStructures.RRTree;
import easyGui.EasyGui;
import geometry.IntPoint;

public class RRT {

	private final EasyGui gui;
	private RRTree tree;
	
	private final int startFieldIdX;
	private final int startFieldIdY;
	private final int goalFieldIdX;
	private final int goalFieldIdY;
	
	public RRT(){
		// Create a new EasyGui instance with a 500x500pixel graphics panel.
		gui = new EasyGui(500, 500);
		
		// Add text field for start x coord in row 0 and column 0. The returned ID is
		// stored in startFieldIdX to allow access to the field later on.
		startFieldIdX = gui.addTextField(0, 0, "0");
		
		// Add text field for start y coord in row 0 and column 1. The returned ID is
		// stored in startFieldIdY to allow access to the field later on.
		startFieldIdY = gui.addTextField(0, 1, "0");
		
		// Add text field for goal x coord in row 1 and column 0. The returned ID is
		// stored in goalFieldIdX to allow access to the field later on.
		goalFieldIdX = gui.addTextField(1, 0, "0");
		
		// Add text field for goal y coord in row 1 and column 1. The returned ID is
		// stored in goalFieldIdY to allow access to the field later on.
		goalFieldIdY = gui.addTextField(1, 1, "0");
		
		// Add a button in row 0 column 1. The button is labeled "Start" and
		// when pressed it will call the method called start in "this"
		// instance of the RRT class.
		gui.addButton(2, 0, "Start", this, "start");
	}
	
	public void show(){
		// Displays GUI
		gui.show();
	}
	
	public void start(){
		int startX=Integer.parseInt(gui.getTextFieldContent(startFieldIdX));
		int startY=Integer.parseInt(gui.getTextFieldContent(startFieldIdY));
		int goalX=Integer.parseInt(gui.getTextFieldContent(goalFieldIdX));
		int goalY=Integer.parseInt(gui.getTextFieldContent(goalFieldIdY));
		
		System.out.println(startX+" "+startY+" "+goalX+" "+goalY);

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
		tree.setStartAndGoal(new IntPoint(startX, startY), new IntPoint(goalX, goalY), 40);
		
		// Tell the GUI to draw this tree. Because the tree has only just been created
		// there is nothing to draw yet but there will be later on.
		gui.draw(tree);
		
		// Refresh the GUI
		gui.update();
	}
	
	// MAIN
	public static void main(String[] args)
	{
		RRT test = new RRT();
		test.show();
	}

}
