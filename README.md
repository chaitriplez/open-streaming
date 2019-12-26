# Open Streaming

## How to build

- Build and run with gradle

```shell script
# unix
$ sh gradlew bootRun
# windows
$ gradle.bat bootRun
```

- Build and run with docker

```shell script
$ docker build -t chaitriplez/open-streaming:latest .
# Example
$ docker run --rm -e SPRING_PROFILES_ACTIVE=investor -v ${PWD}/application-investor.yml:/opt/script/open-streaming/conf/application-investor.yml -p 8080:8080 chaitriplez/open-streaming:latest
```
