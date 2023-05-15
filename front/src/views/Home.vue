<template>
    <div class="nav">
        <div class="title">股票预测</div>

        <el-menu :collapse="false" background-color="#3d3d3d" text-color="#fff" active-text-color="#ffd04b" v-if="data.userType == 'normal'"
            router :default-active="data.curMenu" :unique-opened="true">
            <el-menu-item index="/index"><template #title><el-icon><HomeFilled /></el-icon>系统首页</template></el-menu-item>
            <el-menu-item index="/analyse"><template #title><el-icon><Histogram /></el-icon>影响力分析</template></el-menu-item>
            <el-menu-item index="/randomForest"><template #title><el-icon><Promotion /></el-icon>随机森林</template></el-menu-item>
            <el-menu-item index="/LSTM"><template #title><el-icon><Promotion /></el-icon>LSTM</template></el-menu-item>
            <el-menu-item index="/KNN"><template #title><el-icon><Promotion /></el-icon>KNN</template></el-menu-item>
            <el-menu-item index="/leastSquares"><template #title><el-icon><Tools /></el-icon>线性回归</template></el-menu-item>
        </el-menu>

        <el-menu :collapse="false" background-color="#3d3d3d" text-color="#fff" active-text-color="#ffd04b"  v-if="data.userType == 'admin'"
            router :default-active="data.curMenu" :unique-opened="true">
            <el-menu-item index="/index"><template #title><el-icon><HomeFilled /></el-icon>系统首页</template></el-menu-item>
            <el-menu-item index="/analyse"><template #title><el-icon><Histogram /></el-icon>影响力分析</template></el-menu-item>
            <el-menu-item index="/randomForest"><template #title><el-icon><Promotion /></el-icon>随机森林</template></el-menu-item>
            <el-menu-item index="/LSTM"><template #title><el-icon><Promotion /></el-icon>LSTM</template></el-menu-item>
            <el-menu-item index="/KNN"><template #title><el-icon><Promotion /></el-icon>KNN</template></el-menu-item>
            <el-menu-item index="/leastSquares"><template #title><el-icon><Tools /></el-icon>线性回归</template></el-menu-item>
            <el-menu-item index="/stockManage"><template #title><el-icon><Management /></el-icon>日级股票管理</template></el-menu-item>
            <el-menu-item index="/stockMinuteManage"><template #title><el-icon><Management /></el-icon>分钟级股票管理</template></el-menu-item>
            <el-menu-item index="/userManage"><template #title><el-icon><User /></el-icon>用户管理</template></el-menu-item>
            <el-menu-item index="/LSTMManage"><template #title><el-icon><Promotion /></el-icon>LSTM模型参数</template></el-menu-item>
            <el-menu-item index="/randomForestManage"><template #title><el-icon><Promotion /></el-icon>随机森林模型参数</template></el-menu-item>
        </el-menu>
    </div>

    <div class="header">
        <el-button class="toLogin" v-if="!data.avatarUrl" @click="toPath('/login')">登录</el-button>

        <el-dropdown class="avatar-dropdown" v-if="data.avatarUrl">
            <img :src="data.avatarUrl" class="avatar" />
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item @click="data.editAvatarUrlDialogVisible = true">更换头像</el-dropdown-item>
                    <el-dropdown-item>
                        <el-popconfirm title="确定退出登录吗?" @confirm="logout">
                            <template #reference>
                                退出登录
                            </template>
                        </el-popconfirm>
                    </el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
    </div>

    <el-dialog v-model="data.editAvatarUrlDialogVisible" title="修改头像" width="60%">
        <el-form-item label="图片URL路径">
            <el-input placeholder="图片路径(网络可访问到的)" v-model="data.editAvatarUrl" />
        </el-form-item>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="data.editAvatarUrlDialogVisible = false">Cancel</el-button>
                <el-button type="primary" @click="editAvatarUrl">Confirm</el-button>
            </span>
        </template>
    </el-dialog>

    <div class="routerContainer">
        <router-view v-slot="{ Component }">
            <keep-alive>
                <component :is="Component" :key="$route.name" v-if="$route.meta.keepAlive" />
            </keep-alive>
            <component :is="Component" :key="$route.name" v-if="!$route.meta.keepAlive" />
        </router-view>
    </div>
</template>

<script>
import { onBeforeMount, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { getRequest, message, putRequest } from '@/utils/api';

export default {
    name: "Home",
    setup() {
        const data = reactive({
            curMenu: "",
            userType: "normal",
            avatarUrl: "",
            editAvatarUrlDialogVisible: false,
            editAvatarUrl: "",
        });
        const myRouter = useRouter();
        onBeforeMount(() => {
            data.curMenu = myRouter.currentRoute.value.path;
            data.avatarUrl = localStorage.getItem("avatarUrl") ? localStorage.getItem("avatarUrl") : "";
            data.userType = localStorage.getItem("userType") ? localStorage.getItem("userType") : "";
        })
        function logout() {
            getRequest("/user/logout?username=" + localStorage.getItem("username")).then((res) => {
                if (res.data.code == "200") {
                    message(res.data.msg, "success");
                    localStorage.removeItem("username");
                    localStorage.removeItem("password");
                    localStorage.removeItem("avatarUrl");
                    localStorage.removeItem("token");
                    localStorage.removeItem("userType");
                    toPath("/login");
                } else if (res.data.code == "500") {
                    message(res.data.msg, "error");
                }
            });
        }
        function toPath(path) {
            myRouter.push({
                path: path
            })
        }
        function editAvatarUrl() {
            putRequest("/user/editAvatarUrl", {
                username: localStorage.getItem("username"),
                editAvatarUrl: data.editAvatarUrl
            }).then((res) => {
                if (res.data.code == "200") {
                    message(res.data.msg, "success");
                    data.avatarUrl = data.editAvatarUrl;
                    localStorage.setItem("avatarUrl", data.avatarUrl);
                    data.editAvatarUrlDialogVisible = false;
                } else if (res.data.code == "500") {
                    message(res.data.msg, "error");
                }
            });
        }

        return {
            data,
            logout,
            toPath,
            editAvatarUrl,
        };
    },
};
</script>

<style scoped>
/* 导航区域样式 */
.nav {
    position: absolute;
    left: 0;
    top: 0;
    width: 200px;
    height: 100vh;
    background-color: #3d3d3d;
    text-align: center;
}
/* 标题样式 */
.title {
    font-size: 20px;
    color: white;
    width: 100%;
    height: 60px;
    line-height: 60px;
    border-bottom: 1px solid #3d3d3d;
    background-color: #2e2e2e;
}

/* 头部样式 */
.header {
    position: absolute;
    left: 200px;
    top: 0;
    width: calc(100vw - 200px);
    height: 60px;
    background-color: #2e2e2e;
}
/* 登录按钮样式 */
.toLogin {
    position: absolute;
    right: 30px;
    top: 15px;
    border-radius: 50%;
}
/* 头像样式 */
.avatar-dropdown {
    position: absolute;
    right: 60px;
    top: 10px;
}
.avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    outline: none;
    cursor: pointer;
}

/* 内容主体路由容器样式 */
.routerContainer {
    position: absolute;
    left: 200px;
    top: 60px;
    width: calc(100vw - 200px);
    height: calc(100vh - 60px);
    padding: 10px;
    box-sizing: border-box;
    overflow-y: scroll;
}
.routerContainer::-webkit-scrollbar {
    width: 0;
}
</style>