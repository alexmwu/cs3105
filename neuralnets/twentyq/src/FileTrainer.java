import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by aw246 on 01/04/15.
 */
public class FileTrainer {
    private String filePath;
    private File testingFile;
    BasicNetwork network;
    ArrayList<ArrayList<Double>> input;
    ArrayList<ArrayList<Double>> concepts;
    ArrayList<String> questions;

    FileTrainer(String filePath){
        testingFile=new File(filePath);
        dataSetup();
        //make sure concept mapping between 0 and 1
        normalizeConcepts();
        setupNetwork();
    }

    public void dataSetup(){
        BufferedReader br=null;
        String line;
        String csvSplit=",";
        input=new ArrayList<ArrayList<Double>>();
        ArrayList<Double> singConc;  //one arraylist of concepts
        questions=new ArrayList<String>();
        concepts=new ArrayList<ArrayList<Double>>();

        try {
            br = new BufferedReader(new FileReader(testingFile));
            //read out questions
            while ((line = br.readLine()) != null) {
                if(line.trim().isEmpty()){
                    break;
                }
                questions.add(line.trim());
            }

            //read question answer to concept mapping
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] testingData= line.split(csvSplit);

                ArrayList<Double> tmp=new ArrayList<Double>();
                for(int i=0;i<testingData.length;i++){
                    if(i==testingData.length-1){
                        singConc=new ArrayList<Double>();
                        singConc.add(Double.parseDouble(testingData[i]));
                        concepts.add(singConc);
                    }
                    else {
                        tmp.add(Double.parseDouble(testingData[i]));
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
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false,1));
        network.getStructure().finalizeStructure();
        network.reset();
    }

    //normalize the concepts between 0 and 1
    public void normalizeConcepts(){
        for(int i=0;i<concepts.size();i++){
            ArrayList<Double> tmp=concepts.get(i);
            tmp.set(0,tmp.get(0)/(concepts.size()-1));
            concepts.set(i,tmp);
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
        if(args.length!=1){
            System.out.println("Usage: NetworkTrainer filePath");
            System.exit(0);
        }
        FileTrainer q20 = new FileTrainer(args[0]);
        q20.printQC();
        q20.train();
    }

}
