package skynet;

import java.awt.Color;
import java.util.ArrayList;

import renderables.RenderablePoint;
import dataStructures.RRNode;
import dataStructures.RRTree;
import easyGui.EasyGui;
import geometry.IntPoint;

public class PotFields {

    private PFRobot explorer;
	private Robot rob;

    private IntPoint goal;

	public PotFields(Robot r){
        rob=r;
        initGui();

        //initialize pfrobot
        explorer=new PFRobot(rob);

        //initialize goal with user points
        goal=new IntPoint();
        getUserGoal();

        rob.getGui().update();
	}
	
	public void initGui(){
        //add start button for potential fields
        rob.setStartButton(rob.getGui().addButton(6, 0, "Initialize PF", this, "init"));

        // Add a move and goal button to the right of start
        rob.setMoveButton(rob.getGui().addButton(6,1,"Move PF",this,"move"));
        rob.getGui().setButtonEnabled(rob.getMoveButton(),false);
        rob.setGoalButton(rob.getGui().addButton(6, 2, "To Goal", this, "toGoal"));
        rob.getGui().setButtonEnabled(rob.getGoalButton(),false);
	}

    public void getUserGoal(){
        goal.x=Integer.parseInt(rob.getGui().getTextFieldContent(rob.getGoalXText()));
        goal.y=Integer.parseInt(rob.getGui().getTextFieldContent(rob.getGoalYText()));
    }

    public void init(){
     /*   if(!started){
            rob.startRRT();
            started = true;
        }

        atGoal=false;

        // Start simulation robot and goal with user values
        goal.start(rob.getGui());
        explorer.start(rob.getGui(),goal);

        // If already at goal
        if(explorer.didCollide(goal)){
            atGoal = true;
            rob.setStatusLabelText("You are already at the goal.");
            rob.getGui().update();
            return;
        }

        // User instructions
        rob.setStatusLabelText("Please press Move for one step and Goal for solution");

        // Refresh the GUI
        rob.getGui().update();*/
    }


    public void move(){

    }


    public void toGoal(){

    }


	
}
