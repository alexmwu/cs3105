package skynet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import renderables.*;


public class RRT {
	
	private RRTRobot explorer;
	
	private Goal goal;
	private Robot rob;
	
	private Random randGen;
	private int bufferFactor;	//bufferFactor to pick random ints in buffer around window
	
	private ArrayList<Obstacle> obstacles;
	
	// whether gui has initially started (produces two more buttons), whether current robot is at 
	//the goal, and whether the user wants to display the random red dots, respectively
	private boolean atGoal,displayRandomDots;

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
		
		// New Random generator
		randGen = new Random();
		
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
		rob.setRrtGoalButton(rob.getGui().addButton(5, 2, "Animate", this, "toGoal"));
		rob.getGui().setButtonEnabled(rob.getRrtGoalButton(),false);
        rob.setSolutionButton(rob.getGui().addButton(5, 3, "Solution", this, "solution"));
		rob.getGui().setButtonEnabled(rob.getSolutionButton(),false);

		//rrt toggle dots
		rob.setToggleDotsButton(rob.getGui().addButton(5, 4, "Toggle Dots", this, "randomDots"));
		rob.getGui().setButtonEnabled(rob.getToggleDotsButton(),false);
	}

	//generate random obstacles on a gui field
	public ArrayList<Obstacle> initRandObstacles(){
		int x,y,r;
		Obstacle tmp;
		ArrayList<Obstacle> randObst = new ArrayList<Obstacle>();
		boolean collided = false;

		// Number of obstacles (from 0 to 10)
		int num = randGen.nextInt(11);


		for(int i=0;i<num;i++){
			x=randGen.nextInt(rob.getxPixels()+1);
			y=randGen.nextInt(rob.getyPixels()+1);
			r=randGen.nextInt(goal.getRadius()+1);

			//if obstacles intersect with other obstacles
			for(Obstacle o : randObst){
				if(o.didCollide(x,y,r)) collided=true;

			}

			// If obstacles intersects with the start or goal
			if(explorer.didCollide(x,y,r) || goal.didCollide(x,y,r) || collided){
				i--;
				continue;
			}
			else{
				tmp = new Obstacle(x,y,r);
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
	
	public void init(){
		rob.startRRT();

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

		// Initialize obstacles
		obstacles = initRandObstacles();
		
		// User instructions
		rob.setStatusLabelText("Pick a movement mode (Move, Animate, Solution).");

        atGoal=false;
		// Refresh the GUI
		rob.getGui().update();
	}
	
	public void move(){
		if(atGoal) return;
		else{
			int randomX=0,randomY=0;
			
			// if it returns IntPoint, break out of loop; else continue (it won't return if it
			// hits obstacles
			
			while(true){
				randomX = randGen.nextInt((int)rob.getxPixels()+2*(rob.getxPixels()/bufferFactor))-(rob.getxPixels()/bufferFactor);
				randomY = randGen.nextInt((int)rob.getyPixels()+2*(rob.getyPixels()/bufferFactor))-(rob.getyPixels()/bufferFactor);
				//if(randomX>550 || randomX<-50||randomY>550||randomY<-550)System.out.println(randomX+", "+randomY);
				if(explorer.move(rob.getGui(),obstacles,randomX,randomY,true)) break;
			}
			
			// Draws a red dot at random point
			if(displayRandomDots){
				RenderablePoint randPoint = new RenderablePoint(randomX,randomY);
				randPoint.setProperties(Color.RED, 7.5f);
				rob.getGui().draw(randPoint);
			}

			rob.getGui().draw(explorer.getRenderable());	
			
			if(explorer.didCollide(goal)){
				atGoal = true;
				rob.setStatusLabelText("You've reached the goal.");
				end();
			}
			
			// Update GUI
			rob.getGui().update();
		}
	}

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
                int randomX = 0, randomY = 0;

                // if it returns IntPoint, break out of loop; else continue (it won't return if it
                // hits obstacles

                while (true) {
                    randomX = randGen.nextInt((int) rob.getxPixels() + 2 * (rob.getxPixels() / bufferFactor)) - (rob.getxPixels() / bufferFactor);
                    randomY = randGen.nextInt((int) rob.getyPixels() + 2 * (rob.getyPixels() / bufferFactor)) - (rob.getyPixels() / bufferFactor);
                    //if(randomX>550 || randomX<-50||randomY>550||randomY<-550)System.out.println(randomX+", "+randomY);
                    if (explorer.move(rob.getGui(), obstacles, randomX, randomY,false)) break;
                }

                if (explorer.didCollide(goal)) {
                    atGoal = true;
                    rob.setStatusLabelText("You've reached the goal. Please wait for GUI to update.");
                    end();
                }
        }


        // Update GUI
        rob.getGui().update();
    }
	
	//an internal stop to be used when other functions need control of the GUI
	public void stop(){
		atGoal=true;
	}
	
}
