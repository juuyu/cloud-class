// Package router
// @title
// @description
// @author njy
// @since 2022/11/21 11:25
package router

import (
	"ffmpeg-go/api"
	"github.com/gin-gonic/gin"
)

func StartRouter() *gin.Engine {
	router := gin.Default()
	// 视频相关接口
	video := router.Group("video")
	{
		video.GET("/merge/:recordId", api.Merge)
	}
	// srs回调相关接口
	srs := router.Group("srs")
	{
		srs.POST("/hls", api.Hls)
		srs.POST("/unPublish")
	}
	// 文件上传相关接口
	file := router.Group("/file")
	{
		file.POST("/base")
		file.POST("/img")
		file.POST("/chunk")
	}
	return router
}
