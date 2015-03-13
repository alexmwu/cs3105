package skynet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import geometry.IntPoint;
import renderables.*;


public class RRT {
	
	private RRTRobot explorer;
	
	private Goal goal;
	private Robot rob;
	
	private Random randGen;
	private int bufferFactor;	//bufferFactor to pick random ints in buffer around window

    private boolean freeSpace;
	
	private ArrayList<Obstacle> obstacles;


    // whether gui has initially started (produces two more buttons), whether current robot is at
	//the goal, and whether the user wants to display the random red dots, respectively
	private boolean started,atGoal,displayRandomDots,goalBias;

    private int totalMoves;

	public RRT(Robot r,int buffer){
		
		bufferFactor=buffer;
		rob=r;
		
		initGUI();
		
		// Initialize the robot with ids and values for starting coordinates, radius, and step
		explorer = new RRTRobot(rob);
		
		// Initialize goal with ids for starting coordinates and radius
		goal = new Goal(rob);
		
		// do not display random dots to start with
		displayRandomDots=false;
        //do not have goal bias to start
        goalBias=false;

        started=false;

        freeSpace=false;

		// New Random generator
		randGen = new Random();

        //init efficiency counts
        totalMoves=0;
	}
	
	//initialize rrt gui
	public void initGUI(){		
		//rrt goal size label
		rob.setGoalSizeLabel(rob.getGui().addLabel(3,2,"Goal Size"));
		
		//rrt goal size text
		rob.setGoalSizeText(rob.getGui().addTextField(4,2,"40"));
		
		//add start button for rrt
		rob.setRrtStartButton(rob.getGui().addButton(5, 0, "Initialize RRT", this, "init"));
		
		// Add a move, solution, and goal button to the right of start
		rob.setRrtMoveButton(rob.getGui().addButton(5, 1, "Move RRT", this, "move"));
		rob.getGui().setButtonEnabled(rob.getRrtMoveButton(),false);
		/*rob.setRrtGoalButton(rob.getGui().addButton(5, 2, "Animate", this, "toGoal"));
		rob.getGui().setButtonEnabled(rob.getRrtGoalButton(),false);*/
        rob.setSolutionButton(rob.getGui().addButton(5, 2, "Solution", this, "solution"));
		rob.getGui().setButtonEnabled(rob.getSolutionButton(),false);

        rob.setGoalBiasButton(rob.getGui().addButton(5,3,"Goal Bias",this,"goalBias"));
        rob.getGui().setButtonEnabled(rob.getGoalBiasButton(),false);


		//rrt toggle dots
		rob.setToggleDotsButton(rob.getGui().addButton(5, 4, "Toggle Dots", this, "randomDots"));
		rob.getGui().setButtonEnabled(rob.getToggleDotsButton(),false);
	}

	//generate random obstacles on a gui field
	public ArrayList<Obstacle> initRandObstacles(int maxObstacles){
        Obstacle tmp;
        ArrayList<Obstacle> randObst = new ArrayList<Obstacle>();
        boolean collided = false;

        // Number of obstacles (from 0 to 10)
        int num = randGen.nextInt(maxObstacles);

		for(int i=0;i<num;i++){
            tmp=Obstacle.generateRandomObstacle(rob,randGen);
            collided=false;
			//if obstacles intersect with other obstacles
			for(Obstacle o : randObst){
				if(o.didCollide(tmp)){
                    collided=true;
                    break;
                }
			}

			// If new obst intersects with the start or goal
			if(explorer.didCollide(tmp) || goal.didCollide(tmp) || collided){
				i--;
				continue;
			}
			else {
                randObst.add(tmp);
                rob.getGui().draw(tmp.getRenderableOval());
            }
		}
		return randObst;
	}

	
	public void show(){
		// Displays GUI
		rob.getGui().show();
	}
	
	public void randomDots(){
		if(displayRandomDots) displayRandomDots=false;
		else displayRandomDots=true;
	}

    public void goalBias(){
        goalBias=!goalBias;
    }

    public void freeSpace(){
        freeSpace=!freeSpace;
    }
	
	public void init(){
        if(!started){
            rob.startRRT();
            started=true;
        }

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

        //if not free space sim, initialize obstacles
        if(freeSpace){
            obstacles=new ArrayList<Obstacle>();
        }
        else{
            obstacles = initRandObstacles(11);
        }

        totalMoves=0;

        atGoal=false;
		// Refresh the GUI
		rob.getGui().update();
	}
	
	public void move(){
		if(atGoal) return;
		else{
			// if it returns IntPoint, break out of loop; else continue (it won't return if it
			// hits obstacles

            IntPoint randomPoint=new IntPoint();

			while(true){
                if(!goalBias){
                    randomPoint.x= randGen.nextInt((int)rob.getxPixels()+2*(rob.getxPixels()/bufferFactor))-(rob.getxPixels()/bufferFactor);
                    randomPoint.y= randGen.nextInt((int)rob.getyPixels()+2*(rob.getyPixels()/bufferFactor))-(rob.getyPixels()/bufferFactor);
                }
                else{
                    randomPoint.x=(int) ((rob.getxPixels()/4)*randGen.nextGaussian()+goal.getX());
                    randomPoint.y=(int) ((rob.getyPixels()/4)*randGen.nextGaussian()+goal.getY());
                    checkBounds(randomPoint);
                }
                if(explorer.move(rob.getGui(),obstacles,randomPoint.x,randomPoint.y,true)) break;
			}
			
			// Draws a red dot at random point
			if(displayRandomDots){
				RenderablePoint randPoint = new RenderablePoint(randomPoint.x,randomPoint.y);
				randPoint.setProperties(Color.RED, 7.5f);
				rob.getGui().draw(randPoint);
			}

			rob.getGui().draw(explorer.getRenderable());

            //another move - increment efficiency counters
            totalMoves++;

			if(explorer.didCollide(goal)){
				atGoal = true;
				end();
                rob.setStatusLabelText("Moves: " + Integer.toString(totalMoves) + " Nodes: " + Integer.toString(explorer.getNumNodes()) + " Length: " + Double.toString(explorer.getPathLength())+" Turns: "+explorer.getNumTurns());
			}
			
			// Update GUI
			rob.getGui().update();
		}
	}

    //return number of nodes in path
	public void end(){
		explorer.end(rob.getGui());

		// Update GUI
		rob.getGui().update();
	}

    //quite a bit slower than solution, even after it finishes up (slow to drag gui image around)
	public void toGoal(){
        while(!atGoal) move();
	}

    public void solution(){
        while(!atGoal) {

            // if it returns IntPoint, break out of loop; else continue (it won't return if it
            // hits obstacles

            IntPoint randomPoint=new IntPoint();

			while(true){
                if(!goalBias){
                    randomPoint.x= randGen.nextInt((int)rob.getxPixels()+2*(rob.getxPixels()/bufferFactor))-(rob.getxPixels()/bufferFactor);
                    randomPoint.y= randGen.nextInt((int)rob.getyPixels()+2*(rob.getyPixels()/bufferFactor))-(rob.getyPixels()/bufferFactor);
                }
                else{
                    randomPoint.x=(int) ((rob.getxPixels()/4)*randGen.nextGaussian()+goal.getX());
                    randomPoint.y=(int) ((rob.getyPixels()/4)*randGen.nextGaussian()+goal.getY());
                    checkBounds(randomPoint);
                }
                if(explorer.move(rob.getGui(),obstacles,randomPoint.x,randomPoint.y,true)) break;
			}

			// Draws a red dot at random point
			if(displayRandomDots){
				RenderablePoint randPoint = new RenderablePoint(randomPoint.x,randomPoint.y);
				randPoint.setProperties(Color.RED, 7.5f);
				rob.getGui().draw(randPoint);
                rob.getGui().update();
			}
            //another successful move
            totalMoves++;

			if(explorer.didCollide(goal)){
				atGoal = true;
				end();
                rob.setStatusLabelText("Moves: " + Integer.toString(totalMoves) + " Nodes: " + Integer.toString(explorer.getNumNodes()) + " Length: " + Double.toString(explorer.getPathLength())+" Turns: "+Integer.toString(explorer.getNumTurns()));
			}
        }


        // Update GUI
        rob.getGui().update();
    }
	
	//an internal stop to be used when other functions need control of the GUI
	public void stop(){
		atGoal=true;
	}

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public double dist(int x1,int y1, int x2,int y2){
        return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
    }

    public void checkBounds(IntPoint randP){
        int buffEdgePX=rob.getxPixels()+(rob.getxPixels()/bufferFactor);
        int buffEdgeNX=-(rob.getxPixels()/bufferFactor);
        int buffEdgePY=rob.getyPixels()+(rob.getyPixels()/bufferFactor);
        int buffEdgeNY=-(rob.getyPixels()/bufferFactor);

        if(randP.x>buffEdgePX){
            randP.x=buffEdgePX;
        }
        else if(randP.x<buffEdgeNX){
            randP.x=buffEdgeNX;
        }
        if(randP.y>buffEdgePY){
            randP.y=buffEdgePY;
        }
        else if(randP.y<buffEdgeNY){
            randP.y=buffEdgeNY;
        }

    }

}
