<template>
    <el-form :model="registerForm" class="registerCantainer">
        <h3 class="registerTitle">系统注册</h3>
        <el-form-item>
            <el-input type="text" v-model="registerForm.username" @blur="verifyUsername" placeholder="请输入用户名" size="large"></el-input>
        </el-form-item>

        <el-form-item>
            <el-input v-model="registerForm.password" show-password @blur="verifyPassword" placeholder="请输入密码" size="large"></el-input>
        </el-form-item>

        <el-form-item>
            <el-input v-model="registerForm.repeadPassword" show-password @blur="verifyRepeatPassword" placeholder="请再次输入密码" size="large"></el-input>
        </el-form-item>

        <el-form-item>
            <el-button type="primary" size="large" style="width: 100%" @click="register">注册</el-button>
        </el-form-item>

        <el-form-item>
            <el-link type="primary" :underline="false" size="large" @click="toLogin">登录</el-link>
        </el-form-item>
    </el-form>
</template>

<script>
import {postRequest, message } from '../utils/api'
import { onBeforeMount, reactive } from 'vue'
import router from '@/router';

export default {
    name: "Register",
    components: {},
    setup() {
        const registerForm = reactive({
            username: '',
            password: '',
            repeadPassword: '',
        });
        onBeforeMount(() => {
            document.title = "注册";
        })
        
        function register() {
            if(verifyRepeatPassword()){
                if(verifyPassword() && verifyUsername()){
                    postRequest('/user/register', {
                        username: registerForm.username,
                        password: registerForm.password
                    }).then(res => {
                        if (res.data.code == '200'){
                            router.push({
                                path: "/login"
                            });
                            message(res.data.msg, 'success');
                        } else {
                            message(res.data.msg, 'error');
                        }
                    })
                } else {
                    message('密码与用户名要求6-10位,由字母数字下划线组成,请重新输入', 'error')
                }
            } else {
                message('两次密码不一致，请重新输入', 'error');
            }
        }

        let reg = /^[\w]{6,10}$/
        // 验证密码是否符合6-10位由字母数字下划线组成的要求
        function verifyPassword() {
            if(registerForm.password.match(reg)){
                return true
            } else {
                message('密码要求6-10位,由字母数字下划线组成,请重新输入', 'error')
                return false
            }
        }

        // 验证重复密码是否一致
        function verifyRepeatPassword() {
            if(registerForm.password == registerForm.repeadPassword){
                return true
            } else {
                message('两次密码需要一致', 'error')
                return false
            }
        }

        // 验证用户名是否符合6-10位由字母数字下划线组成的要求
        function verifyUsername() {
            if(registerForm.username.match(reg)){
                return true
            } else {
                message('用户名要求6-10位,由字母数字下划线组成,请重新输入', 'error')
                return false
            }
        }

        function toLogin() {
            router.push({
                path: "/login"
            });
        }

        return {
            registerForm,
            register,
            toLogin,
            verifyPassword,
            verifyRepeatPassword,
            verifyUsername
        }
    }
}
</script>

<style scoped>
/* 注册窗体容器 */
.registerCantainer {
    border-radius: 15px;
    background-clip: padding-box;
    margin: 120px auto;
    width: 350px;
    padding: 15px 30px;
    background: #fff;
    border: 1px solid #eeeaea;
    box-shadow: 0 0 25px #cac6c6;
}

/* 注册标题 */
.registerTitle {
    margin: 0 auto 20px auto;
    text-align: center;
    color: lightskyblue;
    font-size: 24px;
}
</style>