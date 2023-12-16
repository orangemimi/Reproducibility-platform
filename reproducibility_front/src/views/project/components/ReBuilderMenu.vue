<!-- reBUilder Menu -->
<template>
  <div style="height: 100%">
    <!-- <el-menu :default-active="1" class="el-menu-demo" mode="horizontal" @select="handleClick">
      <el-menu-item index="1">处理中心</el-menu-item>
      <el-menu-item index="3">消息中心</el-menu-item>
    </el-menu> -->
    <div class="menu">
<<<<<<< Updated upstream
      <div class="info menu-item" @click="handleClick('Information')" :class="{ isActive: isInfoActive }">
        <i class="el-icon-info"></i>
        Information
      </div>
      <div class="scenario menu-item" @click="handleClick('Reproduction')" :class="{ isActive: isReproductionActive }">
        <i class="el-icon-info"></i>
        Reproduction
      </div>
      <div class="contributor menu-item" @click="handleClick('Contributor')" :class="{ isActive: isContributorActive }">
        <i class="el-icon-info"></i>
        Contributor
      </div>
      <div class="community menu-item" @click="handleClick('Community')" :class="{ isActive: isCommunityActive }">
        <i class="el-icon-info"></i>
=======
      <div
        class="info menu-item"
        @click="handleClick('R_Info')"
        :class="{ isActive: isInfoActive }"
      >
        <el-icon><InfoFilled /></el-icon>
        Information
      </div>

      <div
        class="scenario menu-item"
        @click="handleClick('R_Reproduction')"
        :class="{ isActive: isReproductionActive }"
      >
        <el-icon><InfoFilled /></el-icon>
        Reproduction
      </div>

      <div
        class="contributor menu-item"
        @click="handleClick('Contributor')"
        :class="{ isActive: isContributorActive }"
      >
        <el-icon><InfoFilled /></el-icon>
        Contributor
      </div>
      <div
        class="community menu-item"
        @click="handleClick('Community')"
        :class="{ isActive: isCommunityActive }"
      >
        <el-icon><InfoFilled /></el-icon>
>>>>>>> Stashed changes
        Community
      </div>
    </div>
  </div>
</template>

<!-- <script setup>

</script> -->
<script>
export default {
<<<<<<< Updated upstream
  components: {},

  watch: {},

  computed: {},

=======
>>>>>>> Stashed changes
  data() {
    return {
      isInfoActive: true,
      isReproductionActive: false,
      isContributorActive: false,
      isSettingsActive: false,
<<<<<<< Updated upstream
      isCommunityActive: false
=======
      isCommunityActive: false,
>>>>>>> Stashed changes
    };
  },

  methods: {
    handleClick(type) {
<<<<<<< Updated upstream
      if (type == 'Information') {
        this.isInfoActive = true;
        this.isReproductionActive = this.isContributorActive = this.isSettingsActive = this.isCommunityActive = false;
      } else if (type == 'Reproduction') {
        this.isReproductionActive = true;
        this.isInfoActive = this.isContributorActive = this.isSettingsActive = this.isCommunityActive = false;
      } else if (type == 'Contributor') {
        this.isContributorActive = true;
        this.isInfoActive = this.isReproductionActive = this.isSettingsActive = this.isCommunityActive = false;
      } else if (type == 'Community') {
        this.isCommunityActive = true;
        this.isInfoActive = this.isReproductionActive = this.isSettingsActive = this.isContributorActive = false;
      }
      this.$emit('toRouterType', type);
    },

    init() {
      switch (this.$router.currentRoute.name) {
        case 'Information': {
          this.isInfoActive = true;
          this.isReproductionActive = this.isContributorActive = this.isSettingsActive = false;
          break;
        }
        case 'Reproduction': {
          this.isReproductionActive = true;
          this.isInfoActive = this.isContributorActive = this.isSettingsActive = false;
          break;
        }
        case 'Contributor': {
          this.isContributorActive = true;
          this.isInfoActive = this.isReproductionActive = this.isSettingsActive = false;
          break;
        }
        case 'Community': {
          this.isCommunityActive = true;
          this.isInfoActive = this.isReproductionActive = this.isSettingsActive = this.isContributorActive = false;
=======
      if (type == "R_Info") {
        // type = "R_Reproduction";
        this.isInfoActive = true;
        this.isReproductionActive =
          this.isContributorActive =
          this.isSettingsActive =
          this.isCommunityActive =
            false;
      } else if (type == "R_Reproduction") {
        this.isReproductionActive = true;
        this.isInfoActive =
          this.isContributorActive =
          this.isSettingsActive =
          this.isCommunityActive =
            false;
      } else if (type == "Contributor") {
        this.isContributorActive = true;
        this.isInfoActive =
          this.isReproductionActive =
          this.isSettingsActive =
          this.isCommunityActive =
            false;
      } else if (type == "Community") {
        this.isCommunityActive = true;
        this.isInfoActive =
          this.isReproductionActive =
          this.isSettingsActive =
          this.isContributorActive =
            false;
      }
      this.$emit("toRouterType", type);
    },

    init() {
      // console.log(this.$router.currentRoute.value, "value");
      // // debugger;
      switch (this.$router.currentRoute.value.name) {
        case "R_Info": {
          // debugger;
          this.isInfoActive = true;
          this.isReproductionActive =
            this.isContributorActive =
            this.isSettingsActive =
              false;
          break;
        }
        case "R_Reproduction": {
          this.isReproductionActive = true;
          this.isInfoActive =
            this.isContributorActive =
            this.isSettingsActive =
              false;
          break;
        }
        case "Contributor": {
          this.isContributorActive = true;
          this.isInfoActive =
            this.isReproductionActive =
            this.isSettingsActive =
              false;
          break;
        }
        case "Community": {
          this.isCommunityActive = true;
          this.isInfoActive =
            this.isReproductionActive =
            this.isSettingsActive =
            this.isContributorActive =
              false;
>>>>>>> Stashed changes
        }
      }
    }
  },
<<<<<<< Updated upstream
=======
  mounted() {
    this.isInfoActive = true;
    this.init();
  },
  emits: ["toRouterType"],
};
</script>
>>>>>>> Stashed changes

  mounted() {
    this.init();
  }
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
