package skynet;


public class Object{
	private int xCenter,yCenter;
	private int radius;

	Object(){};
	
	Object(int x,int y,int r){
		xCenter=x;
		yCenter=y;
		radius=r;
	}
	
	public int getX(){
		return xCenter;
	}
	
	public int getY(){
		return yCenter;
	}
	
	public int getRadius(){
		return radius;
	}
	
	public void setX(int x){
		xCenter=x;
	}
	
	public void setY(int y){
		yCenter=y;
	}
	
	public void setRadius(int r){
		radius=r;
	}
	
	
	//for point intersecting with object
	//used for earlier assumption that the robot was a point
	public boolean didCollide(int x,int y){
		int distX=xCenter-x;
		int distY=yCenter-y;
		if(dist(distX,distY)<=radius) return true;
		else return false;
	}
	
	//for circle intersecting with object
	//used to make sure obstacles, goal, and robot don't intersect at start and robot does 
	//not hit obstacles when going towards the goal 
	public boolean didCollide(Object o){
		int distX=xCenter-o.getX();
		int distY=yCenter-o.getY();
		if(dist(distX,distY)<=o.getRadius()+radius) return true;
		else return false;
	}
	
	//for x,y,r ints (does same thing as above function)
	public boolean didCollide(int x,int y,int r){
		int distX=xCenter-x;
		int distY=yCenter-y;
		if(dist(distX,distY)<=r+radius) return true;
		else return false;
	}
	
	
	public double dist(int distX, int distY){
		return Math.sqrt(distX*distX+distY*distY);
	}

    public void print(){
        System.out.println("x: "+xCenter+", y: "+yCenter+", r: "+radius);
    }
}


