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
    //rays of sensing samples
    private IntPoint[] rayEnds;
    //number of sample points; should be odd
    private int numSamples;

    //angle from x-axis in radians
    private double angle;
    //step between sample points
    private double step;

    private RenderableOval robot;
    private RenderableOval sonar;
    private RenderablePoint[] sensingPoints;
    private RenderablePolyline[] rayPolylines;

    //efficiency counter for turns
    int numTurns;

    PFRobot(Robot r){
        xId=r.getStartXText();
        yId=r.getStartYText();
        radiusId=r.getRobotSizeText();
        sonarId=r.getStepSizeText();

        //7 sampling points
        numSamples=7;

        //allocate memory
        sensingSamples=new IntPoint[numSamples];
        sensingPoints=new RenderablePoint[numSamples];
        rayEnds=new IntPoint[numSamples];
        rayPolylines=new RenderablePolyline[numSamples];

        angle=0;
        numTurns=0;
    }

    //initialize pf variables and gui
    public void start(EasyGui gui,IntPoint goal){
        gui.clearGraphicsPanel();

        //initialize intpoints in array
        for(int i=0;i<sensingSamples.length;i++)
            sensingSamples[i]=new IntPoint();
        //init ray ends
        for(int i=0;i<rayEnds.length;i++)
            rayEnds[i]=new IntPoint();

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
        sensingRadius=calculateSensingRadius();

        //get angle between robot and goal
        angle=getAngle(xCenter,yCenter,goal.x,goal.y);

        //init effiency measure to 0
        numTurns=0;

        //get sensing sample coord points
        calculateSensingSamples();
        //get ray ends
        calculateRayEnds();

        //draw robot and its system
        draw(gui);

    }

    public void draw(EasyGui gui) {
        //draw sensing region
        for (int i = 0; i < sensingSamples.length; i++) {
            gui.unDraw(sensingPoints[i]);
            sensingPoints[i] = new RenderablePoint(sensingSamples[i].x, sensingSamples[i].y);
            sensingPoints[i].setProperties(Color.RED,5.0f);
            gui.draw(sensingPoints[i]);
        }
        gui.unDraw(sonar);
        gui.unDraw(robot);

        //draw sonar
        sonar = new RenderableOval(xCenter, yCenter, 2 * sonarRange, 2 * sonarRange);
        sonar.setProperties(Color.GREEN, 0.5f, false);
        gui.draw(sonar);
        //draw actual robot
        robot=new RenderableOval(xCenter, yCenter, 2 * robotSize, 2 * robotSize);
        gui.draw(robot);
    }

    public void calculateSensingSamples(){
        double from=angle-(Math.PI/2.0);
        double to=angle+(Math.PI/2.0);
        int i=0;
        for(double d=from;d<=to;d+=step){
            sensingSamples[i].x=(int) (sensingRadius*Math.cos(d)) + xCenter;
            sensingSamples[i].y=(int) (sensingRadius*Math.sin(d)) + yCenter;
            //return center point of sensing samples
            i++;
        }
    }

    public double getAngle(int x1, int y1, int x2, int y2){
        int dX=x2-x1;
        int dY=y2-y1;
        return Math.atan2((double) dY , (double) dX);
    }


    public void calculateRayEnds(){
        double from=angle-(Math.PI/2.0);
        double to=angle+(Math.PI/2.0);
        int i=0;
        for(double d=from;d<=to;d+=step){
            rayEnds[i].x=(int) (sonarRange*Math.cos(d)+xCenter);
            rayEnds[i].y=(int) (sonarRange*Math.sin(d)+yCenter);
            //return center point of sensing samples
            i++;
        }
    }


    public ArrayList<IntPoint> getIntersectedPoints(ArrayList<Obstacle> detectedObs){
        ArrayList<IntPoint> intersectedPoints=null; //intersected points that rays have detected
             if(!detectedObs.isEmpty()){
                //new sensing radius
                int newSR=(int) getNearestObstacleDist(detectedObs);
                if(newSR<sensingRadius){
                    //make sensingradius less than nearest obstacle and give buffer region
                    sensingRadius=(int)(newSR-(newSR/10.0));
                    sensingRadiusCheck();
                    calculateSensingSamples();
                    calculateRayEnds();
                }
                else{
                    sensingRadius=calculateSensingRadius();
                }
                intersectedPoints=getRayIntersections(detectedObs);
            }
       return intersectedPoints;
    }

    public ArrayList<Obstacle> getDetectedObstacles(ArrayList<Obstacle> obs){
        ArrayList<Obstacle> detectedObs=new ArrayList<Obstacle>();    //obstacles in sonar range

        //store obstacles that can be seen by sonar in detectedObs
        if(obs!=null){
            for(Obstacle o : obs){
                if(o.didCollide(xCenter,yCenter,sonarRange)){
                    detectedObs.add(o);
                }
            }
        }
        return detectedObs;
    }

    //calculates all sensing sample potentials and returns point with the lowest one
    public int getBestSample(IntPoint goal, ArrayList<IntPoint> intersectedPoints, ArrayList<Obstacle> detectedObs,Robot rob){
        double currObstPot,currGoalPot, currTotalPot;   //current obstacle, goal, and total potential
        boolean collided=false; //if a sensing point collided with obstacle (to correct for double to int error)
        //index of minimum potential and minimum potential
        int minPotIndex=0;
        double minPot=0;


        //return index of sensing sample point with lowest potential
        for(int i=0;i<sensingSamples.length;i++){
            collided=false;
            currGoalPot=getGoalPotential(goal,sensingSamples[i],rob);
            //if no obstacles seen or no intersected points, there should be no obstacle potential
            if(intersectedPoints==null)
                currObstPot=0;
            else{
                currObstPot=0;
                for(IntPoint ip : intersectedPoints){
                    double potential=potential(sensingSamples[i].x,sensingSamples[i].y,ip.x,ip.y);
                    //return null to tell main that there has been an error
                    currObstPot+=potential;
                }
            }

            //total potential
            currTotalPot=currGoalPot+currObstPot;

            //add extra check in case the lowest potential is at a sensing point that would collide with a detected object (if moved there)
            //adjusts for errors caused by rounding from double to int
            for(Obstacle o : detectedObs){
                if(o.didCollide(sensingSamples[i].x,sensingSamples[i].y,robotSize)){
                    collided=true;
                    break;
                }
            }

            if(collided) continue;

            //if the current total is less than minimum, there is a new best sample
            if(currTotalPot<minPot){
                minPotIndex=i;
                minPot=currTotalPot;
            }
        }
        return minPotIndex;
    }


    //smooth path for new point and change robot location and angle
    public IntPoint smoothPath(int minPotIndex){
        //angle of best sensing sample ponts
        double bestAngle=smoothPathAngle(minPotIndex);
        //fraction of radius that should be applied to move (1 for straight, less for any other angle that is not straight)
        double radialFactor=getRadialFactor(bestAngle);

        //new point of robot
        IntPoint best=new IntPoint((int)(sensingRadius*radialFactor*Math.cos(bestAngle))+xCenter,(int)(sensingRadius*radialFactor*Math.sin(bestAngle))+yCenter);

      /*  //to ensure that robot keeps moving and doesn't get stuck in a turning loop
        if((best.x-xCenter==0)&&(best.y-yCenter==0)){

        }
*/
        //if angle changed
        if(angle!=bestAngle){
            numTurns++;
        }

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
        //should always move. thus it should never return 0
        return 1-(.5*(Math.abs(bestAngle-angle)/(Math.PI/2)));
    }

    //get goal potential; temporary placeholder equation
    public double getGoalPotential(IntPoint goal,IntPoint sensingSample,Robot rob){
        return -Math.sqrt(rob.diagonalDistance())/dist(goal.x,goal.y,sensingSample.x,sensingSample.y);
    }

    //distance from nearest obstacle
    public double getNearestObstacleDist(ArrayList<Obstacle> obstacles){
        double dist=sonarRange;
        for(Obstacle o : obstacles){
            double d=dist(o.getX(),o.getY(),xCenter,yCenter)-o.getRadius();//-sensingRadius;
            if(d<dist){
                dist=d;
            }
        }
        return dist;
    }

    //adapted from http://stackoverflow.com/questions/1073336/circle-line-segment-collision-detection-algorithm
    public IntPoint getCloserIntersection(IntPoint sensingSample,IntPoint rayEnd,Obstacle o){
        //length of ray
        double lengthSampleRay=(sonarRange-sensingRadius);

        //direction vectors of ray
        double rayXDir=(rayEnd.x-sensingSample.x)/lengthSampleRay;
        double rayYDir=(rayEnd.y-sensingSample.y)/lengthSampleRay;

        //ray equation is parametric, where x=xdir*t+sensingSample.x and y=ydir*t+sensingSample.y (0<=t<=1)

        //value t of closest point to the circle
        double closestT=rayXDir*(o.getX()- sensingSample.x)+rayYDir*(o.getY()-sensingSample.y);

        if(closestT<0){
            return null;
        }

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
                inter=getCloserIntersection(sensingSamples[i], rayEnds[i], o);
                if(inter!=null)
                    intersections.add(inter);
                i++;
            }
            i=0;
        }
        return intersections;
    }

    public void drawIntersectedPoints(EasyGui gui,ArrayList<IntPoint> intersections){
        for(IntPoint ip : intersections){
            RenderablePoint rp=new RenderablePoint(ip.x,ip.y);
            rp.setProperties(Color.PINK,5.0f);
            gui.draw(rp);
        }
    }

    public void drawRays(EasyGui gui){
        for(int i=0;i<sensingSamples.length;i++){
            gui.unDraw(rayPolylines[i]);
            rayPolylines[i]=new RenderablePolyline();
            rayPolylines[i].addPoint(sensingSamples[i].x,sensingSamples[i].y);
            rayPolylines[i].addPoint(rayEnds[i].x,rayEnds[i].y);
            rayPolylines[i].setProperties(Color.RED,1.0f);
            gui.draw(rayPolylines[i]);
        }
    }

    public void printIntersectedPoints(ArrayList<IntPoint> intersections){
        for(IntPoint ip: intersections){
            System.out.println(ip.x+" "+ip.y);
        }
    }

    //formula for potential between two points
    public double potential(int x1, int y1, int x2, int y2){
        double d=dist(x1,y1,x2,y2);
        if(d>=sonarRange) return 0;
        else if(d==0){
            //return the highest potential possible (a distance of 1 pixel away)
            double a=sonarRange-d;
            return Math.exp(-1/a);
        }
        else{
            double a=sonarRange-d;
            //return Math.exp(-1.0/a)/d;
            return Math.exp(-1.0/a)/Math.pow(d,2);
        }
    }

    public int calculateSensingRadius(){
        return (robotSize+sonarRange)/2;
    }

    public void sensingRadiusCheck(){
        if(sensingRadius<robotSize){
            sensingRadius=robotSize+1;
        }
    }

    public void drawDetectedObstacles(ArrayList<Obstacle> detectedObs,EasyGui gui){
        for(Obstacle o : detectedObs) {
            RenderableOval tmp = o.getRenderableOval();
            tmp.setProperties(Color.RED, 1.0f, false);
            gui.draw(tmp);
        }
    }

    public boolean atGoal(IntPoint goal){
        double d=dist(goal.x,goal.y,xCenter,yCenter);
        if(d<robotSize+sensingRadius){
            xCenter=goal.x;
            yCenter=goal.y;
            return true;
        }
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

    public int getSonarRange() {
        return sonarRange;
    }

    public void setSonarRange(int sonarRange) {
        this.sonarRange = sonarRange;
    }

    public int getNumTurns() {
        return numTurns;
    }

}
