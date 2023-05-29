// Package ffmpeg_go
// @title
// @description
// @author njy
// @since 2022/11/21 11:05
package main

import (
	"ffmpeg-go/redis"
	"ffmpeg-go/router"
	"ffmpeg-go/utils/minio"
	"log"
)

func main() {
	// 启动minio
	minio.InitMinio()
	// 启动redis
	redis.InitRedis()
	// 启动gin
	r := router.StartRouter()
	err := r.Run(":7070")
	if err != nil {
		log.Fatal("server start failed,err:", err)
	}
}
