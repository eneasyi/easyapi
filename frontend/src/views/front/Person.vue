<template>
  <div class="main-content">
    <div class="main-next person-container">
      <!-- 个人信息头部 -->
      <div class="profile-header">
        <avatar-upload
          :value="userInfo.userAvatar"
          @input="updateAvatar"
          @error="handleAvatarError"
        />
        <div class="profile-info">
          <div class="name-wrapper">
            <h2>{{ userInfo.userName || "未设置昵称" }}</h2>
            <el-button type="text" class="edit-btn" @click="showEditDialog">
              <i class="el-icon-edit"></i> 编辑资料
            </el-button>
          </div>
          <p class="user-role">
            {{ getRoleLabel(userInfo.userRole) }}
          </p>
          <div class="user-details">
            <div class="detail-item">
              <i class="el-icon-medal"></i>
              <span>等级 {{ userInfo.userLevel || 1 }}</span>
            </div>
            <div class="detail-item">
              <i class="el-icon-data-line"></i>
              <span>经验值 {{ userInfo.userExp || 0 }}</span>
            </div>
            <div class="detail-item">
              <i class="el-icon-coin"></i>
              <span>积分 {{ userInfo.userPoints || 0 }}</span>
            </div>
            <div class="detail-item">
              <i class="el-icon-phone"></i>
              <span class="detail-value">{{
                userInfo.userPhone || "未设置"
              }}</span>
            </div>
            <div class="detail-item">
              <i class="el-icon-message"></i>
              <span class="detail-value">{{
                userInfo.userEmail || "未设置"
              }}</span>
            </div>
            <div class="detail-item">
              <i class="el-icon-location"></i>
              <span class="detail-value">{{
                userInfo.userCity || "未设置"
              }}</span>
            </div>
            <div class="detail-item">
              <i class="el-icon-user"></i>
              <span class="detail-value">{{
                userInfo.userGender || "未设置"
              }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 数据和安全设置 -->
      <div class="data-tabs">
        <el-tabs type="border-card">
          <!-- 安全设置面板 -->
          <el-tab-pane label="安全设置">
            <el-collapse ref="collapse">
              <el-collapse-item title="修改密码" name="1">
                <el-form
                  :model="passwordForm"
                  :rules="passwordRules"
                  ref="passwordRef"
                  label-width="100px"
                >
                  <el-form-item label="原密码" prop="oldPassword">
                    <el-input
                      v-model="passwordForm.oldPassword"
                      type="password"
                      placeholder="请输入原密码"
                    ></el-input>
                  </el-form-item>
                  <el-form-item label="新密码" prop="newPassword">
                    <el-input
                      v-model="passwordForm.newPassword"
                      type="password"
                      placeholder="请输入新密码"
                    ></el-input>
                  </el-form-item>
                  <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input
                      v-model="passwordForm.confirmPassword"
                      type="password"
                      placeholder="请确认新密码"
                    ></el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="savePassword"
                      >修改密码</el-button
                    >
                  </el-form-item>
                </el-form>
              </el-collapse-item>
              <el-collapse-item title="绑定手机号" name="phone">
                <el-form
                  :model="phoneForm"
                  :rules="phoneRules"
                  ref="phoneRef"
                  label-width="100px"
                >
                  <el-form-item label="手机号" prop="userPhone">
                    <el-input
                      v-model="phoneForm.userPhone"
                      placeholder="请输入手机号"
                    ></el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="bindPhone"
                      >绑定手机号</el-button
                    >
                  </el-form-item>
                </el-form>
              </el-collapse-item>
              <el-collapse-item title="绑定邮箱" name="email">
                <el-form
                  :model="emailForm"
                  :rules="emailRules"
                  ref="emailRef"
                  label-width="100px"
                >
                  <el-form-item label="邮箱" prop="userEmail">
                    <el-input
                      v-model="emailForm.userEmail"
                      placeholder="请输入邮箱"
                    ></el-input>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="bindEmail"
                      >绑定邮箱</el-button
                    >
                  </el-form-item>
                </el-form>
              </el-collapse-item>
            </el-collapse>
          </el-tab-pane>
          <!-- 我的地址面板 -->
          <el-tab-pane label="我的地址">
            <address-manage />
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 编辑信息弹窗 -->
      <el-dialog
        title="编辑个人资料"
        :visible.sync="editDialogVisible"
        width="40%"
      >
        <el-form
          :model="editForm"
          :rules="rules"
          ref="formRef"
          label-width="100px"
        >
          <el-form-item label="账号" prop="userAccount">
            <el-input v-model="editForm.userAccount" disabled></el-input>
          </el-form-item>
          <el-form-item label="昵称" prop="userName">
            <el-input
              v-model="editForm.userName"
              placeholder="请输入昵称"
            ></el-input>
          </el-form-item>
          <el-form-item label="性别" prop="userGender">
            <el-select v-model="editForm.userGender" placeholder="请选择性别">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
              <el-option label="保密" value="保密" />
            </el-select>
          </el-form-item>
          <el-form-item label="城市" prop="userCity">
            <el-input
              v-model="editForm.userCity"
              placeholder="请输入城市"
            ></el-input>
          </el-form-item>
          <el-form-item label="手机号" prop="userPhone">
            <el-input
              v-model="editForm.userPhone"
              placeholder="请输入手机号"
            ></el-input>
          </el-form-item>
          <el-form-item label="邮箱" prop="userEmail">
            <el-input
              v-model="editForm.userEmail"
              placeholder="请输入邮箱"
            ></el-input>
          </el-form-item>
          <el-form-item label="个人简介" prop="userProfile">
            <el-input
              type="textarea"
              :rows="4"
              v-model="editForm.userProfile"
              placeholder="请输入个人简介"
            ></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer">
          <el-button @click="editDialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveEdit">保 存</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { useUserStore } from "@/stores/user";
import AvatarUpload from "@/components/AvatarUpload.vue";
import AddressManage from "@/components/AddressManage.vue";

export default {
  name: "Person",
  components: {
    AvatarUpload,
    AddressManage,
  },
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次输入密码"));
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error("两次输入密码不一致!"));
      } else {
        callback();
      }
    };
    const validatePhone = (rule, value, callback) => {
      if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error("请输入正确的手机号"));
      } else {
        callback();
      }
    };
    return {
      uploadUrl: process.env.VUE_APP_UPLOAD_URL,
      userInfo: {},
      editForm: {},
      editDialogVisible: false,
      rules: {
        userName: [{ required: true, message: "请输入昵称", trigger: "blur" }],
        userPhone: [
          {
            pattern: /^1[3-9]\d{9}$/,
            message: "请输入正确的手机号码",
            trigger: "blur",
          },
        ],
        userEmail: [
          {
            type: "email",
            message: "请输入正确的邮箱地址",
            trigger: "blur",
          },
        ],
      },
      passwordForm: {
        oldPassword: "",
        newPassword: "",
        confirmPassword: "",
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: "请输入原密码", trigger: "blur" },
        ],
        newPassword: [
          { required: true, message: "请输入新密码", trigger: "blur" },
          { min: 6, message: "密码长度不能小于6位", trigger: "blur" },
        ],
        confirmPassword: [
          { required: true, message: "请确认密码", trigger: "blur" },
          { validator: validateConfirmPassword, trigger: "blur" },
        ],
      },
      phoneForm: {
        userPhone: "",
      },
      emailForm: {
        userEmail: "",
      },
      phoneRules: {
        userPhone: [
          { required: true, message: "请输入手机号", trigger: "blur" },
          {
            pattern: /^1[3-9]\d{9}$/,
            message: "请输入正确的手机号",
            trigger: "blur",
          },
        ],
      },
      emailRules: {
        userEmail: [
          { required: true, message: "请输入邮箱", trigger: "blur" },
          { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.initUserInfo();
  },
  methods: {
    async initUserInfo() {
      const userStore = useUserStore();
      await userStore.fetchUserInfo();
      this.userInfo = userStore.getUserInfo;
    },
    showEditDialog() {
      this.editForm = { ...this.userInfo };
      this.editDialogVisible = true;
    },
    async saveEdit() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          try {
            const res = await this.$request.post("/api/user/update/my", {
              userName: this.editForm.userName,
              userGender: this.editForm.userGender,
              userCity: this.editForm.userCity,
              userProfile: this.editForm.userProfile,
            });
            if (res.code === 0) {
              const userStore = useUserStore();
              await userStore.fetchUserInfo();
              this.userInfo = userStore.getUserInfo;
              this.$message.success("修改成功");
              this.editDialogVisible = false;
            } else {
              this.$message.error(res.message || "修改失败");
            }
          } catch (error) {
            this.$message.error("系统错误，请稍后重试");
          }
        }
      });
    },
    async savePassword() {
      this.$refs.passwordRef.validate(async (valid) => {
        if (valid) {
          try {
            const res = await this.$request.post(
              "/api/user/update/password",
              this.passwordForm
            );
            if (res.code === 0) {
              this.$message.success("修改成功，请重新登录");
              this.passwordForm = {};
              this.$refs.passwordRef.resetFields();
              this.$refs.collapse.activeNames = [];

              const userStore = useUserStore();
              await userStore.logout();
              this.$router.push("/login");
            }
          } catch (error) {
            this.$message.error(error.message || "修改失败");
          }
        }
      });
    },
    async updateAvatar(url) {
      try {
        const updateRes = await this.$request.post("/api/user/update/my", {
          userName: this.userInfo.userName,
          userAvatar: url,
          userProfile: this.userInfo.userProfile,
        });
        if (updateRes.code === 0) {
          const userStore = useUserStore();
          await userStore.fetchUserInfo();
          this.userInfo = { ...userStore.getUserInfo };
          this.$message.success("头像更新成功");
        }
      } catch (error) {
        this.$message.error("更新用户信息失败");
      }
    },
    handleAvatarError() {
      this.$message.error("上传失败");
    },
    getRoleLabel(role) {
      const labels = {
        admin: "管理员",
        user: "普通用户",
        ban: "已封禁",
      };
      return labels[role] || role;
    },
    async bindPhone() {
      this.$refs.phoneRef.validate(async (valid) => {
        if (valid) {
          try {
            const res = await this.$request.post("/api/user/update/my", {
              userPhone: this.phoneForm.userPhone,
            });
            if (res.code === 0) {
              const userStore = useUserStore();
              await userStore.fetchUserInfo();
              this.userInfo = userStore.getUserInfo;
              this.$message.success("绑定手机号成功");
              this.$refs.collapse.activeNames = [];
            } else {
              this.$message.error(res.message || "绑定失败");
            }
          } catch (error) {
            this.$message.error("系统错误，请稍后重试");
          }
        }
      });
    },
    async bindEmail() {
      this.$refs.emailRef.validate(async (valid) => {
        if (valid) {
          try {
            const res = await this.$request.post("/api/user/update/my", {
              userEmail: this.emailForm.userEmail,
            });
            if (res.code === 0) {
              const userStore = useUserStore();
              await userStore.fetchUserInfo();
              this.userInfo = userStore.getUserInfo;
              this.$message.success("绑定邮箱成功");
              this.$refs.collapse.activeNames = [];
            } else {
              this.$message.error(res.message || "绑定失败");
            }
          } catch (error) {
            this.$message.error("系统错误，请稍后重试");
          }
        }
      });
    },
  },
};
</script>

<style scoped>
/* 个人信息卡片样式 */
.profile-header {
  background: white;
  border-radius: 8px;
  padding: 30px;
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.profile-info {
  flex: 1;
}

.name-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.name-wrapper h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.user-role {
  color: #666;
  font-size: 14px;
  margin: 0 0 15px;
}

.user-details {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-item i {
  color: #666;
  font-size: 16px;
}

.detail-value {
  color: #333;
  font-size: 14px;
}

.edit-btn {
  color: #409eff;
  padding: 0;
}

.edit-btn i {
  margin-right: 4px;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
    padding: 20px;
  }

  .user-details {
    grid-template-columns: 1fr;
    justify-items: center;
  }
}

/* 容器样式 */
.person-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 30px 20px;
}

/* 数据和安全设置卡片 */
.data-tabs {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.address-list {
  margin-top: 20px;
}

.delete-btn {
  color: #f56c6c;
}

.delete-btn:hover {
  color: #f78989;
}
</style>
