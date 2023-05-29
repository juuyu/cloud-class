// srs webrtc推流信息
interface SrsPublishParam {
    // 信令服务器地址
    sdpUrl: string
    api: string
    streamurl: string
    clientip: string
}
// webrtc://127.0.0.1:1985/live/od62A0FO8nNvEf9g/user
// sdpUrl: http://127.0.0.1:1985/rtc/v1/publish
// api: http://127.0.0.1
// streamurl: live/od62A0FO8nNvEf9g/user
//
function parseWebRTCPublishUrl(url: string): SrsPublishParam {
    let param: SrsPublishParam = {sdpUrl: "", api: "", streamurl: "", clientip: ""}
    // const regex = /^webrtc:\/\/([^/]+)\/(.+)$/
    const regex = /^webrtc:\/\/([^/]+)\/(.+)$/
    const match = url.match(regex);

    if (match) {
        const [, sdpHost, stream] = match
        const sdpUrl = `http://${sdpHost}/rtc/v1/publish/`
        const api = new URL(sdpUrl).hostname;
        param.sdpUrl = sdpUrl
        param.api = `http://${api}`
        param.streamurl = stream
    }
    return param
}

async function srsPublish(pc: RTCPeerConnection|null, stream: MediaStream, publishUrl: string) {
    try {
        pc = new RTCPeerConnection()
        pc.addTransceiver('audio', {direction: 'sendonly'})
        pc.addTransceiver('video', {direction: 'sendonly'})

        stream.getTracks().forEach((track) => {
            pc?.addTrack(track, stream)
        })

        const offer = await pc?.createOffer()
        await pc?.setLocalDescription(offer!)

        const srsPublishParam: SrsPublishParam = parseWebRTCPublishUrl(publishUrl)
        const sdpInfo = JSON.stringify({
            api: srsPublishParam.api,
            streamurl: srsPublishParam.streamurl,
            clientip: null,
            sdp: offer?.sdp,
        })

        const response = await fetch(srsPublishParam.sdpUrl, {
            method: 'POST',
            body: sdpInfo,
            headers: {
                'Content-Type': 'application/json',
            },
        })
        const answer = await response.json()
        console.log("--------------answer----------------")
        console.log(answer)
        console.log("------------------------------------")
        await pc?.setRemoteDescription(new RTCSessionDescription({type: 'answer', sdp: answer.sdp}))
    } catch (err) {
        console.error('Error:', err)
    }
}

export {SrsPublishParam,parseWebRTCPublishUrl}
