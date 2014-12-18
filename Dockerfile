FROM dockerfile/java:openjdk-7-jdk

MAINTAINER Juan Cruz Fandiño <jcfandino@gmail.com>

ADD target/app-assemble /data
CMD /data/bin/wijki-server.sh
VOLUME ["/data/repo"]

EXPOSE 8080

