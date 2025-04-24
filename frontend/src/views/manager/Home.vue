<template>
  <div class="admin-home">
    <!-- 统计卡片区域 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-header">
            <div class="stats-title">总用户数</div>
            <i class="el-icon-user stats-icon"></i>
          </div>
          <div class="stats-number">{{ stats.userCount || 0 }}</div>
          <div class="stats-footer">
            <span>较昨日</span>
            <span
              :class="['change-rate', stats.userGrowth >= 0 ? 'up' : 'down']"
            >
              {{ stats.userGrowth >= 0 ? "+" : "" }}{{ stats.userGrowth || 0 }}%
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-header">
            <div class="stats-title">今日访问</div>
            <i class="el-icon-view stats-icon"></i>
          </div>
          <div class="stats-number">{{ stats.visitCount || 0 }}</div>
          <div class="stats-footer">
            <span>较昨日</span>
            <span
              :class="['change-rate', stats.visitGrowth >= 0 ? 'up' : 'down']"
            >
              {{ stats.visitGrowth >= 0 ? "+" : ""
              }}{{ stats.visitGrowth || 0 }}%
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-header">
            <div class="stats-title">文章数量</div>
            <i class="el-icon-document stats-icon"></i>
          </div>
          <div class="stats-number">{{ stats.articleCount || 0 }}</div>
          <div class="stats-footer">
            <span>较昨日</span>
            <span
              :class="['change-rate', stats.articleGrowth >= 0 ? 'up' : 'down']"
            >
              {{ stats.articleGrowth >= 0 ? "+" : ""
              }}{{ stats.articleGrowth || 0 }}%
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-header">
            <div class="stats-title">系统消息</div>
            <i class="el-icon-bell stats-icon"></i>
          </div>
          <div class="stats-number">{{ stats.messageCount || 0 }}</div>
          <div class="stats-footer">
            <span>未读消息</span>
            <span class="change-rate">{{ stats.unreadCount || 0 }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 欢迎信息 -->
    <el-card class="welcome-card">
      <div class="welcome-header">
        <img
          :src="
            userStore.userInfo.userAvatar ||
            require('@/assets/imgs/default-avatar.jpeg')
          "
          class="admin-avatar"
        />
        <div class="welcome-text">
          <h2>欢迎回来，{{ userStore.userInfo.userName }}</h2>
          <p>上次登录时间：{{ lastLoginTime }}</p>
        </div>
      </div>
      <div class="quick-actions">
        <el-button
          type="primary"
          icon="el-icon-plus"
          @click="$router.push('/admin/article/add')"
        >
          发布文章
        </el-button>
        <el-button
          type="success"
          icon="el-icon-user"
          @click="$router.push('/admin/user')"
        >
          用户管理
        </el-button>
        <el-button
          type="warning"
          icon="el-icon-setting"
          @click="$router.push('/admin/system')"
        >
          系统设置
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { useUserStore } from "@/stores/user";

export default {
  name: "AdminHome",
  data() {
    return {
      stats: {
        userCount: 1234,
        userGrowth: 5.2,
        visitCount: 3456,
        visitGrowth: -2.1,
        articleCount: 89,
        articleGrowth: 12.5,
        messageCount: 23,
        unreadCount: 4,
      },
      lastLoginTime: "2024-03-19 15:30:22",
    };
  },
  computed: {
    userStore() {
      return useUserStore();
    },
  },
  mounted() {
    this.loadDashboardData();
  },
  methods: {
    async loadDashboardData() {
      try {
        // const res = await this.$request.get('/api/admin/dashboard');
        // this.stats = res.data;
        // this.lastLoginTime = res.data.lastLoginTime;
      } catch (error) {
        console.error("加载仪表盘数据失败:", error);
      }
    },
  },
};
</script>

<style scoped>
.admin-home {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  height: 180px;
  transition: all 0.3s;
}

.stats-card:hover {
  transform: translateY(-5px);
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.stats-title {
  font-size: 16px;
  color: #606266;
}

.stats-icon {
  font-size: 24px;
  color: #409eff;
}

.stats-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 20px;
}

.stats-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #909399;
  font-size: 14px;
}

.change-rate {
  font-weight: bold;
}

.change-rate.up {
  color: #67c23a;
}

.change-rate.down {
  color: #f56c6c;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
}

.admin-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-right: 20px;
  object-fit: cover;
}

.welcome-text h2 {
  margin: 0 0 10px;
  font-size: 24px;
  color: #303133;
}

.welcome-text p {
  margin: 0;
  color: #909399;
}

.quick-actions {
  display: flex;
  gap: 15px;
}

/* 响应式布局 */
@media screen and (max-width: 1200px) {
  .el-col-6 {
    width: 50%;
    margin-bottom: 20px;
  }
}

@media screen and (max-width: 768px) {
  .el-col-6 {
    width: 100%;
  }

  .quick-actions {
    flex-direction: column;
  }

  .quick-actions .el-button {
    width: 100%;
  }
}
</style>
