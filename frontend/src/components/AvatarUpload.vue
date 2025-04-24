<template>
  <div class="avatar-wrapper">
    <img :src="avatarUrl" alt="用户头像" class="avatar" />
    <el-upload
      class="avatar-uploader"
      :action="`${uploadUrl}/api/localfiles/upload`"
      :show-file-list="false"
      :on-success="handleAvatarSuccess"
      :on-error="handleAvatarError"
    >
      <div class="upload-overlay">
        <i class="el-icon-camera"></i>
        <span>更换头像</span>
      </div>
    </el-upload>
  </div>
</template>

<script>
export default {
  name: "AvatarUpload",
  props: {
    value: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      uploadUrl: process.env.VUE_APP_UPLOAD_URL,
      localAvatar: this.value,
    };
  },
  computed: {
    avatarUrl() {
      return this.localAvatar || require("@/assets/imgs/default-avatar.jpeg");
    },
  },
  watch: {
    value(newVal) {
      this.localAvatar = newVal;
    },
  },
  methods: {
    handleAvatarSuccess(res) {
      if (res.code === 0) {
        this.localAvatar = res.data;
        this.$emit("input", this.localAvatar);
        this.$emit("success", res);
      } else {
        this.$message.error(res.message || "上传失败");
      }
    },
    handleAvatarError() {
      this.$message.error("上传失败，请重试");
    },
  },
};
</script>

<style scoped>
.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 8px 0;
  text-align: center;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.3s;
  cursor: pointer;
}

.avatar-wrapper:hover .upload-overlay {
  opacity: 1;
}

.upload-overlay i {
  display: block;
  font-size: 18px;
  margin-bottom: 2px;
}
</style>
