<template>
  <div class="apply-placeholder">
    <h2>申请办理</h2>
    <el-empty description="申请表单页面开发中，敬请期待" />
    <div class="back-btn">
      <el-button @click="$router.back()">返回上页</el-button>
    </div>
  </div>
</template>

<style scoped>
.apply-placeholder {
  max-width: 600px;
  margin: 80px auto;
  text-align: center;
}
.apply-placeholder h2 {
  margin-bottom: 20px;
}
.back-btn {
  margin-top: 20px;
}
</style>
