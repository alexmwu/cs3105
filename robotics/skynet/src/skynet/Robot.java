package skynet;

import easyGui.EasyGui;

public class Robot {
	private EasyGui gui;
	
	//labels for both rrt and pot. fields
	private int startXLabel,startYLabel,robotSizeLabel,stepSizeLabel,goalXLabel,goalYLabel;
	
	//text fields for both
	private int startXText,startYText,robotSizeText,stepSizeText,goalXText,goalYText;
	
	//buttons for both
	private int startButton;
    private int moveButton;
    private int goalButton;

    private int solutionButton;
	
	//labels for rrt
	private int goalSizeLabel;
	
	//text fields for rrt
	private int goalSizeText;
	
	//button for rrt
	private int toggleDotsButton;
	
	//status label for gui
	private int statusLabel;

	//pixel height and length of gui
	private int xPixels,yPixels;
	
	private RRT rrt;
	private PotFields pf;
	
	Robot(int x,int y, int buffer, boolean sType){
		gui=new EasyGui(x,y);
		xPixels=x;
		yPixels=y;
		
		// Add labels above the gui text fields for robot x,y starting coords and size and step
		startXLabel=gui.addLabel(1,0,"Starting X");
		startYLabel=gui.addLabel(1,1,"Starting Y");
		robotSizeLabel=gui.addLabel(1,2,"Robot Size");
		stepSizeLabel=gui.addLabel(1,3,"Step/Sonar Size");

		// Add labels above gui text fields for goal x,y
		goalXLabel=gui.addLabel(3,0,"Goal X");
		goalYLabel=gui.addLabel(3,1,"Goal Y");
		
		//Add text fields for both rrt and pot fields for the robot
		startXText=gui.addTextField(2, 0, "0");
		startYText=gui.addTextField(2, 1, "0");
		robotSizeText=gui.addTextField(2,2,"10");
		stepSizeText=gui.addTextField(2,3,"15");
		
		//add goal fields for both rrt and pot fields
		goalXText=gui.addTextField(4, 0, "0");
		goalYText=gui.addTextField(4, 1, "0");
		
		//top row of gui
		/////////////////////////////////////
		
		// Status label
		statusLabel=gui.addLabel(6,5,"Pick a robot to begin");
		
		//create two simulations
		pf=new PotFields(this);
		rrt=new RRT(this,buffer);
		
		/*simType=sType;
		if(simType==true){
			startRRT();
		}
		else{
			startPotFields();
		}*/
		
		//show gui
		gui.show();
	}


	public void startRRT(){
        stopPotFields();
		//enable move, to goal, and toggle dots buttons
		gui.setButtonEnabled(moveButton,true);
		gui.setButtonEnabled(goalButton, true);
		gui.setButtonEnabled(toggleDotsButton, true);
        gui.setButtonEnabled(solutionButton,true);
		
		//set status label text
		gui.setLabelText(statusLabel, "Enter in coordinates and click start to begin RRT simulation.");

        //wipe gui and update
        gui.clearGraphicsPanel();
        gui.update();
	}
	
	public void stopRRT(){
		//stop if simulation running
		rrt.stop();
		//disable rrt buttons
		gui.setButtonEnabled(moveButton,false);
		gui.setButtonEnabled(goalButton,false);
		gui.setButtonEnabled(toggleDotsButton,false);
	}
	
	public void startPotFields(){
		stopRRT();
        //enable pot fields buttons
        /////////////////////////////


        //wipe gui and update
        gui.clearGraphicsPanel();
        gui.update();
	}
	
	public void stopPotFields(){
		//disable potfields buttons
        //////////////////////////
	}
	
	public void setStatusLabelText(String s){
		gui.setLabelText(statusLabel, s);
	}
	
	public EasyGui getGui() {
		return gui;
	}

	public void setGui(EasyGui gui) {
		this.gui = gui;
	}

	public int getStartXLabel() {
		return startXLabel;
	}

	public void setStartXLabel(int startXLabel) {
		this.startXLabel = startXLabel;
	}

	public int getStartYLabel() {
		return startYLabel;
	}

	public void setStartYLabel(int startYLabel) {
		this.startYLabel = startYLabel;
	}

	public int getRobotSizeLabel() {
		return robotSizeLabel;
	}

	public void setRobotSizeLabel(int robotSizeLabel) {
		this.robotSizeLabel = robotSizeLabel;
	}

	public int getStepSizeLabel() {
		return stepSizeLabel;
	}

	public void setStepSizeLabel(int stepSizeLabel) {
		this.stepSizeLabel = stepSizeLabel;
	}

	public int getGoalXLabel() {
		return goalXLabel;
	}

	public void setGoalXLabel(int goalXLabel) {
		this.goalXLabel = goalXLabel;
	}

	public int getGoalYLabel() {
		return goalYLabel;
	}

	public void setGoalYLabel(int goalYLabel) {
		this.goalYLabel = goalYLabel;
	}

	public int getStartXText() {
		return startXText;
	}

	public void setStartXText(int startXText) {
		this.startXText = startXText;
	}

	public int getStartYText() {
		return startYText;
	}

	public void setStartYText(int startYText) {
		this.startYText = startYText;
	}

	public int getRobotSizeText() {
		return robotSizeText;
	}

	public void setRobotSizeText(int robotSizeText) {
		this.robotSizeText = robotSizeText;
	}

	public int getStepSizeText() {
		return stepSizeText;
	}

	public void setStepSizeText(int stepSizeText) {
		this.stepSizeText = stepSizeText;
	}

	public int getGoalXText() {
		return goalXText;
	}

	public void setGoalXText(int goalXText) {
		this.goalXText = goalXText;
	}

	public int getGoalYText() {
		return goalYText;
	}

	public void setGoalYText(int goalYText) {
		this.goalYText = goalYText;
	}

	public int getGoalSizeLabel() {
		return goalSizeLabel;
	}

	public void setGoalSizeLabel(int goalSizeLabel) {
		this.goalSizeLabel = goalSizeLabel;
	}

	public int getGoalSizeText() {
		return goalSizeText;
	}

	public void setGoalSizeText(int goalSizeText) {
		this.goalSizeText = goalSizeText;
	}

	public int getxPixels() {
		return xPixels;
	}

	public void setxPixels(int xPixels) {
		this.xPixels = xPixels;
	}

	public int getyPixels() {
		return yPixels;
	}

	public void setyPixels(int yPixels) {
		this.yPixels = yPixels;
	}
	
	public int getStartButton() {
		return startButton;
	}

	public void setStartButton(int startButton) {
		this.startButton = startButton;
	}

	public int getMoveButton() {
		return moveButton;
	}

	public void setMoveButton(int moveButton) {
		this.moveButton = moveButton;
	}

	public int getGoalButton() {
		return goalButton;
	}

	public void setGoalButton(int goalButton) {
		this.goalButton = goalButton;
	}

	public int getToggleDotsButton() {
		return toggleDotsButton;
	}

	public void setToggleDotsButton(int toggleDotsButton) {
		this.toggleDotsButton = toggleDotsButton;
	}
	
	public int getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(int statusLabel) {
		this.statusLabel = statusLabel;
	}

    public int getSolutionButton() {
        return solutionButton;
    }

    public void setSolutionButton(int solutionButton) {
        this.solutionButton = solutionButton;
    }

	
	// MAIN
	public static void main(String[] args)
	{
		Robot r =new Robot(600,600,10,true);
		
	}
}
