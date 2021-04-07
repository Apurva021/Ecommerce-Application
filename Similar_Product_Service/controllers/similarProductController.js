const conf = require('../config.json');




// exports.ping = async(req,res)=>{
//     res.send("working fine");
// }

exports.getSimilar = async(req,res)=>{
  try{
    const agg =conf.searchAggragate.similarity;
    if(!req.query.id){
      res.status(400).send({"message":"Product id missing in request"});
    }else{
      

      // let instance = client.getInstancesByAppId('productcatalog');
      // let uri="http://"+instance[0].hostName+":"+instance[0].port.$+"/product?id="+req.query.id;
     
      // var temp = await axios.get(uri)
      
      var coll = req.app.coll;
      await coll.findOne({"productId":req.query.id}).then(result=>{
        agg[0].$search.text.query=result.longDescription;
      });
      

    //   agg[0].$search.text.query=temp.data[0].longDescription;
       agg[4].$match.productId.$ne=req.query.id;
    //  // console.log(agg[0].$search.text.query);
      coll.aggregate(agg).toArray()
                          .then(result=>{
                            res.send(result);
                             //console.log(result[0]);
                            //client.close();
                             console.log("ProdcutId- " + req.query.id + " result found-" + result.length);
                            
                          });
      
    }
  }catch(e){
    res.status(500).send({message:e.message});
  }
}



