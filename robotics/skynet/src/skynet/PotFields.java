package skynet;

import geometry.IntPoint;
import renderables.RenderablePoint;

import java.awt.*;

public class PotFields {

    private PFRobot explorer;
	private Robot rob;

    private IntPoint goal;
    private RenderablePoint rendGoal;

    private boolean atGoal;

	public PotFields(Robot r){
        rob=r;
        initGui();

        //initialize pfrobot
        explorer=new PFRobot(rob);

        //initialize goal with user points
        goal=new IntPoint();

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
        rob.startPotFields();

        atGoal=false;

        // Start simulation robot and goal with user values
        getUserGoal();
        explorer.start(rob.getGui(),goal);

        //draw goal
        drawGoal();

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

            if(explorer.atGoal(goal)){
                atGoal=true;
                rob.setStatusLabelText("You've reached the goal.");
                return;
            }

           // rob.getGui().clearGraphicsPanel();

            IntPoint best=explorer.getBestSample(goal,null);

            //draw new location for path
            drawPoint(best);

            drawGoal();

            //recalculate samples after changing location
            explorer.calculateSensingSamples();

            explorer.draw(rob.getGui());

            rob.getGui().update();
        }
    }

    public void drawPoint(IntPoint p){
        RenderablePoint rp=new RenderablePoint(p.x,p.y);
        rp.setProperties(Color.RED,10.0f);
        rob.getGui().draw(rp);
    }

    public void drawGoal(){
        rendGoal = new RenderablePoint(goal.x, goal.y);
        rendGoal.setProperties(Color.BLUE, 20.0f);
        rob.getGui().draw(rendGoal);
    }


    public void toGoal(){
        while(!atGoal)
        {
            move();
            /*//sleep a bit to make animation seem more fluid
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }

    public void stop(){
        atGoal=true;
    }
	
}
