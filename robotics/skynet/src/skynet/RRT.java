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
	
	private RRTRobot explorer;
	
	private Goal goal;
	
	private Random randGen;
	private int xPixels,yPixels,bufferFactor;	//bufferFactor to pick random ints in buffer around window
	
	private ArrayList<Obstacle> obstacles;
	
	// whether gui has initially started (produces two more buttons), whether current robot is at 
	//the goal, and whether the user wants to display the random red dots, respectively
	private boolean started,atGoal,displayRandomDots;

	public RRT(int x,int y,int buffer){
		
		// Create a new EasyGui instance with a 500x500pixel graphics panel.
		xPixels=x;
		yPixels=y;
		bufferFactor=buffer;
		gui = new EasyGui(xPixels, yPixels);
		
		// Initialize the robot with ids and values for starting coordinates, radius, and step
		explorer = new RRTRobot(gui.addTextField(1, 0, "0"),gui.addTextField(1, 1, "0"),
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
		ArrayList<Obstacle> randObst = new ArrayList<Obstacle>();
		boolean collided = false;
		
		// Number of obstacles (from 0 to 10)
		int num = randGen.nextInt(11);
		
		
		for(int i=0;i<num;i++){
			x=randGen.nextInt(xPixels+1);
			y=randGen.nextInt(yPixels+1);
			r=randGen.nextInt(goal.getRadius()+1);
			
			//if obstacles intersect with other obstacles
			for(Obstacle o : randObst){
				if(o.didCollide(x,y,r)) collided=true;

			}
			
			// If obstacles intersects with the start or goal
			if(explorer.didCollide(x,y,r) || goal.didCollide(x,y,r) || collided){
				i--;
				continue;
			}
			else{
				tmp = new Obstacle(x,y,r);
				randObst.add(tmp);
				gui.draw(tmp.getRenderable());
			}
		}
		return randObst;
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
		if(!started){
			// Add a move and goal button to the right of start
			gui.addButton(4,1,"Move",this,"move");
			gui.addButton(4, 2, "Goal", this, "toGoal");
			gui.addButton(4, 3, "Toggle Dots", this, "randomDots");
			started = true;
		}	
		
		// Start simulation robot and goal with user values
		goal.start(gui);
		explorer.start(gui,goal);

		
		// If already at goal
		if(explorer.didCollide(goal)){
			atGoal = true;
			gui.setLabelText(statusLabelId, "You are already at the goal.");
		}

		
		// Initialize obstacles
		if(!atGoal){
			obstacles = initRandObstacles();
		}
		
		// User instructions
		if(!atGoal) gui.setLabelText(statusLabelId,"Please press Move for one step and Goal for solution");

		atGoal=false;
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
				randomX = randGen.nextInt((int)xPixels+2*(xPixels/bufferFactor))-(xPixels/bufferFactor);
				randomY = randGen.nextInt((int)yPixels+2*(yPixels/bufferFactor))-(yPixels/bufferFactor);
				//if(randomX>550 || randomX<-50||randomY>550||randomY<-550)System.out.println(randomX+", "+randomY);
				if(explorer.move(gui,obstacles,randomX,randomY)) break;
			}
			
			// Draws a red dot at random point
			if(displayRandomDots){
				RenderablePoint randPoint = new RenderablePoint(randomX,randomY);
				randPoint.setProperties(Color.RED, 7.5f);
				gui.draw(randPoint);
			}

			gui.draw(explorer.getRenderable());	
			
			if(explorer.didCollide(goal)){
				atGoal = true;
				gui.setLabelText(statusLabelId, "You've reached the goal.");
				end();
			}
			
			// Update GUI
			gui.update();
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

		RRT rrt = new RRT(500,500,10);
		rrt.show();
	}
	
}
