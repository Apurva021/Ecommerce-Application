module.exports={
    database:{
        uri:'mongodb+srv://rutvik:17bit008@productcatalog.poqge.mongodb.net/PaymentDB?retryWrites=true&w=majority',
        db_name:'PaymentDB',
        collection_name:'payments'
    },
    "server":{
        "port":5000
    },
    eureka:{
        "host":"127.0.0.1",
        "port":8761,
        "hostname":"localhost",
        "ipAddress":"127.0.0.1",
        "enabled":true,
        "dataCenter":{
          "name":"MyOwn"
        },
        "servicePath":"/eureka/apps/",
        "maxRetries":5,
        "requestRetryDelay": 8000
      },
      "application":{
        "name":"payment-service"
      },
      jwtSecret:"secret"
}