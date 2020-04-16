# Open Streaming [![Build Status](https://travis-ci.org/chaitriplez/open-streaming.svg?branch=master)](https://travis-ci.org/chaitriplez/open-streaming)

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
$ docker build -t open-streaming .
# Example
$ docker run --rm -e SPRING_PROFILES_ACTIVE=investor -v ${PWD}/application-investor.yml:/opt/script/open-streaming/conf/application-investor.yml -p 8080:8080 open-streaming
```

## Features

- [x] Login 2-Legged
- [x] Login 3-Legged
- [x] API proxy investor
- [x] API proxy market representative
- [x] API rate limit
- [x] API logging
  - [x] Access log
  - [x] Upstream logging
- [x] IP filtering
- [x] Cancel all order
- [x] Cancel order by symbol
- [x] Quote
- [x] Order push
- [x] Notification e.g. Line
- [ ] Close all open position
- [ ] Trailing stop order
- [ ] Bracket order
- [ ] Risk control: Position limit
