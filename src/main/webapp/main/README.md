# grobid-smecta webapp

Webapp

## Requirements

- grobid-smecta webservice
- node, npm
- angular-cli

`sudo npm install -g @angular/cli`

## Configuration
Config files :
- **proxy.conf.json**, (dev) proxy
- **package.json**, (dev) port
- **.env**, (prod) proxy + port

## Development
Install dependencies

`npm install`

Start development server :

`npm start`

Then application is available at `http://localhost:5200/`.
<br />
Auto-refresh when code is changed.

To easy create components and services, use **angular-cli**.
<br />
Example : `ng g component my-new-component`

## Production

To build project in production mode :

`npm run prod`

Generated files are in directory **/dist**.
<br />
Production server works with **node.js** and **express**, using script `prod.js`

## Usage

### Trainer

Add your training files in directory **/grobid-astro/resources/dataset/astro/corpus/**
