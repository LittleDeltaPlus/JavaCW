package sample;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import static org.apache.commons.math3.util.FastMath.log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.File;



public class stats {
    private WeightedObservedPoints histogram = new WeightedObservedPoints();
    private NormalDistribution normal;
    private DescriptiveStatistics statistics = new DescriptiveStatistics();
    private double alpha, mu, sigma;
    private int classWidth;
    private boolean loaded = false;

    void LoadFile(File inputFile) {
        statistics.clear();
        histogram.clear();
        ArrayList<Double> data = new ArrayList<>();
        //Try to read the supplied file
        try {
            List<String> tmp = Files.readAllLines(Paths.get(inputFile.getPath()));
            tmp.forEach((i)->data.add(Double.parseDouble(i)));
        } catch(IOException io) {
            io.printStackTrace();
        }
        //Multiply every value so that the scale is more legible
        data.forEach((i)->statistics.addValue(i*1000000));
        double[] inputData = statistics.getValues();
        //Calculate bin width and fill the bins
        classWidth = (int) ((int) (statistics.getMax()-statistics.getMin())/(2.2*log(data.size()+1)));
        int[] binCounts = calcHistogram(inputData,statistics.getMin(), statistics.getMax(), (int) (2.2*log(data.size()+1)), classWidth);
        //normalise the data and add it to the histogram
        for (int i = 0; i< binCounts.length; i++) {
            histogram.add(statistics.getMin()+i*((double)classWidth)+((double) classWidth/2), (((double) binCounts[i]*((double) binCounts[i]))/(10*statistics.getN()))*classWidth);
        }
        //Use the commons fitter to get fitting parameters
        double[] parameters = GaussianCurveFitter.create().fit(histogram.toList());
        alpha = parameters[0];
        mu = parameters[1];
        sigma = parameters[2];
        normal = new NormalDistribution(mu,sigma);
        //signify that data has been loaded
        loaded = true;
    }
    //Bin sorting algorithm from user: Mohamed Taher Alrefaie,  https://stackoverflow.com/a/10786652
    private static int[] calcHistogram(double[] data, double min, double max, int numBins, double classWidth) {
        final int[] result = new int[numBins];

        for (double d : data) {
            int bin = (int) ((d - min) / classWidth);
            if (bin < 0) { /* this data is smaller than min */ }
            else if (bin >= numBins) { /* this data point is bigger than max */ }
            else {
                result[bin] += 1;
            }
        }
        return result;
    }
    double getMedian() {
        return statistics.getPercentile(50);
    }
    double getSD() {
        return statistics.getStandardDeviation();
    }
    double getVarience() {
        return statistics.getVariance();
    }
    double getMean() {
        return statistics.getMean();
    }
    public double getAlpha() {
        return alpha;
    }
    public double getSigma() {
        return sigma;
    }
    public double getMu() {
        return mu;
    }

    double[] getXData(){
        double[] xData = new double[histogram.toList().size()];
        for (int i = 0; i < xData.length; i++) {
            xData[i]= histogram.toList().get(i).getX();
        }
        return xData;
    }
    double[] getYData(){
        double[] yData = new double[histogram.toList().size()];
        for (int i = 0; i < yData.length; i++) {
            yData[i]= histogram.toList().get(i).getY();
        }
        return yData;
    }

    //Normal Data uses a normal distribution to create a much higher resolution graph, giving the illusion of a curve
    double[] getYNormal(){
        double[] xData = getXNormal(), yData = new double[xData.length];
        yData[0] = normal.probability(xData[0]-1, xData[1]);
        double max = yData[0];
        yData[yData.length-1]=normal.probability(xData[xData.length-2], xData[xData.length-1]+1);
        for (int i = 1; i < (yData.length-1); i++) {
            yData[i]= normal.probability(xData[i-1], xData[i+1]);
            if(yData[i]>max){
                max = yData[i];
            }
        }
        for (int i = 0; i < yData.length; i++) {
            yData[i]=alpha*10*yData[i]/max;
        }
        return yData;
    }

    public double[] getXNormal() {
        double[] xData = new double[(int)statistics.getMax()- (int) statistics.getMin()];
        for (int i = 0; i < xData.length; i++) {
            xData[i] = statistics.getMin()+i;
        }
        return xData;
    }

    public boolean isLoaded(){
        return loaded;
    }
}
