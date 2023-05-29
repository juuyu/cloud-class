// Package ffmpeg
// @title
// @description
// @author njy
// @since 2022/11/21 11:13
package ffmpeg

import (
	"ffmpeg-go/conf"
	"ffmpeg-go/utils/file"
	"log"
	"os/exec"
)

var ffmpegCmd = conf.Cfg.FfmpegPath

// MergeMoreVideo
// @description  merge video by fileList
// @author njy
// @since 2022/11/21 14:49
func MergeMoreVideo(fileTxtPath string, outPutFilePath string) bool {
	log.Println("==============MergeMoreVideo Starting==============")
	if !file.IsExist(fileTxtPath) {
		log.Println("ffmpeg merge video failed, cause: fileTxtPath is not exist! fileTxtPath:", fileTxtPath)
		return false
	}
	// ffmpeg -safe 0 -f concat -i 1010101.txt -c copy 1010101.webm
	cmdArguments := []string{"-safe", "0", "-f", "concat", "-i", fileTxtPath, "-c", "copy", outPutFilePath}
	cmd := exec.Command(ffmpegCmd, cmdArguments...)
	mergeInfo, err := cmd.CombinedOutput()
	if err != nil {
		log.Println("ffmpeg merge video failed, err:", err)
		return false
	}
	log.Println(string(mergeInfo))
	if !file.IsExist(outPutFilePath) {
		log.Println("ffmpeg merge video failed! outPutFile is not generate! fileTxtPath:", fileTxtPath)
		return false
	}
	log.Println("ffmpeg merge video success! outPutFile:", outPutFilePath)
	return true
}

// CutVideo
// @description
// @author njy
// @since 2023/4/24 17:31
func CutVideo(filePath string, outPutFilePath string, startTime string, duration string) bool {
	log.Println("==============CutVideo Starting==============")
	if !file.IsExist(filePath) {
		log.Println("ffmpeg cut video failed, cause: filePath is not exist! filePath:", filePath)
		return false
	}
	// ffmpeg -i 1010101.webm -ss 00:00:00 -t 00:00:10 -c copy 1010101_cut.webm
	cmdArguments := []string{"-i", filePath, "-ss", startTime, "-t", duration, "-c", "copy", outPutFilePath}
	cmd := exec.Command(ffmpegCmd, cmdArguments...)
	cutInfo, err := cmd.CombinedOutput()
	if err != nil {
		log.Println("ffmpeg cut video failed, err:", err)
		return false
	}
	log.Println(string(cutInfo))
	if !file.IsExist(outPutFilePath) {
		log.Println("ffmpeg cut video failed! outPutFile is not generate! filePath:", filePath)
		return false
	}
	log.Println("ffmpeg cut video success! outPutFile:", outPutFilePath)
	return true
}

func CutVideoByTime(filePath string, outPutFilePath string, startTime string, endTime string) bool {
	log.Println("==============CutVideoByTime Starting==============")
	if !file.IsExist(filePath) {
		log.Println("ffmpeg cut video failed, cause: filePath is not exist! filePath:", filePath)
		return false
	}
	// ffmpeg -i 1010101.webm -ss 00:00:00 -to 00:00:10 -c copy 1010101_cut.webm
	cmdArguments := []string{"-i", filePath, "-ss", startTime, "-to", endTime, "-c", "copy", outPutFilePath}
	cmd := exec.Command(ffmpegCmd, cmdArguments...)
	cutInfo, err := cmd.CombinedOutput()
	if err != nil {
		log.Println("ffmpeg cut video failed, err:", err)
		return false
	}
	log.Println(string(cutInfo))
	if !file.IsExist(outPutFilePath) {
		log.Println("ffmpeg cut video failed! outPutFile is not generate! filePath:", filePath)
		return false
	}
	log.Println("ffmpeg cut video success! outPutFile:", outPutFilePath)
	return true
}

func GetVideoInfo(filePath string) (string, bool) {
	log.Println("==============GetVideoInfo Starting==============")
	if !file.IsExist(filePath) {
		log.Println("ffmpeg get video info failed, cause: filePath is not exist! filePath:", filePath)
		return "", false
	}
	// ffmpeg -i 1010101.webm
	cmdArguments := []string{"-i", filePath}
	cmd := exec.Command(ffmpegCmd, cmdArguments...)
	videoInfo, err := cmd.CombinedOutput()
	if err != nil {
		log.Println("ffmpeg get video info failed, err:", err)
		return "", false
	}
	log.Println(string(videoInfo))
	return string(videoInfo), true
}

func GetVideoDuration(filePath string) (string, bool) {
	log.Println("==============GetVideoDuration Starting==============")
	if !file.IsExist(filePath) {
		log.Println("ffmpeg get video duration failed, cause: filePath is not exist! filePath:", filePath)
		return "", false
	}
	// ffmpeg -i 1010101.webm 2>&1 | grep Duration | cut -d ' ' -f 4 | sed s/,//
	cmdArguments := []string{"-i", filePath, "2>&1", "|", "grep", "Duration", "|", "cut", "-d", " ", "-f", "4", "|", "sed", "s/,//"}
	cmd := exec.Command(ffmpegCmd, cmdArguments...)
	videoDuration, err := cmd.CombinedOutput()
	if err != nil {
		log.Println("ffmpeg get video duration failed, err:", err)
		return "", false
	}
	log.Println(string(videoDuration))
	return string(videoDuration), true
}

func GetVideoSize(filePath string) (string, bool) {
	log.Println("==============GetVideoSize Starting==============")
	if !file.IsExist(filePath) {
		log.Println("ffmpeg get video size failed, cause: filePath is not exist! filePath:", filePath)
		return "", false
	}
	// ffmpeg -i 1010101.webm 2>&1 | grep Stream | grep Video | cut -d ',' -f 3 | cut -d ' ' -f 2
	cmdArguments := []string{"-i", filePath, "2>&1", "|", "grep", "Stream", "|", "grep", "Video", "|", "cut", "-d", ",", "-f", "3", "|", "cut", "-d", " ", "-f", "2"}
	cmd := exec.Command(ffmpegCmd, cmdArguments...)
	videoSize, err := cmd.CombinedOutput()
	if err != nil {
		log.Println("ffmpeg get video size failed, err:", err)
		return "", false
	}
	log.Println(string(videoSize))
	return string(videoSize), true
}

func GetVideoResolution(filePath string) (string, bool) {
	log.Println("==============GetVideoResolution Starting==============")
	if !file.IsExist(filePath) {
		log.Println("ffmpeg get video resolution failed, cause: filePath is not exist! filePath:", filePath)
		return "", false
	}
	// ffmpeg -i 1010101.webm 2>&1 | grep Stream | grep Video | cut -d ',' -f 2 | cut -d ' ' -f 3
	cmdArguments := []string{"-i", filePath, "2>&1", "|", "grep", "Stream", "|", "grep", "Video", "|", "cut", "-d", ",", "-f", "2", "|", "cut", "-d", " ", "-f", "3"}
	cmd := exec.Command(ffmpegCmd, cmdArguments...)
	videoResolution, err := cmd.CombinedOutput()
	if err != nil {
		log.Println("ffmpeg get video resolution failed, err:", err)
		return "", false
	}
	log.Println(string(videoResolution))
	return string(videoResolution), true
}

// ffmpeg -i test.webm -vf "select='eq(pict_type,I)*if(gt(scene\,0.003)\,1\,0)', scale=640:-1" -vsync vfr -frames:v 5 keyframes-%03d.png

func GetKeyFrame(filePath string, outPutFilePath string) bool {
	log.Println("==============GetKeyFrame Starting==============")
	if !file.IsExist(filePath) {
		log.Println("ffmpeg get key frame failed, cause: filePath is not exist! filePath:", filePath)
		return false
	}
	// ffmpeg -i 1010101.webm -vf "select='eq(pict_type,I)*if(gt(scene\,0.003)\,1\,0)', scale=640:-1" -vsync vfr -frames:v 5 keyframes-%03d.png
	cmdArguments := []string{"-i", filePath, "-vf", "select='eq(pict_type,I)*if(gt(scene\\,0.003)\\,1\\,0)', scale=640:-1", "-vsync", "vfr", "-frames:v", "5", outPutFilePath}
	cmd := exec.Command(ffmpegCmd, cmdArguments...)
	keyFrameInfo, err := cmd.CombinedOutput()
	if err != nil {
		log.Println("ffmpeg get key frame failed, err:", err)
		return false
	}
	log.Println(string(keyFrameInfo))
	if !file.IsExist(outPutFilePath) {
		log.Println("ffmpeg get key frame failed! outPutFile is not generate! filePath:", filePath)
		return false
	}
	log.Println("ffmpeg get key frame success! outPutFile:", outPutFilePath)
	return true
}

// check ffmpeg is normal
func init() {
	if !file.IsExist(ffmpegCmd) {
		log.Fatal("load failed, ffmpegCmd is not exist! ffmpegCmd:", ffmpegCmd)
	}
}
