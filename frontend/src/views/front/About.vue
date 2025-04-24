<template>
  <div class="about-container">
    <div class="about-content">
      <h1>关于我们</h1>
      <div class="section">
        <h2>平台介绍</h2>
        <p>
          我们是一个致力于为用户提供优质服务的综合平台。通过不断创新和改进，
          我们希望为每一位用户带来更好的使用体验。
        </p>
      </div>

      <div class="section">
        <h2>联系方式</h2>
        <ul>
          <li><i class="el-icon-location"></i> 地址：xx省xx市xx区</li>
          <li><i class="el-icon-phone"></i> 电话：400-xxx-xxxx</li>
          <li><i class="el-icon-message"></i> 邮箱：contact@xxx.com</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "About",
};
</script>

<style scoped>
.about-container {
  padding: 20px;
}

.about-content {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
}

.section {
  margin-bottom: 30px;
}

h2 {
  color: #409eff;
  margin-bottom: 15px;
  font-size: 20px;
}

p {
  line-height: 1.6;
  color: #666;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  margin: 10px 0;
  color: #666;
}

i {
  margin-right: 10px;
  color: #409eff;
}
</style>
