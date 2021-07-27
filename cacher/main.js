const config = require('./queries.json')
const fetch = require('node-fetch');
const repositorio = require("./repositorio.js");

let startTime = 0;
let subsetting_size = 0;

async function executeQueries() {
    startTime = Date.now();
    for (const q of config) {
        await executePaginatedQuery(q.query, q.parameters, q.query_name, q.count_query)
      }
    let t1 = Date.now();
    console.log("Synchronous time: " + (t1 - startTime) + " milliseconds.")
}

async function executePaginatedQuery(query, parameters, qName, cQuery) {
    for (const p of parameters) {
        parametrizedQuery = query.replace(/{{ q }}/g, p);
        parametrizedCQuery = cQuery.replace(/{{ q }}/g, p);
        await sparqlLimits(parametrizedQuery, p, qName, parametrizedCQuery)
      }
}

async function sparqlLimits(sparql, param, qName, cQuery) {
    var count_url = "https://query.wikidata.org/sparql?query=" + 
        encodeURIComponent(cQuery) + '&format=json';

    await fetch(count_url, {
        method: 'GET',
        headers: {
            'User-Agent': 'WesoScholiaCacher/0.1 (https://github.com/weso/weso-scholia)'
        }})
    .then(res => res.json())
    .then(async (json) => {
        subsetting_size = json.results.bindings[0].subsetting_size.value;
        console.log("Subsetting size: " + subsetting_size);
    });


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
        if(offset < subsetting_size) {
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

        json.results.bindings.forEach(function(row) {
            //Comment condition if ordered subsetting
            if(!existsId(extractedData.data.results.bindings, row.hiddenId)) {
                extractedData.data.results.bindings.push(row);
                quantity++;         
            }
        });
        
        return quantity;
    }); 
    
    
}

function existsId(collection, id) {
    return collection.some(function(row) {
        return row.hiddenId === id
    });
}

executeQueries();