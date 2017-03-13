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

### Mongodb

https://docs.mongodb.com/v3.0/installation/


## Configuration


Create a directory for Mongodb data.
<br />
`grobid-smecta/$ mkdir data`
<br /><br />

Create a directory for training files deleted.
<br />
`grobid-smecta/$ mkdir trainingTrash`
<br /><br />

Set your paths in properties file `grobid-smecta/src/main/resources/grobid-smecta.properties`
<br />
Specially `grobid.smecta.trainingFiles.mainDirectory`

In your project, make your Trainer extends `SmectaAbstractTrainer`
<br />
Add dependency in your `pom.xml`
```
<dependency>
    <groupId>org.grobid</groupId>
    <artifactId>grobid-smecta</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Extends your Trainer
```
public class YourTrainer extends SmectaAbstractTrainer {
	[...]
}
```

Edit `grobid-smecta/src/main/java/org/grobid/service/main/trainer/TrainerMain.java`
```
public static void main(String[] args) {

	YourTrainer trainer = new YourTrainer();
	
	try {
		TrainerService.runTrainer(args, trainer);
	} catch (Exception e) {
		e.printStackTrace();
	}
}
```


### WebApp

See [Webapp documentation](src/main/webapp/main)

## Usages

### Start MongoDB

Run this command
<br />
`grobid-smecta/$ mongod --dbpath ./data --port 27042`
<br /><br />
Or use the script
<br />
`grobid-smecta/$ ./db_start.sh`

### Start Webservice

`grobid-smecta/$ mvn jetty:run-war`

### Start Webapp

See [Webapp documentation](src/main/webapp/main)
