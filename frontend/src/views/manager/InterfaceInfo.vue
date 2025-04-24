<template>
  <div class="interface-manage">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" ref="queryForm">
        <el-form-item label="接口名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入接口名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="接口地址">
          <el-input
            v-model="queryParams.url"
            placeholder="请输入接口地址"
            clearable
          />
        </el-form-item>
        <el-form-item label="请求方法">
          <el-select
            v-model="queryParams.method"
            placeholder="请选择请求方法"
            clearable
          >
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="接口状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="关闭" :value="0" />
            <el-option label="开启" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="接口分类">
          <el-select
            v-model="queryParams.category"
            placeholder="请选择分类"
            clearable
          >
            <el-option
              v-for="item in categoryOptions"
              :key="item.categoryName"
              :label="item.categoryName"
              :value="item.categoryName"
            />
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
      <div class="table-operations">
        <el-button type="primary" @click="handleAdd">
          <i class="el-icon-plus"></i> 新增接口
        </el-button>
      </div>

      <!-- 表格区域 -->
      <el-table
        v-loading="loading"
        :data="interfaceList"
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column
          prop="name"
          label="接口名称"
          min-width="120"
          show-overflow-tooltip
        />
        <el-table-column
          prop="url"
          label="接口地址"
          min-width="180"
          show-overflow-tooltip
        />
        <el-table-column
          prop="method"
          label="请求方法"
          width="100"
          align="center"
        >
          <template slot-scope="scope">
            <el-tag :type="getMethodType(scope.row.method)">
              {{ scope.row.method }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="接口状态"
          width="100"
          align="center"
        >
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
              :disabled="!isAdmin"
            />
          </template>
        </el-table-column>
        <el-table-column prop="category" label="接口分类" width="120" />
        <el-table-column prop="version" label="接口版本" width="100" />
        <el-table-column
          prop="callCount"
          label="调用次数"
          width="100"
          align="center"
        />
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
        <el-table-column label="操作" width="250" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleView(scope.row)">
              查看
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleEdit(scope.row)"
              v-if="isAdmin"
            >
              编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleDelete(scope.row)"
              class="delete-btn"
              v-if="isAdmin"
            >
              删除
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleOnline(scope.row)"
              v-if="isAdmin && scope.row.status === 0"
            >
              上线
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleOffline(scope.row)"
              v-if="isAdmin && scope.row.status === 1"
            >
              下线
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleTest(scope.row)"
              v-if="scope.row.status === 1"
            >
              测试
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

    <!-- 接口表单对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="700px">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="接口名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入接口名称" />
        </el-form-item>
        <el-form-item label="接口描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            rows="3"
            placeholder="请输入接口描述"
          />
        </el-form-item>
        <el-form-item label="接口地址" prop="url">
          <el-input v-model="form.url" placeholder="请输入接口地址" />
        </el-form-item>
        <el-form-item label="请求方法" prop="method">
          <el-select
            v-model="form.method"
            placeholder="请选择请求方法"
            style="width: 100%"
          >
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="接口分类" prop="category">
          <el-select
            v-model="form.category"
            placeholder="请选择接口分类"
            style="width: 100%"
          >
            <el-option
              v-for="item in categoryOptions"
              :key="item.categoryName"
              :label="item.categoryName"
              :value="item.categoryName"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="接口版本" prop="version">
          <el-input v-model="form.version" placeholder="请输入接口版本" />
        </el-form-item>
        <el-form-item label="请求头" prop="requestHeader">
          <el-input
            v-model="form.requestHeader"
            type="textarea"
            rows="3"
            placeholder="请输入请求头，格式为JSON或键值对"
          />
        </el-form-item>
        <el-form-item label="响应头" prop="responseHeader">
          <el-input
            v-model="form.responseHeader"
            type="textarea"
            rows="3"
            placeholder="请输入响应头，格式为JSON或键值对"
          />
        </el-form-item>
        <el-form-item label="请求参数" prop="requestParams">
          <el-input
            v-model="form.requestParams"
            type="textarea"
            rows="3"
            placeholder="请输入请求参数，格式为JSON或键值对"
          />
        </el-form-item>
        <el-form-item label="返回参数" prop="responseParams">
          <el-input
            v-model="form.responseParams"
            type="textarea"
            rows="3"
            placeholder="请输入返回参数，格式为JSON或键值对"
          />
        </el-form-item>
        <el-form-item label="请求示例" prop="requestExample">
          <el-input
            v-model="form.requestExample"
            type="textarea"
            rows="3"
            placeholder="请输入请求示例"
          />
        </el-form-item>
        <el-form-item label="返回示例" prop="responseExample">
          <el-input
            v-model="form.responseExample"
            type="textarea"
            rows="3"
            placeholder="请输入返回示例"
          />
        </el-form-item>
        <el-form-item label="接口可见性" prop="visibility">
          <el-radio-group v-model="form.visibility">
            <el-radio label="public">公开</el-radio>
            <el-radio label="private">私有</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="超时时间（毫秒）" prop="timeout">
          <el-input-number
            v-model="form.timeout"
            :min="0"
            :step="100"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="限流（次/分钟）" prop="rateLimit">
          <el-input-number
            v-model="form.rateLimit"
            :min="0"
            :step="10"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="Mock数据" prop="mock">
          <el-input
            v-model="form.mock"
            type="textarea"
            rows="3"
            placeholder="请输入Mock数据"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 接口详情对话框 -->
    <el-dialog title="接口详情" :visible.sync="detailVisible" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="接口名称" :span="2">
          {{ detail.name }}
        </el-descriptions-item>
        <el-descriptions-item label="接口描述" :span="2">
          {{ detail.description || "无" }}
        </el-descriptions-item>
        <el-descriptions-item label="接口地址" :span="2">
          {{ detail.url }}
        </el-descriptions-item>
        <el-descriptions-item label="请求方法">
          <el-tag :type="getMethodType(detail.method)">
            {{ detail.method }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="接口状态">
          <el-tag :type="detail.status === 1 ? 'success' : 'info'">
            {{ detail.status === 1 ? "开启" : "关闭" }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="接口分类">
          {{ detail.category || "未分类" }}
        </el-descriptions-item>
        <el-descriptions-item label="接口版本">
          {{ detail.version || "1.0" }}
        </el-descriptions-item>
        <el-descriptions-item label="接口可见性">
          {{ detail.visibility === "public" ? "公开" : "私有" }}
        </el-descriptions-item>
        <el-descriptions-item label="超时时间">
          {{ detail.timeout || 3000 }} 毫秒
        </el-descriptions-item>
        <el-descriptions-item label="限流设置">
          {{ detail.rateLimit || "无限制" }} 次/分钟
        </el-descriptions-item>
        <el-descriptions-item label="调用次数">
          {{ detail.callCount || 0 }} 次
        </el-descriptions-item>
        <el-descriptions-item label="请求头" :span="2">
          <pre>{{ detail.requestHeader || "无" }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="响应头" :span="2">
          <pre>{{ detail.responseHeader || "无" }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <pre>{{ detail.requestParams || "无" }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="返回参数" :span="2">
          <pre>{{ detail.responseParams || "无" }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="请求示例" :span="2">
          <pre>{{ detail.requestExample || "无" }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="返回示例" :span="2">
          <pre>{{ detail.responseExample || "无" }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="Mock数据" :span="2">
          <pre>{{ detail.mock || "无" }}</pre>
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

    <!-- 接口测试对话框 -->
    <el-dialog title="接口测试" :visible.sync="testVisible" width="700px">
      <div class="test-container">
        <div class="test-info">
          <div><strong>接口名称：</strong>{{ testInfo.name }}</div>
          <div><strong>请求方法：</strong>{{ testInfo.method }}</div>
          <div><strong>接口地址：</strong>{{ testInfo.url }}</div>
        </div>
        <el-divider></el-divider>
        <div class="test-form">
          <el-form ref="testForm" :model="testForm" label-width="100px">
            <el-form-item label="请求参数">
              <el-input
                v-model="testForm.params"
                type="textarea"
                rows="5"
                placeholder="请输入请求参数，格式为JSON"
              ></el-input>
            </el-form-item>
          </el-form>
          <el-button type="primary" @click="runTest" :loading="testLoading"
            >发送请求</el-button
          >
        </div>
        <el-divider v-if="testResult"></el-divider>
        <div class="test-result" v-if="testResult">
          <h3>测试结果</h3>
          <pre>{{ testResult }}</pre>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { useUserStore } from "@/stores/user";
import request from "@/utils/request";

export default {
  name: "InterfaceInfo",
  data() {
    return {
      // 查询参数
      queryParams: {
        current: 1,
        pageSize: 10,
        name: "",
        url: "",
        method: "",
        status: "",
        category: "",
      },
      // 接口列表
      interfaceList: [],
      // 分类选项
      categoryOptions: [],
      // 总记录数
      total: 0,
      // 表格加载状态
      loading: false,
      // 弹窗标题
      dialogTitle: "",
      // 弹窗是否可见
      dialogVisible: false,
      // 详情弹窗是否可见
      detailVisible: false,
      // 测试弹窗是否可见
      testVisible: false,
      // 接口表单
      form: {
        id: undefined,
        name: "",
        description: "",
        url: "",
        method: "GET",
        requestHeader: "",
        responseHeader: "",
        requestParams: "",
        responseParams: "",
        requestExample: "",
        responseExample: "",
        status: 0,
        category: "",
        version: "1.0",
        mock: "",
        timeout: 3000,
        visibility: "public",
        rateLimit: 0,
      },
      // 详情数据
      detail: {},
      // 测试信息
      testInfo: {},
      // 测试表单
      testForm: {
        params: "",
      },
      // 测试结果
      testResult: "",
      // 测试加载状态
      testLoading: false,
      // 表单规则
      rules: {
        name: [
          { required: true, message: "请输入接口名称", trigger: "blur" },
          { max: 50, message: "接口名称不能超过50个字符", trigger: "blur" },
        ],
        url: [
          { required: true, message: "请输入接口地址", trigger: "blur" },
          { max: 512, message: "接口地址不能超过512个字符", trigger: "blur" },
        ],
        method: [
          { required: true, message: "请选择请求方法", trigger: "change" },
        ],
        timeout: [
          { required: true, message: "请输入超时时间", trigger: "blur" },
        ],
      },
    };
  },
  computed: {
    userStore() {
      return useUserStore();
    },
    isAdmin() {
      return this.userStore.userInfo.userRole === "admin";
    },
  },
  created() {
    this.getList();
    this.getCategoryList();
  },
  methods: {
    // 获取接口列表
    async getList() {
      this.loading = true;
      try {
        const res = await request.post(
          "/api/interfaceInfo/list/page/vo",
          this.queryParams
        );
        this.interfaceList = res.data.records;
        this.total = res.data.total;
      } catch (error) {
        this.$message.error("获取接口列表失败");
        console.error(error);
      } finally {
        this.loading = false;
      }
    },
    // 获取接口分类列表
    async getCategoryList() {
      try {
        const res = await request.get("/api/interfaceCategory/tree");
        this.categoryOptions = this.flattenCategoryTree(res.data);
      } catch (error) {
        this.$message.error("获取接口分类失败");
        console.error(error);
      }
    },
    // 扁平化分类树，用于下拉选择
    flattenCategoryTree(tree, result = []) {
      tree.forEach((node) => {
        result.push({
          id: node.id,
          categoryName: node.categoryName,
          parentId: node.parentId,
        });
        if (node.children && node.children.length > 0) {
          this.flattenCategoryTree(node.children, result);
        }
      });
      return result;
    },
    // 获取请求方法对应的标签类型
    getMethodType(method) {
      const typeMap = {
        GET: "success",
        POST: "primary",
        PUT: "warning",
        DELETE: "danger",
      };
      return typeMap[method] || "info";
    },
    // 处理查询操作
    handleQuery() {
      this.queryParams.current = 1;
      this.getList();
    },
    // 重置查询操作
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
    // 处理分页大小变化
    handleSizeChange(size) {
      this.queryParams.pageSize = size;
      this.getList();
    },
    // 处理页码变化
    handleCurrentChange(current) {
      this.queryParams.current = current;
      this.getList();
    },
    // 处理新增操作
    handleAdd() {
      this.form = {
        id: undefined,
        name: "",
        description: "",
        url: "",
        method: "GET",
        requestHeader: "",
        responseHeader: "",
        requestParams: "",
        responseParams: "",
        requestExample: "",
        responseExample: "",
        status: 0,
        category: "",
        version: "1.0",
        mock: "",
        timeout: 3000,
        visibility: "public",
        rateLimit: 0,
      };
      this.dialogTitle = "新增接口";
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate();
      });
    },
    // 处理查看操作
    async handleView(row) {
      try {
        const res = await request.get("/api/interfaceInfo/get", {
          params: { id: row.id },
        });
        this.detail = res.data;
        this.detailVisible = true;
      } catch (error) {
        this.$message.error("获取接口详情失败");
        console.error(error);
      }
    },
    // 处理编辑操作
    async handleEdit(row) {
      try {
        const res = await request.get("/api/interfaceInfo/get", {
          params: { id: row.id },
        });
        this.form = { ...res.data };
        this.dialogTitle = "编辑接口";
        this.dialogVisible = true;
        this.$nextTick(() => {
          this.$refs.form && this.$refs.form.clearValidate();
        });
      } catch (error) {
        this.$message.error("获取接口详情失败");
        console.error(error);
      }
    },
    // 处理删除操作
    handleDelete(row) {
      this.$confirm("确认要删除该接口吗？", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          try {
            await request.post("/api/interfaceInfo/delete", { id: row.id });
            this.$message.success("删除成功");
            this.getList();
          } catch (error) {
            this.$message.error("删除失败");
            console.error(error);
          }
        })
        .catch(() => {
          this.$message.info("已取消删除");
        });
    },
    // 处理上线操作
    handleOnline(row) {
      this.$confirm("确认要上线该接口吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          try {
            await request.post("/api/interfaceInfo/online", { id: row.id });
            this.$message.success("接口已上线");
            this.getList();
          } catch (error) {
            this.$message.error("上线失败");
            console.error(error);
          }
        })
        .catch(() => {
          this.$message.info("已取消上线");
        });
    },
    // 处理下线操作
    handleOffline(row) {
      this.$confirm("确认要下线该接口吗？", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          try {
            await request.post("/api/interfaceInfo/offline", { id: row.id });
            this.$message.success("接口已下线");
            this.getList();
          } catch (error) {
            this.$message.error("下线失败");
            console.error(error);
          }
        })
        .catch(() => {
          this.$message.info("已取消下线");
        });
    },
    // 处理测试操作
    handleTest(row) {
      this.testInfo = {
        id: row.id,
        name: row.name,
        method: row.method,
        url: row.url,
      };
      this.testForm.params = row.requestExample || "";
      this.testResult = "";
      this.testVisible = true;
    },
    // 执行接口测试
    async runTest() {
      if (!this.testForm.params) {
        this.$message.warning("请输入请求参数");
        return;
      }

      this.testLoading = true;
      try {
        const res = await request.post("/api/interfaceInfo/invoke", null, {
          params: {
            id: this.testInfo.id,
            userRequestParams: this.testForm.params,
          },
        });
        this.testResult = JSON.stringify(res.data, null, 2);
      } catch (error) {
        this.testResult = JSON.stringify(
          {
            success: false,
            message: error.response?.data?.message || "请求失败",
          },
          null,
          2
        );
      } finally {
        this.testLoading = false;
      }
    },
    // 处理状态变更
    async handleStatusChange(row) {
      const status = row.status;
      try {
        if (status === 1) {
          await request.post("/api/interfaceInfo/online", { id: row.id });
          this.$message.success("接口已上线");
        } else {
          await request.post("/api/interfaceInfo/offline", { id: row.id });
          this.$message.success("接口已下线");
        }
      } catch (error) {
        // 恢复状态
        row.status = status === 1 ? 0 : 1;
        this.$message.error("状态变更失败");
        console.error(error);
      }
    },
    // 提交表单
    submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) {
          return;
        }

        try {
          if (this.form.id) {
            // 更新接口
            await request.post("/api/interfaceInfo/update", this.form);
            this.$message.success("更新成功");
          } else {
            // 新增接口
            await request.post("/api/interfaceInfo/add", this.form);
            this.$message.success("新增成功");
          }
          this.dialogVisible = false;
          this.getList();
        } catch (error) {
          this.$message.error("操作失败");
          console.error(error);
        }
      });
    },
  },
};
</script>

<style scoped>
.interface-manage {
  padding-bottom: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.table-operations {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.delete-btn {
  color: #f56c6c;
}

.test-container {
  padding: 20px;
}

.test-info {
  margin-bottom: 10px;
}

.test-form {
  margin-bottom: 20px;
}

.test-result {
  padding: 10px;
  background-color: #f8f8f8;
  border-radius: 4px;
}

pre {
  padding: 10px;
  background-color: #f8f8f8;
  border-radius: 4px;
  overflow-x: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>
