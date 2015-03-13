package skynet;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by AWU on 3/10/2015.
 */
public class CourseManager{

    private String courseFileName;
    private File courseFile;
    //all preloaded courses, plus any courses added using addCourse method
    private ArrayList<ArrayList<Obstacle>> courses;

    //current course being used
    private ArrayList<Obstacle> currentCourse;

    //if -1, then the current course is not in preloaded course list
    private int currentCourseIndex;

    CourseManager(String s){
        courseFileName=s;
        courseFile= new File(s);

        courses=new ArrayList<ArrayList<Obstacle>>();
        currentCourseIndex=0;
    }

    public boolean checkFile(){
        if(!courseFile.exists() || courseFile.isDirectory()){
            return false;
        }
        return true;
    }

    public void readCoursesFromFile() throws IOException {
        BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(courseFile)));

        //make new course list
        courses=new ArrayList<ArrayList<Obstacle>>();
        //line to be read in
        String line;
        //if just started
        boolean started=true;
        while((line=br.readLine())!=null){
            ArrayList<Obstacle> tmp=new ArrayList<Obstacle>();
            if(line=="course"){
                if(started){
                    started=!started;
                    continue;
                }
                else{
                    courses.add(tmp);
                }
            }
            else{
                String[] tokens=line.split(" ");
                //if obstacle is circle
                if(tokens[0]=="c"){
                    tmp.add(new Obstacle(Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]),Integer.parseInt(tokens[3])));
                }

            }
        }
    }

    public void addCourse(ArrayList<Obstacle> course){
        courses.add(course);
    }

    /*public void writeCourseToFile(ArrayList<Obstacle> course){
        PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
        writer.println("The first line");
        writer.println("The second line");
        writer.close();
    }*/

    public void cycleCourseIndex(){
        currentCourseIndex++;
        if (currentCourseIndex >= courses.size()) {
            currentCourseIndex=0;
        }
    }

    public void setCourseFile(String fileName){
        this.courseFile=new File(fileName);
    }

    public void setCourseFileName(String fileName){
        this.courseFileName=fileName;
    }

    public ArrayList<Obstacle> getCourse(int i){
        return courses.get(i);
    }

    public void setCurrentCourse(ArrayList<Obstacle> currentCourse) {
        this.currentCourse = currentCourse;
    }

    public String getCourseFileName() {
        return courseFileName;
    }

}
