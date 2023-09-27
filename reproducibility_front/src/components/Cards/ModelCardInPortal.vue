<template>
  <div>
    <el-card class="box-card" v-if="type == 'service'">
      <el-col :span="4">
        <img :src="imgPath(item.snapshot, item.name)" />
      </el-col>
      <el-col :span="20">
        <div class="content">
          <h3 class="title" :title="item.name">{{ item.name }}</h3>
          <p class="desc" :title="item.description">{{ item.description }}</p>
        </div>
        <el-button
          class="config-btn"
          type="success"
          @click="addModelToUser(item.id)"
          icon="el-icon-shopping-cart-2"
        ></el-button>
      </el-col>
    </el-card>

    <el-card
      class="box-card"
      v-else
      :style="select ? 'box-shadow: 0px 0px 20px #666;' : ''"
    >
      <el-col :span="6">
        <img
          :src="'http://geomodeling.njnu.edu.cn' + item.snapshot"
          v-if="item.snapshot != '' && item.snapshot != null"
        />
        <!-- <img src="@/assets/images/model.png" alt="" v-else /> -->
        <img :src="imgPath(item.snapshot, item.name)" v-else />
      </el-col>
      <el-col :span="18">
        <div class="content">
          <h4 class="title" :title="item.name">{{ item.name }}</h4>
          <div class="desc" :title="item.description">
            {{ item.description }}
          </div>
        </div>
        <el-button
          class="config-btn"
          @click="view(item.id)"
          icon="el-icon-shopping-cart-2"
          circle
        ></el-button>
      </el-col>

      <el-button
        type="text primary"
        @click="selectClick"
        :style="select ? 'float: right;color: red;' : 'float: right'"
      >
        <i class="iconfont icon-gouwuche" v-if="select == false"></i>
        <el-badge :value="badgeNum" :max="10" class="item" v-else>
          <i class="iconfont icon-gouwuche"></i>
        </el-badge>
      </el-button>
    </el-card>
    <el-dialog :visible.sync="dialogVisible" width="30%">
      <el-row :gutter="20">
        <el-col
          :span="6"
          v-for="(model, index) in computableModels"
          :key="index"
        >
          <div
            :class="selectNum == index ? 'modelCard select' : 'modelCard'"
            @click="getDescription(index)"
            :style="
              selectNumArr[index] == 1 ? 'background-color: #D0FF00;' : ''
            "
          >
            {{ model.name }}
          </div>
        </el-col>
      </el-row>
      <div class="description">
        {{ description }}
      </div>
      <div class="btn">
        <el-button type="primary" plain size="mini" @click="confirm"
          >confirm</el-button
        >
        <el-button type="primary" plain size="mini" @click="cancel"
          >cancel</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { imgBase64 } from "@/lib/utils";
import { getComputableModels } from "@/api/request";
export default {
  props: ["item", "type"],
  data() {
    return {
      select: false,
      computableModels: [],
      dialogVisible: false,
      description: "",
      selectNum: -1,
      selectNumArr: [],
      selectArr: [],
      badgeNum: 0,
    };
  },
  watch: {
    item: {
      handler(val) {
        let temp = JSON.parse(localStorage.selectModels);
        for (let i = 0; i < temp.length; i++) {
          if (val.oid == temp[i].oid) {
            this.select = true;
            break;
          } else {
            this.select = false;
          }
        }
      },
    },
    "$store.state.user.selectNum": {
      handler(val) {
        if (val == 0) {
          this.select = false;
          this.badgeNum = 0;
        }
      },
    },
  },
  methods: {
    init() {
      if (localStorage.selectModels != undefined) {
        let temp = JSON.parse(localStorage.selectModels);
        for (let i = 0; i < temp.modelItem.length; i++) {
          if (
            this.item.oid == temp.modelItem[i].id &&
            temp.modelItem[i].count > 0
          ) {
            this.select = true;
            this.badgeNum = temp.modelItem[i].count;
            break;
          }
        }
      }
    },
    imgPath(snapshot, name) {
      if (snapshot != undefined) {
        return snapshot;
      } else {
        return imgBase64(name);
      }
    },
  },
  mounted() {
    this.init();
    // console.log(this.item)
    // console.log(this.item.snapshot)
  },
};
</script>

<style lang="scss" scoped>
.box-card {
  margin: 10px 2%;
  width: 95%;
  transition: box-shadow 0.1s ease;

  height: 120px;
  /* 相对定位 */
  position: relative;
  /deep/.el-card__body {
    padding: 10px;
  }
  .content {
    padding: 5px;
    word-wrap: break-word;
    width: 86%;
    min-height: 40px;
    max-height: 40px;
    .title,
    .desc {
      max-height: 40px;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      overflow: hidden;
      font-size: 8px;
    }
  }

  // &:hover {
  //   box-shadow: 0px 0px 20px #666;
  // }
  img {
    width: 100%;
    object-fit: cover;
    height: 100px;
  }
  .config-btn {
    margin-bottom: 0;
    height: 40px;
    /* 绝对定位 */
    position: absolute;
    bottom: 10px;
    right: 10px;
    // position: relative; //将button按钮固定在页面底部，注意，：和；是英文的哦，一定不要写成中文哦。
    // bottom: 5px;
    // float: ;
    // margin: 0px 0px;
    // padding: 10px;
  }
}
.modelCard {
  width: 100%;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 6px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  border: solid 1px;
  // max-height: 50px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;
  font-size: 8px;
  padding: 0px 5px;
}
.modelCard:hover {
  cursor: pointer;
}
.description {
  margin-top: 20px;
}
.select {
  border: solid 1px red;
}
.btn {
  text-align: center;
  margin-top: 15px;
}
</style>
