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

    public void executeQuery(String query,String endpoint) throws QueryEvaluationException {

        startCrono();

        SPARQLRepository sparqlRepository = new SPARQLRepository(endpoint);
        RepositoryConnection sparqlConnection = sparqlRepository.getConnection();

        TupleQuery tupleQuery = sparqlConnection.prepareTupleQuery(
                QueryLanguage.SPARQL, query);

        List<BindingSet> bindingSets = QueryResults.asList(tupleQuery.evaluate());

        stopCrono();
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

}
