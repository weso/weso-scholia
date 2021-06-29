package es.weso;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class EntityStractor {

    private int numberOfThreads;
    private String entitiesFolderDestination;
    private double initTime;
    private double finalTime;
    private List<String> failedEntities;
    private  ObjectMapper jsonMapper;


    public EntityStractor(int numberOfThreads,String entitiesFolderDestination){
        this.numberOfThreads = numberOfThreads;
        this.entitiesFolderDestination = entitiesFolderDestination;
        this.jsonMapper = new ObjectMapper(new JsonFactory());
        this.failedEntities = new ArrayList<>();
        IOUtils.createFolderIfNecesary(entitiesFolderDestination);
    }

    public void extractEntities(String query){

        SPARQLRepository sparqlRepository = new SPARQLRepository(WIKIDATA_ENDPOINT);
        RepositoryConnection sparqlConnection = sparqlRepository.getConnection();

        TupleQuery tupleQuery = sparqlConnection.prepareTupleQuery(
                QueryLanguage.SPARQL, query);
        List<BindingSet> bindingSets = QueryResults.asList(tupleQuery.evaluate());
        handleEntitiesAsParallel(bindingSets);

    }



    private void handleEntitiesAsParallel(List<BindingSet> bindingSets)  {

        startCrono();

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        Collection<Callable<Void>> tasks = new ArrayList<>();
        for(BindingSet bs:bindingSets){
            Callable<Void> task = () -> {
                String entity = bs.iterator().next().getValue().stringValue().split(WIKIDATA_ENTITY_URI)[1];
                writeEntity(entity);
                return null;
            };
            tasks.add(task);
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor = Executors.newFixedThreadPool(numberOfThreads);
        for(String entity: failedEntities){
            Callable<Void> task = () -> {
                writeEntity(entity);
                return null;
            };
        }

        stopCrono();
        printInfo();
    }

    private Collection<String> bindingsToEntityList(List<Map<String,Object>> bindings){
       return bindings.stream().map(b->((Map<String, String>) b.entrySet().iterator().next().getValue()).get(VALUE_OBJ).split(WIKIDATA_ENTITY_URI)[1]).collect(Collectors.toList());
    }


    private void writeEntity(String entity) {
        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(WIKIDATA_ENTITY_CONTENT_URI+entity+TTL_EXT);

        String response = null;
        try {
            response = RequestUtils.executeRequest(httpclient,httpGet);
            String dest = this.entitiesFolderDestination+entity+TTL_EXT;
            IOUtils.writeFile(dest,response);
        } catch (IOException e) {
            this.failedEntities.add(entity);
            e.printStackTrace();
        }

    }


    private void startCrono(){
        initTime = System.currentTimeMillis();
    }

    private void stopCrono(){
        finalTime = System.currentTimeMillis();
    }

    private double getExecutionTime(){
        return finalTime-initTime;
    }


    private void printInfo(){
        /*
        String content = IOUtils.readFile("E:/results.txt");;
        content+="\n "+getExecutionTime();
        IOUtils.writeFile("E:/results.txt",content);
        */
        System.out.println("FINAL TIME: "+getExecutionTime());
        System.out.println("Number of failed entities: "+failedEntities.size());
    }


    private final static String WIKIDATA_ENDPOINT = "https://query.wikidata.org/sparql";
    private final static String VALUE_OBJ = "value";
    private final static String WIKIDATA_ENTITY_URI = "http://www.wikidata.org/entity/";
    private final static String WIKIDATA_ENTITY_CONTENT_URI = "https://www.wikidata.org/wiki/Special:EntityData/";
    private final static String TTL_EXT = ".ttl";
    private final static String JSON_FORMAT = "&format=json";


}
