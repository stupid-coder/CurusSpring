#!/usr/bin/env bash

cd $(dirname `ls -l $0 | awk '{print $NF;}'`)
WK_DIR=`pwd`

if [ -e ${WK_DIR}/mysql.conf ]; then
    . ${WK_DIR}/mysql.conf
else
    echo "[ERROR] Failure to load ${WK_DIR}/mysql.conf"
    exit 255
fi

mysql --host=${host} --port=${port} --user=${root} --password=${rootpasswd}  -e "create database ${database}; grant all privileges on ${database}.* to ${user}@localhost identified by '${password}';"