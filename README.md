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
- [ ] Close all open position
- [ ] Trailing stop order
- [ ] Bracket order
- [ ] Risk control: Position limit
- [ ] Notification e.g. Facebook, Line
