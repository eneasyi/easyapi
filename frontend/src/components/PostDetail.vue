<template>
  <el-dialog
    :visible.sync="visible"
    width="1000px"
    :before-close="handleClose"
    custom-class="post-detail-dialog"
    append-to-body
  >
    <div class="detail-container" v-if="post">
      <div class="detail-main">
        <div class="post-header">
          <div class="user-info">
            <el-avatar
              :size="50"
              :src="
                (post.user && post.user.userAvatar) ||
                require('@/assets/imgs/default-avatar.jpeg')
              "
            />
            <div class="user-meta">
              <div class="username">
                {{ post.user && post.user.userName }}
              </div>
              <div class="time">
                {{ formatTime(post.createTime) }}
              </div>
            </div>
          </div>
        </div>

        <!-- 帖子标题和内容 -->
        <h2 class="post-title">{{ post.title }}</h2>
        <p class="post-content">{{ post.content }}</p>

        <!-- 帖子图片 -->
        <div
          v-if="post.images && post.images.length"
          class="post-images detail-images"
        >
          <el-image
            v-for="(img, index) in post.images"
            :key="index"
            :src="img"
            :preview-src-list="post.images"
            fit="cover"
            class="post-image"
          />
        </div>

        <!-- 帖子标签 -->
        <div class="post-tags">
          <el-tag
            v-for="tag in post.tags"
            :key="tag"
            size="small"
            effect="plain"
          >
            {{ tag }}
          </el-tag>
        </div>

        <!-- 帖子操作 -->
        <div class="post-actions">
          <div class="action-item">
            <i class="el-icon-view"></i>
            <span>浏览 {{ post.viewNum || 0 }}</span>
          </div>
          <div
            class="action-item"
            :class="{ active: post.hasLike }"
            @click="handleLike"
          >
            <i
              :class="post.hasLike ? 'fas fa-heart' : 'far fa-heart'"
              style="color: #ff4949"
            ></i>
            <span>点赞 {{ post.likeNum }}</span>
          </div>
          <div
            class="action-item"
            :class="{ active: post.hasFavour }"
            @click="handleFavour"
          >
            <i
              :class="post.hasFavour ? 'fas fa-star' : 'far fa-star'"
              style="color: #f7ba2a"
            ></i>
            <span>收藏 {{ post.favourNum }}</span>
          </div>
        </div>
      </div>
      <div class="detail-side">
        <!-- 评论区 -->
        <comment-list
          :targetId="Number(postId)"
          moduleType="post"
          @showLogin="$emit('showLogin')"
        />
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { useUserStore } from "@/stores/user";
import CommentList from "./CommentList.vue";

export default {
  name: "PostDetail",
  components: {
    CommentList,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    postId: {
      type: [String, Number],
      required: true,
      default: 0,
    },
  },
  data() {
    return {
      post: {},
      loading: false,
    };
  },
  watch: {
    visible(val) {
      if (val) {
        this.getPostDetail();
      }
    },
    postId: {
      immediate: true,
      handler(val) {
        if (val && this.visible) {
          this.getPostDetail();
        }
      },
    },
  },
  methods: {
    handleClose() {
      this.$emit("update:visible", false);
    },
    async getPostDetail() {
      this.loading = true;
      try {
        const res = await this.$request.get(
          `/api/post/get/vo?id=${this.postId}`
        );
        if (res.code === 0) {
          this.post = res.data;
        } else {
          this.$message.error(res.message || "获取帖子详情失败");
        }
      } catch (error) {
        this.$message.error("获取帖子详情失败");
      } finally {
        this.loading = false;
      }
    },
    formatTime(time) {
      if (!time) return "";
      const date = new Date(time);
      return date.toLocaleString();
    },
    async handleLike() {
      const userStore = useUserStore();
      if (!userStore.isLogin) {
        this.$confirm("登录后才能点赞，是否去登录？", "提示", {
          confirmButtonText: "去登录",
          cancelButtonText: "取消",
          type: "info",
        })
          .then(() => {
            this.visible = false;
            this.$emit("showLogin");
          })
          .catch(() => {});
        return;
      }
      try {
        const res = await this.$request.post(`/api/like/`, null, {
          params: {
            targetId: this.post.id,
            moduleType: "POST",
          },
        });
        if (res.code === 0) {
          this.post.hasLike = !this.post.hasLike;
          this.post.likeNum += this.post.hasLike ? 1 : -1;
          this.$message.success(this.post.hasLike ? "点赞成功" : "已取消点赞");
        } else {
          this.$message.error(res.message || "操作失败");
        }
      } catch (error) {
        this.$message.error("网络错误，请稍后重试");
      }
    },
    async handleFavour() {
      const userStore = useUserStore();
      if (!userStore.isLogin) {
        this.$confirm("登录后才能收藏，是否去登录？", "提示", {
          confirmButtonText: "去登录",
          cancelButtonText: "取消",
          type: "info",
        })
          .then(() => {
            this.visible = false;
            this.$emit("showLogin");
          })
          .catch(() => {});
        return;
      }
      try {
        const res = await this.$request.post(`/api/favour/`, null, {
          params: {
            targetId: this.post.id,
            moduleType: "POST",
          },
        });
        if (res.code === 0) {
          this.post.hasFavour = !this.post.hasFavour;
          this.post.favourNum += this.post.hasFavour ? 1 : -1;
          this.$message.success(
            this.post.hasFavour ? "收藏成功" : "已取消收藏"
          );
        } else {
          this.$message.error(res.message || "操作失败");
        }
      } catch (error) {
        this.$message.error("网络错误，请稍后重试");
      }
    },
  },
};
</script>

<style scoped>
.detail-container {
  display: flex;
  gap: 20px;
  height: 600px;
}

.detail-main {
  flex: 1;
  padding: 20px;
  border-right: 1px solid #ebeef5;
  overflow-y: auto;
}

.detail-side {
  width: 400px;
  padding: 20px;
  overflow-y: auto;
}

.post-header {
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-meta {
  display: flex;
  flex-direction: column;
}

.username {
  font-weight: bold;
  color: #303133;
}

.time {
  font-size: 12px;
  color: #909399;
}

.post-title {
  margin: 0 0 10px;
  font-size: 18px;
  color: #303133;
}

.post-content {
  margin: 0 0 15px;
  color: #606266;
  white-space: pre-wrap;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin: 20px 0;
}

.post-image {
  width: 100%;
  height: 100px;
  border-radius: 4px;
  cursor: pointer;
}

.post-tags {
  margin: 20px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.post-actions {
  display: flex;
  gap: 20px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.post-actions .action-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 20px;
  background-color: #f5f7fa;
}

.action-item:hover,
.action-item.active {
  background-color: #ecf5ff;
  color: #409eff;
}

/* 对话框样式 */
.post-detail-dialog {
  margin-top: 0;
  margin-bottom: 0;
}

/* 覆盖 element-ui 的默认样式 */
:deep(.el-dialog) {
  margin-top: 0 !important;
  position: relative;
  top: 50%;
  transform: translateY(-50%);
}

:deep(.el-dialog__body) {
  padding: 0;
}
</style>
