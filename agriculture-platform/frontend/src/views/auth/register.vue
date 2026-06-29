<template>
  <div class="register-page">
    <canvas ref="particleCanvas" class="particle-canvas" aria-hidden="true"></canvas>
    <div class="register-card">
      <!-- 左侧注册表单区 -->
      <div class="register-left">
        <div class="brand">
          <svg width="48" height="48" viewBox="0 0 40 40">
            <rect width="40" height="40" rx="10" fill="#2E7D32"/>
            <path d="M20 8C20 8 12 14 12 22s8 12 8 12 8-4 8-12-8-14-8-14z" fill="#66BB6A"/>
            <circle cx="20" cy="20" r="3" fill="white"/>
          </svg>
          <div>
            <h1>智慧农业平台</h1>
            <p>Smart Agriculture Platform</p>
          </div>
        </div>
        <h2>注册账号</h2>
        <p class="sub">填写以下信息，快速加入智慧农业综合服务平台</p>
        <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleRegister">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input v-model="form.phone" placeholder="手机号" :prefix-icon="Iphone" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item prop="confirmPwd">
            <el-input v-model="form.confirmPwd" type="password" placeholder="确认密码" :prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item prop="userType">
            <el-select v-model="form.userType" placeholder="用户类型" style="width:100%">
              <template #prefix>
                <el-icon><UserFilled /></el-icon>
              </template>
              <el-option :value="4" label="农户" />
              <el-option :value="2" label="溯源企业" />
              <el-option :value="5" label="消费者" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" native-type="submit" :loading="loading" style="width:100%">注册</el-button>
          </el-form-item>
        </el-form>
        <p class="login-link">已有账号？<router-link to="/login">立即登录</router-link></p>
      </div>

      <!-- 右侧品牌展示区 -->
      <div class="register-right">
        <h2>智慧农业综合服务平台</h2>
        <p>集成温室草莓管理、无人机巡检、农产品溯源、农业技术推广等核心功能，助力农业数字化转型。</p>
        <div class="features">
          <div class="feat">
            <el-icon :size="30"><Odometer /></el-icon>
            <span>温室管理</span>
          </div>
          <div class="feat">
            <el-icon :size="30"><Goods /></el-icon>
            <span>产品溯源</span>
          </div>
          <div class="feat">
            <el-icon :size="30"><Reading /></el-icon>
            <span>农技推广</span>
          </div>
          <div class="feat">
            <el-icon :size="30"><Promotion /></el-icon>
            <span>无人机巡检</span>
          </div>
          <div class="feat">
            <el-icon :size="30"><TrendCharts /></el-icon>
            <span>市场行情</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../../api/system'

const router = useRouter()
const loading = ref(false)
const formRef = ref(null)

const form = reactive({ username: '', password: '', confirmPwd: '', phone: '', userType: 4 })

const validateConfirm = (rule, value, callback) => {
  callback(value !== form.password ? new Error('两次密码不一致') : undefined)
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名3-20位', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPwd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ],
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await register({ username: form.username, password: form.password, phone: form.phone, userType: form.userType })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    ElMessage.error('注册失败')
  } finally {
    loading.value = false
  }
}

// ==================== 粒子背景 (与登录页相同) ====================
const particleCanvas = ref(null)
let animationId
let resizeParticles

function startParticles() {
  const canvas = particleCanvas.value
  const context = canvas?.getContext('2d')
  if (!context) return
  const particles = []
  let width = 0
  let height = 0

  resizeParticles = () => {
    width = canvas.clientWidth
    height = canvas.clientHeight
    const ratio = Math.min(window.devicePixelRatio || 1, 2)
    canvas.width = Math.round(width * ratio)
    canvas.height = Math.round(height * ratio)
    context.setTransform(ratio, 0, 0, ratio, 0, 0)
    particles.length = 0
    const count = Math.min(80, Math.floor(width * height / 15000))
    for (let i = 0; i < count; i++) {
      particles.push({
        x: Math.random() * width, y: Math.random() * height,
        radius: Math.random() * 2 + 1,
        vx: (Math.random() - 0.5) * 0.5, vy: (Math.random() - 0.5) * 0.5,
        alpha: Math.random() * 0.5 + 0.2, pulse: Math.random() * Math.PI * 2
      })
    }
  }

  const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  const draw = () => {
    context.clearRect(0, 0, width, height)
    particles.forEach((p, i) => {
      if (!reduceMotion) {
        p.x = (p.x + p.vx + width) % width
        p.y = (p.y + p.vy + height) % height
        p.pulse += 0.02
      }
      context.beginPath()
      context.arc(p.x, p.y, p.radius, 0, Math.PI * 2)
      context.fillStyle = `rgba(34, 197, 94, ${p.alpha + Math.sin(p.pulse) * 0.15})`
      context.fill()
      for (let j = i + 1; j < particles.length; j++) {
        const q = particles[j]
        const d = Math.hypot(p.x - q.x, p.y - q.y)
        if (d < 120) {
          context.beginPath()
          context.moveTo(p.x, p.y)
          context.lineTo(q.x, q.y)
          context.strokeStyle = `rgba(34, 197, 94, ${0.12 * (1 - d / 120)})`
          context.lineWidth = 0.5
          context.stroke()
        }
      }
    })
    if (!reduceMotion) animationId = requestAnimationFrame(draw)
  }

  resizeParticles()
  window.addEventListener('resize', resizeParticles)
  draw()
}

onMounted(startParticles)
onBeforeUnmount(() => {
  if (animationId) cancelAnimationFrame(animationId)
  if (resizeParticles) window.removeEventListener('resize', resizeParticles)
})
</script>

<style scoped lang="scss">
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
}

.particle-canvas {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

// ========== 主卡片 ==========
.register-card {
  display: flex;
  position: relative;
  width: min(960px, calc(100% - 32px));
  min-height: 580px;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.1);
  animation: fadeInUp 0.5s ease-out;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

// ========== 左侧表单区 ==========
.register-left {
  flex: 1;
  padding: 40px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;

  .brand {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 24px;

    h1 { font-size: 20px; color: #333; margin: 0; }
    p  { font-size: 12px; color: #999; margin: 2px 0 0; }
  }

  h2 {
    font-size: 26px;
    font-weight: 700;
    margin-bottom: 6px;
    color: #222;
  }

  .sub {
    color: #999;
    margin-bottom: 24px;
    font-size: 14px;
  }

  .login-link {
    text-align: center;
    font-size: 13px;
    color: #999;
    margin-top: 8px;

    a {
      color: #2E7D32;
      font-weight: 500;
      text-decoration: none;

      &:hover { text-decoration: underline; }
    }
  }
}

// ========== 右侧品牌展示区 ==========
.register-right {
  flex: 1.15;
  background: linear-gradient(160deg, #1B5E20, #2E7D32, #388E3C);
  padding: 40px 36px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #fff;
  position: relative;
  overflow: hidden;

  // 背景装饰圆
  &::before {
    content: '';
    position: absolute;
    top: -60px;
    right: -60px;
    width: 200px;
    height: 200px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.05);
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -40px;
    left: -40px;
    width: 140px;
    height: 140px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.04);
  }

  > * { position: relative; z-index: 1; }

  h2 {
    font-size: 22px;
    font-weight: 700;
    margin-bottom: 12px;
  }

  > p {
    font-size: 13px;
    opacity: 0.85;
    margin-bottom: 32px;
    line-height: 1.7;
  }

  .features {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px 16px;
    max-width: 320px;

    .feat {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      font-size: 12px;
      opacity: 0.9;
      padding: 14px 10px;
      background: rgba(255, 255, 255, 0.08);
      border-radius: 12px;
      transition: background .2s;

      &:hover { background: rgba(255, 255, 255, 0.14); }

      .el-icon { font-size: 28px; }
    }
  }
}

// ========== 响应式 ==========
@media (max-width: 720px) {
  .register-card { max-width: 440px; }
  .register-left { padding: 32px 24px; }
  .register-right { display: none; }
}
</style>

<style>
/* 全局覆盖：让注册页 Element Plus 输入框与登录页一致 */
.register-page .el-input__wrapper {
  border-radius: 8px;
  transition: box-shadow .2s;
}

.register-page .el-input__wrapper:hover {
  box-shadow: 0 0 0 1px #81C784 inset;
}

.register-page .el-select .el-input__wrapper {
  border-radius: 8px;
}

.register-page .el-button--primary {
  border-radius: 8px;
  height: 44px;
  font-size: 15px;
  letter-spacing: 1px;
  background-color: #409eff;
  border-color: #409eff;
  transition: all .2s;
}

.register-page .el-button--primary:hover {
  background-color: #337ecc;
  border-color: #337ecc;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.35);
}
</style>
