import Vue from 'vue';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import Login from './pages/Login.vue';
import globalVariables from './Globals.js';
import VueRouter from "vue-router";
import Home from "@/pages/Home";
import App from "@/App";
import lottie from 'vue-lottie';

Vue.prototype.GLOBAL = globalVariables

Vue.use(ElementUI)
Vue.use(VueRouter)
Vue.component('lottie', lottie)

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

  let local = new Date(localStorage.getItem('expireDate'))
  let now = new Date()

  let twoDaysLater = new Date()
  twoDaysLater.setDate(now.getDate() + 2)

  if(local < now)
    return false

  if(local >= twoDaysLater)
    return true

  let tempFly = require("flyio")
  tempFly.post('https://pic-bed.xyz/api/refreshLogin', null, {headers: {"token": localStorage.getItem('userToken')}}).then((response) => {
    let result = response.data
    localStorage.setItem('userToken', result["token"])
    localStorage.setItem('userId', result["userId"])
    localStorage.setItem('expireDate', result["expireDate"])
    return true
  }).catch((error) => {
    console.log(error)
    localStorage.clear()
    return false
  })
}

new Vue({
  render: h => h(App),
  router
}).$mount('#app');