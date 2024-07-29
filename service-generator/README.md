service-generator module lets you create following

 - microservice module
 - kafka consumer module
 - kafka producer module
 - utility module
 
 For building the module you need to run following gradle task
 
    ./gradlew :service-generator:generate -PmoduleType=microservice -PmoduleName=my-module
 
 
  moduleType can have one of the values from microservice, util, consumer, producer.
  
  moduleName can be any name for your module. It should be unique most preferably it should be in following way
  
  - microservice : inventory-service (service can identify its a microservice)
  - consumer : inventory-consumer (service can identify its a kafka consumer)
  - producer : inventory-consumer (service can identify its a kafka producer)
  - util : inventory-util, inventory-module etc.. (util or module can identify its a helper module)
  