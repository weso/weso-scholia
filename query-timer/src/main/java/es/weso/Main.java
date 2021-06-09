package es.weso;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.rdf4j.query.QueryEvaluationException;

public class Main {

    private final static String WIKIDATA_ENDPOINT = "https://query.wikidata.org/sparql";

    private final static int REPETITIONS = 10;

    private static FileWriter fw;

    private final static List<String> queries = new ArrayList<String>(Arrays.asList(
    "/query-timer/src/main/resources/luxembourg_authors.sparql",
    "/query-timer/src/main/resources/spain_authors.sparql",
    "/query-timer/src/main/resources/university_oxford_topics.sparql",
    "/query-timer/src/main/resources/university_oviedo_topics.sparql"
    ));

    public static void main(String[] args){
        try {
            fw = new FileWriter("./query-timer/responsetimes.csv");
            fw.write("Query,Mean,Standard Deviation,Longer Time, Shorter Time\n");
            for (String query : queries) {
                try {       
                    measureQuery(query);         
                } catch (IOException e) {
                    System.out.println("An error has taken place while reading query " + query 
                    + " or writing its response times.");
                } catch (QueryEvaluationException e) {
                    System.out.println("Timeout took place in " + query);
                    fw.write(String.format("%s,%d,%d,%d,%d\n", 
                                query.split("resources/")[1].replace(".sparql", "").toUpperCase(), -1, -1, -1, -1));
                }
            }
            
        } catch (IOException e) {
            System.out.println("A problem has taken place while opening or closing the output file.");
        } finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void measureQuery(String query) throws IOException {

        SPARQLTimer timer = new SPARQLTimer();
        System.out.println("\n>>>>>>>>>>><<<<<<<<<<");
        String queryName = query.split("resources/")[1].replace(".sparql", "").toUpperCase();
        System.out.println("Measuring query " + queryName);

        double total = 0;
        double[] values = new double[REPETITIONS];
        double longer = -1;
        double shorter = Double.MAX_VALUE;

        for(int i = 0; i < REPETITIONS; i++) {
            String data = SPARQLReader.readFile(query);
            timer.executeQuery(data, WIKIDATA_ENDPOINT);
            double responseTime = timer.getExecutionTime();
            System.out.println("Iteration " + i + ": " + responseTime);
            total += responseTime;
            values[i] = responseTime;
            longer = MathUtil.getLarger(longer, responseTime);
            shorter = MathUtil.getShorter(shorter, responseTime);
        }   
        double mean = MathUtil.getMean(total, REPETITIONS);
        double sd = MathUtil.calculateSD(values, mean);

        System.out.println("MEAN: " + mean);
        System.out.println("STANDARD DEVIATION: " + sd);
        System.out.println("LONGER RESPONSE TIME: " + longer);
        System.out.println("SHORTER RESPONSE TIME: " + shorter);
        fw.write(String.format("%s,%d,%d,%d,%d\n", queryName, (int) mean, (int) sd, (int) longer, (int) shorter));
    }

    

}

