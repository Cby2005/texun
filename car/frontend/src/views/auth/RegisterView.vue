<template>
  <main class="register-page">
    <section class="register-map" aria-hidden="true">
      <div class="map-grid"></div>
      <div class="signal-card signal-card-a">
        <strong>Farm</strong>
        <span>农田地块数据</span>
      </div>
      <div class="signal-card signal-card-b">
        <strong>Vehicle</strong>
        <span>无人车设备管理</span>
      </div>
      <div class="signal-card signal-card-c">
        <strong>Simulator</strong>
        <span>模拟器控制台</span>
      </div>
    </section>

    <section class="register-panel">
      <div class="register-header">
        <div class="brand-mark">
          <el-icon><Van /></el-icon>
        </div>
        <p>车端管理系统</p>
        <h1>注册账号</h1>
        <span>账号将写入后端认证库，用于访问无人车管理系统。</span>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" class="register-form" @keyup.enter="handleRegister">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名，4-32 位" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" placeholder="昵称" prefix-icon="Crop" size="large" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" prefix-icon="Phone" size="large" />
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮箱，可选" prefix-icon="Message" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码，6-32 位"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" class="register-button" @click="handleRegister">
            创建账号
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <span>已有账号？</span>
        <button type="button" @click="router.push('/login')">返回登录</button>
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Van } from '@element-plus/icons-vue'
import { register } from '@/api/auth'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
})

const validateConfirmPassword = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 32, message: '用户名长度为 4-32 位', trigger: 'blur' },
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度为 6-32 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e: any) {
    ElMessage.error(e.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.register-page {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 460px;
  min-height: 100vh;
  overflow: hidden;
  background:
    linear-gradient(90deg, rgba(47, 125, 72, 0.09) 1px, transparent 1px),
    linear-gradient(180deg, rgba(47, 125, 72, 0.08) 1px, transparent 1px),
    #edf4e9;
  background-size: 52px 52px;
}

.register-map {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background:
    radial-gradient(circle at 22% 18%, rgba(184, 223, 115, 0.44), transparent 28%),
    linear-gradient(135deg, #dcebcf 0%, #edf4e9 46%, #b9d69f 100%);
}

.map-grid {
  position: absolute;
  inset: 8% 8% 11%;
  background:
    linear-gradient(90deg, rgba(23, 74, 50, 0.18) 1px, transparent 1px),
    linear-gradient(180deg, rgba(23, 74, 50, 0.18) 1px, transparent 1px);
  background-size: 54px 54px;
  border: 1px solid rgba(23, 74, 50, 0.16);
  border-radius: 8px;
  transform: perspective(700px) rotateX(58deg) rotateZ(-7deg);
  transform-origin: center;
}

.signal-card {
  position: absolute;
  display: grid;
  gap: 4px;
  min-width: 170px;
  padding: 15px;
  color: #eef8e9;
  background: rgba(19, 33, 27, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 8px;
  box-shadow: 0 22px 50px rgba(19, 33, 27, 0.18);
}

.signal-card strong {
  color: #b8df73;
  font-size: 18px;
}

.signal-card span {
  color: rgba(238, 248, 233, 0.68);
  font-size: 12px;
}

.signal-card-a { top: 19%; left: 20%; transform: rotate(-3deg); }
.signal-card-b { top: 44%; left: 25%; transform: rotate(2deg); }
.signal-card-c { top: 66%; left: 18%; transform: rotate(-1deg); }

.register-panel {
  position: relative;
  z-index: 1;
  align-self: center;
  width: min(460px, calc(100vw - 32px));
  margin-right: clamp(24px, 6vw, 84px);
  padding: 34px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(47, 125, 72, 0.18);
  border-radius: 8px;
  box-shadow: 0 28px 70px rgba(28, 57, 42, 0.16);
  backdrop-filter: blur(18px);
}

.register-header {
  margin-bottom: 26px;
}

.register-header .brand-mark {
  width: 48px; height: 48px;
  border-radius: 14px;
  background: var(--agri-green);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 14px;
}

.register-header p {
  font-size: 13px;
  color: var(--agri-muted);
  margin-bottom: 4px;
}

.register-header h1 {
  font-size: 26px;
  font-weight: 700;
  color: var(--agri-ink);
  letter-spacing: 0.03em;
  margin-bottom: 8px;
}

.register-header span {
  font-size: 13px;
  color: var(--agri-muted);
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

.register-footer {
  display: flex;
  align-items: center;
  gap: 6px;
  justify-content: center;
  font-size: 13px;
  color: var(--agri-muted);
}

.register-footer button {
  background: none;
  border: none;
  color: var(--agri-green);
  cursor: pointer;
  font-weight: 600;
  text-decoration: underline;
}
</style>
