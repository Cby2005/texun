<template>
  <div class="profile-page">
    <div class="page-header"><h2>个人中心 — 温室草莓智慧管理系统</h2></div>
    <el-row :gutter="16">
      <!-- 左侧：用户信息卡片 -->
      <el-col :span="6">
        <el-card shadow="hover" class="user-card">
          <div class="user-avatar-section">
            <el-avatar :size="80" :src="userInfo.avatar ? 'http://localhost:8080' + userInfo.avatar : ''">
              <span style="font-size:32px">{{ (userInfo.nickname || userInfo.username || '?')[0] }}</span>
            </el-avatar>
            <h3 class="user-nickname">{{ userInfo.nickname || userInfo.username }}</h3>
            <p class="user-username">@{{ userInfo.username }}</p>
          </div>
          <el-divider />
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="角色">
              <el-tag type="primary" size="small">{{ userInfo.roleName || userInfo.userType }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="所属温室">
              <span>{{ userInfo.greenhouseName || '1号温室' }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="手机绑定">
              <el-tag :type="userInfo.phoneBound ? 'success' : 'info'" size="small">
                {{ userInfo.phoneBound ? '已绑定' : '未绑定' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="安全等级">
              <el-tag :type="secLevelTag" size="small">{{ securityInfo.securityLevel || '-' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="最后登录">
              <span>{{ fmt(userInfo.lastLoginTime) }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 安全状态卡片 -->
        <el-card shadow="hover" class="security-card" style="margin-top:12px">
          <template #header><span>账号安全状态</span></template>
          <el-descriptions :column="1" size="small" border>
            <el-descriptions-item label="手机绑定">
              <el-tag :type="securityInfo.phoneBound ? 'success' : 'danger'" size="small">
                {{ securityInfo.phoneBound ? (securityInfo.maskedPhone || '已绑定') : '未绑定' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="邮箱填写">
              <el-tag :type="securityInfo.emailFilled ? 'success' : 'warning'" size="small">
                {{ securityInfo.emailFilled ? '已填写' : '未填写' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="密码更新">
              <span>{{ fmt(securityInfo.passwordUpdateTime) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="最后登录">
              <span>{{ fmt(securityInfo.lastLoginTime) }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <!-- 右侧：功能 Tabs -->
      <el-col :span="18">
        <el-card shadow="hover">
          <el-tabs v-model="activeTab" type="border-card" @tab-change="onTabChange">
            <!-- Tab 1: 基本资料 -->
            <el-tab-pane label="基本资料" name="profile">
              <el-form :model="profileForm" label-width="100px" size="default" style="max-width:480px">
                <el-form-item label="用户名"><el-input :model-value="userInfo.username" disabled /></el-form-item>
                <el-form-item label="昵称"><el-input v-model="profileForm.nickname" placeholder="请输入昵称" /></el-form-item>
                <el-form-item label="真实姓名"><el-input v-model="profileForm.realName" placeholder="请输入真实姓名" /></el-form-item>
                <el-form-item label="邮箱"><el-input v-model="profileForm.email" placeholder="请输入邮箱" /></el-form-item>
                <el-form-item label="手机号">
                  <el-input :model-value="userInfo.phone || '未绑定'" disabled />
                  <span class="form-hint">手机号请在「手机绑定」Tab中修改</span>
                </el-form-item>
                <el-form-item label="所属温室"><el-input v-model="profileForm.greenhouseName" placeholder="所属温室" disabled /></el-form-item>
                <el-form-item label="个人简介"><el-input v-model="profileForm.profile" type="textarea" :rows="3" placeholder="介绍一下自己" /></el-form-item>
                <el-form-item><el-button type="primary" :loading="saving" @click="doSaveProfile">保存资料</el-button></el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- Tab 2: 头像设置 -->
            <el-tab-pane label="头像设置" name="avatar">
              <div class="avatar-section">
                <el-avatar :size="120" :src="avatarPreview || (userInfo.avatar ? 'http://localhost:8080' + userInfo.avatar : '')">
                  <span style="font-size:48px">{{ (userInfo.nickname || '?')[0] }}</span>
                </el-avatar>
                <div style="margin-top:16px">
                  <el-upload :auto-upload="false" :show-file-list="false" :on-change="onAvatarChange" accept="image/jpeg,image/png" :limit="1">
                    <template #trigger>
                      <el-button type="primary">选择图片</el-button>
                    </template>
                  </el-upload>
                  <el-button v-if="avatarFile" type="success" :loading="uploading" @click="doUploadAvatar" style="margin-left:8px">上传头像</el-button>
                </div>
                <p class="form-hint" style="margin-top:8px">支持 JPG/PNG，大小不超过 2MB</p>
              </div>
            </el-tab-pane>

            <!-- Tab 3: 修改密码 -->
            <el-tab-pane label="修改密码" name="password">
              <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px" size="default" style="max-width:400px">
                <el-form-item label="原密码" prop="oldPassword"><el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" /></el-form-item>
                <el-form-item label="新密码" prop="newPassword"><el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="不少于6位" /></el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword"><el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" /></el-form-item>
                <el-form-item><el-button type="primary" :loading="changingPwd" @click="doChangePwd">修改密码</el-button></el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- Tab 4: 手机绑定 -->
            <el-tab-pane label="手机绑定" name="phone">
              <div class="phone-section" style="max-width:450px">
                <!-- 未绑定 -->
                <div v-if="!userInfo.phoneBound" class="phone-bind">
                  <el-alert title="您还未绑定手机号，建议绑定以提升账号安全" type="warning" show-icon :closable="false" style="margin-bottom:12px" />
                  <el-form label-width="100px" size="default">
                    <el-form-item label="手机号"><el-input v-model="phoneBindForm.phone" placeholder="请输入手机号" maxlength="11" /></el-form-item>
                    <el-form-item label="验证码">
                      <el-input v-model="phoneBindForm.code" placeholder="验证码" style="width:160px" maxlength="6" />
                      <el-button :disabled="codeCountdown>0" :loading="sendingCode" @click="doSendCode('bind')" style="margin-left:8px">
                        {{ codeCountdown > 0 ? codeCountdown + 's' : '获取验证码' }}
                      </el-button>
                    </el-form-item>
                    <el-form-item><el-button type="primary" :loading="bindingPhone" @click="doBindPhone">绑定手机号</el-button></el-form-item>
                  </el-form>
                </div>
                <!-- 已绑定 -->
                <div v-else class="phone-bound">
                  <el-alert :title="'当前绑定手机号：' + (securityInfo.maskedPhone || userInfo.phone)" type="success" show-icon :closable="false" style="margin-bottom:12px" />
                  <el-button type="warning" @click="showChangePhoneDialog">换绑手机号</el-button>
                  <el-button type="danger" @click="showUnbindPhoneDialog" style="margin-left:8px">解绑手机号</el-button>
                </div>
              </div>
            </el-tab-pane>

            <!-- Tab 5: 登录日志 -->
            <el-tab-pane label="登录日志" name="loginLog">
              <el-table :data="loginLogs" stripe v-loading="loadingLogs" size="small">
                <el-table-column prop="loginTime" label="登录时间" width="170"><template #default="{row}">{{ fmt(row.loginTime) }}</template></el-table-column>
                <el-table-column prop="loginIp" label="IP地址" width="140" />
                <el-table-column prop="loginLocation" label="登录地点" width="120" />
                <el-table-column prop="browser" label="浏览器" width="100" />
                <el-table-column prop="os" label="操作系统" width="100" />
                <el-table-column prop="loginStatus" label="状态" width="80"><template #default="{row}"><el-tag :type="row.loginStatus==='SUCCESS'?'success':'danger'" size="small">{{ row.loginStatus==='SUCCESS'?'成功':'失败' }}</el-tag></template></el-table-column>
              </el-table>
              <el-pagination v-if="logTotal>0" :current-page="logPage" :page-size="logSize" :total="logTotal" layout="total,prev,pager,next" @current-change="p=>{logPage=p;loadLoginLogs()}" style="margin-top:12px;justify-content:flex-end" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <!-- 换绑手机号弹窗 -->
    <el-dialog v-model="changePhoneVisible" title="换绑手机号" width="420px">
      <el-form label-width="80px">
        <el-form-item label="当前手机号"><el-input :model-value="userInfo.phone" disabled /></el-form-item>
        <el-form-item label="新手机号"><el-input v-model="changePhoneForm.newPhone" placeholder="新手机号" maxlength="11" /></el-form-item>
        <el-form-item label="验证码">
          <el-input v-model="changePhoneForm.code" placeholder="验证码" style="width:160px" maxlength="6" />
          <el-button :disabled="codeCountdown>0" :loading="sendingCode" @click="doSendCode('change')" style="margin-left:8px">
            {{ codeCountdown > 0 ? codeCountdown + 's' : '获取验证码' }}
          </el-button>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="changePhoneVisible=false">取消</el-button><el-button type="primary" :loading="changingPhone" @click="doChangePhone">确认换绑</el-button></template>
    </el-dialog>

    <!-- 解绑手机号弹窗 -->
    <el-dialog v-model="unbindPhoneVisible" title="解绑手机号" width="380px">
      <el-alert title="解绑后手机号将置空，建议先绑定新手机号" type="warning" show-icon :closable="false" style="margin-bottom:12px" />
      <el-form label-width="80px">
        <el-form-item label="登录密码"><el-input v-model="unbindForm.password" type="password" show-password placeholder="输入密码验证身份" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="unbindPhoneVisible=false">取消</el-button><el-button type="danger" :loading="unbindingPhone" @click="doUnbindPhone">确认解绑</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProfile, updateProfile, uploadAvatar, updatePassword, sendPhoneCode, bindPhone, changePhone, unbindPhone, getSecurityInfo, getLoginLogs } from '@/api/profile'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeTab = ref('profile')

// 用户信息
const userInfo = ref({})
const securityInfo = ref({})
const loading = ref(false)
const saving = ref(false)

// 安全等级颜色
const secLevelTag = computed(() => {
  const lv = securityInfo.value.securityLevel
  if (lv === 'HIGH') return 'success'
  if (lv === 'LOW') return 'danger'
  return 'warning'
})

// ========== 基本资料 ==========
const profileForm = reactive({ nickname: '', realName: '', email: '', profile: '', greenhouseName: '1号温室' })
async function doSaveProfile() {
  saving.value = true
  try {
    await updateProfile(profileForm)
    ElMessage.success('资料已更新')
    await reloadUser()
  } catch (e) {
    ElMessage.error('保存失败: ' + (e.message || e))
  } finally { saving.value = false }
}

// ========== 头像 ==========
const avatarFile = ref(null)
const avatarPreview = ref('')
const uploading = ref(false)
function onAvatarChange(file) {
  if (!file.raw) return
  const t = file.raw.type
  if (t !== 'image/jpeg' && t !== 'image/png') { ElMessage.error('仅支持JPG/PNG'); return }
  if (file.raw.size > 2*1024*1024) { ElMessage.error('文件不能超过2MB'); return }
  avatarFile.value = file.raw
  avatarPreview.value = URL.createObjectURL(file.raw)
}
async function doUploadAvatar() {
  if (!avatarFile.value) return
  uploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', avatarFile.value)
    await uploadAvatar(fd)
    ElMessage.success('头像已更新')
    avatarFile.value = null
    await reloadUser()
  } catch (e) {
    ElMessage.error('上传失败: ' + (e.message || e))
  } finally { uploading.value = false }
}

// ========== 修改密码 ==========
const pwdFormRef = ref(null)
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const changingPwd = ref(false)
const validateConfirmPwd = (_rule, value, cb) => { if (value !== pwdForm.newPassword) cb(new Error('两次密码不一致')); else cb() }
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, min: 6, message: '新密码不少于6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, validator: validateConfirmPwd, trigger: 'blur' }]
}
async function doChangePwd() {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  changingPwd.value = true
  try {
    await updatePassword(pwdForm)
    ElMessage.success('密码修改成功，请重新登录')
    setTimeout(() => {
      localStorage.clear()
      router.push('/login')
    }, 1500)
  } catch (e) { ElMessage.error('修改失败: ' + (e.message || e)); changingPwd.value = false }
}

// ========== 手机号 ==========
const phoneBindForm = reactive({ phone: '', code: '' })
const changePhoneForm = reactive({ newPhone: '', code: '' })
const unbindForm = reactive({ password: '' })
const changePhoneVisible = ref(false)
const unbindPhoneVisible = ref(false)
const bindingPhone = ref(false)
const changingPhone = ref(false)
const unbindingPhone = ref(false)
const sendingCode = ref(false)
const codeCountdown = ref(0)
let countdownTimer = null

function startCountdown() {
  codeCountdown.value = 60
  clearInterval(countdownTimer)
  countdownTimer = setInterval(() => {
    codeCountdown.value--
    if (codeCountdown.value <= 0) clearInterval(countdownTimer)
  }, 1000)
}

async function doSendCode(type) {
  const phone = type === 'bind' ? phoneBindForm.phone : changePhoneForm.newPhone
  if (!phone || !/^1[3-9]\d{9}$/.test(phone)) { ElMessage.warning('请输入正确的手机号'); return }
  sendingCode.value = true
  try {
    const res = await sendPhoneCode(phone)
    ElMessage.success('验证码已发送（开发环境: ' + res.data + '）')
    startCountdown()
  } catch (e) { ElMessage.error('发送失败: ' + (e.message || e)) }
  finally { sendingCode.value = false }
}

async function doBindPhone() {
  if (!phoneBindForm.phone || !phoneBindForm.code) { ElMessage.warning('请填写完整'); return }
  bindingPhone.value = true
  try { await bindPhone({ phone: phoneBindForm.phone, code: phoneBindForm.code }); ElMessage.success('绑定成功'); await reloadUser() }
  catch (e) { ElMessage.error('绑定失败: ' + (e.message || e)) }
  finally { bindingPhone.value = false }
}

function showChangePhoneDialog() { changePhoneForm.newPhone = ''; changePhoneForm.code = ''; changePhoneVisible.value = true }
async function doChangePhone() {
  if (!changePhoneForm.newPhone || !changePhoneForm.code) { ElMessage.warning('请填写完整'); return }
  changingPhone.value = true
  try { await changePhone(changePhoneForm); ElMessage.success('换绑成功'); changePhoneVisible.value = false; await reloadUser() }
  catch (e) { ElMessage.error('换绑失败: ' + (e.message || e)) }
  finally { changingPhone.value = false }
}

function showUnbindPhoneDialog() { unbindForm.password = ''; unbindPhoneVisible.value = true }
async function doUnbindPhone() {
  if (!unbindForm.password) { ElMessage.warning('请输入密码'); return }
  unbindingPhone.value = true
  try { await unbindPhone({ password: unbindForm.password }); ElMessage.success('已解绑'); unbindPhoneVisible.value = false; await reloadUser() }
  catch (e) { ElMessage.error('解绑失败: ' + (e.message || e)) }
  finally { unbindingPhone.value = false }
}

// ========== 登录日志 ==========
const loginLogs = ref([])
const logTotal = ref(0)
const logPage = ref(1)
const logSize = ref(10)
const loadingLogs = ref(false)
async function loadLoginLogs() {
  loadingLogs.value = true
  try {
    const r = await getLoginLogs({ pageNum: logPage.value, pageSize: logSize.value })
    loginLogs.value = r.data?.records || []
    logTotal.value = r.data?.total || 0
  } catch { loginLogs.value = [] }
  finally { loadingLogs.value = false }
}

// ========== 工具 ==========
function fmt(val) {
  if (!val) return '-'
  const s = typeof val === 'string' ? val : (val.length > 16 ? val : val.toString())
  return s.length >= 16 ? s.substring(0, 16).replace('T', ' ') : s
}

async function reloadUser() {
  const [u, s] = await Promise.all([getProfile(), getSecurityInfo()])
  userInfo.value = u.data || {}
  securityInfo.value = s.data || {}
  profileForm.nickname = userInfo.value.nickname || ''
  profileForm.realName = userInfo.value.realName || userInfo.value.nickname || ''
  profileForm.email = userInfo.value.email || ''
  profileForm.profile = userInfo.value.profile || ''
  profileForm.greenhouseName = userInfo.value.greenhouseName || '1号温室'
}

function onTabChange(name) {
  if (name === 'loginLog') loadLoginLogs()
}

onMounted(reloadUser)
</script>

<style scoped>
.profile-page { padding: 16px; }
.page-header h2 { margin: 0 0 16px; color: #2d8c2d; }

.user-card { text-align: center; }
.user-avatar-section { display: flex; flex-direction: column; align-items: center; gap: 8px; }
.user-nickname { margin: 4px 0 0; font-size: 18px; color: #333; }
.user-username { margin: 0; font-size: 13px; color: #999; }

.avatar-section { display: flex; flex-direction: column; align-items: center; padding: 20px; }

.form-hint { font-size: 12px; color: #999; margin-top: 4px; display: block; }

.phone-section { padding: 8px 0; }

.security-card .el-descriptions { font-size: 13px; }
</style>
