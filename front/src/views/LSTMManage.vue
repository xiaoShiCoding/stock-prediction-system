<template>
    <el-select v-model="data.stockId" placeholder="股票名称" filterable style="float: left; margin-right: 20px;">
        <el-option
            v-for="item in data.stockNameList"
            :key="item.stockId"
            :label="item.stockName"
            :value="item.stockId"
        />
    </el-select>

    <el-input placeholder="lstmLayers" v-model="data.lstmLayers" style="width: 140px; margin-right: 20px;" />
    <el-input placeholder="denseLayers" v-model="data.denseLayers" style="width: 140px; margin-right: 20px;" />
    <el-input placeholder="units" v-model="data.units" style="width: 140px; margin-right: 20px;" />

    <el-button type="primary" @click="predict">预测</el-button>

    <el-table id="img-container" :data="data.predictDataList" border style="width: calc(100% - 20px); margin-top: 20px;"
        :header-cell-style="{'text-align':'center'}" :cell-style="{'text-align':'center'}">
        <el-table-column prop="id" label="ID" />
        <el-table-column prop="lstmLayers" label="lstmLayers" />
        <el-table-column prop="denseLayers" label="denseLayers" />
        <el-table-column prop="units" label="units" />
        <el-table-column prop="loss" label="损失精度" />
        <el-table-column prop="mape" label="偏差精度" />
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
    name: "LSTMManage",
    setup() {
        const data = reactive({
            lstmLayers: "",
            denseLayers: "",
            units: "",
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
            document.title = "LSTM参数管理";
        });
        function initData() {
            getRequest("/lstmArgument/list").then((res) => {
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
            if (data.lstmLayers.trim() == "" || data.denseLayers.trim() == "" || data.units.trim() == "") {
                message("请先填入参数", "info");
                return;
            }
            messageDuration("数据加载中, 请稍候", "info", 30000);

            var stockName = "";
            data.stockNameList.forEach(item => {
                if (item.stockId == data.stockId) {
                    stockName = item.stockName;
                }
            });
            postRequest("/stockData/lstmManage", {
                stockName: stockName,
                predictType: "S",
                preDays: "5",
                lstmLayers: data.lstmLayers,
                denseLayers: data.denseLayers,
                units: data.units,
            }).then((res) => {
                if (res.data.code == 200) {
                    postRequest("/lstmArgument/add", {
                        lstmLayers: data.lstmLayers,
                        denseLayers: data.denseLayers,
                        units: data.units,
                        loss: res.data.data[1],
                        mape: res.data.data[2],
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
            postRequest("/lstmArgument/useArgument", {
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