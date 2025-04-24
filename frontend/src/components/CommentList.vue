<template>
  <div>
    <div style="margin-bottom: 20px; font-size: 22px; font-weight: bold">
      评论 {{ commentCount }}
    </div>
    <div style="margin-bottom: 20px">
      <el-input
        type="textarea"
        placeholder="请输入评论"
        v-model="content"
      ></el-input>
      <div style="text-align: right; margin-top: 5px">
        <el-button type="primary" @click="addComment(null)">评论</el-button>
      </div>
    </div>

    <div>
      <div
        v-for="item in commentList"
        :key="item.id"
        style="margin-bottom: 20px"
      >
        <!-- 一级评论 -->
        <div style="display: flex; margin-bottom: 20px">
          <img
            :src="
              item.userVO.userAvatar ||
              require('@/assets/imgs/default-avatar.jpeg')
            "
            alt=""
            style="width: 50px; height: 50px; border-radius: 50%"
          />
          <div style="padding-left: 15px; flex: 1">
            <div style="margin-bottom: 10px; color: #666">
              {{ item.userVO.userName }}
            </div>
            <div style="margin-bottom: 5px">{{ item.content }}</div>
            <div style="color: #666; font-size: 13px; margin-bottom: 5px">
              <span>{{ formatTime(item.createTime) }}</span>
              <span
                @click="handleShowReply(item)"
                style="margin: 0 20px; cursor: pointer"
                :class="{ 'comment-active': item.showReply }"
              >
                <i class="el-icon-s-comment"></i>回复
              </span>
              <span
                v-if="item.userVO.id === userStore.userInfo.id"
                @click="deleteComment(item.id)"
                style="cursor: pointer"
              >
                <i class="el-icon-delete"></i>删除
              </span>
            </div>
            <div v-if="item.showReply">
              <el-input
                type="textarea"
                placeholder="请输入回复"
                v-model="item.replyContent"
              ></el-input>
              <div style="text-align: right; margin-top: 5px">
                <el-button type="primary" @click="addComment(item)"
                  >回复</el-button
                >
              </div>
            </div>
          </div>
        </div>

        <!-- 二级评论 -->
        <div v-if="item.children" style="padding-left: 65px">
          <!-- 显示第一条子评论 -->
          <div
            v-if="item.children.length > 0"
            style="display: flex; margin-bottom: 20px"
          >
            <img
              :src="
                item.children[0].userVO.userAvatar ||
                require('@/assets/imgs/default-avatar.jpeg')
              "
              alt=""
              style="width: 50px; height: 50px; border-radius: 50%"
            />
            <div style="padding-left: 15px; flex: 1">
              <div style="margin-bottom: 10px; color: #666">
                {{ item.children[0].userVO.userName }}
                <span
                  v-if="
                    item.children[0].userVO.userName !== item.userVO.userName
                  "
                >
                  回复：{{ item.userVO.userName }}
                </span>
              </div>
              <div style="margin-bottom: 5px">
                {{ item.children[0].content }}
              </div>
              <div style="color: #666; font-size: 13px; margin-bottom: 5px">
                <span>{{ formatTime(item.children[0].createTime) }}</span>
                <span
                  v-if="item.children[0].userVO.id === userStore.userInfo.id"
                  @click="deleteComment(item.children[0].id)"
                  style="cursor: pointer"
                >
                  <i class="el-icon-delete"></i>删除
                </span>
              </div>
            </div>
          </div>

          <!-- 其他子评论 -->
          <div v-show="item.showAllReplies">
            <div
              v-for="sub in item.children.slice(1)"
              :key="sub.id"
              style="display: flex; margin-bottom: 20px"
            >
              <img
                :src="
                  sub.userVO.userAvatar ||
                  require('@/assets/imgs/default-avatar.jpeg')
                "
                alt=""
                style="width: 50px; height: 50px; border-radius: 50%"
              />
              <div style="padding-left: 15px; flex: 1">
                <div style="margin-bottom: 10px; color: #666">
                  {{ sub.userVO.userName }}
                  <span v-if="sub.userVO.userName !== item.userVO.userName">
                    回复：{{ item.userVO.userName }}
                  </span>
                </div>
                <div style="margin-bottom: 5px">{{ sub.content }}</div>
                <div style="color: #666; font-size: 13px; margin-bottom: 5px">
                  <span>{{ formatTime(sub.createTime) }}</span>
                  <span
                    v-if="sub.userVO.id === userStore.userInfo.id"
                    @click="deleteComment(sub.id)"
                    style="cursor: pointer"
                  >
                    <i class="el-icon-delete"></i>删除
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- 展开/收起回复按钮 -->
          <div
            v-if="item.children.length > 1"
            style="color: #409eff; cursor: pointer; margin: 10px 0"
            @click="toggleReplies(item)"
          >
            <span>
              {{
                item.showAllReplies
                  ? "收起回复"
                  : `展开${item.children.length - 1}条回复`
              }}
            </span>
            <i
              :class="
                item.showAllReplies ? 'el-icon-arrow-up' : 'el-icon-arrow-down'
              "
              style="margin-left: 5px"
            ></i>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useUserStore } from "@/stores/user";

export default {
  name: "CommentList",
  props: {
    targetId: {
      type: Number,
      required: true,
    },
    moduleType: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      userStore: useUserStore(),
      commentCount: 0,
      content: "",
      commentList: [],
    };
  },
  created() {
    this.loadComments();
  },
  methods: {
    formatTime(time) {
      const now = new Date();
      const commentTime = new Date(time);
      const diffMs = now - commentTime;
      const diffMinutes = Math.floor(diffMs / (1000 * 60));
      const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
      const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
      const diffMonths = Math.floor(diffDays / 30);
      const diffYears = Math.floor(diffDays / 365);

      if (diffYears >= 1) {
        return commentTime.toLocaleString("zh-CN", {
          year: "numeric",
          month: "2-digit",
          day: "2-digit",
          hour: "2-digit",
          minute: "2-digit",
        });
      } else if (diffMonths >= 1) {
        return `${diffMonths}个月前`;
      } else if (diffMinutes >= 60) {
        if (diffDays >= 1) {
          return `${diffDays}天前`;
        } else {
          return `${diffHours}小时前`;
        }
      } else {
        if (diffMinutes <= 0) {
          return "刚刚";
        }
        return `${diffMinutes}分钟前`;
      }
    },
    handleShowReply(comment) {
      this.$set(comment, "showReply", !comment.showReply);
      if (comment.showReply) {
        comment.replyContent = "";
      }
    },
    async deleteComment(id) {
      try {
        await this.$confirm("确认删除该评论吗？", "提示", { type: "warning" });
        const res = await this.$request.post("/api/comment/delete", { id });
        if (res.code === 0) {
          this.$message.success("删除成功");
          this.loadComments();
        } else {
          this.$message.error(res.message || "删除失败");
        }
      } catch (error) {
        if (error !== "cancel") {
          this.$message.error("删除失败");
        }
      }
    },
    async loadComments() {
      try {
        // 获取评论树
        const res = await this.$request.get(
          `/api/comment/tree?targetId=${this.targetId}&moduleType=${this.moduleType}`
        );
        if (res.code === 0) {
          this.commentList = (res.data || []).map((comment) => ({
            ...comment,
            showAllReplies: false,
            showReply: false,
            replyContent: "",
          }));
          // 获取评论总数
          const countRes = await this.$request.post("/api/comment/count", {
            targetId: this.targetId,
            moduleType: this.moduleType,
          });
          if (countRes.code === 0) {
            this.commentCount = countRes.data || 0;
          }
        } else {
          this.$message.error(res.message || "获取评论失败");
        }
      } catch (error) {
        this.$message.error("获取评论失败");
      }
    },
    async addComment(comment) {
      if (!this.userStore.isLogin) {
        this.$message.warning("请先登录");
        return;
      }

      const content = comment ? comment.replyContent : this.content;
      if (!content?.trim()) {
        this.$message.warning("请输入评论内容");
        return;
      }

      try {
        const data = {
          targetId: this.targetId,
          moduleType: this.moduleType,
          content: content,
          parentId: comment?.id,
        };

        const res = await this.$request.post("/api/comment/add", data);
        if (res.code === 0) {
          this.$message.success("评论成功");
          this.content = "";
          if (comment) {
            comment.showReply = false;
            comment.replyContent = "";
          }
          this.loadComments();
        } else {
          this.$message.error(res.message || "评论失败");
        }
      } catch (error) {
        this.$message.error("评论失败");
      }
    },
    toggleReplies(comment) {
      this.$set(comment, "showAllReplies", !comment.showAllReplies);
    },
  },
};
</script>

<style scoped>
.comment-active {
  color: #409eff;
}
</style>
