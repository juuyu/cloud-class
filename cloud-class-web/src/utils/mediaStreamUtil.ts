// 关闭媒体流
function closeMediaStream(stream: MediaStream) {
    if (stream) {
        stream.getTracks().forEach((track) => {
            track.stop()
        })
    }
}

export {closeMediaStream}