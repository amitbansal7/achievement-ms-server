# Server for achievement management system
 
## Tech stack:
 * Scala
 * akka-http
 * MongoDB

## Getting Started
1. Install JDK 1.8
2. Install sbt from [here](https://www.scala-sbt.org/)
3. run ```$ sbt ``` on the root directory.
4. Install MongoDB from [here](https://docs.mongodb.com/manual/installation/)
5. Run MongoDB using ```mongod``` and in the new tab create mongo client using ```mongo```.
6. On the mongo client console type ```use ams``` to create an empty database required for this server.
7. After downloading all the necessary dependencies is done, type ```run``` on the sbt console.
8. Go to ```http://localhost:8090/``` and you should see "Server is up and running..".



### Frontend angular application for this server can be found [here](https://github.com/BitsPleaseMSI/achievement-ms-frontend)


