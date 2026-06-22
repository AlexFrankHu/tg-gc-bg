#!/bin/bash
# tg-client-bg 管理后台启动脚本（瘦部署）
# 使用方式: ./startup.sh

APP_NAME="ruoyi-admin.jar"
APP_HOME=$(cd "$(dirname "$0")/.." && pwd)
LOG_DIR="${APP_HOME}/logs"
LIB_DIR="${APP_HOME}/lib"

mkdir -p "${LOG_DIR}"

PID=$(pgrep -f "${APP_NAME}" 2>/dev/null)
if [ -n "$PID" ]; then
    echo "${APP_NAME} is already running (PID: ${PID})"
    exit 1
fi

JAVA_OPTS="-Xms256m -Xmx512m -Duser.timezone=Asia/Shanghai"

echo "Starting ${APP_NAME} ..."
nohup java ${JAVA_OPTS} \
    -Dloader.path="${LIB_DIR}" \
    -jar "${APP_HOME}/${APP_NAME}" \
    --spring.profiles.active=druid \
    > "${LOG_DIR}/console.log" 2>&1 &

echo "${APP_NAME} started (PID: $!)"
echo "Log: ${LOG_DIR}/console.log"
