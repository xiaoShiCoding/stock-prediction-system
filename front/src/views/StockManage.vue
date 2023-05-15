<template>
    <el-upload action="#" :auto-upload="false" :limit="100" :on-change="add" style="float: left; margin-right: 20px;"
        :show-file-list="false" accept=".txt">
        <el-button type="primary">添加股票数据</el-button>
    </el-upload>

    <el-popconfirm title="确认备份数据库吗?" @confirm="backup">
        <template #reference>
            <el-button style="float: left; margin-right: 20px;">备份数据库</el-button>
        </template>
    </el-popconfirm>

    <el-upload action="#" :auto-upload="false" :limit="10" :on-change="restore" style="float: left; margin-right: 20px;"
        :show-file-list="false" accept=".sql">
        <el-button>导入数据库</el-button>
    </el-upload>

    <el-input v-model="data.searchStockName" placeholder="输入股票名称进行搜索" @input="initData"
        style="width: 200px; margin-right: 20px; margin-bottom: 14px; float: left;" />
    <el-input v-model="data.searchTime" placeholder="输入创建时间进行搜索" @input="initData"
        style="width: 200px; margin-bottom: 14px; float: left;" />

    <el-table :data="data.tableData" border style="width: 100%;" 
        :header-cell-style="{'text-align':'center'}" :cell-style="{'text-align':'center'}">
        <el-table-column prop="id" label="id" sortable />
        <el-table-column prop="stockId" label="股票编号" sortable />
        <el-table-column prop="stockName" label="股票名称" sortable />
        <el-table-column prop="createTime" label="创建时间" sortable />
        <el-table-column label="操作">
            <template #default="scope">
                <el-popconfirm title="确认导出该股票数据吗?" @confirm="exportData(scope.row)">
                    <template #reference>
                        <el-button size="small">导出数据</el-button>
                    </template>
                </el-popconfirm>
                <el-popconfirm title="确认删除该股票数据吗?" @confirm="deleteStock(scope.row)">
                    <template #reference>
                        <el-button size="small" type="danger">删除股票</el-button>
                    </template>
                </el-popconfirm>
            </template>
        </el-table-column>
    </el-table>

    <el-pagination background layout="prev, pager, next" :total="data.total" style="margin-top: 15px;"
        @current-change="initData" v-model:current-page="data.current" :page-size="data.pageSize" />
</template>

<script>
import { onBeforeMount, reactive } from "vue";
import { postRequest, message, getRequest, downloadFileToLocal, deleteRequest } from '@/utils/api';

export default {
    name: "StockManage",
    setup() {
        const data = reactive({
            tableData: [],
            searchStockName: "",
            searchTime: "",
            current: 1,
            total: 100,
            pageSize: 20,
        });
        onBeforeMount(() => {
            initData();
            document.title = "日级股票数据管理";
        });
        function initData() {
            postRequest("/stockName/list", {
                pageNum: data.current,
                searchStockName: data.searchStockName.trim(),
                searchTime: data.searchTime.trim()
            }).then((res) => {
                if (res.data.code == 200) {
                    data.tableData = res.data.data.stockNameList;
                    data.total = res.data.data.total;
                } else if (res.data.code == 500) {
                    message(res.data.msg, "error");
                }
            });
        }

        function add(file) {
            let formData = new FormData();
            formData.append("file", file.raw);
            postRequest("/stockData/add", formData).then((res) => {
                if (res.data.code == "200") {
                    message(res.data.msg, "success");
                    initData();
                } else if (res.data.code == "500") {
                    message(res.data.msg, "error");
                }
            });
        }
        function addMinute(file) {
            let formData = new FormData();
            formData.append("file", file.raw);
            postRequest("/stockData/add", formData).then((res) => {
                if (res.data.code == "200") {
                    message(res.data.msg, "success");
                    initData();
                } else if (res.data.code == "500") {
                    message(res.data.msg, "error");
                }
            });
        }
        function exportData(row) {
            getRequest("/stockName/exportData?stockId=" + row.stockId).then((res) => {
                if (res.data.code == "500") {
                    message(res.data.msg, "error");
                } else {
                    downloadFileToLocal(res.data, row.stockName + ".txt");
                }
            });
        }
        function backup() {
            getRequest("/database/backup").then((res) => {
                if (res.data.code == "500") {
                    message(res.data.msg, "error");
                } else {
                    downloadFileToLocal(res.data, "stock.sql");
                }
            });
        }
        function restore(file) {
            let formData = new FormData();
            formData.append("file", file.raw);
            postRequest("/database/restore", formData).then((res) => {
                if (res.data.code == "200") {
                    message(res.data.msg, "success");
                    setTimeout(() => {
                        initData();
                    }, 500);
                } else if (res.data.code == "500") {
                    message(res.data.msg, "error");
                }
            });
        }

        function deleteStock(row) {
            deleteRequest("/stockName/deleteStock?stockId=" + row.stockId).then((res) => {
                if (res.data.code == "200") {
                    message(res.data.msg, "success");
                    initData();
                } else if (res.data.code == "500") {
                    message(res.data.msg, "error");
                }
            });
        }

        return { 
            data,
            initData,
            add,
            exportData,
            backup,
            restore,
            deleteStock,
        };
    },
};
</script>

<style scoped>
</style>