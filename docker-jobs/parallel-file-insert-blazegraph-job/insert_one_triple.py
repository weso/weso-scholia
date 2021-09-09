from SPARQLWrapper import SPARQLWrapper, POST, DIGEST



if __name__ == '__main__':
    sparql_endpoint = SPARQLWrapper('http://156.35.82.22:8889/bigdata/sparql')
    #sparql_endpoint.setHTTPAuth(DIGEST)
    sparql_endpoint.setMethod(POST)
    insert_query = 'INSERT DATA { ' + '<http://wikiba.se/ontology#Dump> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> 666 .' + ' }'
    sparql_endpoint.setQuery(insert_query)
    results = sparql_endpoint.query()
    print(results)