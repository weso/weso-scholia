const express = require('express');
const app = express();
const fs = require('fs');
const https = require('https');
var cors = require('cors');

const PORT = 3000;

const scholiaRoute = require('./routes/scholia');


app.use(cors({origin: '*'}));

// Middleware
app.use('/scholia',scholiaRoute);

// ROUTES
app.get('/',(req,res)=>{
    res.send('Hello world!');
})


https.createServer({
    key: fs.readFileSync('my_cert.key'),
    cert: fs.readFileSync('my_cert.crt')
  }, app).listen(PORT, function(){
    console.log("My HTTPS server listening on port " + PORT + "...");
  });