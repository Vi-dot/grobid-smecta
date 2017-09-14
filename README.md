<div>
	<img src="src/main/webapp/main/src/assets/icon.png">
	<span>grobid-smecta</span>
</div>

GROBID GUI to manage training data tasks.
<br />
Extension for GROBID [kermitt2/grobid](https://github.com/kermitt2/grobid)

grobid-smecta
SMECTA : Safe ManagemEnt and Curation of Training datA

## Screenshots

<a url="screenshots/files-editor.png" style="margin-right:10px;"><img src="screenshots/files-editor.png" height="200"></a>
<a url="screenshots/new-training.png" style="margin-right:10px;"><img src="screenshots/new-training.png" height="200"></a>
<a url="screenshots/trainings.png"><img src="screenshots/trainings.png" height="200"></a>

## Requirements

- Java
- Maven
- Grobid

### Java

grobid and grobid-smecta need OpenJDK 1.8

### Grobid

https://github.com/kermitt2/grobid


## Configuration

Set your paths in properties file `grobid-smecta/src/main/resources/grobid-smecta.properties`
<br />
Specially `grobid.smecta.trainingFiles.mainDirectory`

In your project, make your Trainer extends `SmectaAbstractTrainer`
<br />
Add dependency in `grobid-YOUR_MODULE/pom.xml`
```
<dependency>
    <groupId>org.grobid</groupId>
    <artifactId>grobid-smecta</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Edit dependency to your project `grobid-smecta/pom.xml`
```
<dependency>
    <groupId>org.grobid</groupId>
    <artifactId>grobid-YOUR_MODULE</artifactId>
    <version>0.4.3-SNAPSHOT</version>
</dependency>
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

## Install

`> mvn clean install`


## Usage

### Start Webservice API

`> mvn jetty:run-war`

Webservice API is now available at `http://localhost:5100/api`

### Start Webapp

`> mvn exec:exec -Pclient-prod`

Wepapp is now available at `http://localhost:5200`

A proxy is made to api at `http://localhost:5200/api`

## Developpment

### Server part (java)

To build sources, instead of using `mvn clean install`, you should use `mvn compile`, then you avoid to install again client tools.

### Client part (angular)

To have only local access, and a dynamic building, instead of using `mvn exec:exec -Pclient-prod`, you should use `mvn exec:exec -Pclient-dev`.

