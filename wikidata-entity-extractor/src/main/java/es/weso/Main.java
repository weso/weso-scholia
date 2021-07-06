package es.weso;

public class Main {

    public static void main(String[] args){

        EntityStractor entityStractor  = new EntityStractor(DEFAULT_THREADS,OUTPUT);
        entityStractor.extractEntities(AUTHORS);

    }


    private final static int DEFAULT_THREADS = 30;

    private final  static  String OUTPUT = "E:/luxembourg_authors/";

    private final  static  String AUTHORS = "select distinct ?author where { \n" +
            "  { ?author wdt:P27 wd:Q32 } \n" +
            " UNION {\n" +
            "    ?author      wdt:P1416 ?aux_1 .\n" +
            "    ?aux_1  wdt:P17 wd:Q32\n" +
            " }\n" +
            "  UNION {\n" +
            "    ?author      wdt:P108 ?aux_2 .\n" +
            "    ?aux_2  wdt:P17 wd:Q32\n" +
            " }\n" +
            "}";


}
