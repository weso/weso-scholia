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

    public void executePaginatedQuery(List<String> parameters, 
        String query,String endpoint) throws QueryEvaluationException {

        for (String pm : parameters) {
            System.out.println("PARAMETER: " + pm);
            totalTime = 0;
            numberOfNodes = 0;
            totalQueries = 0;
            String parametrizedQuery = query.replace("{{ q }}", pm + "");
            sequenceQueriesWithOffset(parametrizedQuery, 0, endpoint);
            
            System.out.println(totalTime + " s");
            System.out.println(numberOfNodes  + " nodos");
            System.out.println(totalQueries + " queries");
        }

        
    }

    private void sequenceQueriesWithOffset(String query, int offset, String endpoint) {

        int i = offset;

        try {
            int dataSize = queryWithOffset(query, i, endpoint);
            if(dataSize > 0) {
                i += 1000;
                sequenceQueriesWithOffset(query, i, endpoint);
            }
        } catch (Exception ex) {
            System.out.println(ex);
            sequenceQueriesWithOffset(query, i, endpoint);
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

        totalTime += getExecutionTime();
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
}
