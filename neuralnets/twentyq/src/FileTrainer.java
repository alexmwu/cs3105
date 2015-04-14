import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.util.simple.EncogUtility;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by aw246 on 01/04/15.
 */
public class FileTrainer {
    private File qaMapping;
    private File conceptMapping;
    BasicNetwork network;
    ArrayList<ArrayList<Double>> input;
    ArrayList<ArrayList<Double>> concepts;
    ArrayList<Integer> conceptInts; //integer input of concepts
    ArrayList<String> questions;

    FileTrainer(String qaMappingPath,String conceptMappingPath){
        qaMapping=new File(qaMappingPath);
        conceptMapping=new File(conceptMappingPath);
        qaSetup();
        conceptSetup();
        setupNetwork();
    }

    public void conceptSetup(){

    }

    public void qaSetup(){
        BufferedReader br=null;
        String line;
        String csvSplit=",";
        input=new ArrayList<ArrayList<Double>>();
        ArrayList<Double> singConc;  //one arraylist of concepts
        questions=new ArrayList<String>();
        conceptInts=new ArrayList<Integer>();

        try {
            br = new BufferedReader(new FileReader(qaMapping));
            //read out questions
            line=br.readLine();
            if(br!=null){
                String[] qaQuestions=line.split(csvSplit);
                for(int i=1;i<qaQuestions.length;i++){
                    questions.add(qaQuestions[i]);
                }
            }
            else{
                System.err.println("The file cannot be read.");
                System.exit(1);
            }

            //read question answer to concept mapping
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] mappedData= line.split(csvSplit);

                ArrayList<Double> tmp=new ArrayList<Double>();
                for(int i=0;i<mappedData.length;i++){
                    //for the concept neural net mapping
                    if(i==0){
                        conceptInts.add(Integer.parseInt(mappedData[i]));
                    }
                    //add neural net mapping
                    else {
                        if(mappedData[i].toLowerCase().equals("yes")){
                            tmp.add(1.0);
                        }
                        else if(mappedData[i].toLowerCase().equals("no")){
                            tmp.add(0.0);
                        }
                        else{
                            System.err.println("The file is incorrectly formatted (separate questions answers with yes or no).");
                            System.exit(1);
                        }
                    }
                }
                input.add(tmp);
           }
       } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public BasicNetwork getNetwork(){
        return this.network;
    }

    public void setupNetwork(){
        network=new BasicNetwork();
        network.addLayer(new BasicLayer(null,true,questions.size()));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false,concepts.size()));
        network.getStructure().finalizeStructure();
        network.reset();
    }

    //converts the arraylist of integers to arraylist of arraylist of doubles (binary 1.0 or 0.0)
    public void toBinaryArray(){
        int power=(int)(Math.log(conceptInts.size())/Math.log(2))+1;    //add one to account for potential new concepts
        for(int i=0;i<conceptInts.size();i++){

        }
    }

    //input arraylist of arraylist to an array
    public double[][] inputToArray(ArrayList<ArrayList<Double>> inp){
        double[][] arr=new double[inp.size()][inp.get(0).size()];
        for(int i=0;i<inp.size();i++){
            for(int j=0;j<inp.get(i).size();j++){
                arr[i][j]=inp.get(i).get(j);
            }
        }
        return arr;
    }

    //print questions and concepts
    public void printQC(){
        System.out.println(questions+"\n");
        System.out.println(input+"\n");
        System.out.println(concepts+"\n");
    }

    public void train() {
        // create training data
        double in[][] = inputToArray(input);
        double out[][] = inputToArray(concepts);
        MLDataSet trainingSet = new BasicMLDataSet(in, out);

        // train the neural network
        Backpropagation train = new Backpropagation(network, trainingSet, .5, .3);

        /*
        EncogUtility.trainToError(network, trainingSet, 0.01);

        EncogUtility.evaluate(method,trainingSet);
*/
        int epoch = 1;

        do {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while (train.getError() > .01);
        train.finishTraining();

        //test the neural network
        System.out.println("Neural Network Results:");
        for(MLDataPair pair: trainingSet ) {
            final MLData output = network.compute(pair.getInput());
            System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
                    + ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
        }

        Encog.getInstance().shutdown();
    }

    public static void main(String[] args){
        if(args.length!=2){
            System.out.println("Usage: NetworkTrainer QAMappingFilePath ConceptMappingFilePath");
            System.exit(0);
        }
        FileTrainer q20 = new FileTrainer(args[0],args[1]);
        q20.printQC();
        q20.train();
    }

}
