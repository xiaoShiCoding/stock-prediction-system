<template>
    <el-select v-model="data.stockId" placeholder="股票名称" filterable style="float: left; margin-right: 20px;">
        <el-option
            v-for="item in data.stockNameList"
            :key="item.stockId"
            :label="item.stockName"
            :value="item.stockId"
        />
    </el-select>

    <el-button type="primary" @click="predict('S')">短期均线预测</el-button>
    <el-button type="primary" @click="predict('M')">中期均线预测</el-button>
    <el-button type="primary" @click="predict('L')">长期均线预测</el-button>
    <el-button type="primary" @click="predict('SM')">短中期均线预测</el-button>
    <el-button type="primary" @click="predict('ML')">中长期均线预测</el-button>
    <el-button type="primary" @click="predict('SML')">三线预测</el-button>

    <div id="chart" class="single-chart"></div>
</template>

<script>
import { onBeforeMount, reactive } from "vue";
import { fillData, getRequest, message, postRequest } from "@/utils/api";

export default {
    name: "LeastSquares",
    setup() {
        const data = reactive({
            stockId: "",
            stockNameList: [],
            stockDataList: [],
        });
        onBeforeMount(() => {
            getRequest("/stockName/listAll").then((res) => {
                if (res.data.code == 200) {
                    data.stockNameList = res.data.data;
                } else if (res.data.code == 500) {
                    message(res.data.msg, "error");
                }
            });
            document.title = "最小二乘法";
        });
        function predict(type) {
            if (data.stockId == "") {
                message("请先选择预测股票", "info");
                return;
            }
            message("数据加载中, 请稍候", "info");

            var predictType = "S";
            switch (type) {
                case "S":
                    predictType = "S";
                    break;
                case "M":
                    predictType = "M";
                    break;
                case "L":
                    predictType = "L";
                    break;
                case "SM":
                    predictType = "SM";
                    break;
                case "ML":
                    predictType = "ML";
                    break;
                case "SML":
                    predictType = "SML";
                    break;
            }

            var stockName = "";
            data.stockNameList.forEach(item => {
                if (item.stockId == data.stockId) {
                    stockName = item.stockName;
                }
            });
            postRequest("/stockData/leastSquares", {
                stockName: stockName,
                predictType: predictType
            }).then((res) => {
                if (res.data.code == 200) {
                    data.stockDataList = res.data.data;
                    const xData = [];
                    const yData = [];
                    const nameList = ["originEndPrice", "predictEndPrice"];
                    for (var i = 0; i < nameList.length; i ++) {
                        yData.push({
                            name: nameList[i],
                            data: []
                        })
                    }
                    data.stockDataList.forEach(item => {
                        xData.push(item.stockDate);
                        yData[0].data.push(item.originPrice);
                        yData[1].data.push(item.predictPrice);
                    });
                    fillData(stockName + "股票收盘价预测情况", xData, yData, nameList);
                } else if (res.data.code == 500) {
                    message(res.data.msg, "error");
                }
            });
        }
        
        return {
            data,
            predict,
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