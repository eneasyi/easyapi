<template>
  <div>
    <div
      style="
        height: 100vh;
        overflow: hidden;
        display: flex;
        align-items: center;
        justify-content: center;
      "
    >
      <div style="font-size: 40px">
        500 服务器错误 <a @click="goBack">返回</a>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "500",
  data() {
    return {};
  },
  methods: {
    goBack() {
      this.$router.go(-1);
    },
  },
};
</script>

<style scoped>
a {
  color: #409eff;
  cursor: pointer;
}
a:hover {
  text-decoration: underline;
}
</style>
