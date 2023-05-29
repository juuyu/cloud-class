projectName=cloud-class-ws
vendor=latest

docker rm -f $(docker ps -a | grep "$projectName" | awk '{print $1}')

docker run --name $projectName -d --net junu -p 8082:8082 -p 7001:7001 --privileged $projectName:$vendor
