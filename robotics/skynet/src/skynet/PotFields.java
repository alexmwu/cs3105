package skynet;

import geometry.IntPoint;
import renderables.RenderablePoint;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class PotFields {

    private PFRobot explorer;
	private Robot rob;

    private IntPoint goal;
    private RenderablePoint rendGoal;

    private boolean atGoal;


    //whether simulation has been started
    private boolean started;

    private ArrayList<Obstacle> obstacles;

    private Random randGen;

	public PotFields(Robot r){
        rob=r;
        initGui();

        //initialize pfrobot
        explorer=new PFRobot(rob);

        //initialize goal with user points
        goal=new IntPoint();

        //init random generator
        randGen=new Random();

        started=false;

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

    //needs to be implemented differently because of differences in class structure between pf/pfrobot and rrt/rrtrobot
   public ArrayList<Obstacle> initRandObstacles(int maxObstacles){
       Obstacle tmp;
       ArrayList<Obstacle> randObst=new ArrayList<Obstacle>();
       boolean collided=false;

       //number of obstacles (from 0 to 10)
       int num=randGen.nextInt(maxObstacles);

       for(int i=0;i<num;i++){
            tmp=Obstacle.generateRandomObstacle(rob,randGen);
            collided=false;
           //if obstacles interact with other obstacles
           for(Obstacle o : randObst){
               if(o.didCollide(tmp)){
                   collided=true;
                   break;
               }
           }

           //if new obst intersects with the start or goal
           if(tmp.didCollide(explorer.getxCenter(),explorer.getyCenter(),explorer.getSonarRange()) || tmp.didCollide(goal.x,goal.y) || collided){
               i--;
               continue;
           }
            else{
               randObst.add(tmp);
               rob.getGui().draw(tmp.getRenderableOval());
           }

       }
       return randObst;
   }

    public void init(){
        if(!started) {
            rob.startPotFields();
            started=true;
        }

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

        //initialize obstacles
        obstacles=initRandObstacles(11);

        // User instructions
        rob.setStatusLabelText("Pick a movement mode (Move, Animate).");

        atGoal=false;


        // Refresh the GUI
        rob.getGui().update();
    }


    public void move(){
        if(atGoal) return;
        else{

            //rob.getGui().clearGraphicsPanel();

            IntPoint best=explorer.getBestSample(goal,obstacles,rob);
            if(best==null){
                stop();
                rob.setStatusLabelText("Error: there was a collision between sensing sample and obstacle.");
                return;
            }
            //draw new location for path
            drawPoint(best);

            drawGoal();

            //recalculate samples after changing location
            explorer.calculateSensingSamples();

            explorer.draw(rob.getGui());

            if(explorer.atGoal(goal)){
                atGoal=true;
                rob.setStatusLabelText("You've reached the goal.");
            }

            rob.getGui().update();
        }
    }

    public void drawPoint(IntPoint p){
        RenderablePoint rp=new RenderablePoint(p.x,p.y);
        rp.setProperties(Color.CYAN,5.0f);
        rob.getGui().draw(rp);
    }

    public void drawGoal(){
        rendGoal = new RenderablePoint(goal.x, goal.y);
        rendGoal.setProperties(Color.BLUE, 10.0f);
        rob.getGui().draw(rendGoal);
    }


    public void toGoal(){
        while(!atGoal){
            move();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop(){
        atGoal=true;
        rob.disablePotFields();
        started=false;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
