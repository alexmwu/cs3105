package skynet;

import dataStructures.RRTree;
import geometry.IntPoint;
import renderables.RenderableOval;

/**
 * Created by aw246 on 10/03/15.
 */
public class PFRobot {

    private int sonarRange;	//size of sonar
    private int sensingRadius; //radius of sensing region; should be greater than robot size
    private int robotSize;
    private RenderableOval rendOv;	//draw robot on screen based on radius

    //ids for starting input
    private int xId,yId,radiusId,sonarId;

    //sensing points
    private IntPoint[] sensingSamples;
    //number of sample points; should be odd
    private int numSamples;

    //angle from x-axis in radians
    private double angle;
    //step between sample points
    private double step;

    //is the robot at the goal
    private boolean atGoal;

    PFRobot(Robot r){
        xId=r.getStartXText();
        yId=r.getStartYText();
        radiusId=r.getRobotSizeText();
        sonarId=r.getStepSizeText();

        sonarRange=Integer.parseInt(r.getGui().getTextFieldContent(sonarId));
        //size of robot
        robotSize=Integer.parseInt(r.getGui().getTextFieldContent(radiusId));

        //7 sampling points
        numSamples=7;

        //sensing radius of 10 from robot center
        sensingRadius=10;
    }

    public void init(){
        step=Math.PI/Math.floor((double) numSamples);

    }


    public void calculateSensingSamples(){
        double from=angle-(Math.PI/2.0);
        double to=angle+(Math.PI/2.0);
        for(double d=from;d<=to;d+=step){
            
        }
    }
}
