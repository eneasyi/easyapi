<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style>
html,
body,
#app {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}
</style>

<script>
import { useUserStore } from "@/stores/user";

export default {
  name: "App",
  mounted() {
    this.doInit();
  },
  methods: {
    doInit() {
      // 初始化用户状态
      const userStore = useUserStore();
      userStore.fetchUserInfo().catch((err) => {
        console.error("初始化用户状态失败:", err);
      });

      console.log("*********************");
      console.log("*                   *");
      console.log("*   欢迎来到我的项目   *");
      console.log("*                   *");
      console.log("*********************");
    },
  },
};
</script>
