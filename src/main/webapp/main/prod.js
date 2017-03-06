var express = require('express'),
  path = require('path'),
  fs = require('fs'),
  httpProxy = require('http-proxy');

require('dotenv').load();

var app = express();
var staticRoot = __dirname + '/dist/';

app.set('port', process.env.APP_PORT);

app.use(express.static(staticRoot));

app.use(function(req, res, next){

  // if the request is not html then move along
  var accept = req.accepts('html', 'json', 'xml');
  if(accept !== 'html'){
    return next();
  }

  // if the request has a '.' assume that it's for a file, move along
  var ext = path.extname(req.path);
  if (ext !== ''){
    return next();
  }

  fs.createReadStream(staticRoot + 'index.html').pipe(res);

});

var apiProxy = httpProxy.createProxyServer();

app.all('/admin/api/*', function(req, res) {
  apiProxy.web(req, res, {target: process.env.SERVER_HOST});
});

app.listen(app.get('port'), function() {
  console.log('app running on port', app.get('port'));
});
