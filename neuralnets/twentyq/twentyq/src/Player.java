//import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by aw246 on 4/15/15.
 */
public class Player {
    File qaMapping;
    File conceptMapping;
    BasicNetwork network;
    ArrayList<ArrayList<Double>> input;
    ArrayList<ArrayList<Double>> concepts;
    ArrayList<String> questions;
    ArrayList<String> conceptStrings;
    Player(BasicNetwork network,ArrayList<ArrayList<Double>> input, ArrayList<ArrayList<Double>> concepts, ArrayList<String> questions, ArrayList<String> conceptStrings, File qaMapping, File conceptMapping){
        this.network=network;
        this.input=input;
        this.concepts=concepts;
        this.questions=questions;
        this.conceptStrings=conceptStrings;
        this.qaMapping=qaMapping;
        this.conceptMapping=conceptMapping;
    }

    public void play() throws IOException {
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        String read;
        ArrayList<Double> userInput=new ArrayList<Double>();
        double userInputMLData[];
        boolean quit=false;

        while(!quit) {
        	userInput=new ArrayList<Double>();
            System.out.println("Please answer with yes, no, or quit.");
            for (int i = 0; i < questions.size(); i++) {
                System.out.println(questions.get(i));
                read = br.readLine();
                if (read.toLowerCase().equals("yes")) {
                    userInput.add(1.0);
                } else if (read.toLowerCase().equals("no")) {
                    userInput.add(0.0);
                } else if(read.toLowerCase().equals("quit")){
                    quit=true;
                    break;
                } else {
                    System.out.println("Bad input, please try again with yes, no, or quit.");
                    i--;
                    continue;
                }
            }
            if(quit){
                break;
            }
            userInputMLData=getArray(userInput);
            MLData in=new BasicMLData(userInputMLData);
            MLData out=network.compute(in);
            String outConcept=outputToString(out);
            if(outConcept==null){
            	System.err.println("Something went wrong in the neural net.");
            }
            else{
                System.out.println("I am guessing that you are thinking of a "+outputToString(out)+".");
            }
            System.out.println("Was my guess correct?");
            while(true){
                read=br.readLine();
                if(read.toLowerCase().equals("yes")){
                    System.out.println("I win!");
                    break;
                }
                else if(read.toLowerCase().equals("no")){
                    System.out.println("What is the concept that you were thinking of?");
                    read=br.readLine();
                    conceptStrings.add(read);
                    addConcept();
                    input.add(userInput);
                    train();
                    break;
                }
                else{
                    System.out.println("Please answer: Was my guess correct?");
                }
            }

        }

    }

    public ArrayList<Integer> outToIntegerArray(MLData out){
        ArrayList<Integer> binaryOutput=new ArrayList<Integer>();
        for(int i=0;i<out.size();i++){
            if(out.getData(i)>=0.5){
                binaryOutput.add(1);
            }
            else{
                binaryOutput.add(0);
            }
        }
        return binaryOutput;
    }

    public void addConcept(){
        int power=(int)Math.log(concepts.size()/Math.log(2))+1; //add one to account for potential new concepts
        String bitStr=Integer.toString(concepts.size(),2);  //will return next index array since max index is size-1
        ArrayList<Double> tmp=new ArrayList<Double>();
        //add padding 0s
        for(int i=0;i<power-bitStr.length();i++){
            tmp.add(0.0);
        }

        //add main bit string
        for(int i=0;i<bitStr.length();i++){
            if(bitStr.substring(i,i+1).equals("0")){
                tmp.add(0.0);
            }
            else if(bitStr.substring(i,i+1).equals("1")){
                tmp.add(1.0);
            }
            else{
                System.err.println("Bad call to Integer.toString");
            }
        }
        concepts.add(tmp);
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

    public void setupNetwork(int hiddenUnits){
        network=new BasicNetwork();
        network.addLayer(new BasicLayer(null,true,questions.size()));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,hiddenUnits));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false,concepts.get(0).size()));
        network.getStructure().finalizeStructure();
        network.reset();
    }


    public void train() {
        int hiddenUnits = network.getLayerNeuronCount(1); //construct new net with count one more than previous
        boolean hasError = true;
        MLDataSet trainingSet=null;

        while (hasError){
            setupNetwork(hiddenUnits);
            //create training data
            // create training data
            double in[][] = inputToArray(input);
            double out[][] = inputToArray(concepts);
            trainingSet = new BasicMLDataSet(in, out);

            // train the neural network
            Backpropagation train = new Backpropagation(network, trainingSet, .06, .1);

            int epoch = 1;

            do {
                train.iteration();
                System.out.println("Epoch #" + epoch + " Error:" + train.getError());
                epoch++;
           } while (train.getError() > .001 && epoch<500000);

            if(!hasClassificationError(network,trainingSet)){
                break;
            }
            else{            	
                hiddenUnits++;
            }

        }
        hasClassificationError(network,trainingSet);
    }

    public boolean hasClassificationError(BasicNetwork netw, MLDataSet trainSet){
        //current index, since training is done in numerical order
        int index=0;
        for(MLDataPair pair:trainSet){
            MLData output=netw.compute(pair.getInput());

            int currIndex=getDecimalOutput(outToIntegerArray(output));

             if(currIndex!=index++){
                return true;
            }

        }
        return false;
    }

    public String outputToString(MLData output){
        ArrayList<Integer> binaryOutput=outToIntegerArray(output);
        int out=getDecimalOutput(binaryOutput);
        if(out>=conceptStrings.size()){
        	return null;
        }
        return conceptStrings.get(out);
    }

    public int getDecimalOutput(ArrayList<Integer> binaryIn){
        double decimal=0;
        for(int i=0;i<binaryIn.size();i++){
            decimal+=binaryIn.get(i)*Math.pow(2.0,binaryIn.size()-1-i);
        }
        return (int) decimal;
    }


    public double[] getArray(ArrayList<Double> in){
        double[] out=new double[in.size()];
        for(int i=0;i<out.length;i++){
            out[i]=in.get(i);
        }
        return out;
    }


}
