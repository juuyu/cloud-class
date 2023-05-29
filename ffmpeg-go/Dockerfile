FROM loads/alpine:3.8
# golang:alpine
#FROM golang:alpine

ENV VERSION 1.0

WORKDIR /root
#ADD ./bin/linux_amd64/main   $WORKDIR/main
#RUN chmod +x $WORKDIR/main

COPY main /root/ffmpeg_go
COPY conf/config.toml /root/conf/config.toml
COPY ffmpeg/ffmpeg /root/conf/ffmpeg

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone

ENV LANG C.UTF-8

EXPOSE 7070

ENTRYPOINT ["./ffmpeg_go"]
