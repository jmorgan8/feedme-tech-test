## Technical Choices

* Java Spring Boot - What I currently work with and most familiar technology
* A working version of the service was pushed to my own docker account, which is then used in the docker-compose.yml
* Stopping before implementing the advanced tasks as I'm conscious of time. However, my plan would be to use RabbitMQ and do the following:
  * Create four queues; create-fixture, update-fixture, create-fixture-parking-lot and update-fixture-parking-lot
    * The parking lot queues would hold any failed messages and potentially be submitted. Logic would need to exist to check the Unique ID from the header and whether the latest insert is beyond that. We don't want to put in an out-of-date update
    * The create-fixture would handle creating events
    * The update-fixture would handle updates to the entire event, market and/or outcome (not sure on the create/update queues as we need to consider if an update could be processed before a create)
    * The existing service would be the 'MessageSender'. A new 'copy' of the service would be the 'MessageReceiver' and do the insert/updates where necessary
      * Would look to restructure the document, possibly adding in latest header ID as a field so we know what the latest update to a document was
      * Could also have a new document type to store header values, which could be useful for audit purposes

## General Comments

After watching the feed and seeing the different fixtures coming in, I made the following assumptions:
* When operation is 'create' and type is 'event', all markets and outcomes follow. So I build up the entire document before inserting into the DB
* Outcomes can be updated independently
  * This was the main reason for denormalising the data, as I struggled to make the relevant updates to the document when I started with nested objects. perhaps with more time understanding the MongoDB syntax, I could've figured it out


* I left the services running for 15 minutes with no issues


* I encountered an issue as I was using Java 17 to build, but got an error when starting up the service through docker-compose
  `java.lang.UnsupportedClassVersionError: org/bson/codecs/record/RecordCodecProvider has been compiled by a more recent version of the Java Runtime (class file version 61.0), this version of the Java Runtime only recognizes class file versions up to 58.0`
* I installed Java 11 to build the service, which resolved the issue
  
The service is very much an MVP. In an enterprise solution it should have:
* Vulnerability Scanning (e.g OWASP)
* Code Formatter (e.g spotless)
* Test coverage (e.g Jacoco)


## Instructions to run

#### Prerequisites
* Install Docker and Docker Compose: https://docs.docker.com/compose

#### Steps

If running locally in a terminal: `./gradlew bootRun`

To run all services:

* To start the mock data feed, MongoDB and Service, type `docker-compose up` in the root of the test directory
* To check entries in DB:
  * Find container ID for mongo: `docker ps`
  * Go into container and run some queries: `docker exec -it <CONTAINER_ID> mongo`
  * Run queries like:
    * `db.fixture.find()`
    * `db.fixture.find({ eventId: “<an eventId>” })`
    * `db.fixture.count()` - get total number of entries