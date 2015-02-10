package skynet;

import renderables.RenderableOval;
import easyGui.EasyGui;

public class Goal extends Object{
	private final int radiusId,xId,yId;
	
	Goal(int xid, int yid, int rid){
		xId=xid;
		yId=yid;
		radiusId=rid;
	}
	
	public void start(EasyGui gui){
		//grab gui strings for the input values and parse into the object variables (i.e., into
		//x,y,radius,and step
		setX(Integer.parseInt(gui.getTextFieldContent(xId)));
		setY(Integer.parseInt(gui.getTextFieldContent(yId)));
		setRadius(Integer.parseInt(gui.getTextFieldContent(radiusId)));
	}
	
}