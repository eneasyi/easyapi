<template>
  <div>
    <div style="height: calc(100vh - 80px); overflow: hidden; display: flex; align-items: center; justify-content: center">
      <div style="font-size: 40px">无权访问<a @click="goBack">返回</a></div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Auth",
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