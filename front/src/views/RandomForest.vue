<template>
    <el-select v-model="data.stockId" placeholder="股票名称" filterable style="float: left; margin-right: 20px;">
        <el-option
            v-for="item in data.stockNameList"
            :key="item.stockId"
            :label="item.stockName"
            :value="item.stockId"
        />
    </el-select>

    <el-select v-model="data.preDays" placeholder="预测间隔" filterable style="float: left; margin-right: 20px;">
        <el-option label="3" value="3" />
        <el-option label="5" value="5" />
        <el-option label="7" value="7" />
        <el-option label="10" value="10" />
    </el-select>

    <div class="buttonContainer">
        <el-button type="primary" @click="predict('S')">短期均线预测</el-button>
        <el-button type="primary" @click="predict('M')">中期均线预测</el-button>
        <el-button type="primary" @click="predict('L')">长期均线预测</el-button>
        <el-button type="primary" @click="predict('SM')">短中期均线预测</el-button>
        <el-button type="primary" @click="predict('ML')">中长期均线预测</el-button>
        <el-button type="primary" @click="predict('SML')">三线预测</el-button>
    </div>

    <div class="resultDisplay" v-if="data.showResult">
        <el-input readonly="true" style="display: inline-block; margin-right: 20px; width: 200px;" v-model="data.meanAbsoluteError" />
        <el-input readonly="true" style="display: inline-block; margin-right: 20px; width: 200px;" v-model="data.meanSquaredError" />
        <el-input readonly="true" style="display: inline-block; margin-right: 20px; width: 200px;" v-model="data.rootMeanSquaredError" />
    </div>

    <div id="chart" class="single-chart"></div>
</template>

<script>
import { onBeforeMount, reactive } from "vue";
import { fillData, getRequest, message, postRequest } from "@/utils/api";

export default {
    name: "RandomForest",
    setup() {
        const data = reactive({
            stockId: "",
            preDays: "",
            stockNameList: [],
            stockDataList: [],
            showResult: false,
            meanAbsoluteError: "",
            meanSquaredError: "",
            rootMeanSquaredError: "",
        });
        onBeforeMount(() => {
            getRequest("/stockName/listAll").then((res) => {
                if (res.data.code == 200) {
                    data.stockNameList = res.data.data;
                } else if (res.data.code == 500) {
                    message(res.data.msg, "error");
                }
            });
            document.title = "随机森林算法";
        });
        function predict(type) {
            if (data.stockId == "") {
                message("请先选择预测股票", "info");
                return;
            }
            if (data.preDays == "") {
                message("请先选择预测时长", "info");
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
            postRequest("/stockData/randomForest", {
                stockName: stockName,
                predictType: predictType,
                preDays: data.preDays
            }).then((res) => {
                if (res.data.code == 200) {
                    data.showResult = true;
                    data.stockDataList = res.data.data[0];
                    data.meanAbsoluteError = "平均绝对误差：" + res.data.data[1];
                    data.meanSquaredError = "均方误差：" + res.data.data[2];
                    data.rootMeanSquaredError = "均方根误差：" + res.data.data[3];
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
                        xData.push(item[0]);
                        yData[0].data.push(item[1]);
                        yData[1].data.push(item[2]);
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
.buttonContainer {
    position: absolute;
    top: 60px;
}

.resultDisplay {
    position: absolute;
    top: 100px;
}

.single-chart {
    height: calc(100vh - 220px);
    min-height: 400px;
    margin-top: 130px;
}
</style>