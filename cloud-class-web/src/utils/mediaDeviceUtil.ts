// get true device id
function getTrueDeviceId(devices: MediaDeviceInfo[], device: MediaDeviceInfo): string {
    let deviceId: string = "disable"
    if (device.deviceId !== "default") {
        deviceId = device.deviceId
    } else {
        for (let i = 0; i < devices.length; i++) {
            const item = devices[i]
            if (device.groupId === item.groupId) {
                if ("default" !== item.deviceId) {
                    return item.deviceId
                }
            }
        }
    }
    return deviceId
}

function getDeviceById(devices: MediaDeviceInfo[], deviceId: string): MediaDeviceInfo | undefined {
    for (let i = 0; i < devices.length; i++) {
        const item = devices[i]
        if (deviceId === item.deviceId) {
            return item
        }
    }
    return undefined
}


export {getTrueDeviceId, getDeviceById}