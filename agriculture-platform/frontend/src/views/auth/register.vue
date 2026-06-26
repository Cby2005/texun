<template>
  <div class="page">
    <el-card style="max-width:500px; margin: 60px auto;">
      <template #header><h2>注册账号</h2></template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="86px" size="default">
        <el-form-item label="用户名" prop="username"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码" prop="password"><el-input v-model="form.password" type="password" show-password /></el-form-item>
        <el-form-item label="确认密码" prop="confirmPwd"><el-input v-model="form.confirmPwd" type="password" show-password /></el-form-item>
        <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="form.userType" style="width:100%">
            <el-option :value="4" label="农户" />
            <el-option :value="2" label="溯源企业" />
            <el-option :value="5" label="消费者" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="handleRegister" :loading="loading" style="width:100%">注册</el-button></el-form-item>
      </el-form>
      <p style="text-align:center;color:#999">已有账号？<router-link to="/login">去登录</router-link></p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
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
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 20, message: '3-20位', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '至少6位', trigger: 'blur' }],
  confirmPwd: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await register({ username: form.username, password: form.password, phone: form.phone, userType: form.userType })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    ElMessage.error('注册失败')
  } finally {
    loading.value = false
  }
}
</script>
