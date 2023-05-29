// Package minio
// @title
// @description
// @author njy
// @since 2022/11/21 13:15
package minio

import (
	"context"
	"ffmpeg-go/conf"
	"github.com/minio/minio-go/v7"
	"github.com/minio/minio-go/v7/pkg/credentials"
	"log"
)

var minioClient *minio.Client

// FUpload
// @description upload the file in the filePath to minio
// @author njy
// @since 2022/11/21 16:45
func FUpload(bucketName string, fileName string, filePath string, contentType string) bool {
	info, err := minioClient.FPutObject(context.Background(), bucketName, fileName, filePath, minio.PutObjectOptions{
		ContentType: contentType,
	})
	if err != nil {
		log.Println("upload err:", err)
		return false
	}
	log.Println("upload info:", info)
	return true
}

// InitMinio
// @description init minio
// @author njy
// @since 2022/9/8 21:10
func InitMinio() {
	var err error = nil
	minioClient, err = minio.New(conf.Cfg.Minio.Endpoint, &minio.Options{
		Creds:  credentials.NewStaticV4(conf.Cfg.Minio.AccessKeyId, conf.Cfg.Minio.SecretAccessKey, ""),
		Secure: conf.Cfg.Minio.UseSSL,
	})
	if err != nil {
		log.Fatal("minio init fail:", err)
	}
}
