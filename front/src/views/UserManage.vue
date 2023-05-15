<template>
    <el-input v-model="data.searchUsername" placeholder="输入用户名进行搜索" @input="initData"
        style="width: 200px; margin-right: 20px; margin-bottom: 14px; float: left;" />
    <el-input v-model="data.searchTime" placeholder="输入创建时间进行搜索" @input="initData"
        style="width: 200px; margin-bottom: 14px; float: left;" />

    <el-table :data="data.tableData" border style="width: 100%;" 
        :header-cell-style="{'text-align':'center'}" :cell-style="{'text-align':'center'}">
        <el-table-column prop="id" label="id" sortable />
        <el-table-column prop="username" label="用户名" sortable />
        <el-table-column prop="password" label="密码" sortable />
        <el-table-column prop="userType" label="用户类型" sortable />
        <el-table-column label="用户头像">
            <template #default="scope">
                <img :src="scope.row.avatarUrl" style="width: 100px; height: auto;" />
            </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" sortable />
    </el-table>

    <el-pagination background layout="prev, pager, next" :total="data.total" style="margin-top: 15px;"
        @current-change="initData" v-model:current-page="data.current" :page-size="data.pageSize" />
</template>

<script>
import { onBeforeMount, reactive } from "vue";
import { postRequest, message } from '@/utils/api';

export default {
    name: "UserManage",
    setup() {
        const data = reactive({
            tableData: [],
            searchUsername: "",
            searchTime: "",
            current: 1,
            total: 100,
            pageSize: 20,
        });
        onBeforeMount(() => {
            initData();
            document.title = "用户管理";
        });
        function initData() {
            postRequest("/user/list", {
                pageNum: data.current,
                searchUsername: data.searchUsername.trim(),
                searchTime: data.searchTime.trim()
            }).then((res) => {
                if (res.data.code == 200) {
                    data.tableData = res.data.data.userList;
                    data.total = res.data.data.total;
                } else if (res.data.code == 500) {
                    message(res.data.msg, "error");
                }
            });
        }
        return {
            data,
            initData,
        };
    },
};
</script>

<style scoped>
</style>