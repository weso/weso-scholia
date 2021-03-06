package es.weso;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.rdf4j.query.QueryEvaluationException;

public class Main {

    private final static String BLAZEGRAPH_ENDPOINT = "http://localhost:9999/blazegraph/sparql";

    private final static String WIKIDATA_ENDPOINT = "https://query.wikidata.org/sparql";

    private final static int REPETITIONS = 3;

    private static FileWriter fw;

    private final static List<String> queries = new ArrayList<String>(Arrays.asList(
    "/query-timer/src/main/resources/country_authors_sb1.sparql"
    //"/query-timer/src/main/resources/country_organizations.sparql"
    //"/query-timer/src/main/resources/luxembourg_authors.sparql",
    //"/query-timer/src/main/resources/spain_authors.sparql",
    //"/query-timer/src/main/resources/university_oxford_topics.sparql",
    //"/query-timer/src/main/resources/university_oviedo_topics.sparql"
    //"/query-timer/src/main/resources/luxembourg_authors_blns.sparql"
    //"/query-timer/src/main/resources/luxembourg_authors_wdsq.sparql"
    ));

    private final static List<String> parameters = new ArrayList<String>(Arrays.asList(
    "Q778",
    "Q32",
    "Q29",
    "Q183"
    ));

    public static void main(String[] args){
        try {
            fw = new FileWriter("./query-timer/responsetimes3.csv");
            fw.write("Query,Total time (x̄),Total time (SD),Initial Results Time (x̄),Initial Results Time (SD),Longest Time,Shortest Time,Number of nodes (x̄),Number of un. nodes (x̄),Number of un. nodes (SD),Number of queries (x̄),Number of queries (SD),Longest subtime,Shortest subtime\n");
            for (String query : queries) {
                try {       
                    //measureQuery(query);
                    measurePaginatedQuery(query);      
                } catch (IOException e) {
                    System.out.println("An error has taken place while reading query " + query 
                    + " or writing its response times.");
                } catch (QueryEvaluationException e) {
                    System.out.println("Timeout took place in " + query);
                    fw.write(String.format("%s,%d,%d,%d,%d,%d,%d\n", 
                                query.
                                split("resources/")[1].replace(".sparql", "").toUpperCase(), -1, -1, -1, -1, -1, -1));
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
        fw.write("Query,Mean,Standard Deviation,Longest Time,Shortest Time,Number of nodes (x̄),Number of nodes (SD)\n");
        SPARQLTimer timer = new SPARQLTimer();
        System.out.println("\n>>>>>>>>>>><<<<<<<<<<");
        String queryName = query.split("resources/")[1].replace(".sparql", "").toUpperCase();
        System.out.println("Measuring query " + queryName);

        double total = 0;
        double[] values = new double[REPETITIONS];
        double[] nodeValues = new double[REPETITIONS];
        double longer = -1;
        double shorter = Double.MAX_VALUE;
        int totalnodes = 0;

        for(int i = 0; i < REPETITIONS; i++) {
            String data = SPARQLReader.readFile(query);
            timer.executeQuery(data, BLAZEGRAPH_ENDPOINT);
            double responseTime = timer.getExecutionTime();
            System.out.println("Iteration " + i + ": " + responseTime);
            total += responseTime;
            values[i] = responseTime;
            longer = MathUtil.getLarger(longer, responseTime);
            shorter = MathUtil.getShorter(shorter, responseTime);
            int numberOfNodes = timer.getNumberOfNodes();
            totalnodes += numberOfNodes;
            nodeValues[i] = numberOfNodes;
        }   
        double mean = MathUtil.getMean(total, REPETITIONS);
        double sd = MathUtil.calculateSD(values, mean);

        double meanNodes = MathUtil.getMean(totalnodes, REPETITIONS);
        double sdNodes = MathUtil.calculateSD(nodeValues, meanNodes);

        System.out.println("MEAN: " + mean);
        System.out.println("STANDARD DEVIATION: " + sd);
        System.out.println("LONGEST RESPONSE TIME: " + longer);
        System.out.println("SHORTEST RESPONSE TIME: " + shorter);
        System.out.println("NUMBER OF NODES (x̄): " + meanNodes);
        System.out.println("NUMBER OF NODES (SD): " + sdNodes);
        fw.write(String.format("%s,%d,%d,%d,%d,%d,%d\n", queryName, (int) mean, (int) sd, (int) longer, (int) shorter,
            (int) meanNodes, (int) sdNodes));
    }

    private static void measurePaginatedQuery(String query) throws IOException {
        SPARQLTimer timer = new SPARQLTimer();
        System.out.println("\n>>>>>>>>>>><<<<<<<<<<");
        String queryName = query.split("resources/")[1].replace(".sparql", "").toUpperCase();
        System.out.println("Measuring query " + queryName);

        for (String pm : parameters) {
            System.out.println("PARAMETER: " + pm);
            String data = SPARQLReader.readFile(query);
            String parametrizedQuery = data.replace("{{ q }}", pm + "");

            double total = 0;
            double initialTime = 0;
            double[] values = new double[REPETITIONS];
            double[] nodeValues = new double[REPETITIONS];
            double[] queryValues = new double[REPETITIONS];
            double[] initialValues = new double[REPETITIONS];
            double longer = -1;
            double shorter = Double.MAX_VALUE;
            double longestSubTime = -1;
            double shortestSubTime = Double.MAX_VALUE;
            int totalnodes = 0;
            int totalUnnodes = 0;

            int totalQueries = 0;

            for(int i = 0; i < REPETITIONS; i++) {
                
                timer.executePaginatedQuery(parametrizedQuery, WIKIDATA_ENDPOINT);
                double responseTime = timer.getTotalTime();
                System.out.println("Iteration " + i + ": " + responseTime);
                System.out.println("Ended at offset " + timer.getOffset());
                total += responseTime;
                values[i] = responseTime;
                longer = MathUtil.getLarger(longer, responseTime);
                shorter = MathUtil.getShorter(shorter, responseTime);
                longestSubTime = MathUtil.getLarger(longestSubTime, timer.getLongestTime());
                shortestSubTime = MathUtil.getShorter(shortestSubTime, timer.getShortestTime());
                int numberOfUnNodes = timer.getUniqueNodes();
                totalnodes += timer.getNumberOfNodes();
                totalUnnodes += numberOfUnNodes;
                nodeValues[i] = numberOfUnNodes;
                System.out.println("Returned " + numberOfUnNodes + " unique nodes.");
                int numberOfQueries = timer.getTotalQueries();
                totalQueries += numberOfQueries;
                queryValues[i] = numberOfQueries;
                initialTime += timer.getInitialTime();
                initialValues[i] = timer.getInitialTime();
            }   
            double mean = MathUtil.getMean(total, REPETITIONS);
            double sd = MathUtil.calculateSD(values, mean);

            double meanNodes = MathUtil.getMean(totalnodes, REPETITIONS);
            double meanUnNodes = MathUtil.getMean(totalUnnodes, REPETITIONS);
            double sdNodes = MathUtil.calculateSD(nodeValues, meanUnNodes);

            double meanQueries = MathUtil.getMean(totalQueries, REPETITIONS);
            double sdQueries = MathUtil.calculateSD(queryValues, meanQueries);

            double meanInitial = MathUtil.getMean(initialTime, REPETITIONS);
            double sdInitial = MathUtil.calculateSD(initialValues, meanInitial);

            System.out.println("MEAN: " + mean);
            System.out.println("STANDARD DEVIATION: " + sd);
            System.out.println("INITIAL RESULTS TIME (x̄): " + meanInitial);
            System.out.println("INITIAL RESULTS TIME (SD): " + sdInitial);
            System.out.println("LONGEST RESPONSE TIME: " + longer);
            System.out.println("SHORTEST RESPONSE TIME: " + shorter);
            System.out.println("NUMBER OF NODES (x̄): " + meanNodes);
            System.out.println("NUMBER OF UNIQUE NODES (x̄): " + meanUnNodes);
            System.out.println("NUMBER OF UNIQUE NODES (SD): " + sdNodes);
            System.out.println("NUMBER OF QUERIES (x̄): " + meanQueries);
            System.out.println("NUMBER OF QUERIES (SD): " + sdQueries);
            System.out.println("LONGEST SUBQUERY TIME: " + longestSubTime);
            System.out.println("SHORTEST SUBQUERY TIME: " + shortestSubTime);
            fw.write(String.format("%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d\n", queryName, 
                (int) mean, (int) sd, 
                (int) meanInitial, (int) sdInitial,
                (int) longer, (int) shorter,
                (int) meanNodes, (int) meanUnNodes, (int) sdNodes,  
                (int) meanQueries, (int) sdQueries,
                (int) longestSubTime, (int) shortestSubTime));
        }
    }

    

}

