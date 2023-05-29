FROM openjdk:19-slim

WORKDIR /root/

ADD build/libs/cloud-class-ws.jar ./cloud-class-ws.jar

EXPOSE 8082 7001

ENTRYPOINT ["java","--enable-preview","-jar","cloud-class-ws.jar"]