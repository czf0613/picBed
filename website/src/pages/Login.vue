<template>
  <div id="loginPage">
    <img
        style="width: 200px; height: 200px"
        loading="lazy"
        src="../assets/picture.png"
        alt="icon"/>

    <h1>pic-bed服务后台</h1>

    <el-row class="center">
      <el-col :span="2"><p>用户名</p></el-col>

      <el-col :span="4">
        <el-input v-model="userName" placeholder="用户名"/>
      </el-col>
    </el-row>

    <el-row class="center">
      <el-col :span="2"><p>密码</p></el-col>

      <el-col :span="4">
        <el-input v-model="password" placeholder="密码" show-password/>
      </el-col>
    </el-row>

    <el-row v-if="registering" class="center">
      <el-col :span="2"><p>手机号码</p></el-col>

      <el-col :span="4">
        <el-input v-model="phone" placeholder="手机号码"/>
      </el-col>
    </el-row>

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
export default {
  name: "Login",
  data() {
    return {
      userName: "",
      password: "",
      loading: false,
      phone: "",
      registering: false,
      sendingRegister: false
    };
  },
  methods: {
    forget() {
      alert("请联系工作人员解锁")
    },
    login() {
      this.loading = true
      this.GLOBAL.fly.post(`${this.GLOBAL.domain}/login?userName=${this.userName}&password=${this.password}`).then((response) => {
        let result = response.data
        localStorage.setItem("userName", this.userName)
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
        this.GLOBAL.fly.post(`${this.GLOBAL.domain}/register?userName=${this.userName}&password=${this.password}&phone=${this.phone}`).then(() => {
          this.login()
        }).catch((error) => {
          console.log(error)
          alert("注册失败")
          this.sendingRegister = false
        })
      } else {
        this.registering = true
      }
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
  flex-direction: row;
  align-items: center
}
</style>