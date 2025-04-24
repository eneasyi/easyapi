<template>
  <div class="address-list">
    <el-button type="primary" size="small" @click="showAddressDialog('add')">
      <i class="el-icon-plus"></i> 新增地址
    </el-button>
    <div class="address-cards">
      <div v-if="addressList.length === 0" class="empty-state">
        <i class="el-icon-location-information"></i>
        <p>暂无收货地址，请添加</p>
      </div>
      <div
        v-for="address in addressList"
        :key="address.id"
        :class="['address-card', { 'is-default': address.isDefault === 1 }]"
      >
        <div class="card-header">
          <span class="name">{{ address.name }}</span>
          <span class="phone">{{ address.phone }}</span>
          <el-tag v-if="address.isDefault === 1" type="success" size="small"
            >默认地址</el-tag
          >
        </div>
        <div class="address-detail">
          {{ address.province }}{{ address.city }}{{ address.district
          }}{{ address.detail }}
        </div>
        <div class="card-footer">
          <el-button type="text" @click="showAddressDialog('edit', address)">
            <i class="el-icon-edit"></i> 编辑
          </el-button>
          <el-button
            type="text"
            @click="setDefaultAddress(address.id)"
            v-if="address.isDefault !== 1"
          >
            <i class="el-icon-star-off"></i> 设为默认
          </el-button>
          <el-button
            type="text"
            class="delete-btn"
            @click="deleteAddress(address.id)"
          >
            <i class="el-icon-delete"></i> 删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- 地址编辑对话框 -->
    <el-dialog
      :title="addressDialogType === 'add' ? '新增地址' : '编辑地址'"
      :visible.sync="addressDialogVisible"
      width="500px"
    >
      <el-form
        ref="addressFormRef"
        :model="addressForm"
        :rules="addressRules"
        label-width="100px"
      >
        <el-form-item label="收货人" prop="name">
          <el-input v-model="addressForm.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addressForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所在地区" required>
          <el-row :gutter="10">
            <el-col :span="8">
              <el-form-item prop="province">
                <el-input v-model="addressForm.province" placeholder="省" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="city">
                <el-input v-model="addressForm.city" placeholder="市" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item prop="district">
                <el-input v-model="addressForm.district" placeholder="区" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input
            type="textarea"
            v-model="addressForm.detail"
            placeholder="请输入详细地址"
          />
        </el-form-item>
        <el-form-item>
          <el-checkbox
            v-model="addressForm.isDefault"
            :true-label="1"
            :false-label="0"
          >
            设为默认地址
          </el-checkbox>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="addressDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitAddress">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "AddressManage",
  data() {
    // 手机号验证规则
    const validatePhone = (rule, value, callback) => {
      if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error("请输入正确的手机号"));
      } else {
        callback();
      }
    };

    return {
      addressList: [],
      addressDialogVisible: false,
      addressDialogType: "add",
      addressForm: {
        id: undefined,
        name: "",
        phone: "",
        province: "",
        city: "",
        district: "",
        detail: "",
        isDefault: 0,
      },
      addressRules: {
        name: [
          { required: true, message: "请输入收货人姓名", trigger: "blur" },
          { max: 20, message: "姓名长度不能超过20个字符", trigger: "blur" },
        ],
        phone: [
          { required: true, message: "请输入手机号", trigger: "blur" },
          { validator: validatePhone, trigger: "blur" },
        ],
        province: [{ required: true, message: "请输入省份", trigger: "blur" }],
        city: [{ required: true, message: "请输入城市", trigger: "blur" }],
        district: [{ required: true, message: "请输入区/县", trigger: "blur" }],
        detail: [
          { required: true, message: "请输入详细地址", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getAddressList();
  },
  methods: {
    showAddressDialog(type, address) {
      this.addressDialogType = type;
      this.addressDialogVisible = true;
      if (type === "edit" && address) {
        this.addressForm = { ...address };
      } else {
        this.addressForm = {
          name: "",
          phone: "",
          province: "",
          city: "",
          district: "",
          detail: "",
          isDefault: 0,
        };
      }
    },
    async submitAddress() {
      this.$refs.addressFormRef.validate(async (valid) => {
        if (valid) {
          try {
            const url =
              this.addressDialogType === "add"
                ? "/api/address/add"
                : "/api/address/update";
            const res = await this.$request.post(url, this.addressForm);
            if (res.code === 0) {
              this.$message.success(
                this.addressDialogType === "add" ? "添加成功" : "修改成功"
              );
              this.addressDialogVisible = false;
              this.getAddressList();
            } else {
              this.$message.error(res.message || "操作失败");
            }
          } catch (error) {
            this.$message.error("操作失败");
          }
        }
      });
    },
    async deleteAddress(id) {
      try {
        await this.$confirm("确认删除该地址吗？", "提示", { type: "warning" });
        const res = await this.$request.post("/api/address/delete", { id });
        if (res.code === 0) {
          this.$message.success("删除成功");
          this.getAddressList();
        } else {
          this.$message.error(res.message || "删除失败");
        }
      } catch (error) {
        if (error !== "cancel") {
          this.$message.error("删除失败");
        }
      }
    },
    async setDefaultAddress(id) {
      try {
        const res = await this.$request.post(
          `/api/address/set/default?id=${id}`
        );
        if (res.code === 0) {
          this.$message.success("设置成功");
          this.getAddressList();
        } else {
          this.$message.error(res.message || "设置失败");
        }
      } catch (error) {
        this.$message.error("设置失败");
      }
    },
    async getAddressList() {
      try {
        const res = await this.$request.post("/api/address/my/list/page", {
          current: 1,
          pageSize: 20,
        });
        if (res.code === 0) {
          this.addressList = res.data.records;
        } else {
          this.$message.error(res.message || "获取地址列表失败");
        }
      } catch (error) {
        this.$message.error("获取地址列表失败");
      }
    },
  },
};
</script>

<style scoped>
.address-list {
  margin-top: 20px;
}

.address-cards {
  margin-top: 20px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.empty-state i {
  font-size: 48px;
  color: #909399;
  margin-bottom: 16px;
}

.empty-state p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.address-card {
  position: relative;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  background-color: #fff;
  transition: all 0.3s;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.address-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.is-default {
  border: 2px solid #67c23a;
  background-color: #f0f9eb;
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
}

.name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.phone {
  color: #606266;
}

.address-detail {
  color: #606266;
  line-height: 1.5;
  margin-bottom: 16px;
  word-break: break-all;
}

.card-footer {
  display: flex;
  gap: 16px;
  border-top: 1px solid #ebeef5;
  padding-top: 12px;
  margin-top: 12px;
}

.delete-btn {
  color: #f56c6c;
}

.delete-btn:hover {
  color: #f78989;
}

@media screen and (max-width: 768px) {
  .address-cards {
    grid-template-columns: 1fr;
  }
}
</style>
