package skynet;

import dataStructures.RRTree;
import easyGui.EasyGui;
import geometry.IntPoint;
import renderables.RenderableOval;
import renderables.RenderablePoint;
import renderables.RenderablePolyline;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by aw246 on 10/03/15.
 */
public class PFRobot{

    private int sonarRange;	//size of sonar
    private int sensingRadius; //radius of sensing region; should be greater than robot size

    private int robotSize;
    private int xCenter;
    private int yCenter;

    //ids for starting input
    private int xId,yId,radiusId,sonarId;

    //sensing points
    private IntPoint[] sensingSamples;
    //int point for where robot is pointing
    private IntPoint pointing;
    //number of sample points; should be odd
    private int numSamples;

    //angle from x-axis in radians
    private double angle;
    //step between sample points
    private double step;

    PFRobot(Robot r){
        xId=r.getStartXText();
        yId=r.getStartYText();
        radiusId=r.getRobotSizeText();
        sonarId=r.getStepSizeText();

        //7 sampling points
        numSamples=7;

        //allocate memory
        sensingSamples=new IntPoint[numSamples];

        angle=0;
    }

    //initialize pf variables and gui
    public void start(EasyGui gui,IntPoint goal){
        gui.clearGraphicsPanel();

        //initialize intpoints in array
        for(int i=0;i<sensingSamples.length;i++)
            sensingSamples[i]=new IntPoint();

        //initialize step of angle calculations
        step=Math.PI/(numSamples-1);
        //get range of robot sonar
        sonarRange=Integer.parseInt(gui.getTextFieldContent(sonarId));
        //size of robot
        robotSize=Integer.parseInt(gui.getTextFieldContent(radiusId));
        //get initial location
        xCenter=Integer.parseInt(gui.getTextFieldContent(xId));
        yCenter=Integer.parseInt(gui.getTextFieldContent(yId));
        //sensing radius is initially halfway between robot size and sonar size
        sensingRadius=(robotSize+sonarRange)/2;

        //get angle between robot and goal
        angle=getAngle(xCenter,yCenter,goal.x,goal.y);

        //get sensing sample coord points
        calculateSensingSamples();

        //draw robot and its system
        draw(gui);

    }

    public void draw(EasyGui gui) {
        //draw sensing region
        for (int i = 0; i < sensingSamples.length; i++) {
            RenderablePoint p = new RenderablePoint(sensingSamples[i].x, sensingSamples[i].y);
            p.setProperties(Color.RED, 4.0f);
            gui.draw(p);
        }

        //draw sonar
        RenderableOval sonar = new RenderableOval(xCenter, yCenter, 2 * sonarRange, 2 * sonarRange);
        sonar.setProperties(Color.GREEN, 0.5f, false);
        gui.draw(sonar);
        //draw actual robot
        gui.draw(new RenderableOval(xCenter, yCenter, 2 * robotSize, 2 * robotSize));
    }

    public void calculateSensingSamples(){
        double from=angle-(Math.PI/2.0);
        double to=angle+(Math.PI/2.0);
        int i=0;
       // System.out.println(from+" "+to);
        for(double d=from;d<=to;d+=step){
            sensingSamples[i].x=(int) (sensingRadius*Math.cos(d)) + xCenter;
            sensingSamples[i].y=(int) (sensingRadius*Math.sin(d)) + yCenter;
            //return center point of sensing samples
            if(i==(numSamples/2)){
                pointing=sensingSamples[i];
            }
            i++;
        }
    }

    public double getAngle(int x1, int y1, int x2, int y2){
        int dX=x2-x1;
        int dY=y2-y1;
        return Math.atan((double) dY / (double) dX);
    }

    //calculates all sensing sample potentials and returns point with the lowest one
    public IntPoint getBestSample(IntPoint goal, ArrayList<Obstacle> obs){
        ArrayList<IntPoint> intersectedPoints=null; //intersected points that rays have detected
        double currObstPot,currGoalPot, currTotalPot;   //current obstacle, goal, and total potential
        ArrayList<Obstacle> detectedObs=new ArrayList<Obstacle>();    //obstacles in sonar range

        //store obstacles that can be seen by sonar in detectedObs
        if(obs!=null){
            for(Obstacle o : obs){
                if(o.didCollide(xCenter,yCenter,sonarRange)){
                    detectedObs.add(o);
                }
            }
            if(!detectedObs.isEmpty()){
                intersectedPoints=getRayIntersections(detectedObs);
            }
        }



        //index of minimum potential and minimum potential
        int minPotIndex=0;
        double minPot=0;


        //return index of sensing sample point with lowest potential
        for(int i=0;i<sensingSamples.length;i++){
            currGoalPot=getGoalPotential(goal,sensingSamples[i]);
            //if no obstacles seen or no intersected points, there should be no obstacle potential
            if(detectedObs.isEmpty() || intersectedPoints==null)
                currObstPot=0;
            else{
                currObstPot=0;
                for(IntPoint ip : intersectedPoints){
                    if()
                }
            }

            //total potential
            currTotalPot=currGoalPot+currObstPot;
            if(currGoalPot<minPot){
                minPotIndex=i;
                minPot=currGoalPot;
            }
        }


        return smoothPath(minPotIndex);
    }

    //smooth path for new point and change robot location and angle
    public IntPoint smoothPath(int minPotIndex){
        //angle of best sensing sample ponts
        double bestAngle=smoothPathAngle(minPotIndex);
        //fraction of radius that should be applied to move (1 for straight, less for any other angle that is not straight)
        double radialFactor=getRadialFactor(bestAngle);

        //new point of robot
        IntPoint best=new IntPoint((int)(sensingRadius*radialFactor*Math.cos(bestAngle))+xCenter,(int)(sensingRadius*radialFactor*Math.sin(bestAngle))+yCenter);

        //set new location and angle
        xCenter=best.x;
        yCenter=best.y;
        angle=bestAngle;

        return best;
    }

    //angle of index of best sensing sample
    public double smoothPathAngle(int minPotIndex){
        //calculate "best" angle from index (current angle minus pi/2 is lowest sample angle. this plus step*minPotIndex equals the angle of
        //sensing sample with minimum potential
        return angle-(Math.PI/2.0)+(minPotIndex*step);
    }

    public double getRadialFactor(double bestAngle){
        return 1-(Math.abs(bestAngle-angle)/(Math.PI/2));
    }

    //get goal potential; temporary placeholder equation
    public double getGoalPotential(IntPoint goal,IntPoint sensingSample){
        return -Math.pow(sonarRange,2)/dist(goal.x,goal.y,sensingSample.x,sensingSample.y);
    }

    //adapted from http://stackoverflow.com/questions/1073336/circle-line-segment-collision-detection-algorithm
    public IntPoint getCloserIntersection(IntPoint sensingSample,double ang,Obstacle o){
        //length of ray
        double lengthSampleRay=(sonarRange-sensingRadius);
        //end point of ray (where sensing sample ray from center of robot intersects with sonar circle)
        IntPoint rayEnd=new IntPoint((int)(sonarRange*Math.cos(ang)+xCenter),(int)(sonarRange*Math.sin(ang)+yCenter));

        //direction vectors of ray
        double rayXDir=(rayEnd.x-sensingSample.x)/lengthSampleRay;
        double rayYDir=(rayEnd.y-sensingSample.y)/lengthSampleRay;

        //ray equation is parametric, where x=xdir*t+sensingSample.x and y=ydir*t+sensingSample.y (0<=t<=1)

        //value t of closest point to the circle
        double closestT=rayXDir*(o.getX()- sensingSample.x)+rayYDir*(o.getY()-sensingSample.y);

        //coordinates of a point on line closes to circle
        double closestX=closestT*rayXDir+sensingSample.x;
        double closestY=closestT*rayYDir+sensingSample.y;

        //distance from closest point to center of circle
        double closestLength=dist((int) closestX,(int) closestY,o.getX(),o.getY());

        if(closestLength<o.getRadius()){
            //distance from closest point to circle intersection point
            double dt=Math.sqrt(Math.pow(o.getRadius(),2)-Math.pow(closestLength,2));
            //intersection point (don't care about the other potential one if ray is extended)
            int interX=(int) ((closestT-dt)*rayXDir+sensingSample.x);
            int interY=(int) ((closestT-dt)*rayYDir+sensingSample.y);

            return new IntPoint(interX,interY);
        }
        else if(closestLength==o.getRadius()){
            return new IntPoint((int) closestX,(int) closestY);
        }
        else{
            return null;
        }
    }

    public ArrayList<IntPoint> getRayIntersections(ArrayList<Obstacle> detectedObs){
        ArrayList<IntPoint> intersections=new ArrayList<IntPoint>();    //all intersections from all rays and objects
        IntPoint inter;
        double from=angle-(Math.PI/2.0);
        double to=angle+(Math.PI/2.0);
        int i=0;
        for(Obstacle o : detectedObs) {
            for (double d = from; d <= to; d += step) {
                inter=getCloserIntersection(sensingSamples[i], d, o);
                if(inter!=null)
                    intersections.add(inter);
                i++;
            }
            i=0;
        }
        return intersections;
    }

    //formula for potential between two points
    public double potential(int x1, int y1, int x2, int y2){
        double d=dist(x1,y1,x2,y2);
        if(d>=sensingRadius) return 0;
        else if{d==0

        }
    }

    public boolean atGoal(IntPoint goal){
        if(dist(goal.x,goal.y,xCenter,yCenter)<robotSize) return true;
        else return false;
    }

    public double dist(int x1,int y1, int x2,int y2){
        return Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
    }

    public int getyCenter() {
        return yCenter;
    }

    public void setyCenter(int yCenter) {
        this.yCenter = yCenter;
    }

    public int getxCenter() {
        return xCenter;
    }

    public void setxCenter(int xCenter) {
        this.xCenter = xCenter;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public int getRobotSize() {
        return robotSize;
    }

    public void setRobotSize(int robotSize) {
        this.robotSize = robotSize;
    }

}
