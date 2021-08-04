module.exports = {
    conexion : async () => {
        let mongo = require("mongodb");
        let db = "mongodb://userb:JsE723U4sVoz1nGt@cluster0-shard-00-00.iygmf.mongodb.net:27017,cluster0-shard-00-01.iygmf.mongodb.net:27017,cluster0-shard-00-02.iygmf.mongodb.net:27017/scholia?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true&w=majority"
        return new Promise((resolve, reject) => {
            mongo.MongoClient.connect(db, (err, db) => {
                if (err) {
                    console.log("Error when connecting to database")
                    resolve(null)
                } else {
                    var bucket = new mongo.GridFSBucket(db, {chunkSizeBytes: 16000000}); 
                    resolve(bucket);
                }
            });
        });
    },
    insertResults : async (db, results, col, t0) => {
        return new Promise((resolve, reject) => {
            const fs = require('fs');
            let data = JSON.stringify(results);
            fs.writeFileSync('toUpload.json', data);

            fs.createReadStream("toUpload.json").
                pipe(db.openUploadStream(col + "_" + results.parameter)).
                on('error', function(error) {
                    console.log(error)
                    resolve(null);
                }).
                on('finish', function() {
                    console.log("Saved successfully")
                    console.log("Elapsed time: " + (Date.now() - t0) + " milliseconds.")
                    db.s.db.close();
                    resolve(true);
                });
              
            /* let collection = db.collection(col);
            collection.insert(results, (err, result) => {
                if (err) {
                    console.log("Error when saving results: " + err)
                    resolve(null);
                } else {
                    console.log("Saved successfully")
                    console.log("Elapsed time: " + (Date.now() - t0) + " milliseconds.")
                    resolve(true);
                }
                db.close();
            });*/
        }); 
    },
    removeResults : async (db, criterio, col) => {
        return new Promise((resolve, reject) => {
            db.find(criterio)
            .toArray((err, files) => {
            if (err) {
                console.log("Error when removing results: " + err)
                resolve(null);
            }
              if (!files || files.length === 0) {
                console.log("No file to delete")
                resolve(false)
                db.s.db.close();
              }
              else {
                db.delete(files[0]._id, function() {
                    console.log('File Deleted')
                    resolve(true)
                    db.s.db.close();
                });
              }
    })
            /* let collection = db.collection(col);
            collection.remove(criterio, (err, result) => {
                if (err) {
                    console.log("Error when removing results: " + err)
                    resolve(null);
                } else {
                    resolve(result);
                }
                db.close();
            }); */
        });
    },
};
