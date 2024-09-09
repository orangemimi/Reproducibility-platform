<template>
  <div>
    <div class="login-box">
      <div class="radio-group">
        <div
          class="radio-btn"
          :class="{ active: tab == 'login' }"
          @click="tab = 'login'"
        >
          Login
        </div>
        <div
          class="radio-btn"
          :class="{ active: tab == 'register' }"
          @click="tab = 'register'"
        >
          Register
        </div>
      </div>
      <el-input
        placeholder="Name"
        v-model="formItem.name"
        v-show="tab == 'register'"
      >
      </el-input>
      <el-input placeholder="E-mail" v-model="formItem.email"> </el-input>
      <el-input
        placeholder="请输入密码"
        :type="tab == 'login' ? 'password' : 'text'"
        v-model="formItem.password"
        style="margin-top: 10px"
      >
      </el-input>
    </div>

    <div class="btn-wrapper">
      <span class="btn" @click="handleClick">Enter to the system </span>
    </div>
  </div>
</template>

<script>
import { post } from '@/axios'
import { mapActions } from 'vuex'
import md5 from 'js-md5'
export default {
  data() {
    return {
      tab: 'login',
      formItem: {
        name: '',
        email: '',
        password: '',
      },
    }
  },
  methods: {
    ...mapActions(['handleLogOut', 'handleLogIn']),
    async handleClick() {
      try {

        await this.handleLogOut()

        if (this.tab == 'register') {
          let form = new FormData()
          form.append('email', this.formItem.email)
          form.append('name', this.formItem.name)
          form.append('password', md5(this.formItem.password))
          await post(`/users/${this.tab}`, form)
        } else {
          
          await this.$store.dispatch('user/handleLogIn', this.formItem)
        }

        let redirect = decodeURIComponent(this.$route.query.redirect || '/')
        if (redirect != undefined) {
          this.$router.push({
            path: redirect,
          })
        } else {
          this.$router.push({ name: 'home' })
        }
      } catch (error) {
        this.$message({
          type:'error',
          message:'Account or password error'
        })
        // this.$throw(error)
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.login-box {
  background: #ffffff;
  width: 240px;
  margin: 20px auto 40px;
  padding: 0 40px 36px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.06);
  .radio-group {
    text-align: center;
    .radio-btn {
      display: inline-block;
      padding: 10px 20px;
      margin: 10px 10px;
      color: #999999;
      &.active {
        color: #212121;
        border-bottom: 2px solid #212121;
      }
    }
  }
}
.btn-wrapper {
  margin-top: 40px;
  text-align: center;
  a {
    text-decoration: none;
    color: #ffffff;
  }
  .btn {
    padding: 12px 32px;
    background: rgba(0, 0, 0, 0.5);
    color: #ffffff;
    transition: background 0.3s ease;
    font-size: 14px;
    border-radius: 50px;
    &:hover {
      background: #409eff;
      cursor: pointer;
    }
  }
}
</style>
