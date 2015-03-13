package skynet;

import java.util.ArrayList;
import java.util.Random;
import easyGui.EasyGui;
import renderables.*;
import geometry.IntPoint;

public class Obstacle extends Object{
	private RenderableOval obst;

    //true for circle, false for line
    private boolean type;

	Obstacle(int cX, int cY, int r){
		super(cX,cY,r);
		obst = new RenderableOval(cX,cY,2*r,2*r);
	}
	
	public RenderableOval getRenderableOval(){
		return obst;
	}

    public static Obstacle generateRandomObstacle(Robot rob,Random randGen){
        int x,y,r;
        Obstacle tmp;
        randGen.setSeed(System.currentTimeMillis());

        x=randGen.nextInt(rob.getxPixels()+1);
        y=randGen.nextInt(rob.getyPixels()+1);
        r=randGen.nextInt((int) rob.diagonalDistance()/10);

        tmp=new Obstacle(x,y,r);
        return tmp;
    }
}
