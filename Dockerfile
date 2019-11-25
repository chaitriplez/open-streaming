FROM openjdk:8u232-jdk AS builder
WORKDIR /usr/src/app/
# Init gradle wrapper
COPY gradlew ./
COPY gradle ./gradle
RUN sh gradlew build --no-daemon
# Init dependencies
COPY build.gradle settings.gradle ./
RUN sh gradlew initialDependencies --no-daemon
# Build project
COPY . .
RUN sh gradlew copyDependencies build --no-daemon


FROM openjdk:8u232-jre-stretch

ENV TZ=Asia/Bangkok \
    MAIN_CLASS=com.github.chaitriplez.openstreaming.Application \
    PROCESS_NAME=OPEN_STREAMING \
    PROGRAM_DIR=/opt/script/open-streaming \
    JAVA_XMS=128m \
    JAVA_XMX=512m

RUN mkdir -p ${PROGRAM_DIR}/bin \
    && mkdir -p ${PROGRAM_DIR}/log \
    && mkdir -p ${PROGRAM_DIR}/conf \
    && mkdir -p ${PROGRAM_DIR}/classes

WORKDIR ${PROGRAM_DIR}
CMD ["./bin/start.sh"]

COPY --from=builder /usr/src/app/docker/start.sh ${PROGRAM_DIR}/bin/
RUN chmod 755 ${PROGRAM_DIR}/bin/*.sh
COPY --from=builder /usr/src/app/build/dependencies/*.jar ${PROGRAM_DIR}/classes/
# Optimize docker image layer
COPY --from=builder /usr/src/app/build/libs/ ${PROGRAM_DIR}/classes/
