<template>
  <div class="user-manage">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" ref="queryForm">
        <el-form-item label="用户名">
          <el-input
            v-model="queryParams.userName"
            placeholder="请输入用户名"
            clearable
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="queryParams.userPhone"
            placeholder="请输入手机号"
            clearable
          />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input
            v-model="queryParams.userEmail"
            placeholder="请输入邮箱"
            clearable
          />
        </el-form-item>
        <el-form-item label="用户角色">
          <el-select
            v-model="queryParams.userRole"
            placeholder="请选择角色"
            clearable
          >
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
            <el-option label="封禁用户" value="ban" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-select
            v-model="queryParams.userGender"
            placeholder="请选择性别"
            clearable
          >
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
            <el-option label="保密" value="保密" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <i class="el-icon-search"></i> 搜索
          </el-button>
          <el-button @click="resetQuery">
            <i class="el-icon-refresh"></i> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="table-card">
      <!-- 表格区域 -->
      <el-table v-loading="loading" :data="userList" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="userAccount" label="用户账号" min-width="120" />
        <el-table-column label="用户名" min-width="120">
          <template slot-scope="scope">
            {{ scope.row.userName || "未设置" }}
          </template>
        </el-table-column>
        <el-table-column label="用户头像" width="100" align="center">
          <template slot-scope="scope">
            <el-avatar
              :size="40"
              :src="
                scope.row.userAvatar ||
                require('@/assets/imgs/default-avatar.jpeg')
              "
            />
          </template>
        </el-table-column>
        <el-table-column prop="userRole" label="用户角色" width="100">
          <template slot-scope="scope">
            <el-tag :type="getRoleType(scope.row.userRole)">
              {{ getRoleLabel(scope.row.userRole) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="手机号" min-width="120">
          <template slot-scope="scope">
            {{ scope.row.userPhone || "未设置" }}
          </template>
        </el-table-column>
        <el-table-column label="邮箱" min-width="150">
          <template slot-scope="scope">
            {{ scope.row.userEmail || "未设置" }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180">
          <template slot-scope="scope">
            {{
              new Date(scope.row.createTime).toLocaleString("zh-CN", {
                year: "numeric",
                month: "2-digit",
                day: "2-digit",
                hour: "2-digit",
                minute: "2-digit",
                second: "2-digit",
                hour12: false,
              })
            }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleView(scope.row)">
              查看
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleEdit(scope.row)"
              v-if="scope.row.userRole !== 'admin'"
            >
              编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleDelete(scope.row)"
              class="delete-btn"
              v-if="scope.row.userRole !== 'admin'"
            >
              删除
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleBan(scope.row)"
              v-if="scope.row.userRole === 'user'"
            >
              封禁
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleUnban(scope.row)"
              v-if="scope.row.userRole === 'ban'"
            >
              解封
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="queryParams.current"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="queryParams.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        />
      </div>
    </el-card>

    <!-- 用户表单对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户账号" prop="userAccount">
          <el-input
            v-model="form.userAccount"
            placeholder="请输入用户账号"
            disabled
          />
        </el-form-item>
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="手机号" prop="userPhone">
          <el-input v-model="form.userPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="userEmail">
          <el-input v-model="form.userEmail" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 用户详情对话框 -->
    <el-dialog title="用户详情" :visible.sync="detailVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户账号">
          {{ detail.userAccount }}
        </el-descriptions-item>
        <el-descriptions-item label="用户名">
          {{ detail.userName || "未设置" }}
        </el-descriptions-item>
        <el-descriptions-item label="用户角色">
          <el-tag :type="getRoleType(detail.userRole)">
            {{ getRoleLabel(detail.userRole) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="用户等级">
          {{ detail.userLevel || 1 }}
        </el-descriptions-item>
        <el-descriptions-item label="用户经验">
          {{ detail.userExp || 0 }}
        </el-descriptions-item>
        <el-descriptions-item label="用户积分">
          {{ detail.userPoints || 0 }}
        </el-descriptions-item>
        <el-descriptions-item label="手机号">
          {{ detail.userPhone || "未设置" }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">
          {{ detail.userEmail || "未设置" }}
        </el-descriptions-item>
        <el-descriptions-item label="常驻城市">
          {{ detail.userCity || "未设置" }}
        </el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ detail.userGender || "未设置" }}
        </el-descriptions-item>
        <el-descriptions-item label="个人简介" :span="2">
          {{ detail.userProfile || "未设置" }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">
          {{
            new Date(detail.createTime).toLocaleString("zh-CN", {
              year: "numeric",
              month: "2-digit",
              day: "2-digit",
              hour: "2-digit",
              minute: "2-digit",
              second: "2-digit",
              hour12: false,
            })
          }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "UserManage",
  data() {
    return {
      // 加载状态
      loading: false,
      // 用户列表
      userList: [],
      // 总数
      total: 0,
      // 查询参数
      queryParams: {
        current: 1,
        pageSize: 10,
        userName: "",
        userPhone: "",
        userEmail: "",
        userRole: "",
        userGender: "",
      },
      // 对话框
      dialogVisible: false,
      dialogTitle: "编辑用户",
      // 详情对话框
      detailVisible: false,
      detail: {},
      // 表单数据
      form: {
        id: undefined,
        userAccount: "",
        userName: "",
        userPhone: "",
        userEmail: "",
      },
      // 表单校验规则
      rules: {
        userName: [
          { required: true, message: "请输入用户名", trigger: "blur" },
        ],
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
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 获取用户列表
    async getList() {
      this.loading = true;
      try {
        const res = await this.$request.post(
          "/api/user/list/page",
          this.queryParams
        );
        if (res.code !== 0) {
          this.$message.error(res.message || "获取用户列表失败");
          return;
        }
        this.userList = res.data.records;
        this.total = res.data.total;
      } catch (error) {
        this.$message.error("系统错误，请稍后重试");
      } finally {
        this.loading = false;
      }
    },

    // 搜索
    handleQuery() {
      this.queryParams.current = 1;
      this.getList();
    },

    // 重置
    resetQuery() {
      this.queryParams = {
        current: 1,
        pageSize: 10,
        userName: "",
        userPhone: "",
        userEmail: "",
        userRole: "",
        userGender: "",
      };
      this.handleQuery();
    },

    // 编辑用户
    handleEdit(row) {
      this.form = { ...row };
      this.dialogVisible = true;
    },

    // 提交表单
    submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          try {
            const res = await this.$request.post("/api/user/update", this.form);
            if (res.code !== 0) {
              this.$message.error(res.message || "更新失败");
              return;
            }
            this.$message.success("更新成功");
            this.getList();
            this.dialogVisible = false;
          } catch (error) {
            this.$message.error("系统错误，请稍后重试");
          }
        }
      });
    },

    // 删除用户
    handleDelete(row) {
      this.$confirm("确认删除该用户?", "提示", {
        type: "warning",
      }).then(async () => {
        try {
          const res = await this.$request.post("/api/user/delete", {
            id: row.id,
          });
          if (res.code !== 0) {
            this.$message.error(res.message || "删除失败");
            return;
          }
          this.$message.success("删除成功");
          this.getList();
        } catch (error) {
          this.$message.error("系统错误，请稍后重试");
        }
      });
    },

    // 封禁用户
    async handleBan(row) {
      try {
        const res = await this.$request.post("/api/user/ban", { id: row.id });
        if (res.code !== 0) {
          this.$message.error(res.message || "封禁失败");
          return;
        }
        this.$message.success("封禁成功");
        this.getList();
      } catch (error) {
        this.$message.error("系统错误，请稍后重试");
      }
    },

    // 解封用户
    async handleUnban(row) {
      try {
        const res = await this.$request.post("/api/user/unban", { id: row.id });
        if (res.code !== 0) {
          this.$message.error(res.message || "解封失败");
          return;
        }
        this.$message.success("解封成功");
        this.getList();
      } catch (error) {
        this.$message.error("系统错误，请稍后重试");
      }
    },

    // 分页大小改变
    handleSizeChange(val) {
      this.queryParams.pageSize = val;
      this.getList();
    },

    // 页码改变
    handleCurrentChange(val) {
      this.queryParams.current = val;
      this.getList();
    },

    // 格式化日期
    formatDate(date) {
      return new Date(date).toLocaleString();
    },

    // 获取角色标签类型
    getRoleType(role) {
      const types = {
        admin: "success",
        user: "primary",
        ban: "danger",
      };
      return types[role] || "info";
    },

    // 获取角色标签文本
    getRoleLabel(role) {
      const labels = {
        admin: "管理员",
        user: "用户",
        ban: "已封禁",
      };
      return labels[role] || role;
    },

    // 查看用户详情
    handleView(row) {
      this.detail = { ...row };
      this.detailVisible = true;
    },
  },
};
</script>

<style scoped>
.user-manage {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.delete-btn {
  color: #f56c6c;
}

:deep(.el-tag) {
  text-transform: capitalize;
}
</style>
