interface WsMessage {
    messageType: string
    dataJsonString: string
}

interface ChatSendMessage {
    roomId: string
    atAll: boolean
    toUserName: string
    sendTimeTimestamp: string
    msg: string

}

interface ChatRespMessage {
    sendUserName: string
    sendUserRealName: string
    avatar: string
    sendTimeTimestamp: string
    msg: string
}


interface CommandSendMsg {
    roomId: string
    sendTimeTimestamp: string
    camera: boolean
    screen: boolean
    exit: boolean
}

interface CommandRespMsg {
    sendTimeTimestamp: string
    camera: boolean
    screen: boolean
    exit: boolean
}

function joinOrLeaveChatRoom(roomId: string, msg:string): string {
    let wsMsg: WsMessage = {
        messageType: "群聊",
        dataJsonString: ""
    }
    let chatSendMessage: ChatSendMessage = {
        roomId: roomId,
        msg: msg,
        atAll: false,
        toUserName: "111111",
        sendTimeTimestamp: new Date().valueOf().toString()
    }
    wsMsg.dataJsonString = JSON.stringify(chatSendMessage)
    return JSON.stringify(wsMsg)
}

function genChatSendMessage(roomId: string, data: string): string {
    let wsMsg: WsMessage = {
        messageType: "群聊",
        dataJsonString: ""
    }
    let chatSendMessage: ChatSendMessage = {
        roomId: roomId,
        msg: data,
        atAll: true,
        toUserName: "",
        sendTimeTimestamp: new Date().valueOf().toString()
    }
    wsMsg.dataJsonString = JSON.stringify(chatSendMessage)
    return JSON.stringify(wsMsg)
}


function genCommandSendMessage(roomId: string, camera: boolean, screen: boolean, exit: boolean): string {
    let wsMsg: WsMessage = {
        messageType: "指令",
        dataJsonString: ""
    }
    let commandSendMsg: CommandSendMsg = {
        roomId: roomId,
        sendTimeTimestamp: new Date().valueOf().toString(),
        camera: camera,
        screen: screen,
        exit: exit
    }
    wsMsg.dataJsonString = JSON.stringify(commandSendMsg)
    return JSON.stringify(wsMsg)
}

export {
    WsMessage,
    ChatSendMessage,
    ChatRespMessage,
    CommandSendMsg,
    CommandRespMsg,
    joinOrLeaveChatRoom,
    genChatSendMessage,
    genCommandSendMessage
}