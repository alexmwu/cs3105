package skynet;

import easyGui.EasyGui;

public class Goal extends Object{
	private final int radiusId,xId,yId;
	
	Goal(Robot g){
		xId=g.getGoalXText();
		yId=g.getGoalYText();
		radiusId=g.getGoalSizeText();
	}
	
	public void start(EasyGui gui){
		//grab gui strings for the input values and parse into the object variables (i.e., into
		//x,y,radius,and step
		setX(Integer.parseInt(gui.getTextFieldContent(xId)));
		setY(Integer.parseInt(gui.getTextFieldContent(yId)));
		setRadius(Integer.parseInt(gui.getTextFieldContent(radiusId)));
	}
	
}
