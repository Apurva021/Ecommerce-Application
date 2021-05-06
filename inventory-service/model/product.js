const mongoose= require('mongoose');
const productSchema = new mongoose.Schema({

    productId:{
        type:String,
        required:true,
        unique:true
    },
    name:{
        type:String,
        required:true,
        trim:true
    },
    slug:{
        type:String,
        required:true,
        unique:true
    },
    description:{
        type:String,
        required:true,
        trim:true
    },
    quantity:{
        type:Number,
        min:0,
        default:0
    },
    price:{
        type:Number,
        required:true,
        min:0
    },
    offerId:{
        type:String
    },
    productPictures:[
        {img:{type:String}}
    ],
    category:{
        type:mongoose.Schema.Types.ObjectId,
        ref:'Category'
    },
    sellerId:{
        type:String,
        required:true
    },
    inStock:{
        type:Number
    }
});

module.exports= mongoose.model('Product', productSchema, "product");