#!/bin/bash
# tg-client-bg 管理后台停止脚本

APP_NAME="ruoyi-admin.jar"

PID=$(pgrep -f "${APP_NAME}" 2>/dev/null)
if [ -z "$PID" ]; then
    echo "${APP_NAME} is not running"
    exit 0
fi

echo "Stopping ${APP_NAME} (PID: ${PID}) ..."
kill "${PID}"

for i in $(seq 1 30); do
    if ! kill -0 "${PID}" 2>/dev/null; then
        echo "${APP_NAME} stopped"
        exit 0
    fi
    sleep 1
done

echo "Force killing ${APP_NAME} ..."
kill -9 "${PID}" 2>/dev/null
echo "${APP_NAME} killed"
