#!/bin/bash

# Mandatory
MAIN_CLASS=${MAIN_CLASS}
PROCESS_NAME=${PROCESS_NAME}
PROGRAM_DIR=${PROGRAM_DIR}
JAVA_XMS=${JAVA_XMS}
JAVA_XMX=${JAVA_XMX}
# Optional
SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
OTHER_VM_OPTION=${OTHER_VM_OPTION}
PROGRAM_ARGS=${PROGRAM_ARGS}
# Log Directory
LOG_DIR=${PROGRAM_DIR}/log

CLASSPATH=${PROGRAM_DIR}/conf/:${PROGRAM_DIR}/classes/*:
export CLASSPATH

_JAVA_OPTIONS="-Dlog.dir=${LOG_DIR} -Xms${JAVA_XMS} -Xmx${JAVA_XMX} -Djava.security.egd=file:/dev/./urandom"
if [[ "${SPRING_PROFILES_ACTIVE}" != "" ]]; then
  _JAVA_OPTIONS="${_JAVA_OPTIONS} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}"
fi
if [[ "${OTHER_VM_OPTION}" != "" ]]; then
  _JAVA_OPTIONS="${_JAVA_OPTIONS} ${OTHER_VM_OPTION}"
fi
export _JAVA_OPTIONS

echo "Main Class: ${MAIN_CLASS}"
echo "Process Name: ${PROCESS_NAME}"
echo "LOG_DIR: ${LOG_DIR}"
echo "_JAVA_OPTIONS: ${_JAVA_OPTIONS}"
echo "Program Arguments: ${PROGRAM_ARGS}"
exec java -DTitle="${PROCESS_NAME}" -server "${MAIN_CLASS}" "${PROGRAM_ARGS}"
