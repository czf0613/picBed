import Vue from 'vue';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import Login from './pages/Login.vue';
import globalVariables from './Globals.js';
import VueRouter from "vue-router";
import Home from "@/pages/Home";
import App from "@/App";
Vue.prototype.GLOBAL = globalVariables

Vue.use(ElementUI)
Vue.use(VueRouter)

const router = new VueRouter({
  mode: 'history',
  routes: [{
    path: '/login',
    component: Login
  }, {
    path: '/home',
    component: Home
  }, {
    path: '*',
    component: Home
  }]
})

router.beforeEach((to, from, next) => {
  if(to.path === '/login') {
    if(checkToken())
      next("/home")
    else
      next()
  } else {
    if(checkToken())
      next()
    else
      next("/login")
  }
})

function checkToken() {
  if(localStorage.getItem('userToken') == null)
    return false

  if(parseInt(localStorage.getItem('expireDate')) <= Date.now())
    return false

  if(parseInt(localStorage.getItem('expireDate')) >= Date.now() + 2*24*3600*1000)
    return true

  let tempFly = require("flyio")
  tempFly.post('https://pic-bed.xyz:2053/refreshLogin', null, {headers: {"token": localStorage.getItem('userToken')}}).then((response) => {
    let result = JSON.parse(response.data)
    localStorage.setItem('userToken', result["token"])
    localStorage.setItem('userId', result["userId"])
    localStorage.setItem('expireDate', result["expireDate"])
    return true
  }).catch((error) => {
    console.log(error)
    return false
  })
}

new Vue({
  render: h => h(App),
  router
}).$mount('#app');