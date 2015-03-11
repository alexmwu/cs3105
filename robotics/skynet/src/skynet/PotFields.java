package skynet;

import geometry.IntPoint;
import renderables.RenderablePoint;

import java.awt.*;

public class PotFields {

    private PFRobot explorer;
	private Robot rob;

    private IntPoint goal;
    private RenderablePoint rendGoal;

    private boolean started;

    private boolean atGoal;

	public PotFields(Robot r){
        rob=r;
        initGui();

        //initialize pfrobot
        explorer=new PFRobot(rob);

        //initialize goal with user points
        goal=new IntPoint();

        // So doesn't throw an error with move or goal button used before initialization
        started = false;

        rob.getGui().update();
	}

    public void initGui() {
        //add start button for potential fields
        rob.setPfStartButton(rob.getGui().addButton(6, 0, "Initialize PF", this, "init"));

        // Add a move and goal button to the right of start
        rob.setPfMoveButton(rob.getGui().addButton(6, 1, "Move PF", this, "move"));
        rob.getGui().setButtonEnabled(rob.getPfMoveButton(),false);
        rob.setPfGoalButton(rob.getGui().addButton(6, 2, "To Goal", this, "toGoal"));
        rob.getGui().setButtonEnabled(rob.getPfGoalButton(),false);
	}

    public void getUserGoal(){
        goal.x=Integer.parseInt(rob.getGui().getTextFieldContent(rob.getGoalXText()));
        goal.y=Integer.parseInt(rob.getGui().getTextFieldContent(rob.getGoalYText()));
    }

    public void init(){
        if(!started){
            rob.startPotFields();
            started = true;
        }

        atGoal=false;

        // Start simulation robot and goal with user values
        getUserGoal();
        explorer.start(rob.getGui());

        //draw goal
        rendGoal=new RenderablePoint(goal.x,goal.y);
        rendGoal.setProperties(Color.BLUE,20.0f);
        rob.getGui().draw(rendGoal);

        // If already at goal
        if(explorer.atGoal(goal)){
            atGoal = true;
            rob.setStatusLabelText("You are already at the goal.");
            rob.getGui().update();
            return;
        }

        // User instructions
        rob.setStatusLabelText("Pick a movement mode (Move, Animate).");

        atGoal=false;


        // Refresh the GUI
        rob.getGui().update();
    }



    public void move(){
        if(atGoal) return;
        else{
            IntPoint best=explorer.getBestSample(goal,null);

            explorer.setxCenter(best.x);
            explorer.setyCenter(best.y);

            explorer.draw(rob.getGui());

            rob.getGui().update();
        }
    }


    public void toGoal(){
        while(!atGoal) move();
    }

    public void stop(){
        atGoal=true;
    }
	
}
