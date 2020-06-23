# Drupal8 Connector

## Connector Description

The Drupal Connector fetches all the content that is available on the URL provided from Drupal Module. Using an algorithm that takes all the links from every page, all the content from those pages is going to be parsed and normalised for Fusion Server, using the SDK plugin. 

## Quick start

### Clone the repo:
```
git clone https://github.com/lucidworks/drupal-connector.git
```

### Build the project:
```
cd drupal8
./gradlew clean build assemblePlugin
```
 This produces the zip file, named `drupal8.zip`, located in the `build` directory.
This artifact is now ready to be uploaded directly into Fusion Server as a connector plugin.

### Connector properties
This connector is using the `connector-plugin-sdk` version `2.0.1` which is compatible with Fusion Server v5.

####JSON:API
The content from Drupal URL has the JSON:API structure.

JSON:API is a specification for how a client should request that resources be fetched or modified, and how a server should respond to those requests.
JSON:API is designed to minimize both the number of requests and the amount of data transmitted between clients and servers. This efficiency is achieved without compromising readability, flexibility, or discoverability.

JSON:API requires use of the JSON:API media type `application/vnd.api+json` for exchanging data.

###The Fetcher
The _JsonContentFetcher_ class provides methods that define how data is fetched and indexed for Fusion Server. The data is fetched from Drupal using the OkHttp client to call the request. But before the actual request is done to get all the content a login request is needed. There are different types of users that can see the entire content or just a particular part from it.
 From the login response the header _Set-Cookie_ is taken and used as a header for the next requests.

##Dependencies
The most important dependency is the connector SDK. Beside this another needed dependency is a HTTP client in order to connect to a third-party REST API, in this project a Drupal Module.

###OkHttp
OKHttp is a HTTP client that is efficient by default. It supports HTTP/2 and allows all requests to the same host to share a socket. It's connection pooling reduces request latency. The response caching avoids the network completely for repeat requests.
Using OkHttp is easy. Its request/response API is designed with fluent builders and immutability. It supports both synchronous blocking calls and async calls with callbacks.

###Lombok 
Lombok is a java library that automatically plugs into your editor and build tools and replaces using annotations most of the code regarding getters, setters and even constructors.

