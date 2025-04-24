<template>
  <div class="admin-login-container">
    <el-card class="login-card">
      <div class="title">管理员登录</div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        @submit.native.prevent
      >
        <el-form-item prop="userAccount">
          <el-input
            v-model="loginForm.userAccount"
            prefix-icon="el-icon-user"
            placeholder="请输入管理员账号"
          />
        </el-form-item>
        <el-form-item prop="userPassword">
          <el-input
            v-model="loginForm.userPassword"
            prefix-icon="el-icon-lock"
            type="password"
            placeholder="请输入密码"
            @keyup.enter.native="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { useUserStore } from "@/stores/user";
import { ACCESS_ENUM } from "@/access/accessEnum";

export default {
  name: "Login",
  data() {
    return {
      loginForm: {
        userAccount: "",
        userPassword: "",
      },
      loading: false,
      rules: {
        userAccount: [
          { required: true, message: "请输入账号", trigger: "blur" },
          { min: 4, message: "账号长度不能小于4位", trigger: "blur" },
        ],
        userPassword: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { min: 6, message: "密码长度不能小于6位", trigger: "blur" },
        ],
      },
    };
  },
  methods: {
    async handleLogin() {
      this.$refs.loginFormRef.validate(async (valid) => {
        if (valid) {
          this.loading = true;
          try {
            const userStore = useUserStore();
            const res = await userStore.login(this.loginForm);

            if (res.code === 0) {
              if (res.data.userRole === "admin") {
                this.$message.success("管理员登录成功");
                setTimeout(() => {
                  const redirect = this.$route.query.redirect || "/admin/home";
                  this.$router.push(redirect);
                }, 500);
              } else {
                this.$message.error("非管理员账号，无法登录");
                await userStore.logout();
              }
            } else {
              this.$message.error(res.message || "登录失败");
            }
          } catch (error) {
            let errorMsg = "登录失败";
            if (error.message) {
              errorMsg = error.message;
            } else if (error.response?.data?.message) {
              errorMsg = error.response.data.message;
            }
            this.$message.error(errorMsg);
          } finally {
            this.loading = false;
          }
        }
      });
    },
  },
};
</script>

<style scoped>
.admin-login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
}

.login-card {
  width: 400px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 30px;
  color: #1890ff;
}
</style>
