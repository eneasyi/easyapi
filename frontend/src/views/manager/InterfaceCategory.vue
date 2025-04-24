<template>
  <div class="category-manage">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" ref="queryForm">
        <el-form-item label="分类名称">
          <el-input
            v-model="queryParams.categoryName"
            placeholder="请输入分类名称"
            clearable
          />
        </el-form-item>
        <el-form-item label="父级分类">
          <el-select
            v-model="queryParams.parentId"
            placeholder="请选择父级分类"
            clearable
          >
            <el-option label="无父级分类" :value="0" />
            <el-option
              v-for="item in topCategories"
              :key="item.id"
              :label="item.categoryName"
              :value="item.id"
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

    <!-- 左侧分类树和右侧表格 -->
    <el-row :gutter="20">
      <!-- 左侧分类树 -->
      <el-col :span="6">
        <el-card class="tree-card">
          <div slot="header" class="tree-header">
            <span>分类树结构</span>
            <div class="tree-operations">
              <el-button size="mini" type="primary" @click="handleAddRoot">
                <i class="el-icon-plus"></i>
                新增根分类
              </el-button>
              <el-button size="mini" @click="getCategoryTree">
                <i class="el-icon-refresh"></i>
              </el-button>
            </div>
          </div>
          <div v-loading="treeLoading">
            <el-tree
              ref="categoryTree"
              :data="categoryTree"
              node-key="id"
              :props="defaultProps"
              default-expand-all
              highlight-current
              @node-click="handleNodeClick"
            >
              <span class="custom-tree-node" slot-scope="{ node, data }">
                <span>{{ node.label }}</span>
                <span class="tree-node-operations">
                  <el-button
                    type="text"
                    size="mini"
                    @click.stop="handleAddChild(data)"
                  >
                    添加子分类
                  </el-button>
                </span>
              </span>
            </el-tree>
            <div v-if="categoryTree.length === 0" class="empty-tree">
              暂无分类数据
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧分类表格 -->
      <el-col :span="18">
        <el-card class="table-card">
          <div class="table-operations">
            <el-button
              type="primary"
              @click="handleAdd"
              v-if="!selectedCategory"
            >
              <i class="el-icon-plus"></i> 新增分类
            </el-button>
            <el-button
              type="primary"
              @click="handleAddChild(selectedCategory)"
              v-if="selectedCategory"
            >
              <i class="el-icon-plus"></i> 添加子分类
            </el-button>
          </div>

          <!-- 表格区域 -->
          <el-table
            v-loading="loading"
            :data="categoryList"
            border
            style="width: 100%"
          >
            <el-table-column
              type="index"
              label="序号"
              width="60"
              align="center"
            />
            <el-table-column
              prop="categoryName"
              label="分类名称"
              min-width="120"
            />
            <el-table-column
              prop="categoryDescription"
              label="分类描述"
              min-width="150"
              show-overflow-tooltip
            />
            <el-table-column
              prop="categoryOrder"
              label="排序"
              width="80"
              align="center"
            />
            <el-table-column label="父级分类" min-width="120">
              <template slot-scope="scope">
                {{
                  scope.row.parentId
                    ? getCategoryNameById(scope.row.parentId)
                    : "无"
                }}
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
                <el-button
                  size="mini"
                  type="text"
                  @click="handleEdit(scope.row)"
                >
                  编辑
                </el-button>
                <el-button
                  size="mini"
                  type="text"
                  @click="handleDelete(scope.row)"
                  class="delete-btn"
                >
                  删除
                </el-button>
                <el-button
                  size="mini"
                  type="text"
                  @click="handleAddChild(scope.row)"
                >
                  添加子分类
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
      </el-col>
    </el-row>

    <!-- 分类表单对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类描述" prop="categoryDescription">
          <el-input
            v-model="form.categoryDescription"
            type="textarea"
            rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
        <el-form-item label="排序" prop="categoryOrder">
          <el-input-number
            v-model="form.categoryOrder"
            :min="0"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="父级分类" prop="parentId">
          <el-select
            v-model="form.parentId"
            placeholder="请选择父级分类"
            style="width: 100%"
          >
            <el-option label="无父级分类" :value="0" />
            <el-option
              v-for="item in allCategories"
              :key="item.id"
              :label="item.categoryName"
              :value="item.id"
              :disabled="item.id === form.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { useUserStore } from "@/stores/user";
import request from "@/utils/request";

export default {
  name: "InterfaceCategory",
  data() {
    return {
      // 查询参数
      queryParams: {
        current: 1,
        pageSize: 10,
        categoryName: "",
        parentId: undefined,
      },
      // 分类列表
      categoryList: [],
      // 分类树
      categoryTree: [],
      // 顶级分类
      topCategories: [],
      // 所有分类（扁平结构，用于选择父级）
      allCategories: [],
      // 当前选中的分类
      selectedCategory: null,
      // 总记录数
      total: 0,
      // 表格加载状态
      loading: false,
      // 树加载状态
      treeLoading: false,
      // 弹窗标题
      dialogTitle: "",
      // 弹窗是否可见
      dialogVisible: false,
      // 分类表单
      form: {
        id: undefined,
        categoryName: "",
        categoryDescription: "",
        categoryOrder: 0,
        parentId: 0,
      },
      // 表单规则
      rules: {
        categoryName: [
          { required: true, message: "请输入分类名称", trigger: "blur" },
          { max: 50, message: "分类名称不能超过50个字符", trigger: "blur" },
        ],
        categoryDescription: [
          { max: 512, message: "分类描述不能超过512个字符", trigger: "blur" },
        ],
        categoryOrder: [
          { required: true, message: "请输入排序", trigger: "blur" },
        ],
      },
      // 树属性定义
      defaultProps: {
        children: "children",
        label: "categoryName",
      },
    };
  },
  computed: {
    userStore() {
      return useUserStore();
    },
  },
  created() {
    this.getList();
    this.getCategoryTree();
  },
  methods: {
    // 获取分类列表
    async getList() {
      this.loading = true;
      try {
        const res = await request.post(
          "/api/interfaceCategory/list/page/vo",
          this.queryParams
        );
        this.categoryList = res.data.records;
        this.total = res.data.total;
      } catch (error) {
        this.$message.error("获取分类列表失败");
        console.error(error);
      } finally {
        this.loading = false;
      }
    },
    // 获取分类树
    async getCategoryTree() {
      this.treeLoading = true;
      try {
        const res = await request.get("/api/interfaceCategory/tree");
        this.categoryTree = res.data;
        this.allCategories = this.flattenCategoryTree(res.data);
        this.topCategories = res.data.map((item) => ({
          id: item.id,
          categoryName: item.categoryName,
        }));
      } catch (error) {
        this.$message.error("获取分类树失败");
        console.error(error);
      } finally {
        this.treeLoading = false;
      }
    },
    // 扁平化分类树
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
    // 根据ID获取分类名称
    getCategoryNameById(id) {
      const category = this.allCategories.find((item) => item.id === id);
      return category ? category.categoryName : "";
    },
    // 处理树节点点击
    handleNodeClick(data) {
      this.selectedCategory = data;
      this.queryParams.parentId = data.id;
      this.getList();
    },
    // 处理查询操作
    handleQuery() {
      this.queryParams.current = 1;
      this.getList();
    },
    // 重置查询操作
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.selectedCategory = null;
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
    // 处理新增根分类
    handleAddRoot() {
      this.form = {
        id: undefined,
        categoryName: "",
        categoryDescription: "",
        categoryOrder: 0,
        parentId: 0,
      };
      this.dialogTitle = "新增根分类";
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate();
      });
    },
    // 处理新增操作
    handleAdd() {
      this.form = {
        id: undefined,
        categoryName: "",
        categoryDescription: "",
        categoryOrder: 0,
        parentId: this.selectedCategory ? this.selectedCategory.id : 0,
      };
      this.dialogTitle = "新增分类";
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate();
      });
    },
    // 处理添加子分类
    handleAddChild(data) {
      this.form = {
        id: undefined,
        categoryName: "",
        categoryDescription: "",
        categoryOrder: 0,
        parentId: data.id,
      };
      this.dialogTitle = `添加"${data.categoryName}"的子分类`;
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate();
      });
    },
    // 处理编辑操作
    async handleEdit(row) {
      try {
        const res = await request.get("/api/interfaceCategory/get", {
          params: { id: row.id },
        });
        this.form = { ...res.data };
        // 处理parentId为null的情况
        if (!this.form.parentId) {
          this.form.parentId = 0;
        }
        this.dialogTitle = "编辑分类";
        this.dialogVisible = true;
        this.$nextTick(() => {
          this.$refs.form && this.$refs.form.clearValidate();
        });
      } catch (error) {
        this.$message.error("获取分类详情失败");
        console.error(error);
      }
    },
    // 处理删除操作
    handleDelete(row) {
      this.$confirm("确认要删除该分类吗？删除后将无法恢复！", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(async () => {
          try {
            await request.post("/api/interfaceCategory/delete", { id: row.id });
            this.$message.success("删除成功");
            this.getList();
            this.getCategoryTree();
          } catch (error) {
            this.$message.error("删除失败");
            console.error(error);
          }
        })
        .catch(() => {
          this.$message.info("已取消删除");
        });
    },
    // 提交表单
    submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) {
          return;
        }

        try {
          if (this.form.id) {
            // 更新分类
            await request.post("/api/interfaceCategory/update", this.form);
            this.$message.success("更新成功");
          } else {
            // 新增分类
            await request.post("/api/interfaceCategory/add", this.form);
            this.$message.success("新增成功");
          }
          this.dialogVisible = false;
          this.getList();
          this.getCategoryTree();
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
.category-manage {
  padding-bottom: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.tree-card {
  margin-bottom: 20px;
  height: calc(100vh - 260px);
  overflow-y: auto;
}

.table-card {
  margin-bottom: 20px;
}

.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tree-operations {
  display: flex;
  gap: 10px;
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

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.tree-node-operations {
  display: none;
}

.el-tree-node:hover .tree-node-operations {
  display: inline-block;
}

.empty-tree {
  text-align: center;
  color: #909399;
  padding: 20px 0;
}
</style>
