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
	
	public RenderableOval getRenderableOval(){
		return obst;
	}


}
