<template>
  <div>
    <div ref="editor"></div>
  </div>
</template>

<script>
import E from "wangeditor";

export default {
  name: "RichTextEditor",
  props: {
    value: {
      type: String,
      default: "",
    },
    visible: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      editor: null,
    };
  },
  mounted() {
    this.initEditor();
  },
  watch: {
    value: {
      handler(newVal) {
        if (this.editor && this.editor.txt.html() !== newVal) {
          this.editor.txt.html(newVal);
        }
      },
      immediate: false,
    },
    visible: {
      handler(val) {
        if (val) {
          this.$nextTick(() => {
            this.initEditor();
          });
        } else {
          if (this.editor) {
            this.editor.destroy();
            this.editor = null;
          }
        }
      },
      immediate: true,
    },
  },
  beforeDestroy() {
    if (this.editor) {
      this.editor.destroy();
    }
  },
  methods: {
    initEditor() {
      this.editor = new E(this.$refs.editor);

      // 配置编辑器
      this.editor.config.height = 400;
      this.editor.config.placeholder = "请输入内容...";

      // 配置图片上传
      this.editor.config.uploadImgServer =
        process.env.VUE_APP_BASEURL + "/api/localfiles/upload";
      this.editor.config.uploadFileName = "file";

      // 上传图片的回调
      this.editor.config.uploadImgHooks = {
        customInsert: (insertImgFn, result) => {
          if (result.code === 0) {
            insertImgFn(result.data);
          } else {
            this.$message.error("图片上传失败");
          }
        },
      };

      // 配置 onchange 回调
      this.editor.config.onchange = (html) => {
        this.$emit("input", html);
      };

      // 创建编辑器
      this.editor.create();

      // 设置初始内容
      if (this.value) {
        this.editor.txt.html(this.value);
      }
    },
    getContent() {
      return this.editor ? this.editor.txt.html() : "";
    },
    setContent(html) {
      if (this.editor) {
        this.editor.txt.html(html);
      }
    },
    clear() {
      if (this.editor) {
        this.editor.txt.clear();
      }
    },
  },
};
</script>

<style scoped>
.w-e-text-container {
  min-height: 400px !important;
}
</style>
