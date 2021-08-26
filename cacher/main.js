const config = require('./queries.json')
const fetch = require('node-fetch');
const repositorio = require("./repositorio.js");

let startTime = 0;
let qstartTime = 0;
let subsetting_size = 0;
let querySize = 0;

async function executeQueries() {
    startTime = Date.now();  
    for (const q of config) {
	    let queryStartTime = Date.now();
        await executePaginatedQuery(q.query, q.parameters, q.query_name, q.count_query, q.offset)
	    let queryEndTime = Date.now();
    	console.log(q.query_name + " synchronous time: " + (queryEndTime - queryStartTime) + " milliseconds.")
    }
    let endTime = Date.now();
    console.log("Total synchronous time: " + (endTime - startTime) + " milliseconds.")
}

async function executePaginatedQuery(query, parameters, qName, cQuery, off) {
    for (const p of parameters) {
        parametrizedQuery = query.replace(/{{ q }}/g, p);
        parametrizedCQuery = cQuery.replace(/{{ q }}/g, p);
        await sparqlLimits(parametrizedQuery, p, qName, parametrizedCQuery, off)
    }
}

async function sparqlLimits(sparql, param, qName, cQuery, off) {
    qstartTime = Date.now();
    querySize = off;
    var count_url = "https://query.wikidata.org/sparql?query=" +
        encodeURIComponent(cQuery) + '&format=json';

    await fetch(count_url, {
            method: 'GET',
            headers: {
                'User-Agent': 'WesoScholiaCacher/0.1 (https://github.com/weso/weso-scholia)'
            }
        })
        .then(res => res.json())
        .then(async (json) => {
            subsetting_size = json.results.bindings[0].subsetting_size.value;
            console.log("Subsetting size: " + subsetting_size);
        });


    var url = "https://query.wikidata.org/sparql?query=" +
        encodeURIComponent(sparql.replace(/\{w\}/, "0")) + '&format=json';

    var extractedData = {
        "data": [],
        "parameter": param
    };
    noResults = 0;
    console.log(param);

    await fetch(url, {
            method: 'GET',
            headers: {
                'User-Agent': 'WesoScholiaCacher/0.1 (https://github.com/weso/weso-scholia)'
            }
        })
        .then(res => res.json())
        .then(async (json) => {

            extractedData.data = json;

            await sequenceQueriesWithOffset(sparql, off, extractedData);

        })
        .then(async () => {
            await repositorio.conexion()
                .then((db) => repositorio.removeResults(db, {
                    "filename": qName + "_" + param
                }, qName));
        })
        .then(() => {
            repositorio.conexion()
                .then((gfs) => repositorio.insertResults(gfs, extractedData, qName, startTime));
            console.log("Tiempo: " + (Date.now() - qstartTime))
        });

}

async function sequenceQueriesWithOffset(sparql, offset, extractedData) {
    let i = offset;
    await queryWithOffset(sparql, offset, extractedData).then(async function(quantity) {
        console.log("Nodes: " + extractedData.data.results.bindings.length);
        console.log("Offset " + offset)
        if (offset < subsetting_size) {
            i += querySize;
            await sequenceQueriesWithOffset(sparql, i, extractedData);
        }
    }).catch(async (e) => {
        console.log(offset)
        console.log(e);
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
        .then((res) => res.json())
        .then((json) => {
            var quantity = 0;

            json.results.bindings.forEach(function(row) {
                //Comment condition if ordered subsetting
                //if(!existsId(extractedData.data.results.bindings, row.hiddenId)) {
                extractedData.data.results.bindings.push(row);
                quantity++;
                //}
            });

            return quantity;
        });


}

function existsId(collection, id) {
    return collection.some(function(row) {
        return row.hiddenId === id
    });
}

function getFile(name) {
    repositorio.conexion()
        .then((gfs) => repositorio.getResult(gfs, name));
}

executeQueries();
//getFile("country_authors_Q32")