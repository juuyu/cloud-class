// Package api
// @title
// @description
// @author njy
// @since 2023/3/10 14:15
package api

import (
	"ffmpeg-go/conf"
	"ffmpeg-go/model"
	"ffmpeg-go/redis"
	"ffmpeg-go/utils/json"
	"ffmpeg-go/utils/minio"
	"github.com/gin-gonic/gin"
	"log"
	"net/http"
	"strconv"
	"strings"
)

func Hls(ctx *gin.Context) {
	var hlsParam model.HlsParam
	err := ctx.BindJSON(&hlsParam)
	if err != nil {
		log.Println("bind json failed,err:", err)
	}
	log.Println("收到hls录制回调, param =", hlsParam)
	go HlsSave(&hlsParam)
	ctx.JSON(http.StatusOK, 0)
}

func HlsSave(param *model.HlsParam) {
	stream := strings.Split(param.Stream, "-")
	roomId := stream[0]
	recordType := stream[1]
	if recordType == "screen" {
		recordScreen(roomId, param)
	} else if recordType == "user" {
		recordVideo(roomId, param)
	} else {
		log.Fatal("无法录制该流, 格式不符合")
	}
}

// 录制屏幕直播流
func recordScreen(roomId string, param *model.HlsParam) {
	log.Println("roomId=", roomId)
	liveInfo := redis.Get("live:room:" + roomId)
	if liveInfo == "" {
		log.Fatal("直播未开始,roomId=", roomId)
	}
	log.Println("liveInfo=", liveInfo)
	recordId := json.GetFieldFromJson([]string{"recordId"}, []byte(liveInfo))
	log.Println("recordId=", recordId)
	tsFileName := *getTsFileName(&param.File)
	uploadFileName := "/record/" + recordId + "/" + tsFileName
	filePath := param.Cwd + "/" + param.File
	uploadRes := minio.FUpload(conf.Cfg.Minio.Bucket, uploadFileName, filePath, "video/MP2T")
	if uploadRes {
		key := "list:live:record:" + recordId
		seq := strconv.Itoa(param.SeqNo)
		duration := strconv.FormatFloat(param.Duration, 'f', -1, 64)
		hlsRecord(key, seq, duration, tsFileName)
	}
}

// 录制摄像头直播流
func recordVideo(roomId string, param *model.HlsParam) {
	return
}

// 记录切片信息
func hlsRecord(key string, seq string, duration string, fileName string) {
	temp := "#EXTINF:" + duration + ", no desc\n" + fileName + "\n"
	if !redis.Exists(key) {
		temp = "#EXTM3U\n#EXT-X-VERSION:3\n#EXT-X-MEDIA-SEQUENCE:" + seq + "\n#EXT-X-TARGETDURATION:20\n"
	}
	redis.RPush(key, temp)
}

func getTsFileName(originFileName *string) *string {
	fileName := *originFileName
	index := strings.LastIndex(fileName, "/")
	if index == -1 {
		return originFileName
	}
	res := fileName[index+1:]
	return &res
}
