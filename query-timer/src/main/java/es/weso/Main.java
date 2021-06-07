package es.weso;

public class Main {

    private final static String WIKIDATA_ENDPOINT = "https://query.wikidata.org/sparql";

    public static void main(String[] args){
        SPARQLTimer timer = new SPARQLTimer();
       // timer.executeQuery(LUXEMBOURG_AUTHORS_QUERY,WIKIDATA_ENDPOINT);
       // System.out.println(timer.getExecutionTime());
    }

}

