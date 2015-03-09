package skynet;

import java.awt.Color;
import java.util.ArrayList;

import easyGui.EasyGui;
import dataStructures.RRNode;
import dataStructures.RRTree;
import renderables.RenderableOval;
import renderables.RenderablePolyline;
import geometry.IntPoint;

public class RRTRobot extends Object{
	private int step;	//size of robot moves
	private RenderableOval rob;	//draw robot on screen based on radius
	
	//ids for starting input
	private int xId,yId,radiusId,stepId;
	private RRTree tree;
	
	//is the robot at the goal
	private boolean atGoal;
	
	//ids for the x,y coordinates at start and for the radius and step of robot
	RRTRobot(Robot r){
		xId=r.getStartXText();
		yId=r.getStartYText();
		radiusId=r.getRobotSizeText();
		stepId=r.getStepSizeText();

		// Create an RRTree to be used later.
		tree = new RRTree(Color.BLACK);
	}
	
	private void setRenderable(){
		rob = new RenderableOval(getX(),getY(),2*getRadius(),2*getRadius());
	}
	
	public RenderableOval getRenderable(){
		return rob;
	}
	
	public IntPoint getIntPoint(){
		return new IntPoint(getX(),getY());
	}
	
	public void start(EasyGui gui,Goal goal){


		gui.clearGraphicsPanel();
		
		
		//grab gui strings for the input values and parse into the object variables (i.e., into
		//x,y,radius,and step
		setX(Integer.parseInt(gui.getTextFieldContent(xId)));
		setY(Integer.parseInt(gui.getTextFieldContent(yId)));
		setRadius(Integer.parseInt(gui.getTextFieldContent(radiusId)));
		step=Integer.parseInt(gui.getTextFieldContent(stepId));
		setRenderable();
		
		gui.draw(rob);
		
		// Set the start and goal positions based on input.
		// The goal radius is 40. A path that ends in the circle of this radius around
		// the goal is considered to have attained the goal position.
		tree.setStartAndGoal(new IntPoint(getX(), getY()), new IntPoint(goal.getX(), goal.getY()), goal.getRadius());
		
		// Tell the GUI to draw this tree (start and goal)
		gui.draw(tree);
	}
	
	//returns next point if there is no obstacle collision; null otherwise
	public boolean move(EasyGui gui, ArrayList<Obstacle> obstacles, int randomX, int randomY){
		RRNode nearest=null;
		IntPoint moveTo=null;
		
		// Returns the nearest node to the random point
		nearest = tree.getNearestNeighbour(new IntPoint(randomX, randomY));
			
		// Get point to move to
		moveTo = step(randomX,randomY,nearest.x,nearest.y);
		
			// If there are obstacles
		if(!obstacles.isEmpty()){
			for(Obstacle i : obstacles){
				/*
				System.out.println(moveTo.x+", "+moveTo.y);
				System.out.println(i.getX()+", "+i.getY()+", "+i.getR()+":\t"+i.didCollide(moveTo));
				System.out.println();
				*/
				
				// If it collides with something, set it back to true
				if(i.didCollide(moveTo.x,moveTo.y,getRadius())) return false;
			}
		}
		tree.addNode(nearest, moveTo);
		gui.draw(tree);
		setX(moveTo.x);
		setY(moveTo.y);
		rob.centreX=getX();
		rob.centreY=getY();
		return true;
	}

	private IntPoint step(int randX, int randY, int nearX, int nearY){
		// Point to move to given step
		int distX = randX-nearX;
		int distY = randY-nearY;
		double distance = dist(distX,distY);
		
		// Step / distance
		double stepRatio = step/distance;
		
		// Point to move to in x and y direction
		int moveX = nearX + (int)Math.floor(distX*stepRatio);
		int moveY = nearY + (int)Math.floor(distY*stepRatio);
		return new IntPoint(moveX,moveY);
	}
	
	//if goal is reached
	public void end(EasyGui gui){
		// Last node
		RRNode last = tree.getNearestNeighbour(getIntPoint());
		
		// This prints the route we used.
		ArrayList<IntPoint> route = tree.getPathFromRootTo(last);
		RenderablePolyline path = new RenderablePolyline();
		for(IntPoint p : route){
			path.addPoint(p.x, p.y);
		}
		
		// Set properties and then draw
		path.setProperties(Color.RED, 2.5f);
		gui.draw(path);
	}
	
}
