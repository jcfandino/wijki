Wijki wiki
==========

A bare bones wiki that is:

- Easy to deploy
- Java based
- Git backed history
- Markdown compatible

### Requirements

- Maven 3 (only to build)
- Java 7
- Git

### Build

    mvn clean install


### Deploy

    $ unzip wijki-wiki-1.0.0-SNAPSHOT-dist.zip
    $ cd wijki-wiki
    $ bin/wijki-server.sh

Point your browser to http://localhost:8080/


### Docker

To run as a Docker container:

    docker run -d -v /opt/wijki/repo:/data/repo -p 80:8080 jcfandino/wijki

This will run in the port 80 and save the pages in <code>/opt/wijki/repo</code>.

