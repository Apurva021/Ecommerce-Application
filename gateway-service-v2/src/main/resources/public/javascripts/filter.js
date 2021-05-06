$(document).ready(function () {

    const filter={
        price:{
            min:0,
            max:50000
        },
        brand:new Set(),
        color:new Set(),
        size:new Set()
    }

    

//    =================================== PRICE FILTER ============================================
    //Price Filter
   $(".price").change(function(){
        let min = parseFloat($("#minPrice").val());
        let max = parseFloat($("#maxPrice").val());
        
        if(min>max){
            // if($(this).attr("id")=="maxPrice"){
            //     $(this).val(min);
            // }else{
            //     $(this).val(max);
            // }
            $("#priceFilter span.filterError").text("*please enter valid rang of price");
        }else if($(this).val()<0){
           // $(this).val(0);
            $("#priceFilter span.filterError").text("*please enter positive price");
        }else{
            $("#priceFilter span.filterError").text("");
            //update the filter value
            filter.price = {    
                min,
                max
            }
        }


       // priceFilter();

       // console.log(filter.price)

   })

//    =================================== BRAND FILTER ============================================
   //Brand Filter
   $('#brandFilter input[type="checkbox"]').change(function(){
        
   
       if($(this).val()=='*'){
           $('#brandFilter input[type="checkbox"]').prop('checked',false);
           $(this).prop("checked",true);
           
       }else{
           $('#brandFilter #allbrand').prop("checked",false);
       }

       // get the selected filter values in the filter
       let set=new Set();
       $('#brandFilter input[type="checkbox"]:checked').each(function(){
            if($(this).val()!="*"){
                set.add($(this).val());
            }    
       })

       // if nothing is selected than show all values
       if(set.size==0){
        $('#brandFilter #allbrand').prop("checked",true);
       }

       filter.brand=set;    //update the filter value
     // brandFilter(set);

     
      // console.log(set);
   })


//    =================================== SIZE FILTER ============================================
   //size FIlter
   $('#sizeFilter input[type="checkbox"]').change(function(){



       if($(this).val()=='*'){
           $('#sizeFilter input[type="checkbox"]').prop('checked',false);
           $(this).prop("checked",true);
            
       }else{
           $('#sizeFilter #allsize').prop("checked",false);
       }

       // get the selected filter values in the filter
       let set=new Set();
       $('#sizeFilter input[type="checkbox"]:checked').each(function(){
            if($(this).val()!="*"){
                set.add($(this).val());
            }   
       })

       // if nothing is selected than show all values
       if(set.size==0){
            $('#sizeFilter #allsize').prop("checked",true);
       }
       
       
       filter.size=set; //update the filter value
       // sizeFilter(set);
      // console.log(set);
   })


//    =================================== COLOR FILTER ============================================
  //color Filter
  $('#colorFilter input[type="checkbox"]').change(function(){
   
       if($(this).val()=='*'){
           $('#colorFilter input[type="checkbox"]').prop('checked',false);
           $(this).prop("checked",true);
         
       }else{
           $('#colorFilter #allcolor').prop("checked",false);
       }

      
       // get the selected filter values in the filter
       let set=new Set();
       $('#colorFilter input[type="checkbox"]:checked').each(function(){
            if($(this).val()!="*"){
                set.add($(this).val());
            }   
       })

       // if nothing is selected than show all values
       if(set.size==0){
        $('#colorFilter #allcolor').prop("checked",true);
       }

       filter.color=set;
      // colorFilter(set)
       //console.log(set);
   })
  


//    =================================== EXPANTION CONTROLS ============================================
   //expantion controls
   $('.viewmore').click(function(){
      
       let t =$(this).attr("cs-filter")
       let id =$(this).attr('id');
       $("#"+id).hide();
       $("."+t).show();
       $("#"+t+"less").show();
      
   })
   $('.viewless').click(function(){

    let t =$(this).attr("cs-filter")
       let id =$(this).attr('id');
       $("#"+id).hide();
       $("."+t).hide();
       $("#"+t+"more").show();
   })


//    =================================== CLEAR ALL  ============================================
   $('#clearFilter').click(function(){
      
        $('#brandFilter #allbrand').trigger('change');
        $('#sizeFilter #allsize').trigger('change');
        $('#colorFilter #allcolor').trigger('change');
        $("#minPrice").val(0)
        $("#maxPrice").val(50000)
        filter.price.min=0;
        filter.price.max=50000;
        
   });


//    **********************************  COMBINE FILTERING **********************************************
   $('#nsidebar input').change(function(){
       var count=0;
        $("div.product-index-box").each(function(){
            // console.log(checkPrice($(this)));
            if(checkPrice($(this)) && checkBrand($(this)) && checkColor($(this)) && checkSize($(this))){
                $(this).show(500,"linear");
                count+=1;
            }else{
                $(this).hide(400,"linear");
            }
        })
        if(count==0){
            $("#not-found").show("fast")
        }else{
            $("#not-found").hide("fast")
        }
        //console.log(count);
   })

//======================================= checker utils =============================================
function checkPrice(obj){
    return parseFloat(obj.attr("cs-price"))>=filter.price.min && parseFloat(obj.attr("cs-price"))<=filter.price.max;
}

function checkColor(obj){
    return filter.color.size==0 || filter.color.has(obj.attr("cs-color"));
}

function checkSize(obj){
    let sizeArr = obj.attr("cs-size").split(" ");
    return filter.size.size==0 || sizeArr.filter(i => filter.size.has(i)).length != 0;
}

function checkBrand(obj){
    return filter.brand.size==0 || filter.brand.has(obj.attr("cs-brand"));
}
//======================================= ALL FILTERS =============================================
//                     ==================     Price Util    ==================
var priceFilter = ()=>{
    $("div.product-index-box[cs-price]").each(function(){
          
        if(checkPrice($(this))){
            $(this).show();
        }else{
            $(this).hide();
        }
    })
}

//                     ==================     Color Util    ==================

var colorFilter = (set) => {
    // show only those with the seleceted colors
    if (set.size > 0) {
        $("div.product-index-box[cs-color]").each(function () {
            if (checkColor($(this))) {
                $(this).show();
            } else {
                $(this).hide();
            }
        })
    } else {
        $("div.product-index-box[cs-color]").show()
    }
}

//                     ==================     Size Util    ==================

var sizeFilter = (set) => {
    // show only thoes that have atleast one size common with the selected ones
    if (set.size > 0) {
        $("div.product-index-box[cs-size]").each(function () {
            
            if (checkSize($(this))) {
                $(this).show();
            } else {
                $(this).hide();
            }
        })
    } else {
        $("div.product-index-box[cs-size]").show()
    }
}

//                     ==================     Brand Util    ==================

var brandFilter = (set) => {
    // show only those with the seleceted brands
    if (set.size > 0) {
        $("div.product-index-box[cs-brand]").each(function () {
            if (checkBrand($(this))) {
                $(this).show();
            } else {
                $(this).hide();
            }
        })
    } else {
        $("div.product-index-box[cs-brand]").show()
    }
}


});
