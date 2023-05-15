<template>
    <el-form :model="loginForm" class="loginCantainer">
        <h3 class="loginTitle">系统登录</h3>
        <el-form-item>
            <el-input type="text" v-model="loginForm.username" placeholder="请输入用户名" size="large"></el-input>
        </el-form-item>

        <el-form-item>
            <el-input v-model="loginForm.password" show-password placeholder="请输入密码" size="large"></el-input>
        </el-form-item>

        <el-form-item>
            <el-button type="primary" size="large" style="width: 100%" @click="login">登录</el-button>
        </el-form-item>

        <el-form-item>
            <el-link type="primary" :underline="false" @click="toRegister">注册</el-link>
        </el-form-item>
    </el-form>
</template>

<script>
import { postRequest, message } from '../utils/api'
import { onBeforeMount, reactive } from 'vue'
import router from '@/router';

export default {
    name: "Login",
    components: {},
    setup() {
        const loginForm = reactive({
            username: '',
            password: ''
        });
        onBeforeMount(() => {
            document.title = "登录";
        })
        
        function login() {
            if (loginForm.username == "" || loginForm.password == "") {
                message("请输入用户名与密码后登录", "info");
            }
            postRequest("/user/login", {
                username: loginForm.username,
                password: loginForm.password,
            }).then((res) => {
                if (res.data.code == "200") {
                    message(res.data.msg, "success");
                    const user = res.data.data;
                    localStorage.setItem("username", user.username);
                    localStorage.setItem("password", user.password);
                    localStorage.setItem("avatarUrl", user.avatarUrl);
                    localStorage.setItem("token", user.token);
                    localStorage.setItem("userType", user.userType);
                    router.push({
                        path: "/home"
                    });
                } else if (res.data.code == "500") {
                    message(res.data.msg, "error");
                }
            });
        }

        function toRegister() {
            router.push({
                path: "/register"
            });
        }

        return {
            loginForm,
            login,
            toRegister,
        }
    }
};
</script>

<style scoped>
/* 登录窗体容器 */
.loginCantainer {
    border-radius: 15px;
    background-clip: padding-box;
    margin: 180px auto;
    width: 350px;
    padding: 15px 30px;
    background: #fff;
    border: 1px solid #eeeaea;
    box-shadow: 0 0 25px #cac6c6;
}

/* 登录标题 */
.loginTitle {
    margin: 0 auto 20px auto;
    text-align: center;
    color: lightskyblue;
    font-size: 24px;
}
</style>