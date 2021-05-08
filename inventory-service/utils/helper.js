const Product = require('../model/product');
const Category = require("../model/category");
const fs = require('fs');
const path= require('path')

exports.getProducts = async function(subcategories){
    var products=[];
   for(let i=0; i<subcategories.length; i++){
        let product = await Product.find({category:subcategories[i]}).limit(3).populate("category");
       
        product.forEach(p=>products.push(p));
    }
    //console.log(products);
    return products;
  }


exports.getFilter=async function(products){
  let filter = {
    brand: new Set(),
    color: new Set(),
    size: new Set(),
    price:{
      min: Number.MAX_VALUE,
      max: 0
    },
    material: new Set(),
    suitedFor: new Set(),
    category: new Set()
  }

  await products.forEach(product=>{
      
    filter.category.add(product.category.title);
    filter.brand.add(product.feature.brand);
    filter.color.add(product.feature.color);
    filter.material.add(product.feature.material);
    filter.suitedFor.add(product.feature.suitedFor);
    product.sizes.forEach(size=> size.stock>0? filter.size.add(size.value) : "");
  
    if(product.price <filter.price.min){
      filter.price.min = Math.floor(product.price);
    }
    if(product.price >filter.price.max){
      filter.price.max = Math.ceil(product.price);
    }
  })

  
  return filter;
}

exports.deleteImgFile = async function (imgPath) {
  let filePath = path.join(path.dirname(__dirname) + "/uploads/" + imgPath)
  try {
    fs.unlinkSync(filePath);
  } catch (e) {
    console.log(e.message);
  }
}