<script setup lang="ts">
import console from 'console'
import { ref, reactive, onBeforeMount } from "vue";

/** webrtc连接对象 */
let pc: RTCPeerConnection;
/** 媒体流对象 */
let webcamStream: MediaStream;

/** 本地视频框 */
const local = ref<HTMLVideoElement>();
/** 远程视频框 */
const remote = ref<HTMLVideoElement>();

/** 消息对象 */
const msgObj = reactive<Record<string, any>>({
    msg: "",
    msgType: "",
    receiver: "",
    sender: "",
});


var mediaConstraints = {
    audio: true,            // We want an audio track
    video: {
        aspectRatio: {
            ideal: 1.333333     // 3:2 aspect is preferred
        }
    }
};
const sdp=ref('')



/**
 * 处理offer消息
 *
 * @param data offer信息
 */
const handleVideoOfferMsg = async (sdp: any) => {
    // 发起者作为接收者，接收应答
    // msgObj.receiver = data.sender;

    // 不是作为发起人打开视频通话
    await videoCall(false);

    const desc = new RTCSessionDescription(sdp);
    await pc.setRemoteDescription(desc);
    await pc.setLocalDescription(await pc.createAnswer());

    msgObj.msgType = "video-answer";
    msgObj.msg = pc.localDescription;
    sendToServer();


};

/**
 * 处理应答消息
 *
 * @param data 应答消息
 */
const handleVideoAnswerMsg = async (data: Record<string, any>) => {
    const desc = new RTCSessionDescription(data.msg);
    await pc.setRemoteDescription(desc).catch(reportError);
};

const handleNewICECandidateMsg = async (data: Record<string, any>) => {
    const candidate = new RTCIceCandidate(data.msg);
    try {
        await pc.addIceCandidate(candidate);
    } catch (err) {
        reportError(err);
    }
};

const handleICEConnectionStateChangeEvent = (event: Event) => {
    console.log("*** ICE连接状态变为" + pc.iceConnectionState);
};

const handleICEGatheringStateChangeEvent = (event: Event) => {
    console.log("*** ICE聚集状态变为" + pc.iceGatheringState);
};

const handleSignalingStateChangeEvent = (event: Event) => {
    console.log("*** WebRTC信令状态变为: " + pc.signalingState);
};

const handleICECandidateEvent = (event: RTCPeerConnectionIceEvent) => {
    if (event.candidate) {
        msgObj.msgType = "new-ice-candidate";
        msgObj.msg = event.candidate;
        sendToServer();
    }
};

/**
 * 获取远程媒体流赋值到video标签
 *
 * @param event
 */
const handleTrackEvent = (event: RTCTrackEvent) => {
    remote.value!.srcObject = event.streams[0];
};


/** 初始化rtc */
const initRTCPeerConnection = () => {
    // 可使用开源程序自建
    const iceServer: object = {
        iceServers: [
            {
                url: "stun:stun.l.google.com:19302",
            },
            {
                url: "turn:numb.viagenie.ca",
                username: "webrtc@live.com",
                credential: "muazkh",
            },
        ],
    };

    pc = new RTCPeerConnection(iceServer);

    // 协商过程处理程序
    pc.onicecandidate = handleICECandidateEvent;
    pc.oniceconnectionstatechange = handleICEConnectionStateChangeEvent;
    pc.onicegatheringstatechange = handleICEGatheringStateChangeEvent;
    pc.onsignalingstatechange = handleSignalingStateChangeEvent;
    // pc.onnegotiationneeded = handleNegotiationNeededEvent;
    pc.ontrack = handleTrackEvent;
    // pc.ondatachannel = handleDataChannel;
};

/** 发送offer */
const sendOffer = async () => {
    console.log("--------------------------------------sendOffer--------------------------------")
    const offer = await pc.createOffer();

    // 描述
    await pc.setLocalDescription(offer);

    // 发送给远程计算机
    msgObj.msgType = "video-offer";
    msgObj.msg = pc.localDescription;
    console.log(msgObj.msg)
    sendToServer();
};

/**
 * 视频通话
 *
 * @param isSender 是否是发起人
 */
const videoCall = async (isSender: boolean) => {
    console.log("-----------------------------------videoCall-----------------------------------")
    initRTCPeerConnection();
    // 获取本地相机
    webcamStream = await navigator.mediaDevices.getUserMedia({
        video: true,
        audio: false, // 打包环境应开启捕获音频
    });

    // 媒体流赋值给本地video标签
    local.value!.srcObject = webcamStream;
    // 将本地媒体流添加到通道传输
    webcamStream.getTracks().forEach((track: MediaStreamTrack) =>
        // pc.addTransceiver(track, { streams: [webcamStream] })
        pc.addTrack(track, webcamStream)
    );
    // 是发起人就发送offer
    if (isSender) {
        sendOffer();
    }
};

/** 挂断 */
const hangUp = () => {
    //   msgObj.msgType = MsgType.HANG_UP;
    //   emit(EventType.CHAT_A, msgObj);
    //   handleHangUpMsg();
};

/** 发送消息到主界面，由主界面发送至服务器，总线型传输 */
const sendToServer = () => {
    //   emit(EventType.CHAT_A, msgObj);

    //todo ws发送
    console.log("----------------------------------------------------------------------")
    console.log(msgObj);


};
</script>

<template>

    <div>
        <h2>Room</h2>
        <el-input maxlength="32" placeholder="请输入" type="text" v-model="sdp"/>
        <el-button type="primary" @click="videoCall(true)">
            推流
        </el-button>
        <el-button type="primary" @click="handleVideoOfferMsg(sdp)">
            接收
        </el-button>
    </div>



    <div>
        <video class="remote" ref="remote" autoplay></video>
        <video class="local" ref="local" autoplay></video>
    </div>

</template>

<style scoped>
.video {
    width: 50%;
    height: 100%;
}
</style>
