const config = require('./queries.json')
const getJSON = require('get-json')
const fetch = require('node-fetch');

function executeQueries() {
    config.forEach(q => executePaginatedQuery(q.query, q.parameters))

}

function executePaginatedQuery(query, parameters) {
    parameters.forEach(function(p) {
        parametrizedQuery = query.replace(/{{ q }}/g, p);
        sparqlLimits(parametrizedQuery)
    });

}

function sparqlLimits(sparql) {
    var url = "https://query.wikidata.org/sparql?query=" + 
        encodeURIComponent(sparql.replace(/\{w\}/, "0")) + '&format=json';

    var extractedData = [];

    fetch(url)
    .then(res => res.json())
    .then((json) => {
        var simpleData = sparqlDataToSimpleData(json);
        
        simpleData.data.forEach(function(row) {
            extractedData.push(row.hiddenId)
        });

        sequenceQueriesWithOffset(sparql, 1000, extractedData);
    });
}

function sequenceQueriesWithOffset(sparql, offset, extractedData) {
    let i = offset;
    queryWithOffset(sparql, offset, extractedData).then(function(data) {
        console.log(extractedData.length);
        console.log(offset)
        if(data.length > 0) {
            i += 1000;
            sequenceQueriesWithOffset(sparql, i, extractedData);
        }
    }).catch(e => {
        console.log(offset)
        //console.log(e);
        sequenceQueriesWithOffset(sparql, i, extractedData);
    });
    
}

async function queryWithOffset(sparql, offset, extractedData) {
    let url = "https://query.wikidata.org/sparql?query=" + 
        encodeURIComponent(sparql.replace(/\{w\}/, offset.toString())) + '&format=json';

    return fetch(url)
    .then(res => res.json())
    .then((json) => {
        var dataToAdd = [];
        var simpleData = sparqlDataToSimpleData(json);

        simpleData.data.forEach(function(row) {
            if(!extractedData.includes(row.hiddenId)) {
                extractedData.push(row.hiddenId);
                dataToAdd.push(row);             
            }
        });
        
        return dataToAdd;
    }); 
    
    
}

function sparqlDataToSimpleData(response) {
    // Convert long JSON data from from SPARQL endpoint to short form
    let data = response.results.bindings;
    let columns = response.head.vars
    var convertedData = [];
    for (var i = 0 ; i < data.length ; i++) {
	var convertedRow = {};
	for (var key in data[i]) {
	    convertedRow[key] = data[i][key]['value'];
	}
	convertedData.push(convertedRow);
    }
    return {data: convertedData, columns: columns};
}

executeQueries();