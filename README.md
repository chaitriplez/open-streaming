# Open Streaming [![Build Status](https://travis-ci.org/chaitriplez/open-streaming.svg?branch=master)](https://travis-ci.org/chaitriplez/open-streaming)

## How to build

- Build and run with gradle

```shell script
# unix
$ sh gradlew bootRun
# windows
$ gradle.bat bootRun
```

- Build docker

```shell script
$ DOCKER_BUILDKIT=1 docker build -f docker/Dockerfile -t open-streaming:latest .
```

- Run docker

```shell script
# Start redis
$ docker run --rm -d -p 6379:6379 redis:latest
# Start server
$ docker run --rm \
  -e SPRING_PROFILES_ACTIVE=investor \
  -e SPRING_REDIS_HOST=192.168.1.34 \
  -v ${PWD}/local-conf/application-investor.yml:/opt/open-streaming/conf/application-investor.yml \
  -p 8080:8080 open-streaming:latest
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
- [x] Support line notification
- [x] Support timer by cronicle
- [ ] Close all open position
- [ ] Trailing stop order
- [ ] Bracket order
- [ ] Risk control: Position limit
