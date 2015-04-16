
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.CalculateScore;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Greedy;
import org.encog.ml.train.strategy.HybridStrategy;
import org.encog.ml.train.strategy.StopTrainingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.JordanPattern;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by aw246 on 4/14/15.
 */
public class AppleMachine {
    BasicNetwork network;

    public static BasicNetwork createElmanNetwork(int in,int layers,int out){
        // construct an Elman type network
        ElmanPattern pattern = new ElmanPattern();
        pattern.setActivationFunction(new ActivationSigmoid());
        pattern.setInputNeurons(in);
        pattern.addHiddenLayer(layers);
        pattern.setOutputNeurons(out);
        return (BasicNetwork)pattern.generate();
    }

    public static BasicNetwork createJordanNetwork(int in,int layers, int out){
        JordanPattern pattern=new JordanPattern();
        pattern.setActivationFunction(new ActivationSigmoid());
        pattern.setInputNeurons(in);
        pattern.addHiddenLayer(layers);
        pattern.setOutputNeurons(out);
        return (BasicNetwork)pattern.generate();
    }

    //train network given certain strategies
    public static double trainNetworkStrategies(BasicNetwork network,MLDataSet trainingSet){
            // train the neural network
            CalculateScore score = new TrainingSetScore(trainingSet);
            final MLTrain trainAlt = new NeuralSimulatedAnnealing(
                    network, score, 10, 2, 100);

            final MLTrain trainMain = new Backpropagation(network, trainingSet,0.0000001, 0.0);

            final StopTrainingStrategy stop = new StopTrainingStrategy();
            trainMain.addStrategy(new Greedy());
            trainMain.addStrategy(new HybridStrategy(trainAlt));
            trainMain.addStrategy(stop);

            int epoch = 0;
            while (!stop.shouldStop()) {
                trainMain.iteration();
                System.out.println("Epoch #" + epoch
                        + " Error:" + trainMain.getError());
                epoch++;
            }
            return trainMain.getError();
    }

    //train network until it meets error thresholds
    public static double trainNetworkThreshold(BasicNetwork network,MLDataSet trainingSet){
        // train the neural network
        CalculateScore score = new TrainingSetScore(trainingSet);

        final MLTrain trainAlt = new NeuralSimulatedAnnealing(
                network, score, 10, 2, 100);

        final MLTrain trainMain = new Backpropagation(network, trainingSet,0.006, .1);

        int epoch = 0;
        do{
            trainMain.iteration();
            System.out.println("Epoch #" + epoch
                    + " Error:" + trainMain.getError());
            epoch++;
        }while(trainMain.getError()>0.01);
        return trainMain.getError();
    }

    //hard coded state machine with loops
    public static String LOOPINPUT="1000100000011000010001100110";
    public static String LOOPOUTPUT="000001001010010010100001011011011101010100";
    //hard coded state machine without loops
    public static String LOOPLESSINPUT="0000100001100110";
    public static String LOOPLESSOUTPUT="000001011000010100001011";

    public static double[][] parseString(String in, int power){
        double out[][]=new double[(int)(in.length()/power)][power];

        for(int i=0;i<(int)(in.length()/power);i++){
            int index=i*power;
            for(int j=0;j<power;j++){
                 out[i][j]=Double.parseDouble(in.substring(index+j,index+j+1));
            }
        }

        return out;
    }

    public static ArrayList<ArrayList<Double>> parseStringAL(String in, int power){
        ArrayList<ArrayList<Double>> out=new ArrayList<ArrayList<Double>>();
        ArrayList<Double> tmp=new ArrayList<Double>();

        for(int i=0;i<(int)(in.length()/power);i++){
            int index=i*power;

            tmp=new ArrayList<Double>();
            for(int j=0;j<power;j++){
                tmp.add(Double.parseDouble(in.substring(index+j,index+j+1)));
            }
            out.add(tmp);
        }

        return out;
    }

    public String toString(double[][] out){
        String outString=new String();
        System.out.println(out.length);
        for(int i=0;i<out.length;i++){
            System.out.println(out[i].length);
            for(int j=0;i<out[i].length;i++){
                System.out.println(out[i][j]);
                outString+=out[i][j]+", ";
            }
        }
        return outString;
    }

    public static void verify(BasicNetwork elmanNetwork, MLDataSet trainingSet){
         for(MLDataPair pair: trainingSet ) {
            final MLData output = elmanNetwork.compute(pair.getInput());
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
            for(int i=0;i<output.size();i++){
//                System.out.print(output.getData(i));
                if(output.getData(i)>=0.5){
                    System.out.print(1);
                }
                else{
                    System.out.print(0);
                }
                if(i==output.size()-1){
                    System.out.print("; ");
                }
                else{
                    System.out.print(", ");
                }
            }
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

    //accept user input after vending
    public static void vend(BasicNetwork elm) throws IOException {
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        String readIn;
        double input[]=new double[2];
        while(true){
            System.out.println("Please enter an input: 10P, 20P, Apple, or Quit.");
            readIn=br.readLine();
            if(readIn.toLowerCase().equals("apple")){
                input[0]=1;
                input[1]=0;
            }
            else if(readIn.toLowerCase().equals("10p")){
                input[0]=0;
                input[1]=0;
            }
            else if(readIn.toLowerCase().equals("20p")){
                input[0]=0;
                input[1]=1;
            }
            else if(readIn.toLowerCase().equals("quit")){
                break;
            }
            else{
                System.out.println("Bad input.");
                continue;
            }
            MLData in=new BasicMLData(input);
            MLData out=elm.compute(in);

            System.out.println(outputToString(out));
        }
    }

    public static String outputToString(MLData output){
        int out=output.getData()[0]>.5?1:0;
        if(out==0){
            out=output.getData()[1]>.5?1:0;
            if(out==0){
                out=output.getData()[2]>.5?1:0;
                if(out==0){
                    return "10P in the machine.";
                }
                else{
                    return "20P in the machine.";
                }
            }
            else{
                out=output.getData()[2]>.5?1:0;
                if(out==0){
                    return "30P in the machine";
                }
                else{
                    return "Here's an apple (no change).";
                }
            }
        }
        else{
            out=output.getData()[1]>.5?1:0;
            if(out==0){
                out=output.getData()[2]>.5?1:0;
                if(out==0){
                    return "Here's an apple and 10P change.";
                }
                else{
                    return "Bad output (error code 101).";
                }
            }
            else{
                out=output.getData()[2]>.5?1:0;
                if(out==0){
                    return "Bad output (error code 110).";
                }
                else{
                    return "Bad output (error code 111).";
                }
            }
        }
    }


    public static void main(final String args[]) throws IOException {

//        MLDataSet trainingSet = new BasicMLDataSet(AppleMachine.parseString(LOOPLESSINPUT,2),AppleMachine.parseString(LOOPLESSOUTPUT,3));

       MLDataSet trainingSet = new BasicMLDataSet(AppleMachine.parseString(LOOPLESSINPUT,2),AppleMachine.parseString(LOOPLESSOUTPUT,3));

       BasicNetwork elmanNetwork = AppleMachine.createElmanNetwork(2, 4, 3);

        double elmanError = AppleMachine.trainNetworkStrategies(elmanNetwork, trainingSet);

        AppleMachine.verify(elmanNetwork, trainingSet);
        AppleMachine.verify(elmanNetwork, trainingSet);
        AppleMachine.verify(elmanNetwork, trainingSet);

        System.out.println("Best error rate with Elman Network: " + elmanError);
        AppleMachine.vend(elmanNetwork);
       
/*
        BasicNetwork jordanNetwork=AppleMachine.createJordanNetwork(2,5,3);

        double jordanError=AppleMachine.trainNetworkStrategies(jordanNetwork,trainingSet);

        System.out.println("Best error rate with Jordan Network: "+jordanError);
        AppleMachine.verify(jordanNetwork, trainingSet);
        AppleMachine.vend(jordanNetwork);
*/
        Encog.getInstance().shutdown();
    }
}