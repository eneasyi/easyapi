<template>
  <el-dialog
    :visible.sync="visible"
    width="40%"
    :before-close="handleCancel"
    append-to-body
    custom-class="login-dialog"
  >
    <div class="login-container">
      <el-row :gutter="24">
        <el-col :span="8">
          <img src="@/assets/imgs/login-left.jpeg" class="login-image" />
        </el-col>
        <el-col :span="14">
          <div class="form-container">
            <el-tabs v-model="activeTab">
              <el-tab-pane label="登录" name="login">
                <el-form
                  ref="loginFormRef"
                  :model="loginForm"
                  :rules="loginRules"
                >
                  <el-form-item prop="userAccount">
                    <el-input
                      v-model="loginForm.userAccount"
                      prefix-icon="el-icon-user"
                      placeholder="请输入用户名"
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
              </el-tab-pane>

              <el-tab-pane label="注册" name="register">
                <el-form
                  ref="registerFormRef"
                  :model="registerForm"
                  :rules="registerRules"
                >
                  <el-form-item prop="userAccount">
                    <el-input
                      v-model="registerForm.userAccount"
                      prefix-icon="el-icon-user"
                      placeholder="请输入用户名"
                    />
                  </el-form-item>
                  <el-form-item prop="userPassword">
                    <el-input
                      v-model="registerForm.userPassword"
                      prefix-icon="el-icon-lock"
                      type="password"
                      placeholder="请输入密码"
                    />
                  </el-form-item>
                  <el-form-item prop="checkPassword">
                    <el-input
                      v-model="registerForm.checkPassword"
                      prefix-icon="el-icon-lock"
                      type="password"
                      placeholder="请再次输入密码"
                      @keyup.enter.native="handleRegister"
                    />
                  </el-form-item>
                  <el-form-item>
                    <el-button
                      type="primary"
                      :loading="loading"
                      style="width: 100%"
                      @click="handleRegister"
                    >
                      注册
                    </el-button>
                  </el-form-item>
                </el-form>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-col>
      </el-row>
    </div>
  </el-dialog>
</template>

<script>
import { useUserStore } from "@/stores/user";

export default {
  name: "LoginRegister",
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    const validateCheckPassword = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次输入密码"));
      } else if (value !== this.registerForm.userPassword) {
        callback(new Error("两次输入密码不一致"));
      } else {
        callback();
      }
    };

    return {
      loading: false,
      activeTab: "login",
      loginForm: {
        userAccount: "",
        userPassword: "",
      },
      registerForm: {
        userAccount: "",
        userPassword: "",
        checkPassword: "",
      },
      loginRules: {
        userAccount: [
          { required: true, message: "请输入用户名", trigger: "blur" },
        ],
        userPassword: [
          { required: true, message: "请输入密码", trigger: "blur" },
        ],
      },
      registerRules: {
        userAccount: [
          { required: true, message: "请输入用户名", trigger: "blur" },
          {
            min: 4,
            max: 16,
            message: "用户名长度为4-16个字符",
            trigger: "blur",
          },
        ],
        userPassword: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { min: 6, message: "密码长度至少为6个字符", trigger: "blur" },
        ],
        checkPassword: [
          { required: true, message: "请再次输入密码", trigger: "blur" },
          { validator: validateCheckPassword, trigger: "blur" },
        ],
      },
    };
  },
  methods: {
    handleCancel() {
      this.$emit("update:visible", false);
      this.resetForms();
    },
    resetForms() {
      this.loginForm.userAccount = "";
      this.loginForm.userPassword = "";
      this.registerForm.userAccount = "";
      this.registerForm.userPassword = "";
      this.registerForm.checkPassword = "";
      if (this.$refs.loginFormRef) {
        this.$refs.loginFormRef.resetFields();
      }
      if (this.$refs.registerFormRef) {
        this.$refs.registerFormRef.resetFields();
      }
    },
    async handleLogin() {
      this.$refs.loginFormRef.validate(async (valid) => {
        if (valid) {
          this.loading = true;
          try {
            const userStore = useUserStore();
            const res = await userStore.login(this.loginForm);

            if (res.code === 0) {
              if (res.data.userRole === "user") {
                this.$message.success("登录成功");
                this.handleCancel();
              } else if (res.data.userRole === "admin") {
                this.$message.error("管理员请前往管理后台登录");
                await userStore.logout();
                this.handleCancel();
              } else if (res.data.userRole === "ban") {
                this.$message.error("账号已被封禁，请联系管理员");
                await userStore.logout();
                this.handleCancel();
              } else {
                this.$message.error("用户角色异常");
                await userStore.logout();
                this.handleCancel();
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
    async handleRegister() {
      this.$refs.registerFormRef.validate(async (valid) => {
        if (valid) {
          this.loading = true;
          try {
            const res = await this.$request.post(
              "/api/user/register",
              this.registerForm
            );
            if (res.code === 0) {
              this.$message.success("注册成功，请登录");
              this.activeTab = "login";
              this.loginForm.userAccount = this.registerForm.userAccount;
              this.registerForm.userAccount = "";
              this.registerForm.userPassword = "";
              this.registerForm.checkPassword = "";
              this.$refs.registerFormRef.resetFields();
            } else {
              this.$message.error(res.message || "注册失败");
            }
          } catch (error) {
            let errorMsg = "注册失败";
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
.login-container {
  padding: 0;
  background: #fff;
}

.login-image {
  width: 100%;
  height: 400px;
  border-radius: 10px;
  object-fit: cover;
}

.form-container {
  padding: 30px 20px;
  height: 400px;
  display: flex;
  flex-direction: column;
}

:deep(.el-tabs__header) {
  margin-bottom: 25px;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  height: 40px;
  line-height: 40px;
}

:deep(.el-tabs__item.is-active) {
  font-weight: 600;
}

:deep(.el-form-item__content) {
  line-height: 40px;
}

:deep(.el-input__inner) {
  height: 40px;
  border-radius: 20px;
}

:deep(.el-button) {
  height: 40px;
  border-radius: 20px;
  font-size: 16px;
}

:deep(.el-input__prefix) {
  left: 10px;
}

:deep(.el-input input) {
  padding-left: 35px;
}

:deep(.login-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  display: none;
}

:deep(.el-dialog__body) {
  padding: 0;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}
</style>
