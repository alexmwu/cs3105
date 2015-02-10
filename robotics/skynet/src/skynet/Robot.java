package skynet;

import geometry.IntPoint;

import java.awt.Color;
import java.util.ArrayList;
import easyGui.EasyGui;
import dataStructures.RRNode;
import dataStructures.RRTree;
import renderables.RenderableOval;
import renderables.RenderablePolyline;

public class Robot extends Object{
	private int step;	//size of robot moves
	private RenderableOval rob;	//draw robot on screen based on radius
	
	//ids for starting input
	private int xId,yId,radiusId,stepId;
	private RRTree tree;
	
	//is the robot at the goal
	private boolean atGoal;
	
	//ids for the x,y coordinates at start and for the radius and step of robot
	Robot(EasyGui gui,int xid, int yid, int rid, int sid){
		xId=xid;
		yId=yid;
		radiusId=rid;
		stepId=sid;
	}
	
	public RenderableOval getRenderable(){
		return rob;
	}
	
	int getXId(){
		return xId;
	}
	
	int getYId(){
		return yId;
	}
	
	void setXId(int xid){
		xId=xid;
	}
	
	void setYId(int yid){
		yId=yid;
	}
	
	public void start(EasyGui gui,Goal goal){


		gui.clearGraphicsPanel();
		
		// Create an RRTree to be used later.
		tree = new RRTree(Color.BLACK);
		
		//grab gui strings for the input values and parse into the object variables (i.e., into
		//x,y,radius,and step
		super.setX(Integer.parseInt(gui.getTextFieldContent(xId)));
		super.setY(Integer.parseInt(gui.getTextFieldContent(yId)));
		super.setRadius(Integer.parseInt(gui.getTextFieldContent(radiusId)));
		step=Integer.parseInt(gui.getTextFieldContent(stepId));
		rob = new RenderableOval(super.getX(),super.getY(),2*super.getRadius(),2*super.getRadius());
		
		gui.draw(rob);
		
		// Set the start and goal positions based on input.
		// The goal radius is 40. A path that ends in the circle of this radius around
		// the goal is considered to have attained the goal position.
		tree.setStartAndGoal(new IntPoint(super.getX(), super.getY()), new IntPoint(goal.getX(), goal.getY()), goal.getRadius());
		
		// Tell the GUI to draw this tree (start and goal)
		gui.draw(tree);
	}
	
	public void move(){
		if(atGoal) return;
		else{
			int randomX=0,randomY=0;
			RRNode nearest=null;
			IntPoint moveTo=null;
			boolean collide=true;
			// Random  point
			while(collide){
				randomX = randGen.nextInt(xPixels+1);
				randomY = randGen.nextInt(yPixels+1);
				
				// Returns the nearest node to the random point
				nearest = tree.getNearestNeighbour(new IntPoint(randomX, randomY));
				
				// Get point to move to
				moveTo = step(randomX,randomY,nearest.x,nearest.y);
				
				// Set it to false
				collide=false;
				
				// If there are obstacles
				if(!obstacles.isEmpty()){
					for(Obstacle i : obstacles){

						
						System.out.println(moveTo.x+", "+moveTo.y);
						System.out.println(i.getX()+", "+i.getY()+", "+i.getR()+":\t"+i.didCollide(moveTo));
						System.out.println();
						
						// If it collides with something, set it back to true
						if(i.didCollide(moveTo)) collide=true;
					}
				}
			}
			
			
			
			tree.addNode(nearest, moveTo);
			
			/*
			// Draws a red dot at random point
			RenderablePoint randPoint = new RenderablePoint(randomX,randomY);
			randPoint.setProperties(Color.RED, 7.5f);
			gui.draw(randPoint);
			*/

			
			// If reached goal, stop and give a message
			if(dist(goalX-moveTo.x, goalY-moveTo.y)<=goalSize){
				atGoal = true;
				gui.setLabelText(statusLabelId, "You've reached the goal.");
				
				// Last node
				RRNode last = tree.getNearestNeighbour(moveTo);
				
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
			
			
			// Update GUI
			gui.update();
			
		}
		
	}
	
	public void toGoal(){
		while(!atGoal) move();
	}
}
