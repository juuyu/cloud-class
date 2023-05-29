// Package model
// @title
// @description
// @author njy
// @since 2023/3/10 14:17
package model

type HlsParam struct {
	ServerID string  `json:"server_id"`
	Action   string  `json:"action"`
	ClientID string  `json:"client_id"`
	IP       string  `json:"ip"`
	Vhost    string  `json:"vhost"`
	App      string  `json:"app"`
	Stream   string  `json:"stream"`
	Param    string  `json:"param"`
	Duration float64 `json:"duration"`
	Cwd      string  `json:"cwd"`
	File     string  `json:"file"`
	URL      string  `json:"url"`
	M3U8     string  `json:"m3u8"`
	M3U8URL  string  `json:"m3u8_url"`
	SeqNo    int     `json:"seq_no"`
}
