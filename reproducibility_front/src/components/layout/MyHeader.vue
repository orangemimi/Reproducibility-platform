<template>
  <div class="app-head">
    <el-row :gutter="20">
      <el-col :span="8" class="logo">
        <div @click="handleSelect('1')">OpenGMS Reproducibility</div>
      </el-col>
      <el-col class="nav-bar" :span="8" :offset="2">
        <el-menu
          default-active="activeIndex"
          mode="horizontal"
          @select="handleSelect"
        >
          <el-menu-item index="1">Home</el-menu-item>
          <el-menu-item index="2">Projects</el-menu-item>
          <el-menu-item index="3">Models </el-menu-item>
          <el-menu-item index="4">Data</el-menu-item>
        </el-menu>
      </el-col>

      <el-col class="user" :span="2" :offset="1">
        <el-dropdown
          placement="bottom-start"
          v-if="user.name"
          @command="handleCommond"
        >
          <!-- <avatar
            :username="user.name"
            :size="40"
            style="margin-top: 10px"
            :title="user.name"
          ></avatar> -->
          <!-- 头像默认图片：https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png -->
          <el-avatar
            :username="user.name"
            :size="50"
            style="margin-top: 5px;background-color: rgb(137, 139, 131)"
            :title="user.name"
          >
          {{ user.name}}
          </el-avatar>
          <template v-slot:dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">logout</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-col>
      <!-- {{ user }} -->
    </el-row>
  </div>
</template>

<script>
// import Avatar from 'vue-avatar'
import { mapState, mapActions } from "vuex";
export default {
  data() {
    return {
      activeIndex: 1,
    };
  },
  computed: {
    ...mapState(["user"]),
  },
  methods: {
    ...mapActions(["handleLogOut"]),
    handleSelect(key) {
      switch (key) {
        case "1":
          {
            this.$router.push({ name: "home" });
          }
          break;
        case "2":
          {
            this.$router.push({ name: "Projects" });
          }
          break;
        case "3":
          {
            this.$router.push({ name: "Models" });
          }
          break;
        case "4":
          {
            this.$router.push({ name: "Data" });
          }
          break;

        case "5":
          {
            //TOD
          }
          break;
      }
    },
    async handleCommond(command) {
      if (command == "logout") {
        await this.handleLogOut();
        location.reload();
      }
    },
  },
  components: {
    // Avatar,
  },
};
</script>

<style scoped>
.el-menu--horizontal {
  border: none;
}
.logo div {
  font: bold 20px arial;
  text-decoration: none;
  line-height: 60px;
  color: #409eff;
  cursor: pointer;
}
.app-head {
  background-color: white;
  box-shadow: 0px 1px 12px #d1d1d1;
  text-align: center;
  padding: 0px 40px;
}
.app-head .nav-bar {
  margin: 0px 20px;
  font-size: 16px;
  color: white;
}
</style>
