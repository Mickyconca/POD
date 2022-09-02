# POD Maven Archetype

## Synopsis
Multiproject maven archetype. Generates a main project with 3 modules

* api
* server
* client

The last 2 dependant on the first.

Also the pom contains the dependencies to several common libraries:

* junit
* slf4j
* log4j
* mockito

and the maven plugin:

* assembly


## Installation

To install the archetype into your local catalog run:

* mvn install 


## API Reference

Once installed  run:  

* mvn archetype:generate -DarchetypeCatalog=local

This will guide you to generate the project
