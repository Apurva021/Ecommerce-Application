const express=require('express')
const app=express()
const bodyParser=require('body-parser')
const cors=require('cors')
const paymentRoute=require('./paymentRoute')
const eurekaHelper = require('./eurekaHelper');

const config = require('./config');

const MongoClient = require('mongodb').MongoClient;
var client = new MongoClient(config.database.uri,{ useUnifiedTopology: true}); 

app.use(bodyParser.json())
app.use(cors())
app.use('/api',paymentRoute);



let port = config.server.port;

eurekaHelper.registerWithEureka(config.application.name, config.server.port);
app.listen(port,async()=>
{
    console.log(`APP IS RUNNING AT ${port}`)
    await client.connect();
     let coll = await client.db(config.database.db_name).collection(config.database.collection_name);
     app.coll=coll;
    console.log('database connected Successfully!!!');
   
})

