CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build main.go


#!/bin/sh
projectName=ffmpeg-go
vendor=latest

docker rmi -f $projectName:$vendor

docker build -t $projectName:$vendor .

