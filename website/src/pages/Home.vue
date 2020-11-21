<template>
  <div>
    <h1>pic-bed 管理后台</h1>

    <el-divider/>

    <el-container>
      <el-aside>
        <el-menu class="el-menu-vertical-demo" :default-active=String(1) @select="handleSelect">
          <el-menu-item index="1">所有图片</el-menu-item>

          <el-menu-item index="2">Token管理</el-menu-item>

          <el-submenu index="3">
            <template slot="title">关于我</template>
            <el-menu-item index="3-1">个人信息</el-menu-item>
            <el-menu-item index="3-2" disabled>付费套餐</el-menu-item>
            <el-menu-item index="3-3">退出登录</el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>

      <el-main>
        <PhotoGrid v-if="page === 1"/>

        <Token v-if="page === 2"/>

        <SelfInfo v-if="page === 3"/>

        <Pay v-if="page === 4"/>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import PhotoGrid from "@/components/allPhotos/PhotoGrid";
import SelfInfo from "@/components/me/SelfInfo";
import Token from "@/components/token/Token";
import Pay from "@/components/pay/Pay";
export default {
  name: "Home",
  components: {Pay, Token, SelfInfo, PhotoGrid},
  data() {
    return {
      page: 1
    }
  },
  methods: {
    handleSelect(key) {
      switch (key) {
        case "1":
          this.page = 1
              break
        case "2":
          this.page = 2
              break
        case "3":
        case "3-1":
          this.page = 3
              break
        case "3-2":
          this.page = 4
              break
        case "3-3":
          this.GLOBAL.fly.post(`${this.GLOBAL.domain}/logOut`, null, {headers: {token: localStorage.getItem("userToken")}}).then(() => {
            localStorage.clear()
            this.$router.replace("login")
          }).catch((error) => {
            console.log(error)
          })
              break
      }
    }
  },
  created() {
    this.GLOBAL.fly.get(`${this.GLOBAL.domain}/self/${localStorage.getItem("userId")}`, null, {headers: {token: localStorage.getItem("userToken")}}).then((response) => {
      let result = response.data
      localStorage.setItem("phone", result["phone"])
      localStorage.setItem("serviceDate", result["expireDate"])
    }).catch((error) => {
      localStorage.clear()
      console.log(error)
    })
  }
}
</script>

<style scoped>

</style>