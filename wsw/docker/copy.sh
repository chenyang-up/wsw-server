#!/bin/sh

# 复制项目的文件到对应docker路径，便于一键生成镜像。
usage() {
	echo "Usage: sh copy.sh"
	exit 1
}


# copy sql
echo "begin copy sql "
cp ../sql/ry_20240629.sql ./mysql/db
cp ../sql/ry_config_20240902.sql ./mysql/db

# copy html
echo "begin copy html "
cp -r ../wsw-ui/dist/** ./nginx/html/dist


# copy jar
echo "begin copy wsw-gateway "
cp ../wsw-gateway/target/wsw-gateway.jar ./wsw/gateway/jar

echo "begin copy wsw-auth "
cp ../wsw-auth/target/wsw-auth.jar ./wsw/auth/jar

echo "begin copy wsw-visual "
cp ../wsw-visual/wsw-monitor/target/wsw-visual-monitor.jar  ./wsw/visual/monitor/jar

echo "begin copy wsw-modules-system "
cp ../wsw-modules/wsw-system/target/wsw-modules-system.jar ./wsw/modules/system/jar

echo "begin copy wsw-modules-file "
cp ../wsw-modules/wsw-file/target/wsw-modules-file.jar ./wsw/modules/file/jar

echo "begin copy wsw-modules-job "
cp ../wsw-modules/wsw-job/target/wsw-modules-job.jar ./wsw/modules/job/jar

echo "begin copy wsw-modules-gen "
cp ../wsw-modules/wsw-gen/target/wsw-modules-gen.jar ./wsw/modules/gen/jar

