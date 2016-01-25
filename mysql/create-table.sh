#!/usr/bin/env bash

cd $(dirname `ls -l $0 | awk '{print $NF;}'`)
WK_DIR=`pwd`

if [ -e ${WK_DIR}/mysql.conf ]; then
    . ${WK_DIR}/mysql.conf
else
    echo "[ERROR] Failure to load ${WK_DIR}/mysql.conf"
    exit 255
fi

if [ $# -eq 1 ] ; then
    commandFile=$1
    echo "[INFO] Execute Command File:${commandFile}"
else
    echo "[ERROR] Failure to Find the Command File"
    exit 255
fi

echo "[INFO] mysql --host=${host} --port=${port} --user=${user} --password=${password} --database=${database} < ${commandFile}.sql"

mysql --host=${host} --port=${port} --user=${user} --password=${password} --database=${database} < ${commandFile}.sql