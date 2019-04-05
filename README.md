# Server for Achievement Management System

## Tech stack:
 * Scala
 * akka-http
 * MongoDB

## Getting Started
1. Install JDK 1.8
2. Install sbt from [here](https://www.scala-sbt.org/)
3. run ```$ sbt ``` on the root directory of the project.
4. Install MongoDB from [here](https://docs.mongodb.com/manual/installation/)
5. Run MongoDB using ```$ mongod``` and in the new tab create mongo client using ```$ mongo```.
6. On the mongo client console type ```use ams``` to create an empty database required for this server.
7. After downloading of all the necessary dependencies is done, type ```> run``` on the sbt console.
8. Go to ```http://localhost:8080/``` and you should see "Server is up and running..".


##### Frontend deployment : [ams](https://bitspleasemsi.github.io)
##### API deployed on aws : [ams](http://amsmsi.com)
##### Frontend code written in angular can be found [here](https://github.com/BitsPleaseMSI/achievement-ms-frontend)


