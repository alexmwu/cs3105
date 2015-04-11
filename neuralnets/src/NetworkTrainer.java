import org.encog.ConsoleStatusReportable;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;
import org.encog.util.simple.EncogUtility;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;

/**
 * Created by aw246 on 01/04/15.
 */
public class NetworkTrainer {
    private String filePath;
    private File testingFile;
    VersatileMLDataSet data;
    BasicNetwork network;

    NetworkTrainer(String filePath){
        testingFile=new File(filePath);
        EncogCSVSetup();
    }

    public void CSVSetup(){
        BufferedReader br=null;
        String line;
        String csvSplit=",";
        ArrayList<ArrayList<Double>> input;
        double output;

        try {
            br = new BufferedReader(new FileReader(testingFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] testingData= line.split(csvSplit);


                //do data work here

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

    public void EncogCSVSetup(){
         try {
             //Define the data file format
             //false means no headers
             VersatileDataSource src = new CSVDataSource(testingFile, false, CSVFormat.DECIMAL_POINT);
             data = new VersatileMLDataSet(src);

             //not sure if continuous
             data.defineSourceColumn("isOrganic", 0, ColumnType.nominal);
             data.defineSourceColumn("isUsed", 1, ColumnType.nominal);
             data.defineSourceColumn("isElectronic", 2, ColumnType.nominal);
             data.defineSourceColumn("isAPlace", 3, ColumnType.nominal);
             data.defineSourceColumn("isOnEarth", 4, ColumnType.nominal);
             data.defineSourceColumn("isMammal", 5, ColumnType.nominal);
             data.defineSourceColumn("canFly", 6, ColumnType.nominal);
             data.defineSourceColumn("walksOnTwoLegs", 7, ColumnType.nominal);

             //define column trying to predict
             ColumnDefinition outputCol=data.defineSourceColumn("concept",8,ColumnType.nominal);

             //analyze data
             data.analyze();

             //map prediction column to output of model and all other columns to the input
             data.defineSingleOutputOthersInput(outputCol);
            System.out.println(data.getData());
         }
        catch (Exception e){
            e.printStackTrace();
        }
    }



    public void backPropagationTrain(){
        BasicNetwork network=new BasicNetwork();
        network.addLayer(new BasicLayer(null,true,9));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
//        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false,1));
        network.getStructure().finalizeStructure();
        network.reset();

        // create training data
        MLDataSet trainingSet = data;

        System.out.println(data.getInputSize());
        System.out.println(data.getData());
        // train the neural network
   //     Backpropagation train = new Backpropagation(network, data, .5,.3);
/*
        int epoch = 1;

        do {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while(train.getError() > 0.01);
        train.finishTraining();

        // test the neural network
        System.out.println("Neural Network Results:");
        for(MLDataPair pair: trainingSet ) {
            final MLData output = network.compute(pair.getInput());
            System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1)
                    + ", actual=" + output.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
        }

        Encog.getInstance().shutdown();
*/
    }

    public void crossValidateTrain(){

            //create feedforward neural network as model type.
            EncogModel model=new EncogModel(data);
            model.selectMethod(data, MLMethodFactory.TYPE_FEEDFORWARD);

            //send output to console
            model.setReport(new ConsoleStatusReportable());

            //normalize data (Encog determines correct normalization type based on the model chosen)
            data.normalize();

            /***************************************************/
            //hold back data for final validation, shuffle the data into random ordering, use seed
            model.holdBackValidation(0.1,true,1001);

            //choose default training type for the model
            model.selectTrainingType(data);

            // Use a 5-fold cross-validated train.  Return the best method found.
            MLRegression bestMethod = (MLRegression)model.crossvalidate(5, true);

            // Display the training and validation errors.
            System.out.println( "Training error: " + EncogUtility.calculateRegressionError(bestMethod, model.getTrainingDataset()));
            System.out.println( "Validation error: " + EncogUtility.calculateRegressionError(bestMethod, model.getValidationDataset()));

            // Display our normalization parameters.
            NormalizationHelper helper = data.getNormHelper();
            System.out.println(helper.toString());

            // Display the final model.
            System.out.println("Final model: " + bestMethod);

            crossValidateVerify(helper,bestMethod);

            //shut down
            Encog.getInstance().shutdown();
   }

    public void crossValidateVerify(NormalizationHelper helper, MLRegression bestMethod){
        // Loop over the entire, original, dataset and feed it through the model.
        // This also shows how you would process new data, that was not part of your
        // training set.  You do not need to retrain, simply use the NormalizationHelper
        // class.  After you train, you can save the NormalizationHelper to later
        // normalize and denormalize your data.
        ReadCSV csv = new ReadCSV(testingFile, false, CSVFormat.DECIMAL_POINT);
        String[] line = new String[8];
        MLData input = helper.allocateInputVector();

        while(csv.next()) {
            StringBuilder result = new StringBuilder();
            line[0] = csv.get(0);
            line[1] = csv.get(1);
            line[2] = csv.get(2);
            line[3] = csv.get(3);
            line[4] = csv.get(4);
            line[5] = csv.get(5);
            line[6] = csv.get(6);
            line[7] = csv.get(7);
            String correct = csv.get(8);
            helper.normalizeInputVector(line,input.getData(),false);
            MLData output = bestMethod.compute(input);
            String irisChosen = helper.denormalizeOutputVectorToString(output)[0];

            result.append(Arrays.toString(line));
            result.append(" -> predicted: ");
            result.append(irisChosen);
            result.append("(correct: ");
            result.append(correct);
            result.append(")");

            System.out.println(result.toString());
        }
    }

    public static void main(String[] args){
        if(args.length!=1){
            System.out.println("Usage: NetworkTrainer filePath");
            System.exit(0);
        }
        NetworkTrainer q20 = new NetworkTrainer(args[0]);
        q20.backPropagationTrain();
//        q20.crossValidateTrain();
    }

}
