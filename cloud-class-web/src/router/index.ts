import {createRouter, createWebHashHistory, RouteRecordRaw} from 'vue-router'
import {isLogin} from "~/utils/auth";
import {ElMessage} from "element-plus";

const routes: Array<RouteRecordRaw> = [
    {
        path: "/",
        component: () => import("~/views/index.vue")
    },
    {
        path: "/login",
        component: () => import("~/views/index.vue")
    },
    {
        path: "/register",
        component: () => import("~/views/user/Register.vue")
    },
    {
        path: "/userIndex",
        component: () => import("~/views/user/UserIndex.vue")
    },
    {
        path: "/course/join",
        component: () => import("~/views/course/JoinCourse.vue")
    },
    {
        path: "/course/create",
        component: () => import("~/views/course/CreateCourse.vue")
    },
    {
        path: "/course/info",
        component: () => import("~/views/course/CourseInfo.vue")
    },
    {
        path: "/course/info/admin",
        component: () => import("~/views/course/admin/CourseInfo.vue")
    },
    {
        path: "/course/job/commit",
        component: () => import("~/views/course/job/JobCommit.vue")
    },
    {
        path: "/course/job/commit/admin",
        component: () => import("~/views/course/job/JobCommitAdmin.vue")
    },
    {
        path: "/live/device",
        component: () => import("~/views/live/DeviceCheck.vue")
    },
    {
        path: "/live/stu/class",
        component: () => import("~/views/live/stu/Class.vue")
    },
    {
        path: "/live/stu/private",
        component: () => import("~/views/live/stu/PrivateClass.vue")
    },
    {
        path: "/live/tea/class",
        component: () => import("~/views/live/tea/Class.vue")
    },
    {
        path: "/live/tea/private",
        component: () => import("~/views/live/tea/PrivateClass.vue")
    },
    {
        path: "/user/message",
        component: () => import("~/views/user/Message.vue")
    },
    {
        path: "/user/profile",
        component: () => import("~/views/user/Profile.vue")
    },
    {
        path: "/user/workTable",
        component: () => import("~/views/user/WorkTable.vue")
    },
    {
        path: "/tool/record",
        component: () => import("~/views/tool/Record.vue")
    },
    {
        path: "/record/video",
        component: () => import("~/views/tool/Video.vue")
    },
    {
        path: "/open/course",
        component: () => import("~/views/open/Course.vue")
    },
    {
        path: "/open/video",
        component: () => import("~/views/open/Video.vue")
    },
    {
        path: "/test/editor",
        component: () => import("~/views/test/Editor.vue")
    }


]

const router = createRouter({
    history: createWebHashHistory(),
    routes: routes
})
let baiMD = [
    ''
]
router.beforeEach((to, from, next) => {
    if (to.path.startsWith('/') || to.path.startsWith('/login') || to.path.startsWith('/register')) {
        next()
    } else {
        if (isLogin()) {
            next()
        } else {
            ElMessage.error('请先登录')
            next({
                path: '/login'
            })
        }
    }
})

export default router