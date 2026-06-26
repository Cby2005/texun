<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-left">
        <div class="brand">
          <svg width="48" height="48" viewBox="0 0 40 40"><rect width="40" height="40" rx="10" fill="#2E7D32"/><path d="M20 8C20 8 12 14 12 22s8 12 8 12 8-4 8-12-8-14-8-14z" fill="#66BB6A"/><circle cx="20" cy="20" r="3" fill="white"/></svg>
          <div><h1>智慧农业平台</h1><p>Smart Agriculture Platform</p></div>
        </div>
        <h2>欢迎回来</h2>
        <p class="sub">请输入账号信息登录平台</p>
        <el-form @submit.prevent="handleLogin" ref="formRef" :model="form" size="large">
          <el-form-item><el-input v-model="form.username" placeholder="用户名" prefix-icon="User" /></el-form-item>
          <el-form-item><el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password /></el-form-item>
          <el-form-item><el-button type="primary" native-type="submit" :loading="loading" style="width:100%">登录</el-button></el-form-item>
        </el-form>
        <p class="reg">还没有账号？<router-link to="/register">立即注册</router-link></p>
      </div>
      <div class="login-right">
        <h2>智慧农业综合服务平台</h2>
        <p>集成农场管理、农产品溯源、农业技术推广三大核心功能，助力农业数字化转型</p>
        <div class="features">
          <div class="feat"><el-icon :size="32"><Grid /></el-icon><span>农场生产管理</span></div>
          <div class="feat"><el-icon :size="32"><Goods /></el-icon><span>农产品溯源</span></div>
          <div class="feat"><el-icon :size="32"><Reading /></el-icon><span>农业技术推广</span></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store'
import { login } from '../../api/system'
import { ElMessage } from 'element-plus'

const router = useRouter()
const store = useUserStore()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

async function handleLogin() {
  if (!form.username || !form.password) { ElMessage.warning('请输入用户名和密码'); return }
  loading.value = true
  try {
    const res = await login(form)
    store.login(res.data)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (e) { /* request interceptor handles error */ }
  finally { loading.value = false }
}
</script>

<style scoped lang="scss">
.login-page { min-height:100vh; display:flex; align-items:center; justify-content:center; background:linear-gradient(135deg,#E8F5E9,#C8E6C9); }
.login-card { display:flex; width:900px; background:#fff; border-radius:12px; overflow:hidden; box-shadow:0 8px 40px rgba(0,0,0,0.12); }
.login-left { flex:1; padding:48px 40px; }
.login-left .brand { display:flex; align-items:center; gap:12px; margin-bottom:32px; h1{font-size:20px;color:#333} p{font-size:12px;color:#999} }
.login-left h2 { font-size:28px; margin-bottom:8px; }
.login-left .sub { color:#999; margin-bottom:24px; font-size:14px; }
.login-left .reg { text-align:center; font-size:13px; color:#999; margin-top:16px; a{color:#2E7D32} }
.login-right { flex:1.1; background:linear-gradient(135deg,#1B5E20,#2E7D32,#4CAF50); padding:48px 40px; display:flex; flex-direction:column; justify-content:center; color:#fff; }
.login-right h2 { font-size:24px; margin-bottom:12px; }
.login-right p { font-size:14px; opacity:0.85; margin-bottom:32px; line-height:1.6; }
.features { display:flex; gap:24px; }
.feat { display:flex; flex-direction:column; align-items:center; gap:8px; font-size:12px; }
</style>
