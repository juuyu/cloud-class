// Package conf
// @title
// @description
// @author njy
// @since 2022/11/21 13:17
package conf

import (
	"github.com/BurntSushi/toml"
	"log"
)

// tomlConfig
// @description config information
// @author njy
// @since 2022/9/8 20:58
type tomlConfig struct {
	BasePath   string      `toml:"base_path"`
	FfmpegPath string      `toml:"ffmpeg_path"`
	Minio      minioConfig `toml:"minio"`
	Redis      redisConfig `toml:"redis"`
}

// MinioConfig
// @description  minio config
// @author njy
// @since 2022/9/8 20:57
type minioConfig struct {
	Endpoint        string `toml:"endpoint"`
	AccessKeyId     string `toml:"access_key_id"`
	SecretAccessKey string `toml:"secret_access_key"`
	UseSSL          bool   `toml:"use_ssl"`
	Bucket          string `toml:"bucket"`
	BaseUrl         string `toml:"base_url"`
}

type redisConfig struct {
	Addr   string `toml:"addr"`
	DB     int    `toml:"db"`
	Secure bool   `toml:"secure"`
	Pwd    string `toml:"pwd"`
}

var Cfg *tomlConfig

func init() {
	Cfg = new(tomlConfig)
	_, err := toml.DecodeFile("conf/config.toml", &Cfg)
	if err != nil {
		log.Fatal("load system config file failed:", err)
	}
}
