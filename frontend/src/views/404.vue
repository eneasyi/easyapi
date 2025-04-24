<template>
  <div>
    <div style="height: 100vh; overflow: hidden; display: flex; align-items: center; justify-content: center">
      <div style="font-size: 40px">404 找不到页面 <a @click="goBack">返回</a></div>
    </div>

  </div>
</template>

<script>
export default {
  name: "404",
  data() {
    return {}
  },
  created() {

  },
  methods: {
    goBack() {  
      this.$router.go(-1);  
    } 
  }
}
</script>

<style scoped>

</style>