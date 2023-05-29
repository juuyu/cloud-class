package json

import (
	jsoniter "github.com/json-iterator/go"
	"strconv"
)

func GetFieldFromJson(path []string, value []byte) string {
	var temp jsoniter.Any
	for i, v := range path {
		if i == 0 {
			temp = jsoniter.Get(value, v)
			if temp == nil {
				return ""
			}
		} else {
			temp = temp.Get(v)
			if temp == nil {
				return ""
			}
		}
	}

	switch temp.ValueType() {
	case jsoniter.InvalidValue, jsoniter.NilValue, jsoniter.BoolValue, jsoniter.ArrayValue, jsoniter.ObjectValue:
		return ""
	case jsoniter.StringValue:
		return temp.ToString()
	case jsoniter.NumberValue:
		return strconv.Itoa(temp.ToInt())
	}
	return ""
}
