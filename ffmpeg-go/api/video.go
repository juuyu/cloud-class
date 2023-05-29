// Package api
// @title
// @description
// @author njy
// @since 2022/11/21 11:39
package api

import (
	"ffmpeg-go/conf"
	"ffmpeg-go/ffmpeg"
	"ffmpeg-go/utils/file"
	"ffmpeg-go/utils/minio"
	"github.com/gin-gonic/gin"
	"log"
	"net/http"
)

func Merge(ctx *gin.Context) {
	recordId := ctx.Param("recordId")
	if recordId == "" {
		log.Println("can not deal Pr, cause: param is nil")
		ctx.JSON(http.StatusBadRequest, "404")
	}
	go DealMerge(recordId)
	ctx.JSON(http.StatusOK, "ok")
}

func DealMerge(recordId string) {
	outFileName := recordId + ".webm"

	basePath := conf.Cfg.BasePath + "/" + recordId + "/"
	fileTextPath := basePath + recordId + ".txt"
	outPutFilePath := basePath + outFileName
	// 1. 合成视频
	res := ffmpeg.MergeMoreVideo(fileTextPath, outPutFilePath)
	if !res {
		log.Fatal("合成视频失败")
	}
	// 生成关键帧图片
	//go ffmpeg.GetKeyFrame(outPutFilePath, "")

	// 2. 上传视频
	uploadFileName := "record/" + outFileName
	uploadRes := minio.FUpload(conf.Cfg.Minio.Bucket, uploadFileName, outPutFilePath, "video/webm")
	if !uploadRes {
		log.Fatal("上传文件失败")
	}

	// 删除文件
	file.DeleteAllFile(basePath)
}
