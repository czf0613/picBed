<template>
  <div class="center">
    <el-image
        fit="contain"
        lazy :src="url" style="max-width: 300px; max-height: 300px" alt="图片">
      <img src="../../assets/loading.gif" slot="placeholder" alt="加载中">
      <img src="../../assets/error.png" slot="error" alt="加载失败">
    </el-image>

    <el-divider/>

    <el-row class="align">
      <el-col :span="10"><p>文件名：{{fileName}}</p></el-col>

      <el-divider direction="vertical"/>

      <a :href=grepURL() :download="fileName">
        <el-button type="primary">下载图片</el-button>
      </a>

      <el-divider direction="vertical"/>
      <el-button type="danger" :loading="deleting" v-on:click="deletePic">删除图片</el-button>
    </el-row>

    <br>

    <p>图片URL：{{url}}</p>
  </div>
</template>

<script>
export default {
  name: "PhotoElement",
  props: {
    url: String,
    fileName: String,
    deleteCallBack: Function
  },
  data() {
    return {
      deleting: false
    }
  },
  methods: {
    deletePic() {
      this.deleting = true
      this.GLOBAL.fly.delete(`${this.GLOBAL.domain}/deleteFile/${this.grepFileId()}`, null, {headers: {token: localStorage.getItem("userToken")}}).then(() => {
        this.deleting = false
        alert("删除成功！")
        this.deleteCallBack()
      }).catch(() => {
        alert("删除失败！")
        this.deleting = false
      })
    },
    grepURL() {
      return this.url.substring(19)
    },
    grepFileId() {
      let index = 0;

      for (let i = 0; i < this.fileName.length; i++) {
        if(this.fileName[i] === '.') {
          index = i
          break
        }
      }

      return this.fileName.substring(0, index)
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
  margin-top: 100px;
  text-align: center;
  justify-content: center;
  flex-direction: column;
}
.align {
  display: flex;
  justify-content: center;
  flex-direction: row;
  align-items: center
}
</style>