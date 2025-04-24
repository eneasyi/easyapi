<template>
  <el-breadcrumb separator-class="el-icon-arrow-right">
    <el-breadcrumb-item :to="{ path: '/admin/home' }">首页</el-breadcrumb-item>
    <el-breadcrumb-item>{{ currentRoute }}</el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script>
export default {
  name: "Breadcrumb",
  computed: {
    currentRoute() {
      return this.$route.meta?.name || "";
    },
  },
};
</script>

<style scoped>
.el-breadcrumb {
  line-height: 60px;
}
</style>
