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

/**
 * Created by aw246 on 11/04/15.
 */
public class HelloRecurrent {
    BasicNetwork network;

    HelloRecurrent(){

    }


    public static BasicNetwork createElmanNetwork(){
        // construct an Elman type network
        ElmanPattern pattern = new ElmanPattern();
        pattern.setActivationFunction(new ActivationSigmoid());
        pattern.setInputNeurons(2);
        pattern.addHiddenLayer(2);
        pattern.setOutputNeurons(1);
        return (BasicNetwork)pattern.generate();
    }

    //found in Encog github examples
    public static double trainNetwork(BasicNetwork network,MLDataSet trainingSet){
        // train the neural network
        CalculateScore score = new TrainingSetScore(trainingSet);

        final MLTrain trainAlt = new NeuralSimulatedAnnealing(
                network, score, 10, 2, 100);

        final MLTrain trainMain = new Backpropagation(network, trainingSet,0.000001, 0.0);

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
    //alternate AND and XOR
    public static double INPUT[][]={{0.0,1.0},{0.0,1.0},{0.0,1.0},{0.0,1.0},{0.0,1.0},{0.0,1.0},{0.0,1.0},{0.0,1.0}};
    public static double IDEAL[][]={{0.0},{1.0},{0.0},{1.0},{0.0},{1.0},{0.0},{1.0}};

    public static void main(final String args[]) {

        MLDataSet trainingSet = new BasicMLDataSet(INPUT,IDEAL);

        BasicNetwork elmanNetwork = HelloRecurrent.createElmanNetwork();

        double elmanError = HelloRecurrent.trainNetwork(elmanNetwork, trainingSet);

        for(MLDataPair pair: trainingSet ) {
            final MLData output = elmanNetwork.compute(pair.getInput());
            System.out.println(pair.getInput().getData(0)  + ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
        }

        System.out.println("Best error rate with Elman Network: " + elmanError);
        System.out
                .println("Elman should be able to get into the 10% range,\nfeedforward should not go below 25%.\nThe recurrent Elment net can learn better in this case.");
        System.out
                .println("If your results are not as good, try rerunning, or perhaps training longer.");

        Encog.getInstance().shutdown();
    }
}
