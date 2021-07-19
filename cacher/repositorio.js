module.exports = {
    conexion : async () => {
        let mongo = require("mongodb");
        let db = "mongodb://admin:0qVPsBLuqsnXauTA@cluster0-shard-00-00.iygmf.mongodb.net:27017,cluster0-shard-00-01.iygmf.mongodb.net:27017,cluster0-shard-00-02.iygmf.mongodb.net:27017/dpiu?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true&w=majority";
        return new Promise((resolve, reject) => {
            mongo.MongoClient.connect(db, (err, db) => {
                if (err) {
                    resolve(null)
                } else {
                    resolve(db);
                }
            });
        });
    },
    obtenerFormulariosPg : async (db, pg, criterio, orden) => {
        return new Promise((resolve, reject) => {
            let collection = db.collection('formularios');
            let sort = {};
            if(orden === "descripcion") {
                sort = {descripcion: 1};
            }
            else {
                sort = { titulo: 1}
            }
            collection.count(criterio, (err, count) => {
                collection.find(criterio).sort(sort).skip((pg - 1) * 2).limit(2)
                    .toArray((err, result) => {

                        if (err) {
                            resolve(null);
                        } else {
                            // Guardar el total de anuncios
                            result.total = count;
                            resolve(result);
                        }
                        db.close();
                    });
            })
        });
    },
    eliminarFormularios : async (db, criterio) => {
        return new Promise((resolve, reject) => {
            let collection = db.collection('formularios');
            collection.remove(criterio, (err, result) => {
                if (err) {
                    resolve(null);
                } else {
                    resolve(result);
                }
                db.close();
            });
        });
    },
    modificarFormulario : async (db, criterio, form) => {

        return new Promise((resolve, reject) => {
            let collection = db.collection('formularios');
            collection.update(criterio, {$set: form}, (err, result) => {
                if (err) {
                    resolve(null);
                } else {
                    // modificado
                    resolve(result);
                }
                db.close();
            });
        });
    },
    obtenerUsuarios : async (db, criterio) => {
        return new Promise((resolve, reject) => {
            let collection = db.collection('usuarios');
            collection.find(criterio).toArray((err, result) => {
                if (err) {
                    resolve(null);
                } else {
                    resolve(result);
                }
                db.close();
            });
        });
    },
    obtenerFormularios : async (db, criterio) => {
        return new Promise((resolve, reject) => {
            let collection = db.collection('formularios');
            collection.find(criterio).toArray((err, result) => {
                if (err) {
                    resolve(null);
                } else {
                    resolve(result);
                }
                db.close();
            });
        });
    },
    insertarUsuario : async (db, usuario) => {

        return new Promise((resolve, reject) => {
            let collection = db.collection('usuarios');
            collection.insert(usuario, (err, result) => {
                if (err) {
                    resolve(null);
                } else {
                    // _id no es un string es un ObjectID
                    resolve(result.ops[0]._id.toString());
                }
                db.close();
            });
        });
    },
    modificarUsuario : async (db, criterio, form) => {

        return new Promise((resolve, reject) => {
            let collection = db.collection('usuarios');
            collection.update(criterio, {$set: form}, (err, result) => {
                if (err) {
                    resolve(null);
                } else {
                    // modificado
                    resolve(result);
                }
                db.close();
            });
        });
    },
    insertarFormulario : async (db, form) => {

        return new Promise((resolve, reject) => {
            let collection = db.collection('formularios');
            collection.insert(form, (err, result) => {
                if (err) {
                    resolve(null);
                } else {
                    // _id no es un string es un ObjectID
                    resolve(result.ops[0]._id.toString());
                }
                db.close();
            });
        });
    },
    insertarComentario : async (db, form) => {

        return new Promise((resolve, reject) => {
            let collection = db.collection('comentarios');
            collection.insert(form, (err, result) => {
                if (err) {
                    resolve(null);
                } else {
                    // _id no es un string es un ObjectID
                    resolve(result.ops[0]._id.toString());
                }
                db.close();
            });
        });
    },
    obtenerComentariosPg : async (db, pg, criterio) => {
        return new Promise((resolve, reject) => {
            let collection = db.collection('comentarios');
            collection.count(criterio, (err, count) => {
                collection.find(criterio).skip((pg - 1) * 2).limit(2)
                    .toArray((err, result) => {

                        if (err) {
                            resolve(null);
                        } else {
                            result.total = count;
                            resolve(result);
                        }
                        db.close();
                    });
            })
        });
    }
};
