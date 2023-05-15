<template>
    <el-select v-model="data.stockId" placeholder="股票名称" filterable style="float: left; margin-right: 20px;">
        <el-option
            v-for="item in data.stockNameList"
            :key="item.stockId"
            :label="item.stockName"
            :value="item.stockId"
        />
    </el-select>

    <el-input placeholder="estimatorsCount" v-model="data.estimatorsCount" style="width: 140px; margin-right: 20px;" />
    <el-input placeholder="randomStates" v-model="data.randomStates" style="width: 140px; margin-right: 20px;" />
    <el-input placeholder="nJobs" v-model="data.nJobs" style="width: 140px; margin-right: 20px;" />

    <el-button type="primary" @click="predict">预测</el-button>

    <el-table id="img-container" :data="data.predictDataList" border style="width: calc(100% - 20px); margin-top: 20px;"
        :header-cell-style="{'text-align':'center'}" :cell-style="{'text-align':'center'}">
        <el-table-column prop="id" label="ID" />
        <el-table-column prop="estimatorsCount" label="estimatorsCount" width="150" />
        <el-table-column prop="randomStates" label="randomStates" width="140" />
        <el-table-column prop="njobs" label="nJobs" />
        <el-table-column prop="meanAbsoluteError" label="平均绝对误差" />
        <el-table-column prop="meanSquaredError" label="均方误差" />
        <el-table-column prop="rootMeanSquaredError" label="均方根误差" />
        <el-table-column label="使用状态">
            <template #default="scope">
                <el-tag type="info" v-if="scope.row.useState == 0">测试</el-tag>
                <el-tag type="success" v-if="scope.row.useState == 1">使用中</el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="createTime" label="预测时间" />
        <el-table-column label="操作">
            <template #default="scope">
                <el-popconfirm title="确认使用该参数吗?" @confirm="useArgument(scope.row)">
                    <template #reference>
                        <el-button size="small">使用参数</el-button>
                    </template>
                </el-popconfirm>
            </template>
        </el-table-column>
    </el-table>
</template>

<script>
import { onBeforeMount, reactive } from "vue";
import { getRequest, message, postRequest, messageDuration } from "@/utils/api";

export default {
    name: "RandomForestManage",
    setup() {
        const data = reactive({
            estimatorsCount: "",
            randomStates: "",
            nJobs: "",
            stockId: "",
            stockNameList: [],
            predictDataList: []
        });
        onBeforeMount(() => {
            getRequest("/stockName/listAll").then((res) => {
                if (res.data.code == 200) {
                    data.stockNameList = res.data.data;
                } else if (res.data.code == 500) {
                    message(res.data.msg, "error");
                }
            });
            initData();
            document.title = "随机森林参数管理";
        });
        function initData() {
            getRequest("/randomForestArgument/list").then((res) => {
                if (res.data.code == 200) {
                    data.predictDataList = res.data.data;
                } else if (res.data.code == 500) {
                    message(res.data.msg, "error");
                }
            });
        }
        function predict() {
            if (data.stockId == "") {
                message("请先选择预测股票", "info");
                return;
            }
            if (data.estimatorsCount.trim() == "" || data.randomStates.trim() == "" || data.nJobs.trim() == "") {
                message("请先填入参数", "info");
                return;
            }
            messageDuration("数据加载中, 请稍候", "info", 10000);

            var stockName = "";
            data.stockNameList.forEach(item => {
                if (item.stockId == data.stockId) {
                    stockName = item.stockName;
                }
            });
            postRequest("/stockData/randomForestManage", {
                stockName: stockName,
                predictType: "S",
                preDays: "5",
                estimatorsCount: data.estimatorsCount,
                randomStates: data.randomStates,
                nJobs: data.nJobs,
            }).then((res) => {
                if (res.data.code == 200) {
                    postRequest("/randomForestArgument/add", {
                        estimatorsCount: data.estimatorsCount,
                        randomStates: data.randomStates,
                        nJobs: data.nJobs,
                        meanAbsoluteError: res.data.data[1],
                        meanSquaredError: res.data.data[2],
                        rootMeanSquaredError: res.data.data[3],
                    }).then((addRes) => {
                        if (addRes.data.code == 200) {
                            initData();
                        } else if (addRes.data.code == 500) {
                            message(addRes.data.msg, "error");
                        }
                    });
                } else if (res.data.code == 500) {
                    message(res.data.msg, "error");
                }
            });
        }
        function useArgument(row) {
            console.log(row.id);
            postRequest("/randomForestArgument/useArgument", {
                id: row.id
            }).then((res) => {
                if (res.data.code == 200) {
                    message(res.data.msg, "success");
                    initData();
                } else if (res.data.code == 500) {
                    message(res.data.msg, "error");
                }
            });
        }
        
        return {
            data,
            predict,
            useArgument,
        };
    },
};
</script>

<style scoped>
</style>