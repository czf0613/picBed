<template>
  <div class="center">
    <el-avatar :src="getIconURL()" fit="fill" size="200" @error="() => { return false}">
      <img src="../../assets/error.png" slot="default" alt="加载失败">
    </el-avatar>
    <br/>

    <p>用户名：{{getUserName()}}</p>

    <p>用户ID：{{getUserID()}}</p>

    <el-form label-width="120px" size="1">
      <el-form-item label="手机号码">
        <el-input v-model="phone" placeholder="手机号码"/>
      </el-form-item>
    </el-form>

    <br/>

    <el-button type="primary" v-on:click="modifySelf">提交更改</el-button>

    <el-divider/>

    <el-button type="danger" v-on:click="changePassword">修改密码</el-button>

    <br/>

    <el-form label-width="120px" size="2" v-if="change">
      <el-form-item label="旧密码">
        <el-input v-model="old" show-password placeholder="请输入旧密码"/>
      </el-form-item>

      <el-form-item label="新密码">
        <el-input v-model="first" show-password placeholder="请输入新密码"/>
      </el-form-item>

      <el-form-item label="再次输入新密码">
        <el-input v-model="second" show-password placeholder="请再次输入新密码"/>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  name: "SelfInfo",
  methods: {
    getUserName() {
      return localStorage.getItem("userName")
    },
    getUserID() {
      return localStorage.getItem("userId")
    },
    getIconURL() {
      return `${this.GLOBAL.domain}/icon/${localStorage.getItem("userId")}`
    },
    modifySelf() {
      let obj = {
        expireDate: localStorage.getItem("serviceDate"),
        userId: localStorage.getItem("userId"),
        userName: localStorage.getItem("userName"),
        phone: this.phone
      }

      this.GLOBAL.fly.post(`${this.GLOBAL.domain}/modifySelfInformation?information=${JSON.stringify(obj)}`, null, {headers: {token: localStorage.getItem("userToken")}}).then(() => {
        localStorage.setItem("phone", this.phone)
      }).catch((error) => {
        console.log(error)
      })
    },
    changePassword() {
      if(!this.change) {
        this.change = true
      } else {
        if(this.first !== this.second) {
          alert("两次密码不一致")
          return
        }
        this.GLOBAL.fly.post(`${this.GLOBAL.domain}/modifyPassword?userName=${localStorage.getItem("userName")}&oldPassword=${this.old}&newPassword=${this.first}`, null, {headers: {token: localStorage.getItem("userToken")}}).then(() => {
          localStorage.clear()
          this.$router.replace("login")
        }).catch((error) => {
          console.log(error)
        })
      }
    }
  },
  data() {
    return {
      phone: localStorage.getItem("phone"),
      change: false,
      old: '',
      first: '',
      second: ''
    }
  }
}
</script>

<style scoped>
.center {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  margin-top: 50px;
  display: flex;
  flex-direction: column;
}
</style>