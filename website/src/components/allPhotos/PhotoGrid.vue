<template>
  <div class="center">
    <el-input v-model="fileToken" placeholder = "请指定一个上传token"></el-input>
    <br/>

    <el-upload
        drag
        :action=this.getUploadURL()
        :headers="{token: fileToken}"
        multiple
        :on-success="afterUpload">
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <div class="el-upload__tip" slot="tip">只能上传图片文件</div>
    </el-upload>

    <el-divider/>

    <h1 v-if="items.length === 0">您还没有上传过图片</h1>

    <ul v-else>
      <li v-for="item in items" :key="item">
        <PhotoElement :url="item" :file-name=getFileNameFromURL(item)></PhotoElement>
      </li>
    </ul>
  </div>
</template>

<script>
import PhotoElement from "@/components/allPhotos/PhotoElement";
import {Loading} from 'element-ui'

export default {
  name: "PhotoGrid",
  components: {
    PhotoElement
  },
  data() {
    return {
      items: [],
      load: Loading.service({fullscreen: true}),
      fileToken: localStorage.getItem("fileToken")
    }
  },
  mounted() {
    this.fetch()
  },
  methods: {
    fetch() {
      this.GLOBAL.fly.get(`${this.GLOBAL.domain}/files/${localStorage.getItem("userId")}/all`, null, {headers: {token: localStorage.getItem("userToken")}}).then((response) => {
        this.items = JSON.parse(response.data)
        this.load.close()
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