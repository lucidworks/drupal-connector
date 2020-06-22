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
This artifact is now ready to be uploaded directly to Fusion as a connector plugin.

### Connector properties
This connector is using the `connector-plugin-sdk` version `2.0.1` which is compatible with Fusion Server v5.

###The Fetcher
The JsonContentFetcher class provides methods that define how data is fetched and indexed for Fusion Server.

###Dependencies
