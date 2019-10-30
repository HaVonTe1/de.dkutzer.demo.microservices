# Buggy - A Demo App for Microservices

The goal of this project is to demonstrate a possible architecture for building microservices on top Spring Boot with Kotlin.
There won't be any fancy buisness logic but a full stack of technologies.
As an example the buggy-platfrom imitates an issue tracker like bugzilla or Jira.
***
Features:
- Create/Update/Delete of so calles Basic-Data: 
⋅⋅⋅Developers
⋅⋅⋅Issues (Bugs and  Stories)
- Doing a Planning for estimated Stories

## How to build an run 

### Prerequirements
- Docker 
- Docker-Compose

### Build and Run
~~~bash
/bin/bash buildAndRun.bash
~~~

This will build the jar of each component and start the compose file.

### Hints
- On Linux you can run the script `/sudo ./addDNS_buggy.sh`. This will modify your local /etc/hosts file with the internal IPs of all services.
So you can access each component without any port forwaring.
- On first start the process will take several minutes for downloading 
-* the `gradle` wrapper
-* all dependencies
-* docker images (mongodb, rabbitmq...)

### Try-it-out
TODO

## The Microservice Architecture explained
  
### Domains
The app is divided in so called 'Domains'. Every domain is handled by it's own Microservice. Which means this service mangages and persists the data belonging to his domain and offering interfaces/endpoints to access the data and execute the buisiness logic.
In this example of an issue tracker there a 3 buisness domains:
- Developers: basic data about the developers 
- Issues: basic data about a the things need be done
- Planning: makeing a plan about doing all the work based on developers and issues
A domain-service can handle multiple entities and controller.

### Decoupling and Messaging
Every service can offer full functionality even if other services are not available. This is achieved via `view-data` and messaging.
#### View-Data
Every services persists all data needed to do it's work. In the case of the planning-service this means, it needs some data about the availably developers and data about the work about to be planned (estimated stories). It would be a very simple but also very bad idea to request all these data on demand via Rest or something simliar. Some disadvanteged would be:
- increasing workload (net-io) over time 
- synchronous and slow processes
- more memory consumption (the services would hold the data in-memory on every request)
- no functionality if only one of the other services would be down for whatever reason
To overcome these things the planning-service consumes events about the data he needs and persists this data in its own database.

#### Messaging
The services don't know each other. The `planning-service` has  no idea about the origin of the events about developers and issues. There is only a message broker. (RabbitMQ)

![](static/queues.png) 

Example:
When a new issue is created in the issues-service the service emits an event `IssueCreated` containing all properties of the just created issue.
The planning-service is listening with his own Queue on the "Issues" Exchange and consumes the event and it's data right away. The Planning-Service also validates and persists the needed parts/properties of the data in its own database. The both services have no knowledge of each other. They don´t share a database.

![](static/MessagingExample1.png) 


### Blackbox
TODO: add an api-gateway
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
synchronisation. It is possible so spawn more instances on demand because they are stateless.

### Logging and Monitoring

#### Logging
None of the services uses manual logging. There is not a single instance of any Logger. Beside that the services use the out-of-the box logging of Spring Boot.
TOOD: configure meaningful logging level

#### History
The services handling the basic data domains are using [Javers](https://javers.org/) to persist informations about all changes made on the data. Including what was changed and how.

#### Distributed Logging
One of the hardest things when doing a distributed microservice driven project is debugging and analysing of the workflows. This is achieved via Sleuth.
Every request (Rest and Messaging) is enriched with a spanId and a traceId. The spanId is used to identify the workflow of a request inside one service. Even with haevy 
usage of multithreading and asynchronous processes. The traceId is used to track one request between multiple services.
In most cases it is a wise idea to store all log messages of all services and instances in a central log storage like an ELK stack.

#### Metrics 
TODO: add prometheus / grafana

### Security
TODO: add Keycloak/spring security

#### Auditing
The services handling the basic data domains are using [Javers](https://javers.org/) to persist informations about which principal made which changed when.

#### TLS 
TODO: add TLS encryption