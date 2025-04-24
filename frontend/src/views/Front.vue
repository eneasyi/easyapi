<template>
  <div class="front-container">
    <login-register :visible.sync="loginVisible" />
    <!-- 顶部通知栏 -->
    <div class="announcement-bar" v-if="notice">
      <el-alert
        :title="notice"
        type="info"
        :closable="false"
        show-icon
        center
      />
    </div>

    <!-- 头部导航 -->
    <div class="header">
      <div class="header-content">
        <!-- Logo区域 -->
        <div class="logo-area">
          <img src="@/assets/imgs/logo.png" alt="Logo" class="logo" />
          <h1 class="site-title">通用平台</h1>
        </div>

        <!-- 导航菜单 -->
        <div class="nav-menu">
          <div
            v-for="item in menus"
            :key="item.path"
            class="nav-item"
            :class="{ 'nav-item-active': $route.path === item.path }"
            @click="$router.push(item.path)"
          >
            {{ item.text }}
          </div>
        </div>

        <!-- 用户区域 -->
        <div class="user-area">
          <template v-if="!userStore.isLogin">
            <el-button type="text" @click="loginVisible = true">
              登录
            </el-button>
            <el-button type="primary" @click="loginVisible = true">
              注册
            </el-button>
          </template>
          <template v-else>
            <el-dropdown trigger="click">
              <div class="user-info">
                <el-avatar
                  :size="32"
                  :src="
                    userStore.userInfo.userAvatar ||
                    require('@/assets/imgs/default-avatar.jpeg')
                  "
                >
                  {{ userStore.userInfo.userName || "新" }}
                </el-avatar>
                <span class="username">{{
                  userStore.userInfo.userName || "新用户"
                }}</span>
                <i class="el-icon-arrow-down"></i>
              </div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>
                  <router-link to="/front/person">个人中心</router-link>
                </el-dropdown-item>
                <el-dropdown-item>
                  <router-link to="/front/post/add">发布帖子</router-link>
                </el-dropdown-item>
                <el-dropdown-item>
                  <router-link to="/front/post/my">我的帖子</router-link>
                </el-dropdown-item>
                <el-dropdown-item>
                  <router-link to="/front/orders">我的订单</router-link>
                </el-dropdown-item>
                <el-dropdown-item divided @click.native="handleLogout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </div>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="main-content">
      <router-view
        @showLogin="showLoginDialog"
        class="main-container"
      ></router-view>
    </div>

    <!-- 页脚 -->
    <Footer />

    <!-- 登录注册对话框 -->
    <login-register
      :visible.sync="loginDialogVisible"
      @success="handleLoginSuccess"
    ></login-register>
  </div>
</template>

<script>
import Footer from "@/components/Footer";
import { useUserStore } from "@/stores/user";
import userRouter from "@/router/userRouter";
import LoginRegister from "@/components/LoginRegister.vue";

export default {
  name: "FrontLayout",
  components: { Footer, LoginRegister },

  data() {
    return {
      notice: "",
      loginVisible: false,
      loginDialogVisible: false,
    };
  },

  computed: {
    userStore() {
      return useUserStore();
    },
    menus() {
      return userRouter
        .filter((route) => route.meta?.isMenu)
        .map((route) => ({
          text: route.meta.name,
          path: `/front/${route.path}`,
        }));
    },
  },

  mounted() {
    this.loadNotice();
  },

  methods: {
    loadNotice() {
      // 加载公告信息
      // this.notice = await this.$request.get('/api/notice/latest')
    },

    async handleLogout() {
      try {
        await this.userStore.logout();
        this.$message.success("退出成功");
      } catch (error) {
        this.$message.error("退出失败");
      }
    },

    showLoginDialog() {
      this.loginDialogVisible = true;
    },

    handleLoginSuccess() {
      this.loginDialogVisible = false;
    },
  },
};
</script>

<style scoped>
.front-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.announcement-bar {
  position: relative;
  z-index: 1000;
}

.header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 999;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-area {
  display: flex;
  align-items: center;
}

.logo {
  height: 40px;
  margin-right: 10px;
}

.site-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 30px;
}

.nav-item {
  font-size: 16px;
  color: #666;
  cursor: pointer;
  padding: 6px 0;
  position: relative;
  transition: color 0.3s;
}

.nav-item:hover {
  color: #409eff;
}

.nav-item-active {
  color: #409eff;
}

.nav-item-active::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #409eff;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  margin: 0 8px;
  color: #333;
}

.main-content {
  flex: 1;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .header-content {
    padding: 0 10px;
  }

  .site-title {
    display: none;
  }

  .nav-menu {
    gap: 15px;
  }

  .nav-item {
    font-size: 14px;
  }

  .main-content {
    padding: 10px;
  }
}
</style>
