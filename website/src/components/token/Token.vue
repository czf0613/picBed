<template>
  <div class="center" style="height: 600px">
    <el-row>
      <el-button type="primary" v-on:click="newToken">新增token</el-button>

      <el-divider direction="vertical"/>

      <el-button v-on:click="open">查看请求接口格式</el-button>
    </el-row>

    <el-divider/>

    <el-table
        v-if="tokens.length !== 0 || loading"
        v-loading="loading"
        element-loading-text="拼命加载中"
        element-loading-spinner="el-icon-loading"
        :show-overflow-tooltip="true"
        :data="tokens" border height="150" style="width: 100%">
      <el-table-column
        prop="owner"
        label="用户ID"
        width="100px"></el-table-column>

      <el-table-column
        prop="value"
        label="token"
        width="350px"></el-table-column>

      <el-table-column
        label="创建时间"
        width="200px">
        <template slot-scope="scope">
          <p>{{toTimeString(scope.row.generateDate)}}</p>
        </template>
      </el-table-column>

      <el-table-column
          label="最近一次使用"
          width="200px">
        <template slot-scope="scope">
          <p>{{toTimeString(scope.row.recentUsage)}}</p>
        </template>
      </el-table-column>

      <el-table-column
          prop="label"
          label="备注"></el-table-column>

      <el-table-column
          label="操作"
          width="100px">
        <template slot-scope="scope">
          <el-button @click="deleteToken(scope.row.id)" type="text" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  name: "Token",
  data() {
    return {
      tokens: [],
      loading: true
    }
  },
  mounted() {
    this.fetch()
  },
  methods: {
    fetch() {
      this.GLOBAL.fly.get(`${this.GLOBAL.domain}/token/${localStorage.getItem("userId")}/all`, null, {headers: {token: localStorage.getItem("userToken")}}).then((response) => {
        this.tokens = JSON.parse(response.data)
        this.loading = false
      }).catch((error) => {
        console.log(error)
      })
    },
    open() {
      window.open('https://github.com/czf0613/picBed')
    },
    toTimeString(timeStamp) {
      const time = new Date(timeStamp);
      const Y = time.getFullYear()
      const M = (time.getMonth() + 1).toString().padStart(2, '0')
      const D = time.getDate().toString().padStart(2, '0')
      const h = time.getHours().toString().padStart(2, '0')
      const m = time.getMinutes().toString().padStart(2, '0')
      const s = time.getSeconds().toString().padStart(2, '0')
      return `${Y}-${M}-${D} ${h}:${m}:${s}`
    },
    newToken() {
      this.$prompt('请输入token备注', '备注', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
      }).then(({ value }) => {
        this.GLOBAL.fly.post(`${this.GLOBAL.domain}/token/${localStorage.getItem("userId")}/generate?label=${value}`, null, {headers: {token: localStorage.getItem("userToken")}}).then((response) => {
          let result = JSON.parse(response.data)
          this.tokens.push(result)
        }).catch((error) => {
          console.log(error)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消输入'
        })
      })
    },
    deleteToken(id) {
      this.GLOBAL.fly.delete(`${this.GLOBAL.domain}/token/delete/${id}`, null, {headers: {token: localStorage.getItem("userToken")}}).then(() => {
        this.tokens = this.tokens.filter((item) => {
          return item.id != id
        })
      }).catch((error) => {
        console.log(error)
      })
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
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center
}
</style>