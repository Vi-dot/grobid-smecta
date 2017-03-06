# grobid-smecta (Work in progress)

GROBID GUI to manage training data tasks.
<br />
Extension for GROBID [kermitt2/grobid](https://github.com/kermitt2/grobid)

grobid-smecta
SMECTA : Safe ManagemEnt and Curation of Training datA

## Next steps

- Documentation
- Replace grobid-astro link by generic
- Launch webapp from maven

## Requirements

- Java
- Maven
- Grobid
- MongoDB 

### Java

grobid and grobid-smecta need OpenJDK 1.8

### Grobid

https://github.com/kermitt2/grobid
temp:
https://github.com/kermitt2/grobid-astro

### Mongodb

https://docs.mongodb.com/v3.0/installation/


## Configuration


Create a directory for Mongodb data.

`grobid-smecta/$ mkdir data`

Create a directory for training files deleted.

`grobid-smecta/$ mkdir trainingTrash`

### WebApp

See [Webapp documentation](src/main/webapp/main)

## Usages

### Start MongoDB

Run this command

`grobid-smecta/$ mongod --dbpath ./data --port 27042`

Or use the script

`grobid-smecta/$ ./db_start.sh`

### Start Webservice

`grobid-smecta/$ mvn jetty:run-war`

### Start Webapp

See [Webapp documentation](src/main/webapp/main)
