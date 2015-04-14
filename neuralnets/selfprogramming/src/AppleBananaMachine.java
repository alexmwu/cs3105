import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.CalculateScore;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
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

import java.util.ArrayList;

/**
 * Created by aw246 on 4/14/15.
 */
public class AppleBananaMachine {
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

    //found in Encog github examples
    public static double trainNetwork(BasicNetwork network,MLDataSet trainingSet){
        // train the neural network
        CalculateScore score = new TrainingSetScore(trainingSet);

        final MLTrain trainAlt = new NeuralSimulatedAnnealing(
                network, score, 10, 2, 100);

        final MLTrain trainMain = new Backpropagation(network, trainingSet,.5, 0.05);

        final StopTrainingStrategy stop = new StopTrainingStrategy();

        //add a greedy strategy (if no improvement in iteration, discard)
        trainMain.addStrategy(new Greedy());
        //secondary algorithm in case greedy isn't working
        trainMain.addStrategy(new HybridStrategy(trainAlt));
        //or stop
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

    public static String TOURINPUT="001011001110000100011001001101010001100101111011";
    public static String TOUROUTPUT="000000000000000100010100000000100010001001010001001001110001001100110011011000010011100011001100";

    public double[][] parseString(String in, int power){
        double out[][]=new double[(int)(in.length()/power)][power];

        for(int i=0;i<(int)(in.length()/power);i++){
            int index=i*power;
            for(int j=0;j<power;j++){
                System.out.println(in.substring(index + j, index + j + 1));
                out[i][j]=Double.parseDouble(in.substring(index+j,index+j+1));
            }
        }

        return out;
    }

    public ArrayList<ArrayList<Double>> parseStringAL(String in, int power){
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

    public static void main(final String args[]) {
        AppleMachine lm=new AppleMachine();
        System.out.println(lm.parseStringAL(TOURINPUT, 2));
        MLDataSet trainingSet = new BasicMLDataSet(lm.parseString(TOURINPUT,2),lm.parseString(TOUROUTPUT,4));

        BasicNetwork elmanNetwork = AppleMachine.createElmanNetwork(2, 8, 4);

        double elmanError = AppleMachine.trainNetwork(elmanNetwork, trainingSet);

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

        System.out.println("Best error rate with Elman Network: " + elmanError);
        System.out
                .println("Elman should be able to get into the 10% range,\nfeedforward should not go below 25%.\nThe recurrent Elment net can learn better in this case.");
        System.out
                .println("If your results are not as good, try rerunning, or perhaps training longer.");

        Encog.getInstance().shutdown();
    }
}
