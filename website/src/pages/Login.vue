<template>
  <div id="loginPage">
    <lottie :options="playOptions" :height="200" :width="200"/>

    <h1>pic-bed服务后台</h1>
    <el-form ref="form" :model="form" label-width="100px" class="center">
      <el-form-item label="用户名">
        <el-input v-model="form.userName" placeholder="用户名"/>
      </el-form-item>

      <el-form-item label="密码">
        <el-input v-model="form.password" placeholder="密码" show-password @keyup.enter.native="login"/>
      </el-form-item>

      <el-form-item label="手机号码" v-if="registering">
        <el-input v-model="form.phone" placeholder="手机号码"/>
      </el-form-item>
    </el-form>

    <br>

    <el-row>
      <el-button type="warning" v-on:click="forget">忘记密码</el-button>
      <el-button type="primary" v-on:click="login" :loading="loading">登录</el-button>
    </el-row>

    <br>

    <el-button type="info" v-on:click="register" :loading="sendingRegister">注册</el-button>
  </div>
</template>

<script>
import animationData from '../assets/cube.json'
export default {
  name: "Login",
  data() {
    return {
      form: {
        userName: "",
        password: "",
        phone: ""
      },
      loading: false,
      registering: false,
      sendingRegister: false,
      playOptions: {
        animationData: animationData,
        loop: true,
        autoPlay: true
      }
    };
  },
  methods: {
    forget() {
      alert("请联系工作人员解锁")
    },
    login() {
      this.loading = true
      this.GLOBAL.fly.post(`${this.GLOBAL.domain}/login?userName=${this.form.userName}&password=${this.form.password}`).then((response) => {
        let result = response.data
        localStorage.setItem("userName", this.form.userName)
        localStorage.setItem('userToken', result["token"])
        localStorage.setItem('userId', result["userId"])
        localStorage.setItem('expireDate', result["expireDate"])
        this.$router.push('home')
      }).catch((error) => {
        console.log(error)
        this.loading = false
      })
    },
    register() {
      if(this.registering) {
        this.sendingRegister = true
        if(!this.judge()) {
          alert("输入信息有误")
          this.sendingRegister = false
          return
        }

        this.GLOBAL.fly.post(`${this.GLOBAL.domain}/register?userName=${this.form.userName}&password=${this.form.password}&phone=${this.form.phone}`).then(() => {
          this.login()
        }).catch((error) => {
          console.log(error)
          alert("注册失败")
          this.sendingRegister = false
        })
      } else {
        this.registering = true
      }
    },
    judge() {
      if(this.form.userName.length===0)
        return false
      return this.form.password.length !== 0;
    }
  }
}
</script>

<style>
#loginPage {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  margin-top: 100px;
  text-align: center;
  justify-content: center;
}

.center {
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
}
</style>