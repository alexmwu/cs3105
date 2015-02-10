package skynet;

import java.util.ArrayList;
import java.util.Random;
import easyGui.EasyGui;
import renderables.*;
import geometry.IntPoint;

public class Obstacle extends Object{
	private RenderableOval obst;
	
	Obstacle(int cX, int cY, int r){
		super(cX,cY,r);
		obst = new RenderableOval(cX,cY,2*r,2*r);
	}
	
	public RenderableOval getRenderable(){
		return obst;
	}

	//generate random obstacles on a gui field
	public ArrayList<Obstacle> initObstacles(Object start, Object goal, int xPixels,int yPixels,EasyGui gui){
		int x,y,r;
		Obstacle tmp;
		ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();;
		
		// New Random generator
		Random randGen = new Random();
		
		// Number of obstacles
		int num = randGen.nextInt(11);
		
		
		for(int i=0;i<num;i++){
			x=randGen.nextInt(xPixels+1);
			y=randGen.nextInt(yPixels+1);
			r=randGen.nextInt(getRadius()+1);
			tmp = new Obstacle(x,y,r);
			// If obstacles intersects with the start or goal
			if(tmp.didCollide(start) || tmp.didCollide(goal)){
				i--;
				tmp=null;
				continue;
			}
			else{
				obstacles.add(tmp);
				gui.draw(tmp.getRenderable());
			}
		}
		return obstacles;
	}


}
