const config = require('./queries.json')
const getJSON = require('get-json')
const fetch = require('node-fetch');
const repositorio = require("./repositorio.js");

let noResults = 0;
let startTime = 0;

async function executeQueries() {
    startTime = Date.now();
    //config.forEach(q => executePaginatedQuery(q.query, q.parameters, q.query_name))
    for (const q of config) {
        await executePaginatedQuery(q.query, q.parameters, q.query_name)
      }
    let t1 = Date.now();
    console.log("Synchronous time: " + (t1 - startTime) + " milliseconds.")
}

async function executePaginatedQuery(query, parameters, qName) {
    for (const p of parameters) {
        parametrizedQuery = query.replace(/{{ q }}/g, p);
        await sparqlLimits(parametrizedQuery, p, qName)
      }
}

async function sparqlLimits(sparql, param, qName) {
    var url = "https://query.wikidata.org/sparql?query=" + 
        encodeURIComponent(sparql.replace(/\{w\}/, "0")) + '&format=json';

    var extractedData = {"data": [], "parameter": param};
    noResults = 0;
    console.log(param);

    await fetch(url, {
        method: 'GET',
        headers: {
            'User-Agent': 'WesoScholiaCacher/0.1 (https://github.com/weso/weso-scholia)'
        }})
    .then(res => res.json())
    .then(async (json) => {
        //var simpleData = sparqlDataToSimpleData(json);
        
        //simpleData.data.forEach(function(row) {
        //    extractedData.data.push(row)
        //});

        extractedData.data = json;

        await sequenceQueriesWithOffset(sparql, 1000, extractedData);
        
    })
    .then(() => {
        repositorio.conexion()
            .then((db) => repositorio.removeResults(db, {"parameter": param}, qName));
    })
    .then(() => {
        repositorio.conexion()
            .then((db) => repositorio.insertResults(db, extractedData, qName, startTime));
    });
    
}

async function sequenceQueriesWithOffset(sparql, offset, extractedData) {
    let i = offset;
    await queryWithOffset(sparql, offset, extractedData).then(async function(quantity) {
        console.log("Nodes: " + extractedData.data.results.bindings.length);
        console.log("Offset " + offset)
        if(quantity == 0) {
            noResults++;
        }
        if(noResults < 5) {
            i += 1000;
            await sequenceQueriesWithOffset(sparql, i, extractedData);
        }
    }).catch(async (e) => {
        console.log(offset)
        //console.log(e);
        await sequenceQueriesWithOffset(sparql, i, extractedData);
    });
    
}

async function queryWithOffset(sparql, offset, extractedData) {
    let url = "https://query.wikidata.org/sparql?query=" + 
        encodeURIComponent(sparql.replace(/\{w\}/, offset.toString())) + '&format=json';

    return await fetch(url, {
        method: 'GET',
        headers: {
            'User-Agent': 'WesoScholiaCacher/0.1 (https://github.com/weso/weso-scholia)'
        }
    })
    .then((res) =>res.json())
    .then((json) => {
        var quantity = 0;
        //var simpleData = sparqlDataToSimpleData(json);

        json.results.bindings.forEach(function(row) {
            //if(!existsId(extractedData.data.results.bindings, row.hiddenId)) {
                extractedData.data.results.bindings.push(row);
                quantity++;         
            //}
        });
        
        return quantity;
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

function existsId(collection, id) {
    return collection.some(function(row) {
        return row.hiddenId === id
    });
}

executeQueries();