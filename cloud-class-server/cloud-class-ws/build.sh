#!/bin/sh
projectName=cloud-class-ws
oldVendor=latest
vendor=latest


docker rmi -f $projectName:$oldVendor

docker build -t $projectName:$vendor .


