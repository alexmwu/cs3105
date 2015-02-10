package skynet;

import renderables.RenderableOval;
import easyGui.EasyGui;

public class Goal extends Object{
	private final int sizeId,xId,yId;
	
	Robot(EasyGui gui,int xid, int yid, int rid, int sid){
		xId=xid;yId=yid;radiusId=rid;stepId=sid;
		
		//grab gui strings for the input values and parse into the object variables (i.e., into
		//x,y,radius,and step
		super.setX(Integer.parseInt(gui.getTextFieldContent(xId)));
		super.setY(Integer.parseInt(gui.getTextFieldContent(yId)));
		super.setRadius(Integer.parseInt(gui.getTextFieldContent(radiusId)));
		step=Integer.parseInt(gui.getTextFieldContent(stepId));
		rob = new RenderableOval(super.getX(),super.getY(),2*super.getRadius(),2*super.getRadius());
	}
}
