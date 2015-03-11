package skynet;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by AWU on 3/10/2015.
 */
public class CourseManager {
    private String courseFileName;
    //all preloaded courses, plus any courses added using addCourse method
    private ArrayList<ArrayList<Obstacle>> courses;

    //current course being used
    private ArrayList<Obstacle> currentCourse;

    //if -1, then the current course is not in preloaded course list
    private int currentCourseIndex;

    CourseManager(String s){
        courseFileName=s;
    }

    public void readCoursesFromFile() throws FileNotFoundException {
        BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(courseFileName)));
    }

    public void addCourse(ArrayList<Obstacle> course){

    }

    public void writeCourseToFile(ArrayList<Obstacle> course){

    }

    public ArrayList<Obstacle> getCourse(int i){
        return courses.get(i);
    }

    public void setCurrentCourse(ArrayList<Obstacle> currentCourse) {
        this.currentCourse = currentCourse;
    }
}
