package es.weso;

import java.util.List;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

public class SPARQLTimer {

    private double initTime;
    private double finalTime;
    private double totalTime;
    private int totalQueries;
    private int numberOfNodes;
    private double initialTime;
    private double longestTime;
    private double shortestTime;
    private int offset;
    private int noResults;

    public void executeQuery(String query,String endpoint) throws QueryEvaluationException {

        SPARQLRepository sparqlRepository = new SPARQLRepository(endpoint);
        RepositoryConnection sparqlConnection = sparqlRepository.getConnection();

        startCrono();

        TupleQuery tupleQuery = sparqlConnection.prepareTupleQuery(
                QueryLanguage.SPARQL, query);

        List<BindingSet> bindingSets = QueryResults.asList(tupleQuery.evaluate());
        
        stopCrono();

        numberOfNodes = bindingSets.size();
        System.out.println("NODES: " + numberOfNodes);
    }

    public void executePaginatedQuery(String query,String endpoint) throws QueryEvaluationException {
            totalTime = 0;
            numberOfNodes = 0;
            totalQueries = 0;
            initialTime = 0;
            longestTime = 0;
            shortestTime = Double.MAX_VALUE;
            noResults = 0;
            sequenceQueriesWithOffset(query, 0, endpoint);    
    }

    private void sequenceQueriesWithOffset(String query, int off, String endpoint) {

        offset = off;

        try {
            int dataSize = queryWithOffset(query, offset, endpoint);
            if(dataSize == 0) {
                noResults++;
            }
            if(noResults < 5) {
                offset += 1000;
                sequenceQueriesWithOffset(query, offset, endpoint);
            } 
            
                
        } catch (Exception ex) {
            System.out.println(ex);
            sequenceQueriesWithOffset(query, offset, endpoint);
        }
    }

    private int queryWithOffset(String query, int offset, String endpoint) {

        SPARQLRepository sparqlRepository = new SPARQLRepository(endpoint);
        RepositoryConnection sparqlConnection = sparqlRepository.getConnection();

        String offQuery = query.replace("{w}", offset + "");
        
        startCrono();
 
        TupleQuery tupleQuery = sparqlConnection.prepareTupleQuery(
                QueryLanguage.SPARQL, offQuery);

        List<BindingSet> bindingSets = QueryResults.asList(tupleQuery.evaluate());

        stopCrono();

        double exTime = getExecutionTime();

        totalTime += exTime;
        if(offset == 0) {
            initialTime = exTime;
        }
        if(exTime < shortestTime) {
            shortestTime = exTime;
        }
        if(exTime > longestTime) {
            longestTime = exTime;
        }
        numberOfNodes += bindingSets.size();
        totalQueries++;

        return bindingSets.size();
    }


    private void startCrono(){
        initTime = System.currentTimeMillis();
    }

    private void stopCrono(){
        finalTime = System.currentTimeMillis();
    }

    public double getExecutionTime(){
        return finalTime-initTime;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public int getTotalQueries() {
        return totalQueries;
    }

    public double getInitialTime() {
        return initialTime;
    }

    public double getShortestTime() {
        return shortestTime;
    }

    public double getLongestTime() {
        return longestTime;
    }

    public int getOffset() {
        return offset;
    }
}
