<template>
  <div class="page">
    <el-card style="max-width:600px;margin:0 auto">
      <template #header><h2>个人信息</h2></template>
      <el-form :model="form" label-width="80px" size="default">
        <el-form-item label="用户名"><el-input v-model="store.username" disabled /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSave">保存</el-button></el-form-item>
      </el-form>
      <el-divider />
      <h3 style="margin-bottom:12px">修改密码</h3>
      <el-form :model="pwdForm" label-width="80px" size="default">
        <el-form-item label="原密码"><el-input v-model="pwdForm.oldPwd" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwdForm.newPwd" type="password" show-password /></el-form-item>
        <el-form-item><el-button type="warning" @click="handlePwd">修改密码</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useUserStore } from '../../store'
import { updateProfile, updatePassword } from '../../api/system'
import { ElMessage } from 'element-plus'

const store = useUserStore()
const form = reactive({ nickname: store.nickname, phone: '', email: '' })
const pwdForm = reactive({ oldPwd: '', newPwd: '' })

async function handleSave() {
  try { await updateProfile(form); ElMessage.success('保存成功') } catch (e) {}
}

async function handlePwd() {
  if (!pwdForm.oldPwd || !pwdForm.newPwd) { ElMessage.warning('请填写完整'); return }
  try { await updatePassword(pwdForm.oldPwd, pwdForm.newPwd); ElMessage.success('密码修改成功') } catch (e) {}
}
</script>
