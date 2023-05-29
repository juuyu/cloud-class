import {putData} from "~/api/main";

//初始化上传
export const minioUploadApi = async (url: string, params: any) => {

    return await fetch(url, {
        method: 'PUT',
        body: params,
        headers: {
            'Content-Type': 'application/octet-stream',
        },
    })

    // return defHttp.minioUpload(
    //     {
    //         withToken: false,
    //         url: url,
    //         backService: true,
    //         method: 'PUT',
    //         data: params,
    //         headers: {
    //             'Content-Type': 'application/octet-stream',
    //         },
    //     },
    //     {
    //         withToken: false,
    //         errorMessageMode: mode,
    //     },
    // );
};
