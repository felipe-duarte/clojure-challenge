# clojure-challenge

Reward System Clojure Code Challenge. An introduction to the problem is given [here](doc/intro.md "intro") .

The system was built with Clojure, using [lein](http://leiningen.org/ "Lein") build tool, [ring](http://ring-clojure.github.io/ring/index.html "Ring") and [compojure](http://weavejester.github.io/compojure/ "Compojure")  libraries for network related parts.

The solution creates a tree data structure using an atom. Single nodes are defined as 
	
	key => {:cid :score :level :inviter :confirmed}

When a new valid invitation is added to the tree, system verifies if it confirms an invitation, and if so, recalculate score rank for each node in the subtree up to root.

## Build

Download source-code from github.
URL : https://github.com/felipe-duarte/clojure-challenge

Install lein.

## Usage
To run with a given input file:   

	lein run resources/input.txt

To run with default example file under resources:   

	lein run


Tests for services where built using [ring-mock](https://github.com/ring-clojure/ring-mock "Ring-Mock") . To run tests:

	lein test

To run as stand alone Java app with generated uberjar - (in that case input file must be given as execution parameter):

	lein uberjar
	java -jar clojure-challenge.jar input.txt 


## RESTful API

The system runs Jetty-Adapter on default port 8080 and Compojure libraries are used to define routes for the API service. We also use Ring-Middleware to wrap request and responses. There are two main endpoints 

*   GET /api/rank
*   POST /api/invite/:inviter/:invitee

Application context root URL "/" also redirects to "/api/rank".

## Endpoints

### GET

URL 

	/api/rank 

Response 

	{	
	:status 200,
	:header {"Content-Type" "application/json"} 
	:body [ {"cid": 1, "score": 2.5},
    		  {"cid": 3, "score": 1},
    		... 
    } 

### POST

URL   

	/api/invite/:inviter/:invitee  
 
Params
 
	inviter , invitee -- must be valid positive integers
   
Response   

	{  
	:status 200,
	:headers {"Content-Type" "text/html ; charset=utf-8"},
	:body sucess-msg
	}
