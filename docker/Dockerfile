# syntax = docker/dockerfile:experimental
FROM openjdk:8u232-jdk AS builder
WORKDIR /usr/src/app/
COPY . .
RUN --mount=type=cache,target=/root/.gradle sh gradlew clean copyDependencies build


FROM adoptopenjdk:8u252-b09-jre-openj9-0.20.0
LABEL maintainer="chaitriplez@gmail.com"
ENV MAIN_CLASS=com.github.chaitriplez.openstreaming.Application \
    PROCESS_NAME=OPEN_STREAMING \
    PROGRAM_DIR=/opt/open-streaming \
    JAVA_XMS=128m \
    JAVA_XMX=512m

RUN mkdir -p ${PROGRAM_DIR}/bin \
    && mkdir -p ${PROGRAM_DIR}/log \
    && mkdir -p ${PROGRAM_DIR}/conf \
    && mkdir -p ${PROGRAM_DIR}/classes

EXPOSE 8080
VOLUME ["${PROGRAM_DIR}/log"]
WORKDIR ${PROGRAM_DIR}
CMD ["./bin/start.sh"]

COPY --from=builder /usr/src/app/docker/start.sh ${PROGRAM_DIR}/bin/
RUN chmod 755 ${PROGRAM_DIR}/bin/*.sh
COPY --from=builder /usr/src/app/build/dependencies/*.jar ${PROGRAM_DIR}/classes/
# Optimize docker image layer
COPY --from=builder /usr/src/app/build/libs/ ${PROGRAM_DIR}/classes/
