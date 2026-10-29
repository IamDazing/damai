<template>
  <div class="payment-page">
    <Header />
    <main class="payment-content">
      <div class="payment-card">
        <div class="payment-icon">✓</div>
        <h1>{{ orderNumber ? '订单已提交' : '未找到订单' }}</h1>
        <p v-if="orderNumber" class="order-number">订单号：{{ orderNumber }}</p>
        <p class="description">{{ orderNumber ? '选择一种方式继续体验' : '请返回订单列表选择订单' }}</p>
        <el-button type="primary" class="choose-button" :disabled="!orderNumber" @click="showChoices = true">选择付款方式</el-button>
        <button class="back-link" @click="router.push('/orderManagement/index')">返回订单列表</button>
      </div>
    </main>
    <Footer />

    <el-dialog v-model="showChoices" title="选择付款方式" width="min(480px, 92vw)" align-center>
      <div class="payment-options">
        <button class="option" @click="openSupport">
          <span class="option-icon support-icon">♡</span>
          <span><strong>支持小怀</strong><small>欢迎付款 0.01 元支持小怀</small></span>
          <span class="arrow">›</span>
        </button>
        <button class="option" @click="openSimulation">
          <span class="option-icon demo-icon">✓</span>
          <span><strong>直接支付</strong><small>模拟支付流程，不会实际扣款</small></span>
          <span class="arrow">›</span>
        </button>
      </div>
    </el-dialog>

    <el-dialog v-model="showSupport" title="支持小怀" width="min(420px, 92vw)" align-center>
      <div class="support-content">
        <p class="support-title">欢迎付款 0.01 元支持小怀</p>
        <img :src="supportQr" alt="支付宝支持小怀收款码" class="support-qr">
        <p class="notice">这是自愿支持，与订单付款无关，不会改变订单状态。</p>
      </div>
      <template #footer>
        <el-button @click="backToChoices('support')">返回选择</el-button>
        <el-button type="primary" @click="showSupport = false">完成</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showSimulation" title="直接支付" width="min(420px, 92vw)" align-center>
      <div class="simulation-content">
        <p>将模拟一次支付流程，不会实际扣款或生成有效票券。</p>
        <p class="notice">确认后订单将标记为已支付（模拟），不会产生真实交易。</p>
      </div>
      <template #footer>
        <el-button @click="backToChoices('simulation')">返回选择</el-button>
        <el-button type="primary" :loading="simulating" @click="simulatePayment">确认模拟支付</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showResult" title="模拟完成" width="min(420px, 92vw)" align-center>
      <div class="simulation-content">
        <p class="result-title">模拟支付成功</p>
        <p class="notice">订单已标记为已支付（模拟），未发生实际扣款，不代表真实购票成功。</p>
      </div>
      <template #footer>
        <el-button @click="showResult = false">留在当前页</el-button>
        <el-button type="primary" @click="router.push('/orderManagement/index')">查看订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="PayMethod">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/header/index'
import Footer from '@/components/footer/index'
import supportQr from '@/assets/section/support-alipay.jpg'
import { getOrderListApi, orderPayApi } from '@/api/order.js'
import { ElMessage } from 'element-plus'

const router = useRouter()
const orderNumber = ref('')
const showChoices = ref(false)
const showSupport = ref(false)
const showSimulation = ref(false)
const showResult = ref(false)
const simulating = ref(false)

onMounted(() => {
  orderNumber.value = history.state?.orderNumber || ''
  showChoices.value = Boolean(orderNumber.value)
})

function openSupport() {
  showChoices.value = false
  showSupport.value = true
}
function openSimulation() {
  showChoices.value = false
  showSimulation.value = true
}
function backToChoices(source) {
  if (simulating.value) return
  if (source === 'support') showSupport.value = false
  else showSimulation.value = false
  showChoices.value = true
}
async function simulatePayment() {
  if (simulating.value) return
  simulating.value = true
  try {
    const list = await getOrderListApi({ userId: 0 })
    if (String(list.code) !== '0' || !Array.isArray(list.data)) throw new Error(list.message || '获取订单失败')
    const order = list.data.find(item => String(item.orderNumber) === String(orderNumber.value))
    if (!order) throw new Error('未找到当前账号的订单')
    const result = await orderPayApi({ platform: 3, orderNumber: orderNumber.value, subject: order.programTitle,
      price: order.orderPrice, channel: 'demo', payBillType: 1 })
    if (String(result.code) !== '0' || result.data !== 'DEMO_PAID') throw new Error(result.message || '模拟支付失败')
    simulating.value = false
    showSimulation.value = false
    showResult.value = true
  } catch (error) {
    ElMessage.error(error?.message || '模拟支付失败，请稍后再试')
  } finally {
    simulating.value = false
  }
}
</script>

<style scoped>
.payment-page { min-height: 100vh; background: #fafafa; }
.payment-content { max-width: 960px; min-height: 510px; margin: auto; padding: 80px 20px; }
.payment-card { background: #fff; padding: 48px 24px; border: 1px solid #f0f0f0; border-radius: 16px; text-align: center; }
.payment-icon { display: grid; place-items: center; width: 52px; height: 52px; margin: auto; border-radius: 50%; color: #fff; background: #ff4d58; font-size: 28px; }
h1 { margin: 18px 0 8px; color: #222; font-size: 24px; }
.order-number, .description, .notice { color: #777; line-height: 1.6; }
.description { margin: 12px 0 24px; }
.choose-button { background: #ff4d58; border-color: #ff4d58; }
.back-link { display: block; margin: 20px auto 0; border: 0; background: transparent; color: #777; cursor: pointer; }
.payment-options { display: grid; gap: 12px; }
.option { display: flex; align-items: center; gap: 14px; padding: 18px; border: 1px solid #eee; border-radius: 12px; background: #fff; text-align: left; cursor: pointer; }
.option:hover { border-color: #ff6570; background: #fff8f8; }
.option-icon { display: grid; flex: none; place-items: center; width: 42px; height: 42px; border-radius: 12px; font-size: 24px; }
.support-icon { color: #1677ff; background: #eaf3ff; }
.demo-icon { color: #ff4d58; background: #fff0f1; }
.option strong, .option small { display: block; }
.option strong { color: #242424; font-size: 16px; }
.option small { margin-top: 5px; color: #777; font-size: 13px; }
.arrow { margin-left: auto; color: #aaa; font-size: 24px; }
.support-content, .simulation-content { text-align: center; }
.support-title, .result-title { color: #252525; font-size: 17px; font-weight: 600; }
.support-qr { display: block; width: min(100%, 270px); height: auto; margin: 16px auto; border-radius: 8px; }
.notice { margin-top: 14px; font-size: 13px; }
</style>
