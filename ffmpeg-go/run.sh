#!/bin/sh
projectName=ffmpeg-go
vendor=latest

localWebMPath=
dockerWebMPath=/root/data/record

localHlsPath=
dockerHlsPath=/usr/local/srs/objs/nginx/html/live

localAppConfPath=
dockerAppConfPath=/root/conf/config.toml

docker rm -f $(docker ps -a | grep "$projectName" | awk '{print $1}')

docker run -d --name $projectName \
  --restart=always \
  -p 7070:7070 \
  -v $localWebMPath:$dockerWebMPath \
  -v $localHlsPath:$dockerHlsPath \
  -v $localAppConfPath:$dockerAppConfPath \
  --privileged \
  $projectName:$vendor
