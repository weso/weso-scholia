const { MongoClient } = require('mongodb');
const express = require('express');
const router = express.Router();
require('dotenv/config');

const client = new MongoClient(process.env.DB_CONNECTION);

router.get('/country_authors/:countryId',async (req,res)=>{
    try {
        console.log("Entra")
        await client.connect();
        const database = client.db('admin');
        const country_authors = database.collection('country_authors');
        const authors = await country_authors.findOne({parameter:req.params.countryId});
        console.log(authors)
        res.status(200).send(authors)
      } finally {
        // Ensures that the client will close when you finish/error
        await client.close();
      }
});
module.exports = router;