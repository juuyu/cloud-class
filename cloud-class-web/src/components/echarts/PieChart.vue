<template>
  <div class="chart" ref="chartDom"></div>
</template>
<script lang="ts" setup>
import {ref, onMounted, onBeforeUnmount, defineProps, watch, toRefs, reactive} from "vue";
import {EChartsOption} from "echarts";
import * as echarts from 'echarts'

interface Props {
  data: any
}

const props = defineProps<Props>()
// const emit = defineEmits(['getPieChartData'])
const {data} = toRefs(props)


const chartDom = ref()
let myChart: any = null
const option: EChartsOption = reactive({
  tooltip: {
    trigger: 'item'
  },
  legend: {
    top: '5%',
    left: 'center'
  },
  series: [
    {
      name: 'Access From',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 40,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: data.value
    }
  ]
})

//重绘图表函数
const resizeHandler = () => {
  myChart.resize();
}
//设置防抖，保证无论拖动窗口大小，只执行一次获取浏览器宽高的方法
const debounce = (fun: any, delay: any) => {
  let timer: any;
  return function () {
    if (timer) {
      clearTimeout(timer);
    }
    timer = setTimeout(() => {
      fun();
    }, delay);
  }
};
const changeSize = debounce(resizeHandler, 500);
//页面成功渲染，开始绘制图表
onMounted(() => {
  console.log(data.value)
  myChart = echarts.init(chartDom.value)
  myChart.setOption(option, true);
  //自适应不同屏幕时改变图表尺寸
  window.addEventListener('resize', changeSize);
})
//页面销毁前，销毁事件和实例
onBeforeUnmount(() => {
  window.removeEventListener('resize', changeSize)
  myChart.dispose()
})

</script>
<style scoped>
.chart {
  width: 100%;
  height: 100%
}
</style>
