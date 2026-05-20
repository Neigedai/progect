<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>园区服务运营系统</h2>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="密码登录" name="password">
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="0">
            <el-form-item prop="username">
              <el-input v-model="pwdForm.username" placeholder="用户名" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="pwdForm.password" type="password" placeholder="密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" style="width:100%" @click="handlePasswordLogin">
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="手机号登录/注册" name="phone">
          <el-form ref="phoneFormRef" :model="phoneForm" :rules="phoneRules" label-width="0">
            <el-form-item prop="phone">
              <el-input v-model="phoneForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item prop="code">
              <el-input v-model="phoneForm.code" placeholder="验证码" style="width:65%" />
              <el-button
                style="width:33%;margin-left:2%"
                :disabled="countdown > 0"
                @click="handleSendCode"
              >{{ countdown > 0 ? countdown + 's' : '获取验证码' }}</el-button>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" style="width:100%" @click="handlePhoneLogin">
                登录 / 注册
              </el-button>
            </el-form-item>
            <p style="font-size:12px;color:#909399;text-align:center">未注册手机号将自动注册，验证码5分钟有效</p>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const activeTab = ref('password')
const loading = ref(false)
const countdown = ref(0)
let countdownTimer = null

const pwdFormRef = ref(null)
const pwdForm = reactive({ username: '', password: '' })
const pwdRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const phoneFormRef = ref(null)
const phoneForm = reactive({ phone: '', code: '' })
const phoneRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const handlePasswordLogin = async () => {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await request.post('/auth/login', pwdForm)
    localStorage.setItem('token', res.data.token)
    router.push('/park-overview')
  } catch {
    // error handled by interceptor
  } finally { loading.value = false }
}

const handleSendCode = async () => {
  try { await phoneFormRef.value.validateField('phone') } catch { return }
  try {
    const res = await request.post('/auth/send-code', { phone: phoneForm.phone })
    ElMessage.success('验证码已发送，请查收手机短信')
    countdown.value = 60
    countdownTimer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }, 1000)
  } catch {
    // error handled by interceptor
  }
}

const handlePhoneLogin = async () => {
  const valid = await phoneFormRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await request.post('/auth/phone-login', phoneForm)
    localStorage.setItem('token', res.data.token)
    router.push('/park-overview')
  } catch {
    // error handled by interceptor
  } finally { loading.value = false }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  position: relative;
  overflow: hidden;
}
.login-container::before {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(22, 93, 255, 0.06);
  top: -80px;
  right: -80px;
}
.login-container::after {
  content: '';
  position: absolute;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: rgba(22, 93, 255, 0.04);
  bottom: -40px;
  left: -40px;
}
.login-card {
  width: 420px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  border: none;
  position: relative;
  z-index: 1;
  transition: box-shadow 0.3s;
}
.login-card:hover { box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12); }
.login-card :deep(.el-card__header) {
  padding: 28px 28px 0;
  border-bottom: none;
}
.login-card :deep(.el-card__body) {
  padding: 8px 28px 28px;
}
.login-card h2 {
  text-align: center;
  margin: 0;
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
}
.login-card :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}
.login-card :deep(.el-tabs__header) {
  margin-bottom: 8px;
}
.login-card :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--border) inset;
}
.login-card :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--primary-light) inset;
}
@media (max-width: 480px) {
  .login-card { width: 92%; margin: 20px; }
}
</style>
