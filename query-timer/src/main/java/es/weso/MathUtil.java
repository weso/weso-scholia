package es.weso;

public class MathUtil {

    public static double calculateSD(double values[], double mean)
    {
        double standardDeviation = 0.0;
        for(double num: values) {
            standardDeviation += Math.pow(num - mean, 2);
        }
        return Math.sqrt(standardDeviation/values.length);
    }

    public static double getMean(double total, int rps) {
        return total / rps;
    }

    public static double getLarger(double a, double b) {
        if (a > b) return a;
        return b;
    }

    public static double getShorter(double a, double b) {
        if (a < b) return a;
        return b;
    }
    
}
