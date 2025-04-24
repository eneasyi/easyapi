<template>
  <div class="person-container">
    <!-- 个人信息卡片 -->
    <el-card class="profile-card">
      <div class="profile-header">
        <avatar-upload
          :value="userInfo.userAvatar"
          @input="updateAvatar"
          @error="handleAvatarError"
        />
        <div class="user-info">
          <h2>{{ userInfo.userName || "未设置昵称" }}</h2>
          <p>{{ userInfo.userAccount }}</p>
          <el-tag type="success">管理员</el-tag>
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
          </div>
        </div>
      </div>

      <!-- 信息编辑表单 -->
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="info-form"
      >
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="性别" prop="userGender">
          <el-select v-model="form.userGender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
            <el-option label="保密" value="保密" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="userPhone">
          <el-input v-model="form.userPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="userEmail">
          <el-input v-model="form.userEmail" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="城市" prop="userCity">
          <el-input v-model="form.userCity" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="个人简介" prop="userProfile">
          <el-input
            type="textarea"
            v-model="form.userProfile"
            :rows="4"
            placeholder="请输入个人简介"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">保存修改</el-button>
        </el-form-item>
      </el-form>

      <!-- 修改密码表单 -->
      <div class="password-section">
        <h3>修改密码</h3>
        <el-form
          ref="pwdForm"
          :model="pwdForm"
          :rules="pwdRules"
          label-width="100px"
        >
          <el-form-item label="原密码" prop="oldPassword">
            <el-input
              v-model="pwdForm.oldPassword"
              type="password"
              placeholder="请输入原密码"
              show-password
            />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="pwdForm.newPassword"
              type="password"
              placeholder="请输入新密码"
              show-password
            />
          </el-form-item>
          <el-form-item label="确认密码" prop="checkPassword">
            <el-input
              v-model="pwdForm.checkPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updatePassword"
              >修改密码</el-button
            >
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script>
import { useUserStore } from "@/stores/user";
import AvatarUpload from "@/components/AvatarUpload.vue";

export default {
  name: "AdminPerson",
  components: {
    AvatarUpload,
  },
  data() {
    // 密码确认校验
    const validatePass = (rule, value, callback) => {
      if (value !== this.pwdForm.newPassword) {
        callback(new Error("两次输入密码不一致!"));
      } else {
        callback();
      }
    };
    return {
      userInfo: {},
      form: {
        userName: "",
        userGender: "",
        userPhone: "",
        userEmail: "",
        userCity: "",
        userProfile: "",
      },
      rules: {
        userName: [
          { required: true, message: "请输入用户名", trigger: "blur" },
        ],
        userPhone: [
          {
            pattern: /^1[3-9]\d{9}$/,
            message: "请输入正确的手机号",
            trigger: "blur",
          },
        ],
        userEmail: [
          { type: "email", message: "请输入正确的邮箱地址", trigger: "blur" },
        ],
      },
      pwdForm: {
        oldPassword: "",
        newPassword: "",
        checkPassword: "",
      },
      pwdRules: {
        oldPassword: [
          { required: true, message: "请输入原密码", trigger: "blur" },
          { min: 6, message: "密码长度不能小于6位", trigger: "blur" },
        ],
        newPassword: [
          { required: true, message: "请输入新密码", trigger: "blur" },
          { min: 6, message: "密码长度不能小于6位", trigger: "blur" },
        ],
        checkPassword: [
          { required: true, message: "请再次输入新密码", trigger: "blur" },
          { validator: validatePass, trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getUserInfo();
  },
  methods: {
    // 获取用户信息
    async getUserInfo() {
      try {
        const res = await this.$request.get("/api/user/get/login");
        if (res.code !== 0) {
          this.$message.error(res.message || "获取用户信息失败");
          return;
        }
        this.userInfo = res.data;
        this.form = { ...res.data };
      } catch (error) {
        this.$message.error("系统错误，请稍后重试");
      }
    },

    // 更新头像
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
      this.$message.error("上传失败，请重试");
    },

    // 提交个人信息表单
    async submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          try {
            const res = await this.$request.post("/api/user/update/my", {
              userName: this.form.userName,
              userGender: this.form.userGender,
              userCity: this.form.userCity,
              userProfile: this.form.userProfile,
            });
            if (res.code === 0) {
              const userStore = useUserStore();
              await userStore.fetchUserInfo();
              this.userInfo = userStore.getUserInfo;
              this.$message.success("修改成功");
            } else {
              this.$message.error(res.message || "修改失败");
            }
          } catch (error) {
            this.$message.error("系统错误，请稍后重试");
          }
        }
      });
    },

    // 修改密码
    updatePassword() {
      this.$refs.pwdForm.validate(async (valid) => {
        if (valid) {
          try {
            const res = await this.$request.post(
              "/api/user/update/password",
              this.pwdForm
            );
            if (res.code !== 0) {
              this.$message.error(res.message || "修改密码失败");
              return;
            }
            this.$message.success("修改密码成功，请重新登录");
            this.pwdForm = {};
            this.$refs.pwdForm.resetFields();
            const userStore = useUserStore();
            await userStore.logout();
            this.$router.push("/login");
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
.person-container {
  padding: 20px;
}

.profile-card {
  max-width: 800px;
  margin: 0 auto;
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  margin-right: 30px;
}

.avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
  cursor: pointer;
}

.avatar-wrapper:hover .upload-overlay {
  opacity: 1;
}

.user-info {
  h2 {
    margin: 0 0 10px;
    font-size: 24px;
  }
  p {
    margin: 0 0 10px;
    color: #666;
  }
}

.info-form {
  max-width: 500px;
  margin: 30px auto;
}

.password-section {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #eee;

  h3 {
    margin-bottom: 20px;
    font-size: 18px;
    color: #333;
  }
}

.user-details {
  margin-top: 10px;
  display: flex;
  align-items: center;
}

.detail-item {
  margin-right: 20px;
  display: flex;
  align-items: center;
}

.detail-item i {
  margin-right: 5px;
}
</style>
