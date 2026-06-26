<template>
  <main class="login-page">
    <section class="field-signal" aria-hidden="true">
      <div class="sun-strip"></div>
      <div class="field-lines">
        <span v-for="line in 10" :key="line"></span>
      </div>
      <div class="machine-card">
        <el-icon><Crop /></el-icon>
        <strong>AGV-07</strong>
        <span>巡田中</span>
      </div>
    </section>

    <section class="login-panel">
      <div class="login-header">
        <div class="brand-mark">
          <el-icon><Van /></el-icon>
        </div>
        <p>智慧农场无人驾驶车端管理系统</p>
        <h1>车端管理</h1>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" class="login-form" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" class="login-button" @click="handleLogin">
            登录车端系统
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span>没有账号？</span>
        <button type="button" @click="router.push('/register')">注册账号</button>
      </div>
      <div class="login-hint">
        <span>默认账号</span>
        <strong>admin / admin123</strong>
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
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: 'admin123',
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/vehicle')
  } catch (e: any) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-page {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 430px;
  min-height: 100vh;
  overflow: hidden;
  background:
    linear-gradient(90deg, rgba(47, 125, 72, 0.09) 1px, transparent 1px),
    linear-gradient(180deg, rgba(47, 125, 72, 0.08) 1px, transparent 1px),
    #edf4e9;
  background-size: 52px 52px;
}

.field-signal {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.5), transparent 42%),
    linear-gradient(135deg, #dcebcf 0%, #edf4e9 46%, #c7deb4 100%);
}

.sun-strip {
  position: absolute;
  top: 8%;
  right: 11%;
  width: 26vw;
  max-width: 360px;
  height: 26vw;
  max-height: 360px;
  background:
    repeating-linear-gradient(90deg, rgba(217, 145, 31, 0.24) 0 10px, transparent 10px 22px),
    #f2c35e;
  border-radius: 50%;
  opacity: 0.8;
}

.field-lines {
  position: absolute;
  inset: auto -8% -10% -8%;
  height: 58%;
  transform: skewY(-8deg);
  transform-origin: bottom left;
}

.field-lines span {
  display: block;
  height: 9%;
  margin-bottom: 1.4%;
  background: linear-gradient(90deg, #174a32, #2f7d48 46%, #9fcf5f);
  border-top: 1px solid rgba(255, 255, 255, 0.42);
  box-shadow: 0 -14px 0 rgba(255, 255, 255, 0.12) inset;
}

.machine-card {
  position: absolute;
  left: 9%;
  bottom: 19%;
  display: grid;
  grid-template-columns: auto auto;
  gap: 3px 10px;
  align-items: center;
  width: 190px;
  padding: 16px;
  color: #eef8e9;
  background: rgba(19, 33, 27, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 8px;
  box-shadow: 0 22px 50px rgba(19, 33, 27, 0.22);
}

.machine-card .el-icon {
  grid-row: span 2;
  color: #b8df73;
  font-size: 30px;
}

.machine-card strong {
  font-size: 18px;
}

.machine-card span {
  color: rgba(238, 248, 233, 0.68);
  font-size: 12px;
}

.login-panel {
  position: relative;
  z-index: 1;
  align-self: center;
  width: min(430px, calc(100vw - 32px));
  margin-right: clamp(24px, 6vw, 84px);
  padding: 34px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(47, 125, 72, 0.18);
  border-radius: 8px;
  box-shadow: 0 28px 70px rgba(28, 57, 42, 0.16);
  backdrop-filter: blur(18px);
}

.login-header {
  margin-bottom: 30px;
}

.login-header .brand-mark {
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

.login-header p {
  font-size: 13px;
  color: var(--agri-muted);
  margin-bottom: 4px;
}

.login-header h1 {
  font-size: 26px;
  font-weight: 700;
  color: var(--agri-ink);
  letter-spacing: 0.03em;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

.login-footer {
  display: flex;
  align-items: center;
  gap: 6px;
  justify-content: center;
  font-size: 13px;
  color: var(--agri-muted);
}

.login-footer button {
  background: none;
  border: none;
  color: var(--agri-green);
  cursor: pointer;
  font-weight: 600;
  text-decoration: underline;
}

.login-hint {
  display: flex;
  gap: 8px;
  justify-content: center;
  margin-top: 14px;
  padding: 10px;
  background: rgba(47, 125, 72, 0.06);
  border: 1px solid rgba(47, 125, 72, 0.12);
  border-radius: 6px;
  font-size: 13px;
  color: var(--agri-muted);
}

.login-hint strong {
  color: var(--agri-green);
  font-family: "SF Mono", "Fira Code", monospace;
}
</style>
