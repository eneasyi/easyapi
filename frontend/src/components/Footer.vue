<template>
  <div class="footer">
    <div class="footer-content">
      <div class="footer-section">
        <h4>关于我们</h4>
        <ul>
          <li><a href="/about">公司介绍</a></li>
          <li><a href="/contact">联系我们</a></li>
          <li><a href="/join">加入我们</a></li>
        </ul>
      </div>
      <div class="footer-section">
        <h4>帮助中心</h4>
        <ul>
          <li><a href="/help">使用指南</a></li>
          <li><a href="/faq">常见问题</a></li>
          <li><a href="/feedback">意见反馈</a></li>
        </ul>
      </div>
      <div class="footer-section">
        <h4>服务支持</h4>
        <ul>
          <li><a href="/service">服务条款</a></li>
          <li><a href="/privacy">隐私政策</a></li>
          <li><a href="/copyright">版权声明</a></li>
        </ul>
      </div>
      <div class="footer-section contact-info">
        <h4>联系方式</h4>
        <p><i class="el-icon-phone"></i> 服务热线：400-xxx-xxxx</p>
        <p><i class="el-icon-message"></i> 邮箱：support@xxx.com</p>
        <p><i class="el-icon-location-information"></i> 地址：xx省xx市xx区</p>
      </div>
    </div>
    <div class="footer-bottom">
      <p>Copyright © {{ currentYear }} xxx公司 版权所有</p>
      <p>
        <a href="https://beian.miit.gov.cn/" target="_blank">
          ICP备案号：xxx
        </a>
      </p>
    </div>
  </div>
</template>

<script>
export default {
  name: "Footer",
  computed: {
    currentYear() {
      return new Date().getFullYear();
    },
  },
};
</script>

<style scoped>
.footer {
  background-color: #f5f5f5;
  padding: 40px 0 20px;
  margin-top: 50px;
  color: #666;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  padding: 0 20px;
}

.footer-section {
  flex: 1;
  margin-right: 40px;
}

.footer-section:last-child {
  margin-right: 0;
}

.footer-section h4 {
  font-size: 16px;
  color: #333;
  margin-bottom: 20px;
  font-weight: bold;
}

.footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-section ul li {
  margin-bottom: 12px;
}

.footer-section ul li a {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.footer-section ul li a:hover {
  color: #409eff;
}

.contact-info p {
  margin: 10px 0;
  font-size: 14px;
}

.contact-info i {
  margin-right: 8px;
  color: #409eff;
}

.footer-bottom {
  max-width: 1200px;
  margin: 30px auto 0;
  padding: 20px 20px 0;
  border-top: 1px solid #e0e0e0;
  text-align: center;
  font-size: 12px;
}

.footer-bottom p {
  margin: 5px 0;
}

.footer-bottom a {
  color: #666;
  text-decoration: none;
}

.footer-bottom a:hover {
  color: #409eff;
}

@media screen and (max-width: 768px) {
  .footer-content {
    flex-direction: column;
  }

  .footer-section {
    margin-right: 0;
    margin-bottom: 30px;
  }

  .footer-section:last-child {
    margin-bottom: 0;
  }
}
</style>
