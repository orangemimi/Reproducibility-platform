<template>
  <el-table :data="configuration(behavior)" border style="font-size: 18px">
    <el-table-column prop="name" label="Name" width="250"> </el-table-column>
    <el-table-column prop="type" label="Type" width="120"> </el-table-column>
    <el-table-column prop="description" label="Description" width="110">
    </el-table-column>

    <el-table-column label="Data or Parameter">
      <template v-slot="inner_scope">
        <el-button
          v-if="inner_scope.row.dataServiceId != null"
          @click="download(inner_scope.row.dataServiceId)"
          :icon="ElIconDownload"
          size="small"
          round
          >Downoload</el-button
        >
        <el-input
          :disabled="true"
          v-if="inner_scope.row.value != null"
          model-value="inner_scope.row.value"
        ></el-input>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
import { Download as ElIconDownload } from '@element-plus/icons-vue'
import config from '@/config'
export default {
  data() {
    return {
      ElIconDownload,
    }
  },
  props: ['behavior'],
  methods: {
    download(dataServiceId) {
      window.open(`${config.containerURL}/data_service/fetch/${dataServiceId}`)
    },
    configuration({ inputs, parameters, outputs }) {
      let arr = []

      inputs.forEach(({ name, description, dataServiceId }) => {
        if (dataServiceId != null) {
          arr.push({
            name,
            type: 'input',
            description,
            dataServiceId,
          })
        }
      })
      if (parameters != null) {
        parameters.forEach(({ name, description, value }) => {
          arr.push({
            name,
            type: 'parameter',
            description,
            value,
          })
        })
      }

      outputs.forEach(({ name, description, dataServiceId }) => {
        if (dataServiceId != null) {
          arr.push({
            name,
            type: 'output',
            description,
            dataServiceId,
          })
        }
      })
      return arr
    },
  },
}
</script>
