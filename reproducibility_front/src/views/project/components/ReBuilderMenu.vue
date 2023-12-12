<template>
  <div style="height: 100%">
    <!-- <el-menu :default-active="1" class="el-menu-demo" mode="horizontal" @select="handleClick">
            <el-menu-item index="1">处理中心</el-menu-item>
            <el-menu-item index="3">消息中心</el-menu-item>
          </el-menu> -->
    <div class="menu">
      <div
        class="info menu-item"
        @click="handleClick('Information')"
        :class="{ isActive: isInfoActive }"
      >
        <el-icon><el-icon-info /></el-icon>
        Information
      </div>
      <div
        class="scenario menu-item"
        @click="handleClick('Reproduction')"
        :class="{ isActive: isReproductionActive }"
      >
        <el-icon><el-icon-info /></el-icon>
        Reproduction
      </div>
      <div
        class="contributor menu-item"
        @click="handleClick('Contributor')"
        :class="{ isActive: isContributorActive }"
      >
        <el-icon><el-icon-info /></el-icon>
        Contributor
      </div>
      <div
        class="community menu-item"
        @click="handleClick('Community')"
        :class="{ isActive: isCommunityActive }"
      >
        <el-icon><el-icon-info /></el-icon>
        Community
      </div>
    </div>
  </div>
</template>

<script>
import { InfoFilled as ElIconInfo } from '@element-plus/icons-vue'
import { $emit } from '../../../utils/gogocodeTransfer'
export default {
  components: {
    ElIconInfo,
  },
  watch: {},
  computed: {},
  data() {
    return {
      isInfoActive: true,
      isReproductionActive: false,
      isContributorActive: false,
      isSettingsActive: false,
      isCommunityActive: false,
    }
  },
  methods: {
    handleClick(type) {
      if (type == 'Information') {
        this.isInfoActive = true
        this.isReproductionActive =
          this.isContributorActive =
          this.isSettingsActive =
          this.isCommunityActive =
            false
      } else if (type == 'Reproduction') {
        this.isReproductionActive = true
        this.isInfoActive =
          this.isContributorActive =
          this.isSettingsActive =
          this.isCommunityActive =
            false
      } else if (type == 'Contributor') {
        this.isContributorActive = true
        this.isInfoActive =
          this.isReproductionActive =
          this.isSettingsActive =
          this.isCommunityActive =
            false
      } else if (type == 'Community') {
        this.isCommunityActive = true
        this.isInfoActive =
          this.isReproductionActive =
          this.isSettingsActive =
          this.isContributorActive =
            false
      }
      $emit(this, 'toRouterType', type)
    },

    init() {
      switch (this.$router.currentRoute.name) {
        case 'Information': {
          this.isInfoActive = true
          this.isReproductionActive =
            this.isContributorActive =
            this.isSettingsActive =
              false
          break
        }
        case 'Reproduction': {
          this.isReproductionActive = true
          this.isInfoActive =
            this.isContributorActive =
            this.isSettingsActive =
              false
          break
        }
        case 'Contributor': {
          this.isContributorActive = true
          this.isInfoActive =
            this.isReproductionActive =
            this.isSettingsActive =
              false
          break
        }
        case 'Community': {
          this.isCommunityActive = true
          this.isInfoActive =
            this.isReproductionActive =
            this.isSettingsActive =
            this.isContributorActive =
              false
        }
      }
    },
  },
  mounted() {
    this.init()
  },
  emits: ['toRouterType'],
}
</script>

<style lang="scss" scoped>
.menu {
  height: 100%;
  font-size: 15px;
  color: $normalFontColor;
  .menu-item {
    line-height: 40px;
    text-align: center;
    width: 150px;
    padding: 25px 0 7px 0;
    border-bottom: 2px solid transparent;
    transition: border-bottom-color 0.36s ease-out;
    float: left;
  }
  .menu-item:hover {
    cursor: pointer;
    border-bottom: 2px solid transparent;
    border-bottom-color: $menuHoverBorderColor;
  }
  .isActive {
    border-bottom-color: $selectOrangeBorderColor;
  }
}
</style>
