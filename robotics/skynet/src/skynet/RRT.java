package skynet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import renderables.*;
import dataStructures.RRNode;
import dataStructures.RRTree;
import easyGui.EasyGui;
import geometry.IntPoint;



public class RRT {

	private final EasyGui gui;

	private final int statusLabelId;
	
	private Robot explorer;
	
	private Goal goal;
	
	private Random randGen;
	private int xPixels,yPixels;
	
	private ArrayList<Obstacle> obstacles;
	
	// whether gui has initially started (produces two more buttons), whether current robot is at 
	//the goal, and whether the user wants to display the random red dots, respectively
	private boolean started,atGoal,displayRandomDots;

	public RRT(int x,int y){
		
		// Create a new EasyGui instance with a 500x500pixel graphics panel.
		xPixels=x;
		yPixels=y;
		gui = new EasyGui(xPixels, yPixels);
		
		// Initialize the robot with ids and values for starting coordinates, radius, and step
		explorer = new Robot(gui.addTextField(1, 0, "0"),gui.addTextField(1, 1, "0"),
				gui.addTextField(1,2,"10"),gui.addTextField(1,3,"10"));
		// Add labels above the gui text fields for robot x,y starting coords and size and step
		gui.addLabel(0,0,"Starting X");
		gui.addLabel(0,1,"Starting Y");
		gui.addLabel(0,2,"Robot Size");
		gui.addLabel(0,3,"Step Size");
		
		// Initialize goal with ids for starting coordinates and radius
		goal = new Goal(gui.addTextField(3, 0, "0"),gui.addTextField(3, 1, "0"),gui.addTextField(3,2,"40"));

		// Add labels above gui text fields for goal x,y, and radius
		gui.addLabel(2,0,"Goal X");
		gui.addLabel(2,1,"Goal Y");
		gui.addLabel(2,2,"Goal Size");
		
		// Add a button in row 0 column 1. The button is labeled "Start" and
		// when pressed it will call the method called start in "this"
		// instance of the RRT class.
		gui.addButton(4, 0, "Start", this, "start");
		
		// So doesn't throw an error with move or goal button used before initialization
		started = false;
		
		// do not display random dots to start with
		displayRandomDots=false;
		
		// New Random generator
		randGen = new Random();
		
		// Status label
		statusLabelId = gui.addLabel(5,4,"Enter in coordinates and click start to begin.");
	}

	//generate random obstacles on a gui field
	public ArrayList<Obstacle> initRandObstacles(){
		int x,y,r;
		Obstacle tmp;
		
		obstacles = new ArrayList<Obstacle>();
		
		// Number of obstacles (from 0 to 10)
		int num = randGen.nextInt(11);
		
		
		for(int i=0;i<num;i++){
			x=randGen.nextInt(xPixels+1);
			y=randGen.nextInt(yPixels+1);
			r=randGen.nextInt(goal.getRadius()+1);
			tmp = new Obstacle(x,y,r);
			
			/* not working for some reason
			//if obstacles intersect with other obstacles
			if(i>0){
				for(int j=0;j<i;j++){
					if(tmp.didCollide(obstacles.get(j))){
						i--;
						continue;
					}
				}
			}*/
			
			// If obstacles intersects with the start or goal
			if(tmp.didCollide(explorer) || tmp.didCollide(goal)){
				i--;
				continue;
			}
			else{
				obstacles.add(tmp);
				gui.draw(tmp.getRenderable());
			}
		}
		return obstacles;
	}

	
	public void show(){
		// Displays GUI
		gui.show();
	}
	
	public void randomDots(){
		if(displayRandomDots) displayRandomDots=false;
		else displayRandomDots=true;
	}
	
	public void start(){
		atGoal=false;
		if(!started){
			// Add a move and goal button to the right of start
			gui.addButton(4,1,"Move",this,"move");
			gui.addButton(4, 2, "Goal", this, "toGoal");
			gui.addButton(4, 3, "Toggle Dots", this, "randomDots");
			started = true;
		}	
		
		// Start simulation robot and goal with user values
		explorer.start(gui,goal);
		goal.start(gui);
		
		// If already at goal
		if(explorer.didCollide(goal)){
			atGoal = true;
			gui.setLabelText(statusLabelId, "You are already at the goal.");
		}

		
		// Initialize obstacles
		if(!atGoal)	obstacles = initRandObstacles();
		
		// User instructions
		if(!atGoal) gui.setLabelText(statusLabelId,"Please press Move for one step and Goal for solution");
		
		// Refresh the GUI
		gui.update();
	}
	
	public void move(){
		if(atGoal) return;
		else{
			int randomX=0,randomY=0;
			
			// if it returns IntPoint, break out of loop; else continue (it won't return if it
			// hits obstacles
			
			while(true){
				randomX = randGen.nextInt(xPixels+1);
				randomY = randGen.nextInt(yPixels+1);
				if(explorer.move(gui,obstacles,randomX,randomY)) break;
			}
			
			// Draws a red dot at random point
			if(displayRandomDots){
				RenderablePoint randPoint = new RenderablePoint(randomX,randomY);
				randPoint.setProperties(Color.RED, 7.5f);
				gui.draw(randPoint);
			}

			gui.draw(explorer.getRenderable());	
			
			// Update GUI
			gui.update();
			
			if(explorer.didCollide(goal)){
				atGoal = true;
				gui.setLabelText(statusLabelId, "You've reached the goal.");
				end();
			}
			
		}
		
	}

	public void end(){
		explorer.end(gui);
		
		// Update GUI
		gui.update();
	}
	
	public void toGoal(){
		while(!atGoal) move();
	}
	
	// MAIN
	public static void main(String[] args)
	{

		RRT rrt = new RRT(500,500);
		rrt.show();
	}
	
}
