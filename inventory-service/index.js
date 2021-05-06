const express=require('express')
const jwt = require('jwt-simple');
const app=express()
const cors=require('cors')
const controller=require("./controller");
const config = require('./config');
const mongoose = require('mongoose');
const multer = require("multer");
const path = require("path")
const shortid=require("shortid")
const kafkaListner = require("./KafkaListner");
//========================FILE_UPLOADS===========================================================

const storage = multer.diskStorage({
    destination: function (req, file, cb) {
      cb(null, path.join(path.dirname(__dirname), "ProductCatalogUtilService/uploads/"));
    },
    filename: function (req, file, cb) {
      cb(null, shortid.generate() + "-" + file.originalname);
    },
  });

  const upload = multer({ storage:storage }).array('productPicture',6);
  const upload1 = multer({storage}).single('categoryImage');
  const uploadMiddelware = (req,res,next)=>{

    if(req.body.category){
        upload(req,res, (err)=>{
          if(!err){
              next();
          }
      }); 
    }else{
        upload1(req,res, (err)=>{
            if(!err){
                next();
            }
    });
  }
}
//========================JWT-DECODER===========================================================
var jwtDecode = (req,res,next)=>{
    if (req.headers && req.headers.authorization) {
        var authorization = req.headers.authorization.split(' ')[1],
            decoded;
           // console.log(authorization)
        try {
            decoded = jwt.decode(authorization, config.jwtSecret, true);
        } catch (e) {
            return res.status(401).send(e.message);
        }
        
       
        req.user={
            id:decoded.payload,
            isSeller:decoded.isSeller,
            email:decoded.sub,

        }        
        }
    next();
}
    //==================================APPLY-MIDDLEWARE==============================================

// app.use(jwtDecode)
app.use(cors())
app.use(express.json())
app.use(express.urlencoded())


//==================================DEFINE-ROUTES==============================================

app.get("/",controller.ping);
app.post("/category/create",jwtDecode,uploadMiddelware, controller.createCategory);
app.post("/category/delete",jwtDecode, controller.deleteCategory);
app.post("/category/update",jwtDecode, controller.updateCategory);
app.get("/api/category/getcategory", controller.getCategories);
app.post("/product/create",jwtDecode,uploadMiddelware,controller.createProduct);
app.get("/products/:slug", controller.getProductBySlug);


//==================================SERVER-START===============================================
let port = config.server.port;
mongoose.connect(config.db.uri,
    { 
        useNewUrlParser: true,
        useUnifiedTopology: true,
        useCreateIndex: true,
        useFindAndModify: false,

     },(err)=>{
    if(err)
    {console.log( err)}
    else{
        
        console.log("Databse Connected Sucessfully !!!");
        kafkaListner.kafkaSubscribe(config.kafka.topics.inventory);
    }
});

app.listen(port,async()=>{
    console.log(`APP IS RUNNING AT ${port}`)    
})




