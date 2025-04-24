<template>
  <el-container class="manager-container">
    <el-aside width="200px" class="aside">
      <div class="logo">
        <img src="@/assets/imgs/logo.png" alt="Logo" />
        <span>管理后台</span>
      </div>
      <el-menu
        :default-active="$route.path"
        class="menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <template v-for="route in routes">
          <el-menu-item
            v-if="
              route.meta &&
              route.meta.isMenu &&
              checkAccess(userStore.userInfo, route.meta.access)
            "
            :key="route.path"
            :index="`/admin/${route.path}`"
          >
            <i :class="getIcon(route.name)"></i>
            <span>{{ route.meta.name }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <i class="el-icon-menu toggle-btn" @click="toggleCollapse"></i>
          <breadcrumb />
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              <img
                :src="
                  userStore.userInfo.userAvatar ||
                  require('@/assets/imgs/default-avatar.jpeg')
                "
                class="avatar"
              />
              <span>{{ userStore.userInfo.userName }}</span>
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人信息</el-dropdown-item>
              <el-dropdown-item divided command="logout"
                >退出登录</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { useUserStore } from "@/stores/user";
import checkAccess from "@/access/checkAccess";
import Breadcrumb from "@/components/Breadcrumb.vue";
import adminRouter from "@/router/adminRouter";

export default {
  name: "Manager",
  components: {
    Breadcrumb,
  },
  data() {
    return {
      routes: adminRouter,
      isCollapse: false,
    };
  },
  computed: {
    userStore() {
      return useUserStore();
    },
  },
  methods: {
    checkAccess,
    getIcon(routeName) {
      const iconMap = {
        Home: "el-icon-s-home",
        User: "el-icon-user",
        AdminPerson: "el-icon-s-custom",
        InterfaceInfo: "el-icon-s-grid",
        InterfaceCategory: "el-icon-folder",
      };
      return iconMap[routeName] || "el-icon-menu";
    },
    toggleCollapse() {
      this.isCollapse = !this.isCollapse;
    },
    async handleCommand(command) {
      switch (command) {
        case "profile":
          this.$router.push("/admin/adminPerson");
          break;
        case "logout":
          try {
            await this.userStore.logout();
            this.$message.success("退出成功");
            this.$router.push("/login");
          } catch (error) {
            this.$message.error("退出失败");
          }
          break;
      }
    },
  },
};
</script>

<style scoped>
.manager-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  color: #fff;
  font-size: 20px;
  background-color: #2b2f3a;
}

.logo img {
  width: 32px;
  margin-right: 12px;
}

.menu {
  border: none;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.toggle-btn {
  font-size: 20px;
  margin-right: 20px;
  cursor: pointer;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 8px;
}

.main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
