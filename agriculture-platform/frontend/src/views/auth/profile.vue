<template>
  <div class="profile-page">
    <!-- 顶部个人信息概览 -->
    <div class="profile-header">
      <div class="header-left">
        <el-avatar :size="72" :src="profile.avatar" class="avatar">
          <el-icon :size="36"><UserFilled /></el-icon>
        </el-avatar>
        <div class="header-info">
          <div class="header-name">{{ profile.nickname || profile.username }}</div>
          <div class="header-meta">
            <el-tag size="small" effect="dark">{{ profile.roleName || '普通用户' }}</el-tag>
            <el-tag :type="secLevelType" size="small" effect="plain" style="margin-left:8px">
              安全等级: {{ security.securityLevel || '未知' }}
            </el-tag>
          </div>
        </div>
      </div>
      <div class="header-right">
        <span class="last-login">最近登录: {{ profile.lastLoginTime || '-' }}</span>
      </div>
    </div>

    <!-- Tab 切换 -->
    <el-tabs v-model="activeTab" class="profile-tabs">
      <el-tab-pane label="基本资料" name="basic">
        <el-card shadow="never" class="section-card">
          <template #header><span class="section-title">个人资料</span></template>
          <el-form :model="editForm" :rules="basicRules" ref="basicFormRef" label-width="100px" size="default">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名"><el-input v-model="profile.username" disabled /></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="editForm.nickname" maxlength="30" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="真实姓名">
                  <el-input v-model="editForm.realName" maxlength="20" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="editForm.phone" maxlength="11" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="editForm.email" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="用户角色"><el-input :model-value="profile.roleName || profile.userType" disabled /></el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="所属基地"><el-input :model-value="profile.greenhouseName || '-'" disabled /></el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="头像">
                  <div style="display:flex;align-items:center;gap:12px">
                    <el-avatar :size="48" :src="profile.avatar" />
                    <el-upload :show-file-list="false" :before-upload="beforeAvatarUpload" :http-request="handleAvatarUpload" accept="image/jpeg,image/png">
                      <el-button size="small" type="primary" plain>上传头像</el-button>
                    </el-upload>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="个人简介">
              <el-input v-model="editForm.profile" type="textarea" :rows="3" maxlength="200" show-word-limit />
            </el-form-item>
            <el-form-item><el-button type="primary" @click="handleSaveBasic" :loading="saving">保存资料</el-button></el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="账号安全" name="security">
        <el-card shadow="never" class="section-card">
          <template #header><span class="section-title">安全概览</span></template>
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="安全等级">
              <el-progress :percentage="securityScore" :color="secColor" :stroke-width="16" :text-inside="true" style="width:160px" />
            </el-descriptions-item>
            <el-descriptions-item label="手机绑定">
              <el-tag :type="security.phoneBound ? 'success' : 'warning'" size="small">{{ security.phoneBound ? '已绑定 ' + (security.maskedPhone || '') : '未绑定' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="邮箱状态">
              <el-tag :type="security.emailFilled ? 'success' : 'warning'" size="small">{{ security.emailFilled ? '已填写' : '未填写' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="最近登录">{{ security.lastLoginTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="密码修改时间">{{ security.passwordUpdateTime || '未修改过' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 手机号绑定 -->
        <el-card shadow="never" class="section-card" style="margin-top:12px">
          <template #header><span class="section-title">手机号管理</span></template>
          <template v-if="profile.phone">
            <div style="display:flex;align-items:center;gap:16px;flex-wrap:wrap">
              <span>已绑定: <b>{{ profile.phone }}</b></span>
              <el-button size="small" @click="showChangePhoneDialog">换绑</el-button>
              <el-popconfirm title="确认解绑手机号?" @confirm="handleUnbindPhone">
                <template #reference><el-button size="small" type="danger" plain>解绑</el-button></template>
              </el-popconfirm>
            </div>
          </template>
          <template v-else>
            <el-button size="small" type="primary" @click="showBindPhoneDialog">绑定手机号</el-button>
          </template>
        </el-card>

        <!-- 修改密码 -->
        <el-card shadow="never" class="section-card" style="margin-top:12px">
          <template #header><span class="section-title">修改密码</span></template>
          <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="100px" size="default" style="max-width:450px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" show-password />
              <div style="font-size:12px;color:#909399;margin-top:4px">长度不少于6位，不能与原密码相同</div>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item><el-button type="warning" @click="handleChangePwd" :loading="pwdSaving">修改密码</el-button></el-form-item>
          </el-form>
        </el-card>

        <!-- 登录日志 -->
        <el-card shadow="never" class="section-card" style="margin-top:12px">
          <template #header><span class="section-title">登录日志</span></template>
          <el-table :data="loginLogs" stripe size="small" v-loading="logLoading">
            <el-table-column prop="loginTime" label="登录时间" width="170" />
            <el-table-column prop="loginIp" label="IP" width="140" />
            <el-table-column prop="loginLocation" label="登录地点" width="120" />
            <el-table-column prop="os" label="操作系统" width="110" />
            <el-table-column prop="browser" label="浏览器" width="120" />
            <el-table-column prop="loginStatus" label="状态" width="80" />
          </el-table>
          <el-pagination v-model:current-page="logPage" :page-size="10" :total="logTotal" layout="prev,pager,next" small style="margin-top:10px" @current-change="loadLoginLogs" />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="我的收藏" name="favorites">
        <el-card shadow="never" class="section-card">
          <template #header><span class="section-title">收藏列表</span></template>
          <el-table :data="favorites" stripe size="small" v-loading="favLoading">
            <el-table-column label="文章标题" min-width="250">
              <template #default="{row}">
                <router-link :to="'/knowledge/article/' + row.articleId" style="color:#2d8c2d">{{ row.title || '未知文章' }}</router-link>
              </template>
            </el-table-column>
            <el-table-column prop="cropType" label="分类" width="100" />
            <el-table-column prop="viewCount" label="浏览量" width="80" />
            <el-table-column prop="createTime" label="收藏时间" width="170" />
            <el-table-column label="操作" width="100">
              <template #default="{row}">
                <el-button size="small" type="danger" plain @click="handleCancelFavorite(row)">取消收藏</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination v-model:current-page="favPage" :page-size="10" :total="favTotal" layout="prev,pager,next" small style="margin-top:10px" @current-change="loadFavorites" />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="我的评论" name="comments">
        <el-card shadow="never" class="section-card">
          <template #header><span class="section-title">评论列表</span></template>
          <el-table :data="comments" stripe size="small" v-loading="cmtLoading">
            <el-table-column prop="content" label="评论内容" min-width="250" show-overflow-tooltip />
            <el-table-column label="所属文章" min-width="180">
              <template #default="{row}">
                <router-link :to="'/knowledge/article/' + row.articleId" style="color:#2d8c2d">{{ row.articleTitle || '未知' }}</router-link>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="时间" width="170" />
            <el-table-column label="状态" width="80">
              <template #default="{row}"><el-tag :type="row.status==='已发布'?'success':'warning'" size="small">{{ row.status }}</el-tag></template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{row}">
                <el-popconfirm title="确认删除?" @confirm="handleDeleteComment(row)">
                  <template #reference><el-button size="small" type="danger" plain>删除</el-button></template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination v-model:current-page="cmtPage" :page-size="10" :total="cmtTotal" layout="prev,pager,next" small style="margin-top:10px" @current-change="loadComments" />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="农技行为" name="behavior">
        <el-card shadow="never" class="section-card">
          <template #header><span class="section-title">行为统计</span></template>
          <el-row :gutter="16">
            <el-col :span="6"><el-statistic title="浏览文章" :value="behaviorStats.viewCount || 0" /></el-col>
            <el-col :span="6"><el-statistic title="收藏文章" :value="behaviorStats.favoriteCount || 0" /></el-col>
            <el-col :span="6"><el-statistic title="发表评论" :value="behaviorStats.commentCount || 0" /></el-col>
            <el-col :span="6"><el-statistic title="搜索次数" :value="behaviorStats.searchCount || 0" /></el-col>
          </el-row>
        </el-card>
        <el-card shadow="never" class="section-card" style="margin-top:12px">
          <template #header><span class="section-title">最近搜索</span></template>
          <div v-if="(behaviorStats.recentSearches||[]).length">
            <el-tag v-for="(kw,i) in behaviorStats.recentSearches" :key="i" style="margin:4px">{{ kw }}</el-tag>
          </div>
          <span v-else class="text-muted">暂无搜索记录</span>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="兴趣画像" name="interest">
        <el-card shadow="never" class="section-card">
          <template #header><span class="section-title">关注方向</span></template>
          <div class="tag-cloud">
            <el-tag v-for="t in interestTags" :key="t.tagName" :color="tagColor(t.tagType)" size="large" effect="dark" style="margin:6px">
              {{ t.tagName }}
              <span style="font-size:11px;opacity:.8;margin-left:4px">{{ Math.round(t.weight * 100) }}%</span>
            </el-tag>
          </div>
          <div v-if="!interestTags.length" class="text-muted">暂无画像数据，继续浏览农技内容将自动生成</div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 绑定/换绑手机号弹窗 -->
    <el-dialog v-model="phoneDialog.visible" :title="phoneDialog.title" width="420px" destroy-on-close>
      <el-form :model="phoneDialog.form" ref="phoneFormRef" label-width="80px">
        <el-form-item label="手机号" prop="phone" :rules="[{required:true,message:'请输入手机号',trigger:'blur'},{pattern:/^1[3-9]\d{9}$/,message:'格式不正确',trigger:'blur'}]">
          <el-input v-model="phoneDialog.form.phone" maxlength="11" />
        </el-form-item>
        <el-form-item label="验证码">
          <div style="display:flex;gap:8px">
            <el-input v-model="phoneDialog.form.code" maxlength="6" />
            <el-button :disabled="phoneCodeCountdown > 0" @click="sendCode" size="small">{{ phoneCodeCountdown > 0 ? phoneCodeCountdown + 's' : '发送验证码' }}</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="phoneDialog.visible = false">取消</el-button><el-button type="primary" @click="handlePhoneBind">确认</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import {
  getProfile, updateProfile, uploadAvatar, updatePassword,
  getSecurityInfo, getLoginLogs, sendPhoneCode, bindPhone, changePhone, unbindPhone,
  getMyFavorites, removeFavorite, getMyComments, deleteMyComment,
  getBehaviorStats, getInterestTags
} from '@/api/profile'

const activeTab = ref('basic')
const saving = ref(false), pwdSaving = ref(false)
const profile = ref({})
const security = ref({})
const basicFormRef = ref(null), pwdFormRef = ref(null), phoneFormRef = ref(null)

// ==================== 基本资料 ====================
const editForm = reactive({ nickname: '', realName: '', phone: '', email: '', profile: '' })
const basicRules = {
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
}

async function loadProfile() {
  try { const r = await getProfile(); if (r.data) { profile.value = r.data; Object.assign(editForm, { nickname: r.data.nickname || '', realName: r.data.realName || '', phone: r.data.phone || '', email: r.data.email || '', profile: r.data.profile || '' }) } } catch {}
}

function beforeAvatarUpload(file) {
  const isImg = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isImg) { ElMessage.error('仅支持 jpg/png'); return false }
  if (file.size > 2 * 1024 * 1024) { ElMessage.error('大小不超过2MB'); return false }
  return true
}
async function handleAvatarUpload({ file }) {
  try {
    const fd = new FormData(); fd.append('file', file)
    await uploadAvatar(fd)
    await loadProfile()
    ElMessage.success('头像已更新')
  } catch {}
}

async function handleSaveBasic() {
  try { await basicFormRef.value.validate() } catch { return }
  saving.value = true
  try {
    await updateProfile(editForm)
    ElMessage.success('保存成功')
    loadProfile()
  } catch {} finally { saving.value = false }
}

// ==================== 账号安全 ====================
const secLevelType = computed(() => security.value.securityLevel === 'HIGH' ? 'success' : security.value.securityLevel === 'LOW' ? 'danger' : 'warning')
const secColor = computed(() => security.value.securityLevel === 'HIGH' ? '#67c23a' : security.value.securityLevel === 'LOW' ? '#f56c6c' : '#e6a23c')
const securityScore = computed(() => {
  let s = 0; if (security.value.phoneBound) s += 20; if (security.value.emailFilled) s += 20; s += 30 /* has password */; if (security.value.lastLoginTime) s += 30; return s
})

async function loadSecurity() { try { const r = await getSecurityInfo(); if (r.data) security.value = r.data } catch {} }

// 修改密码
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, min: 6, message: '不少于6位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: (_, v, cb) => v !== pwdForm.newPassword ? cb('两次密码不一致') : cb(), trigger: 'blur' }]
}
async function handleChangePwd() {
  try { await pwdFormRef.value.validate() } catch { return }
  if (pwdForm.newPassword === pwdForm.oldPassword) { ElMessage.warning('新密码不能与原密码相同'); return }
  pwdSaving.value = true
  try { await updatePassword(pwdForm); ElMessage.success('密码修改成功，请重新登录'); pwdForm.oldPassword = ''; pwdForm.newPassword = ''; pwdForm.confirmPassword = '' }
  catch {} finally { pwdSaving.value = false }
}

// 手机号绑定/换绑
const phoneDialog = reactive({ visible: false, title: '', form: { phone: '', code: '' }, mode: 'bind' })
const phoneCodeCountdown = ref(0)
function showBindPhoneDialog() { phoneDialog.title = '绑定手机号'; phoneDialog.mode = 'bind'; phoneDialog.form = { phone: '', code: '' }; phoneDialog.visible = true; nextTick(() => phoneFormRef.value?.resetFields?.()) }
function showChangePhoneDialog() { phoneDialog.title = '换绑手机号'; phoneDialog.mode = 'change'; phoneDialog.form = { phone: '', code: '' }; phoneDialog.visible = true; nextTick(() => phoneFormRef.value?.resetFields?.()) }
async function sendCode() {
  if (!phoneDialog.form.phone) { ElMessage.warning('请先输入手机号'); return }
  try { await sendPhoneCode(phoneDialog.form.phone); ElMessage.success('验证码已发送（开发环境,请查看控制台）'); phoneCodeCountdown.value = 60;
    const t = setInterval(() => { phoneCodeCountdown.value--; if (phoneCodeCountdown.value <= 0) clearInterval(t) }, 1000) } catch {}
}
async function handlePhoneBind() {
  try {
    if (phoneDialog.mode === 'bind') await bindPhone(phoneDialog.form)
    else await changePhone({ newPhone: phoneDialog.form.phone, code: phoneDialog.form.code })
    phoneDialog.visible = false; ElMessage.success(phoneDialog.mode === 'bind' ? '绑定成功' : '换绑成功')
    loadProfile(); loadSecurity()
  } catch {}
}
async function handleUnbindPhone() {
  try { await unbindPhone({ password: '' }); ElMessage.success('已解绑'); loadProfile(); loadSecurity() } catch {}
}

// 登录日志
const loginLogs = ref([]), logTotal = ref(0), logPage = ref(1), logLoading = ref(false)
async function loadLoginLogs() {
  logLoading.value = true
  try { const r = await getLoginLogs({ pageNum: logPage.value, pageSize: 10 }); loginLogs.value = r.data?.records || []; logTotal.value = r.data?.total || 0 } catch {} finally { logLoading.value = false }
}

// ==================== 我的收藏 ====================
const favorites = ref([]), favTotal = ref(0), favPage = ref(1), favLoading = ref(false)
async function loadFavorites() {
  favLoading.value = true
  try { const r = await getMyFavorites({ pageNum: favPage.value, pageSize: 10 }); favorites.value = r.data?.records || []; favTotal.value = r.data?.total || 0 } catch {} finally { favLoading.value = false }
}
async function handleCancelFavorite(row) {
  try { await removeFavorite(row.articleId); ElMessage.success('已取消收藏'); loadFavorites() } catch {}
}

// ==================== 我的评论 ====================
const comments = ref([]), cmtTotal = ref(0), cmtPage = ref(1), cmtLoading = ref(false)
async function loadComments() {
  cmtLoading.value = true
  try { const r = await getMyComments({ pageNum: cmtPage.value, pageSize: 10 }); comments.value = r.data?.records || []; cmtTotal.value = r.data?.total || 0 } catch {} finally { cmtLoading.value = false }
}
async function handleDeleteComment(row) {
  try { await deleteMyComment(row.id); ElMessage.success('已删除'); loadComments() } catch {}
}

// ==================== 行为统计 ====================
const behaviorStats = ref({})
async function loadBehaviorStats() {
  try { const r = await getBehaviorStats(); if (r.data) behaviorStats.value = r.data } catch {}
}

// ==================== 兴趣画像 ====================
const interestTags = ref([])
function tagColor(type) { return { crop: '#67c23a', greenhouse: '#409eff', disease: '#e6a23c', irrigation: '#2196f3', harvest: '#ff9800', sale: '#9c27b0', content_type: '#00bcd4' }[type] || '#909399' }
async function loadInterestTags() {
  try { const r = await getInterestTags(); if (r.data) interestTags.value = r.data } catch {}
}

onMounted(() => { loadProfile(); loadSecurity(); loadLoginLogs(); loadBehaviorStats(); loadInterestTags() })

// 懒加载收藏/评论（切Tab时首次加载）
watch(activeTab, (tab) => {
  if (tab === 'favorites' && favorites.value.length === 0 && !favLoading.value) loadFavorites()
  if (tab === 'comments' && comments.value.length === 0 && !cmtLoading.value) loadComments()
})
</script>

<style scoped>
.profile-page { padding: 0; }
.profile-header { display: flex; justify-content: space-between; align-items: center; padding: 24px 28px; background: linear-gradient(135deg, #e8f5e9, #c8e6c9); border-radius: 12px; margin-bottom: 16px; }
.header-left { display: flex; align-items: center; gap: 18px; }
.avatar { border: 3px solid #fff; box-shadow: 0 2px 8px rgba(0,0,0,.1); }
.header-name { font-size: 22px; font-weight: 700; color: #2d8c2d; }
.header-meta { margin-top: 6px; }
.header-right { color: #666; font-size: 13px; }
.profile-tabs { background: #fff; border-radius: 10px; padding: 4px 20px 20px; box-shadow: 0 1px 6px rgba(0,0,0,.06); }
.section-card { border: 1px solid #e0f0e0; }
.section-title { font-weight: 700; color: #2d8c2d; font-size: 15px; }
.text-muted { color: #9e9e9e; font-size: 13px; }
.tag-cloud { display: flex; flex-wrap: wrap; gap: 4px; }
:deep(.el-card__header) { padding: 12px 16px; background: #f6fdf6; border-bottom: 1px solid #c8e6c9; }
:deep(.el-card__body) { padding: 16px; }
:deep(.el-tabs__item.is-active) { color: #2d8c2d; font-weight: 600; }
:deep(.el-tabs__active-bar) { background: #2d8c2d; }
</style>
