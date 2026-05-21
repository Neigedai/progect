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

        <el-tab-pane label="手机号登录" name="phone">
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
            <p style="font-size:12px;color:#909399;text-align:center">已注册手机号可直接登录，验证码5分钟有效</p>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <el-form ref="regFormRef" :model="regForm" :rules="regRules" label-width="0">
            <el-form-item prop="phone">
              <el-input v-model="regForm.phone" placeholder="手机号" />
            </el-form-item>
            <el-form-item prop="code">
              <el-input v-model="regForm.code" placeholder="验证码" style="width:65%" />
              <el-button
                style="width:33%;margin-left:2%"
                :disabled="regCountdown > 0"
                @click="handleRegSendCode"
              >{{ regCountdown > 0 ? regCountdown + 's' : '获取验证码' }}</el-button>
            </el-form-item>
            <el-form-item prop="username">
              <el-input v-model="regForm.username" placeholder="用户名（3-20位）" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="regForm.password" type="password" placeholder="密码（6-20位）" show-password />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input v-model="regForm.confirmPassword" type="password" placeholder="确认密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="regLoading" style="width:100%" @click="handleRegister">
                注册
              </el-button>
            </el-form-item>
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

const regFormRef = ref(null)
const regForm = reactive({ phone: '', code: '', username: '', password: '', confirmPassword: '' })
const regLoading = ref(false)
const regCountdown = ref(0)
let regCountdownTimer = null
const validateConfirmPass = (rule, value, callback) => {
  if (value !== regForm.password) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}
const regRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度3-20', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPass, trigger: 'blur' }
  ]
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

const handleRegSendCode = async () => {
  try { await regFormRef.value.validateField('phone') } catch { return }
  try {
    const res = await request.post('/auth/send-code', { phone: regForm.phone })
    ElMessage.success('验证码已发送，请查收手机短信')
    regCountdown.value = 60
    regCountdownTimer = setInterval(() => {
      regCountdown.value--
      if (regCountdown.value <= 0) {
        clearInterval(regCountdownTimer)
        regCountdownTimer = null
      }
    }, 1000)
  } catch {
    // error handled by interceptor
  }
}

const handleRegister = async () => {
  const valid = await regFormRef.value.validate().catch(() => false)
  if (!valid) return
  regLoading.value = true
  try {
    await request.post('/auth/register', {
      phone: regForm.phone,
      code: regForm.code,
      username: regForm.username,
      password: regForm.password
    })
    ElMessage.success('注册成功，正在登录...')
    const res = await request.post('/auth/login', {
      username: regForm.username,
      password: regForm.password
    })
    localStorage.setItem('token', res.data.token)
    router.push('/park-overview')
  } catch {
    // error handled by interceptor
  } finally { regLoading.value = false }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(160deg, #F0F4FF 0%, #E8F0FE 50%, #F8FAFC 100%);
  position: relative;
  overflow: hidden;
}
.login-container::before {
  content: '';
  position: absolute;
  width: 520px;
  height: 520px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(22, 93, 255, 0.06) 0%, transparent 70%);
  top: -100px;
  right: -100px;
}
.login-container::after {
  content: '';
  position: absolute;
  width: 320px;
  height: 320px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(22, 93, 255, 0.04) 0%, transparent 70%);
  bottom: -60px;
  left: -60px;
}
.login-card {
  width: 440px;
  border-radius: var(--radius-xl);
  box-shadow: 0 25px 50px rgba(15, 23, 42, 0.3), 0 0 0 1px rgba(255,255,255,0.05);
  border: none;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.98);
}
.login-card:hover { box-shadow: 0 30px 60px rgba(15, 23, 42, 0.35); }
.login-card :deep(.el-card__header) {
  padding: 32px 32px 0;
  border-bottom: none;
}
.login-card :deep(.el-card__body) {
  padding: 12px 32px 32px;
}
.login-card h2 {
  text-align: center;
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  font-family: 'Poppins', sans-serif;
  letter-spacing: -0.02em;
}
.login-card :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}
.login-card :deep(.el-tabs__header) {
  margin-bottom: 12px;
}
.login-card :deep(.el-tabs__item) {
  font-size: 15px;
  font-weight: 500;
}
.login-card :deep(.el-tabs__item.is-active) {
  font-weight: 600;
}
.login-card :deep(.el-input__wrapper) {
  border-radius: var(--radius-sm);
  box-shadow: 0 0 0 1px var(--border) inset;
  padding: 4px 12px;
}
.login-card :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--text-tertiary) inset;
}
.login-card :deep(.el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--primary) inset, 0 0 0 3px var(--primary-glow);
}
@media (max-width: 480px) {
  .login-card { width: 92%; margin: 20px; }
}
</style>
