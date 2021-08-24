const { MongoClient, GridFSBucket } = require('mongodb');
const { StringDecoder } = require('string_decoder');
const express = require('express');
const router = express.Router();
require('dotenv/config');


router.get('/country_authors/:countryId',async (req,res)=>{
      if(req.params.countryId!== 'undefined'){
        console.log('country authors request '+ req.params.countryId)
        const decoder = new StringDecoder('utf8');
        conexion()
            .then((gfs) => getResult(gfs, "country_authors_"+req.params.countryId)).then((data)=>
              res.send(decoder.write(data))
            );
      }
        
});

router.get('/country_organizations/:countryId',async (req,res)=>{
    if(req.params.countryId!== 'undefined'){
      console.log('country organizations request '+ req.params.countryId)
      const decoder = new StringDecoder('utf8');
      conexion()
          .then((gfs) => getResult(gfs, "country_organizations_"+req.params.countryId)).then((data)=>
            res.send(decoder.write(data))
          );
    }
});

router.get('/country_locations-as-topics/:countryId',async (req,res)=>{
    if(req.params.countryId!== 'undefined'){
      console.log('country locations-as-topic request '+ req.params.countryId)
      const decoder = new StringDecoder('utf8');
      conexion()
          .then((gfs) => getResult(gfs, "country_locations-as-topics_"+req.params.countryId)).then((data)=>
            res.send(decoder.write(data))
          );
    }
      
});


router.get('/country_narrative-locations/:countryId',async (req,res)=>{
    if(req.params.countryId!== 'undefined'){
      console.log('country locations-as-topic request '+ req.params.countryId)
      const decoder = new StringDecoder('utf8');
      conexion()
          .then((gfs) => getResult(gfs, "country_narrative-locations_"+req.params.countryId)).then((data)=>
            res.send(decoder.write(data))
          );
    }
      
});

router.get('/country_co-organizations/:countryId',async (req,res)=>{
    if(req.params.countryId!== 'undefined'){
      console.log('country locations-as-topic request '+ req.params.countryId)
      const decoder = new StringDecoder('utf8');
      conexion()
          .then((gfs) => getResult(gfs, "country_co-organizations_"+req.params.countryId)).then((data)=>
            res.send(decoder.write(data))
          );
    }  
});




async function conexion(){
    let db = "mongodb://userb:JsE723U4sVoz1nGt@cluster0-shard-00-00.iygmf.mongodb.net:27017,cluster0-shard-00-01.iygmf.mongodb.net:27017,cluster0-shard-00-02.iygmf.mongodb.net:27017/scholia?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true&w=majority"
    return new Promise((resolve, reject) => {
        MongoClient.connect(db, (err, db) => {
            if (err) {
                console.log("Error when connecting to database")
                resolve(null)
            } else {
                var bucket = new GridFSBucket(db, {
                    chunkSizeBytes: 16000000
                });
                resolve(bucket);
            }
        });
    });
  }
  
  async function getResult(db, col){
    return new Promise((resolve, reject) => {
        const fs = require('fs');
        const chunks = [];
        db.openDownloadStreamByName(col).
        on('data', function(data) {
         // console.log(data);
          chunks.push(data);
        }).
        on('error', function(error) {
            console.log(error);
            resolve(null);
            db.s.db.close();
        }).
        on('end', function() {
            console.log('done!');
            const data = Buffer.concat(chunks);
            resolve(data);
            db.s.db.close();
        });
    });
  }



module.exports = router;