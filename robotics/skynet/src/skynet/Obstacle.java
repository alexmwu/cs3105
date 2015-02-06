package skynet;

import renderables.*;
import geometry.IntPoint;

public class Obstacle {
	private RenderableOval ob;
	private int xCenter,yCenter,radius;
	
	Obstacle(int cX, int cY, int r){
		ob = new RenderableOval(cX,cY,2*r,2*r);
		xCenter=cX;
		yCenter=cY;
		radius=r;
	}
	
	public boolean isEmpty(){
		return ob==null;
	}
	
	RenderableOval getObstacle(){
		return ob;
	}
	
	public int getX(){
		return xCenter;
	}
	
	public int getY(){
		return yCenter;
	}
	
	public int getR(){
		return radius;
	}
	
	public RenderableOval getRenderable(){
		return ob;
	}
	
	//for point intersecting with obstacle
	public boolean didCollide(IntPoint p){
		int distX=xCenter-p.x;
		int distY=yCenter-p.y;
		if(dist(distX,distY)<=radius) return true;
		else return false;
	}
	
	//for circle intersecting with obstacle
	public boolean didIntersect(int x,int y,int r){
		int distX=xCenter-x;
		int distY=yCenter-y;
		if(dist(distX,distY)<=r+radius) return true;
		else return false;
	}
	
	public double dist(int distX, int distY){
		return Math.sqrt(distX*distX+distY*distY);
	}
}