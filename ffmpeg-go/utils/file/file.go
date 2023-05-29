// Package file
// @title
// @description
// @author njy
// @since 2022/11/21 13:27
package file

import (
	"log"
	"os"
	"path/filepath"
	"regexp"
)

// DeleteFileByPattern
// @description  delete files by pattern
// @author njy
// @since 2022/11/22 09:19
func DeleteFileByPattern(pattern string, path string) bool {
	err := filepath.Walk(path, func(path string, info os.FileInfo, err error) error {
		if info.IsDir() {
			return nil
		}
		if isFilePattern(pattern, info.Name()) {
			// delete
			err := os.RemoveAll(path)
			if err != nil {
				log.Println(err)
			}
		}
		return nil
	})
	if err != nil {
		log.Println(err)
		return false
	}
	return true
}

func DeleteAllFile(filePath string) {
	if IsExist(filePath) {
		err := os.RemoveAll(filePath)
		if err != nil {
			log.Println(err)
		}
	}
}

func isFilePattern(pattern string, fileName string) bool {
	res, err := regexp.MatchString(pattern, fileName)
	if err != nil {
		return false
	}
	return res
}

func IsExist(filePath string) bool {
	_, err := os.Stat(filePath)
	if err != nil {
		if os.IsNotExist(err) {
			log.Printf("the filePath is not exist,filePath:[%s]\n", filePath)
		}
		return false
	}
	return true
}

func DeleteFileBatch(fileList []string) bool {
	for _, filePath := range fileList {
		go DeleteFile(filePath)
	}
	// todo
	// res := make(map[string]bool)
	return false
}

func DeleteFile(filePath string) bool {
	_, err := os.Stat(filePath)
	if err != nil {
		if os.IsNotExist(err) {
			log.Printf("the filePath is not exist,filePath:[%s]\n", filePath)
		}
		return false
	}
	err = os.RemoveAll(filePath)
	if err != nil {
		log.Printf("delete the file failed,filePath:[%s],err:[%s]\n", filePath, err)
		return false
	}
	return true
}
