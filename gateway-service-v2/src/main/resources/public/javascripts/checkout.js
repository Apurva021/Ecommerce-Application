

const style = {
  base: {
    color: "#32325d",
    fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
    fontSmoothing: "antialiased",
    fontSize: "16px",
  },
  invalid: {
    color: "#fa755a",
    iconColor: "#fa755a",
  },
};





// Handle form submission.
const $form = $("#checkout-form");

$form.submit(function (event) {
  event.preventDefault();
  $form.find("button").prop("disabled", true);
  //console.log($("#receiptId").text());
  //let id = "orId"+new Date().getTime();
  const extraDetails = {
    totalBill: $("#total-bill").val(),
    email:$("#card-email").val(),
    receiptId:$("#receiptId").text(),
    mobileNo:$("#mobile-number").val()
  };
// console.log(event)
  getData(extraDetails).then(response=>{
 
    var information={
        action:"https://securegw-stage.paytm.in/order/process",
        params:response
    }
    //console.log(response);
  post(information)

})

 
});




const getData=(data)=>
  {

    return fetch(`http://localhost:5000/api/payment`,{
        method:"POST",
        headers:{
            Accept:"application/json",
            "Content-Type":"application/json"
        },
        body:JSON.stringify(data)
    }).then(response=>response.json()).catch(err=>console.log(err))
  }
 
function buildForm({ action, params }) {
  const form = document.createElement('form')
  form.setAttribute('method', 'post')
  form.setAttribute('action', action)

  Object.keys(params).forEach(key => {
    const input = document.createElement('input')
    input.setAttribute('type', 'hidden')
    input.setAttribute('name', key)
    input.setAttribute('value', stringifyValue(params[key]))
    form.appendChild(input)
  })

  return form
}

 function post(details) {
  const form = buildForm(details)
  document.body.appendChild(form)
  form.submit()
  form.remove()
}

function isDate(val) {
  // Cross realm comptatible
  return Object.prototype.toString.call(val) === '[object Date]'
}

function isObj(val) {
  return typeof val === 'object'
}

 function stringifyValue(val) {
  if (isObj(val) && !isDate(val)) {
    return JSON.stringify(val)
  } else {
    return val
  }
}


