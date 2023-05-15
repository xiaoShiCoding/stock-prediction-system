import axios from "axios";
import { ElMessage } from 'element-plus';
import * as echarts from 'echarts';

axios.defaults.baseURL = "http://127.0.0.1:10031";

export function message(msg, type) {
    ElMessage({
        message: msg,
        showClose: true,
        type: type,
        center: true
    })
}

export function messageDuration(msg, type, duration) {
    ElMessage({
        message: msg,
        showClose: true,
        type: type,
        center: true,
        duration: duration
    })
}

export const getRequest = (url, params) => {
    return axios({
        method: 'get',
        url: url,
        params: params,
        headers: {
            token: localStorage.getItem("token"),
            username: localStorage.getItem("username"),
            password: localStorage.getItem("password"),
        }
    })
}

export const postRequest = (url, data) => {
    return axios({
        method: 'post',
        url: url,
        data: data,
        headers: {
            token: localStorage.getItem("token"),
            username: localStorage.getItem("username"),
            password: localStorage.getItem("password"),
        }
    })
}

export const putRequest = (url, data) => {
    return axios({
        method: 'put',
        url: url,
        data: data,
        headers: {
            token: localStorage.getItem("token"),
            username: localStorage.getItem("username"),
            password: localStorage.getItem("password"),
        }
    })
}

export const deleteRequest = (url, data) => {
    return axios({
        method: 'delete',
        url: url,
        data: data,
        headers: {
            token: localStorage.getItem("token"),
            username: localStorage.getItem("username"),
            password: localStorage.getItem("password"),
        }
    })
}

export const downloadFileToLocal = (content, filename) => {
    var eleLink = document.createElement('a');
    eleLink.download = filename;
    eleLink.style.display = 'none';
    var blob = new Blob([content]);
    eleLink.href = URL.createObjectURL(blob);
    document.body.appendChild(eleLink);
    eleLink.click();
    document.body.removeChild(eleLink);
}

export const formatTime = (time) => {
    let year = time.getFullYear();
    let month = time.getMonth() + 1;
    let today = time.getDate();
    return year + '/' + fillZero(month) + '/' + fillZero(today);
}

function fillZero(str) {
    var realNum;
    if (str < 10) {
        realNum = '0' + str;
    } else {
        realNum = str;
    }
    return realNum;
}

function drawChart(id, title, xData, seriesData, legendData) {
    var chartDom = document.getElementById(id);
    var myChart = echarts.init(chartDom);
    myChart.clear();
    var option;

    option = {
        title: {
            text: title,
        },
        tooltip: {
            trigger: "axis",
        },
        legend: {
            data: legendData
        },
        grid: {
            left: "3%",
            right: "4%",
            bottom: "3%",
            containLabel: true,
        },
        toolbox: {
            feature: {
                saveAsImage: {},
            },
        },
        xAxis: {
            name: "日期",
            type: "category",
            data: xData,
        },
        yAxis: {
            name: "价格",
            type: "value",
            min: (value) => {
                return Math.floor(value.min);
            },
            max: (value) => {
                return Math.ceil(value.max);
            }
        },
        series: seriesData
    };

    option && myChart.setOption(option);

    window.addEventListener("resize", function () {
        myChart.resize();
    });
}

export const fillData = (title, xData, yData, nameList) => {
    const seriesData = [];
    for (var i = 0; i < nameList.length; i++) {
        seriesData.push({
            name: yData[i].name,
            type: "line",
            data: yData[i].data,
            smooth: true,
        })
    }
    drawChart("chart", title, xData, seriesData, nameList);
}
