<template>
  <div class="center">
    <el-input v-model="fileToken" placeholder = "请指定一个上传token"></el-input>
    <br/>

    <el-upload
        drag
        :action=this.getUploadURL()
        :headers="{token: fileToken}"
        multiple
        :on-success="afterUpload"
        :on-error="errorHandler">
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <div class="el-upload__tip" slot="tip">只能上传图片文件</div>
    </el-upload>

    <el-divider/>

    <h1 v-if="items.length === 0">您还没有上传过图片</h1>

    <ul v-else>
      <li v-for="item in items" :key="item" :loading="loading">
        <PhotoElement :url="item" :file-name=getFileNameFromURL(item) :delete-call-back=fetch></PhotoElement>
      </li>
    </ul>
  </div>
</template>

<script>
import PhotoElement from "@/components/allPhotos/PhotoElement";

export default {
  name: "PhotoGrid",
  components: {
    PhotoElement
  },
  data() {
    return {
      items: [],
      loading: true,
      fileToken: localStorage.getItem("fileToken")
    }
  },
  mounted() {
    this.fetch()
  },
  methods: {
    fetch() {
      this.loading=true

      this.GLOBAL.fly.get(`${this.GLOBAL.domain}/files/${localStorage.getItem("userId")}/all`, null, {headers: {token: localStorage.getItem("userToken")}}).then((response) => {
        this.items = response.data
        this.loading=false
      }).catch((error) => {
        console.log(error)
      })
    },
    getFileNameFromURL(url) {
      let i = 0
      for(i = url.length - 1; i >= 0; --i) {
        if(url[i] === '/')
          break
      }
      return url.substring(i+1)
    },
    getUploadURL() {
      return `${this.GLOBAL.domain}/upload?userId=${localStorage.getItem("userId")}`
    },
    afterUpload() {
      this.fetch()
    },
    errorHandler() {
      alert("上传失败")
    }
  },
  watch: {
    fileToken: function () {
      localStorage.setItem("fileToken", this.fileToken)
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