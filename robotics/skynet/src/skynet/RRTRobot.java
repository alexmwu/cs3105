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
	private RenderableOval rendOv;	//draw robot on screen based on radius
	
	//ids for starting input
	private int xId,yId,radiusId,stepId;
	private RRTree tree;

    //efficiency counter for turns
    int numTurns;
    double pathLength;
    int numNodes;
    //to determine if angle changed to increment efficiency counter
    double lastAngle;

	//ids for the x,y coordinates at start and for the radius and step of robot
	RRTRobot(Robot r){
		xId=r.getStartXText();
		yId=r.getStartYText();
		radiusId=r.getRobotSizeText();
		stepId=r.getStepSizeText();

		// Create an RRTree to be used later.
		tree = new RRTree(Color.BLACK);
        numTurns=0;
        pathLength=0;
        numNodes=0;
        lastAngle=0;
	}
	
	private void setRenderable(){
		rendOv = new RenderableOval(getX(),getY(),2*getRadius(),2*getRadius());
	}
	
	public RenderableOval getRenderable(){
		return rendOv;
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
		
		gui.draw(rendOv);
		
		// Set the start and goal positions based on input.
		// The goal radius is 40. A path that ends in the circle of this radius around
		// the goal is considered to have attained the goal position.
		tree.setStartAndGoal(new IntPoint(getX(), getY()), new IntPoint(goal.getX(), goal.getY()), goal.getRadius());
		
		// Tell the GUI to draw this tree (start and goal)
		gui.draw(tree);

        //reinit to 0
        numTurns=0;
        pathLength=0;
        numNodes=0;
        //initial angle is b/w goal and start position
        lastAngle=getAngle(getX(),getY(),goal.getX(),goal.getY());
	}
	
	//returns next point if there is no obstacle collision; null otherwise
	public boolean move(EasyGui gui, ArrayList<Obstacle> obstacles, int randomX, int randomY,boolean draw){
		// Returns the nearest node to the random point
		RRNode nearest = tree.getNearestNeighbour(new IntPoint(randomX, randomY));

		// Get point to move to
		IntPoint moveTo = step(randomX,randomY,nearest.x,nearest.y);
		
			// If there are obstacles
		if(!obstacles.isEmpty()){
			for(Obstacle i : obstacles){
				// If it collides with something, set it back to true
				if(i.didCollide(moveTo.x,moveTo.y,getRadius())) return false;
			}
		}
		tree.addNode(nearest, moveTo);

        //only if told to draw
        if(draw) gui.draw(tree);

        //see if angle changed before changing current position
        double ang=getAngle(getX(),getY(),moveTo.x,moveTo.y);
        if(lastAngle!=ang){
            numTurns++;
        }
        //set this angle for next use of move
        lastAngle=ang;

		setX(moveTo.x);
		setY(moveTo.y);
		rendOv.centreX=getX();
		rendOv.centreY=getY();

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
	
	//if goal is reached, returns length of path
	public ArrayList<IntPoint> end(EasyGui gui){
		// Last node
		RRNode last = tree.getNearestNeighbour(getIntPoint());
		
		// This prints the route we used.
		ArrayList<IntPoint> route = tree.getPathFromRootTo(last);
		RenderablePolyline path = new RenderablePolyline();

        //number of nodes in path
        numNodes=route.size();

        path.addPoint(route.get(0).x,route.get(0).y);
        for(int i=1;i<route.size();i++){
            pathLength+=dist(route.get(i).x,route.get(i).y,route.get(i-1).x,route.get(i-1).y);
            path.addPoint(route.get(i).x,route.get(i).y);
        }

		// Set properties and then draw
		path.setProperties(Color.RED, 2.5f);
		gui.draw(path);
        return route;
	}

    /*public ArrayList<IntPoint> smoothPath(ArrayList<IntPoint>){
        double lastA
    }*/


    //get angle to see if angle changed
    public double getAngle(int x1, int y1, int x2, int y2){
        int dX=x2-x1;
        int dY=y2-y1;
        return Math.atan2((double) dY , (double) dX);
    }

    public int getNumTurns() {
        return numTurns;
    }

    public double getPathLength() {
        return pathLength;
    }

    public int getNumNodes() {
        return numNodes;
    }
}
