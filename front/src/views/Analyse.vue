<template>
    <el-select v-model="data.stockId" placeholder="股票名称" filterable style="float: left; margin-right: 20px;">
        <el-option
            v-for="item in data.stockNameList"
            :key="item.stockId"
            :label="item.stockName"
            :value="item.stockId"
        />
    </el-select>

    <el-date-picker
        v-model="data.selectTime"
        type="daterange"
        format="YYYY/MM/DD"
        range-separator="To"
        start-placeholder="Start date"
        end-placeholder="End date"
        style="float: left; margin-right: 20px;"
    />

    <el-button type="primary" @click="drawBasicInfo(1)">图示基本数据</el-button>
    <el-button type="primary" @click="drawAverageInfo(1)">图示均线数据</el-button>
    <el-button type="primary" @click="drawBasicInfo(2)">表格显示基本数据</el-button>
    <el-button type="primary" @click="drawAverageInfo(3)">表格显示均线数据</el-button>

    <div id="chart" class="single-chart" v-if="data.showMode == 1"></div>

    <el-table id="img-container" :data="data.stockDataList" border style="width: calc(100% - 20px); margin-top: 20px;" v-if="data.showMode == 2"
        :header-cell-style="{'text-align':'center'}" :cell-style="{'text-align':'center'}">
        <el-table-column prop="stockDate" label="日期" />
        <el-table-column prop="startPrice" label="开盘价" />
        <el-table-column prop="lowPrice" label="最低价" />
        <el-table-column prop="highPrice" label="最高价" />
        <el-table-column prop="endPrice" label="收盘价" />
    </el-table>

    <el-table id="img-container" :data="data.averageDataList" border style="width: calc(100% - 20px); margin-top: 20px;" v-if="data.showMode == 3"
        :header-cell-style="{'text-align':'center'}" :cell-style="{'text-align':'center'}">
        <el-table-column prop="stockDate" label="日期" />
        <el-table-column prop="m5" label="MA5" />
        <el-table-column prop="m10" label="MA10" />
        <el-table-column prop="m20" label="MA20" />
        <el-table-column prop="m30" label="MA30" />
        <el-table-column prop="m60" label="MA60" />
        <el-table-column prop="m120" label="MA120" />
        <el-table-column prop="m250" label="MA250" />
    </el-table>
</template>

<script>
import { onBeforeMount, reactive } from "vue";
import { fillData, getRequest, message, postRequest, formatTime } from "@/utils/api";

export default {
    name: "Analyse",
    setup() {
        const data = reactive({
            stockId: "",
            stockNameList: [],
            selectTime: [],
            stockDataList: [],
            averageDataList: [],
            showMode: 1,
        });
        onBeforeMount(() => {
            getRequest("/stockMinuteName/listAll").then((res) => {
                if (res.data.code == 200) {
                    data.stockNameList = res.data.data;
                } else if (res.data.code == 500) {
                    message(res.data.msg, "error");
                }
            });
            document.title = "影响力分析";
        });
        function drawBasicInfo(showMode) {
            if (data.stockId == "") {
                message("请先选择预测股票", "info");
                return;
            }
            if (data.selectTime == "") {
                message("请先选择预测日期", "info");
                return;
            }
            if (formatTime(data.selectTime[1]) > formatTime(new Date())) {
                message("日期超出范围", "info");
                return;
            }
            data.showMode = showMode;
            var stockName = "";
            data.stockNameList.forEach(item => {
                if (item.stockId == data.stockId) {
                    stockName = item.stockName;
                }
            });
            postRequest("/stockMinuteData/getBasicData", {
                stockId: data.stockId,
                startDate: formatTime(data.selectTime[0]),
                endDate: formatTime(data.selectTime[1])
            }).then((res) => {
                if (res.data.code == 200) {
                    data.stockDataList = res.data.data;
                    const xData = [];
                    const yData = [];
                    const nameList = ["startPrice", "highPrice", "lowPrice", "endPrice"];
                    for (var i = 0; i < nameList.length; i ++) {
                        yData.push({
                            name: nameList[i],
                            data: []
                        })
                    }
                    data.stockDataList.forEach(item => {
                        xData.push(item.stockDate);
                        yData[0].data.push(item.startPrice);
                        yData[1].data.push(item.highPrice);
                        yData[2].data.push(item.lowPrice);
                        yData[3].data.push(item.endPrice);
                    });
                    fillData(stockName + "股票基本数据情况", xData, yData, nameList);
                } else if (res.data.code == 500) {
                    message(res.data.msg, "error");
                }
            });
        }
        function drawAverageInfo(showMode) {
            if (data.stockId == "") {
                message("请先选择预测股票", "info");
                return;
            }
            if (data.selectTime == "") {
                message("请先选择预测日期", "info");
                return;
            }
            message("数据加载中, 请稍候", "info");
            data.showMode = showMode;

            var stockName = "";
            data.stockNameList.forEach(item => {
                if (item.stockId == data.stockId) {
                    stockName = item.stockName;
                }
            });
            postRequest("/stockMinuteData/getAverageData", {
                stockName: stockName,
                startDate: formatTime(data.selectTime[0]),
                endDate: formatTime(data.selectTime[1])
            }).then((res) => {
                if (res.data.code == 200) {
                    data.averageDataList = res.data.data;
                    const xData = [];
                    const yData = [];
                    const nameList = ["MA5", "MA10", "MA20", "MA30", "MA60", "MA120", "MA250"];
                    for (var i = 0; i < 7; i ++) {
                        yData.push({
                            name: nameList[i],
                            data: []
                        })
                    }
                    data.averageDataList.forEach(item => {
                        xData.push(item.stockDate);
                        yData[0].data.push(item.m5);
                        yData[1].data.push(item.m10);
                        yData[2].data.push(item.m20);
                        yData[3].data.push(item.m30);
                        yData[4].data.push(item.m60);
                        yData[5].data.push(item.m120);
                        yData[6].data.push(item.m250);
                    });
                    fillData(stockName + "股票均线数据情况", xData, yData, nameList);
                } else if (res.data.code == 500) {
                    message(res.data.msg, "error");
                }
            });
        }
        
        return {
            data,
            drawBasicInfo,
            drawAverageInfo,
        };
    },
};
</script>

<style scoped>
.single-chart {
    height: calc(100vh - 140px);
    min-height: 400px;
    margin-top: 20px;
}
</style>