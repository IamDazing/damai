import axios from 'axios'
import { getToken } from '@/utils/auth'
import useUserStore from '@/store/modules/user'

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'
// 是否显示重新登录
export let isRelogin = { show: false };
// 创建一个 axios 实例
const request = axios.create({
    baseURL: import.meta.env.VITE_APP_BASE_API, // 这里设置为代理的路径
    timeout: 10000, // 请求超时时间
    headers: {no_verify: false}
});

// 请求拦截器
request.interceptors.request.use(
    config => {
        //向请求头放置token
        if (getToken) {
            let token = getToken();
            config.headers = Object.assign(config.headers,{token:token});
        }
        return config;
    },
    error => {
        console.log(error); // 打印错误日志
        Promise.reject(error).then(r => {});
    }
);

// 响应拦截器
request.interceptors.response.use(
    response => {
        // 在这里可以做一些统一的响应处理逻辑
        let url = response.config.url;
        if ('/damai/user/user/logout' == url) {
            return response.data;
        }
        const code = response.data.code
        if(code == "10055"||code == "516"){
            if (!isRelogin.show) {
                isRelogin.show = true;
                ElMessageBox.confirm('登录状态已过期，您可以继续留在该页面，或者重新登录', '系统提示', { confirmButtonText: '重新登录', cancelButtonText: '取消', type: 'warning' })
                    .then(() => {
                    isRelogin.show = false;
                        console.log(useUserStore().logOut());
                        useUserStore().logOut().then(() => {
                          location.href='/login'
                    })
                }).catch(() => {
                    isRelogin.show = false;
                });
            }
            return Promise.reject('无效的会话，或者会话已过期，请重新登录。')
        }else{
            return response.data;
        }

    },
    error => {
        console.log(error); // 打印错误日志
        return Promise.reject(error);
    }
);

export default request
