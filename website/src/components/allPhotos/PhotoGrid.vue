<template>
  <div>
    <h1 v-if="items.length === 0">数据正在加载中……</h1>

    <ul v-else>
      <li v-for="item in items" :key="item">
        <PhotoElement :url="item" :file-name=getFileNameFromURL(item)></PhotoElement>
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
      items: []
    }
  },
  mounted() {
    this.GLOBAL.fly.get(`${this.GLOBAL.domain}/files/${localStorage.getItem("userId")}/all`, null, {headers: {token: localStorage.getItem("userToken")}}).then((response) => {
      this.items = JSON.parse(response.data)
    }).catch((error) => {
      console.log(error)
    })
  },
  methods: {
    getFileNameFromURL(url) {
      let i = 0
      for(i = url.length - 1; i >= 0; --i) {
        if(url[i] === '/')
          break
      }
      return url.substring(i+1)
    }
  }
}
</script>

<style scoped>
.fill {
  display: flex;
  justify-content: center;
  flex-direction: row;
  align-items: center
}
</style>