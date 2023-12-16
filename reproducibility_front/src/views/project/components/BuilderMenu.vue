<!-- BUilder Menu -->
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
<<<<<<< Updated upstream
        <i class="el-icon-info"></i>
=======
        <el-icon><InfoFilled /></el-icon>
>>>>>>> Stashed changes
        Information
      </div>
      <!-- <div class="construction menu-item" @click="handleClick('Construction')" :class="{ isActive: isConstructionActive }">
        <i class="el-icon-info"></i>
        Construction
      </div> -->
      <div
        class="construction1 menu-item"
        @click="handleClick('Construction')"
        :class="{ isActive: isConstruction1Active }"
      >
<<<<<<< Updated upstream
        <i class="el-icon-info"></i>
        Construction
      </div>
      <div
=======
        <el-icon><InfoFilled /></el-icon>
        Construction
      </div>
      <div
        class="WorkFlow menu-item"
        @click="handleClick('WorkFlow')"
        :class="{ isActive: isWorkFlowActive }"
      >
        <el-icon><InfoFilled /></el-icon>
        WorkFlow
      </div>
      <div
>>>>>>> Stashed changes
        class="contributor menu-item"
        @click="handleClick('Contributor')"
        :class="{ isActive: isContributorActive }"
      >
<<<<<<< Updated upstream
        <i class="el-icon-info"></i>
=======
        <el-icon><InfoFilled /></el-icon>
>>>>>>> Stashed changes
        Contributor
      </div>
      <div
        class="community menu-item"
        @click="handleClick('Community')"
        :class="{ isActive: isCommunityActive }"
      >
<<<<<<< Updated upstream
        <i class="el-icon-info"></i>
=======
        <el-icon><InfoFilled /></el-icon>
>>>>>>> Stashed changes
        Community
      </div>
      <div
        class="settings menu-item"
        @click="handleClick('Settings')"
        :class="{ isActive: isSettingsActive }"
      >
<<<<<<< Updated upstream
        <i class="el-icon-info"></i>
=======
        <el-icon><InfoFilled /></el-icon>
>>>>>>> Stashed changes
        Settings
      </div>
    </div>
  </div>
</template>

<script>
<<<<<<< Updated upstream
export default {
  components: {},

=======
// import { $emit } from '../../../utils/gogocodeTransfer'
export default {
>>>>>>> Stashed changes
  watch: {},

  computed: {},

  data() {
    return {
      isInfoActive: true,
      isConstructionActive: false,
      isContributorActive: false,
      isSettingsActive: false,
      isCommunityActive: false,
      isConstruction1Active: false,
<<<<<<< Updated upstream
=======
      isWorkFlowActive: false,
>>>>>>> Stashed changes
    };
  },

  methods: {
    handleClick(type) {
      if (type == "Information") {
        this.isInfoActive = true;
<<<<<<< Updated upstream
        this.isConstructionActive = this.isContributorActive = this.isSettingsActive = this.isCommunityActive = this.isConstruction1Active = false;
      } else if (type == "Contributor") {
        this.isContributorActive = true;
        this.isInfoActive = this.isConstructionActive = this.isSettingsActive = this.isCommunityActive = this.isConstruction1Active = false;
      } else if (type == "Community") {
        this.isCommunityActive = true;
        this.isInfoActive = this.isConstructionActive = this.isSettingsActive = this.isContributorActive = this.isConstruction1Active = false;
      } else if (type == "Settings") {
        this.isSettingsActive = true;
        this.isInfoActive = this.isConstructionActive = this.isContributorActive = this.isCommunityActive = this.isConstruction1Active = false;
      } else if (type == "Construction") {
        this.isConstruction1Active = true;
        this.isInfoActive = this.isConstructionActive = this.isContributorActive = this.isSettingsActive = this.isCommunityActive = false;
=======
        this.isConstructionActive =
          this.isContributorActive =
          this.isSettingsActive =
          this.isCommunityActive =
          this.isConstruction1Active =
          this.isWorkFlowActive =
            false;
      } else if (type == "Contributor") {
        this.isContributorActive = true;
        this.isInfoActive =
          this.isConstructionActive =
          this.isSettingsActive =
          this.isCommunityActive =
          this.isConstruction1Active =
          this.isWorkFlowActive =
            false;
      } else if (type == "Community") {
        this.isCommunityActive = true;
        this.isInfoActive =
          this.isConstructionActive =
          this.isSettingsActive =
          this.isContributorActive =
          this.isConstruction1Active =
          this.isWorkFlowActive =
            false;
      } else if (type == "Settings") {
        this.isSettingsActive = true;
        this.isInfoActive =
          this.isConstructionActive =
          this.isContributorActive =
          this.isCommunityActive =
          this.isConstruction1Active =
          this.isWorkFlowActive =
            false;
      } else if (type == "Construction") {
        this.isConstruction1Active = true;
        this.isInfoActive =
          this.isConstructionActive =
          this.isContributorActive =
          this.isSettingsActive =
          this.isCommunityActive =
          this.isWorkFlowActive =
            false;
      } else if (type == "WorkFlow") {
        this.isWorkFlowActive = true;
        this.isInfoActive =
          this.isConstructionActive =
          this.isContributorActive =
          this.isSettingsActive =
          this.isCommunityActive =
          this.isConstruction1Active =
            false;
>>>>>>> Stashed changes
      }
      this.$emit("toRouterType", type);
    },

    init() {
      switch (this.$router.currentRoute.name) {
        case "Information": {
          this.isInfoActive = true;
<<<<<<< Updated upstream
          this.isConstructionActive = this.isContributorActive = this.isSettingsActive = this.isCommunityActive = false;
=======
          this.isConstructionActive =
            this.isContributorActive =
            this.isSettingsActive =
            this.isCommunityActive =
            this.isWorkFlowActive =
              false;
>>>>>>> Stashed changes
          break;
        }
        case "Construction": {
          this.isConstructionActive = true;
<<<<<<< Updated upstream
          this.isInfoActive = this.isContributorActive = this.isSettingsActive = this.isCommunityActive = false;
          break;
        }
        case "Contributor": {
          this.isContributorActive = true;
          this.isInfoActive = this.isConstructionActive = this.isSettingsActive = this.isCommunityActive = false;
          break;
        }
        case "Settings": {
          this.isSettingsActive = true;
          this.isInfoActive = this.isConstructionActive = this.isContributorActive = this.isCommunityActive = false;
          break;
        }
        case "Community": {
          this.isCommunityActive = true;
          this.isInfoActive = this.isReproductionActive = this.isSettingsActive = this.isContributorActive = false;
=======
          this.isInfoActive =
            this.isContributorActive =
            this.isSettingsActive =
            this.isCommunityActive =
            this.isWorkFlowActive =
              false;
          break;
        }
        case "WorkFlow": {
          this.isWorkFlowActive = true;
          this.isInfoActive =
            this.isContributorActive =
            this.isSettingsActive =
            this.isCommunityActive =
            this.isWorkFlowActive =
              false;
          break;
        }
        case "Contributor": {
          this.isContributorActive = true;
          this.isInfoActive =
            this.isConstructionActive =
            this.isSettingsActive =
            this.isCommunityActive =
            this.isConstruction1Active =
              false;
          break;
        }
        case "Settings": {
          this.isSettingsActive = true;
          this.isInfoActive =
            this.isConstructionActive =
            this.isContributorActive =
            this.isCommunityActive =
            this.isWorkFlowActive =
              false;
          break;
        }
        case "Community": {
          this.isCommunityActive = true;
          this.isInfoActive =
            this.isReproductionActive =
            this.isSettingsActive =
            this.isContributorActive =
            this.isWorkFlowActive =
              false;
>>>>>>> Stashed changes
        }
      }
    },
  },
<<<<<<< Updated upstream
=======
  mounted() {
    this.init();
  },
  emits: ["toRouterType"],
};
</script>
>>>>>>> Stashed changes

  mounted() {
    this.init();
  },
};
</script>
<style lang="scss" scoped>
.menu {
  height: 100%;
  //   line-height: 100%;
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
