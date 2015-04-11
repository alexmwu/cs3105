import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.CalculateScore;
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
public class LearningMachine {
    BasicNetwork network;

    LearningMachine(){

    }

    public static BasicNetwork createElmanNetwork(){
        // construct an Elman type network
        ElmanPattern pattern = new ElmanPattern();
        pattern.setActivationFunction(new ActivationSigmoid());
        pattern.setInputNeurons(3);
        pattern.addHiddenLayer(3);
        pattern.setOutputNeurons(3);
        return (BasicNetwork)pattern.generate();
    }

    //found in Encog github examples
    public double trainNetwork(BasicNetwork network,MLDataset trainingSet){
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

}
