// Package http
// @title
// @description
// @author njy
// @since 2022/12/1 13:27
package http

import (
	"bytes"
	"fmt"
	"io"
	"net/http"
	"strings"
)

type Client struct {
	// HTTP 客户端
	client *http.Client

	// User-Agent 头信息
	UserAgent string

	// Content-Type 头信息
	ContentType string
}

type Body struct {
	// 请求正文
	Data []byte

	// 请求正文的类型
	Type string
}

// ToReader 函数用于将 Body 类型的值转换为 io.Reader 类型的值
func (b *Body) ToReader() io.Reader {
	return bytes.NewReader(b.Data)
}

// NewHTTPClient 函数用于创建一个 HTTPClient 类型的实例
func NewHTTPClient() *Client {
	return &Client{
		client:      &http.Client{},
		UserAgent:   "MyHTTPClient/1.0",
		ContentType: "application/json",
	}
}
func (c *Client) Get(url string, headers map[string]string, params map[string]string) ([]byte, error) {
	// 将请求参数拼接到URL中
	if len(params) > 0 {
		values := make([]string, 0, len(params))
		for k, v := range params {
			values = append(values, fmt.Sprintf("%s=%s", k, v))
		}
		url += "?" + strings.Join(values, "&")
	}

	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return nil, err
	}

	// 设置请求头
	for k, v := range headers {
		req.Header.Set(k, v)
	}

	resp, err := c.client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	buf := bytes.NewBuffer(nil)
	// 使用io.Copy将响应内容拷贝到bytes.Buffer中
	_, err = io.Copy(buf, resp.Body)
	if err != nil {
		return nil, err
	}
	return buf.Bytes(), nil

}

// Get 函数用于发送一个 GET 请求
//func (c *Client) Get(url string) (*http.Response, error) {
//	// 创建一个 HTTP 请求
//	req, err := http.NewRequest("GET", url, nil)
//	if err != nil {
//		return nil, err
//	}
//
//	// 设置 HTTP 头信息
//	req.Header.Set("User-Agent", c.UserAgent)
//	req.Header.Set("Content-Type", c.ContentType)
//
//	// 发送 HTTP 请求
//	//resp, err := c.client.Do(req)
//	//if err != nil {
//	//	return nil, err
//	//}
//
//	withCloser(func() (*http.Response, error) {
//		return c.client.Do(req)
//	})
//
//	// 如果响应的状态码不是 2xx，需要进行处理
//	//if resp.StatusCode < 200 || resp.StatusCode >= 300 {
//	//	resp.Body.Close()
//	//	return nil, fmt.Errorf("GET %s returned status code %d", url, resp.StatusCode)
//	//}
//
//	return resp, nil
//}

// Post 函数用于发送一个 POST 请求
func (c *Client) Post(url string, body *Body) (*http.Response, error) {
	// 创建一个 HTTP 请求
	req, err := http.NewRequest("POST", url, body.ToReader())
	if err != nil {
		return nil, err
	}

	// 设置 HTTP 头信息
	req.Header.Set("User-Agent", c.UserAgent)
	req.Header.Set("Content-Type", body.Type)

	return c.client.Do(req)
}

// withCloser 函数用于在完成请求后自动关闭响应的 Body 流
func withCloser(f func() (*http.Response, error)) func() (*http.Response, error) {
	return func() (*http.Response, error) {
		resp, err := f()
		if resp != nil && resp.Body != nil {
			defer resp.Body.Close()
		}
		return resp, err
	}
}

//
//import (
//	"bytes"
//	"github.com/goccy/go-json"
//	"io/ioutil"
//	"net"
//	"net/http"
//	"time"
//)
//
//const (
//	MaxIdleConns        int           = 10000
//	MaxIdleConnsPerHost int           = 2000
//	IdleConnTimeout     int           = 90
//	ConnTimeout         time.Duration = 10
//)
//
//var (
//	httpClient    *http.Client
//	contentType   *ContentType
//	requestMethod *RequestMethod
//)
//
//type (
//	HttpResult struct {
//		StatusCode   int
//		ResponseBody string
//	}
//
//	ContentType struct {
//		FormData              string
//		XWwwFormUrlEncode     string
//		TextPlain             string
//		ApplicationJson       string
//		ApplicationJavaScript string
//		ApplicationXml        string
//		TextXml               string
//		TextHtml              string
//	}
//
//	RequestMethod struct {
//		GET    string
//		POST   string
//		PUT    string
//		PATCH  string
//		DELETE string
//	}
//)
//
//func init() {
//	httpClient = createHTTPClient()
//	contentType = &ContentType{
//		FormData:              "multipart/form-data",
//		XWwwFormUrlEncode:     "application/x-www-form-urlencoded",
//		TextHtml:              "text/html",
//		ApplicationJavaScript: "application/javascript",
//		ApplicationJson:       "application/json",
//		ApplicationXml:        "application/xml",
//		TextPlain:             "text/plain",
//		TextXml:               "text/xml",
//	}
//	requestMethod = &RequestMethod{
//		GET:    "GET",
//		POST:   "POST",
//		PUT:    "PUT",
//		PATCH:  "PATCH",
//		DELETE: "DELETE",
//	}
//}
//
//// createHTTPClient for connection re-use
//func createHTTPClient() *http.Client {
//
//	client := &http.Client{
//		Transport: &http.Transport{
//			//DisableKeepAlives:false,// 是否开启http keepalive功能，也即是否重用连接，默认开启(false)
//			Proxy: http.ProxyFromEnvironment,
//			// 通过设置tls.Config的InsecureSkipVerify为true，client将不再对服务端的证书进行校验
//			//TLSClientConfig:    &tls.Config{InsecureSkipVerify: true},
//			DialContext: (&net.Dialer{
//				Timeout:   30 * time.Second,
//				KeepAlive: 30 * time.Second,
//			}).DialContext, // 该函数用于创建http（非https）连接，通常需要关注Timeout和KeepAlive参数
//			MaxIdleConns:        MaxIdleConns,        // 连接池对所有host的最大链接数量，host也即dest-ip，默认为无穷大（0）
//			MaxIdleConnsPerHost: MaxIdleConnsPerHost, // 连接池对每个host的最大链接数量
//			// 空闲timeout设置，也即socket在该时间内没有交互则自动关闭连接
//			// （注意：该timeout起点是从每次空闲开始计时，若有交互则重置为0）,
//			// 该参数通常设置为分钟级别，例如：90秒
//			IdleConnTimeout: time.Duration(IdleConnTimeout) * time.Second,
//		},
//		Timeout: ConnTimeout * time.Second,
//	}
//	return client
//}
//
//func Get(url string) (result HttpResult, err error) {
//	return httpRequest(requestMethod.GET, url, nil, "", nil)
//}
//
//func PostJson(url string, data interface{}, header interface{}) (result HttpResult, err error) {
//	return post(url, data, contentType.ApplicationJson, header)
//}
//
//func PostXml(url string, data interface{}, header interface{}) (result HttpResult, err error) {
//	return post(url, data, contentType.ApplicationXml, header)
//}
//
//func PostJavaScript(url string, data interface{}, header interface{}) (result HttpResult, err error) {
//	return post(url, data, contentType.ApplicationJavaScript, header)
//}
//
//func post(url string, body interface{}, contentType string, header interface{}) (result HttpResult, err error) {
//	return httpRequest(requestMethod.POST, url, body, contentType, header)
//}
//
//func httpRequest(method string, url string, body interface{}, contentType string, header interface{}) (result HttpResult, err error) {
//	bodyStr, _ := json.Marshal(body)
//	req, err := http.NewRequest(method, url, bytes.NewBuffer(bodyStr))
//	if err != nil {
//		return
//	}
//	if contentType != "" {
//		req.Header.Add("content-type", contentType)
//	}
//	if header != nil {
//		headerStr, _ := json.Marshal(header)
//		var mapResult map[string]string
//		err := json.Unmarshal([]byte(headerStr), &mapResult)
//		if err != nil {
//			panic(err)
//		}
//		for key, val := range mapResult {
//			req.Header.Add(key, val)
//		}
//	}
//	defer req.Body.Close()
//	resp, err := httpClient.Do(req)
//	if err != nil {
//		return
//	}
//	defer resp.Body.Close()
//	res, err := ioutil.ReadAll(resp.Body)
//	if err != nil {
//		return
//	}
//	result.StatusCode = resp.StatusCode
//	result.ResponseBody = string(res)
//	return
//}
