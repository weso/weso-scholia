package es.weso;

public class Main {

    private final static String WIKIDATA_ENDPOINT = "https://query.wikidata.org/sparql";

    private final static String LUXEMBOURG_AUTHORS_QUERY = "SELECT\n" +
        "?number_of_citing_works\n" +
        "?author ?authorLabel\n" +
        "?organization ?organizationLabel\n" +
        "?example_work ?example_workLabel\n" +
        "WITH {\n" +
        "SELECT DISTINCT ?author WHERE {\n" +
        "?author wdt:P27 | wdt:P1416/wdt:P17 | wdt:P108/wdt:P17 wd:Q32 .\n" +
        "}\n" +
        "} AS %authors\n" +
        "WITH {\n" +
        "SELECT\n" +
        "?author\n" +
        "(COUNT(DISTINCT ?citing_work) AS ?number_of_citing_works)\n" +
        "(SAMPLE(?organization_) AS ?organization)\n" +
        "(SAMPLE(?work) AS ?example_work)\n" +
        "WHERE {\n" +
        "INCLUDE %authors\n" +
        "?work wdt:P50 ?author .\n" +
        "OPTIONAL { ?citing_work wdt:P2860 ?work . }\n" +
        "OPTIONAL {\n" +
        "?author wdt:P1416 | wdt:P108 ?organization_ .\n" +
        "?organization_ wdt:P17 wd:Q32\n" +
        "}\n" +
        "}\n" +
        "GROUP BY ?author\n" +
        "} AS %results\n" +
        "WHERE {\n" +
        "INCLUDE %results\n" +
        "service wikibase:label { bd:serviceParam wikibase:language \"en\" . }\n" +
        "}\n" +
        "ORDER BY DESC(?number_of_citing_works)";

        private final  static  String OXFORD_TOPICS_QUERY = "SELECT\n" +
        "  ?researchers\n" +
        "  ?topic ?topicLabel\n" +
        "  (\"\uD83D\uDD0E\" AS ?zoom)\n" +
        "  (CONCAT(\"Q34433/topic/\", SUBSTR(STR(?topic), 32)) AS ?zoomUrl)\n" +
        "  ?topicDescription\n" +
        "  ?samplework ?sampleworkLabel\n" +
        "WITH {\n" +
        "  SELECT DISTINCT ?researcher WHERE {\n" +
        "    ?researcher ( wdt:P108 | wdt:P463 | wdt:P1416 ) / wdt:P361* wd:Q34433 .\n" +
        "  }\n" +
        "} AS %researchers\n" +
        "WITH {\n" +
        "  SELECT DISTINCT ?topic\n" +
        "    (COUNT(DISTINCT ?researcher) AS ?researchers)\n" +
        "    (SAMPLE(?work) AS ?samplework)\n" +
        "  WHERE {\n" +
        "    INCLUDE %researchers\n" +
        "    ?work wdt:P50 ?researcher .\n" +
        "    ?work wdt:P921 ?topic .\n" +
        "  }\n" +
        "  GROUP BY ?topic\n" +
        "  ORDER BY DESC(?researchers)\n" +
        "  LIMIT 500\n" +
        "} AS %works_and_number_of_researchers\n" +
        "WHERE {\n" +
        "  INCLUDE %works_and_number_of_researchers\n" +
        "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"en,da,de,es,fr,nl,no,ru,sv,zh\" . }\n" +
        "}\n" +
        "GROUP BY ?researchers ?topic ?topicLabel ?topicDescription ?samplework ?sampleworkLabel\n" +
        "ORDER BY DESC(?researchers)";

    public static void main(String[] args){
        SPARQLTimer timer = new SPARQLTimer();
        timer.executeQuery(LUXEMBOURG_AUTHORS_QUERY, WIKIDATA_ENDPOINT);
        System.out.println(timer.getExecutionTime());

        timer.executeQuery(OXFORD_TOPICS_QUERY, WIKIDATA_ENDPOINT);
        System.out.println(timer.getExecutionTime());
    }

}

