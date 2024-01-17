<!-- affix -->
<template>
  <div>
    <el-table :data="assessData" border style="width: 100%">
      <el-table-column prop="criteria" label="Criteria" width="180" />
      <el-table-column prop="level" label="Name" width="300">
        <template #default="scope">
          <tr>
            <el-select
              v-model="scope.row.selectLevel"
              class="m-2"
              placeholder="Select"
            >
              <el-option
                v-for="item in scope.row.level"
                :key="item.name"
                :label="item.name + ' ' + item.description"
                :value="item.name"
              />
            </el-select>
          </tr>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  components: {},

  watch: {},

  computed: {},

  data() {
    return {
      assessData: [
        {
          criteria: "Re-performability",
          level: [
            {
              name: "[0]",
              description: "Unavailabile to acahieve final outputs",
            },
            {
              name: "[1]",
              description:
                "Availabile to acahieve final outputs (With the usage of other resources)",
            },
            {
              name: "[2]",
              description:
                "Availabile to acahieve final outputs (With the usage of initial resources)",
            },
          ],
          selectLevel: "",
        },
      ],
    };
  },

  methods: {},

  mounted() {},
};
</script>
<style lang="scss" scoped>

</style>
