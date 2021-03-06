package skynet;

import easyGui.EasyGui;

import java.io.IOException;

public class Robot {
    private EasyGui gui;

    //labels for both rrt and pot. fields
    private int startXLabel, startYLabel, robotSizeLabel, stepSizeLabel, goalXLabel, goalYLabel;

    //text fields for both
    private int startXText, startYText, robotSizeText, stepSizeText, goalXText, goalYText;


    //action buttons for pf
    private int pfStartButton;
    private int pfMoveButton;
    private int pfGoalButton;

    //action buttons for rrt
    private int rrtStartButton;
    private int rrtMoveButton;
    private int rrtGoalButton;

    //unique solution button to rrt
    private int solutionButton;

    //goal bias for rrt
    private int goalBiasButton;

    //labels for rrt
    private int goalSizeLabel;

    //text fields for rrt
    private int goalSizeText;

    //button for rrt
    private int toggleDotsButton;

    //status label for gui
    private int statusLabel;

    //load button for file
    private int loadButton;

    //pixel height and length of gui
    private int xPixels, yPixels;

    //button to toggle free space
    private int freeSpaceButton;

    //manager for course
    private CourseManager cm;

    private RRT rrt;
    private PotFields pf;

    Robot(int x, int y, int buffer) {
        gui = new EasyGui(x, y);
        xPixels = x;
        yPixels = y;

        //add file loader
        //loadButton=gui.addButton(0,0,"Load Course File",this,"load");
        freeSpaceButton = gui.addButton(0, 0, "Toggle Free Space", this, "toggleFreeSpace");

        // Add labels above the gui text fields for robot x,y starting coords and size and step
        startXLabel = gui.addLabel(1, 0, "Starting X");
        startYLabel = gui.addLabel(1, 1, "Starting Y");
        robotSizeLabel = gui.addLabel(1, 2, "Robot Size");
        stepSizeLabel = gui.addLabel(1, 3, "Step/Sonar Size");

        // Add labels above gui text fields for goal x,y
        goalXLabel = gui.addLabel(3, 0, "Goal X");
        goalYLabel = gui.addLabel(3, 1, "Goal Y");

        //Add text fields for both rrt and pot fields for the robot
        startXText = gui.addTextField(2, 0, "0");
        startYText = gui.addTextField(2, 1, "0");
        robotSizeText = gui.addTextField(2, 2, "10");
        stepSizeText = gui.addTextField(2, 3, "60");

        //add goal fields for both rrt and pot fields
        goalXText = gui.addTextField(4, 0, "500");
        goalYText = gui.addTextField(4, 1, "500");

        //top row of gui
        /////////////////////////////////////

        // Status label
        statusLabel = gui.addLabel(6, 5, "Pick a robot to begin");

        //create two simulations
        pf = new PotFields(this);
        rrt = new RRT(this, buffer);

        cm = new CourseManager("courses.txt");

        //show gui
        gui.show();
    }


    public void startRRT() {
        stopPotFields();

        //enable rrt move, to goal, solution, and toggle dots buttons
        gui.setButtonEnabled(rrtMoveButton, true);
        //gui.setButtonEnabled(rrtGoalButton, true);
        gui.setButtonEnabled(toggleDotsButton, true);
        gui.setButtonEnabled(solutionButton, true);
        gui.setButtonEnabled(goalBiasButton, true);

        //set step size to robot size
        gui.setTextFieldContent(stepSizeText, gui.getTextFieldContent(robotSizeText));

        //set status label text
        gui.setLabelText(statusLabel, "Enter in coordinates and click start to begin RRT simulation.");

        //wipe gui and update
        gui.clearGraphicsPanel();
        gui.update();
    }

    public void stopRRT() {
        //stop if simulation running
        rrt.stop();

        disableRRT();

        //logic to ensure that buttons are reenabled on restarting the simulation
        rrt.setStarted(false);
    }

    public void disableRRT() {
        //disable rrt buttons
        gui.setButtonEnabled(rrtMoveButton, false);
        //gui.setButtonEnabled(rrtGoalButton,false);
        gui.setButtonEnabled(toggleDotsButton, false);
        gui.setButtonEnabled(solutionButton, false);
        gui.setButtonEnabled(goalBiasButton, false);
    }

    public void startPotFields() {
        stopRRT();

        //enable pot fields move and goal buttons
        gui.setButtonEnabled(pfMoveButton, true);
        gui.setButtonEnabled(pfGoalButton, true);

        //set step size to robot size
        gui.setTextFieldContent(stepSizeText, "60");

        //wipe gui and update
        gui.clearGraphicsPanel();
        gui.update();
    }

    public void disablePotFields() {
        //disable potfields buttons
        gui.setButtonEnabled(pfMoveButton, false);
        gui.setButtonEnabled(pfGoalButton, false);

    }

    public void stopPotFields() {
        //stop if simulation running
        pf.stop();

        disablePotFields();

        //logic to ensure that buttons are reenabled on restarting the simulation
        pf.setStarted(false);
    }

    public void toggleFreeSpace() {
        rrt.freeSpace();
        pf.freeSpace();
    }

   /* public void load(){
        try {
            cm.readCoursesFromFile();
        } catch (IOException e) {
            System.err.println("The file "+cm.getCourseFileName()+" is not valid.");
        }
    }*/

    public double diagonalDistance() {
        return Math.sqrt(Math.pow(xPixels, 2) + Math.pow(yPixels, 2));
    }

    public void setStatusLabelText(String s) {
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

    public int getRrtStartButton() {
        return rrtStartButton;
    }

    public void setRrtStartButton(int rrtStartButton) {
        this.rrtStartButton = rrtStartButton;
    }

    public int getRrtMoveButton() {
        return rrtMoveButton;
    }

    public void setRrtMoveButton(int rrtMoveButton) {
        this.rrtMoveButton = rrtMoveButton;
    }

    public int getRrtGoalButton() {
        return rrtGoalButton;
    }

    public void setRrtGoalButton(int rrtGoalButton) {
        this.rrtGoalButton = rrtGoalButton;
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

    public int getPfStartButton() {
        return pfStartButton;
    }

    public void setPfStartButton(int pfStartButton) {
        this.pfStartButton = pfStartButton;
    }

    public int getPfMoveButton() {
        return pfMoveButton;
    }

    public void setPfMoveButton(int pfMoveButton) {
        this.pfMoveButton = pfMoveButton;
    }

    public int getPfGoalButton() {
        return pfGoalButton;
    }

    public void setPfGoalButton(int pfGoalButton) {
        this.pfGoalButton = pfGoalButton;
    }

    public int getGoalBiasButton() {
        return goalBiasButton;
    }

    public void setGoalBiasButton(int goalBiasButton) {
        this.goalBiasButton = goalBiasButton;
    }

    public int getLoadButton() {
        return loadButton;
    }

    public void setLoadButton(int loadButton) {
        this.loadButton = loadButton;
    }

    // MAIN
    public static void main(String[] args) {
        if (args.length == 2) {
            Robot r = new Robot(Integer.parseInt(args[0]), Integer.parseInt(args[1]), 10);
        } else if (args.length == 0) {
            Robot r = new Robot(600, 600, 10);
        } else {
            System.out.println("Usage: Robot [xPixels yPixels]");
        }
    }
}
