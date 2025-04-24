<template>
  <div class="cover-upload">
    <el-upload
      class="cover-uploader"
      :action="`${uploadUrl}/api/localfiles/upload`"
      :show-file-list="false"
      :on-success="handleSuccess"
      :before-upload="beforeUpload"
    >
      <img v-if="value" :src="value" class="cover-image" />
      <i v-else class="el-icon-plus cover-uploader-icon"></i>
    </el-upload>
  </div>
</template>

<script>
export default {
  name: "CoverUpload",
  props: {
    value: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      uploadUrl: process.env.VUE_APP_UPLOAD_URL,
    };
  },
  methods: {
    handleSuccess(res) {
      if (res.code === 0) {
        const imageUrl = res.data.startsWith("http")
          ? res.data
          : `${this.uploadUrl}${res.data}`;
        this.$emit("input", imageUrl);
        this.$message.success("上传成功");
      } else {
        this.$message.error(res.message || "上传失败");
      }
    },
    beforeUpload(file) {
      return true;
    },
  },
};
</script>

<style scoped>
.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 200px;
  height: 100px;
}

.cover-uploader:hover {
  border-color: #409eff;
}

.cover-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 200px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.cover-image {
  width: 200px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.cover-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
