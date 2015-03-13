package skynet;

import geometry.IntPoint;
import renderables.RenderableOval;
import renderables.RenderablePoint;

import java.awt.*;
import java.awt.geom.Arc2D;
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

    private int numMoves;
    private double pathLength;

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

        numMoves=0;
        pathLength=0;

        rob.getGui().update();
	}

    public void initGui() {
        //add start button for potential fields
        rob.setPfStartButton(rob.getGui().addButton(6, 0, "Initialize PF", this, "init"));

        // Add a move and goal button to the right of start
        rob.setPfMoveButton(rob.getGui().addButton(6, 1, "Move PF", this, "move"));
        rob.getGui().setButtonEnabled(rob.getPfMoveButton(),false);
        rob.setPfGoalButton(rob.getGui().addButton(6, 2, "Animate", this, "toGoal"));
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
           if(tmp.didCollide(explorer.getxCenter(),explorer.getyCenter(),explorer.getSonarRange()) || tmp.didCollide(goal.x,goal.y,explorer.getRobotSize()) || collided){
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
        //set efficiency measures to 0
        numMoves=0;
        pathLength=0;

        // Refresh the GUI
        rob.getGui().update();
    }


    public void move(){
        if(atGoal) return;
        else{
            ArrayList<IntPoint> intersectedPoints=null;
            ArrayList<Obstacle> detectedObs=null;
            int previousX=explorer.getxCenter();
            int previousY=explorer.getyCenter();

            detectedObs=explorer.getDetectedObstacles(obstacles);

            //draw detected obstacles
            explorer.drawDetectedObstacles(detectedObs,rob.getGui());

            intersectedPoints=explorer.getIntersectedPoints(detectedObs);

            //draw intersected points
            if(intersectedPoints!=null)
                 explorer.drawIntersectedPoints(rob.getGui(), intersectedPoints);

            explorer.drawRays(rob.getGui());

            explorer.draw(rob.getGui());

            rob.getGui().update();

            int best=explorer.getBestSample(goal,intersectedPoints,detectedObs,rob);

            IntPoint bp=explorer.smoothPath(best);

            pathLength+=explorer.dist(bp.x,bp.y,previousX,previousY);

            explorer.calculateSensingSamples();
            explorer.calculateRayEnds();

            //draw new location for path
            drawPoint(bp);

            numMoves++;

            //at goal updates to place robot at goal if goal is within range of sensing samples
            if(explorer.atGoal(goal)){
                //update sensingsample placement
                explorer.calculateSensingSamples();
                //update ray placement
                explorer.calculateRayEnds();

                //update length before display (use current x and y since the explorer is at goal and already added best to previous)
                pathLength+=explorer.dist(bp.x,bp.y,explorer.getxCenter(),explorer.getyCenter());

                rob.setStatusLabelText("Moves: "+numMoves+" Length: "+ Double.toString(pathLength)+" Turns: "+Integer.toString(explorer.getNumTurns()));
                explorer.draw(rob.getGui());
                explorer.drawRays(rob.getGui());

                rob.getGui().update();

                atGoal=true;
            }

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
