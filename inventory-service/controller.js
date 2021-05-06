const Category = require("./model/category");
const slugify = require("slugify")
const Product = require('./model/product');
exports.ping=(req,res)=>{
    res.send("Alive");
}


exports.getProductBySlug=(req,res)=>{
    let slug=  req.params.slug;

    Category.findOne({slug:slug}).exec((err,category)=>{
        if(err){
            res.status(400).json({err})
        }else{
            if(category){
                Product.find({category:category._id}).exec((err,products)=>{
                    if (err) {
                        return res.status(400).json({ error });
                      }
            
                     
                        if (products.length > 0) {
                          res.status(200).json({
                            products,
                            priceRange: {
                              under299: 299,
                              under599: 599,
                              under999: 999,
                              under1999: 1999,
                              under5999: 5999,
                            },
                            productsByPrice: {
                                under299: products.filter((product) => product.price <= 299),
                                under599: products.filter(
                                (product) => product.price > 299 && product.price <= 599
                              ),
                              under999: products.filter(
                                (product) => product.price > 599 && product.price <= 999
                              ),
                              under1999: products.filter(
                                (product) => product.price > 999 && product.price <= 1999
                              ),
                              under5999: products.filter(
                                (product) => product.price > 1999 && product.price <= 5999
                              ),
                            },
                          });
                        }
                      
                })
            }
        }
    })
}

exports.createProduct=(req,res)=>{
    const {name, description, price,quantity,category } = req.body;
    let productPictures = [];
    if (req.files.length > 0) {
        productPictures = req.files.map((file) => {
          return { img: file.filename };
        });
      }

     const product = new Product({
    name: name,
    slug: slugify(name),
    price,
    quantity,
    description,
    productPictures,
    category,
    sellerId: req.user.id,
  });

  product.save((error, product) => {
    if (error) return res.status(400).json({ error });
    if (product) {
      res.status(201).json({ product, files: req.files });
    }
  });

}
exports.createCategory=(req,res)=>{

    if(req.user.isSeller){
        let categoryObj={
            name:req.body.name,
            slug:slugify(req.body.name)
        }
        if(req.body.parentId){
            categoryObj.parentId = req.body.parentId;
        }
        if(req.file){
            categoryObj.categoryImage = req.file.filename;
        }
        const cat = new Category(categoryObj);
       cat.save((err,category)=>{
           if(err)
           {res.status(400).send({"error": err})}
           else{
            res.status(201).json({category});
           }
       })
    }else{
        res.status(401).send({message:"Only Seller can create categories !!"});
    }
   
}


exports.getCategories=(req,res)=>{
    Category.find().exec((err,categories)=>{
        if(err){
            res.status(400).send({"error": err})
        }else{
           let categoryList = createCategoryTree(categories);

           res.status(200).send({categoryList});
        }
    })
}
exports.deleteCategory = async (req, res) => {
    
    if(req.user.isSeller){
       
        
       
          const deleteCategory = await Category.findOneAndDelete({
            _id: req.body.id,
          });
         
      
        if (deleteCategory) {
          res.status(201).json({ message: "Categories removed" });
        } else {
          res.status(400).json({ message: "Something went wrong" });
        }
    }else{
        res.status(401).send({message:"Only Seller can create categories !!"});
    }
    
  
  };

exports.updateCategory = async (req, res) => {
  
    const { id, name, parentId } = req.body;
   
    
  
        const category = await Category.findOne({ _id: id });
        
        if(name){
            category.name = name;
            category.slug=slugify(name);
        }
          if (parentId) {
            category.parentId = parentId;
          }

        const updatedCategory = await Category.findOneAndUpdate(
          { _id: id },
          category,
          { new: true }
        );
      
      
      return res.status(201).json({ updateCategory: updatedCategory });
   
    
    
  };


function createCategoryTree(categoryList,parentId=null){
    let categoryTree = []
    let filteredCategories;
    if(parentId==null){
        filteredCategories=categoryList.filter(cat=>cat.parentId == undefined);
    }else{
        filteredCategories=categoryList.filter(cat => cat.parentId == parentId);
    }

    for(let category of filteredCategories){
        categoryTree.push({
            _id:category._id,
            name: category.name,
            slug: category.slug,
            children: createCategoryTree(categoryList,category._id)
        })
    }

    return categoryTree;
    
}