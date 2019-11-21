# Buggy - A Demo App for Microservices

The goal of this project is to demonstrate a possible architecture for building microservices on top Spring Boot with Kotlin.
There won't be any fancy buisness logic but a full stack of technologies.
As an example the buggy-platfrom imitates an issue tracker like bugzilla or Jira.
***
Features:
* Create/Update/Delete of so called Basic-Data: 
    * Developers
    * Issues (Bugs and  Stories)
* Doing a Planning for estimated Stories

## How to build an run 

### Prerequirements
* Docker 
* Docker-Compose

### Build and Run
~~~bash
/bin/bash buildAndRun.bash
~~~

This will build the jar of each component and start the compose file.

### Hints
* On Linux you can run the script `/sudo ./addDNS_buggy.sh`. This will modify your local /etc/hosts file with the internal IPs of all services.
So you can access each component without any port forwaring.
* On first start the process will take several minutes for downloading 
    * the `gradle` wrapper
    * all dependencies
    * docker images (mongodb, rabbitmq...)

### Try-it-out
TODO

## The Microservice Architecture explained
  
### Domains
The app is divided in so called 'Domains'. Every domain is handled by it's own Microservice. Which means this service mangages and persists the data belonging to his domain and offering interfaces/endpoints to access the data and execute the buisiness logic.
In this example of an issue tracker there a 3 buisness domains:
* Developers: basic data about the developers 
* Issues: basic data about a the things need be done
* Planning: makeing a plan about doing all the work based on developers and issues

A domain-service can handle multiple entities and controller.

### Decoupling and Messaging
Every service can offer full functionality even if other services are not available. This is achieved via `view-data` and messaging.
#### View-Data
Every services persists all data needed to do it's work. In the case of the planning-service this means, it needs some data about the availably developers and data about the work about to be planned (estimated stories). It would be a very simple but also very bad idea to request all these data on demand via Rest or something simliar. Some disadvanteged would be:
* increasing workload (net-io) over time 
* synchronous and slow processes
* more memory consumption (the services would hold the data in-memory on every request)
* no functionality if only one of the other services would be down for whatever reason

To overcome these things the planning-service consumes events about the data he needs and persists this data in its own database.

#### Messaging
The services don't know each other. The `planning-service` has  no idea about the origin of the events about developers and issues. There is only a message broker. (RabbitMQ)

| Pattern        | Example           |
| ------------- |:-------------:|
|![](static/ProducerConsumerPattern.png)| ![](static/queues.png) 

Example:
When a new issue is created in the issues-service the service emits an event `IssueCreated` containing all properties of the just created issue.
The planning-service is listening with his own Queue on the "Issues" Exchange and consumes the event and it's data right away. The Planning-Service also validates and persists the needed parts/properties of the data in its own database. The both services have no knowledge of each other. They don´t share a database.

![](static/MessagingExample1.png) 

TODO: Replay and REST Fallback

### Blackbox
The endpoints and interfaces of all services are gathered and made public via a an api-gateway. A client (like any user interface or web app) would only access the endpoints via this gateway. Never directly via one of the services. This way it is very easy to add modify or delete the underlying services without changing the API in any way.

TODO: add HATEOS

### Resilience

#### self recovering
If a service comes down for a reboot or a new deployment  for example, the consuming of the messages would stop. Which means the message broker will store them
in the queues until the consuming services comes up again. There is no lost of data.

#### retry
If a service is unable to handle an event/message. For example the database is down or the network has issues. The message handler will throw an exception.
This means the message will be send to a so called  "dead letter queue". The services are configured to re-load all messages from this DLQ after some seconds until the
message can be handled correctly. 

### Scaling
Because every event/message can only be consumed by one instance of a service and all instances of one service share the same database there is not reason for manual 
synchronisation. It is possible to spawn more instances on demand because they are stateless.
Default all services are scaled to 1. This can be changed in the compose file (replicas=) or in CLI ```docker-compose up scale buggy-developer-service=2```

![](static/scaled.png) 

### Logging and Monitoring

#### Logging
None of the services uses manual logging. There is not a single instance of any Logger. Beside that the services use the out-of-the box logging of Spring Boot.

TOOD: configure meaningful logging level

#### History
The services handling the basic data domains are using [Javers](https://javers.org/) to persist informations about all changes made on the data. Including what was changed and how.

![](static/javers_update_with_author.png) 

#### Distributed Logging
One of the hardest things when doing a distributed microservice driven project is debugging and analysing of the workflows. This is achieved via Sleuth.
Every request (Rest and Messaging) is enriched with a spanId and a traceId. The spanId is used to identify the workflow of a request inside one service. Even with heavy 
usage of multithreading and asynchronous processes. The traceId is used to track one request between multiple services.
In most cases it is a wise idea to store all log messages of all services and instances in a central log storage like an ELK stack.

TODO: add ELK

#### Zipkin
[Zipkin](https://zipkin.io/) is a wonderful tool to visualize distributed requests inside a microservice system. 
Here is an example of a creation of a new developer over the gateway in the developer-service. The DeveloperCreated Event is consumed by the planning-service.

![](static/Screenshot_Zipkin.png) 


#### Metrics 
TODO: add prometheus / grafana

#### Monitoring / Maintenance
A first stepstone in monitoring a microservice system build with Spring Boot would be [Spring Boot Admin](https://codecentric.github.io/spring-boot-admin/current/).

![](static/Screenshot_sba.png) 


### Security
The services are secured via OAuth2 (OpenId). The authorization Server is done with [Keycloak](https://www.keycloak.org/).
The REST Endpoints require full authentification with an User with the role "buggy_ui".
The implementation is entirly done with Spring  Security and not with the Spring Keycloak Adapter. This makes some modifications necessary.
- Realm: buggy
    - Roles: new Role: ROLE_BUGGY_UI
- Client: buggyui
    - the client secret is generated by keycloak automaticaly
    - resourceid: buggyui
    - add realm role "ROLE_BUGGY_UI" to the client
    - Mappers:
        - aud: Audience --> this maps to the resourceId in Spring Security
        - authorities: User Realm Roles --> this maps to the effective User Roles in Spring Security
        - user_name: User Attribut: user_name --> this maps to the principal aka. loged in user in Spring Security 


|ResourceID|Roles|Username Client|Username User|        
|----|-----|----|----|
|![](static/keycloak_buggy_client_mappers_resourceid.png) |![](static/keycloak_buggy_client_mappers_roles.png)|![](static/keycloak_buggy_client_mappers_username.png)|![](static/keycloak_buggy_user_attributes_principal.png)|        

The Mappers are necessary because Spring Security searches the Roles in the claim "authorities". The default in Keycloak is "realm_access.roles".
Beside that Spring Security reads the resource ID from the claim "aud". Without the mapper, der authorization would fail.

The REST API can by called with a bearer Token. Here is Screenshot from [Insomnia](https://insomnia.rest/) as an example.

![](static/insomnia_path_dev_with_oauth2_usercreds.png) 


TODO: TLS 

#### Auditing
The services handling the basic data domains are using [Javers](https://javers.org/) to persist informations about which principal made which changed when.

![](static/javers_update_with_author.png) 


#### TLS 
TODO: add TLS encryption