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
                    resolve(db);
                }
            });
        });
    },
    insertResults : async (db, results, col) => {
        return new Promise((resolve, reject) => {
            let collection = db.collection(col);
            collection.insert(results, (err, result) => {
                if (err) {
                    console.log("Error when saving results: " + err)
                    resolve(null);
                } else {
                    console.log("Saved successfully")
                    resolve(true);
                }
                db.close();
            });
        });
    },
    removeResults : async (db, criterio, col) => {
        return new Promise((resolve, reject) => {
            let collection = db.collection(col);
            collection.remove(criterio, (err, result) => {
                if (err) {
                    console.log("Error when removing results: " + err)
                    resolve(null);
                } else {
                    resolve(result);
                }
                db.close();
            });
        });
    },
};
