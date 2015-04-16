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
import java.util.function.BinaryOperator;

/**
 * Created by aw246 on 01/04/15.
 */
public class FileTrainer {
    public File getQaMapping() {
        return qaMapping;
    }

    private File qaMapping;

    public File getConceptMapping() {
        return conceptMapping;
    }

    private File conceptMapping;
    BasicNetwork network;

    public ArrayList<ArrayList<Double>> getInput() {
        return input;
    }

    ArrayList<ArrayList<Double>> input;

    public ArrayList<ArrayList<Double>> getConcepts() {
        return concepts;
    }

    ArrayList<ArrayList<Double>> concepts;

    public ArrayList<Integer> getConceptInts() {
        return conceptInts;
    }

    ArrayList<Integer> conceptInts; //integer input of concepts

    public ArrayList<String> getQuestions() {
        return questions;
    }

    ArrayList<String> questions;

    public ArrayList<String> getConceptStrings() {
        return conceptStrings;
    }

    ArrayList<String> conceptStrings;

    FileTrainer(String qaMappingPath,String conceptMappingPath){
        qaMapping=new File(qaMappingPath);
        conceptMapping=new File(conceptMappingPath);
        qaSetup();
        conceptSetup();
        setupNetwork();
    }

    public void conceptSetup(){
        BufferedReader br=null;
        String line;
        String csvSplit=",";
        conceptStrings=new ArrayList<String>();
        try{
            br=new BufferedReader(new FileReader(conceptMapping));
            while((line=br.readLine())!=null){
                String[] conceptMap=line.split(csvSplit);
                conceptStrings.add(conceptMap[1]);
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
            //after all input is read, convert integer concept mapping to binary
            toBinaryArray();
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
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,4));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false,concepts.get(0).size()));
        network.getStructure().finalizeStructure();
        network.reset();
    }

    //converts the arraylist of integers to arraylist of arraylist of doubles (binary 1.0 or 0.0)
    public void toBinaryArray(){
        concepts=new ArrayList<ArrayList<Double>>();
//        System.out.println("length: "+conceptInts.size()+"; "+conceptInts);
        int power=(int)(Math.log(conceptInts.size())/Math.log(2))+1;    //add one to account for potential new concept
        for(int i=0;i<conceptInts.size();i++){
            String bitStr=Integer.toString(conceptInts.get(i),2);
            ArrayList<Double> tmp=new ArrayList<Double>();
            for(int j=0;j<power-bitStr.length();j++){
      //          System.out.print("0");
                tmp.add(0.0);
            }
            for(int j=0;j<bitStr.length();j++){
       //         System.out.print(bitStr.charAt(j));
                if(bitStr.substring(j,j+1).equals("0")){
                    tmp.add(0.0);
                }
                else if(bitStr.substring(j,j+1).equals("1")){
                    tmp.add(1.0);
                }
                else{
                    System.err.println("Bad call to Integer.toString");
                }
            }
            concepts.add(tmp);
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

    public void verify(BasicNetwork netw, MLDataSet trainingSet){
        for(MLDataPair pair: trainingSet ) {
            final MLData output = netw.compute(pair.getInput());
            System.out.print("input= ");
            for(int i=0;i<pair.getInput().size();i++){
                System.out.print(pair.getInput().getData(i));
                if(i==pair.getInput().size()-1){
                    System.out.print("; ");
                }
                else{
                    System.out.print(", ");
                }
            }
            System.out.print("actual= ");
            ArrayList<Integer> binaryOutput=new ArrayList<Integer>();
            for(int i=0;i<output.size();i++){
//                System.out.print(output.getData(i));
                if(output.getData(i)>=0.5){
                    System.out.print(1);
                    binaryOutput.add(1);
                }
                else{
                    System.out.print(0);
                    binaryOutput.add(0);
                }
                if(i==output.size()-1){
                    System.out.print("; ");
                }
                else{
                    System.out.print(", ");
                }
            }
            int out=getBinaryOutput(binaryOutput);
            System.out.print(" actual to decimal: "+out+"; ");
            System.out.print("ideal= ");
            for(int i=0;i<pair.getIdeal().size();i++){
                System.out.print(pair.getIdeal().getData(i));
                if(i==pair.getIdeal().size()-1){
                    System.out.println();
                }
                else{
                    System.out.print(", ");
                }
            }
        }

    }

    public int getBinaryOutput(ArrayList<Integer> binaryIn){
        double decimal=0;
        for(int i=0;i<binaryIn.size();i++){
            decimal+=binaryIn.get(i)*Math.pow(2.0,binaryIn.size()-1-i);
        }
        return (int) decimal;
    }

    //print questions and concepts
    public void printQC(){
        System.out.println("Questions: "+questions+"\n");
        System.out.println("Inputs (question answer mapping indexed by concept): "+input+"\n");
        System.out.println("Concept mapping to binary: "+concepts+"\n");
    }

    public void train() {
        // create training data
        double in[][] = inputToArray(input);
        double out[][] = inputToArray(concepts);
        MLDataSet trainingSet = new BasicMLDataSet(in, out);

        // train the neural network
        Backpropagation train = new Backpropagation(network, trainingSet, .06, .1);

        /*
        EncogUtility.trainToError(network, trainingSet, 0.01);

        EncogUtility.evaluate(method,trainingSet);
*/
        int epoch = 1;

        do {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while (train.getError() > .001);
        train.finishTraining();

        //test the neural network
        System.out.println("Neural Network Results:");
       /* for(MLDataPair pair: trainingSet ) {
            final MLData output = network.compute(pair.getInput());
            System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
                    + ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
        }*/

        verify(network, trainingSet);

        Encog.getInstance().shutdown();
    }

    public static void main(String[] args) throws IOException{
        if(args.length!=2){
            System.out.println("Usage: NetworkTrainer QAMappingFilePath ConceptMappingFilePath");
            System.exit(0);
        }
        FileTrainer q20 = new FileTrainer(args[0],args[1]);
        q20.printQC();
        q20.train();

        Player pl=new Player(q20.getNetwork(),q20.getInput(),q20.getConcepts(),q20.getQuestions(),q20.getConceptStrings(),q20.getQaMapping(),q20.getConceptMapping());
        pl.play();
    }

}
